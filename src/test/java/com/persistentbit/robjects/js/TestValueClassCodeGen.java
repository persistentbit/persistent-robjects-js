package com.persistentbit.robjects.js;

import com.persistentbit.core.collections.PStream;
import com.persistentbit.jjson.mapping.JJMapper;
import com.persistentbit.jjson.mapping.description.JJTypeDescription;
import com.persistentbit.robjects.js.examples.Name;
import com.persistentbit.robjects.js.examples.Person;
import com.persistentbit.robjects.js.examples.ValueWithGen;
import org.junit.Test;

import javax.script.*;


/**
 * Created by petermuys on 3/09/16.
 */
public class TestValueClassCodeGen {

    @Test
    public void testSimpleValueClass(){

        CodeGenValueClass cg = new CodeGenValueClass();
        String s = cg.generate(Name.class).writeToString();
        s += cg.generate(Person.class).writeToString();
        runJs(s);
    }
    @Test
    public void testGenerics(){
        JJMapper mapper = new JJMapper();
        JJTypeDescription td = mapper.describe(ValueWithGen.class);
        CodeGenValueClass cg = new CodeGenValueClass();
        cg.generate(td).write(System.out);
    }

    private void printWithLineNumbers(String txt){
        PStream.from(txt.split("\n")).zipWithIndex().forEach(t -> System.err.println("" + (t._1+1) + "\t" + t._2));
    }

    private void runJs(String script){
        ScriptEngine scriptEngine;
        ScriptEngineManager sem = new ScriptEngineManager();
        scriptEngine = sem.getEngineByExtension("js");
        ScriptContext context = new SimpleScriptContext();
        Bindings engineScope = context.getBindings(ScriptContext.ENGINE_SCOPE);

        try {
            /*Path temp = new File(System.getProperty("java.io.tmpdir"),name).toPath();
            Files.write(temp,script.getBytes("UTF-8"));
            System.out.println(script);*/
            printWithLineNumbers(script);
            Object result = scriptEngine.eval(script);
            //System.out.println("Got result:" + result);
        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }
}
