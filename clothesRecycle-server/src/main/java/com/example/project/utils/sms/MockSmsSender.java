package com.example.project.utils.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 开发环境模拟短信发送器。
 */
@Component
public class MockSmsSender implements SmsSender {

    private static final Logger log = LoggerFactory.getLogger(MockSmsSender.class);

    @Override
    public void sendCode(String phone, String code) {
        log.info("模拟短信发送成功 phone={}, code={}", phone, code);
    }
}
