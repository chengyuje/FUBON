package com.systex.jbranch.platform.common.query.result.impl.hibernate;

import java.util.Map;


public class EntryImpl<K, V> implements Map.Entry<K, V>{

	private K key;
	private V value;
	
	
	public EntryImpl(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}
	
	public K setKey(K key){
		this.key = key;
		return this.key;
	}

}
