package com.securityx.mef.log.mapreduce.logutils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.api.util.Strings;

import com.google.common.collect.Maps;

public class LogSampler {

    private int minMd5AsInt = Integer.MAX_VALUE;
    private int maxMd5AsInt = 0;
    private MessageDigest digest;
    private ByteBuffer buff;
    private final Map<String,Matcher> matchers;
    private final Set<String> fields;

    private final boolean doSampling;
    private final boolean doFiltering;

    public LogSampler(String fieldRegexPairs, int minMd5AsInt, int maxMd5AsInt) {
        matchers = Maps.newLinkedHashMap();
        if (!Strings.isEmpty(fieldRegexPairs)) {
          for (String regexPair : fieldRegexPairs.split("(?<!\\\\)\\,")) {
            if (!Strings.isEmpty(regexPair)) {
              String[] patternParts = regexPair.split(":", 2);
              if (patternParts.length == 2) {
                System.err.println("logsample pattern [" + patternParts[0]+ " -> "+ patternParts[1] + "]");
                matchers.put(patternParts[0], Pattern.compile(patternParts[1]).matcher(""));
              }
            }
          }
        }
        this.minMd5AsInt = minMd5AsInt;
        this.maxMd5AsInt = maxMd5AsInt;
        try {
            this.digest = MessageDigest.getInstance("md5");
        } catch(NoSuchAlgorithmException e) {
            System.err.println(e);
            //let's force values to set doSampling to false.
            this.minMd5AsInt = Integer.MAX_VALUE;
            this.maxMd5AsInt = 0;
        }
        assert(matchers != null);
        this.fields = this.matchers.keySet();
        this.doSampling = (this.minMd5AsInt <= this.maxMd5AsInt); // the == will filter out everything
        this.doFiltering = (this.minMd5AsInt == 0 && this.maxMd5AsInt == 0);
    }

    public boolean withinBounds(String valueStr) {
        if (this.minMd5AsInt < this.maxMd5AsInt) {
            int hash = this.md5AsInt(valueStr);
            return hash >= this.minMd5AsInt && hash < this.maxMd5AsInt;
        }
        return false;
    }

  /**
   * Compute if we need to process this record
   * 
   * If the record or field is null, we drop the record. If the record or field
   * is empty, we drop the record in filtering mode and keep otherwise.
   * Alternatively, the field has to match the pattern and to be within the
   * bounds for the md5 hash.
   * 
   * @param r
   *          - parser output
   * @return boolean - whether we want to keep the record
   */
    public boolean needProcess(Map<String, List<Object>> r) {
      if (r == null)
        return false;
      if (!this.fields.isEmpty()) {
          for (String field : fields) {
             if (r.keySet().contains(field)) {
                 String value = String.valueOf(r.get(field).get(0));
                 if (value == null)
                     return false;
                 if (value.isEmpty())
                     return !doFiltering;
                 if (this.matchers.get(field).reset(value).find()) {
                     return doFiltering || !doSampling || withinBounds(value);
                 } else {
                     return false;
                 }
             }
          }
      }
      return !doFiltering;
    }

  /**
   * Return the first 4 bytes of a hash of the value
   * 
   * The actual range is 0 to 32,768
   * 
   * @param str
   *          - String
   * @return int - First 31 bits of the md5 hash [0, 32768)
   */
    private int md5AsInt(String str){
        this.digest.update(str.getBytes(), 0, str.length());
        buff = ByteBuffer.wrap(this.digest.digest());
        int out = (0x7FFF & this.buff.getInt());
        buff.clear();
        return out;
    }
}
