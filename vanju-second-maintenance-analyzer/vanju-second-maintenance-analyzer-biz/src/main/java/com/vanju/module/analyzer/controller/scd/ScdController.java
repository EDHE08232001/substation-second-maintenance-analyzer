package com.vanju.module.analyzer.controller.scd;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vanju.framework.common.pojo.CommonResult;
import com.vanju.framework.common.pojo.PageParam;
import com.vanju.framework.common.pojo.PageResult;
import com.vanju.framework.common.util.object.BeanUtils;
import com.vanju.module.analyzer.controller.scd.params.GetDataGroupByBatchNoParams;
import com.vanju.module.analyzer.controller.scd.params.GetDiffDataParams;
import com.vanju.module.analyzer.controller.scd.params.GetModelDataParams;
import com.vanju.module.analyzer.controller.scd.vo.ScdBatchVO;
import com.vanju.module.analyzer.controller.scd.vo.ScdRegAddrValDiffVO;
import com.vanju.module.analyzer.dal.dataobject.scd.*;
import com.vanju.module.analyzer.dal.mysql.scd.ScdRegaddrMapper;
import com.vanju.module.analyzer.dal.mysql.scd.ScdRegaddrValMapper;
import com.vanju.module.analyzer.service.scd.ScdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.vanju.framework.common.pojo.CommonResult.success;
import static com.vanju.framework.common.pojo.CommonResult.error;

/**
 * 二次检修仪测试 Controller
 */
@Slf4j
@RestController
@RequestMapping("/scd")
public class ScdController {
    private String BATCH_NO_KEY = "scd:batch_no";
    //    private String scdFilePath = "/home/debian/project/install_env/data/jxyServer/scdFile/";
    //    private String cmdBinPath = "/home/debian/project/install_env/data/jxyServer/bin";

    @Value("${vanju.scd.file-path}")
    private String scdFilePath;

    @Value("${vanju.scd.cmd-bin-path}")
    private String cmdBinPath;

    @Autowired
    ScdService scdService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ScdRegaddrMapper scdRegaddrMapper;
    @Autowired
    ScdRegaddrValMapper scdRegaddrValMapper;

    @GetMapping("/run-script")
    public List<String> listLibDirectoryWithPwd() throws Exception {
        List<String> result = new ArrayList();
        ProcessBuilder lsPb = new ProcessBuilder("./scdparse", scdFilePath + "devScd.scd");
        lsPb.directory(new File(cmdBinPath));
        lsPb.redirectErrorStream(true);
        Process lsProcess = lsPb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(lsProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        }
        lsProcess.waitFor();
        return result;
    }

    @GetMapping("/sclparse/{scdId}/{parentId}")
    public CommonResult<Boolean> sclparse(@PathVariable("scdId") Long scdId, @PathVariable("parentId") Long parentId) {

        String tableName = "biz_scd_regaddr_" + parentId;
        scdService.checkAndCreateTable(tableName);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////


//        String sclparseFilePath = scdFilePath + "sclparse";
//        //        String sclparseFilePath = "C:\\Users\\ZBC\\Desktop\\sclparse";
//        try {
//            //            BufferedReader reader = new BufferedReader(new FileReader(sclparseFilePath));
//            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sclparseFilePath), "GBK"));
//            String line;
//            int lineNumber = 1;
//
//            List<ScdRegaddrDO> scdRegaddrDOList = new ArrayList(16);
//
//            while ((line = reader.readLine()) != null) {
//                lineNumber++;
//                if (lineNumber <= 3) continue;
//                // 在这里添加你的解析逻辑
//                scdRegaddrDOList.add(sclparseLine(scdId, line));
//            }
//
//            if (!CollectionUtil.isEmpty(scdRegaddrDOList)) {
//                scdRegaddrMapper.insert(scdRegaddrDOList);
//                return CommonResult.success(true);
//            }
//        } catch (IOException e) {
//            log.error("Error reading file: {}", e);
//        }
        return CommonResult.success(false);
    }

    @GetMapping("/exeCmdAndGetData/{scdId}")
    public CommonResult<Boolean> exeCmdAndGetData(@PathVariable("scdId") Long scdId) {
        String sclDataFilePath = scdFilePath + "scldata1";
        List<String> result = new ArrayList();
        try {
            ProcessBuilder lsPb = new ProcessBuilder("./getdata", scdFilePath + "sclparse");
            lsPb.directory(new File(cmdBinPath));
            lsPb.redirectErrorStream(true);
            Process lsProcess = lsPb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(lsProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line);
                }
            }
            lsProcess.waitFor(20, TimeUnit.SECONDS);

            // 2. 检查文件是否生成
            File file = new File(sclDataFilePath);
            if (!file.exists() || !file.canRead()) {
                log.error("文件未生成或不可读: {}", sclDataFilePath);
            }

            // 3. 获取 batchNo 和 batchDate
            Long batchNo = redisTemplate.opsForValue().increment(BATCH_NO_KEY);
            Date batchDate = new Date();

            // 4. 读取并解析文件内容
            List<ScdRegaddrValDO> dataList = new ArrayList(16);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                String line;
                int lineNumber = 1;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (lineNumber <= 3) continue; // 跳过前两行

                    ScdRegaddrValDO item = getDataLine(batchNo, batchDate, scdId, line);
                    dataList.add(item);
                }
            }

            // 5. 写入数据库
            if (!CollectionUtil.isEmpty(dataList)) {
                scdRegaddrValMapper.insert(dataList);
            }

            return CommonResult.success(true);

        } catch (Exception e) {
            log.error("执行命令或处理文件失败", e);
        }
        return CommonResult.success(false);
    }


    @GetMapping("/getdata/{scdId}")
    public CommonResult<Boolean> getdata(@PathVariable("scdId") Long scdId) {
        //        String sclparseFilePath = scdFilePath + "sclparse";
        String sclparseFilePath = "C:\\Users\\ZBC\\Desktop\\scldata1";
        try {
            // 获取 batchNo：自动递增
            Long batchNo = redisTemplate.opsForValue().increment(BATCH_NO_KEY);
            Date batchDate = new Date();

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sclparseFilePath), "GBK"));
            String line;
            int lineNumber = 1;

            List<ScdRegaddrValDO> scdRegaddrDOList = new ArrayList(16);

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= 3) continue;
                // 在这里添加你的解析逻辑
                scdRegaddrDOList.add(getDataLine(batchNo, batchDate, scdId, line));
            }

            if (!CollectionUtil.isEmpty(scdRegaddrDOList)) {
                scdRegaddrValMapper.insert(scdRegaddrDOList);
                return CommonResult.success(true);
            }
        } catch (IOException e) {
            log.error("Error reading file: {}", e);
        }
        return CommonResult.success(false);
    }


    @GetMapping("/getDataGroupByBatchNoByScdId")
    public CommonResult<PageResult<ScdBatchVO>> getDataGroupByBatchNoByScdId(GetDataGroupByBatchNoParams getDataGroupByBatchNoParams) {
        PageResult<ScdRegaddrValDO> pageList = scdService.getDataGroupByBatchNoByScdId(getDataGroupByBatchNoParams);
        return success(BeanUtils.toBean(pageList, ScdBatchVO.class));
    }

    @GetMapping("/getDiffData")
    public CommonResult<PageResult<ScdRegAddrValDiffVO>> getDiffData(GetDiffDataParams getDiffDataParams) {
        //        PageResult<ScdRegAddrValDiffVO> pageList = scdService.getDiffData(getDiffDataParams);
        return success(null);
    }

    private static ScdRegaddrDO sclparseLine(Long scdId, String line) {
        String[] parts = line.split("\\s+");
        ScdRegaddrDO scdRegaddrDO = new ScdRegaddrDO().setName(parts[1]).setRegAddr(parts[2]).setType(parts[3]).setScdId(scdId).setCreateDatetime(new Date());
        return scdRegaddrDO;
    }

    private static ScdRegaddrValDO getDataLine(Long batchNo, Date batchDate, Long scdId, String dataline) {
        String[] parts = dataline.split("\\s+");
        ScdRegaddrValDO scdRegaddrValDO = new ScdRegaddrValDO().setScdId(scdId).setBatchNo(batchNo).setRegAddr(parts[2]).setRegAddrVal(parts[4]).setCreateDatetime(batchDate);
        return scdRegaddrValDO;
    }

    @PostMapping("/saveScd")
    public ResponseEntity<?> saveVideo(@RequestParam("scdFile") MultipartFile scdFile, @RequestParam String stationId) {
        try {
            String result = scdService.saveScdFile(scdFile,stationId);
            return ResponseEntity.ok(new ApiResponse(200, "scd保存成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(500, "scd保存失败：" + e.getMessage(), null));
        }
    }

//    @GetMapping("/getTest")
//    public CommonResult<PageResult<ScdRegAddrValDiffVO>> getTest() {
//        Date parse_time = new Date();
//        scdService.parseScdFile("G:/TestFile/scdFile/station-103/2025-6-25_15_51_06/sclparse",parse_time);
//        return success(null);
//    }

    @GetMapping("/createScdData")
    public CommonResult<List<ScdDeviceValueDO>> createScdData(@RequestParam String filePath,@RequestParam String type) throws IOException, InterruptedException {
        Date parse_time = new Date();
        List<ScdDeviceValueDO> scdDataList = scdService.createScdFileData(filePath,parse_time,type);
        return success(scdDataList);
    }

    @GetMapping("/getModelData")
    public CommonResult<Page<ScdCurrentInfoDO>> getModelData(@RequestParam String deviceName, @RequestParam String scdContact, @RequestParam String type, @RequestParam Integer PageNo, @RequestParam Integer PageSize) {
        Page<ScdCurrentInfoDO> pageResult = scdService.selectModelData(deviceName,scdContact,type,PageNo,PageSize);
        return success(pageResult);
    }

    @GetMapping("/getCheckTime")
    public CommonResult<List<String>> getCheckFrontData(@RequestParam String stationSign, @RequestParam String type) {
        return success(scdService.getCheckTime(stationSign,type));
    }

    @GetMapping("/getCheckFrontData")
    public CommonResult<List<ScdCheckFrontDO>> getCheckFrontData(@RequestParam String stationSign,@RequestParam String option, @RequestParam String time) throws ParseException {
        return success(scdService.getScdCheckFrontData(stationSign,option,time));
    }

    @GetMapping("/getCheckAfterData")
    public CommonResult<List<ScdCheckAfterDO>> getCheckAfterData(@RequestParam String stationSign,@RequestParam String option, @RequestParam String time) throws ParseException {
        return success(scdService.getScdCheckAfterData(stationSign,option,time));
    }

}