package com.trace.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author dengxiaolin
 * @since 2021/01/11
 */
public class NetworkUtils {
    private static String localIp;

    static {
        localIp = buildLocalIp();
    }

    private static String buildLocalIp() {
        try {
            Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
            while (faces.hasMoreElements()) {
                NetworkInterface face = faces.nextElement();
                if (face.isLoopback() || face.isVirtual() || !face.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> address = face.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress addr = address.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress() && !addr.isAnyLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        }
        catch (Exception e) {
        }

        return "127.0.0.1";
    }

    public static String getLocalIp() {
        return localIp;
    }
}
