package com.persistentbit.robjects.js.examples;

import com.persistentbit.core.Tuple2;

/**
 * Created by petermuys on 3/09/16.
 */
public class ValueWithGen<A,B> {
    public A valueA;
    public Tuple2<A,String> tupleAString;
    public Tuple2<Integer,Tuple2<A,B>> tuple_IntTupleAB;
    public String justAString;

    public ValueWithGen(A valueA, Tuple2<A, String> tupleAString,Tuple2<Integer,Tuple2<A,B>> tuple_IntTupleAB,String justAString) {
        this.valueA = valueA;
        this.tupleAString = tupleAString;
        this.tuple_IntTupleAB = tuple_IntTupleAB;
        this.justAString = justAString;
    }
}
