package com.yy;

import com.yy.config.Configuration;

/**
 * @author yuechu
 */
public class RpcBootStrap {

    private static final RpcBootStrap RPC_BOOT_STRAP = new RpcBootStrap();

    private Configuration configuration;


    public RpcBootStrap() {
        this.configuration = new Configuration();
    }

    public static RpcBootStrap getInstance() {
        return RPC_BOOT_STRAP;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

}
