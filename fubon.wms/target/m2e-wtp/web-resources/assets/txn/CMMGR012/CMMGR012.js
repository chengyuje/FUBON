/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR012_Controller',
    function($scope, $controller, socketService, ngDialog, projInfoService, sysInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR012_Controller";
        $scope.isCollapsed = false;
          $scope.open = function($event, index) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope['opened'+index] = true;
          };
          
          //初始化
        $scope.init = function(){
         	$scope.inputVO = {
         			tipBranchId: '',
         			tipWorkStationId: '',
         			tipTeller: '',
         			cmbFunctionId: '',
         			tipTxnCode: '',
         			cmbRoleId: '',
         			dtfLastUpdate: undefined
         	};    
        } 
        $scope.init();
        
//        初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        }
        
        $scope.inquireInit();
        
        //取得comboboxlist
        $scope.iniRoles = function(){
        	socketService.sendRecv("CMMGR012", "init", "com.systex.jbranch.app.server.fps.cmmgr012.CMMGR012InputVO", {})
    		.then(
    		function(oResp) {
    			$scope.mappingSet['RoleId'] = [];
    			$scope.mappingSet['FunctionId'] = [];
    			angular.forEach(oResp[0].body.roleList, function(row, index, objs){
    				$scope.mappingSet['RoleId'].push({LABEL: row.NAME, DATA: row.ROLEID});
    			}); 			
    			angular.forEach(oResp[0].body.functionList, function(row, index, objs){
    				$scope.mappingSet['FunctionId'].push({LABEL: row.NAME, DATA: row.NAME});
    			});	
    		},
    		function(oErr) {
    			$scope.showErrorMsg(oErr);
    		});	
        };
        $scope.iniRoles();   
   	    $scope.exportData = function(){
  			$scope.inputVO.varCsvType = $scope.CSV_TYPE;            
  			$scope.sendRecv("CMMGR012", "export","com.systex.jbranch.app.server.fps.cmmgr012.CMMGR012InputVO",
  					$scope.inputVO, function(tota, isError) {
  						if (!isError) {
  							$scope.adgParameter = tota[0].body.adgParameter;
  							console.log('$scope.adgParameter='+JSON.stringify($scope.adgParameter));
  							return;
  						}
  					});
  		}
   	    
   	    //查詢
         $scope.inquire = function(){	
	    	  $scope.sendRecv("CMMGR012", "inquire", "com.systex.jbranch.app.server.fps.cmmgr012.CMMGR012InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {	
	                          
	                    	  $scope.pagingList($scope.paramList, tota[0].body.dataList);
	                          $scope.outputVO = tota[0].body;
	                          return;
	                      }
	                  });
	     }
			
			//判斷日期
			$scope.limitDate = function() {
	        	$scope.startMaxDate = $scope.inputVO.dtfLastUpdate;
	            $scope.endMinDate = $scope.inputVO.dtfStartDate;
	        };

    }
);