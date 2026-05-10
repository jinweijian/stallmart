package com.stallmart.cart.internal.service;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.AddCartItemParams;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.cart.dto.CartItemDTO;
import com.stallmart.cart.internal.model.CartStatus;
import com.stallmart.cart.internal.repository.CartEntity;
import com.stallmart.cart.internal.repository.CartItemEntity;
import com.stallmart.cart.internal.repository.CartItemRepository;
import com.stallmart.cart.internal.repository.CartRepository;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.StoreService;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    private final StoreService storeService;
    private final CartRepository cartRepository;
    private final CartItemRepository itemRepository;

    public CartServiceImpl(StoreService storeService, CartRepository cartRepository, CartItemRepository itemRepository) {
        this.storeService = storeService;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<CartDTO> listByUser(long userId) {
        return toDTOs(cartRepository.findByUserIdOrderByUpdatedAtDesc(userId));
    }

    @Override
    public List<CartDTO> listByStore(long storeId) {
        storeService.getStore(storeId);
        return toDTOs(cartRepository.findByStoreIdOrderByUpdatedAtDesc(storeId));
    }

    @Override
    public List<CartDTO> listAll() {
        return toDTOs(cartRepository.findAllByOrderByUpdatedAtDesc());
    }

    @Override
    @Transactional
    public CartDTO addItem(long userId, AddCartItemParams request) {
        storeService.getStore(request.storeId());
        ProductDTO product = storeService.getProduct(request.productId());
        if (!product.storeId().equals(request.storeId())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        CartEntity cart = cartRepository.findByUserIdAndStoreId(userId, request.storeId()).orElseGet(() -> {
            CartEntity created = new CartEntity();
            created.userId = userId;
            created.storeId = request.storeId();
            created.status = CartStatus.ACTIVE;
            return created;
        });
        cart.updatedAt = Instant.now();
        CartEntity savedCart = cartRepository.save(cart);

        CartItemEntity item = new CartItemEntity();
        item.cartId = savedCart.id;
        item.productId = product.id();
        item.productName = product.name();
        item.quantity = request.quantity();
        item.unitPrice = product.price();
        item.specsText = request.specsText();
        itemRepository.save(item);

        return toDTO(savedCart);
    }

    @Override
    @Transactional
    public void clear(long userId, long storeId) {
        cartRepository.findByUserIdAndStoreId(userId, storeId).ifPresent(cartRepository::delete);
    }

    private CartDTO toDTO(CartEntity cart) {
        return toDTO(cart, itemRepository.findByCartIdOrderByIdAsc(cart.id));
    }

    private List<CartDTO> toDTOs(List<CartEntity> carts) {
        if (carts.isEmpty()) {
            return List.of();
        }
        List<Long> cartIds = carts.stream().map(cart -> cart.id).toList();
        Map<Long, List<CartItemEntity>> itemsByCartId = itemRepository.findByCartIdInOrderByCartIdAscIdAsc(cartIds).stream()
                .collect(Collectors.groupingBy(item -> item.cartId, LinkedHashMap::new, Collectors.toList()));
        return carts.stream()
                .map(cart -> toDTO(cart, itemsByCartId.getOrDefault(cart.id, List.of())))
                .toList();
    }

    private CartDTO toDTO(CartEntity cart, List<CartItemEntity> items) {
        return new CartDTO(
                cart.id,
                cart.userId,
                cart.storeId,
                cart.status,
                cart.updatedAt,
                items.stream()
                        .map(this::toItemDTO)
                        .toList()
        );
    }

    private CartItemDTO toItemDTO(CartItemEntity item) {
        return new CartItemDTO(item.productId, item.productName, item.quantity, item.unitPrice, item.specsText);
    }
}
