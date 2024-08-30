'use strict';
eSoafApp.controller('PMS711_creditNewGoalSetTSController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS711_creditNewGoalSetTSController";
	
	//import xls
	$scope.importfile = function() 
	{
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		if($scope.inputVO.realfileName == null||$scope.inputVO.realfileName == undefined)
		{
			$scope.showErrorMsg('上傳檔案不能為空');
			return;
		}
		$scope.sendRecv("PMS711", "setCreditNewGoalTS", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
				function(tota, isError) 
				{
					if (isError) 
					{
						return;
					}else{
						//調用存儲過程
						$scope.callStoredCreditTs();
					};
				}
		);
    };
  //調用存儲過程存excel表
   $scope.callStoredCreditTs = function() {
		$scope.sendRecv("PMS711", "callStoredCreditTs", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
			function(tota, isError) {
				if(tota[0].body.errorMessage==null||tota[0].body.errorMessage==""){
					$scope.showMsg("ehl_01_common_010");
					$scope.queryCreditNewGoalTS();
				}else{
					$scope.showErrorMsg(tota[0].body.errorMessage);
				}
		});
    };
	$scope.inquireInit = function(){
		$scope.showList = [];
	}
	$scope.inquireInit();
    //查詢T/S設定
    $scope.queryCreditNewGoalTS = function(){
    	$scope.inputVO.date_year = $scope.date_year;
    	$scope.inputVO.personType = $scope.personType;
		$scope.sendRecv("PMS711", "queryCreditNewGoalTS", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.showList.length == 0) {
							$scope.ifShow = false;
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						//$scope.ifShow = true;
						$scope.showList =  tota[0].body.showList;;
						$scope.outputVO = tota[0].body;
						return;
					}
		});
    }
    $scope.queryCreditNewGoalTS();
	 //文件名
    $scope.uploadFinshed = function(name,rname) 
    {
    		$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
    };
  //有數據傳入按鈕才生效
    $scope.checkName = function(){
    	var check = false;
    	if($scope.inputVO.fileName != undefined && $scope.inputVO.fileName != ''){
    		check = true;
    	}
    	return check;
    }
});