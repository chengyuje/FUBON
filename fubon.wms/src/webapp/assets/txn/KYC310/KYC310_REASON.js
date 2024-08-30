/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC310_REASONController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC310_REASONController";

		$scope.init = function(){
			 $scope.inputVO.cust_school = '';
			 $scope.inputVO.edu_choose = '';
			 $scope.inputVO.edu_change = '';
			 $scope.inputVO.health_change  ='';
			 $scope.inputVO.sameEmail_choose = '';
			 $scope.inputVO.sameEmail_reason = '';
			 debugger;
		};
		
        $scope.init();
        
        $scope.confirm = function(){
        	var passData;
        	//18歲(含)以上，學校由國中以下異動為高中職以上
        	 if($scope.data.index == 1){
        		//沒有選擇原因
        		if($scope.inputVO.edu_choose == ''){
        			$scope.showErrorMsg('ehl_01_KYC310_038');
        		}
        		 else{
        		 passData = {
        				 edu_choose : $scope.inputVO.edu_choose
        		 };
        		 $scope.closeThisDialog(passData);
        		}
             }
        	 //重大傷病證明從有到無	
        	 else if($scope.data.index == 3){
        		 //沒有選擇原因
         		if($scope.inputVO.health_change == ''){
         			$scope.showErrorMsg('ehl_01_KYC310_038');
         		}
         		else{
        		 passData = {
        			 health_change : $scope.inputVO.health_change
        		 };
        		 $scope.closeThisDialog(passData);
        		}             	
             }
        	 //#0437「客戶已填過「衍生性金融商品客戶適性評估問卷」，請檢視客戶投資經驗填答內容與本次填答是否一致。」
        	 else if($scope.data.index == 5){
        		 $scope.closeThisDialog(passData);
        	 }
        	 else if($scope.data.index == 7){
        		 //沒有選擇原因
         		if($scope.inputVO.sameEmail_choose == '' || $scope.inputVO.sameEmail_choose == undefined){
         			$scope.showErrorMsg('ehl_01_KYC310_038');
         			return;
         		}
         		if($scope.inputVO.sameEmail_choose == 5 && ($scope.inputVO.sameEmail_reason == '' || $scope.inputVO.sameEmail_reason == undefined)){
         			$scope.showErrorMsg('ehl_01_KYC310_038');
         			return;
         		}
        		 passData = {
        				 sameEmail_choose : $scope.inputVO.sameEmail_choose,
        				 sameEmail_reason : $scope.inputVO.sameEmail_reason
        		 };
        		 $scope.closeThisDialog(passData);
        	           	
             }
        }		
});
