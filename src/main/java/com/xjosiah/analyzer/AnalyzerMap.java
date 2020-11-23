package com.xjosiah.analyzer;

import java.util.HashMap;

/**
 * 用HashMap结构来组成关键词表 - [KeyWord - SYN]
 * @author xjosiah
 * @since 2020.11.23
 */
public class AnalyzerMap {
    private static HashMap<String,Integer> wordMap;

    static{
        wordMap = new HashMap<>();
        wordMap.put("main",1);
        wordMap.put("int",2);
        wordMap.put("char",3);
        wordMap.put("if",4);
        wordMap.put("else",5);
        wordMap.put("for",6);
        wordMap.put("while",7);
        wordMap.put("return",8);
        wordMap.put("void",9);
        wordMap.put("STRING",50);
        wordMap.put("CHAR",51);
        wordMap.put("ID",10);
        wordMap.put("INT",20);
        wordMap.put("=",21);
        wordMap.put("+",22);
        wordMap.put("-",23);
        wordMap.put("*",24);
        wordMap.put("/",25);
        wordMap.put("(",26);
        wordMap.put(")",27);
        wordMap.put("[",28);
        wordMap.put("]",29);
        wordMap.put("{",30);
        wordMap.put("}",31);
        wordMap.put(",",32);
        wordMap.put(":",33);
        wordMap.put(";",34);
        wordMap.put(">",35);
        wordMap.put("<",36);
        wordMap.put(">=",37);
        wordMap.put("<=",38);
        wordMap.put("==",29);
        wordMap.put("!=",40);
    }

    /**
     * 获取词表的接口
     * @return  关键词表
     */
    public static HashMap<String, Integer> getWordMap() {
        return wordMap;
    }
}
