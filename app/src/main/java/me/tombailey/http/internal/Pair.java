package me.tombailey.http.internal;

/**
 * Created by Tom on 21/01/2017.
 */

public class Pair<A, B> {

    private A mFirst;
    private B mSecond;

    public Pair(A first, B second) {
        mFirst = first;
        mSecond = second;
    }

    public A first() {
        return mFirst;
    }

    public B second() {
        return mSecond;
    }

}
