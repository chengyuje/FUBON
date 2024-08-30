/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR010_Controller',
    function($scope, $controller, socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR010_Controller";

        $scope.iniRoles = function(){
        	$scope.mappingSet['roleDesc'] = projInfoService.mappingSet['rolesDesc'];
        	if($scope.mappingSet['roleDesc'] != undefined){
        		return;
        	}

//        	socketService.sendRecv("CMMGR010", "inquireRoleList", "com.systex.jbranch.app.server.fps.cmmgr010.CMMGR010InputVO", {})
//    		.then(
//    		function(oResp) {
//    			$scope.mappingSet['rolesDesc'] = [];
//    			angular.forEach(oResp[0].body.roleList, function(row, index, objs){
//    				$scope.mappingSet['rolesDesc'].push({LABEL: row.NAME, DATA: row.ROLEID});
//    			});
//    			projInfoService.mappingSet['rolesDesc'] = $scope.mappingSet['rolesDesc'];
//    		},
//    		function(oErr) {
//    			$scope.showErrorMsg(oErr);
//    		});	
         };
        
         $scope.iniRoles();
        
         $scope.init = function(){
        	$scope.inputVO = {
        			hostid: '',
        			ip: '',
        			port: undefined,//值不能為空所以需設為undefined
        			username: ''
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
        	
	    	  $scope.sendRecv("CMMGR010", "inquire", "com.systex.jbranch.app.server.fps.cmmgr010.CMMGR010InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
	                    	  $scope.paramList = tota[0].body.resultList;
	                    	  $scope.outputVO = tota[0].body;
	                    	  
	                    	  if ($scope.paramList.length == 0) {
	                    		  $scope.showMsg("ehl_01_common_009");
	                    	  }
	                    	  
	                          return;
	                      }
	                  }
	    	  );
	     }
        
	     $scope.update = function (row) {
	    	 var dialog = ngDialog.open({
	    		 template: 'assets/txn/CMMGR010/CMMGR010_EDIT.html',
	    		 className: 'CMMGR010',
	    		 showClose: false,
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
    }
);
