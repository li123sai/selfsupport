package com.bootdo.meituan.service.impl;

import com.bootdo.common.utils.idutis.IdWorker;
import com.bootdo.meituan.banma.constants.CancelOrderReasonId;
import com.bootdo.meituan.banma.constants.OpenApiConfig;
import com.bootdo.meituan.banma.constants.RequestConstant;
import com.bootdo.meituan.banma.request.CancelOrderRequest;
import com.bootdo.meituan.banma.sign.SignHelper;
import com.bootdo.meituan.banma.util.HttpClient;
import com.bootdo.meituan.banma.util.ParamBuilder;
import com.bootdo.meituan.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @ClassName PushServiceImpl
 * @Author LS
 * @Description 类描述:
 * @Date 2019/6/3 22:59
 * @Version 1.0
 **/
@Service
@Slf4j
public class PushServiceImpl implements PushService{
    @Autowired
    private IdWorker idWorker;


    /**
     * 取消订单
     * @param deliveryId        配送活动标识
     * @param mtPeisongId       美团配送内部订单id，最长不超过32个字符
     * @param cancelReasonId    取消原因类别
     * @param cancelReason      详细取消原因
     */
    public void cancelOrder(Long deliveryId, String mtPeisongId, int cancelReasonId, String cancelReason) {
        try {
            CancelOrderRequest cor = new CancelOrderRequest();
            cor.setAppkey(OpenApiConfig.TEST_APP_KEY);
            cor.setTimestamp(System.currentTimeMillis());
            cor.setVersion("1.0");
            cor.setCancelOrderReasonId(CancelOrderReasonId.PARTNER_REASON);
            cor.setCancelReason(cancelReason);
            cor.setDeliveryId(deliveryId);
            cor.setMtPeisongId(mtPeisongId);

            //签名
            String sign = SignHelper.generateSign(ParamBuilder.convertToMap(cor), OpenApiConfig.TEST_SECRET);

            cor.setSign(sign);
            Map<String, String> endMap = ParamBuilder.convertToMap(cor);
            HttpClient.post(RequestConstant.ORDER_CANCEL, endMap);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException no) {
            no.printStackTrace();
            log.info("签名异常");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("取消订单出现异常");
        }
    }

}
