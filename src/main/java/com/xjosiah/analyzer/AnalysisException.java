package com.xjosiah.analyzer;

/**
 * 通用的词法分析异常
 * @author xjosiah
 */
public class AnalysisException extends Throwable{
    public AnalysisException(String message) {
        super(message);
    }
}
