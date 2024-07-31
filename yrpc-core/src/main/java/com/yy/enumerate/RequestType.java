package com.yy.enumerate;

/**
 * 请求类型的枚举类，定义了不同的请求类型。
 */
public enum RequestType {

    /**
     * 普通请求类型。
     * 请求类型标识为1，代表一个普通的业务请求。
     */
    REQUEST((byte) 1, "普通请求"),

    /**
     * 心跳请求类型。
     * 请求类型标识为2，用于检测连接是否正常，不包含具体的业务逻辑。
     */
    HEARTBEAT((byte) 2, "心跳检测");

    /**
     * 构造方法，初始化请求类型。
     *
     * @param i       请求类型的标识字节，用于内部标识和传输。
     * @param typeName 请求类型的名称，用于外部描述和展示。
     */
    RequestType(byte i, String typeName) {
        this.i = i;
        this.typeName = typeName;
    }

    /**
     * 获取请求类型的标识字节。
     *
     * @return 请求类型的标识字节。
     */
    public byte getI() {
        return i;
    }

    /**
     * 获取请求类型的名称。
     *
     * @return 请求类型的名称。
     */
    public String getTypeName() {
        return typeName;
    }

    private byte i;
    private String typeName;
}

