package com.vanju.module.system.convert.auth;

import com.vanju.module.system.controller.auth.vo.AuthLoginRespVO;
import com.vanju.module.system.controller.auth.vo.AuthPermissionInfoRespVO;
import com.vanju.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.vanju.module.system.dal.dataobject.permission.MenuDO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-06T10:17:50+0800",
    comments = "version: 1.6.2, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
public class AuthConvertImpl implements AuthConvert {

    @Override
    public AuthLoginRespVO convert(OAuth2AccessTokenDO bean) {
        if ( bean == null ) {
            return null;
        }

        AuthLoginRespVO.AuthLoginRespVOBuilder authLoginRespVO = AuthLoginRespVO.builder();

        authLoginRespVO.accessToken( bean.getAccessToken() );
        authLoginRespVO.expiresTime( bean.getExpiresTime() );
        authLoginRespVO.refreshToken( bean.getRefreshToken() );
        authLoginRespVO.userId( bean.getUserId() );

        return authLoginRespVO.build();
    }

    @Override
    public AuthPermissionInfoRespVO.MenuVO convertTreeNode(MenuDO menu) {
        if ( menu == null ) {
            return null;
        }

        AuthPermissionInfoRespVO.MenuVO.MenuVOBuilder menuVO = AuthPermissionInfoRespVO.MenuVO.builder();

        menuVO.alwaysShow( menu.getAlwaysShow() );
        menuVO.component( menu.getComponent() );
        menuVO.componentName( menu.getComponentName() );
        menuVO.icon( menu.getIcon() );
        menuVO.id( menu.getId() );
        menuVO.keepAlive( menu.getKeepAlive() );
        menuVO.name( menu.getName() );
        menuVO.parentId( menu.getParentId() );
        menuVO.path( menu.getPath() );
        menuVO.visible( menu.getVisible() );

        return menuVO.build();
    }
}
