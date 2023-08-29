package com.leadnews.search.interceptor;

import com.leadnews.model.user.pojos.ApUser;
import com.leadnews.model.wemedia.pojos.WmUser;
import com.leadnews.search.utils.thread.UserLocalUtil;
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
public class TokenInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        Optional.ofNullable(userId).ifPresent(id -> {
            ApUser apUser = new ApUser();
            apUser.setId(Long.valueOf(userId));
            UserLocalUtil.setUser(apUser);
            logger.info("将用户id：{} 设置到UserLocalUtil中", id);
        });

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("UserLocalUtil - 清理");
        UserLocalUtil.clear();
    }
}
