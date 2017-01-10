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
package com.securityx.model.mef.morphline.command;

import com.codahale.metrics.Meter;
import com.securityx.model.mef.MorphlineRecordSupportFormatValidator;
import com.securityx.model.mef.MorphlineRecordValidator;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.model.mef.field.api.ValidationLogger;
import com.securityx.model.mef.field.api.WebProxyMefField;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.*;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A validateMef command  runs Mef validator on record fields.
 *
 *  * validateSupportedFormatAlt  {
 *     failOnUnknownFields : "false"
 *     passThrough : false
 *     defaultFormat : "WebProxyDpi" # default if missing WebProxyWeb
 *
 * }

 */
public final class NewValidateSupportedFormatCommandBuilder implements CommandBuilder {

    public Collection<String> getNames() {
        return Collections.singletonList("validateSupportedFormatAlt");
    }

    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new NewValidateSupportedFormatCommand(this, config, parent, child, context);
    }


    ///////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    ///////////////////////////////////////////////////////////////////////////////
    private static final class NewValidateSupportedFormatCommand extends AbstractCommand {
        private final Logger logger = LoggerFactory.getLogger(NewValidateSupportedFormatCommand.class);

        private final boolean failOnUnknownFields;
        private final boolean passThrough;
        private final Meter validatedRecords;
        private final Meter failedRecord;
        private final ValidationLogger validationLogger;
        private final MorphlineRecordValidator morphlineRecordValidator;
        private final String defaultFormatStr;
        private final EnumMap<SupportedFormats, MorphlineRecordSupportFormatValidator> validators;
        private SupportedFormats defaultFormat;

        @SuppressWarnings("unchecked")
        public NewValidateSupportedFormatCommand(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            this.failOnUnknownFields = getConfigs().getBoolean(config, "failOnUnknownFields", true);
            this.passThrough = getConfigs().getBoolean(config, "passThrough", false);
            defaultFormatStr = getConfigs().getString(config, "defaultFormat", SupportedFormats.WebProxyMef.name());
            validationLogger = new ValidationLogger();
            morphlineRecordValidator = new MorphlineRecordValidator(failOnUnknownFields);
            validators = new EnumMap<SupportedFormats, MorphlineRecordSupportFormatValidator>(SupportedFormats.class);
            try{
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

            validateArguments();
            validatedRecords = getMeter("validatedRecords");
            failedRecord = getMeter("failedRecord");
        }

        @Override
        protected boolean doProcess(Record record) {

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

            if (status || this.passThrough) {
                if (! status) {
                        record.removeAll(WebProxyMefField.logSourceType.getPrettyName());
                        record.put(WebProxyMefField.logSourceType.getPrettyName(), "UnMatched-validation");
                    if(logger.isDebugEnabled())
                        logger.debug("ERROR : passthough Validation failure : record:" +  record.toString());

                }
                return getChild().process(record);
            }
            if(logger.isDebugEnabled())
                logger.debug("ERROR : Validation failure : record:" + record.toString());
            return status;
        }

    }

}