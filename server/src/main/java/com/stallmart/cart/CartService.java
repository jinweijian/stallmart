package com.stallmart.cart;

import com.stallmart.cart.dto.AddCartItemParams;
import com.stallmart.cart.dto.CartDTO;
import java.util.List;

public interface CartService {

    List<CartDTO> listByUser(long userId);

    List<CartDTO> listByStore(long storeId);

    List<CartDTO> listAll();

    CartDTO addItem(long userId, AddCartItemParams request);

    void clear(long userId, long storeId);
}
