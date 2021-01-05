package com.csp.kakaochatbot;

public class Util {

    public static int getNumber(String str) {
        return Integer.parseInt(str.replaceAll("[^0-9]", ""));
    }

}
