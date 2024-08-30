/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM110_detailController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $http, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "SQM110_detailController";
	
	$scope.pri_id = projInfoService.getPriID();
	
	getParameter.XML(["SQM.QTN_TYPE","FUBONSYS.HEADMGR_ROLE", "SQM.ANS_TYPE", "SQM.ANS_TYPE_PUSH"], function(totas) {
		if (totas) {
			$scope.ANS_TYPE_PUSH = totas.data[totas.key.indexOf('SQM.ANS_TYPE_PUSH')];
			$scope.ANS_TYPE = totas.data[totas.key.indexOf('SQM.ANS_TYPE')];
			$scope.QTN_LIST = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
			$scope.HEADMGR_ROLE = totas.data[totas.key.indexOf('FUBONSYS.HEADMGR_ROLE')];
			$scope.role_id = projInfoService.getRoleID();
			$scope.headmgr_flag = false;//判斷是否為總行人員flag
			angular.forEach($scope.HEADMGR_ROLE, function(row, index, objs){
				if(row.DATA == $scope.role_id ){
					$scope.headmgr_flag = true;
					return;	
				}
			});
		}
	});	
	
	$scope.$parent.userID = projInfoService.getUserID();
	$scope.$parent.roleID = projInfoService.getRoleID();
	$scope.inputVO.seq = $scope.row.SEQ;
	$scope.inputVO.qtnType = $scope.row.QTN_TYPE;
	$scope.inputVO.data_date = $scope.row.DATA_DATE;
	$scope.inputVO.trade_date = $scope.row.TRADE_DATE;
	$scope.inputVO.end_date = $scope.row.END_DATE;
	$scope.inputVO.cust_id = $scope.row.CUST_ID;
	$scope.inputVO.cust_name = $scope.row.CUST_NAME;
	$scope.inputVO.resp_note = $scope.row.RESP_NOTE == null ? '無質化意見。' : $scope.row.RESP_NOTE;
	$scope.inputVO.branch_nbr = $scope.row.BRANCH_NBR;
	$scope.BRANCH_NAME = $scope.row.BRANCH_NAME;
	$scope.inputVO.emp_name = $scope.row.EMP_NAME;
	$scope.inputVO.emp_id = $scope.row.EMP_ID;
	$scope.inputVO.case_no = $scope.row.CASE_NO;
	$scope.inputVO.last_visit_date = $scope.row.LAST_VISIT_DATE;
	$scope.inputVO.con_degree = $scope.row.CON_DEGREE == null ? '新開戶' : $scope.row.CON_DEGREE;
	$scope.inputVO.frq_day = $scope.row.FRQ_DAY;
	
	// 取明細檔資料
	
	$scope.getDtlData = function() {
		$scope.sendRecv( "SQM110", "getDtlData", "com.systex.jbranch.app.server.fps.sqm110.SQM110DetailInputVO", {'case_no' : $scope.inputVO.case_no}, function(totas, isError) {
			if (!isError) {
				$scope.outputVO = totas[0].body;
				$scope.dtlDataList = totas[0].body.resultList;
				angular.forEach($scope.dtlDataList, function(row, index, objs) {
					$scope.inputVO.brh_desc = row.BRH_DESC;
					$scope.inputVO.waiting_time = row.WAITING_TIME;
					$scope.inputVO.working_time = row.WORKING_TIME;
					$scope.inputVO.cur_job_y = row.CUR_JOB_Y;
					$scope.inputVO.cur_job_m = row.CUR_JOB_M;
					$scope.inputVO.sup_emp_name = row.SUP_EMP_NAME;
					$scope.inputVO.sup_emp_id = row.SUP_EMP_ID;
					$scope.inputVO.sup_cur_job = row.SUP_CUR_JOB;
					$scope.inputVO.improve_desc = row.IMPROVE_DESC;
					$scope.inputVO.op_sup_remark = row.OP_SUP_REMARK;
					$scope.inputVO.rc_vice_sup_remark = row.RC_VICE_SUP_REMARK;
//					$scope.inputVO.last_visit_date = row.LAST_VISIT_DATE;
//					$scope.inputVO.con_degree = row.CON_DEGREE;
//					$scope.inputVO.frq_day = row.FRQ_DAY;
					$scope.inputVO.deduction_initial = row.DEDUCTION_INITIAL;
					$scope.inputVO.rc_sup_remark = row.RC_SUP_REMARK;
					$scope.inputVO.headmgr_remark = row.HEADMGR_REMARK;
					$scope.inputVO.case_flow = row.CASE_FLOW; // 是否為已進入案件流程
					$scope.inputVO.creator = row.CREATOR;
					$scope.merge_rc_sup_remark = $scope.inputVO.rc_vice_sup_remark != null ? "處副主管: "+$scope.inputVO.rc_vice_sup_remark : null ;
					$scope.merge_rc_sup_remark = $scope.inputVO.rc_sup_remark != null ? $scope.merge_rc_sup_remark != null ? $scope.merge_rc_sup_remark+'\n處主管: '+$scope.inputVO.rc_sup_remark : '處主管: '+$scope.inputVO.rc_sup_remark : $scope.merge_rc_sup_remark;
				});
				
			}
			;
		});
	}

	if ($scope.inputVO.case_no != null) {
		$scope.getDtlData();
	} 
	
	$scope.getSup_Job_title_name = function(sup_emp_id) {
		$scope.sendRecv("SQM110", "getJob_title_name", "com.systex.jbranch.app.server.fps.sqm110.SQM110DetailInputVO", {'emp_id' : sup_emp_id, 'trade_date' : $scope.row.TRADE_DATE}, function(totas, isError) {
			if (!isError) {
				$scope.outputVO = totas[0].body;
					$scope.empList = totas[0].body.resultList;
					angular.forEach($scope.empList, function(row, index, objs) {
						$scope.inputVO.sup_emp_id = row.EMP_ID;
						$scope.inputVO.sup_emp_name = row.EMP_NAME;
						$scope.inputVO.sup_cur_job = row.CUR_JOB;
					});
					
			}
			;
		});
	}
	if ($scope.inputVO.sup_emp_id != null) {
		$scope.getSup_Job_title_name($scope.inputVO.sup_emp_id);
	}
	
	$scope.getJob_title_name = function(emp_id) {
		$scope.sendRecv("SQM110", "getJob_title_name", "com.systex.jbranch.app.server.fps.sqm110.SQM110DetailInputVO", {'emp_id' : emp_id, 'trade_date' : $scope.row.TRADE_DATE}, function(totas, isError) {
			if (!isError) {
				$scope.outputVO = totas[0].body;
				$scope.empList = totas[0].body.resultList;
				angular.forEach($scope.empList, function(row, index, objs) {
					$scope.inputVO.emp_id = row.EMP_ID;
					$scope.inputVO.emp_name = row.EMP_NAME;
					$scope.inputVO.cur_job = row.CUR_JOB;
				});
				
			}
			;
		});
	}
	
	if ($scope.row.EMP_ID != null) {
		$scope.getJob_title_name($scope.row.EMP_ID);
	}
	
	// $scope.qtnType == '5' 為簡訊問題拿自己的
	if ($scope.inputVO.qtnType != 'WMS05') {
		
		$scope.sendRecv("SQM110", "getQuestion", "com.systex.jbranch.app.server.fps.sqm110.SQM110DetailInputVO", {'qtnType':$scope.row.QTN_TYPE, 'data_date':$scope.row.DATA_DATE, 'cust_id':$scope.row.CUST_ID}, function(totas, isError) {
			if (!isError) {
				$scope.column = totas[0].body.totalList;
				angular.forEach($scope.column, function(row, index) {
					if(index == 0) {
						var test = row.ANSWER.indexOf("{")==-1?row.ANSWER.length:row.ANSWER.indexOf("{");
						row.ANSWER = row.ANSWER.substring(0, test);
					}
				});
				$scope.outputVO = totas[0].body;
				$scope.totalList = _.orderBy(totas[0].body.totalList,[ 'QST_ORDER' ]);
//				$scope.data = totas[0].body.resultList;
//				$scope.resultList = totas[0].body.resultList;
			}
			;
		});
	} else {
		$scope.totalData = [];
		var QST_LIST = [ {'QST_DESC' : '客戶手機' ,'ANSWER' : $scope.row.MOBILE_NO}, 
		                 {'QST_DESC' : '簡訊內容' ,'ANSWER' : $scope.row.QUESTION_DESC}, 
		                 {'QST_DESC' : '客戶回覆' ,'ANSWER' : $scope.row.ANSWER}];
		
		$scope.column = QST_LIST;
	}	
	
	$scope.printPdf = function() {
		$scope.inputVO.checkList=[{"CASE_NO": $scope.inputVO.case_no}];
		$scope.sendRecv("SQM110", "download", "com.systex.jbranch.app.server.fps.sqm110.SQM110InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.showSuccessMsg('下載成功');
				$scope.closeThisDialog('successful');
			}						
		});
		
	};
	
});
	
				