package com.zhuanche.util.excel;

/**
 * @author zhangzhengguang
 * @create 2017-07-17
 **/

import org.apache.poi.ss.usermodel.DateUtil;

import java.util.Calendar;

/**
 * @Description:  excel 自定义日期
 * @Param:
 * @return:
 * @Author: lunan
 * @Date: 2018/7/9
 */
class XSSFDateUtil extends DateUtil {
    protected static int absoluteDay(Calendar cal, boolean use1904windowing) {
        return DateUtil.absoluteDay(cal, use1904windowing);
    }
}
