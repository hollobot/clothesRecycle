package com.example.project.common.constant;

/**
 * 系统公共常量
 */
public final class SystemConstant {

    private SystemConstant() {}

    /** 默认页码 */
    public static final int DEFAULT_PAGE_NUM = 1;

    /** 默认每页条数 */
    public static final int DEFAULT_PAGE_SIZE = 12;

    /** 逻辑未删除 */
    public static final int NOT_DELETED = 0;

    /** 逻辑已删除 */
    public static final int DELETED = 1;

    /** Token 请求头名称 */
    public static final String TOKEN_HEADER = "Authorization";
}
