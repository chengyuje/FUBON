/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_fcSub4Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		//console.log('111')
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_fcSub4Controller";
		
		$scope.init = function(){
			$scope.flag = 'E';
			$scope.PMS_711TYPE = $scope.connector('get','PMS_711TYPE');
		}
		$scope.init();

		
		$scope.select = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.checkflag = $scope.PMS_711TYPE;
			$scope.inputVO.cflag = $scope.flag;
			$scope.showRowList = new Object();
			$scope.inputVO.EDate1 = $scope.endDate;
			for(var key in $scope.showMonthList){
				$scope.showRowList[$scope.showMonthList[key]] = key; 
			}
			$scope.inputVO.subProjectSeqId = $scope.subProjectSeqId;
			if($scope.inputVO.personType=='5'){
				$scope.sendRecv("PMS711","queryMetSubEX","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.showList.length>0){
									$scope.showList = tota[0].body.showList;
									for(var i=0;i<$scope.showRowList.length;i++){
										alert(JSON.stringify($scope.showRowList.length));
									}
									$scope.outputVO = tota[0].body;
									$scope.totalChange();
//									alert("queryMetSub "+JSON.stringify($scope.outputVO));
								}
							}
						});
			}else{
				if($scope.inputVO.checkflag != '理財戶網行銀實動戶目標'){
					$scope.sendRecv("PMS711","queryFcSub","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
							function(tota, isError) {
								if (!isError) {
									$scope.showList = tota[0].body.showList;
									$scope.outputVO = tota[0].body;
									$scope.totalChange();
									//return;
								}
							});
					}else{
						$scope.sendRecv("PMS711","queryMetSubEX","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
								function(tota, isError) {
									if (!isError) {
										if(tota[0].body.showList.length>0){
											$scope.showList = tota[0].body.showList;
//											DATE_YEARMON
//											alert(JSON.stringify($scope.showList[0]));
											$scope.outputVO = tota[0].body;
											$scope.totalChange();
										}
									}
								});

					}

//				$scope.sendRecv("PMS711","queryFcSub","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
//						function(tota, isError) {
//							if (!isError) {
//								$scope.showList = tota[0].body.showList;
//								$scope.outputVO = tota[0].body;
////								alert("queryFcSub "+JSON.stringify($scope.outputVO));
//								//return;
//							}
//						});
			}
			
		}
		$scope.select();
		//計算統計值
		$scope.totalChange = function(){
			for(var i=0; i < $scope.showList.length; i++){
				var sum = 0;
				for(var key in $scope.showMonthList){
					sum += Number($scope.showList[i][key]);
				}
				$scope.showList[i].TOTAL = sum;
			}
		}
		//FC,FCH,PS,分行個金主管整批上傳
		$scope.batchUpload = function(date_year) {
			var personType = $scope.inputVO.personType;
			var subProjectSeqId = $scope.inputVO.subProjectSeqId;
			var checkflag = $scope.inputVO.checkflag;
			if($scope.inputVO.date_year == null || $scope.inputVO.date_year == '' || $scope.inputVO.personType == null || $scope.inputVO.personType == '')
	    	{
	    		$scope.showErrorMsg('欄位檢核錯誤:KPI年度與人員類別必填');
	    		return;
	    	}
			var dialog = ngDialog.open({
				template : 'assets/txn/PMS711/PMS711_batchUpload.html',
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
					$scope.query(personType);
				}
			});
		}
		
		/******轉換******/
	    $scope.changeToCklcmb = function () {
	    	$scope.flag = 'E';
	    	$("#met01_flag0").addClass('table-span2');
	    	$("#met01_flag1").removeClass('table-span2');
	    	$("#met01_flag2").removeClass('table-span2');

	    	$scope.select();
	    }
	    $scope.changeToTbsymb = function () {
	    	$scope.flag = 'I';
	    	$("#met01_flag1").addClass('table-span2');
	    	$("#met01_flag0").removeClass('table-span2');
	    	$("#met01_flag2").removeClass('table-span2');

	    	$scope.select();
	    } 
	    $scope.changeToTbsymb1 = function () {
	    	$scope.flag = 'P';
	    	$("#met01_flag2").addClass('table-span2');
	    	$("#met01_flag0").removeClass('table-span2');
	    	$("#met01_flag1").removeClass('table-span2');

	    	$scope.select();
	    } 

		
});