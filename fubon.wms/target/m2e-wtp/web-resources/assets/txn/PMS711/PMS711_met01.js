'use strict';
eSoafApp.controller('PMS711_met01Controller', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS711_met01Controller";
	//初始化
	$scope.init = function(){
		$scope.inputVO = {
				date_year  : '',
				personType : ''
				
		};
		$scope.flag = '1';
	}
	$scope.init();
	
	/** 查詢 * */
	$scope.query = function() {
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.inputVO.flag = $scope.flag;
		$scope.inputVO.EDate1 = $scope.endDate;
		$scope.showRowList = new Object();
		var i = 0;
		for(var key in $scope.showMonthList){
			i++;	
			$scope.showRowList[$scope.showMonthList[key]] = key; 
		}
		$scope.showLength = i;
		$scope.inputVO.subProjectSeqId = Number($scope.subId);
		$scope.showList = [];
		$scope.sendRecv("PMS711","queryMet01","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.showList.length>0){
							$scope.showList = tota[0].body.showList;
							$scope.outputVO = tota[0].body;
							$scope.totalChange();
						}
					}else{
						$scope.showMsg("ehl_01_common_024");		//執行失敗
					}
				});
	}
	$scope.query();
	//計算統計值
	$scope.totalChange = function(){
		for(var i=0; i < $scope.showList.length; i++){
			var sum = 0;
			for(var key in $scope.showMonthList){
				if(!isNaN($scope.showList[i][key]))
					sum += Number($scope.showList[i][key]);
			}
			$scope.showList[i].TOTAL = sum;
		}
	}	
	
	/******轉換******/
    $scope.changeToCklcmb = function () {
    	$scope.flag = '1';
    	$("#met01_flag0").addClass('table-span2');
    	$("#met01_flag1").removeClass('table-span2');
    	$scope.query();
    }
    $scope.changeToTbsymb = function () {
    	$scope.flag = '2';
    	$("#met01_flag1").addClass('table-span2');
    	$("#met01_flag0").removeClass('table-span2');
    	$scope.query();
    } 
	
	
	/*$scope.save = function() {
		$scope.inputVO.showList = $scope.showList;
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.inputVO.subProjectSeqId = $scope.subId;
		for(var i=0; i<$scope.showList.length; i++){
			for(var key in $scope.showList[i]){
				if(null==String($scope.showList[i][key]) || ''==String($scope.showList[i][key])){
					$scope.showMsg("ehl_01_common_022");  ////欄位檢核錯誤：必要輸入欄位
					return;
				}
			}
		}
		$scope.sendRecv("PMS711", "saveMet01","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
			$scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.showMsg("ehl_01_common_002"); //更新成功
				}else{
					$scope.showErrorMsg("ehl_01_common_007");//更新失敗
				}
			});
	}*/
});