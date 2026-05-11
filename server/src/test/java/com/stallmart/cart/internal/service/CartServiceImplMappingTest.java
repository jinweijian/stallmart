package com.stallmart.cart.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stallmart.cart.dto.CartDTO;
import com.stallmart.cart.internal.model.CartStatus;
import com.stallmart.cart.internal.repository.CartEntity;
import com.stallmart.cart.internal.repository.CartItemEntity;
import com.stallmart.cart.internal.repository.CartItemRepository;
import com.stallmart.cart.internal.repository.CartRepository;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.internal.model.StoreStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceImplMappingTest {

    @Mock
    private StoreService storeService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository itemRepository;

    @Test
    void shouldLoadCartItemsInBatch_whenListByStore() {
        CartEntity first = cart(1L, 10L);
        CartEntity second = cart(2L, 20L);
        when(storeService.getStore(1L)).thenReturn(store());
        when(cartRepository.findByStoreIdOrderByUpdatedAtDesc(1L)).thenReturn(List.of(first, second));
        when(itemRepository.findByCartIdInOrderByCartIdAscIdAsc(List.of(1L, 2L)))
                .thenReturn(List.of(item(100L, 1L), item(200L, 2L)));
        CartServiceImpl service = new CartServiceImpl(storeService, cartRepository, itemRepository);

        List<CartDTO> carts = service.listByStore(1L);

        assertThat(carts).hasSize(2);
        assertThat(carts.get(0).items()).hasSize(1);
        assertThat(carts.get(1).items()).hasSize(1);
        verify(itemRepository).findByCartIdInOrderByCartIdAscIdAsc(List.of(1L, 2L));
        verify(itemRepository, never()).findByCartIdOrderByIdAsc(1L);
        verify(itemRepository, never()).findByCartIdOrderByIdAsc(2L);
    }

    private StoreDTO store() {
        return new StoreDTO(1L, 2L, 6L, "forestFruitTeaCrayon", "小新の水果茶屋", "饮品", "自然水果",
                "/avatar.png", "/cover.png", "stall-001", "上海环球港店", StoreStatus.OPEN);
    }

    private CartEntity cart(long id, long userId) {
        CartEntity cart = new CartEntity();
        cart.id = id;
        cart.userId = userId;
        cart.storeId = 1L;
        cart.status = CartStatus.ACTIVE;
        cart.updatedAt = Instant.parse("2026-05-10T00:00:00Z");
        return cart;
    }

    private CartItemEntity item(long id, long cartId) {
        CartItemEntity item = new CartItemEntity();
        item.id = id;
        item.cartId = cartId;
        item.productId = 1L;
        item.productName = "柚子柠檬茶";
        item.quantity = 1;
        item.unitPrice = new BigDecimal("12.00");
        item.specsText = "中杯";
        return item;
    }
}
