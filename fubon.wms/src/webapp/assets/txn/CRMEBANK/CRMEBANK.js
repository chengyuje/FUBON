/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRMEBankController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRMEBankController";
		
		$scope.init = function(){
			$scope.inputVO = {
					PARAM_CODE:'',
					PARAM_DESC:'',
					PARAM_NAME:'',
					PARAM_NAME_EDIT:'',
					PARAM_ORDER:'',
					PARAM_STATUS:'',
					PARAM_TYPE:'',
					VERSION:'',
					CREATETIME:'',
					CREATOR:'',
					LASTUPDATE:'',
					MODIFIER:''
			};
			$scope.resultList = [];
		};
		$scope.init();
		
		$scope.query = function(){
			$scope.sendRecv("CRMEBANK", "query", "com.systex.jbranch.app.server.fps.crmebank.CRMEBANKInputVO", $scope.inputVO , function(tota, isError) {
				if (!isError) {
					if(tota[0].body.welcomeList.length == 0) {
						$scope.resultList = [];
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}														
					$scope.resultList = tota[0].body.welcomeList;
					$scope.outputVO = tota[0].body;							
					return;
				}
			});
		};
		$scope.query();
		
		$scope.insert = function(){
			var dialog = ngDialog.open({
        		template: 'assets/txn/CRMEBANK/CRMEBANK_INSERT.html',
        		className: 'CRMEBANK_INSERT',
        		controller: ['$scope',function($scope){
        			$scope.title_type='Add';
        		}]
        	});
        	dialog.closePromise.then(function(data){
        		$scope.query();
        	});
		};
		
		$scope.updata = function(row){
			$scope.inputVO = row;
			if(($scope.inputVO.PARAM_CODE == null || $scope.inputVO.PARAM_CODE == '') || 
			   ($scope.inputVO.PARAM_DESC == null || $scope.inputVO.PARAM_DESC == '') || 
			   ($scope.inputVO.PARAM_NAME == null || $scope.inputVO.PARAM_NAME == '')) {
		          $scope.showErrorMsgInDialog('欄位檢核錯誤:請輸入必要欄位');
		          return;
		    }
			$scope.sendRecv("CRMEBANK", "updata", "com.systex.jbranch.app.server.fps.crmebank.CRMEBANKInputVO", $scope.inputVO , function(tota, isError) {
            	if (tota.length > 0) {
            		$scope.showSuccessMsg('ehl_01_common_002');
            		$scope.query();
            	};
			});
		};
		
		$scope.delete = function(row){
			$scope.inputVO = row;
			var txtMsg = $filter('i18n')('ehl_02_common_001');
			$confirm({text: txtMsg},{size: 'sm'}).then(function(){
				$scope.sendRecv("CRMEBANK", "delete", "com.systex.jbranch.app.server.fps.crmebank.CRMEBANKInputVO", $scope.inputVO , function(tota, isError) {
				if (tota.length > 0) {
            		$scope.showSuccessMsg('ehl_01_common_003');
            			$scope.query();
            		};
				});
			});	
		};
});
