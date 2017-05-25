package com.circle.core.util;


/**
 * 各种校验类,包含各种正则表达式,和各种校验方法, 并返回错误码
 * 
 * @author chenxx
 *
 */
public class Verification {
	/**
	 * 以1开始的手机号. 18322695762
	 */
	public static final String REGEX_MOBILE = "^1[1-9][0-9]{9}$";
	/**
	 * 校验是否为数字
	 */
	public static final String REGEX_NUMBER = "^[0-9]+$";
	public static final String REGEX_DOUBLE = "^[0-9]+(.[0-9]+)?$";
	/**
	 * 校验long型数据
	 */
	public static final String REGEX_LONG = "^[0-9]{1,19}$";
	/**
	 * 校验type类型数据
	 */
	public static final String REGEX_TYPE = "^[0-9]{1,2}$";
	/**
	 * 昵称正则表达式,中文、英文、数字包括下划线
	 */
	public static final String REGEX_NICKNAME = "^[\u4E00-\u9FA5A-Za-z0-9_\\.]{1,32}$";
	/**
	 * 邮箱正则验证
	 */
	public static final String REGEX_EMAIL = "^[0-9]{1,2}$";

	/**
	 * 数据是否正确
	 * 
	 * @param str
	 * @return
	 */
	public static boolean longReg(String str) {
		if (str == null) {
			return false;
		}
		return str.matches(REGEX_LONG);
	}

	/**
	 * 用户昵称格式校验
	 * 
	 * @param nickName
	 * @return
	 */
	public static boolean nickNameReg(String nickName) {
		if (nickName == null) {
			return false;
		}
		return nickName.matches(REGEX_NICKNAME);
	}

	/**
	 * 校验long类型邮箱数据是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean emailReg(String email) {
		if (email == null) {
			return false;
		}
		return email.matches(REGEX_EMAIL);
	}

	/**
	 * 校验long类型数据是否正确
	 * 
	 * @param type
	 * @return
	 */
	public static boolean typeReg(String type) {
		if (type == null) {
			return false;
		}
		return type.matches(REGEX_TYPE);
	}

	/**
	 * 校验手机号是否正确
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean mobileReg(String mobile) {
		if (mobile == null) {
			return false;
		}
		return mobile.matches(REGEX_MOBILE);
	}

	/**
	 * 数字类型校验
	 * 
	 * @param number
	 * @return
	 */
	public static boolean numberReg(String number) {
		if (number == null) {
			return false;
		}
		return number.matches(REGEX_NUMBER);
	}
	public static boolean doubleReg(String number) {
		if (number == null) {
			return false;
		}
		return number.matches(REGEX_DOUBLE);
	}

	/**
	 * 
	 * @param def 默认值
	 * @param numstr 需要转换的 字符串
	 * @return
	 */
	public static int getInt(int def, String numstr) {
		if (numstr == null || !numberReg(numstr))
			return def;
		return Integer.valueOf(numstr);
	}
	public static Integer integer(String numstr) {
		if (numstr == null || !numberReg(numstr))
			return null;
		return Integer.valueOf(numstr);
	}

	public static double getDoule(double def, String value) {
		if (value == null || !doubleReg(value))
			return def;
		return Double.valueOf(value);
	}
	public static long getLong(long def, String value) {
		if (value == null || !longReg(value))
			return def;
		return Long.valueOf(value);
	}
	public static float getFloat(float def, String value) {
		if (value == null || !doubleReg(value))
			return def;
		return Float.valueOf(value);
	}
}
