/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM410_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $q, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SQM410_DETAILController";
		
//		$controller('SQM120_sendController', {$scope: $scope});
		
//		getParameter.XML(["SQM.QTN_TYPE", "SQM.ANS_TYPE"], function(totas) {
//			if (totas) {
//				alert('here');
//				$scope.mappingSet['SQM.QTN_TYPE'] = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
//				$scope.mappingSet['SQM.ANS_TYPE'] = totas.data[totas.key.indexOf('SQM.ANS_TYPE')];
//			}
//		});
				
		$scope.init = function() {
			
			$scope.inputVO = $scope.connector("get", "SQM410_inputVO");
			$scope.connector("set", "SQM410_inputVO", null);
			
			$scope.inputVO.seq = $scope.inputVO.row.SEQ;
			$scope.inputVO.qtnType = $scope.inputVO.row.QTN_TYPE;
			$scope.inputVO.data_date = $scope.inputVO.row.DATA_DATE;
			$scope.inputVO.trade_date = $scope.inputVO.row.TRADE_DATE;
			$scope.inputVO.end_date = $scope.inputVO.row.END_DATE;
			$scope.inputVO.cust_id = $scope.inputVO.row.CUST_ID;
			$scope.inputVO.cust_name = $scope.inputVO.row.CUST_NAME;
			$scope.inputVO.resp_note = $scope.inputVO.row.RESP_NOTE == null ? '無質化意見。' : $scope.inputVO.row.RESP_NOTE;
			$scope.inputVO.branch_nbr = $scope.inputVO.row.BRANCH_NBR;
			$scope.inputVO.branch_name = $scope.inputVO.row.BRANCH_NAME;
			$scope.inputVO.emp_name = $scope.inputVO.row.EMP_NAME;
			$scope.inputVO.emp_id = $scope.inputVO.row.EMP_ID;
			$scope.inputVO.case_no = $scope.inputVO.row.CASE_NO;
			$scope.inputVO.last_visit_date = $scope.inputVO.row.LAST_VISIT_DATE;
			$scope.inputVO.con_degree_desc = $scope.inputVO.row.CON_DEGREE == null ? '新開戶' : $scope.inputVO.row.CON_DEGREE;
			$scope.inputVO.con_degree = $scope.inputVO.row.CON_DEGREE;
			$scope.inputVO.frq_day = $scope.inputVO.row.FRQ_DAY;
			$scope.inputVO.mobile_no = $scope.inputVO.row.MOBILE_NO;
			$scope.inputVO.question_desc = $scope.inputVO.row.QUESTION_DESC;
			$scope.inputVO.answer = $scope.inputVO.row.ANSWER;
			$scope.QTN_LIST = $scope.inputVO.QTN_LIST;
			$scope.role_id = projInfoService.getRoleID();
		};
		$scope.init();
		
		$scope.viewDetail = function() {
//			alert(JSON.stringify($scope.inputVO));
			$scope.sendRecv("SQM120", "getDtlData", "com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO", {'case_no' : $scope.inputVO.case_no},
				function(totas, isError) {
					if (totas[0].body.resultList == null || totas[0].body.resultList.length == 0) {
//						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					
					$scope.dtlDataList = totas[0].body.resultList;
					angular.forEach($scope.dtlDataList,	function(row, index, objs) {
						$scope.inputVO.brh_desc = row.BRH_DESC;
						$scope.inputVO.waiting_time = row.WAITING_TIME;
						$scope.inputVO.working_time = row.WORKING_TIME;
						$scope.inputVO.cur_job_y = row.CUR_JOB_Y;
						$scope.inputVO.cur_job_m = row.CUR_JOB_M;
						$scope.inputVO.sup_emp_name = row.SUP_EMP_NAME_NEW;
						$scope.inputVO.sup_emp_id = row.SUP_EMP_ID_NEW;
						$scope.inputVO.sup_cur_job = row.SUP_CUR_JOB_NEW;
						$scope.inputVO.improve_desc = row.IMPROVE_DESC;
						$scope.inputVO.op_sup_remark = row.OP_SUP_REMARK;
						$scope.inputVO.rc_vice_sup_remark = row.RC_VICE_SUP_REMARK;
						$scope.inputVO.deduction_initial = row.DEDUCTION_INITIAL;
						$scope.inputVO.rc_sup_remark = row.RC_SUP_REMARK;
						$scope.inputVO.headmgr_remark = row.HEADMGR_REMARK;
						$scope.inputVO.case_flow = row.CASE_FLOW; // 是否為已進入案件流程
						$scope.inputVO.creator = row.CREATOR;
						if($scope.inputVO.deduction_initial == 'Y' || $scope.inputVO.deduction_initial == 'N'){	
							$scope.merge_rc_sup_remark = $scope.inputVO.rc_vice_sup_remark != null ? "處副主管: "+$scope.inputVO.rc_vice_sup_remark : "處副主管無簽核意見" ;
							$scope.merge_rc_sup_remark = $scope.inputVO.rc_sup_remark != null ? $scope.merge_rc_sup_remark != null ? $scope.merge_rc_sup_remark+'\n處主管: '+$scope.inputVO.rc_sup_remark : '處主管: '+$scope.inputVO.rc_sup_remark : $scope.merge_rc_sup_remark+'\n處主管無簽核意見';
						}
					});
					
					$scope.getSup_Job_title_name($scope.inputVO.sup_emp_id);
			});
		};
		$scope.viewDetail();
		
		$scope.getQuestions = function() {
			// $scope.qtn_type == '5' 為簡訊問題拿自己的
			debugger;
			if ($scope.inputVO.qtnType != 'WMS05') {

				$scope.sendRecv("SQM120", "getQuestion", "com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO", {'qtnType':$scope.inputVO.qtnType, 'data_date':$scope.inputVO.data_date, 'cust_id':$scope.inputVO.cust_id, 'isFrom':'SQM410'},
					function(totas, isError) {
						if (!isError) {
							$scope.column = totas[0].body.totalList;
							angular.forEach($scope.column, function(row, index) {
								if(index == 0) {
									var test = row.ANSWER.indexOf("{") == -1 ? row.ANSWER.length : row.ANSWER.indexOf("{");
									row.ANSWER = row.ANSWER.substring(0, test);
								}
							});
							$scope.outputVO = totas[0].body;
							$scope.totalList = _.orderBy(totas[0].body.totalList,['QST_ORDER']);
							$scope.mappingSet['SQM.ANS_TYPE'] = totas[0].body.ansTypeList;
							$scope.mappingSet['SQM.ANS_TYPE_PUSH'] = totas[0].body.ansTypePushList;
						};
				});
			} else {
				$scope.totalData = [];
				var QST_LIST = [ {'QST_DESC' : '客戶手機' ,'ANSWER' : $scope.inputVO.mobile_no}, 
				                 {'QST_DESC' : '簡訊內容' ,'ANSWER' : $scope.inputVO.question_desc}, 
				                 {'QST_DESC' : '客戶回覆' ,'ANSWER' : $scope.inputVO.answer}];
			
				$scope.column = QST_LIST;
				//只要xml資料
				$scope.sendRecv("SQM120", "getQuestion", "com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO", {'qtnType':$scope.inputVO.qtnType, 'data_date':$scope.inputVO.data_date, 'cust_id':$scope.inputVO.cust_id, 'isFrom':'SQM410'},
			 	    function(totas, isError) {
				        if (!isError) {
				        	$scope.mappingSet['SQM.ANS_TYPE'] = totas[0].body.ansTypeList;
				        	$scope.mappingSet['SQM.ANS_TYPE_PUSH'] = totas[0].body.ansTypePushList;
						};
				 });
			}
		}
		$scope.getQuestions();
		
		$scope.getSup_Job_title_name = function(sup_emp_id) {
			$scope.sendRecv("SQM120", "getJob_title_name", "com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO",
							{'emp_id' : sup_emp_id, 'trade_date' : $scope.inputVO.trade_date},
				function(totas, isError) {
					if (!isError) {
						$scope.outputVO = totas[0].body;
						$scope.empList = totas[0].body.resultList;
						$scope.inputVO.sup_emp_name = null;
						$scope.inputVO.sup_cur_job = null;
						angular.forEach($scope.empList, function(row, index, objs) {
							$scope.inputVO.sup_emp_id = row.EMP_ID;
							$scope.inputVO.sup_emp_name = row.EMP_NAME;
							$scope.inputVO.sup_cur_job = row.CUR_JOB;
						});
					};
				});
		}
		
		//區督導建議將儲存於textarea之中
		$scope.addSugg = function(){//營運區指示處理
			var sugg = "";
			var str = "";
			if($scope.inputVO.op_sup_remark){//避免出現null
				str = $scope.inputVO.op_sup_remark + "";
			}
			if(str.indexOf("1.建議扣分")==-1 && str.indexOf("1.建議不扣分")==-1){//無自動加上文字
				if ($scope.inputVO.prededuction_initial == 'Y'){
					sugg = "1.建議扣分\n2.請填寫意見:\n" + str;
				}
				if ($scope.inputVO.prededuction_initial == 'N'){
					sugg = "1.建議不扣分\n2.請填寫意見:\n" + str;
				}
			}else{
				if ($scope.inputVO.prededuction_initial == 'Y'){
					sugg = str.replace("1.建議不扣分","1.建議扣分");
				}
				if ($scope.inputVO.prededuction_initial == 'N'){
					sugg = str.replace("1.建議扣分","1.建議不扣分");					
				}
			}
			
			$scope.inputVO.op_sup_remark = sugg;
		}
		
		$scope.addSugg2 = function(){//處級指示處理
			var sugg = "";
			var str = "";
			if($scope.inputVO.jobTitleName == '處副主管' && $scope.inputVO.rc_vice_sup_remark){
				str = $scope.inputVO.rc_vice_sup_remark + "";
			}
			if($scope.inputVO.jobTitleName == '處主管' && $scope.inputVO.rc_sup_remark){
				str = $scope.inputVO.rc_sup_remark + "";
			}
			
			if(str.indexOf("1.建議扣分")==-1 && str.indexOf("1.建議不扣分")==-1){//無自動加上文字
				if ($scope.inputVO.deduction_initial == 'Y'){
					sugg = "1.建議扣分\n2.請填寫意見:\n" + str;
				}
				if ($scope.inputVO.deduction_initial == 'N'){
					sugg = "1.建議不扣分\n2.請填寫意見:\n" + str;
				}
			}else{
				if ($scope.inputVO.deduction_initial == 'Y'){
					sugg = str.replace("1.建議不扣分","1.建議扣分");
				}
				if ($scope.inputVO.deduction_initial == 'N'){
					sugg = str.replace("1.建議扣分","1.建議不扣分");					
				}
			}
			if($scope.inputVO.jobTitleName == '處副主管'){
				$scope.inputVO.rc_vice_sup_remark = sugg;
			}
			if($scope.inputVO.jobTitleName == '處主管'){
				$scope.inputVO.rc_sup_remark = sugg;
			}
			
		}
		//業務處長意見將儲存於textarea之中
		
		$scope.getJob_title_name = function(emp_id) {
			$scope.sendRecv("SQM120", "getJob_title_name", "com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO",
							{'emp_id' : emp_id, 'trade_date' : $scope.inputVO.trade_date},
				function(totas, isError) {
					if (!isError) {
						$scope.outputVO = totas[0].body;
						$scope.empList = totas[0].body.resultList;
						$scope.inputVO.emp_name = null;
						$scope.inputVO.cur_job = null;
						angular.forEach($scope.empList, function(row, index, objs) {
							$scope.inputVO.emp_id = row.EMP_ID;
							$scope.inputVO.emp_name = row.EMP_NAME;
							$scope.inputVO.cur_job = row.CUR_JOB;
						});
					};
				});
		}
		if ($scope.inputVO.emp_id != null) {
			$scope.getJob_title_name($scope.inputVO.emp_id);
		}
		
		//儲存並送出
		$scope.saveAndSend = function() {
//			debugger;
			//TODO: 檢核
			// 情境1 : 督導輸入建議進行儲存
			if($scope.inputVO.privilegeID == '012'){
				//檢核1: 簽核意見必需輸入
//				debugger;
				if(!$scope.inputVO.prededuction_initial || 
						($scope.inputVO.prededuction_initial != 'RL' && $scope.inputVO.op_sup_remark.length<=17)){
					$scope.showErrorMsg("請輸入督導簽核意見");
					return;
				}
				//檢核2: 退件必須輸入退件原因
				if($scope.inputVO.prededuction_initial == 'RL' && !$scope.inputVO.op_sup_reject_reason){
					$scope.showErrorMsg("請輸入退件原因");
					return;
				}
			}
			// 情境2 : 業務處長輸入進行儲存
			if($scope.inputVO.privilegeID == '013'){
				//檢核1: 如果不扣分情況下, 簽核意見必需輸入
				if(!$scope.inputVO.deduction_initial){
					$scope.showErrorMsg("請輸入"+inputVO.jobTitleName+"簽核意見");
					return;
				}else{
					if($scope.inputVO.deduction_initial == 'N'){ //不扣分情況
						if($scope.inputVO.jobTitleName == '處副主管' && $scope.inputVO.rc_vice_sup_remark.length<=17){
							$scope.showErrorMsg("請輸入處副主管簽核意見");
							return;
						}
						if($scope.inputVO.jobTitleName == '處主管' && $scope.inputVO.rc_sup_remark.length<=17){
							$scope.showErrorMsg("請輸入處主管簽核意見");
							return;
						}
					}
					if($scope.inputVO.deduction_initial == 'Y'){ //扣分情況
						if($scope.inputVO.jobTitleName == '處副主管' && $scope.inputVO.rc_vice_sup_remark.length<=16){
							$scope.showErrorMsg("請輸入處副主管簽核意見");
							return;
						}
						if($scope.inputVO.jobTitleName == '處主管' && $scope.inputVO.rc_sup_remark.length<=16){
							$scope.showErrorMsg("請輸入處主管簽核意見");
							return;
						}
					}
				}
				//檢核2: 退件必須輸入退件原因
				if($scope.inputVO.deduction_initial == 'RL' && !$scope.inputVO.rc_sup_reject_reason1){
					$scope.showErrorMsg("請輸入退件原因");
					return;
				}
				if($scope.inputVO.deduction_initial == 'RC' && !$scope.inputVO.rc_sup_reject_reason2){
					$scope.showErrorMsg("請輸入退件至建立人原因");
					return;
				}
			}
			
			//退件-->SQM120.returns
			//送出至下一關-->SQM120.save
			if($scope.inputVO.prededuction_initial == 'RL'
				|| $scope.inputVO.deduction_initial == 'RL'
				|| $scope.inputVO.deduction_initial == 'RC'){
				$scope.returnInputVO = [];
				$scope.returnInputVO =
					{
					  'returns_type':$scope.inputVO.deduction_initial == 'RC'?'returnsToCreator':'returns',
					  'seq':$scope.inputVO.seq,
					  'qtnType':$scope.inputVO.qtnType,
					  'cust_id':$scope.inputVO.cust_id,
					  'data_date':$scope.inputVO.data_date,
					  'loginID':$scope.inputVO.loginEmpID,
					  'isFrom':'SQM410',
					  'returns_remark':''
					};
					//不同角色的退件原因, 填寫欄位不同, 需各別判斷
					if($scope.inputVO.privilegeID == '012'){
						$scope.returnInputVO.returns_remark = $scope.inputVO.op_sup_reject_reason;
					}
					if($scope.inputVO.privilegeID == '013' && $scope.inputVO.deduction_initial == 'RL'){//退件
						$scope.returnInputVO.returns_remark = $scope.inputVO.rc_sup_reject_reason1;
					}
					if($scope.inputVO.privilegeID == '013' && $scope.inputVO.deduction_initial == 'RC'){//退件至建立人
						$scope.returnInputVO.returns_remark = $scope.inputVO.rc_sup_reject_reason2;
					}
				$scope.sendRecv("SQM120","returns","com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO",$scope.returnInputVO,
						function(tota,isError){
			    			if(isError){
			    				$scope.showErrorMsg(tota[0].body.msgData);
			    			}
			               	if (tota.length > 0) {
				        		$scope.showSuccessMsg('ehl_01_common_002');
				        		$scope.backCaseList();
			            };
			    });
			}else{
				//建議扣分或不扣分. 送至下一關
				if($scope.inputVO.privilegeID == '012'){
//					if($scope.inputVO.prededuction_initial == 'N' && ($scope.inputVO.op_sup_remark.length<50 || $scope.inputVO.op_sup_remark.length>100)){
					if($scope.inputVO.prededuction_initial == 'N' && !$scope.inputVO.op_sup_remark){
						$scope.showErrorMsg("請輸入建議不扣分原因200字內");
						return;
					}
				}
				
				$scope.inputVO.loginID = $scope.inputVO.loginEmpID;
				$scope.inputVO.isFrom = "SQM410";
				
				$scope.sendRecv("SQM120","save","com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO", $scope.inputVO,
						function(tota,isError){
			    			if(isError){
			    				$scope.showErrorMsg(tota[0].body.msgData);
			    			}
			               	if (tota.length > 0) {
				        		$scope.showSuccessMsg('ehl_01_common_002');
				        		$scope.backCaseList();
			               	};
			    });
			}
			
			
		};
		
		// 資料儲存-暫存
		$scope.save = function() {
			$scope.editIputVO = angular.copy($scope.inputVO);
			$scope.editIputVO.save_type = "save";
			$scope.editIputVO.owner_emp_id = ""; //暫存不能指定下一個處理人員
			$scope.editIputVO.loginID = $scope.inputVO.loginEmpID;
			$scope.editIputVO.isFrom = "SQM410";
			$scope.sendRecv(
							"SQM120",
							"save",
							"com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO",
							$scope.editIputVO,
							function(tota, isError) {
								if (isError) {
									$scope.showErrorMsg(tota[0].body.msgData);
								}
								if (tota.length > 0) {
									$scope.showSuccessMsg('ehl_01_common_002');
								}
								;
							});

		};
		
		$scope.backMainList = function() {
			//loginEmpID, branchID, branchName, jobTitleName由SQL410.js傳入
			$scope.connector("set", "SQM410_inputVO", $scope.inputVO);
			$scope.connector("set", "SQM410_routeURL", 'assets/txn/SQM410/SQM410.html');
		}
		
		$scope.backCaseList = function() {
			//loginEmpID, branchID, branchName由SQL410.js傳入
//			$scope.empID = $scope.inputVO.empID;	//loginID
//			$scope.inputVO = {
//				empID 	  : $scope.inputVO.empID, 
//				branchID  : $scope.inputVO.branchID, 
//				branchName: $scope.inputVO.branchName
//			};
			$scope.connector("set", "SQM410_inputVO", $scope.inputVO);
			$scope.connector("set", "SQM410_routeURL", 'assets/txn/SQM410/SQM410_CASELIST.html');
		}
		
		//送出人選項
		$scope.getSendRole = function() {	
			
			$scope.sendRecv("SQM120","getSendRole","com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO",
					{'branch_nbr':$scope.inputVO.row.BRANCH_NBR,'loginID':$scope.inputVO.loginEmpID,'isFrom':'SQM410', 'qtnType':$scope.inputVO.qtnType}
					,function(tota,isError){
					if(tota[0].body.totalList.length == 0) {
						$scope.roleList = [];
						$scope.empData = [];
						$scope.outputVO={};
						$scope.showMsg("ehl_01_common_009");
						
		    			return;
		    		}
					$scope.roleList = tota[0].body.resultList;
					angular.forEach($scope.roleList, function(row, index, objs){
						if(index == 0){
							$scope.inputVO.owner_role_id = row.DATA;
						}
					});	
//					$scope.roleList.push(tota[0].body.resultList);
	    			$scope.empData = tota[0].body.totalList;
					$scope.outputVO = tota[0].body;		
					return;
	    	});
			
		};
		$scope.getSendRole();
		
		$scope.roleChange = function() {		
			var role_id = $scope.inputVO.owner_role_id;
			$scope.empList = []; 
			angular.forEach($scope.empData, function(row, index, objs){
				if(role_id == row.ROLE_ID){
					var emp = {'LABEL':row.EMP_NAME,'DATA':row.EMP_ID};	
				    $scope.empList.push(emp);
				    if(index == 0){
						$scope.inputVO.owner_emp_id = row.EMP_ID;
					}    
				}
			});	
		};
});