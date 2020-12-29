package com.xjosiah.grammer;

import java.util.Stack;

/**
 * 根据分析表来分析特定字符串是否符合语法规矩的一个ll(1)分析器
 *
 * @since 2020.12.28
 */
public class StackParser {
    private String[][] analyzeTable;
    private String[] VtTable;
    private Stack<String> expStack = new Stack<>();
    //  行数
    private int ananlyzeTableRow;
    //  列数
    private int annnlyzeTableCol;

    /**
     * 基于栈的分析器
     *
     * @param analyerTable 分析表
     */
    public StackParser(String[][] analyerTable) {

        this.analyzeTable = analyerTable;
        ananlyzeTableRow = analyerTable.length;
        annnlyzeTableCol = analyerTable[0].length;

        VtTable = new String[annnlyzeTableCol - 1];

        for (int i = 0; i < VtTable.length; i++) {
            VtTable[i] = analyerTable[0][i + 1];
        }

    }

    /**
     * 初始化分析栈
     */
    private void initExpStack() {
        if (!expStack.empty())
            expStack.clear();
        expStack.push("#");
        expStack.push("E");
    }

    /**
     * 分析字符串
     *
     * @param input 需分析的字符串
     */
    public void analyzeStr(String input) {
        System.out.println("符号栈\t\t\t当前输入符号\t\t\t输入串\t\t\t");
        initExpStack();
        //  存放输入字符串的 StringBuilder 并在末尾自动补全一个结束符
        StringBuilder inputStrBuilder = new StringBuilder(input);
        inputStrBuilder.append("#");
        String inputChar = getInputChar(inputStrBuilder);
        //  存放从栈中弹出的符号
        String x;
        do {
            printTackleMsg(inputChar,inputStrBuilder.toString());
            x = expStack.pop();
            if (inVt(x)) {
                if (x.equals(inputChar)) {
                    if (!inputChar.equals("#"))
                        inputChar = getInputChar(inputStrBuilder);
                } else {
                    error(inputChar);
                    return;
                }
            } else {
                if (!tackleInAnalyzerTable(x, inputChar)) {
                    error(inputChar);
                    return;
                }
            }
        } while (!(inputChar.equals("#")&&x.equals("#")));
        System.out.println("分析成功！");
    }

    /**
     * 获取当前要处理的字符，并从输入字符串中剔除
     *
     * @param sb 输入字符串
     * @return 要处理的字符
     */
    private String getInputChar(StringBuilder sb) {
        char inputChar = sb.charAt(0);
        sb.deleteCharAt(0);
        return String.valueOf(inputChar);
    }

    /**
     * 查找当前字符是否为终结符
     *
     * @param x
     * @return
     */
    private boolean inVt(String x) {
        for (String vt : VtTable) {
            if (vt.equals(x))
                return true;
        }
        return false;
    }

    /**
     * 输出错误信息
     *
     * @param errorChar
     */
    private void error(String errorChar) {
        System.err.println("【输入串错误】该字符无法处理： " + errorChar);
    }

    /**
     * 利用分析表处理数据
     *
     * @param x         刚从符号栈顶弹出的元素
     * @param inputChar 当前输入符号
     * @return 是否成功分析
     */
    private boolean tackleInAnalyzerTable(String x, String inputChar) {
        int row = -1;
        for (int i = 0; i < ananlyzeTableRow - 1; i++) {
            if (analyzeTable[i + 1][0].equals(x)) {
                row = i + 1;
                break;
            }
        }
        //  从分析表中找不到符合的非终结符
        if (row == -1) {
            return false;
        }

        int col = -1;
        for (int i = 0; i < annnlyzeTableCol - 1; i++) {
            if (analyzeTable[0][i + 1].equals(inputChar)) {
                col = i + 1;
                break;
            }
        }
        //  找不到对应的终结符
        if (col == -1) {
            return false;
        }

        //  从分析表中取到的表达式 即 analyzeTable[对应的非终结符下标][对应的终结符下标]
        String analyzerExpStr;
        //  异常处理
        if ((analyzerExpStr = analyzeTable[row][col]).equals("")) {
            return false;
        }

        //  如果为 ε 则不需要压栈
        if (analyzerExpStr.equals("ε"))
            return true;

        //  逆序压栈
        for (int i = analyzerExpStr.length() - 1; i >= 0; i--) {
            expStack.push(String.valueOf(analyzerExpStr.charAt(i)));
        }
        return true;
    }

    /**
     * 输出信息
     * @param inputChar
     * @param inputStr
     */
    private void printTackleMsg(String inputChar,String inputStr){
        StringBuilder expStackStr = new StringBuilder();
        for (String s :
                expStack) {
            expStackStr.append(s);
        }
        System.out.print(expStackStr);
        int trims = expStackStr.length() / 4;
        for (int i=5;i>trims;i--){
            System.out.print("\t");
        }
        System.out.print(inputChar+"\t\t\t\t"+inputStr+"\r\n");
    }
}
