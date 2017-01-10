package com.securityx.model.mef.morphline.command;

import com.securityx.model.external.ExternalFieldsToWebProxyMefFields;
import com.securityx.model.external.ExternalFormatToWebProxyMefFormat;
import com.securityx.model.mef.field.api.WebProxyMefField;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * convert ExternalFormatToMefFormat 
 * webProxyMefFormat { 
 *  formatIdFromFieldName : "formatId" #optional default is formatId
 *  discardUnmappableFields : "false"  #optional default is "false"
 *  discardOriginalMappedFields "true" #optional default is "true" }
 */
public class ExternalFormatToWebProxyMefFormatCommandBuilder implements CommandBuilder {

  private static final String commandName = "webProxyMefFormat";

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList(commandName);
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    //System.out.println("config:" + config);
    String formatIdFromFieldName = config.getString("formatIdFromFieldName");
    if (formatIdFromFieldName == null) {
      formatIdFromFieldName = "formatId";
    }
    boolean discardUnmappableFields = false;
    ConfigValue value = config.getValue("discardUnmappableFields");
    if (value != null) {
      discardUnmappableFields = config.getBoolean("discardUnmappableFields");
    }
    boolean discardOriginalMappedFields = true;
    value = config.getValue("discardOriginalMappedFields");
    if (value != null) {
      discardOriginalMappedFields = config.getBoolean("discardUnmappableFields");
    }
    return new ExternalFormatToMefFormatCommand(config, context, parent, child,
            formatIdFromFieldName, discardUnmappableFields, discardOriginalMappedFields);
  }

  static class ExternalFormatToMefFormatCommand implements Command {

    private final Logger logger = LoggerFactory.getLogger(ExternalFormatToMefFormatCommand.class);

    private final Config config;
    private final Command parent;
    private final Command child;
    private final MorphlineContext context;
    private final String formatIdFromFieldName;
    private final boolean discardUnmappableFields;
    private final boolean discardOriginalMappedFields;

    public ExternalFormatToMefFormatCommand(Config config, MorphlineContext context,
            Command parent, Command child, String formatIdFromFieldName,
            boolean discardUnmappableFields, boolean discardOriginalMappedFields) {
      this.config = config;
      this.parent = parent;
      this.child = child;
      this.context = context;
      this.formatIdFromFieldName = formatIdFromFieldName;
      this.discardUnmappableFields = discardUnmappableFields;
      this.discardOriginalMappedFields = discardOriginalMappedFields;

    }

    @Override
    public void notify(Record notification) {
      child.notify(notification);
    }

    @Override
    public boolean process(Record record) {

      ExternalFormatToWebProxyMefFormat externalFormatToMefFormat = getFormat(record);
      if (externalFormatToMefFormat == null) {
          if(logger.isDebugEnabled())
             logger.debug("ERROR : externalFormatToMefFormat is null");
        return false;
      } else {
        Set<String> mappableFields = new HashSet<String>();
        Set<String> originalMappedFields = new HashSet<String>();
        for (ExternalFieldsToWebProxyMefFields externalFieldsToMefFields : externalFormatToMefFormat.getExternalFieldsToMefFields()) {
          for (String externalFieldName : externalFieldsToMefFields.getExternalFieldNames()) {
            mappableFields.add(externalFieldName);

            List got = record.get(externalFieldName);
            if (got != null && ! got.isEmpty()) {
              originalMappedFields.add(externalFieldName);
              WebProxyMefField mefField = externalFieldsToMefFields.getMefField(externalFieldName);
              if (mefField != null) {
                if (logger.isDebugEnabled()) {
                  logger.debug("converting " + externalFormatToMefFormat.name() + "." + externalFieldName+ " to "+mefField.getPrettyName());
                }

                for (Object o : got) {
                  record.put(mefField.name(), o);
                }
              }else{
                  if(logger.isDebugEnabled())
                     logger.debug("Meffield is null");
              }
            }
          }
        }
        // remove logSourceType and replace par output format
        record.removeAll(formatIdFromFieldName);
        record.put(formatIdFromFieldName, "WebProxyMef");
        // add externalLogSourceType
        record.put("externalLogSourceType", externalFormatToMefFormat.name());

        if (discardUnmappableFields ||discardOriginalMappedFields){
          for (String key : record.getFields().keySet()) {
            if (discardUnmappableFields && !mappableFields.contains(key)) {
              record.removeAll(key);
            }
            if (discardOriginalMappedFields && originalMappedFields.contains(key)) {
              record.removeAll(key);
            }
          }
        }
        return this.child.process(record);
      }

    }

    @Override
    public Command getParent() {
      return parent;
    }

    private ExternalFormatToWebProxyMefFormat getFormat(Record record) {
      if (record == null) {
        return null;
      }
      List got = record.get(formatIdFromFieldName);
      if (got == null) {
        context.getExceptionHandler().handleException(new IllegalStateException("command:'" + commandName
                + "' expects to find a field named '" + formatIdFromFieldName + "'."), record);
        return null;
      }
      if (got.size() == 1) {
        String externalFormatId = got.get(0).toString();
        try {
          return ExternalFormatToWebProxyMefFormat.valueOf(externalFormatId);
        } catch (IllegalArgumentException iae) {
          context.getExceptionHandler().handleException(iae, record);
          return null;
        }
      } else {
          if(logger.isDebugEnabled())
              logger.debug("ERROR : external conversion issue with record:" + record.toString());
        context.getExceptionHandler().handleException(new IllegalStateException("command:'" + commandName
                + "' expects to find a field named '" + formatIdFromFieldName + "' with only 1 entry but had " + got.size() + " entried."), record);
        return null;
      }
    }
  }
}
