package com.stallmart.store.internal.service;

import com.stallmart.store.internal.model.SpecType;
import com.stallmart.store.internal.model.StoreStyleStatus;
import com.stallmart.store.internal.repository.ProductRepository;
import com.stallmart.store.internal.repository.ProductSpecEntity;
import com.stallmart.store.internal.repository.ProductSpecRepository;
import com.stallmart.store.internal.repository.StoreRepository;
import com.stallmart.store.internal.repository.StoreStyleEntity;
import com.stallmart.store.internal.repository.StoreStyleRepository;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.style.dto.SpecUpsertParams;
import com.stallmart.style.dto.StorefrontThemeDTO;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.style.dto.StyleUpsertParams;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.persistence.JsonSupport;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StylePackageService {

    private final StoreStyleRepository styleRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final ProductSpecRepository specRepository;
    private final JsonSupport json;

    public StylePackageService(
            StoreStyleRepository styleRepository,
            StoreRepository storeRepository,
            ProductRepository productRepository,
            ProductSpecRepository specRepository,
            JsonSupport json
    ) {
        this.styleRepository = styleRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.specRepository = specRepository;
        this.json = json;
    }

    public List<StyleDTO> listStyles() {
        return styleRepository.findAllByOrderByIdAsc().stream().map(this::toStyleDTO).toList();
    }

    public List<StyleDTO> listActiveStyles() {
        return styleRepository.findByStatusOrderByIdAsc(StoreStyleStatus.ACTIVE).stream().map(this::toStyleDTO).toList();
    }

    public StyleDTO getStyle(long id) {
        return toStyleDTO(getStyleEntity(id));
    }

    @Transactional
    public StyleDTO createStyle(StyleUpsertParams request) {
        validateStyleRequest(request, null);
        StoreStyleEntity style = new StoreStyleEntity();
        style.name = request.name();
        style.code = request.code();
        style.previewUrl = request.previewUrl();
        style.status = normalizeStyleStatus(request.status());
        style.version = 1;
        style.themeJson = json.write(request.theme());
        return toStyleDTO(styleRepository.save(style));
    }

    @Transactional
    public StyleDTO updateStyle(long id, StyleUpsertParams request) {
        StoreStyleEntity style = getStyleEntity(id);
        validateStyleRequest(request, id);
        style.name = request.name();
        style.code = request.code();
        style.previewUrl = request.previewUrl();
        style.status = normalizeStyleStatus(request.status());
        style.version += 1;
        style.themeJson = json.write(request.theme());
        return toStyleDTO(styleRepository.save(style));
    }

    @Transactional
    public StyleDTO updateStyleStatus(long id, StoreStyleStatus status) {
        StoreStyleEntity style = getStyleEntity(id);
        style.status = status;
        style.version += 1;
        return toStyleDTO(styleRepository.save(style));
    }

    @Transactional
    public void deleteStyle(long id) {
        StoreStyleEntity style = getStyleEntity(id);
        if (storeRepository.existsByStyleId(id)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        styleRepository.delete(style);
    }

    public List<SpecDTO> listSpecs(long styleId) {
        getStyleEntity(styleId);
        return specRepository.findByStyleIdOrderByIdAsc(styleId).stream()
                .map(this::toSpecDTO)
                .toList();
    }

    @Transactional
    public SpecDTO createSpec(long styleId, SpecUpsertParams request) {
        getStyleEntity(styleId);
        ProductSpecEntity spec = new ProductSpecEntity();
        spec.styleId = styleId;
        spec.name = request.name();
        spec.specType = request.specType();
        spec.required = request.required();
        spec.optionsJson = json.write(request.options() == null ? List.of() : request.options());
        return toSpecDTO(specRepository.save(spec));
    }

    @Transactional
    public SpecDTO updateSpec(long styleId, long specId, SpecUpsertParams request) {
        getStyleEntity(styleId);
        ProductSpecEntity spec = specRepository.findById(specId)
                .filter(candidate -> candidate.styleId.equals(styleId))
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        spec.name = request.name();
        spec.specType = request.specType();
        spec.required = request.required();
        spec.optionsJson = json.write(request.options() == null ? List.of() : request.options());
        return toSpecDTO(specRepository.save(spec));
    }

    @Transactional
    public void deleteSpec(long styleId, long specId) {
        getStyleEntity(styleId);
        boolean linked = productRepository.findAllByOrderByIdAsc().stream()
                .filter(product -> storeRepository.findById(product.storeId)
                        .map(store -> store.styleId.equals(styleId))
                        .orElse(false))
                .anyMatch(product -> json.longList(product.specIdsJson).contains(specId));
        if (linked) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        ProductSpecEntity spec = specRepository.findById(specId)
                .filter(candidate -> candidate.styleId.equals(styleId))
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        specRepository.delete(spec);
    }

    StyleDTO toStyleDTO(StoreStyleEntity style) {
        return new StyleDTO(
                style.id,
                style.name,
                style.code,
                style.previewUrl,
                json.read(style.themeJson, StorefrontThemeDTO.class),
                style.status,
                style.version
        );
    }

    private SpecDTO toSpecDTO(ProductSpecEntity spec) {
        return new SpecDTO(spec.id, spec.styleId, spec.name, spec.specType, spec.required, json.stringList(spec.optionsJson));
    }

    private StoreStyleEntity getStyleEntity(long id) {
        return styleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    private void validateStyleRequest(StyleUpsertParams request, Long currentId) {
        if (request == null || request.theme() == null || request.name() == null || request.name().isBlank()
                || request.code() == null || request.code().isBlank()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        boolean duplicateCode = styleRepository.findAllByOrderByIdAsc().stream()
                .anyMatch(style -> style.code.equalsIgnoreCase(request.code())
                        && (currentId == null || !style.id.equals(currentId)));
        if (duplicateCode) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        validateThemeContract(request.theme());
    }

    private void validateThemeContract(StorefrontThemeDTO theme) {
        if (theme.colors() == null || theme.colors().isEmpty()
                || theme.categoryIconLibrary() == null || theme.categoryIconLibrary().isEmpty()
                || theme.assetSizes() == null || theme.assetSizes().isEmpty()
                || theme.pageThemes() == null || theme.pageThemes().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
    }
}
