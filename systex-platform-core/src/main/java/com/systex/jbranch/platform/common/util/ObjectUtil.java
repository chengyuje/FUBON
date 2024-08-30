package com.systex.jbranch.platform.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;


public class ObjectUtil {

	//supported multi thread 3rd party
	private static XStream XS = new XStream(new JettisonMappedXmlDriver());
	
	public static byte[] getBytes(Object obj) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(obj);
		out.flush();
		byte[] bytes = bout.toByteArray();
		bout.close();
		out.close();

		return bytes;
	}

	public static Object getObject(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(bi);
		Object obj = oi.readObject();
		bi.close();
		oi.close();
		return obj;
	}
	
	public static byte[] blobToByteArr(Blob blob) throws SQLException{
		return blob.getBytes(1, (int) blob.length());//若session已結束再操作blob將丟列外
	}
	
	public static Blob byteArrToBlob(byte[] bytes) throws SerialException, SQLException{
		return new SerialBlob(bytes);
	}
	
	public static Blob fileInputStreamToBlob(FileInputStream fis, Long length) throws IOException{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		return Hibernate.getLobCreator(session).createBlob(fis, length);
	}
	
	/**
	 *  Vo轉Map
	 * @param vo
	 * @return
	 */
	public static Map voToMap(Object vo) throws JBranchException{
		Map map = null;
		try {
			map = PropertyUtils.describe(vo);
			map.remove("class");
		} catch (Exception e) {
			throw new JBranchException(e.getMessage(), e);
		}
		
		return map;
	}
	
	/**
	 *  Vo轉Map<String, String>
	 * @param vo
	 * @return
	 */
	public static Map<String, String> voToStringMap(Object vo) throws JBranchException{
		Map map = null;
		try {
			map = PropertyUtils.describe(vo);
			map.remove("class");
			Set set = map.entrySet();
			Iterator<Entry> it = set.iterator();
			while(it.hasNext()){
				Entry entry = it.next();
				Object value = entry.getValue();
				if(value != null && value instanceof String == false){
					map.put(entry.getKey(), value.toString());
				}
			}
		} catch (Exception e) {
			throw new JBranchException(e.getMessage(), e);
		}
		
		return map;
	}
	
	/**
	 * Map轉Vo
	 * @param map
	 * @param vo
	 * @return
	 */
	public static Object mapToVo(Map map, Object vo){
		BeanMap bean = new BeanMap(vo);
		bean.putAll(map);
		return vo;
	}
	
	/**
	 * 複製類別成員
	 * @param dest 複製目地
	 * @param orig 複製來源
	 * @throws JBranchException
	 */
	public static void copyProperties(Object dest, Object orig) throws JBranchException{
		try {
			PropertyUtils.copyProperties(dest, orig);
		} catch (Exception e) {
			throw new JBranchException(e.getMessage(), e);
		}
	}
	
	public static Object jsonToVo(String json){
		return XS.fromXML(json);
	}
	
	public static String voToJson(Object vo){
		return XS.toXML(vo);
	}
	
}
