///======================
/// 原ValidateUtil.as
///======================
eSoafApp.factory('validateService',['$rootScope', function($rootScope) {

	var regExp_twbid = /^[0-9]{8}$/;

	return {
		
		isValidTWPID: function(id) {
			
			//建立字母分數陣列(A~Z)
			var city = new Array(
									1,10,19,28,37,46,55,64,39,73,82, 2,11,
									20,48,29,38,47,56,65,74,83,21, 3,12,30
								)
			id = id.toUpperCase();
			var custId = id.toUpperCase();
			// 使用「正規表達式」檢驗格式
			console.log("id = "+ id);
			if (id.search(/^[A-Z](1|2|8|9)\d{8}$/i) == -1) {
				$rootScope.showErrorMsg('客戶編號檢核:基本格式錯誤('+custId+')');
				return false;
			}
			
			//將字串分割為陣列(IE必需這麼做才不會出錯)
			id = id.split('');
			//計算總分
			var total = city[id[0].charCodeAt(0)-65];
			for(var i=1; i<=8; i++){
				total += eval(id[i]) * (9 - i);
			}
			//補上檢查碼(最後一碼)
			total += eval(id[9]);
			//檢查比對碼(餘數應為0);
			console.log("total = "+ total);
			var checkTotal_Mod10=((total%10 == 0 ));
			if(checkTotal_Mod10==false){
			   $rootScope.showErrorMsg('客戶編號檢核:身分證號檢核錯誤('+custId+')');
			}
			return checkTotal_Mod10;
		},
		isValidTWBID: function(value) {
			var weight='12121241';
			var type2=false; //第七個數是否為七
			if (regExp_twbid.test(value)) {
				var tmp=0;
				var sum=0;
				for (var i=0; i < 8; i++) {
					tmp= Number(value.charAt(i)) * Number(weight.charAt(i));
					sum+=parseInt((tmp / 10) + (tmp % 10), 10); //取出十位數和個位數相加
					if (i == 6 && value.charAt(i) == '7'){
						type2=true;
					}
				}
				if (type2){
					if ((sum % 10) == 0 || ((sum + 1) % 10) == 0){ //如果第七位數為7
						return true;
					}
				}else{
					if ((sum % 10) == 0){
						return true;
					}
				}
			}
			return false;
		},
		
		/**
		 * 舊版:非富邦
		 * 檢查是否為合法的  id (身分證字號/營利事業統一編號),若不是合法  id 則  throw Error 訊息
		 * 
		 */
		 
		checkID: function(value) {
			if(value.length >= 11) return;
			if(value.length == 8){
				if(!isNaN(Number(value))) // 假如非數字,則放行,如果是數字,則進行統編檢查
				{ 
					if(!this.isValidTWBID(value)){
						$rootScope.showErrorMsg('客戶編號檢核:營利事業編號檢核錯誤');
						throw new Error('營利事業編號檢核錯誤');
					}
				}				
			}else{
				if(isNaN(Number(value.charAt(0))) && !isNaN(Number(value.charAt(1)))) // 第一碼為英文字母且第二碼為數字時,進行身分證字號檢查
				{ 		
					var val = value.substring(0,10);			
					if( !this.isValidTWPID(val)){
						$rootScope.showErrorMsg('客戶編號檢核:身分證號檢核錯誤');
						throw new Error('身分證號檢核錯誤');
					}
				}								
			}
		},
		
		
		/**
		 * 檢查是否為合法的  id (身分證字號),若不是合法  id 則  throw Error 訊息 return false
		 * 
		 * 2017/02/06 FB新理規
		 * 基/債/股 下單輸入客戶ID欄位需做下列檢核，才能查詢該 客戶電文(申轉贖變更、庫存查客戶、下單商品管理查客戶)
		 * 1. 先判斷 客戶ID欄位 小於 長度8碼 ，即不符合客戶ID ，踢退
		 * 2. 後判斷自然人 客戶ID欄位 大於等於長度 10碼，再驗證身分證字號規則。
		 * 3. 最後判斷法人 客戶ID欄位 大於等於長度 8碼 and 長度小於10碼，即為法人
		 * 自然人: 1.欄位長度>= 10碼 2.身分證檢核
		 * 法人(EX: OBU客戶): 1.欄位長度>= 8碼 and 欄位長度< 10碼
		 */
		checkCustID: function(value) {
			if (!value){
				//$rootScope.showErrorMsg('客戶編號檢核:客戶編號不能為空值');
				return false;
			}else if (value && value.length < 8) {
				$rootScope.showErrorMsg('客戶編號檢核:不足8碼('+value+')');
				//即不符合客戶ID ，踢退
				return false; 
			} else if(value.length >= 10) {
				//自然人
				
				if(isNaN(Number(value.charAt(0))) && !isNaN(Number(value.charAt(1)))) // 第一碼為英文字母且第二碼為數字時,進行身分證字號檢查
				{ 		
					var val = value;//value.substring(0,10);			
					if( !this.isValidTWPID(val)){
						
						return false;
					}
					return true;
				} else {
					return true;//為10碼的OBU
				}								
			} else if(value.length >= 8 && value.length < 10) {
				return true;  //法人(EX: OBU客戶 )			   TODO:OBU要檢核營利編號?	
			}
			$rootScope.showErrorMsg('客戶編號檢核:失敗('+value+')');
			return false;
		}
		
	};
}]);