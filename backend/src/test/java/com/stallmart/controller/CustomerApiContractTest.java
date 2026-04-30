package com.stallmart.controller;

import com.stallmart.common.result.Result;
import com.stallmart.model.dto.CreateOrderDTO;
import com.stallmart.model.entity.Order;
import com.stallmart.model.entity.OrderItem;
import com.stallmart.model.entity.Product;
import com.stallmart.model.entity.Store;
import com.stallmart.model.entity.User;
import com.stallmart.model.vo.OrderVO;
import com.stallmart.model.vo.ProductVO;
import com.stallmart.model.vo.StoreVO;
import com.stallmart.model.vo.UserVO;
import com.stallmart.repository.OrderItemMapper;
import com.stallmart.service.OrderService;
import com.stallmart.service.ProductService;
import com.stallmart.service.StoreService;
import com.stallmart.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerApiContractTest {

    @Test
    void storeDetailReturnsCustomerHomeContract() {
        StoreService storeService = mock(StoreService.class);
        StoreController controller = new StoreController(storeService);
        Store store = new Store();
        store.setId(1L);
        store.setName("小新の水果茶屋");
        store.setDescription("自然水果 · 新鲜现制");
        store.setAvatarUrl("https://example.com/store.png");
        store.setQrCode("qr-fruit-tea");
        store.setStyleId(6L);
        store.setStatus("active");
        when(storeService.findById(1L)).thenReturn(store);

        Result<StoreVO> result = controller.getStore(1L);

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals(1L, result.getData().getId());
        assertEquals("小新の水果茶屋", result.getData().getName());
        assertEquals("自然水果 · 新鲜现制", result.getData().getDescription());
        assertEquals(6L, result.getData().getStyleId());
        assertEquals("active", result.getData().getStatus());
    }

    @Test
    void productListReturnsCustomerMenuContract() {
        ProductService productService = mock(ProductService.class);
        ProductController controller = new ProductController(productService);
        Product product = new Product();
        product.setId(101L);
        product.setStoreId(1L);
        product.setName("霸气西柚柠檬茶");
        product.setDescription("西柚果肉 + 香水柠檬 + 茉莉绿茶");
        product.setPrice(new BigDecimal("18.00"));
        product.setImageUrl("https://example.com/grapefruit-tea.png");
        product.setSpecIds("1,2");
        product.setStatus("active");
        product.setSortOrder(1);
        when(productService.findByStoreId(1L)).thenReturn(List.of(product));

        Result<List<ProductVO>> result = controller.getProducts(1L);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
        ProductVO data = result.getData().get(0);
        assertEquals(101L, data.getId());
        assertEquals("霸气西柚柠檬茶", data.getName());
        assertEquals(new BigDecimal("18.00"), data.getPrice());
        assertEquals("active", data.getStatus());
        assertEquals(1, data.getSortOrder());
    }

    @Test
    void myOrdersReturnCustomerOrderContract() {
        OrderService orderService = mock(OrderService.class);
        OrderItemMapper orderItemMapper = mock(OrderItemMapper.class);
        OrderController controller = new OrderController(orderService, orderItemMapper);
        HttpServletRequest request = mock(HttpServletRequest.class);
        Order order = new Order();
        order.setId(501L);
        order.setOrderNo("ORD202604300001");
        order.setCustomerId(9L);
        order.setStoreId(1L);
        order.setTotalAmount(new BigDecimal("34.00"));
        order.setStatus("ready");
        order.setConfirmCode("A108");
        order.setRemark("少冰");
        order.setCreatedAt(LocalDateTime.of(2026, 4, 30, 12, 30));
        OrderItem item = new OrderItem();
        item.setId(9001L);
        item.setOrderId(501L);
        item.setProductId(101L);
        item.setProductName("霸气西柚柠檬茶");
        item.setPrice(new BigDecimal("18.00"));
        item.setQuantity(1);
        item.setSubtotal(new BigDecimal("18.00"));
        item.setSpecName("标准杯");
        when(request.getAttribute("userId")).thenReturn(9L);
        when(orderService.findByCustomerId(9L)).thenReturn(List.of(order));
        when(orderItemMapper.selectList(any())).thenReturn(List.of(item));

        Result<List<OrderVO>> result = controller.getMyOrders(request);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
        OrderVO data = result.getData().get(0);
        assertEquals("ORD202604300001", data.getOrderNo());
        assertEquals("ready", data.getStatus());
        assertEquals("A108", data.getConfirmCode());
        assertEquals(1, data.getItems().size());
        assertEquals("霸气西柚柠檬茶", data.getItems().get(0).getProductName());
    }

    @Test
    void createOrderUsesAuthenticatedCustomerAndReturnsConfirmCode() {
        OrderService orderService = mock(OrderService.class);
        OrderItemMapper orderItemMapper = mock(OrderItemMapper.class);
        OrderController controller = new OrderController(orderService, orderItemMapper);
        HttpServletRequest request = mock(HttpServletRequest.class);
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setStoreId(1L);
        dto.setRemark("少糖");
        CreateOrderDTO.OrderItemDTO itemDTO = new CreateOrderDTO.OrderItemDTO();
        itemDTO.setProductId(101L);
        itemDTO.setQuantity(2);
        itemDTO.setSpecName("标准杯");
        dto.setItems(List.of(itemDTO));
        Order order = new Order();
        order.setId(502L);
        order.setOrderNo("ORD202604300002");
        order.setCustomerId(9L);
        order.setStoreId(1L);
        order.setTotalAmount(new BigDecimal("36.00"));
        order.setStatus("pending");
        order.setConfirmCode("B205");
        order.setRemark("少糖");
        order.setCreatedAt(LocalDateTime.of(2026, 4, 30, 12, 40));
        when(request.getAttribute("userId")).thenReturn(9L);
        when(orderService.create(dto, 9L)).thenReturn(order);
        when(orderItemMapper.selectList(any())).thenReturn(List.of());

        Result<OrderVO> result = controller.createOrder(dto, request);

        assertEquals(200, result.getCode());
        assertEquals("ORD202604300002", result.getData().getOrderNo());
        assertEquals("pending", result.getData().getStatus());
        assertEquals("B205", result.getData().getConfirmCode());
        verify(orderService).create(dto, 9L);
    }

    @Test
    void userProfileReturnsMaskedPhoneAndRole() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController(userService);
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(9L);
        user.setNickname("小新");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setPhone("13800138000");
        user.setHasPhone(true);
        user.setRole("customer");
        when(request.getAttribute("userId")).thenReturn(9L);
        when(userService.findById(9L)).thenReturn(user);

        Result<UserVO> result = controller.getProfile(request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("小新", result.getData().getNickname());
        assertEquals("138****8000", result.getData().getPhone());
        assertEquals("customer", result.getData().getRole());
    }
}
