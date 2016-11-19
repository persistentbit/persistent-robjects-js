package com.persistentbit.robjects.js;

import com.persistentbit.jjson.mapping.JJMapper;
import com.persistentbit.jjson.mapping.description.JJTypeDescription;
import com.persistentbit.robjects.js.examples.Person;
import com.persistentbit.robjects.js.examples.ValueWithGen;
import org.junit.Test;

/**
 * Created by petermuys on 3/09/16.
 */
public class TestValueClassCodeGen {

    @Test
    public void testSimpleValueClass(){
        JJMapper          mapper = new JJMapper();
        JJTypeDescription td     = mapper.describe(Person.class);
        //CodeGenValueClass cg     = new CodeGenValueClass(td);
        //cg.generate();
        //cg.write(System.out);
    }
    @Test
    public void testGenerics(){
        JJMapper mapper = new JJMapper();
        JJTypeDescription td = mapper.describe(ValueWithGen.class);
        //CodeGenValueClass cg = new CodeGenValueClass(td);
        //cg.generate();
        //cg.write(System.out);
    }
}
