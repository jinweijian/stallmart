package com.stallmart.order;

import com.stallmart.order.dto.CreateOrderParams;
import com.stallmart.order.dto.OrderCountsDTO;
import com.stallmart.order.dto.OrderDTO;
import java.util.List;

public interface OrderService {

    OrderDTO create(long userId, CreateOrderParams request);

    OrderDTO get(long userId, long orderId);

    List<OrderDTO> list(long userId);

    List<OrderDTO> listAll();

    List<OrderDTO> listByStore(long storeId);

    OrderCountsDTO counts(long userId);

    OrderDTO accept(long id);

    OrderDTO reject(long id);

    OrderDTO prepare(long id);

    OrderDTO ready(long id);

    OrderDTO complete(long id);
}
