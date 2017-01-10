/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.mef.log.mapreduce;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author jyrialhon
 */
public class AvroToJsonUtil {
    public static String toAvroJsonString(Object value, Schema schema) throws IOException {
        try {
            final ByteArrayOutputStream jsonOutputStream = new ByteArrayOutputStream();
            final JsonEncoder jsonEncoder =
                    EncoderFactory.get().jsonEncoder(schema, jsonOutputStream);
            final GenericDatumWriter writer = new GenericDatumWriter(schema);
            writer.write(value, jsonEncoder);
            jsonEncoder.flush();
            return new String(jsonOutputStream.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException("Internal error: " + ioe);
        }
    }
}
