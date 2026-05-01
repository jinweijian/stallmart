package com.stallmart.style.dto;

import java.util.List;

public record SpecDTO(Long id, Long styleId, String name, String specType, boolean required, List<String> options) {
}
