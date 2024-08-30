/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR009_Controller',
    function($scope,$filter, $controller, socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR009_Controller";
        $scope.Math=Math;
        //DATEPICKER
        $scope.open = function($event, index) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope['opened'+index] = true;
          };
          $scope.disabledStartDate = function(){
        	  $scope.disStartDt = !$scope.disStartDt; 
          }
          
        $scope.init = function(){
        	$scope.inputVO = {
        			tipBranchId: '',
        			tipWorkStationId: '',
        			tipTeller: '',
        			tipParamType: '',
        			tipPtypeName: '',
        			tipParamCode: '',
        			tipParamName: '',
        			dtfEffectDate: undefined, 
        			dtfLastUpdate: undefined
        	};
        }
        $scope.init();
    
//         初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        }
        
        $scope.inquireInit();
        
        $scope.inquire = function(){
	    	  $scope.sendRecv("CMMGR009", "inquire", "com.systex.jbranch.app.server.fps.cmmgr009.CMMGR009InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
	                    	  $scope.pagingList($scope.paramList, tota[0].body.dataList);
	                    	  
	                          $scope.outputVO = tota[0].body;
	                          return;
	                      }
	                  });
        	  };

       $scope.exportData = function(){
        
        	$scope.sendRecv("CMMGR009", "export",
        			"com.systex.jbranch.app.server.fps.cmmgr009.CMMGR009InputVO",
        			$scope.inputVO, function(tota, isError) {
        			if (!isError) {
        			$scope.adgParameter = tota[0].body.adgParameter;
        			onsole.log('$scope.adgParameter='+JSON.stringify($scope.adgParameter));
        			return;
        			}
        	});
       }
	      
    }
);
