package com.stallmart.cart.internal.api;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.AddCartItemParams;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.support.security.CurrentUserResolver;
import com.stallmart.support.web.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final CurrentUserResolver currentUserResolver;

    public CartController(CartService cartService, CurrentUserResolver currentUserResolver) {
        this.cartService = cartService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public Result<List<CartDTO>> list(HttpServletRequest request) {
        return Result.success(cartService.listByUser(currentUserResolver.resolve(request)));
    }

    @PostMapping("/items")
    public Result<CartDTO> addItem(@Valid @RequestBody AddCartItemParams request, HttpServletRequest servletRequest) {
        return Result.success(cartService.addItem(currentUserResolver.resolve(servletRequest), request));
    }

    @DeleteMapping("/stores/{storeId}")
    public Result<Void> clear(@PathVariable long storeId, HttpServletRequest servletRequest) {
        cartService.clear(currentUserResolver.resolve(servletRequest), storeId);
        return Result.success(null);
    }
}
