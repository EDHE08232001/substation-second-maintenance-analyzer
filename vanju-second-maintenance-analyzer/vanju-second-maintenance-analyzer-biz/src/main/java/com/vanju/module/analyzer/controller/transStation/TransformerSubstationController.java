package com.vanju.module.analyzer.controller.transStation;

import com.vanju.framework.common.pojo.CommonResult;
import com.vanju.framework.common.util.object.BeanUtils;
import com.vanju.module.analyzer.controller.transStation.vo.TransformerSubstationListReqVO;
import com.vanju.module.analyzer.controller.transStation.vo.TransformerSubstationRespVO;
import com.vanju.module.analyzer.controller.transStation.vo.TransformerSubstationSaveReqVO;
import com.vanju.module.analyzer.controller.transStation.vo.TransformerSubstationSimpleRespVO;
// 删除了未使用的DeviceDO导入
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentModelDO;
import com.vanju.module.analyzer.dal.dataobject.transStation.TransformerSubstationDO;
import com.vanju.module.analyzer.enums.SCDFileEnum;
import com.vanju.module.analyzer.service.transStation.TransformerSubstationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.vanju.framework.common.pojo.CommonResult.success;

/**
 * 变电站 Controller
 */
@Slf4j
@RestController
@RequestMapping("/transStation")
public class TransformerSubstationController {

    @Resource
    private TransformerSubstationService transformerSubstationService;

    @Operation(summary = "创建变电站树结构")
    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 路径前统一加斜杠
    // 使用@ModelAttribute接受表单数据，便于上传文件
    public CommonResult<Long> createTransformerSubstation(@Valid @ModelAttribute TransformerSubstationSaveReqVO createReqVO) {
        Long transformerSubstationId = transformerSubstationService.createTransformerSubstation(createReqVO);
        return success(transformerSubstationId);
    }

    @PutMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 路径前加斜杠
    @Operation(summary = "更新站")
    // 同样用@ModelAttribute绑定表单参数
    public CommonResult<Boolean> updateTransformerSubstation(@Valid @ModelAttribute TransformerSubstationSaveReqVO updateReqVO) {
        transformerSubstationService.updateTransformerSubstation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete") // 路径前加斜杠
    @Operation(summary = "删除站")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<Boolean> deleteTransformerSubstation(@RequestParam("id") Long id) {
        transformerSubstationService.deleteTransformerSubstation(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取站列表")
    public CommonResult<List<TransformerSubstationRespVO>> getTransformerSubstationList(TransformerSubstationListReqVO reqVO) {
        List<TransformerSubstationDO> list = transformerSubstationService.getTransformerSubstationList(reqVO.setScd(null));
        return success(BeanUtils.toBean(list, TransformerSubstationRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取站精简信息列表", description = "只包含被开启的站，主要用于前端的下拉选项")
    public CommonResult<List<TransformerSubstationSimpleRespVO>> getSimpleTransformerSubstationList() {
        List<TransformerSubstationDO> list = transformerSubstationService.getTransformerSubstationList(new TransformerSubstationListReqVO().setScd(SCDFileEnum.FALSE.getIsSCDFile()));
        return success(BeanUtils.toBean(list, TransformerSubstationSimpleRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得站信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<TransformerSubstationRespVO> getTransformerSubstation(@RequestParam("id") Long id) {
        TransformerSubstationDO TransformerSubstation = transformerSubstationService.getTransformerSubstation(id);
        return success(BeanUtils.toBean(TransformerSubstation, TransformerSubstationRespVO.class));
    }

    @GetMapping("/selectStaDev")
    @Operation(summary = "获得站的装置信息")
    // 参数名称调整为stationId
    @Parameter(name = "stationId", description = "编号", required = true, example = "1024")
    public CommonResult<List<ScdCurrentModelDO>> selectStaDev(@RequestParam("stationId") String station_id) {
        return success(transformerSubstationService.selectDevListData(station_id));
    }



}