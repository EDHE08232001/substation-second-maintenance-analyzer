-- 为相关表添加 data_set_name 字段
-- 执行前请备份数据库

-- 为 biz_scd_dev_current_info 表添加 data_set_name 字段
ALTER TABLE biz_scd_dev_current_info ADD COLUMN data_set_name VARCHAR(255) COMMENT '数据集名称';

-- 为 biz_scd_dev_info 表添加 data_set_name 字段  
ALTER TABLE biz_scd_dev_info ADD COLUMN data_set_name VARCHAR(255) COMMENT '数据集名称';

-- 为 biz_scd_dev_value 表添加 data_set_name 字段
ALTER TABLE biz_scd_dev_value ADD COLUMN data_set_name VARCHAR(255) COMMENT '数据集名称';

-- 为 biz_scd_dev_check_front 表添加 data_set_name 字段
ALTER TABLE biz_scd_dev_check_front ADD COLUMN data_set_name VARCHAR(255) COMMENT '数据集名称';

-- 为 biz_scd_dev_check_after 表添加 data_set_name 字段
ALTER TABLE biz_scd_dev_check_after ADD COLUMN data_set_name VARCHAR(255) COMMENT '数据集名称';

-- 验证字段是否添加成功
DESCRIBE biz_scd_dev_current_info;
DESCRIBE biz_scd_dev_info;
DESCRIBE biz_scd_dev_value;
DESCRIBE biz_scd_dev_check_front;
DESCRIBE biz_scd_dev_check_after; 