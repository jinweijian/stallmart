package com.stallmart.management.dto;

import com.stallmart.cart.dto.CartDTO;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.user.dto.UserProfileDTO;
import java.math.BigDecimal;
import java.util.List;

public record VendorWorkspaceDTO(
        StoreDTO store,
        StoreDecorationDTO decoration,
        List<ProductDTO> products,
        List<OrderDTO> orders,
        List<CartDTO> carts,
        List<UserProfileDTO> users,
        List<StyleDTO> styles,
        long orderCount,
        long cartCount,
        BigDecimal salesAmount
) {
}
