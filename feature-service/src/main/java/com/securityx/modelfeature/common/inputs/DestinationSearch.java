package com.securityx.modelfeature.common.inputs;

import com.sun.istack.NotNull;

/**
 * Class to represent the JsonInput to the apis in the form:
 * {
 *     dateTime:"2014-12-09T03:57:31.000Z",
 *     url:"http://www.yahoohoo.com/agehv",
 *     ips:"192.16.170.44",
 *     domains:"www.yahoohoo.com"
 *
 * }
 *
 * Created by harish on 12/15/14.
 */
public class DestinationSearch {

    @NotNull
    String dateTime;

    int lastNDays = 30;

    String urls;

    String domains;

    String ips;

    String sourceAddress;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getDomains() {
        return domains;
    }

    public String getIps() {
        return ips;
    }

    public int getLastNDays() {
        return lastNDays;
    }

    public void setLastNDays(int lastNDays) {
        this.lastNDays = lastNDays;
    }

    public void setDomains(String domains) {
        this.domains = domains;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
}
