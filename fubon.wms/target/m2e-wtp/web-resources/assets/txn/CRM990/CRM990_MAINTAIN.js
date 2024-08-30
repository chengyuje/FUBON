/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM990_MAINTAINController',
		function($rootScope, $scope, $controller, $confirm, $timeout, $filter, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$controller('RegionController', {$scope: $scope});
	
	$scope.controllerName = "CRM990_MAINTAINController";
	
	// filter
	getParameter.XML(["KYC.CAREER","CRM.COMPLAIN_SOURCE","CRM.NEW.COMPLAIN_SOURCE"], function(totas) {
		if (totas) {
			if($scope.newComplainSource){
				$scope.mappingSet['COMPLAIN_SOURCE'] = totas.data[totas.key.indexOf('CRM.NEW.COMPLAIN_SOURCE')];
			}else{
				$scope.mappingSet['COMPLAIN_SOURCE'] = totas.data[totas.key.indexOf('CRM.COMPLAIN_SOURCE')];
			}
			$scope.CAREER = totas.data[totas.key.indexOf('KYC.CAREER')];
		}
	});
	
	$scope.sendRecv("CRM990", "getPriID", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {},
		function(tota, isError) {
			if (!isError) {
				$scope.priID = tota[0].body.privilege_id;
			}
	});
	
	$scope.loginID = sysInfoService.getUserID();
	$scope.role = sysInfoService.getPriID();
	
	// date picker
	$scope.happen_dateOptions = {
		maxDate: new Date()
	};
	
//	$scope.end_dateOptions = {
//		minDate: new Date()
//	};
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	//變更分行別時觸動
	$scope.changeBranch = function() {
		if($scope.is806 && $scope.inputVO.grade != undefined && $scope.inputVO.grade != ''){
			if($scope.inputVO.branch_nbr != '806'){
				$scope.inputVO.handle_condition2 = undefined;
				$scope.inputVO.handle_condition3 = undefined;
			}
//			$scope.handle_control();
		}
	}
	
	$scope.getMaxEndDate = function() {
		//查詢最大結案日期(一級限1個營業日、二級限3個營業日、三級限5個營業日<結案日-建立日，當日結案為1日>)
		$scope.sendRecv("CRM990", "getMaxEndDate", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
			{'grade': $scope.inputVO.grade, 'happen_date': $scope.inputVO.happen_date },
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length > 0){
							$scope.happen_date = new Date();
							if($scope.inputVO.happen_date != undefined){
								$scope.happen_date = $scope.inputVO.happen_date;
							}

							var maxEndDate = $scope.toJsDate(tota[0].body.resultList[0].MAX_END_DATE);
							$scope.end_dateOptions = {
								minDate: $scope.happen_date,
								maxDate: maxEndDate
							};
							
							if($scope.inputVO.end_date != undefined && 
							   $scope.inputVO.end_date != null &&
							   $scope.inputVO.end_date != ''){
								
								if($scope.inputVO.end_date < $scope.happen_date ||
								   $scope.inputVO.end_date > maxEndDate){
									
									$scope.inputVO.end_date = undefined;
								}
							}
						}
					}
		});
	}
	
	//變更等級時觸動
	$scope.changeGrade = function() {
		$scope.getMaxEndDate();
	}
	
	/**客訴來源:金管會、其他主管機關、評議中心、存證信函、律師/陳情/書函
	 * 客訴類型:保險
	 * 只能建立三級客訴**/
	//變更客訴來源/客訴類型時觸動
	$scope.ctrlGrade = function() {
		if($scope.inputVO.complain_source == '15' || 
		   $scope.inputVO.complain_source == '18' ||
		   $scope.inputVO.complain_source == '19' ||
		   $scope.inputVO.complain_source == '24' ||
		   $scope.inputVO.complain_source == '25' ){
			$scope.inputVO.grade = '3';
			$scope.gradeCtrl = true;
		}else{
			$scope.gradeCtrl = false;
		}
	}
	
	/**
	 * 字數長度計算
	 */
	$scope.checkLenght = function(type, cloum, limit) {
		var text = $scope.inputVO[type];
		if (!text) {
			$scope[cloum] = limit;
		} else {
			var len = text.length;
			$scope[cloum] = parseInt(limit)-parseInt(len);
		}
	}
	
	//關閉視窗也需關掉$timeout
	$scope.stopTimeout = function() {
		if(timer){
			$timeout.cancel(timer);			
		}
	}
	
	//每隔20分鐘跳出視窗提醒是否儲存
	$scope.callAtTimeout = function() {
		$confirm({text: '提醒您，先儲存後再繼續填寫喔！'}, {size: 'sm'})
		.then(function(success) {
			$scope.save();
			timer = $timeout(function(){ $scope.callAtTimeout(); }, 1200000);
//			timer = $timeout(function(){ $scope.callAtTimeout(); }, 5000);		//for test
		}, function(){
			timer = $timeout(function(){ $scope.callAtTimeout(); }, 1200000);
//			timer = $timeout(function(){ $scope.callAtTimeout(); }, 5000);		//for test
		});
	}

	//每隔20分鐘跳出視窗提醒是否儲存
	if($scope.act == 'edit' || $scope.act == undefined){
		var timer;
		timer = $timeout(function(){ $scope.callAtTimeout(); }, 1200000);
//		timer = $timeout(function(){ $scope.callAtTimeout(); }, 5000);		//for test
	}
	
	//督導(二級)以上主管新建二級以上客訴案件需同步處理情形
	var userID = sysInfoService.getUserID();
	var creater = ($scope.resultList == undefined ? '' : $scope.resultList.CREATOR);
	$scope.syncHandler = function(level, condition) {
		if($scope.act == undefined || ($scope.act == 'edit' && userID == creater)){
			if($scope.inputVO.grade != '1' && $scope.role > '011'){
				
				var config = {
					'012': [1,2],
					'039': [1,2],
					'013': [1,2,3],
					'040': [1,2,3],
//					'041': [1,2,3,4]
				}
				
				// filter 
				if (!config[$scope.role] || config[$scope.role].indexOf(level) < 0) {
					return;
				}
				
				$scope.inputVO.handle_condition1 = condition;
				$scope.inputVO.handle_condition2 = condition;
				if ($scope.role != '012' && $scope.role != '039') {
					$scope.inputVO.handle_condition3 = condition;
				}
//				if ($scope.role == '041') {
//					$scope.inputVO.handle_condition4 = condition;
//				}
			}		
		}
		if($scope.act == 'edit' && userID != creater && $scope.resultList.GRADE == '3'){
			if($scope.resultList.EDITOR_CONDITION2 == null || $scope.resultList.EDITOR_CONDITION2 == userID){
				if ($scope.role == '013' && (level == '2' || level == '3')) {
					$scope.inputVO.handle_condition2 = condition;
					$scope.inputVO.handle_condition3 = condition;
				}
			}
		}
	}
	
	//初始化
	$scope.init = function() {
		$scope.lock = false;
		$scope.hideSave = false;
		$scope.lockCondition = false;
		$scope.gradeCtrl = false;
		$scope.back_control = true;
		$scope.is806 = false;
		$scope.custIdFlag = '';
		$scope.reportName = '';
		
		if($scope.priID == '038' || $scope.priID == '039' || $scope.priID == '040'){	//客服組長、客服科長、客服部長
			$scope.is806 = true;
		}
		
		if($scope.resultList != undefined) {			
			$scope.custIdFlag = $scope.resultList.CUST_ID;
			$scope.reportName = $scope.resultList.REPORT_NAME == undefined ? '' : $scope.resultList.REPORT_NAME;
			
			$scope.inputVO = {
					reportID : $scope.resultList.COMPLAIN_LIST_ID,
					complain_list_id : $scope.resultList.COMPLAIN_LIST_ID,
					case_type : $scope.resultList.CASE_TYPE,
					emp_id : $scope.resultList.EMP_ID,
					emp_name : $scope.resultList.EMP_NAME,
					ao_code : $scope.resultList.AO_CODE,
					service_yn : $scope.resultList.SERVICE_YN,
					branch_nbr : $scope.resultList.BRANCH_NBR,
					happen_date : $scope.toJsDate($scope.resultList.HAPPEN_DATE),
					end_date : $scope.toJsDate($scope.resultList.END_DATE),
					grade : $scope.resultList.GRADE,
					complain_source : $scope.resultList.COMPLAIN_SOURCE,
					complain_type : $scope.resultList.COMPLAIN_TYPE,
					complain_summary : $scope.resultList.COMPLAIN_SUMMARY,
					complain_product : $scope.resultList.COMPLAIN_PRODUCT,
					complain_product_currency : $scope.resultList.COMPLAIN_PRODUCT_CURRENCY,
					complain_product_amoun : $scope.resultList.COMPLAIN_PRODUCT_AMOUN,
					buy_date : $scope.toJsDate($scope.resultList.BUY_DATE),
					cust_id : $scope.resultList.CUST_ID,
					cust_name : $scope.resultList.CUST_NAME,
					birthdate : $scope.toJsDate($scope.resultList.BIRTHDATE),
					occup : $scope.resultList.OCCUP,
					phone : $scope.resultList.PHONE,
					education : $scope.resultList.EDUCATION,
					open_acc_date : $scope.toJsDate($scope.resultList.OPEN_ACC_DATE),
					total_asset : $scope.resultList.TOTAL_ASSET,
					check_sheet : $scope.resultList.CHECK_SHEET,
					buy_product_type : $scope.resultList.BUY_PRODUCT_TYPE,
					upload_file : $scope.resultList.UPLOAD_FILE,
					problem_describe : $scope.resultList.PROBLEM_DESCRIBE,
					cust_describe : $scope.resultList.CUST_DESCRIBE,
					handle_condition1 : $scope.resultList.HANDLE_CONDITION1,
					handle_condition2 : $scope.resultList.HANDLE_CONDITION2,
					handle_condition3 : $scope.resultList.HANDLE_CONDITION3,
					handle_condition4 : $scope.resultList.HANDLE_CONDITION4,
					createtime : $scope.toJsDate($scope.resultList.CREATETIME),
					treat_cust_fairly : $scope.resultList.TREAT_CUST_FAIRLY
			}
			$scope.ctrlGrade();
			
			//查詢最大結案日期(一級限1個營業日、二級限3個營業日、三級限5個營業日<結案日-建立日，當日結案為1日>)
			$scope.sendRecv("CRM990", "getMaxEndDate", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
				{'grade': $scope.inputVO.grade, 'happen_date': $scope.inputVO.happen_date },
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length > 0){
								$scope.end_dateOptions = {
									minDate: $scope.inputVO.happen_date,
									maxDate: $scope.toJsDate(tota[0].body.resultList[0].MAX_END_DATE)
								};
							}
						}
			});
			
			if($scope.act != undefined){
				if($scope.act == 'edit') {	//編輯
					if($scope.role_id != undefined && $scope.role == '071' && ($scope.role_id == 'B041' || $scope.role_id == 'B042')){ //$scope.role == '041' && ($scope.role_id == 'B024' || $scope.role_id == 'B025')
						$scope.lock = false;
						if ($scope.role_id == 'B042') //$scope.role_id == 'B025'
							$scope.hideSaveAndSend = true;
					} else {
						if(sysInfoService.getUserID() != $scope.resultList.CREATOR)
							$scope.lock = true;
						if($scope.role == '071'){ //$scope.role == '041'
							$scope.lockCondition = true;
							$scope.hideSaveAndSend = true;
							$scope.hideSave = true;
						}
					}
					//查詢是否有前一級
					$scope.sendRecv("CRM990", "checkFoword", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
						{'complain_list_id': $scope.inputVO.complain_list_id,
						 'grade': $scope.inputVO.grade},
							function(tota, isError) {
								if (!isError) {
									if(tota[0].body.hasFowordStep == true){
										$scope.back_control = false;
									}
									if(tota[0].body.resultList.length > 0){
										$scope.inputVO.pri_dept_name = tota[0].body.resultList[0].PRI_DEPT_NAME;
										$scope.inputVO.pri_job_title_name = tota[0].body.resultList[0].PRI_JOB_TITLE_NAME;
										$scope.inputVO.pri_emp_name = tota[0].body.resultList[0].PRI_EMP_NAME;
										$scope.inputVO.pri_emp_id = tota[0].body.resultList[0].PRI_EMP_ID;
									}
								}
					});
				} else if ($scope.act == 'detail'){		//查詢
					$scope.lock = true;
					$scope.lockCondition = true;
				}
			}
		} else {
			$scope.inputVO = {
					reportID : '',
					complain_list_id : '',
					emp_id : '',
					emp_name : '',
					ao_code : '',
					service_yn : undefined,
					branch_nbr : undefined,
					happen_date : new Date,
					end_date : undefined,
					open_acc_date : undefined,
					createtime : undefined
			}
		}
		
		//取得轄下分行
		// ["塞空ao_code用YN", $scope.inputVO, "區域NAME", "區域LISTNAME", "營運區NAME", "營運區LISTNAME", "分行別NAME", "分行別LISTNAME", "ao_codeNAME", "ao_codeLISTNAME", "emp_idNAME", "emp_idLISTNAME"]
	    $scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_list", "EMP_LIST"];
	    $scope.RegionController_setName($scope.test);
	    
	    if($scope.role == '038' || $scope.role == '039' || $scope.role == '040'){	//客服部人員
	    	$scope.BRANCH_LIST.push({LABEL: '客服部', DATA: '806'});
	    }
	    
	    if($scope.role == '071' || $scope.role == '072'){
	    	$scope.BRANCH_LIST.push({LABEL: '客戶關懷中心', DATA: '01782'});
	    	$scope.BRANCH_LIST.push({LABEL: '個金分行業務管理部', DATA: '175'});
	    }
	    
	    if($scope.role == '041' || $scope.role == '042'){	//個金分行業務管理部_分行內控品管科經辦、科長
	    	$scope.BRANCH_LIST.push({LABEL: '個金分行業務管理部', DATA: '175'});
	    }
	    
	    $scope.BRANCH_LIST = _.sortBy($scope.BRANCH_LIST, ['DATA']);
	    
		//textarea字數判斷
	    $scope.checkLenght('complain_summary', "summary_lenght", "150");
		$scope.checkLenght('problem_describe', "problem_lenght", "800");
		$scope.checkLenght('cust_describe', "cust_describe_lenght", "500");
		$scope.checkLenght('handle_condition1', "condition1_lenght", "800");
		$scope.checkLenght('handle_condition2', "condition2_lenght", "500");
		$scope.checkLenght('handle_condition3', "condition3_lenght", "500");
		$scope.checkLenght('handle_condition4', "condition4_lenght", "1000");
		
		$scope.sendRecv("CRM990", "getPriID", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {},
			function(tota, isError) {
				if (!isError) {
					$scope.priID = tota[0].body.privilege_id;
				}
		});
	}
	
	//取得理專資訊
	$scope.getEmpInfoById = function() {
		if($scope.inputVO.emp_id != '' && $scope.inputVO.emp_id != undefined){
			$scope.sendRecv("CRM990", "getEmpInfo", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {'emp_id': $scope.inputVO.emp_id},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0){
								$scope.inputVO.emp_id = '';
								$scope.showMsg("無此理專員編，請重新輸入。");
								return;
							}
							$scope.inputVO.emp_name = tota[0].body.resultList[0].EMP_NAME;
							$scope.inputVO.ao_code = tota[0].body.resultList[0].AO_CODE;
							$scope.inputVO.service_yn = tota[0].body.resultList[0].SERVICE_YN;
							return;
						}
			});
		}
	}
	
	$scope.getEmpInfoByAo = function() {
		if($scope.inputVO.ao_code != '' && $scope.inputVO.ao_code != undefined){
			$scope.sendRecv("CRM990", "getEmpInfo", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {'ao_code': $scope.inputVO.ao_code},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0){
								$scope.inputVO.ao_code = '';
								$scope.showMsg("無此AO Code，請重新輸入。");
							}
							$scope.inputVO.emp_id = tota[0].body.resultList[0].EMP_ID;
							$scope.inputVO.emp_name = tota[0].body.resultList[0].EMP_NAME;
							$scope.inputVO.service_yn = tota[0].body.resultList[0].SERVICE_YN;
							return;
						}
			});
		}
	}
	
	//取得開戶日期
	$scope.getOpenDate = function(branch_nbr, cust_id) {
		$scope.sendRecv("CRM990", "getOpenDate", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
				{'branch_nbr': branch_nbr, 'cust_id': cust_id},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length > 0){
								$scope.inputVO.open_acc_date = new Date(tota[0].body.resultList[0].OPEN_DATE);		//開戶日期
								return;										
							}
						}
		});
	}
	
	//取得客戶基本資料
	$scope.custDate = function(cust_id) {
		$scope.sendRecv("CRM990", "getCustData", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", {'cust_id': cust_id},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0){
							$scope.inputVO.cust_id = '';
							$scope.showMsg("ehl_01_SOT_001");		//客戶ID輸入錯誤，或此客戶並非本行客戶
						} else {
							$scope.inputVO.cust_name = tota[0].body.resultList[0].CUST_NAME;						//客戶姓名
							$scope.inputVO.birthdate = $scope.toJsDate(tota[0].body.resultList[0].BIRTH_DATE);		//出生年月日(西元)
//							$scope.inputVO.occup = tota[0].body.resultList[0].;										//職業別
//							$scope.inputVO.phone = tota[0].body.resultList[0].MOBILE_NO;							//聯絡電話
							$scope.inputVO.education = tota[0].body.resultList[0].EDUCATION_STAT;					//學歷
							$scope.inputVO.total_asset = tota[0].body.resultList[0].AUM_AMT;						//總往來資產AUM
							$scope.inputVO.check_sheet = tota[0].body.resultList[0].BILL_TYPE;						//寄送帳單註記
							return;								
						}
					}
		});
	}
	
	//取得客戶手機(電文)
	$scope.getCustPhone = function(cust_id) {
		$scope.sendRecv("CRM621", "inquire_phone", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO", {'cust_id' : cust_id},
				function(tota, isError) {
					if(tota[0].body.resultList.length > 0){
//						alert(JSON.stringify(tota[0].body.resultList));
						$scope.phoneList = tota[0].body.phoneList;
						angular.forEach($scope.phoneList, function(row) {
							if(row.CODE == '5'){
//								alert(row.TEL_NO);
								$scope.inputVO.phone = row.NUMBER;	//聯絡電話
							}
						});
					}
		});
	}
	
	//取得職業別(電文) & 取得客戶手機(電文)
	$scope.getCareer = function(cust_id) {
		$scope.sendRecv("CRM611", "inquire", "com.systex.jbranch.app.server.fps.crm611.CRM611InputVO", {'cust_id': cust_id},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.fc032151OutputVO != null) {
							var careerNbr = tota[0].body.fc032151OutputVO.CAREER;
							$scope.inputVO.occup = $filter('mapping')(careerNbr, $scope.CAREER);
//							$scope.inputVO.occup = tota[0].body.fc032151OutputVO.CAREER;
							
							//取得客戶手機(電文)
							$scope.getCustPhone(cust_id)
						}
					}
		});
	}
	
	$scope.tagCustID = function() {
		if($scope.inputVO.cust_id != '' && $scope.inputVO.cust_id != undefined)
			$scope.custIdFlag = $scope.inputVO.cust_id.toUpperCase();
	}
	
	//處理客戶基本資料
	$scope.getCustData = function() {
		if($scope.inputVO.cust_id != '' && $scope.inputVO.cust_id != undefined){
			$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();		//轉大寫
			
			if($scope.custIdFlag != '' && $scope.inputVO.cust_id == $scope.custIdFlag)
				return;
			
			//若編輯者非建案人員，則不帶出客戶資訊及歷史客訴資訊
			if($scope.resultList != undefined && $scope.loginID != $scope.resultList.CREATOR)
				return;
			
			$scope.inputVO.cust_name = ''				//客戶姓名
			$scope.inputVO.birthdate = undefined		//出生年月日(西元)
			$scope.inputVO.occup = '';					//職業別
			$scope.phoneList = [];
			$scope.inputVO.phone = '';					//聯絡電話
			$scope.inputVO.education = undefined;		//學歷
			$scope.inputVO.total_asset = '';			//總往來資產AUM
			$scope.inputVO.check_sheet = undefined;		//寄送帳單註記
			
			//取得開戶日期
			if($scope.inputVO.branch_nbr != undefined){
				$scope.getOpenDate($scope.inputVO.branch_nbr, $scope.inputVO.cust_id);
			}
			//取得客戶基本資料
			$scope.custDate($scope.inputVO.cust_id);
			
			//取得職業別(電文) & 取得客戶手機(電文)
			$scope.getCareer($scope.inputVO.cust_id);
		}
	}
	
	//檢查欄位是否有值
	$scope.checkVal = function(val) {
		if(val != undefined && val.trim() != ''){
			return true;		
		} else {
			return false;
		}
	}
	
	//取客戶前客訴案由及處理情形之資料
	$scope.getHisComInfo = function() {		
		if($scope.inputVO.cust_id != '' && $scope.inputVO.cust_id != undefined){
			$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();		//轉大寫
			
			if($scope.custIdFlag != '' && $scope.inputVO.cust_id == $scope.custIdFlag)
				return;
			
			//若編輯者非建案人員，則不帶出客戶資訊及歷史客訴資訊
			if($scope.resultList != undefined && $scope.loginID != $scope.resultList.CREATOR)
				return;
			
			$scope.sendRecv("CRM990", "getHisComInfo", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
					{'cust_id': $scope.inputVO.cust_id,
					 'complain_list_id': $scope.inputVO.complain_list_id},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length > 0){
//								alert(JSON.stringify(tota[0].body.resultList[0]));
//								$scope.inputVO.grade = tota[0].body.resultList[0].GRADE;				//等級
								
								var hisComInfo = tota[0].body.resultList[0];
								if ($scope.checkVal(hisComInfo.COMPLAIN_SOURCE))
									$scope.inputVO.complain_source = hisComInfo.COMPLAIN_SOURCE;			//客訴來源
								if ($scope.checkVal(hisComInfo.COMPLAIN_TYPE))
									$scope.inputVO.complain_type = hisComInfo.COMPLAIN_TYPE;				//客訴類型
								if ($scope.checkVal(hisComInfo.COMPLAIN_TYPE))
									$scope.inputVO.complain_summary = hisComInfo.COMPLAIN_SUMMARY;			//客訴摘要
								if ($scope.checkVal(hisComInfo.COMPLAIN_TYPE))
									$scope.inputVO.complain_product = hisComInfo.COMPLAIN_PRODUCT;			//客訴商品
								if ($scope.checkVal(hisComInfo.COMPLAIN_TYPE))
									$scope.inputVO.problem_describe = hisComInfo.PROBLEM_DESCRIBE;			//問題實況
								if ($scope.checkVal(hisComInfo.COMPLAIN_TYPE))
									$scope.inputVO.cust_describe = hisComInfo.CUST_DESCRIBE;				//客戶訴求
								
								var handleCondition1 = hisComInfo.HANDLE_CONDITION1;
								var handleCondition2 = hisComInfo.HANDLE_CONDITION2;
								var handleCondition3 = hisComInfo.HANDLE_CONDITION3;
								var handleCondition4 = hisComInfo.HANDLE_CONDITION4;
								
								if ($scope.checkVal(handleCondition1))
									$scope.inputVO.handle_condition1 = handleCondition1;		//第一級處理情形
								
								if ($scope.checkVal(handleCondition2))
									$scope.inputVO.handle_condition2 = handleCondition2;		//第二級處理情形
								
								if ($scope.checkVal(handleCondition3))
									$scope.inputVO.handle_condition3 = handleCondition3;		//第三級處理情形
								
								if ($scope.checkVal(handleCondition4))
									$scope.inputVO.handle_condition4 = handleCondition4;		//第四級處理情形
								
								if ($scope.checkVal(hisComInfo.REPORT_NAME)){
									$scope.reportName = hisComInfo.REPORT_NAME;
									$scope.inputVO.reportID = hisComInfo.COMPLAIN_LIST_ID;									
								}
//								return;										
							}
						}
			});
		}
	}
	
	//儲存
	$scope.save = function() {
		if(!$scope.checkVal($scope.inputVO.case_type)) {
			$scope.showErrorMsg('請選擇案件分流。');
		} else if (!$scope.checkVal($scope.inputVO.grade)) {
			$scope.showErrorMsg('請選擇等級。');
		} else if ($scope.priID >= '071' && !$scope.checkVal($scope.inputVO.treat_cust_fairly)) { //$scope.priID >= '041'
			$scope.showErrorMsg('請選擇是否符合公平待客原則？');
		} else {
			if($scope.inputVO.cust_id != '' && $scope.inputVO.cust_id != undefined){
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();		//轉大寫
			}
			$scope.sendRecv("CRM990", "save", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList){
								$scope.resultList = tota[0].body.resultList[0];
								$scope.init();
							}
							//儲存成功需重新計時
							$timeout.cancel(timer);
							timer = $timeout(function(){ $scope.callAtTimeout(); }, 1200000);
//							timer = $timeout(function(){ $scope.callAtTimeout(); }, 5000);		//for test
							
							$scope.showSuccessMsg('ehl_01_common_025'); //儲存成功
							return;
						}
			});
		}
	}

//	必輸欄位檢核(案件分流、分行別、發生日期、等級、客訴來源、客訴類型、客訴摘要、客訴商品、客戶姓名、問題實況、客戶訴求)
	$scope.requiredCheck = function() {
		$scope.doNext = true;
		var errorMsg = '';
		if(!$scope.checkVal($scope.inputVO.case_type)) {
			errorMsg = '請選擇案件分流。';
		} else if (!$scope.checkVal($scope.inputVO.cust_name)) {
			errorMsg = '請填寫客戶姓名。';
		} else if (!$scope.checkVal($scope.inputVO.branch_nbr)) {
			errorMsg = '請選擇分行別。';
		} else if ($scope.inputVO.happen_date == undefined) {
			errorMsg = '請選擇發生日期。';
		} else if (!$scope.checkVal($scope.inputVO.grade)) {
			errorMsg = '請選擇等級。';
		} else if (!$scope.checkVal($scope.inputVO.complain_source)) {
			errorMsg = '請選擇客訴來源。';
		} else if (!$scope.checkVal($scope.inputVO.complain_type)) {
			errorMsg = '請選擇客訴類型。';
		} else if (!$scope.checkVal($scope.inputVO.complain_summary)) {
			errorMsg = '請填寫客訴摘要。';
		} else if (!$scope.checkVal($scope.inputVO.complain_product)) {
			errorMsg = '請填寫客訴商品。';
		} else if (!$scope.checkVal($scope.inputVO.problem_describe)) {
			errorMsg = '請填寫問題實況。';
		} else if (!$scope.checkVal($scope.inputVO.cust_describe)) {
			errorMsg = '請填寫客戶訴求。';
		} else {
			if ($scope.priID > '040') {
				if(!$scope.checkVal($scope.inputVO.handle_condition4)) {
					if($scope.act == undefined || ($scope.resultList != undefined && $scope.resultList.HANDLE_STEP == 'A'))
						errorMsg = '欄位檢核錯誤："九、總行處理情形" 為必要輸入欄位，請輸入後重試。';					
				}
				if (!$scope.checkVal($scope.inputVO.treat_cust_fairly)) {
					errorMsg = '請選擇是否符合公平待客原則？';
				}
			} else {
				var grade = $scope.inputVO.grade;
				if(grade == '1') {
					if(!$scope.checkVal($scope.inputVO.handle_condition1))
						errorMsg = '欄位檢核錯誤："六、第一級處理情形" 為必要輸入欄位，請輸入後重試。';
				} else {
					if(($scope.priID < '012' || ($scope.priID == '038' && $scope.inputVO.branch_nbr == '806')) && 
							!$scope.checkVal($scope.inputVO.handle_condition1)) {
						errorMsg = '欄位檢核錯誤："六、第一級處理情形" 為必要輸入欄位，請輸入後重試。';	
					}
				}
				if(grade == '2') {
					if ((($scope.priID >= '012' && !$scope.is806) || 
							($scope.priID == '039' && $scope.inputVO.branch_nbr == '806')) && 
							!$scope.checkVal($scope.inputVO.handle_condition2)) {
						errorMsg = '欄位檢核錯誤："七、第二級處理情形" 為必要輸入欄位，請輸入後重試。';
					}
				}
				if(grade == '3') {
					if (($scope.priID == '012' || ($scope.priID == '039' && $scope.inputVO.branch_nbr == '806')) && 
							!$scope.checkVal($scope.inputVO.handle_condition2)) {
						errorMsg = '欄位檢核錯誤："七、第二級處理情形" 為必要輸入欄位，請輸入後重試。';
						
					} else if (($scope.priID == '013' || ($scope.priID == '040' && $scope.inputVO.branch_nbr == '806')) && 
							!$scope.checkVal($scope.inputVO.handle_condition3)) {
						errorMsg = '欄位檢核錯誤："八、第三級處理情形" 為必要輸入欄位，請輸入後重試。';
					}
				}
			}
		}
		if(errorMsg != ''){
			$scope.showErrorMsg(errorMsg);
			$scope.doNext = false;
		}
	}
	
	//客訴案件結案
	$scope.endTheCase = function() {
		$scope.sendRecv("CRM990", "endTheCase", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
			{'complain_list_id': $scope.inputVO.complain_list_id},
			function(tota, isError) {
				if (!isError) {
					//關閉計時提醒
					$timeout.cancel(timer);
					
					$scope.showSuccessMsg('ehl_01_common_023');		//執行成功
					$scope.closeThisDialog('successful');
				}
		});
	}
	
	//確認是否有下一覆核層級
	$scope.checkNext = function() {
		$scope.sendRecv("CRM990", "checkNext", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", 
				{'complain_list_id': $scope.inputVO.complain_list_id},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.hasNextStep == true){
							if($scope.role > '040' && ($scope.inputVO.end_date == null || $scope.inputVO.end_date == undefined)){
								$scope.showErrorMsg('請輸入結案日期。');
								return;
							} else {
								//關閉計時提醒
								$timeout.cancel(timer);
								
								var preInputVO = $scope.inputVO;
								var dialog = ngDialog.open({
									template: 'assets/txn/CRM990/CRM990_NEXT.html',
									className: 'CRM990_NEXT',
									showClose: false,
									controller: ['$scope', function($scope) {
										$scope.preInputVO = preInputVO;
									}]
								});
								dialog.closePromise.then(function (data) {
									if(data.value === 'successful'){
										$scope.closeThisDialog('successful');
										
									} else if(data.value === 'cancel'){
										//若沒有送出下一層級就返回編輯，則重新開啟計時提醒
										timer = $timeout(function(){ $scope.callAtTimeout(); }, 1200000);
//										timer = $timeout(function(){ $scope.callAtTimeout(); }, 5000);
									}
								});													
							}
						} else {
							if($scope.inputVO.end_date == null || $scope.inputVO.end_date == undefined){
								$scope.showErrorMsg('請輸入結案日期。');
								return;
							} else {
								$scope.endTheCase();												
							}
						}
					}
			});
	}
	
	//儲存並送出
	$scope.nextStep = function() {
		//必輸欄位檢核
		$scope.requiredCheck();
		
		if($scope.doNext == true){
			$confirm({text: '確定送出此次編輯-並更新處理狀態'}, {size: 'sm'}).then(function() {			
				$scope.sendRecv("CRM990", "save", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.complain_list_id = tota[0].body.complain_list_id;
							$scope.checkNext();
						}
				});
			});
		}
	}
	
	//退回
	$scope.backStep = function(step) {
		//關閉計時提醒
		$timeout.cancel(timer);
		
		var preInputVO = $scope.inputVO;
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM990/CRM990_REJECT.html',
			className: 'CRM990_REJECT',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.preInputVO = preInputVO;
				$scope.step = step;
			}]
		});
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.showSuccessMsg('ehl_01_common_023');		//執行成功
				$scope.closeThisDialog('successful');
				
			} else if(data.value === 'cancel'){
				//若沒有退回就返回編輯，則重新開啟計時提醒
				timer = $timeout(function(){ $scope.callAtTimeout(); }, 1200000);
//				timer = $timeout(function(){ $scope.callAtTimeout(); }, 5000);
			}
		});
	}
	
	//上傳投資明細損益表
	$scope.uploadFinshed = function(name, rname) {
		$scope.inputVO.fileName = name;
		$scope.inputVO.realfileName = rname;
	};

	//上傳招攬/事件報告書
	$scope.uploadReport = function(name, rname) {
		$scope.inputVO.reportName = name;
		$scope.inputVO.realReportName = rname;
	};
	
	//取得客訴表-投資明細損益表
	$scope.downloadFile = function() {
		$scope.sendRecv("CRM990", "downloadFile", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO",
			{'complain_list_id' : $scope.inputVO.complain_list_id}, 
				function(tota, isError) {
					if (!isError) {
					}
		});
	}
	
	//刪除
	$scope.deleteCase = function() {
		$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
			$scope.sendRecv("CRM990", "deleteCase", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO",
					{'complain_list_id' : $scope.inputVO.complain_list_id}, 
						function(tota, isError) {
							if (!isError) {
								//刪除成功需關閉計時提醒
								$timeout.cancel(timer);
								
								$scope.showSuccessMsg('ehl_01_common_003');		//刪除成功
								$scope.closeThisDialog('successful');
							}
				});
		});
	}
	
	// 預覽招攬/事件報告書
	$scope.reportView = function(){
		$scope.sendRecv("CRM990", "getReportView", "com.systex.jbranch.app.server.fps.crm990.CRM990InputVO",
				{'complain_list_id' : $scope.inputVO.reportID},
				function(tota, isError) {
					if (!isError) {
						var description = tota[0].body.pdfUrl;
						window.open('./INS300_PDF.html?pdfurl=' + description, 'Description');
						return;
					}
		});
	}
	
	// 刪除招攬/事件報告書
	$scope.deleteReport = function(){
		$confirm({text: '是否刪除招攬/事件報告書？ '}, {size: 'sm'}).then(function() {
			$scope.reportName = '';
//			$scope.inputVO.reportName = '';
			$scope.inputVO.reportID = '';			
		});
	}

	$scope.init();
});