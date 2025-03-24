package com.nami.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nami.springbootinit.model.entity.Sign;
import com.nami.springbootinit.model.entity.User;

import java.util.List;

/**
* @author nami
* @description 针对表【sign(签到积分信息表)】的数据库操作Service
* @createDate 2025-03-02 22:27:50
*/
public interface SignService extends IService<Sign> {

    boolean signIn(User loginUser);

    Sign getByUserId(Long userId);

    List<Sign> getBySignStatus(Integer status);

}
