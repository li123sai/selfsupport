package com.bootdo.meituan.service.impl;

import com.alibaba.fastjson.JSON;
import com.bootdo.common.utils.pub.PubMethod;
import com.bootdo.meituan.banma.constants.OpenApiConfig;
import com.bootdo.meituan.banma.constants.RequestConstant;
import com.bootdo.meituan.banma.request.QueryOrderRequest;
import com.bootdo.meituan.banma.response.QueryOrderResponse;
import com.bootdo.meituan.banma.sign.SignHelper;
import com.bootdo.meituan.banma.util.HttpClient;
import com.bootdo.meituan.banma.util.ParamBuilder;
import com.bootdo.meituan.service.QueryServie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName QueryServiceImpl
 * @Author LS
 * @Description 类描述:
 * @Date 2019/6/3 23:01
 * @Version 1.0
 **/
@Service
@Slf4j
public class QueryServiceImpl implements QueryServie{


    /**
     * 查询订单状态及对应的骑手信息
     * @param deliveryId    配送活动标识
     * @param mtPeisongId   美团配送内部订单id，最长不超过32个字符
     */
    @Override
    public QueryOrderResponse findOrderStatus(String deliveryId,String mtPeisongId){
        QueryOrderResponse queryOrderResponse=null;
        if(PubMethod.isEmpty(deliveryId) || PubMethod.isEmpty(mtPeisongId)){
            return null;
        }
        try{
            QueryOrderRequest qor=new QueryOrderRequest();
            qor.setAppkey(OpenApiConfig.TEST_APP_KEY);
            qor.setTimestamp(System.currentTimeMillis());
            qor.setVersion("1.0");
            qor.setDeliveryId(Long.valueOf(deliveryId));
            qor.setMtPeisongId(mtPeisongId);

            String sign = SignHelper.generateSign(ParamBuilder.convertToMap(qor), OpenApiConfig.TEST_SECRET);
            qor.setSign(sign);
            String result = HttpClient.post(RequestConstant.ORDER_QUERY, ParamBuilder.convertToMap(qor));
            queryOrderResponse = PubMethod.Json2Object(result, QueryOrderResponse.class);
            return queryOrderResponse;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException no) {
            no.printStackTrace();
            log.info("签名异常");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("查询订单出现异常");
        }
        return null;
    }


    public static void main(String[] args) {
        QueryServiceImpl queryService=new QueryServiceImpl();
        queryService.findOrderStatus("123","1231");
    }
}
