package com.jiaruiblog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class InfixToRPN {
//
//    // 返回运算符的优先级
//    private static int getPriority(char op) {
//        switch (op) {
//            case '|':
//                return 1;
//            case '&':
//                return 2;
//            case '!':
//                return 3;
//            default:
//                return 0;
//        }
//    }
//
//    // 将中缀表达式转化为后缀表达式
//    public static String infixToRPN(String infix) {
//        StringBuilder output = new StringBuilder();  // 用于存储后缀表达式的字符串
//        Stack<Character> operatorStack = new Stack<>();  // 用于存储运算符的栈
//
//        for (char c : infix.toCharArray()) {
//            switch (c) {
//                case '(':  // 左括号直接入栈
//                    operatorStack.push(c);
//                    break;
//                case ')':  // 遇到右括号时，将栈中的运算符依次弹出，直到遇到左括号为止
//                    while (!operatorStack.empty() && operatorStack.peek() != '(') {
//                        output.append(' ').append(operatorStack.pop());
//                    }
//                    operatorStack.pop();  // 弹出左括号
//                    break;
//                case '|':
//                case '&':
//                case '!':
//                    // 当前运算符的优先级高于栈顶运算符的优先级时，直接入栈
//                    while (!operatorStack.empty() && getPriority(c) <= getPriority(operatorStack.peek())) {
//                        output.append(' ').append(operatorStack.pop());
//                    }
//                    operatorStack.push(c);
//                    break;
//                default:
//                    output.append(c);
//                    break;
//            }
//        }
//
//        // 将栈中剩余的运算符全部弹出
//        while (!operatorStack.empty()) {
//            output.append(' ').append(operatorStack.pop());
//        }
//
//        return output.toString().trim();  // 去除字符串两端的空格
//    }
//
//    public static void main(String[] args) {
//        String infix = "(dqx | (( zbw & PYB )))";
//        String rpn = infixToRPN(infix);
//        System.out.println(rpn);  // 输出：dqx zbw PYB & |
//
//
//        String input = rpn;
//        String[] tokens = input.split("\\s+"); // 将输入字符串按照空格分割成一个字符串数组
//        List<String> list = new ArrayList<>(Arrays.asList(tokens)); // 将字符串数组转换为ArrayList
//
//        for(String str:list){
//            System.out.println("str:"+str+"\"");
//        }
//        System.out.println(list);
//    }

    public static List<String> extractEmTags(String input) {
        List<String> matches = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<em>[^<]*</em>");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    public static void main(String[] args) {
        String input = "这是一个<em>测试</em>字符串，其中包含<em>多个</em>子串。";
        List<String> matches = extractEmTags(input);
        for (String match : matches) {
            System.out.println(match);
        }
    }
}
