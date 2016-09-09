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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Name name = (Name) o;

        if (firstName != null ? !firstName.equals(name.firstName) : name.firstName != null) return false;
        return lastName != null ? lastName.equals(name.lastName) : name.lastName == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        return result;
    }
}
