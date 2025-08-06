package com.vanju.module.infra.convert.file;

import com.vanju.module.infra.controller.file.vo.config.FileConfigSaveReqVO;
import com.vanju.module.infra.dal.dataobject.file.FileConfigDO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-06T10:17:54+0800",
    comments = "version: 1.6.2, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
public class FileConfigConvertImpl implements FileConfigConvert {

    @Override
    public FileConfigDO convert(FileConfigSaveReqVO bean) {
        if ( bean == null ) {
            return null;
        }

        FileConfigDO.FileConfigDOBuilder fileConfigDO = FileConfigDO.builder();

        fileConfigDO.id( bean.getId() );
        fileConfigDO.name( bean.getName() );
        fileConfigDO.remark( bean.getRemark() );
        fileConfigDO.storage( bean.getStorage() );

        return fileConfigDO.build();
    }
}
