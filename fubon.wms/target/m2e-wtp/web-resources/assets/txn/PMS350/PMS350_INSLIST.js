/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS350_INSLISTController',
	function(sysInfoService,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS350_INSLISTController";
		$scope.init = function(){
			$scope.inputVO = {
					rolesName:'',
					chkCode:[],
					code:[]
			}
		}
		$scope.init();
		
		$scope.aa=function(){
			$scope.inputVO.USER_TYPE = $scope.type;
	    	   $scope.sendRecv("PMS350", "personnel", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
						function(tota, isError) {
	    		              if (!isError) {
								if(tota[0].body.resultList.length == 0) {
									
									$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.paramList =tota[0].body.resultList;
								$scope.len=$scope.paramList.length;
								return;
							}				
				});
	       }
	       $scope.aa();  
	       
	       
        $scope.toggleSelection = function (data) {
        	var idx = $scope.inputVO.chkCode.indexOf(data),
        	_idx=$scope.inputVO.code.indexOf(data);
        	if (idx > -1) {
        		$scope.inputVO.chkCode.splice(idx, 1);
        		$scope.inputVO.code.splice(idx, 1);
        	} else {
        		$scope.inputVO.chkCode.push(data);
        	}
        };
        
        $scope.authoritySelection = function (data) {
        	var idx = $scope.inputVO.code.indexOf(data),
        	_idx=$scope.inputVO.chkCode.indexOf(data);
        	
        	if (idx > -1) {
        		$scope.inputVO.code.splice(idx, 1);
        	} else {
        		$scope.inputVO.code.push(data);
        		if(_idx == -1)
        			$scope.inputVO.chkCode.push(data);
        	}
        };
        
        $scope.save = function() {
        	if($scope.inputVO.USER_TYPE == 'uploader'){
        		$scope.closeThisDialog($scope.inputVO.chkCode);
        	}else{
        		$scope.closeThisDialog($scope.inputVO);
        	}
        	
        };
                	
});
