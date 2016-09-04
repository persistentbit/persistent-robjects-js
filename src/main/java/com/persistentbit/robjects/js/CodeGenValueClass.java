package com.persistentbit.robjects.js;

import com.persistentbit.core.Tuple2;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.jjson.mapping.description.JJPropertyDescription;
import com.persistentbit.jjson.mapping.description.JJTypeDescription;
import com.persistentbit.jjson.mapping.description.JJTypeSignature;
import com.persistentbit.sourcegen.SourceGen;

/**
 * Created by petermuys on 3/09/16.
 */
public class CodeGenValueClass extends SourceGen{
    private final JJTypeDescription td;
    private String className;
    public CodeGenValueClass(JJTypeDescription typeDescription){
        this.td = typeDescription;
    }

    public void generate() {
        className = toSimpleName(td.getTypeSignature().getJavaClassName());
        println("/*");
        println(" *   VALUE CLASS " + className);
        println("/*");

        bs("var " + className + " = function(" + td.getProperties().map(i -> i.getName()).plusAll(td.getTypeSignature().getGenerics().keys().map(k -> "toJson"+k)).toString(",") + ")");{
            bs("this._data = ");{
                printAsLines(
                        td.getProperties().map(p -> "_" + p.getName() + " : " + p.getName()).plusAll(
                                td.getTypeSignature().getGenerics().keys().map(g -> "_toJson" + g + ": toJson" + g)
                        )
                ,",");

            be("};");}
        be("};");}

        bs(className + ".fromJson = function(json)");{
            println("return new " + className + "(" +
                     td.getProperties().map(p -> propFromJson("json." + p.getName(),p.getName(),p.getTypeSignature())).toString(",")
                    + ");");
        be("};");}
        td.getProperties().forEach(p -> {
            bs("Object.defineProperty(" + className + ".prototype, " + p.getName() + ",");{
                println("get: function() { return this._data._" + p.getName() + "},");
                println("enumerable: true, configurable: true");
            be("});");}

        });
        td.getProperties().forEach(p -> {
            bs(className + ".prototype.with" + firstCap(p.getName()) + " = function(" + p.getName()+")");{
                println("return new " + className + "(" +
                        td.getProperties().map(pp -> (pp.getName().equals(p.getName()) ? "" : "this._data._") + pp.getName()).toString(",") + ");"
                );
            be("};");}
        });

        bs(className + ".prototype.json = function()");{
            bs("return ");{
                printAsLines(td.getProperties().map(p -> p.getName() + ": " + propToJson(p)),",");
            be("};");}
        be("};");}
    }

    private String propToJson(JJPropertyDescription prop){
        if(prop.getTypeSignature().getJsonType() != JJTypeSignature.JsonType.jsonObject){
            return "this._data._" + prop.getName();
        }
        return "valueObjectToJson(this._data._" + prop.getName() + ")";
    }

    private String propFromJson(String json,String name,JJTypeSignature sig){
        if(sig.getJsonType() != JJTypeSignature.JsonType.jsonObject){
            return "function(a) { return a; }";
        }



        String simpleName = toSimpleName(sig.getJavaClassName());
        if(td.getTypeSignature().getGenerics().keys().contains(simpleName)){
            return "this._data_.fromJson" + name;
        }

        if(simpleName.equalsIgnoreCase("object")){

            if(sig.getGenerics().isEmpty()){
                return "this._data_.fromJson" + name +"(" + json + ")";
            }
            Tuple2<String,JJTypeSignature> subGen = sig.getGenerics().head();
            return propFromJson(json,subGen._1,subGen._2);
        }
        String res = simpleName + ".fromJson(" + json ;
        if(sig.getGenerics().isEmpty() == false) {
            res += ",";
        }
        res += sig.getGenerics().map(t -> propFromJson(json,t._1, t._2)).toString(",");
        return res + ")";

    }



    private void printAsLines(PStream<?> items, String sep){
        int cnt = items.size();
        for(Object o  : items){
            cnt--;
            String s = cnt == 0? "" : sep;
            println(o.toString() + s);
        }
    }

    private String toSimpleName(String name){
        int i = name.lastIndexOf('.');
        if(i >=0){
            name = name.substring(i+1);
        }
        return name.replace('$','_');
    }
    private String firstCap(String name){
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
