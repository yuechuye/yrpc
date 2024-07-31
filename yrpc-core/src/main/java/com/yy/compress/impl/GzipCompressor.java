package com.yy.compress.impl;


import com.yy.common.exceptions.CompressException;
import com.yy.compress.Compressor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
/**
 * Gzip压缩器实现类，实现了Compressor接口，用于提供Gzip压缩和解压缩的功能。
 */
public class GzipCompressor implements Compressor {
    /**
     * 对给定的字节数组进行Gzip压缩。
     *
     * @param bytes 待压缩的字节数组
     * @return 压缩后的字节数组，如果输入为null则返回null
     * @throws CompressException 如果压缩过程中发生IO异常，则抛出此异常
     */
    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null){
            return null;
        }
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(bos)){
            gos.write(bytes);
            gos.finish();
            byte[] byteArray = bos.toByteArray();
            log.debug("压缩字节数组从【{}】压缩到了【{}】", bytes.length, byteArray.length);
            return byteArray;
        } catch (IOException e) {
            log.error("压缩过程出现问题");
            throw new CompressException(e);
        }
    }

    /**
     * 对给定的Gzip压缩字节数组进行解压缩。
     *
     * @param bytes Gzip压缩后的字节数组
     * @return 解压缩后的字节数组，如果输入为null则返回null
     * @throws CompressException 如果解压缩过程中发生IO异常，则抛出此异常
     */
    @Override
    public byte[] decompress(byte[] bytes) {
        if (bytes == null){
            return null;
        }
        try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            GZIPInputStream gis = new GZIPInputStream(bis)){
            byte[] allBytes = gis.readAllBytes();
            log.debug("压缩字节数组从【{}】解压到了【{}】", bytes.length, allBytes.length);
            return allBytes;
        } catch (IOException e) {
            log.error("解压过程出现问题");
            throw new CompressException(e);
        }
    }
}

