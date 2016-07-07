/*
 * Copyright (c) 2016. Hefei Royalstar Electronic Appliance Group Co., Ltd. All rights reserved.
 */

package com.zhoulang.zhouandroidlibrary.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Arrays;

/**
 * 跟网络相关的工具类
 *
 */
public class NetUtils {
    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

//    public static boolean isAppNetConnected() {
//        Context context = VDContextHolder.getContext();
//        return context != null && isConnected(context);
//    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    public static String getWifiIp(Context context) {
        // 只获取wifi地址
        String ip = "0.0.0.0";
        WifiManager wifimanage = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);//获取WifiManager
        //检查wifi是否开启
        if (wifimanage.isWifiEnabled()) { // 没开启wifi时,ip地址为0.0.0.0
            WifiInfo wifiinfo = wifimanage.getConnectionInfo();
            ip = intToIp(wifiinfo.getIpAddress());
        }
        return ip;
    }

    //将获取的int转为真正的ip地址,参考的网上的，修改了下
    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    /**
     * 是否在同一网段中
     *
     * @param ip
     * @param otherIp
     * @return
     */
    public static boolean isIpInSameSegment(String ip, String otherIp) {
        boolean result = false;
        if (isIp(ip) && isIp(otherIp)) {
            String[] ips = ip.split("[.]");
            String[] oips = otherIp.split("[.]");
            ips = Arrays.copyOf(ips, 3);
            oips = Arrays.copyOf(oips, 3);
            result = Arrays.equals(ips, oips);
        }
        return result;
    }

    public static boolean isIp(String ip) {
        if (TextUtils.isEmpty(ip))
            return false;
        String pattern = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\" +
                ".(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
        return ip.matches(pattern);
    }

    public static NetworkInfo getConnNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo connectNetInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] allNetworks = manager.getAllNetworks();
            if (allNetworks == null || allNetworks.length == 0) {
                // not network.
                connectNetInfo = null;
            } else {
                //has network.
                for (Network network : allNetworks) {
                    NetworkInfo info = manager.getNetworkInfo(network);
                    if (info == null) continue;
                    if (info.isConnected()) {
                        connectNetInfo = info;
                        break;
                    }
                }
            }
        } else {
            //for sdk low level.
            connectNetInfo = getConnectNetInfo(manager);

        }

        return connectNetInfo;
    }

    public static String getNetExtraInfo(Context context) {
        NetworkInfo networkInfo = getConnNetworkInfo(context);
        String extraInfo = null;
        if (networkInfo != null) {
            extraInfo = networkInfo.getExtraInfo();
            if (TextUtils.isEmpty(extraInfo)) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                String wifiSsid = getWifiSsid(wifiManager, networkInfo);
                if (!TextUtils.isEmpty(wifiSsid))
                    extraInfo = wifiSsid;
            }
        }
        return extraInfo;
    }

    public static String getWifiSsid(WifiManager wifiMgr, @NonNull NetworkInfo wifiInfo) {
        if (wifiInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiInfo info = wifiMgr.getConnectionInfo();
            return info != null ? info.getSSID() : null;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private static NetworkInfo getConnectNetInfo(ConnectivityManager manager) {
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
            return wifiInfo;
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo != null && mobileInfo.isConnected())
            return mobileInfo;
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected())
            return activeInfo;
        return null;
    }


}
