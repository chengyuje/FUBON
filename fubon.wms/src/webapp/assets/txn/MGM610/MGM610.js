/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM610Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "MGM610Controller";
		
		// date picker
		$scope.case_startOptions = {
				formatYear: 'yyyy',
			    startingDay: 1,
			    minMode: 'month'
		};
		
		$scope.case_endOptions = {
				formatYear: 'yyyy',
			    startingDay: 1,
			    minMode: 'month'
		};
		
		$scope.model = {};
		
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.case_startOptions.maxDate = $scope.inputVO.case_end;
			$scope.case_endOptions.minDate = $scope.inputVO.case_start;
		};
		
    	//取得活動代碼
        $scope.sendRecv("MGM110", "getActSeq", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.mappingSet['ACT_SEQ'] = [];
						angular.forEach(tota[0].body.resultList, function(row) {
							$scope.mappingSet['ACT_SEQ'].push({LABEL: row.ACT_NAME, DATA: row.ACT_SEQ});
	        			});
						return;
					}
		});
    	
		$scope.init = function() {
			$scope.resultList =[];
			$scope.outputVO = [];
			$scope.case_startOptions.maxDate = undefined;
			$scope.case_endOptions.minDate = undefined;
			$scope.inputVO = {};
	        // ["塞空ao_code用YN", $scope.inputVO, "區域NAME", "區域LISTNAME", "營運區NAME", "營運區LISTNAME", "分行別NAME", "分行別LISTNAME", "ao_codeNAME", "ao_codeLISTNAME", "emp_idNAME", "emp_idLISTNAME"]
	        $scope.test = ['Y', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.test);
		}
		$scope.init();
		
		$scope.inquire = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			
			if($scope.inputVO.act_seq != undefined && $scope.inputVO.act_seq != ''){
				$scope.sendRecv("MGM610", "inquire", "com.systex.jbranch.app.server.fps.mgm610.MGM610InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length == 0) {
									$scope.showMsg("ehl_01_common_009");	//查無資料
		                			return;
		                		} else {
		                			angular.forEach(tota[0].body.resultList, function(row) {
		                				row.ACT_SEQ = $scope.inputVO.act_seq;
		                			});
		                			
		                			$scope.resultList = tota[0].body.resultList;
		                			$scope.outputVO = tota[0].body;
		                			return;
		                		}						
							}
				});
			} else {
				$scope.showErrorMsg('請選擇活動代碼。');
				return;
			}
		}
		
		$scope.download = function() {
			if($scope.resultList.length != 0){
				$scope.sendRecv("MGM610", "download", "com.systex.jbranch.app.server.fps.mgm610.MGM610InputVO", 
					{'resultList' : $scope.resultList},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length == 0) {
									$scope.showMsg("ehl_01_common_009");	//查無資料
		                			return;
		                		} else {
		                			$scope.resultList = tota[0].body.resultList;
		                			$scope.outputVO = tota[0].body;
		                			return;
		                		}						
							}
				});
			} else {
				$scope.showErrorMsg('無查詢結果可供下載。');
				return;
			}
		}
});
		