/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.logcollection.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * a collection of utilities dealing with regexes
 * @author jyrialhon
 */
public class RegexUtils {
  
  public  static String biggestMatch(Pattern p, String str){
    StringBuilder sb =  new StringBuilder();
    String pattern = p.pattern();
    int maxMatched = 0;
    Matcher bestMatch = null;
    for (int i = 1; i <= pattern.length(); i++){
      boolean matched =  false;
      try {
        Pattern tmpPattern = Pattern.compile(pattern.substring(0, i));
        Matcher m = tmpPattern.matcher(str);
        if (m.find(0)){
          matched = true;
          bestMatch = m;
        }
      }catch (PatternSyntaxException e){
        //hide wrong patterns 
      }
      if (matched){
        maxMatched = i;
      }
    }
    sb.append("pattern : "+p.pattern()+"\n");
    sb.append("string : "+str+"\n");
    if (maxMatched < p.pattern().length())
      sb.append("truncated regex: "+p.pattern().substring(maxMatched+1, p.pattern().length()-1));
    sb.append("matching subpattern : '"+p.pattern().substring(0, maxMatched)+"'\n");
    if (bestMatch != null)  {
      sb.append("matched substring : '"+str.substring(0, bestMatch.end())+"'\n");
      if (maxMatched < p.pattern().length())
        sb.append("truncated regex: "+p.pattern().substring(maxMatched+1, p.pattern().length()-1)+"\n");
      sb.append("truncated string: "+str.substring(bestMatch.end(), str.length())+"\n"+"\n");

    }

    return sb.toString();
  }
  
}
