/**================================================================================================
@program: eMasking.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
/**================================================================================================
     * Preferences:
     * [caseI = 1  身份證字號 (10)]
     * [caseI = 2  統一編號(8)]
     * [caseI = 3  外國人編號(10)]
     * [caseI = 4  地址(x)]
     * [caseI = 5  市話(x)] 
     * [caseI = 6  手機欄位(10)] 
     * [caseI = 7  姓名欄位(x)]
=================================================================================================*/
eSoafApp.filter('eMasking', function() {
    return function (item, caseI) {
    	//check param
    	if(!angular.isDefined(item)){return}
    	if(!item || !caseI){return}
    	item.toString().trim();
    	var lenI = item.trim().length, strTmp="";
    	/* Select Case */
		switch(caseI) { 
			/* 身份證字號 */
			//2016/05/12 Allen 因欄位可能有統編資料故多判斷統編條件。
			/* ID */
            case 1:
            	if (lenI >= 7) {
//            		modify by jimmy in 2017/03/28 依照富邦給予的個資遮蔽範圍修改
            		var strTmp_a = item.substring(0, 5), strTmp_b = item.substring(7,lenI);
            		//compose string
            		strTmp = strTmp_a + "**" + strTmp_b;
            	} else{
//            		var strTmp_a = item.substring(0, 5);
//            		//compose string
            		strTmp = item;
            	}		            	
                break;
            /* 統一編號 */
            /* Uniform number */
            case 2: 
            	if (lenI == 8) {
            		var strTmp_a = item.substring(0, 5);
            		//compose string
            		strTmp = strTmp_a + "***";
            	}	            	
                break;
            /* 外國人編號 */
            /* Foreigners ID */
            case 3: 
            	if (lenI == 10) {
            		var strTmp_a = item.substring(0, 5),
            		    strTmp_b = item.charAt(9);
            		//compose string
            		strTmp = strTmp_a + "****" + strTmp_b;
            	}	            	
                break;
            /* 地址 */
            /* Address */
            //2016/01/14 ArthurKO 若地址欄位輸入的字符數量少於要遮罩的數量則不需遮罩回傳原參數。
            case 4: 
            	if (lenI > 6) {
            		var strTmp_a = item.substring(0, 5),
            		    strTmp_b = "";
            		for (var i = lenI; i > 6; i--) {
            			strTmp_b = strTmp_b + "*";
            		}
            		//compose string
            		strTmp = strTmp_a + strTmp_b;
            	}else if (lenI > 0 && lenI <= 6) {
            		//compose string
            		strTmp = item;
            	}
                break;
            /* 市話 */
            /* Local phone number */
            //condition: 輸入的參數大於5碼，顯示前5碼，後面遮罩。
            //2016/01/14 ArthurKO 若市話欄位輸入的字符數量少於要遮罩的數量則不需遮罩回傳原參數。
            case 5: 
            	if (lenI > 5) {
            		var strTmp_a = item.substring(0, 5),
            		    strTmp_b = "";
            		for (var i = lenI; i > 5; i--) {
            			strTmp_b = strTmp_b + "*";
            		}
            		//compose string
            		strTmp = strTmp_a + strTmp_b;
            	}else if (lenI > 0 && lenI <= 5) {
            		//compose string
            		strTmp = item;
            	}
                break;
            /* 手機 */
            //condition: 輸入的參數大於等於9碼，顯示前5碼，後面遮罩。
            case 6: 
            	if (lenI > 5) {
            		var strTmp_a = item.substring(0, 5),
            		    strTmp_b = "";
            		for (var i = lenI; i > 5; i--) {
            			strTmp_b = strTmp_b + "*";
            		}
            		//compose string
            		strTmp = strTmp_a + strTmp_b;
            	}else if (lenI > 0 && lenI <= 5) {
            		//compose string
            		strTmp = item;
            	}		            	
                break;
            /* 姓名 */
            case 7: 
//        		modify by jimmy in 2017/03/28 依照富邦給予的個資遮蔽範圍修改
            	if (lenI <= 3) {
            		var strTmp_a = item.charAt(0),
            		    strTmp_b = item.charAt(2);
//            		for (var i = lenI; i > 1; i--) {
//            			strTmp_b = strTmp_b + "*";
//            		}
            		//compose string
            		strTmp = strTmp_a +"*"+ strTmp_b;
            	}else{
            		var strTmp_a = item.charAt(0),
            			strTmp_b = item.substring(2,lenI);
            		strTmp = strTmp_a +"*"+ strTmp_b;
            	}
//            	else if (lenI == 4) {
//            		var strTmp_a = item.substring(0, 2),
//            		    strTmp_b = "**";
//            		//compose string
//            		strTmp = strTmp_a + strTmp_b;
//            	}else if (lenI > 4) {
//            		var strTmp_a = item.substring(0, 3),
//            		    strTmp_b = "";
//            		for (var i = lenI; i > 3; i--) {
//            			strTmp_b = strTmp_b + "*";
//            		}
//            		//compose string
//            		strTmp = strTmp_a + strTmp_b;
//            	}
                break;
            // 2017 11/29
            case 8:
            	// 還要增加英文姓名遮蔽, 第1-6字母要遮, 中文姓名遮倒數第二字元
            	if(lenI <= 3) {
            		var strTmp_a = item.charAt(0),
            		    strTmp_b = item.charAt(2);
            		strTmp = strTmp_a +"*"+ strTmp_b;
            	}
            	else if(lenI > 7) {
            		var strTmp_a = item.substring(6, lenI);
            		strTmp = "******" + strTmp_a;
            	}
            	else {
            		var strTmp_a = item.substring(0,lenI-2),
        				strTmp_b = item.substring(lenI-1,lenI);
            		strTmp = strTmp_a +"*"+ strTmp_b;
            	}
            	break;
            /* Others */
            default: 
            	return item;
		}
		return strTmp;
    };
});