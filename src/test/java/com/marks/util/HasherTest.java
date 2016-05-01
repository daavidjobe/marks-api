package com.marks.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by David Jobe on 4/29/16.
 */
public class HasherTest {

    @Test
    public void makeSalt() throws Exception {
        assertTrue(Hasher.makeSalt() instanceof String);
    }

    @Test
    public void hash() throws Exception {
        assertTrue(Hasher.hash("test").length() == 64);
    }

}