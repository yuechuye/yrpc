package com.yy.compress;

/**
 * Compressor接口定义了压缩和解压缩字节数据的功能。
 * 它提供了两种方法：compress用于压缩字节数组，decompress用于解压缩字节数组。
 */
public interface Compressor {
    /**
     * 压缩给定的字节数组。
     *
     * @param bytes 待压缩的字节数组。
     * @return 压缩后的字节数组。
     */
    /**
     * 对字节进行压缩
     * @param bytes 待压缩的字节数组
     * @return 压缩后的字节数组
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压缩给定的字节数组。
     *
     * @param bytes 待解压缩的字节数组。
     * @return 解压缩后的字节数组。
     */
    /**
     * 对字节进行解压
     * @param bytes 待解压的字节数组
     * @return 解压后的字节数组
     */
    byte[] decompress(byte[] bytes);
}
