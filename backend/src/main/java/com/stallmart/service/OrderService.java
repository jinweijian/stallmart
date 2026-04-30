package com.stallmart.service;

import com.stallmart.model.dto.CreateOrderDTO;
import com.stallmart.model.entity.Order;
import java.util.List;

public interface OrderService {
    Order create(CreateOrderDTO dto, Long customerId);
    Order findById(Long id);
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByStoreId(Long storeId);
    void updateStatus(Long id, String status);
    void accept(Long id);
    void reject(Long id, String reason);
    void prepare(Long id);
    void ready(Long id);
    void complete(Long id);
}
