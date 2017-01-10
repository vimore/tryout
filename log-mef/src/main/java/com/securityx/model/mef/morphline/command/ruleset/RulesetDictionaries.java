/*
 * Copyright 2013 Cloudera Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.securityx.model.mef.morphline.command.ruleset;

import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.MorphlineCompilationException;
import org.kitesdk.morphline.base.Configs;
import org.kitesdk.morphline.shaded.com.google.code.regexp.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Utility that parses and resolves a set of grok dictionaries ala logstash.
 */
final class RulesetDictionaries {

  /*
   * Uses a shaded version of com.google.code.regexp-0.2.3 to minimize potential dependency issues.
   * See https://github.com/tony19/named-regexp
   */
  
  private final Config config;
  private final Map<String, String> dictionary = new HashMap();
  
  private final Logger LOG = LoggerFactory.getLogger(RulesetDictionaries.class);
 
  public RulesetDictionaries(Config config, Configs configs) {
    this.config = config;
    try {
      for (String dictionaryFile : configs.getStringList(config, "dictionaryFiles", new ArrayList<String>())) {
        loadDictionaryFile(new File(dictionaryFile));
      }
      String dictionaryString = configs.getString(config, "dictionaryString", "");
      loadDictionary(new StringReader(dictionaryString));
    } catch (IOException e) {
      throw new MorphlineCompilationException("Cannot compile grok dictionary", config, e);
    }
    resolveDictionaryExpressions();    
  }
  
  public Pattern compileExpression(String expr) {
    expr = resolveExpression(expr);     
    //LOG.debug("expr after : {}", expr);    
    // TODO extract and replace conversions (?<queue_field:int>foo)    
    return Pattern.compile(expr);
  }
  
  private Config getConfig() {
    return config;
  }
  
  private void loadDictionaryFile(File fileOrDir) throws IOException {
    if (!fileOrDir.exists()) {
      throw new FileNotFoundException("File not found: " + fileOrDir);
    }
    if (!fileOrDir.canRead()) {
      throw new IOException("Insufficient permissions to read file: " + fileOrDir);
    }
    if (fileOrDir.isDirectory()) {
      File[] files = fileOrDir.listFiles();
      Arrays.sort(files);
      for (File file : files) {
        loadDictionaryFile(file);
      }
    } else {
      Reader reader = new InputStreamReader(new FileInputStream(fileOrDir), "UTF-8");
      try {
        loadDictionary(reader);
      } finally {
        try {
          reader.close();
        }catch (IOException e){
          LOG.error("error closing directory file", e);
        }
      }
    }      
  }
  
  private void loadDictionary(Reader reader) throws IOException {
    for (String line : CharStreams.readLines(reader)) {
      line = line.trim();
      if (line.length() == 0) {
        continue; // ignore empty lines
      }
      if (line.startsWith("#")) {
        continue; // ignore comment lines
      }
      int i = line.indexOf(" ");
      if (i < 0) {
        throw new MorphlineCompilationException("Dictionary entry line must contain a space to separate name and value: " + line, getConfig());
      }
      if (i == 0) {
        throw new MorphlineCompilationException("Dictionary entry line must contain a name: " + line, getConfig());
      }
      String name = line.substring(0, i);
      String value = line.substring(i + 1, line.length()).trim();
      if (value.length() == 0) {
        throw new MorphlineCompilationException("Dictionary entry line must contain a value: " + line, getConfig());
      }
      dictionary.put(name, value);
    }      
  }
  
  private void resolveDictionaryExpressions() {
    boolean wasModified = true;
    while (wasModified) {
      wasModified = false;
      for (Map.Entry<String, String> entry : dictionary.entrySet()) {
        String expr = entry.getValue();
        String resolvedExpr = resolveExpression(expr);        
        wasModified = (expr != resolvedExpr);
        if (wasModified) {
          entry.setValue(resolvedExpr);
          break;
        }
      }
    }
    LOG.debug("dictionary: {}", Joiner.on("\n").join(new TreeMap(dictionary).entrySet()));
    for (Map.Entry<String, String> entry : dictionary.entrySet()) {
      Pattern.compile(entry.getValue()); // validate syntax
    }
  }

  private String resolveExpression(String expr) {
    String PATTERN_START = "%{";
    String PATTERN_END= "}";
    char SEPARATOR = ':';
    while (true) {
      int i = expr.indexOf(PATTERN_START);
      if (i < 0) {
        break;
      }     
      int j = expr.indexOf(PATTERN_END, i + PATTERN_START.length());
      if (j < 0) {
        break;
      }     
      String grokPattern = expr.substring(i + PATTERN_START.length(),  j);
      //LOG.debug("grokPattern=" + grokPattern + ", entryValue=" + entryValue);
      int p = grokPattern.indexOf(SEPARATOR);
      String regexName = grokPattern;
      String groupName = null;
      String conversion = null; // FIXME
      if (p >= 0) {
        regexName = grokPattern.substring(0, p);
        groupName = grokPattern.substring(p+1, grokPattern.length());
        int q = groupName.indexOf(SEPARATOR);
        if (q >= 0) {
          conversion = groupName.substring(q+1, groupName.length());
          groupName = groupName.substring(0, q);
        }
      }
      //LOG.debug("patternName=" + patternName + ", groupName=" + groupName + ", conversion=" + conversion);
      String refValue = dictionary.get(regexName);
      if (refValue == null) {
        throw new MorphlineCompilationException("Missing value for name: " + regexName, getConfig());
      }
      if (refValue.contains(PATTERN_START)) {
        break; // not a literal value; defer resolution until next iteration
      }
      String replacement = refValue;
      if (groupName != null) { // named capturing group
        replacement = "(?<" + groupName + ">" + refValue + ")";
      }
      expr = new StringBuilder(expr).replace(i, j + PATTERN_END.length(), replacement).toString();
    }
    return expr;
  }
  
}
