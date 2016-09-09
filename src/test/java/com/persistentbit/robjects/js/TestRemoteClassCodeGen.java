package com.persistentbit.robjects.js;

import com.persistentbit.robjects.js.examples.App;
import org.junit.Test;

/**
 * @author Peter Muys
 * @since 6/09/2016
 */
public class TestRemoteClassCodeGen {
    @Test
    public void testApiCodeGen(){
        String s =new CodeGenRemoteClass(new JSCodeGenSettings(JSCodeGenSettings.ModuleType.commonJs, JSCodeGenSettings.CodeType.js5)).generateAll(App.class).writeToString();
        System.err.println(s);
    }
}
