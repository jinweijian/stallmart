package com.stallmart.store.internal.service;

import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.exception.AppException;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.store.dto.UpdateDecorationParams;
import com.stallmart.store.dto.UpdateStoreParams;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.product.dto.ProductUpsertParams;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.store.StoreService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements StoreService {

    private final Map<Long, StoreDTO> stores = new ConcurrentHashMap<>();
    private final Map<Long, ProductDTO> products = new ConcurrentHashMap<>();
    private final Map<Long, StyleDTO> styles = new ConcurrentHashMap<>();
    private final Map<Long, List<SpecDTO>> specsByStyle = new ConcurrentHashMap<>();
    private final AtomicLong productIdSequence = new AtomicLong(4);

    public StoreServiceImpl() {
        styles.put(1L, new StyleDTO(1L, "夏威夷风", "hawaiian", null));
        styles.put(2L, new StyleDTO(2L, "烧烤风", "BBQ", null));
        styles.put(3L, new StyleDTO(3L, "市集风", "market", null));

        specsByStyle.put(1L, List.of(
                new SpecDTO(1L, 1L, "杯型", "SIZE", true, List.of("中杯", "大杯")),
                new SpecDTO(2L, 1L, "甜度", "SWEET", false, List.of("无糖", "少糖", "正常糖"))
        ));

        stores.put(1L, new StoreDTO(
                1L,
                2L,
                1L,
                "hawaiian",
                "海边水果茶",
                "饮品",
                "新鲜水果茶和轻食摊位",
                "/static/default-avatar.png",
                null,
                "stall-001",
                "市集东入口 12 号",
                "OPEN"
        ));

        products.put(1L, new ProductDTO(1L, 1L, "百香果柠檬茶", "酸甜清爽", new BigDecimal("12.00"), null, "饮品", "ACTIVE", 1));
        products.put(2L, new ProductDTO(2L, 1L, "芒果椰椰", "芒果和椰乳", new BigDecimal("16.00"), null, "饮品", "ACTIVE", 2));
        products.put(3L, new ProductDTO(3L, 1L, "烤肠", "现烤热卖", new BigDecimal("8.00"), null, "小吃", "ACTIVE", 3));
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
    public StoreDTO getStoreByQrCode(String qrCode) {
        return stores.values().stream()
                .filter(store -> store.qrCode().equals(qrCode))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
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
        ProductDTO product = new ProductDTO(
                id,
                storeId,
                request.name(),
                request.description(),
                request.price(),
                request.imageUrl(),
                request.category(),
                request.status() == null ? "ACTIVE" : request.status(),
                request.sortOrder()
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
        ProductDTO updated = new ProductDTO(
                current.id(),
                current.storeId(),
                request.name(),
                request.description(),
                request.price(),
                request.imageUrl(),
                request.category(),
                request.status() == null ? current.status() : request.status(),
                request.sortOrder()
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
                store.styleId(),
                store.styleCode(),
                style
        );
    }

    @Override
    public StoreDecorationDTO updateDecoration(long storeId, UpdateDecorationParams request) {
        StoreDTO current = getStore(storeId);
        StyleDTO style = request.styleId() == null ? getStyle(current.styleId()) : getStyle(request.styleId());
        StoreDTO updated = new StoreDTO(
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
        );
        stores.put(storeId, updated);
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
}
