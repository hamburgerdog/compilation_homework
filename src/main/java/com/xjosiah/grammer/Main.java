package com.xjosiah.grammer;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> ll1Array = new ArrayList<>();
        initLl1(ll1Array);
        Grammer grammer = new Grammer(ll1Array, 'E');
        grammer.creatAlzTable();
    }

    private static void initLl1(ArrayList<String> ll1Array) {
        //  假设有：E' = M  T' = L
        ll1Array.add("E->TM");
        ll1Array.add("M->+TM");
        ll1Array.add("M->ε");
        ll1Array.add("T->FL");
        ll1Array.add("L->*FL");
        ll1Array.add("L->ε");
        ll1Array.add("F->(E)");
        ll1Array.add("F->i");
    }
}
