package com.xjosiah.analyzer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class AnalysisTest {
    @Test
    public void test(){
        Analysis analysis = new Analysis("src/main/resources/test.c");
        System.out.println(analysis.getFileAllLine());
    }

    @Test
    public void testDoAnalysis(){
        Analysis analysis = new Analysis("src/main/resources/test.c");
        String s = analysis.getFileAllLine();
        System.out.println(s);
        ArrayList<StringBuilder> resultStr = analysis.doAnalysis(s);
        resultStr.forEach(System.out::println);
    }
}
