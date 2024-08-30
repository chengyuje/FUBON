/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC213Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC213Controller";
		
		$scope.init = function(){
				if($scope.updateRisk){
					var temp_risk = $scope.connector('get','KYC213_addRISK');
					$scope.inputVO = {
							RL_VERSION : $scope.RL_VERSION,
							RISK_LEVEL : [],
							DEL_CUST_RL_ID : []
					}
					if(temp_risk){
						$scope.inputVO.RISK_LEVEL = temp_risk.RISK_LEVEL;
//						$scope.connector('set','KYC213_addRISK','');
						$scope.updateRisk = false;
					}else{
						$scope.sendRecv("KYC213","queryData","com.systex.jbranch.app.server.fps.kyc213.KYC213InputVO",
								$scope.inputVO,function(tota,isError){
	        					if(!isError){
	        						$scope.inputVO.RISK_LEVEL = tota[0].body.RiskLevelList;
	        					}
						});
					}
				}else{
					$scope.inputVO = {
							RL_VERSION : $scope.RL_VERSION,
							RISK_LEVEL : [],
							DEL_CUST_RL_ID : []
					}
				}
				
		}
		$scope.init();
		
		
		$scope.addRow = function(){
			$scope.inputVO.RISK_LEVEL.push({});
			angular.forEach($scope.inputVO.RISK_LEVEL,function(row,index){
				row.Display_order = index + 1;
			});
		}
		
		$scope.deleteRow = function(index,row){
			if(row.CUST_RL_ID){
				$scope.inputVO.DEL_CUST_RL_ID.push(row.CUST_RL_ID);
			}
			$scope.inputVO.RISK_LEVEL.splice(index,1);
			angular.forEach($scope.inputVO.RISK_LEVEL,function(row,index){
				row.Display_order = index +1;
			});
		}
		
		$scope.saveData = function(){
			if($scope.inputVO.RISK_LEVEL.length<1){
				//為新增風險級距
				$scope.showErrorMsg('ehl_01_common_022');
			}else{
				//檢查欄位
				if($scope.risk.$invalid){
            		$scope.showErrorMsg('ehl_01_common_022');
				}else{
            			$scope.closeThisDialog($scope.inputVO.RL_VERSION);
						$scope.connector('set','KYC213_addRISK',$scope.inputVO);
				}
			}

		}
		
		
		
		
	}
);