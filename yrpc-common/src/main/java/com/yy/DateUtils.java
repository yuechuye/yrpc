package com.yy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类，提供日期相关的静态方法。
 * @author yuechu
 */
public class DateUtils {

    /**
     * 根据指定的日期格式字符串解析日期。
     *
     * @param pattern 日期格式字符串，例如"yyyy-MM-dd"。
     * @return 解析后的Date对象。
     * @throws RuntimeException 如果解析过程中发生错误，则抛出运行时异常。
     */
    public static Date get(String pattern){
        // 使用默认的日期格式"yyyy-MM-dd"创建SimpleDateFormat实例
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 尝试解析传入的pattern字符串为Date对象
            return simpleDateFormat.parse(pattern);
        } catch (ParseException e) {
            // 如果解析失败，抛出运行时异常，并将解析异常作为原因
            throw new RuntimeException(e);
        }
    }
}
