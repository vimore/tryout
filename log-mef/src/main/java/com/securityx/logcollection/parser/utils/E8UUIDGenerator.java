package com.securityx.logcollection.parser.utils;


import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class E8UUIDGenerator {
    public static final int SRC_MD5_MAX_LENGTH = 4;
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private final Integer nbRegion;
    private final String undef;
    private final TimeBasedGenerator uuidGenerator;
    private MessageDigest mDigest = null;
    private int region;
    private UUID type1;
    private final Map<CharSequence,CharSequence> emptyHeader = new HashMap<CharSequence,CharSequence>(1);

    public E8UUIDGenerator(int nbRegion) {
        this.nbRegion = nbRegion;
        EthernetAddress addr = EthernetAddress.fromInterface();
        uuidGenerator = Generators.timeBasedGenerator(addr);
        try {
            mDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(E8UUIDGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        undef = md5("undef");

    }

    public static String toHex(byte[] data) {
        char[] chars = new char[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            chars[i * 2] = HEX_DIGITS[(data[i] >> 4) & 0xf];
            chars[i * 2 + 1] = HEX_DIGITS[data[i] & 0xf];
        }
        return new String(chars).toLowerCase();
    }

    public String generateUUID(Map<CharSequence, CharSequence> headers, String rawlog) {

        type1 = uuidGenerator.generate();
        region = (int) ( type1.timestamp() % nbRegion );
//        System.out.println(region);
        return String.format("%s-%s-%s-%s", Integer.toHexString(region), type1.toString(), srcMd5(headers).subSequence(0, SRC_MD5_MAX_LENGTH), md5(rawlog));
    }

    public String md5(String input) {
        mDigest.reset();
        byte[] result;
        try {
            result = mDigest.digest(input.getBytes("UTF-8"));
            return toHex(result);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(E8UUIDGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    protected String srcMd5(Map<CharSequence, CharSequence> headers) {
        // policy get the further information

        if (headers.containsKey("host")) {
            // host comes from remote syslog sender
            return md5(headers.get("host").toString());
        } else if (headers.containsKey("hostname") && headers.containsKey("category")) {
            // hostname comes from local flume-ng
            return md5(headers.get("hostname").toString() + headers.get("category").toString());
        } else if (headers.containsKey("hostname")) {
            return md5(headers.get("hostname").toString());
        } else {
            return undef;
        }
    }

    public static void main(String[] args){
        E8UUIDGenerator uuid = new E8UUIDGenerator(3);
        Map<CharSequence,CharSequence> header = new HashMap<CharSequence,CharSequence>();
        header.put("a", "A");
        for (int i=0;i<10;i++){
            String rawlog = String.format("string %d", i);
            System.out.println(uuid.generateUUID(header, rawlog));

        }

    }

    public String  generateUUIfromRawLogOnly(CharSequence rawLog) {
        return generateUUID(emptyHeader, rawLog.toString());
    }
}

