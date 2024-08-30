'use strict';
eSoafApp.controller('SQM320_sendController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $confirm) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "SQM320_sendController";

	$scope.review = function() {		

		$scope.REVIEWER_LIST = []; 
		$scope.inputVO = {
			emp_id : $scope.emp_id,
			yearQtr : $scope.yearQtr,
			reviewerList : []
		}
		
		angular.forEach($scope.reviewerList, function(row, index, objs){
			$scope.REVIEWER_LIST.push({'LABEL':row.EMP_ID + ' - ' + row.EMP_NAME + ' - ' + row.ROLE_ID, 'DATA':row});
		});	
	};
	
	$scope.review();
    
	//送出
	$scope.send = function() {	
		$scope.inputVO.reviewerList[0] = $scope.inputVO.REVIEWER_LIST;
		
		if($scope.parameterTypeEditForm.$invalid){
    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
    		return;
    	}
		
		
		
		$scope.sendRecv("SQM320", "saveReview", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota,isError){
           	if (!isError) {
           		$scope.showSuccessMsg('設定成功');
        		$scope.closeThisDialog('successful');
        	};
    	});
		
	};

});