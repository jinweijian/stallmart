package com.stallmart.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
                .andExpect(jsonPath("$.data.name").value("海边水果茶"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));

        mockMvc.perform(get("/stores/1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.data[0].price").value(12.00));
    }

    @Test
    void authLoginAndRefreshReturnTokens() throws Exception {
        mockMvc.perform(post("/auth/wechat/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"wx-code","nickname":"测试用户","avatarUrl":"/avatar.png"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.data.user.nickname").value("测试用户"));
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
        mockMvc.perform(get("/admin/vendor/me/summary")
                        .header("X-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.store.id").value(1))
                .andExpect(jsonPath("$.data.products", hasSize(3)))
                .andExpect(jsonPath("$.data.carts", hasSize(1)));

        mockMvc.perform(post("/admin/vendor/me/products")
                        .header("X-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "黑糖珍珠鲜奶",
                                  "description": "商家后台新增商品",
                                  "price": 18.00,
                                  "category": "饮品",
                                  "status": "ACTIVE",
                                  "sortOrder": 8
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("黑糖珍珠鲜奶"));

        mockMvc.perform(get("/stores/1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(4)))
                .andExpect(jsonPath("$.data[3].name").value("黑糖珍珠鲜奶"));
    }

    @Test
    void platformCanEnterVendorWorkspace() throws Exception {
        mockMvc.perform(get("/admin/platform/vendors/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.store.name").value("海边水果茶"))
                .andExpect(jsonPath("$.data.decoration.styleCode").value("hawaiian"))
                .andExpect(jsonPath("$.data.users", hasSize(2)));
    }
}
