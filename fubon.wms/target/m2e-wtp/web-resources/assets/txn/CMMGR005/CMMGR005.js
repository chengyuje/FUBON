/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR005Controller',
    function($scope, $controller, socketService, ngDialog, projInfoService, getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR005Controller";

        // date picker
        // 活動起迄日
        $scope.startDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.endDate || $scope.maxDate;
			$scope.endDateOptions.minDate = $scope.inputVO.startDate || $scope.minDate;
		};
		// date picker end
		
//        $scope.iniRoles = function(){
//        	socketService.sendRecv("CMMGR005", "inquire", "com.systex.jbranch.app.server.fps.cmmgr005.CMMGR005InputVO", {})
//    		.then(
//    		function(oResp) {
//    			$scope.mappingSet['type'] = [];
//    			angular.forEach(oResp[0].body.queryList, function(row, index, objs){
//    				$scope.mappingSet['type'].push({LABEL: row.NAME, DATA: row.TPYE});
//    			});
//    			console.log(JSON.stringify($scope.mappingSet['type']));
//    			projInfoService.mappingSet['type'] = $scope.mappingSet['type'];
//    		},
//    		function(oErr) {
//    			$scope.showErrorMsg(oErr);
//    		});	
//        };
        
        $scope.initCsvType = function(){
        	getParameter.XML(['TBSYSSCHDADMASTER.TYPE','CMMGR005.TYPE','CMMGR005.STATUS','CMMGR005.RESULT'], function(totas) {
				if(len(totas)>0){
					projInfoService.mappingSet['TBSYSSCHDADMASTER.TYPE'] = totas.data[totas.key.indexOf('TBSYSSCHDADMASTER.TYPE')];
					$scope.mappingSet['CMMGR005.TYPE'] = totas.data[totas.key.indexOf('CMMGR005.TYPE')];
					$scope.mappingSet['CMMGR005.STATUS'] = totas.data[totas.key.indexOf('CMMGR005.STATUS')];
					$scope.mappingSet['CMMGR005.RESULT'] = totas.data[totas.key.indexOf('CMMGR005.RESULT')];
				}
				 
			});
//        	var comboboxInputVO = {'type': 'TBSYSSCHDADMASTER.TYPE', 'desc': false};
//            $scope.requestComboBox(comboboxInputVO, function(totas) {
//                /**
//                 * 取回相關 ComboBox Data
//                 * @type {Array}
//                 */
//                 if (totas[totas.length - 1].body.result === 'success') {
//                	 projInfoService.mappingSet['TBSYSSCHDADMASTER.TYPE'] = totas[0].body.result;
//                 }
//            });
        };
        
//        $scope.iniRoles();
        $scope.initCsvType();
        
        $scope.init = function(){
        	$scope.inputVO = {
        			auditid: '',
        			scheduleId: '',
        			jobId: '',
        			type: '',
        			status: '',
        			result: '',
        			hlbAuditId: '',
        			startDate : new Date()
        	};
        	$scope.limitDate();
        	
        }
        $scope.init();
        
//      初始分頁資訊
        $scope.inquireInit = function(){
//        	$scope.initLimit();
        	$scope.paramList = [];
        }
        
        $scope.inquireInit();
        
        $scope.inquire = function(){
	    	  
	    	  $scope.sendRecv("CMMGR005", "inquire", "com.systex.jbranch.app.server.fps.cmmgr005.CMMGR005InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
//	                          $scope.mappingSet['CMMGR005.TYPE'] = projInfoService.mappingSet['CMMGR005.TYPE'];
//							  $scope.mappingSet['CMMGR005.STATUS'] = projInfoService.mappingSet['CMMGR005.STATUS'];
//							  $scope.mappingSet['CMMGR005.RESULT'] = projInfoService.mappingSet['CMMGR005.RESULT'];	                    	  
							  $scope.paramList = tota[0].body.queryList;	                    	  
	                          $scope.outputVO = tota[0].body;
	                      }else{
	                    	  $scope.showErrorMsg('ehl_01_common_000');
	                      }
	                  });
	      }
        
	      $scope.messageInquire = function (row) {
	    	  // 2016/6/6 By:David Add: showClose : false
              var dialog = ngDialog.open({
                  template: 'assets/txn/CMMGR005/CMMGR005_EDIT.html',
                  className: 'CMMGR005',
                  showClose : false,
                  controller: ['$scope', function($scope) {
                	  $scope.row = row;
                  }]
              });
              dialog.closePromise.then(function (data) {
//                	  $scope.inquireInit();
//                	  $scope.inquire();
              });
          };
          
          $scope.inquire();
          
          //批次執行時間查詢
          $scope.showExecuteTime = function() {
        	  var dialog = ngDialog.open({
                  template: 'assets/txn/CMMGR005/CMMGR005_SHOW_EXETIME.html',
                  className: 'CMMGR005',
                  showClose : false,
                  controller: ['$scope', function($scope) {
                  }]
              });
              dialog.closePromise.then(function (data) {
              });
          }
    }
);

