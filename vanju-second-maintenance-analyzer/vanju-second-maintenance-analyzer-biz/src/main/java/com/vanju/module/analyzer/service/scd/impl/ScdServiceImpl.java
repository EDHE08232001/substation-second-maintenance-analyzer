package com.vanju.module.analyzer.service.scd.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vanju.framework.common.pojo.PageResult;
import com.vanju.framework.common.pojo.PageParam;
import com.vanju.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.vanju.module.analyzer.controller.scd.params.GetDataGroupByBatchNoParams;
import com.vanju.module.analyzer.dal.dataobject.scd.*;
import com.vanju.module.analyzer.dal.mysql.scd.*;
import com.vanju.module.analyzer.service.scd.ScdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Service
public class ScdServiceImpl implements ScdService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    ScdRegaddrValMapper scdRegaddrValMapper;
    @Autowired
    ScdRecordMapper scdRecordMapper;
    @Autowired
    DeviceMapper deviceMapper;
    @Autowired
    DeviceDetailMapper deviceDetailMapper;
    @Autowired
    ScdValueMapper scdValueMapper;
    @Autowired
    ScdCurrentModelMapper scdCurrentModelMapper;
    @Autowired
    ScdCurrentInfoMapper scdCurrentInfoMapper;
    @Autowired
    ScdCheckFrontMapper scdCheckFrontMapper;
    @Autowired
    ScdCheckAfterMapper scdCheckAfterMapper;

    @Value("${scd.upload.path}")
    private String uploadPath;

    @Value("${scd.max.size}")
    private long maxFileSize;

    @Override
    public PageResult<ScdRegaddrValDO> getDataGroupByBatchNoByScdId(GetDataGroupByBatchNoParams getDataGroupByBatchNoParams) {
        PageResult<ScdRegaddrValDO> pageList = scdRegaddrValMapper.getPageGroupByBatchNoByScdId(getDataGroupByBatchNoParams);
        return pageList;
    }

    @Override
    public void checkAndCreateTable(String tableName) {
        if (!isTableExists(tableName)) {
            createTable(tableName);
        }
    }
    /**
     * 检查表是否存在
     *
     * @param tableName 表名
     * @return 表是否存在
     */
    public boolean isTableExists(String tableName) {
        String query = "SELECT COUNT(*) FROM information_schema.tables " + "WHERE table_name = ? AND table_schema = DATABASE()";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, tableName);
        return count != null && count > 0;
    }

    /**
     * 创建表
     *
     * @param tableName 表名
     */
    public void createTable(String tableName) {
        // 获取原表结构
        String createTableSql = "CREATE TABLE " + tableName + " LIKE biz_scd_regaddr";
        jdbcTemplate.execute(createTableSql);
    }

    public String saveScdFile(MultipartFile scdFile, String stationId) throws IOException, ParseException, InterruptedException {

        if (scdFile.isEmpty()) {
            throw new IllegalArgumentException("上传的scd文件为空");
        }

        if (scdFile.getSize() > maxFileSize) {
            throw new IllegalArgumentException("scd文件大小超过限制");
        }

        String contentType = scdFile.getContentType();
//        if (contentType == null || !contentType.startsWith("video/")) {
//            throw new IllegalArgumentException("不支持的文件类型");
//        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d HH:mm:ss");
        String dateStr = sdf.format(date);
        String dateStrN = dateStr.replace("/","-").replace(":","_").replace(" ","_");

        String filePath = uploadPath + File.separator + stationId + File.separator + dateStrN;

        File userDir = new File(filePath);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        String originalFilename = scdFile.getOriginalFilename();

        File destFile = new File(userDir.getPath() + File.separator + originalFilename);
        scdFile.transferTo(destFile);

        ScdRecordDO scdRecordDO = new ScdRecordDO();
        scdRecordDO.setFileName(originalFilename);
        scdRecordDO.setStationId(stationId);
        scdRecordDO.setUploadTime(date);
        scdRecordDO.setFilePath(filePath);
        scdRecordMapper.insert(scdRecordDO);

        List<String> createScdModelResult = createScdFileModel(filePath,date,stationId,originalFilename);

        return stationId + File.separator + originalFilename;
    }

    public List<String> createScdFileModel(String filePath,Date date,String stationId,String originalFilename) throws InterruptedException, IOException {
        List<String> result = new ArrayList<>();
//        String filPathN =  filePath+"/devScd.scd";
        String filPathN =  filePath + File.separator + originalFilename;
        ProcessBuilder lsPb = new ProcessBuilder("./scdparse",filPathN);
        //scd程序所在位置
        lsPb.directory(new java.io.File("/home/debian/project/install_env/data/jxyServer/bin"));
        lsPb.redirectErrorStream(true);
        Process lsProcess = lsPb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(lsProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        }
        lsProcess.waitFor();

        parseScdFile(filePath+"/sclparse", date,stationId);

        return result;
    }

    public void parseScdFile(String filePath,Date parse_time,String stationId) {
        if (filePath == null || filePath.trim().isEmpty()) {
            log.error("文件路径不能为空");
            throw new IllegalArgumentException("文件路径不能为空");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("文件不存在: {}", filePath);
            throw new IllegalArgumentException("文件不存在: " + filePath);
        }
        if (!file.isFile()) {
            log.error("指定路径不是文件: {}", filePath);
            throw new IllegalArgumentException("指定路径不是文件: " + filePath);
        }

        log.info("开始解析SCD文件: {}", filePath);

        List<DeviceDO> deviceDOS = new ArrayList<>();
        List<ScdCurrentModelDO> deviceCurrentDOS = new ArrayList<>();
        DeviceDO currentDeviceDO = null;
        String currentDatasetName = null; // 当前数据集名称
        String currentDatasetSign = null; // 当前数据集引用标识
        int lineNumber = 0;
        boolean isFirstLine = true;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"))) {
            String line;
            int debugLineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isEmpty()) {
                    continue;
                }
                if (isFirstLine) {
//                    log.debug("跳过头部信息行: {}", line);
                    isFirstLine = false;
                    continue;
                }
                
                // 添加前几行的调试信息
                if (debugLineCount < 10) {
                    log.debug("第{}行: '{}'", lineNumber, line);
                    debugLineCount++;
                }
                
                // 计算制表符数量来判断层级
                int tabCount = countLeadingTabs(line);
                
                if (tabCount == 0 && line.contains("\t")) {
                    // 装置行：没有制表符前缀，包含制表符
                    if (currentDeviceDO != null) {
                        deviceDOS.add(currentDeviceDO);
//                        log.debug("保存装置: {}", currentDeviceDO.getDeviceName());
                    }

                    String[] signParts = filePath.split("scdFile/", 2);
                    String[] parts = line.split("\t");
                    if (parts.length >= 4) {
                        currentDeviceDO = new DeviceDO();
                        currentDeviceDO.setStationSign(stationId);
                        currentDeviceDO.setDeviceName(parts[0].trim());
                        currentDeviceDO.setDeviceId(parts[1].trim());
                        currentDeviceDO.setIpAddress(parts[2].trim());
                        currentDeviceDO.setParseTime(parse_time);
                        currentDeviceDO.setScdContact(signParts[1].replace("/sclparse", ""));
                        try {
                            currentDeviceDO.setPort(Integer.parseInt(parts[3].trim()));
                        } catch (NumberFormatException e) {
                            currentDeviceDO.setPort(0);
                        }
                        currentDeviceDO.setDetails(new ArrayList<>());
                        currentDatasetName = null; // 重置数据集名称
                    } else {
                        log.warn("装置行格式不正确，列数不足: {}", line);
                    }
                }
                else if (tabCount == 1 && line.contains("\t")) {
                    // 数据集行：一个制表符前缀，包含制表符
                    String[] parts = line.split("\t");
                    log.debug("数据集行原始内容: '{}'", line);
                    log.debug("数据集行分割后: parts.length={}", parts.length);
                    for (int i = 0; i < parts.length; i++) {
                        log.debug("parts[{}] = '{}'", i, parts[i]);
                    }
                    if (parts.length >= 3) {
                        // 数据集行的格式：\t数据集名称\t数据集引用\t类型
                        // parts[0] 是空字符串（因为行以制表符开头）
                        // parts[1] 是数据集名称
                        // parts[2] 是数据集引用
                        currentDatasetName = parts[1].trim(); // 数据集名称
                        currentDatasetSign = parts[2].trim(); // 数据集引用标识
                        log.debug("发现数据集: '{}', 引用标识: '{}'", currentDatasetName, currentDatasetSign);
                    } else {
                        log.warn("数据集行格式不正确: {}", line);
                    }
                }
                else if (tabCount == 2 && currentDeviceDO != null) {
                    // 明细行：两个制表符前缀
                    log.debug("处理明细行: '{}'", line);
                    log.debug("当前数据集名称: '{}'", currentDatasetName);
                    parseDetailLine(line.trim(), currentDeviceDO, stationId, currentDatasetName, currentDatasetSign);
                }
                else {
                    log.debug("跳过未知格式行: {}", line);
                }
            }
            if (currentDeviceDO != null) {
                deviceDOS.add(currentDeviceDO);
//                log.debug("保存最后一个装置: {}", currentDeviceDO.getDeviceName());
            }
//            log.info("SCD文件解析完成: {}, 共发现 {} 个装置", filePath, deviceDOS.size());
            processDevices(deviceDOS,stationId);
        } catch (IOException e) {
            log.error("读取SCD文件失败: {}", filePath, e);
            throw new RuntimeException("读取SCD文件失败: " + filePath, e);
        } catch (Exception e) {
            log.error("解析SCD文件时发生错误: {}", filePath, e);
            throw new RuntimeException("解析SCD文件时发生错误: " + filePath, e);
        }
    }

    /**
     * 计算字符串开头的制表符数量
     */
    private int countLeadingTabs(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == '\t') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private void parseDetailLine(String line, DeviceDO deviceDO, String stationId, String datasetName, String datasetSign) {

        String[] parts = line.split("\t");

        if (parts.length >= 3) {
            DeviceDetailDO detail = new DeviceDetailDO();

            detail.setDescription(parts[0].trim());

            detail.setReference(parts[1].trim());

            try {
                detail.setType(Integer.parseInt(parts[2].trim()));
                detail.setStatus(Integer.parseInt(parts[5].trim()));
            } catch (NumberFormatException e) {
                log.warn("无法解析值字段: {}");
//                detail.setValue(String.valueOf(0)); // 默认值
            }
            detail.setStationSign(stationId);
            detail.setDeviceName(deviceDO.getDeviceName());
            detail.setDeviceId(deviceDO.getDeviceId());
            detail.setScdSign(deviceDO.getScdContact());
            detail.setDataSetName(datasetName); // 设置数据集名称
            detail.setDataSetSign(datasetSign); // 设置数据集引用标识
            
            log.debug("解析明细行: description={}, dataSetName={}, dataSetSign={}", 
                     detail.getDescription(), detail.getDataSetName(), detail.getDataSetSign());

            // 添加到装置的明细列表
            deviceDO.getDetails().add(detail);

        } else {
            log.warn("无效的明细行格式: {}", line);
        }
    }

    // 处理解析出的装置信息
    private void processDevices(List<DeviceDO> deviceDOS,String stationId) {
        List<ScdCurrentModelDO> ScdCurrentModelList = scdCurrentModelMapper.selectList(
            new LambdaQueryWrapperX<ScdCurrentModelDO>()
                .eq(ScdCurrentModelDO::getStationSign, stationId)
        );
        List<ScdCurrentInfoDO> ScdCurrentInfoList = scdCurrentInfoMapper.selectList(
                new LambdaQueryWrapperX<ScdCurrentInfoDO>()
                        .eq(ScdCurrentInfoDO::getStationSign, stationId)
        );
//        int Station = 0;
        if(ScdCurrentModelList.size()==0 && ScdCurrentInfoList.size()==0){
//            Station = 1;
        }
        else{
            scdCurrentModelMapper.delete(ScdCurrentModelDO::getStationSign, stationId);
            scdCurrentInfoMapper.delete(ScdCurrentInfoDO::getStationSign, stationId);
        }

        // 比较ScdCurrentModelList和deviceDOS
        if (ScdCurrentModelList.size() != deviceDOS.size()) {
            log.info("装置数量发生变化，原有数量：{}，新数量：{}", ScdCurrentModelList.size(), deviceDOS.size());
        } else {
            log.info("装置数量一致，数量为：{}", deviceDOS.size());
        }

        for (DeviceDO deviceDO : deviceDOS) {
            ScdCurrentModelDO currentModelDO = new ScdCurrentModelDO();
            BeanUtils.copyProperties(deviceDO, currentModelDO);

            List<DeviceDetailDO> details = deviceDO.getDetails();
            log.info("装置 {} 有 {} 个明细", deviceDO.getDeviceName(), details.size());

            List<ScdCurrentInfoDO> currentDetails = new ArrayList<>();
            for (DeviceDetailDO detail : details) {
                ScdCurrentInfoDO info = new ScdCurrentInfoDO();
                BeanUtils.copyProperties(detail, info);
                // 手动设置dataSetName字段，确保它被正确复制
                info.setDataSetName(detail.getDataSetName());
                // 手动设置dataSetSign字段，确保它被正确复制
                info.setDataSetSign(detail.getDataSetSign());
                log.debug("明细: description={}, dataSetName={}, dataSetSign={}", 
                         detail.getDescription(), detail.getDataSetName(), detail.getDataSetSign());
                currentDetails.add(info);
            }

//            deviceMapper.insert(deviceDO);
//            deviceDetailMapper.insert(details);

            scdCurrentModelMapper.insert(currentModelDO);
            log.info("插入装置模型数据: {}", currentModelDO.getDeviceName());
            
            if (!currentDetails.isEmpty()) {
                scdCurrentInfoMapper.insert(currentDetails);
                log.info("插入装置信息数据: {} 条", currentDetails.size());
            } else {
                log.warn("装置 {} 没有明细数据", deviceDO.getDeviceName());
            }

        }
//        if(Station == 1){
//            // 用于标记是否有变化
//            boolean hasChange = false;
//
//            // 遍历新解析的deviceDOS
//            for (DeviceDO deviceDO : deviceDOS) {
//                // 查找是否有对应的旧数据
//                ScdCurrentModelDO oldModel = ScdCurrentModelList.stream()
//                        .filter(m ->
//                                m.getDeviceId().equals(deviceDO.getDeviceId()) &&
//                                        m.getDeviceName().equals(deviceDO.getDeviceName())
//                        )
//                        .findFirst()
//                        .orElse(null);
//
//                if (oldModel == null) {
//                    log.info("新增装置：deviceId={}, deviceName={}", deviceDO.getDeviceId(), deviceDO.getDeviceName());
//                    hasChange = true;
//                    continue;
//                }
//                // 比较字段
//                if (!safeEquals(oldModel.getIpAddress(), deviceDO.getIpAddress())
//                        || !safeEquals(oldModel.getPort(), deviceDO.getPort())
//                        || !safeEquals(oldModel.getDevStatus(), deviceDO.getDevStatus())) {
//                    log.info("装置信息发生变化，deviceId={}, deviceName={}", deviceDO.getDeviceId(), deviceDO.getDeviceName());
//                    hasChange = true;
//                }
//            }
//        }
    }

    // 工具方法，避免空指针
    private boolean safeEquals(Object a, Object b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    public List<ScdDeviceValueDO> createScdFileData(String filePath,Date date,String type) throws InterruptedException, IOException {
        List<String> result = new ArrayList<>();
        String filPathN =  filePath+"/sclparse";
        ProcessBuilder lsPb = new ProcessBuilder("./getdata",filPathN);
        //sclparse程序所在位置
        lsPb.directory(new java.io.File("/home/debian/project/install_env/data/jxyServer/bin"));
        lsPb.redirectErrorStream(true);
        Process lsProcess = lsPb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(lsProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        }
        lsProcess.waitFor();

        List<ScdDeviceValueDO> createDateList = parseScdValue(filePath+"/scldata1", date,type);

        if("采集".equals(type)){
            String[] signParts = filePath.split("scdFile/", 2);
            List<ScdCurrentInfoDO> scdCurrentModelList = scdCurrentInfoMapper.selectList(
                    new LambdaQueryWrapperX<ScdCurrentInfoDO>()
                            .eq(ScdCurrentInfoDO::getScdSign, signParts[1])
            );

            // 1. 构建reference到ScdCurrentInfoDO的映射，方便查找
            Map<String, ScdCurrentInfoDO> referenceToCurrentInfo = new HashMap<>();
            for (ScdCurrentInfoDO info : scdCurrentModelList) {
                referenceToCurrentInfo.put(info.getReference(), info);
            }

            // 2. 遍历createDateList，关联并赋值
            for (ScdDeviceValueDO deviceValue : createDateList) {
                if (deviceValue.getDetails() == null) continue;
                for (ScdValueDO valueDO : deviceValue.getDetails()) {
                    ScdCurrentInfoDO currentInfo = referenceToCurrentInfo.get(valueDO.getReference());
                    if (currentInfo != null) {
                        currentInfo.setValue(valueDO.getValue());
                        currentInfo.setValueUpdateTime(new Date());
                    }
                }
            }
            scdCurrentInfoMapper.updateBatch(scdCurrentModelList);
        }

        return createDateList;
    }

    public List<ScdDeviceValueDO> parseScdValue(String filePath,Date parse_time,String type) {
        if (filePath == null || filePath.trim().isEmpty()) {
            log.error("文件路径不能为空");
            throw new IllegalArgumentException("文件路径不能为空");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("文件不存在: {}", filePath);
            throw new IllegalArgumentException("文件不存在: " + filePath);
        }
        if (!file.isFile()) {
            log.error("指定路径不是文件: {}", filePath);
            throw new IllegalArgumentException("指定路径不是文件: " + filePath);
        }

        List<ScdDeviceValueDO> deviceDOS = new ArrayList<>();
        ScdDeviceValueDO currentDeviceDO = null;
        String currentDatasetName = null; // 当前数据集名称
        String currentDatasetSign = null; // 当前数据集引用标识
        int lineNumber = 0;
        boolean isFirstLine = true;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isEmpty()) {
                    continue;
                }
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // 计算制表符数量来判断层级
                int tabCount = countLeadingTabs(line);
                
                if (tabCount == 0 && line.contains("\t")) {
                    // 装置行：没有制表符前缀，包含制表符
                    if (currentDeviceDO != null) {
                        deviceDOS.add(currentDeviceDO);
                    }
                    String[] signParts = filePath.split("scdFile/", 2);
                    String[] parts = line.split("\t");
                    if (parts.length >= 4) {
                        currentDeviceDO = new ScdDeviceValueDO();
                        currentDeviceDO.setDeviceName(parts[0].trim());
                        currentDeviceDO.setDeviceId(parts[1].trim());
                        currentDeviceDO.setIpAddress(parts[2].trim());
                        currentDeviceDO.setParseTime(parse_time);
                        currentDeviceDO.setScdContact(signParts[1].replace("/scldata1", ""));
                        try {
                            currentDeviceDO.setPort(Integer.parseInt(parts[3].trim()));
                        } catch (NumberFormatException e) {
                            currentDeviceDO.setPort(0);
                        }
                        currentDeviceDO.setDetails(new ArrayList<>());
                        currentDatasetName = null; // 重置数据集名称
                    } else {
                        log.warn("装置行格式不正确，列数不足: {}", line);
                    }
                }
                else if (tabCount == 1 && line.contains("\t")) {
                    // 数据集行：一个制表符前缀，包含制表符
                    String[] parts = line.split("\t");
                    if (parts.length >= 3) {
                        // 数据集行的格式：\t数据集名称\t数据集引用\t类型
                        // parts[0] 是空字符串（因为行以制表符开头）
                        // parts[1] 是数据集名称
                        // parts[2] 是数据集引用
                        currentDatasetName = parts[1].trim(); // 数据集名称
                        currentDatasetSign = parts[2].trim(); // 数据集引用标识
                        log.debug("发现数据集: {}, 引用标识: {}", currentDatasetName, currentDatasetSign);
                    } else {
                        log.warn("数据集行格式不正确: {}", line);
                    }
                }
                else if (tabCount == 2 && currentDeviceDO != null) {
                    // 明细行：两个制表符前缀
                    parseValueDetailLine(line.trim(), currentDeviceDO, parse_time, currentDatasetName, currentDatasetSign);
                }
                else {
                    log.debug("跳过未知格式行: {}", line);
                }
            }
            if (currentDeviceDO != null) {
                deviceDOS.add(currentDeviceDO);
            }

            processDevicesValue(deviceDOS,type);

            return deviceDOS;
        } catch (IOException e) {
            log.error("读取SCD文件失败: {}", filePath, e);
            throw new RuntimeException("读取SCD文件失败: " + filePath, e);
        } catch (Exception e) {
            log.error("解析SCD文件时发生错误: {}", filePath, e);
            throw new RuntimeException("解析SCD文件时发生错误: " + filePath, e);
        }
    }

    private void parseValueDetailLine(String line, ScdDeviceValueDO deviceDO, Date parse_time, String datasetName, String datasetSign) {

        String[] parts = line.split("\t");

        if (parts.length >= 3) {
            ScdValueDO detail = new ScdValueDO();

            detail.setDescription(parts[0].trim());
            detail.setReference(parts[1].trim());
            detail.setValue(parts[3].trim());
            detail.setCheckTime(parse_time);

            try {
                detail.setType(Integer.parseInt(parts[2].trim()));
                detail.setStatus(Integer.parseInt(parts[5].trim()));
            } catch (NumberFormatException e) {
                log.warn("无法解析值字段: {}");
            }

            detail.setDeviceName(deviceDO.getDeviceName());
            detail.setDeviceId(deviceDO.getDeviceId());
            detail.setScdSign(deviceDO.getScdContact());
            detail.setDataSetName(datasetName); // 设置数据集名称
            detail.setDataSetSign(datasetSign); // 设置数据集引用标识

            deviceDO.getDetails().add(detail);
        } else {
            log.warn("无效的明细行格式: {}", line);
        }
    }

    private void processDevicesValue(List<ScdDeviceValueDO> deviceDOS,String type) {
        if(("采集").equals(type)){
            for (ScdDeviceValueDO deviceDO : deviceDOS) {
                List<ScdValueDO> details = deviceDO.getDetails();
                scdValueMapper.insert(details);
            }
        }
        else if(("巡检-前").equals(type)){
            List<ScdCheckFrontDO> scdCheckFrontList = new ArrayList<>();
            for (ScdDeviceValueDO deviceDO : deviceDOS) {
                for(ScdValueDO scdValueDO:deviceDO.getDetails()){
                    ScdCheckFrontDO scdCheckFront = new ScdCheckFrontDO();
                    scdCheckFront.setDescription(scdValueDO.getDescription());
                    scdCheckFront.setReference(scdValueDO.getReference());
                    scdCheckFront.setType(scdValueDO.getType());
                    scdCheckFront.setValue(scdValueDO.getValue());
                    scdCheckFront.setStatus(scdValueDO.getStatus());
                    scdCheckFront.setDeviceName(deviceDO.getDeviceName());
                    scdCheckFront.setDeviceId(deviceDO.getDeviceId());
                    scdCheckFront.setScdSign(getScdSign(scdValueDO));
                    scdCheckFront.setCheckTime(scdValueDO.getCheckTime());
                    scdCheckFront.setStationSign(scdValueDO.getScdSign().split("/")[0]);
                    scdCheckFront.setDataSetName(scdValueDO.getDataSetName()); // 设置数据集名称
                    scdCheckFront.setDataSetSign(scdValueDO.getDataSetSign()); // 设置数据集引用标识

                    scdCheckFrontList.add(scdCheckFront);
                }
            }
            scdCheckFrontMapper.insert(scdCheckFrontList);
        }
        else if(("巡检-后").equals(type)){
            List<ScdCheckAfterDO> scdCheckAfterList = new ArrayList<>();
            for (ScdDeviceValueDO deviceDO : deviceDOS) {
                for(ScdValueDO scdValueDO:deviceDO.getDetails()){
                    ScdCheckAfterDO scdCheckAfter = new ScdCheckAfterDO();
                    scdCheckAfter.setDescription(scdValueDO.getDescription());
                    scdCheckAfter.setReference(scdValueDO.getReference());
                    scdCheckAfter.setType(scdValueDO.getType());
                    scdCheckAfter.setValue(scdValueDO.getValue());
                    scdCheckAfter.setStatus(scdValueDO.getStatus());
                    scdCheckAfter.setDeviceName(deviceDO.getDeviceName());
                    scdCheckAfter.setDeviceId(deviceDO.getDeviceId());
                    scdCheckAfter.setScdSign(scdValueDO.getScdSign());
                    scdCheckAfter.setCheckTime(scdValueDO.getCheckTime());
                    scdCheckAfter.setStationSign(scdValueDO.getScdSign().split("/")[0]);
                    scdCheckAfter.setDataSetName(scdValueDO.getDataSetName()); // 设置数据集名称
                    scdCheckAfter.setDataSetSign(scdValueDO.getDataSetSign()); // 设置数据集引用标识

                    scdCheckAfterList.add(scdCheckAfter);
                }
            }
            scdCheckAfterMapper.insert(scdCheckAfterList);
        }

    }

    private static String getScdSign(ScdValueDO scdValueDO) {
        return scdValueDO.getScdSign();
    }

    public Page<ScdCurrentInfoDO> selectModelData(String deviceName, String scdContact, String type, Integer PageNo, Integer PageSize) {

        int typeCount = -1;

        if("定值".equals(type)){
            typeCount = 1;
        }
        else if("装置参数".equals(type)){
            typeCount = 2;
        }
        else if("光功率".equals(type)){
            typeCount = 3;
        }
        else if("压板".equals(type)){
            typeCount = 4;
        }
        else if("遥信".equals(type)){
            typeCount = 5;
        }
        else if("遥测".equals(type)){
            typeCount = 6;
        }
        else {
            typeCount = 0;
        }

        LambdaQueryWrapperX<ScdCurrentInfoDO> queryWrapper = new LambdaQueryWrapperX<ScdCurrentInfoDO>();
        if(typeCount == 4){
            queryWrapper.eqIfPresent(ScdCurrentInfoDO::getScdSign, scdContact)
                    .eqIfPresent(ScdCurrentInfoDO::getDeviceName, deviceName)
                    .eqIfPresent(ScdCurrentInfoDO::getType, typeCount)
                    .eqIfPresent(ScdCurrentInfoDO::getDataSetName, "保护压板数据集");
        }
        else {
            queryWrapper.eqIfPresent(ScdCurrentInfoDO::getScdSign, scdContact)
                    .eqIfPresent(ScdCurrentInfoDO::getDeviceName, deviceName)
                    .eqIfPresent(ScdCurrentInfoDO::getType, typeCount);
        }

        Page<ScdCurrentInfoDO> page = new Page<>(PageNo, PageSize);

        Page<ScdCurrentInfoDO> pageResult = scdCurrentInfoMapper.selectPage(page, queryWrapper);

        int count = (PageNo-1) * PageSize + 1;
        for(ScdCurrentInfoDO devD : pageResult.getRecords()){
            devD.setId((long) count);
            count++;
        }

        if(typeCount == 4){
            for(ScdCurrentInfoDO devD : pageResult.getRecords()){
                if(devD.getDescription().contains("软压板")){
                    devD.setYaBanType("软压板");
                }
                else {
                    devD.setYaBanType("硬压板");
                }
            }
        }

        return pageResult;
    }

    public List<String> getCheckTime(String stationSign,String type){

        if("巡检-前".equals(type)){
            List<ScdCheckFrontDO> ScdCurrentModelList = scdCheckFrontMapper.selectList(
                    new LambdaQueryWrapperX<ScdCheckFrontDO>()
                            .eq(ScdCheckFrontDO::getStationSign, stationSign)
            );
            List<Date> checkTimeList = ScdCurrentModelList.stream()
                    .map(ScdCheckFrontDO::getCheckTime)
                    .distinct()
                    .collect(Collectors.toList());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<String> checkTimeListFinal = new ArrayList<>();
            for(Date date:checkTimeList){
                checkTimeListFinal.add(sdf.format(date));
            }
            return checkTimeListFinal;
        }
        else if("巡检-后".equals(type)){
            List<ScdCheckAfterDO> ScdCurrentModelList = scdCheckAfterMapper.selectList(
                    new LambdaQueryWrapperX<ScdCheckAfterDO>()
                            .eq(ScdCheckAfterDO::getStationSign, stationSign)
            );
            List<Date> checkTimeList = ScdCurrentModelList.stream()
                    .map(ScdCheckAfterDO::getCheckTime)
                    .distinct()
                    .collect(Collectors.toList());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<String> checkTimeListFinal = new ArrayList<>();
            for(Date date:checkTimeList){
                checkTimeListFinal.add(sdf.format(date));
            }
            return checkTimeListFinal;
        }
        return null;
    }

    public List<ScdCheckFrontDO> getScdCheckFrontData(String stationSign,String option,String time) throws ParseException {

        int typeCount = -1;

        if("定值".equals(option)){
            typeCount = 1;
        }
        else if("装置参数".equals(option)){
            typeCount = 2;
        }
        else if("光功率".equals(option)){
            typeCount = 3;
        }
        else if("压板".equals(option)){
            typeCount = 4;
        }
        else if("遥信".equals(option)){
            typeCount = 5;
        }
        else if("遥测".equals(option)){
            typeCount = 6;
        }
        else if("All".equals(option)){
            typeCount = -1;
        }
        else {
            typeCount = 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(time);
        List<ScdCheckFrontDO> ScdCheckFrontList = scdCheckFrontMapper.selectList(
                new LambdaQueryWrapperX<ScdCheckFrontDO>()
                        .eq(ScdCheckFrontDO::getStationSign, stationSign)
                        .eq(!(typeCount == -1),ScdCheckFrontDO::getType, typeCount)
                        .eq(ScdCheckFrontDO::getCheckTime, date)
        );

        return ScdCheckFrontList;
    }

    public List<ScdCheckAfterDO> getScdCheckAfterData(String stationSign,String option,String time) throws ParseException {

        int typeCount = -1;

        if("定值".equals(option)){
            typeCount = 1;
        }
        else if("装置参数".equals(option)){
            typeCount = 2;
        }
        else if("光功率".equals(option)){
            typeCount = 3;
        }
        else if("压板".equals(option)){
            typeCount = 4;
        }
        else if("遥信".equals(option)){
            typeCount = 5;
        }
        else if("遥测".equals(option)){
            typeCount = 6;
        }
        else if("All".equals(option)){
            typeCount = -1;
        }
        else {
            typeCount = 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(time);
        List<ScdCheckAfterDO> ScdCheckAfterList = scdCheckAfterMapper.selectList(
                new LambdaQueryWrapperX<ScdCheckAfterDO>()
                        .eq(ScdCheckAfterDO::getStationSign, stationSign)
                        .eq(!(typeCount == -1),ScdCheckAfterDO::getType, typeCount)
                        .eq(ScdCheckAfterDO::getCheckTime, date)
        );

        return ScdCheckAfterList;
    }

}
