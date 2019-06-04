package com.bootdo.meituan.service;

import com.bootdo.meituan.banma.response.QueryOrderResponse;

/**
 * @InterfaceName QueryServie
 * @Author LS
 * @Description 接口描述:
 * @Date 2019/6/3 23:02
 * @Version 1.0
 **/
public interface QueryServie {
    QueryOrderResponse findOrderStatus(String deliveryId, String mtPeisongId);
}
