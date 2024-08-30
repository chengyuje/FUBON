/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC216Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC216Controller";
		
		$scope.init = function(){
			
			if($scope.updateRisk){
				var temp_risk = $scope.connector('get','KYC216_addRISK');
				$scope.inputVO = {
						RLR_VERSION : $scope.RLR_VERSION,
						LRATE_RISK_LEVEL : [],
						LRATE_DEL_CUST_RL_ID : []
				}
				if(temp_risk){
					$scope.inputVO.LRATE_RISK_LEVEL = temp_risk.LRATE_RISK_LEVEL;
					$scope.updateRisk = false;
				}else{
					$scope.sendRecv("KYC216","queryData","com.systex.jbranch.app.server.fps.kyc216.KYC216InputVO",
							$scope.inputVO,function(tota,isError){
        					if(!isError){
        						$scope.inputVO.LRATE_RISK_LEVEL = tota[0].body.LRateList;
        					}
					});
				}
			}else{
				$scope.inputVO = {
						RLR_VERSION : $scope.RLR_VERSION,
						LRATE_RISK_LEVEL : [],
						LRATE_DEL_CUST_RL_ID : []
				}
			}
				
		}
		$scope.init();
		
		
		$scope.addRow = function(){
			$scope.inputVO.LRATE_RISK_LEVEL.push({});
			angular.forEach($scope.inputVO.LRATE_RISK_LEVEL,function(row,index){
				row.Display_order = index + 1;
			});
		}
		
		$scope.deleteRow = function(index,row){
			if(row.LRATE_CUST_RL_ID){
				$scope.inputVO.LRATE_DEL_CUST_RL_ID.push(row.LRATE_CUST_RL_ID);
			}else if(row.LCUST_RL_ID){
				$scope.inputVO.LRATE_DEL_CUST_RL_ID.push(row.LCUST_RL_ID);
			}
				
			$scope.inputVO.LRATE_RISK_LEVEL.splice(index,1);
			angular.forEach($scope.inputVO.LRATE_RISK_LEVEL,function(row,index){
				row.Display_order = index +1;
			});
		}
		
		$scope.saveData = function(){
			
			angular.forEach($scope.inputVO.LRATE_RISK_LEVEL,function(row,index){
				
				if(row.LCUST_RL_ID != null){
					row.LCUST_RL_ID = row.LCUST_RL_ID.toUpperCase();
				}
				if(row.LRL_NAME == undefined  && row.LRATE_RL_NAME == undefined){
					$scope.showErrorMsg('請輸入級距名稱');
					return;
				}
				if(row.LRL_UP_RATE == undefined  && row.LRATE_RL_UP_RATE == undefined){
					$scope.showErrorMsg('請輸入級距名稱');
					return;
				}
			});
			
			if($scope.inputVO.LRATE_RISK_LEVEL.length<1){
				//為新增風險級距
				$scope.showErrorMsg('ehl_01_common_022');
			}else{
				//檢查欄位
				if($scope.risk.$invalid){
            		$scope.showErrorMsg('設定有誤，請重新檢查');
				}else{
            			$scope.closeThisDialog($scope.inputVO.RLR_VERSION);
						$scope.connector('set','KYC216_addRISK',$scope.inputVO);
				}
			}

		}
		
	}
);