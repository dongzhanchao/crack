package com.crack.me.active;

public interface Crackable {
    public String[][] crackme(String uid, int licenceType, int licenceNumber, String systemId) throws Exception;

    public String getSystemId() throws Exception;
}
