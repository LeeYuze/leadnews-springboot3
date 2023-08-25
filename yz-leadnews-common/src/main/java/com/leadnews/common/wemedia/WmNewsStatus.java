package com.leadnews.common.wemedia;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
public class WmNewsStatus {
    /**
     * 草稿
     */
    public static final Integer DRAFT = 0;

    /**
     * 提交
     */
    public static final Integer SUMMIT = 1;

    /**
     * 审核失败
     */
    public static final Integer AUTH_FAIL = 2;

    /**
     * 审核通过
     */
    public static final Integer AUTHED = 8;

    /**
     * 已发布
     */
    public static final Integer PUBLISH = 9;
}
