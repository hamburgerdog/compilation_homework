package com.xjosiah.analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 一个简单的C语言词法分析器
 * @author xjosiah
 * @since 2020.11.23
 * @version 1.0
 */
public class Analysis {
    //  用于初始化当前关键词表的C语言源程序的URI
    private String uri;
    //  关键词表
    private HashMap<String, Integer> wordMap;
    //  存放最终分析的结果
    private ArrayList<StringBuilder> resultStr;
    //  过滤后的C语言源程序
    private ArrayList<String> fileAllLine;

    public Analysis(String uri) {
        this.uri = uri;
        wordMap = AnalyzerMap.getWordMap();
        resultStr = new ArrayList<>();
        fileAllLine = new ArrayList<>();
        //  读文件 获取 fileAllline
        this.initFileAllLine();
    }

    /**
     * @return 源文件过滤后的初始字符串
     */
    public ArrayList<String> getFileAllLine() {
        return fileAllLine;
    }

    /**
     * 过滤文件获取初始字符串，如过滤文件中的注释
     */
    public void initFileAllLine() {
        try {
            List<String> allLines = Files.readAllLines(Paths.get(uri));
            //  用于跳过多行注释 即 /* skip */
            boolean skip = false;
            for (String line : allLines) {
                //  单行注释
                if (line.trim().startsWith("/*") && line.trim().endsWith("*/")) {
                    continue;
                }
                //  多行注释
                if (line.trim().startsWith("/*")) {
                    skip = true;
                    continue;
                }
                if (line.trim().endsWith("*/")) {
                    skip = false;
                    continue;
                }
                //  单行注释
                if (line.trim().startsWith("//") || line.trim().startsWith("#")) {
                    continue;
                }
                if (skip)
                    continue;
                fileAllLine.add(line.trim());
            }
//            System.out.println(fileAllLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用与该分析器相关的当前关键词表来进行词法分析
     * @param s 源字符串
     * @return  分析结果
     * @throws AnalysisException    常见的词法分析异常
     */
    public ArrayList<StringBuilder> doAnalysis(String s) throws AnalysisException {
        String allLine = s;
        //  操作的StringBuilder对象
        StringBuilder strTmp = new StringBuilder();
        //  一次操作的字符数量
        int mulIntTmp;
        //  与当前字符相关联的keyWord
        StringBuilder keyWord = new StringBuilder();
        //  当前字符
        char thisChar;
        //  开始从fileAllLine字符串中进行解析
        for (int i = 0; i < allLine.length(); ) {
            thisChar = allLine.charAt(i);
                        switch (thisChar) {
                case 'm':
                    //  初始化当前KeyWord
                    keyWord = initStrBulider(keyWord, "main");
                    //  进行词法分析
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'i':
                    keyWord = initStrBulider(keyWord, "int");
                    //  char(int)类型后紧跟一个char(int)是不符合词法规律的，因此此处的char视为是ID类型中的子字符串
                    //  与'i'对应的有两个关键词，即 int id
                    if (!isID()) {
                        mulIntTmp = getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    } else
                        mulIntTmp = appenIDtoStrTmp(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    if (mulIntTmp == 1) {
                        strTmp.deleteCharAt(strTmp.length() - 1);
                        keyWord = initStrBulider(keyWord, "if");
                        mulIntTmp = getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    }
                    i += mulIntTmp;
                    break;
                case 'c':
                    if (!isID()) {
                        keyWord = initStrBulider(keyWord, "char");
                        i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    } else
                        i += appenIDtoStrTmp(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'e':
                    keyWord = initStrBulider(keyWord, "else");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'f':
                    keyWord = initStrBulider(keyWord, "for");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'w':
                    keyWord = initStrBulider(keyWord, "while");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'r':
                    keyWord = initStrBulider(keyWord, "return");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'v':
                    keyWord = initStrBulider(keyWord, "void");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '\'':
                case '\"':
                    try {
                        i += getSTRING(strTmp, thisChar);
                    } catch (CHARException e) {
                        i += 1;
                        e.printStackTrace();
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    i += getINT(strTmp, thisChar);
                    break;
                case '=':
                    keyWord = initStrBulider(keyWord, "==");
                    mulIntTmp = getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    if (mulIntTmp == 1) {
                        strTmp.deleteCharAt(strTmp.length() - 1);
                        keyWord = initStrBulider(keyWord, "=");
                        mulIntTmp = getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    }
                    i += mulIntTmp;
                    break;
                case '+':
                    keyWord = initStrBulider(keyWord, "+");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '-':
                    keyWord = initStrBulider(keyWord, "-");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '*':
                    keyWord = initStrBulider(keyWord, "*");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '/':
                    keyWord = initStrBulider(keyWord, "/");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '(':
                    keyWord = initStrBulider(keyWord, "(");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ')':
                    keyWord = initStrBulider(keyWord, ")");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '[':
                    keyWord = initStrBulider(keyWord, "[");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ']':
                    keyWord = initStrBulider(keyWord, "]");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '{':
                    keyWord = initStrBulider(keyWord, "{");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '}':
                    keyWord = initStrBulider(keyWord, "}");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ',':
                    keyWord = initStrBulider(keyWord, ",");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ':':
                    keyWord = initStrBulider(keyWord, ":");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ';':
                    keyWord = initStrBulider(keyWord, ";");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '>':
                    keyWord = initStrBulider(keyWord, ">=");
                    mulIntTmp = getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    if (mulIntTmp == 1) {
                        strTmp.deleteCharAt(strTmp.length() - 1);
                        keyWord = initStrBulider(keyWord, ">");
                        mulIntTmp = getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    }
                    i += mulIntTmp;
                    break;
                case '<':
                    keyWord = initStrBulider(keyWord, "<=");
                    mulIntTmp = getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    if (mulIntTmp == 1) {
                        strTmp.deleteCharAt(strTmp.length() - 1);
                        keyWord = initStrBulider(keyWord, "<");
                        mulIntTmp = getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    }
                    i += mulIntTmp;
                    break;
                case '!':
                    keyWord = initStrBulider(keyWord, "!=");
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ' ':
                    if (isSpaceInString(strTmp))
                        strTmp.append(thisChar);
                    i++;
                    break;
                default:
                    keyWord = initStrBulider(keyWord, String.valueOf(thisChar));
                    i += getKeyWord(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
            }
        }
        return resultStr;
    }
    /**
     * 词法分析
     * @param strTmp 暂存的字符串
     * @param subStr 当前处理的字符串，用于与KEYWORD比较
     * @param keyWord   关键词
     * @return  处理的字符数量
     * @throws AnalysisException
     */
    public int getKeyWord(StringBuilder strTmp, String subStr, String keyWord) throws AnalysisException {
        //  查看当前关键词表中有无该关键词，没有则直接跳过，并暂存当前字符
        if (isKeyWord(keyWord)) {
            if (subStr.equals(keyWord)) {
                //  处理暂存的字符串，找到关键词也意味着前面暂存未处理的字符串结束
                if (strTmp.length() != 0) {
                    //  如果是STRING类型中包含的KEYWORD则直接跳过
                    if (strTmp.charAt(0) == '"') {
                        strTmp.append(keyWord);
                        return keyWord.length();
                    }
                    //  异常处理
                    if (strTmp.toString().matches(".*\\W.*")) {
                        if (String.valueOf(strTmp.charAt(0)).matches("^[0-9]"))
                            throw new AnalysisException("errorString:" + strTmp.toString() + "\t赋值错误：INT类型的常量中不能插入符号");
                        else
                            throw new AnalysisException("errorString:" + strTmp.toString() + "\t变量定义错误：变量名称中不能含有特殊符号");

                    }
                    //  正常插入处理 INT 和 ID
                    if (String.valueOf(strTmp.charAt(0)).matches("^[0-9]")) {
                        resultStr.add(new StringBuilder(20 + "\t:\t" + strTmp));
                    } else {
                        resultStr.add(new StringBuilder(10 + "\t:\t" + strTmp));
                    }
                    //  清空暂存数据
                    strTmp.delete(0, strTmp.length());
                }
                //  添加关键词到结果中
                resultStr.add(new StringBuilder(getSyn(keyWord) + "\t:\t" + keyWord));
                return keyWord.length();
            }
        }
        strTmp.append(keyWord.charAt(0));
        return 1;
    }

    /**
     * 处理STRING类型的字符串
     * @param strTmp    暂存字符串
     * @param thisChar  当前字符
     * @return  处理的字符数量
     * @throws CHARException
     */
    public int getSTRING(StringBuilder strTmp, char thisChar) throws CHARException {
        if (thisChar == '\'' || thisChar == '\"') {
            //  暂存数据为空则代表字符串起始
            if (strTmp.length() == 0) {
                strTmp.append(thisChar);
            }
            //  暂存数据不为空 则 如果当前字符于字符串起始字符相等 说明字符或字符串结束
            else if (thisChar == strTmp.charAt(0)) {
                //  '' 中只能存放单个字符
                if (thisChar == '\'' && strTmp.length() == 2) {
                    resultStr.add(new StringBuilder(51 + "\t:\t" + strTmp.charAt(1)));
                } else if (thisChar == '\"') {
                    resultStr.add(new StringBuilder(50 + "\t:\t" + strTmp.subSequence(1, strTmp.length())));
                } else {
                    // 异常处理
                    throw new CHARException("errorString:" + strTmp.toString() + "'\tchar类型变量错误：''中只能存放单个字符");
                }
                strTmp.delete(0, strTmp.length());
            }else {
                //  异常处理
                throw new CHARException("errorString:" + strTmp.toString() + "'\tchar类型变量错误：'' 和 \"\" 不对应");
            }
        }
        return 1;
    }

    /**
     * 处理INT类型的字符
     * @param strTmp    暂存字符串
     * @param thisChar  当前字符
     * @return
     */
    public int getINT(StringBuilder strTmp, char thisChar) {
        strTmp.append(thisChar);
        return 1;
    }

    /**
     * 查看当前关键词表
     * @param inputStr  要查询的KEYWORD
     * @return          当前关键词表是否包含该关键词
     */
    public boolean isKeyWord(String inputStr) {
        if (wordMap.get(inputStr) == null) {
            return false;
        } else
            return true;
    }

    /**
     * 获取关键词表中KEYWORD对应的SYN
     * @param key   KEYWORD
     * @return
     */
    public int getSyn(String key) {
        return wordMap.get(key);
    }

    /**
     * 用KEYWORD初始化StringBuilder类型的当前关键词
     * @param sb    当前关键词
     * @param keyword   关键词
     * @return
     */
    public StringBuilder initStrBulider(StringBuilder sb, String keyword) {
        sb.delete(0, sb.length());
        sb.append(keyword);
        return sb;
    }
    /**
     * 用于辨认ID类型的字符串，即char、int等关键词可以被包含为变量名
     * @return  字符串是否是ID类型
     */
    public boolean isID() {
        if (resultStr.size() <= 1) {
            return false;
        } else {
            String s = resultStr.get(resultStr.size() - 1).toString();
            return s.endsWith("int") || s.endsWith("char");
        }
    }
    /**
     * 将ID类型的字符串添加到暂存字符串中
     * @param strTmp    暂存字符串
     * @param subStr    截断于fileAllLine的子字符串（当前字符的后续，大小为于KEYWORD长度相同）用来与KEYWORD比较
     * @param keyWord   关键词
     * @return          操作的字符数量
     */
    public int appenIDtoStrTmp(StringBuilder strTmp, String subStr, String keyWord) {
        if (subStr.equals(keyWord)) {
            strTmp.append(keyWord);
            return keyWord.length();
        } else {
            strTmp.append(keyWord.charAt(0));
            return 1;
        }
    }
    public boolean isSpaceInString(StringBuilder strTmp){
        if (strTmp.length()==0)
            return false;
        else if (strTmp.charAt(0)=='"' || strTmp.charAt(0)=='\'')
            return true;
        else
            return false;
    }
}