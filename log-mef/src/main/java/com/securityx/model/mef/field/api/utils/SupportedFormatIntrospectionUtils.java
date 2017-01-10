/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.model.mef.field.api.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * handle java side effect of avro serialization
 *
 * @author jyrialhon
 */
public class SupportedFormatIntrospectionUtils {

    /**
     * build record attribut name
     *
     * @param name
     * @return
     */
    public static String toObjAttribut(String name) {
        String out = "";
        String[] items = name.split("_");
        for (String item : items) {
            out += Character.toUpperCase(item.charAt(0)) + item.substring(1);
        }
        return out;
    }

    /**
     * Build Object Name based on Hierachie and current level, Hierachie is
     * derived from Field namespace
     *
     * @param objHierarchie
     * @param currentLevel
     * @return
     */
    public static String toObjectName(String prefix, String[] objHierarchie, int currentLevel) {
        String out = new String(prefix);
        for (int i = 0; i < currentLevel + 1; i++) {
            out += Character.toUpperCase(objHierarchie[i].charAt(0)) + objHierarchie[i].substring(1);
        }
        return out;
    }

    /**
     * Build object stack from manespace
     *
     * @param prefix
     * @param nameSpace
     * @return
     */
    public static Map<String, String> getStack(String prefix, String nameSpace) {
        Map<String, String> stack = new HashMap<String, String>();
        String[] objHierarchie = nameSpace.split("\\.");
        // generate setters
        String obj = null;
        for (int i = 0; i < objHierarchie.length; i++) {
            String setter = "set" + SupportedFormatIntrospectionUtils.toObjAttribut(objHierarchie[i]);
            stack.put(obj, setter);
            obj = SupportedFormatIntrospectionUtils.toObjectName(prefix, objHierarchie, i);
        }
        return stack;
    }

    /**
     * Generate Avro Object Class for namespace
     *
     * @param namespace
     * @param prefix
     * @return
     */
    public static String genAvroObjectClass(String namespace, String prefix) {
        String out = "";
        if (null != namespace) {
            String[] objHierarchie = namespace.split("\\.");
            for (String s : objHierarchie) {
                out += Character.toUpperCase(s.charAt(0)) + s.substring(1);
            }
        }
        if (null != prefix)
            return prefix + out;
        else
            return out;

    }

}
