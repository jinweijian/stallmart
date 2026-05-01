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
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.style.dto.SpecUpsertParams;
import com.stallmart.style.dto.StorefrontCategoryDTO;
import com.stallmart.style.dto.StorefrontThemeDTO;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements StoreService {

    private final Map<Long, StoreDTO> stores = new ConcurrentHashMap<>();
    private final Map<Long, ProductDTO> products = new ConcurrentHashMap<>();
    private final Map<Long, CategoryDTO> categories = new ConcurrentHashMap<>();
    private final Map<Long, StyleDTO> styles = new ConcurrentHashMap<>();
    private final Map<Long, List<SpecDTO>> specsByStyle = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> bannersByStore = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> colorsByStore = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> iconUrlsByStore = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> imageUrlsByStore = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> copywritingByStore = new ConcurrentHashMap<>();
    private final AtomicLong productIdSequence = new AtomicLong(4);
    private final AtomicLong categoryIdSequence = new AtomicLong(4);
    private final AtomicLong skuIdSequence = new AtomicLong(10);
    private final AtomicLong specIdSequence = new AtomicLong(3);

    public StoreServiceImpl() {
        styles.put(1L, new StyleDTO(1L, "夏威夷风", "hawaiian", null, hawaiianTheme()));
        styles.put(2L, new StyleDTO(2L, "烧烤风", "BBQ", null, simpleTheme("BBQ", "烧烤风", "#E74C3C", "#F39C12", "#FDEDEC")));
        styles.put(3L, new StyleDTO(3L, "市集风", "market", null, simpleTheme("market", "市集风", "#F39C12", "#8BC34A", "#FFF8E7")));
        styles.put(6L, new StyleDTO(6L, "森系水果茶", "forestFruitTeaCrayon", "/static/storefront/forest/preview.png", forestFruitTeaTheme()));

        specsByStyle.put(1L, List.of(
                new SpecDTO(1L, 1L, "杯型", "SIZE", true, List.of("中杯", "大杯")),
                new SpecDTO(2L, 1L, "甜度", "SWEET", false, List.of("无糖", "少糖", "正常糖"))
        ));
        specsByStyle.put(2L, List.of());
        specsByStyle.put(3L, List.of());
        specsByStyle.put(6L, List.of(
                new SpecDTO(1L, 6L, "杯型", "SIZE", true, List.of("中杯", "大杯")),
                new SpecDTO(2L, 6L, "甜度", "SWEET", true, List.of("少糖", "正常糖"))
        ));

        stores.put(1L, new StoreDTO(
                1L,
                2L,
                6L,
                "forestFruitTeaCrayon",
                "小新の水果茶屋",
                "饮品",
                "当季鲜果茶，清爽一夏",
                "/static/default-avatar.png",
                "/static/storefront/forest/cover.png",
                "stall-001",
                "市集东入口 12 号",
                "OPEN"
        ));
        bannersByStore.put(1L, List.of("/static/storefront/forest/banner-seasonal.png", "/static/storefront/forest/banner-tea.png"));
        imageUrlsByStore.put(1L, Map.of(
                "heroIllustration", "/static/storefront/forest/hero-forest-tea.png",
                "mascot", "/static/storefront/forest/mascot.png",
                "productPlaceholder", "/static/storefront/forest/product-placeholder.png"
        ));
        copywritingByStore.put(1L, Map.of(
                "branchName", "上海环球港店",
                "heroEyebrow", "小新の",
                "heroTitle", "水果茶屋",
                "heroSubtitle", "自然水果 · 新鲜现制",
                "promoTitle", "鲜果时令上新",
                "promoSubtitle", "当季水果 · 清爽一夏",
                "promoActionText", "立即尝鲜"
        ));

        categories.put(1L, new CategoryDTO(1L, 1L, "PRODUCT", "清爽柠檬", 1, "ACTIVE"));
        categories.put(2L, new CategoryDTO(2L, 1L, "PRODUCT", "多肉葡萄", 2, "ACTIVE"));
        categories.put(3L, new CategoryDTO(3L, 1L, "PRODUCT", "香甜芒果", 3, "ACTIVE"));

        products.put(1L, product(1L, 1L, 1L, "百香果柠檬茶", "酸甜清爽", null, "ACTIVE", 1, List.of(1L, 2L), List.of(
                new ProductSkuDTO(1L, List.of("中杯", "少糖"), new BigDecimal("12.00"), 99, "ACTIVE"),
                new ProductSkuDTO(2L, List.of("大杯", "少糖"), new BigDecimal("15.00"), 99, "ACTIVE")
        )));
        products.put(2L, product(2L, 1L, 2L, "阳光青提多多", "阳光玫瑰青提 + 乳酸菌", null, "ACTIVE", 2, List.of(1L, 2L), List.of(
                new ProductSkuDTO(3L, List.of("中杯", "正常糖"), new BigDecimal("16.00"), 99, "ACTIVE"),
                new ProductSkuDTO(4L, List.of("大杯", "正常糖"), new BigDecimal("19.00"), 99, "ACTIVE")
        )));
        products.put(3L, product(3L, 1L, 3L, "芒芒百香绿茶", "大颗芒果肉 + 百香果", null, "ACTIVE", 3, List.of(1L, 2L), List.of(
                new ProductSkuDTO(5L, List.of("中杯", "少糖"), new BigDecimal("17.00"), 99, "ACTIVE"),
                new ProductSkuDTO(6L, List.of("大杯", "正常糖"), new BigDecimal("20.00"), 99, "ACTIVE")
        )));
    }

    @Override
    public StoreDTO getStore(long id) {
        StoreDTO store = stores.get(id);
        if (store == null) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return store;
    }

    @Override
    public StorefrontDTO getStorefront(long id) {
        StoreDTO store = getStore(id);
        return StorefrontDTO.from(store, getDecoration(id));
    }

    @Override
    public StoreDTO getStoreByQrCode(String qrCode) {
        return stores.values().stream()
                .filter(store -> store.qrCode().equals(qrCode))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    @Override
    public StorefrontDTO getStorefrontByQrCode(String qrCode) {
        StoreDTO store = getStoreByQrCode(qrCode);
        return StorefrontDTO.from(store, getDecoration(store.id()));
    }

    @Override
    public List<StoreDTO> listStores() {
        return stores.values().stream()
                .sorted(Comparator.comparing(StoreDTO::id))
                .toList();
    }

    @Override
    public List<StoreDTO> listStoresByOwner(long ownerId) {
        return stores.values().stream()
                .filter(store -> store.ownerId().equals(ownerId))
                .sorted(Comparator.comparing(StoreDTO::id))
                .toList();
    }

    @Override
    public StoreDTO updateStore(long id, UpdateStoreParams request) {
        StoreDTO current = getStore(id);
        StoreDTO updated = new StoreDTO(
                current.id(),
                current.ownerId(),
                current.styleId(),
                current.styleCode(),
                request.name() == null ? current.name() : request.name(),
                current.category(),
                request.description() == null ? current.description() : request.description(),
                request.logoUrl() == null ? current.avatarUrl() : request.logoUrl(),
                request.coverUrl() == null ? current.coverUrl() : request.coverUrl(),
                current.qrCode(),
                current.address(),
                request.status() == null ? current.status() : request.status()
        );
        stores.put(id, updated);
        return updated;
    }

    @Override
    public List<CategoryDTO> listCategories(long storeId, String module) {
        getStore(storeId);
        return categories.values().stream()
                .filter(category -> category.storeId().equals(storeId))
                .filter(category -> module == null || category.module().equalsIgnoreCase(module))
                .sorted(Comparator.comparingInt(CategoryDTO::sortOrder).thenComparing(CategoryDTO::id))
                .toList();
    }

    @Override
    public CategoryDTO createCategory(long storeId, CategoryUpsertParams request) {
        getStore(storeId);
        CategoryDTO category = new CategoryDTO(
                categoryIdSequence.getAndIncrement(),
                storeId,
                request.module() == null ? "PRODUCT" : request.module().toUpperCase(),
                request.name(),
                request.sortOrder(),
                request.status() == null ? "ACTIVE" : request.status()
        );
        categories.put(category.id(), category);
        return category;
    }

    @Override
    public List<ProductDTO> listProducts(long storeId) {
        getStore(storeId);
        return products.values().stream()
                .filter(product -> product.storeId().equals(storeId))
                .sorted(Comparator.comparingInt(ProductDTO::sortOrder))
                .toList();
    }

    @Override
    public ProductDTO getProduct(long id) {
        ProductDTO product = products.get(id);
        if (product == null) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return product;
    }

    @Override
    public ProductDTO createProduct(long storeId, ProductUpsertParams request) {
        getStore(storeId);
        long id = productIdSequence.getAndIncrement();
        ProductDTO product = product(
                id,
                storeId,
                request.categoryId(),
                request.name(),
                request.description(),
                primaryImage(request),
                request.status() == null ? "ACTIVE" : request.status(),
                request.sortOrder(),
                requireSpecIds(storeId, request.specIds()),
                skuDtos(request.skus())
        );
        products.put(id, product);
        return product;
    }

    @Override
    public ProductDTO updateProduct(long storeId, long productId, ProductUpsertParams request) {
        getStore(storeId);
        ProductDTO current = getProduct(productId);
        if (!current.storeId().equals(storeId)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        ProductDTO updated = product(
                current.id(),
                storeId,
                request.categoryId(),
                request.name(),
                request.description(),
                primaryImage(request),
                request.status() == null ? current.status() : request.status(),
                request.sortOrder(),
                requireSpecIds(storeId, request.specIds()),
                skuDtos(request.skus())
        );
        products.put(productId, updated);
        return updated;
    }

    @Override
    public ProductDTO updateProductStatus(long storeId, long productId, String status) {
        getStore(storeId);
        ProductDTO current = getProduct(productId);
        if (!current.storeId().equals(storeId)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        ProductDTO updated = new ProductDTO(
                current.id(),
                current.storeId(),
                current.categoryId(),
                current.categoryName(),
                current.name(),
                current.description(),
                current.price(),
                current.imageUrl(),
                current.mainImageUrl(),
                current.category(),
                status,
                current.sortOrder(),
                current.specIds(),
                current.skus()
        );
        products.put(productId, updated);
        return updated;
    }

    @Override
    public StoreDecorationDTO getDecoration(long storeId) {
        StoreDTO store = getStore(storeId);
        StyleDTO style = getStyle(store.styleId());
        return new StoreDecorationDTO(
                store.id(),
                store.name(),
                store.avatarUrl(),
                store.coverUrl(),
                bannersByStore.getOrDefault(store.id(), List.of()),
                store.styleId(),
                store.styleCode(),
                style,
                style.theme().layoutVersion(),
                style.theme(),
                mergeMap(style.theme().colors(), colorsByStore.get(store.id())),
                style.theme().iconNames(),
                mergeMap(style.theme().iconUrls(), iconUrlsByStore.get(store.id())),
                mergeMap(style.theme().imageUrls(), imageUrlsByStore.get(store.id())),
                mergeMap(style.theme().copywriting(), copywritingByStore.get(store.id())),
                style.theme().categories()
        );
    }

    @Override
    public StoreDecorationDTO updateDecoration(long storeId, UpdateDecorationParams request) {
        StoreDTO current = getStore(storeId);
        StyleDTO style = request.styleId() == null ? getStyle(current.styleId()) : getStyle(request.styleId());
        stores.put(storeId, new StoreDTO(
                current.id(),
                current.ownerId(),
                style.id(),
                style.code(),
                current.name(),
                current.category(),
                request.description() == null ? current.description() : request.description(),
                request.logoUrl() == null ? current.avatarUrl() : request.logoUrl(),
                request.coverUrl() == null ? current.coverUrl() : request.coverUrl(),
                current.qrCode(),
                current.address(),
                current.status()
        ));
        if (request.banners() != null) {
            bannersByStore.put(storeId, List.copyOf(request.banners()));
        }
        if (request.colors() != null) {
            colorsByStore.put(storeId, copyMap(request.colors()));
        }
        if (request.iconUrls() != null) {
            iconUrlsByStore.put(storeId, copyMap(request.iconUrls()));
        }
        if (request.imageUrls() != null) {
            imageUrlsByStore.put(storeId, copyMap(request.imageUrls()));
        }
        if (request.copywriting() != null) {
            copywritingByStore.put(storeId, copyMap(request.copywriting()));
        }
        return getDecoration(storeId);
    }

    @Override
    public List<StyleDTO> listStyles() {
        return styles.values().stream()
                .sorted(Comparator.comparing(StyleDTO::id))
                .toList();
    }

    @Override
    public StyleDTO getStyle(long id) {
        StyleDTO style = styles.get(id);
        if (style == null) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return style;
    }

    @Override
    public List<SpecDTO> listSpecs(long styleId) {
        getStyle(styleId);
        return new ArrayList<>(specsByStyle.getOrDefault(styleId, List.of()));
    }

    @Override
    public SpecDTO createSpec(long styleId, SpecUpsertParams request) {
        getStyle(styleId);
        SpecDTO spec = new SpecDTO(
                specIdSequence.getAndIncrement(),
                styleId,
                request.name(),
                request.specType(),
                request.required(),
                request.options() == null ? List.of() : List.copyOf(request.options())
        );
        List<SpecDTO> specs = new ArrayList<>(specsByStyle.getOrDefault(styleId, List.of()));
        specs.add(spec);
        specsByStyle.put(styleId, specs);
        return spec;
    }

    @Override
    public SpecDTO updateSpec(long styleId, long specId, SpecUpsertParams request) {
        getStyle(styleId);
        List<SpecDTO> specs = new ArrayList<>(specsByStyle.getOrDefault(styleId, List.of()));
        for (int index = 0; index < specs.size(); index += 1) {
            SpecDTO current = specs.get(index);
            if (current.id().equals(specId)) {
                SpecDTO updated = new SpecDTO(
                        current.id(),
                        styleId,
                        request.name(),
                        request.specType(),
                        request.required(),
                        request.options() == null ? List.of() : List.copyOf(request.options())
                );
                specs.set(index, updated);
                specsByStyle.put(styleId, specs);
                return updated;
            }
        }
        throw new AppException(ErrorCode.NOT_FOUND);
    }

    @Override
    public void deleteSpec(long styleId, long specId) {
        getStyle(styleId);
        boolean linked = products.values().stream()
                .filter(product -> getStore(product.storeId()).styleId().equals(styleId))
                .anyMatch(product -> product.specIds().contains(specId));
        if (linked) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        List<SpecDTO> specs = new ArrayList<>(specsByStyle.getOrDefault(styleId, List.of()));
        boolean removed = specs.removeIf(spec -> spec.id().equals(specId));
        if (!removed) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        specsByStyle.put(styleId, specs);
    }

    private ProductDTO product(
            Long id,
            Long storeId,
            Long categoryId,
            String name,
            String description,
            String imageUrl,
            String status,
            int sortOrder,
            List<Long> specIds,
            List<ProductSkuDTO> skus
    ) {
        CategoryDTO category = requireCategory(storeId, categoryId);
        return new ProductDTO(
                id,
                storeId,
                category.id(),
                category.name(),
                name,
                description,
                minSkuPrice(skus),
                imageUrl,
                imageUrl,
                category.name(),
                status,
                sortOrder,
                specIds,
                skus
        );
    }

    private CategoryDTO requireCategory(long storeId, Long categoryId) {
        if (categoryId == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        CategoryDTO category = categories.get(categoryId);
        if (category == null || !category.storeId().equals(storeId)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return category;
    }

    private List<Long> requireSpecIds(long storeId, List<Long> specIds) {
        if (specIds == null || specIds.isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        List<Long> validIds = listSpecs(getStore(storeId).styleId()).stream().map(SpecDTO::id).toList();
        if (!validIds.containsAll(specIds)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return List.copyOf(specIds);
    }

    private List<ProductSkuDTO> skuDtos(List<ProductSkuParams> skus) {
        if (skus == null || skus.isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return skus.stream()
                .map(sku -> {
                    if (sku.specValues() == null || sku.specValues().isEmpty() || sku.price() == null) {
                        throw new AppException(ErrorCode.BAD_REQUEST);
                    }
                    return new ProductSkuDTO(
                            skuIdSequence.getAndIncrement(),
                            List.copyOf(sku.specValues()),
                            sku.price(),
                            sku.stock(),
                            sku.status() == null ? "ACTIVE" : sku.status()
                    );
                })
                .toList();
    }

    private BigDecimal minSkuPrice(List<ProductSkuDTO> skus) {
        return skus.stream()
                .map(ProductSkuDTO::price)
                .min(BigDecimal::compareTo)
                .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST));
    }

    private String primaryImage(ProductUpsertParams request) {
        return request.mainImageUrl() == null || request.mainImageUrl().isBlank()
                ? request.imageUrl()
                : request.mainImageUrl();
    }

    private StorefrontThemeDTO forestFruitTeaTheme() {
        return new StorefrontThemeDTO(
                "forestFruitTeaCrayon",
                "森系水果茶",
                "customer-storefront-v1",
                mapOfOrdered(
                        "primary", "#6F9646",
                        "secondary", "#B8C77A",
                        "accent", "#F2B94B",
                        "background", "#FBFAEF",
                        "surface", "#FFFDF4",
                        "text", "#4C6040",
                        "mutedText", "#7A866D",
                        "border", "#DCE6C7",
                        "price", "#6F9646"
                ),
                mapOfOrdered(
                        "location", "forest-location",
                        "cart", "forest-cart",
                        "checkout", "forest-checkout",
                        "delivery", "forest-delivery",
                        "sectionLeaf", "forest-leaf"
                ),
                mapOfOrdered(
                        "location", "/static/storefront/forest/icons/location.png",
                        "cart", "/static/storefront/forest/icons/cart.png",
                        "checkout", "/static/storefront/forest/icons/checkout.png",
                        "delivery", "/static/storefront/forest/icons/delivery.png",
                        "sectionLeaf", "/static/storefront/forest/icons/leaf.png"
                ),
                mapOfOrdered(
                        "heroIllustration", "/static/storefront/forest/hero-forest-tea.png",
                        "mascot", "/static/storefront/forest/mascot.png",
                        "productPlaceholder", "/static/storefront/forest/product-placeholder.png",
                        "promoIllustration", "/static/storefront/forest/promo-drink.png"
                ),
                mapOfOrdered(
                        "branchName", "上海环球港店",
                        "heroEyebrow", "小新の",
                        "heroTitle", "水果茶屋",
                        "heroSubtitle", "自然水果 · 新鲜现制",
                        "promoTitle", "鲜果时令上新",
                        "promoSubtitle", "当季水果 · 清爽一夏",
                        "promoActionText", "立即尝鲜"
                ),
                List.of(
                        new StorefrontCategoryDTO("recommend", "人气推荐", "forest-recommend", "/static/storefront/forest/icons/recommend.png", "荐"),
                        new StorefrontCategoryDTO("citrus", "清爽柠檬", "forest-citrus", "/static/storefront/forest/icons/citrus.png", "柠"),
                        new StorefrontCategoryDTO("grape", "多肉葡萄", "forest-grape", "/static/storefront/forest/icons/grape.png", "葡"),
                        new StorefrontCategoryDTO("mango", "香甜芒果", "forest-mango", "/static/storefront/forest/icons/mango.png", "芒"),
                        new StorefrontCategoryDTO("tea", "鲜果茶桶", "forest-tea", "/static/storefront/forest/icons/tea.png", "茶"),
                        new StorefrontCategoryDTO("extra", "加料小料", "forest-extra", "/static/storefront/forest/icons/extra.png", "料")
                )
        );
    }

    private StorefrontThemeDTO hawaiianTheme() {
        return simpleTheme("hawaiian", "夏威夷风", "#2ECC71", "#F9D66E", "#FEF9E7");
    }

    private StorefrontThemeDTO simpleTheme(String code, String name, String primary, String secondary, String background) {
        return new StorefrontThemeDTO(
                code,
                name,
                "customer-storefront-v1",
                mapOfOrdered(
                        "primary", primary,
                        "secondary", secondary,
                        "accent", "#FF8E5E",
                        "background", background,
                        "surface", "#FFFFFF",
                        "text", "#2D3436",
                        "mutedText", "#7A7A6A",
                        "border", "#E8E2C8",
                        "price", primary
                ),
                mapOfOrdered(
                        "location", code + "-location",
                        "cart", code + "-cart",
                        "checkout", code + "-checkout",
                        "delivery", code + "-delivery",
                        "sectionLeaf", code + "-section"
                ),
                Map.of(),
                Map.of(),
                mapOfOrdered(
                        "heroEyebrow", "今日推荐",
                        "heroTitle", name,
                        "heroSubtitle", "新鲜现制 · 即点即取",
                        "promoTitle", "本店上新",
                        "promoSubtitle", "精选好物限时供应",
                        "promoActionText", "去看看"
                ),
                List.of(
                        new StorefrontCategoryDTO("recommend", "人气推荐", code + "-recommend", null, "荐"),
                        new StorefrontCategoryDTO("tea", "饮品", code + "-drink", null, "饮"),
                        new StorefrontCategoryDTO("extra", "加料", code + "-extra", null, "料")
                )
        );
    }

    private Map<String, String> mapOfOrdered(String... pairs) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int index = 0; index < pairs.length; index += 2) {
            map.put(pairs[index], pairs[index + 1]);
        }
        return Map.copyOf(map);
    }

    private Map<String, String> mergeMap(Map<String, String> defaults, Map<String, String> overrides) {
        Map<String, String> merged = new LinkedHashMap<>(defaults);
        if (overrides != null) {
            merged.putAll(overrides);
        }
        return Map.copyOf(merged);
    }

    private Map<String, String> copyMap(Map<String, String> source) {
        return Map.copyOf(new LinkedHashMap<>(source));
    }
}
