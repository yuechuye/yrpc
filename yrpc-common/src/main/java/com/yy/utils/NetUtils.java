package com.yy.utils;

import com.yy.exception.NetException;
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
     * 遍历所有网络接口和它们的地址，找到第一个非循环回路地址且为内网地址的IP。
     * 如果找不到内网IP，则返回外网IP；如果都没有，则返回null。
     *
     * @return 本机的内网IP地址字符串。
     * @throws NetException 如果获取IP地址时发生异常。
     */
    public static String getInIp() {
        try {
            String localip = null;
            String netip = null;
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            boolean finded = false;
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
                            && !ip.getHostAddress().contains(":")) {
                        netip = ip.getHostAddress();
                        finded = true;
                    } else if (ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
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
            throw new NetException();
        }
    }

    public static void main(String[] args) {
        String ip = NetUtils.getInIp();
        System.out.println(ip);
    }
}
