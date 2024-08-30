/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR004_Controller',
    function($scope, $controller, socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR004_Controller";
        
        //combobox datasource 來源為非TBSYSPARAMTYPE資料表時，才直接使用socketService
        $scope.iniRoles = function(){
        };
        
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
        
        $scope.iniRoles();
        $scope.initCsvType();
        
        $scope.init = function(){
        	$scope.inputVO = {
         			schedulename: '',
        			description: '',
        			hlbscheduleid:''
        	};
        }
        $scope.init();
        
//      初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.paramList = [];
        }
        $scope.inquireInit();
        
        $scope.inquire = function(){
	    	 
	    	  $scope.sendRecv("CMMGR004", "inquire", "com.systex.jbranch.app.server.fps.cmmgr004.CMMGR004InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
//	                    	  $scope.pagingList($scope.paramList, tota[0].body.resultList);
	                    	  $scope.paramList = tota[0].body.resultList;
	                          $scope.outputVO = tota[0].body;
	                          return;
	                      }
	                  });
	      }
        
        
	      $scope.edit = function (row) {
	    	  
	    	  console.log('edit data='+JSON.stringify(row));
              var dialog = ngDialog.open({
                  template: 'assets/txn/CMMGR004/CMMGR004_EDIT.html',
                  className: 'FPCAL300',
                  showClose : false,
                  controller: ['$scope', function($scope) {
                	  $scope.row = row;
                  }]
           
              });
            
              dialog.closePromise.then(function (data) {
                  if(data.value === 'successful'){
                	  $scope.inquireInit();
                	  $scope.inquire();
                	 
                  }
              });
          };
          
          $scope.add = function () {
	    	  console.log('add data=');
              var dialog = ngDialog.open({
                  template: 'assets/txn/CMMGR004/CMMGR004_EDIT.html',
                  className: 'FPCAL300'
              });
              dialog.closePromise.then(function (data) {
                  if(data.value === 'successful'){
                	  $scope.inquireInit();
                	  $scope.inquire();
                  }
              });
          };
          
          //*** 手動執行 ***
          $scope.execute = function(row) {
        	  $scope.inputVO.hlbscheduleid = angular.copy(row.SCHEDULEID);
        	  $scope.sendRecv("CMMGR004", "execute", "com.systex.jbranch.app.server.fps.cmmgr004.CMMGR004QueryInputVO", 
        			  $scope.inputVO,
        			  function(totas, isError) {
		              	if (!isError) {
		              		$scope.showMsg("手動執行排程 代號[" + row.SCHEDULEID + "]");
		              	}
					  }
        	  );
          }
    }
);
