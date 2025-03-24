package com.nami.springbootinit.job.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nami.springbootinit.common.ErrorCode;
import com.nami.springbootinit.exception.BusinessException;
import com.nami.springbootinit.model.entity.Sign;
import com.nami.springbootinit.service.SignService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nami404
 * * @date 2025/3/3 00:06
 */
@Component
public class ScheduledTasks {

    @Resource
    private SignService signService;

    private static final Integer status = 1;

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetSignInStatus() {
        List<Sign> signList = signService.getBySignStatus(status);
        signList.forEach(sign -> sign.setSignInStatus(0));
        boolean result = signService.updateBatchById(signList);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "定时将签到状态修改为未签到失败");
        }
    }

}
