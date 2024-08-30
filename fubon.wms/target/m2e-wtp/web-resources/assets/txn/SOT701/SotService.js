/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.factory('sotService', function() { // $rootScope, $scope, $controller,  $confirm, socketService, ngDialog, projInfoService
    return {
        hello(name) {
            alert(`hello !`);
        },

        /** 判別是否為168帳號 **/
        is168(acct) {
        	acct = acct.trim();
        	if(acct.length != 14){
        		acct = acct.substr(0,14);
        	}
            if (acct && acct.trim() && acct.length == 14) {
                var flag = acct.charAt(0);

                if ((flag == '0' && acct.substring(5, 8) == '168') // 舊帳號判別
                    || (flag == '8' && acct.substring(0, 4) == '8168') // 新帳號判別
                ) return true;
            }
            return false;
        },
        /**#0695 判斷是否為數存戶 **/
        isDigitAcct(acct,list){
        	acct = acct.trim();
        	var isDigit = true;
			angular.forEach(list, function(acctCcyRow){
				if(isDigit){
					if (acct == acctCcyRow.DATA.split('_')[0]) {	
						isDigit = false;
					}				
				}

			});		
			if(isDigit){
				return true;
			} else {
				return false;
			}
			
        },
        /**1507 Fund心投，可參考 FundRule.java **/
        isMultiple(code){
        	if(null == code || undefined == code) {
        		return false;
        	}
        	if(code.match("2|4")) {
        		return true;
        	} else {
        		return false;
        	}
        },
        isMultipleN(code){
        	if(null == code || undefined == code) {
        		return false;
        	}
        	if(code.match("3|5")) {
        		return true;
        	} else {
        		return false;
        	}
        },
        isFundProject(code){
        	if(null == code || undefined == code) {
        		return false;
        	}
        	if(code.match("4|5")) {
        		return true;
        	} else {
        		return false;
        	}
        },

		// 藉由電文 TxType 判斷是否為富邦基金專案：
		// Y： Fund久久
		// A： Fund心投
		isFundProjectByTxType(txType) {
			return /[YA]/.test(txType);
		}
    }
});