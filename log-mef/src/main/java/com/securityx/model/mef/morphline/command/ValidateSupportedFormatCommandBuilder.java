package com.securityx.model.mef.morphline.command;

import com.securityx.model.mef.MorphlineRecordSupportFormatValidator;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.model.mef.field.api.ValidationLogger;
import com.securityx.model.mef.field.api.WebProxyMefField;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;

/**
 * run constraints on supported formats
 * validateSupportedFormat { 
 *   failOnUnknownFields : "false" 
 *   defaultFormat : "WebProxyDpi" # default is missing WebProxyWeb
 * }
 */
public class ValidateSupportedFormatCommandBuilder implements CommandBuilder {

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("validateSupportedFormat");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    //System.out.println("config:" + config);
    boolean failOnUnknownFields = config.getBoolean("failOnUnknownFields");
    
    String defaultFormatStr = SupportedFormats.WebProxyMef.name();
    if (config.hasPath("defaultFormat")){
      defaultFormatStr = config.getString("defaultFormat");
    }
    ValidationLogger validationLogger = new ValidationLogger();
    EnumMap<SupportedFormats, MorphlineRecordSupportFormatValidator> validators = new EnumMap<SupportedFormats, MorphlineRecordSupportFormatValidator>(SupportedFormats.class);
    SupportedFormats defaultFormat;
    try {
      // get the format
      defaultFormat = SupportedFormats.valueOf(defaultFormatStr);
    } catch (IllegalArgumentException e) {
      // defaulting to WebProxyMef

      defaultFormat = null;
    }
    if (defaultFormat == null){
      throw new IllegalArgumentException("invalid defaultFormat value: "+defaultFormatStr);
    }
    for (SupportedFormats format : SupportedFormats.values()) {
      MorphlineRecordSupportFormatValidator validator = new MorphlineRecordSupportFormatValidator(format, failOnUnknownFields);
      validators.put(format, validator);
    }

    return new ValidateSupportefFormatCommand(config, context, parent, child, validationLogger, defaultFormat, validators);
  }

  static class ValidateSupportefFormatCommand implements Command {

    private final Logger logger = LoggerFactory.getLogger(ValidateSupportefFormatCommand.class);
    private final Config config;
    private final Command parent;
    private final Command child;
    private final MorphlineContext context;
    private final ValidationLogger validationLogger;
    private final EnumMap<SupportedFormats, MorphlineRecordSupportFormatValidator> validators;
    private final SupportedFormats defaultFormat;

    public ValidateSupportefFormatCommand(Config config, MorphlineContext context,
            Command parent, Command child,
            ValidationLogger validationLogger,
            SupportedFormats defaultFormat,
            EnumMap<SupportedFormats, MorphlineRecordSupportFormatValidator> validators) {
      this.config = config;
      this.parent = parent;
      this.child = child;
      this.context = context;
      this.validationLogger = validationLogger;
      this.validators = validators;
      this.defaultFormat = defaultFormat;

    }

    @Override
    public void notify(Record notification) {
      child.notify(notification);
    }

    @Override
    public boolean process(Record record) {
      boolean status = true;
      String recordFormat;
      recordFormat = (String) record.getFirstValue(WebProxyMefField.logSourceType.getPrettyName());
      // either a SupportedFormat or Log-Mef
      SupportedFormats format=null;
      if (null == recordFormat){
          if(logger.isDebugEnabled())
            logger.debug("ERROR : "+WebProxyMefField.logSourceType.getPrettyName()+" with a null value for "+record.toString());
      }else try {
          format = SupportedFormats.valueOf(recordFormat);
          status = status & this.validators.get(format).validate(validationLogger, record);
        } catch (IllegalArgumentException ex) {
        // silent : do nothing
        }
      // if (format different from defaultformat
      if (format != this.defaultFormat)
        status = status & this.validators.get(SupportedFormats.WebProxyMef).validate(validationLogger, record);
      
      if (status) {
        if (child != null) {
          return child.process(record);
        }
      }
      if(logger.isDebugEnabled())
        logger.debug("ERROR : Validation failure : record:" + record.toString());
      return status;
    }

    @Override
    public Command getParent() {
      return parent;
    }
  }
}
