/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG120_EDITController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG120_EDITController";
		
		// filter
		getParameter.XML(["ORG.ATCH_REASON"], function(totas) {
			if (totas) {
				$scope.mappingSet['ORG.ATCH_REASON'] = totas.data[totas.key.indexOf('ORG.ATCH_REASON')];
			}
		});
		
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
        $scope.limitDate2 = function(){
        	$scope.dateTemp = true;
        }
        //
		
		$scope.initial = function(){
			$scope.aoSize = 0;
			$scope.inputVO = {
					regionCenterID	: '',				//區域中心
					opAreaID		: '',					//營運區
					branchNbr		: '',					//分行
					ao_code			: '',					//主code
					empID			: $scope.row.EMP_ID,		//員工編號
					aoPerfEffDate	: undefined,		//業績生效日
					nonPerfAoCode	: '',				//維護code
					temp_Code		: '',					//是否副code
					atchCode		: '',					//副code
					activeDate		: undefined,			//副code生效日
					aoCodeAtchReason: '',			//副code理由
					aoType			: '',						//FC/FCH
					typeOne			: '',					//????
					aoList			: []
			};
		}
		
		$scope.initial();
		
        $scope.inquire = function(){
 			$scope.sendRecv("ORG120", "showORG120MOD", "com.systex.jbranch.app.server.fps.org120.ORG120InputVO", {'empID' :$scope.inputVO.empID},
				function(tota, isError) {
					if (!isError) {
						$scope.paramList = tota[0].body.modList;

						$scope.inputVO.regionCenterID = $scope.paramList[0].REGION_CENTER_ID;
						$scope.inputVO.opAreaID = $scope.paramList[0].BRANCH_AREA_ID;
						$scope.inputVO.branchNbr = $scope.paramList[0].BRANCH_NBR;
						$scope.inputVO.ao_code = $scope.paramList[0].AO_CODE;
						$scope.inputVO.empID = $scope.paramList[0].EMP_ID;
						$scope.inputVO.aoType = $scope.paramList[0].PRIVILEGEID;
						$scope.inputVO.aoPerfEffDate = $scope.toJsDate($scope.paramList[0].PERF_EFF_DATE); //業績生效日
						
						$scope.getFreeAoCode();
						
						if ($scope.paramList[0].PRIVILEGEID == "FC" && $scope.paramList[0].otherCodeList.length > 0) {
							$scope.inputVO.aoList = $scope.paramList[0].otherCodeList;
							$scope.aoSize = $scope.paramList[0].otherCodeList.length;
						} else if ($scope.paramList[0].PRIVILEGEID == "FCH" && $scope.paramList[0].otherCodeList.length > 0) {
							$scope.inputVO.aoList = $scope.paramList[0].otherCodeList;
							$scope.aoSize = $scope.paramList[0].otherCodeList.length;
						}
					}
 			}); 	
        };

        $scope.inquire();
        
        //=== add 副code list
        $scope.addAoCode = function () {
        	if ($scope.inputVO.atchCode) {
            	$scope.inputVO.aoList.push({AO_CODE: $scope.inputVO.atchCode,
						            		ACTIVE_DATE: $scope.inputVO.activeDate, 
						            		AO_CODE_ATCH_REASON: $scope.inputVO.aoCodeAtchReason, 
						            		COUNTS: 0});
            	
            	angular.forEach($scope.mappingSet['freeAoCode'], function(row, index, objs){	
            		if (row.DATA == $scope.inputVO.atchCode) {
						$scope.mappingSet['freeAoCode'].splice(index, 1);
					}
    			});
            	
            	$scope.inputVO.atchCode = "";
            	$scope.inputVO.activeDate = undefined;
            	$scope.inputVO.aoCodeAtchReason = "";
        	}
        }
        
        $scope.delAoCode = function (row) {
        	var index = $scope.inputVO.aoList.indexOf(row);
        	$scope.inputVO.aoList.splice(index, 1);
        }
        //=== end
        
        //=== 取得可使用CODE
        $scope.getFreeAoCode = function(){
	        $scope.sendRecv("ORG120", "getFreeAoCode", "com.systex.jbranch.app.server.fps.org120.ORG120InputVO", {},
	        		function(tota, isError) {
			        	if (!isError) {
		        			$scope.mappingSet['freeAoCode'] = tota[0].body.freeAoCodeList;
			        	 }
	         }); 
        }
        
        $scope.addReview = function () {
        	var deferred = $q.defer();
        	
    		$scope.sendRecv("ORG120", "addAoCodeSetting", "com.systex.jbranch.app.server.fps.org120.ORG120InputVO", $scope.inputVO,
    				function(tota, isError) {		                        	
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
							$scope.showMsg('ehl_01_common_005');
							
							return;
						}
						
						if(tota.length > 0) {
							deferred.resolve("success");
						}
    				}
    		);
    		
    		return deferred.promise;
        }
        
        $scope.addAoCodeSetting = function(){
        	if ($filter('date')($scope.inputVO.aoPerfEffDate, "yyyy-MM-dd") !== $filter('date')($scope.toJsDate($scope.paramList[0].PERF_EFF_DATE), "yyyy-MM-dd")) {
    			$scope.inputVO.typeOne = '1';
    			$scope.addReview().then(function(data) {
    				$scope.showSuccessMsg('ehl_01_common_015');
    				$scope.closeThisDialog('successful');
				});
    		}
        	
        	if ($scope.inputVO.aoList.length != $scope.aoSize) {
    			$scope.inputVO.typeOne = ($scope.inputVO.aoType == 'FC' ? '2' : '3');
    			$scope.addReview().then(function(data) {
    				$scope.showSuccessMsg('ehl_01_common_015');
    				$scope.closeThisDialog('successful');
				});
    		}
        };

});