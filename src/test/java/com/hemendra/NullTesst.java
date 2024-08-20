package com.hemendra;

import java.util.Optional;

public class NullTesst {

    public static void main(String[] args) {
        String s = null;
        Optional.ofNullable(s)
                .map(String::toLowerCase)
                .filter(p->p.startsWith("h"))
                .ifPresent(a-> {
                    System.out.println("Hello");
                });
    }
}
