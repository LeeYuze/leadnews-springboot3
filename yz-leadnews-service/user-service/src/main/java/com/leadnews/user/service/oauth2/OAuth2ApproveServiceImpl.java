package com.leadnews.user.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ApproveDO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ClientDO;
import com.leadnews.user.mapper.oauth2.OAuth2ApproveMapper;
import com.leadnews.user.utils.collection.CollectionUtils;
import com.leadnews.user.utils.date.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ApproveServiceImpl implements OAuth2ApproveService {

    /**
     * 批准的过期时间，默认 30 天
     */
    private static final Integer TIMEOUT = 30 * 24 * 60 * 60; // 单位：秒


    private final OAuth2ApproveMapper oAuth2ApproveMapper;

    private final OAuth2ClientService oAuth2ClientService;


    @Override
    public boolean checkForPreApproval(Long userId, Integer userType, String clientId, Collection<String> requestedScopes) {
        // 第一步，基于 Client 的自动授权计算，如果 scopes 都在自动授权中，则返回 true 通过
        OAuth2ClientDO clientDO = oAuth2ClientService.validOAuthClientFromCache(clientId);
        Assert.notNull(clientDO, "客户端不能为空"); // 防御性编程

        if (CollUtil.containsAll(clientDO.getAutoApproveScopes(), requestedScopes)) {
            // gh-877 - if all scopes are auto approved, approvals still need to be added to the approval store.
            LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT);
            for (String scope : requestedScopes) {
                saveApprove(userId, userType, clientId, scope, true, expireTime);
            }
            return true;
        }

        // 第二步，算上用户已经批准的授权。如果 scopes 都包含，则返回 true
        List<OAuth2ApproveDO> approveDOs = getApproveList(userId, userType, clientId);
        Set<String> scopes = CollectionUtils.convertSet(approveDOs, OAuth2ApproveDO::getScope,
                OAuth2ApproveDO::getApproved); // 只保留未过期的 + 同意的

        return CollUtil.containsAll(scopes, requestedScopes);
    }

    @Override
    public boolean updateAfterApproval(Long userId, Integer userType, String clientId, Map<String, Boolean> requestedScopes) {
        // 如果 requestedScopes 为空，说明没有要求，则返回 true 通过
        if (CollUtil.isEmpty(requestedScopes)) {
            return true;
        }

        // 更新批准的信息
        boolean success = false; // 需要至少有一个同意
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT);
        for (Map.Entry<String, Boolean> entry : requestedScopes.entrySet()) {
            if (entry.getValue()) {
                success = true;
            }
            saveApprove(userId, userType, clientId, entry.getKey(), entry.getValue(), expireTime);
        }

        return success;
    }

    @Override
    public List<OAuth2ApproveDO> getApproveList(Long userId, Integer userType, String clientId) {
        List<OAuth2ApproveDO> approveDOs = oAuth2ApproveMapper.selectListByUserIdAndUserTypeAndClientId(
                userId, userType, clientId);
        approveDOs.removeIf(o -> DateUtils.isExpired(o.getExpiresTime()));
        return approveDOs;
    }

    void saveApprove(Long userId, Integer userType, String clientId,
                     String scope, Boolean approved, LocalDateTime expireTime) {
        // 先更新
        OAuth2ApproveDO approveDO = new OAuth2ApproveDO().setUserId(userId).setUserType(userType)
                .setClientId(clientId).setScope(scope).setApproved(approved).setExpiresTime(expireTime);
        if (oAuth2ApproveMapper.update(approveDO) == 1) {
            return;
        }
        // 失败，则说明不存在，进行更新
        oAuth2ApproveMapper.insert(approveDO);
    }

}
