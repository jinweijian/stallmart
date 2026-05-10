package com.stallmart.management;

import com.stallmart.product.dto.AssetDTO;
import com.stallmart.store.dto.StoreDTO;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface VendorAssetService {

    AssetDTO upload(StoreDTO store, MultipartFile file, String folder) throws IOException;
}
