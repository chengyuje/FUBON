/**
 * 
 */
package com.systex.jbranch.fubon.commons;

import java.util.Arrays;

import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class ValidUtil {
	/**
	 * 身分證及統編欄位的檢核.
	 *<p>
	 *<ol>
	 * <li>輸入位數限7位,8位,10位,11位</li>
	 * <li>若為公司(境內)統編,做公司統編檢查</li>
	 * <li>若為個人(本國人)統編,做身份證統編檢查</li>
	 * <li>若為個人(外國人1)統編,做外國人統一證號檢查</li>
	 * <li>若要輸入誤別,需先於統編後補空白,再於第11碼補誤別</li>
	 * <li>若公司統編欄位輸入11位(第9及第10位必須為空白)</li>
	 * <li>誤別規則:
	 * <ol>
	 * <li>第11位為空按,檢查統編</li>
	 * <li>第11位為2~9、A~Z,(表重號)檢查統編</li>
	 * <li>第11位為1,(表機關編錯)不檢查統編</li>
	 * </ol>
	 * </li>
	 *</ol>
	 *<p>
	 * @throws JBranchException 
	 */
	public static boolean isValidIDorRCNumber(String item_value) throws JBranchException {

		// 若欄位為空白，03677367(港基)或VG225567時，則不做檢核----->黃振興要求
		if (item_value.trim().length() == 0 || "03677367".equals(item_value) || "VG225567".equals(item_value))
			return true;

		// 統一編號輸入錯誤:輸入位數有誤, 輸入位數限7位, 8位,10位,11位
		if (!(item_value.length() == 7 || item_value.length() == 8
				|| item_value.length() == 10 || item_value.length() == 11)) {
			return false;
		}

		item_value = item_value.toUpperCase();
		
		/*
		 * format檢核通過,則回傳符合的類型 return: -1 : 表error 0 : 公司(境內,一般法人) 8碼格式(或含誤別)
		 * 99999999 1 : 公司(境外,聯徵提供) 8碼格式(或含誤別) XXXX9999 2 : 公司(境外,免送聯徵)
		 * 8碼格式(或含誤別) 999XX999 3 : 彙總帳號(免送聯徵) 8碼格式(或含誤別) 999X9999 4 :
		 * 個人(本國人,一般個人) 10碼格式(或含誤別) X999999999 5 : 個人(外國人1) 10碼格式(或含誤別)
		 * XX99999999 6 : 個人(外國人2) 10碼格式(或含誤別) 99999999XX 7 : 個人(大陸人,有居民證)
		 * 10碼格式(或含誤別) 9999999999 8 : 個人(大陸人,無居民證) 7碼格式(或含誤別) 9999999
		 */
		int ret = validateFormat(item_value);

		// 如果不符合format, 則return, 因message已在validationFormat顯示, 故不再顯示
		if (ret == -1)
			return false;

		// 補滿11位
		if (ret >= 0 && item_value.length() != 11) {
			item_value = fillSpace(item_value, 10, false);
			item_value = item_value + " ";
		}

		char char11 = item_value.charAt(10); // 最末1碼

		if (!((char11 >= '2' && char11 <= '9')
				|| (char11 >= 'A' && char11 <= 'Z') || (char11 == ' '))) {
			// 誤別不為2~9, A~Z或空白者,即為1, 不檢查
		} else {
			// 誤別為2~9, A~Z或空白者,則檢查
			// 公司統編檢查
			if (ret == 0) {
				if (!validateCoID(item_value)) {
					// CHECK0005 營利事業統一編號輸入錯誤:檢核錯誤
					throw new APException("營利事業統一編號輸入錯誤");
				}
			}
			// 個人(本國人)身份證號檢查
			if (ret == 4) {
				if (!validateID(item_value, true)) {
					// CHECK0007 國民身份證統一編號輸入錯誤:檢核錯誤
					throw new APException("國民身份證統一編號輸入錯誤");
				}
			}
			// 個人(外國人1)統一證號檢查
			if (ret == 5) {
				Boolean chkFlag = false;
				if (validateForeID(item_value, true)) {
					chkFlag = true;
				}
				if (validateID(item_value, true)) {
					chkFlag = true;
				}
				if (!chkFlag) {
					// CHECK0101 外國人統一證號輸入錯誤:檢核錯誤
					throw new APException("外國人統一證號輸入錯誤");					
				}
			}
		}
		return true;
		//System.out.println("公司統一編號/身份證號" + item_value + "檢核OK");
	}

	/**
	 * 公司統一編號/身份證號format檢核.
	 *<p>
	 * 
	 * <br>
	 * 2002/05/20異動申請單(FC-01) by 余鈴羚 && 授管處賴志祥 <br>
	 * <br>
	 * 一、統編格式: <br>
	 * <br>
	 * 種類 長度 格式 格式+誤別 檢核原則 <br>
	 * ----------------------- ---- ----------- ----------- ------------------ <br>
	 * 公司(境內,一般法人) 8碼 99999999 99999999 X 檢核公式1 <br>
	 * 公司(境外,聯徵提供) 8碼 XXXX9999 XXXX9999 X 英文字須為大寫,前2碼為國家別,3-7為流水號,最末碼為檢查號 <br>
	 * 公司(境外,免送聯徵) 8碼 999XX999 999XX999 X 英文字須為大寫,前3碼為分行代號,4-5為國家別,6-8為流水號 <br>
	 * 彙總帳號(免送聯徵) 8碼 999X9999 999X9999 X 英文字須為大寫,前3碼為分行代號,4-8為流水號 <br>
	 * <br>
	 * 個人(本國人,一般個人) 10碼 X999999999 X999999999X 第二碼僅可為1~2,檢核公式2 <br>
	 * 個人(外國人1) 10碼 XX99999999 XX99999999X 第二碼僅可為A~F,檢核公式3 <br>
	 * 個人(外國人2) 10碼 99999999XX 99999999XXX 英文字須為大寫 <br>
	 * 個人(大陸人,有居民證) 10碼 9999999999 9999999999X 英文字須為大寫 <br>
	 * 個人(大陸人,無居民證) 7碼 9999999 9999999 X 第一碼需為9 <br>
	 * 95/12/14 add by 伶憶： <br>
	 * SWIFT CODE 11碼 XXXXXX----- 前六碼為英文，後五碼不限文數字 <br>
	 * <br>
	 * 二、檢核原則: <br>
	 * <br>
	 * (1)檢核公式1:(公司統編8碼) <br>
	 * 1.統編每一碼各自 * 基數後, 十位數與個位數相加 <br>
	 * 2.將所有值累加起來, 若第7碼為7, 先減7, 若能除以10能整除, 即符合檢核 <br>
	 * <br>
	 * 統編 A B C D E F G H <br>
	 * x x x x x x x x <br>
	 * 基數 1 2 1 2 1 2 4 1 <br>
	 * <br>
	 * 1.SUM = A*1 + (B*2/10 + B*2%10) + C*1 + (D*2/10 + D*2%10) + E*1 + (F*2/10
	 * + F*2%10) + <br>
	 * (G*4/10 + G*4%10) + H*1 <br>
	 * 2.若G = 7 , SUM = SUM -7 <br>
	 * 3.SUM%10 若為0, 則符合, 否則為錯誤 <br>
	 * <br>
	 * (2)檢核公式2:(個人統編10碼) <br>
	 * 1.第一碼字母對應定義的值 <br>
	 * 2.統編每一碼各自 * 基數後, <br>
	 * 3.將所有值累加起來, 以10 減去 累計值除以10的餘數, 得到檢碼, 檢碼若為10, 則檢碼為0 <br>
	 * 4.檢碼若與最末碼相符, 即符合檢核 <br>
	 * <br>
	 * 統編 A B C D E F G H I J <br>
	 * x x x x x x x x <br>
	 * 基數 X 8 7 6 5 4 3 2 1 <br>
	 * <br>
	 * 1.SUM = X(代表的值) + B*8 + C*7 + D*6 + E*5 + F*4 + G*3 + H*2 + I*1 <br>
	 * 2.CHECK = 10 - SUM%10, if CHECK = 10 then CHECK = 0 <br>
	 * 3.if J = CHECK, 則符合, 否則為錯誤 <br>
	 * <br>
	 * (3)檢核公式3:(外國人統編10碼) <br>
	 * 1.第一碼字母轉換為數字, 如A轉為10, B轉為11... <br>
	 * 2.第二碼字母僅可為A~F, 轉換為數字, 再取其個位數, 如A轉為10, 取個位數為0, <br>
	 * 3.統編每一碼各自 * 基數後, <br>
	 * 4.將所有值累加起來, 以10 減去 累計值除以10的餘數, 得到檢碼, 檢碼若為10, 則檢碼為0 <br>
	 * 5.檢碼若與最末碼相符, 即符合檢核 <br>
	 * <br>
	 * 統編 A B C D E F G H I J <br>
	 * 轉碼 A1 A2 B C D E F G H I J <br>
	 * x x x x x x x x x x <br>
	 * 基數 1 9 8 7 6 5 4 3 2 1 <br>
	 * 1.SUM = A1*1 + A2*9 + B*8 + C*7 + D*6 + E*5 + F*4 + G*3 + H*2 + I*1 <br>
	 * 2.CHECK = 10 - SUM%10, if CHECK = 10 then CHECK = 0 <br>
	 * 3.if J = CHECK, 則符合, 否則為錯誤 <br>
	 * <br>
	 * (4)其它: <br>
	 * 1.個人(大陸人,無居民證)中第一位數需為9 <br>
	 * 2.英文字皆須為大寫 <br>
	 * 3.誤別可為 空白 (檢查統編, id), <br>
	 * 1 (機關編錯,不檢查統編, id) <br>
	 * 2~9, A~Z (重號,其中Z為呆帳誤別專用)
	 * 
	 * @return format檢核通過,則回傳符合的類型 <br>
	 *         -1 : 表error <br>
	 *         0 : 公司(境內,一般法人) 8碼格式(或含誤別) 99999999 <br>
	 *         1 : 公司(境外,聯徵提供) 8碼格式(或含誤別) XXXX9999 <br>
	 *         2 : 公司(境外,免送聯徵) 8碼格式(或含誤別) 999XX999 <br>
	 *         3 : 彙總帳號(免送聯徵) 8碼格式(或含誤別) 999X9999 <br>
	 *         4 : 個人(本國人,一般個人) 10碼格式(或含誤別) X999999999 <br>
	 *         5 : 個人(外國人1) 10碼格式(或含誤別) XX99999999 <br>
	 *         6 : 個人(外國人2) 10碼格式(或含誤別) 99999999XX <br>
	 *         7 : 個人(大陸人,有居民證) 10碼格式(或含誤別) 9999999999 <br>
	 *         8 : 個人(大陸人,無居民證) 7碼格式(或含誤別) 9999999 <br>
	 *         9 : SWIFT CODE 11碼格式 XXXXXX-----
	 */
	
	public static int validateFormat(String idno) throws APException {
		String[] id_temp = new String[11];
		id_temp[0] = "99999999"; // 公司(境內,一般法人)
		id_temp[1] = "XXXX9999"; // 公司(境外-聯徵提供,境外-免送聯徵)
		id_temp[2] = "999XX999";
		id_temp[3] = "999X9999"; // 彙總帳號(免送聯徵)
		id_temp[4] = "X999999999"; // 個人(本國人,一般個人)
		id_temp[5] = "XX99999999";
		id_temp[6] = "99999999XX"; // 外國人統編
		id_temp[7] = "9999999999";
		id_temp[8] = "9999999";
		id_temp[9] = "XXXXXX-----";
		id_temp[10] = "XXXXXX--";

		String ori_idno = idno;
		boolean check_flag = false;
		int ret = -1;

		if (idno == null || idno.trim().length() < 7) {
			return -1;
		}

		// 若有誤別, 則先去除誤別, 再判定格式
		if (idno.length() == 11) {
			int count_alpha = 0;
			for (int i = 0; i < 6; i++) {
				if ((idno.charAt(i) >= 'A' && idno.charAt(i) <= 'Z') || 
					(idno.charAt(i) >= 'a' && idno.charAt(i) <= 'z')) {
					count_alpha++;
				}
			}
			if (count_alpha == 6) {
				// 環球銀行金融電信協會（Society for Worldwide Interbank Financial Telecommunication）
				// idno可能為SWIFT CODE，維持11碼
			} else {
				idno = idno.substring(0, 10).trim();
			}
		}

		for (int i = 0; i < id_temp.length; i++) {
			// 判斷是否符合任一個統編的長度
			if (idno.length() == id_temp[i].length()) {
				check_flag = true; // 如果有符合, 則設chec_flag = true;
			} else {
				check_flag = false;
				continue;
			}
			//System.out.println(id_temp[i]);
			// 判斷檢核是否相同
			for (int j = 0; j < id_temp[i].length(); j++) {
				char check = id_temp[i].charAt(j);
				char value = idno.charAt(j);
				if (check == '9') {
					if (value >= '0' && value <= '9') {
						check_flag = true;
					} else {
						check_flag = false;
						break;
					}
				}

				if (check == 'X') {
					if ((value >= 'A' && value <= 'Z')) {
						check_flag = true;
					} else {
						check_flag = false;
						break;
					}
					// SWIFT CODE 後五碼不限文數字
				} else if (check == '-') {
					if ((value >= 'A' && value <= 'Z') || 
						(value >= 'a' && value <= 'z') || 
						(value >= '0' && value <= '9')) {
						check_flag = true;
					} else {
						check_flag = false;
						break;
					}
				} else if (check != '9' && check != 'X') {
					if (value == check) {
						check_flag = true;
					} else {
						check_flag = false;
						break;
					}
				}
			}
			
			//WMS-CR-20200130-01_調整外來人口統一證號
			if(i==4 && (idno.charAt(1)=='8' || idno.charAt(1)=='9')){
				check_flag = true;
			}

			// 若符合某一個格式,就不再判斷其它格式			
			if (check_flag == true) {
				ret = i;
				break;
			}
		}
		//System.out.println("check_flag is:" + check_flag + " ret is:" + ret);

		char firstchar = idno.charAt(0); // 第1碼值
		char char2 = idno.charAt(1); // 第2碼值
		char char4 = idno.charAt(3); // 第4碼值
		char char5 = idno.charAt(4); // 第5碼值
		char char9 = ' '; // 第9碼值
		char char11 = ' '; // 最末1碼

		if (ret == -1) {
			if (idno.length() == 7) {
				// CHECK0087 個人(大陸人-無居民證)統編, 7碼皆須為數字
				throw new APException("個人(大陸人-無居民證)統編, 7碼皆須為數字");
			}
			if (idno.length() == 8) {
				if (!(firstchar >= '0' && firstchar <= '9')) {
					// CHECK0089 公司(境外-聯徵提供)統編,前4碼皆須為大寫英文字, 後4碼皆須為數字
					throw new APException("公司(境外-聯徵提供)統編,前4碼皆須為大寫英文字, 後4碼皆須為數字");
				} else {
					if (!(char4 >= '0' && char4 <= '9')) {
						if (!(char5 >= '0' && char5 <= '9')) {
							// CHECK0091
							// 彙總帳號(免送聯徵),前3碼為分行代號,4-8為流水號,第4碼須為英文字,餘為數字
							throw new APException("彙總帳號(免送聯徵),前3碼為分行代號,4-8為流水號,第4碼須為英文字,餘為數字");
						} else {
							// CHECK0090
							// 公司(境外-免送聯徵)統編,前3碼為分行代號,4-5碼為國家代碼,6-8為流水號
							// ,4-5碼須為英文字,餘為數字
							throw new APException("公司(境外-免送聯徵)統編,前3碼為分行代號,4-5碼為國家代碼,6-8為流水號,4-5碼須為英文字,餘為數字");
						}
					} else {
						// CHECK0088 公司(一般)統編,8碼皆須為數字
						throw new APException("公司(一般)統編,8碼皆須為數字");
					}
				}
			}
			if (idno.length() == 10) {
				char9 = idno.charAt(8); // 第9碼值
				if (!(firstchar >= '0' && firstchar <= '9')) {
					if (!(char2 >= '0' && char2 <= '9')) {
						// CHECK0099 個人(外國人1)統編, 前2碼須為大寫英文,後8碼皆為數字
						throw new APException("個人(外國人1)統編, 前2碼須為大寫英文,後8碼皆為數字");
					} else {
						// CHECK0084 個人(本國人,一般個人)統編,第1碼須為大寫英文,後9碼皆為數字
						throw new APException("個人(本國人,一般個人)統編,第1碼須為大寫英文,後9碼皆為數字");
					}
				} else {
					if (!(char9 >= '0' && char9 <= '9')) {
						// CHECK0085 個人(外國人2)統編, 前8碼須為數字, 第9第10碼須為大寫英文字
						throw new APException("個人(外國人2)統編, 前8碼須為數字, 第9第10碼須為大寫英文字");
					} else {
						// CHECK0086 個人(大陸人,有居民證)統編, 10碼皆須為數字
						throw new APException("個人(大陸人,有居民證)統編, 10碼皆須為數字");
					}
				}
			} else if (idno.length() == 11) {
				// CHECK0301 輸入統編不符合Swift code、公司或個人統編格式
				throw new APException("輸入統編不符合Swift code、公司或個人統編格式");
			} else {
				// CHECK0093 輸入統編不符合公司或個人統編格式
				throw new APException("輸入統編不符合公司或個人統編格式");
			}
		}

		// 補滿11位
		if (ret >= 0) {
			if (ret == 9 || ret == 10) {
				// SWIFT CODE不做以下檢核，RETURN
				return ret;
			} else {
				idno = fillSpace(ori_idno, 11, false);
			}
		}

		char11 = idno.charAt(10); // 最末1碼

		// 誤別僅為1-9, A-Z或空白
		if (!((char11 >= '1' && char11 <= '9') || (char11 >= 'A' && char11 <= 'Z') || (char11 == ' '))) {
			// CHECK0039 誤別(第11碼)請輸入 1-9 , A-Z, a-z 或空白
			throw new APException("誤別(第11碼)請輸入 1-9 , A-Z, a-z 或空白");
		}

		// 當檢核個人(本國人,一般個人)身份時,第二碼須為1 or 2
		if (ret == 4) {
			if (!(char2 == '1' || char2 == '2' || char2 == '8' || char2 == '9')) {
				// CHECK0008 國民身份證統一編號輸入錯誤:第二碼應為1或2
				throw new APException("國民身份證統一編號輸入錯誤:第二碼應為1或2。個人(外國人1)統一編號輸入錯誤:第二碼應為8或9。");
			}
		}

		// 當檢核個人(外國人1)身份時,第二碼須為A~D(原為A~F)
		if (ret == 5) {
			if (!(char2 >= 'A' && char2 <= 'D')) {
				// CHECK0100 個人(外國人1)統一編號輸入錯誤:第二碼應為A,B,C,D,E或F
				throw new APException("個人(外國人1)統一編號輸入錯誤:第二碼應為A,B,C,D,E或F");
			}
		}

		// 當檢核個人(大陸人,無居民證) 身份時,第一碼須為9
		if (ret == 8) {
			if (!(firstchar == '9')) {
				// CHECK0092 個人(大陸人,無居民證) 統一編號輸入錯誤:第一碼應為9
				throw new APException("個人(大陸人,無居民證)	統一編號第一碼應為9");
			}
		}
		return ret;
	}

	/**
	 * 公司統一編號檢核. <BR>
	 * 檢核通過則回傳true
	 * 
	 * @return boolean
	 */
	private static boolean validateCoID(String s) throws APException {
		char ch = s.charAt(0);
		// 因發生經濟部網站查得到此一公司統編，故放開
		if ("90346564".equals(s.trim())) {
			return true;
		}
		int sum = 0, ch_value = 0;
		for (int i = 0; i < 8; i++) {
			ch = s.charAt(i);
			if (!((ch >= '0') && (ch <= '9'))) {
				// 營利事業統一編號輸入錯誤:請輸入8碼數字
				throw new APException("營利事業統一編號請輸入8碼數字");
			}
			if (i == 1 || i == 3 || i == 5) {
				ch_value = Character.getNumericValue(ch) * 2;
				sum += ch_value / 10 + ch_value % 10;
			} else if (i == 6) {
				ch_value = Character.getNumericValue(ch) * 4;
				sum += ch_value / 10 + ch_value % 10;
			} else {
				sum += Character.getNumericValue(ch);
			}
		}
		if (sum % 5 != 0) {
			if (s.charAt(6) == '7') {
				sum -= 9;
				if (sum % 5 != 0) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 身分證編號檢核. <BR>
	 * 檢核通過則回傳true
	 * 
	 * @return boolean
	 */
	public static boolean validateID(String s, boolean do_checksum) {
		if (do_checksum) {
			char check_digit = getChecksumDigit(s);
			if (check_digit == s.charAt(9)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 無戶籍人民身份一證號檢核. <BR>
	 * 檢核通過則回傳true
	 * 
	 * @return boolean
	 */
	public static boolean validateForeID(String s, boolean do_checksum) {
		if (do_checksum) {
			char check_digit = getForeChecksumDigit(s);
			if (check_digit == s.charAt(9)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 對輸入字串依指定字元補滿到指定長度.
	 * 
	 * @param <code>in</code> 輸入字串
	 * @param <code>length</code> 指定長度
	 * @param <code>right</code> 是否右靠
	 * @param <code>ch</code> 指定字元
	 * @return 補滿後字串
	 * 
	 * @author Malo Jwo 2000/11/13
	 */
	public static String fillSpace(String in, int length, boolean right) {
		StringBuffer sb = new StringBuffer();
		if (in == null) {
			in = "";
		}
		int inLength = in.length();
		if (inLength >= length) {
			return in;
		} else {
			if (right) {
				for (int i = 0; i < length - inLength; i++) {
					sb.append(' ');
				}
				sb.append(in);
			} else {
				sb.append(in);
				for (int i = 0; i < length - inLength; i++) {
					sb.append(' ');
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 計算出身分證編號檢查碼.
	 * 
	 * @return char
	 */
	private static char getChecksumDigit(String s) {
		int j = 8;
		int sum = getLetterValue(s.charAt(0));
		for (int i = 1; i <= 8; i++) {
			char ch = s.charAt(i);
			sum += (ch - '0') * j;
			j--;
		}

		int r = sum % 10;
		r = 10 - r;
		if (r == 10) {
			return '0';
		} else {
			return (char) (r + '0');
		}
	}  

	/**
	 * 計算出無戶籍人民身份一證號檢查碼.
	 * @return char
	 */
	private static char getForeChecksumDigit(String s) {
		int j = 9;
		int firstchar = getForeLetterValue(s.charAt(0)); //第一碼轉成ASCII code對應的數字
		int char2 = getForeLetterValue(s.charAt(1)) % 10; //第一碼轉成ASCII code對應的數字, 取個位數

		s = Integer.toString(firstchar) + Integer.toString(char2) + s.substring(2);
		int sum = s.charAt(0) - '0';

		for (int i = 1; i <= 9; i++) {
			char ch = s.charAt(i);
			sum += ((ch - '0') * j) % 10;
			j--;
		}
		int r = sum % 10;
		r = 10 - r;
		if (r == 10) {
			return '0';
		} else {
			return (char) (r + '0');
		}
	}

	/**
	 * 將身分證編號第一碼英文依檢核條件轉為					<BR>
	 * 相對應之數字以作檢核計算用
	 */
	private static int getLetterValue(char ch) {
		ch = Character.toUpperCase(ch);
		int k = ch - 'A' + 10;

		if(ch == 'Z'){
		  k = 33;
		} else if(ch >= 'X'){
		  k -= 3;
		} else if(ch == 'W'){
		  k = 32;
		} else if(ch >= 'P'){
		  k -= 2;
		} else if(ch == 'O'){
		  k = 35;
		} else if(ch >= 'J'){
		  k -= 1;
		} else if(ch == 'I'){
		  k = 34;
		} else if(ch >= 'A'){
		  // Do nothing
		}

		int lv = (k / 10) + (k % 10) * 9;

		return lv;
	}
	

	/**
	 * 將身分證編號第一碼英文依檢核條件轉為<BR>
	 * 相對應之數字以作檢核計算用
	 */
	private static int getForeLetterValue(char ch) {
		ch = Character.toUpperCase(ch);
		int k = ch - 'A' + 10;
		if (ch == 'Z') {
			k = 33;
		} else if (ch >= 'X') {
				k -= 3;
		} else if (ch == 'W') {
				k = 32;
		} else if (ch >= 'P') {
				k -= 2;
		} else if (ch == 'O') {
				k = 35;
		} else if (ch >= 'J') {
				k -= 1;
		} else if (ch == 'I') {
				k = 34;
		} else if (ch >= 'A') {
		}
		return k;
	}
}
