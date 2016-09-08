package com.persistentbit.robjects.js;

import com.persistentbit.core.Tuple2;
import com.persistentbit.jjson.mapping.JJMapper;
import com.persistentbit.jjson.mapping.description.JJTypeDescription;
import com.persistentbit.jjson.mapping.description.JJTypeSignature;
import com.persistentbit.sourcegen.SourceGen;



/**
 * Created by petermuys on 3/09/16.
 */
public class CodeGenValueClass{

    public CodeGenValueClass(){

    }

    public SourceGen generate(JJTypeDescription td){
        Generator g = new Generator(td);
        g.generate();
        return g;
    }
    public SourceGen    generate(Class<?> valueClass){
        return generate(valueClass,new JJMapper());
    }
    public SourceGen    generate(Class<?> valueClass, JJMapper mapper){
        JJTypeDescription td = mapper.describe(valueClass);
        return generate(td);
    }

    class Generator  extends AbstractCodeGen{
        private JJTypeDescription td;
        private String className;

        private Generator(JJTypeDescription td){
            this.td = td;
            this.className = toSimpleName(td.getTypeSignature().getJavaClassName());
        }


        public void generate() {
            className = toSimpleName(td.getTypeSignature().getJavaClassName());
            println("/*");
            println(" *   VALUE CLASS " + className);
            println("*/");

            bs("var " + className + " = function(" + td.getProperties().map(i -> i.getName()).toString(",") + ")");{
                bs("this._data = ");{
                    printAsLines(
                            td.getProperties().map(p -> "_" + p.getName() + " : " + p.getName())
                            ,",");

                    be("};");}
                be("};");}


            bs(className + ".fromJson = function(json" + prefixNotEmpty(td.getTypeSignature().getGenerics().keys().map(n -> "fromJson" +n ),",") + ")");{
                println("return new " + className + "(" +
                        td.getProperties().map(p -> propFromJson(true,"json." + p.getName(),p.getName(),p.getTypeSignature())).toString(",")
                        + ");");
                be("};");}
            td.getProperties().forEach(p -> {
                bs("Object.defineProperty(" + className + ".prototype, " + str(p.getName()) + ",");{
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

            bs(className + ".prototype.json = function("+ td.getTypeSignature().getGenerics().keys().map(g -> "toJson" + g ).toString(",") + ")");{
                bs("return ");{
                    printAsLines(td.getProperties().map(p -> p.getName() + ": " + propToJson(p.getName(),p.getTypeSignature())),",");
                    be("};");}
                be("};");}
        }

        private String propToJson(String name,JJTypeSignature sig){
            JJTypeSignature.JsonType jt = sig.getJsonType();
            String value = "this._data._" + name;
            if(jt.isJsonPrimitive()){
                return value;
            }
            if(jt.isJsonCollection()){
                throw new RuntimeException("Not Yet");
            }

            String generics = sig.getGenerics().map(tgt -> toJsonGenerics(tgt._1,tgt._2)).toString(",");

            return value + ".json(" + generics + ")";

        }
        private String toJsonGenerics(String name,JJTypeSignature sig){
            JJTypeSignature.JsonType jt = sig.getJsonType();
            if(jt.isJsonPrimitive()){
                return "function(v) { return v; }";
            }
            if(jt.isJsonCollection()){
                throw new RuntimeException("Not Yet");
            }
            String simpleName = toSimpleName(sig.getJavaClassName());
            if(sig.getGenerics().isEmpty()){
                return "toJson" + name;
            }
            String allGenerics =sig.getGenerics().map(gt-> toJsonGenerics(gt._1,gt._2)).toString(",");
            if(sig.getGenerics().size() == 1){
                return allGenerics;
            }
            return "function(v) { v.json(" + allGenerics + ")";

        }

        private String propFromJson(boolean asValue,String json,String name,JJTypeSignature sig){
            if(sig.getJsonType() != JJTypeSignature.JsonType.jsonObject){
                if(asValue){
                    return json;
                }
                return "function(a) { return a; }";

            }



            String simpleName = toSimpleName(sig.getJavaClassName());
            if(td.getTypeSignature().getGenerics().keys().contains(simpleName)){
                return "this._data_.fromJson" + name;
            }

            if(simpleName.equalsIgnoreCase("object")){

                if(sig.getGenerics().isEmpty()){
                    if(asValue) {
                        return  "fromJson" + name + "(" + json + ")";
                    }
                    return "fromJson" + name;
                }
                Tuple2<String,JJTypeSignature> subGen = sig.getGenerics().head();
                return propFromJson(asValue,json,subGen._1,subGen._2);
            }
            String res = simpleName + ".fromJson(" + (asValue ? json : "v");
            if(sig.getGenerics().isEmpty() == false) {
                res += ",";
            }
            res += sig.getGenerics().map(t -> propFromJson(false,json,t._1, t._2)).toString(",");
            res += ")";
            if(asValue == false){
                return "function(v) { return " + res + "; }";
            }
            return res;
        }



    }


}
