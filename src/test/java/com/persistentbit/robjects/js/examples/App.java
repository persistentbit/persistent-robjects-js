package com.persistentbit.robjects.js.examples;

import com.persistentbit.core.Tuple2;
import com.persistentbit.robjects.annotations.Remotable;

/**
 * @author Peter Muys
 * @since 6/09/2016
 */
@Remotable
public interface App {

    Person getAuthor();
    UserSession getUserSession(Tuple2<String,String> unpw);
}
