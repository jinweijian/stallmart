package com.stallmart.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    void storeAndProductEndpointsMatchMiniProgramContract() throws Exception {
        mockMvc.perform(get("/stores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("小新の水果茶屋"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));

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
                                  "sortOrder": 20,
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("热卖新品"));

        mockMvc.perform(get("/admin/vendor/me/categories?module=PRODUCT")
                        .header("Authorization", "Bearer " + vendorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(4))));
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
    void vendorCanUploadDecorationImageAndUpdateThemeColors() throws Exception {
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
                                  "colors": {
                                    "primary": "#2F7A4F",
                                    "secondary": "#A5C98A",
                                    "accent": "#F2B94B"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.colors.primary").value("#2F7A4F"));
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
}
