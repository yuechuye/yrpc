package com.yy.common.utils;


import com.yy.common.exceptions.NetException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Slf4j
public class NetUtils {
    /**
     * 获取本机的内网ip地址
     * @return ip地址
     */
    public static String getInIp() {
        try {
            String localip = null;// 本地IP，如果没有配置外网IP则返回它
            String netip = null;// 外网IP
            Enumeration<NetworkInterface> netInterfaces;
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress()
                            &&!ip.isLoopbackAddress()
                            &&ip.getHostAddress().indexOf(":") == -1){// 外网IP
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress()
                            &&!ip.isLoopbackAddress()
                            &&ip.getHostAddress().indexOf(":") == -1){// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }
            if (netip != null && !"".equals(netip)) {
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
