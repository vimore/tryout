/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.model.mef.field.api.utils;

import com.securityx.model.mef.field.api.SupportedFormat;
import com.securityx.model.mef.field.api.SupportedFormats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jyrialhon
 */
public class MefAvroSchemaGenerator {
    private Logger logger = LoggerFactory.getLogger(MefAvroSchemaGenerator.class);

    private String genIdent(int nb) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nb; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private String genRecord(String name) {
        return "record  " + name + " {\n";
    }

    private String genAttr(SupportedFormat supportedFormat) {
        String name = supportedFormat.getAvroName();
        if ("protocol".equals(name)) {
            name = "protocol_";
            logger.warn("protocol can not be used as attribut name, it's a kleyword, postponed by \"_\"");
        }
        if ("bytes".equals(name)) {
            name = "bytes_";
            logger.warn("bytes can not be used as attribut name, it's a kleyword, postponed by \"_\"");
        }
        if (supportedFormat.isNullable()) {

            DataFormat dataformat = DataFormat._integer.getDataFormat(supportedFormat.getArgClass());
            if (null == dataformat) {
                if (logger.isDebugEnabled())
                    logger.debug("ERROR : unsupported format ! : " + supportedFormat.getArgClass());
            }

            return "union { "
                    + dataformat.getAvroType()
                    + ", null } " + supportedFormat.getAvroName() + ";\n";
        } else {
            return DataFormat._integer.getDataFormat(supportedFormat.getArgClass()).getAvroType()
                    + " " + supportedFormat.getAvroName() + ";\n";
        }
    }

    private void process(String prefix, Map<String, Object> root, StringBuilder outSb, SchemaGenatorContext ctx) {
        //2 pass one for records, another for attributes
        if (logger.isDebugEnabled())
            logger.debug("INFO : prefix : " + prefix);
        StringBuilder typesSb = new StringBuilder();
        StringBuilder varSb = new StringBuilder();
        for (String key : root.keySet()) {
            Object current = root.get(key);
            if (current instanceof Map) {
                if (logger.isDebugEnabled())
                    logger.debug("INFO : before processing type : " + key);
                StringBuilder sb = new StringBuilder();
                String currentType = prefix + Character.toUpperCase(key.charAt(0)) + key.substring(1);
                process(currentType, (Map<String, Object>) current, sb, new SchemaGenatorContext(ctx.getIndentLevel()));
                varSb.append(genIdent(ctx.getIndentLevel() + 2));
                varSb.append("union { " + currentType + ", null } " + key + ";\n");
                typesSb.append(sb.toString());
                if (logger.isDebugEnabled())
                    logger.debug("INFO : after processing type : " + key);
            }
        }
        outSb.append(typesSb.toString());

        // args pass
        if (logger.isDebugEnabled())
            logger.debug("INFO : generating record for prefix " + prefix);
        outSb.append(genIdent(ctx.getIndentLevel()));
        outSb.append(genRecord(prefix));
        outSb.append(varSb.toString());
        for (String key : root.keySet()) {
            Object current = root.get(key);
            if (current instanceof SupportedFormat) {
                outSb.append(genIdent(ctx.getIndentLevel() + 2));
                outSb.append(genAttr((SupportedFormat) current));
            }
        }
        outSb.append(genIdent(ctx.getIndentLevel()));
        outSb.append("}\n\n");
        if (logger.isDebugEnabled()) {
            logger.debug("INFO : done");
            logger.debug("INFO " + outSb.toString());
        }
    }

    private static class SchemaGenatorContext {

        int indentLevel = 0;

        /**
         * set indentLevent
         *
         * @param indentLevel
         */
        public void setIndentLevel(int indentLevel) {
            this.indentLevel = indentLevel;
        }

        /**
         * get identLevel
         *
         * @return identLevel
         */
        public int getIndentLevel() {
            return indentLevel;
        }

        public SchemaGenatorContext() {
        }

        public SchemaGenatorContext(int indentLevel) {
            this.indentLevel = indentLevel;
        }

    }

    /**
     * attempt to create avro schema from java code
     *
     * @param args
     */
    private enum DataFormat {

        _string(CharSequence.class, "string"),
        _integer(Integer.class, "int"),
        _long(Long.class, "long");
        private final Map<Class, DataFormat> mappings = new HashMap<Class, DataFormat>();
        private Class objClass;
        private String avroType;

        private DataFormat(Class objClass, String avroType) {
            this.objClass = objClass;
            this.avroType = avroType;
        }

        public DataFormat getDataFormat(Class objClass) {
            if (mappings.isEmpty()) {
                initMappings();
            }
            return mappings.get(objClass);
        }

        public String getAvroType() {
            return this.avroType;
        }

        private void initMappings() {
            for (DataFormat f : values()) {
                mappings.put(f.objClass, f);
            }
        }
    }

    public void genSchema() throws IOException {
        for (SupportedFormats format : SupportedFormats.values()) {
            Collection<SupportedFormat> formatFields = format.getSupportedFormatFields();
            Map<String, Object> root = new HashMap<String, Object>();
            for (SupportedFormat field : formatFields) {
                for (SupportedFormat f : field.getSupportedFormatfields()) {
                    System.out.println(f.getNamespace() + "." + f.getPrettyName());
                    String namespace = f.getNamespace();
                    if (null == namespace) {
                        root.put(f.getAvroName(), f);
                    } else {
                        String[] nodes = namespace.split("\\.");
                        Map<String, Object> current = root;
                        for (String node : nodes) {
                            if (current.containsKey(node)) {
                                Object objNode = current.get(node);
                                if (objNode instanceof Map) {
                                    current = (Map<String, Object>) objNode;
                                } else if (objNode instanceof SupportedFormat) {
                                    //error it is a leaf
                                    System.out.println("ERROR : it is a leaf, supposed to be a map");
                                }
                            } else {
                                Map<String, Object> subnode = new HashMap<String, Object>();
                                current.put(node, subnode);
                                current = subnode;
                            }
                        }
                        current.put(f.getAvroName(), f);
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("@namespace(\"");
            sb.append(SupportedFormats.AVROOUTPACKAGE);
            sb.append("\")\n");
            sb.append("protocol " + format.name().toString() + "Protocol {\n");
            process(format.name(), root, sb, new SchemaGenatorContext(2));
            sb.append("}\n");
            File avroOut = new File("./src/main/avro/" + format.name().toString() + ".avdl.new");
            if (avroOut.exists()) {
                avroOut.delete();
            }
            FileWriter writer = new FileWriter(avroOut);
            writer.append(sb.toString());
            writer.close();
        }
    }

    public static void main(String[] args) throws IOException {


        MefAvroSchemaGenerator generator = new MefAvroSchemaGenerator();
        generator.genSchema();
    }

  /* for memory wrong attenpt data need to be split 
  
   public static void main(String[] args) {
   StringBuilder sb =  new StringBuilder();
   sb.append("{\n");
   sb.append("  \"namespace\": \"com.securityx.model.mef\",\n"
   + "  \"type\": \"record\",\n"
   + "  \"name\": \"MefExtended\",\n"
   + "  \"fields\": [\n");

   for (SupportedFormats format : SupportedFormats.values()) {
   sb.append("    {\n" +
   "      \"name\":\""+format.name()+"\",\n" +
   "      \"type\":{\n" +
   "      \"type\":\"record\",\n" +
   "      \"name\":\""+format.name()+"Format\",\n" +
   "      \"fields\":[\n");

   Collection<SupportedFormat> formatFields = format.getExternalFieldsToMefFields();
   for (SupportedFormat field : formatFields) {
   for (SupportedFormat f : field.getSupportedFormatfields()) {
   if (!f.isNullable()) {
   sb.append("        {\"name\": \"" + 
   f.getAvroName() + 
   "\", \"type\": \"" + DataFormat._integer.getDataFormat(f.getArgClass()).getAvroType() + "\"},\n");
   } else {
   sb.append("        {\"name\": \"" + f.getAvroName() + "\", \"type\": [\"null\",\"" + DataFormat._integer.getDataFormat(f.getArgClass()).getAvroType() + "\"], \"default\" : null},\n");
   }

   }
        
   }
   sb.delete(sb.length()-2, sb.length());
   sb.append("\n      ]\n"+
   "    }\n"+
   "   },\n"              );
   }
   sb.delete(sb.length()-2, sb.length());
   sb.append("\n  ]\n");
   sb.append("}");
   System.out.println(sb.toString());
   }
   */
}
