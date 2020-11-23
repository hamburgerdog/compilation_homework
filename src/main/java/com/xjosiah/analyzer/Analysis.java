package com.xjosiah.analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Analysis {
    private String uri;
    private HashMap<String, Integer> wordMap;
    private HashMap<String, Boolean> thisWordMap;
    private ArrayList<StringBuilder> resultStr;
    private String fileAllLine;

    public Analysis(String uri) {
        this.uri = uri;
        thisWordMap = new HashMap<>();
        resultStr = new ArrayList<>();
        wordMap = AnalyzerMap.getWordMap();
        this.initMap();
    }

    public String getFileAllLine() {
        return fileAllLine;
    }

    public void initMap() {
        fileAllLine = "";
        try {
            List<String> allLines = Files.readAllLines(Paths.get(uri));
            for (String line : allLines) {
                if (line.startsWith("//")) {
                    break;
                }
                String[] ss = line.trim().split(" ");
                for (String s : ss) {
                    fileAllLine += s;
                }
            }
            for (String s : wordMap.keySet()) {
                thisWordMap.put(s, fileAllLine.contains(s));
            }
            System.out.println(thisWordMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<StringBuilder> doAnalysis(String s) throws AnalysisException {
        String allLine = s;
        StringBuilder strTmp = new StringBuilder();
        StringBuilder keyWord = new StringBuilder();
        int mulIntTmp;
        char thisChar;
        for (int i = 0; i < allLine.length(); ) {
            thisChar = allLine.charAt(i);
            switch (thisChar) {
                case 'm':
                    keyWord = initStrBulider(keyWord, "main");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'i':
                    keyWord = initStrBulider(keyWord, "int");
                    mulIntTmp = getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    if (mulIntTmp == 1) {
                        strTmp.deleteCharAt(strTmp.length() - 1);
                        keyWord = initStrBulider(keyWord, "if");
                        mulIntTmp = getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    }
                    i += mulIntTmp;
                    break;
                case 'c':
                    keyWord = initStrBulider(keyWord, "char");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'e':
                    keyWord = initStrBulider(keyWord, "else");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'f':
                    keyWord = initStrBulider(keyWord, "for");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'w':
                    keyWord = initStrBulider(keyWord, "while");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'r':
                    keyWord = initStrBulider(keyWord, "return");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case 'v':
                    keyWord = initStrBulider(keyWord, "void");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '\'':
                case '\"':
                    try {
                        i += getSTRING(strTmp, thisChar);
                    } catch (CHARException e) {
                        i += 1;
                        e.printStackTrace();
                        return null;
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
                    mulIntTmp = getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    if (mulIntTmp == 1) {
                        strTmp.deleteCharAt(strTmp.length() - 1);
                        keyWord = initStrBulider(keyWord, "=");
                        mulIntTmp = getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    }
                    i += mulIntTmp;
                    break;
                case '+':
                    keyWord = initStrBulider(keyWord, "+");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '-':
                    keyWord = initStrBulider(keyWord, "-");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '*':
                    keyWord = initStrBulider(keyWord, "*");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '/':
                    keyWord = initStrBulider(keyWord, "/");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '(':
                    keyWord = initStrBulider(keyWord, "(");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ')':
                    keyWord = initStrBulider(keyWord, ")");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '[':
                    keyWord = initStrBulider(keyWord, "[");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ']':
                    keyWord = initStrBulider(keyWord, "]");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '{':
                    keyWord = initStrBulider(keyWord, "{");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '}':
                    keyWord = initStrBulider(keyWord, "}");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ',':
                    keyWord = initStrBulider(keyWord, ",");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ':':
                    keyWord = initStrBulider(keyWord, ":");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case ';':
                    keyWord = initStrBulider(keyWord, ";");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                case '>':
                    keyWord = initStrBulider(keyWord, ">=");
                    mulIntTmp = getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    if (mulIntTmp == 1) {
                        strTmp.deleteCharAt(strTmp.length() - 1);
                        keyWord = initStrBulider(keyWord, ">");
                        mulIntTmp = getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    }
                    i += mulIntTmp;
                    break;
                case '<':
                    keyWord = initStrBulider(keyWord, "<=");
                    mulIntTmp = getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    if (mulIntTmp == 1) {
                        strTmp.deleteCharAt(strTmp.length() - 1);
                        keyWord = initStrBulider(keyWord, "<");
                        mulIntTmp = getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    }
                    i += mulIntTmp;
                    break;
                case '!':
                    keyWord = initStrBulider(keyWord, "!=");
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
                default:
                    keyWord = initStrBulider(keyWord, String.valueOf(thisChar));
                    i += getKeyWorld(strTmp, allLine.substring(i, i + keyWord.length()), keyWord.toString());
                    break;
            }
        }
        if (strTmp.length() != 0) {
            resultStr.add(new StringBuilder(10 + "\t:\t" + strTmp));
            strTmp.delete(0, strTmp.length());
        }
        return resultStr;
    }

    public int getKeyWorld(StringBuilder strTmp, String subStr, String keyWord) throws AnalysisException {
        if (isKeyWord(keyWord)) {
            if (subStr.equals(keyWord)) {
                if (strTmp.length() != 0) {
                    if (strTmp.toString().matches(".*\\W.*")) {
                        if (String.valueOf(strTmp.charAt(0)).matches("^[0-9]"))
                            throw new AnalysisException("errorString:" + strTmp.toString() + "\t赋值错误：INT类型的常量中不能插入符号");
                        else
                            throw new AnalysisException("errorString:" + strTmp.toString() + "\t变量定义错误：变量名称中不能含有特殊符号");

                    }
                    if (String.valueOf(strTmp.charAt(0)).matches("^[0-9]")) {
                        resultStr.add(new StringBuilder(20 + "\t:\t" + strTmp));
                    } else {
                        resultStr.add(new StringBuilder(10 + "\t:\t" + strTmp));
                    }
                    strTmp.delete(0, strTmp.length());
                }
                resultStr.add(new StringBuilder(getSyn(keyWord) + "\t:\t" + keyWord));
                return keyWord.length();
            }
        }
        strTmp.append(keyWord.charAt(0));
        return 1;
    }

    public int getSTRING(StringBuilder strTmp, char thisChar) throws CHARException {
        if (thisChar == '\'' || thisChar == '\"') {
            if (strTmp.length() == 0) {
                strTmp.append(thisChar);
            } else if (thisChar == strTmp.charAt(0)) {
                if (thisChar == '\'' && strTmp.length() == 2) {
                    resultStr.add(new StringBuilder(51 + "\t:\t" + strTmp.charAt(1)));
                } else if (thisChar == '\"') {
                    resultStr.add(new StringBuilder(50 + "\t:\t" + strTmp.subSequence(1, strTmp.length())));
                } else {
                    // 异常处理
                    throw new CHARException("errorString:" + strTmp.toString() + "'\tchar类型变量错误：''中只能存放单个字符");
                }
                strTmp.delete(0, strTmp.length());
            }
        }
        return 1;
    }

    public int getINT(StringBuilder strTmp, char thisChar) {
        strTmp.append(thisChar);
        return 1;
    }

    public boolean isKeyWord(String inputStr) {
        if (thisWordMap.get(inputStr) == null) {
            return false;
        } else
            return thisWordMap.get(inputStr);
    }

    public int getSyn(String key) {
        return wordMap.get(key);
    }

    public StringBuilder initStrBulider(StringBuilder sb, String keyword) {
        sb.delete(0, sb.length());
        sb.append(keyword);
        return sb;
    }
}