package com.xjosiah.analyzer;

import org.junit.Test;

import java.util.ArrayList;

public class AnalysisTest {
    @Test
    public void test() {
        Analysis analysis = new Analysis("src/main/resources/test.c");
        ArrayList<String> fileAllLine = analysis.getFileAllLine();
        ArrayList<StringBuilder> stringBuilders = new ArrayList<>();
        for (String s : fileAllLine) {
            try {
                stringBuilders = analysis.doAnalysis(s);
            } catch (AnalysisException e) {
                e.printStackTrace();
            }
        }
        stringBuilders.forEach(System.out::println);
    }
//    @Test
//    public void testDoAnalysis() {
////        Analysis analysis = new Analysis("src/main/resources/test.c");
//        Analysis analysis = new Analysis("src/main/resources/test1.c");
////        Analysis analysis = new Analysis("src/main/resources/test2.c");
//        String s = analysis.getFileAllLine();
//        ArrayList<StringBuilder> resultStr = null;
//        try {
//            resultStr = analysis.doAnalysis(s);
//            if (resultStr!=null)
//                resultStr.forEach(System.out::println);
//        } catch (AnalysisException e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void regixTest() {
//        StringBuilder stringBuilder = new StringBuilder("12342314");
//        char c = stringBuilder.charAt(0);
//        System.out.println(stringBuilder.toString().matches("^.*\\D+.*"));
        String s = "oqweriuohasdf/*1239872314*/123";
//        String[] split = s.split("^.*((/\\*){1}.*(\\*/){1}){1}.*");
        String[] split = s.split("\\*/");
        System.out.println(split[0]+" "+split[split.length-1]);
//        System.out.println(s.matches("^.*((/\\*){1}.*(\\*/){1}){1}.*"));
    }
    @Test
    public void testAnalysis2() {
        Analysis2 analysis2 = new Analysis2("src/main/resources/test.c");
        ArrayList<String> stringArrayList = null;
        try {
            stringArrayList = analysis2.doAnalysis();
            stringArrayList.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
