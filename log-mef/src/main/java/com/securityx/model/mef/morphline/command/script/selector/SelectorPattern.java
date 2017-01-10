package com.securityx.model.mef.morphline.command.script.selector;

import com.securityx.model.mef.field.api.WebProxyMefField;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SelectorPattern {
  final private Logger logger = LoggerFactory.getLogger(SelectorPattern.class);
  final String readFromFieldNamed;
  final Pattern regexPattern;
  final String writePatternToFieldNamed;
  final List<ScriptHarness> harnesses = new ArrayList<ScriptHarness>();
  final List<SelectorPattern> subSeletorPatterns = new ArrayList<SelectorPattern>();
  final SelectorPatternMode mode;

  public void setLazyProcessing(boolean lazyProcessing) {
    this.lazyProcessing = lazyProcessing;
  }

  private boolean lazyProcessing =true;

  public SelectorPattern(String readFromFieldNamed, Pattern regexPattern, String writePatternToFieldNamed, SelectorPatternMode mode) {
    this.readFromFieldNamed = readFromFieldNamed;
    this.regexPattern = regexPattern;
    this.writePatternToFieldNamed = writePatternToFieldNamed;
    this.mode=mode;
  }

  public void shutdown() {
    for (SelectorPattern p : subSeletorPatterns) {
      if (p != null) {
        p.shutdown();
      }
    }
    for (ScriptHarness s : harnesses) {
      if (s != null) {
        s.shutdown();
      }
    }
  }

  /**
   *
   * @param record
   * @return return record for true and null for false;
   */
  public SelectorPatternOutput process(Record record) {
    List<Record> results = null;
    SelectorPatternOutput output = null;
    Record result = null;
    boolean matched = false;
    boolean hasLogSourceType = false;

    if (record == null) {
      return null;
    }
    Object readValue = record.getFirstValue(readFromFieldNamed);
    if (readValue == null) {
      return null;
    }
    ParsingMetadata parsingMeta = null;
    if ( record.getFields().containsKey(ScriptSelectorCommand._PARSING_META)){
        parsingMeta = (ParsingMetadata)record.getFirstValue(ScriptSelectorCommand._PARSING_META);
      if (this.mode == SelectorPatternMode.greedy)
        readValue = parsingMeta.getParsableStr(readFromFieldNamed, readValue.toString());
    }
    if(logger.isDebugEnabled())
      logger.debug("SelectorPattern : " + this.regexPattern.toString() + " processing field: "+readFromFieldNamed+" with value: " + readValue);
    Matcher matcher = regexPattern.matcher(readValue.toString());

    if (matcher.find(0)) {
      if(logger.isDebugEnabled())
        logger.debug("SelectorPattern : " + this.regexPattern.toString() + " match");
      parsingMeta.setLastMatched(readFromFieldNamed, matcher.end());
      Record copy = record.copy();
      if (writePatternToFieldNamed != null) {
        copy.put(writePatternToFieldNamed, matcher.toMatchResult().group());
      }

      if (harnesses.size() > 0) {

        for (ScriptHarness harness : harnesses){
          if (writePatternToFieldNamed != null) {
            copy.put(writePatternToFieldNamed, matcher.toMatchResult().group());
          }
          results = harness.feed(copy);
          if (results != null && results.size() > 0 ) {
            matched = true;
            break; //message parsed : leave loop
          }
          if (matched)
            break;
        }
      }else{
        results = new ArrayList<Record>();
        results.add(record);
      }

      for (Record r : results) {
        if (this.lazyProcessing && r.getFields().keys().contains(WebProxyMefField.logSourceType.getPrettyName())){
          hasLogSourceType = true;
          break; //lazyProcessing leaves on first find.
        }
      }
      if (! hasLogSourceType){
        if (harnesses.isEmpty() || matched ) {
          int cpt = 0;
          for (Record r : results) {
            cpt++;
            if (logger.isDebugEnabled())
              logger.debug(String.format("SelectorPattern: apply sub pattern to record %d/%d", cpt, results.size()));
            if (subSeletorPatterns.size() > 0) {
              for (SelectorPattern subSeletorPattern : subSeletorPatterns) {
                Record subCopy = r.copy();
                if (writePatternToFieldNamed != null) {
                  subCopy.put(writePatternToFieldNamed, matcher.toMatchResult().group());
                }
                if (logger.isDebugEnabled())
                  logger.debug("subselector : " + subSeletorPattern.regexPattern.toString());
                SelectorPatternOutput out = subSeletorPattern.process(subCopy);
                if (out != null && out.isMatched()) {
                  output = out;
                  break;
                }
              }
            }
          }
        }
        if (output==null ){
          output = new SelectorPatternOutput(results, false);
        }

        return output;
      } else //not matched by morphline script
      {
        if (hasLogSourceType){
          return new SelectorPatternOutput(results, hasLogSourceType);
        } else{
          return  null;
        }
      }
    } else {
      if(logger.isDebugEnabled())
        logger.debug("SelectorPattern : " + this.regexPattern.toString() + " does not match ");
      //selector regex not matched
      return null;
    }
  }
  @Override
  public String toString(){
    return "SelectorPattern : "+this.regexPattern.toString();
  }
}
