package com.persistentbit.robjects.js.examples;

import com.persistentbit.core.Tuple2;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.utils.BaseValueClass;

/**
 * Created by petermuys on 10/09/16.
 */
public class ValueWithCollections<A> extends BaseValueClass{
    private final PList<Integer>    listInt;
    private final PList<Tuple2<A,String>>  listAString;

    public ValueWithCollections(PList<Integer> listInt, PList<Tuple2<A, String>> listAString) {
        this.listInt = listInt;
        this.listAString = listAString;
    }
}
