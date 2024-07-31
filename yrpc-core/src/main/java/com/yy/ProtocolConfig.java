/**
 * 协议配置类
 * 该类用于封装协议的相关配置信息，提供协议名称的存储和获取。
 */
package com.yy;

import lombok.Data;

@Data
public class ProtocolConfig {

    /**
     * 协议名称
     * 该字段用于存储协议的名称，以便在应用程序中根据协议名称进行相应的处理。
     */
    private String protocol;

    /**
     * 构造函数
     * @param protocol 协议名称
     *                 该构造函数用于初始化ProtocolConfig对象，设置协议名称。
     */
    public ProtocolConfig(String protocol) {
        this.protocol = protocol;
    }
}

