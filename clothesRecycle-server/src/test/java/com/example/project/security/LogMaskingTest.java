package com.example.project.security;

import com.example.project.config.SaTokenConfig;
import com.example.project.exception.BusinessException;
import com.example.project.exception.GlobalExceptionHandler;
import com.example.project.handler.aspect.LogAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 日志脱敏测试。
 */
@ExtendWith(OutputCaptureExtension.class)
class LogMaskingTest {

    /**
     * 异常日志应脱敏手机号、学号和 token。
     */
    @Test
    void should_mask_phone_student_id_and_token_in_exception_logs(CapturedOutput output) {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        handler.handleBusinessException(new BusinessException("phone=13812341234 sid=2023123456 token=abc1234567890"));

        assertThat(output.getOut()).contains("138****1234");
        assertThat(output.getOut()).contains("20****56");
        assertThat(output.getOut()).doesNotContain("13812341234");
        assertThat(output.getOut()).doesNotContain("2023123456");
        assertThat(output.getOut()).doesNotContain("abc1234567890");
    }

    /**
     * 登录日志应脱敏手机号、学号和 token。
     */
    @Test
    void should_mask_phone_student_id_and_token_in_login_logs(CapturedOutput output) throws Exception {
        Method method = SaTokenConfig.class.getDeclaredMethod(
                "logLoginEvent", String.class, String.class, String.class);
        method.setAccessible(true);
        method.invoke(null, "13812341234", "2023123456", "abc1234567890");

        assertThat(output.getOut()).contains("138****1234");
        assertThat(output.getOut()).contains("20****56");
        assertThat(output.getOut()).doesNotContain("13812341234");
        assertThat(output.getOut()).doesNotContain("2023123456");
        assertThat(output.getOut()).doesNotContain("abc1234567890");
    }

    /**
     * 请求日志应脱敏手机号、学号和 token。
     */
    @Test
    void should_mask_phone_student_id_and_token_in_request_logs(CapturedOutput output) throws Throwable {
        LogAspect logAspect = new LogAspect();
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Signature signature = Mockito.mock(Signature.class);

        Mockito.when(joinPoint.getTarget()).thenReturn(this);
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(signature.getName()).thenReturn("requestLog");
        Mockito.when(joinPoint.getArgs()).thenReturn(new Object[]{
                "phone=13812341234",
                "sid=2023123456",
                "token=abc1234567890"
        });
        Mockito.when(joinPoint.proceed()).thenReturn("ok");

        logAspect.around(joinPoint);

        assertThat(output.getOut()).contains("138****1234");
        assertThat(output.getOut()).contains("20****56");
        assertThat(output.getOut()).doesNotContain("13812341234");
        assertThat(output.getOut()).doesNotContain("2023123456");
        assertThat(output.getOut()).doesNotContain("abc1234567890");
    }
}
