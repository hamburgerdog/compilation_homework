package com.xjosiah.grammer;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 运行程序
 */
public class Main {
    public static void main(String[] args) {
        //  表达式
        ArrayList<String> ll1Array = new ArrayList<>();
        initLl1(ll1Array);
        //  分析表生成器
        Grammer grammer = new Grammer(ll1Array, 'E');
        String[][] alzTable = grammer.creatAlzTable();
        //  根据生成分析表的分析器
        StackParser stackParser = new StackParser(alzTable);
        /*----------------------请注意不用加#作为终结符------------------------*/
        stackParser.analyzeStr("i+i*i");
    }

    //  初始化表达式
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
