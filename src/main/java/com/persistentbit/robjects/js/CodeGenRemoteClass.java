package com.persistentbit.robjects.js;

import com.persistentbit.jjson.mapping.JJMapper;
import com.persistentbit.robjects.describe.RemoteClassDescription;
import com.persistentbit.robjects.describe.RemoteDescriber;
import com.persistentbit.robjects.describe.RemoteMethodDescription;
import com.persistentbit.robjects.describe.RemoteServiceDescription;
import com.persistentbit.sourcegen.SourceGen;

/**
 * @author Peter Muys
 */
public class CodeGenRemoteClass {
    public CodeGenRemoteClass(){

    }
    public SourceGen generate(RemoteClassDescription cd){
        Generator g = new Generator(cd);
        g.generate();
        return g;
    }
    public SourceGen    generate(Class<?> rootClass){
        return generate(rootClass,new JJMapper());
    }
    public SourceGen    generate(Class<?> rootClass, JJMapper mapper){
        RemoteServiceDescription rsd = new RemoteDescriber(mapper).descripeRemoteService(rootClass);
        SourceGen sg = new SourceGen();
        rsd.getRemoteObjects().forEach(rc -> {
            Generator csg= new Generator(rc);
            csg.generate();
            sg.add(csg);
        });

        return sg;
    }

    class Generator  extends AbstractCodeGen {
        private RemoteClassDescription cd;
        private String className;

        private Generator(RemoteClassDescription cd) {
            this.cd = cd;
            this.className = toSimpleName(cd.getType().getJavaClassName());
        }


        public void generate() {
            println("/*");
            println(" *   REMOTE CLASS " + className);
            println("*/");
            bs("var " + className + " = function(caller,remoteObjectDef)");{
                println("this._caller = caller;");
                println("this._callStack = remoteObjectDef.callStack;");
                bs("for (var i=0; i < remoteObjectDef.remoteMethods.length; i++)");{
                    println("var methodDef = remoteObjectDef.remoteMethods[i];");
                    println("var methodName = methodDef.methodName;");
                    println("this[\"md_\"+methodName] = methodDef;");
                be();}
                bs("for(i=0; i< remoteObjectDef.remoteCache.length; i++)"); {
                    println("var methodDef = remoteObjectDef.remoteCache[i].key;");
                    println("var value = remoteObjectDef.remoteCache[i].value;");
                    println("var methodName = methodDef.methodName;");
                    println("this['Cached_'+ methodName] = value; }");
                be();}
            be("};");}
            for(RemoteMethodDescription rmd :cd.getMethods()){
                bs(className+".prototype." + rmd.getMethodName() + " = function(" + rmd.getParameters().map(p-> p.getName()).toString(",") + ")");{

                be("};");}
            }

        }


    }

}
