package com.jiaruiblog.entity;

//帮我创建一个有三个字符串的枚举类，其值为“correct”、“summarize”、“ask”

public enum AskOptional {
    CORRECT("correct"),
    SUMMARIZE("summarize"),
    ASK("ask"),
    ADVICE("advice");

    private String value;

    AskOptional(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
