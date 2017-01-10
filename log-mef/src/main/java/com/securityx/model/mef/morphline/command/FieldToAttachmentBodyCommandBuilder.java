package com.securityx.model.mef.morphline.command;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * toAttachmentBody  {
 *     inputField: "somefield"
 *     attachmentMimeType: "application/json"
 * }
 */
public class FieldToAttachmentBodyCommandBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("toAttachmentBody");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        //System.out.println("config:" + config);
        String inputField = config.getString("inputField");
        String attachmentMineType = config.getString("attachmentMimeType");

        return new FieldToAttachmentBodyCommand(config, context, parent, child, inputField, attachmentMineType);
    }

    static class FieldToAttachmentBodyCommand implements Command {
        private final Logger logger = LoggerFactory.getLogger(FieldToAttachmentBodyCommand.class);
        private final Config config;
        private final Command parent;
        private final Command child;
        private final MorphlineContext context;
        private final String inputField;
        private final String attachmentMineType;


        public FieldToAttachmentBodyCommand(Config config, MorphlineContext context,
                                            Command parent, Command child,
                                            String inputField, String attachmentMineType) {
            this.config = config;
            this.parent = parent;
            this.child = child;
            this.context = context;
            this.inputField = inputField;
            this.attachmentMineType = attachmentMineType;
        }

        @Override
        public void notify(Record notification) {
            child.notify(notification);
        }

        @Override
        public boolean process(Record record) {
            String inputValue = (String) record.getFirstValue(this.inputField);
            if (null != inputValue) {
                record.removeAll(Fields.ATTACHMENT_BODY);
                record.removeAll(Fields.ATTACHMENT_MIME_TYPE);
                record.put(Fields.ATTACHMENT_BODY, new ByteArrayInputStream(inputValue.getBytes()));
                record.put(Fields.ATTACHMENT_MIME_TYPE, this.attachmentMineType);

                return child.process(record);
            }else{
                logger.error("FieldToAttachmentBodyCommand: "+this.inputField+" value is null");
                return false;
            }
        }

        @Override
        public Command getParent() {
            return parent;
        }
    }
}
