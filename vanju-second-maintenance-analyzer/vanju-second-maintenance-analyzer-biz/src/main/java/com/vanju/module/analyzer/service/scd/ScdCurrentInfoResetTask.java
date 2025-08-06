package com.vanju.module.analyzer.service.scd;

import com.vanju.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentInfoDO;
import com.vanju.module.analyzer.dal.mysql.scd.ScdCurrentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ScdCurrentInfoResetTask {
    @Autowired
    private ScdCurrentInfoMapper scdCurrentInfoMapper;

    @Scheduled(fixedRate = 5 * 60 * 1000) // 每5分钟执行一次
    public void resetExpiredValues() {
        Date now = new Date();
        List<ScdCurrentInfoDO> expiredList = scdCurrentInfoMapper.selectList(
                new LambdaQueryWrapperX<ScdCurrentInfoDO>()
                        .isNotNull(ScdCurrentInfoDO::getValueUpdateTime)
        );
        for (ScdCurrentInfoDO info : expiredList) {
            if (info.getValueUpdateTime() != null &&
                    now.getTime() - info.getValueUpdateTime().getTime() > 30 * 60 * 1000) {
                info.setValue("-1");
                info.setValueUpdateTime(null);
                scdCurrentInfoMapper.updateById(info);
            }
        }
    }

}
