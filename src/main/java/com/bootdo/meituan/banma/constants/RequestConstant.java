package com.bootdo.meituan.banma.constants;

/**
 * API接口信息
 */
public class RequestConstant {

    /**
     * 开放平台接口地址，正式账号/测试账号采用同一地址
     */
    public static final String API_URL = "https://peisongopen.meituan.com/api";

    /**
     * 订单创建（门店方式）
     */
    public static final String ORDER_CREATE_BY_SHOP = API_URL + "/order/createByShop";

    /**
     * 取消订单
     */
    public static final String ORDER_CANCEL = API_URL + "/order/delete";

    /**
     * 查询订单状态
     */
    public static final String ORDER_QUERY = API_URL + "/order/status/query";

    /**
     * 模拟测试订单接单
     */
    public static final String MOCK_ORDER_ACCEPT = API_URL + "/test/order/arrange";

    /**
     * 模拟测试订单取货
     */
    public static final String MOCK_ORDER_PICKUP = API_URL + "/test/order/pickup";

    /**
     * 模拟测试订单完成
     */
    public static final String MOCK_ORDER_DELIVER = API_URL + "/test/order/deliver";

    /**
     * 模拟测试订单改派
     */
    public static final String MOCK_ORDER_REARRANGE = API_URL + "/test/order/rearrange";

    /**
     * 配送能力预校验
     */
    public static final String ORDER_CHECK_DELIVERY_ABILITY = API_URL + "/order/check";

    /**
     * 订单创建（送货分拣方式）
     */
    public static final String ORDER_CREATE_BY_COORDINATES = API_URL + "/order/createByCoordinates";

    /**
     * 评价骑手
     */
    public static final String ORDER_EVALUATE = API_URL + "/order/evaluate";

    /**
     * 获取骑手当前位置
     */
    public static final String GET_RIDER_LOCATION = API_URL + "/order/rider/location";

    /**
     * 查询合作方门店的配送范围
     */
    public static final String QUERY_SHOP_AREA = API_URL + "/shop/area/query";
}
