package com.example.project.utils.sms;

/**
 * 短信发送抽象。
 */
public interface SmsSender {

    /**
     * 发送短信验证码。
     */
    void sendCode(String phone, String code);
}
