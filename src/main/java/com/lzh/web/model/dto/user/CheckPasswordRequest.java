package com.lzh.web.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码请求参数
 * @author lzh
 */
@Data
public class CheckPasswordRequest implements Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 新密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

    private static final long serialVersionUID = -3667981360714173953L;

}