package com.bootdo.meituan.banma.request;

/**
 * 查询订单参数
 */
public class QueryShopAreaRequest extends AbstractRequest {

    /**
     * 配送服务代码
     */
    private Integer deliveryServiceCode;

    /**
     * 取货门店id
     */
    private String shopId;


    public Integer getDeliveryServiceCode() {
        return deliveryServiceCode;
    }

    public void setDeliveryServiceCode(Integer deliveryServiceCode) {
        this.deliveryServiceCode = deliveryServiceCode;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "QueryShopAreaRequest{" +
                "deliveryServiceCode=" + deliveryServiceCode +
                ", shopId='" + shopId + '\'' +
                ", appkey='" + appkey + '\'' +
                ", timestamp=" + timestamp +
                ", version='" + version + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
