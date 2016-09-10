package com.persistentbit.robjects.js;

import com.persistentbit.core.collections.PStream;
import com.persistentbit.jjson.mapping.JJMapper;
import com.persistentbit.jjson.mapping.description.JJTypeDescription;
import com.persistentbit.jjson.nodes.JJPrinter;
import com.persistentbit.robjects.js.examples.Name;
import com.persistentbit.robjects.js.examples.Person;
import com.persistentbit.robjects.js.examples.ValueWithCollections;
import com.persistentbit.robjects.js.examples.ValueWithGen;
import org.junit.Test;

import javax.script.*;

/**
 * Created by petermuys on 10/09/16.
 */
public class TestCollectionsCodeGen {
    private final JSCodeGenSettings settings = new JSCodeGenSettings(JSCodeGenSettings.ModuleType.none, JSCodeGenSettings.CodeType.js5);
    @Test
    public void testCollections(){
        JJMapper mapper = new JJMapper();
        JJTypeDescription td = mapper.describe(ValueWithCollections.class);

        System.out.println(JJPrinter.print(true ,mapper.write(td)));
        CodeGenValueClass cg = new CodeGenValueClass(settings);
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
            printWithLineNumbers(script);
            Object result = scriptEngine.eval(script);
        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }
}
