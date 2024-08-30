package com.systex.jbranch.platform.common.query.result.impl.hibernate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * 實作Set interface
 *
 */
public class KeySet implements Set{
	
	private Set<String> keys = new HashSet<String>();
	private String[] columnNames = null;
	
	KeySet(String[] columnNames){
		this.columnNames = columnNames;
		for(int i=0; i<columnNames.length; i++){
			keys.add(columnNames[i]);
		}
	}
	
	public boolean add(Object o){
		throw new UnsupportedOperationException("add() Unsupported");
	}
	
	public boolean remove(Object o){
		throw new UnsupportedOperationException("remove() Unsupported");
	}
	
	public boolean addAll(Collection c){
		throw new UnsupportedOperationException("addAll() Unsupported");
	}
	
	public void clear(){
		throw new UnsupportedOperationException("clear() Unsupported");
	}
	
	public boolean contains(Object o){
		return keys.contains(o);
	}
	
	public boolean containsAll(Collection c){
		return keys.containsAll(c);
	}
	
	public boolean equals(Object o){
		return keys.equals(o);
	}

	public int hashCode(){
		return keys.hashCode();
	}

	public boolean isEmpty(){
		return keys.isEmpty();
	}

	public Iterator iterator(){
		return keys.iterator();
	}
 
	public boolean removeAll(Collection c){
		throw new UnsupportedOperationException("removeAll() Unsupported");
	}

	public boolean retainAll(Collection c){
		return keys.retainAll(c);
	}

	public int size(){
		return keys.size();
	}

	public Object[] toArray(){
		return keys.toArray();
	}

	public Object[] toArray(Object[] a){
		return keys.toArray(a);
	}

}
