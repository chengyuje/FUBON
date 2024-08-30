/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM410_EDITController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM410_EDITController";
		
		// filter
		getParameter.XML(["MGM.GIFT_KIND", "MGM.DELIVERY_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.GIFT_KIND'] = totas.data[totas.key.indexOf('MGM.GIFT_KIND')];
				$scope.mappingSet['MGM.DELIVERY_STATUS'] = totas.data[totas.key.indexOf('MGM.DELIVERY_STATUS')];
			}
		});
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.seq_list = [];
//			alert(JSON.stringify($scope.resultList));
			$scope.outputVO = $scope.resultList;
		}
		$scope.init();
		
		//本頁全選
		$scope.page = function() {
        	if ($scope.pageControlVO.pageChoice) {
        		angular.forEach($scope.paramList, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.paramList, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
        
        //全選
        $scope.all = function() {
        	if ($scope.pageControlVO.allChoice) {
        		$scope.pageControlVO.pageChoice = true;
        		angular.forEach($scope.resultList, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		$scope.pageControlVO.pageChoice = false;
        		angular.forEach($scope.resultList, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
	    
	    //整批放行確認後儲存
	    $scope.save = function () {
	    	$scope.inputVO.editList = [];
	    	angular.forEach($scope.resultList, function(row){
	    		if (row.SELECTED == true) {
	    			$scope.inputVO.editList.push(row);
	    		}
	    	});
	    	if($scope.inputVO.editList.length == 0){
	    		$scope.showErrorMsg("請勾選。");
				return;
	    	} else {
//	    		alert(JSON.stringify($scope.inputVO.editList));
	    		$scope.sendRecv("MGM410", "save", "com.systex.jbranch.app.server.fps.mgm410.MGM410InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.showSuccessMsg("ehl_01_common_023");		//執行成功
								$scope.closeThisDialog('successful');
								return;
							}
				});
	    	}
	    	
	    }
});
