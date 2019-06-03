package com.bootdo.meituan.banma.response;


import com.bootdo.meituan.banma.vo.OrderIdInfo;

/**
 * 取消订单响应类
 */
public class CancelOrderResponse extends AbstractResponse {

    private OrderIdInfo data;

    public OrderIdInfo getData() {
        return data;
    }

    public void setData(OrderIdInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CreateOrderResponse {" +
                "code=" + code +
                ", message=" + message +
                ", data=" + data +
                '}';
    }
}
