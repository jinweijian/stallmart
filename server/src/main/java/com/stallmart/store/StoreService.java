package com.stallmart.store;

import com.stallmart.store.dto.UpdateStoreParams;
import com.stallmart.store.dto.UpdateDecorationParams;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.store.dto.StorefrontDTO;
import com.stallmart.product.dto.CategoryDTO;
import com.stallmart.product.dto.CategoryUpsertParams;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.product.dto.ProductUpsertParams;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.style.dto.SpecUpsertParams;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.style.dto.StyleDTO;
import java.util.List;

public interface StoreService {

    StoreDTO getStore(long id);

    StorefrontDTO getStorefront(long id);

    StoreDTO getStoreByQrCode(String qrCode);

    StorefrontDTO getStorefrontByQrCode(String qrCode);

    List<StoreDTO> listStores();

    List<StoreDTO> listStoresByOwner(long ownerId);

    StoreDTO updateStore(long id, UpdateStoreParams request);

    List<CategoryDTO> listCategories(long storeId, String module);

    CategoryDTO createCategory(long storeId, CategoryUpsertParams request);

    List<ProductDTO> listProducts(long storeId);

    ProductDTO getProduct(long id);

    ProductDTO createProduct(long storeId, ProductUpsertParams request);

    ProductDTO updateProduct(long storeId, long productId, ProductUpsertParams request);

    ProductDTO updateProductStatus(long storeId, long productId, String status);

    StoreDecorationDTO getDecoration(long storeId);

    StoreDecorationDTO updateDecoration(long storeId, UpdateDecorationParams request);

    List<StyleDTO> listStyles();

    StyleDTO getStyle(long id);

    List<SpecDTO> listSpecs(long styleId);

    SpecDTO createSpec(long styleId, SpecUpsertParams request);

    SpecDTO updateSpec(long styleId, long specId, SpecUpsertParams request);

    void deleteSpec(long styleId, long specId);
}
