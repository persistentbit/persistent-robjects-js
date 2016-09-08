package com.persistentbit.robjects.js.examples;

import com.persistentbit.core.Tuple2;
import com.persistentbit.robjects.annotations.Remotable;
import com.persistentbit.robjects.annotations.RemoteCache;

/**
 * @author Peter Muys
 * @since 6/09/2016
 */
@Remotable
public interface App {
    @RemoteCache
    Tuple2<Name,Integer>    getAuthorNameAndId();
    Person getAuthor();
    UserSession getUserSession(Tuple2<String,String> unpw);
}
