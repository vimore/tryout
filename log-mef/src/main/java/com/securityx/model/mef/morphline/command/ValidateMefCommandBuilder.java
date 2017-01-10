package com.securityx.model.mef.morphline.command;

import com.securityx.model.mef.MorphlineRecordValidator;
import com.securityx.model.mef.field.api.ValidationLogger;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * 
 * validateMef  {
 *     failOnUnknownFields : "false"
 * }
 */
public class ValidateMefCommandBuilder implements CommandBuilder {
    
    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("validateMef");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        //System.out.println("config:" + config);
        boolean failOnUnknownFields = config.getBoolean("failOnUnknownFields");
        ValidationLogger validationLogger = new ValidationLogger();
        MorphlineRecordValidator morphlineRecordValidator = new MorphlineRecordValidator(failOnUnknownFields);
        return new ValidateMefCommand(config, context, parent, child, validationLogger, morphlineRecordValidator);
    }

    static class ValidateMefCommand implements Command {
        private final Logger logger = LoggerFactory.getLogger(ValidateMefCommand.class);
        private final Config config;
        private final Command parent;
        private final Command child;
        private final MorphlineContext context;
        private final ValidationLogger validationLogger;
        private final MorphlineRecordValidator morphlineRecordValidator;

        public ValidateMefCommand(Config config, MorphlineContext context,
                Command parent, Command child,
                ValidationLogger validationLogger,
                MorphlineRecordValidator morphlineRecordValidator) {
            this.config = config;
            this.parent = parent;
            this.child = child;
            this.context = context;
            this.validationLogger = validationLogger;
            this.morphlineRecordValidator = morphlineRecordValidator;
        }

        @Override
        public void notify(Record notification) {
          child.notify(notification);
        }

        @Override
        public boolean process(Record record) {
            boolean validate = morphlineRecordValidator.validate(validationLogger, record);
            if (validate) {
                if (child != null) {
                    return child.process(record);
                }
            }
            if(logger.isDebugEnabled())
                logger.debug("ERROR : Validation failure : record:"+record.toString());
            return validate;
        }

        @Override
        public Command getParent() {
            return parent;
        }
    }
}
