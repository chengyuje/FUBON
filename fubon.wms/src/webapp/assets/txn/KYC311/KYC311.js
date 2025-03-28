/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC311Controller', function(
	$rootScope, $scope,$filter, $controller, $confirm , socketService, ngDialog, 
	projInfoService,sysInfoService,$timeout,getParameter) {
	
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "KYC311Controller";
	$scope.kyc311inputvo = "com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO";
	$scope.kyc310inputvo = "com.systex.jbranch.app.server.fps.kyc310.KYC310InputVO";
	
    //計算年齡
    function getAge(dateString) {
        var today = new Date();
        var birthDate = new Date(dateString);
        var age = today.getFullYear() - birthDate.getFullYear();
        var m = today.getMonth() - birthDate.getMonth();
        
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        
        return age;
    }
    
     // filter
	getParameter.XML(["KYC.RISK_DESC","KYC.03_RESULT_DESC","KYC.RISK_TYPE"], function(totas) {
		if (totas) {
			//KYC311與報表呈現 XML 通通修改為 KYC.03_RESULT_DESC
			$scope.result_descList = totas.data[totas.key.indexOf('KYC.03_RESULT_DESC')];//法人風險屬性說明
			$scope.risk_LevelList = totas.data[totas.key.indexOf('KYC.RISK_TYPE')];
			
		}
	});
	
	/**initialize**/
	$scope.init = function() {
		//取得學歷、職業、婚姻、子女數、重大傷病、第三題商品類別、第三題商品經驗年期、學歷變更原因、重大傷病變更原因、EMAIL變更原因，中英文參數資料
    	$scope.sendRecv("KYC310", "getParamData", $scope.kyc310inputvo, $scope.inputVO , function(tota, isError){
			if(!isError) {
				debugger
				$scope.CAREERList = tota[0].body.crrList;
				$scope.CHILD_NOList = tota[0].body.chlList;
				$scope.EDUCATIONList = tota[0].body.eduList;
				$scope.MARRAGEList = tota[0].body.marList;
				$scope.mappingEMAIL_CHANGE = tota[0].body.emailChangeList;
				$scope.EDU_CHANGEList = tota[0].body.eduChangeList;
				$scope.mappingHEALTH = tota[0].body.heaList;
			} 	
		});
    	debugger
		$scope.questionList = $scope.connector('get','KYC310Question');
		$scope.basic_information = $scope.connector('get','KYC310');
		$scope.finish = false;
		$scope.finishForPrint = false;
		$scope.inputVO = {
			cust_name 		: $scope.basic_information.cust_name,
			CUST_ID 		: $scope.basic_information.CUST_ID.toUpperCase(),
			SEQ 			: $scope.basic_information.seq,
			down_risk_level : $scope.basic_information.CUST_RISK_AFR,
			GENDER			: $scope.basic_information.GENDER,
			EDUCATION		: $scope.basic_information.EDUCATION,
			CAREER			: $scope.basic_information.CAREER,
			MARRAGE			: $scope.basic_information.MARRAGE,
			CHILD_NO		: $scope.basic_information.CHILD_NO,
			SICK_TYPE		: $scope.basic_information.SICK_TYPE,
			OUT_ACCESS		: '',
			EMP_ID			: '',
			DAY				: $scope.basic_information.DAY,
			NIGHT			: $scope.basic_information.NIGHT,
			TEL_NO			: $scope.basic_information.TEL_NO,
			TELNO_COD		: $scope.basic_information.TELNO_COD,
			FAX				: $scope.basic_information.FAX,
			FAX_COD			: $scope.basic_information.FAX_COD,
			questionList	: $scope.questionList,
			basic_information : $scope.basic_information,
			CUST_ADDR_1		: $scope.basic_information.CUST_ADDR_1,
			EMAIL_ADDR		: $scope.basic_information.EMAIL_ADDR,
			CUST_RISK_AFR	: $scope.basic_information.CUST_RISK_AFR,
			birthday 		: $scope.basic_information.birthday,
			EXPIRY_DATE		: $scope.basic_information.EXPIRY_DATE,
			//add by Brian
			SCHOOL          : $scope.basic_information.SCHOOL,
			EDU_CHANGE      : $scope.basic_information.EDU_CHANGE,
			HEALTH_CHANGE   : $scope.basic_information.HEALTH_CHANGE,
			REC_SEQ         : $scope.basic_information.REC_SEQ,
			//add by SamTu
			CUST_EMAIL_BEFORE : $scope.basic_information.CUST_EMAIL_BEFORE,
		    SAMEEMAIL_REASON : $scope.basic_information.SAMEEMAIL_REASON,
		    SAMEEMAIL_CHOOSE : $scope.basic_information.SAMEEMAIL_CHOOSE,
		    NEED_COMPARISON_YN : $scope.basic_information.NEED_COMPARISON_YN
		}
		
		if($scope.inputVO.GENDER == '1'){
			$scope.inputVO.GENDER = '男(Male)';
		}
		else if($scope.inputVO.GENDER == '2'){
			$scope.inputVO.GENDER = '女(Female)';
		}
		
		var birthdaydate = new Date($scope.basic_information.birthday);
		var birthdayyear = birthdaydate.getFullYear();
		var birthdaymonth = birthdaydate.getMonth()+1;
		
		if(birthdaymonth<10){
			birthdaymonth = "0" + birthdaymonth
		}
		
		var birthdayday = birthdaydate.getDate();
		
		if(birthdayday<10){
			birthdayday = "0" + birthdayday;
		}
		
		if(birthdayyear && birthdaymonth && birthdayday){
			$scope.birthday = birthdayyear + '-' + birthdaymonth + '-' + birthdayday;
		}
		
		$scope.SICK_TYPEList = [
			{LABEL : '1', DATA : '無(None)'},
			{LABEL : '2', DATA : '有，但不影響本人對投資商品及其風險之理解(Yes, but it does not affect my understanding of investment products and their risks.)'},
			{LABEL : '3', DATA : '有，而且會影響本人對投資商品及其風險之理解(Yes, and it affects my understanding of investment products and their risks.)'}
		];
	}
	
	$scope.init();//initialize variable

	$scope.mapdata = function(){
		for(var e = 0 ; e<$scope.SICK_TYPEList.length;e++){
			if($scope.basic_information.SICK_TYPE === $scope.SICK_TYPEList[e].LABEL){
				$scope.basic_information.SICK_TYPE_data = $scope.SICK_TYPEList[e].DATA;
			}
		}
		
		if($scope.basic_information.CUST_RISK_AFR != 'C1'){
			if($scope.basic_information.CUST_RISK_AFR == 'C2'){
				$scope.RISK_LEVEL_List = [
				 	{LABEL : '保守型(Conservative)' , DATA : 'C1'}, 
				 	{LABEL : '不調整' , DATA : $scope.basic_information.CUST_RISK_AFR}
				];
			}
			
			switch ($scope.basic_information.CUST_RISK_AFR) {
				case 'C2':
					$scope.RISK_LEVEL_List = [
	                  	{LABEL : '保守型(Conservative)' , DATA : 'C1'},
	                  	{LABEL : '不調整' , DATA : $scope.basic_information.CUST_RISK_AFR}
	                ];
					
					break;
					
				case 'C3':
					$scope.RISK_LEVEL_List = [
						{LABEL : '保守型(Conservative)' , DATA : 'C1'},
						{LABEL : '穩健型(Moderate)' , DATA : 'C2'},
						{LABEL : '不調整' , DATA : $scope.basic_information.CUST_RISK_AFR}
					];
					
					break;
					
				case 'C4':
					$scope.RISK_LEVEL_List = [
						{LABEL : '保守型(Conservative)' , DATA : 'C1'},
						{LABEL : '穩健型(Moderate)' , DATA : 'C2'},
						{LABEL : '成長型(Growth)' , DATA : 'C3'},
						{LABEL : '不調整' , DATA : $scope.basic_information.CUST_RISK_AFR}
					];
					
					break;
					
				default:
					break;
			}
		}
	}
	
	$timeout(function(){$scope.mapdata();},300);

	//列印中文版
	$scope.saveandPrintData = function(){
		$scope.sendRecv("KYC311" , "print" , $scope.kyc311inputvo , $scope.inputVO , function(tota,isError){
    		if (isError) {
    			$scope.showErrorMsg(tota[0].body.msgData);
    		}
		});
	}
	
	//列印英文版
	$scope.saveandPrintData_ENG = function(){
		$scope.sendRecv("KYC311" , "print_ENG" , $scope.kyc311inputvo , $scope.inputVO , function(tota,isError){
    		if (isError) {
    			$scope.showErrorMsg(tota[0].body.msgData);
    		}
		});
	}
	
	//刪除
	$scope.deleteData = function(){
		if(!$scope.checkBoss()){
			return;
		}	
		
		var dialog = ngDialog.open({
    		template: 'assets/txn/KYC311/KycRemoveTheCase.html',
    		className: 'KYC311',
    		scope : $scope,
    		showClose: false,
    		controller: ['$scope', function($scope) {}]
    	})
    	.closePromise.then(function (data) {
    		if(data.value == 'cancel'){
				return;
			}
    		
    		$scope.inputVO.DEL_TYPE = data.value;
			
			$scope.sendRecv("KYC311" , "delete" , $scope.kyc311inputvo , $scope.inputVO , function(tota,isError) {
            	if (isError) {
            		$scope.showErrorMsg(tota[0].body.msgData);
            	}
            	
            	if (tota.length > 0) {
            		if(tota[0].body != null){
            			if(tota[0].body.ErrorMsg != null)
            				$scope.showErrorMsg('ehl_01_KYC310_009',tota[0].body.ErrorMsg);
            			else if(tota[0].body.ErrorMsg016 != null)
            				$scope.showErrorMsg('ehl_02_KYC310_016',tota[0].body.ErrorMsg016);
            		}else{
            			$scope.finish=true;
            			$scope.finishForPrint = true;
	            		$scope.showSuccessMsg('ehl_01_common_003');
            		}
            	};
			});
    	});
	}
	
	//取得基本資料label
    function getLabel(type,Data){
    	var label = '';
    	switch (type) {
		case 'EDUCATION':
			//教育程度為其他時為空
			if(Data!='7'){
				for(var i=0;i<$scope.EDUCATIONList.length;i++){
					if($scope.EDUCATIONList[i].DATA == Data){
						label = $scope.EDUCATIONList[i].LABEL;
					}
				}
			}
			break;
		case 'EDU_CHANGE':
			debugger
			for(var i=0;i<$scope.EDU_CHANGEList.length;i++){
				if($scope.EDU_CHANGEList[i].DATA == Data){
					label = $scope.EDU_CHANGEList[i].LABEL;
				}
			}
			break;
		case 'CAREER':
			for(var i=0;i<$scope.CAREERList.length;i++){
				if($scope.CAREERList[i].DATA == Data){
					label = $scope.CAREERList[i].LABEL;
				}
			}
			break;
		case 'MARRAGE':
			for(var i=0;i<$scope.MARRAGEList.length;i++){
				if($scope.MARRAGEList[i].DATA == Data){
					label = $scope.MARRAGEList[i].LABEL;
				}
			}
			break;
		case 'CHILD_NO':
			for(var i=0;i<$scope.CHILD_NOList.length;i++){
				if($scope.CHILD_NOList[i].DATA == Data){
					label = $scope.CHILD_NOList[i].LABEL;
				}
			}
			break;
		case 'HEALTH':
			for(var i=0;i<$scope.mappingHEALTH.length;i++){
				if($scope.mappingHEALTH[i].DATA == Data){
					label = $scope.mappingHEALTH[i].LABEL;
				}
			}
			break;
		default:
			break;
		}
    	return label;
    }
	
    //小於12歲的基本資料檢核
    $scope.underTwele_choic = function(){
    	if(($scope.inputVO.EDUCATION != '8' && $scope.inputVO.EDUCATION != '6' && $scope.inputVO.EDUCATION != '5' )||//教育程度
    		($scope.inputVO.CAREER != '12' && $scope.inputVO.CAREER != '19' && $scope.inputVO.CAREER != '22') ||//職業
    		$scope.inputVO.MARRAGE == '2' ||//婚姻
    		$scope.inputVO.CHILD_NO != '0' ||//子女數
    		$scope.underTwele_QOne()
    		){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    //小於12歲的Q1檢核
    $scope.underTwele_QOne = function(){
    	if($scope.inputVO.questionList[0].ANSWER_LIST[0].select && $scope.inputVO.questionList[0].ANSWER_LIST[0].select == true){
    		return true;
    	}else if($scope.inputVO.questionList[0].ANSWER_LIST[1].select && $scope.inputVO.questionList[0].ANSWER_LIST[1].select == true){
    		return true;
    	}else if($scope.inputVO.questionList[0].ANSWER_LIST[2].select && $scope.inputVO.questionList[0].ANSWER_LIST[2].select == true){
    		return true;
    	}else{
    		return false;
    	}
    }
	
    //完成
	$scope.btnSubmit = function(){
		var windowMsgObj = {
    		title	: "提醒視窗",
    		text	: "OP已列印表單",
			ok		: "是",
            cancel	: "否"
    	};
		
//		if($scope.inputVO.REC_SEQ != '' ){
//			if($scope.inputVO.REC_SEQ.length < 16){
//				$scope.showErrorMsg("錄音序號長度需大於16位");
//				$scope.inputVO.REC_SEQ = "";
//				return;
//			}
//		}
		
		$confirm(windowMsgObj).then(function(){
			if($scope.inputVO.CUST_ID.length>=10){
				//自然人需做基本資料比對
				$scope.check_persion();
			}else{
				//法人
				$scope.check_corp();
			}
    	});
	}
	
	//法人檢核
	$scope.check_corp = function(){
		var Q3ProdExpChg = undefined;
		var Q3ProdDecrease = undefined;
		var EMAIL = undefined;
		var EMAIL2 = undefined;
		
		$scope.sendRecv("KYC311" , "getLastResult" , $scope.kyc311inputvo , $scope.inputVO , function(tota,isError){
			//與前次承作Q3 or Q8&Q9答案比對
			if(tota[0].body.Q3ProdExpChgList != null && tota[0].body.Q3ProdExpChgList.length > 0){	//Q3投資年期上升2級以上
				Q3ProdExpChg = "";
				for(var i = 0 ; i < tota[0].body.Q3ProdExpChgList.length ; i++) {
					Q3ProdExpChg += $filter('i18n')('ehl_01_KYC310_023',
							[tota[0].body.Q3ProdExpChgList[i].ProdType, tota[0].body.Q3ProdExpChgList[i].PreProdExp, tota[0].body.Q3ProdExpChgList[i].CurProdExp]) + "<br/>";
				}
        	}
			
        	if(tota[0].body.Q3ProdDecrease){	//Q3投資商品上次有本次沒有
        		Q3ProdDecrease = $filter('i18n')('ehl_01_KYC310_024',[tota[0].body.Q3ProdDecrease]);
        	}
        	
        	if(tota[0].body.Comparison.EMAIL){
				debugger;
				var beforeEMAIL = tota[0].body.Comparison.EMAIL.beforEMAIL;
				var afterEMAIL = tota[0].body.Comparison.EMAIL.afterEMAIL;
				//顯示文字調整
		        if(beforeEMAIL == undefined || beforeEMAIL == ''){
		        	beforeEMAIL = '無';
		        }
		        if(afterEMAIL == undefined || afterEMAIL == ''){
		        	afterEMAIL = '無';
		        }
		        $scope.sameEmail_reason_show = '';
		        if($scope.inputVO.SAMEEMAIL_REASON){
		        	$scope.sameEmail_reason_show = $scope.inputVO.SAMEEMAIL_REASON;
		        } else if($scope.inputVO.SAMEEMAIL_CHOOSE){
		        	$scope.sameEmail_reason_show = $scope.mappingEMAIL_CHANGE[$scope.inputVO.SAMEEMAIL_CHOOSE -1].LABEL;
		        }
				
				if(beforeEMAIL == afterEMAIL){
				    debugger;
					EMAIL = '客戶留存的電子郵件為：【' + afterEMAIL + '】，請確認解說人員是否與客戶確認資料正確性。\n';
				}else if(beforeEMAIL != afterEMAIL && $scope.inputVO.SAMEEMAIL_CHOOSE){
				    debugger;
					EMAIL = '客戶留存的電子郵件由【' + beforeEMAIL + '】更新為【' + afterEMAIL +
					'】，本行已發送確認連結至客戶新留存信箱，本次變更將於點選確認連結後生效，請確認解說人員是否與客戶確認資料正確性。\n';
					EMAIL2 = '經比對，客戶留存的電子郵件與本行其他客戶有重覆情形，原因為：【' +  $scope.sameEmail_reason_show + '】。\n';
				}else if(beforeEMAIL != afterEMAIL && !$scope.inputVO.SAMEEMAIL_CHOOSE){
					debugger;
					if(afterEMAIL == '無'){
						EMAIL = '客戶留存的電子郵件由【' + beforeEMAIL + '】更新為【' + afterEMAIL +
						'】，請確認解說人員是否與客戶確認資料正確性。\n';
					}else {
						EMAIL = '客戶留存的電子郵件由【' + beforeEMAIL + '】更新為【' + afterEMAIL +
						'】，本行已發送確認連結至客戶新留存信箱，本次變更將於點選確認連結後生效，請確認解說人員是否與客戶確認資料正確性。\n';
					}
				}
			}
        	
        	if(Q3ProdExpChg != undefined || Q3ProdDecrease != undefined || EMAIL != undefined) {
	        	$confirm({
	        		title:"提醒視窗",
					text1:Q3ProdExpChg,
					text2:Q3ProdDecrease,
					text3:EMAIL,
					text4:EMAIL2,
					ok: "確定送出",
	                cancel: "修改資料"
	        	}).then(function(){
	        		$scope.SubmitData();
	        	});
        	} else {
        		$scope.SubmitData();
        	}
		});
	}
	
	//自然人檢核
	$scope.check_persion = function(){
		$scope.sendRecv("KYC311" , "chkAuth" , $scope.kyc311inputvo, $scope.inputVO , function(tota,isError) {
			var age = getAge($scope.inputVO.birthday);
				var EDUCTION = undefined;
				var CAREER = undefined;
				var MARRIAGE = undefined;
				var CHILDREN = undefined;
				var HEALTH = undefined;
				var Q3ProdExpChg = undefined;
        		var Q3ProdDecrease = undefined;
        		var EMAIL = undefined;
        		var EMAIL2 = undefined;
				
        		$scope.sendRecv("KYC311" , "getLastResult" , $scope.kyc311inputvo , $scope.inputVO , function(tota,isError){
	        		//基本資料比對
        			debugger
        			$scope.EDUCATIONforCheck = "";
					if(tota[0].body.Comparison.EDUCTION){
						$scope.EDUCATIONforCheck = tota[0].body.Comparison.EDUCTION.beforEDUCTION;
						if(getLabel('EDUCATION', tota[0].body.Comparison.EDUCTION.beforEDUCTION) == "") {
							EDUCTION = '【教育程度】異動為【'+getLabel('EDUCATION', tota[0].body.Comparison.EDUCTION.afterEDUCTION)+'】\n';
						} else {
							EDUCTION = '【教育程度】從【'+getLabel('EDUCATION', tota[0].body.Comparison.EDUCTION.beforEDUCTION)+'】異動為【'+getLabel('EDUCATION', tota[0].body.Comparison.EDUCTION.afterEDUCTION)+'】\n';
						}
					}
					
					if(tota[0].body.Comparison.CAREER){
						CAREER = '【職業】從【'+getLabel('CAREER', tota[0].body.Comparison.CAREER.beforCAREER)+'】異動為【'+getLabel('CAREER', tota[0].body.Comparison.CAREER.afterCAREER)+'】\n';
					}
					
					if(tota[0].body.Comparison.MARRIAGE){
						MARRIAGE = '【婚姻】從【'+getLabel('MARRAGE', tota[0].body.Comparison.MARRIAGE.beforMARRIAGE)+'】異動為【'+getLabel('MARRAGE', tota[0].body.Comparison.MARRIAGE.afterMARRIAGE)+'】\n';
					}
					
					if(tota[0].body.Comparison.CHILDREN){
						CHILDREN = '【子女人數】從【'+getLabel('CHILD_NO', tota[0].body.Comparison.CHILDREN.beforCHILDREN)+'】異動為【'+getLabel('CHILD_NO', tota[0].body.Comparison.CHILDREN.afterCHILDREN)+'】\n';
					}
					
					if(tota[0].body.Comparison.HEALTH){
						HEALTH = '【重大傷病證明】從【'+getLabel('HEALTH', tota[0].body.Comparison.HEALTH.beforHEALTH)+'】異動為【'+getLabel('HEALTH', $scope.inputVO.SICK_TYPE)+'】\n';
					}
					if(tota[0].body.Comparison.EMAIL){
						debugger;
						var beforeEMAIL = tota[0].body.Comparison.EMAIL.beforEMAIL;
						var afterEMAIL = tota[0].body.Comparison.EMAIL.afterEMAIL;
						
				        //顯示文字調整
				        if(beforeEMAIL == undefined || beforeEMAIL == ''){
				        	beforeEMAIL = '無';
				        }
				        if(afterEMAIL == undefined || afterEMAIL == ''){
				        	afterEMAIL = '無';
				        }
				        $scope.sameEmail_reason_show = '';
				        if($scope.inputVO.SAMEEMAIL_REASON){
				        	$scope.sameEmail_reason_show = $scope.inputVO.SAMEEMAIL_REASON;
				        } else if($scope.inputVO.SAMEEMAIL_CHOOSE){
				        	$scope.sameEmail_reason_show = $scope.mappingEMAIL_CHANGE[$scope.inputVO.SAMEEMAIL_CHOOSE -1].LABEL;
				        }
						
						if(beforeEMAIL == afterEMAIL){
						    debugger;
							EMAIL = '客戶留存的電子郵件為：【' + afterEMAIL + '】，請確認解說人員是否與客戶確認資料正確性。\n';
						}else if(beforeEMAIL != afterEMAIL && $scope.inputVO.SAMEEMAIL_CHOOSE){
						    debugger;
							EMAIL = '客戶留存的電子郵件由【' + beforeEMAIL + '】更新為【' + afterEMAIL +
							'】，本行已發送確認連結至客戶新留存信箱，本次變更將於點選確認連結後生效，請確認解說人員是否與客戶確認資料正確性。\n';
							EMAIL2 = '經比對，客戶留存的電子郵件與本行其他客戶有重覆情形，原因為：【' +  $scope.sameEmail_reason_show + '】。\n';
						}else if(beforeEMAIL != afterEMAIL && !$scope.inputVO.SAMEEMAIL_CHOOSE){
							debugger;
							if(afterEMAIL == '無'){
								EMAIL = '客戶留存的電子郵件由【' + beforeEMAIL + '】更新為【' + afterEMAIL +
								'】，請確認解說人員是否與客戶確認資料正確性。\n';
							}else {
								EMAIL = '客戶留存的電子郵件由【' + beforeEMAIL + '】更新為【' + afterEMAIL +
								'】，本行已發送確認連結至客戶新留存信箱，本次變更將於點選確認連結後生效，請確認解說人員是否與客戶確認資料正確性。\n';
							}
						}
					}
					
					//與前次承作Q3 or Q8&Q9答案比對
					if(tota[0].body.Q3ProdExpChgList != null && tota[0].body.Q3ProdExpChgList.length > 0){	//Q3投資年期上升2級以上
						Q3ProdExpChg = "";
						for(var i = 0 ; i < tota[0].body.Q3ProdExpChgList.length ; i++) {
							Q3ProdExpChg += $filter('i18n')('ehl_01_KYC310_023',
									[tota[0].body.Q3ProdExpChgList[i].ProdType, tota[0].body.Q3ProdExpChgList[i].PreProdExp, tota[0].body.Q3ProdExpChgList[i].CurProdExp]) + "<br/>";
						}
		        	}
					
		        	if(tota[0].body.Q3ProdDecrease){	//Q3投資商品上次有本次沒有
		        		Q3ProdDecrease = $filter('i18n')('ehl_01_KYC310_024',[tota[0].body.Q3ProdDecrease]);
		        	}
					
	        		//12歲以下檢核
					if(age<=12 && $scope.underTwele_choic()){
						var ANSList = $scope.inputVO.questionList[0].ANSWER_LIST;
			        	var Income = '';
			        	var EDUCTION_12 = undefined;
						var CHILD_NO_12 = undefined;
						var MARRIAGE_12 = undefined;
						var CAREER_12 = undefined;
						var INCOME_12 = undefined;
						
			        	if(ANSList != undefined){
			            	for(var i=0;i<3;i++){
			            		if(ANSList[i].select == true){
			            			Income = Income+ANSList[i].ANSWER_DESC.substr(3)+'、';
			            		}
			            	}
			        	}
						
//			        	if($scope.inputVO.EDUCATION != '8' && $scope.inputVO.EDUCATION != '6' && $scope.inputVO.EDUCATION != '5'){
//			        		EDUCTION_12 = $filter('i18n')('ehl_01_KYC310_019',[getLabel('EDUCATION', $scope.inputVO.EDUCATION)]);
//			        	}
			        	
			        	if($scope.inputVO.CHILD_NO != '0'){
			        		CHILD_NO_12 = $filter('i18n')('ehl_01_KYC310_020',[getLabel('CHILD_NO', $scope.inputVO.CHILD_NO)]);
			        	}
			        	
			        	if($scope.inputVO.MARRAGE == '2'){
			        		MARRIAGE_12 = $filter('i18n')('ehl_01_KYC310_021',[getLabel('MARRAGE', $scope.inputVO.MARRAGE)]);
			        	}
			        	
			        	if($scope.inputVO.CAREER != '12' && $scope.inputVO.CAREER != '19' && $scope.inputVO.CAREER != '22'){
			        		CAREER_12 = $filter('i18n')('ehl_01_KYC310_026',[getLabel('CAREER', $scope.inputVO.CAREER)]);
			        	}
			        	
			        	if(Income != ''){
			        		INCOME_12 = $filter('i18n')('ehl_01_KYC310_022',[Income.substr(0,Income.length-1)]);
			        	}
			        	
			        	$confirm({
			        		title:"提醒視窗",
			        		text:EDUCTION_12,
							text1:CAREER_12,
							text2:MARRIAGE_12,
							text3:CHILD_NO_12,
							text4:INCOME_12,
							text5:EDUCTION,
							text6:CAREER,
	    					text7:MARRIAGE,
	    					text8:CHILDREN,
	    					text9:HEALTH,
	    					text10:Q3ProdExpChg,
	    					text11:Q3ProdDecrease,
	    					text12:EMAIL,
	    					text13:EMAIL2,
							ok: "確定送出",
			                cancel: "修改資料"
			        	}).then(function(){
			        		$scope.SubmitData();
			        	});
			        	
					}
					else if(EDUCTION != undefined || CAREER != undefined || MARRIAGE != undefined || 
							CHILDREN != undefined || HEALTH != undefined || Q3ProdExpChg != undefined || Q3ProdDecrease != undefined ||
							EMAIL != undefined){
						
						//18歲(含)以上客戶，學歷由國中以下異動為高中職以上
						if(age >= 18) {
							if(($scope.EDUCATIONforCheck == '8' || $scope.EDUCATIONforCheck == '6' || $scope.EDUCATIONforCheck == '7') &&
	    						($scope.inputVO.EDUCATION == '5' || $scope.inputVO.EDUCATION == '4' || $scope.inputVO.EDUCATION == '3' || $scope.inputVO.EDUCATION == '2' || $scope.inputVO.EDUCATION == '1')) {
								EDUCTION = '【教育程度】從【'+getLabel('EDUCATION', $scope.EDUCATIONforCheck)+'】異動為【'+getLabel('EDUCATION', $scope.inputVO.EDUCATION)+'】，學校名稱【'+$scope.inputVO.SCHOOL+'】，原因為【'+getLabel('EDU_CHANGE', $scope.inputVO.EDU_CHANGE)+'】\n';
							}
						}
						
						//非12歲以下且基本資料異動
						$confirm({
	    					title:"提醒視窗",
	    					text:EDUCTION,
	    					text1:CAREER,
	    					text2:MARRIAGE,
	    					text3:CHILDREN,
	    					text4:HEALTH,
	    					text5:Q3ProdExpChg,
	    					text6:Q3ProdDecrease,
	    					text7:EMAIL,
	    					text8:EMAIL2,
	    					ok: "確定送出",
	    	                cancel: "修改資料"
	    				}).then(function(){
							$scope.SubmitData();
						});
						
					}else{
						//基本資料未異動
						$scope.SubmitData();
					}
        		});
			
		});//chkAuth end
	}

	//主管覆核帳號檢核&&儲存資料並上送電文
	$scope.SubmitData = function(){
		if(!$scope.checkBoss()){
			return;
		}	
		
		$scope.sendRecv("KYC311" , "Submit" , $scope.kyc311inputvo , $scope.inputVO , function(tota,isError){
			if (isError) {
        		$scope.showErrorMsg(tota[0].body.msgData);
        	}
        	
        	if (tota.length > 0) {
        		if(tota[0].body != null){
        			if(tota[0].body.ErrorMsg != null)
        				$scope.showErrorMsg('ehl_01_KYC310_009',tota[0].body.ErrorMsg);
        			else if(tota[0].body.ErrorMsg016 != null)
        				$scope.showErrorMsg('ehl_02_KYC310_016',tota[0].body.ErrorMsg016);
        			else if(tota[0].body.ErrorMsg043 != null)
        				$scope.showErrorMsg('ehl_01_KYC310_043',tota[0].body.ErrorMsg043);
        			else if(tota[0].body.success != null){
	            		$scope.finish = true;
	            		$scope.finishForPrint = false;
	            		$confirm({text: '請告知該客戶KYC到期日為' + $filter('date')($scope.toJsDate($scope.inputVO.EXPIRY_DATE), 'yyyy/MM/dd')}, {size: 'sm'}).then(function(success) {
	            		});
	            		$scope.showSuccessMsg(tota[0].body.success);
        			}
        		}
        		else{
        			if(tota.length >=3 && tota[1].body != null)
            			$scope.showErrorMsg(tota[1].body.ErrorMsg);
            		$scope.finish = true;
            		$scope.finishForPrint = false;
            		$scope.showSuccessMsg(tota[0].body.success);
        		}

        	};
		});
	}
	
	$scope.checkBoss = function(){
		if($scope.inputVO.EMP_ID == undefined || $scope.inputVO.EMP_ID == null || $scope.inputVO.EMP_ID == ''){
			$scope.showErrorMsg('請輸入主管員編');
			return false;
		}
		else if($scope.inputVO.EMP_PASSWORD == undefined || $scope.inputVO.EMP_PASSWORD == null || $scope.inputVO.EMP_PASSWORD == ''){
			$scope.showErrorMsg('請輸入主管密碼');
			return false;
		}
		
		return true;
	}
});
