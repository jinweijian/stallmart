package com.stallmart.management.internal.service;

import com.stallmart.management.VendorAssetService;
import com.stallmart.product.dto.AssetDTO;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalVendorAssetService implements VendorAssetService {

    private static final long MAX_IMAGE_SIZE = 10L * 1024L * 1024L;

    private final Path uploadRoot;

    public LocalVendorAssetService() {
        this(Path.of("uploads"));
    }

    LocalVendorAssetService(Path uploadRoot) {
        this.uploadRoot = uploadRoot;
    }

    @Override
    public AssetDTO upload(StoreDTO store, MultipartFile file, String folder) throws IOException {
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        if (file.isEmpty() || file.getSize() > MAX_IMAGE_SIZE || !contentType.startsWith("image/")) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        String extension = extensionOf(file.getOriginalFilename(), contentType);
        String filename = UUID.randomUUID() + extension;
        Path directory = uploadRoot.resolve("stores").resolve(String.valueOf(store.id())).resolve(folder)
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(directory);
        Path target = directory.resolve(filename);
        file.transferTo(target);
        return new AssetDTO("/uploads/stores/" + store.id() + "/" + folder + "/" + filename, filename, file.getSize());
    }

    private String extensionOf(String filename, String contentType) {
        if (filename != null && filename.contains(".")) {
            String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
            if (extension.matches("\\.(png|jpg|jpeg|gif|webp)")) {
                return extension;
            }
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
