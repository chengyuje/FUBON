/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM612_assets_editController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM612_assets_editController";
       
    	/****初始化資料*****/
        $scope.init = function(){
        	
            $scope.row_assets = $scope.row_assets || {};
            
        	$scope.inputVO = {
        			cust_id: $scope.row_assets.CUST_ID,
        			assets_id: $scope.row_assets.ASSETS_ID,
        			assets_name: $scope.row_assets.ASSETS_NAME,
        			assets_amt: $scope.row_assets.ASSETS_AMT,
        			assets_note: $scope.row_assets.ASSETS_NOTE
            };
        	console.log('inputVO='+JSON.stringify($scope.inputVO));
        };
        $scope.init();
		
		
		//修改
		$scope.assets_editconfirm = function(){
        	if($scope.parameterTypeEditForm.$invalid){
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	$scope.sendRecv("CRM612", "assets_editconfirm", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO,
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('修改成功');
	                	$scope.closeThisDialog('successful');
	                };
	            }
        	);
        }
		
		
		
		
	}
);