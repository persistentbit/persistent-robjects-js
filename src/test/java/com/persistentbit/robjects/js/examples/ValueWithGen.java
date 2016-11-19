package com.persistentbit.robjects.js.examples;


import com.persistentbit.core.tuples.Tuple2;

/**
 * Created by petermuys on 3/09/16.
 */
public class ValueWithGen<A> {

    public A                                valueA;
    public Tuple2<A, String>                tupleAString;
    public String                           justAString;
    public Tuple2<Tuple2<A,Integer>,Double> tupleTupleAIntDouble;
    public ValueWithGen(A valueA, Tuple2<A, String> tupleAString,String justAString,Tuple2<Tuple2<A,Integer>,Double> tupleTupleAIntDouble) {
        this.valueA = valueA;
        this.tupleAString = tupleAString;
        this.tupleTupleAIntDouble = tupleTupleAIntDouble;
    }
}
