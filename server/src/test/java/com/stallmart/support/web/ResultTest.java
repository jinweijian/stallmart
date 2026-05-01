package com.stallmart.support.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("统一响应测试")
public class ResultTest {

    @Test
    void shouldUseCurrentContract_whenSuccess() {
        Result<String> result = Result.success("ok");

        assertThat(result.code()).isEqualTo(200);
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.data()).isEqualTo("ok");
        assertThat(result.timestamp()).isPositive();
    }

    @Test
    void shouldHaveNoData_whenError() {
        Result<Void> result = Result.error(10004, "not_found");

        assertThat(result.code()).isEqualTo(10004);
        assertThat(result.message()).isEqualTo("not_found");
        assertThat(result.data()).isNull();
    }
}
