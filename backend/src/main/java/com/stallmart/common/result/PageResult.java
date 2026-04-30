package com.stallmart.common.result;

import lombok.Data;
import java.util.List;

/**
 * 分页响应封装
 */
@Data
public class PageResult<T> {

    private List<T> list;
    private Pagination pagination;

    @Data
    public static class Pagination {
        private int page;
        private int size;
        private long total;
        private int totalPages;
    }

    public static <T> PageResult<T> of(List<T> list, int page, int size, long total) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);

        Pagination pagination = new Pagination();
        pagination.setPage(page);
        pagination.setSize(size);
        pagination.setTotal(total);
        pagination.setTotalPages((int) Math.ceil((double) total / size));

        result.setPagination(pagination);
        return result;
    }
}
