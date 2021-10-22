package com.ruoyi.common.security.feign;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.ruoyi.common.core.utils.ip.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign 请求拦截器
 * 
 * @author ruoyi
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor
{

    @Override
    public void apply(RequestTemplate requestTemplate)
    {
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        if (StringUtils.isNotNull(httpServletRequest))
        {
            Map<String, String> headers = ServletUtils.getHeaders(httpServletRequest);
            // 传递用户信息请求头，防止丢失
            String userId = headers.get(CacheConstants.DETAILS_USER_ID);
            if (StringUtils.isNotEmpty(userId))
            {
                requestTemplate.header(CacheConstants.DETAILS_USER_ID, userId);
            }
            String userName = headers.get(CacheConstants.DETAILS_USERNAME);
            if (StringUtils.isNotEmpty(userName))
            {
                requestTemplate.header(CacheConstants.DETAILS_USERNAME, userName);
            }
            String tenant = headers.get(CacheConstants.DETAILS_TENANT_ID);
            if (StringUtils.isNotEmpty(tenant))
            {
                requestTemplate.header(CacheConstants.DETAILS_TENANT_ID, tenant);
            }
            String tenantId = headers.get(CacheConstants.TENANT_ID);
            if (StringUtils.isNotEmpty(tenantId))
            {
                requestTemplate.header(CacheConstants.TENANT_ID, tenantId);
            }
            String authentication = headers.get(CacheConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isNotEmpty(authentication))
            {
                requestTemplate.header(CacheConstants.AUTHORIZATION_HEADER, authentication);
            }

            // 配置客户端IP
            requestTemplate.header("X-Forwarded-For", IpUtils.getIpAddr(ServletUtils.getRequest()));
        }
    }
}