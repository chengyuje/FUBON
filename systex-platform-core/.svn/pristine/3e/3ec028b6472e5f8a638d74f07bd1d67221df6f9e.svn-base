package com.systex.jbranch.platform.common.report.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.systex.jbranch.platform.common.report.generator.jfreechart.Series;

public class DataSetCalc {
	private Map<Object, Object> body = null;
	
	public DataSetCalc(){
		this.body = new HashMap<Object, Object>();
	}
	
	public synchronized void put(Object key, Object value){
		if(key != null && value != null){

			if(value instanceof BigDecimal){
				BigDecimal v = (BigDecimal) body.get(key);
				BigDecimal v2 = new BigDecimal(0);

				try{
					v2 = (BigDecimal) value;
				}catch(Exception e){
					//ignore
				}
				if(v == null){
					v = new BigDecimal(0);
				}
				v = v.add(v2);
				body.put(key, v);
			}else{
				Double v = null;
				Double v2 = new Double(0.0);
				try{
					v2 = Double.parseDouble(value.toString());
				}catch(Exception e){
//					ignore
				}
				
				if(body.get(key) == null){
					v = new Double(0.0);
				}else{
					v = ((Number) body.get(key)).doubleValue();
				}
				v += v2;
				body.put(key, v);
			}
		}
	}
	
	public synchronized Object get(Object key){
		return body.get(key);
	}

	public Set<Object> getKeySet(){
		return body.keySet();
	}
	
	public Series getSeries(){
		Iterator<Object> it = getKeySet().iterator();
		List<String> colNames = new ArrayList<String>();
		List<Number> numbers = new ArrayList<Number>();
		while(it.hasNext()){
			Object key = it.next();
			colNames.add((String) key);
			numbers.add((Number) get(key));
		}
		Series seri = new Series();
		seri.setCates(colNames.toArray(new String[colNames.size()]));
		seri.setValues(numbers.toArray(new Number[numbers.size()]));
		return seri;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return body.toString();
	}
}
