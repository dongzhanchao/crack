package com.crack.me.active;

public class Crack implements Crackable {

    public String getSystemId() throws Exception {
	return new SystemIdFactory().generateSystemId();
    }

    public String[][] crackme(String uid) throws Exception {
	return crackme(uid, LicenceCode.PROFESSIONAL_TYPE, LicenceCode.UNLIMITED_LICENCE_NUMBER, null);
    }

    public String[][] crackme(String uid, int licenceType) throws Exception {
	return crackme(uid, licenceType, LicenceCode.UNLIMITED_LICENCE_NUMBER, null);
    }

    public String[][] crackme(String uid, String systemId) throws Exception {
	return crackme(uid, LicenceCode.PROFESSIONAL_TYPE, LicenceCode.UNLIMITED_LICENCE_NUMBER, systemId);
    }

    public String[][] crackme(String uid, int licenceType, String systemId) throws Exception {
	return crackme(uid, licenceType, LicenceCode.UNLIMITED_LICENCE_NUMBER, systemId);
    }

    public String[][] crackme(String uid, int licenceType, int licenceNumber, String systemId) throws Exception {

	if (systemId == null || "".equals(systemId)) {
	    systemId = getSystemId();
	}

	LicenceCode lc = new LicenceCode();
	String licenceCode = lc.generateLicenceCode(uid, licenceNumber, licenceType);
	String dateString = lc.getExpirationDate(licenceCode);

	ActivationCode ac = ActivationCode.fromCode(systemId, licenceCode, dateString);

	String perpareCode = ac.generateActivationCode();
	String activeCode = new RSAKey().encryption(perpareCode);

	return new String[][] {//
	/* */new String[] { "LICENSEE", uid },//
		new String[] { "LICENSE_KEY", licenceCode },//
		new String[] { "ACTIVATION_CODE", perpareCode },//
		new String[] { "ACTIVATION_KEY", activeCode } };

    }

}
