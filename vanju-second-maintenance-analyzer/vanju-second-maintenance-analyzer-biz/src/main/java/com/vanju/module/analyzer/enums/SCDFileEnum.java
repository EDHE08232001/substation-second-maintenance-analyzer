package com.vanju.module.analyzer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 波形类型枚举
 *
 * @author ZBC
 * @date 2025/03/05
 */
@Getter
@AllArgsConstructor
public enum SCDFileEnum {
    /**
     * 普通波形
     */
    FALSE(0),
    /**
     * 故障波形
     */
    TRUE(1);

    /**
     * 性别
     */
    private final Integer isSCDFile;
}
