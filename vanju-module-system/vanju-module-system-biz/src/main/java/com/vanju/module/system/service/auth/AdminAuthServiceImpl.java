package com.vanju.module.system.service.auth;

import cn.hutool.core.util.ObjectUtil;
import com.vanju.framework.common.enums.CommonStatusEnum;
import com.vanju.framework.common.enums.UserTypeEnum;
import com.vanju.framework.common.util.servlet.ServletUtils;
import com.vanju.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.vanju.module.system.controller.auth.vo.AuthLoginReqVO;
import com.vanju.module.system.controller.auth.vo.AuthLoginRespVO;
import com.vanju.module.system.controller.auth.vo.AuthRegisterReqVO;
import com.vanju.module.system.convert.auth.AuthConvert;
import com.vanju.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.vanju.module.system.dal.dataobject.user.AdminUserDO;
import com.vanju.module.system.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.vanju.module.system.enums.logger.LoginLogTypeEnum;
import com.vanju.module.system.enums.logger.LoginResultEnum;
import com.vanju.module.system.enums.oauth2.OAuth2ClientConstants;
import com.vanju.module.system.service.logger.LoginLogService;
import com.vanju.module.system.service.member.MemberService;
import com.vanju.module.system.service.oauth2.OAuth2TokenService;
import com.vanju.module.system.service.user.AdminUserService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.vanju.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.vanju.module.system.enums.ErrorCodeConstants.*;

/**
 * Auth Service 实现类
 *
 * @author 万炬源码
 */
@Service
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {

    @Resource
    private AdminUserService userService;
    @Resource
    private LoginLogService loginLogService;
    @Resource
    private OAuth2TokenService oauth2TokenService;
    @Resource
    private MemberService memberService;
    /**
     *
     */
    @Resource
    private OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;

    @Resource
    private Validator validator;

    /**
     * 验证码的开关，默认为 true
     */
    //    @Value("${vanju.captcha.enable:true}")
    //    private Boolean captchaEnable;
    @Override
    public AdminUserDO authenticate(String username, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_USERNAME;
        // 校验账号是否存在
        AdminUserDO user = userService.getUserByUsername(username);
        if (user == null) {
            createLoginLog(null, username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!userService.isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    @Override
    public AuthLoginRespVO login(AuthLoginReqVO reqVO) {
        // 校验验证码
//        validateCaptcha(reqVO);

        // 使用账号密码，进行登录
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());

        // 如果 socialType 非空，说明需要绑定社交用户
//        if (reqVO.getSocialType() != null) {
//            socialUserService.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
//                    reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState()));
//        }
        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME);
    }

    private void createLoginLog(Long userId, String username, LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logTypeEnum.getType());
        //        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(username);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        reqDTO.setResult(loginResult.getResult());
        loginLogService.createLoginLog(reqDTO);
        // 更新最后登录时间
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            userService.updateUserLogin(userId, ServletUtils.getClientIP());
        }
    }

    @VisibleForTesting
    //    void validateCaptcha(AuthLoginReqVO reqVO) {
    //        // 如果验证码关闭，则不进行校验
    //        if (!captchaEnable) {
    //            return;
    //        }
    //        // 校验验证码
    //        ValidationUtils.validate(validator, reqVO, AuthLoginReqVO.CodeEnableGroup.class);
    //        CaptchaVO captchaVO = new CaptchaVO();
    //        captchaVO.setCaptchaVerification(reqVO.getCaptchaVerification());
    //        ResponseModel response = captchaService.verification(captchaVO);
    //        // 验证不通过
    //        if (!response.isSuccess()) {
    //            // 创建登录失败日志（验证码不正确)
    //            createLoginLog(null, reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.CAPTCHA_CODE_ERROR);
    //            throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR, response.getRepMsg());
    //        }
    //    }

    private AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType) {
        // 插入登陆日志
        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(userId, getUserType().getValue(), OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        // 查询实时数据，每个用户的最后一次数据的时间至redis
        // oauth2AccessTokenRedisDAO.setRealtimeDataEndtime(userId);
        // 构建返回结果
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

    @Override
    public AuthLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, OAuth2ClientConstants.CLIENT_ID_DEFAULT);
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

    @Override
    public void logout(String token, Integer logType) {
        // 删除访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(token);
        if (accessTokenDO == null) {
            return;
        }
        // 删除成功，则记录登出日志
        createLogoutLog(accessTokenDO.getUserId(), accessTokenDO.getUserType(), logType);
    }

    private void createLogoutLog(Long userId, Integer userType, Integer logType) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logType);
        //        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(userType);
        if (ObjectUtil.equal(getUserType().getValue(), userType)) {
            reqDTO.setUsername(getUsername(userId));
        } else {
            reqDTO.setUsername(memberService.getMemberUserMobile(userId));
        }
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
        loginLogService.createLoginLog(reqDTO);
    }

    private String getUsername(Long userId) {
        if (userId == null) {
            return null;
        }
        AdminUserDO user = userService.getUser(userId);
        return user != null ? user.getUsername() : null;
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.ADMIN;
    }

    @Override
    public AuthLoginRespVO register(AuthRegisterReqVO registerReqVO) {
        // 1. 校验验证码
//        validateCaptcha(registerReqVO);

        // 2. 校验用户名是否已存在
        Long userId = userService.registerUser(registerReqVO);

        // 3. 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(userId, registerReqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME);
    }

//    @VisibleForTesting
//    void validateCaptcha(AuthRegisterReqVO reqVO) {
//        // 如果验证码关闭，则不进行校验
//        if (!captchaEnable) {
//            return;
//        }
//        // 校验验证码
//        ValidationUtils.validate(validator, reqVO, AuthLoginReqVO.CodeEnableGroup.class);
//        CaptchaVO captchaVO = new CaptchaVO();
//        captchaVO.setCaptchaVerification(reqVO.getCaptchaVerification());
//        ResponseModel response = captchaService.verification(captchaVO);
//        // 验证不通过
//        if (!response.isSuccess()) {
//            throw exception(AUTH_REGISTER_CAPTCHA_CODE_ERROR, response.getRepMsg());
//        }
//    }
}
