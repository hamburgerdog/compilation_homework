package com.xjosiah.analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 一个简单的词法分析器
 */
public class Analysis2 {
    //  用于初始化当前关键词表的C语言源程序的URI
    private String uri;
    //  关键词表
    private HashMap<String, Integer> wordMap;
    //  存放最终分析的结果
    private ArrayList<String> resultStr;
    //  过滤后的C语言源程序
    private ArrayList<String> fileAllLine;

    public Analysis2(String uri) {
        this.uri = uri;
        this.wordMap = AnalyzerMap.getWordMap();
        resultStr = new ArrayList<>();
        fileAllLine = new ArrayList<>();
        initFileAllLine();
    }

    /**
     * 过滤文件获取初始字符串，如过滤文件中的注释
     */
    private void initFileAllLine() {
        try {
            List<String> allLines = Files.readAllLines(Paths.get(uri));
            //  用于跳过多行注释 即 /* skip */
            boolean skip = false;
            for (String line : allLines) {
                if (line.trim().startsWith("/*") && line.trim().endsWith("*/")) {
                    continue;
                }
                //  多行注释
                else if (line.trim().startsWith("/*")) {
                    skip = true;
                    continue;
                } else if (line.trim().endsWith("*/")) {
                    skip = false;
                    continue;
                }
                //  单行注释
                else if (line.trim().startsWith("//") || line.trim().startsWith("#")) {
                    continue;
                } else if (line.matches(".*(//)+.*")) {
                    String[] split = line.split("//");
                    fileAllLine.add(split[0]);
                    continue;
                }
                else if(line.matches("^.*((/\\*){1}.*(\\*/){1}){1}.*")){
                    String[] split = line.split("/\\*");
                    fileAllLine.add(split[0]);
                    split = split[1].split("\\*/");
                    fileAllLine.add(split[split.length-1]);
                    continue;
                }
                else if (line.matches("^.*(/\\*)+.*")) {
                    skip=true;
                    String[] split = line.split("/\\*");
                    fileAllLine.add(split[0]);
                    continue;
                }else if (line.matches("^.*(\\*/)+.*")){
                    skip=false;
                    String[] split = line.split("\\*/");
                    fileAllLine.add(split[split.length-1]);
                    continue;
                }else if (skip==true){
                    continue;
                }
                //  单行注释
                fileAllLine.add(line.trim());
            }
//            System.out.println(fileAllLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对词法分析器中存放的字符串进行分析
     * @return
     * @throws Exception
     */
    public ArrayList<String> doAnalysis() throws Exception {
        StringBuilder strTmp = new StringBuilder();
        int i;
        for (String line : fileAllLine) {
            if (strTmp.length() != 0) {
                if (strTmp.charAt(0) != '"') {
                    if (wordMap.get(strTmp.toString()) != null) {
                        resultStr.add(addWord(strTmp));
                    } else if (String.valueOf(strTmp.charAt(0)).matches("[a-zA-Z]")) {
                        resultStr.add(addWord(strTmp, 20));
                    } else if (!strTmp.toString().matches("^.*\\D+.*")) {
                        resultStr.add(addWord(strTmp, 10));
                    } else
                        resultStr.add(addWord(strTmp, 99));
                }
            }
            //  逐行查找
            for (i = 0; i < line.length(); i++) {
                char nowChar = line.charAt(i);
                //  如果当前字符串是非符号单词则继续读取
                if (String.valueOf(nowChar).matches("[0-9a-zA-Z]")) {
                    if (strTmp.length() != 0) {
                        if (String.valueOf(strTmp.charAt(0)).matches("[<>!=]")) {
                            resultStr.add(addWord(strTmp));
                        }
                    }
                    strTmp.append(nowChar);
                    continue;
                }
                //  遇到符号则开始处理暂存的数据 - 分三种情况 ： ID 和 INT [> >= < <= ! != = ==]
                else if (strTmp.length() != 0) {
                    //  处理多重关键词情况 在 [> < ! =] 情况下，一定没有暂存的数据
                    if (String.valueOf(strTmp.charAt(0)).matches("[<>=!]")) {
                        if (nowChar == '=') {
                            strTmp.append(nowChar);
                            resultStr.add(addWord(strTmp));
                            continue;
                        } else {
                            resultStr.add(addWord(strTmp));
                        }
                    }
                    //  处理关键词，从wordmap里匹配则代表当前暂存字符是关键词
                    else if (wordMap.get(strTmp.toString()) != null) {
                        resultStr.add(addWord(strTmp));
                    } else if (!strTmp.toString().matches("^.*\\D+.*")) {
                        resultStr.add(addWord(strTmp, 10));
                    } else if (String.valueOf(strTmp.charAt(0)).matches("[a-zA-z]")) {
                        resultStr.add(addWord(strTmp, 20));
                    } else if (String.valueOf(strTmp.charAt(0)).matches("\\d")) {
                        resultStr.add(addWord(strTmp, 99));
                    }
                }

                //  处理当前字符 如果是[ + - / * \ {} [] () ] 这类字符型的关键词则直接加入到关键词表中跳过就行
                if (String.valueOf(nowChar).matches("[+\\-/*()\\[\\]{}:,;]")) {
                    if (strTmp.length() == 0) {
                        resultStr.add(addWord(new StringBuilder("" + nowChar)));
                        //  切记要清空暂存的数据，因为addWord会释放的内容是基于传入的StringBuilder对象
                        //  这里新建了一个临时对象，因此需要手动清空StringBuilder对象
                        initStringBuilder(strTmp);
                        continue;
                    } else if (strTmp.charAt(0) == '"') {
                        strTmp.append(nowChar);
                        continue;
                    }
                } else if (String.valueOf(nowChar).matches("[<>!=]")) {
                    strTmp.append(nowChar);
                    continue;
                }
                switch (nowChar) {
                    //  在之前的情形中，" ' 和 单引号 并不会被处理
                    case '\'':
                    case '"':
                        if (strTmp.length() == 0)
                            strTmp.append(nowChar);
                        else if (strTmp.charAt(0) == nowChar) {
                            if (nowChar == '\'' && strTmp.length() == 2) {
                                resultStr.add(addWord(new StringBuilder("" + strTmp.charAt(1)), 51));
                                initStringBuilder(strTmp);
                            } else if (nowChar == '"') {
                                resultStr.add(addWord(new StringBuilder(strTmp.substring(1)), 50));
                                initStringBuilder(strTmp);
                            } else
                                throw new Exception("String error");
                        } else
                            throw new Exception("String error");
                        break;
                    //  处理空格 如果是空格并不被包含在双引号的字符串中，则直接跳过
                    case ' ':
                        if (strTmp.length() == 0)
                            break;
                        if (strTmp.charAt(0) == '"')
                            strTmp.append(nowChar);
                        else if (!strTmp.toString().matches("^.*\\D+.*")) {
                            resultStr.add(addWord(strTmp, 10));
                        } else {
                            resultStr.add(addWord(strTmp, 20));
                        }
                        break;
                    default:
                        resultStr.add(addWord(new StringBuilder("" + nowChar), 99));
                        initStringBuilder(strTmp);
                }
            }
        }
        return resultStr;
    }

    /**
     * 主要用于清空暂存的字符串
     * @param sb
     */
    private void initStringBuilder(StringBuilder sb) {
        sb.delete(0, sb.length());
    }

    /**
     * 向结果集中存放数据
     *  @param sb   要存放的字符串，SYN为关键词自动匹配
     * @return
     */
    private String addWord(StringBuilder sb) {
        String result = wordMap.get(sb.toString()) + ":" + sb.toString();
        initStringBuilder(sb);
        return result;
    }

    /**
     * 向结果集中存放数据
     * @param sb    要存放的字符串
     * @param SYN   指定一个SYN
     * @return
     */
    private String addWord(StringBuilder sb, int SYN) {
        String result = SYN + ":" + sb.toString();
        initStringBuilder(sb);
        return result;
    }

    /**
     * @return 源文件过滤后的初始字符串
     */
    public ArrayList<String> getFileAllLine() {
        return fileAllLine;
    }
}
