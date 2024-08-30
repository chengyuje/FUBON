/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM321_editController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM321_editController";
		
     	//取的FCH駐點行資料
		$scope.mappingSet['branch'] = [];
		angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){
			$scope.mappingSet['branch'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
		});
		
		//get分行別
		$scope.getBranchNbr = function() {
			$scope.inputVO.ass_brh = '';
			$scope.mappingSet['ass_brh'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
					if(row.BRANCH_AREA_ID == $scope.inputVO.fch_mast_brh) {
						$scope.mappingSet['ass_brh'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
					}
			});
		};
        
        //取的優先順序資料
		$scope.mappingSet['order'] = [{LABEL: "1", DATA: "1"},
			                          {LABEL: "2", DATA: "2"},
                                      {LABEL: "3", DATA: "3"},
			                          {LABEL: "4", DATA: "4"},
			                          {LABEL: "5", DATA: "5"},
			                          {LABEL: "6", DATA: "6"},
			                          {LABEL: "7", DATA: "7"},
			                          {LABEL: "8", DATA: "8"},
			                          {LABEL: "9", DATA: "9"},
			                          {LABEL: "10", DATA: "10"}
			                          ];
        
    	/****初始化資料*****/
        $scope.init = function(){
        	$scope.inputVO = {
					fch_mast_brh: $scope.row.FCH_MAST_BRH,
					priority_order: $scope.row.PRIORITY_ORDER
            };
        	$scope.getBranchNbr();
        	$scope.inputVO.ass_brh = $scope.row.ASS_BRH;
        	$scope.inputVO.oriass_brh = $scope.row.ASS_BRH;
        	$scope.inputVO.orifch_mast_brh = $scope.row.FCH_MAST_BRH;
        	
        };
        $scope.init();
		
		
		//修改
		$scope.editConfirm = function(){
        	$scope.sendRecv("CRM321", "editConfirm", "com.systex.jbranch.app.server.fps.crm321.CRM321InputVO", $scope.inputVO,
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('修改成功');
	                	$scope.closeThisDialog('successful');
	                };
	            }
        	);
        }
		
		
		
		
	}
);