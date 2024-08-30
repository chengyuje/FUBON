/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM110_MGMController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM110_MGMController";
		
		$scope.loginBranchID = sysInfoService.getBranchID();
		
		// filter
		getParameter.XML(["MGM.LDAY_AUM_AMT"], function(totas) {
			if (totas) {
				$scope.aum_amt = totas.data[totas.key.indexOf('MGM.LDAY_AUM_AMT')][0].DATA
			}
		});
		
		// 初始化
		$scope.initMGM = function(){
			$scope.inputVO.mgm_cust_id = '';
			$scope.inputVO.mgm_cust_name = '';
			$scope.inputVO.branch_nbr = '';
			$scope.inputVO.emp_name = '';
		}
		
		$scope.initBeMGM = function(){
			$scope.inputVO.be_mgm_cust_id = '';
			$scope.inputVO.be_mgm_cust_name = '';
			$scope.inputVO.be_mgm_cust_phone = '';
		}
		
		$scope.init = function(){
			$scope.editFlag = true;
			
			if($scope.row != undefined){
				//有任一表單已由主管覆核，就不可作修改
				if($scope.row.MGM_SIGN_STATUS == '3' || $scope.row.BE_MGM_SIGN_STATUS == '3'){
					$scope.editFlag = false;
				}
				
				//修改
				$scope.inputVO.seq = $scope.row.SEQ;
				$scope.inputVO.act_seq = $scope.row.ACT_SEQ;
				
				$scope.inputVO.mgm_cust_id = $scope.row.MGM_CUST_ID;
				$scope.inputVO.mgm_cust_name = $scope.row.CUST_NAME;
				$scope.inputVO.branch_nbr = $scope.row.BRA_NBR;
				$scope.inputVO.emp_name = $scope.row.EMP_NAME;
				
				$scope.inputVO.mgm_start_date = $scope.toJsDate($scope.row.MGM_START_DATE);
				
				$scope.inputVO.be_mgm_cust_id = $scope.row.BE_MGM_CUST_ID;
				$scope.inputVO.be_mgm_cust_name = $scope.row.BE_MGM_CUST_NAME;
				$scope.inputVO.be_mgm_cust_phone = $scope.row.BE_MGM_CUST_PHONE;
				
				$scope.inputVO.memo = $scope.row.MEMO;
				
				//查詢已上傳的推薦人簽署表單&被推薦人簽署表單
				$scope.sendRecv("MGM110", "getSignForm", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'seq' : $scope.row.SEQ},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length > 0){
									$scope.mgmSignFormName = tota[0].body.resultList[0].MGM_SIGN_FORM_NAME;
									$scope.beMgmSignFormName = tota[0].body.resultList[0].BE_MGM_SIGN_FORM_NAME;
								}
								return;
							}
				});
			} else {
				//新增
				$scope.inputVO.act_seq = $scope.act_seq;
				$scope.inputVO.seq = '';
				$scope.initMGM();
				$scope.initBeMGM();				
			}
			
		};
		$scope.init();
		
		// date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
			
			$scope.limitDate();
		};
		
		// 推薦日只能選取活動期間內，且僅能補鍵三個月內推薦之案件
		$scope.limitDate = function() {
			$scope.sendRecv("MGM110", "getActPeriod", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
//							$scope.mgm_start_dateOptions.minDate = tota[0].body.resultList[0].EFF_DATE;
//							$scope.mgm_start_dateOptions.maxDate = tota[0].body.resultList[0].DEADLINE;
							var eff_date = tota[0].body.resultList[0].EFF_DATE;
							var act_deadline = $scope.toJsDate(tota[0].body.resultList[0].DEADLINE);
							var mgm_min_date = tota[0].body.resultList[0].MGM_MIN_DATE;
							var today = new Date();
							
							if(mgm_min_date < eff_date){
								$scope.mgm_start_dateOptions.minDate = eff_date;
							} else {
								$scope.mgm_start_dateOptions.minDate = mgm_min_date;
							}
							
							if(act_deadline < today){
								$scope.mgm_start_dateOptions.maxDate = act_deadline;
							} else {
								$scope.mgm_start_dateOptions.maxDate = today;
							}
							
							return;
						}
			});
		};
		
		// 檢核推薦人資格 (聯行通通可以"鍵機"，不限分行及理專)
		$scope.checkMGMCust = function(){
			if($scope.inputVO.mgm_cust_id.trim() != ''){
				$scope.inputVO.mgm_cust_id = $scope.inputVO.mgm_cust_id.toUpperCase();
				
				if($scope.inputVO.be_mgm_cust_id.trim() != ''){
					if($scope.inputVO.mgm_cust_id == $scope.inputVO.be_mgm_cust_id){
						$scope.showErrorMsg('推薦人與被推薦人不可為同一人。');
						$scope.initMGM();
						return;
					}
				}
				
				$scope.sendRecv("MGM110", "checkMGMCust", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'mgm_cust_id' : $scope.inputVO.mgm_cust_id},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length == 0) {
									//查無資料
									$scope.showErrorMsg('ehl_01_cus130_002', [$scope.inputVO.mgm_cust_id]);			//無此客戶ID：{0}
									$scope.initMGM();
									return;
								}else{
									//TBCRM_CUST_MAST.VIP_DEGREE為NULL、空白、M 都是非理財會員
									if(tota[0].body.resultList[0].VIP_DEGREE == null || 
											tota[0].body.resultList[0].VIP_DEGREE.trim() == '' ||
											tota[0].body.resultList[0].VIP_DEGREE.trim() == 'M'){
//										$scope.showErrorMsg('此推薦人不具備理財會員之身分，故不符合活動參與資格。');
//										$scope.initMGM();
//										return;
										if(tota[0].body.resultList[0].AO_CODE == null){
											$scope.showErrorMsg('此推薦人不具備理財會員之身分且無專屬理專服務，故不符合活動參與資格。');
											$scope.initMGM();
											return;
										}
									}
									
									$scope.inputVO.mgm_cust_name = tota[0].body.resultList[0].CUST_NAME;
									$scope.inputVO.branch_nbr = tota[0].body.resultList[0].BRA_NBR;
									$scope.inputVO.emp_name = tota[0].body.resultList[0].EMP_NAME;
									
								}
							}
				});
			}else{
				$scope.initMGM();
			}
		}
		
		// 檢核被推薦人
		$scope.checkBeMGMCust = function(){
			
			if($scope.inputVO.be_mgm_cust_id.trim() != ''){
				$scope.inputVO.be_mgm_cust_id = $scope.inputVO.be_mgm_cust_id.toUpperCase();
				
				if($scope.inputVO.mgm_cust_id.trim() != ''){
					if($scope.inputVO.mgm_cust_id == $scope.inputVO.be_mgm_cust_id){
						$scope.showErrorMsg('推薦人與被推薦人不可為同一人。');
						$scope.initBeMGM();
						return;
					}
				}
				
				if($scope.inputVO.be_mgm_cust_id.trim().length < 10){
					$scope.showErrorMsg('法人不得為被推薦人。');
					$scope.initBeMGM();
					return;
				}
				
				// 檢核該被推薦人於活動期間是否已被推薦
				$scope.sendRecv("MGM110", "checkMGMHis", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length != 0 && tota[0].body.resultList[0].BE_MGM_CUST_ID != null ){
									$scope.MGMFlag = 'Y';
									$scope.showErrorMsg('該被推薦人於活動期間已被推薦。');
									$scope.initBeMGM();
									return;
								}else{
									// 檢核該被推薦人資格(1.非理財會員，且總往來資產<=10萬		2.全新開戶)
									$scope.sendRecv("MGM110", "checkBeMGMCust", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
											function(tota, isError) {
												if (!isError) {
													if(tota[0].body.resultList[0].FIRST_OPEN_YEAR == null){
														$scope.showErrorMsg('此客戶尚未開戶。');
														$scope.initBeMGM();
														return;
														
													}else{
														//非活動年度全新開戶 ===> 舊戶
														if(tota[0].body.resultList[0].FIRST_OPEN_YEAR < tota[0].body.resultList[0].ACT_YEAR){
															//TBCRM_CUST_MAST.VIP_DEGREE為NULL、空白、M 都是非理財會員
															if(tota[0].body.resultList[0].VIP_DEGREE != null && 
															   tota[0].body.resultList[0].VIP_DEGREE.trim() != '' &&
															   tota[0].body.resultList[0].VIP_DEGREE.trim() != 'M'){
																$scope.showErrorMsg('此客戶為本行理財會員，故不符合活動參與資格。');
																$scope.initBeMGM();
																return;
															}
															
															//活動年度之前一年度12月31日AUM餘額加總小於等值新台幣10萬元
															if(tota[0].body.resultList[0].LDAY_AUM_AMT > $scope.aum_amt){
																$scope.showErrorMsg('此客戶AUM餘額加總大於等值新台幣' + $scope.aum_amt + '元，故不符合活動參與資格。');
																$scope.initBeMGM();
																return;
															}
														}
														$scope.inputVO.be_mgm_cust_name = tota[0].body.resultList[0].CUST_NAME;
														$scope.inputVO.be_mgm_cust_phone = tota[0].body.resultList[0].CUST_PHONE;
													}
												}
									});
								}
							}
				});
			}else{
				$scope.initBeMGM();
			}
		}
		
		// 推薦人於推薦當時需年滿20歲，方可符合活動參與資格。
		$scope.checkAge = function(){
			if($scope.inputVO.mgm_start_date != null){
				$scope.sendRecv("MGM110", "checkAge", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
						function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length > 0) {
							
							if(tota[0].body.resultList[0].AGE < 0){
								$scope.showErrorMsg('推薦人於推薦當時需年滿20歲，方可符合活動參與資格。');
								$scope.inputVO.mgm_start_date = undefined;
								return;
							}
						}
					}
				});				
			}
		}
		
//		// 送出及列印
//		$scope.sendPrint = function(){
//			$scope.sendRecv("MGM110", "sendPrint", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
//					function(tota, isError) {
//						if (!isError) {
////	                 		$scope.showMsg("ehl_01_common_023");			//執行成功	
//	                 		$scope.closeThisDialog('successful');
//	                 	}
//			});				
//		}
		
		//上傳推薦人簽署表單(掃描檔)
        $scope.uploadMGM = function(name, rname) {
        	if(name){
        		$scope.inputVO.mgm_sign_form_name = name;
            	$scope.inputVO.real_mgm_sign_form_name = rname;
        	}
        };
        
        //上傳被推薦人簽署表單(掃描檔)
        $scope.uploadBeMGM = function(name, rname) {
        	if(name){
        		$scope.inputVO.be_mgm_sign_form_name = name;
            	$scope.inputVO.real_be_mgm_sign_form_name = rname;
        	}
        };
        
		//確認及儲存
		$scope.confirmSave = function(){
			$scope.sendRecv("MGM110", "confirmSave", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.seq = tota[0].body.resultList[0].SEQ;
							$scope.showSuccessMsg('ehl_01_common_025'); 	//儲存成功
						}
			});				
		}
        
        //送出及上傳
		$scope.send = function(){
			$scope.sendRecv("MGM110", "send", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
//             		$scope.showMsg("ehl_01_common_023");			//執行成功	
             		$scope.closeThisDialog('successful');
             	}
			});	
		}
		
		//檢視已上傳的簽署表
		$scope.signFormView = function(formType){
			$scope.sendRecv("MGM110", "signFormView", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", 
				{'formType': formType, 'seq' : $scope.row.SEQ},
    				function(tota, isError) {
    					if (!isError) {
    						var description = tota[0].body.pdfUrl;
    						window.open('./INS300_PDF.html?pdfurl=' + description, 'Description');
    						return;
    					}
    		});
		}
		
});
