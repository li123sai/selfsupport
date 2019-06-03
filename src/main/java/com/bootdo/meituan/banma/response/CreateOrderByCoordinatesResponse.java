package com.bootdo.meituan.banma.response;


import com.bootdo.meituan.banma.vo.OrderIdInfo;

/**
 * 订单创建(送货分拣方式)响应类
 */
public class CreateOrderByCoordinatesResponse extends AbstractResponse {

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
