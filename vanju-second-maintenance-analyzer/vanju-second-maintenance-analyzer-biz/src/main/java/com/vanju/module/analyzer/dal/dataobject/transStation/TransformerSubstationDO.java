package com.vanju.module.analyzer.dal.dataobject.transStation;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_transformer_substation")
public class TransformerSubstationDO {
    /**
     *
     */
    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * 部门ID
     */
    @TableId
    private Long id;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 父部门ID
     * <p>
     * 关联 {@link #id}
     */
    private Long parentId;
    /**
     * 显示顺序
     */
    private Integer sort;
    /**
     *
     */
    private Integer scd;

    private String stationId;

    private Date createTime;
}
