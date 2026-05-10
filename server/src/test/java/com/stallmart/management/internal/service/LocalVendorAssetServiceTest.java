package com.stallmart.management.internal.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.stallmart.product.dto.AssetDTO;
import com.stallmart.store.dto.StoreDTO;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class LocalVendorAssetServiceTest {

    @TempDir
    private Path uploadRoot;

    @Test
    void shouldStoreImageUnderStoreFolder_whenUploadIsValid() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "file",
                "product.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{1, 2, 3, 4}
        );

        LocalVendorAssetService service = new LocalVendorAssetService(uploadRoot);

        AssetDTO asset = service.upload(store(), image, "products");

        assertThat(asset.url()).startsWith("/uploads/stores/1/products/");
        assertThat(asset.filename()).endsWith(".png");
        assertThat(asset.size()).isEqualTo(4);
        assertThat(Files.exists(uploadRoot.resolve("stores/1/products").resolve(asset.filename()))).isTrue();
    }

    private StoreDTO store() {
        return new StoreDTO(
                1L,
                2L,
                6L,
                "forestFruitTeaCrayon",
                "小新の水果茶屋",
                "饮品",
                "自然水果",
                "/avatar.png",
                "/cover.png",
                "stall-001",
                "上海环球港店",
                "OPEN"
        );
    }
}
