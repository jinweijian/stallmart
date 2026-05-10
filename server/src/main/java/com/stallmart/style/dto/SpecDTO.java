package com.stallmart.style.dto;

import com.stallmart.store.internal.model.SpecType;
import java.util.List;

public record SpecDTO(Long id, Long styleId, String name, SpecType specType, boolean required, List<String> options) {
}
