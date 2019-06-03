package com.bootdo.meituan.banma.vo;

import java.util.List;

/**
 * 订单商品信息列表
 */
public class OpenApiGoods {
    private List<OpenApiGood> goods;

    public List<OpenApiGood> getGoods() {
        return goods;
    }

    public void setGoods(List<OpenApiGood> goods) {
        this.goods = goods;
    }

    @Override
    public String toString() {
        return "OpenApiGoods {" +
                "goods=" + goods +
                "}";
    }
}


