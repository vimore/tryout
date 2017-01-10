package com.securityx.model.mef.morphline.command.avro;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import java.util.*;

/**
 * Command that sanitizes record values that are unknown to cause Solr indexing failures
 */
public final class SanitizeSolrFieldValuesBuilder implements CommandBuilder {

    public Collection<String> getNames() {
        return Collections.singletonList("sanitizeSolrFieldValues");
    }

    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new SanitizeSolrFieldValues(this, config, parent, child, context);
    }


    ///////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    ///////////////////////////////////////////////////////////////////////////////
    private static final class SanitizeSolrFieldValues extends AbstractCommand {


        SanitizeSolrFieldValues(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            validateArguments();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected boolean doProcess(Record record) {

            Collection<Map.Entry> entries = new ArrayList<Map.Entry>(record.getFields().asMap().entrySet());
            Record out = new Record();
            for (Map.Entry<String, Collection<Object>> entry : entries) {
                String key = entry.getKey();
                List values = record.get(key);
                for(Object value : values){
                    if(value instanceof CharSequence){
                        out.put(key, stripNonCharCodepoints((CharSequence)value));
                    }else{
                        out.put(key, value);
                    }
                }
            }
            // pass record to next command in chain:
            return super.doProcess(out);

        }

    }
    /**
     * Strip all non-characters, which can cause SolrReducer problems if present.
     * This is borrowed from Apache Nutch.
     */
    private static String stripNonCharCodepoints(CharSequence input) {
        StringBuilder stripped = new StringBuilder(input.length());
        char ch;
        for (int i = 0; i < input.length(); i++) {
            ch = input.charAt(i);
            // Strip all non-characters http://unicode.org/cldr/utility/list-unicodeset.jsp?a=[:Noncharacter_Code_Point=True:]
            // and non-printable control characters except tabulator, new line and carriage return
            if (ch % 0x10000 != 0xffff && // 0xffff - 0x10ffff range step 0x10000
                    ch % 0x10000 != 0xfffe && // 0xfffe - 0x10fffe range
                    (ch <= 0xfdd0 || ch >= 0xfdef) && // 0xfdd0 - 0xfdef
                    (ch > 0x1F || ch == 0x9 || ch == 0xa || ch == 0xd)) {
                stripped.append(ch);
            }
        }
        return stripped.toString();
    }
}