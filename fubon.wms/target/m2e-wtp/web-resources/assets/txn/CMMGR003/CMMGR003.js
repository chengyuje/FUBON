/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR003_Controller',
    function($scope, $controller, socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR003_Controller";
        
        $scope.initCsvType = function(){

        	var comboboxInputVO = {'param_type': 'TBSYSPARAMTYPE.CSV_TYPE', 'desc': false};
            $scope.requestComboBox(comboboxInputVO, function(totas) {
                /**
                 * 取回相關 ComboBox Data
                 * @type {Array}
                 */
                 if (totas[totas.length - 1].body.result === 'success') {
                	 projInfoService.mappingSet['TBSYSPARAMTYPE.CSV_TYPE'] = totas[0].body.result;
                 }
            });
        };
        
        $scope.initCsvType();
        
        $scope.init = function(){
        	$scope.inputVO = {
        			processor: '',
        			calendar: ''
        	};
        }
        
        $scope.init();
        
//      初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        }
        
        $scope.inquireInit();
        
        	$scope.inquire = function(){
	    	  
	    	  $scope.sendRecv("CMMGR003", "inquire", "com.systex.jbranch.app.server.fps.cmmgr003.CMMGR003InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
	                    	  
	                    	  $scope.mappingSet['CMMGR003.CALENDAR'] = projInfoService.mappingSet['CMMGR003.CALENDAR'];
	                    	  
	                      }
	                  });
	      }
        
        	$scope.refresh = function(){
        	if($scope.parameterTypeForm.$valid == false){
        		console.log('form=' + JSON.stringify($scope.parameterTypeForm));
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
	    	  $scope.paramList = [];
	    	  
	    	  $scope.sendRecv("CMMGR003", "refresh", "com.systex.jbranch.app.server.fps.cmmgr003.CMMGR003InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {

//	                    	  $scope.mappingSet['CMMGR003.CALENDAR'] = projInfoService.mappingSet['CMMGR003.CALENDAR'];
//	                    	  
//	                    	  $scope.initLimit();
//	                          $scope.paramList = tota[0].body.dataList;
//	                          $scope.showMsg('查詢筆數[' + $scope.paramList.length + ']');
	                          
//	                    	  $scope.pagingList是繼承自BaseController而來
//	                    	  $scope.paramList須與html裡的ng-repeat="row in results = paramList ..."中的paramList同名
//	                    	  paramList命名各交易可自行決定，一般會與java端下行命名相同，方便debug
//	                    	  tota[0].body.paramList為java端下行內容，視java端命名而定
	                    	  $scope.pagingList($scope.paramList, tota[0].body.dataList);
	                    	  
//	                    	     將java端下行body內容指定於$scope.outputVO，以便分頁元件使用
	                          $scope.outputVO = tota[0].body;
	                          return;
	                      }
	                  });
	      	}
        
        	$scope.restart = function(){
	    	  
	    	  $scope.paramList = [];
	    	  
	    	  $scope.sendRecv("CMMGR003", "restart", "com.systex.jbranch.app.server.fps.cmmgr003.CMMGR003InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
	                    	  
	                    	  $scope.mappingSet['CMMGR003.CALENDAR'] = projInfoService.mappingSet['CMMGR003.CALENDAR'];
	                    	  
	                      }
	                  });
	      	}
        
        	$scope.refreshCandlander = function(){
	    	  
	    	  $scope.paramList = [];
	    	  
	    	  $scope.sendRecv("CMMGR003", "refreshCandlander", "com.systex.jbranch.app.server.fps.cmmgr003.CMMGR003InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
	                    	  
	                    	  $scope.mappingSet['CMMGR003.CALENDAR'] = projInfoService.mappingSet['CMMGR003.CALENDAR'];

	                      }
	                  });
        	};
	      
	        $scope.iniRoles = function(){
	        	socketService.sendRecv("CMMGR003", "getProcessList", "com.systex.jbranch.app.server.fps.cmmgr003.CMMGR003ProcessorOutputVO", {})
	    		.then(
		    		function(oResp) {
		    			$scope.mappingSet['processor'] = [];
		    			angular.forEach(oResp[0].body.processList, function(row, index, objs){
		    				$scope.mappingSet['processor'].push({LABEL: row, DATA: row});
		    			});
		    			projInfoService.mappingSet['processor'] = $scope.mappingSet['processor'];
		    		},
		    		function(oErr) {
		    			$scope.showErrorMsg(oErr);
		    		});	
	        };
	        
	        $scope.iniRoles();
    }
);
