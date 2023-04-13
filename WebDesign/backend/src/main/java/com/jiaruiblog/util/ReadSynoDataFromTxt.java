package com.jiaruiblog.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public  class ReadSynoDataFromTxt {
//    public List<List<String>> result = new ArrayList<>();

    public static List<List<String>> readDataFromTxt() throws IOException {
        List<List<String>> result = new ArrayList<>();

        ClassPathResource resource = new ClassPathResource("static/synonyms.txt");
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        for (String line : lines) {
            line = line.replaceAll("\"", "");
            String[] elements = line.split(",");
            List<String> row = new ArrayList<>();
            for (String element : elements) {
                row.add(element);
            }
            result.add(row);
        }
        return result;
    }


    public static String tokenNotSYno(String target,List<String> list){

        System.out.println("原本的数据为："+target);
        System.out.print("同义词有：");
        for(String word:list){
            System.out.print(word);
        }
        String regex = "<bm>([^*<>]+)</bm>";
        String replacement = "$1";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String s = matcher.group(1);
            System.out.println("正则表达式输出为："+s);
            if (!list.contains(s)) {
                matcher.appendReplacement(sb, replacement.replace("$1", s));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
