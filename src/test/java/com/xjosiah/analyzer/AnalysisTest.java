package com.xjosiah.analyzer;

import org.junit.Test;

import java.util.ArrayList;

public class AnalysisTest {
    @Test
    public void testDoAnalysis() {
//        Analysis analysis = new Analysis("src/main/resources/test.c");
//        Analysis analysis = new Analysis("src/main/resources/test1.c");
        Analysis analysis = new Analysis("src/main/resources/test2.c");
        String s = analysis.getFileAllLine();
        ArrayList<StringBuilder> resultStr = null;
        try {
            resultStr = analysis.doAnalysis(s);
            if (resultStr!=null)
                resultStr.forEach(System.out::println);
        } catch (AnalysisException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void regixTest() {
        StringBuilder stringBuilder = new StringBuilder("1.123");
        char c = stringBuilder.charAt(0);
        System.out.println(String.valueOf(c).matches("^[0-9]"));
    }

}
