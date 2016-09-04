package com.persistentbit.robjects.js.examples;

import com.persistentbit.core.Tuple2;

/**
 * Created by petermuys on 3/09/16.
 */
public class ValueWithGen<A> {
    public A valueA;
    public Tuple2<A,String> tupleAString;

    public ValueWithGen(A valueA, Tuple2<A, String> tupleAString) {
        this.valueA = valueA;
        this.tupleAString = tupleAString;
    }
}
