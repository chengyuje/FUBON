/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS340_UPLOADController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS340_UPLOADController";
		$controller('BaseController', {$scope: $scope});
		$scope.init = function() {
			$scope.inputVO = {
				userId  :projInfoService.getUserID(),
				sTime : $scope.yearMon
			};
			$scope.largeAgrList = [];
		};
		$scope.init();
	    
	  //import xls
		$scope.importfile = function() {
			$scope.inputVO.sTime = $scope.yearMon;
			if($scope.inputVO.realfileName==null||$scope.inputVO.realfileName==undefined){
				$scope.showErrorMsg('上傳資料不能為空');
				return;
			}
			$scope.sendRecv("PMS340", $scope.uploadName, "com.systex.jbranch.app.server.fps.pms340.PMS340InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							return;
						}else{
//							$scope.showMsg("ehl_01_common_010");
//							$scope.closeThisDialog('cancel');
							//調用存儲過程
							$scope.callStored();
						}
							
					});
	    };
	    
    //調用存儲過程存excel表
	   $scope.callStored = function() {
		   
//		    $scope.inputVO.uploadName = $scope.uploadName;
			$scope.sendRecv("PMS340", "callStored", "com.systex.jbranch.app.server.fps.pms340.PMS340InputVO", $scope.inputVO,
					function(tota, isError) {
						if(tota[0].body.errorMessage==null||tota[0].body.errorMessage==""){
							$scope.showMsg("ehl_01_common_010");
							$scope.closeThisDialog('cancel');
						}else{
							$scope.showErrorMsg(tota[0].body.errorMessage);
						}
			});
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
        
        /**download sample files**/
    	$scope.downloadSample = function() {	
        	$scope.sendRecv("PMS340", "downloadSample", "com.systex.jbranch.app.server.fps.pms340.PMS340InputVO", {},
    				function(tota, isError) {
    					if (!isError) {
    						return;
    					}
    		});
    	};
});