package com.crack.me.active;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据不同的版本和用户名计算出相应的code
 * 
 * @author macbookpro
 */
public class LicenceCode {

    private final static String LICENCE_KEY = "Decompiling this copyrighted software is a violation of both your license agreement and the Digital Millenium Copyright Act of 1998 (http://www.loc.gov/copyright/legislation/dmca.pdf). Under section 1204 of the DMCA, penalties range up to a $500,000 fine or up to five years imprisonment for a first offense. Think about it; pay for a license, avoid prosecution, and feel better about yourself.";

    private Map<Integer, String> licenceTypes = null;

    public static final int STANDARD_TYPE = 0;
    public static final int PROFESSIONAL_TYPE = 1;
    public static final int BLUE_TYPE = 2;
    public static final int SPRING_TYPE = 3;

    public static final int DEFAULT_LICENCE_TYPE = PROFESSIONAL_TYPE;
    public static final int UNLIMITED_LICENCE_NUMBER = 0;

    public LicenceCode() {
	initLicenceType();
    }

    private void initLicenceType() {
	licenceTypes = new HashMap<Integer, String>();
	licenceTypes.put(STANDARD_TYPE, "YE2MY");
	licenceTypes.put(PROFESSIONAL_TYPE, "YE3MP");
	licenceTypes.put(BLUE_TYPE, "YE3MB");
	licenceTypes.put(SPRING_TYPE, "YE3MS");
	/**
	 * 9.1 又增加了许多编码，就不一一列出了，这几个比较常用
	 */
    }

    private String getLicenceTypeCode(int type) {
	String licenceType = licenceTypes.get(type);
	if (licenceType == null) {
	    licenceType = licenceTypes.get(DEFAULT_LICENCE_TYPE);
	}
	return licenceType;
    }

    /**
     * 生成code
     * <p>
     * 过程如下
     * <li>1. userid 取第一个字母
     * <li>2. licenceType Code (MyEclipse版本)
     * <li>3. 300 (License Version)
     * <li>4. licenseNumber（授权数量，格式化三位，不够位补零）
     * <li>5. base = 1 + 2 + 3 + 4
     * <li>6. (base + LICENCE_KEY + userid) 进行编码
     * <li>6. base + 编码后的 再进行转换
     * 
     * @param userId
     * @param licenseNum
     * @param licenceType
     * @return
     */
    public String generateLicenceCode(String userId, int licenceNum, int licenceType) {
	/**
	 * 破解日期<br>
	 * 日期为三年后的今天的前一天
	 */
	Calendar calendar = Calendar.getInstance();
	calendar.add(Calendar.YEAR, 3);
	calendar.add(Calendar.DAY_OF_YEAR, -1);

	String uid = userId.substring(0, 1);
	String licenceTypeCode = getLicenceTypeCode(licenceType);
	String licenceVersion = "300";
	String licenceNumber = new DecimalFormat("000").format(Integer.valueOf(licenceNum));
	String expirationDate = new SimpleDateFormat("yyMMdd").format(calendar.getTime()) + "0";

	String base = uid + licenceTypeCode + "-" + licenceVersion + licenceNumber + "-" + expirationDate;

	/* 加密成数字 */
	String needDecode = base + LICENCE_KEY + userId;
	int suf = decode(needDecode);

	// base 和 加密结果的数字 相加 然后进行转换
	String code = base + String.valueOf(suf);
	return transform(code);
    }

    /**
     * 字符串提取每个字符，然后转换成字符的asc码 <br>
     * a(0) = 0<br>
     * x = 字符串的长度<br>
     * a(x) = 31 * a(x - 1) + [each char ascii(byte value)] <br>
     * 结果取绝对值
     * <p>
     * 其实这个方法就是String类的hashcode()，比较有效的哈希算法<br>
     * very simple
     * 
     * @param s
     * @return
     */
    private int decode(String s) {
	char[] ac = s.toCharArray();
	int sum = 0;
	for (int i = 0; i < ac.length; i++) {
	    sum = 31 * sum + ac[i];
	}
	return Math.abs(sum);
    }

    /**
     * 字符转换
     * <p>
     * 转换规则：<br>
     * <li>1. 转换范围 [0~9],[A~Z],[a~z]
     * <li>2. 转换规则 从相应区间中间分开，从中间的右边开始重新编排编码，后面不够的从最开始继续编码.
     * <li>3. 其他范围的字符不进行转换
     * <p>
     * 字符对照表 字符(ASCII值)
     * 
     * <pre>
     * 0(48)	1(49)	2(50)	3(51)	4(52)	5(53)	6(54)	7(55)	8(56)	9(57)	
     * 5(53)	6(54)	7(55)	8(56)	9(57)	0(48)	1(49)	2(50)	3(51)	4(52)	
     * ******************************************************************************
     * A(65)	B(66)	C(67)	D(68)	E(69)	F(70)	G(71)	H(72)	I(73)	J(74)	K(75)	L(76)	M(77)	N(78)	O(79)	P(80)	Q(81)	R(82)	S(83)	T(84)	U(85)	V(86)	W(87)	X(88)	Y(89)	Z(90)	
     * N(78)	O(79)	P(80)	Q(81)	R(82)	S(83)	T(84)	U(85)	V(86)	W(87)	X(88)	Y(89)	Z(90)	A(65)	B(66)	C(67)	D(68)	E(69)	F(70)	G(71)	H(72)	I(73)	J(74)	K(75)	L(76)	M(77)	
     * ******************************************************************************
     * a(97)	b(98)	c(99)	d(100)	e(101)	f(102)	g(103)	h(104)	i(105)	j(106)	k(107)	l(108)	m(109)	n(110)	o(111)	p(112)	q(113)	r(114)	s(115)	t(116)	u(117)	v(118)	w(119)	x(120)	y(121)	z(122)
     * n(110)	o(111)	p(112)	q(113)	r(114)	s(115)	t(116)	u(117)	v(118)	w(119)	x(120)	y(121)	z(122)	a(97)	b(98)	c(99)	d(100)	e(101)	f(102)	g(103)	h(104)	i(105)	j(106)	k(107)	l(108)	m(109)
     * ******************************************************************************
     * 
     * <pre>
     * @param s
     * @return
     */
    private String transform(String s) {
	byte[] bytes = s.getBytes();
	char[] changed = new char[s.length()];
	for (int i = 0; i < bytes.length; i++) {

	    int value = bytes[i];
	    if ((value >= '0') && (value <= '9')) {
		value = ((((value - '0') + 5) % 10) + '0');
	    } else if ((value >= 'A') && (value <= 'Z')) {
		value = ((((value - 'A') + 13) % 26) + 'A');
	    } else if ((value >= 'a') && (value <= 'z')) {
		value = ((((value - 'a') + 13) % 26) + 'a');
	    }
	    changed[i] = (char) value;
	}

	return String.valueOf(changed);
    }

    public boolean isLicenceCorrect(String uid, String licenceCode) {
	if (licenceCode == null || licenceCode.length() < 22) {
	    return false;
	}
	String lc = transform(licenceCode);
	String base = lc.substring(0, 21);
	int srcCode = Integer.parseInt(lc.substring(21, lc.length()));
	int desCode = decode(base + LICENCE_KEY + uid);
	return (srcCode == desCode);
    }

    public boolean isLicenceDateExpired(String licenceCode) {
	if (licenceCode == null || licenceCode.length() < 22) {
	    return true;
	}
	try {
	    String lc = transform(licenceCode);
	    String date = lc.substring(14, 20);
	    Date licenceDate = new SimpleDateFormat("yyyyMMdd").parse("20" + date);
	    Calendar licenceCalendar = Calendar.getInstance();
	    licenceCalendar.setTime(licenceDate);

	    int compare = licenceCalendar.compareTo(Calendar.getInstance());

	    return compare <= 0;
	} catch (ParseException e) {
	    e.printStackTrace();
	    return true;
	}
    }

    public String getExpirationDate(String licenceCode) {
	String lc = transform(licenceCode);
	// String uid = lc.substring(0, 1);
	// String licenceTypeCode = lc.substring(2, 6);
	// int licenseVersion = Integer.parseInt(lc.substring(7, 8));
	// int licenseVersionType = Integer.parseInt(lc.substring(8, 10));
	// int licenseNumber = Integer.parseInt(lc.substring(10, 13));
	String date = lc.substring(14, 20);

	return date;
    }

    public void showLicence(String licenceCode) {
	licenceCode = transform(licenceCode);
	/* ... */
    }

}
