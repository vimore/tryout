package com.securityx.modelfeature.common;

import com.google.common.base.Objects;

/**
 * Created by harish on 1/14/15.
 */
public class EntityInfo {


    String dateTime;
    String ipAddress;
    String macAddress;
    String hostName;
    String userName;

    public EntityInfo(String dateTime, String ipAddress, String macAddress, String hostName, String userName) {
        this.dateTime = dateTime;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.hostName = hostName;
        this.userName = userName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityInfo)) return false;

        EntityInfo that = (EntityInfo) o;

        if (hostName != null ? !hostName.equals(that.hostName) : that.hostName != null) return false;
        if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null) return false;
        if (macAddress != null ? !macAddress.equals(that.macAddress) : that.macAddress != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ipAddress != null ? ipAddress.hashCode() : 0;
        result = 31 * result + (macAddress != null ? macAddress.hashCode() : 0);
        result = 31 * result + (hostName != null ? hostName.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        return result;
    }
}
