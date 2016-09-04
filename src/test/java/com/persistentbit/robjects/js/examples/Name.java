package com.persistentbit.robjects.js.examples;

import com.persistentbit.core.utils.BaseValueClass;

/**
 * Created by petermuys on 3/09/16.
 */
public class Name extends BaseValueClass{
    public final String firstName;
    public final String lastName;

    public Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
