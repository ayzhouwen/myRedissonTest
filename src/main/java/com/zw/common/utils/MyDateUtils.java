package com.zw.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 时间工具类
 *
 * @author kingsmartsi
 */
@Slf4j
public class MyDateUtils  {

    /**
     * 输出执行时间
     * @param name
     * @param starttime
     */
    public static String execTime(String name,Long starttime){
        Double time= Convert.toDouble(DateUtil.spendMs(starttime));
        StringBuilder sb=new StringBuilder();
        sb.append(name).append(":").append(time).append("毫秒,").append( NumberUtil.decimalFormat("#.##",time/1000))
                .append("秒,").append(NumberUtil.decimalFormat("#.##",time/1000/60)).append("分钟");
        return sb.toString();
    }
}
