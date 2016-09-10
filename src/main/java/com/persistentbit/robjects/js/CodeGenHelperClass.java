package com.persistentbit.robjects.js;

import com.persistentbit.sourcegen.SourceGen;

/**
 * Created by petermuys on 10/09/16.
 */
public class CodeGenHelperClass extends AbstractCodeGen{
    private final JSCodeGenSettings settings;
    private final String className;

    private CodeGenHelperClass(JSCodeGenSettings settings){
        this.settings = settings;
        this.className = settings.getUtilsClassName();
    }

    private SourceGen generate() {
        println("var " + className + " = function() { return {}; };");
        println("");
        bs(className + ".arraysEqual = function(leftArray,rightArray,comparator)");
        {
            println("if(leftArray.length !== rightArray.length){ return false; }");
            bs("for(var i=0; i < leftArray.length; i++)");
            {
                println("if(comparator(leftArray[i],rightArray[i]) == false) { return false; }");
            }
            be("}");
            println("return true; ");
        }be("};");

        println(className + ".self = function(value) { return value; };");

        bs(className + ".arrayMap = function(arr,mappingFunction)");{
            println("return arr.map(mappingFunction);");
        be("};");}

        bs(className + ".getJsonData = function(value)"); {
            println("if(value == null) { return null; }");
            bs("if(value instanceof Array)"); {
                println("return " + className + ".arrayMap(value,function(v) { return getJsonData(v); });");
                be("}");}
            bs("if(typeof value === 'object')");{
                println("return value.jsonData();");
            be("}");}
            println("return value;");
        be("};");}

        bs(className + ".objectsEqual = function(left,right)"); {
            bs("if(value instanceof Array)"); {
                println("return " + className + ".arraysEqual(left,right," + className + ".objectsEqual);");
                be("}");}
            bs("if(typeof left === 'object')");{
                println("return left.equals(right);");
            be("}");}
            println("return value;");
            be("};");}



        return this;
    }

    private void notYet() {
        println("throw 'Not Yet Implemented';");
    }

    static SourceGen create(JSCodeGenSettings settings){
        return new CodeGenHelperClass(settings).generate();
    }
}
