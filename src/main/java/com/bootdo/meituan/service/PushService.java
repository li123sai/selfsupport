package com.bootdo.meituan.service;

/**
 * @ClassName PushService
 * @Author LS
 * @Description 接口描述:
 * @Date 2019/6/3 23:02
 * @Version 1.0
 **/
public interface PushService {
    void cancelOrder(Long deliveryId, String mtPeisongId, int cancelReasonId, String cancelReason);
}
