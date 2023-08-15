package com.leadnews.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 自媒体用户信息表
 * @TableName wm_user
 */
@TableName(value ="wm_user")
@Data
public class WmUser implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "ap_user_id")
    private Integer apUserId;

    /**
     * 
     */
    @TableField(value = "ap_author_id")
    private Integer apAuthorId;

    /**
     * 登录用户名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 登录密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 盐
     */
    @TableField(value = "salt")
    private String salt;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 头像
     */
    @TableField(value = "image")
    private String image;

    /**
     * 归属地
     */
    @TableField(value = "location")
    private String location;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 状态
            0 暂时不可用
            1 永久不可用
            9 正常可用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 账号类型
            0 个人 
            1 企业
            2 子账号
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 运营评分
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 最后一次登录时间
     */
    @TableField(value = "login_time")
    private Date loginTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}