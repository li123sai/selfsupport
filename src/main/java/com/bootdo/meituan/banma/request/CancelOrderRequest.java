package com.bootdo.meituan.banma.request;


import com.bootdo.meituan.banma.constants.CancelOrderReasonId;

/**
 * 取消订单参数
 */
public class CancelOrderRequest extends AbstractRequest {
    /**
     * 配送活动标识
     */
    private long deliveryId;

    /**
     * 美团配送内部订单id，最长不超过32个字符
     */
    private String mtPeisongId;

    /**
     * 取消原因类别，默认为接入方原因
     */
    private CancelOrderReasonId cancelOrderReasonId;

    /**
     * 详细取消原因，最长不超过256个字符
     */
    private String cancelReason;


    public long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getMtPeisongId() {
        return mtPeisongId;
    }

    public void setMtPeisongId(String mtPeisongId) {
        this.mtPeisongId = mtPeisongId;
    }

    public CancelOrderReasonId getCancelOrderReasonId() {
        return cancelOrderReasonId;
    }

    public void setCancelOrderReasonId(CancelOrderReasonId cancelOrderReasonId) {
        this.cancelOrderReasonId = cancelOrderReasonId;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @Override
    public String toString() {
        return "CancelOrderRequest [deliveryId=" + deliveryId + ", mtPeisongId="
                + mtPeisongId + ", cancelOrderReasonId=" + cancelOrderReasonId
                + ", cancelReason=" + cancelReason + "]";
    }
}
