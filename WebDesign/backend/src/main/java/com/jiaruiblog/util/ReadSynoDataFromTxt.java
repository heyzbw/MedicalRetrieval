package com.jiaruiblog.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class ReadSynoDataFromTxt {
//    public List<List<String>> result = new ArrayList<>();

    public static List<List<String>> readDataFromTxt() throws IOException {
        List<List<String>> result = new ArrayList<>();

        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/static/synonyms.txt"));
        for(String line:lines){
            line = line.replaceAll("\"","");
            String[] elements = line.split(",");
            List<String> row = new ArrayList<>();
            for(String element:elements){
                row.add(element);
            }
            result.add(row);
        }
        return result;
    }

    public static String tokenNotSYno(String target,List<String> list){

        String regex = "<bm>([^*<>]+)</bm>";
        String replacement = "$1";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String s = matcher.group(1);
            if (!list.contains(s)) {
                matcher.appendReplacement(sb, replacement.replace("$1", s));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
