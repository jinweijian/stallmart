package com.stallmart.management;

import com.stallmart.order.dto.OrderDTO;
import com.stallmart.store.dto.StoreDTO;

public interface VendorOrderCommandService {

    OrderDTO transition(StoreDTO store, long orderId, String action);
}
