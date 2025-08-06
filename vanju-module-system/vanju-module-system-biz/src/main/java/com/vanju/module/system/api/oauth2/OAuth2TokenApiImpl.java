package com.vanju.module.system.api.oauth2;

import com.vanju.framework.common.util.object.BeanUtils;
import com.vanju.module.system.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.vanju.module.system.api.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import com.vanju.module.system.api.oauth2.dto.OAuth2AccessTokenRespDTO;
import com.vanju.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.vanju.module.system.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.vanju.module.system.service.oauth2.OAuth2TokenService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * OAuth2.0 Token API 实现类
 *
 * @author 万炬源码
 */
@Service
public class OAuth2TokenApiImpl implements OAuth2TokenApi {

    @Resource
    private OAuth2TokenService oauth2TokenService;

    @Resource
    OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;

    @Override
    public OAuth2AccessTokenRespDTO createAccessToken(OAuth2AccessTokenCreateReqDTO reqDTO) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(
                reqDTO.getUserId(), reqDTO.getUserType(), reqDTO.getClientId(), reqDTO.getScopes());
        return BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRespDTO.class);
    }

    @Override
    public OAuth2AccessTokenCheckRespDTO checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.checkAccessToken(accessToken);
        return BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenCheckRespDTO.class);
    }

    @Override
    public OAuth2AccessTokenRespDTO removeAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(accessToken);
        return BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRespDTO.class);
    }

    @Override
    public OAuth2AccessTokenRespDTO refreshAccessToken(String refreshToken, String clientId) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, clientId);
        return BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRespDTO.class);
    }
}
