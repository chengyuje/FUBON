/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD234_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD234_EDITController";
		
		$scope.init = function() {
			if($scope.row) {
        		$scope.inputVO = {
        			prdId: $scope.row.PRD_ID,
        			fundName: $scope.row.FUND_CNAME,
        			fileName: '',
        			fileRealName: ''
        		};
        		$scope.ispdf = false;
        	}
		};
		$scope.init();
		
		$scope.uploadFinshed = function(name, rname) {
			
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        	var pdf = $scope.inputVO.fileRealName.split(".");
        	$scope.inputVO.prdNo = pdf[0];
        	if(pdf[pdf.length-1] != "pdf"){
        		$scope.showErrorMsg("只能上傳pdf檔");
        		$scope.ispdf = false;
        		return;
        	}else{
        		$scope.ispdf = true;
        	}
        	debugger;
        	if(pdf[0] != $scope.inputVO.prdId){
        		$scope.showErrorMsg("上傳檔案與基金代號不符");
        		$scope.ispdf = false;
        		return;
        	}
        };
        
		$scope.save = function() {
			$scope.sendRecv("PRD234", "upload", "com.systex.jbranch.app.server.fps.prd234.PRD234InputVO", $scope.inputVO,
					function(tota, isError) {
        				if(!isError){
        					$scope.showSuccessMsg('ehl_01_common_004'); 	
							$scope.closeThisDialog('successful');
        				}
		            	
			});
		};
		
		
});