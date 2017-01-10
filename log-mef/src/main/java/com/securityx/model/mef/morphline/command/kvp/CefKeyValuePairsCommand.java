package com.securityx.model.mef.morphline.command.kvp;

import com.securityx.model.external.cef.CEFToMefMappings;
import com.securityx.model.mef.field.api.WebProxyMefField;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CefKeyValuePairsCommand implements Command {
  private final String fieldHeaderPattern;
  private Logger logger = LoggerFactory.getLogger(CefKeyValuePairsCommand.class);
  private final Config config;
  private final Command parent;
  private final Command child;
  private final MorphlineContext context;
  private final String inputFieldName;
  private final boolean trim;
  private ArrayList<Pattern> fieldsPattern;
  

  public CefKeyValuePairsCommand(Config config,
          MorphlineContext context,
          Command parent,
          Command child,
          String inputFieldName,
          String fieldHeaderPattern,
          Set<String> fields,
          boolean trim) {
    this.config = config;
    this.parent = parent;
    this.child = child;
    this.context = context;
    this.inputFieldName = inputFieldName;
    this.trim = trim;
    this.fieldHeaderPattern=fieldHeaderPattern;
    buildKeys(fields);

  }
  
  private void buildKeys(Set<String> fields){
    this.fieldsPattern = new ArrayList<Pattern>();
    if (fields.size()>0) {
      for (String field : fields) {
        fieldsPattern.add(Pattern.compile(this.fieldHeaderPattern+"(" + field + ")="));
      }
      logger.debug("fields list initialized : "+fields);
    }else{
      logger.error("cefKeyValueCommand init looks wrong empty field list provided");
    }
  }

  private void buildKeys(){
    this.fieldsPattern = new ArrayList<Pattern>();

    for (WebProxyMefField mef : WebProxyMefField.values()){
      fieldsPattern.add(Pattern.compile("(?:\\s+|^)("+mef.name()+")="));
    }
    for (CEFToMefMappings alias : CEFToMefMappings.values()){
      fieldsPattern.add(Pattern.compile("(?:\\s+|^)("+alias.getFieldName()+")="));
    }
  }

  @Override
  public void notify(Record notification) {
    child.notify(notification);
  }

  @Override
  public boolean process(Record record) {
    final StringBuilder sb = new StringBuilder();
    List got = record.get(inputFieldName);
    if (got == null || got.isEmpty()) {
      return false;
    }
    for (Object r : got) {
      final String str = r.toString();  
      HashMap<String,String> res = parse(str);
      for (String k : res.keySet()){
        record.put(k, (Object) res.get(k));
      }
    }
    return child.process(record);
  }
  
  private static class KvpKey implements Comparable<KvpKey>{
      private final String key;
      private final int start;
      private final int stop;
    public KvpKey(String k, int start, int stop){
      this.key = k;
      this.start = start;
      this.stop = stop;
    }

    public String getKey() {
      return key;
    }
      public int getStart() {
      return start;
    }

    public int getStop() {
      return stop;
    }

    @Override
    public int compareTo(KvpKey o) {
       //ascending order
       return this.start - o.getStart();
    }

} 

  public HashMap<String,String> parse(String str){
    HashMap<String, String> out= new HashMap<String,String>();
    ArrayList<KvpKey> keys = new ArrayList<KvpKey>();
    
    for( Pattern p : this.fieldsPattern){
        
        Matcher m  = p.matcher(str);
        if (m.find()){
          KvpKey k = new KvpKey(m.group(1),m.start(),m.end());
          keys.add(k);
          //logger.debug(p.toString());
        }

    }
    Collections.sort(keys);
    for (int i=0; i<keys.size()-1; i++){
        KvpKey current = keys.get(i);
        KvpKey next = keys.get(i+1);
        String value = str.substring(current.getStop(), next.getStart());
        out.put(current.getKey(),value);
        if (logger.isDebugEnabled())
          logger.debug("cefkvpPair: "+current.getKey()+" : "+value);
      }
      KvpKey last = keys.get(keys.size()-1);
      String value = str.substring(last.getStop());
      out.put(last.getKey(), value);
      return out;
    }

  @Override
  public Command getParent() {
    return parent;
  }

/*public static void main(String[] args){
  CefKeyValuePairsCommand c =  new CefKeyValuePairsCommand(null, null,null, null, null, false);
  c.parse("cs1Label=Compliancy_Policy_Name cs2Label=Compliancy_Policy_Subrule_Name cs3Label=Host_Compliancy_Status cs4Label=Compliancy_Event_Trigger cs1=Operating_System  cs2=Updatedcs3=yes cs4=CounterAct_Action dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000");
}*/

}


