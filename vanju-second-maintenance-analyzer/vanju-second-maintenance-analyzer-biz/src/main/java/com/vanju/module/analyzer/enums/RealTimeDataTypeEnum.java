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
public enum RealTimeDataTypeEnum {
    /**
     * 遥信
     */
    REMOTE_SIGNALING(0),
    /**
     * 遥测
     */
    TELEMETRY(1);

    /**
     * 性别
     */
    private final Integer type;
}
