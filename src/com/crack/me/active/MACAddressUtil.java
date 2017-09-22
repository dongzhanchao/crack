package com.crack.me.active;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网卡地址及计算后的地址<br>
 * 计算的算法其实很简单，就是网卡地址的后两位转换为两位的十六进制，不够位的补零
 * 
 * @author macbookpro
 * 
 */
public class MACAddressUtil {

    String macAddress = null;
    String computeMacAddress = null;

    /* 不需要进行地址计算的网卡名称 */
    private static final Pattern HARDWARE_PATTERN = Pattern.compile("(.*wireless.*)|(.*tunnel.*)|(.*atapi.*)|(.*bluetooth.*)|(.*vnic.*)|(.*vmnet.*)", 2);

    /**
     * 使用Java内部类<code>java.net.NetworkInterface</code> 解析和计算网卡地址
     * <p>
     * 计算流程：
     * <li>首先找出所有的网卡
     * <li>筛选网卡（网卡的属性和名称两个条件一次筛选）
     * <li>计算网卡
     * <p>
     * 计算的算法其实很简单，就是网卡地址的后两位转换为两位的十六进制，不够位的补零 <br>
     * <simple>6-41 --> 06D7</simple>
     * <p>
     * 网卡物理地址
     * <p>
     * MAC地址是网卡的物理地址，它由48位二进制数表示。其中前面24位表示网络厂商标识符，后24位表示序号。 每个不同的网络厂商会有不同的厂商标识符
     * ，而每个厂商所生产出来的网卡都是依序号不断变化的，所以每块网卡的MAC地址是世界上独一无二的 （特殊情况除外：如要通过修改MAC地址来通过认证时
     * ）。一般我们采用六个十六进制数来表示一个完整的MAC地址，如00:e0:4c:01:02:85。
     * <p>
     * <code>java.net.NetworkInterface.getHardwareAddress()</code> 获取的是byte[]
     * 数组，每个byte转换为十六进制即常见的mac地址 <br>
     * 如果没有，则返回0000
     * 
     * @return 计算后的mac地址
     * @throws Exception
     */
    public String getMacAddressWithNetworkInterface() {
	String computeMacAddress = null;
	try {
	    /* 列出所有网络接口，即网卡 */
	    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	    /* 依次循环网卡进行处理 */
	    while (networkInterfaces.hasMoreElements()) {
		NetworkInterface ni = networkInterfaces.nextElement();
		/**
		 * 过滤网卡
		 * <p>
		 * <li>不能为空
		 * <li>不是虚拟
		 * <li>不是回环
		 * <li>正在使用
		 */
		if (ni != null && !ni.isVirtual() && !ni.isLoopback() && ni.isUp()) {
		    byte[] hardwareAddress = ni.getHardwareAddress();
		    // 基本条件（不为空，长度大于2）
		    if (hardwareAddress != null && hardwareAddress.length != 2) {
			// 主要记录

			/**
			 * 原来的逻辑为
			 * 
			 * <pre>
			 * int offsetIndex = 0;
			 * for (int i = 0; i &lt; hardwareAddress.length; i++) {
			 *     offsetIndex = hardwareAddress[i] &gt; 0 ? 1 : 0;
			 * }
			 * if(offsetIndex!=0){...}
			 * </pre>
			 * 
			 */

			/* 网卡地址是否合法 */
			boolean isMacAddressLegal = false;
			for (byte b : hardwareAddress) {
			    if (b <= 0) {
				/* 正确的网卡地址中byte位的值，必须有一个为小于等于0的 */
				isMacAddressLegal = true;
				break;
			    }
			}

			if (isMacAddressLegal) {
			    /* 网卡名称 */
			    String hardwareName = ni.getDisplayName();
			    if (hardwareName != null && hardwareName.length() != 0) {
				/* 过滤掉不需要计算的网卡 */
				Matcher matcher = HARDWARE_PATTERN.matcher(hardwareName);
				if (!matcher.lookingAt()) {
				    computeMacAddress = String.format("%02x%02x", new Object[] {//
					    /* 地址的最后两位转为十六进制 */
					    /*    */Byte.valueOf(hardwareAddress[(hardwareAddress.length - 2)]), //
						    Byte.valueOf(hardwareAddress[(hardwareAddress.length - 1)]) //
					    });
				    this.macAddress = buildMacAddress(hardwareAddress);
				}
			    }
			}
		    }
		}
	    }
	} catch (SocketException e) {
	    e.printStackTrace();
	}
	/* 假如没有结果，则返回0000 */
	return (computeMacAddress == null ? "0000" : computeMacAddress);
    }

    public String buildMacAddress(byte[] bytes) {
	StringBuffer sb = new StringBuffer();
	if (bytes != null) {
	    for (byte b : bytes) {
		sb.append(String.format("%02x", b) + ":");
	    }
	    sb.deleteCharAt(sb.length() - 1);
	}
	return sb.toString();
    }

    /**
     * 通过系统进程，执行命令，返回执行结果
     * 
     * @param commands
     * @return
     */
    public String executeCommand(String[] commands) {
	Process process = null;
	BufferedReader br = null;
	String returnValue = null;
	try {
	    process = Runtime.getRuntime().exec(commands);
	    br = new BufferedReader(new InputStreamReader(process.getInputStream()), 128);
	    returnValue = br.readLine();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    // 关闭连接
	    if (process != null) {
		try {
		    if (br != null) {
			br.close();
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		try {
		    process.getErrorStream().close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		try {
		    process.getOutputStream().close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return returnValue;
    }

    /**
     * 通过操作系统的命令来获取mac地址，并进行计算。<br>
     * 如果没有，则返回0000
     * 
     * @return 计算后的mac地址
     */
    public String getMacAddressWithOS() {
	/* 系统进程，用于调用系统命令 */
	Process process = null;
	String macAddress = null;
	BufferedReader br = null;
	/* 操作系统名称 */
	String osName = System.getProperty("os.name");
	if (osName != null) {
	    try {
		/**
		 * 根据不同的操作系统，选择不同的系统能够进程进行读取mac地址
		 */
		if (osName.startsWith("Windows")) {// Windows
		    process = Runtime.getRuntime().exec(new String[] { "ipconfig", "/all" }, null);
		} else if ((osName.startsWith("Solaris")) || (osName.startsWith("SunOS"))) { // Sun
		    /* 查看主机名 */
		    String hostName = executeCommand(new String[] { "uname", "-n" });
		    if (hostName != null) {
			process = Runtime.getRuntime().exec(new String[] { "/usr/sbin/arp", hostName }, null);
		    }
		} else if (new File("/usr/sbin/lanscan").exists()) {// HP-UX
		    process = Runtime.getRuntime().exec(new String[] { "/usr/sbin/lanscan" }, null);
		} else if (new File("/sbin/ifconfig").exists()) {// Unix,Linux,Mac
		    process = Runtime.getRuntime().exec(new String[] { "/sbin/ifconfig", "-a" }, null);
		}
		// end if

		if (process != null) {
		    // 已经执行的操作系统命令
		    br = new BufferedReader(new InputStreamReader(process.getInputStream()), 128);
		    String line = null;
		    while ((line = br.readLine()) != null) {
			// 找出匹配mac的字符串，并返回macAddress
			macAddress = macAddressParser(line);
			// 找到mac地址，返回
			if (macAddress != null && Hex.parseShort(macAddress) != 0xff) {
			    this.macAddress = macAddress;
			    break;
			}
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		// 关闭连接
		if (process != null) {
		    try {
			if (br != null) {
			    br.close();
			}
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		    try {
			process.getErrorStream().close();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		    try {
			process.getOutputStream().close();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    }
	}
	/**
	 * 如00:e0:4c:01:02:85<br>
	 * 取最后的两位十六进制
	 */
	String computeMacAddress = null;
	int length = macAddress != null ? macAddress.length() : 0;
	if (length >= 5) {
	    computeMacAddress = macAddress.substring(length - 5, length - 3) + macAddress.substring(length - 2, length);
	}
	return (computeMacAddress != null ? computeMacAddress : "0000");
    }

    /**
     * The MAC address parser attempts to find the following patterns:
     * 
     * <ul>
     * <li>.{1,2}:.{1,2}:.{1,2}:.{1,2}:.{1,2}:.{1,2}</li>
     * <li>.{1,2}-.{1,2}-.{1,2}-.{1,2}-.{1,2}-.{1,2}</li>
     * </ul>
     * 
     * Attempts to find a pattern in the given String.
     * 
     * @see <a href="http://johannburkard.de/software/uuid/">UUID</a>
     * @author <a href="mailto:jb@eaio.com">Johann Burkard</a>
     * @version $Id: MACAddressParser.java 1888 2009-03-15 12:43:24Z johann $
     * 
     * @param in
     *            the String, may not be <code>null</code>
     * 
     * @return the substring that matches this pattern or <code>null</code>
     * 
     */
    public String macAddressParser(String line) {
	String out = line;

	int hexStart = out.indexOf("0x");
	if (hexStart != -1 && out.indexOf("ETHER") != -1) {
	    int hexEnd = out.indexOf(' ', hexStart);
	    if (hexEnd > hexStart + 2) {
		out = out.substring(hexStart, hexEnd);
	    }
	} else {
	    int octets = 0;
	    int lastIndex, old, end;
	    if (out.indexOf('-') > -1) {
		out = out.replace('-', ':');
	    }
	    lastIndex = out.lastIndexOf(':');
	    if (lastIndex > out.length() - 2) {
		out = null;
	    } else {
		end = Math.min(out.length(), lastIndex + 3);
		++octets;
		old = lastIndex;
		while (octets != 5 && lastIndex != -1 && lastIndex > 1) {
		    lastIndex = out.lastIndexOf(':', --lastIndex);
		    if (old - lastIndex == 3 || old - lastIndex == 2) {
			++octets;
			old = lastIndex;
		    }
		}
		if (octets == 5 && lastIndex > 1) {
		    out = out.substring(lastIndex - 2, end).trim();
		} else {
		    out = null;
		}
	    }
	}

	if (out != null && out.startsWith("0x")) {
	    out = out.substring(2);
	}

	return out;
    }

    /**
     * 获取网卡计算结果<br>
     * 首先通过Java获取，如果获取不到，则通过操作系统命令获取
     * 
     * @return
     */
    public String getMacAddress() {
	if (this.computeMacAddress != null) {
	    return computeMacAddress;
	}
	String macAddr = getMacAddressWithNetworkInterface();
	if (macAddr == null || "0000".equals(macAddr)) {
	    macAddr = getMacAddressWithOS();
	}
	computeMacAddress = macAddr;
	return macAddr;
    }

}
