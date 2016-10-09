package com.dut.note.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class Converter {
    private static final String ARRAY_SEPARATOR = ";";

    public static String listToString(List<Object> list){
        StringBuilder builder = new StringBuilder();

        if (list == null) {
            return "";
        }

        for(Object obj : list){
            builder.append(obj.toString());
            builder.append(ARRAY_SEPARATOR);
        }
        return builder.toString();
    }

    public static String listStringToString(List<String> list) {
        StringBuilder builder = new StringBuilder();

        if (list == null) {
            return "";
        }

        for(String str : list){
            builder.append(str);
            builder.append(ARRAY_SEPARATOR);
        }
        return builder.toString();
    }

    public static List<String> stringToList(String str){
        List<String> list = new ArrayList();
        if (str == null) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(str, ARRAY_SEPARATOR);
        while(tokenizer.hasMoreTokens()){
            list.add(tokenizer.nextToken());
        }
        return list;
    }

    public static String toCondition(Object obj) {
        if (obj != null) {
            return "'%" + obj.toString() + "%'";
        } else {
            return "'%'";
        }
    }

    public static String toParam(Object obj) {
        if (obj != null)  {
            return "'" + obj.toString().replace("'", "''") + "'";
        } else {
            return "''";
        }
    }

    public static String toParam(List listObj) {
        String ret = listToString(listObj);
        return ret.isEmpty() ? "''" : "'" + ret.replace("'", "''") + "'";
    }

}
