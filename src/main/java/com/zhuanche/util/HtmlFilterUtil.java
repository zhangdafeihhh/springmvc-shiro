package com.zhuanche.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author fanht
 * @Description  html过滤器
 * @Date 2018/11/30 上午10:00
 * @Version 1.0
 */
public class HtmlFilterUtil {
    public static String HTMLTagSpirit(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style样式正则表达式
        String regEx_html = "<[^>]+>"; // 定义HTML标签正则表达式

		/*
		 * 过滤script标签
		 */
        Pattern pScript = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher mScript = pScript.matcher(htmlStr);
        htmlStr = mScript.replaceAll("");

		/*
		 * 过滤style标签
		 */
        Pattern pStyle = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher mStyle = pStyle.matcher(htmlStr);
        htmlStr = mStyle.replaceAll("");

		/*
		 * 过滤html标签
		 */
        Pattern pHtml = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher mH_html = pHtml.matcher(htmlStr);
        htmlStr = mH_html.replaceAll("");

        // 替换所有空格
        htmlStr = htmlStr.replaceAll("&nbsp;", "");

        return htmlStr.trim();
    }

}
