package com.stallmart.store.internal.service;

import com.stallmart.product.dto.CategoryDTO;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.store.internal.model.CategoryStatus;
import com.stallmart.store.internal.repository.StoreDecorationEntity;
import com.stallmart.store.internal.repository.StoreEntity;
import com.stallmart.style.dto.StorefrontCategoryDTO;
import com.stallmart.style.dto.StorefrontCategoryIconDTO;
import com.stallmart.style.dto.StorefrontThemeDTO;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.support.persistence.JsonSupport;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class StoreDecorationAssembler {

    private final JsonSupport json;

    public StoreDecorationAssembler(JsonSupport json) {
        this.json = json;
    }

    public StoreDecorationDTO assemble(
            StoreEntity store,
            StyleDTO style,
            StoreDecorationEntity decoration,
            List<CategoryDTO> categories
    ) {
        StorefrontThemeDTO theme = style.theme();
        List<StorefrontCategoryIconDTO> categoryIconLibrary = resolveCategoryIconLibrary(theme, decoration);
        return new StoreDecorationDTO(
                store.id,
                store.name,
                store.logoUrl,
                store.coverUrl,
                decoration == null ? List.of() : json.stringList(decoration.bannersJson),
                store.styleId,
                style.code(),
                style,
                theme.layoutVersion(),
                theme,
                mergeMap(theme.colors(), decoration == null ? null : json.stringMap(decoration.colorsJson)),
                theme.iconNames(),
                mergeMap(theme.iconUrls(), decoration == null ? null : json.stringMap(decoration.iconUrlsJson)),
                mergeMap(theme.imageUrls(), decoration == null ? null : json.stringMap(decoration.imageUrlsJson)),
                mergeMap(theme.copywriting(), decoration == null ? null : json.stringMap(decoration.copywritingJson)),
                categoryIconLibrary,
                resolveStorefrontCategories(categories, categoryIconLibrary),
                theme.assetSizes(),
                theme.pageThemes()
        );
    }

    private List<StorefrontCategoryIconDTO> resolveCategoryIconLibrary(
            StorefrontThemeDTO theme,
            StoreDecorationEntity decoration
    ) {
        Map<String, String> overrides = decoration == null ? Map.of() : json.stringMap(decoration.categoryIconUrlsJson);
        return theme.categoryIconLibrary().stream()
                .map(icon -> new StorefrontCategoryIconDTO(
                        icon.key(),
                        icon.name(),
                        overrides.getOrDefault(icon.key(), icon.iconUrl()),
                        icon.fallbackText()
                ))
                .toList();
    }

    private List<StorefrontCategoryDTO> resolveStorefrontCategories(
            List<CategoryDTO> categories,
            List<StorefrontCategoryIconDTO> iconLibrary
    ) {
        Map<String, StorefrontCategoryIconDTO> iconsByKey = new LinkedHashMap<>();
        for (StorefrontCategoryIconDTO icon : iconLibrary) {
            iconsByKey.put(icon.key(), icon);
        }

        List<StorefrontCategoryDTO> storefrontCategories = new ArrayList<>();
        StorefrontCategoryIconDTO recommendIcon = iconsByKey.get("recommend");
        if (recommendIcon != null) {
            storefrontCategories.add(new StorefrontCategoryDTO(
                    "recommend",
                    recommendIcon.name(),
                    recommendIcon.key(),
                    recommendIcon.iconUrl(),
                    recommendIcon.fallbackText(),
                    0,
                    CategoryStatus.ACTIVE
            ));
        }

        categories.stream()
                .filter(category -> category.status() == CategoryStatus.ACTIVE)
                .forEach(category -> {
                    StorefrontCategoryIconDTO icon = iconsByKey.get(category.iconKey());
                    storefrontCategories.add(new StorefrontCategoryDTO(
                            String.valueOf(category.id()),
                            category.name(),
                            category.iconKey(),
                            icon == null ? null : icon.iconUrl(),
                            icon == null ? category.name().substring(0, 1) : icon.fallbackText(),
                            category.sortOrder(),
                            category.status()
                    ));
                });
        return storefrontCategories;
    }

    private Map<String, String> mergeMap(Map<String, String> defaults, Map<String, String> overrides) {
        Map<String, String> merged = new LinkedHashMap<>(defaults == null ? Map.of() : defaults);
        if (overrides != null) {
            merged.putAll(overrides);
        }
        return Map.copyOf(merged);
    }
}
