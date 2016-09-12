package com.persistentbit.robjects.js;

import com.persistentbit.core.collections.PStream;
import com.persistentbit.jjson.mapping.description.JJClass;
import com.persistentbit.jjson.mapping.description.JJTypeSignature;
import com.persistentbit.sourcegen.SourceGen;

/**
 * @author Peter Muys
 * @since 6/09/2016
 */
public abstract class AbstractCodeGen extends SourceGen{
    protected JSCodeGenSettings settings;

    public AbstractCodeGen(JSCodeGenSettings settings){
        this.settings = settings;
    }

    protected  String prefixNotEmpty(PStream<String> stream, String value){
        if(stream.isEmpty()){
            return "";
        }
        return value + stream.toString(value);
    }

    protected void printAsLines(PStream<?> items, String sep){
        int cnt = items.size();
        for(Object o  : items){
            cnt--;
            String s = cnt == 0? "" : sep;
            println(o.toString() + s);
        }
    }
    protected String toJson(String value,JJTypeSignature sig){
        return "JSON.stringify(" + toJsonData(value,sig) +")";
    }
    protected String toJsonData(String value,JJTypeSignature sig){
        if(sig.getJsonType().isJsonPrimitive()){
            return value;
        } else if(sig.getJsonType().isJsonCollection()){
            if(sig.getJsonType() == JJTypeSignature.JsonType.jsonArray || sig.getJsonType() == JJTypeSignature.JsonType.jsonSet){
                return settings.getUtilsClassName() + ".arrayMap(" + value +  ", function(item) { return " + toJsonData("item",sig.getGenerics().values().head()) + "})";
            } else {
                //Map
                throw new RuntimeException("Not Yet");
            }

        } else if(sig.getJsonType() == JJTypeSignature.JsonType.jsonObject){
            if(sig.getGenerics().isEmpty()) {
                return value + ".jsonData()";
            }
            return settings.getUtilsClassName()+".getJsonData(" + value+ ")";
        }
        throw new RuntimeException("Should not happen: " + sig.getJsonType());
    }

    protected String toSimpleName(JJClass name){
        /*int i = name.lastIndexOf('.');
        if(i >=0){
            name = name.substring(i+1);
        }*/
        return name.getClassName().replace('$','_');
    }
    protected String firstCap(String name){
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
