package com.vanju.module.system.service.logger;

import com.vanju.framework.common.pojo.PageResult;
import com.vanju.framework.common.util.object.BeanUtils;
import com.vanju.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.vanju.module.system.controller.logger.vo.loginlog.LoginLogPageReqVO;
import com.vanju.module.system.dal.dataobject.logger.LoginLogDO;
import com.vanju.module.system.dal.mysql.logger.LoginLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        loginLogMapper.insert(loginLog);
    }

}
