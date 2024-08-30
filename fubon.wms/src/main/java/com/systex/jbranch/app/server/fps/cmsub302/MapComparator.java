package com.systex.jbranch.app.server.fps.cmsub302;

import java.util.Comparator;
import java.util.Map;
import java.util.Vector;

public class MapComparator implements Comparator<Map<String, String>> 
{
	public final class CompareKey {
		public String keyString;
		public int valueType;
		public int orderType;

		public CompareKey(String key, int valueType, int orderType) {
			this.keyString = key;
			this.valueType = valueType;
			this.orderType = orderType;
		}
	}

	public static final int VAL_TYPE_STRING = 'S';
	public static final int VAL_TYPE_INT = 'I';
	public static final int VAL_TYPE_DECIMAL = 'D';

	public static final int ORDER_TYPE_ASCENDING = 0;
	public static final int ORDER_TYPE_DESCENDING = 1;
	private final Vector<CompareKey> m_vecCompareKey;

	public MapComparator() {
		this.m_vecCompareKey = new Vector<CompareKey>();
	}

	public void addCompareKey(String strKey, int iValType, int iOrderType) {
		CompareKey key = new CompareKey(strKey, iValType, iOrderType);
		m_vecCompareKey.addElement(key);
	}

	public int compare(Map<String, String> first, Map<String, String> second) {
		for (int iIdx = 0; iIdx < m_vecCompareKey.size(); iIdx++) {
			CompareKey cmpKey = m_vecCompareKey.elementAt(iIdx);
			if (cmpKey == null)
				continue;

			String firstValue = first.get(cmpKey.keyString);
			String secondValue = second.get(cmpKey.keyString);
			int iCompValue = 0;

			if (cmpKey.valueType == VAL_TYPE_INT) {
				iCompValue = compareIntValue(firstValue, secondValue);
			} else if (cmpKey.valueType == VAL_TYPE_DECIMAL) {
				iCompValue = compareDecimalValue(firstValue, secondValue);
			} else {
				iCompValue = firstValue.compareTo(secondValue);
			}

			if (iCompValue != 0) {
				if (cmpKey.orderType == ORDER_TYPE_DESCENDING) {
					iCompValue = -iCompValue;
				}

				return iCompValue;
			}
		}
		return 0;
	}

	public int compareIntValue(String strFirst, String strSecond) {

		try {
			Integer intFirst = Integer.valueOf(strFirst);
			Integer intSecond = Integer.valueOf(strSecond);
			return intFirst.compareTo(intSecond);
		} catch (Exception e) {
			return strFirst.compareTo(strSecond);
		}

	}

	public int compareDecimalValue(String strFirst, String strSecond) {
		try {
			Double doubleFirst = Double.valueOf(strFirst);
			Double doubleSecond = Double.valueOf(strSecond);
			return doubleFirst.compareTo(doubleSecond);
		} catch (Exception e) {
			return strFirst.compareTo(strSecond);
		}
	}
}
