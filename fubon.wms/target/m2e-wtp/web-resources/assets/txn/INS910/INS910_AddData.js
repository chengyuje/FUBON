'use strict';
eSoafApp.controller('INS910_AddDataController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS910_AddDataController";
		
		// combobox
		getParameter.XML(["INS.PLAN_TYPE", "INS.EARNED_CAL_WAY"], function(totas) {
			if (totas) {
				$scope.PLAN_TYPE = totas.data[totas.key.indexOf('INS.PLAN_TYPE')];
				$scope.EARNED_CAL_WAY = totas.data[totas.key.indexOf('INS.EARNED_CAL_WAY')];
			}
		});
		
		$scope.queryPrd = function() {
			var deferred = $q.defer();
			if($scope.inputVO.PRD_ID) {
				$scope.inputVO.PRD_ID = $scope.inputVO.PRD_ID.toUpperCase();
				$scope.sendRecv("INS910", "queryPrd", "com.systex.jbranch.app.server.fps.ins910.INS910InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.resultList.length == 0) {
								$scope.showErrorMsg("ehl_01_common_009");
								$scope.inputVO.INSPRD_ANNUAL = "";
								$scope.ANNUALSET = [];
								deferred.resolve();
							} else {
								$scope.inputVO.INSPRD_NAME = tota[0].body.resultList[0].INSPRD_NAME;
								$scope.inputVO.PRD_UNIT = tota[0].body.resultList[0].PRD_UNIT;
	        					$scope.inputVO.UNIT_NAME = tota[0].body.resultList[0].UNIT_NAME;
	        					$scope.inputVO.CURR_CD = tota[0].body.resultList[0].CURR_CD;
	        					var tempdata = tota[0].body.resultList[0].ANNUAL.split(',');
	        					$scope.ANNUALSET = [];
	        					angular.forEach(tempdata, function(row) {
	        						$scope.ANNUALSET.push({LABEL: row, DATA: row});
	        					});
	        					if($scope.inputVO.INSPRD_ANNUAL) {
	        						var inIndex = tempdata.indexOf($scope.inputVO.INSPRD_ANNUAL.toString());
	        						if(inIndex == -1) $scope.inputVO.INSPRD_ANNUAL = "";
		        					else $scope.findKEYNO();
	        					}
	        					deferred.resolve();
							}
						}
				});
			} else {
				$scope.inputVO.INSPRD_ANNUAL = "";
				$scope.ANNUALSET = [];
				deferred.resolve();
			}
			return deferred.promise;
		};
		
		$scope.findKEYNO = function() {
			if($scope.inputVO.INSPRD_ANNUAL) {
				$scope.sendRecv("INS910", "queryKEYNO", "com.systex.jbranch.app.server.fps.ins910.INS910InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.KEY_NO = tota[0].body.resultList[0].KEY_NO;
							$scope.inputVO.insdata_key_no = tota[0].body.insdata_key_no;
							// 查詢投保年齡上、下限
							$scope.sendRecv("INS910","queryAge","com.systex.jbranch.app.server.fps.ins910.INS910InputVO", {'KEY_NO' : $scope.inputVO.KEY_NO},
                				function(tota,isError) {
        							if (!isError) {
        								$scope.inputVO.MIN_AGE = tota[0].body.resultList[0].MIN_AGE;
        								$scope.inputVO.MAX_AGE = tota[0].body.resultList[0].MAX_AGE;
        							}
	        				});
						} else {
							$scope.inputVO.KEY_NO = "";
							$scope.inputVO.insdata_key_no = "";
							$scope.inputVO.MIN_AGE = "";
							$scope.inputVO.MAX_AGE = "";
						}
				});
			} else {
				$scope.inputVO.KEY_NO = "";
				$scope.inputVO.insdata_key_no = "";
				$scope.inputVO.MIN_AGE = "";
				$scope.inputVO.MAX_AGE = "";
			}
		};
		
		$scope.ROW_INSTYPE = $scope.connector('get','INS910_INSTYPE');
		$scope.init = function() {
			if($scope.DataRow)
        		$scope.isUpdate = true;
			$scope.DataRow = $scope.DataRow || {};
        	$scope.inputVO = {
        		KEY_NO: $scope.DataRow.KEY_NO,
        		SUGGEST_TYPE: $scope.DataRow.SUGGEST_TYPE || $scope.ROW_INSTYPE,
        		PRD_ID: $scope.DataRow.PRD_ID,
        		INSPRD_ANNUAL: $scope.DataRow.INSPRD_ANNUAL,
        		POLICY_AMT_DISTANCE: $scope.DataRow.POLICY_AMT_DISTANCE,
        		POLICY_AMT_MIN: $scope.DataRow.POLICY_AMT_MIN,
        		POLICY_AMT_MAX: $scope.DataRow.POLICY_AMT_MAX,
        		CVRG_RATIO: $scope.DataRow.CVRG_RATIO,
        		CURR_CD:$scope.DataRow.CURR_CD,
        		ESTATE_PLAN:$scope.DataRow.ESTATE_PLAN ? $scope.DataRow.ESTATE_PLAN : 'N',
				EARNED_CAL_WAY: $scope.DataRow.EARNED_CAL_WAY,
				EARNED_YEAR: $scope.DataRow.EARNED_YEAR,
				EARNED_RATIO: $scope.DataRow.EARNED_RATIO
            };
        	if($scope.isUpdate) {
        		$scope.queryPrd().then(function(data) {
					$scope.findKEYNO();
				});
        	}
		};
		$scope.init();
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(parseFloat($scope.inputVO.POLICY_AMT_MIN) > parseFloat($scope.inputVO.POLICY_AMT_MAX)) {
				$scope.showErrorMsg('欄位檢核錯誤:保額上下限');
        		return;
			}
			if(!$scope.inputVO.insdata_key_no) {
				$scope.showErrorMsg('欄位檢核錯誤:繳費年期');
        		return;
			}
			$scope.closeThisDialog($scope.inputVO);
		};
		
		
});