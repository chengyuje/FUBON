/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_batchUploadeController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_batchUploadeController";

		$scope.init = function() {
			$scope.inputVO = {
			};
		};
		$scope.init();
	    
	  //import xls
		$scope.importfile1 = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			if($scope.inputVO.realfileName == null||$scope.inputVO.realfileName == undefined)
			{
				$scope.showErrorMsg('上傳檔案不能為空');
				return;
			}
			$scope.sendRecv("PMS711", "batchUploade", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
					function(tota, isError) 
					{
						if (isError) 
						{
							return;
						}else{
							//調用存儲過程
//							$scope.callStored();
							$scope.showMsg("ehl_01_common_010");
							$scope.closeThisDialog('cancel');

						};
					}
			);
		};
	  //文件名
	    $scope.uploadFinshed = function(name,rname) {
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
        
//        //調用存儲過程
        $scope.callStored = function(){
        	$scope.sendRecv("PMS711","callStored", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
					function(tota, isError) {
						if(tota[0].body.errorMessage==null||tota[0].body.errorMessage==""){
							$scope.showMsg("ehl_01_common_010");
							$scope.closeThisDialog('cancel');
						}else{
							$scope.showErrorMsg(tota[0].body.errorMessage);
						}
			});
        }
	    
});