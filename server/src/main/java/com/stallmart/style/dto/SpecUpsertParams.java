package com.stallmart.style.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SpecUpsertParams(
        @NotNull Long styleId,
        @NotBlank String name,
        @NotBlank String specType,
        boolean required,
        List<String> options
) {
}
