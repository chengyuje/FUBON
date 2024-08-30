/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS200HistoryController', 
	    function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter) {
	        $controller('BaseController', {$scope: $scope});
	        $scope.controllerName = "FPS200HistoryController"; 
	        
	        $scope.isCollapsed = false;
			$scope.open = function($event, index) {
				$event.preventDefault();
				$event.stopPropagation();
				$scope['opened' + index] = !$scope['opened' + index];
			};
	        
	        $scope.getDataSource = function(){
				$scope.getXML = ['FPCRM.PLAN_STATUS', 'FPCRM.PLAN_TYPE'];
				getParameter.XML($scope.getXML, function(totas) {
					 if(len(totas)>0){
						 var tmp = totas;
			        	 $scope.planStatusList  = tmp.data[tmp.key.indexOf('FPCRM.PLAN_STATUS')];
			        	 $scope.planTypeList  = tmp.data[tmp.key.indexOf('FPCRM.PLAN_TYPE')];
					 }
				});
	        }
			$scope.getDataSource();
	        
	        /** initialize **/
	        $scope.init = function(){
	        	$scope.inputVO = {
	        			planStatus : '',
	        			isInvalid : false,
	        			planStartDate : undefined,
	        			planEndDate : undefined
	        	};
	        	$scope.planList = [];

	        }
	        
	        $scope.init();
	        
	        $scope.query = function(){
	        	
	        	$scope.planList = [];
	        	$scope.inputVO.custID = $scope.connector("get", "custID");
	        	if($scope.inputVO.custID == undefined){
	        		$scope.showErrorMsg("請指定客戶！");
	        		return;
	        	}
	        	
	        	$scope.sendRecv("FPS200", "queryPlan", "com.systex.jbranch.app.server.fps.fps200.FPS200InputVO", $scope.inputVO,
			              function(tota, isError) {
			                  if (!isError) {
			                	  $scope.outputVO = tota[0].body;
			                	  $scope.planList = tota[0].body.planList;
			                  }
			                  else{
			                	  $scope.showErrorMsg(tota);
			                  }
			                
			      });
	        	
	        }
	        
	        $scope.openPrint = function(row){
	        	 var dialog = ngDialog.open({
	                  template: 'assets/txn/FPS200/FPS200_PRINT.html',
	                  className: 'FPS200',
	                  showClose : false,
	                  controller: ['$scope', function($scope) {
	                	  $scope.row = row;
	                  }]
	              });
	              dialog.closePromise.then(function(data) {
						if (data.value === 'successful') {
						}
					});
	        }
	        
	        $scope.toPLAN = function(row){
	        	if(row.PLAN_STATUS == '1'){
	        		$scope.connector("set", "STEP", 'STEP1');
	        	}
	        	if(row.PLAN_STATUS == '3'){
	        		$scope.connector("set", "STEP1VO", {PLAN_ID : row.PLAN_ID, CUST_ID : row.CUST_ID, NEW_FLAG : '', PLAN_AMT : 0});
	        		$scope.connector("set", "STEP", 'STEP4');
	        	}
	        }
	        
	        $scope.limitDate = function() {
	        	$scope.startMaxDate = $scope.inputVO.planEndDate;
	            $scope.endMinDate = $scope.inputVO.planStartDate;
	        };
	        
}
);