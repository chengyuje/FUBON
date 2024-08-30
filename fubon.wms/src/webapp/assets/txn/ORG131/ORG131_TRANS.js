/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG131_TRANSController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG131_TRANSController";

		$scope.initial = function(){
			if($scope.row)
				$scope.isUpdate = true;
			
			$scope.inputVO = {
				region_center_id: '',
				branch_area_id 	: '',
				branch_nbr 		: ''
			}
						
			if($scope.isUpdate){
				$scope.inputVO.seq = $scope.row.SEQ;
				$scope.inputVO.branch_nbr_back = $scope.row.BRANCH_NBR;
				$scope.inputVO.cust_id = $scope.row.CUST_ID;
				$scope.inputVO.status = $scope.row.STATUS;
				$scope.inputVO.emp_name=$scope.row.EMP_NAME;
			}
		};
		$scope.initial();
	
		$scope.trans = function(){
			$scope.sendRecv("ORG131", "rewcust", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
							
						}
						$scope.rewcust = tota[0].body.rewcust;
						$scope.outputVO = tota[0].body;
						
						if(tota[0].body.rewcust[0].REWCUST == "1"){
							$scope.showErrorMsg("該面談者ID曾於分行面試。");
						} else {
							$scope.sendRecv("ORG131", "transrmk", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
									function(tota, isError) {
									if(!isError){
										if (isError) {
											$scope.showErrorMsg(tota[0].body.msgData);
										}
										
										if(tota.length > 0) {
											$scope.showMsg("ehl_01_common_006");
											$scope.closeThisDialog('successful');
							        		return;
										}
									}
						 	});
						}
			});
		};
		
	
		/**===================================================================================================================**/		
		
		 //區域中心資訊
		$scope.genRegion = function() {
			$scope.sendRecv("ORG131", "region", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",{},
					function(tota, isError) {
						if(!isError){  
							if(tota[0].body.resultList7.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								return;
							}
							
							$scope.resultList7 = tota[0].body.resultList7;
							$scope.mappingSet['region'] = [];
							angular.forEach($scope.resultList7, function(row, index, objs){	
								$scope.mappingSet['region'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
							});
						}
			});
		};
		$scope.genRegion();
		
		//營運區資訊
		$scope.regionChange = function() {
			$scope.inputVO.branch_area_id ='';
			$scope.sendRecv("ORG131", "op", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",{},
					function(tota, isError) {
						if(!isError){  
							if(tota[0].body.resultList8.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								return;
							}
							
							$scope.resultList8 = tota[0].body.resultList8;
								
							$scope.mappingSet['op'] = [];			
							angular.forEach($scope.resultList8, function(row, index, objs){				
								if(row.PARENT_DEPT_ID == $scope.inputVO.region_center_id)	
									$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});				
							});
						}
			});
		};
		$scope.regionChange();
	        
		//分行資訊
		$scope.areaChange = function() {
			$scope.inputVO.branch_nbr ='';
			$scope.sendRecv("ORG131", "branch", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",{},
					function(tota, isError) {
						if(!isError){  
							if(tota[0].body.resultList9.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								return;
							}
							
							$scope.resultList9 = tota[0].body.resultList9;
							
							$scope.mappingSet['branch'] = [];
							angular.forEach($scope.resultList9, function(row, index, objs){				
								if(row.PARENT_DEPT_ID == $scope.inputVO.branch_area_id)			
									$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
							});	
						}
			});
		}; 
		$scope.areaChange();
 	
        /**======================================================date=================================================================**/

});