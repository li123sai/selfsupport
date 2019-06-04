package com.bootdo.common.utils.pub;

/**
* @Author :  LS
* @Description  描述: 系统所需常量
* @Date   :  2018/11/5 15:08
* @Param  :
* @return :
**/
public class SystemConstants {
	// 验证码缓存key
	public static final String VERIFY_CODE_CACHE_KEY="verify_code_";
	public static final String VERIFY_CODE_FORGOT_CACHE_KEY="verify_code_forgot_";
	// 验证码验证次数缓存key
	public static final String VERIFY_CODE_NUM_CACHE_KEY="verify_code_num_";

	// 短信验证码key
	public static final String VERIFY_CODE_PHONE_CACHE_KEY="verify_code_phone_";
	// 忘记密码验证码key
	public static final String VERIFY_CODE_PHONE_FORGOT_CACHE_KEY="verify_code_phone_forgot_";

	// 绑定手机号验证码key
	public static final String VERIFY_CODE_PHONE_BINDING_CACHE_KEY="verify_code_phone_binding_";

	public static final String USER_EXCHANGE_SCALE="user_exchange_scale_";
	public static final String OUT_NGOT_RECORD_STATUS_MSG="out_ngot_record_status_msg";
	public static final String OUT_RECORD_STATUS_NGOTMT_MSG="out_record_status_ngotmt_msg";
	public static final String OUT_AOA_RECORD_STATUS_NGOTMT_MSG="out_aoa_record_status_ngotmt_msg";
	// 邮箱正则表达式
	public static final String EMAIL_PATTERN = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	// 手机号正则表达式
	public static final String PHONE_PATTERN = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
	//私钥
	public static final String APPSECERT="3ba840858f39aa25af9dcae5c1fdccf9";

	public static final String PASSWORD_PATTERN="^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";

	public static final String SEND_SMS_LIST_GN="send_sms_list_gn";
	public static final String SEND_SMS_LIST_GJ="send_sms_list_gj";
	public static final String SEND_SMS_CHANNEL="send_sms_channel";

}
