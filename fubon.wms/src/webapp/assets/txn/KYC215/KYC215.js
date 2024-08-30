/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC215Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC215Controller";
		
		getParameter.XML(['KYC.RISK_SCORE_LEVEL'], function(tota) {
			if(tota){
				$scope.mappingSet['KYC.RISK_SCORE_LEVEL'] = tota.data[tota.key.indexOf('KYC.RISK_SCORE_LEVEL')];
				$scope.c_column = $scope.mappingSet['KYC.RISK_SCORE_LEVEL'].find(DATA => DATA = 'C');
				$scope.w_column = $scope.mappingSet['KYC.RISK_SCORE_LEVEL'].find(DATA => DATA = 'W');

				$scope.c = parseInt($scope.c_column.LABEL) + 1;   // for column
				$scope.w = $scope.w_column.LABEL;                 // for row
				if(!$scope.updateRisk){
					$scope.inputVO.C_VAL[0] = -999;
					$scope.inputVO.C_VAL[$scope.c_column.LABEL] = 999;
					
					$scope.inputVO.W_VAL[0] = -999;
					$scope.inputVO.W_VAL[$scope.w_column.LABEL] = 999;
				}
			}
		});
				
		$scope.init = function(){
			if($scope.updateRisk){
				var temp_risk = $scope.connector('get','KYC215_addRISK');
				$scope.inputVO = {
						RS_VERSION :$scope.RS_VERSION,
						C_VAL : [],
						W_VAL : [],
						CUST_RL_ID : []
				}
				if(temp_risk){
					$scope.inputVO.C_VAL = temp_risk.C_VAL;
					$scope.inputVO.W_VAL = temp_risk.W_VAL;
					$scope.inputVO.CUST_RL_ID = temp_risk.CUST_RL_ID;
					$scope.updateRisk = false;
				}else{
					$scope.sendRecv("KYC215","queryData","com.systex.jbranch.app.server.fps.kyc215.KYC215InputVO",$scope.inputVO,
						function(tota,isError){
        					if(!isError){
        						
        						angular.forEach(tota[0].body.WList,function(row,index){
        							$scope.inputVO.W_VAL[index] = row.W_VAL;
        						});
        						
        						angular.forEach(tota[0].body.CList,function(row,index){
        							$scope.inputVO.C_VAL[index] = row.C_VAL;
        						});
        						
        						angular.forEach(tota[0].body.ResultList,function(row){
        							$scope.inputVO.CUST_RL_ID[row.W_LEVEL] = {};
        						});
        						angular.forEach(tota[0].body.ResultList,function(row){
        							$scope.inputVO.CUST_RL_ID[row.W_LEVEL][row.C_LEVEL] = row.CUST_RL_ID;
        						});							
        					}
					});
				}
			}else{
				$scope.inputVO = {
						RS_VERSION : $scope.RS_VERSION,
						C_VAL : [],
						W_VAL : [],
						CUST_RL_ID : []
				}
			}
				
		}
		$scope.init();
		
		$scope.checkboundaries = function(index,type){
			if(type == 'W'){
				if($scope.inputVO.W_VAL[index-1] != '' && $scope.inputVO.W_VAL[index] != ''){
					if(parseInt($scope.inputVO.W_VAL[index-1]) >= parseInt($scope.inputVO.W_VAL[index])){
						$scope.showErrorMsg("區間設定有誤，請重新檢查");
						$scope.inputVO.W_VAL[index] = '';
					}
				}
				if($scope.inputVO.W_VAL[index+1] != '' && $scope.inputVO.W_VAL[index] != ''){
					if(parseInt($scope.inputVO.W_VAL[index+1]) <= parseInt($scope.inputVO.W_VAL[index])){
						$scope.showErrorMsg("區間設定有誤，請重新檢查");
						$scope.inputVO.W_VAL[index] = '';
					}
				}			
				
			}else{
				
				if($scope.inputVO.C_VAL[index-1] != '' && $scope.inputVO.C_VAL[index] != ''){
					if(parseInt($scope.inputVO.C_VAL[index-1]) >= parseInt($scope.inputVO.C_VAL[index])){
						$scope.showErrorMsg("區間設定有誤，請重新檢查");
						$scope.inputVO.C_VAL[index] = '';
					}
				}
				if($scope.inputVO.C_VAL[index+1] != '' && $scope.inputVO.C_VAL[index] != ''){
					if(parseInt($scope.inputVO.C_VAL[index+1]) <= parseInt($scope.inputVO.C_VAL[index])){
						$scope.showErrorMsg("區間設定有誤，請重新檢查");
						$scope.inputVO.C_VAL[index] = '';
					}
				}
			}
			
		}

		$scope.saveData = function(){
			if($scope.inputVO.C_VAL.includes(undefined)){
				$scope.showErrorMsg('請輸入風險承受能力分數');
				return;
			}
			if($scope.inputVO.W_VAL.includes(undefined)){
				$scope.showErrorMsg('請輸入風險偏好分數');
				return;
			}
			if($scope.risk.$invalid){
        		$scope.showErrorMsg('風險級距輸入有誤，請重新檢查');
        		return;
			}
			$scope.closeThisDialog($scope.inputVO.RS_VERSION);
			$scope.connector('set','KYC215_addRISK',$scope.inputVO);
		}
		
	
});