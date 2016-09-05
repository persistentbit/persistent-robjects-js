package com.persistentbit.sourcegen;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Peter Muys
 * @since 22/12/2015
 */
public class SourceGen
{

    private boolean blockOpensOnSameLine    =   true;


    public SourceGen(){
    }

    public SourceGen(boolean blockOpensOnSameLine){
        this.blockOpensOnSameLine = blockOpensOnSameLine;
    }

    static private class CmdSubSource implements Consumer<SourceOut>
    {
        private final SourceGen sub;

        public CmdSubSource(SourceGen sub)
        {
            this.sub = sub;
        }

        @Override
        public void accept(SourceOut out) {
            sub.write(out);
        }

    }
    private final List<Consumer<SourceOut>> code    =   new ArrayList<>();

    /*public <T extends CustomCmd> T add(final T cmd){
        add(new SourceGenCmd()
        {

            @Override
            public void write(SourceOut out)
            {
                cmd.getSourceGen().write(out);
            }
        });
        return cmd;
    }*/

    public SourceGen add(SourceGen subSourceGen){
        add(new CmdSubSource(subSourceGen));
        return subSourceGen;
    }

    private SourceGen add(Consumer<SourceOut> cmd){
        code.add(cmd);
        return this;
    }



    public SourceGen bs(){
        return add(o -> o.bs());
    }
    public SourceGen bs(Object txt){
        return add(o ->o.print(txt).bs());
    }
    public SourceGen be(){
        return add(o -> o.be());
    }
    public SourceGen be(String be) { return add(o -> o.be(be));}
    public SourceGen indent() {
        return add(o -> o.indent());
    }

    public SourceGen outdent() {
        return add(o -> o.outdent());
    }

    public String str(Object obj){
        return "\"" + obj + "\"";
    }

    public SourceGen    println(Object obj){
        return add(o -> o.println(obj));
    }
    public SourceGen    nl(){
        return println("");
    }

    public SourceGen print(Object obj){
        return add(o -> o.print(obj));
    }

    public SourceGen    subSource(){
        SourceGen sub = new SourceGen();
        add(new CmdSubSource(sub));
        return sub;
    }


    public void write(OutputStream out){
        try(Writer w = new OutputStreamWriter(out))
        {
            write(w);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public void write(Writer writer){
        try(PrintWriter w = new PrintWriter(writer))
        {
            write(w);
        }
    }
    public void write(PrintWriter printWriter){
        write(new SourceOut(printWriter,blockOpensOnSameLine));
    }

    public void write(SourceOut out){
        for(Consumer<SourceOut> cmd : code){
            cmd.accept(out);
        }
    }

    public String   writeToString(){
        try(ByteArrayOutputStream bout = new ByteArrayOutputStream()){
            write(bout);
            bout.flush();
            return bout.toString();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }






    /*static public void main(String...args){
        SourceGen sout = new SourceGen();
        JavaImports imports = sout.add(new JavaImports("be.schaubroeck.bevolking"));


        imports.add("be.schaubroeck.bevolking.po.Persoon");
        imports.add(List.class,ArrayList.class);
        sout.bs("public class PersoonPO implements Persoon");
        SourceGen clsContent  = sout.subSource();
        SourceGen f = sout.subSource();
        SourceGen c = sout.subSource();
        SourceGen m = sout.subSource();
        sout.be();


        f.println("//Fields");
        c.println("");
        m.println("//Methods");

        f.println("private final int id;");
        f.println("private final Rrn rrn;");
        c.bs("public PersoonPO(int id, Rrn rrn)");
            c.println("this.id = id;");
            c.println("this.rrn = rrn");
        c.be();
        JavaMethod mToString = new JavaMethod(m,"String","toString").setOverride();
        mToString.out.print("return ").str("Persoon").println(";");

        JavaClass jc = new JavaClass(sout,imports,"TestClass");
        jc.field("int","id").createGetter();
        jc.field("String","name").createGetter().createSetter();


        sout.write(System.out);
    }*/
}
