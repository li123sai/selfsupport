package com.bootdo.meituan.controller;

import com.bootdo.common.controller.ResultController;
import com.bootdo.common.utils.pub.PubMethod;
import com.bootdo.meituan.banma.response.QueryOrderResponse;
import com.bootdo.meituan.service.QueryServie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.util.resources.ga.LocaleNames_ga;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName QueryController
 * @Author LS
 * @Description 类描述:
 * @Date 2019/6/4 10:20
 * @Version 1.0
 **/
@Controller
@RequestMapping("mt/query")
public class QueryController extends ResultController {

    @Autowired
    private QueryServie queryServie;


    @ResponseBody
    @RequestMapping(value = "orderSatus",method = {RequestMethod.POST})
    public Map<String,Object> queryOrderSataus(@RequestParam(value = "deliveryId",required = true)String deliveryId,
                                               @RequestParam(value = "mtPeisongId",required = true)String mtPeisongId,
                                               HttpServletResponse response){
        //解决跨域请求
        //response.addHeader("Access-Control-Allow-Origin", "*");
        QueryOrderResponse orderStatus = queryServie.findOrderStatus(deliveryId, mtPeisongId);
        if( PubMethod.isEmpty(orderStatus.getData()) || !PubMethod.isEmpty(orderStatus) ){
            return resultFailure(orderStatus.getCode(),orderStatus.getMessage());
        }
        return  resultSuccess(orderStatus.getData());

    }

}
