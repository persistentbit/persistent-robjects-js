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
            this.className = toSimpleName(td.getTypeSignature().getCls());
        }


        public void generate() {
            className = toSimpleName(td.getTypeSignature().getCls());
            println("/*");
            println(" *   VALUE CLASS " + className);
            println("*/");

            bs("var " + className + " = function(" + td.getProperties().map(i -> i.getName()).toString(",") + ")");{
                bs("this._data = ");{
                    printAsLines(
                            td.getProperties().map(p -> "_" + p.getName() + " : typeof " + p.getName() + " === 'undefined' ? null : " + p.getName())
                            ,",");

                    be("};");}
                be("};");}


            bs(className + ".fromJson = function(json" + prefixNotEmpty(td.getTypeSignature().getGenerics().keys().map(n -> "fromJson" +n ),",") + ")");{
                println("return new " + className + "(" +
                        td.getProperties().map(p -> propFromJson(true,"json." + p.getName(),p.getName(),p.getTypeSignature())).toString(",")
                        + ");");
                be("};");}
            /* OLD VERSION
            bs(className + ".prototype.json = function("+ td.getTypeSignature().getGenerics().keys().map(g -> "toJson" + g ).toString(",") + ")");{
                bs("return ");{
                    printAsLines(td.getProperties().map(p -> p.getName() + ": " + propToJson(p.getName(),p.getTypeSignature())),",");
                    be("};");}
                be("};");}
            */
            bs(className + ".prototype.jsonData = function()");{
                bs("return ");{
                    printAsLines(td.getProperties().map(p -> {
                        String name = p.getName();
                        String thisName = "this." + name;
                        if(p.getTypeSignature().getGenerics().isEmpty()){
                            return  name + ": " + thisName;
                        } else {
                            return  name + ": " + thisName +" == null ? null : (typeof " + thisName  + " == 'object' ? "+thisName + ".jsonData() : "+ thisName  + ")";
                        }

                    }),",");
                    be("};");}
                be("};");}
            println(className + ".prototype.json = function() { return JSON.stringify(this.jsonData()); }");
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
            bs(className + ".prototype.equals = function(other)"); {
                println("if(!other) { return false; }");
                println("if(this === other) { return true; }");
                td.getProperties().forEach(p -> {
                    String name = p.getName();
                    String thisName = "this." + name;
                    String otherName = "other." + name;
                    //if (firstName != null ? !firstName.equals(name.firstName) : name.firstName != null) return false;



                        if(p.getTypeSignature().getJsonType().isJsonPrimitive()){
                            println("if(" + thisName + "!== " + otherName + ") { return false; }");
                        } else if(p.getTypeSignature().getJsonType().isJsonCollection()){
                            throw new RuntimeException("Not Yet");
                        } else {
                            String notEqual = "(typeof " + thisName + " === 'object' ? !" + thisName + ".equals(" + otherName + ") : " + thisName + " !== " + otherName+")";

                            if(p.getTypeSignature().getGenerics().isEmpty()) {
                                notEqual =  "!" + thisName + ".equals(" + otherName + ")";
                            }
                            println("if(" + thisName + " !== null ? " + notEqual +" : " + otherName + "!== null) { return false; }" );
                        }

                });
                println("return true; ");
            be("};");}


        }
        private String isDefined(String value){
            return "(" + value + " !== null && typeof " + value + "!== 'undefined')";
        }
        private String isUndefined(String value){
            return "(" + value + " === null || typeof " + value + "=== 'undefined')";
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
            if(toSimpleName(sig.getCls()).equalsIgnoreCase("Object")){
                //Assume this is one of the class generics...
                return generics + "(" + value + ")";
            }

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
            String simpleName = toSimpleName(sig.getCls());
            if(sig.getGenerics().isEmpty()){
                return "toJson" + name;
            }
            String allGenerics =sig.getGenerics().map(gt-> toJsonGenerics(gt._1,gt._2)).toString(",");
            if(sig.getGenerics().size() == 1){
                return allGenerics;
            }
            return "function(v) { return v.json(" + allGenerics + "); }";

        }

        private String propFromJson(boolean asValue,String json,String name,JJTypeSignature sig){
            if(sig.getJsonType() != JJTypeSignature.JsonType.jsonObject){
                if(asValue){
                    return json;
                }
                return "function(a) { return a; }";

            }



            String simpleName = toSimpleName(sig.getCls());
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
