/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC520_CheckController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC520_CheckController";
		
		$scope.checkParamList = [];	//問卷內容資料
		$scope.rlVersionList = [];	//風險級距資料
		$scope.flwList = [];		//說明備註
		
		  //問卷類別
		getParameter.XML(["KYC.PRO_TYPE"],function(totas){
			if(totas){
				//問卷類別
				$scope.mappingSet['KYC.PRO_TYPE'] = totas.data[0];
			}
		});
		
		//init
		$scope.init = function(){
			var date = new Date($scope.row.ACTIVE_DATE);
			var today = new Date().setHours(0,0,0,0);
//			if(date < today){
//				$scope.notpass = true;
//			}else{
//				$scope.notpass = false;
//			}
			if ($scope.row.STATUS != "02")
				$scope.notpass = false;
			else 
				$scope.notpass = true;
			console.log(JSON.stringify($scope.row));
			console.log($scope.notpass);
			$scope.inputVO = {
				examVersion: $scope.row.EXAM_VERSION,
				questionVersion: undefined,
				rlVersion: $scope.row.RL_VERSION,
				expiryDate: $scope.toJsDate($scope.row.CREATETIME),
				empID: sysInfoService.getUserID(), //當前使用者帳號
				empName: undefined,
				branchID: sysInfoService.getBranchID(),
				check: undefined,
				remark: undefined
			};
		};
		$scope.init();

		//checkInquire
		$scope.checkInquire = function(){
			$scope.sendRecv("KYC520", "checkInquire", "com.systex.jbranch.app.server.fps.kyc520.KYC520InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.checkParamList = tota[0].body.resultList;
						$scope.rlVersionList = tota[0].body.rlVersionList;
						$scope.flwList = tota[0].body.flwList;
						$scope.inputVO.rlVersion = $scope.checkParamList[0].RL_VERSION;
						$scope.outputVO = tota[0].body;
						return;
					}
				});
		}
		$scope.checkInquire();

		/**
		 * 取當前使用者姓名&主管輸入備註
		 */
		$scope.getRemarkInfo = function() {
			console.log(JSON.stringify($scope.flwList));
			var flwMaxLen = $scope.flwList.length - 1;
			$scope.inputVO.remark = $scope.flwList[flwMaxLen].REMARK;
			$scope.inputVO.empName = $scope.flwList[flwMaxLen].SIGNOFF_NAME;
		}

		//審核
		$scope.approve = function(){
			$scope.getRemarkInfo();
			$scope.sendRecv("KYC520", "approve", "com.systex.jbranch.app.server.fps.kyc520.KYC520InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.closeThisDialog('successful');
					}
				});
		}

		//退回
		$scope.reject = function () {
			$scope.getRemarkInfo();
			$scope.sendRecv("KYC520", "reject", "com.systex.jbranch.app.server.fps.kyc520.KYC520InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.closeThisDialog('successful');
					}
				});
		}
});
