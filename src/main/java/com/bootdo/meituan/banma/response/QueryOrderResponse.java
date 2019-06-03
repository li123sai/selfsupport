package com.bootdo.meituan.banma.response;


import com.bootdo.meituan.banma.vo.OrderStatusInfo;

/**
 * 查询订单状态响应类
 */
public class QueryOrderResponse extends AbstractResponse {

    private OrderStatusInfo data;

    public OrderStatusInfo getData() {
        return data;
    }

    public void setData(OrderStatusInfo data) {
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
