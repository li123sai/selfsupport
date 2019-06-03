package com.bootdo.meituan.service.impl;

import com.bootdo.common.utils.StringUtils;
import com.bootdo.common.utils.idutis.IdWorker;
import com.bootdo.meituan.banma.constants.CancelOrderReasonId;
import com.bootdo.meituan.banma.constants.OpenApiConfig;
import com.bootdo.meituan.banma.constants.RequestConstant;
import com.bootdo.meituan.banma.request.CancelOrderRequest;
import com.bootdo.meituan.banma.sign.SignHelper;
import com.bootdo.meituan.banma.util.HttpClient;
import com.bootdo.meituan.banma.util.ParamBuilder;
import com.bootdo.meituan.banma.util.SHA1Util;
import com.bootdo.meituan.service.DeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName DeliveryServiceImpl
 * @Author LS
 * @Description 类描述:
 * @Date 2019/5/30 20:35
 * @Version 1.0
 **/
@Service
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private IdWorker idWorker;

    public void crateByShop(){
        Map<String,Object> map=new HashMap<>();
/*        map.put("delivery_id",);
        map.put("order_id",);
        map.put("shop_id",);
        map.put("delivery_service_code",);
        map.put("receiver_name",);
        map.put("",);
        map.put("",);
        map.put("",);
        map.put("",);
        map.put("",);
        map.put("",);*/
        //HttpClient.post(RequestConstant.ORDER_CREATE_BY_SHOP,);
    }


    public void cancelOrder(Long deliveryId,String mtPeisongId,int cancelReasonId,String cancelReason ){
        try {
            CancelOrderRequest cor = new CancelOrderRequest();
            cor.setAppkey(OpenApiConfig.TEST_APP_KEY);

            cor.setTimestamp(System.currentTimeMillis());
            cor.setVersion("1.0");
            cor.setCancelOrderReasonId(CancelOrderReasonId.PARTNER_REASON);
            cor.setCancelReason(cancelReason);
            cor.setDeliveryId(deliveryId);
            cor.setMtPeisongId(mtPeisongId);
            Map<String, String> stringStringMap = ParamBuilder.convertToMap(cor);
            //加密签名
            Map<String, String> pm = ParamBuilder.convertToMap(cor);
            Set<String> set = pm.keySet();
            List<String> keyList = new ArrayList<>(set);
            Collections.sort(keyList);
            // 加密前字符串拼接
            StringBuilder signStr = new StringBuilder();
            for (String key : keyList) {
                if (key.equals( "sign" )) {
                    continue ;
                }
                Object value = pm.get(key);
                if (value == null || (value.getClass().isArray() && byte . class .isAssignableFrom(value.getClass().getComponentType()))) {
                    continue ;
                }
                String valueString = value.toString();
                if (StringUtils.isEmpty(valueString)) {
                    continue ;
                }
                signStr.append(key).append(value);
            }
            // 计算SHA1签名
            String sign = SHA1Util.Sha1( OpenApiConfig.TEST_SECRET + signStr.toString()).toLowerCase();

            cor.setSign(sign);
            Map<String, String> endMap = ParamBuilder.convertToMap(cor);
            HttpClient.post(RequestConstant.ORDER_CANCEL, endMap);
        }catch(Exception e){
            e.printStackTrace();
            log.info("取消订单出现异常");
        }
    }
}
