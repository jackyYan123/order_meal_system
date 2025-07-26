package com.restaurant.auth.dto;

/**
 * 微信小程序登录请求DTO
 */
public class MiniLoginRequest {
    
    /**
     * 微信登录临时凭证
     */
    private String code;
    
    /**
     * 用户信息加密数据
     */
    private String encryptedData;
    
    /**
     * 加密算法的初始向量
     */
    private String iv;
    
    /**
     * 用户昵称
     */
    private String nickName;
    
    /**
     * 用户头像
     */
    private String avatarUrl;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
} 