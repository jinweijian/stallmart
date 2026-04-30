package com.stallmart.service;

import com.stallmart.model.entity.StoreStyle;
import java.util.List;

public interface StoreStyleService {
    List<StoreStyle> findAll();
    StoreStyle findById(Long id);
}
