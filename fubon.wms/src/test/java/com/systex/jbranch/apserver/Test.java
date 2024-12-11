package com.systex.jbranch.apserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		 String input = "User: John, Age: 25";
	        String regex = "User: (\\w+), Age: (\\d+)";

	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(input);

	        if (matcher.find()) {
	            System.out.println("整體匹配: " + matcher.group(0)); // 整體匹配
	            System.out.println("第一組 (名字): " + matcher.group(1)); // 第一組 (名字)
	            System.out.println("第二組 (年齡): " + matcher.group(2)); // 第二組 (年齡)
	        }
		
	}
	
	
}


