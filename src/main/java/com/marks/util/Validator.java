package com.marks.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Jobe on 4/29/16.
 */
public class Validator {

    private Pattern passwordPattern;
    private Pattern urlPattern;
    private Matcher matcher;

    public Validator() {
        passwordPattern = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{5,30}");
        urlPattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|].[-a-zA-Z]");
    }

    public boolean validatePassword(String password) {
        matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    public boolean validateUrl(String url) {
        matcher = urlPattern.matcher(url);
        return matcher.matches();
    }
}
