package com.bootdo.meituan.banma.response;


import com.bootdo.meituan.banma.vo.OrderIdInfo;

/**
 * 评价骑手响应类
 */
public class EvaluateResponse extends AbstractResponse {

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
