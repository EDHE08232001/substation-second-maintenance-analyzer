package com.vanju.module.analyzer.controller.publicHandle;

import com.vanju.framework.common.pojo.CommonResult;
import com.vanju.module.analyzer.dal.dataobject.publicHandle.ScdConstantDO;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentInfoDO;
import com.vanju.module.analyzer.service.publicHandle.PublicHandleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/publicHandle")
public class PublicHandleController {

    @Resource
    PublicHandleService publicHandleService;

    @PostMapping("/saveConstantData")
    public CommonResult<String> saveConstantData(@RequestBody List<ScdCurrentInfoDO> objects) {
        return CommonResult.success(publicHandleService.saveConstantData(objects));
    }

    @GetMapping("/getConstantData")
    public CommonResult<List<ScdConstantDO>> getConstantData(@RequestParam("scdSign") String scdSign) {
        return CommonResult.success(publicHandleService.selectConstantData(scdSign));
    }

}
