package com.stallmart.store.internal.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stallmart.product.dto.CategoryDTO;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.store.internal.repository.StoreDecorationEntity;
import com.stallmart.store.internal.repository.StoreEntity;
import com.stallmart.style.dto.StorefrontCategoryIconDTO;
import com.stallmart.style.dto.StorefrontThemeDTO;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.support.persistence.JsonSupport;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StoreDecorationAssemblerTest {

    @Test
    void shouldMergeThemeDefaultsAndDecorationOverrides_whenBuildDecoration() {
        JsonSupport json = new JsonSupport(new ObjectMapper());
        StoreDecorationAssembler assembler = new StoreDecorationAssembler(json);
        StoreDecorationEntity decoration = new StoreDecorationEntity();
        decoration.storeId = 1L;
        decoration.bannersJson = json.write(List.of("/banner.png"));
        decoration.colorsJson = json.write(Map.of("primary", "#315F3E"));
        decoration.categoryIconUrlsJson = json.write(Map.of("category1", "/uploads/category-1.png"));

        StoreDecorationDTO result = assembler.assemble(
                store(),
                style(),
                decoration,
                List.of(new CategoryDTO(1L, 1L, "PRODUCT", "清爽柠檬", "category1", 10, "ACTIVE"))
        );

        assertThat(result.colors()).containsEntry("primary", "#315F3E");
        assertThat(result.colors()).containsEntry("surface", "#FFFDF4");
        assertThat(result.banners()).containsExactly("/banner.png");
        assertThat(result.categoryIconLibrary())
                .extracting(StorefrontCategoryIconDTO::iconUrl)
                .contains("/uploads/category-1.png");
        assertThat(result.categories()).extracting("id").containsExactly("recommend", "1");
        assertThat(result.categories().get(1).iconUrl()).isEqualTo("/uploads/category-1.png");
    }

    private StoreEntity store() {
        StoreEntity store = new StoreEntity();
        store.id = 1L;
        store.ownerId = 2L;
        store.styleId = 6L;
        store.name = "小新の水果茶屋";
        store.logoUrl = "/logo.png";
        store.coverUrl = "/cover.png";
        return store;
    }

    private StyleDTO style() {
        StorefrontThemeDTO theme = new StorefrontThemeDTO(
                "forestFruitTeaCrayon",
                "森系水果茶",
                "customer-storefront-v1",
                Map.of("primary", "#6F9646", "surface", "#FFFDF4"),
                Map.of("cart", "cart"),
                Map.of("cart", "/cart.png"),
                Map.of("heroIllustration", "/hero.png"),
                Map.of("heroTitle", "水果茶屋"),
                List.of(
                        new StorefrontCategoryIconDTO("recommend", "人气推荐", "/recommend.png", "荐"),
                        new StorefrontCategoryIconDTO("category1", "清爽柠檬", "/category-1.png", "柠")
                ),
                Map.of("tabBarReserve", "132rpx"),
                Map.of("home", Map.of("sectionTitle", "人气推荐"))
        );
        return new StyleDTO(6L, "森系水果茶", "forestFruitTeaCrayon", "/preview.png", theme, "ACTIVE", 1);
    }
}
