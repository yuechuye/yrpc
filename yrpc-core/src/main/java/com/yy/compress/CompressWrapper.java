package com.yy.compress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 压缩包装类，用于根据不同的压缩类型选择相应的压缩器进行数据压缩。
 * 该类提供了对压缩类型和压缩器的封装，以便于管理和使用不同的压缩算法。
 */
public class CompressWrapper {
    /**
     * 压缩代码，用于标识不同的压缩类型。
     * 通过压缩代码，可以快速选择和切换不同的压缩算法。
     */
    private byte code;

    /**
     * 压缩类型，描述当前使用的压缩算法的类型。
     * 压缩类型可以是例如"ZIP"、"GZIP"等字符串，用于更直观地表示和区分不同的压缩算法。
     */
    private String compressType;

    /**
     * 压缩器实例，用于执行实际的压缩操作。
     * 压缩器是根据压缩类型动态选择和创建的，每种压缩类型对应一个压缩器实例。
     */
    private Compressor compressor;
}
