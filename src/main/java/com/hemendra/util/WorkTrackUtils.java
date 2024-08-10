package com.hemendra.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

@Component
@Slf4j
public class WorkTrackUtils {

    public String getOsName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public String getOsVersion() {
        return System.getProperty("os.version");
    }

    public String getOsArchitecture() {
        return System.getProperty("os.arch");
    }

    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public String getJavaHome() {
        return System.getProperty("java.home");
    }

    public String getJavaVendor() {
        return System.getProperty("java.vendor");
    }

    public String getUserName() {
        return System.getProperty("user.name");
    }

    public String getUserHome() {
        return System.getProperty("user.home");
    }

    public String getUserDir() {
        return System.getProperty("user.dir");
    }

    public String getIpAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Error getting IP address: {}", e.getMessage());
        }
        return "";
    }

    public String getHostName() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
            log.error("Error getting host name: {}", e.getMessage());
        }
        return "";
    }

    public final String getMacAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            String macAddress = formatMacAddress(hardwareAddress);
            return macAddress;
        } catch (UnknownHostException | SocketException e) {
            log.error("Error getting MAC address: {}", e.getMessage());
        }
        return "";
    }

    private static String formatMacAddress(byte[] mac) {
        if (mac == null) {
            return "N/A";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }
}
