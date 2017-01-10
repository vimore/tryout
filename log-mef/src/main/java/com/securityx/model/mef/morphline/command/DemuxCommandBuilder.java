package com.securityx.model.mef.morphline.command;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 *  demux {
 *    inputField: "splitable"
 *    outputField: "split"
 *    splitRegex: ", "
 *    replicate: true
 *    cleanUpInputField: true
 *  }
 */
public class DemuxCommandBuilder implements CommandBuilder {
    
    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("demux");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        //System.out.println("config:" + config);
        boolean replicate = config.getBoolean("replicate");
        boolean cleanUpInputField = config.getBoolean("cleanUpInputField");
        String inputField = config.getString("inputField");
        String outputField = config.getString("outputField");
        String splitRegex = config.getString("splitRegex");
        Pattern splitPattern = Pattern.compile(splitRegex);

        return new DemuxCommand(config, context, parent, child, inputField, outputField, splitPattern, replicate, cleanUpInputField);
    }

    static class DemuxCommand implements Command {
        private final Logger logger = LoggerFactory.getLogger(DemuxCommand.class);
        private final Config config;
        private final Command parent;
        private final Command child;
        private final MorphlineContext context;
        private final boolean replicate;
        private final boolean cleanup;
        private final String inputField;
        private final String outputField;
        private final Pattern splitPattern;
        private Record out = null;



        public DemuxCommand(Config config, MorphlineContext context,
                Command parent, Command child,
                String input, String output, Pattern splitPattern, boolean replicate, boolean cleanup) {
            this.config = config;
            this.parent = parent;
            this.child = child;
            this.context = context;
            this.cleanup = cleanup;
            this.replicate = replicate;
            this.inputField = input;
            this.outputField = output;
            this.splitPattern = splitPattern;

        }

        @Override
        public void notify(Record notification) {
          child.notify(notification);
        }

        @Override
        public boolean process(Record record) {

            if (record.getFields().containsKey(this.inputField)){
                List<String> values = record.get(this.inputField);
                boolean processed = true;
                for(String value : values){
                    String[] splits = this.splitPattern.split(value);
                    for (String split : splits) {
                        if (this.replicate) {
                            this.out = record.copy();
                            if (this.cleanup)
                                out.removeAll(this.inputField);
                        } else {
                            this.out = new Record();
                        }
                        out.put(this.outputField, split);
                        processed &= this.child.process(out);
                    }
                }
                return processed;
            }else{
                if(logger.isDebugEnabled())
                    logger.debug("ERROR : input field '"+this.inputField+"' does not exist in "+record.toString());
                 return false;
            }
        }

        @Override
        public Command getParent() {
            return parent;
        }
    }
}
