package com.bootdo.common.utils.pub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PubMethod {
	private static Logger logger = LoggerFactory.getLogger(PubMethod.class);
	private static boolean DEBUG = false;
	public static String DATE_FORMAT = "yyyy-MM-dd";
	public static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static String[] TYPE_SIMPLE = {"java.lang.Integer", "java.lang.Double", "java.lang.Long", "java.lang.Short", "int", "java.sql.Timestamp"};
	public static String TYPE_INTEGER = "java.lang.Integer,int";
	public static String TYPE_DATE = "java.sql.Timestamp";
	public static String TYPE_DOUBLE = "java.lang.Double";
	public static String TYPE_LONG = "java.lang.Long";
	public static String TYPE_SHORT = "java.lang.Short";

	public static void toPrintln(Object content) {
		if (DEBUG) {
			logger.info("Print at " + (new Date()) + ": <<<" + (content == null ? "null" : content.toString()) + ">>>");
		}
	}
	public static void toPrintln() {
		if (DEBUG) {
			System.out.println();
		}
	}

	/**
	 * 将iSource转为长度为iArrayLen的byte数组，字节数组的低位是整型的低字节位
	 * 
	 * @param iSource
	 * @param iArrayLen
	 * @return
	 */
	public static byte[] toByteArray(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	/**
	 * 将iSource转为长度为iArrayLen的byte数组，字节数组的低位是long的低字节位
	 * 
	 * @param iSource
	 * @param iArrayLen
	 * @return
	 */
	public static byte[] toByteArray(long iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 8) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	/**
	 * 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
	 * 
	 * @param bRefArr
	 * @return
	 */
	public static int toInt(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < 4; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	/**
	 * 将byte数组bRefArr转为一个长整数,字节数组的低位是整型的低字节位
	 * 
	 * @param bRefArr
	 * @return
	 */
	public static long toLong(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < 8; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	public static String formatDouble(DecimalFormat formater, Double val) {
		if (isEmpty(formater))
			formater = new DecimalFormat("#.00");
		if (val == null)
			return "0.00";
		return formater.format(val.doubleValue());
	}

	public static double convertDouble2double(Double val) {
		if (isEmpty(val)) {
			return 0.0;
		}
		return val.doubleValue();
	}


	// 异常数值回写
	public void ReWrtOnError(String actionname, String methodname, HttpServletRequest request) {
		String url = actionname + "?method=" + methodname;
		if (!url.startsWith("/")) {
			url = "/" + actionname + "?method=" + methodname;
		}
		Map paramMap = request.getParameterMap();
		if (!PubMethod.isEmpty(paramMap)) {
			Iterator keysetiterator = paramMap.keySet().iterator();
			for (; keysetiterator.hasNext();) {
				String key = "" + keysetiterator.next();
				if ("bizdivCondition".equals(key))
					continue;
				String val = null;
				try {
					val = request.getParameter(key);
				} catch (Exception e) {
				}
				if ((val != null) && !val.equals("")) {
					url += "&" + key + "=" + val;
				}
			}
		}
		request.setAttribute("retpath", url);// 抛异常

	}

	/*
	 * 把数值转换为List author:
	 */
	public static List Array2List(Object[] objarr) {
		if (isEmpty(objarr))
			return null;
		else {
			List list = new ArrayList();
			int len = objarr.length;
			for (int index = 0; index < len; index++) {
				list.add(objarr[index]);
			}
			return list;
		}
	}

	/*
	 * 判断结果集是否唯一 @author:
	 */
	public static boolean isUnique(List list) {
		if (!isEmpty(list)) {
			if (list.size() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * 判断结果集是否唯一 @author:
	 */
	public static boolean isUnique(Collection collection) {
		if (!isEmpty(collection)) {
			if (collection.size() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * 判断结果集是否唯一 @author:
	 */
	public static boolean isUnique(Set set) {
		if (!isEmpty(set)) {
			if (set.size() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * 判断结果集是否唯一 @author:
	 */
	public static boolean isUnique(Map map) {
		if (!isEmpty(map)) {
			if (map.size() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * 获取字符串 @author:
	 */
	public static String getString(Object obj) {
		if (obj == null || obj.equals("null"))
			return "";
		else
			return obj.toString();
	}

	/*
	 * 把List里头的对象转换为以特定分割符号分割的字符串。
	 * 
	 * 主要用于生成 sql/hql 语句中的In 子句。
	 * 
	 * author:chen-huiming.
	 */
	public static String getList2StringBySplitter(List listObj, String Splitter) {
		if (Splitter == null)
			Splitter = ",";
		if (Splitter.equals(""))
			Splitter = ",";
		String tmp = " ";
		if (listObj == null || listObj.size() == 0)
			return "";
		int objSize = listObj.size();

		for (int index = 0; index < objSize; index++) {
			if (index != objSize - 1) {
				if (listObj.get(index) instanceof String)
					tmp += "'" + listObj.get(index) + "'" + Splitter;
				else
					tmp += listObj.get(index) + Splitter;
			} else {
				if (listObj.get(index) instanceof String)
					tmp += "'" + listObj.get(index) + "'";
				else
					tmp += listObj.get(index);
			}

		}
		return tmp;
	}

	/*
	 * 获取对象的所有信息 @author:chenhm
	 */
	public static Map getObjFieldVals(Object obj) {
		if (isEmpty(obj))
			return null;
		Map result = null;
		Field[] Fieldsarray = obj.getClass().getDeclaredFields();

		int fildsLength = 0;
		BeanWrapper beanWrapper = new org.springframework.beans.BeanWrapperImpl(obj);
		if (!PubMethod.isEmpty(Fieldsarray)) {
			fildsLength = Fieldsarray.length;
			result = new HashMap(fildsLength);
			for (int index = 0; index < fildsLength; index++) {
				String fieldname = Fieldsarray[index].getName();
				Object objval = new Object();
				if (!fieldname.equals("serialVersionUID") && !fieldname.toUpperCase().equals("CGLIB$BOUND")) {
					try {
						objval = beanWrapper.getPropertyValue(fieldname);
					} catch (BeansException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!PubMethod.isEmpty(objval)) {
					result.put(fieldname, objval);
				}
			}
		}
		return result;
	}

	public static Map getObjFieldVals(Object obj, Class cls) {
		if (isEmpty(obj))
			return null;
		Map result = null;
		Field[] Fieldsarray = cls.getDeclaredFields();

		int fildsLength = 0;
		BeanWrapper beanWrapper = new org.springframework.beans.BeanWrapperImpl(obj);
		if (!PubMethod.isEmpty(Fieldsarray)) {
			fildsLength = Fieldsarray.length;
			result = new HashMap(fildsLength);
			for (int index = 0; index < fildsLength; index++) {
				String fieldname = Fieldsarray[index].getName();
				Object objval = new Object();
				String sobjval = "";
				if (!fieldname.equals("serialVersionUID")) {
					if (!isEmpty(beanWrapper.getPropertyValue(fieldname))) {
						objval = beanWrapper.getPropertyValue(fieldname);
						sobjval = objval.toString();
					}
				}
				if (!PubMethod.isEmpty(sobjval)) {
					result.put(fieldname, sobjval);
				} else {
					result.put(fieldname, "");
				}
			}
		}
		return result;
	}

	/*
	 * 获取包装后的对象. @author:
	 */
	public static BeanWrapper getWrapperedObj(Object obj) {
		BeanWrapper beanWrapper = new org.springframework.beans.BeanWrapperImpl(obj);
		return beanWrapper;
	}

	public static void initObject(Object srcObj) {

		if (srcObj == null)
			return;
		Method[] method = srcObj.getClass().getDeclaredMethods();
		for (int index = 0; index < method.length; index++) {
			String methodName = method[index].getName();
			methodName = (methodName == null) ? "" : methodName.trim();
			if (methodName.startsWith("get")) {
				String aa = method[index].getReturnType().getName();
				if (method[index].getReturnType().getName().equals("java.lang.String"))
					;
				else
					continue;
				String fieldName = methodName.substring(3);// cut 'get'
				Method desMethod = getMethodByName(srcObj, "set" + fieldName);
				Object val = null;
				try {

					val = method[index].invoke(srcObj, null);
					if (val == null || val.toString().equals("null"))
						desMethod.invoke(srcObj, new Object[]{""});
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * 获取包装后的对象 @author:
	 */
	public static List getWrapperedObjList(List list) {
		if (isEmpty(list))
			return null;
		List result = new ArrayList();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			result.add(getWrapperedObj(iterator.next()));
		}
		return result;
	}

	public static Object getFieldValue(final Object obj, String fieldName) {
		Object val = null;
		try {
			Method[] method = obj.getClass().getDeclaredMethods();
			for (int index = 0; index < method.length; index++) {
				PubMethod.toPrintln("method[index].getName():" + method[index].getName());
				if (method[index].getName().equalsIgnoreCase("get" + fieldName)) {
					PubMethod.toPrintln(fieldName);
					val = method[index].invoke(obj, null);
					PubMethod.toPrintln(fieldName + ":" + val);
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;
	}

	/**
	 * 检查类中是否有指定的方法名
	 *
	 * @author wangjianhua
	 * @param obj
	 * @param methodName
	 * @return
	 */
	public static boolean hasMethodByName(Object obj, String methodName) {
		boolean hasMethod = false;
		methodName = (methodName == null) ? "" : methodName.trim();
		if (isEmpty(obj) || isEmpty(methodName)) {
			return hasMethod;
		}

		Method[] method = obj.getClass().getDeclaredMethods();
		for (int index = 0; index < method.length; index++) {
			// PubMethod.toPrint("method[index].getName():"+method[index].getName());
			String tmpMethodName = method[index].getName();
			tmpMethodName = (tmpMethodName == null) ? "" : tmpMethodName.trim();
			if (tmpMethodName.equals(methodName)) {
				hasMethod = true;
				break;
			}
		}
		return hasMethod;
	}

	/**
	 * 通过指定的方法名找到Method
	 *
	 * @author wangjianhua
	 * @param obj
	 * @param methodName
	 * @return
	 */
	public static Method getMethodByName(Object obj, String methodName) {
		Method resMethod = null;
		methodName = (methodName == null) ? "" : methodName.trim();
		if (isEmpty(obj) || isEmpty(methodName)) {
			return resMethod;
		}

		Method[] method = obj.getClass().getDeclaredMethods();
		for (int index = 0; index < method.length; index++) {
			// PubMethod.toPrint("method[index].getName():"+method[index].getName());
			String tmpMethodName = method[index].getName();
			tmpMethodName = (tmpMethodName == null) ? "" : tmpMethodName.trim();
			if (tmpMethodName.equals(methodName)) {
				resMethod = method[index];
				break;
			}
		}
		return resMethod;
	}

	/**
	 * po对象的拷贝，用于业务po对象给该业务历史po对象赋值
	 * （注：相同名字的属性其类型须一致；且属性的类型最好不是原始类型，类似int.long，最好使用Integer.Long ...）
	 *
	 * @author wangjianhua
	 * @param srcObj
	 * @param desObj
	 * @return
	 */
	public static void copyPersistantObject(Object srcObj, Object desObj) {
		if (isEmpty(srcObj) || isEmpty(desObj)) {
			System.err.println("NullPointerException at PubMethod.copyPersistantObject\n...........");
			// throw new NullPointerException();
		}
		Method[] method = srcObj.getClass().getDeclaredMethods();
		for (int index = 0; index < method.length; index++) {
			// PubMethod.toPrint("method[index].getName():"+method[index].getName());
			String methodName = method[index].getName();
			methodName = (methodName == null) ? "" : methodName.trim();
			if (methodName.startsWith("get") && hasMethodByName(desObj, methodName)) {
				String fieldName = methodName.substring(3);// cut 'get'
				Method desMethod = getMethodByName(desObj, "set" + fieldName);
				Object val = null;
				try {
					val = method[index].invoke(srcObj, null);
					if (val == null || "".equals(val.toString().trim())) {
						continue;
					}
					desMethod.invoke(desObj, new Object[]{val});
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将PO对象的属性和值拼为sql 主要用在：后台界面中获得的数据来自biz，在界面上显示列表后需要后续处理时使用（如：代码申请中的代码发布）
	 *
	 * @param object
	 * @return
	 */
	public static String getObjectParam(Object object) {
		StringBuffer sb = new StringBuffer("");
		String flag = "<-|->";
		Method[] method = object.getClass().getDeclaredMethods();
		boolean hasParam = false;
		for (int index = 0; index < method.length; index++) {
			// PubMethod.toPrint("method[index].getName():"+method[index].getName());
			String methodName = method[index].getName();
			methodName = (methodName == null) ? "" : methodName.trim();
			if (methodName.startsWith("get")) {
				Object val = null;
				try {
					val = method[index].invoke(object, null);
					val = (val == null) ? new String("") : val;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				String fieldName = smallFistLetter(methodName.substring(3));
				sb.append(fieldName + "=" + val.toString()).append(flag);
				hasParam = true;
			}
		}
		String result = sb.toString();
		if (hasParam == true) {
			result = result.substring(0, result.length() - flag.length());
		}
		// PubMethod.toPrint(object.getClass().getName() + " - " + result);
		// com.sinosure.sol.persistence.po.VbCodeapply -
		// remark=<|>clientno=070001<|>bnsid=1145<|>bnsstate=109203<|>bnsstage=2<|>applicantno=008<|>chnname=kit
		// 猫<|>codeapplyid=4741<|>bizclientno=BIZ<|>bizacceptno=<|>applytype=1<|>bizbackreason=<|>deleteflag=1<|>applicant=操作员1<|>applydate=2007-04-27
		// 09:14:57.0<|>engname=hello kit<|>countrycode=<|>builddate=2007-04-24
		// 16:05:09.0<|>objectid=3607<|>approveopinion=<|>chnaddr=<|>engaddr=<|>approvecode=<|>hobjectid=5054<|>objecttype=3<|>bnsname=银行代码申请<|>clientchnname=买方名称<|>clientengname=英文名称
		// <|>acceptdate=<|>accepter=<|>accepterno=<|>builder=操作员1<|>builderno=008<|>clientsigner=<|>clientsignerno=<|>clientsigntime=<|>repealdate=<|>repealer=<|>repealerno=<|>repealreason=<|>solsigner=<|>solsignerno=<|>solsigntime=<|>bnscode=010902<|>
		return result;
	}

	/**
	 * 将通过方法 getObjectParam 生成的字符串组装成 PO对象，getObjectParam方法的反操作
	 * 只适合属性类型都是String类型的PO对象
	 *
	 * @param object
	 * @param params
	 */
	public static Object getObjectFromParams(Object object, String params) {
		params = (params == null) ? "" : params.trim();
		String flag = "<-|->";
		String[] paramStrArr = params.split(flag);
		for (int index = 0; index < paramStrArr.length; index++) {
			String param = paramStrArr[index];
			String fieldName = "";
			String value = "";
			int pos = param.indexOf("=");
			if (pos != -1) {
				fieldName = param.substring(0, pos);
				value = param.substring(pos + 1);
				try {
					Method method = object.getClass().getDeclaredMethod("set" + capitalFistLetter(fieldName), new Class[]{Class.forName("java.lang.String")});
					method.invoke(object, new Object[]{value});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return object;
	}

	/**
	 * 字符串的首字母小写
	 *
	 * @param val
	 * @return
	 */
	public static String smallFistLetter(String val) {
		val = (val == null) ? "" : val.trim();
		if ("".equals(val)) {
			return "";
		}
		val = val.substring(0, 1).toLowerCase() + val.substring(1);
		return val;
	}

	/**
	 * 字符串的首字母大写
	 *
	 * @param val
	 * @return
	 */
	public static String capitalFistLetter(String val) {
		val = (val == null) ? "" : val.trim();
		if ("".equals(val)) {
			return "";
		}
		val = val.substring(0, 1).toUpperCase() + val.substring(1);
		return val;
	}

	/**
	 * List 深拷贝:序列化|反序列化方法 注：记着放到集合中的元素要能够序列化，所以必须实现Serializable接口。
	 *
	 * @param src
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static List copyListBySerialize(List src) throws IOException, ClassNotFoundException {
		if (PubMethod.isEmpty(src))
			return null;

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		List dest = (List) in.readObject();
		return dest;
	}

	/**
	 * List 浅拷贝
	 *
	 * @param src
	 * @param dest
	 */
	public static void copyCollectionByAdd(Collection src, Collection dest) {
		if (PubMethod.isEmpty(src) || PubMethod.isEmpty(dest))
			return;

		// for (int i = 0 ; i< src.size() ;i++) {//jdk 1.4
		for (Object obj : src) {// jdk 1.5 以上版本
			// Object obj = src.get(i);
			dest.add(obj);
		}
	}

	/*
	 * 对象间值的相互拷贝.
	 */
	public static void copyPropeties(Object srcObj, Object destObj) {
		try {
			if (!PubMethod.isEmpty(srcObj)) {
				org.springframework.beans.BeanUtils.copyProperties(srcObj, destObj);
			}

		} catch (BeansException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 判断是否存在 单引号。
	public static boolean isContainSingleQuotes(String str) {
		if (str.indexOf("'") >= 0)
			return true;
		else
			return false;
	}

	// 判断是否为空。
	public static boolean isEmpty(String Value) {
		return (Value == null || Value.trim().equals(""));
	}

	/*
	 * @function:判空 @author:
	 */
	public static boolean isEmpty(List list) {
		if (list == null || list.size() == 0)
			return true;
		else
			return false;
	}

	/*
	 * @function:判空 @author:
	 */
	public static boolean isEmpty(Set set) {
		if (set == null || set.size() == 0)
			return true;
		else
			return false;
	}

	/*
	 * @function:判空 @author:
	 */
	public static boolean isEmpty(Map map) {
		if (map == null || map.size() == 0)
			return true;
		else
			return false;
	}

	// 判断是否为空。
	public static boolean isEmpty(Object Value) {
		if (Value == null)
			return true;
		else
			return false;
	}

	public static boolean isEmpty(Double value) {
		if (value == null || value.doubleValue() == 0.0)
			return true;
		else
			return false;
	}

	// 判断是否为空。
	public static boolean isEmpty(Long obj) {
		if (obj == null || obj.longValue() == 0)
			return true;
		else
			return false;
	}

	// 判断是否为空。
	public static boolean isEmpty(Object[] Value) {
		if (Value == null || Value.length == 0)
			return true;
		else
			return false;
	}

	// 返回有效状态值
	public static int validState() {
		return 1;
	}

	// 返回无效状态值
	public static int invalidState() {
		return 0;
	}

	// 判断状态是否有效。0无效、1有效、9删除。
	public static boolean isValid(int state) {
		if (state == 1)
			return true;
		else
			return false;
	}

	// Set集合到List的转换
	public static List getList(Set set) {
		List list = new ArrayList();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	/**
	 * 把List转换为Set
	 *
	 * @param list
	 * @return
	 */
	public static Set convertList2Set(List list) {
		if (list == null || list.size() == 0)
			return null;
		Set set = new HashSet();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			set.add(iterator.next());
		}
		return set;
	}

	// 返回删除状态值。
	public static int getDeletedState() {
		return 9;
	}

	// 写日志文件。(头信息，内容信息，绝对路径）
	public void wrtFile(String[] heads, List content, String AbsolutPath) {
		// 建立文件对象。
		File file = new File(AbsolutPath);
		FileWriter fileWriter = null;
		// 判断文件是否存在
		if (file.exists()) {
			try {
				// 存在则删除之。
				file.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 重新新建日志文件。
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 建立FileWriter对象（在file对象的基础上）。
		try {
			fileWriter = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 写文件头信息。
		for (int headCount = 0; headCount < heads.length; headCount++) {
			try {
				fileWriter.write(heads[headCount]);
				fileWriter.write("\t\t");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 换行
		try {
			if (fileWriter != null)
				fileWriter.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 写内容
		for (int contentCount = 0; contentCount < content.size(); ++contentCount) {
			// 每行内容
			String[] tempRowContent = (String[]) content.get(contentCount);
			// 写入一行信息。
			for (int cnt = 0; cnt < tempRowContent.length; cnt++) {
				try {
					// 滤去无效字符。（filter out unqulified characters).
					if (("" + tempRowContent[cnt]).equals("null")) {
						tempRowContent[cnt] = "";
					}
					fileWriter.write(tempRowContent[cnt]);
					fileWriter.write("\t\t");
					fileWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				fileWriter.write("\n");
				if (contentCount % 2000 == 0)
					fileWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			file = null;
			if (fileWriter != null) {
				fileWriter.close();
				fileWriter = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 按要求分割字符串.
	 */
	public static Object[] splitString(String srcStr, String splitter) {
		if (srcStr == null)
			return new String[]{""};
		String[] tmpArr = srcStr.split(splitter);
		if (tmpArr == null || tmpArr.length == 0) {
			return new String[]{""};
		} else {
			for (int index = 0; index < tmpArr.length; index++) {
				tmpArr[index] = tmpArr[index].trim();
			}
			return tmpArr;
		}
	}

	/**
	 * 分隔字符串
	 *
	 * @param src
	 * @param token
	 * @author wangjh
	 * @return
	 */
	public static String[] splits(String src, String token) {
		String[] res = null;
		if (src == null || "".equals(src.trim()) || token == null) {
			res = new String[0];
			return res;
		}
		token = token.trim();
		StringBuffer str = new StringBuffer();
		if (!"".equals(token)) {
			for (int i = src.indexOf(token); i != -1; i = src.indexOf(token)) {
				str.append(" " + src.substring(0, i + 1) + " ");
				src = src.substring(i + 1);
			}
		} else {
			token = " ";
			str.append(src);
		}

		StringTokenizer st = new StringTokenizer(str.toString(), token);
		res = new String[st.countTokens()];
		int j = 0;
		while (st.hasMoreElements()) {
			res[j] = ((String) st.nextElement()).trim();
			// PubMethod.toPrint("Token: <" + res[j] + ">");
			j++;
		}
		st = null;
		return res;
	}

	// 使用常用分割符号分割字符串.(,，.。空格|)
	public static List splitStringWithUsualTokens(String srcStr) {
		List result = new ArrayList();
		if (PubMethod.isEmpty(srcStr))
			return result;

		// srcStr =srcStr.replaceAll(".", ",");
		srcStr = srcStr.replaceAll("，", ",");
		srcStr = srcStr.replaceAll("。", ",");
		srcStr = srcStr.replaceAll("、", ",");
		srcStr = srcStr.replaceAll("@", ",");
		srcStr = srcStr.replaceAll("、", ",");
		srcStr = srcStr.replaceAll("/", ",");
		// srcStr =srcStr.replaceAll("|", ",");
		srcStr = srcStr.replaceAll(" ", ",");
		srcStr = srcStr.replaceAll("\t", ",");

		Object[] temp = splitString(srcStr, ",");
		if (!PubMethod.isEmpty(temp)) {
			for (int index = 0; index < temp.length; index++) {
				if ("".equals(("" + temp[index]).trim())) {
					continue;
				}
				if (",".equals(("" + temp[index]).trim())) {
					continue;
				}
				result.add(temp[index]);
			}
		} else {
			result.add(srcStr);
		}

		return result;
	}

	public static String formatDateTime(Date date, String format) {
		SimpleDateFormat outFormat = new SimpleDateFormat(format);
		return outFormat.format(date);
	}

	public static String formatDateTime(Timestamp ts, String format) {
		SimpleDateFormat outFormat = new SimpleDateFormat(format);
		return outFormat.format(ts);
	}

	public static String formatDateTime(Date date, String format, String timeZone) {
		SimpleDateFormat outFormat = new SimpleDateFormat(format);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());
		c.set(Calendar.MILLISECOND, 0);
		c.setTimeZone(TimeZone.getTimeZone(timeZone));
		return outFormat.format(c.getTime());
	}
	public static String formatDate(Date date) {
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return outFormat.format(date);
	}


	public static String formatDateByPattern(Date date,String pattern){
		SimpleDateFormat outFormat = new SimpleDateFormat(pattern);
		return outFormat.format(date);
	}

	/**
	 * 验证时间格式是否为yyyy-MM-dd HH:mm:ss，注意2011-1-1 23:59:59和2011-01-01
	 * 23:59:59认为都是合法的
	 *
	 * @param date
	 * @return
	 */
	public static boolean checkDateFormat(String date) {

		Pattern p = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher matcher = p.matcher(date);
		if (matcher.find()) {
			// System.out.println("right");
			return true;
		} else {
			// System.out.println("wrong");
			return false;
		}
	}
	public static boolean isYesterdayTime(Date atime, String timeZone) {
		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		cld.set(Calendar.MILLISECOND, 0);
		cld.add(Calendar.DAY_OF_MONTH, -1);
		int yyear = cld.get(Calendar.YEAR);
		int ymonth = cld.get(Calendar.MONTH);
		int yday = cld.get(Calendar.DAY_OF_MONTH);

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		c.setTime(atime);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int second = c.get(Calendar.SECOND);

		if (year == yyear && month == ymonth && day == yday && second > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isTodayTime(Date atime, String timeZone) {
		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		cld.set(Calendar.MILLISECOND, 0);
		int nowyear = cld.get(Calendar.YEAR);
		int nowmonth = cld.get(Calendar.MONTH);
		int nowday = cld.get(Calendar.DAY_OF_MONTH);

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		c.setTime(atime);
		c.set(Calendar.MILLISECOND, 0);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int second = c.get(Calendar.SECOND);

		if (year == nowyear && month == nowmonth && day == nowday && second > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isTodayTime(long atime, String timeZone) {
		return isTodayTime(new Date(atime), timeZone);
	}

	public static boolean isYesterdayTime(long atime, String timeZone) {
		return isYesterdayTime(new Date(atime), timeZone);
	}

	public static boolean isTodayTime(long atime) {
		Calendar cld = Calendar.getInstance();
		// cld.setTime(new Date());
		int year = cld.get(Calendar.YEAR);
		int month = cld.get(Calendar.MONTH);
		int day = cld.get(Calendar.DAY_OF_MONTH);
		Calendar todaycld = Calendar.getInstance();
		todaycld.set(year, month, day, 0, 0, 0);
		if (atime >= todaycld.getTime().getTime()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isLastdayTime(long atime) {
		Calendar cld = Calendar.getInstance();
		// cld.setTime(new Date());
		cld.add(Calendar.DAY_OF_MONTH, -1);
		int year = cld.get(Calendar.YEAR);
		int month = cld.get(Calendar.MONTH);
		int day = cld.get(Calendar.DAY_OF_MONTH);
		Calendar lastdaycld = Calendar.getInstance();
		lastdaycld.set(year, month, day, 0, 0, 0);
		if (atime >= lastdaycld.getTime().getTime()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getFileExt(String fileName) {
		if (fileName != null) {
			String fileExt = "";
			fileName = fileName.toLowerCase();
			int index = fileName.lastIndexOf(".");
			fileExt = fileName.substring(index, fileName.length());
			return fileExt;
		} else {
			return "";
		}
	}

	public static List stringValues2List(String[] values) {
		List l = new ArrayList();
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				l.add(values[i]);
			}
		} else {
			l.add("0");
		}
		return l;
	}

	public static String getWebRealPath(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(request.getServerName());
		if (request.getServerPort() != 80) {
			sb.append(":");
			sb.append(request.getServerPort());
		}

		// sb.append(request.getContextPath());
		// sb.append("/");
		return sb.toString();
	}

	/**
	 * 全角字符串转换为半角字符串
	 *
	 * @param QJstr
	 * @return
	 */
	public static String SBCchange(String QJstr) {
		String outStr = "";
		String Tstr = "";
		byte[] b = null;

		for (int i = 0; i < QJstr.length(); i++) {
			try {
				Tstr = QJstr.substring(i, i + 1);
				b = Tstr.getBytes("unicode");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			if (b[3] == -1) {
				b[2] = (byte) (b[2] + 32);
				b[3] = 0;

				try {
					outStr = outStr + new String(b, "unicode");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else
				outStr = outStr + Tstr;
		}

		return outStr;
	}

	/**
	 * 字符串左补零
	 *
	 * @param s
	 * @param len
	 * @return
	 */
	public static String lPad(String s, int len) {
		if (s.length() > len) {
			return s;
		}
		char[] cs = new char[len];
		for (int i = 0; i < len; i++) {
			if (len - s.length() > i) {
				cs[i] = '0';
			}
			else {
				cs[i] = s.charAt(i - (len - s.length()));
			}
		}
		return String.copyValueOf(cs);
	}

	/**
	 * 格式化成金额字符串 xxxx.xx
	 *
	 * @param str
	 * @return
	 */
	public static String format2Money(String str) {
		int len = str.length();
		String ret;
		if (len <= 2) {
			ret = "0." + lPad(str, 2);
		} else {
			ret = str.substring(0, len - 2) + "." + str.substring(len - 2);
		}
		return ret;
	}

	/**
	 * 格式化成金额字符串 xxx,xxx.xx
	 *
	 * @param obj
	 * @return
	 */
	public static String format2Money2(Object obj) {
		DecimalFormat format = new DecimalFormat("###,###.00");
		return format.format(obj);
	}

	/**
	 * 把传入的浮点数,截取指定的小数位
	 *
	 * @param f
	 * @param n
	 * @return
	 */
	public static String truncFloatNumber(double f, int n) {
		String str = String.valueOf(f);
		int pos = str.indexOf(".");
		if (n <= 0)
			return str.substring(0, pos);
		else {
			pos += n + 1;
			if (pos > str.length())
				pos = str.length();
			return str.substring(0, pos);
		}
	}

	/**
	 * 把传入的数字,转换为方便阅读的格式.(K,M, G等..) 如1024=1k, 1024k=1M,1024M=1G
	 *
	 * @param num
	 * @return
	 */
	public static String numberToReadbleString(double num) {
		if (num < 0)
			return "";
		if (num < 1024 * 1024) {
			return truncFloatNumber(num / 1024, 2) + "K";
		} else if ((num >= 1024 * 1024) && num < 1024 * 1024 * 1024) {
			return truncFloatNumber(num / (1024 * 1024), 2) + "M";
		} else {
			return truncFloatNumber(num / (1024 * 1024 * 1024), 2) + "G";
		}
	}

	/**
	 * 处理sql字符串中的单引号
	 *
	 * @param s1
	 * @return
	 */
	public static String fixSQLString(String s1) {
		String s2 = new String("");
		for (int i = 0; i < s1.length(); i++) {
			s2 += s1.charAt(i);
			if ('\'' == s1.charAt(i))
				s2 += '\'';
		}
		return s2;
	}

	/**
	 * 目的和fixSQLString一样，区别在首尾多加单引号 比如help'help --> 'help''help'
	 *
	 * @param s1
	 *            被修正的字符串
	 * @return 修正过的字符串
	 */
	public static String fixSQLStringPlusSingleQuote(String s1) {
		return "'" + fixSQLString(s1) + "'";
	}

	/**
	 * 判断字符串s是否包含再数组中
	 *
	 * @param s
	 * @param array
	 * @return
	 * @throws Exception
	 */
	public static boolean isInArray(String s, String[] array) throws Exception {
		boolean b = false;
		if (s == null)
			return b;
		try {
			for (int i = 0; array != null && i < array.length; i++) {
				if (s.equals(array[i]))
					return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return b;
	}

	/**
	 * 把数组内容组合成1个大字符串,每个数组元素添加单引号,并且用','分隔
	 *
	 * @param array
	 * @return
	 */
	public static String getStringFromArray(String[] array) {

		String str = "";
		if (array == null || array.length <= 0)
			return "";

		try {
			for (int i = 0; i < array.length; i++) {
				str += "'" + array[i] + "',";
			}
			if (str.length() > 0)
				str = str.substring(0, str.length() - 1);
		} catch (Exception e) {
		}

		return str;
	}

	public static Long checkLongNull(long o) {
		if (o != 0)
			return o == 0 ? new Long(0) : new Long(o);
		return new Long(o);
	}

	public static Double checkDoubleNull(double o) {
		if (o != 0)
			return o == 0 ? new Double(0) : new Double(o);
		return new Double(o);
	}

	public static String checkNull(String o) {
		if (o == null || o.equals(""))
			return "0";
		else
			return o;
	}

	/**
	 * 保留double的小数点后的位数
	 *
	 * @author wangjianhua
	 * @param val

	 * @return
	 */
	public static String roundDouble(double val) {
		DecimalFormat df = new DecimalFormat("#.##");

		return df.format(val);
		/*
		 * double factor = Math.pow(10, precision); return Math.floor(val *
		 * factor + 0.5) / factor;
		 */
	}

	/**
	 * toString
	 *
	 * @param object
	 * @return
	 */
	public static String objectToString(Object object) {
		return object == null ? "" : object.toString().trim();
	}

	/**
	 * 判断字符串是否在列表中存在
	 *
	 * @author wangjianhua
	 * @param list
	 * @param nodeNo
	 * @return
	 */
	public static boolean isInList(List list, String nodeNo) {
		if (PubMethod.isEmpty(list) || PubMethod.isEmpty(nodeNo)) {
			return false;
		}
		boolean result = false;
		for (Iterator it = list.iterator(); it.hasNext();) {
			String string = (String) it.next();
			if (nodeNo.equals(string)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * BigDecimal 转换为 Double
	 *
	 * @author wangjianhua
	 * @param value
	 * @return
	 */
	public static Double bigDecimal2Double(BigDecimal value) {
		if (PubMethod.isEmpty(value)) {
			return new Double(0.0);
		}
		return new Double(value.doubleValue());
	}

	public static Integer bigInteger2Integer(BigInteger value) {
		if (PubMethod.isEmpty(value)) {
			return new Integer(0);
		}
		return new Integer(value.intValue());
	}

	public static Long bigInteger2Long(BigInteger value) {
		if (PubMethod.isEmpty(value)) {
			return new Long(0l);
		}
		return new Long(value.intValue());
	}

	/**
	 * BigDecimal 转换为 Integer
	 *
	 * @author wangjianhua
	 * @param value
	 * @return
	 */
	public static Integer bigDecimal2Integer(BigDecimal value) {
		if (PubMethod.isEmpty(value)) {
			return Integer.valueOf(0);
		}
		return Integer.valueOf(value.intValue());
	}

	/**
	 * BigDecimal 转换为 Long
	 *
	 * @author wangjianhua
	 * @param value
	 * @return
	 */
	public static Long bigDecimal2Long(BigDecimal value) {
		if (PubMethod.isEmpty(value)) {
			return new Long(0l);
		}
		return new Long(value.longValue());
	}

	/**
	 * BigDecimal 转换为 String
	 *
	 * @author wangjianhua
	 * @param value
	 * @return
	 */
	public static String bigDecimal2String(BigDecimal value) {
		if (PubMethod.isEmpty(value)) {
			return "";
		}
		return value.toString();
	}

	/**
	 * BigDecimal 转换为 String
	 *
	 * @author wangjianhua
	 * @param value
	 * @return
	 */

	/**
	 * 将数组中的元素用指定的分隔符分隔
	 *
	 * @author wangjianhua
	 * @param array
	 * @param token
	 * @return
	 */
	public static String arrayToString(Object[] array, String token) {
		if (PubMethod.isEmpty(array) || array.length == 0) {
			return "";
		}
		String result = "";
		for (int i = 0; i < array.length; i++) {
			String item = array[i].toString();
			/*
			 * if(i != 0) { result += token; }
			 */
			result += item;
			result += token;
		}
		return result;
	}

	/**
	 * 将数组中的元素用指定的分隔符分隔,结尾不尾随token分隔符 创建时间: 2012-2-18 下午05:19:46
	 *
	 * @param array
	 * @param token
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String arrayToStringNoEndToken(Object[] array, String token) {
		if (PubMethod.isEmpty(array) || array.length == 0) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			String item = array[i].toString();
			if (result.length() <= 0) {
				result.append(item);
			} else {
				result.append(token).append(item);
			}

		}
		return result.toString();
	}
	/**
	 * 将字符串指定的分隔符分开后放入列表中
	 *
	 * @param data
	 * @param token
	 * @author wangjiahua
	 * @return
	 */
	public static List stringToList(String data, String token) {
		List res = new ArrayList();
		if (data == null || "".equals(data.trim()) || token == null) {
			return res;
		}
		token = token.trim();
		StringBuffer str = new StringBuffer();
		if (!"".equals(token)) {
			for (int i = data.indexOf(token); i != -1; i = data.indexOf(token)) {
				str.append(" " + data.substring(0, i + 1) + " ");
				data = data.substring(i + 1);
			}
		} else {
			token = " ";
			str.append(data);
		}

		StringTokenizer st = new StringTokenizer(str.toString(), token);
		while (st.hasMoreElements()) {
			String value = ((String) st.nextElement()).trim();
			if (!"".equals(value)) {
				res.add(value);
			}
		}
		st = null;
		if (res != null && res.size() == 0) {
			res.add(data);
		}
		return res;
	}

	// 把字符串转换为List容器对象。
	public static List convertString2List(String srcString, String spiliter) {
		if (isEmpty(srcString) || isEmpty(spiliter))
			return null;

		Object[] objsArray = srcString.split(spiliter);
		List list = new ArrayList();
		if (!isEmpty(objsArray)) {
			for (int index = 0; index < objsArray.length; index++) {
				list.add(objsArray[index]);
			}
		}

		return list;
	}

	/**
	 * 日期的加减
	 *
	 * @param t
	 * @param type
	 * @param amount
	 * @author wangjianhua
	 * @return
	 */
	public static Timestamp modifyDate(Timestamp t, String type, int amount) {
		Date date = new Date();
		date.setTime(t.getTime());
		Date newDate = modifyDate(date, type, amount);
		return new Timestamp(newDate.getTime());
	}

	/**
	 * 日期的加减
	 *
	 * @param date
	 * @param type
	 *            Y-年 M-月 D-天
	 * @param amount
	 *            加减的数量
	 * @author wangjianhua
	 * @return
	 */
	public static Date modifyDate(Date date, String type, int amount) {
		if (date == null) {
			date = new Date();
		}
		if (isEmpty(type)) {
			type = "";
		}
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		// PubMethod.toPrint(now.get(Calendar.YEAR) + " " +
		// (now.get(Calendar.MONTH)+1) + " " + now.get(Calendar.DAY_OF_YEAR) + "
		// " + now.get(Calendar.DAY_OF_WEEK));
		if ("Y".equalsIgnoreCase(type)) {
			now.add(Calendar.YEAR, amount);
		} else if ("M".equalsIgnoreCase(type)) {
			now.add(Calendar.MONTH, amount);
		} else if ("D".equalsIgnoreCase(type)) {
			now.add(Calendar.DAY_OF_YEAR, amount);
		}
		return now.getTime();
	}

	public static String digitalFormat(Object value, String formatter) {
		if (PubMethod.isEmpty(value)) {
			return "";
		}
		if (PubMethod.isEmpty(formatter)) {
			formatter = "###,###.00";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		String res = null;
		try {
			if (value instanceof Double) {
				res = format.format(((Double) value).doubleValue());
			}
			if (value instanceof Long) {
				res = format.format(((Long) value).longValue());
			}
		} catch (Exception e) {
			return "";
		}
		return res;
	}

	public static Double digitalFormatDouble(Double value, String formatter) {
		if (PubMethod.isEmpty(value)) {
			return new Double(0.0);
		}
		if (PubMethod.isEmpty(formatter)) {
			formatter = "###.00";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		Double res = null;
		try {
			res = new Double(format.format(value.doubleValue()));
		} catch (Exception e) {
			return new Double(0.0);
		}
		return res;
	}

	public static String digitalFormat(double value, String formatter) {
		if (PubMethod.isEmpty(formatter)) {
			formatter = "###,###.00";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		return format.format(value);
	}

	public static String digitalFormat(float value, String formatter) {
		if (PubMethod.isEmpty(formatter)) {
			formatter = "###,###.00";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		return format.format(value);
	}

	public static Double digitalFormatDoublebit(Double value, String bitnum) {
		String formatter = "";
		if (PubMethod.isEmpty(value)) {
			return new Double(0.0);
		}

		if (value < 0) {
			value = value - 0.0000001;
		}
		// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
		else if (value > 0) {
			value = value + 0.0000001;
		}
		// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入

		if (PubMethod.isEmpty(bitnum) || "2".equals(bitnum)) {
			formatter = "###.00";
		} else if ("3".equals(bitnum)) {
			formatter = "###.000";
		} else if ("4".equals(bitnum)) {
			formatter = "###.000";
		}
		if ("0".equals(bitnum)) {
			formatter = "###";
		}
		DecimalFormat format = new DecimalFormat(formatter);
		Double res = null;
		try {
			res = new Double(format.format(value.doubleValue()));
		} catch (Exception e) {
			return new Double(0.0);
		}
		return res;
	}

	/**
	 * 从jdbc取数据库连接
	 *
	 * @param driver
	 * @param url
	 * @param user
	 * @param pass
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnectionByJdbc(String driver, String url, String user, String pass) throws Exception {
		Class.forName(driver);
		return DriverManager.getConnection(url, user, pass);
	}

	/**
	 * 关闭Statement
	 *
	 * @param stm
	 */
	public static void closeStatement(Statement stm) throws Exception {
		try {
			if (stm != null) {
				stm.close();
				stm = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 关闭ResultSet
	 *
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs) throws Exception {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 关闭数据库连接
	 *
	 * @param conn
	 */
	public static void closeConnection(Connection conn) throws Exception {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 判断业务状态是否出运中信保已受理的状态
	 *
	 * @param bnsstate
	 * @return
	 */
	public static boolean isSolAccepted(String bnsstate) {
		if (bnsstate == null || bnsstate.equals(""))
			return false;
		if (bnsstate.equals("2") // 撤单
				|| bnsstate.equals("11") // 制单中
				|| bnsstate.equals("12") // 退回制单人
				|| bnsstate.equals("13") // 复核中
				|| bnsstate.equals("14") // 退回复核人
				|| bnsstate.equals("15") // 中信保退回
		) {
			return false;
		}
		return true;
	}

	public static int getDayLasted(Timestamp tm) {
		long timediff = (System.currentTimeMillis() - tm.getTime()) / 1000 / 3600; // 得到相差的小时数
		return (((int) timediff) / 24) + 1;
	}

	public static int[] getRowCol(String str) throws Exception {

		int[] rowcol = new int[2];
		try {
			String strRow = "";
			String strCol = "";
			int tmp = 0;
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')
					strRow += String.valueOf(str.charAt(i));
				else if (str.charAt(i) >= '0' && str.charAt(i) <= '9')
					strCol += String.valueOf(str.charAt(i));
			}

			strRow = PubMethod.charToNumber(strRow);
			strCol = String.valueOf(Integer.parseInt(strCol) - 1);

			if (Integer.parseInt(strRow) < 0)
				throw new Exception();
			if (Integer.parseInt(strCol) < 0)
				throw new Exception();

			rowcol[0] = Integer.parseInt(strRow);
			rowcol[1] = Integer.parseInt(strCol);

		} catch (Exception e) {
			throw e;
		}

		return rowcol;
	}

	public static String charToNumber(String str) {
		String retVuale = "";
		long vuale = 0;
		for (int i = str.length(); i > 0; i--) {
			int bytes = str.length() - i;
			char ch = str.charAt(i - 1);
			int iTmp = ch - 'A' + 1;

			vuale += iTmp * getNumber(26, bytes);
		}
		vuale = vuale - 1;

		if (vuale >= 0)
			retVuale = String.valueOf(vuale);
		return retVuale;
	}

	public static String numberToChar(String retVuale, String number, int level) {

		long vuale = new Long(number).longValue();
		long multiple;
		int residual;

		char temp;
		if (level > 0) {
			multiple = vuale / 27;
			residual = (int) vuale % 27;
			temp = (char) (residual - 1 + 'A');
		} else {
			multiple = vuale / 26;
			residual = (int) vuale % 26;
			temp = (char) (residual + 'A');
		}

		if (multiple == 0) {
			retVuale += temp;
		} else {
			retVuale = numberToChar(retVuale, String.valueOf(multiple), level + 1) + temp;
		}

		return retVuale;

	}

	public static long getNumber(int number1, int number2) {
		long l = 1;
		if (number2 < 0)
			return 0;
		else if (number2 == 0)
			return 1;
		for (int i = 1; i <= number2; i++) {
			l = l * number1;
		}
		return l;
	}

	public static String moneyFormat(String s) {
		try {
			String s1 = "";
			String s2 = "";
			String s3 = "";
			if (s.indexOf(",") != -1)
				return s;
			if (s.equals(""))
				return "";
			if (s.equals("0"))
				return "0.00";
			if (s.substring(0, 1).equals("-")) {
				s3 = "-";
				s = s.substring(1, s.length());
			}
			if (s.substring(0, 1).equals("."))
				s = "0" + s;
			int i = s.indexOf("E");
			if (i != -1) {
				int j = Integer.valueOf(s.substring(i + 1, s.length())).intValue();
				String s4 = s.substring(0, i);
				if (j > 0) {
					if (s4.length() - 2 <= j) {
						int l = (j - (s4.length() - 2)) + 1;
						for (int i1 = 0; i1 < l; i1++)
							s4 = s4 + "0";

					}
					String s7 = s4.substring(0, j + 2) + "." + s4.substring(j + 2, s4.length());
					s4 = s7.substring(0, 1) + s7.substring(2, s7.length());
				} else {
					j = Math.abs(j);
					String s8 = s4.substring(0, 1) + s4.substring(2, s4.length());
					s4 = "0.";
					for (int j1 = 0; j1 < j - 1; j1++)
						s4 = s4 + "0";

					s4 = s4 + s8;
				}
				s = s4;
			}
			int k = s.indexOf(".");
			if (k != -1) {
				s1 = s.substring(0, k);
				s2 = s.substring(k + 1, s.length());
			} else {
				s1 = s;
				s2 = "00";
			}
			if (s2.length() > 2) {
				String s5 = s2.substring(0, 2);
				String s9 = s2.substring(2, 3);
				int k1 = Integer.valueOf(s9).intValue();
				boolean flag = false;
				if (k1 >= 5) {
					int l1 = Integer.valueOf(s5).intValue();
					if (l1 <= 8) {
						l1++;
						s2 = "0" + String.valueOf(l1);
					} else if (++l1 >= 100) {
						String s11 = String.valueOf(l1);
						s2 = s11.substring(1, 3);
						long l2 = Long.valueOf(s1).longValue();
						l2++;
						s1 = String.valueOf(l2);
					} else {
						s2 = String.valueOf(l1);
					}
				} else {
					s2 = s5;
				}
			}
			if (s1.length() > 3) {
				String s6 = s1;
				String s10 = "";
				for (; s6.length() > 3; s6 = s6.substring(0, s6.length() - 3))
					s10 = "," + s6.substring(s6.length() - 3, s6.length()) + s10;

				s10 = s6 + s10;
				s1 = s10;
			}
			if (s2.length() == 1)
				s2 = s2 + "0";
			return s3 + s1 + "." + s2;
		} catch (Exception e) {
			e.printStackTrace();
			return s;
		}
	}

	// 采用四舍五入的方法格式化数据
	// 入口：dblInput （double） :需要格式化的数据
	// strFormat（String） :格式如：##，###.00
	// 出口：String 经过格式化得来的数值
	public static String toFormatNum(double dblInput, String strFormat) {
		try {

			if (dblInput < 0)
				dblInput = dblInput - 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else if (dblInput > 0)
				dblInput = dblInput + 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else
				dblInput = 0;
			if (strFormat == null || strFormat.equals(""))
				strFormat = "###,###,###.00";
			DecimalFormat fmt = new DecimalFormat(strFormat); // "#,###,##0.00"
			strFormat = fmt.format(dblInput);
			// PubMethod.toPrint("old======"+dblInput+" new======"+strFormat) ;
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toFormatNum1 :" + e.toString());
		}
		return strFormat;
	}

	public static String toFormatNum2(double dblInput, String strFormat) {
		try {

			if (dblInput < 0)
				dblInput = dblInput - 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else if (dblInput > 0)
				dblInput = dblInput + 0.0000001;
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else
				dblInput = 0;
			if (strFormat == null || strFormat.equals(""))
				strFormat = "0.00";
			DecimalFormat fmt = new DecimalFormat(strFormat); // "#,###,##0.00"
			strFormat = fmt.format(dblInput);
			// PubMethod.toPrint("old======"+dblInput+" new======"+strFormat) ;
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toFormatNum1 :" + e.toString());
		}
		return strFormat;
	}

	// 采用四舍五入的方法格式化数据
	// 入口：strInput （String） :需要格式化的数据
	// strFormat（String） :格式如：##，###.00
	// 出口：String 经过格式化得来的数值
	public static String toFormatNum(String strInput, String strFormat) {
		try {
			double dblInput = 0;
			// 数据初始化
			if (strInput == null) {
				return "";
			}
			strInput = strInput.trim();
			if (strInput.equals("") || strInput.equalsIgnoreCase("null")) {
				return "";
			}

			// 赋值
			dblInput = Double.parseDouble(strInput);

			if (dblInput < 0) {
				dblInput = dblInput - 0.0000001;
				// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			}
			else if (dblInput > 0) {
				dblInput = dblInput + 0.0000001;
				// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			}
			else {
				dblInput = 0;
			}
			if (strFormat == null || strFormat.equals("")) {
				strFormat = "###,###,###.00";
			}
			DecimalFormat fmt = new DecimalFormat(strFormat); // "#,###,##0.00"
			strFormat = fmt.format(dblInput);
			PubMethod.toPrintln("old======" + strInput + "      new======" + strFormat);
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toFormatNum2 :" + e.toString());
		}
		return strFormat;

	}

	public static String toFormatNum2(String strInput, String strFormat) {
		try {
			double dblInput = 0;
			// 数据初始化
			if (strInput == null) {
				return "";
			}
			strInput = strInput.trim();
			if (strInput.equals("") || strInput.equalsIgnoreCase("null")) {
				return "";
			}

			// 赋值
			dblInput = Double.parseDouble(strInput);

			if (dblInput < 0) {
				dblInput = dblInput - 0.0000001;
			}
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else if (dblInput > 0) {
				dblInput = dblInput + 0.0000001;
			}
			// DecimalFormat有BUDGE，当末位为5时无法正确进行四舍五入
			else {
				dblInput = 0;
			}
			if (strFormat == null || strFormat.equals("")) {
				strFormat = "###.00";
			}
			DecimalFormat fmt = new DecimalFormat(strFormat); // "#,###,##0.00"
			strFormat = fmt.format(dblInput);
			PubMethod.toPrintln("old======" + strInput + "      new======" + strFormat);
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toFormatNum2 :" + e.toString());
		}
		return strFormat;

	}

	// 将用字符串表示的数值转化为字符串

	public static String toNumberic(String strInput) {
		String strReturn = strInput;
		try {
			// 做简单的字符判断处理
			if (strInput == null)
				return "";
			strInput = strInput.trim();
			if (strInput.equals("") || strInput.equalsIgnoreCase("null"))
				return "";

			int intDelete = 0; // 去掉“,”“$”,"￥","＄","￡","％","%", " "
			String strDelete = ","; // 1
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete) + strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "$"; // 2
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete) + strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "￥"; // 3
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete) + strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "＄"; // 4
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete) + strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "￡"; // 5
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete) + strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "％"; // 6
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete) + strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = "%"; // 7
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete) + strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
			strDelete = " "; // 8
			intDelete = strReturn.indexOf(strDelete);
			while (intDelete != -1) {
				strReturn = strReturn.substring(0, intDelete) + strReturn.substring(intDelete + 1);
				intDelete = strReturn.indexOf(strDelete);
			}
		} catch (Exception e) {
			PubMethod.toPrintln("Error in ChangeNumberic.toNumberic :" + e.toString());
		}
		return strReturn;
	}

	/**
	 * 将List中的元素加入另一个List
	 *
	 * @param subList
	 * @param list
	 */
	public static void addListElementToList(List subList, List list) {
		if (PubMethod.isEmpty(subList)) {
			return;
		}
		for (Iterator itSubList = subList.iterator(); itSubList.hasNext();) {
			list.add(itSubList.next());
		}
	}

	/**
	 * 返回数组的第一个元素，且造型为String 类型
	 *
	 * @param obj
	 * @return
	 */
	public static String getFirstEleFromArr(Object obj) {
		String[] strArr = (String[]) obj;
		if (strArr == null || strArr.length == 0) {
			return "";
		}
		return (String) strArr[0];
	}

	/**
	 * 字符串长度不够指定长度，则在左边补0
	 *
	 * @param val
	 * @param numbers
	 * @return
	 */
	public static String getFormatString(String val, int numbers) {
		if (PubMethod.isEmpty(val)) {
			return "";
		}
		val = val.trim();
		while (val.length() < numbers) {
			val = "0" + val;
		}
		return val;
	}

	/**
	 * 判断是否为action字符串
	 *
	 * @param urlAction
	 * @return
	 */
	public static boolean isUrlString(String urlAction) {
		if (PubMethod.isEmpty(urlAction)) {
			return false;
		}
		if (urlAction.endsWith(".jsp") || urlAction.endsWith(".jsf") || urlAction.endsWith(".html") || urlAction.endsWith(".htm")) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 判断是否为数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		boolean returnValue = false;
		String patternValue = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
		Pattern pattern = Pattern.compile(patternValue);
		if (pattern.matcher(str).matches()) {// 浮点数
			returnValue = true;
		}

		pattern = Pattern.compile("^[1-9]\\d*$");
		if (pattern.matcher(str).matches()) {// 正整数
			returnValue = true;
		}
		return returnValue;
	}

	/**
	 * 单个判断是否为数字
	 *
	 * @param str
	 * @return
	 */

	public static boolean isNum(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 构造字符串
	 *
	 * @param str
	 * @param newId
	 * @return
	 */
	public static String bulidString(String str, Long newId) {
		if (null != str && !"".equals(str) && !PubMethod.isEmpty(newId)) {
			str = str + "," + newId.toString();
		} else if (!PubMethod.isEmpty(newId)) {
			str = newId.toString();
		}
		return str;
	}

	public static String bulidStringTypeStr(String str, String newStr) {
		if (!PubMethod.isEmpty(str)) {
			if (!PubMethod.isEmpty(newStr)) {
				str = str + "," + newStr;
			}

		} else {
			str = newStr;
		}
		return str;
	}

	/**
	 * 返回字符串
	 *
	 * @param str
	 * @return 格试 '1','2','3'
	 */
	public static String returnSenderOrderIds(String str) {
		String newSendIdStr = "";
		if (!PubMethod.isEmpty(str)) {
			str = str.trim();
			str = str.replaceAll("\r\n", ",");
			str = str.replaceAll("'", "");
			str = str.replaceAll(" ", ",");
			str = str.replaceAll("、", ",");
			str = str.replaceAll("，", ",");

			str = str.replaceAll("。", ",");
			str = str.replaceAll(";", ",");
			str = str.replaceAll("；", ",");
			str = str.replaceAll(":", ",");
			str = str.replaceAll("：", ",");

			String[] strs = str.split(",");

			for (int i = 0; i < strs.length; i++) {
				String string = strs[i];
				if (!PubMethod.isEmpty(string)) {
					if (PubMethod.isEmpty(newSendIdStr)) {
						newSendIdStr = "'" + string + "'";
					} else {
						newSendIdStr = newSendIdStr + "," + "'" + string + "'";
					}
				}

			}

		}
		return newSendIdStr;
	}

	/**
	 * 返回字符串
	 *
	 * @param str
	 * @return 格试 1,2,3
	 */
	public static String returnSenderOrderIdsNotHaveInvertedComma(String str) {
		String newSendIdStr = "";
		if (!PubMethod.isEmpty(str)) {
			str = str.trim();
			str = str.replaceAll("\r\n", ",");
			str = str.replaceAll(" ", ",");
			str = str.replaceAll("'", "");
			String[] strs = str.split(",");
			for (int i = 0; i < strs.length; i++) {
				String string = strs[i];
				if (!PubMethod.isEmpty(string)) {
					if (PubMethod.isEmpty(newSendIdStr)) {
						newSendIdStr = string;
					} else {
						newSendIdStr = newSendIdStr + "," + string;
					}
				}

			}

		}
		return newSendIdStr;
	}

	public static String returnSenderOrderIdsNotHaveInvertedCommaTs(String str) {
		String newSendIdStr = "";
		if (!PubMethod.isEmpty(str)) {
			str = str.trim();
			str = str.replaceAll("\r\n", ",");
			str = str.replaceAll(" ", ",");
			str = str.replaceAll("、", ",");
			str = str.replaceAll("，", ",");
			str = str.replaceAll("'", "");
			str = str.replaceAll("。", ",");
			str = str.replaceAll(";", ",");
			str = str.replaceAll("；", ",");
			str = str.replaceAll(":", ",");
			str = str.replaceAll("：", ",");
			str = str.replaceAll("-", ",");
			String[] strs = str.split(",");

			for (int i = 0; i < strs.length; i++) {
				String string = strs[i];
				if (!PubMethod.isEmpty(string)) {
					if (PubMethod.isEmpty(newSendIdStr)) {
						newSendIdStr = string;
					} else {
						newSendIdStr = newSendIdStr + "," + string;
					}
				}

			}

		}
		return newSendIdStr;
	}

	/**
	 * 返回日期之差的秒数
	 *
	 * @param dates
	 * @param datee
	 * @return
	 */
	public static long differThird(String dates, String datee) {

		Date date1 = strToDateLong(dates);
		Date date2 = strToDateLong(datee);
		return (date2.getTime() - date1.getTime()) / 1000; // 用立即数，减少乘法计算的开销

	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 *
	 * @param strDate
	 * @return
	 */
	public static Date strToDateLong(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 数值合并
	 *
	 * @author baihui
	 * @param a
	 * @param b
	 * @return
	 */
	public static Object[] SeveralCombines(Object[] a, Object[] b) {

		Object[] s = new Object[a.length + b.length];

		int i;
		for (i = 0; i < a.length; i++) {
			s[i] = a[i];
		}

		for (int j = 0; j < b.length; j++) {
			s[i + j] = b[j];
		}

		return s;
	}

	public static String[] chineseDigits = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

	/**
	 * 把金额转换为汉字表示的数量，小数点后四舍五入保留两位
	 *
	 * @param amount
	 * @return
	 */
	public static String amountToChinese(double amount) {

		if (amount > 99999999999999.99 || amount < -99999999999999.99)
			throw new IllegalArgumentException("参数值超出允许范围 (-99999999999999.99 ～ 99999999999999.99)！");

		boolean negative = false;
		if (amount < 0) {
			negative = true;
			amount = amount * (-1);
		}

		long temp = Math.round(amount * 100);
		int numFen = (int) (temp % 10); // 分
		temp = temp / 10;
		int numJiao = (int) (temp % 10); // 角
		temp = temp / 10;
		// temp 目前是金额的整数部分

		int[] parts = new int[20]; // 其中的元素是把原来金额整数部分分割为值在 0~9999 之间的数的各个部分
		int numParts = 0; // 记录把原来金额整数部分分割为了几个部分（每部分都在 0~9999 之间）
		for (int i = 0;; i++) {
			if (temp == 0) {
				break;
			}
			int part = (int) (temp % 10000);
			parts[i] = part;
			numParts++;
			temp = temp / 10000;
		}

		boolean beforeWanIsZero = true; // 标志“万”下面一级是不是 0

		String chineseStr = "";
		for (int i = 0; i < numParts; i++) {

			String partChinese = partTranslate(parts[i]);
			if (i % 2 == 0) {
				if ("".equals(partChinese))
					beforeWanIsZero = true;
				else
					beforeWanIsZero = false;
			}

			if (i != 0) {
				if (i % 2 == 0)
					chineseStr = "亿" + chineseStr;
				else {
					if ("".equals(partChinese) && !beforeWanIsZero) { // 如果“万”对应的
						// part 为
						// 0，而“万”下面一级不为
						// 0，则不加“万”，而加“零”
						chineseStr = "零" + chineseStr;
					}
					else {
						if (parts[i - 1] < 1000 && parts[i - 1] > 0) {// 如果"万"的部分不为
							// 0,
							// 而"万"前面的部分小于
							// 1000
							// 大于 0，
							// 则万后面应该跟“零”
							chineseStr = "零" + chineseStr;
						}
						chineseStr = "万" + chineseStr;
					}
				}
			}
			chineseStr = partChinese + chineseStr;
		}

		if ("".equals(chineseStr)) {// 整数部分为 0, 则表达为"零元"
			chineseStr = chineseDigits[0];
		}
		else if (negative) {// 整数部分不为 0, 并且原金额为负数
			chineseStr = "负" + chineseStr;
		}

		chineseStr = chineseStr + "元";

		if (numFen == 0 && numJiao == 0) {
			chineseStr = chineseStr + "整";
		} else if (numFen == 0) { // 0 分，角数不为 0
			chineseStr = chineseStr + chineseDigits[numJiao] + "角";
		} else { // “分”数不为 0
			if (numJiao == 0) {
				chineseStr = chineseStr + "零" + chineseDigits[numFen] + "分";
			}
			else {
				chineseStr = chineseStr + chineseDigits[numJiao] + "角" + chineseDigits[numFen] + "分";
			}
		}

		return chineseStr;

	}

	/**
	 * 把一个 0~9999 之间的整数转换为汉字的字符串，如果是 0 则返回 ""
	 *
	 * @param amountPart
	 * @return
	 */
	private static String partTranslate(int amountPart) {

		if (amountPart < 0 || amountPart > 10000) {
			throw new IllegalArgumentException("参数必须是大于等于 0，小于 10000 的整数！");
		}

		String[] units = new String[]{"", "拾", "佰", "仟"};

		int temp = amountPart;

		String amountStr = new Integer(amountPart).toString();
		int amountStrLength = amountStr.length();
		boolean lastIsZero = true; // 在从低位往高位循环时，记录上一位数字是不是 0
		String chineseStr = "";

		for (int i = 0; i < amountStrLength; i++) {
			if (temp == 0) // 高位已无数据
				break;
			int digit = temp % 10;
			if (digit == 0) { // 取到的数字为 0
				if (!lastIsZero) // 前一个数字不是 0，则在当前汉字串前加“零”字;
					chineseStr = "零" + chineseStr;
				lastIsZero = true;
			} else { // 取到的数字不是 0
				chineseStr = chineseDigits[digit] + units[i] + chineseStr;
				lastIsZero = false;
			}
			temp = temp / 10;
		}
		return chineseStr;
	}

	public static String getStringFromArray(Long[] array) {
		String str = "";
		if (array == null || array.length <= 0)
			return "";

		for (int i = 0; i < array.length; i++) {
			str += array[i] + ",";
		}
		if (str.length() > 0)
			str = str.substring(0, str.length() - 1);

		return str;

	}

	public static String getNotFormatToday() {
		return getToday().replaceAll("-", "");
	}

	public static String getToday() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		return formater.format(new Date());
	}

	public static String getTimeStamp() {
		StringBuilder str = new StringBuilder();
		Date ca = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		str.append(sdf.format(ca));
		return str.toString();
	}
	/**
	 * 判断是否为数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumericOrPrice(String str) {
		boolean returnValue = false;
		String patternValue = "^(0|[1-9]\\d*)(\\.\\d+)?$";
		Pattern pattern = Pattern.compile(patternValue);
		if (pattern.matcher(str).matches()) {// 浮点数
			returnValue = true;
		}

		return returnValue;
	}


	private static boolean isReturn(String statusDesc, Map result) {

		if (statusDesc.indexOf("到达") < 0 && statusDesc.indexOf("离开") < 0 && statusDesc.indexOf("安排投递") < 0 && statusDesc.indexOf("投递并签收") < 0)
			return false;

		Set<String> sets = result.keySet();
		// 按时间排序
		List<String> times = new ArrayList<String>(sets);
		Collections.sort(times);
		if (statusDesc.indexOf("投递并签收") >= 0) {
			String firstDesc = (String) result.get(times.get(0));
			String firstAdd = firstDesc.substring(0, firstDesc.lastIndexOf("收寄"));
			String lastAdd = statusDesc.substring(0, statusDesc.indexOf("投递并签收"));
			if (firstAdd.equals(lastAdd))
				return true;
			else
				return false;
		}

		if (statusDesc.indexOf("安排投递") >= 0) {
			String firstDesc = (String) result.get(times.get(0));
			String firstAdd = firstDesc.substring(0, firstDesc.lastIndexOf("收寄"));
			String lastAdd = statusDesc.substring(0, statusDesc.indexOf("安排投递"));
			if (firstAdd.equals(lastAdd))
				return true;
			else
				return false;
		}
		boolean r = false;
		for (String key : times) {
			String temDesc = (String) result.get(key);
			if (temDesc.equals(statusDesc))
				return r;
			if (temDesc.indexOf("未妥投") >= 0) {
				r = true;
				break;
			}
		}
		return r;
	}

	/**
	 * 换算两个日期之间的秒数数
	 *
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getHourSecond(String time1, String time2) {
		long quot = 0;
		// long day = 0;
		long hour = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date1 = formatter.parse(time1);
			Date date2 = formatter.parse(time2);
			quot = date1.getTime() - date2.getTime();
			hour = quot / 1000;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hour;
	}

	/**
	 * 判断两个对象的类型是否一致 创 建 人: 文超 创建时间: 2011-9-11 下午03:02:43
	 *
	 * @param obj1
	 * @param obj2
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isSameClassType(Object obj1, Object obj2) {
		if (PubMethod.isEmpty(obj1) || PubMethod.isEmpty(obj2)) {
			return false;
		}
		boolean flag = obj1.getClass().getName().equals(obj2.getClass().getName());
		return flag;
	}

	/**
	 * 过滤属性字段 创 建 人: 文超 创建时间: 2011-9-11 下午03:04:13
	 *
	 * @param fieldsArray
	 *            JavaBean 所以的属性项集合
	 * @param includeFieds
	 *            需要对比的属性值项
	 * @param excludeFields
	 *            将这些属性排除在外，不进行对拼
	 * @param isMatch
	 *            是否需要完全匹配，true 完全匹配，false 非完全匹配。非完全匹配值如果某一个字段在属性项集合中不存在则正常进行
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static List<String> filterFieldsName(Field[] fieldsArray, String includeFieds, String excludeFields, boolean isMatch) {
		List<String> result = new ArrayList<String>();

		if (PubMethod.isEmpty(fieldsArray)) {
			return null;
		}

		int fieldsLength = fieldsArray.length;
		List<String> fieldsName = new ArrayList<String>();
		for (int i = 0; i < fieldsLength; i++) {
			fieldsName.add(fieldsArray[i].getName());
		}

		/** 如果此两个参数值都为空，则全部进行对比 */
		if (PubMethod.isEmpty(includeFieds) && PubMethod.isEmpty(excludeFields)) {
			result.addAll(fieldsName);
			return result;
		}
		/** 只匹配知道的属性 */
		if (!PubMethod.isEmpty(includeFieds)) {
			String[] array = includeFieds.split(",");
			for (int i = 0; i < array.length; i++) {
				String inName = array[i];
				if (!fieldsName.contains(inName)) {
					if (isMatch) {// 如果是完全匹配，则直接返回
						return null;
					}
				} else {
					result.add(inName);
				}
			}
			return result;
		}
		/** 排除的属性字段 */
		if (!PubMethod.isEmpty(excludeFields)) {
			String[] array = excludeFields.split(",");
			result.addAll(fieldsName);
			for (int i = 0; i < array.length; i++) {
				String exName = array[i];
				if (!fieldsName.contains(exName)) {
					if (isMatch) {
						return null;
					}
				} else {
					result.remove(exName);
				}
			}
			return result;
		}

		return null;
	}

	/**
	 * 比较两个实例对象属性值是否相同 创 建 人: 文超 创建时间: 2011-9-11 下午03:07:09
	 *
	 * @param obj1
	 *            对象1
	 * @param obj2
	 *            对象2
	 * @param includeFieds
	 *            指定对比的属性
	 * @param excludeFields
	 *            将这些属性排除在外，不进行对拼
	 * @param isMatch
	 *            是否需要完全匹配，true 完全匹配，false 非完全匹配
	 * @return 对象对比的属性值相同 返回 true，不相同 返回false
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean compareObjFieldVals(Object obj1, Object obj2, String includeFieds, String excludeFields, boolean isMatch) {
		boolean result = true;
		boolean isSame = PubMethod.isSameClassType(obj1, obj2);
		if (!isSame) {// 如果两对象不是同一类型，则直接返回false
			return false;
		}

		Field[] obj1Fieldsarray = obj1.getClass().getDeclaredFields();
		List<String> fieldsName = PubMethod.filterFieldsName(obj1Fieldsarray, includeFieds, excludeFields, isMatch);
		if (!PubMethod.isEmpty(fieldsName)) {
			BeanWrapper beanWrapper1 = new org.springframework.beans.BeanWrapperImpl(obj1);
			BeanWrapper beanWrapper2 = new org.springframework.beans.BeanWrapperImpl(obj2);
			for (String fieldname : fieldsName) {// 对象属性值 进行比较
				Object objval1 = new Object();
				Object objval2 = new Object();
				if (!fieldname.equals("serialVersionUID") && !fieldname.toUpperCase().equals("CGLIB$BOUND")) {
					try {
						objval1 = beanWrapper1.getPropertyValue(fieldname);
						objval2 = beanWrapper2.getPropertyValue(fieldname);
						if (PubMethod.isEmpty(objval1)) {
							if (!PubMethod.isEmpty(objval2)) {
								return false;
							}
						} else {
							if (!objval1.equals(objval2)) {
								return false;
							}
						}
					} catch (BeansException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			return false;
		}
		return result;
	}
	/**
	 * 换算两个日期之间的天-时-分
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static String getHourMinute(String time1, String time2) {
		long quot = 0;
		long day = 0;
		long hour = 0;
		long minute = 0;
		String dayHour = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date1 = formatter.parse(time1);
			Date date2 = formatter.parse(time2);

			quot = date1.getTime() - date2.getTime();
			minute = (quot / 1000 - quot / 1000 / 3600 * 3600) / 60;
			quot = quot / 1000 / 60 / 60;

			day = quot / 24;
			hour = quot % 24;

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (day == 0 && hour == 0) {
			dayHour = minute + "分";
		} else if (day == 0 && hour != 0) {
			dayHour = hour + "小时" + minute + "分";
		} else {
			if (day > 1) {
				dayHour = day + "天" + hour + "小时" + minute + "分" + "-true";
			} else {
				dayHour = day + "天" + hour + "小时" + minute + "分" + "-false";
			}

		}
		return dayHour;
	}

	/**
	 * <功能详细描述> 创建时间: 2012-2-23 上午11:17:45
	 * 
	 * @param time1
	 * @param time2
	 * @param subDay
	 *            相差的天数，如果天数超过此值，后面跟true，否则跟false
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String getHourMinute(String time1, String time2, int subDay) {
		long quot = 0, day = 0, hour = 0, minute = 0;
		String dayHour = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date1 = formatter.parse(time1);
			Date date2 = formatter.parse(time2);
			quot = date1.getTime() - date2.getTime();
			minute = (quot / 1000 - quot / 1000 / 3600 * 3600) / 60;
			quot = quot / 1000 / 60 / 60;
			day = quot / 24;
			hour = quot % 24;

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (day == 0 && hour == 0) {
			dayHour = minute + "分";
		} else if (day == 0 && hour != 0) {
			dayHour = hour + "小时" + minute + "分";
		} else {
			if (day > subDay) {
				dayHour = day + "天" + hour + "小时" + minute + "分" + "-true";
			} else {
				dayHour = day + "天" + hour + "小时" + minute + "分" + "-false";
			}

		}
		return dayHour;
	}

	/**
	 * 描述:返回32大写MD5值
	 * 
	 * @param s
	 * @param encode
	 * @return
	 * @author jinzhou.shao
	 * @date 2015-1-16 下午5:34:32
	 */
	public final static String upper32MD5(String s, String encode) {
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

		try {
			byte[] md = md5(s, encode);
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 描述:
	 * 
	 * @param encode
	 * @return
	 * @author jinzhou.shao
	 * @throws NoSuchAlgorithmException
	 * @date 2015-1-16 下午5:37:37
	 */
	public static byte[] md5(String s, String encode) throws NoSuchAlgorithmException {
		Charset def = Charset.defaultCharset();
		if (encode != null) {
			def = Charset.forName(encode);
		}

		byte[] btInput = s.getBytes(def);
		// 获得MD5摘要算法的 MessageDigest 对象
		MessageDigest mdInst = MessageDigest.getInstance("MD5");
		// 使用指定的字节更新摘要
		mdInst.update(btInput);
		// 获得密文
		byte[] md = mdInst.digest();

		return md;
	}

	/**
	 * 描述:返回小写32MD5值
	 * 
	 * @param s
	 * @param encode
	 * @return
	 * @author jinzhou.shao
	 * @date 2015-1-16 下午5:42:55
	 */
	public final static String lower32MD5(String s, String encode) {
		String value = null;
		try {
			return value = new String(Hex.encodeHex(md5(s, encode)));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * java特殊字符转换 创 建 人: 文超 创建时间: 2011-10-29 下午05:24:32
	 * 
	 * @param input
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String filterString(String input) {
		Map<String, String> reg = new HashMap<String, String>();
		reg.put("&lt;", "<");
		reg.put("&gt;", ">");
		reg.put("&quot;", "\"");
		reg.put("&amp;", "&");
		reg.put("&apos;", "'");
		Iterator it = reg.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = reg.get(key);
			input = input.replaceAll(key, value);
		}
		return input;
	}

	/**
	 * 获取时间list对象
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<Map<String, String>> getListTime(String startTime, String endTime) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		// Date endD = PubMethod.strToDateLong(endTime);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(PubMethod.strToDateLong(startTime));
		Calendar c2 = Calendar.getInstance();
		c2.setTime(PubMethod.strToDateLong(endTime));
		// Map<String,String > map = new HashMap<String, String>();
		// 当天
		if (isOneDay(c1, c2)) {
			Map<String, String> map1 = new HashMap<String, String>();
			map1.put("start", startTime);
			map1.put("end", endTime);
			list.add(map1);
			return list;
		}
		// 大于1天
		while (!isOneDay(c1, c2)) {
			Map<String, String> map = new HashMap<String, String>();
			Date date = c1.getTime();
			map.put("start", PubMethod.formatDateTime(date, "yyyy-MM-dd HH:mm:ss"));
			map.put("end", PubMethod.formatDateTime(date, "yyyy-MM-dd") + " 23:59:59");
			list.add(map);
			String newdate = PubMethod.formatDateTime(date, "yyyy-MM-dd") + " 00:00:00";
			c1.setTime(PubMethod.strToDateLong(newdate));
			c1.add(c1.DAY_OF_MONTH, 1);
		}
		// 最后一天
		Map<String, String> map = new HashMap<String, String>();
		map.put("start", PubMethod.formatDateTime(c1.getTime(), "yyyy-MM-dd HH:mm:ss"));
		map.put("end", endTime);
		list.add(map);
		return list;
	}
	public static boolean isOneDay(Calendar c1, Calendar c2) {
		if ((c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
				&& (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)))
			return true;
		else
			return false;
	}

	/****
	 * 字符串转成数组 dulin
	 * */
	public static String[] StrList(String ValStr) {
		int i = 0;
		String TempStr = ValStr;
		String[] returnStr = new String[ValStr.length() + 1 - TempStr.replace(",", "").length()];
		ValStr = ValStr + ",";

		while (ValStr.indexOf(',') > 0) {
			returnStr[i] = ValStr.substring(0, ValStr.indexOf(","));
			ValStr = ValStr.substring(ValStr.indexOf(",") + 1, ValStr.length());
			i++;
		}
		return returnStr;
	}

	/**
	 * 网页中文数据转码 ,如&#26080;&#26597;&#35810;&#35760;&#24405; 转换为汉字
	 * 
	 * @param dataStr
	 * @return
	 */
	public static String decodeUnicode(final String dataStr) {
		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			int system = 10;// 进制
			if (start == 0) {
				int t = dataStr.indexOf("&#");
				if (start != t)
					start = t;
			}
			end = dataStr.indexOf(";", start + 2);
			String charStr = "";
			if (end != -1) {
				charStr = dataStr.substring(start + 2, end);

				// 判断进制
				char s = charStr.charAt(0);
				if (s == 'x' || s == 'X') {
					system = 16;
					charStr = charStr.substring(1);
				}
			}
			// 转换
			try {

				if (!PubMethod.isEmpty(charStr)) {
					char letter = (char) Integer.parseInt(charStr, system);
					buffer.append(new Character(letter).toString());
				}

			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			// 处理当前unicode字符到下一个unicode字符之间的非unicode字符
			start = dataStr.indexOf("&#", end);
			if (start - end > 1) {
				buffer.append(dataStr.substring(end + 1, start));
			}

			// 处理最后面的非unicode字符
			if (start == -1) {
				int length = dataStr.length();
				if (end + 1 != length) {
					buffer.append(dataStr.substring(end + 1, length));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * 字符串拼接 创 建 人: 文超 创建时间: 2012-4-10 上午10:19:29
	 * 
	 * @param array
	 * @param type
	 *            0 返回数字串，如1,2,3。非0：返回字符串，如 '1','2','3'
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String joinToSql(String[] array, int type) {
		StringBuffer result = new StringBuffer();
		if (PubMethod.isEmpty(array) || array.length <= 0) {
			return null;
		}
		for (int i = 0; i < array.length; i++) {
			String value = type == 0 ? array[i] : "'" + array[i] + "'";
			PubMethod.concat(result, value);
		}
		return result.toString();
	}

	/**
	 * 数组转化为Map 创 建 人: 文超 创建时间: 2012-4-10 下午04:15:44
	 * 
	 * @param array
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static Map arrayToMap(String[] array) {
		Map map = new HashMap();
		if (array.length <= 0)
			return map;
		for (int i = 0; i < array.length; i++) {
			map.put(array[i], array[i]);
		}
		return map;
	}

	/**
	 * 格式化当前时间 创 建 人: 文超 创建时间: 2013-10-13 上午10:00:45
	 * 
	 * @param format
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String getCurSysDate(String format) {
		Calendar date = Calendar.getInstance();
		Date sysDate = date.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String curDate = formatter.format(sysDate);
		return curDate;
	}
	/**
	 * 字符转换函数 liendan
	 * 
	 * @param s
	 * @return output:如果字符串为null,返回为空,否则返回该字符串
	 */
	public static String nullObject2String(Object s) {
		String str = "";
		try {
			str = s.toString();
		} catch (Exception e) {
			str = "";
		}
		return str;
	}
	/**
	 * 对象转为double liendan
	 * 
	 * @param s
	 * @return output:如果字符串为null,返回为空,否则返回0.0s
	 */
	public static double nullObject2Double(Object s) {
		double i = 0.0;

		String str = "";
		try {
			str = s.toString();
			i = Double.parseDouble(str);
		} catch (Exception e) {
			i = 0.0;
		}

		return i;
	}
	/**
	 * 对象转为bigDecimal liendan
	 * 
	 * @param s
	 * @return output:如果字符串为null,返回为空,否则返回0.0s
	 */
	public static BigDecimal nullObject2Decimal(Object s) {
		BigDecimal i = null;

		String str = "";
		try {
			str = s.toString();
			i = new BigDecimal(str);;
		} catch (Exception e) {
			i = null;
		}

		return i;
	}
	/**
	 * 将一个对象转换为String,如果 liendan
	 * 
	 * @param s
	 * @return
	 */
	public static Integer nullObject2Integer(Object s) {
		return Integer.valueOf(PubMethod.nullObject2int(s));
	}
	/**
	 * 将一个对象转换为整形 liendan
	 * 
	 * @param s
	 * @return
	 */
	public static int nullObject2int(Object s) {
		String str = "";
		int i = 0;
		try {
			str = s.toString();
			i = Integer.parseInt(str);
		} catch (Exception e) {
			i = 0;
		}
		return i;
	}
	/**
	 * 将一个对象转换为字节 liendan
	 * 
	 * @param s
	 * @return
	 */
	public static byte nullObject2byte(Object s) {
		String str = "";
		byte i = 0;
		try {
			str = s.toString();
			i = Byte.parseByte(str);
		} catch (Exception e) {
			i = 0;
		}
		return i;
	}

	/**
	 * 将一个字符串转换为时间戳Timestamp liendan
	 * 
	 * @param s
	 * @return
	 */
	public static Timestamp nullString2Timestamp(String s) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setLenient(false);
		Timestamp ts = null;
		try {
			ts = new Timestamp(format.parse(s).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ts;
	}

	/**
	 * 字符转换函数如果字符串为null,返回为空,否则返回该字符串 liendan
	 * 
	 * @param s
	 * @return
	 */
	public static String null2String(String s) {
		return s == null ? "" : s;
	}
	/**
	 * 字符转换函数,如果字符串1为null,返回为字符串2,否则返回该字符串
	 * 
	 * @param s
	 * @param s1
	 * @return
	 */
	public static String null2String(String s, String s1) {
		return s == null ? s1 : s;
	}
	/**
	 * 字符转换函数:如果字符串1为null或者不能转换成整型,返回为0
	 * 
	 * @param s
	 * @return
	 */
	public static int null2int(String s) {
		int i = 0;
		try {
			i = Integer.parseInt(s);
		} catch (Exception e) {
			i = 0;
		}
		return i;
	}
	/**
	 * 字符转换函数:如果字符串1为null或者不能转换成整型,返回为0
	 * 
	 * @param s
	 * @return
	 */
	public static long null2Long(String s) {
		long i = 0;
		try {
			i = Long.parseLong(s);
		} catch (Exception e) {
			i = 0;
		}
		return i;
	}

	/**
	 * 对象转换为long型
	 * 
	 * @param s
	 * @return
	 */
	public static long nullObject2Long(Object s) {
		long i = 0;

		String str = "";
		try {
			str = s.toString();
			i = Long.parseLong(str);
		} catch (Exception e) {
			i = 0;
		}

		return i;
	}
	/**
	 * 对象转换为Short型
	 * 
	 * @param s
	 * @return
	 */
	public static short nullObject2Short(Object s) {
		short i = 0;

		String str = "";
		try {
			str = s.toString();
			i = Short.parseShort(str);
		} catch (Exception e) {
			i = 0;
		}

		return i;
	}
	/**
	 * <summary> 转全角的函数(SBC case) </summary> <param name="input">任意字符串</param>
	 * <returns>全角字符串</returns> <remarks> 全角空格为12288，半角空格为32
	 * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248 </remarks>
	 **/
	public static String ToSBC(String input) {
		// 半角转全角：
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127) {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	/**
	 * <summary> 转半角的函数(DBC case) </summary> <param name="input">任意字符串</param>
	 * <returns>半角字符串</returns> <remarks> 全角空格为12288，半角空格为32
	 * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248 </remarks>
	 **/
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375) {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}
	/**
	 * 得到空格之后的字符
	 *
	 * @param str
	 * @return Date
	 * @throws ParseException
	 */
	public static String splitSpace(String str) throws ParseException {
		if (str.contains(" ")) {
			return str.split(" ")[1];
		} else {
			return str;
		}
	}
	/**
	 * 把String类型转换为Integer
	 * 
	 * @param
	 *            str
	 * @return Integer
	 */
	public static Integer parseInteger(String str) {
		if (str == null || str.equals("")) {
			return 0;
		} else {
			return Integer.parseInt(str);
		}
	}

	/**
	 * 把String类型转换为Date
	 * 
	 * @param
	 *            str
	 * @return Date
	 * @throws ParseException
	 */
	public static Date parseDate(String str) throws ParseException {
		if (str == null || str.equals("")) {
			return null;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			Date date = sdf.parse(str);
			return date;
		}
	}

	/**
	 * 转换对象（用户定义的对象）。设置对象的Id。
	 * 
	 * @param
	 *            clazz
	 * @param
	 *            str
	 * @return Object
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws ParseException
	 */
	public static Object parseObject(Class clazz, String str) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException {
		Object obj;
		if (str == null || str.equals("")) {
			obj = null;
		} else {
			obj = clazz.newInstance();
			Method m = clazz.getMethod("setId", str.getClass());
			m.invoke(obj, str);
		}
		return obj;
	}
	/**
	 * 判断是否是简单数据类型
	 * 
	 * @param
	 *            type
	 */
	public static boolean isSimpleType(String type) {
		for (int i = 0; i < TYPE_SIMPLE.length; i++) {
			if (type.equals(TYPE_SIMPLE[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据类型进行转换
	 * 
	 * @param
	 *            clazz
	 * @param
	 *            str
	 * @return Object
	 * @throws ParseException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 */
	public static Object parseByType(Class clazz, String str) throws ParseException, InstantiationException, IllegalAccessException, SecurityException,
			IllegalArgumentException, NoSuchMethodException, InvocationTargetException {
		Object r = "";
		String clazzName = splitSpace(clazz.getName());
		if (isSimpleType(clazzName)) {
			if (TYPE_INTEGER.contains(clazzName)) {
				r = parseInteger(str);
			} else if (TYPE_DATE.contains(clazzName)) {
				r = nullString2Timestamp(str);
			} else if (TYPE_DOUBLE.contains(clazzName)) {
				r = nullObject2Double(str);
			} else if (TYPE_LONG.contains(clazzName)) {
				r = nullObject2Long(str);
			} else if (TYPE_SHORT.contains(clazzName)) {
				r = nullObject2Short(str);
			}

		} else {
			r = parseObject(clazz, str);
		}
		return r;
	}
	/**
	 * 实现将源类(Map类型)属性拷贝到目标类中
	 * 
	 * @param
	 *            map
	 * @param
	 *            obj
	 */
	public static void copyProperties(Map map, Object obj) throws Exception {
		// 获取目标类的属性信息
		BeanInfo targetbean = Introspector.getBeanInfo(obj.getClass());
		PropertyDescriptor[] propertyDescriptors = targetbean.getPropertyDescriptors();
		// 对每个目标类的属性查找set方法，并进行处理
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor pro = propertyDescriptors[i];
			Method wm = pro.getWriteMethod();
			if (wm != null) {
				Iterator ite = map.keySet().iterator();
				while (ite.hasNext()) {
					String key = (String) ite.next();

					// 判断匹配
					if (key.toLowerCase().equals(pro.getName().toLowerCase())) {
						if (!Modifier.isPublic(wm.getDeclaringClass().getModifiers())) {
							wm.setAccessible(true);
						}
						Object value = map.get(key);
						String pt = splitSpace(pro.getPropertyType().getName());
						// 判断类型是否匹配，不匹配则作强制转换
						if (value != null) {
							if (!(pt.equals(value.getClass().getName()))) {
								value = parseByType(pro.getPropertyType(), value.toString());
							}
							// 调用目标类对应属性的set方法对该属性进行填充
							wm.invoke((Object) obj, new Object[]{value});
						}
						break;
					}
				}
			}
		}
	}
	public static int getRandomNum(int begin, int end) {
		int j = 0;
		do {
			Random rand = new Random();
			j = rand.nextInt(end) + 1;
		} while (j < begin);
		return j;
	}
	/**
	 * 是否为手机号格式
	 * 

	 * @param receiverMobile
	 *
	 */
	public static boolean isMobileNO(String receiverMobile) {
		if (PubMethod.isEmpty(receiverMobile)) {
			return false;
		}
		String mobileRe = "^1\\d{10}$";
		Pattern pMobile = Pattern.compile(mobileRe);
		boolean mobileOk = pMobile.matcher(receiverMobile).matches();
		return mobileOk;
	}
	public static <T> T Json2Object(String json, Class<T> clazz) {
		T obj = null;
		if(isEmpty(json)){
			return null;
		}
		ObjectMapper om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		try {
			obj = om.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 获取泛型的Collection Type
	 * @param jsonStr json字符串
	 * @param collectionClass 泛型的Collection
	 * @param elementClasses 元素类型
	 */
	public static <T> T Json2Collection(String jsonStr, Class<?> collectionClass, Class<?>... elementClasses) {
		T t=null;
		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
		try {
			t=mapper.readValue(jsonStr, javaType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 描述:Object转换json
	 *
	 * @param o
	 * @return
	 * @author zhao
	 * @date 2015-1-9 下午4:26:19
	 */
	public static String Object2Json(Object o) {
		ObjectMapper om = new ObjectMapper();
		om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String json = "";
		try {
			json = om.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}


	/**
	 * 字符串的连接 创 建 人: 文超 创建时间: 2012-1-5 下午07:29:54
	 * 
	 * @param srcStr
	 * @param addStr
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static StringBuffer concat(StringBuffer srcStr, String addStr) {
		if (!PubMethod.isEmpty(srcStr) && srcStr.length() > 0 && !PubMethod.isEmpty(addStr)) {
			srcStr.append(",").append(addStr);
		} else {
			srcStr.append(addStr);
		}
		return srcStr;
	}

	public static StringBuffer concat(StringBuffer srcStr, String addStr, String splitStr) {
		if (PubMethod.isEmpty(addStr)) {
			return srcStr;
		}
		if (!PubMethod.isEmpty(srcStr) && srcStr.length() > 0 && !PubMethod.isEmpty(addStr)) {
			srcStr.append(splitStr).append(addStr);
		} else {
			srcStr.append(addStr);
		}
		return srcStr;
	}
	/**
	 * @Title: findDates
	 * @Description:查询一段时间内所有的年月日
	 * @param dBegin
	 * @param dEnd
	 * @return
	 * @throws Exception
	 * @date 2015-10-26 下午3:09:56
	 * @author liendan
	 */
	public static List<Date> findDates(Date dBegin, Date dEnd) {
		List lDate = new ArrayList();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		return lDate;
	}
	/**
	 * @Title: 切换正数和负数
	 * @Description:切换正数和负数
	 * @param number
	 *            数字
	 * @return 正数或者负数
	 * @throws Exception
	 * @date 2015-12-12 下午3:09:56
	 * @author liendan
	 */
	public static BigDecimal changeNumber(BigDecimal number) {
		number = number.multiply(new BigDecimal(-1));
		return number;
	}
	/**
	 * 获取一定长度的随机字符串
	 * 
	 * @param length
	 *            指定字符串长度
	 * @return 一定长度的字符串
	 */
	public static String getRandomStringByLength(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String getRandomNumByLength() {
		String base = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 获取当前  所在周的周末
	 * 
	 * @return
	 */
	public static Date getSundayOfTheDate(Date date) {
		Calendar now = Calendar.getInstance();
		now.setFirstDayOfWeek(Calendar.MONDAY);
		now.setTime(date);

		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);

		now.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		return now.getTime();
	}

	/**
	 * 获取当前 所在月的最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfTheDateMonth(Date date) {
		Calendar now = Calendar.getInstance();
		now.setFirstDayOfWeek(Calendar.MONDAY);
		now.setTime(date);

		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);

		now.set(Calendar.DAY_OF_MONTH, now.getActualMaximum(Calendar.DAY_OF_MONTH));

		return now.getTime();
	}

	/**
	 * 获取今日所剩余的毫秒数
	 * 
	 * @return
	 */
	public static Long getRemainMillSecondOfTheCurrentDay() {
		Calendar endDay = Calendar.getInstance();

		endDay.set(Calendar.HOUR_OF_DAY, 23);
		endDay.set(Calendar.MINUTE, 59);
		endDay.set(Calendar.SECOND, 59);

		long msnow = System.currentTimeMillis();

		return endDay.getTimeInMillis() - msnow;
	}
	/**
	 * 检验日期格式对否正确
	 * 
	 * @param checkValue
	 *            时间字符串
	 * @return boolean true正确，false错误
	 */
	public static boolean checkTime(String checkValue) {
		boolean flag = false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期
			sdf.setLenient(false);
			sdf.parse(checkValue);
			flag = true;
		} catch (ParseException e) {
			flag = false;
		}
		return flag;
	}
	/**
	  * 判断字符串是否是整数
	  */
	 public static boolean isInteger(String value) {
	  try {
	   Integer.parseInt(value);
	   return true;
	  } catch (NumberFormatException e) {
	   return false;
	  }
	 }

	 /**
	  * 判断字符串是否是浮点数
	  */
	 public static boolean isDouble(String value) {
	  try {
	   Double.parseDouble(value);
	   if (value.contains("."))
	    return true;
	   return false;
	  } catch (NumberFormatException e) {
	   return false;
	  }
	 }

	 /**
	  * 判断字符串是否是数字
	  */
	 public static boolean isNumber(String value) {
	  return isInteger(value) || isDouble(value);
	 }
	/**
	 * 获取Class对象
	 * 
	 * @param className
	 * @return 初始化失败返回 null
	 */
	public static Class<?> getClassByName(String className) {

		try {

			if (className != null && !"".equals(className.trim())) {
				return Class.forName(className);
			}

		} catch (ClassNotFoundException e) {
			logger.error("加载{}失败,无法找到文件!", className);
		}

		return null;
	}
	
	/**
	 * 
	 * 方法描述:金额精度校验
	 * @param value
	 * @return
	 * @since v1.0
	 */
	public static boolean validScale(BigDecimal value){
		if(value == null){
			return false;
		}
		if(value.scale() > 2){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * 方法描述:金额精度校验
	 * @param value
	 * @return
	 * @since v1.0
	 */
	public static boolean validScale3(BigDecimal value){
		if(value == null){
			return false;
		}
		if(value.scale() > 3){
			return true;
		}
		return false;
	}
	/**
	 * 根据交易类型和交易状态判断交易是否结束
	 * @author zhonghai.zhao@amssy.com
	 * @version 1.0.0
	 * @return {@link Short}
	 * @param tradeCat 交易类别 0购物 ，1提现，2充值，3通信，4返利，5转账，6扣款，7分利
	 * @param status
	 * 订单状态 
	 * 购物支付交易状态（0--99） 0 待支付 10支付中 20买家支付成功待签收 30签收成功 40 支付失败 50 取消支付（未付款成功）60退款 前端页面展示 交易失败（支付成功退款未签收）
	 * 提现交易状态（100--199）100 待初审 110待复审 120初审失败 130复审驳回至初审 140初审再提交 150复审失败 160待打款 170打款中 180打款成功 190打款失败
	 * 充值交易状态（200--299） 200 待充值 210 充值中 220 充值成功 230 充值失败
	 * 返利交易状态（400--499）410 返利成功  420 优惠券交易成功 
	 * 扣除佣金状态（600-699）600 待扣款 610 扣款成功 
	 * @return
	 */
	public static Short getEndIfByStatus(Short tradeCat,Integer status){
		Short endIf=0;  //交易是否结束状态 0未结束 1已结束
		if(tradeCat==0){//购物
			if(status>=30) endIf = 1;
		}else if(tradeCat==1){//提现
			if(status.intValue() == 120 || status.intValue() == 150 
					|| status.intValue() == 180 || status.intValue() == 190) {
				endIf = 1;
			}
		}else if(tradeCat==2){//充值
			if(status.intValue() >= 220) {
				endIf = 1;
			}
		}else if(tradeCat==3){//通信
			if(status>=700) endIf = 1;
		}else if(tradeCat==4){//返利
			if(status==410||status==420) endIf=1;
		}else if(tradeCat==5){//转账
			if(status.intValue() == 220 || status.intValue() == 230) {
				endIf = 1;
			}
		}else if(tradeCat==6){//扣除佣金
			if(status==610) endIf=1;
		}else if(tradeCat==7){//分利
			if(status==610) endIf=1;
		}
		
		return endIf;
	}
	/**
	 * 从字符串中抽取手机号（已去重）
	 * @author zhonghai.zhao@amssy.com
	 * @version 1.0.0
	 * @return {@link Set<String>}
	 * @param text
	 * @return
	 */
	public static Set<String> getMobileFormText(String text) {
		if (text == null || text.length() == 0) {
			return null;
		}
		Pattern pattern = Pattern.compile("\\d?1\\d+");
		Matcher matcher = pattern.matcher(text);
		Set<String> set = new HashSet<String>();
		while (matcher.find()) {
			String numStr=matcher.group();
			if(PubMethod.isMobileNO(numStr)){
				set.add(numStr);
			}
		}
		return set;
	}

	public static Boolean verifyEthAddress(String address){

		if(isEmpty(address)){
			return false;
		}
		String regex="^0x[0-9a-fA-F]{40}$";
		if(address.matches(regex)){
			return true;
		}else{
			return false;
		}
	}


	//获取最小整数比例 x是要转化比例的小数
	public static BigDecimal[] getMinScale(String x){
		BigDecimal[] result=new BigDecimal[2];
		int pow=x.length()-x.indexOf(".")-1;
		BigDecimal a= new BigDecimal(x).multiply(BigDecimal.valueOf(10).pow(pow));
		BigDecimal b=BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(10).pow(pow));
		BigDecimal a1=getMaxCommonDivisor(a,b);
		result[0]=a.divide(a1);
		result[1]=b.divide(a1);
		return result;
	}
	//求最大公约数函数
	public static BigDecimal getMaxCommonDivisor(BigDecimal a,BigDecimal b)
	{
		BigDecimal maxCommonDivisor=BigDecimal.valueOf(0);
		if(a.compareTo(b)<0)
		{   //交换a、b的值
			a=a.add(b);
			b=a.subtract(b);
			a=a.subtract(b);
		}
		if(a.divideAndRemainder(b)[1].compareTo(BigDecimal.valueOf(0))==0)
		{
			maxCommonDivisor = b;
		}
		while(a.divideAndRemainder(b)[1].compareTo(BigDecimal.valueOf(0))>0)
		{
			a=a.divideAndRemainder(b)[1];
			if(a.compareTo(b)<0)
			{
				a=a.add(b);
				b=a.subtract(b);
				a=a.subtract(b);
			}
			if(a.divideAndRemainder(b)[1].compareTo(BigDecimal.valueOf(0))==0)
			{
				maxCommonDivisor = b;
			}
		}
		return maxCommonDivisor;
	}





	/**
	 * 显示时间，如果与当前时间差别小于一天，则自动用**秒(分，小时)前，如果大于一天则用format规定的格式显示
	 *
	 * @author wxy
	 * @param ctime
	 *            时间
	 * @param format
	 *            格式 格式描述:例如:yyyy-MM-dd yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String showTime(Date ctime, String format,String language) {
		//System.out.println("当前时间是："+new Timestamp(System.currentTimeMillis()));


		//System.out.println("发布时间是："+df.format(ctime).toString());
		String r = "";
		if(ctime==null)return r;
		if(format==null)format="MM-dd HH:mm";

		long nowtimelong = System.currentTimeMillis();

		long ctimelong = ctime.getTime();
		long result = Math.abs(nowtimelong - ctimelong);

		if(result < 60000){// 一分钟内
			if("en".equals(language)){
				r="Just Now";
			}else{
				r = "刚刚";
			}
		}else if (result >= 60000 && result < 3600000){// 一小时内
			long seconds = result / 60000;
			if("en".equals(language)){
				r=seconds + " Mins Ago";
			}else{
				r = seconds + " 分钟前";
			}
		}else if (result >= 3600000 && result < 86400000){// 一天内
			long seconds = result / 3600000;
			if("en".equals(language)){
				r=seconds + " Hrs Ago";
			}else{
				r = seconds + " 小时前";
			}
		}else{// 日期格式
			format="MM-dd HH:mm";
			SimpleDateFormat df = new SimpleDateFormat(format);
			if("en".equals(language)){
				DateTime dtx=new DateTime(ctime);
				dtx.minusHours(8);
				ctime=dtx.toDate();
			}
			r = df.format(ctime).toString();
		}
		return r;
	}

	//生成随机用户名，数字和字母组成,
	public static String getStringRandom(int length) {

		String val = "";
		Random random = new Random();

		//参数length，表示生成几位随机数
		for(int i = 0; i < length; i++) {

			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			//输出字母还是数字
			if( "char".equalsIgnoreCase(charOrNum) ) {

				//输出是大写字母还是小写字母
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char)(random.nextInt(26) + temp);
			} else if( "num".equalsIgnoreCase(charOrNum) ) {
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}

	/**
	 * 输入对应值,获取需要的日期
	 * 0  当天
	 * 1  明天
	 * -1 昨天
	 * @return "yyyy-MM-dd"格式字符串
	 */
	public static String getPastDate(int num){
		//昨天日期
		// TODO 如果需要时区版时间可以将注释放开
		//DateTimeFormatter dateTimeFormatter = new DateTimeFormatter("yyyy-MM-dd");
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, num);
		date = calendar.getTime();
		//String currentDate = dateTimeFormatter.print(date, Locale.CHINA);
		String currentDate = formater.format(date);

		return currentDate;
	}


	/**
	 * 输入对应值,获取需要的日期
	 * 0  当天
	 * 1  明天
	 * -1 昨天
	 * @return Timestamp 时间
	 */
	public static Timestamp getPastTime(int num){
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, num);
		date = calendar.getTime();
		return new Timestamp(date.getTime());
	}

	/**
	 * 返回待签名的map
	 * @param map
	 * @return
	 */
	public static Map<String,Object> generateSignMap(Map<String,Object> map) {
		map.put("timestamp",System.currentTimeMillis());
		logger.info("map---->"+map);
		ObjectMapper om=new ObjectMapper();
		JacksonConfig.globalConfig(om,true);
		try {
			map=PubMethod.Json2ObjectByConfig(PubMethod.Object2JsonByConfig(map),Map.class);
			String json=om.writeValueAsString(map)+SystemConstants.APPSECERT;
			logger.info("????????????"+json);
			String signValue = DigestUtils.md5Hex(json);
			logger.info("signValue==="+signValue+"==jsonStr==="+json);

			map.put("sign",signValue);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return map;

	}

	public static <T> T Json2ObjectByConfig(String json, Class<T> clazz) {
		T obj = null;
		if(isEmpty(json)){
			return null;
		}
		ObjectMapper om = new ObjectMapper();
		JacksonConfig.globalConfig(om,true);
		try {
			obj = om.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 描述:Object转换json
	 *
	 * @param o
	 * @return
	 * @author
	 * @date 2015-1-9 下午4:26:19
	 */
	public static String Object2JsonByConfig(Object o) {
		ObjectMapper om = new ObjectMapper();
		JacksonConfig.globalConfig(om,true);
		String json = "";
		try {
			json = om.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
}