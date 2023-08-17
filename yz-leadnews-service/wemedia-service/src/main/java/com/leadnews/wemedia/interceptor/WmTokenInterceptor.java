package com.leadnews.wemedia.interceptor;

import com.leadnews.model.wemedia.pojos.WmUser;
import com.leadnews.wemedia.utils.thread.WmUserLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

/**
 * @author lihaohui
 * @date 2023/8/17
 */
public class WmTokenInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(WmTokenInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        Optional.ofNullable(userId).ifPresent(id -> {
            WmUser wmUser = new WmUser();
            wmUser.setId(Long.valueOf(id));
            WmUserLocalUtil.setUser(wmUser);
            logger.info("将用户id：{} 设置到WmUserLocalUtil中", id);
        });

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("WmUserLocalUtil - 清理");
        WmUserLocalUtil.clear();
    }
}
