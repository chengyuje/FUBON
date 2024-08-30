/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CUS130_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS130_DETAILController";
		
		// combobox
		getParameter.XML(["CUS.IVG_TYPE", "CUS.IVG_PLAN_TYPE", "COMMON.YES_NO"], function(totas) {
			if (totas) {
				$scope.IVG_TYPE = totas.data[totas.key.indexOf('CUS.IVG_TYPE')];
				$scope.IVG_PLAN_TYPE = totas.data[totas.key.indexOf('CUS.IVG_PLAN_TYPE')];
				$scope.COMMON_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			}
		});
		//
		
		$scope.sendRecv("CUS130", "getDetail", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'seq':$scope.row.IVG_PLAN_SEQ},
				function(totas, isError) {
                	if (!isError) {
                		$scope.totalData = totas[0].body.resultList;
                		$scope.outputVO = totas[0].body;
                		$scope.totalData = _.orderBy($scope.totalData, ['REGION_CENTER_ID', 'BRANCH_AREA_ID', 'BRANCH_NBR', 'ROLE_NAME', 'EMP_ID']);
                		$scope.column = totas[0].body.resultList2;
                		angular.forEach($scope.totalData, function(row, index){
        					row.ROW_NO = index + 1;
        				});
                	};
				}
		);
		
        // download
        $scope.download = function() {
        	$scope.sendRecv("CUS130", "download", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'fileID': $scope.row.DOC_ID,'fileName': $scope.row.DOC_NAME},
					function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
			);
        };
        //
        
        $scope.exportData = function() {
        	$scope.mappingSet['EXPORT_DATA'] = angular.copy($scope.totalData);
        	angular.forEach($scope.mappingSet['EXPORT_DATA'], function(row){
        		angular.forEach(row.SUBITEM, function(rowDetail){
        			if (rowDetail.FIELD_TYPE == "4") {
        				rowDetail.FIELD_VALUE = $filter('date')(parseFloat(rowDetail.FIELD_VALUE), 'yyyy/MM/dd');
        			}
        		});
			});
        	
        	$scope.sendRecv("CUS130", "export", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'listBase':$scope.mappingSet['EXPORT_DATA'],'custList':$scope.column},
    				function(totas, isError) {
		        		if (!isError) {
							return;
						}
    				}
    		);
        };
        
        
		
});