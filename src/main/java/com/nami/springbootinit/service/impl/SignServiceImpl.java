package com.nami.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nami.springbootinit.common.ErrorCode;
import com.nami.springbootinit.constant.RedisConstant;
import com.nami.springbootinit.exception.BusinessException;
import com.nami.springbootinit.mapper.SignMapper;
import com.nami.springbootinit.model.entity.Sign;
import com.nami.springbootinit.model.entity.User;
import com.nami.springbootinit.service.SignService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author nami
* @description 针对表【sign(签到积分信息表)】的数据库操作Service实现
* @createDate 2025-03-02 22:27:50
*/
@Service
@Slf4j
public class SignServiceImpl extends ServiceImpl<SignMapper, Sign>
    implements SignService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public synchronized boolean signIn(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long userId = loginUser.getId();
        // 先判断是否已签到过
        String isSignIn = stringRedisTemplate.opsForValue().get(RedisConstant.USER_SIGN_IN_REDIS_ID + userId);
        if (!StringUtils.isEmpty(isSignIn) && isSignIn.equals("succeed")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您今天已签到，请明天再来");
        }
        // 计算当前时间至次日零点的秒数
        LocalDateTime currentDatetime = LocalDateTime.now();

        LocalDateTime nextDayMidnight = currentDatetime.plusDays(1).withHour(0).
                withMinute(0).withSecond(0).withNano(0);
        Duration duration = Duration.between(currentDatetime, nextDayMidnight);
        long seconds = Math.abs(duration.getSeconds());
        // 将签到信息存入 Redis
        try {
            stringRedisTemplate.opsForValue().set(RedisConstant.USER_SIGN_IN_REDIS_ID + userId,
                    "succeed", seconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.info("用户 {} 签到失败", userId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "签到失败");
        }

        // 先判断签到表中是否有这个用户签到记录
        QueryWrapper<Sign> signQueryWrapper = Wrappers.query();
        signQueryWrapper.eq("userId", userId);
        Sign selectedOne = this.baseMapper.selectOne(signQueryWrapper);
        if (ObjectUtils.isEmpty(selectedOne)) {
            Sign newSign = Sign.builder().userId(userId).build();
            boolean saveResult = this.save(newSign);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "签到记录新建失败，数据库错误");
            }
        }

        Sign sign = this.baseMapper.selectOne(signQueryWrapper);
        Long signId = sign.getId();
        UpdateWrapper<Sign> signUpdateWrapper = new UpdateWrapper<>();
        signUpdateWrapper.eq("id", signId);
        signUpdateWrapper.setSql("score = score + 10");
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = currentDateTime.format(formatter);
        signUpdateWrapper.set("signInTime", formatDateTime);
        signUpdateWrapper.set("signInStatus", 1);
        boolean update = this.update(signUpdateWrapper);
        return update;
    }

    public Sign getByUserId(Long userId) {
        QueryWrapper<Sign> queryWrapper = Wrappers.query();
        queryWrapper.eq("userId", userId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    public List<Sign> getBySignStatus(Integer status) {
        QueryWrapper<Sign> queryWrapper = Wrappers.query();
        queryWrapper.eq("signInStatus", status);
        return this.list(queryWrapper);
    }
}




