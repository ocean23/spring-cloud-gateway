package com.fujfu.gateway.config.properties.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpMethod;

import java.io.Serializable;

/**
 * @author Jun Luo
 * @date 2021/3/31 5:59 PM
 */
@Getter
@Setter
@ToString

public class RequestUrlVO implements Serializable {
    private static final long serialVersionUID = 2345245231L;

    private HttpMethod method;

    private String pattern;
}
