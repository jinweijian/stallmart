package com.stallmart.store.internal.service;

import com.stallmart.product.dto.CategoryDTO;
import com.stallmart.product.dto.CategoryUpsertParams;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.product.dto.ProductSkuDTO;
import com.stallmart.product.dto.ProductSkuParams;
import com.stallmart.product.dto.ProductUpsertParams;
import com.stallmart.store.internal.repository.CategoryEntity;
import com.stallmart.store.internal.repository.CategoryRepository;
import com.stallmart.store.internal.repository.ProductEntity;
import com.stallmart.store.internal.repository.ProductRepository;
import com.stallmart.store.internal.repository.ProductSkuEntity;
import com.stallmart.store.internal.repository.ProductSkuRepository;
import com.stallmart.store.internal.repository.StoreEntity;
import com.stallmart.store.internal.repository.StoreRepository;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.persistence.JsonSupport;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCatalogService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductSkuRepository skuRepository;
    private final JsonSupport json;
    private final StylePackageService stylePackageService;

    public ProductCatalogService(
            StoreRepository storeRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            ProductSkuRepository skuRepository,
            JsonSupport json,
            StylePackageService stylePackageService
    ) {
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
        this.json = json;
        this.stylePackageService = stylePackageService;
    }

    public List<CategoryDTO> listCategories(long storeId, String module) {
        getStoreEntity(storeId);
        List<CategoryEntity> categories = module == null
                ? categoryRepository.findByStoreIdOrderBySortOrderAscIdAsc(storeId)
                : categoryRepository.findByStoreIdAndModuleIgnoreCaseOrderBySortOrderAscIdAsc(storeId, module);
        return categories.stream().map(this::toCategoryDTO).toList();
    }

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

    public List<ProductDTO> listProducts(long storeId) {
        getStoreEntity(storeId);
        return productRepository.findByStoreIdOrderBySortOrderAscIdAsc(storeId).stream()
                .map(this::toProductDTO)
                .toList();
    }

    public ProductDTO getProduct(long id) {
        return toProductDTO(getProductEntity(id));
    }

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
        List<Long> validIds = stylePackageService.listSpecs(getStoreEntity(storeId).styleId).stream().map(SpecDTO::id).toList();
        if (!validIds.containsAll(specIds)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return List.copyOf(specIds);
    }

    private StoreEntity getStoreEntity(long id) {
        return storeRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    private String normalizeCategoryIconKey(String iconKey) {
        return iconKey == null || iconKey.isBlank() ? "recommend" : iconKey;
    }

    private String primaryImage(ProductUpsertParams request) {
        return request.mainImageUrl() == null || request.mainImageUrl().isBlank()
                ? request.imageUrl()
                : request.mainImageUrl();
    }
}
