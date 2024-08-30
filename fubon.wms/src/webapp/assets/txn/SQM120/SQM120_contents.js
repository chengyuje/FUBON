/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM120_contentsController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "SQM120_contentsController";
//	$controller('PMSRegionController', {$scope: $scope});	
	$scope.inputVO.seq = $scope.row.SEQ;
	$scope.inputVO.qtnType = $scope.row.QTN_TYPE;
	$scope.inputVO.data_date = $scope.row.DATA_DATE;
	$scope.inputVO.cust_id = $scope.row.CUST_ID;
	$scope.inputVO.trade_date = $scope.row.TRADE_DATE;
	$scope.inputVO.end_date = $scope.row.END_DATE;
	$scope.inputVO.cust_id = $scope.row.CUST_ID;
	$scope.inputVO.cust_name = $scope.row.CUST_NAME;
	$scope.inputVO.branch_nbr = $scope.row.BRANCH_NBR;
	$scope.BRANCH_NAME = $scope.row.BRANCH_NAME;
	$scope.inputVO.emp_name = $scope.row.EMP_NAME;
	$scope.inputVO.emp_id = $scope.row.EMP_ID;
	$scope.inputVO.case_no = $scope.row.CASE_NO;
	
	if($scope.row.RESP_NOTE != null){
		$scope.inputVO.resp_note = $scope.row.RESP_NOTE;
	}else{
		$scope.inputVO.resp_note = "無質化意見。";
	}
	
	// XML
	getParameter.XML(["SQM.ANS_TYPE", "SQM.QTN_TYPE", "SQM.ANS_TYPE_PUSH"], function(totas) {
		if (totas) {
			$scope.ANS_TYPE_PUSH = totas.data[totas.key.indexOf('SQM.ANS_TYPE_PUSH')];
			$scope.ANS_TYPE = totas.data[totas.key.indexOf('SQM.ANS_TYPE')];
			$scope.QTN_LIST = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
		}
	});
	
	//$scope.qtnType == '5' 為簡訊問題拿自己的
	if('WMS05' != $scope.inputVO.qtnType){
		
		$scope.sendRecv("SQM120", "getQuestion", "com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO", $scope.inputVO,
				function(totas, isError) {
	            	if (!isError) {
	            		$scope.column = totas[0].body.totalList;
	            		angular.forEach($scope.column, function(row, index) {
							if(index == 0) {
								var test = row.ANSWER.indexOf("{")==-1?row.ANSWER.length:row.ANSWER.indexOf("{");
								row.ANSWER = row.ANSWER.substring(0, test);
							}
						});
	            		$scope.outputVO = totas[0].body;
	            		$scope.totalList = _.orderBy(totas[0].body.totalList, ['QST_ORDER']);
	            	};
				}
		);
	}else{
		$scope.totalData = [];
		
		var QST_LIST = [ {
			'QST_DESC' : '客戶手機' ,'ANSWER' : $scope.row.MOBILE_NO
		}, {
			'QST_DESC' : '簡訊內容' ,'ANSWER' : $scope.row.QUESTION_DESC
		}, {
			'QST_DESC' : '客戶回覆' ,'ANSWER' : $scope.row.ANSWER
		}
		];
	
		$scope.column = QST_LIST;
	}
	
	//資料儲存
	$scope.removeData = function() {	
		$scope.sendRecv("SQM120","removeData","com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO", $scope.inputVO,
    			function(tota,isError){
    			if(isError){
    				
    				$scope.showErrorMsg(tota[0].body.msgData);
    			}
               	if (tota.length > 0) {
	        		$scope.showSuccessMsg('ehl_01_common_002');
	        		$scope.closeThisDialog('successful');

            	};
    	});
	};

});