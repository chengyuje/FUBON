/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM120_returnsController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "SQM120_returnsController";
	    
	//送出
	$scope.returns = function() {	
		if($scope.parameterTypeEditForm.$invalid){
    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
    		return;
    	}
		$scope.sendRecv("SQM120","returns","com.systex.jbranch.app.server.fps.sqm120.SQM120EditInputVO",{'returns_type':$scope.returns_type,'qtnType':$scope.qtnType, 'seq':$scope.seq , 'cust_id':$scope.cust_id, 'data_date':$scope.data_date , 'returns_remark':$scope.inputVO.returns_remark}
				,function(tota,isError){
    			if(isError){
    				$scope.showErrorMsg(tota[0].body.msgData);
    			}
               	if (tota.length > 0) {
	        		$scope.showSuccessMsg('退件成功');
	        		$scope.closeThisDialog('successful');
            	};
    	});
		
	};

});