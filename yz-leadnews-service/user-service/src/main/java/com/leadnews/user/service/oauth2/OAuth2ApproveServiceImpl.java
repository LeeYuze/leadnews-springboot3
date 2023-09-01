package com.leadnews.user.service.oauth2;

import com.leadnews.user.dal.dataobject.oauth2.OAuth2ApproveDO;
import com.leadnews.user.mapper.oauth2.OAuth2ApproveMapper;
import com.leadnews.user.utils.date.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ApproveServiceImpl implements OAuth2ApproveService{

    private final OAuth2ApproveMapper oAuth2ApproveMapper;


    @Override
    public boolean checkForPreApproval(Long userId, Integer userType, String clientId, Collection<String> requestedScopes) {
        return false;
    }

    @Override
    public boolean updateAfterApproval(Long userId, Integer userType, String clientId, Map<String, Boolean> requestedScopes) {
        return false;
    }

    @Override
    public List<OAuth2ApproveDO> getApproveList(Long userId, Integer userType, String clientId) {
        List<OAuth2ApproveDO> approveDOs = oAuth2ApproveMapper.selectListByUserIdAndUserTypeAndClientId(
                userId, userType, clientId);
        approveDOs.removeIf(o -> DateUtils.isExpired(o.getExpiresTime()));
        return approveDOs;
    }
}
