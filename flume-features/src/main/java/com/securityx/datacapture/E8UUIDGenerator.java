package com.securityx.datacapture;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.apache.flume.Context;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class E8UUIDGenerator {
    private MessageDigest mDigest=null;
    private final Integer nbRegion;
    private int region;
    private UUID type1;
    private final String undef;
    private final TimeBasedGenerator uuidGenerator;


    protected E8UUIDGenerator(int nbRegion) {
        this.nbRegion = nbRegion;
        EthernetAddress addr = EthernetAddress.fromInterface();
        uuidGenerator = Generators.timeBasedGenerator(addr);
        try {
            mDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(E8UUIDGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        undef= md5("undef");

    }
    public String generateUUID(Map<String, String> headers, String rawlog) {

        type1 = uuidGenerator.generate();
        region = (int)( type1.timestamp() % nbRegion );


        return String.format("%s-%s-%s-%s", Integer.toHexString((int)region), type1.toString(),srcMd5(headers).subSequence(0, SRC_MD5_MAX_LENGTH),md5(rawlog));
    }
    public static final int SRC_MD5_MAX_LENGTH = 4;  private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    public static String toHex(byte[] data) {
        char[] chars = new char[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            chars[i * 2] = HEX_DIGITS[(data[i] >> 4) & 0xf];
            chars[i * 2 + 1] = HEX_DIGITS[data[i] & 0xf];
        }
        return new String(chars).toLowerCase();
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

    protected String srcMd5(Map<String, String> headers){
        // policy get the further information

        if (headers.containsKey("host")){
            // host comes from remote syslog sender
            return md5(headers.get("host"));
        }else if (headers.containsKey("hostname") && headers.containsKey("category")){
            // hostname comes from local flume-ng
            return md5(headers.get("hostname")+headers.get("category"));
        }else if (headers.containsKey("hostname")){
            return md5(headers.get("hostname"));
        }else {
            return undef;
        }
    }

    public static void main(String[] args){
    /*
        E8UUIDGenerator uuid = new E8UUIDGenerator(3);
        Map<String,String> header = new HashMap<String, String>();
        header.put("a", "A");
        for (int i=0;i<10;i++){
            String rawlog = String.format("string %d", i);
            System.out.println(uuid.generateUUID(header, rawlog));

        }
    */
    }
}
