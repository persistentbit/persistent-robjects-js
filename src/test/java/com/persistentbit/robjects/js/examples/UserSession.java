package com.persistentbit.robjects.js.examples;

import com.persistentbit.robjects.annotations.Remotable;

/**
 * @author Peter Muys
 * @since 6/09/2016
 */
@Remotable
public interface UserSession {
    int getUserId();
    void update(int id, Person p);
}
