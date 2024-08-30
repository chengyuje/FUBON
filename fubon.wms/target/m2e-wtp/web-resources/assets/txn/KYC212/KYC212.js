/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC212Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC212Controller";
		
		$scope.init = function(){
			$scope.inputVO.Weights = $scope.row.answer;
			$scope.connector('set','KYC212_temp',$scope.inputVO.Weights);
//				$scope.inputVO = {
//						EXAM_VERSION : $scope.row.EXAM_VERSION,
//						QUESTION_VERSION : $scope.row.QUESTION_VERSION
//				}
//				$scope.sendRecv("KYC212","InitialWeight","com.systex.jbranch.app.server.fps.kyc212.KYC212InputVO",
//						$scope.inputVO,function(tota,isError){
//						if(!isError){
//							$scope.inputVO.Weights = tota[0].body.Weights;
//						}
//				});
		}
		$scope.init();
		
		
		$scope.saveData = function(){
//			alert(JSON.stringify($scope.inputVO.Weights))
			if($scope.weight.$invalid){
				$scope.showErrorMsg('ehl_01_common_022')
			}else{
				$scope.closeThisDialog($scope.inputVO.Weights);
			}
//			if($scope.copy){
//				$scope.connector('set','KYC212',$scope.inputVO.Weights);
//				$scope.closeThisDialog('successful');
//			}else{
//				$scope.sendRecv("KYC212","SaveWeight","com.systex.jbranch.app.server.fps.kyc212.KYC212InputVO",
//						$scope.inputVO,function(tota,isError){
//					if (tota.length > 0) {
//	            		$scope.showSuccessMsg('ehl_01_common_001');
//	        			$scope.closeThisDialog('successful');
//					}
//				});	
//			}
		}
		
		
		
		
	}
);