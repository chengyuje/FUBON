/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR011_EDITController',
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR011_EDITController";
        
        $scope.init = function(){
        	if($scope.row){
        		$scope.isUpdate = true;
        	}
            $scope.row = $scope.row || {};
            $scope.allowFuns = $scope.allowFuns || {};
            $scope.chkFuns = false;
            
        	$scope.inputVO = {
    			txnCode: $scope.row.TXNCODE,
    			txnName: $scope.row.TXNNAME,
    			sysType: $scope.row.SYSTYPE,
    			jrnType: $scope.row.JRNTYPE,
    			secApply: $scope.apply,
    			moduleId: $scope.row.MODULEID,
    			chkMaintenance: false,
    			chkQuery: false,
    			chkExport: false,
    			chkPrint: false,
    			chkWatermark: false,
    			chkSecurity: false,
    			chkConfirm: false,
    			chkMobile: false,
    			chkScreen: false
            };
        	for (var i = 0; i < $scope.allowFuns.length; i++) {
				switch($scope.allowFuns[i].FUNCTIONID) {
					case 'maintenance':
						$scope.inputVO.chkMaintenance = true;
						break;
					case 'query':
						$scope.inputVO.chkQuery = true;
						break;
					case 'export':
						$scope.inputVO.chkExport = true;
						break;
					case 'print':
						$scope.inputVO.chkPrint = true;
						break;
					case 'watermark':
						$scope.inputVO.chkWatermark = true;
						break;
					case 'security':
						$scope.inputVO.chkSecurity = true;
						break;
					case 'confirm':
						$scope.inputVO.chkConfirm = true;
						break;
					case 'mobile':
						$scope.inputVO.chkMobile = true;
						break;
					case 'screen':
						$scope.inputVO.chkScreen = true;
						break;
				}
			}
        	console.log('inputVO='+JSON.stringify($scope.inputVO));
        };
        
        $scope.init();
        
        $scope.save = function(){
        	$scope.chkFuns = false;
        	if(!($scope.inputVO.chkMaintenance || $scope.inputVO.chkQuery || $scope.inputVO.chkExport || $scope.inputVO.chkPrint || $scope.inputVO.chkWatermark
        			|| $scope.inputVO.chkSecurity || $scope.inputVO.chkConfirm || $scope.inputVO.chkMobile || $scope.inputVO.chkScreen)){
        		$scope.chkFuns = true;
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if($scope.parameterTypeEditForm.$invalid){
        		console.log('form=' + JSON.stringify($scope.parameterTypeEditForm));
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	console.log('save='+$scope.row.TXNCODE);
        	$scope.inputVO.operType="Create";
        	if($scope.row.TXNCODE != undefined){
        		$scope.inputVO.operType="Update";
        	}
        	$scope.sendRecv("CMMGR011", "operation", "com.systex.jbranch.app.server.fps.cmmgr011.CMMGR011InputVO", $scope.inputVO,
    			function(totas, isError) {
	                if (isError) {
	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	                    return;
	                }
	                 if (totas.length > 0) {
	                	 $scope.showMsg('儲存成功');
	                	 $scope.closeThisDialog('successful');
	                 };
	            }
        	);
        };
        
        $scope.del = function(){
        	if ($scope.row.TXNCODE == undefined){
        		$scope.showErrorMsgInDialog('無法刪除');
        		return;
        	}
        	$scope.inputVO.operType="Delete";
    		$scope.sendRecv("CMMGR011", "operation", "com.systex.jbranch.app.server.fps.cmmgr011.CMMGR011InputVO", $scope.inputVO,
	    		function(totas, isError) {
    				if (isError) {
		               	$scope.showErrorMsgInDialog(totas.body.msgData);
		            	return;
    				}
    				if (totas.length > 0) {
    					$scope.showMsg('刪除成功');
    					$scope.closeThisDialog('delsuccessful');
    				};
    			}
	        );
        };
        
    }
);
