package com.stallmart.style.dto;

import com.stallmart.store.internal.model.SpecType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SpecUpsertParams(
        @NotNull Long styleId,
        @NotBlank String name,
        @NotBlank SpecType specType,
        boolean required,
        List<String> options
) {
}
