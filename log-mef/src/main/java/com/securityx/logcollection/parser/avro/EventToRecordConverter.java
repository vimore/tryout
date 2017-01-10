package com.securityx.logcollection.parser.avro;

import com.securityx.flume.log.avro.Event;
import com.securityx.logcollection.parser.utils.E8UUIDGenerator;
import com.securityx.model.mef.field.api.WebProxyMefField;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * @author jyrialhon
 */


public class EventToRecordConverter {
    private final E8UUIDGenerator uuidGenerator;

    public enum LogCollectionFields{
        category(WebProxyMefField.logCollectionCategory.getPrettyName()),
        time(WebProxyMefField.logCollectionTime.getPrettyName()),
        host(WebProxyMefField.logCollectionHost.getPrettyName()),
        message(WebProxyMefField.message.getPrettyName()),
        attachmentBody(Fields.ATTACHMENT_BODY),
        attachmentMimeType(Fields.ATTACHMENT_MIME_TYPE),
        uuid(WebProxyMefField.uuid.getPrettyName())

        ;

        private String field;
        LogCollectionFields(String field){
            this.field = field;
        }

        public String getPrettyName(){
            return this.field;
        }
    }

    public EventToRecordConverter(int nbRegion){
        uuidGenerator = new E8UUIDGenerator(nbRegion);
    }

    public  Record readOnlyAvroRecord(Event avroEvent) {
        Record out = new Record();
        //process header data
        Map<CharSequence, CharSequence> header = avroEvent.getHeaders();
        String category = null;
        String host = null;
        String hostname = null;
        String uuid = null;
        for (Map.Entry<CharSequence, CharSequence> att : header.entrySet()) {
            //System.out.println(att.getKey()+ " : "+ att.getValue());
            if (att.getKey().toString().equals("category")) {
                out.put(LogCollectionFields.category.getPrettyName(), att.getValue().toString());
                category = att.getValue().toString();
                continue;
            }
            if (att.getKey().toString().equals("timestamp")) {
                out.put(LogCollectionFields.time.getPrettyName(), att.getValue().toString());
                continue;
            }
            if (att.getKey().toString().equals("host")) {
                host = att.getValue().toString();
                continue;
            }
            if (att.getKey().toString().equals("hostname")) {
                hostname = att.getValue().toString();
                continue;
            }
            if (att.getKey().toString().equals("uuid")) {
                uuid = att.getValue().toString();
                continue;
            }
        }

        if (host != null) {
            out.put(LogCollectionFields.host.getPrettyName(), host);

        } else if (hostname != null) {
            out.put(LogCollectionFields.host.getPrettyName(), hostname);
        } else {
            out.put(LogCollectionFields.host.getPrettyName(), "_undefined_host");
        }
        String rawlog = new String(avroEvent.getBody().array());



        //process body
        // for packet : we need to provide data within the expected format
        String str;
        if ("packet".equals(category) || "tanium".equals(category) || "tanium_feed".equals(category)) {
            //cleanup with assumption it's a json or xml record
            str = new String(avroEvent.getBody().array());
            str = str.replaceAll("^[^\\{]+", "");

            out.put(LogCollectionFields.attachmentBody.getPrettyName(), new ByteArrayInputStream(str.getBytes()));//new ByteArrayInputStream(avroEvent.getBody().array()));// = "_attachment_body";
            out.put(LogCollectionFields.attachmentMimeType.getPrettyName(), "application/json"); // = "_attachment_mimetype";
            if (header.containsKey("taniumQuestion"))
                out.put("taniumQuestion", header.get("taniumQuestion")); // = "_attachment_mimetype";
        } else if ("flume_tanium".equals(category) || "ciscoise".equals(category)) {
            //cleanup with assumption it's a json or xml record
            str = new String(avroEvent.getBody().array());
            str = str.replaceAll("^[^<]+", "");

            out.put(LogCollectionFields.attachmentBody.getPrettyName(), new ByteArrayInputStream(str.getBytes()));//new ByteArrayInputStream(avroEvent.getBody().array()));// = "_attachment_body";
            out.put(LogCollectionFields.attachmentMimeType.getPrettyName(), "application/xml"); // = "_attachment_mimetype";
            if (header.containsKey("saved_question_name"))
                out.put("taniumQuestion", header.get("saved_question_name")); // = "_attachment_mimetype";
            if (header.containsKey("result_set_timestamp"))
                out.put("taniumTime", header.get("result_set_timestamp")); // = "_attachment_mimetype";
        } else { //default to log format with log in "message" field.
            str = new String(avroEvent.getBody().array()).replace("\u0000", "");
            out.put(LogCollectionFields.message.getPrettyName(), str);
            if (header.containsKey("taniumQuestion"))
                out.put("taniumQuestion", header.get("taniumQuestion"));
        }
        if (uuid == null){
            uuid = uuidGenerator.generateUUID(header, rawlog);
        }
        out.put(LogCollectionFields.uuid.getPrettyName(), uuid);
        return out;
    }

    private static E8UUIDGenerator _uuidGenerator;

    public static E8UUIDGenerator getUuidGenerator() {
        return getUuidGenerator(3);
    }
    public static E8UUIDGenerator getUuidGenerator(int nbRegions){
        if (_uuidGenerator == null){
            _uuidGenerator = new E8UUIDGenerator(nbRegions) ;
        }
        return  _uuidGenerator;
    }
}
