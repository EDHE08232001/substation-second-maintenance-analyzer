package com.vanju.module.analyzer.controller.scd.params;

import com.vanju.framework.common.pojo.PageParam;
import lombok.Data;

/**
 *
 */
@Data
public class GetDiffDataParams extends PageParam {
    private Long type;
    private Long scdId;
    private Long batchNo;
}
