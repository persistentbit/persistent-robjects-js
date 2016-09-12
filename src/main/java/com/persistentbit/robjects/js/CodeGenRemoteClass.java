package com.persistentbit.robjects.js;

import com.persistentbit.jjson.mapping.JJMapper;
import com.persistentbit.jjson.mapping.description.JJTypeSignature;
import com.persistentbit.robjects.describe.RemoteClassDescription;
import com.persistentbit.robjects.describe.RemoteDescriber;
import com.persistentbit.robjects.describe.RemoteMethodDescription;
import com.persistentbit.robjects.describe.RemoteServiceDescription;
import com.persistentbit.sourcegen.SourceGen;

/**
 * @author Peter Muys
 */
public class CodeGenRemoteClass {
    private final JJMapper mapper;
    private final CodeGenValueClass codeGenValueClass;
    private final JSCodeGenSettings settings;

    public CodeGenRemoteClass(JSCodeGenSettings codeGenSettings){
        this(new CodeGenValueClass(codeGenSettings),new JJMapper());
    }
    public CodeGenRemoteClass(CodeGenValueClass codeGenValueClass,JJMapper mapper){
        this.codeGenValueClass = codeGenValueClass;
        this.mapper = mapper;
        this.settings = codeGenValueClass.getSettings();
    }

    public SourceGen generateHelperClass() {
        return CodeGenHelperClass.create(codeGenValueClass.getSettings());
    }

    public SourceGen generate(RemoteClassDescription cd){
        Generator g = new Generator(cd);
        g.generate();
        return g;
    }
    public SourceGen    generateValues(Class<?> rootClass){

        RemoteServiceDescription rsd = new RemoteDescriber(mapper).descripeRemoteService(rootClass);
        SourceGen sg = new SourceGen();
        sg.println("// VALUE CLASSES  for REMOTE SERVICE " + rsd.getRootSignature().getCls());
        rsd.getValueObjects().forEach(rc -> {
            SourceGen csg = codeGenValueClass.generate(rc);
            sg.add(csg);
        });

        return sg;
    }
    public SourceGen    generateRemotes(Class<?> rootClass){
        RemoteServiceDescription rsd = new RemoteDescriber(mapper).descripeRemoteService(rootClass);
        SourceGen sg = new SourceGen();
        sg.println("// REMOTE CLASSES  for REMOTE SERVICE " + rsd.getRootSignature().getCls());
        rsd.getRemoteObjects().forEach(rc -> {
            Generator csg= new Generator(rc);
            csg.generate();
            sg.add(csg);
        });

        return sg;
    }

    public SourceGen    generateAll(Class<?> rootClass){
        RemoteServiceDescription rsd = new RemoteDescriber(mapper).descripeRemoteService(rootClass);
        SourceGen sg = new SourceGen();
        sg.println("// REMOTE SERVICE " + rsd.getRootSignature().getCls());
        sg.add(generateValues(rootClass));
        sg.add(generateRemotes(rootClass));
        return sg;
    }

    class Generator  extends AbstractCodeGen {
        private RemoteClassDescription cd;
        private String className;

        private Generator(RemoteClassDescription cd) {
            super(CodeGenRemoteClass.this.settings);
            this.cd = cd;
            this.className = toSimpleName(cd.getType().getCls());
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
                    if(rmd.isCached()){
                        println("return " + fromJSon("this.Cached_" + rmd.getMethodName(),rmd.getReturnType()));
                    } else {
                        bs("var rMethodCall = ");{
                            println("methodToCall: this['md_" +  rmd.getMethodName()+ "'],");
                            bs("arguments: ["); {
                                printAsLines(rmd.getParameters().map(t -> {
                                   return toJson(t.getName(),t.getTypeSignature());
                                }),",");
                            }be("]");
                        }be("};");
                        bs("var rCall =");{
                            println("callStack : this._callStack,");
                            println("thisCall : rMethodCall");
                        }be("};");
                        println("return this._caller(rCall).then(function (rCallResult) {");
                        indent();{
                            bs("if(rCallResult.exception)"); {
                                println("throw rCallResult.exception;");
                            }be("}");
                            if(rmd.isReturnsRemoteObject()) {
                                println("var robject = rCallResult.robject;");
                                bs("if(robject !== null && typeof robject !== 'undefined')");
                                {
                                    println("return new " + toSimpleName(rmd.getReturnType().getCls()) + "(this._caller,rCallResult.robject);");
                                }
                                be("}");
                                println("return null;");
                            } else {
                                println("return " + fromJSon("rCallResult.value",rmd.getReturnType()));
                            }

                        }
                        outdent();println("}, function (notOk) {");
                        indent();
                        outdent(); println("});");
                    }
                be("};");}
            }

        }
        /*public String toJSon(String value, JJTypeSignature typeSignature){
            JJTypeSignature.JsonType jsonType = typeSignature.getJsonType();
            if(jsonType.isJsonPrimitive()){
                return value;
            } else if (jsonType != JJTypeSignature.JsonType.jsonObject){
                throw new RuntimeException("Not Yet");
            }
            return value + ".toJson";
        }*/


        public String fromJSon(String json, JJTypeSignature typeSignature){
            JJTypeSignature.JsonType jsonType = typeSignature.getJsonType();
            if(jsonType.isJsonPrimitive()){
                return json;
            } else if (jsonType != JJTypeSignature.JsonType.jsonObject){
                throw new RuntimeException("Not Yet");
            }
            String converters = "";
            converters += prefixNotEmpty(typeSignature.getGenerics().values().map(gt -> fromJSonGenericsMapper(gt)),", ");

            return toSimpleName(typeSignature.getCls()) + ".fromJson("+ json + converters + ")";
        }
        public String fromJSonGenericsMapper(JJTypeSignature typeSignature){
            JJTypeSignature.JsonType jsonType = typeSignature.getJsonType();
            if(jsonType.isJsonPrimitive()){
                return "function(json){ return json; }";
            } else if (jsonType != JJTypeSignature.JsonType.jsonObject){
                throw new RuntimeException("Not Yet");
            }
            return "function(json) { return " + fromJSon("json",typeSignature) + "; }";
        }


    }

}
