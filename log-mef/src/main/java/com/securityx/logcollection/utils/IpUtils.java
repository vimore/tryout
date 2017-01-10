/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.logcollection.utils;

import com.ibm.icu.text.IDNA;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jyrialhon
 */
public class IpUtils {

  public final static Pattern ip = Pattern.compile(
          "^(?i)(?:::ffff:)?(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
  private final static Pattern fqdn = Pattern.compile(
          "(^(?:(?!\\d+\\.|-)[a-zA-Z0-9_\\-]{1,63}(?<!-)\\.?)+(?:[a-zA-Z]{2,})$)", Pattern.CASE_INSENSITIVE);
  private final static Pattern firstdnsletter = Pattern.compile(
          "^[a-z0-9]", Pattern.CASE_INSENSITIVE);
  private final static Pattern dnsletters = Pattern.compile(
          "^[a-z0-9_-]+", Pattern.CASE_INSENSITIVE);
  private final static Pattern lasttoken = Pattern.compile(
          "^[a-z0-9]{2,}$", Pattern.CASE_INSENSITIVE);
  private final static IDNA idna = IDNA.getUTS46Instance(IDNA.USE_STD3_RULES);
  private static StringBuilder idnaBuffer =  new StringBuilder();

  public static boolean isValidIpv4Address(String value) {
    Matcher m = ip.matcher(value);
    if (m.matches()) {
      for (int i = 1; i <= 4; i++) {
        int a = Integer.parseInt(m.group(i));
        if (a > 255 || a < 0) {
          return false;
        }
        // enforce IP constraints : first and last byte can't be 0
        if (i % 3 == 1 && a == 0) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public static boolean isValidIpv6Address(String value) {
    String[] bytes = value.split(":");
    int nbelt = 0;
    boolean isValid = true;
    String ipv6 = "";
    int colapsed = 0;
    for (int i = 0; i < bytes.length; i++) {
      nbelt++;
      isValid = isValid && (bytes[i].length() <= 4);
      if (bytes[i].length() > 0) {
        isValid = isValid && isHex(bytes[i]);
      } else {
        colapsed++;
      }
    }
    isValid = isValid && (colapsed < 2);
    return isValid;
  }
  public static boolean isValidMacAddress(String value){
    if (value != null) {
      value = value.trim();
      String mac = value.replaceFirst("^0x", "");
      if (mac.length() == 12 && isHex(mac)) {
        return true;
      } else {
        if (mac.length() <= 17) { // : or - sep expected
          String[] hexa = mac.split("-|:");
          if (hexa.length != 6) {
            return false;  // expect 6 substrings
          }
          for (int i = 0; i < hexa.length; i++) {
            if (!isHex(hexa[i])) {
              return false;
            }
          }
          return true;
        } else {
          return false; //bad formatted mac
        }
      }
    }
    return false;
  }

  private static final boolean isHex(char c) {
    return (Character.isDigit(c)
            || Character.isWhitespace(c)
            || (("abcdefABCDEF".indexOf(c)) >= 0));
  }

  private static final boolean isHex(String str) {
    for (int i = 0; i < str.length(); i++) {
      if (!isHex(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  private static boolean isIDNACompliant(String value){
    IDNA.Info info = new IDNA.Info();
    idnaBuffer.delete(0, idnaBuffer.capacity());

    idnaBuffer = idna.nameToASCII(value, idnaBuffer, info);
    if (!info.hasErrors()){
      return true;
    }  else {
      return false;
    }
  }
  public static final boolean isFqdn(String value) {
    return isFqdn(value, false, null, null);
  }


    public static final boolean isFqdn(String value, boolean collect, StringBuilder hostNameBuf, StringBuilder dnsDomainBuf) {

    String[] dnsparts = value.toLowerCase().split("\\.");
    if (dnsparts.length >= 2){
      Matcher m = firstdnsletter.matcher(dnsparts[0]);
      if (!m.find())
        return false;
      int i=0;

      for(String part : dnsparts){
        m=dnsletters.matcher(part);
        if (! isIDNACompliant(part) && ! m.matches())
          return false;
        else if (collect) {
          if (i++ < 1)
            hostNameBuf.append(idnaBuffer.toString());
          else {
            dnsDomainBuf.append(idnaBuffer.toString());
            dnsDomainBuf.append(".");
          }
        }

      }
      m = lasttoken.matcher(dnsparts[dnsparts.length - 1]);
      if (! m.matches())
        return false;
      // clear last "." from dsnDomainBuf
      if (collect){
        dnsDomainBuf.setLength(Math.max(dnsDomainBuf.length() - 1, 0));
      }

      return true;
    }else
      return false;
  }

 /*public static void main(String[] args){
    
    String value = "jyria.domain.local";
    int dotIndex = value.indexOf('.');
    String host  = value.substring(0, dotIndex);
    String dns  = value.substring(dotIndex+1, value.length());
    System.out.println(host+" : "+dns);
  }*/
}
