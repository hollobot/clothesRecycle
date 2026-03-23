package com.example.project.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志脱敏工具。
 * <p>用于统一脱敏手机号、学号和 token，避免敏感信息泄露到日志。</p>
 */
public final class LogMaskUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("(?<!\\d)(1\\d{2})(\\d{4})(\\d{4})(?!\\d)");
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("(?<!\\d)(\\d{2})(\\d{4,})(\\d{2})(?!\\d)");
    private static final Pattern TOKEN_PAIR_PATTERN = Pattern.compile("(?i)(token|authorization)(\\s*[=:]\\s*)([A-Za-z0-9._\\-]{6,})");
    private static final Pattern BEARER_PATTERN = Pattern.compile("(?i)(Bearer\\s+)([A-Za-z0-9._\\-]{6,})");

    private LogMaskUtil() {
    }

    /**
     * 对任意文本执行统一脱敏。
     */
    public static String mask(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return rawText;
        }
        String masked = maskPhone(rawText);
        masked = maskToken(masked);
        return maskStudentId(masked);
    }

    /**
     * 对对象数组执行脱敏并转字符串，适用于请求参数日志。
     */
    public static String maskArgs(Object[] args) {
        if (args == null) {
            return "[]";
        }
        return Arrays.stream(args)
                .map(item -> mask(String.valueOf(item)))
                .toList()
                .toString();
    }

    private static String maskPhone(String text) {
        Matcher matcher = PHONE_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1) + "****" + matcher.group(3));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String maskStudentId(String text) {
        Matcher matcher = STUDENT_ID_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String prefix = matcher.group(1);
            String suffix = matcher.group(3);
            matcher.appendReplacement(sb, prefix + "****" + suffix);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String maskToken(String text) {
        String firstPass = replaceTokenPair(text);
        return replaceBearerToken(firstPass);
    }

    private static String replaceTokenPair(String text) {
        Matcher matcher = TOKEN_PAIR_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String tokenValue = matcher.group(3);
            matcher.appendReplacement(sb, matcher.group(1) + matcher.group(2) + maskTokenValue(tokenValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String replaceBearerToken(String text) {
        Matcher matcher = BEARER_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1) + maskTokenValue(matcher.group(2)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String maskTokenValue(String token) {
        if (token == null || token.length() < 6) {
            return "******";
        }
        return token.substring(0, 3) + "***" + token.substring(token.length() - 3);
    }
}
