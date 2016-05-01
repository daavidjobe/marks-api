package com.marks.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Jobe on 4/29/16.
 */
public class Validator {

    private Pattern passwordPattern;
    private Matcher matcher;

    public Validator() {
        passwordPattern = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{5,10}");
    }

    public boolean validatePassword(String password) {
        matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }
}
