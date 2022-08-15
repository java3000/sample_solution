package ru.vk.competition.minbenchmark;

import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String QUERY_PATTERN = "([a-zA-Z*\\s]+)"; // "Customerлала = валидное имя :)"
        //String QUERY_PATTERN = "^(select)(.+)([A-Z][a-z]+)+$"; // "Customerлала = валидное имя :)"
        String target = "select * from Album";
        Pattern pattern = Pattern.compile(QUERY_PATTERN, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

        System.out.println(pattern.matcher(target).matches());
        System.out.println(pattern.matcher(target).find());
    }
}
