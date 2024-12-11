/**
 * 
 */
'use strict';
eSoafApp.controller('KYC310Controller', function(
	$rootScope , $scope , $controller , $confirm , sysInfoService , socketService , ngDialog , 
	projInfoService , $filter , $timeout , getParameter
){
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "KYC310Controller";
    $scope.kyc310inputvo = "com.systex.jbranch.app.server.fps.kyc310.KYC310InputVO";
    
    // date picker
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
	getParameter.XML(["KYC.BASE_SCORE","KYC.CRR_RULE"], function(totas) {
		if (totas) {			
			$scope.cust_sum 			     = totas.data[totas.key.indexOf('KYC.BASE_SCORE')];
			//CRR_RULE: Y=> 總分與矩陣取低者為C值; N=> 總分取得C值對應
			$scope.mappingCRR_RULE		 	 = totas.data[totas.key.indexOf('KYC.CRR_RULE')];
			$scope.inputVO.CRR_RULE		 	 = $scope.mappingCRR_RULE.length > 0 ? $scope.mappingCRR_RULE[0].LABEL : "Y";
		}
	});
    
	//取得基本資料label
    function getLabel(type,Data){
    	var label = '';
    	
    	switch (type) {
			case 'EDUCATION':
				//教育程度為其他時為空
				if(Data!='7'){
					for(var i=0;i<$scope.mappingEDUCATION.length;i++){
						if($scope.mappingEDUCATION[i].DATA == Data){
							label = $scope.mappingEDUCATION[i].LABEL;
						}
					}
				}
				break;
			case 'CAREER':
				for(var i=0;i<$scope.mappingCAREER.length;i++){
					if($scope.mappingCAREER[i].DATA == Data){
						label = $scope.mappingCAREER[i].LABEL;
					}
				}
				break;
			case 'MARRAGE':
				for(var i=0;i<$scope.mappingMARRAGE.length;i++){
					if($scope.mappingMARRAGE[i].DATA == Data){
						label = $scope.mappingMARRAGE[i].LABEL;
					}
				}
				break;
			case 'CHILD_NO':
				for(var i=0;i<$scope.mappingCHILD_NO.length;i++){
					if($scope.mappingCHILD_NO[i].DATA == Data){
						label = $scope.mappingCHILD_NO[i].LABEL;
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
        
	//config
	$scope.model = {};
	
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
		
	//初始化
    $scope.init = function(){
    	$scope.majorSick = true;
    	$scope.company = false;
    	$scope.noupdatedata = false;
    	$scope.querybirthday = false;
    	$scope.temp_QNineAns = undefined;
    	$scope.email_same = false;
    	$scope.sameEmailData = undefined;
		$scope.EDUCATION_LABEL = '';
		$scope.CHILD_NO_LABEL = '';
		$scope.MARRAGE_LABEL = '';
    	$scope.inputVO = {
			CUST_ID:'',
			COMMENTARY_STAFF:'',
			QUESTION_TYPE:'',
			chk_type:'',
			Y_N_update:'2'
    	}
    	
    	//取得學歷、職業、婚姻、子女數、重大傷病、第三題商品類別、第三題商品經驗年期、學歷變更原因、重大傷病變更原因、EMAIL變更原因，中英文參數資料
    	$scope.sendRecv("KYC310", "getParamData", $scope.kyc310inputvo, $scope.inputVO , function(tota, isError){
			if(!isError) {
				debugger
				$scope.mappingCAREER 		     = tota[0].body.crrList;
				$scope.inputVO.CAREERList 	     = tota[0].body.crrList;
				
				$scope.mappingCHILD_NO 		     = tota[0].body.chlList;
				$scope.inputVO.CHILD_NOList      = tota[0].body.chlList;
				
				$scope.mappingEDUCATION 	     = tota[0].body.eduList;
				$scope.inputVO.EDUCATIONList     = tota[0].body.eduList;
				
				$scope.mappingMARRAGE 		     = tota[0].body.marList;
				$scope.inputVO.MARRAGEList 	     = tota[0].body.marList;
				
				$scope.mappingHEALTH 		     = tota[0].body.heaList;
				$scope.inputVO.SICK_TYPEList     = tota[0].body.heaList;
				
				$scope.mappingQ3_PROD_TYPE		 = tota[0].body.q3ProdTypeList;
				$scope.inputVO.Q3_PROD_TYPEList	 = tota[0].body.q3ProdTypeList;
				
				$scope.mappingQ3_PROD_EXP		 = tota[0].body.q3ProdExpList;
				$scope.inputVO.Q3_PROD_EXPList	 = tota[0].body.q3ProdExpList;
				
				$scope.mappingEDU_CHANGE         = tota[0].body.eduChangeList;
				$scope.inputVO.EDU_CHANGEList    = tota[0].body.eduChangeList;
				
				$scope.mappingHEALTH_CHANGE      = tota[0].body.healthChangeList;
				$scope.inputVO.HEALTH_CHANGEList = tota[0].body.healthChangeList;
				
				$scope.mappingEMAIL_CHANGE       = tota[0].body.emailChangeList;
				$scope.inputVO.EMAIL_CHANGEList  = tota[0].body.emailChangeList;
			} 	
		});
    };
    
    $scope.init();
    
    //分頁初始化
    $scope.inquireInit = function(){
		$scope.quest_list = [];
		$scope.persional = [];
    };
    
    //基本資料初始化
    $scope.inipersional = function(){
		$scope.inputVO.cust_name = '';
		$scope.inputVO.cust_id = '';
		$scope.inputVO.EDUCATION = '';
		$scope.inputVO.CAREER = '';
		$scope.inputVO.MARRAGE = '';
		$scope.inputVO.CHILD_NO = '';
		$scope.inputVO.Y_N_update = '2';
		$scope.inputVO.gender_M = undefined;
		$scope.inputVO.gender_W = undefined;
		$scope.inputVO.birthday = undefined;
		$scope.inputVO.DAY_TYPE = false;
		$scope.inputVO.DAY_COD = undefined;
		$scope.inputVO.DAY = null;
		$scope.inputVO.NIGHT_TYPE = false;
		$scope.inputVO.NIGHT_COD = undefined;
		$scope.inputVO.NIGHT = undefined;
		$scope.inputVO.TEL_NO_TYPE = false;
		$scope.inputVO.TELNO_COD = undefined;
		$scope.inputVO.TEL_NO = undefined;		
		$scope.inputVO.FAX_TYPE = false;
		$scope.inputVO.FAX_COD = undefined;
		$scope.inputVO.FAX = undefined;
		$scope.inputVO.SICK_TYPE = undefined;
		$scope.inputVO.CUST_ADDR_1 = undefined;
		$scope.inputVO.EMAIL_ADDR = undefined;
		$scope.inputVO.CUST_EMAIL_BEFORE = undefined;
		$scope.lastrisklevel = undefined;
		$scope.EDUCATION_LABEL = '';
		$scope.CHILD_NO_LABEL = '';
		$scope.MARRAGE_LABEL = '';
		$scope.inputVO.CAREERList = $scope.mappingCAREER;
		$scope.inputVO.CHILD_NOList = $scope.mappingCHILD_NO;
		$scope.inputVO.EDUCATIONList = $scope.mappingEDUCATION;
		$scope.inputVO.MARRAGEList = $scope.mappingMARRAGE;
		$scope.inputVO.SICK_TYPEList = $scope.mappingHEALTH;
		//add by Brian
		$scope.inputVO.EDU_CHANGEList = $scope.mappingEDU_CHANGE;
		$scope.inputVO.HEALTH_CHANGEList = $scope.mappingHEALTH_CHANGE;
		$scope.inputVO.SCHOOL = '';
		$scope.inputVO.EDU_CHANGE = '';
		$scope.inputVO.HEALTH_CHANGE = '';
		$scope.inputVO.PDF_KYC_FLAG = "N";
    }
        
    //判斷手機開頭是否09
    $scope.checkTEL = function(){
    	if($scope.inputVO.TEL_NO_TYPE == true){
    		var TEL_title = $scope.inputVO.TEL_NO.substr(0,2);
    		
    		if(TEL_title == '09'){
    			return true;
    		}
    		//允許為空值，已用於更新手機號碼為空值
    		else if($scope.inputVO.TEL_NO == ''){
    			return true;
    		}
    		else{
    			return;
    		}
    	}
    	else{
    		return true;
    	}
    }
    
	//查詢
    $scope.queryData = function(){
    	$scope.inipersional();
    	$scope.inquireInit();
    	
    	if($scope.inputVO.CUST_ID ==''){
    		//無客戶ID但必須輸入解說人員
    		if($scope.inputVO.COMMENTARY_STAFF == ''){
    			$scope.showMsg("ehl_01_KYC310_005");
    		}
    		else{
    			$scope.inputVO.QUESTION_TYPE='02';
    			$scope.noupdatedata=false;
    			$scope.company=false;
    			$scope.querybirthday = false;
    			//不是其他程式進入
    			$scope.inputVO.FromElsePrintBlank = false;
    			
    			$scope.sendRecv("KYC310" , "NonCustID" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
					if(!isError){
			        	$scope.majorSick = false;
					}
					
					$scope.quest_list = tota[0].body.questionnaireList;
					
					if($scope.quest_list != null && $scope.inputVO.CUST_ID.length>=10){
						$scope.temp_QNineAns = angular.copy($scope.quest_list[8].ANSWER_LIST);
					}
					
					//題目&答案By Report
					$scope.inputVO.quest_list = tota[0].questionnaireList;
    			});
    		}
    	}
    	else{
    		if($scope.inputVO.COMMENTARY_STAFF == ''){
    			$scope.showMsg("ehl_01_KYC310_005");
    		}
    		else{
    			if($scope.inputVO.CUST_ID.length>=10){
    				$scope.inputVO.QUESTION_TYPE='02';
    				$scope.noupdatedata=false;
    				$scope.company=false;
    				$scope.querybirthday = false;
    			}else{
    				$scope.inputVO.QUESTION_TYPE='03';
    				$scope.noupdatedata=true;
    				$scope.querybirthday = true;
    				$scope.company=true;
    			}
    			
    			//不是其他程式進入
    			$scope.inputVO.FromElsePrintBlank=false;
    			
    			//查詢抓取客戶資料
    			$scope.sendRecv("KYC310" , "chkTimes" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
    				//有錯誤
    				if(isError){
						$scope.majorSick = true;
					}
    				//無錯誤
    				else{
						$scope.majorSick = false;
					}
    				//2020.12.03 #437
    				$scope.inputVO.isPSR = tota[0].body.isPSR;
    		    	if($scope.inputVO.isPSR){
    		    		$scope.showErrorMsg("ehl_01_KYC310_048");
    		    	}
    				//2018/08/01之後沒有做過網行銀KYC，臨櫃KYC也沒有輸入過錄音序號，需跳題
    				//debugger;
    				if(tota[0].body.doneKYC != "Y" ){
    					$scope.showMsg('ehl_02_KYC310_020');
					}
    				//已經刪除問卷兩次
    				if(tota[0].body.deletetwo){
						$scope.showMsg("ehl_01_KYC310_002");
						$scope.majorSick=true;
					}
    				//尚未刪除兩次問卷
    				else{
    					debugger
						$scope.quest_list = tota[0].body.questionnaireList;
						$scope.degrade = tota[0].body.degrade;			//特簽
						$scope.degrade_date = $scope.toJsDate(tota[0].body.degradeDate);	//免降等到期日
						$scope.inputVO.DEGRADE_DATE = $scope.degrade_date;
						$scope.custEduHighSchool = tota[0].body.custEduHighSchool;			//高中職以上學歷註記
						
						//高資產客戶註記
						$scope.inputVO.HNWC_DUE_DATE = $scope.toJsDate(tota[0].body.hnwcDueDate);
						$scope.inputVO.HNWC_INVALID_DATE = $scope.toJsDate(tota[0].body.hnwcInvaidDate);
						$scope.inputVO.HNWC_VALID_YN = tota[0].body.hnwcValidYN;
						
						//未滿18歲法代風險屬性
						$scope.inputVO.legalRegKycLevel = tota[0].body.legalRegKycLevel;
						//端末年收入
						$scope.inputVO.incomeFromCBS = tota[0].body.incomeFromCBS;
						
						if($scope.quest_list != null && $scope.inputVO.CUST_ID.length>=10){
    						$scope.temp_QNineAns = angular.copy($scope.quest_list[8].ANSWER_LIST);
						}
						
    					//題目&答案By Report
    					$scope.inputVO.quest_list = tota[0].questionnaireList;
    					
    					if($scope.quest_list.length > 0){
        					$scope.RISKList = $scope.quest_list[0].RISK_LIST;
    					}
    					
    					$scope.persional = tota[0].body.personalIformationList;
    					
    					$scope.vul_flag = $scope.persional[0].VUL_FLAG;						// 是否為弱勢（Y/N）
    					$scope.cust_pro_date = $scope.persional[0].CUST_PRO_DATE;			// 專業投資人效期
    					$scope.inputVO.CUST_PRO_DATE = $scope.toJsDate($scope.persional[0].CUST_PRO_DATE);	// 專業投資人效期
    					$scope.cust_pro_end = $scope.persional[0].CUST_PRO_END;				// 專業投資人效期是否到期
    					$scope.degrade_end = $scope.persional[0].DEGRADE_END;				// 免降等效期是否到期  					
    					
    					$scope.degrade_flag = $scope.persional[0].DEGRADE_FLAG;
    					
    					if(!tota[0].body.checkdoquestion){
        					$scope.inputVO.before_persional = $scope.persional;
        					if(tota[0].body.lastRiskLevel.length>0){
            					$scope.lastrisklevel = tota[0].body.lastRiskLevel;
        					}
    		        	}
    					else{
    		        		$scope.inputVO.before_persional = $scope.persional[0].before_persional;
    		        	}
    					
    					if($scope.persional != null){
    						if($scope.inputVO.CUST_ID.length < 10){
    							$scope.inputVO.Y_N_update = '1';
    						}
    						else{
    							$scope.inputVO.Y_N_update = $scope.persional[0].UPDATE_YN;
        						
    							if($scope.inputVO.Y_N_update == undefined){
        							$scope.inputVO.Y_N_update = '2';
        						}
    						}
    						
    						$scope.inputVO.cust_name = $scope.persional[0].CUST_NAME;
        					$scope.inputVO.cust_id = $scope.persional[0].CUST_ID;
        					$scope.inputVO.KYC_TEST_DATE_TEMP = $scope.persional[0].KYC_TEST_DATE;
        					$scope.inputVO.REC_SEQ = $scope.persional[0].REC_SEQ;
        					
        					$scope.inputVO.SCHOOL = $scope.persional[0].CUST_SCHOOL;
        					$scope.inputVO.EDU_CHANGE = $scope.persional[0].CUST_EDU_CHANGE;
        					
        					if($scope.persional[0].GENDER != null){
            					$scope.inputVO.GENDER = $scope.persional[0].GENDER.trim();
        					}
        					else{
        						$scope.inputVO.GENDER = '';
        					}
        					
        					if($scope.persional[0].EDUCATION != null){
            					$scope.inputVO.EDUCATION = $scope.persional[0].EDUCATION.trim();
            					$scope.EDUCATIONforCheck = $scope.inputVO.EDUCATION;
        					}
        					else{
            					$scope.inputVO.EDUCATION = '';
        					}
        					
        					if($scope.persional[0].CAREER != null){
            					$scope.inputVO.CAREER = $scope.persional[0].CAREER.trim();
        					}
        					else{
        						$scope.inputVO.CAREER = '';
        					}
        					
        					if($scope.persional[0].MARRAGE != null){
            					$scope.inputVO.MARRAGE = $scope.persional[0].MARRAGE.trim();
        					}
        					else{
            					$scope.inputVO.MARRAGE = '';
        					}
        					
        					if($scope.persional[0].CHILD_NO != null){
            					$scope.inputVO.CHILD_NO = $scope.persional[0].CHILD_NO.trim();
        					}
        					else{
            					$scope.inputVO.CHILD_NO = '';
        					}
        					
        					if($scope.persional[0].SICK_TYPE != null){
            					$scope.inputVO.SICK_TYPE = $scope.persional[0].SICK_TYPE.trim();
            					$scope.SICK_TYPEforCheck = $scope.inputVO.SICK_TYPE;
        					}
        					else{
            					$scope.inputVO.SICK_TYPE = '';
        					}
        					
        					$scope.inputVO.CUST_ADDR_1 = $scope.persional[0].CUST_ADDR_1;
        					if(tota[0].body.checkdoquestion){
        						$scope.inputVO.CUST_EMAIL_BEFORE = $scope.persional[0].CUST_EMAIL_BEFORE;
        					}else{
        						$scope.inputVO.CUST_EMAIL_BEFORE = $scope.persional[0].EMAIL_ADDR;
        					} 					
        					$scope.inputVO.EMAIL_ADDR = $scope.persional[0].EMAIL_ADDR;
        					$scope.inputVO.SAMEEMAIL_REASON = $scope.persional[0].SAMEEMAIL_REASON;
        					$scope.inputVO.SAMEEMAIL_CHOOSE = $scope.persional[0].SAMEEMAIL_CHOOSE;
        					
        					if($scope.inputVO.CUST_EMAIL_BEFORE == ''){						
        						$scope.inputVO.CUST_EMAIL_BEFORE = undefined;
        					}	
        					if($scope.inputVO.EMAIL_ADDR == ''){						
        						$scope.inputVO.EMAIL_ADDR = undefined;
        					}
        					
        					$scope.inputVO.RPRS_ID = $scope.persional[0].RPRS_ID;	//公司負責人ID
        					$scope.inputVO.RPRS_NAME = $scope.persional[0].RPRS_NAME;	//公司負責人姓名
        					
        					if($scope.inputVO.SICK_TYPE == '3'){
        						$scope.showMsg("ehl_01_KYC310_003");
        						$scope.majorSick = true;
        					}
        					
        					if($scope.persional[0].GENDER =='1'){
        						$scope.inputVO.gender_M=true;
        					}
        					
        					if($scope.persional[0].GENDER =='2'){
        						$scope.inputVO.gender_W=true;
        					}
        					
        					if($scope.persional[0].birthday != '' && $scope.persional[0].birthday != undefined){
        						$scope.inputVO.birthday = Date.parse($scope.persional[0].birthday);
        						$scope.querybirthday = true;
//            						//超過80歲&非專業投資人，無法承作KYC
//                					if(getAge($scope.inputVO.birthday) >= 80 && $scope.persional[0].TX_FLAG == 'N'){
//                						$scope.quest_list = undefined;
////                					$scope.showMsg("ehl_01_KYC310_006");
//                						$scope.showErrorEDUCTION('ehl_01_KYC310_006');
//                						$scope.majorSick = true;
//                					}
        					}
        					
        					//問卷頁面是否有勾選(日間電話)
        					if($scope.persional[0].DAY != undefined){
        						$scope.inputVO.DAY_TYPE = true;
//        						$scope.inputVO.DAY = $scope.persional[0].DAY;
//        						$scope.inputVO.DAY_COD = $scope.persional[0].DAYCOD;
        					}
        					console.log($scope.inputVO.DAY_TYPE);
        					//待審核頁面是否有勾選(日間電話)
        					if($scope.persional[0].DAY_TYPE == '1'){
        						$scope.inputVO.DAY_TYPE = true;
        					}else if($scope.persional[0].DAY_TYPE == '2'){
        						$scope.inputVO.DAY_TYPE = false;
        					}
        		
        					$scope.inputVO.DAY = $scope.persional[0].DAY;
        					$scope.inputVO.DAY_COD = $scope.persional[0].DAYCOD;
        					
        					//問卷頁面是否有勾選(夜間電話)
        					if($scope.persional[0].NIGHT != undefined){
        						$scope.inputVO.NIGHT_TYPE = true;
//        						$scope.inputVO.NIGHT = $scope.persional[0].NIGHT;
//        						$scope.inputVO.NIGHT_COD = $scope.persional[0].NIGHTCOD;
        					} 
        					
        					//待審核頁面是否有勾選(夜間電話)
        					if($scope.persional[0].NIGHT_TYPE == '1'){
        						$scope.inputVO.NIGHT_TYPE = true;
        					}else if($scope.persional[0].NIGHT_TYPE == '2'){
        						$scope.inputVO.NIGHT_TYPE = false;
        					} 
        						
        					$scope.inputVO.NIGHT = $scope.persional[0].NIGHT;
        					$scope.inputVO.NIGHT_COD = $scope.persional[0].NIGHTCOD;
        					
        					//問卷頁面是否有勾選(行動電話)
        					if($scope.persional[0].TEL_NO != undefined){
        						$scope.inputVO.TEL_NO_TYPE = true;
//        						$scope.inputVO.TEL_NO = $scope.persional[0].TEL_NO;
//        						$scope.inputVO.TELNO_COD = $scope.persional[0].TELNO_COD;
        					}
        					
        					//待審核頁面是否有勾選(行動電話)
        					if($scope.persional[0].TEL_NO_TYPE == '1'){
        						$scope.inputVO.TEL_NO_TYPE = true;
        					}else if($scope.persional[0].TEL_NO_TYPE == '2'){
        						$scope.inputVO.TEL_NO_TYPE = false;
        					}
        						
        					$scope.inputVO.TEL_NO = $scope.persional[0].TEL_NO;
        					$scope.inputVO.TELNO_COD = $scope.persional[0].TELNO_COD;
        					
        					//問卷頁面是否有勾選(傳真)
        					if($scope.persional[0].FAX != undefined){
        						$scope.inputVO.FAX_TYPE = true;
//        						$scope.inputVO.FAX = $scope.persional[0].FAX;
//        						$scope.inputVO.FAX_COD = $scope.persional[0].FAX_COD;
        					}
        					
        					//待審核頁面是否有勾選(傳真)
        					if($scope.persional[0].FAX_TYPE == '1'){
        						$scope.inputVO.FAX_TYPE = true;
        					}else if($scope.persional[0].FAX_TYPE == '2'){
        						$scope.inputVO.FAX_TYPE = false;
        					}
        					
        					$scope.inputVO.FAX = $scope.persional[0].FAX;
        					$scope.inputVO.FAX_COD = $scope.persional[0].FAX_COD;
        					             					
        					
    					}
    					//檢查特殊條件職業、年齡、重大傷病、專投
    					$scope.checkEDUCTION();
    					$scope.checksicktype();
					}
    				
					if(tota[0].body.checkdoquestion){
		        		$scope.dobouledo();
		        	}
    			});
    		}
    	}
    };
        
    //是否更新資料
    $scope.checkupdate = function(){
    	//不更新資料
    	if($scope.inputVO.Y_N_update == '1'){
    		$scope.noupdatedata = true;
    		if($scope.persional[0].GENDER != null){
				$scope.inputVO.GENDER = $scope.persional[0].GENDER.trim();
			}else{
				$scope.inputVO.GENDER = '';
			}
    		
			if($scope.persional[0].EDUCATION != null){
				$scope.inputVO.EDUCATION = $scope.persional[0].EDUCATION.trim();
			}else{
				$scope.inputVO.EDUCATION = '';
			}
			
			if($scope.persional[0].CAREER != null){
				$scope.inputVO.CAREER = $scope.persional[0].CAREER.trim();
			}else{
				$scope.inputVO.CAREER = '';
			}
			
			if($scope.persional[0].MARRAGE != null){
				$scope.inputVO.MARRAGE = $scope.persional[0].MARRAGE.trim();
			}else{
				$scope.inputVO.MARRAGE = '';
			}
			
			if($scope.persional[0].CHILD_NO != null){
				$scope.inputVO.CHILD_NO = $scope.persional[0].CHILD_NO.trim();
			}else{
				$scope.inputVO.CHILD_NO = '';
			}
			
			if($scope.persional[0].SICK_TYPE != null){
				$scope.inputVO.SICK_TYPE = $scope.persional[0].SICK_TYPE.trim();
			}else{
				$scope.inputVO.SICK_TYPE = '';
			}
			
			$scope.inputVO.CUST_ADDR_1 = $scope.persional[0].CUST_ADDR_1;
			$scope.inputVO.EMAIL_ADDR = $scope.persional[0].EMAIL_ADDR;
			
			if($scope.inputVO.EMAIL_ADDR == ''){
				$scope.inputVO.EMAIL_ADDR = undefined;
			}
			
			if($scope.persional[0].DAY != null){
				$scope.inputVO.DAY_TYPE = true;
				$scope.inputVO.DAY = $scope.persional[0].DAY;
				$scope.inputVO.DAY_COD = $scope.persional[0].DAYCOD;
			}
			
			if($scope.persional[0].NIGHT != null){
				$scope.inputVO.NIGHT_TYPE = true;
				$scope.inputVO.NIGHT = $scope.persional[0].NIGHT;
				$scope.inputVO.NIGHT_COD = $scope.persional[0].NIGHTCOD;
			} 
			
			if($scope.persional[0].TEL_NO != null){
				$scope.inputVO.TEL_NO_TYPE = true;
				$scope.inputVO.TEL_NO = $scope.persional[0].TEL_NO;
			}
			
			if($scope.persional[0].FAX != null){
				$scope.inputVO.FAX_TYPE = true;
				$scope.inputVO.FAX = $scope.persional[0].FAX;
			} 
    	}
    	
    	//更新資料
    	if($scope.inputVO.Y_N_update == '2'){
    		$scope.noupdatedata = false;
    	}
    }
    
    //清除
    $scope.Initial = function(){
    	$scope.init();
    	$scope.inipersional();
    	$scope.inquireInit()
    };
    
    //列印空白表單--中文CH、英文ENG
    $scope.printNewQuestionnaire = function(type){
    	if($scope.inputVO.isPSR){
    		$scope.showErrorMsg("ehl_01_KYC310_048");
    	}
    	if($scope.inputVO.CUST_ID ==''){
    		//無客戶ID列印空白表單
    		if($scope.inputVO.COMMENTARY_STAFF == ''){
    			$scope.showMsg("ehl_01_KYC310_005");
    		}
    		else{
    			$scope.inputVO.chk_type = (type == 'ENG' ? 'print_ENG' : 'print');
    			$scope.inputVO.quest_list = $scope.quest_list;
    			$scope.sendRecv("KYC310" , "chkAuth" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
    				if(tota[0].body == true){
        				//$scope.showMsg("列印空白");
					}
    			});
    		}
    	}
    	else{
    		if($scope.inputVO.COMMENTARY_STAFF == ''){
    			$scope.showMsg("ehl_01_KYC310_005");
    		}
    		else{
    			if($scope.inputVO.CUST_ID.length>=10){
    				$scope.inputVO.QUESTION_TYPE='02';//自然人
    			}
    			else{
    				$scope.inputVO.QUESTION_TYPE='03';//法人
    			}
    			
    			$scope.inputVO.chk_type = (type == 'ENG' ? 'print_ENG' : 'print');
    			$scope.inputVO.quest_list = $scope.quest_list;
    			$scope.sendRecv("KYC310" , "chkAuth" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
					if(tota[0].body == true){
        				//$scope.showMsg("列印空白");
					}
    			});
    		}
    	}
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
    	if($scope.inputVO.quest_list[0].ANSWER_LIST[0].select && $scope.inputVO.quest_list[0].ANSWER_LIST[0].select == true){
    		return true;
    	}
    	else if($scope.inputVO.quest_list[0].ANSWER_LIST[1].select && $scope.inputVO.quest_list[0].ANSWER_LIST[1].select == true){
    		return true;
    	}
    	else if($scope.inputVO.quest_list[0].ANSWER_LIST[2].select && $scope.inputVO.quest_list[0].ANSWER_LIST[2].select == true){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    //算分&比對風險區間&分數相關欄位
    //判斷計分規則CRR_RULE，取得風險屬性C值
    $scope.checkQuestion_Risk = function(INVESTOREXAM_M){
		var sum = 0;		//原始總分
		var sumC = 0;		//風險偏好總分
		var sumW = 0;		//風險承受能力總分
		var sumCW = 0;		//CRR總分_矩陣
		var intScore = 0;	//截距分
		var ansList = [];
		var q3SelCount = 0;	//Q3答案選項選了幾個
		//sum = Number($scope.cust_sum[0].DATA);	//原來參數中的基本分應該不用了，改為放在TABLE中的截距分
		
		//檢查未選取題目&算分
		for(var a = 0 ; a < $scope.quest_list.length ; a++){
			var ans2 = [];
			
			//截距分
			intScore =  $scope.quest_list[a].INT_SCORE;
			
			//第3題分數與複選題不同，只選最高分計算
			if($scope.quest_list[a].QST_NO == "3") {
				var Q3Score = -999;	//第3題分數
				for(var b = 0 ; b < $scope.quest_list[a].ANSWER_LIST.length ; b++){					
					if($scope.quest_list[a].ANSWER_LIST[b].select == true){
						ans2.push($scope.quest_list[a].ANSWER_LIST[b].ANSWER_SEQ);
						
						//這個答案選項分數比較高，第3題取這個選項的分數
						if($scope.quest_list[a].ANSWER_LIST[b].FRACTION > Q3Score) {
							Q3Score = $scope.quest_list[a].ANSWER_LIST[b].FRACTION;
						}
						
						//Q3每一種商品都要選年期
						q3SelCount++;
					}
					
					//檢查是否每個問題都有回答
					if(b == $scope.quest_list[a].ANSWER_LIST.length - 1){
						if(q3SelCount < $scope.inputVO.Q3_PROD_TYPEList.length) {
							return $scope.showMsg("Q3每一種商品都必需選年期");
						}
					}
				}
				
				//分數加上Q3最高分選項分數
				sum += Q3Score;
				if($scope.quest_list[a].SCORE_TYPE == "C") sumC += Q3Score
				if($scope.quest_list[a].SCORE_TYPE == "W") sumW += Q3Score;
//				alert("Q3Score: " + Q3Score);
			} else {	//非第3題
				for(var b = 0 ; b < $scope.quest_list[a].ANSWER_LIST.length ; b++) {
					if($scope.quest_list[a].ANSWER_LIST[b].select == true){
						ans2.push($scope.quest_list[a].ANSWER_LIST[b].ANSWER_SEQ);
						sum += $scope.quest_list[a].ANSWER_LIST[b].FRACTION;
						
						if($scope.quest_list[a].SCORE_TYPE == "C") sumC += $scope.quest_list[a].ANSWER_LIST[b].FRACTION;
						if($scope.quest_list[a].SCORE_TYPE == "W") sumW += $scope.quest_list[a].ANSWER_LIST[b].FRACTION;
					}
					
					//檢查是否每個問題都有回答
					if(b == $scope.quest_list[a].ANSWER_LIST.length - 1){
						if(ans2.length <= 0){
							return $scope.showMsg("Q"+(a+1)+"未選取答案");
						}
					}
				}
			}
			
			ansList.push(ans2);
		}
		
//		alert("Sum: " + sum + "\n sumC: " + sumC + "\n sumW: " + sumW);
		
		//核對風險區間(原始總分計算)
		sum += intScore;	//加上截距分
		var risk_level = '';
		var max_risk = $scope.quest_list[0].RISK_LIST.length-1
		
		//原始總分
		if(sum<=0){
			$scope.inputVO.SCORE_ORI_TOT = 0;
		}
		else{
			$scope.inputVO.SCORE_ORI_TOT = sum;
		}
		
		$scope.inputVO.ANSWER_2 = ansList;
		
		if($scope.quest_list[0].RISK_LIST.length>1){
			if(sum<=$scope.quest_list[0].RISK_LIST[0].RL_UP_RATE){
				risk_level = $scope.quest_list[0].RISK_LIST[0].CUST_RL_ID;
			}
			else{
				for(var c = 0;c<$scope.quest_list[0].RISK_LIST.length;c++){
					for(var d = c+1;d>c;d--){
						if(d<$scope.quest_list[0].RISK_LIST.length){
							if(sum > $scope.quest_list[0].RISK_LIST[c].RL_UP_RATE && 
							   sum <= $scope.quest_list[0].RISK_LIST[d].RL_UP_RATE
							){
								risk_level = $scope.quest_list[0].RISK_LIST[d].CUST_RL_ID;
							}
							else if(sum>$scope.quest_list[0].RISK_LIST[max_risk].RL_UP_RATE || sum==$scope.quest_list[0].RISK_LIST[max_risk].RL_UP_RATE){
								risk_level = $scope.quest_list[0].RISK_LIST[max_risk].CUST_RL_ID;
							}
						}
					}
				}
			}
		}else{
			risk_level = $scope.quest_list[0].RISK_LIST[0].CUST_RL_ID;
		}
		
		//原始總分取得的風險屬性
		$scope.inputVO.CRR_ORI = risk_level;
		$scope.inputVO.RISKRANGE = $scope.inputVO.SCORE_ORI_TOT;
		
		//CW矩陣計算
		sumCW = sumC + sumW + intScore;	//總分 = 風險偏好總分 + 風險承受能力加總 + 截距分
		$scope.inputVO.SCORE_C = sumC;
		$scope.inputVO.SCORE_W = sumW;
		$scope.inputVO.SCORE_CW_TOT = sumCW;
		$scope.inputVO.RISK_LOSS_RATE = ($scope.inputVO.SCORE_ORI_TOT + 62) / 1000 * 100; //#0579
		if($scope.inputVO.RISK_LOSS_RATE <= 0) $scope.inputVO.RISK_LOSS_RATE = 0;
		
		$scope.inputVO.RS_VERSION = $scope.quest_list[0].RS_VERSION;	//降等機制矩陣版號
		$scope.inputVO.RLR_VERSION = $scope.quest_list[0].RLR_VERSION;	//可承受風險損失率版號
		$scope.sendRecv("KYC310" , "getCRRMatrix" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
			if(tota[0].body.CRR_MATRIX){
				$scope.inputVO.CRR_MATRIX = tota[0].body.CRR_MATRIX;	//風險級距矩陣C值
			}
			if(tota[0].body.RISK_LOSS_LEVEL){
				$scope.inputVO.RISK_LOSS_LEVEL = tota[0].body.RISK_LOSS_LEVEL;	//可承受風險損失率對應C值
			}
			
			//Y: 總分與CW都要算，取低者為風險屬性及分數
			if($scope.inputVO.CRR_RULE == "Y") {
				if($scope.inputVO.CRR_MATRIX.substring(1,2) < $scope.inputVO.CRR_ORI.substring(1,2)) {
					risk_level = $scope.inputVO.CRR_MATRIX;
					$scope.inputVO.RISKRANGE = $scope.inputVO.SCORE_CW_TOT;
				} else {
					risk_level = $scope.inputVO.CRR_ORI;
					$scope.inputVO.RISKRANGE = $scope.inputVO.SCORE_ORI_TOT;
				}
			}
			
			//算完分數後，判斷是否降等 => submitData
			//判斷是否專投
			var age = getAge($scope.inputVO.birthday);
			var sicktype = $scope.inputVO.SICK_TYPE;
			var eduction = $scope.inputVO.EDUCATION;
			var degrade = $scope.degrade;//特簽
			
			if($scope.persional != null){
				//判斷是否為專業投資人或高資產客戶
				if($scope.persional[0].TX_FLAG == "Y" || $scope.inputVO.HNWC_VALID_YN == "Y") {
//					alert('專投：' + risk_level);
					var txFlag = $scope.persional[0].TX_FLAG;
					$scope.inputVO.CUST_RISK_AFR = $scope.TX_declineLevel(age, sicktype, eduction, risk_level , degrade, txFlag);
					
					if($scope.inputVO.CUST_RISK_AFR != undefined){
						if(INVESTOREXAM_M != undefined){
							$scope.inputVO.TEST_BEF_DATE = INVESTOREXAM_M.TEST_BEF_DATE;
							$scope.inputVO.CUST_RISK_BEF = INVESTOREXAM_M.CUST_RISK_BEF;
						}
						$scope.inputVO.EXAM_VERSION = $scope.quest_list[0].EXAM_VERSION
						$scope.submitData();
					}
				} else {
//					alert('非專投：' + risk_level);
					$scope.inputVO.CUST_RISK_AFR = $scope.declineLevel(age, sicktype, eduction, risk_level , degrade);
					
					if($scope.inputVO.CUST_RISK_AFR != undefined){
						if(INVESTOREXAM_M != undefined){
							$scope.inputVO.TEST_BEF_DATE = INVESTOREXAM_M.TEST_BEF_DATE;
							$scope.inputVO.CUST_RISK_BEF = INVESTOREXAM_M.CUST_RISK_BEF;
						}
						
						$scope.inputVO.EXAM_VERSION = $scope.quest_list[0].EXAM_VERSION
						$scope.submitData();
					}
				}					
			} else {
				//$scope.inputVO.CUST_RISK_AFR = $scope.declineLevel(age, sicktype, eduction, risk_level , degrade);
				$scope.inputVO.CUST_RISK_AFR = risk_level; //法人不降等
				
				if($scope.inputVO.CUST_RISK_AFR != undefined){
					if(INVESTOREXAM_M != undefined){
						$scope.inputVO.TEST_BEF_DATE = INVESTOREXAM_M.TEST_BEF_DATE;
						$scope.inputVO.CUST_RISK_BEF = INVESTOREXAM_M.CUST_RISK_BEF;
					}
					
					$scope.inputVO.EXAM_VERSION = $scope.quest_list[0].EXAM_VERSION
					$scope.submitData();
				}
			}
		});
    }
    
    //填寫完成
    $scope.btnSubmit = function(){

    	//客戶id是空的
    	if($scope.inputVO.CUST_ID ==''){
    		$scope.showMsg("ehl_01_KYC310_004");
    		return;
    	}
		//解說人員是空的
    	else if($scope.inputVO.COMMENTARY_STAFF == ''){
			$scope.showMsg("ehl_01_KYC310_005");
			return;
		}
    	
    	var EMAIL = undefined;
		var EMAIL2 = undefined;
        debugger;
        //顯示文字調整
        $scope.email_before_show = $scope.inputVO.CUST_EMAIL_BEFORE;
        if($scope.email_before_show == undefined || $scope.email_before_show == ''){
       	 $scope.email_before_show = '無';
        }
        $scope.email_after_show = $scope.inputVO.EMAIL_ADDR;
        if($scope.email_after_show == undefined || $scope.email_after_show == ''){
        	$scope.email_after_show = '無';
        }
        $scope.sameEmail_reason_show = '';
        if($scope.inputVO.SAMEEMAIL_REASON){
        	$scope.sameEmail_reason_show = $scope.inputVO.SAMEEMAIL_REASON;
        } else if($scope.inputVO.SAMEEMAIL_CHOOSE){
        	$scope.sameEmail_reason_show = $scope.mappingEMAIL_CHANGE[$scope.inputVO.SAMEEMAIL_CHOOSE -1].LABEL;
        }
        debugger;
		if($scope.inputVO.CUST_EMAIL_BEFORE == $scope.inputVO.EMAIL_ADDR){
			EMAIL = '客戶留存的電子郵件為：【' + $scope.email_before_show + '】，請客戶再次確認留存資料正確性。';
		}else if($scope.inputVO.CUST_EMAIL_BEFORE != $scope.inputVO.EMAIL_ADDR && $scope.haveSameEmail){
			EMAIL = '客戶留存的電子郵件由【' + $scope.email_before_show + '】更新為【' + $scope.email_after_show +
			'】，本行已發送確認連結至客戶新留存信箱，本次變更將於點選確認連結後生效，請客戶再次確認留存資料正確性。';
			EMAIL2 = '經比對，客戶留存的電子郵件與本行其他客戶有重覆情形，原因為：【' +  $scope.sameEmail_reason_show + '】，請客戶再次確認。';
		}else if($scope.inputVO.CUST_EMAIL_BEFORE != $scope.inputVO.EMAIL_ADDR && !$scope.haveSameEmail){
		    if($scope.email_after_show == '無'){
		    	EMAIL = '客戶留存的電子郵件由【' + $scope.email_before_show + '】更新為【' + $scope.email_after_show +
				'】，請客戶再次確認留存資料正確性。';
		    }else {
		    	EMAIL = '客戶留存的電子郵件由【' + $scope.email_before_show + '】更新為【' + $scope.email_after_show +
				'】，本行已發送確認連結至客戶新留存信箱，本次變更將於點選確認連結後生效，請客戶再次確認留存資料正確性。';
		    }		
		}
				 	    								
		//自然人
		if($scope.inputVO.CUST_ID.length >= 10){
			$scope.inputVO.QUESTION_TYPE='02';

    		//判斷基本資料欄位檢核
			if($scope.check_person_data()){
    			$scope.inputVO.quest_list = $scope.quest_list;
    			$scope.inputVO.chk_type = 'submit';
    			
    			//取得檢核資料
    			$scope.sendRecv("KYC310" , "chkAuth" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
    				var age = getAge($scope.inputVO.birthday);
    				var chkAuthReturn = tota[0].body;
    				
    					var EDUCTION = undefined;
    					var CAREER = undefined;
    					var MARRIAGE = undefined;
    					var CHILDREN = undefined;
    					var HEALTH = undefined;
    					var Q3ProdExpChg = undefined;
		        		var Q3ProdDecrease = undefined;
		        		var Q10Q11Check = undefined;
		        		var Q2IncomeCheck = undefined;

		        		//未滿18歲客戶，學歷填寫高中職以上且無"高中職以上學歷註記"，不可填答
						if(age < 18 && $scope.custEduHighSchool == 'N' && 
							($scope.inputVO.EDUCATION == '5' || $scope.inputVO.EDUCATION == '4' || $scope.inputVO.EDUCATION == '3' 
								|| $scope.inputVO.EDUCATION == '2' || $scope.inputVO.EDUCATION == '1')) {
							$scope.showErrorMsg('該客戶無「高中職以上學歷註記」，需檢附學歷證明文件並於端末06711501交易維護註記，始可填答高中職(含)以上學歷');
							return;
						}
						
		        		//確認與上一次承作基本資料差異，若有異動會跳提示訊息
    		        	$scope.sendRecv("KYC310", "getLastResult", $scope.kyc310inputvo, $scope.inputVO, 
    		        		function(tota,isError)
    		        	{
	    					if(tota[0].body.Comparison.EDUCTION){
	    						$scope.inputVO.CUST_EDUCTION_BEFORE = tota[0].body.Comparison.EDUCTION.beforEDUCTION;
	    						if(getLabel('EDUCATION', tota[0].body.Comparison.EDUCTION.beforEDUCTION) == "") {
	    							EDUCTION = '【教育程度】異動為【'+getLabel('EDUCATION', tota[0].body.Comparison.EDUCTION.afterEDUCTION)+'】，請再次向客戶確認\n';
	    						} else {		    						
		    						EDUCTION = '【教育程度】從【' + getLabel('EDUCATION', tota[0].body.Comparison.EDUCTION.beforEDUCTION) + '】異動為【'+getLabel('EDUCATION', tota[0].body.Comparison.EDUCTION.afterEDUCTION)+'】，請再次向客戶確認\n';	    					
	    						}
	    					} else {
	    						$scope.inputVO.CUST_EDUCTION_BEFORE = $scope.inputVO.EDUCATION;
	    					} 
	    					
	    					if(tota[0].body.Comparison.CAREER){
	    						$scope.inputVO.CUST_CAREER_BEFORE = tota[0].body.Comparison.CAREER.beforCAREER;
	    						CAREER = '【職業】從【'+getLabel('CAREER', tota[0].body.Comparison.CAREER.beforCAREER)+'】異動為【'+getLabel('CAREER', tota[0].body.Comparison.CAREER.afterCAREER)+'】，請再次向客戶確認\n';
	    					}else{
	    						$scope.inputVO.CUST_CAREER_BEFORE = $scope.inputVO.CAREER;
	    					}
	    					
	    					if(tota[0].body.Comparison.MARRIAGE){
	    						$scope.inputVO.CUST_MARRIAGE_BEFORE = tota[0].body.Comparison.MARRIAGE.beforMARRIAGE;
	    						MARRIAGE = '【婚姻】從【'+getLabel('MARRAGE', tota[0].body.Comparison.MARRIAGE.beforMARRIAGE)+'】異動為【'+getLabel('MARRAGE', tota[0].body.Comparison.MARRIAGE.afterMARRIAGE)+'】，請再次向客戶確認\n';
	    					}
	    					else{
	    						$scope.inputVO.CUST_MARRIAGE_BEFORE = $scope.inputVO.MARRAGE;
	    					}
	    					
	    					if(tota[0].body.Comparison.CHILDREN){
	    						$scope.inputVO.CUST_CHILDREN_BEFORE = tota[0].body.Comparison.CHILDREN.beforCHILDREN
	    						CHILDREN = '【子女人數】從【'+getLabel('CHILD_NO', tota[0].body.Comparison.CHILDREN.beforCHILDREN)+'】異動為【'+getLabel('CHILD_NO', tota[0].body.Comparison.CHILDREN.afterCHILDREN)+'】，請再次向客戶確認\n';
	    					}
	    					else{
	    						$scope.inputVO.CUST_CHILDREN_BEFORE = $scope.inputVO.CHILD_NO;
	    					}
	    					
	    					if(tota[0].body.Comparison.HEALTH){
	    						$scope.inputVO.CUST_HEALTH_BEFORE = tota[0].body.Comparison.HEALTH.beforHEALTH
	    						HEALTH = '【重大傷病證明】從【'+getLabel('HEALTH', tota[0].body.Comparison.HEALTH.beforHEALTH)+'】異動為【'+getLabel('HEALTH', $scope.inputVO.SICK_TYPE)+'】，請再次向客戶確認\n';
	    					}
	    					else{
	    						$scope.inputVO.CUST_HEALTH_BEFORE = $scope.inputVO.SICK_TYPE;
	    					}
	    					
	    					//與前次承作Q3 or Q8&Q9答案比對
	        				if(tota[0].body.Q3ProdExpChgList != null && tota[0].body.Q3ProdExpChgList.length > 0){	//Q3投資年期上升2級以上
	        					Q3ProdExpChg = "";
	        					for(var i = 0 ; i < tota[0].body.Q3ProdExpChgList.length ; i++) {
	        						Q3ProdExpChg += $filter('i18n')('ehl_01_KYC310_015',
	        								[tota[0].body.Q3ProdExpChgList[i].ProdType, tota[0].body.Q3ProdExpChgList[i].PreProdExp, tota[0].body.Q3ProdExpChgList[i].CurProdExp]) + "<br/>";
	        					}
				        	}
	        				
				        	if(tota[0].body.Q3ProdDecrease){	//Q3投資商品上次有本次沒有
				        		Q3ProdDecrease = $filter('i18n')('ehl_01_KYC310_016',[tota[0].body.Q3ProdDecrease]);
				        	}
//				        	console.log(JSON.stringify(tota));
				        	
				        	//Q10 & Q11 檢核
    			        	if(chkAuthReturn.ansSeqQ10 == "1" && chkAuthReturn.ansSeqQ11 == "5") {
//    			        		Q10Q11Check = $filter('i18n')('ehl_01_KYC310_045');
    			        		$scope.showErrorMsg('ehl_01_KYC310_045');
    			        		return;
    			        	} else if(chkAuthReturn.ansSeqQ10 == "4" && chkAuthReturn.ansSeqQ11 == "1") {
//    			        		Q10Q11Check = $filter('i18n')('ehl_01_KYC310_046');
    			        		$scope.showErrorMsg('ehl_01_KYC310_046');
    			        		return;
    			        	}
    			        	
    			        	//Q2年收入檢核
    			        	//KYC收入與端末收入不符
    			        	if(tota[0].body.Comparison.INCOME_CBS && tota[0].body.Comparison.INCOME_CBS.INCOMECBS_CHK == "N") {
    			        		Q2IncomeCheck = "客戶於本行留存年收入為【" + tota[0].body.Comparison.INCOME_CBS.INCOME_CBS + "】萬元，本次更新為【" + tota[0].body.Comparison.INCOME_CBS.Q2ANS_DESC + "】，請同步更新客戶端末系統96700000年收入欄位\n";
    			        	}
    			        	
			        	   	//未滿18歲檢核
							if(age < 18) {
								var ANSList = $scope.inputVO.quest_list[0].ANSWER_LIST;
					        	var Income = '';
					        	var EDUCTION_12 = undefined;
								var CHILD_NO_12 = undefined;
								var MARRIAGE_12 = undefined;
								var CAREER_12 = undefined;
								var INCOME_12 = undefined;
								
					        	if(ANSList != undefined){
					            	for(var i=0;i<3;i++){
					            		if(ANSList[i].select == true){
					            			Income = Income+ANSList[i].ANSWER_DESC+'、';
					            		}
					            	}
					        	}
					        	//未滿12歲
					        	if(age < 12) {
						        	//未滿12歲且子女人數為【{0}】，請再次向客戶確認
						        	if($scope.inputVO.CHILD_NO != '0'){
						        		CHILD_NO_12 = $filter('i18n')('ehl_01_KYC310_012',[getLabel('CHILD_NO', $scope.inputVO.CHILD_NO)]);
						        	}
					        	}
					        	//未滿15 歲
								if(age < 15) {
									//未滿15 歲且職業為【 {0} 】，請再次向客戶確認
									if($scope.inputVO.CAREER != '12' && $scope.inputVO.CAREER != '19' && $scope.inputVO.CAREER != '22'){
						        		CAREER_12 = $filter('i18n')('ehl_01_KYC310_025',[getLabel('CAREER', $scope.inputVO.CAREER)]);
						        	}
									//未滿15歲且收入來源為【{0}】，請再次向客戶確認
						        	if(Income != ''){
						        		INCOME_12 = $filter('i18n')('ehl_01_KYC310_014',[Income.substr(0,Income.length-1)]);
						        	}
								} else {
									//15 歲(含)以上
									if(($scope.inputVO.CAREER == '21' || $scope.inputVO.CAREER == '17' || $scope.inputVO.CAREER == '22') 
											&& ($scope.underTwele_QOne())) {
										var Income = '';
										if(ANSList != undefined){
							            	for(var i=0;i<3;i++){
							            		if(ANSList[i].select == true){
							            			Income = Income+ANSList[i].ANSWER_DESC+'、';
							            		}
							            	}
							        	}
										//職業為【{0}】，收入來源為【{1}】，請再次向客戶確認
						        		CAREER_12 = $filter('i18n')('ehl_01_KYC310_050',[getLabel('CAREER', $scope.inputVO.CAREER), Income.substr(0,Income.length-1)]);
						        	}
								}
								//未滿18 歲
								if(age < 18) {
									//未滿18 歲且婚姻狀態為【{0}】，請再次向客戶確認
									if($scope.inputVO.MARRAGE == '2'){
						        		MARRIAGE_12 = $filter('i18n')('ehl_01_KYC310_013',[getLabel('MARRAGE', $scope.inputVO.MARRAGE)]);
						        	}
								}
								
					        	debugger;
					        	//將異動結果傳入提醒視窗
	    	    				var parameter = {        	    						
	    	    						text:CHILD_NO_12,
	    								text1:CAREER_12,
	    								text2:INCOME_12,
	    								text3:EDUCTION_12,
	    								text4:MARRIAGE_12,
	    								text5:EDUCTION,
	                					text6:CAREER,
	                					text7:MARRIAGE,
	                					text8:CHILDREN,
	                					text9:HEALTH,
	                					text10:Q3ProdExpChg,
	                					text11:Q3ProdDecrease,
	                					text14:Q10Q11Check,
	                					text15:Q2IncomeCheck,
	                					INVESTOREXAM_M:tota[0].body.Comparison.INVESTOREXAM_M,
	                					text16:EMAIL,
	                					text17:EMAIL2
	    	    				};
					        	
	    	    				//重大傷病判斷式
	    			        	if($scope.SICK_TYPEforCheck == '2'){
	    			        		if($scope.inputVO.SICK_TYPE == '1'){
	    			        			$scope.showReasonDialog(3,$scope.inputVO.HEALTH_CHANGEList,function(data){
	    			        				if(data.value == 'cancel'){
	    			        					return;
	    			        				}
	    			        				$scope.inputVO.HEALTH_CHANGE = data.value.health_change;
	    			        				$scope.Under12Confirm(parameter);
	    			        			});   			        		
	    			        		} else {
	    			        			$scope.Under12Confirm(parameter);
	    			        		}
	    			        	} else {
	    			        		$scope.Under12Confirm(parameter);
	    			        	}
							} else if(EDUCTION != undefined || CAREER != undefined || MARRIAGE != undefined || 
									 CHILDREN != undefined || HEALTH != undefined || Q3ProdExpChg != undefined || Q3ProdDecrease != undefined ||
									 Q10Q11Check != undefined || EMAIL != undefined || Q2IncomeCheck != undefined) {
								debugger;
								//將異動結果傳入提醒視窗
	    	    				var parameter = {    	    					
	                					text:EDUCTION,
	                					text1:CAREER,
	                					text2:MARRIAGE,
	                					text3:CHILDREN,
	                					text4:HEALTH,
	                					text5:Q3ProdExpChg,
	                					text6:Q3ProdDecrease,
	                					text7:Q10Q11Check,
	                					text11:Q2IncomeCheck,
	                					INVESTOREXAM_M:tota[0].body.Comparison.INVESTOREXAM_M,
	                					text16:EMAIL,
	                					text17:EMAIL2
	    	    				};
								debugger;
								//18歲(含)以上客戶，學歷由國中以下異動為高中職以上，需留存學校名稱及原因
								if(age >= 18) {
									if(($scope.EDUCATIONforCheck == '' || $scope.EDUCATIONforCheck == '8' || $scope.EDUCATIONforCheck == '6' || $scope.EDUCATIONforCheck == '7') &&
	    	    						($scope.inputVO.EDUCATION == '5' || $scope.inputVO.EDUCATION == '4' || $scope.inputVO.EDUCATION == '3' || $scope.inputVO.EDUCATION == '2' || $scope.inputVO.EDUCATION == '1')) {
										$scope.showReasonDialog(1,$scope.inputVO.EDU_CHANGEList,function(data) {
    	    								if(data.value == 'cancel'){
    	    									return;
    	    								}
        	    							$scope.inputVO.EDU_CHANGE = data.value.edu_choose;
        	    							//重大傷病判斷式
        		    			        	if($scope.SICK_TYPEforCheck == '2'){
        		    			        		if($scope.inputVO.SICK_TYPE == '1'){
        		    			        			$scope.showReasonDialog(3,$scope.inputVO.HEALTH_CHANGEList,function(data){
        		    			        				if(data.value == 'cancel'){
        		    			        					return;
        		    			        				}
        	        			        				$scope.inputVO.HEALTH_CHANGE = data.value.health_change;
        		    			        				$scope.Over12Confirm(parameter);	    							
        		    			        			});   			        		
        		    			        		}else{
        		    			        			$scope.Over12Confirm(parameter);
        		    			        		}
        		    			        	}else{
        		    			        		$scope.Over12Confirm(parameter);
        		    			        	} 
    	    							});
	    	    					} else {
	    	    						//重大傷病判斷式
    		    			        	if($scope.SICK_TYPEforCheck == '2'){
    		    			        		if($scope.inputVO.SICK_TYPE == '1'){
    		    			        			$scope.showReasonDialog(3,$scope.inputVO.HEALTH_CHANGEList,function(data){
    		    			        				if(data.value == 'cancel'){
    		    			        					return;
    		    			        				}
    	        			        				$scope.inputVO.HEALTH_CHANGE = data.value.health_change;
    		    			        				$scope.Over12Confirm(parameter);	    							
    		    			        			});   			        		
    		    			        		}else{
    		    			        			$scope.Over12Confirm(parameter);
    		    			        		}
    		    			        	}else{
    		    			        		$scope.Over12Confirm(parameter);
    		    			        	} 
	    	    					}
								} else {									
									//重大傷病判斷式
		    			        	if($scope.SICK_TYPEforCheck == '2'){
		    			        		if($scope.inputVO.SICK_TYPE == '1'){
		    			        			$scope.showReasonDialog(3,$scope.inputVO.HEALTH_CHANGEList,function(data){
		    			        				if(data.value == 'cancel'){
		    			        					return;
		    			        				}
	        			        				$scope.inputVO.HEALTH_CHANGE = data.value.health_change;
		    			        				$scope.Over12Confirm(parameter);	    							
		    			        			});   			        		
		    			        		}else{
		    			        			$scope.Over12Confirm(parameter);
		    			        		}
		    			        	}else{
		    			        		$scope.Over12Confirm(parameter);
		    			        	} 
								}
							} else {
								//基本資料未異動
								$scope.checkQuestion_Risk(tota[0].body.Comparison.INVESTOREXAM_M);
							}
    		        	});
    				
    			});//chkAuth end
			}
			else{
				//基本資料欄位檢核錯誤訊息
				if($scope.inputVO.EDUCATION == '7'){
					//教育程度為其他時，教育程度需重新填寫
					$scope.showErrorMsg('ehl_02_KYC310_011');
				}else if($scope.inputVO.EDUCATION == ''){
					//無教育程度基本資料，請更新資料
					$scope.showErrorMsg('ehl_02_KYC310_011');
				}else if($scope.inputVO.CAREER == '' || $scope.inputVO.CAREER == '19'){
					//無職業基本資料(或為"19 其他")，請更新資料
					$scope.showErrorMsg('ehl_02_KYC310_012');
				}else if($scope.inputVO.MARRAGE == ''){
					//無婚姻基本資料，請更新資料
					$scope.showErrorMsg('ehl_02_KYC310_013');
				}else if($scope.inputVO.CHILD_NO == ''){
					//無子女人數基本資料，請更新資料
					$scope.showErrorMsg('ehl_02_KYC310_014');
				}else if($scope.inputVO.SICK_TYPE == ''){
					//無重大傷病基本資料，請更新資料
					$scope.showErrorMsg('ehl_02_KYC310_015');
				}
//				else if($scope.inputVO.DAY == null ||$scope.inputVO.DAY == ''){
//					//無日間電話資料，請更新資料
//					$scope.showErrorMsg('無日間電話資料，請更新資料');
//				}
				else{
					$scope.showErrorMsg('基本資料未填寫')
				}
			}
		}
		//法人
		else {
			$scope.inputVO.QUESTION_TYPE='03';
			$scope.inputVO.quest_list = $scope.quest_list;
			$scope.inputVO.chk_type = 'submit';
			
			if($scope.quest_list.length > 0){
				$scope.sendRecv("KYC310" , "chkAuth" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
					//確認答案計算分數
					var sum = 0;		//原始總分
					var sumC = 0;		//風險偏好總分
					var sumW = 0;		//風險承受能力總分
					var sumCW = 0;		//CRR總分_矩陣
					var intScore = 0;	//截距分
					var ansList = [];
					var q3SelCount = 0;	//Q3答案選項選了幾個
					
					//檢查未選取題目&算分
					for(var a = 0 ; a < $scope.quest_list.length ; a++){
						var ans2 = [];
						
						//截距分
						intScore =  $scope.quest_list[a].INT_SCORE;
						
						//第3題分數與複選題不同，只選最高分計算
						if($scope.quest_list[a].QST_NO == "3") {
							var Q3Score = -999;	//第3題分數
							debugger;
							for(var b = 0 ; b < $scope.quest_list[a].ANSWER_LIST.length ; b++){					
								if($scope.quest_list[a].ANSWER_LIST[b].select == true){
									ans2.push($scope.quest_list[a].ANSWER_LIST[b].ANSWER_SEQ);
									
									//這個答案選項分數比較高，第3題取這個選項的分數
									if($scope.quest_list[a].ANSWER_LIST[b].FRACTION > Q3Score) {
										Q3Score = $scope.quest_list[a].ANSWER_LIST[b].FRACTION;
									}
									
									//Q3每一種商品都要選年期
									q3SelCount++;
								}
								
								//檢查是否每個問題都有回答
								if(b == $scope.quest_list[a].ANSWER_LIST.length - 1){									
									if(q3SelCount < $scope.inputVO.Q3_PROD_TYPEList.length) {
										return $scope.showMsg("Q3每一種商品都必需選年期");
									}
								}
							}
							
							//分數加上Q3最高分選項分數
							sum += Q3Score;
							if($scope.quest_list[a].SCORE_TYPE == "C") sumC += Q3Score
							if($scope.quest_list[a].SCORE_TYPE == "W") sumW += Q3Score;
//							alert("Q3Score: " + Q3Score);
						} else {	//非第3題
							for(var b = 0 ; b < $scope.quest_list[a].ANSWER_LIST.length ; b++) {
								if($scope.quest_list[a].ANSWER_LIST[b].select == true){
									ans2.push($scope.quest_list[a].ANSWER_LIST[b].ANSWER_SEQ);
									sum += $scope.quest_list[a].ANSWER_LIST[b].FRACTION;
									
									if($scope.quest_list[a].SCORE_TYPE == "C") sumC += $scope.quest_list[a].ANSWER_LIST[b].FRACTION;
									if($scope.quest_list[a].SCORE_TYPE == "W") sumW += $scope.quest_list[a].ANSWER_LIST[b].FRACTION;
								}
								
								//檢查是否每個問題都有回答
								if(b == $scope.quest_list[a].ANSWER_LIST.length - 1){
									if(ans2.length <= 0){
										return $scope.showMsg("Q"+(a+1)+"未選取答案");
									}
								}
							}
						}
						
						ansList.push(ans2);
					}
					
//					alert("Sum: " + sum + "\n sumC: " + sumC + "\n sumW: " + sumW);
					
					//比對風險值
					sum += intScore;	//加上截距分
					var risk_level = '';					
					var max_risk = $scope.quest_list[0].RISK_LIST.length-1;
					
					//原始總分
					if(sum<=0){
						$scope.inputVO.SCORE_ORI_TOT = 0;
					}
					else{
						$scope.inputVO.SCORE_ORI_TOT = sum;
					}
					
					$scope.inputVO.ANSWER_2 = ansList;
					
					if($scope.quest_list[0].RISK_LIST.length > 1){
						if(sum <= $scope.quest_list[0].RISK_LIST[0].RL_UP_RATE){
							risk_level = $scope.quest_list[0].RISK_LIST[0].CUST_RL_ID;
						} else {
							for(var c = 0;c<$scope.quest_list[0].RISK_LIST.length;c++){
								for(var d = c + 1 ; d > c ; d--) {
									if(d<$scope.quest_list[0].RISK_LIST.length) {
    									if(sum>$scope.quest_list[0].RISK_LIST[c].RL_UP_RATE && sum <= $scope.quest_list[0].RISK_LIST[d].RL_UP_RATE) {
    										risk_level = $scope.quest_list[0].RISK_LIST[d].CUST_RL_ID;
    									} else if(sum>$scope.quest_list[0].RISK_LIST[max_risk].RL_UP_RATE || sum == $scope.quest_list[0].RISK_LIST[max_risk].RL_UP_RATE) {
    										risk_level = $scope.quest_list[0].RISK_LIST[max_risk].CUST_RL_ID;
    									}
									}
								}
							}
						}
					}
					else{
						risk_level = $scope.quest_list[0].RISK_LIST[0].CUST_RL_ID;
					}
					
					//原始總分取得的風險屬性
					$scope.inputVO.CRR_ORI = risk_level;
					$scope.inputVO.RISKRANGE = $scope.inputVO.SCORE_ORI_TOT;
					
					//CW矩陣計算
					sumCW = sumC + sumW + intScore;	//總分 = 風險偏好總分 + 風險承受能力加總 + 截距分
					$scope.inputVO.SCORE_C = sumC;
					$scope.inputVO.SCORE_W = sumW;
					$scope.inputVO.SCORE_CW_TOT = sumCW;
					$scope.inputVO.RISK_LOSS_RATE = ($scope.inputVO.SCORE_ORI_TOT + 62) / 1000 * 100; //#0579
					if($scope.inputVO.RISK_LOSS_RATE <= 0) $scope.inputVO.RISK_LOSS_RATE = 0;
					
					$scope.inputVO.RS_VERSION = $scope.quest_list[0].RS_VERSION;	//降等機制矩陣版號
					$scope.inputVO.RLR_VERSION = $scope.quest_list[0].RLR_VERSION;	//可承受風險損失率版號
					$scope.sendRecv("KYC310" , "getCRRMatrix" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
						if(tota[0].body.CRR_MATRIX){
							$scope.inputVO.CRR_MATRIX = tota[0].body.CRR_MATRIX;	//風險級距矩陣C值
						}
						if(tota[0].body.RISK_LOSS_LEVEL){
							$scope.inputVO.RISK_LOSS_LEVEL = tota[0].body.RISK_LOSS_LEVEL;	//可承受風險損失率對應C值
						}
						
						//Y: 總分與CW都要算，取低者為風險屬性及分數
						if($scope.inputVO.CRR_RULE == "Y") {
							if($scope.inputVO.CRR_MATRIX.substring(1,2) < $scope.inputVO.CRR_ORI.substring(1,2)) {
								risk_level = $scope.inputVO.CRR_MATRIX;
								$scope.inputVO.RISKRANGE = $scope.inputVO.SCORE_CW_TOT;
							} else {
								risk_level = $scope.inputVO.CRR_ORI;
								$scope.inputVO.RISKRANGE = $scope.inputVO.SCORE_ORI_TOT;
							}
						}
						
						//C值
						$scope.inputVO.CUST_RISK_AFR = risk_level;
			        	
						var Q3ProdExpChg = undefined;
		        		var Q3ProdDecrease = undefined;
		        		
		        		$scope.sendRecv("KYC310", "getLastResult", $scope.kyc310inputvo, $scope.inputVO, function(tota,isError)
				        {
		        			var INVESTOREXAM_M = null;
							if(tota[0].body.Comparison != null) {
								INVESTOREXAM_M = tota[0].body.Comparison.INVESTOREXAM_M;
							}
	    		        	if(INVESTOREXAM_M != undefined && INVESTOREXAM_M != null){
		        				$scope.inputVO.TEST_BEF_DATE = INVESTOREXAM_M.TEST_BEF_DATE;
		        				$scope.inputVO.CUST_RISK_BEF = INVESTOREXAM_M.CUST_RISK_BEF;
							}
	        				$scope.inputVO.EXAM_VERSION = $scope.quest_list[0].EXAM_VERSION
	        				
	        				//與前次承作Q3 or Q8&Q9答案比對
	            			if(tota[0].body.Q3ProdExpChgList != null && tota[0].body.Q3ProdExpChgList.length > 0){	//Q3投資年期上升2級以上
	            				Q3ProdExpChg = "";
	            				for(var i = 0 ; i < tota[0].body.Q3ProdExpChgList.length ; i++) {
	            					Q3ProdExpChg += $filter('i18n')('ehl_01_KYC310_015',
	            							[tota[0].body.Q3ProdExpChgList[i].ProdType, tota[0].body.Q3ProdExpChgList[i].PreProdExp, tota[0].body.Q3ProdExpChgList[i].CurProdExp]) + "<br/>";
	            				}
	    		        	}
	            			
	    		        	if(tota[0].body.Q3ProdDecrease){	//Q3投資商品上次有本次沒有
	    		        		Q3ProdDecrease = $filter('i18n')('ehl_01_KYC310_016',[tota[0].body.Q3ProdDecrease]);
	    		        	}
	    		        	
	    		        	if(Q3ProdExpChg != undefined || Q3ProdDecrease != undefined || EMAIL!= undefined) {
	    			        	//將異動結果傳入提醒視窗
    							var parameter = {
                    					text1:Q3ProdExpChg,
                    					text2:Q3ProdDecrease,
                    					text16:EMAIL,
	                					text17:EMAIL2
        	    				};
    							$scope.CorpConfirm(parameter);	    							
    			        	} else {
    			        		$scope.submitData();	
    			        	}
		    			});
	    				
					});
    			});
			}
			else{
				$scope.showErrorMsg("無題目答案");
			}//確認$scope.quest_list end
		}//驗證法人or自然人 end

    }
    
    //基本資料欄位檢核
    $scope.check_person_data = function(){
		if(	$scope.inputVO.cust_name == '' ||//檢核姓名 
			$scope.inputVO.cust_id == '' ||//身分證字號
			$scope.inputVO.birthday == undefined ||//出生年月日
			$scope.inputVO.EDUCATION == '' ||//教育程度
			$scope.inputVO.EDUCATION == '7' ||//教育程度
			$scope.inputVO.CAREER == '' || $scope.inputVO.CAREER == '19' ||//職業(未填或為"19 其他")
			$scope.inputVO.MARRAGE == '' ||//婚姻
			$scope.inputVO.CHILD_NO == '' ||//子女數
			$scope.inputVO.SICK_TYPE == '' ||//重大傷病
			$scope.inputVO.Y_N_update == '' ||//是否更新資料
			//檢核性別
			($scope.inputVO.gender_M == false && $scope.inputVO.gender_W == false) ||
			($scope.inputVO.gender_M == false && $scope.inputVO.gender_W == undefined) ||
			($scope.inputVO.gender_M == undefined && $scope.inputVO.gender_W == false) ||
			($scope.inputVO.gender_M == undefined && $scope.inputVO.gender_W == undefined) 
//			||
			//電話-日
//			($scope.inputVO.DAY_TYPE == true && $scope.inputVO.DAY == '') ||
//			($scope.inputVO.DAY == null)
			//電話-夜
//			($scope.inputVO.NIGHT_TYPE == true && $scope.inputVO.NIGHT == undefined) || 
//			($scope.inputVO.NIGHT_TYPE == true && $scope.inputVO.NIGHT == '') || 
			//行動電話
//			($scope.inputVO.TEL_NO_TYPE == true && $scope.inputVO.TEL_NO == undefined) || 
//			($scope.inputVO.TEL_NO_TYPE == true && $scope.inputVO.TEL_NO == '') ||
			//傳真
//			($scope.inputVO.FAX_TYPE == true && $scope.inputVO.FAX == undefined) 
//			|| ($scope.inputVO.FAX_TYPE == true && $scope.inputVO.FAX == '')
		){
			console.log($scope.inputVO.DAY)
			return false;
		}else{
			console.log($scope.inputVO.DAY)
			return true;
		}
    }
   
    //系統自動控管邏輯，執行自動降(一般客戶)
    $scope.declineLevel = function(age, sicktype, eduction, cValue, degrade){
    	debugger
    	var isC2 = false;
    	cValue 	 = cValue 	== null ? '' : cValue;//原C值
    	eduction = eduction == null ? '' : eduction;//學歷
    	age 	 = age 		== null ? 0  : age;//年齡
    	
    	//判斷是否可以承作kyc
    	if($scope.chkCanKyc(age , sicktype , eduction)){
			return undefined;
		}
    	 
    	//18歲以下，只有國中、小學學歷
    	//沒有法代：最高C2 (不管有沒有重大傷病)
    	//有法代：
    	//	1.沒有重大傷病，取法代孰低者C值
    	//  2.有重大傷病：法代孰低者為C1，則C1，否則最高C2
		if(age < 18 && new RegExp("[6]").test(eduction)){
			if($scope.inputVO.legalRegKycLevel != undefined && $scope.inputVO.legalRegKycLevel != null && $scope.inputVO.legalRegKycLevel != "") {
				//若有法代風險屬性，則以法代為上限
				var kVal = $scope.inputVO.legalRegKycLevel.substring(1,2) <= cValue.substring(1,2) ? $scope.inputVO.legalRegKycLevel : cValue;
				if(sicktype == "2") { //有全民健康保險重大傷病證明，但不影響投資風險理解
					//若有法代風險屬性且有重大傷病，則以法代為上限但最高C2
					return new RegExp("C[2,3,4]").test(kVal) ? $scope.RISKList[1].CUST_RL_ID : kVal;
				} else {
					//若有法代風險屬性且沒有重大傷病，則以法代為上限
					return kVal;
				}
			} else {
				//沒有法代KYC資料(法代沒有KYC或已過期)，最高C2
				return new RegExp("C[2,3,4]").test(cValue) ? $scope.RISKList[1].CUST_RL_ID : cValue;
			}
		}
    	
    	//弱勢客戶
    	if($scope.chkNoSpecialSigningReduceLevel(age , sicktype , eduction)) {
    		if(degrade == "Y" && $scope.degrade_end == 'N') {
    			//有免降等註記且未到期 ==> 不降等；若C值為C3 or C4，則KYC到期日=免降等到期日
    			if(cValue == "C3" || cValue == "C4"){
    				$scope.inputVO.EXPIRY_DATE = $scope.degrade_date;
    				
    				//客戶為非專投且為弱勢客戶且具免降等註記且為C3~C4
    				//表單列印：您本次風險屬性評估結果到期日：____
    				$scope.inputVO.PDF_KYC_FLAG = 'Y';
    			}
    			
    			return cValue;
    		} else {
    			//沒有免降等註記 ==> 最高C2
    			return new RegExp("C[2,3,4]").test(cValue) ? $scope.RISKList[1].CUST_RL_ID : cValue;
    		}
    	} else {
    		//非弱勢客戶不降等
    		return cValue;
    	}
    }
    
    //系統自動控管邏輯，執行自動降(專業客戶/專業投資人/高資產客戶)
    $scope.TX_declineLevel = function(age , sicktype , eduction , cValue , degrade, txFlag){
    	debugger
    	cValue 	 = cValue 	== null ? '' : cValue;//原C值
    	eduction = eduction == null ? '' : eduction;//學歷
    	age 	 = age 		== null ? 0  : age;//年齡
		
    	//判斷是否可以承作kyc
    	if($scope.chkCanKyc(age , sicktype , eduction)){
			return undefined;
		}
		
    	//18歲以下，只有國中、小學學歷
    	//沒有法代：最高C2 (不管有沒有重大傷病)
    	//有法代：
    	//	1.沒有重大傷病，取法代孰低者C值
    	//  2.有重大傷病：法代孰低者為C1，則C1，否則最高C2
		if(age < 18 && new RegExp("[6]").test(eduction)){
			if($scope.inputVO.legalRegKycLevel != undefined && $scope.inputVO.legalRegKycLevel != null && $scope.inputVO.legalRegKycLevel != "") {
				//若有法代風險屬性，則以法代為上限
				var kVal = $scope.inputVO.legalRegKycLevel.substring(1,2) <= cValue.substring(1,2) ? $scope.inputVO.legalRegKycLevel : cValue;
				if(sicktype == "2") { //有全民健康保險重大傷病證明，但不影響投資風險理解
					//若有法代風險屬性且有重大傷病，則以法代為上限但最高C2
					return new RegExp("C[2,3,4]").test(kVal) ? $scope.RISKList[1].CUST_RL_ID : kVal;
				} else {
					//若有法代風險屬性且沒有重大傷病，則以法代為上限
					return kVal;
				}
			} else {
				//沒有法代KYC資料(法代沒有KYC或已過期)，最高C2
				return new RegExp("C[2,3,4]").test(cValue) ? $scope.RISKList[1].CUST_RL_ID : cValue;
			}
		}
		
    	//非高資產客戶且為弱勢客戶且非65~70歲非弱勢專投
    	if($scope.inputVO.HNWC_VALID_YN != "Y" && //非高資產客戶
    			!(txFlag == "Y" && age >= 65 && age < 70 && !$scope.chkNoSpecialSigningReduceLevel(40 , sicktype , eduction)) && //非65~70歲非弱勢專投
    			$scope.chkNoSpecialSigningReduceLevel(age , sicktype , eduction)) { //弱勢客戶
    		if(degrade == "Y" && $scope.degrade_end == 'N') {
    			//有免降等註記且未到期 ==> 不降等
    			if(cValue == "C4"){
    				//若C值為C4，則KYC到期日=免降等到期日與評估日+1年-1日取孰近
    				var today = new Date();//取當日日期
    				var tDate = new Date();//取當日日期
    				tDate.setFullYear(today.getFullYear() + 1);//評估日+1年
    				tDate.setDate(today.getDate() - 1);//評估日+1年-1日
    				var eDate = $filter('date')(tDate,'yyyy-MM-dd 00:00:00');
    				if($scope.degrade_date < $scope.toJsDate(eDate)) {
    					$scope.inputVO.EXPIRY_DATE = $scope.degrade_date;
    				}
    				
    				//客戶為專投且為弱勢客戶且具免降等註記且為C4
    				//表單列印：“您已申請免適用客戶風險屬性評估結果控管機制，該機制所定風險屬性等級期限至您當時專業投資人資格有效為止，故本次風險屬性評估結果到期日為：________”
    				$scope.inputVO.PDF_KYC_FLAG = 'YTX';
    			}
    			
    			return cValue;
    		} else {
    			//沒有免降等註記 ==> 最高C3
    			return new RegExp("C[3,4]").test(cValue) ? $scope.RISKList[2].CUST_RL_ID : cValue;
    		}
    	} else {
    		//非弱勢客戶不降等
    		//高資產客戶不降等
    		//65~70歲非弱勢專投不降等
    		return cValue;
    	}
    }
    
    //判斷是否可以承作KYC
    $scope.chkCanKyc = function(age , sicktype , eduction){
    	var isCanNotKyc = false;
    	
       	//有全民健康保險重大傷病證明，而且會影響本人對投資商品及其風險之理解(為維護您的權益，以下第二部分的風險屬性評估將無法填答)
    	//年齡18歲(含)以上，不識字
    	if(isCanNotKyc = sicktype == '3' || ( age >= 18 && (  eduction == '8') )){
    		$scope.showMsg('ehl_01_KYC310_006');
    	}
    	
    	return isCanNotKyc;
    }
    
    //判斷是否3中1
    $scope.chkNoSpecialSigningReduceLevel = function(age , sicktype , eduction){
    	var isReduceLevel = false;
    	
    	//年齡65歲(含)以上
    	isReduceLevel = age >= 65;
    	//年齡18歲(含)以上，教育程度圍國中(含以下)
    	isReduceLevel = isReduceLevel || ( age >= 18 && new RegExp("[6]").test(eduction) );
    	//有全民健康保險重大傷病證明，但不影響投資風險理解
    	isReduceLevel = isReduceLevel || sicktype == '2';
    	
    	return isReduceLevel;
    }
    
    //送出
    $scope.submitData = function(){
		debugger;
    	$scope.inputVO.KYC_TEST_DATE = new Date();
    	//法人不檢核電話號碼，自然人因為可以修改基本資料所以需要檢查
    	if($scope.inputVO.CUST_ID.length <= 10 || $scope.checkTEL()){
    		$scope.inputVO.degrade = $scope.degrade;
    		//add by Brian
    		$scope.basic_information = angular.copy($scope.inputVO)
    		$scope.inputVO.basic_information = $scope.basic_information;
    		//檢核是否需要填寫差異說明表
    		$scope.sendRecv("KYC310" , "getLastKYCComparisonData" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError) {
    			debugger
    			if (!isError) {
    				$scope.inputVO.NEED_COMPARISON_YN = "N";
            		if(tota[0].body.lastKYCComparisonData && tota[0].body.lastKYCComparisonData.NEED_COMPARISON_YN == "Y") { //需填寫差異表
    					$scope.inputVO.NEED_COMPARISON_YN = tota[0].body.lastKYCComparisonData.NEED_COMPARISON_YN; //是否需填寫差異表問卷
    					$scope.inputVO.COMP_QUES = tota[0].body.lastKYCComparisonData.COMP_QUES; //差異表問卷內容
    					$scope.inputVO.LAST_SEQ = tota[0].body.lastKYCComparisonData.LAST_SEQ; //比較差異的(前次)客戶風險評估問卷主鍵
    					$scope.inputVO.LAST_ANSWER_2 = tota[0].body.lastKYCComparisonData.LAST_ANSWER_2; //比較差異的(前次)問卷答案選項
    					
            			var inputVO = $scope.inputVO;
            			var dialog = ngDialog.open({
            				template: 'assets/txn/KYC310/KYC310_COMPARISON.html',
            				className: 'KYC310_COMPARISON',
            				controller:['$scope',function($scope){
            					$scope.inputVO = inputVO;
            				}]
            			});
            			dialog.closePromise.then(function(data){
            				debugger
            				if(data.value != undefined && data.value != 'cancel') {
            					//差異表填答答案內容
            					$scope.inputVO.ANSWER_COMP = data.value;
            					//存檔
            					$scope.doSubmit(); 
            				}
            			});
            		} else {
            			//不需填寫差異表，直接存檔
            			$scope.doSubmit(); 
            		}
            	}
    		});
    	} else {
    		$scope.showErrorMsg('行動必須09開頭');
    	}
    }
   
    $scope.doSubmit = function() {
    	$scope.sendRecv("KYC310" , "submit" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
        	if (isError) {
        		$scope.showErrorMsg(tota[0].body.msgData);
        	}
        	
        	if (tota.length > 0) {
        		if($scope.inputVO.gender_M==true){
        			$scope.inputVO.GENDER ='男';
				}
        		if($scope.inputVO.gender_W==true){
					$scope.inputVO.GENDER ='女'
				}
        		$scope.inputVO.EXPIRY_DATE = tota[0].body.EXPIRY_DATE;
        		$scope.inputVO.seq = tota[0].body.seq;
        		$scope.showSuccessMsg('ehl_01_common_001');
        		$scope.connector('set','KYC310Question',$scope.quest_list);
        		$scope.connector('set','KYC310', $scope.inputVO);
        		$rootScope.menuItemInfo.url = "assets/txn/KYC311/KYC311.html";
        	};
		});
    }
    
    $scope.dobouledo = function(){
    	$scope.inputVO.seq = $scope.persional[0].SEQ;
    	$scope.inputVO.CUST_RISK_AFR = $scope.persional[0].CUST_RISK_AFR;
    	$scope.inputVO.EXAM_VERSION = $scope.persional[0].EXAM_VERSION;
    	$scope.inputVO.EXPIRY_DATE = $scope.persional[0].EXPIRY_DATE;
    	$scope.inputVO.NEED_COMPARISON_YN = $scope.persional[0].NEED_COMPARISON_YN;
		$scope.connector('set','KYC310Question',$scope.quest_list);
		$scope.connector('set','KYC310', $scope.inputVO);
		$rootScope.menuItemInfo.url = "assets/txn/KYC311/KYC311.html";
    }
    
    $scope.ansClear = function(ansList,row){
    	//清除前項選擇項目
    	for(var a=0;a<ansList.length;a++){
    		if(ansList[a].ANSWER_SEQ != row.ANSWER_SEQ){
    			ansList[a].select=undefined;
    		}
    	}
    }
    
    //確認教育程度
    $scope.checkEDUCTION = function(){
    	//18歲不識字
    	if($scope.inputVO.EDUCATION == 8){
    		$scope.showErrorSicktypeEduction('ehl_01_KYC310_010' , function (data) {
                if(data.value === 'cancel'){
                	$scope.inputVO.EDUCATION = '';
                }
            });
    		
    		return false;
    	}
    	
    	return true;
    }
    
    //確認領用全民健康保險重大傷病證明
    $scope.checksicktype = function(){
    	//有重大傷病，且影響判斷
    	if($scope.inputVO.SICK_TYPE == 3){
    	   $scope.showErrorSicktypeEduction('ehl_01_KYC310_018' , function (data) {
    		   if(data.value === 'cancel'){
    	        	$scope.inputVO.SICK_TYPE = undefined;
    	       }
    	   });
    	   
    	   return false;
    	}
    	
    	return true;
    }
    
    //學歷會跳出的錯誤訊息視窗
    $scope.showErrorSicktypeEduction = function(type , excuteFunc){
    	var txtMsg = $filter('i18n')(type);
    	var txtMsg1 = undefined;
    	var txtMsg2 = undefined;
    	var txtMsg3 = undefined;
    	var txtMsg4 = undefined;
    	var txtMsg5 = undefined;
    	var txtMsg6 = undefined;
    	
    	if(type=='ehl_01_KYC310_006'){
    		txtMsg1 = '註：特定狀況係指';
    		txtMsg2 = '(一)年齡80歲(含)以上之非專業投資人';
    		txtMsg3 = '(二)同時符合下列兩項以上之非專業投資人';
    		txtMsg4 = '1.年齡70歲(含)~80歲';
    		txtMsg5 = '2.年齡18歲(含)以上，教育程度為國中(含)以下';
    		txtMsg6 = '3.領有全民健康保險的重大傷病證明，但不影響對投資商品及其風險的理解';
    	}
    	
		var dialog = ngDialog.open({
			template:	'assets/txn/KYC310/KYC310_EDIT.html',
			className:	'KYC310_DETAIL',
			showClose:	false,
			controller:	['$scope',function($scope){
				$scope.data = {
						text:txtMsg,
						text1:txtMsg1,
						text2:txtMsg2,
						text3:txtMsg3,
						text4:txtMsg4,
						text5:txtMsg5,
						text6:txtMsg6
				}
			}]
		});
		
        dialog.closePromise.then(excuteFunc);
    }
    
    //add by Brian
    //確定原因的提醒視窗
    $scope.showReasonDialog = function(idx,data,executeFunc){
    	debugger;
    	var sameEmailDataList = $scope.sameEmailData;
		var dialog = ngDialog.open({
			template:	'assets/txn/KYC310/KYC310_REASON.html',
			className:	'KYC310_REASON',
			showClose:	false,
			controller:	['$scope',function($scope){
				$scope.data = {
					index:idx , 
					options:data,
					sameEmailData: sameEmailDataList				               
				}
			}]
		});
		
        dialog.closePromise.then(executeFunc);
    	
    }
    
    //add by Brian
    //12歲以下基本資料異動確認，如和前項經驗不符，則顯示確認訊息   
    $scope.Under12ConfirmWithMsg = function(data){
     	$confirm({
//    		title:"提醒視窗",
//    		text:txtMsg,
//    		text1:EDUCTION_12,
//			text2:CAREER_12,
//			text3:MARRIAGE_12,
//			text4:CHILD_NO_12,
//			text5:INCOME_12,
//			text6:EDUCTION,
//			text7:CAREER,
//			text8:MARRIAGE,
//			text9:CHILDREN,
//			text10:HEALTH,
//			text11:QEight,
//			text12:QNine,
//			ok: "確定送出",
//          cancel: "修改資料"
     		title:"提醒視窗",
            text:data.text,
        	text1:data.text1,
			text2:data.text2,
			text3:data.text3,
			text4:data.text4,
			text5:data.text5,
			text6:data.text6,
			text7:data.text7,
			text8:data.text8,
			text9:data.text9,
			text10:data.text10,
			text11:data.text11,
			text12:data.text12,
			text14:data.text14,
			text15:data.text15,
			ok: "確定送出",
			cancel: "修改資料"
    	})
    	.then(function(){
//			$scope.checkQuestion_Risk(tota[0].body.Comparison.INVESTOREXAM_M);
			$scope.checkQuestion_Risk(data.INVESTOREXAM_M);	
    	});
    }
    
    //add by Brian
    //非12歲基本資料異動確認，如和前項經驗不符，則顯示確認訊息   
    $scope.Over12ConfirmWithMsg = function(data){
    	//非12歲以下且基本資料異動
		$confirm({
//			title:"提醒視窗",
//			text:txtMsg,
//			text1:EDUCTION,
//			text2:CAREER,
//			text3:MARRIAGE,
//			text4:CHILDREN,
//			text5:HEALTH,
//			text6:QEight,
//			text7:QNine,
//			ok: "確定送出",
//          cancel: "修改資料"
            title:"提醒視窗",
            text:data.text,
            text1:data.text1,
            text2:data.text2,
            text3:data.text3,
            text4:data.text4,
            text5:data.text5,
            text6:data.text6,
            text7:data.text7,
            text8:data.text8,
            text9:data.text9,
            text16:data.text16,
			text17:data.text17,
            ok: "確定送出",
            cancel: "修改資料"
		})
		.then(function(){
//			$scope.checkQuestion_Risk(tota[0].body.Comparison.INVESTOREXAM_M);
			$scope.checkQuestion_Risk(data.INVESTOREXAM_M);	
		});	
    }
    
    //add by Brian
    //12歲以下基本資料異動確認
    $scope.Under12Confirm = function(data){
    	$confirm({
//    		title:"提醒視窗",
//    		text:EDUCTION_12,
//			text1:CAREER_12,
//			text2:MARRIAGE_12,
//			text3:CHILD_NO_12,
//			text4:INCOME_12,
//			text5:EDUCTION,
//			text6:CAREER,
//			text7:MARRIAGE,
//			text8:CHILDREN,
//			text9:HEALTH,
//			text10:QEight,
//			text11:QNine,
//			ok: "確定送出",
//          cancel: "修改資料"
            title:"提醒視窗",
            text:data.text,
            text1:data.text1,
            text2:data.text2,
            text3:data.text3,
            text4:data.text4,
            text5:data.text5,
            text6:data.text6,
            text7:data.text7,
            text8:data.text8,
            text9:data.text9,
            text10:data.text10,
            text11:data.text11,
            text14:data.text14,
            text15:data.text15,
            text16:data.text16,
			text17:data.text17,
            ok: "確定送出",
            cancel: "修改資料"
    	})
    	.then(function(){
//			$scope.checkQuestion_Risk(tota[0].body.Comparison.INVESTOREXAM_M);
			$scope.checkQuestion_Risk(data.INVESTOREXAM_M);	
    	});  	
    }
    
    //add by Brian
    //非12歲基本資料異動確認
    $scope.Over12Confirm = function(data){
    	debugger;
    	//非12歲以下且基本資料異動
		$confirm({
//			title:"提醒視窗",
//			text:EDUCTION,
//			text1:CAREER,
//			text2:MARRIAGE,
//			text3:CHILDREN,
//			text4:HEALTH,
//			text5:QEight,
//			text6:QNine,
//			ok: "確定送出",
//          cancel: "修改資料"
            title:"提醒視窗",
            text:data.text,
            text1:data.text1,
            text2:data.text2,
            text3:data.text3,
            text4:data.text4,
            text5:data.text5,
            text6:data.text6,
            text7:data.text7,
            text8:data.text8,
            text9:data.text16,
			text10:data.text17,
			text11:data.text11,
            ok: "確定送出",
            cancel: "修改資料"
		})
		.then(function(){
//			$scope.checkQuestion_Risk(tota[0].body.Comparison.INVESTOREXAM_M);
			$scope.checkQuestion_Risk(data.INVESTOREXAM_M);	
		});   	
    }
    
    //法人資料異動確認
    $scope.CorpConfirm = function(data){
    	debugger;
    	$confirm({
            title:"提醒視窗",
            text:data.text,
            text1:data.text1,
            text2:data.text2,
            text3:data.text16,
			text4:data.text17,
            ok: "確定送出",
            cancel: "修改資料"
		})
		.then(function(){
			$scope.submitData();	
		});   	
    }
                 
    //電話choicbox勾選變動
    $scope.telephoneChange = function(type,name){
    	if(!type){
    		switch (name) {
    		case 'DAY':
    			$scope.inputVO.DAY = $scope.persional[0].DAY;
				$scope.inputVO.DAY_COD = $scope.persional[0].DAYCOD;
    			break;
    		case 'NIGHT':
    			$scope.inputVO.NIGHT = $scope.persional[0].NIGHT;
				$scope.inputVO.NIGHT_COD = $scope.persional[0].NIGHTCOD;
    			break;
    		case 'TEL_NO':
    			$scope.inputVO.TEL_NO = $scope.persional[0].TEL_NO;
    			break;
    		case 'FAX':
    			$scope.inputVO.FAX = $scope.persional[0].FAX;
    			break;
    		default:
    			break;
    		}
    	}
    	
    }
        
	$scope.chkPhone = function(val){
		$scope.inputVO[val] = $scope.inputVO[val].replace(/[^\d,\#]/, '');
	}
	
	//第3題特殊處理
	//同一商品只能選一投資經驗
	$scope.Q3AnsSelected = function(ansList, rowIdx, colIdx) {
		//清除前項選擇項目
    	for(var a=0; a<ansList.length; a++){
    		if(ansList[a].ANSWER_SEQ.toString().substring(0,1) == rowIdx){
    			for(var j=0; j<$scope.inputVO.Q3_PROD_EXPList.length; j++) {
    				if(ansList[a].ANSWER_SEQ.toString().substring(1,2) != colIdx) {
    					ansList[a].select = undefined;
    				}
    			}
    		}
    	}
	}
	
	//Q11特殊處理背景顏色與字型顏色
	$scope.setQ11Style = function(idx) {
		switch(idx) {
			case 0:
				return {'background-color': '#D0D0D0', 'color': 'black'};
			case 1:
				return {'background-color': '#DEE8EF', 'color': 'black'};
			case 2:
				return {'background-color': '#B0C4DE', 'color': 'black'};
			case 3:
				return {'background-color': '#5686BF', 'color': 'white'};
			case 4:
				return {'background-color': '#00477D', 'color': 'white'};
		}
	}
	
	//半形轉全形
	$scope.ChangeToFullWidth = function (text) { 
		debugger;
		var temp = ""; 
		for (var i = 0; i < text.length; i++ ) { 
			 var charCode = text.charCodeAt(i);
			if (charCode == 32) { 
				charCode = 12288; 
				} 
			if (charCode > 32 && charCode < 127) { 
				charCode += 65248;  
				} 
			temp = temp + String.fromCharCode(charCode);
			} 
		debugger;
		$scope.inputVO.CUST_ADDR_1 = temp; 
	}

	$scope.checkEmail = () => {
		// ^\w+：@ 之前必須以一個以上的文字&數字開頭，例如 abc
		// ((-\w+)：@ 之前可以出現 1 個以上的文字、數字或「-」的組合，例如 -abc-
		// (\.\w+))：@ 之前可以出現 1 個以上的文字、數字或「.」的組合，例如 .abc.
		// ((-\w+)|(\.\w+))*：以上兩個規則以 or 的關係出現，並且出現 0 次以上 (所以不能 –. 同時出現)
		// @：中間一定要出現一個 @
		// [A-Za-z0-9]+：@ 之後出現 1 個以上的大小寫英文及數字的組合
		// (\.|-)：@ 之後只能出現「.」或是「-」，但這兩個字元不能連續時出現
		// ((\.|-)[A-Za-z0-9]+)*：@ 之後出現 0 個以上的「.」或是「-」配上大小寫英文及數字的組合
		// \.[A-Za-z]+$/：@ 之後出現 1 個以上的「.」配上大小寫英文及數字的組合，結尾需為大小寫英文
    	let emailRule = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z]+$/;
    	if (!emailRule.test($scope.inputVO.EMAIL_ADDR)) {
    		if ($scope.inputVO.EMAIL_ADDR) { // 有輸入 email 再顯示錯誤訊息
    			$scope.showErrorMsg('Email 格式錯誤：${$scope.inputVO.EMAIL_ADDR}'); 			
    		} else if ($scope.inputVO.CUST_EMAIL_BEFORE && ($scope.inputVO.EMAIL_ADDR == undefined || $scope.inputVO.EMAIL_ADDR == null || $scope.inputVO.EMAIL_ADDR == '' )) {
        		$scope.showErrorMsg('ehl_01_KYC310_049');      		
    		}   	
    		$scope.inputVO.EMAIL_ADDR = $scope.inputVO.CUST_EMAIL_BEFORE;
    		return;
		}
    	
		$scope.sendRecv("KYC310" , "checkAllBranchEmail" , $scope.kyc310inputvo , $scope.inputVO , function(tota,isError){
			
			$scope.haveSameEmail = tota[0].body.haveSameEmail;

			if($scope.haveSameEmail){
				debugger;
	            $scope.sameEmailData = tota[0].body.sameEmailData;
		      $scope.showReasonDialog(7,$scope.inputVO.EMAIL_CHANGEList,function(data){
		    	  if(data.value == 'cancel'){
		    		  $scope.showMsg("信箱重覆理由並未按下確定按鈕，請重新輸入信箱。");
		    		  $scope.inputVO.EMAIL_ADDR = $scope.inputVO.CUST_EMAIL_BEFORE;
		    		  return
  				}
		      $scope.inputVO.SAMEEMAIL_CHOOSE = data.value.sameEmail_choose;
	          $scope.inputVO.SAMEEMAIL_REASON = data.value.sameEmail_reason;
		    });
			}
            
		});    	
	}
	$scope.checkPSR = function(){
    	//#0437  若有填過衍生性金融商品客戶適性評估問卷先顯示提醒視窗
    	if($scope.inputVO.isPSR){
    	    $scope.showReasonDialog(5,null,function(data){
    	    	 $scope.btnSubmit();
    	    });
    	}else{
    		 $scope.btnSubmit();
    	}
	}
	
	
});