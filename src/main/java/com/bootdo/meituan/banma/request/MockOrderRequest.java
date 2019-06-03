package com.bootdo.meituan.banma.request;

/**
 * 模拟订单参数
 */
public class MockOrderRequest extends AbstractRequest {

    /**
     * 配送活动标识
     */
    private Long deliveryId;

    /**
     * 美团配送内部订单id，最长不超过32个字符
     */
    private String mtPeisongId;

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getMtPeisongId() {
        return mtPeisongId;
    }

    public void setMtPeisongId(String mtPeisongId) {
        this.mtPeisongId = mtPeisongId;
    }

    @Override
    public String toString() {
        return "MockOrderRequest [" +
                "deliveryId=" + deliveryId +
                ", mtPeisongId=" + mtPeisongId + "]";
    }
}
