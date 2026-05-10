package com.stallmart.store.internal.service;

import com.stallmart.product.dto.CategoryDTO;
import com.stallmart.product.dto.CategoryUpsertParams;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.product.dto.ProductUpsertParams;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.store.dto.StorefrontDTO;
import com.stallmart.store.dto.UpdateDecorationParams;
import com.stallmart.store.dto.UpdateStoreParams;
import com.stallmart.store.internal.model.ProductStatus;
import com.stallmart.store.internal.model.StoreStyleStatus;
import com.stallmart.store.internal.repository.StoreDecorationEntity;
import com.stallmart.store.internal.repository.StoreDecorationRepository;
import com.stallmart.store.internal.repository.StoreEntity;
import com.stallmart.store.internal.repository.StoreRepository;
import com.stallmart.store.internal.repository.StoreStyleEntity;
import com.stallmart.store.internal.repository.StoreStyleRepository;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.style.dto.SpecUpsertParams;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.style.dto.StyleUpsertParams;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.persistence.JsonSupport;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreStyleRepository styleRepository;
    private final StoreDecorationRepository decorationRepository;
    private final JsonSupport json;
    private final StoreDecorationAssembler decorationAssembler;
    private final ProductCatalogService productCatalogService;
    private final StylePackageService stylePackageService;

    public StoreServiceImpl(
            StoreRepository storeRepository,
            StoreStyleRepository styleRepository,
            StoreDecorationRepository decorationRepository,
            JsonSupport json,
            StoreDecorationAssembler decorationAssembler,
            ProductCatalogService productCatalogService,
            StylePackageService stylePackageService
    ) {
        this.storeRepository = storeRepository;
        this.styleRepository = styleRepository;
        this.decorationRepository = decorationRepository;
        this.json = json;
        this.decorationAssembler = decorationAssembler;
        this.productCatalogService = productCatalogService;
        this.stylePackageService = stylePackageService;
    }

    @Override
    public StoreDTO getStore(long id) {
        return toStoreDTO(getStoreEntity(id));
    }

    @Override
    public StorefrontDTO getStorefront(long id) {
        StoreDTO store = getStore(id);
        return StorefrontDTO.from(store, getDecoration(id));
    }

    @Override
    public StoreDTO getStoreByQrCode(String qrCode) {
        return toStoreDTO(storeRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)));
    }

    @Override
    public StorefrontDTO getStorefrontByQrCode(String qrCode) {
        StoreDTO store = getStoreByQrCode(qrCode);
        return StorefrontDTO.from(store, getDecoration(store.id()));
    }

    @Override
    public StorefrontDTO getStorefrontByAppId(String appId) {
        StoreEntity store = storeRepository.findByAppId(appId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        return StorefrontDTO.from(toStoreDTO(store), getDecoration(store.id));
    }

    @Override
    public List<StoreDTO> listStores() {
        return storeRepository.findAllByOrderByIdAsc().stream().map(this::toStoreDTO).toList();
    }

    @Override
    public List<StoreDTO> listStoresByOwner(long ownerId) {
        return storeRepository.findByOwnerIdOrderByIdAsc(ownerId).stream().map(this::toStoreDTO).toList();
    }

    @Override
    @Transactional
    public StoreDTO updateStore(long id, UpdateStoreParams request) {
        StoreEntity current = getStoreEntity(id);
        current.name = request.name() == null ? current.name : request.name();
        current.description = request.description() == null ? current.description : request.description();
        current.logoUrl = request.logoUrl() == null ? current.logoUrl : request.logoUrl();
        current.coverUrl = request.coverUrl() == null ? current.coverUrl : request.coverUrl();
        current.status = request.status() == null ? current.status : request.status();
        return toStoreDTO(storeRepository.save(current));
    }

    @Override
    public List<CategoryDTO> listCategories(long storeId, String module) {
        return productCatalogService.listCategories(storeId, module);
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(long storeId, CategoryUpsertParams request) {
        return productCatalogService.createCategory(storeId, request);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(long storeId, long categoryId, CategoryUpsertParams request) {
        return productCatalogService.updateCategory(storeId, categoryId, request);
    }

    @Override
    public List<ProductDTO> listProducts(long storeId) {
        return productCatalogService.listProducts(storeId);
    }

    @Override
    public ProductDTO getProduct(long id) {
        return productCatalogService.getProduct(id);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(long storeId, ProductUpsertParams request) {
        return productCatalogService.createProduct(storeId, request);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(long storeId, long productId, ProductUpsertParams request) {
        return productCatalogService.updateProduct(storeId, productId, request);
    }

    @Override
    @Transactional
    public ProductDTO updateProductStatus(long storeId, long productId, ProductStatus status) {
        return productCatalogService.updateProductStatus(storeId, productId, status);
    }

    @Override
    public StoreDecorationDTO getDecoration(long storeId) {
        StoreEntity store = getStoreEntity(storeId);
        StyleDTO style = getStyle(store.styleId);
        StoreDecorationEntity decoration = decorationRepository.findById(storeId).orElse(null);
        return decorationAssembler.assemble(store, style, decoration, listCategories(storeId, "PRODUCT"));
    }

    @Override
    @Transactional
    public StoreDecorationDTO updateDecoration(long storeId, UpdateDecorationParams request) {
        if (hasStylePackageOverrides(request)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        StoreEntity current = getStoreEntity(storeId);
        StoreStyleEntity style = request.styleId() == null ? getStyleEntity(current.styleId) : getStyleEntity(request.styleId());
        if (request.styleId() != null && style.status != StoreStyleStatus.ACTIVE) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        current.styleId = style.id;
        current.description = request.description() == null ? current.description : request.description();
        current.logoUrl = request.logoUrl() == null ? current.logoUrl : request.logoUrl();
        current.coverUrl = request.coverUrl() == null ? current.coverUrl : request.coverUrl();
        storeRepository.save(current);

        StoreDecorationEntity decoration = decorationRepository.findById(storeId).orElseGet(() -> {
            StoreDecorationEntity entity = new StoreDecorationEntity();
            entity.storeId = storeId;
            return entity;
        });
        if (request.banners() != null) {
            decoration.bannersJson = json.write(request.banners());
        }
        if (request.colors() != null) {
            decoration.colorsJson = json.write(request.colors());
        }
        if (request.iconUrls() != null) {
            decoration.iconUrlsJson = json.write(request.iconUrls());
        }
        if (request.categoryIconUrls() != null) {
            decoration.categoryIconUrlsJson = json.write(request.categoryIconUrls());
        }
        if (request.imageUrls() != null) {
            decoration.imageUrlsJson = json.write(request.imageUrls());
        }
        if (request.copywriting() != null) {
            decoration.copywritingJson = json.write(request.copywriting());
        }
        decorationRepository.save(decoration);
        return getDecoration(storeId);
    }

    private boolean hasStylePackageOverrides(UpdateDecorationParams request) {
        return request.colors() != null
                || request.iconUrls() != null
                || request.categoryIconUrls() != null
                || request.imageUrls() != null
                || request.copywriting() != null;
    }

    @Override
    public List<StyleDTO> listStyles() {
        return stylePackageService.listStyles();
    }

    @Override
    public List<StyleDTO> listActiveStyles() {
        return stylePackageService.listActiveStyles();
    }

    @Override
    public StyleDTO getStyle(long id) {
        return stylePackageService.getStyle(id);
    }

    @Override
    @Transactional
    public StyleDTO createStyle(StyleUpsertParams request) {
        return stylePackageService.createStyle(request);
    }

    @Override
    @Transactional
    public StyleDTO updateStyle(long id, StyleUpsertParams request) {
        return stylePackageService.updateStyle(id, request);
    }

    @Override
    @Transactional
    public StyleDTO updateStyleStatus(long id, StoreStyleStatus status) {
        return stylePackageService.updateStyleStatus(id, status);
    }

    @Override
    @Transactional
    public void deleteStyle(long id) {
        stylePackageService.deleteStyle(id);
    }

    @Override
    public List<SpecDTO> listSpecs(long styleId) {
        return stylePackageService.listSpecs(styleId);
    }

    @Override
    @Transactional
    public SpecDTO createSpec(long styleId, SpecUpsertParams request) {
        return stylePackageService.createSpec(styleId, request);
    }

    @Override
    @Transactional
    public SpecDTO updateSpec(long styleId, long specId, SpecUpsertParams request) {
        return stylePackageService.updateSpec(styleId, specId, request);
    }

    @Override
    @Transactional
    public void deleteSpec(long styleId, long specId) {
        stylePackageService.deleteSpec(styleId, specId);
    }

    private StoreDTO toStoreDTO(StoreEntity store) {
        StoreStyleEntity style = getStyleEntity(store.styleId);
        return new StoreDTO(
                store.id,
                store.ownerId,
                store.styleId,
                style.code,
                store.name,
                store.category,
                store.description,
                store.logoUrl,
                store.coverUrl,
                store.qrCode,
                store.address,
                store.status
        );
    }

    private StoreEntity getStoreEntity(long id) {
        return storeRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    private StoreStyleEntity getStyleEntity(long id) {
        return styleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

}
