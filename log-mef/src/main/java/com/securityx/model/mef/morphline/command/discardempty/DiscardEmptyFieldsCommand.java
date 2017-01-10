package com.securityx.model.mef.morphline.command.discardempty;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import java.util.ArrayList;
import java.util.List;

class DiscardEmptyFieldsCommand implements Command {

  private final Config config;
  private final Command parent;
  private final Command child;
  private final MorphlineContext context;
  

  private enum Precision{
    s(new Float(0.001)),
    ms(1),
    ns(1000);
    
    private float scale;
    private Precision(float scale){
      this.scale = scale;
    }
    public float getScale(){
      return this.scale;
    }
}

  public DiscardEmptyFieldsCommand(Config config,
          MorphlineContext context,
          Command parent,
          Command child) {
    this.config = config;
    this.parent = parent;
    this.child = child;
    this.context = context;
  }

  @Override
  public void notify(Record notification) {
    child.notify(notification);
  }

  @Override
  public boolean process(Record record) {
    List<String> toBeDeleted = new ArrayList();
    for(String key : record.getFields().keySet()){
      Object value = record.getFirstValue(key);
      boolean isValid = true;
      isValid = isValid && null != value;
      if (value instanceof String){
        String str = (String) value;
        isValid = isValid && str.length() > 0;
      }
      if (! isValid){
        toBeDeleted.add(key);
      }
    }
    for (String key : toBeDeleted){
      record.removeAll(key);
    }
    return child.process(record);
  }
  

  @Override
  public Command getParent() {
    return parent;
  }
  
  
}
