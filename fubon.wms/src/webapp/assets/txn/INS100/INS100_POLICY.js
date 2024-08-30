/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS100PolicyController',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, $q, validateService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS100PolicyController";
		
						
		$scope.initial = function() {
			$scope.inputVO = {					
					policyNbr: undefined			//保單號碼
			};
			$scope.inputVO.custId = $scope.custId;
			$scope.inputVO.policyNbr = null;
			$scope.inputVO.isFilter = false;
			$scope.sendRecv("INS100", "queryPolicyData", "com.systex.jbranch.app.server.fps.ins100.INS100InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.policyList = tota[0].body.policyList;
							$scope.outputVO = tota[0].body;
//							console.log("INS100 queryData:" + JSON.stringify($scope.policyList));
							if($scope.policyList.length == 0) {
	                			return;
	                		}							
						}
			});
	    };
	    $scope.initial();
	    
	    //點擊新增保單號碼 – 新增
	    $scope.addPolicyData = function() {
			if($scope.parameterTypeEditForm.$invalid) {
    		$scope.showErrorMsg("ehl_01_common_022");
    		return;
	    	} else {
	    		$scope.sendRecv("INS100", "addPolicyData", "com.systex.jbranch.app.server.fps.ins100.INS100InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								console.log(tota[0].body);
								if (tota[0].body.errorMsg != null) {
									$scope.showErrorMsg(tota[0].body.errorMsg);								
									return;
								} else if (tota[0].body == 1){
									$scope.showMsg("ehl_01_INS100_003");
								}else {
									$scope.initial();
									$scope.showMsg("ehl_01_common_025");	
									return;
								}
							}							
				});
	    	}			
	    };
	    
	    //點擊刪除 – 刪除
	    $scope.deletePolicyData = function(row) {
			var txtMsg = "";
			if ($scope.policyList.length == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("INS100", "deletePolicyData", "com.systex.jbranch.app.server.fps.ins100.INS100InputVO", {custId : $scope.inputVO.custId, policyNbr: row.POLICY_NBR},
						function(tota, isError) {
							if (!isError) {								
								$scope.initial();
								$scope.showMsg("ehl_01_common_003");
								return;
							}
				});
            });
		};
		
		$scope.regclick = function(){
			var isNoPass = false;
			var reg=/[@#\$%\^&\*]+/g ;
			var str = $scope.inputVO.policyNbr;
		    if(reg.test(str)){
		    	$scope.inputVO.policyNbr = null;
		    	isNoPass = true;
				$scope.showErrorMsg('請勿輸入特殊符號！');
		    }
		    if(str.match(/^[^ ].*\s+.*[^ ]$/)){
		    	$scope.inputVO.policyNbr = null;
		    	isNoPass = true;
		    	$scope.showErrorMsg('請勿輸入空白！');
		    }
		    return isNoPass;
		}
});