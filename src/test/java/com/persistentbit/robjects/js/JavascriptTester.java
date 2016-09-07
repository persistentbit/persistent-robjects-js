package com.persistentbit.robjects.js;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;

import javax.script.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Peter Muys
 * @since 7/09/2016
 */
public class JavascriptTester {
    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
    private final ExecutorService e = Executors.newSingleThreadExecutor();
    private final ScriptContext context = new SimpleScriptContext();
    private final Bindings engineScope = context.getBindings(ScriptContext.ENGINE_SCOPE);
    private PList<Timer> timers = PList.empty();
    private String script = "";

    public JavascriptTester() {
        try {
            engineScope.put("runtime", this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addResources("/JavascriptTester.js");
        addResources("/promise-7.0.4.min.js");
    }

    public Timer createTimer(String name) {
        Timer t = new Timer(name, true);
        timers = timers.plus(t);
        return t;
    }

    public void cancelTimer(Object obj) {
        if (obj == null || obj instanceof Timer == false) {
            //System.out.println("cancelTimer invalid timer:" + obj);
            return;
        }
        Timer timer = (Timer) obj;
        timer.cancel();
        //int cnt = timers.size();
        timers = timers.filter(t -> t.equals(timer) == false);
        //if(timers.size() != cnt-1){
        //    System.out.println("Timer not found: " + timer);
        //}
    }

    public void waitForTimers() {
        while (timers.isEmpty() == false) {
            Thread.yield();
        }
    }

    public <T> T run() {
        Object result = null;
        try {
            //String s = script + "runtime.waitForTimers();";
            String s = script;
            result = scriptEngine.eval(s, context);
            //timers.forEach(t -> t.cancel());
            waitForTimers();
            System.out.println("WaitForTimers done...");
            //e.awaitTermination(1, TimeUnit.SECONDS);
            e.shutdown();
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        return (T) result;
    }

    public JavascriptTester addVar(String name, Object value){
        try {
            engineScope.put(name, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public JavascriptTester addResources(String... resourceNames) {
        PStream.from(resourceNames).map(n -> {
            try {
                return new String(Files.readAllBytes(Paths.get(getClass().getResource(n).toURI())));
            } catch (Exception e1) {
                throw new RuntimeException("Error adding resource " + n, e1);
            }
        }).forEach(s -> add(s));
        return this;
    }

    public JavascriptTester add(String... scripts) {
        for (String s : scripts) {
            script += s;
        }
        return this;
    }

    public void Assert(boolean value, String message) {
        if (value == false) {
            throw new RuntimeException(message);
        }
    }

    void dump() {
        PStream.from(script.split("\n")).zipWithIndex().forEach(t -> System.out.println("" + t._1 + ":\t" + t._2));
    }


    public void runLater(Runnable r) {
        e.submit(r);
        //r.run();
    }

    static public void main(String... args) {
        JavascriptTester rt = new JavascriptTester();
        rt.add("assert(true,'this is true');");
        //rt.add("assert(false,'this is false');");
        rt.add("Promise.resolve(\"Hello\").then(function(f) { print(f); return f;}).then(function(f){ print(\"2e: \" + f);}).then(function(f){ for(var i=0;i<10;i++){print(i);} });");
        rt.add("print('js-Done');");
        rt.run();
    }
}
