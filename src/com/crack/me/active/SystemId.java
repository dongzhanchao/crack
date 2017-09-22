package com.crack.me.active;

import java.util.Random;

/**
 * systemID Length (19)
 * <p>
 * <li>TYPE_FIELD 1位
 * <li>FIELD 1位
 * <li>HOST_FIELD 地址编码 2位
 * <li>SYSINFO_FIELD 系统id编码 7位
 * <li>MAC_FIELD mac地址编码 4位
 * <li>HD_FIELD 处理器编码 4位
 * <p>
 * 该类实例由SystemIdFactory产生
 * 
 * @author macbookpro
 */
public class SystemId {
    public static final int NODE_LOCKED = 1;
    public static final int NODE_UNLOCKED = 2;

    /* DEFAULT VALUE (NO VALUE == 0) */
    public static final String NO_HOST = "00";
    public static final String NO_SYSINFO = "0000000";
    public static final String NO_MAC = "0000";
    public static final String NO_HD = "0000";

    public static final int FORMAT_TYPE_FIELD_LENGTH = 1;
    public static final int FORMAT_FIELD_LENGTH = 1;

    public static final int HOST_FIELD_LENGTH = "00".length();
    public static final int SYSINFO_FIELD_LENGTH = "0000000".length();
    public static final int MAC_FIELD_LENGTH = "0000".length();
    public static final int HD_FIELD_LENGTH = "0000".length();

    public static final int SYSTEM_ID_LENGTH = 2 + HOST_FIELD_LENGTH + SYSINFO_FIELD_LENGTH + MAC_FIELD_LENGTH + HD_FIELD_LENGTH;

    private int field = 0;
    private String hostInfo = NO_HOST;
    private String systemInfo = NO_SYSINFO;
    private String macAddress = NO_MAC;
    private String HDSerial = NO_HD;

    /**
     * 根据SystemId编码解码成SystemId类
     * 
     * @param systemId
     * @return
     * @throws Exception
     */
    public static SystemId fromCode(String systemId) throws Exception {
	if (isNullOrEmpty(systemId) || (systemId.length() < SYSTEM_ID_LENGTH)) {
	    throw new Exception("InvalidLicenseDataException!{systemId is null or length not right!}");
	}
	// 第一个位置 TypeField 标志位的类型
	int typeField = Integer.parseInt(systemId.substring(0, 1), 16);
	if ((typeField != 1) && (typeField != 2)) {
	    throw new Exception("InvalidLicenseDataException!{systemId first letter must be 1 or 2 !}");
	}
	// 第二个位置 标志位
	int field = Integer.parseInt(systemId.substring(1, 2), 16);
	String host = NO_HOST, systemInfo = NO_SYSINFO, macAddress = NO_MAC, hdSerial = NO_HD;

	if ((field & 0x1) == 1) {
	    host = systemId.substring(2, 2 + HOST_FIELD_LENGTH);
	}
	if ((field & 0x2) == 2) {
	    systemInfo = systemId.substring(4, 4 + SYSINFO_FIELD_LENGTH);
	}
	if ((field & 0x4) == 4) {
	    macAddress = systemId.substring(11, 11 + MAC_FIELD_LENGTH);
	}
	if ((field & 0x8) == 8) {
	    hdSerial = systemId.substring(15, 15 + HD_FIELD_LENGTH);
	}

	return new SystemId(field, host, systemInfo, macAddress, hdSerial);
    }

    public SystemId(int field, String hostInfo, String systemInfo, String macAddress, String HDSerial) {
	this.field = field;
	this.hostInfo = ((isNullOrEmpty(hostInfo)) || (!isIdLegal(hostInfo)) ? NO_HOST : hostInfo);
	this.systemInfo = ((isNullOrEmpty(systemInfo)) || (!isIdLegal(systemInfo)) ? NO_SYSINFO : systemInfo);
	this.macAddress = ((isNullOrEmpty(macAddress)) || (!isIdLegal(macAddress)) ? NO_MAC : macAddress);
	this.HDSerial = ((isNullOrEmpty(HDSerial)) || (!isIdLegal(HDSerial)) ? NO_HD : HDSerial);
    }

    /**
     * 是否处于锁定状态,默认为<code>true</code>
     * 
     * @return
     */
    public boolean isNodeLocked() {
	return true;
    }

    public int getField() {
	return field;
    }

    public String getHostInfo() {
	return hostInfo;
    }

    public boolean hasHostInfo() {
	return !NO_HOST.equals(hostInfo);
    }

    public String getSystemInfo() {
	return systemInfo;
    }

    public boolean hasSystemInfo() {
	return !NO_SYSINFO.equals(systemInfo);
    }

    public String getMacAddress() {
	return macAddress;
    }

    public boolean hasMacAddress() {
	return !NO_MAC.equals(macAddress);
    }

    public String getHDSerial() {
	return HDSerial;
    }

    public boolean hasHDSerial() {
	return !NO_HD.equals(HDSerial);
    }

    /**
     * 对比顺序为 逆序<br>
     * 只要有一个能对应上，及完成匹配,返回<code>true</code>
     * <p>
     * <li>1. hdSerial
     * <li>2. MacAddress
     * <li>3. HostInfo
     * <li>4. SystemInfo
     * 
     * @param other
     * @return
     */
    public boolean matches(SystemId other) {
	if (other == null) {
	    return false;
	}
	if ((hasHDSerial()) && (other.hasHDSerial()) && (getHDSerial().equals(other.getHDSerial()))) {
	    return true;
	}
	if ((hasMacAddress()) && (other.hasMacAddress()) && (getMacAddress().equals(other.getMacAddress()))) {
	    return true;
	}
	if ((hasHostInfo()) && (other.hasHostInfo()) && (getHostInfo().equals(other.getHostInfo()))) {
	    return true;
	}
	return (hasSystemInfo()) && (other.hasSystemInfo()) && (getSystemInfo().equals(other.getSystemInfo()));
    }

    /**
     * 合成SystemId字符串
     * 
     * @return
     */
    public String getCode() {
	int typeField = isNodeLocked() ? 1 : 2;
	int field = 0;
	field += (hasHostInfo() ? 1 : 0);
	field += (hasSystemInfo() ? 2 : 0);
	field += (hasMacAddress() ? 4 : 0);
	field += (hasHDSerial() ? 8 : 0);

	return Integer.toString(typeField, 16) //
		+ Integer.toString(field, 16) //
		+ (hasHostInfo() ? getHostInfo() : randomId(HOST_FIELD_LENGTH))//
		+ (hasSystemInfo() ? getSystemInfo() : randomId(SYSINFO_FIELD_LENGTH))//
		+ (hasMacAddress() ? getMacAddress() : randomId(MAC_FIELD_LENGTH))//
		+ (hasHDSerial() ? getHDSerial() : randomId(HD_FIELD_LENGTH));
    }

    /**
     * 随机成成相应位数的16进制数字
     * 
     * @param idLength
     * @return
     */
    private String randomId(int idLength) {
	StringBuffer localStringBuffer = new StringBuffer(idLength);
	Random localRandom = new Random();
	for (int i = 0; i < idLength; i++)
	    localStringBuffer.append(Integer.toHexString(localRandom.nextInt(16)));
	return localStringBuffer.toString();
    }

    /**
     * ID 检验器<br>
     * 所有字符都可以由16进制转换成byte都视为合法
     * 
     * @param id
     * @return
     */
    private static boolean isIdLegal(String id) {
	if (isNullOrEmpty(id))
	    return false;
	for (int i = 0; i < id.length(); i++) {
	    String str = id.substring(i, i + 1);
	    try {
		Byte.parseByte(str, 16);
	    } catch (NumberFormatException e) {
	    	e.printStackTrace();
		return false;
	    }
	}
	return true;
    }

    public static boolean isNullOrEmpty(String value) {
	return ((null == value) || ("".equals(value)));
    }

    @Override
    public String toString() {
	return "TypeField:\t" + (isNodeLocked() ? 1 : 2) //
		+ "\nField:    \t" + this.field//
		+ "\nHostInfo:\t" + this.hostInfo//
		+ "\nSystemInfo:\t" + this.systemInfo//
		+ "\nMacAddress:\t" + this.macAddress//
		+ "\nHDSerial:\t" + this.HDSerial//
	;
    }
  
}
