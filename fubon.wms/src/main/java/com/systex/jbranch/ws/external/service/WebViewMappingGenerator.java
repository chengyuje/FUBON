package com.systex.jbranch.ws.external.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WebViewMappingGenerator {
	public static void main(String...args){
		String path = "D:\\Development\\workspace\\fubon.wms.1.5\\src\\webapp";
		File rootFile = new File(path);
		
		for(File file : loadPath(rootFile)){
			String filePath = file.getPath();
			filePath = filePath.replaceAll(".*webapp\\\\", "");
//			System.out.println(filePath);
//			System.out.println(file.getName().replaceFirst("\\.html$", ""));
			
			System.out.println("<entry key=\"" + file.getName().replaceFirst("\\.html$", "") + "\" value=\"" + filePath + "\" />");
			
			
		}
	}
	
	public static List<File> loadPath(File file){
		List<File> list = new ArrayList<File>();
		
		if(file.isDirectory()){
			for(File subfile : file.listFiles()){
				list.addAll(loadPath(subfile));
			}
		}
		else if(file.isFile() && file.getName().matches(".*\\.html")){
			list.add(file);
		}
		return list;
	}
}
