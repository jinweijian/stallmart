package com.stallmart.store;

import com.stallmart.store.dto.UpdateStoreParams;
import com.stallmart.store.dto.UpdateDecorationParams;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.product.dto.ProductUpsertParams;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.style.dto.StyleDTO;
import java.util.List;

public interface StoreService {

    StoreDTO getStore(long id);

    StoreDTO getStoreByQrCode(String qrCode);

    List<StoreDTO> listStores();

    List<StoreDTO> listStoresByOwner(long ownerId);

    StoreDTO updateStore(long id, UpdateStoreParams request);

    List<ProductDTO> listProducts(long storeId);

    ProductDTO getProduct(long id);

    ProductDTO createProduct(long storeId, ProductUpsertParams request);

    ProductDTO updateProduct(long storeId, long productId, ProductUpsertParams request);

    StoreDecorationDTO getDecoration(long storeId);

    StoreDecorationDTO updateDecoration(long storeId, UpdateDecorationParams request);

    List<StyleDTO> listStyles();

    StyleDTO getStyle(long id);

    List<SpecDTO> listSpecs(long styleId);
}
