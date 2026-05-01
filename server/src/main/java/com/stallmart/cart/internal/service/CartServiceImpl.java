package com.stallmart.cart.internal.service;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.AddCartItemParams;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.cart.dto.CartItemDTO;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.StoreService;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final StoreService storeService;
    private final AtomicLong idSequence = new AtomicLong(2);
    private final Map<Long, CartDTO> carts = new ConcurrentHashMap<>();

    public CartServiceImpl(StoreService storeService) {
        this.storeService = storeService;
        carts.put(1L, new CartDTO(
                1L,
                1L,
                1L,
                "ACTIVE",
                Instant.now(),
                List.of(new CartItemDTO(2L, "芒果椰椰", 1, storeService.getProduct(2L).price(), "少糖"))
        ));
    }

    @Override
    public List<CartDTO> listByUser(long userId) {
        return carts.values().stream()
                .filter(cart -> cart.userId().equals(userId))
                .sorted(Comparator.comparing(CartDTO::updatedAt).reversed())
                .toList();
    }

    @Override
    public List<CartDTO> listByStore(long storeId) {
        storeService.getStore(storeId);
        return carts.values().stream()
                .filter(cart -> cart.storeId().equals(storeId))
                .sorted(Comparator.comparing(CartDTO::updatedAt).reversed())
                .toList();
    }

    @Override
    public List<CartDTO> listAll() {
        return carts.values().stream()
                .sorted(Comparator.comparing(CartDTO::updatedAt).reversed())
                .toList();
    }

    @Override
    public CartDTO addItem(long userId, AddCartItemParams request) {
        storeService.getStore(request.storeId());
        ProductDTO product = storeService.getProduct(request.productId());
        if (!product.storeId().equals(request.storeId())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        CartDTO current = findUserCart(userId, request.storeId());
        List<CartItemDTO> items = new ArrayList<>(current == null ? List.of() : current.items());
        items.add(new CartItemDTO(product.id(), product.name(), request.quantity(), product.price(), request.specsText()));

        CartDTO updated = new CartDTO(
                current == null ? idSequence.getAndIncrement() : current.id(),
                userId,
                request.storeId(),
                "ACTIVE",
                Instant.now(),
                items
        );
        carts.put(updated.id(), updated);
        return updated;
    }

    @Override
    public void clear(long userId, long storeId) {
        CartDTO current = findUserCart(userId, storeId);
        if (current != null) {
            carts.remove(current.id());
        }
    }

    private CartDTO findUserCart(long userId, long storeId) {
        return carts.values().stream()
                .filter(cart -> cart.userId().equals(userId) && cart.storeId().equals(storeId))
                .findFirst()
                .orElse(null);
    }
}
