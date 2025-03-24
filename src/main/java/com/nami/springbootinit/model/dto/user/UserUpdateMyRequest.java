package com.nami.springbootinit.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新个人信息请求
 
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户确认密码
     */
    private String checkPassword;

    /**
     * 用户头像
     */
    private String userAvatar;

    private static final long serialVersionUID = 1L;
}