package com.securityx.model.mef.morphline.command.kvp;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CefFormatCommand implements Command {
    private final String fieldHeaderPattern;
    private final Config config;
    private final Command parent;
    private final Command child;
    private final MorphlineContext context;
    private final String inputFieldName;
    private final boolean trim;
    private final Matcher fieldMatcher;
    private Logger logger = LoggerFactory.getLogger(CefFormatCommand.class);


    public CefFormatCommand(Config config,
                            MorphlineContext context,
                            Command parent,
                            Command child,
                            String inputFieldName,
                            String fieldHeaderPattern,
                            boolean trim) {
        this.config = config;
        this.parent = parent;
        this.child = child;
        this.context = context;
        this.inputFieldName = inputFieldName;
        this.trim = trim;
        this.fieldHeaderPattern = fieldHeaderPattern;
        // tested slower of about 6% against negative look behind
        //this.fieldMatcher = Pattern.compile("=([^\\\\][\\w\\.]+)" + this.fieldHeaderPattern).matcher("");
        this.fieldMatcher = Pattern.compile("=(?!\\\\)([\\w\\.]+)(?:" + this.fieldHeaderPattern+"|$)").matcher("");
        if (logger.isDebugEnabled())
            logger.debug("fieldMatcher init : "+this.fieldMatcher.pattern().toString());

    }


    @Override
    public void notify(Record notification) {
        child.notify(notification);
    }

    @Override
    public boolean process(Record record) {
        List got = record.get(inputFieldName);
        if (got == null || got.isEmpty()) {
            return false;
        }
        for (Object r : got) {
            final String str = r.toString();
            HashMap<String, String> res = parse(str);
            if (this.trim){
                for (Map.Entry<String, String> e : res.entrySet()){
                   e.setValue(e.getValue().trim());
                }
            }
            for (String k : res.keySet()) {
                record.put(k, (Object) res.get(k));
            }
        }
        return child.process(record);
    }

    /**
     * Look for cef key and split cef extension string into key value pairs.
     * the strategy to identify all keys is to look for
     * @param str
     * @return
     */
    public HashMap<String, String> parse(String str) {
        final StringBuilder sb = new StringBuilder();
        String rev = sb.append(str).reverse().toString();
        HashMap<String, String> out = new HashMap<String, String>();
        ArrayList<KvpKey> keys = new ArrayList<KvpKey>();
        //logger.debug(rev);

        this.fieldMatcher.reset(rev);
        while (fieldMatcher.find()) {
            KvpKey k = new KvpKey(str.substring(str.length() - fieldMatcher.end(1), str.length() - fieldMatcher.start(1)),
                    str.length() - fieldMatcher.end(1),
                    str.length() - fieldMatcher.start(1));
            keys.add(k);
            //logger.debug(k.toString());
        }

        Collections.sort(keys);
        for (int i = 0; i < keys.size() - 1; i++) {
            KvpKey current = keys.get(i);
            KvpKey next = keys.get(i + 1);
            String value = str.substring(current.getStop() + 1, next.getStart() - 1);
            out.put(current.getKey(), value);
            if (logger.isDebugEnabled())
                logger.debug("cefkvpPair: " + current.getKey() + " : '" + value + "'");
        }
        KvpKey last = keys.get(keys.size() - 1);
        String value = str.substring(last.getStop() + 1);
        if (logger.isDebugEnabled())
            logger.debug("cefkvpPair: " + last.getKey() + " : '" + value + "'");

        out.put(last.getKey(), value);

        return out;
    }

    @Override
    public Command getParent() {
        return parent;
    }

    private static class KvpKey implements Comparable<KvpKey> {
        private final String key;
        private final int start;
        private final int stop;

        public KvpKey(String k, int start, int stop) {
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

/*public static void main(String[] args){
  CefKeyValuePairsCommand c =  new CefKeyValuePairsCommand(null, null,null, null, null, false);
  c.parse("cs1Label=Compliancy_Policy_Name cs2Label=Compliancy_Policy_Subrule_Name cs3Label=Host_Compliancy_Status cs4Label=Compliancy_Event_Trigger cs1=Operating_System  cs2=Updatedcs3=yes cs4=CounterAct_Action dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000");
}*/


}




