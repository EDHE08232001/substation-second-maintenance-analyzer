package com.vanju.module.infra.convert.config;

import com.vanju.framework.common.pojo.PageResult;
import com.vanju.module.infra.controller.config.vo.ConfigRespVO;
import com.vanju.module.infra.controller.config.vo.ConfigSaveReqVO;
import com.vanju.module.infra.dal.dataobject.config.ConfigDO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-05T11:38:16+0800",
    comments = "version: 1.6.2, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
public class ConfigConvertImpl implements ConfigConvert {

    @Override
    public PageResult<ConfigRespVO> convertPage(PageResult<ConfigDO> page) {
        if ( page == null ) {
            return null;
        }

        PageResult<ConfigRespVO> pageResult = new PageResult<ConfigRespVO>();

        pageResult.setList( convertList( page.getList() ) );
        pageResult.setTotal( page.getTotal() );

        return pageResult;
    }

    @Override
    public List<ConfigRespVO> convertList(List<ConfigDO> list) {
        if ( list == null ) {
            return null;
        }

        List<ConfigRespVO> list1 = new ArrayList<ConfigRespVO>( list.size() );
        for ( ConfigDO configDO : list ) {
            list1.add( convert( configDO ) );
        }

        return list1;
    }

    @Override
    public ConfigRespVO convert(ConfigDO bean) {
        if ( bean == null ) {
            return null;
        }

        ConfigRespVO configRespVO = new ConfigRespVO();

        configRespVO.setKey( bean.getConfigKey() );
        configRespVO.setCategory( bean.getCategory() );
        configRespVO.setCreateTime( bean.getCreateTime() );
        configRespVO.setId( bean.getId() );
        configRespVO.setName( bean.getName() );
        configRespVO.setRemark( bean.getRemark() );
        configRespVO.setType( bean.getType() );
        configRespVO.setValue( bean.getValue() );
        configRespVO.setVisible( bean.getVisible() );

        return configRespVO;
    }

    @Override
    public ConfigDO convert(ConfigSaveReqVO bean) {
        if ( bean == null ) {
            return null;
        }

        ConfigDO configDO = new ConfigDO();

        configDO.setConfigKey( bean.getKey() );
        configDO.setCategory( bean.getCategory() );
        configDO.setId( bean.getId() );
        configDO.setName( bean.getName() );
        configDO.setRemark( bean.getRemark() );
        configDO.setValue( bean.getValue() );
        configDO.setVisible( bean.getVisible() );

        return configDO;
    }
}
