package com.cmcglobal.jiraplugin.poc.utils;

/* 
    @created by 10:06 AM 8/13/2021
    @author NPC
    @project CRM
    @Github : https://github.com/congphuong1703
    @Facebook : https://facebook.com/congphuong1703
*/

import javax.servlet.http.HttpServletRequest;

public class UtilHelper {

    public static String getContextPathFromHttpRequest(HttpServletRequest request) {
        String url = "";
        String base = request.getContextPath();

        Integer post = request.getServerPort();
        String sPost = "";

        if (post > 0) {
            sPost = ":" + post;
        }

        url = request.getScheme() + "://" + request.getServerName() + sPost + base;
        return url;
    }
}