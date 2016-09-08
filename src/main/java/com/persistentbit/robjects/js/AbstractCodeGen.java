package com.persistentbit.robjects.js;

import com.persistentbit.core.collections.PStream;
import com.persistentbit.sourcegen.SourceGen;

/**
 * @author Peter Muys
 * @since 6/09/2016
 */
public class AbstractCodeGen extends SourceGen{

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

    protected String toSimpleName(String name){
        int i = name.lastIndexOf('.');
        if(i >=0){
            name = name.substring(i+1);
        }
        return name.replace('$','_');
    }
    protected String firstCap(String name){
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
