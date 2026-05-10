package com.stallmart.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void h5LocalOriginCanCallAppApi() throws Exception {
        mockMvc.perform(options("/app/bootstrap")
                        .header("Origin", "http://localhost:10086")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "content-type,x-client-version,x-platform"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:10086"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("GET")));
    }

    @Test
    void storeAndProductEndpointsMatchMiniProgramContract() throws Exception {
        mockMvc.perform(get("/stores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("小新の水果茶屋"))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andExpect(jsonPath("$.data.decoration.categoryIconLibrary", hasSize(greaterThanOrEqualTo(6))))
                .andExpect(jsonPath("$.data.decoration.categories[0].iconKey").value("recommend"))
                .andExpect(jsonPath("$.data.decoration.categories[0].iconUrl", notNullValue()))
                .andExpect(jsonPath("$.data.decoration.categories[1].iconKey").value("category1"))
                .andExpect(jsonPath("$.data.decoration.categories[1].iconUrl").value("/static/storefront/forest/icons/category-1.png"))
                .andExpect(jsonPath("$.data.decoration.categories[2].iconKey").value("category2"))
                .andExpect(jsonPath("$.data.decoration.categories[2].iconUrl").value("/static/storefront/forest/icons/category-2.png"));

        mockMvc.perform(get("/stores/1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.data[0].price").value(12.00));
    }

    @Test
    void authLoginAndRefreshReturnTokens() throws Exception {
        String response = mockMvc.perform(post("/auth/wechat/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"wx-code","nickname":"测试用户","avatarUrl":"/avatar.png"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.data.user.nickname").value("测试用户"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String refreshToken = response.replaceAll(".*\\\"refreshToken\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()));
    }

    @Test
    void createOrderThenVendorAcceptsIt() throws Exception {
        String response = mockMvc.perform(post("/orders")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "storeId": 1,
                                  "remark": "不要吸管",
                                  "items": [
                                    {"productId": 1, "quantity": 2, "specsText": "大杯"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("NEW"))
                .andExpect(jsonPath("$.data.totalAmount").value(24.00))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String orderId = response.replaceAll(".*\\\"id\\\":(\\d+).*", "$1");

        mockMvc.perform(put("/orders/" + orderId + "/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
    }

    @Test
    void adminProductChangesAreVisibleToMiniProgramStoreEndpoint() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");

        mockMvc.perform(get("/admin/vendor/me/summary")
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.store.id").value(1))
                .andExpect(jsonPath("$.data.products", hasSize(3)))
                .andExpect(jsonPath("$.data.carts", hasSize(1)));

        mockMvc.perform(post("/admin/vendor/me/products")
                        .header("Authorization", "Bearer " + vendorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "黑糖珍珠鲜奶",
                                  "description": "商家后台新增商品",
                                  "categoryId": 1,
                                  "mainImageUrl": "/uploads/stores/1/products/milk-tea.jpg",
                                  "specIds": [1, 2],
                                  "skus": [
                                    {
                                      "specValues": ["中杯", "少糖"],
                                      "price": 18.00,
                                      "stock": 88,
                                      "status": "ACTIVE"
                                    }
                                  ],
                                  "status": "ACTIVE",
                                  "sortOrder": 8
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("黑糖珍珠鲜奶"))
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.price").value(18.00))
                .andExpect(jsonPath("$.data.skus", hasSize(1)));

        mockMvc.perform(get("/stores/1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(4)))
                .andExpect(jsonPath("$.data[3].name").value("黑糖珍珠鲜奶"));
    }

    @Test
    void vendorCanCreateProductCategory() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");

        mockMvc.perform(post("/admin/vendor/me/categories")
                        .header("Authorization", "Bearer " + vendorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "module": "PRODUCT",
                                  "name": "热卖新品",
                                  "iconKey": "category1",
                                  "sortOrder": 20,
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("热卖新品"))
                .andExpect(jsonPath("$.data.iconKey").value("category1"));

        mockMvc.perform(get("/admin/vendor/me/categories?module=PRODUCT")
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(4))))
                .andExpect(jsonPath("$.data[3].iconKey").value("category1"));

        mockMvc.perform(put("/admin/vendor/me/categories/4")
                        .header("Authorization", "Bearer " + vendorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "module": "PRODUCT",
                                  "name": "热卖新品",
                                  "iconKey": "category2",
                                  "sortOrder": 20,
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.iconKey").value("category2"));
    }

    @Test
    void vendorCanUpdateProductSaleStatus() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");

        mockMvc.perform(put("/admin/vendor/me/products/1/off-sale")
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));

        mockMvc.perform(put("/admin/vendor/me/products/1/sold-out")
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("SOLD_OUT"));

        mockMvc.perform(put("/admin/vendor/me/products/1/on-sale")
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void cannotDeleteLinkedSpec() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/admin/vendor/me/specs/1")
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10000));
    }

    @Test
    void vendorCanUploadProductImage() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");
        MockMultipartFile image = new MockMultipartFile(
                "file",
                "product.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{1, 2, 3, 4}
        );

        mockMvc.perform(multipart("/admin/vendor/me/assets/product-image")
                        .file(image)
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.url", notNullValue()));
    }

    @Test
    void vendorCanUploadDecorationImageAndUpdateStoreDisplayFields() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");
        MockMultipartFile image = new MockMultipartFile(
                "file",
                "banner.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{1, 2, 3, 4}
        );

        mockMvc.perform(multipart("/admin/vendor/me/assets/decoration-image")
                        .file(image)
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.url", notNullValue()));

        mockMvc.perform(put("/admin/vendor/me/decoration")
                        .header("Authorization", "Bearer " + vendorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "styleId": 6,
                                  "logoUrl": "/uploads/decoration/new-logo.png",
                                  "coverUrl": "/uploads/decoration/new-cover.png",
                                  "banners": ["/uploads/decoration/banner.png"],
                                  "description": "只更新商家自己的展示信息"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.logoUrl").value("/uploads/decoration/new-logo.png"))
                .andExpect(jsonPath("$.data.coverUrl").value("/uploads/decoration/new-cover.png"))
                .andExpect(jsonPath("$.data.banners[0]").value("/uploads/decoration/banner.png"))
                .andExpect(jsonPath("$.data.colors.primary").value("#6F9646"));
    }

    @Test
    void vendorCannotOverrideStylePackageFieldsFromDecorationApi() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");

        mockMvc.perform(put("/admin/vendor/me/decoration")
                        .header("Authorization", "Bearer " + vendorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "styleId": 6,
                                  "colors": {
                                    "primary": "#2F7A4F"
                                  },
                                  "copywriting": {
                                    "heroTitle": "商家私改风格文案"
                                  },
                                  "iconUrls": {
                                    "cart": "/uploads/decoration/cart.png"
                                  },
                                  "categoryIconUrls": {
                                    "recommend": "/uploads/decoration/recommend.png"
                                  },
                                  "imageUrls": {
                                    "heroIllustration": "/uploads/decoration/hero.png"
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10000));
    }

    @Test
    void vendorCanReadCustomerOrderRecords() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");

        mockMvc.perform(get("/admin/vendor/me/users/1/orders")
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].userId").value(1));
    }

    @Test
    void platformCanEnterVendorWorkspace() throws Exception {
        String platformToken = loginAdmin("platform", "platform123");

        mockMvc.perform(get("/admin/platform/vendors/1/summary")
                        .header("Authorization", "Bearer " + platformToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.store.name").value("小新の水果茶屋"))
                .andExpect(jsonPath("$.data.decoration.styleCode").value("forestFruitTeaCrayon"))
                .andExpect(jsonPath("$.data.decoration.layoutVersion").value("customer-storefront-v1"))
                .andExpect(jsonPath("$.data.decoration.colors.primary").value("#6F9646"))
                .andExpect(jsonPath("$.data.users", hasSize(2)));
    }

    @Test
    void vendorTokenCannotReadPlatformApis() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");

        mockMvc.perform(get("/admin/platform/vendors")
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(10005));
    }

    @Test
    void platformCanCreateUpdateAndDeleteUnusedStylePackage() throws Exception {
        String platformToken = loginAdmin("platform", "platform123");

        String response = mockMvc.perform(post("/admin/platform/styles")
                        .header("Authorization", "Bearer " + platformToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stylePayload("果园测试风", "orchardTest", "INACTIVE", "#547D42")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("果园测试风"))
                .andExpect(jsonPath("$.data.code").value("orchardTest"))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String styleId = response.replaceAll(".*\\\"id\\\":(\\d+).*", "$1");

        mockMvc.perform(get("/admin/platform/styles/" + styleId)
                        .header("Authorization", "Bearer " + platformToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("orchardTest"));

        mockMvc.perform(put("/admin/platform/styles/" + styleId)
                        .header("Authorization", "Bearer " + platformToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stylePayload("果园测试风 Pro", "orchardTest", "ACTIVE", "#315F3E")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("果园测试风 Pro"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.version").value(2))
                .andExpect(jsonPath("$.data.theme.colors.primary").value("#315F3E"));

        mockMvc.perform(delete("/admin/platform/styles/" + styleId)
                        .header("Authorization", "Bearer " + platformToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/admin/platform/styles/" + styleId)
                        .header("Authorization", "Bearer " + platformToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void platformCannotDeleteStylePackageUsedByStore() throws Exception {
        String platformToken = loginAdmin("platform", "platform123");

        mockMvc.perform(delete("/admin/platform/styles/6")
                        .header("Authorization", "Bearer " + platformToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10000));
    }

    @Test
    void vendorCannotWritePlatformStylePackage() throws Exception {
        String vendorToken = loginAdmin("vendor", "vendor123");

        mockMvc.perform(post("/admin/platform/styles")
                        .header("Authorization", "Bearer " + vendorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stylePayload("越权风格", "vendorForbiddenStyle", "ACTIVE", "#315F3E")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(10005));
    }

    @Test
    void vendorCannotSelectInactiveStylePackage() throws Exception {
        String platformToken = loginAdmin("platform", "platform123");
        String vendorToken = loginAdmin("vendor", "vendor123");

        mockMvc.perform(put("/admin/platform/styles/1/unpublish")
                        .header("Authorization", "Bearer " + platformToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));

        mockMvc.perform(put("/admin/vendor/me/decoration")
                        .header("Authorization", "Bearer " + vendorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"styleId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10000));
    }

    private String loginAdmin(String account, String password) throws Exception {
        String response = mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"" + account + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return response.replaceAll(".*\\\"accessToken\\\":\\\"([^\\\"]+)\\\".*", "$1");
    }

    private String stylePayload(String name, String code, String status, String primaryColor) {
        return """
                {
                  "name": "%s",
                  "code": "%s",
                  "previewUrl": "/static/storefront/forest/preview.png",
                  "status": "%s",
                  "theme": {
                    "code": "%s",
                    "name": "%s",
                    "layoutVersion": "customer-storefront-v1",
                    "colors": {
                      "primary": "%s",
                      "secondary": "#B8C77A",
                      "accent": "#F2B94B",
                      "background": "#FBFAEF",
                      "surface": "#FFFDF4",
                      "text": "#4C6040",
                      "mutedText": "#7A866D",
                      "border": "#DCE6C7",
                      "price": "%s"
                    },
                    "iconNames": {
                      "location": "forest-location",
                      "cart": "forest-cart",
                      "checkout": "forest-checkout",
                      "delivery": "forest-delivery",
                      "sectionLeaf": "forest-leaf"
                    },
                    "iconUrls": {},
                    "imageUrls": {},
                    "copywriting": {
                      "heroEyebrow": "今日推荐",
                      "heroTitle": "%s",
                      "heroSubtitle": "新鲜现制",
                      "promoTitle": "本店上新",
                      "promoSubtitle": "精选好物",
                      "promoActionText": "去看看"
                    },
                    "categoryIconLibrary": [
                      {
                        "key": "recommend",
                        "name": "人气推荐",
                        "iconUrl": null,
                        "fallbackText": "荐"
                      }
                    ],
                    "assetSizes": {
                      "tabBarReserve": "132rpx"
                    },
                    "pageThemes": {
                      "home": {
                        "sectionTitle": "人气推荐"
                      }
                    }
                  }
                }
                """.formatted(name, code, status, code, name, primaryColor, primaryColor, name);
    }
}
