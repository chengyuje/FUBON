package com.systex.jbranch.fubon.commons.cbs.entity;

import com.systex.jbranch.fubon.commons.cbs.inf.IDPattern;

import java.util.regex.Pattern;

/**
 * 判斷客戶的 ID TYPE
 *
 * 該類別 For 電文用
 * 資料庫則是交給 FN_GET_CBS_ID_TYPE.sql 判斷
 * @author Eli
 * @date 2018023
 *
 */
public enum CBSIDPattern implements IDPattern {
	/**
	 * 19-內政部配賦統一證號
	 * 10位數字：9999999999
	 * 7位數字：9999999
	 */
	MOI {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^[\\d]{7}|[\\d]{10}$");
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}

		@Override
		public String getCode() {
			return "19";
		}
	},
	/**
	 * 21-本國法人統一編號
	 * 8位數字：99999999
	 * 6位數字+2碼空白+1碼檢查碼：999999__9
	 */
	INC {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^(\\d{8}|\\d{6}\\s{2}\\d)$");
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}

		@Override
		public String getCode() {
			return "21";
		}
	},
	/**
	 * 22-境外法人虛擬統編
	 * 4位英文+4位數字：XXXX9999
	 * **/
	OINC {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^[A-Z]{4}[\\d]{4}$", Pattern.CASE_INSENSITIVE);
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}

		@Override
		public String getCode() {
			return "22";
		}
	},
	/**
	 * 29-境外法人(免送聯徵)，OBU 法人
	 * 3位數字+2位英文+3位數字：999XX999
	 */
	OBU {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^[\\d]{3}[A-Z]{2}[\\d]{3}$", Pattern.CASE_INSENSITIVE);
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}

		@Override
		public String getCode() {
			return "29";
		}
	},
	/**
	 * 32-虛擬ID
	 * "88"+9位英文：99XXXXXXXXX
	 * "88"+6位英文：88XXXXXX
	 * "88"+8位英文：88XXXXXXXX
	 */
	MT {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^88(([A-Z]{6})|([A-Z]{8,9}))$", Pattern.CASE_INSENSITIVE);
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}

		@Override
		public String getCode() {
			return "32";
		}
	},
	/**
	 * 11-身分證統一編號
	 * 1位英文+9位數字(第二位0~7)：X999999999
	 * 1位英文+9位數字(第二位0~7)+1位檢查碼：X9999999999
	 */
	ID {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^[A-Z][01234567]\\d{8}(\\d)?$", Pattern.CASE_INSENSITIVE);
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}
			
		@Override
		public String getCode() {
			return "11";
		}
	},
	/**
	 * 12-統一證號
	 * 1位英文+9位數字(第二位8~9)：X999999999
	 * 2位英文+8位數字：XX99999999
	 */
	ARC {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^([A-Z]{2}[\\d]{8}|[A-Z][89]\\d{8})$", Pattern.CASE_INSENSITIVE);
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}

		@Override
		public String getCode() {
			return "12";
		}
	},
	/**
	 * 13-境外自然人證號
	 * 8位數字+2位英文（出生年月日 + 姓名前 2 碼）：99999999XX
	 * 8位數字+2位英文+1位檢查碼（第 11 碼為檢查碼）：99999999XX9
	 */
	OID {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^[\\d]{8}[A-Z]{2}\\d?$", Pattern.CASE_INSENSITIVE);
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}

		@Override
		public String getCode() {
			return "13";
		}
	},
	/**
	 * 31-集團ID
	 * "GRP"+8位數字：XXX99999999
	 */
	GRP {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^GRP[\\d]{8}$");
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}

		@Override
		public String getCode() {
			return "31";
		}
	},
	/**
	 * 23-SWIFT CODE
	 * 6位英文+2位英數：XXXXXXZZ
	 * 6位英文+2位英數+3位英數：XXXXXXZZZZZ
	 */
	SWIFT {
		@Override
		public Pattern getPattern() {
			return Pattern.compile("^[A-Z]{6}([A-Z0-9]{2}|[A-Z0-9]{5})$", Pattern.CASE_INSENSITIVE);
		}

		@Override
		public boolean match(String id) {
			return this.getPattern().matcher(id).matches();
		}

		@Override
		public String getCode() {
			return "23";
		}
	},
	/**
	 * 39-舊資料轉置
	 * 不屬於其他類型 ID TYPE
	 */
	OTH {
		@Override
		public Pattern getPattern() {
			return null;
		}

		@Override
		public boolean match(String id) {
			return true;
		}

		@Override
		public String getCode() {
			return "39";
		}
	}
}
