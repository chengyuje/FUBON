package com.systex.jbranch.fubon.bth.job.business_logic;

public class Try {

	public String ID_TYPE(String ID) {

		int Length = 0;
		Length = ID.trim().length();
		switch (Length) {
		case 7:

			if (IsNumeric(ID)) {
				// 7位數字 內政部配賦統一證號
				return "19";
			}
			break;

		case 8:

			if (IsNumeric(ID)) {
				// 8位數字 本國法人統一編號
				return "21";
			}

			else if (IsLetter(ID.substring(0, 4)) && IsNumeric(ID.substring(4, 4))) {
				// 4位英文字母+4位數字 境外法人虛擬統編(聯徵中心編配境外法人虛擬統編)
				return "22";
			}

			else if (IsNumeric(ID.substring(0, 3)) && IsLetter(ID.substring(3, 2)) && IsNumeric(ID.substring(5, 3))) {
				// 3位數字+2位英文字母+3位數字 OBU法人(本行編配)
				return "29";
			}

			else if (ID.substring(0, 2) == "88" && IsLetter(ID.substring(2, 6))) {
				// 88Walkin(Walkin客戶) /88treasury (市庫統制帳戶)/88+後面全英文字母 虛擬ID
				return "32";
			}
			break;

		case 10:

			if (IsLetter(ID.substring(0, 1)) && IsNumeric(ID.substring(1, 9))) {
				// 1位英文字母+9位數字 身分證統一編號
				return "11";
			}

			else if (IsLetter(ID.substring(0, 2)) && IsNumeric(ID.substring(2, 8))) {
				// 2位英文字母+8位數字 居留證/基資表統一證號
				return "12";
			}

			else if (IsNumeric(ID.substring(0, 8)) && IsLetter(ID.substring(8, 2))) {
				// 8位數字+2位英文字母 境外自然人證號 出生年月日 + 姓名前2碼
				return "13";
			}

			else if (IsNumeric(ID)) {
				// 10位數字 內政部配賦統一證號
				return "19";
			}

			else if (ID.substring(0, 2) == "88" && IsLetter(ID.substring(2, 8))) {
				// 88Walkin(Walkin客戶) /88treasury (市庫統制帳戶)/88+後面全英文字母 虛擬ID
				return "32";
			}
			break;

		case 11:
			// if (ID.Substring(0, 3) == "TFB" && IsNumeric(ID.Substring(3, 8)))
			// {
			// //TFB+8位數字 徵審ID
			// return "31";
			// }

			if (ID.substring(0, 3) == "GRP" && IsNumeric(ID.substring(3, 8))) {
				// GRP+8位數字 徵審ID
				return "31";
			} else if (ID.substring(0, 2) == "88" && IsLetter(ID.substring(2, 9))) {
				// 88Walkin(Walkin客戶) /88treasury (市庫統制帳戶)/88+後面全英文字母 虛擬ID
				return "32";
			}
			break;
		}
		return "39";
	}

	// / <summary>
	// / 驗證由英文字母組成的字串
	// / </summary>
	// / <param name="input">要驗證的字串</param>
	// / <returns>驗證通過返回true</returns>

	public boolean IsLetter(String input) {
		String Letter = "^[A-Za-z]+$";
		return input.matches(Letter); 
	}

	// / <summary>
	// / 驗證由數字組成的字串
	// / </summary>
	// / <param name="input">要驗證的字串</param>
	// / <returns>驗證通過返回true</returns>
	public static boolean IsNumeric(String input) {
		String Numeric = "^[0-9]+$";
		return input.matches(Numeric); 
		
	}
	
	public static void main(String args[]){
		Try test = new Try();
		
		System.out.println(test.ID_TYPE("A120159327"));
	}
}
