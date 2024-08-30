'use strict';
eSoafApp.controller('PMS711_ps02Controller', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS711_ps02Controller";
	//初始化
	$scope.init = function(){
		$scope.inputVO = {
				date_year  : '',
				personType : ''
				
		};
		$scope.flag = '0';
	}
	$scope.init();
	/** 查詢 * */
	$scope.query = function() {
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.inputVO.flag = $scope.flag;
		$scope.showRowList = new Object();
		var i = 0;
		for(var key in $scope.showMonthList){
			i++;	
			$scope.showRowList[$scope.showMonthList[key]] = key; 
		}
		$scope.showLength = i;
		$scope.inputVO.subProjectSeqId = Number($scope.subId);
		$scope.sendRecv("PMS711","queryPs02","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showList = tota[0].body.showList;
						return;
					}else{
						$scope.showMsg("ehl_01_common_024");		//執行失敗
					}
				});
		
		/*console.log('711_01');
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.inputVO.flag = $scope.flag;
		$scope.sendRecv("PMS711", "queryps021","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
		$scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.showList == null
						|| tota[0].body.showList.length == 0) {
					$scope.showList = [];
					$scope.showMsg("ehl_01_common_009");
					return;
				}
				$scope.showList = tota[0].body.showList;
			}else{
				$scope.showMsg("ehl_01_common_024");		//執行失敗
			}
		});*/
	}
	
	/******轉換******/
    $scope.changeToCklcmb = function () {
    	$scope.flag = '0';
    	$("#ps021_flag0").addClass('table-span2');
    	$("#ps021_flag1").removeClass('table-span2');
    	$scope.query();
    }
    $scope.changeToTbsymb = function () {
    	$scope.flag = '1';
    	$("#ps021_flag1").addClass('table-span2');
    	$("#ps021_flag0").removeClass('table-span2');
    	$scope.query();
    } 
	
	
	$scope.query();
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
		$scope.sendRecv("PMS711", "saveps021","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
			$scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.showMsg("ehl_01_common_002"); //更新成功
				}else{
					$scope.showErrorMsg("ehl_01_common_007");//更新失敗
				}
			});
	}*/
});