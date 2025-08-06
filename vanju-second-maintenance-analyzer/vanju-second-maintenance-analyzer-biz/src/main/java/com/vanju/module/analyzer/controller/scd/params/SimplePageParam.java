package com.vanju.module.analyzer.controller.scd.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 简单分页参数
 */
@Schema(description = "简单分页参数")
@Data
public class SimplePageParam {

    @Schema(description = "页码，从 1 开始", example = "1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    /**
     * 获取 MyBatis Plus 页码（从 0 开始）
     */
    public Long getMpPageNo() {
        return (long) (pageNo - 1);
    }

    /**
     * 获取 MyBatis Plus 每页大小
     */
    public Long getMpPageSize() {
        return (long) pageSize;
    }
} 