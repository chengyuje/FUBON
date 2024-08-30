package com.systex.jbranch.platform.util;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.w3c.dom.Document;


public class PrimitiveMap<K> implements IPrimitiveMap<K> {

	protected Map map = null;

	public PrimitiveMap()
	{
		map = new HashMap();
	}

	public PrimitiveMap(Map map)
	{
		this.map = map;
	}

	public static PrimitiveMap<Object> valueOf(Map map)
	{
		return new PrimitiveMap<Object>(map);
	}

	public boolean getBool(K key) {

		Object o = get(key.toString());
		return o instanceof Boolean ? (Boolean)o : Boolean.valueOf(o.toString());
	}

	public byte getByte(K key) {

		Object o = get(key.toString());
		return o instanceof Number ? ((Number)o).byteValue() : Byte.valueOf(o.toString());
	}

	public char getChar(K key) {

		return (Character) get(key.toString());
	}

	public Date getDate(K key) {

		return (Date)get(key.toString());
	}

	public double getDbl(K key) {

		Object o = get(key.toString());
		return o instanceof Number ? ((Number)o).doubleValue() : Double.valueOf(o.toString());
	}

	public float getFloat(K key) {

		Object o = get(key.toString());
		return o instanceof Number ? ((Number)o).floatValue() : Float.valueOf(o.toString());
	}

	public int getInt(K key) {

		Object o = get(key.toString());
		return o instanceof Number ? ((Number)o).intValue() : Integer.valueOf(o.toString());
	}

	public List getList(K key) {

		return (List)get(key.toString());
	}

	public long getLong(K key) {

		Object o = get(key.toString());
		return o instanceof Number ? ((Number)o).longValue() : Long.valueOf(o.toString());
	}

	public IPrimitiveMap getMap(K key) {

		Object o = get(key.toString());
		if(o instanceof IPrimitiveMap)
			return (IPrimitiveMap)o;
		else
			return new PrimitiveMap((Map)o);
	}

	public Object getObj(K key) {

		return get(key.toString());
	}

	public short getShort(K key) {

		Object o = get(key.toString());
		return o instanceof Number ? ((Number)o).shortValue() : Short.valueOf(o.toString());
	}

	public String getStr(K key) {

		return ObjectUtils.toString(get(key.toString()));
	}

	public Document getXml(K key) {

		return (Document)get(key.toString());
	}

	public void setBool(K key, boolean value) {
		put(key.toString(),value);
	}

	public void setByte(K key, byte value) {
		put(key.toString(),value);
	}

	public void setChar(K key, char value) {
		put(key.toString(),value);
	}

	public void setDate(K key, Date value) {
		put(key.toString(),value);
	}

	public void setDbl(K key, double value) {
		put(key.toString(),value);
	}

	public void setFloat(K key, float value) {
		put(key.toString(),value);
	}

	public void setInt(K key, int value) {
		put(key.toString(),value);
	}

	public void setList(K key, List value) {
		put(key.toString(),value);
	}

	public void setLong(K key, long value) {
		put(key.toString(),value);
	}

	public void setMap(K key, Map value) {
		put(key.toString(),value);
	}

	public void setObj(K key, Object value) {
		put(key.toString(),value);
	}

	public void setShort(K key, short value) {
		put(key.toString(),value);
	}

	public void setStr(K key, String value) {
		put(key.toString(),value);
	}

	public void setXml(K key, Document value) {
		put(key.toString(),value);
	}

	public void clear() {

		map.clear();
	}

	public boolean containsKey(Object key) {

		return map.containsKey(key.toString());
	}

	public boolean containsValue(Object value) {

		return map.containsValue(value);
	}

	public Set entrySet() {

		return map.entrySet();
	}

	public Object get(Object key) {

		return map.get(key);
	}

	public boolean isEmpty() {

		return map.isEmpty();
	}

	public Set keySet() {

		return map.keySet();
	}

	public Object put(Object key, Object value) {

		return map.put(key, value);
	}

	public void putAll(Map t) {

		map.putAll(t);
	}

	public Object remove(Object key) {

		return map.remove(key);
	}

	public int size() {

		return map.size();
	}

	public Collection values() {

		return map.values();
	}
}
