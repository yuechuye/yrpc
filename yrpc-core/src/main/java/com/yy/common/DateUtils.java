package com.yy.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类，提供日期相关的静态方法。
 */
public class DateUtils {

    /**
     * 根据指定的日期格式字符串解析日期。
     *
     * @param pattern 日期字符串的格式，例如"yyyy-MM-dd"。
     * @return 解析后的Date对象。
     * @throws RuntimeException 如果解析失败，则抛出运行时异常。
     */
    public static Date get(String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return simpleDateFormat.parse(pattern);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
