package com.crack.me.active;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 激活码
 * 
 * @author macbookpro
 * 
 */
public class ActivationCode {

    private int formate = 1;
    private SystemId systemId = null;
    private String licenceCode = null;
    private Date date = null;

    // generate code need
    private int licenceLength = 0;
    private String dateString = null;

    /**
     * 根据加密后的激活码生成
     * 
     * @param activeCode
     *            加密后的激活码
     * @return
     * @throws Exception
     */
    public static ActivationCode fromCode(String activeCode) throws Exception {
	String decode = new RSAKey().decryption(activeCode);
	int formate = Integer.parseInt(decode.substring(1, 2));// substring(0,1)???
	if (formate != 1) {
	    throw new Exception("InvalidSystemDataException! {active decode first code must be 1 !}");
	}
	System.out.println(decode);
	String si = decode.substring(1, 20);
	SystemId systemId = SystemId.fromCode(si);

	int licenceLength = Integer.parseInt(decode.substring(20, 22));
	int licenceLastIndex = 22 + licenceLength;
	String licence = decode.substring(22, licenceLastIndex);
	String decodeDate = decode.substring(licenceLastIndex);
	Date licenceDate = new SimpleDateFormat("yyyyMMdd").parse("20" + decodeDate);

	return new ActivationCode(formate, systemId, licence, licenceDate);
    }

    public static ActivationCode fromCode(String systemId, String licenceCode, String dateString) throws Exception {
	Date licenceDate = new SimpleDateFormat("yyyyMMdd").parse("20" + dateString);
	int formate = Integer.parseInt(systemId.substring(0, 1));
	SystemId si = SystemId.fromCode(systemId);
	return new ActivationCode(formate, si, licenceCode, licenceDate);
    }

    private ActivationCode(SystemId systemId, String licenceCode, Date date) {
	this(1, systemId, licenceCode, date);
    }

    private ActivationCode(int formate, SystemId systemId, String licenceCode, Date date) {
	this.formate = formate;
	this.systemId = systemId;
	this.licenceCode = licenceCode;
	this.date = date;
	this.licenceLength = licenceCode.length();
	this.dateString = new SimpleDateFormat("yyMMdd").format(date);
    }

    public int getFormate() {
	return formate;
    }

    public SystemId getSystemId() {
	return systemId;
    }

    public String getLicenceCodeCode() {
	return licenceCode;
    }

    public Date getDate() {
	return date;
    }

    public boolean isExpired() {
	return isExpired(new Date());
    }

    public boolean isExpired(Date otherDate) {
	return otherDate.getTime() > date.getTime();
    }

    public String generateActivationCode() {

	return "1" // DEFAULT, 0~9 is OK
		+ systemId.getCode() //
		+ licenceLength + //
		licenceCode + //
		dateString;
    }

    @Override
    public String toString() {
	return "****[flag]****\n" + formate//
		+ "\n****[SystemId]****\n" + systemId//
		+ "\n****[licenceCode]****\n" + licenceCode//
		+ "\n****[Date]****\n" + new SimpleDateFormat("yyyy-MM-dd").format(date)//
	;
    }

}
