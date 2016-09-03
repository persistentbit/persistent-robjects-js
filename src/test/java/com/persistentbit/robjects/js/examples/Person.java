package com.persistentbit.robjects.js.examples;

import com.persistentbit.core.utils.BaseValueClass;

/**
 * Created by petermuys on 3/09/16.
 */
public class Person extends BaseValueClass{
    public final int id;
    public final Name name;

    public Person(int id, Name name) {
        this.id = id;
        this.name = name;
    }
}
