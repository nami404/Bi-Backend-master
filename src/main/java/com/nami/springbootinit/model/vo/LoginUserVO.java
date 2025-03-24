package com.nami.springbootinit.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 已登录用户视图（脱敏）
 
 **/
@Data
public class LoginUserVO implements Serializable {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 当前用户总共生成图表数量
     */
    private Long generatingCount;

    /**
     * 当前用户积分
     */
    private Integer score;

    /**
     * 签到状态，0表示未签到，1表示已签到
     */
    private Integer signInStatus;

    /**
     * 签到时间
     */
    private Date signInTime;

    private static final long serialVersionUID = 1L;
}