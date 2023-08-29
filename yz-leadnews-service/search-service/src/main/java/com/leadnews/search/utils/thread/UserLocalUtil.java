package com.leadnews.search.utils.thread;

import com.leadnews.model.user.pojos.ApUser;
import com.leadnews.model.wemedia.pojos.WmUser;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
public class UserLocalUtil {
    private final static ThreadLocal<ApUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加用户
     */
    public static void  setUser(ApUser user){
        WM_USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取用户
     */
    public static ApUser getUser(){
        return WM_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理用户
     */
    public static void clear(){
        WM_USER_THREAD_LOCAL.remove();
    }
}
