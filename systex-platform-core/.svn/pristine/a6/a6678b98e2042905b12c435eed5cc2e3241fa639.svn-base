package com.systex.jbranch.platform.common.report.generator.msoffice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class BookmarkData {
	
	private Map<String, BookmarkDetail> map = new HashMap<String, BookmarkDetail>();
	
	public void setBookmarkInfo(String name, BookmarkDetail bd){

		map.put(name, bd);
	}
	
	public BookmarkDetail getDetail(String name){
		
		return map.get(name);
	}
	
	public Set<Entry<String, BookmarkDetail>> iterator(){
		
		return map.entrySet();
	}
}
