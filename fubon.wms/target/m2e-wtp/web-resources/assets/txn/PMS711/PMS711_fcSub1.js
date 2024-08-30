/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS7111_fcSubController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		//console.log('111')
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS7111_fcSubController";
		
		$scope.init = function(){
			$scope.flag = '1';
			$scope.fusub1 = '1';
			$scope.PMS_711TYPE = $scope.connector('get','PMS_711TYPE');
		    var date = new Date();
		    $scope.year = date.getFullYear();
		}
		$scope.init();

		
		$scope.select = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.checkflag = $scope.connector('get','PMS_711TYPE');
			$scope.inputVO.cflag = $scope.flag;
			$scope.inputVO.fusub1 = $scope.fusub1;

			$scope.showRowList = new Object();
			for(var key in $scope.showMonthList){
				$scope.showRowList[$scope.showMonthList[key]] = key; 
			}
			$scope.inputVO.subProjectSeqId = $scope.subProjectSeqId;
//			if($scope.inputVO.personType=='5'){
				$scope.sendRecv("PMS711","queryMetSub1","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.showList1.length>0){
									$scope.showList1 = tota[0].body.showList1;
//									DATE_YEARMON
//									alert(JSON.stringify($scope.showList[0]));
									$scope.outputVO = tota[0].body;
//									alert("queryMetSub "+JSON.stringify($scope.outputVO));
								}
							}
						});
//			}else{
//				if($scope.inputVO.checkflag != '理財戶網行銀實動戶目標'){
//				$scope.sendRecv("PMS711","queryFcSub","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
//						function(tota, isError) {
//							if (!isError) {
//								$scope.showList = tota[0].body.showList;
//								$scope.outputVO = tota[0].body;
////								alert("queryFcSub "+JSON.stringify($scope.outputVO));
//								//return;
//							}
//						});
//				}else{
//					$scope.sendRecv("PMS711","queryMetSub","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
//							function(tota, isError) {
//								if (!isError) {
//									if(tota[0].body.showList.length>0){
//										$scope.showList = tota[0].body.showList;
////										DATE_YEARMON
////										alert(JSON.stringify($scope.showList[0]));
//										$scope.outputVO = tota[0].body;
////										alert("queryMetSub "+JSON.stringify($scope.outputVO));
//									}
//								}
//							});
//
//				}
//			}
			
		}
		$scope.select();
		
		//FC,FCH,PS,分行個金主管整批上傳
		$scope.batchUpload1 = function(date_year) {
//			alert("aa");
			var personType = $scope.inputVO.personType;
			var subProjectSeqId = $scope.inputVO.subProjectSeqId;
			var checkflag = $scope.inputVO.checkflag;
			if($scope.inputVO.date_year == null || $scope.inputVO.date_year == '' || $scope.inputVO.personType == null || $scope.inputVO.personType == '')
	    	{
	    		$scope.showErrorMsg('欄位檢核錯誤:KPI年度與人員類別必填');
	    		return;
	    	}
			var dialog = ngDialog.open({
				template : 'assets/txn/PMS711/PMS711_batchUploade.html',
				className : 'PMS711_batchUpload',
				showClose : false,
				controller : [ '$scope', function($scope) {
					$scope.date_year = date_year,
					$scope.personType = personType;
					$scope.checkflag = checkflag;

				} ]
			});
			dialog.closePromise.then(function(data) {
				if (data.value == 'cancel') {
//					$scope.query(personType);
					$scope.select();

				}
			});
		}
		
		/******轉換******/
	    $scope.changeToCklcmb = function () {
	    	$scope.flag = '1';
	    	$("#met01_flag0").addClass('table-span2');
	    	$("#met01_flag1").removeClass('table-span2');
	    	$scope.select();
	    }
	    $scope.changeToTbsymb = function () {
	    	$scope.flag = '2';
	    	$("#met01_flag1").addClass('table-span2');
	    	$("#met01_flag0").removeClass('table-span2');
	    	$scope.select();
	    } 

		
});