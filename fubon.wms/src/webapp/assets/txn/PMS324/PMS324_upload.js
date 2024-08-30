/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS324_uploadController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) 
	{
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS324_uploadController";
		/** 窗口彈出 **/
    	$scope.upload = function(startTime,endTime)
    	{
    		var dialog = ngDialog.open({
    			template: 'assets/txn/PMS324/PMS324_showData.html',
    			className: 'PMS324_showData',
    			showClose: false,
    			controller: ['$scope', function($scope) {
    				$scope.startTime = startTime;
    				$scope.endTime = endTime;
                }]
    		});
    	}
	  //import xls
		$scope.importfile = function() 
		{
			$scope.inputVO.startTime = $scope.s1Time.substr(0,6);
			$scope.inputVO.endTime = $scope.s1Time.substr(7,6);
			if($scope.inputVO.realfileName == null||$scope.inputVO.realfileName == undefined)
			{
				$scope.showErrorMsg('上傳檔案不能為空');
				return;
			}
			$scope.sendRecv("PMS324", "addData", "com.systex.jbranch.app.server.fps.pms324.PMS324InputVO", $scope.inputVO,
					function(tota, isError) 
					{
						if (isError) 
						{
							return;
						}else{
							//調用存儲過程
							$scope.callStored();
						};
					}
			);
	    };
	    
	    
		/**download sample files**/
	    $scope.downloadSample = function() {			
        	$scope.sendRecv("PMS324", "downloadSample", "com.systex.jbranch.app.server.fps.pms324.PMS324InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		  
	    
	    
	  //調用存儲過程存excel表
	   $scope.callStored = function() {
			$scope.inputVO.startTime = $scope.s1Time.substr(0,6);
			$scope.inputVO.endTime = $scope.s1Time.substr(7,6);
			$scope.sendRecv("PMS324", "callStored", "com.systex.jbranch.app.server.fps.pms324.PMS324InputVO", $scope.inputVO,
					function(tota, isError) {
						if(tota[0].body.errorMessage==null||tota[0].body.errorMessage==""){
							$scope.showMsg("ehl_01_common_010");
							$scope.closeThisDialog('cancel');
							$scope.upload($scope.inputVO.startTime,$scope.inputVO.endTime);
						}else{
							$scope.showErrorMsg(tota[0].body.errorMessage);
						}
			});
	    };
	    
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