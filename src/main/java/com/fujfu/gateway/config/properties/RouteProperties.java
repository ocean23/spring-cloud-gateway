package com.fujfu.gateway.config.properties;

import com.fujfu.gateway.config.properties.vo.RequestUrlVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jun Luo
 * @date 2021/3/9 4:11 PM
 */
@Component
@Setter
@Getter
@ConfigurationProperties("route")
public class RouteProperties {
    /**
     * 允许访问的服务
     * 比如 https://example.com/spring-cloud-frontend/，/spring-cloud-frontend 便是服务名
     */
    private List<String> allowedService = new ArrayList<>();
    /**
     * 不需要鉴权也可以访问的接口（登录相关接口不会写在这里面）
     */
    private List<RequestUrlVO> noAuthorizationRequiredUrl = new ArrayList<>();
    /**
     * 屏蔽的接口，比如 swagger 接口
     */
    private List<RequestUrlVO> blockUrl = new ArrayList<>();
    /**
     * 登录相关的接口，无需鉴权
     */
    private List<RequestUrlVO> loginUrl = new ArrayList<>();
}
