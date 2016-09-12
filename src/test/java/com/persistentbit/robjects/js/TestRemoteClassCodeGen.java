package com.persistentbit.robjects.js;

import com.persistentbit.robjects.js.examples.App;
import com.persistentbit.sourcegen.SourceGen;
import org.junit.Test;

/**
 * @author Peter Muys
 * @since 6/09/2016
 */
public class TestRemoteClassCodeGen {
    @Test
    public void testApiCodeGen(){
        SourceGen sg = new SourceGen();
        CodeGenRemoteClass cgr =new CodeGenRemoteClass(new JSCodeGenSettings(JSCodeGenSettings.ModuleType.commonJs, JSCodeGenSettings.CodeType.js5));
        sg.add(cgr.generateHelperClass());
        sg.add(cgr.generateAll(App.class));

        System.err.println(sg.writeToString());
    }
}
