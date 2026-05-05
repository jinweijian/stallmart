package com.stallmart.store.internal.service;

import com.stallmart.product.dto.CategoryDTO;
import com.stallmart.product.dto.CategoryUpsertParams;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.product.dto.ProductSkuDTO;
import com.stallmart.product.dto.ProductSkuParams;
import com.stallmart.product.dto.ProductUpsertParams;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.store.dto.StorefrontDTO;
import com.stallmart.store.dto.UpdateDecorationParams;
import com.stallmart.store.dto.UpdateStoreParams;
import com.stallmart.store.internal.repository.CategoryEntity;
import com.stallmart.store.internal.repository.CategoryRepository;
import com.stallmart.store.internal.repository.ProductEntity;
import com.stallmart.store.internal.repository.ProductRepository;
import com.stallmart.store.internal.repository.ProductSkuEntity;
import com.stallmart.store.internal.repository.ProductSkuRepository;
import com.stallmart.store.internal.repository.ProductSpecEntity;
import com.stallmart.store.internal.repository.ProductSpecRepository;
import com.stallmart.store.internal.repository.StoreDecorationEntity;
import com.stallmart.store.internal.repository.StoreDecorationRepository;
import com.stallmart.store.internal.repository.StoreEntity;
import com.stallmart.store.internal.repository.StoreRepository;
import com.stallmart.store.internal.repository.StoreStyleEntity;
import com.stallmart.store.internal.repository.StoreStyleRepository;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.style.dto.SpecUpsertParams;
import com.stallmart.style.dto.StorefrontCategoryDTO;
import com.stallmart.style.dto.StorefrontCategoryIconDTO;
import com.stallmart.style.dto.StorefrontThemeDTO;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.persistence.JsonSupport;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreStyleRepository styleRepository;
    private final StoreDecorationRepository decorationRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductSkuRepository skuRepository;
    private final ProductSpecRepository specRepository;
    private final JsonSupport json;

    public StoreServiceImpl(
            StoreRepository storeRepository,
            StoreStyleRepository styleRepository,
            StoreDecorationRepository decorationRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            ProductSkuRepository skuRepository,
            ProductSpecRepository specRepository,
            JsonSupport json
    ) {
        this.storeRepository = storeRepository;
        this.styleRepository = styleRepository;
        this.decorationRepository = decorationRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
        this.specRepository = specRepository;
        this.json = json;
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
        getStoreEntity(storeId);
        List<CategoryEntity> categories = module == null
                ? categoryRepository.findByStoreIdOrderBySortOrderAscIdAsc(storeId)
                : categoryRepository.findByStoreIdAndModuleIgnoreCaseOrderBySortOrderAscIdAsc(storeId, module);
        return categories.stream().map(this::toCategoryDTO).toList();
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(long storeId, CategoryUpsertParams request) {
        getStoreEntity(storeId);
        CategoryEntity category = new CategoryEntity();
        category.storeId = storeId;
        category.module = request.module() == null ? "PRODUCT" : request.module().toUpperCase();
        category.name = request.name();
        category.iconKey = normalizeCategoryIconKey(request.iconKey());
        category.sortOrder = request.sortOrder();
        category.status = request.status() == null ? "ACTIVE" : request.status();
        return toCategoryDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(long storeId, long categoryId, CategoryUpsertParams request) {
        getStoreEntity(storeId);
        CategoryEntity current = categoryRepository.findById(categoryId)
                .filter(category -> category.storeId.equals(storeId))
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        current.module = request.module() == null ? current.module : request.module().toUpperCase();
        current.name = request.name();
        current.iconKey = normalizeCategoryIconKey(request.iconKey());
        current.sortOrder = request.sortOrder();
        current.status = request.status() == null ? current.status : request.status();
        return toCategoryDTO(categoryRepository.save(current));
    }

    @Override
    public List<ProductDTO> listProducts(long storeId) {
        getStoreEntity(storeId);
        return productRepository.findByStoreIdOrderBySortOrderAscIdAsc(storeId).stream()
                .map(this::toProductDTO)
                .toList();
    }

    @Override
    public ProductDTO getProduct(long id) {
        return toProductDTO(getProductEntity(id));
    }

    @Override
    @Transactional
    public ProductDTO createProduct(long storeId, ProductUpsertParams request) {
        getStoreEntity(storeId);
        ProductEntity product = new ProductEntity();
        product.storeId = storeId;
        applyProductRequest(product, request, storeId);
        ProductEntity saved = productRepository.save(product);
        saveSkus(saved.id, request.skus());
        return toProductDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(long storeId, long productId, ProductUpsertParams request) {
        getStoreEntity(storeId);
        ProductEntity current = getProductEntity(productId);
        if (!current.storeId.equals(storeId)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        applyProductRequest(current, request, storeId);
        ProductEntity saved = productRepository.save(current);
        skuRepository.deleteByProductId(saved.id);
        saveSkus(saved.id, request.skus());
        return toProductDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProductStatus(long storeId, long productId, String status) {
        getStoreEntity(storeId);
        ProductEntity current = getProductEntity(productId);
        if (!current.storeId.equals(storeId)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        current.status = status;
        return toProductDTO(productRepository.save(current));
    }

    @Override
    public StoreDecorationDTO getDecoration(long storeId) {
        StoreEntity store = getStoreEntity(storeId);
        StyleDTO style = getStyle(store.styleId);
        StoreDecorationEntity decoration = decorationRepository.findById(storeId).orElse(null);
        StorefrontThemeDTO theme = style.theme();
        List<StorefrontCategoryIconDTO> categoryIconLibrary = resolveCategoryIconLibrary(theme, decoration);
        return new StoreDecorationDTO(
                store.id,
                store.name,
                store.logoUrl,
                store.coverUrl,
                decoration == null ? List.of() : json.stringList(decoration.bannersJson),
                store.styleId,
                style.code(),
                style,
                theme.layoutVersion(),
                theme,
                mergeMap(theme.colors(), decoration == null ? null : json.stringMap(decoration.colorsJson)),
                theme.iconNames(),
                mergeMap(theme.iconUrls(), decoration == null ? null : json.stringMap(decoration.iconUrlsJson)),
                mergeMap(theme.imageUrls(), decoration == null ? null : json.stringMap(decoration.imageUrlsJson)),
                mergeMap(theme.copywriting(), decoration == null ? null : json.stringMap(decoration.copywritingJson)),
                categoryIconLibrary,
                resolveStorefrontCategories(storeId, categoryIconLibrary),
                theme.assetSizes(),
                theme.pageThemes()
        );
    }

    @Override
    @Transactional
    public StoreDecorationDTO updateDecoration(long storeId, UpdateDecorationParams request) {
        StoreEntity current = getStoreEntity(storeId);
        StoreStyleEntity style = request.styleId() == null ? getStyleEntity(current.styleId) : getStyleEntity(request.styleId());
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

    @Override
    public List<StyleDTO> listStyles() {
        return styleRepository.findAllByOrderByIdAsc().stream().map(this::toStyleDTO).toList();
    }

    @Override
    public StyleDTO getStyle(long id) {
        return toStyleDTO(getStyleEntity(id));
    }

    @Override
    @Transactional
    public StyleDTO updateStyleStatus(long id, String status) {
        StoreStyleEntity style = getStyleEntity(id);
        style.status = status;
        style.version += 1;
        return toStyleDTO(styleRepository.save(style));
    }

    @Override
    public List<SpecDTO> listSpecs(long styleId) {
        getStyleEntity(styleId);
        return specRepository.findByStyleIdOrderByIdAsc(styleId).stream()
                .map(this::toSpecDTO)
                .toList();
    }

    @Override
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

    @Override
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

    @Override
    @Transactional
    public void deleteSpec(long styleId, long specId) {
        getStyleEntity(styleId);
        boolean linked = productRepository.findAllByOrderByIdAsc().stream()
                .filter(product -> getStoreEntity(product.storeId).styleId.equals(styleId))
                .anyMatch(product -> json.longList(product.specIdsJson).contains(specId));
        if (linked) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        ProductSpecEntity spec = specRepository.findById(specId)
                .filter(candidate -> candidate.styleId.equals(styleId))
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        specRepository.delete(spec);
    }

    private void applyProductRequest(ProductEntity product, ProductUpsertParams request, long storeId) {
        CategoryEntity category = requireCategory(storeId, request.categoryId());
        product.categoryId = category.id;
        product.name = request.name();
        product.description = request.description();
        product.mainImageUrl = primaryImage(request);
        product.status = request.status() == null ? "ACTIVE" : request.status();
        product.sortOrder = request.sortOrder();
        product.specIdsJson = json.write(requireSpecIds(storeId, request.specIds()));
    }

    private void saveSkus(Long productId, List<ProductSkuParams> skus) {
        if (skus == null || skus.isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        for (ProductSkuParams sku : skus) {
            if (sku.specValues() == null || sku.specValues().isEmpty() || sku.price() == null) {
                throw new AppException(ErrorCode.BAD_REQUEST);
            }
            ProductSkuEntity entity = new ProductSkuEntity();
            entity.productId = productId;
            entity.specValuesJson = json.write(sku.specValues());
            entity.price = sku.price();
            entity.stock = sku.stock();
            entity.status = sku.status() == null ? "ACTIVE" : sku.status();
            skuRepository.save(entity);
        }
    }

    private ProductDTO toProductDTO(ProductEntity product) {
        CategoryEntity category = requireCategory(product.storeId, product.categoryId);
        List<ProductSkuDTO> skus = skuRepository.findByProductIdOrderByIdAsc(product.id).stream()
                .map(this::toSkuDTO)
                .toList();
        BigDecimal price = skus.stream()
                .map(ProductSkuDTO::price)
                .min(BigDecimal::compareTo)
                .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST));
        return new ProductDTO(
                product.id,
                product.storeId,
                category.id,
                category.name,
                product.name,
                product.description,
                price,
                product.mainImageUrl,
                product.mainImageUrl,
                category.name,
                product.status,
                product.sortOrder,
                json.longList(product.specIdsJson),
                skus
        );
    }

    private ProductSkuDTO toSkuDTO(ProductSkuEntity sku) {
        return new ProductSkuDTO(
                sku.id,
                json.stringList(sku.specValuesJson),
                sku.price,
                sku.stock,
                sku.status
        );
    }

    private SpecDTO toSpecDTO(ProductSpecEntity spec) {
        return new SpecDTO(spec.id, spec.styleId, spec.name, spec.specType, spec.required, json.stringList(spec.optionsJson));
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

    private CategoryDTO toCategoryDTO(CategoryEntity category) {
        return new CategoryDTO(
                category.id,
                category.storeId,
                category.module,
                category.name,
                category.iconKey,
                category.sortOrder,
                category.status
        );
    }

    private StyleDTO toStyleDTO(StoreStyleEntity style) {
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

    private StoreEntity getStoreEntity(long id) {
        return storeRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    private StoreStyleEntity getStyleEntity(long id) {
        return styleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    private ProductEntity getProductEntity(long id) {
        return productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    private CategoryEntity requireCategory(long storeId, Long categoryId) {
        if (categoryId == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return categoryRepository.findById(categoryId)
                .filter(category -> category.storeId.equals(storeId))
                .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST));
    }

    private List<Long> requireSpecIds(long storeId, List<Long> specIds) {
        if (specIds == null || specIds.isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        List<Long> validIds = listSpecs(getStoreEntity(storeId).styleId).stream().map(SpecDTO::id).toList();
        if (!validIds.containsAll(specIds)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return List.copyOf(specIds);
    }

    private List<StorefrontCategoryIconDTO> resolveCategoryIconLibrary(StorefrontThemeDTO theme, StoreDecorationEntity decoration) {
        Map<String, String> overrides = decoration == null ? Map.of() : json.stringMap(decoration.categoryIconUrlsJson);
        return theme.categoryIconLibrary().stream()
                .map(icon -> new StorefrontCategoryIconDTO(
                        icon.key(),
                        icon.name(),
                        overrides.getOrDefault(icon.key(), icon.iconUrl()),
                        icon.fallbackText()
                ))
                .toList();
    }

    private List<StorefrontCategoryDTO> resolveStorefrontCategories(long storeId, List<StorefrontCategoryIconDTO> iconLibrary) {
        Map<String, StorefrontCategoryIconDTO> iconsByKey = new LinkedHashMap<>();
        for (StorefrontCategoryIconDTO icon : iconLibrary) {
            iconsByKey.put(icon.key(), icon);
        }

        List<StorefrontCategoryDTO> storefrontCategories = new ArrayList<>();
        StorefrontCategoryIconDTO recommendIcon = iconsByKey.get("recommend");
        if (recommendIcon != null) {
            storefrontCategories.add(new StorefrontCategoryDTO(
                    "recommend",
                    recommendIcon.name(),
                    recommendIcon.key(),
                    recommendIcon.iconUrl(),
                    recommendIcon.fallbackText(),
                    0,
                    "ACTIVE"
            ));
        }

        listCategories(storeId, "PRODUCT").stream()
                .filter(category -> "ACTIVE".equalsIgnoreCase(category.status()))
                .forEach(category -> {
                    StorefrontCategoryIconDTO icon = iconsByKey.get(category.iconKey());
                    storefrontCategories.add(new StorefrontCategoryDTO(
                            String.valueOf(category.id()),
                            category.name(),
                            category.iconKey(),
                            icon == null ? null : icon.iconUrl(),
                            icon == null ? category.name().substring(0, 1) : icon.fallbackText(),
                            category.sortOrder(),
                            category.status()
                    ));
                });
        return storefrontCategories;
    }

    private String normalizeCategoryIconKey(String iconKey) {
        return iconKey == null || iconKey.isBlank() ? "recommend" : iconKey;
    }

    private String primaryImage(ProductUpsertParams request) {
        return request.mainImageUrl() == null || request.mainImageUrl().isBlank()
                ? request.imageUrl()
                : request.mainImageUrl();
    }

    private Map<String, String> mergeMap(Map<String, String> defaults, Map<String, String> overrides) {
        Map<String, String> merged = new LinkedHashMap<>(defaults == null ? Map.of() : defaults);
        if (overrides != null) {
            merged.putAll(overrides);
        }
        return Map.copyOf(merged);
    }
}
