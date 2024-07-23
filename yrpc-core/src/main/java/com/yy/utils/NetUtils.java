package com.yy.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络工具类，用于获取本机的内网IP地址。
 * @author yuechu
 */
@Slf4j
public class NetUtils {
    /**
     * 获取本机的内网IP地址。
     * 遍历所有网络接口和它们的地址，找到第一个非循环回路地址（loopback address）和非链路本地地址（link-local address）的IPv4地址。
     * 如果找到外网IP，则返回外网IP；否则返回内网IP。
     *
     * @return 本机的内网IP地址
     * @throws RuntimeException 如果获取IP地址时发生异常
     */
    public static String getInIp() {
        try {
            // 本地IP，如果没有配置外网IP则返回它
            String localip = null;
            // 外网IP
            String netip = null;
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            // 是否找到外网IP
            boolean finded = false;
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
                            // 外网IP
                            && !ip.getHostAddress().contains(":")) {
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
                            // 内网IP
                            && !ip.getHostAddress().contains(":")) {
                        localip = ip.getHostAddress();
                    }
                }
            }
            if (netip != null && !netip.isEmpty()) {
                return netip;
            } else {
                return localip;
            }
        } catch (SocketException e) {
            log.error("获取内网ip时发生异常:", e);
            throw new RuntimeException("获取内网ip时发生异常");
        }
    }

    public static void main(String[] args) {
        String ip = NetUtils.getInIp();
        System.out.println(ip);
    }
}
