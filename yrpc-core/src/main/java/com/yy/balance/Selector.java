package com.yy.balance;

import java.net.InetSocketAddress;

/**
 * 选择器接口。
 * <p>
 * 该接口定义了一个选择器的行为，选择器用于从一组候选人中选择下一个要处理的元素。
 * 在具体的实现中，候选人可以是任何类型的对象，而选择逻辑则根据具体应用场景来定义。
 * </p>
 */
public interface Selector {


    /**
     * 获取下一个要处理的地址。
     * <p>
     * 该方法用于从一组候选人中选择下一个要处理的地址。在具体的实现中，地址的选择逻辑可以根据需求来定义。
     * </p>
     *
     * @return 返回一个InetSocketAddress对象，表示下一个要处理的地址。
     */
    InetSocketAddress getNext();

}

