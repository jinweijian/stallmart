package com.stallmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stallmart.common.exception.BusinessException;
import com.stallmart.common.result.ErrorCode;
import com.stallmart.model.entity.StoreStyle;
import com.stallmart.repository.StoreStyleMapper;
import com.stallmart.service.StoreStyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreStyleServiceImpl implements StoreStyleService {

    private final StoreStyleMapper storeStyleMapper;

    @Override
    public List<StoreStyle> findAll() {
        return storeStyleMapper.selectList(
            new LambdaQueryWrapper<StoreStyle>()
                .eq(StoreStyle::getDeleted, false)
        );
    }

    @Override
    public StoreStyle findById(Long id) {
        StoreStyle style = storeStyleMapper.selectById(id);
        if (style == null || style.getDeleted()) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return style;
    }
}
