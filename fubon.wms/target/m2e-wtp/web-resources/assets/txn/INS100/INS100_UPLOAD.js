/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS100_UPLOADController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS100_UPLOADController";

		$scope.init = function() {
			$scope.UploadinputVO = {
					custId:$scope.custId
			}
		};
		$scope.init();
	    
	  //import pdf
		$scope.importfile = function() {
			if($scope.UploadinputVO.realfileName==null||$scope.UploadinputVO.realfileName==undefined){
				$scope.showErrorMsg('上傳資料不能為空');
				return;
			}
			$scope.sendRecv("INS100", "uploadAgreeHis", "com.systex.jbranch.app.server.fps.ins100.INS100InputVO", $scope.UploadinputVO,
					function(tota, isError) {
						if (isError) {
							return;
						}else{
							$scope.showSuccessMsg('ehl_01_common_010');
							$scope.closeThisDialog('cancel1');
						};
					}
			);
	    };
	    
	  //文件名
	    $scope.uploadFinshed = function(name,rname) {
        		$scope.UploadinputVO.fileName = name;
            	$scope.UploadinputVO.realfileName = rname;
        };
        
      //有數據傳入按鈕才生效
        $scope.checkName = function(){
        	var check = false;
        	if($scope.UploadinputVO.fileName != undefined && $scope.UploadinputVO.fileName != ''){
        		check = true;
        	}
        	return check;
        }
	    
	    
});