package com.securityx.model.mef.morphline.command.ruleset;

import com.securityx.logcollection.utils.RegexUtils;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Configs;
import org.kitesdk.morphline.shaded.com.google.code.regexp.GroupInfo;
import org.kitesdk.morphline.shaded.com.google.code.regexp.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


 final class Node{
    private Logger logger = LoggerFactory.getLogger(Node.class);
    private final String regex;
    private final Matcher matcher;
    private ArrayList<Node> children= new ArrayList<Node>();
    private final boolean addEmptyStrings;
    private int nodeId = 0;
    private static int cpt = 0;
    private final boolean debugMode;
    
    public static Node NewInstance(Config config, Configs configs, boolean addEmptyStrings, boolean debugMode){
      Node n = new Node(config, configs, addEmptyStrings, debugMode);
      return n;
    }
    public static void reset(){
      cpt=0;
    }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public int getNodeId() {
    return nodeId;
  }

    public Node(Config config, Configs configs, boolean addEmptyStrings, boolean debugMode) {
      this.debugMode=debugMode;
      nodeId=cpt++;
      this.addEmptyStrings = addEmptyStrings;
      regex = configs.getString(config, "expr", "");
      RulesetDictionaries dict = new RulesetDictionaries(config, configs);
      matcher = dict.compileExpression("^"+regex).matcher("");
      List<? extends Config> nodeList = config.getConfigList("children");
      for (Config c : nodeList){
        Node n = Node.NewInstance(c, configs, addEmptyStrings, debugMode);
        children.add(n);
      }
      
    }
    
    protected boolean doProcess(String msg, Record outputRecord ) {
      matcher.reset(msg);
      if(logger.isDebugEnabled())
        logger.debug("regex : "+this.regex);
        logger.debug("msg : '"+msg+"'");
        logger.debug("pattern : "+this.matcher.standardPattern().toString());
     
      if (matcher.find()){
        if(logger.isDebugEnabled())
          logger.debug("match");
        outputRecord.put("_nodes", nodeId);
        extractFast(outputRecord, matcher);
        if (this.children.size()>0){ //has children... going down
          Iterator<Node> nodes = this.children.iterator();
          String remainingToMatch = doTrim(msg, matcher);
          boolean match = false;
          while(nodes.hasNext() && ! match){
            Node n = nodes.next();
            if (n.doProcess(remainingToMatch, outputRecord)){
              match = true;
            }
          }
          return match;
        }else{
          return true;
        }
      }else{
        if (this.debugMode){
            if(logger.isDebugEnabled())
              logger.debug("ERROR : "+RegexUtils.biggestMatch(this.matcher.standardPattern(), msg));
        }
      }
      if (logger.isDebugEnabled())
        logger.debug(this.matcher.standardPattern().toString()+" does not match "+msg);
      return false;
    }
    
    private String doTrim(String str, Matcher m){
      return str.substring(m.end());
    }
    private void extractFast(Record outputRecord, Matcher matcher) {
      for (Map.Entry<String, List<GroupInfo>> entry : matcher.namedPattern().groupInfo().entrySet()) {
        String groupName = entry.getKey();
        List<GroupInfo> list = entry.getValue();
        int idx = list.get(0).groupIndex();
        int group = idx > -1 ? idx + 1 : -1; // TODO cache that number (perf)?
        String value = matcher.group(group);
        if (value != null && (value.length() > 0 || addEmptyStrings)) {
          outputRecord.put(groupName, value);
        }
      }
    }

    

    private boolean doMatch(Record inputRecord, Record outputRecord, boolean doExtract) {
      return false;
    }

    private void extract(Record outputRecord, Matcher matcher, boolean doExtract) {
      if (doExtract) {
        extractFast(outputRecord, matcher);
      }
    }
    
    public String toString(){
      return String.valueOf(nodeId);
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    ///////////////////////////////////////////////////////////////////////////////
    private static enum NumRequiredMatches {
      atLeastOnce,
      once,
      all     
    }     

  }
  

