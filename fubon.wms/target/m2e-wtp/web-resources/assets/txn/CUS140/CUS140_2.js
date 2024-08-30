	/*
 */
'use strict';
eSoafApp.controller('CUS140_2Controller',
	function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS140_2Controller";

		// combobox
		getParameter.XML(["CUS.IVG_TYPE", "CUS.IVG_PLAN_TYPE"], function(totas) {
			if (totas) {
				$scope.IVG_TYPE = totas.data[totas.key.indexOf('CUS.IVG_TYPE')];
				$scope.IVG_PLAN_TYPE = totas.data[totas.key.indexOf('CUS.IVG_PLAN_TYPE')];
			}
		});
		//

		// date picker
        $scope.model = {};
        $scope.open = function($event, elementOpened) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.model[elementOpened] = !$scope.model[elementOpened];
        };
        // date picker end

		$scope.init = function(){
			$scope.inputVO = {
				empID: $scope.row.CONTENT_EMP_ID,
				regionID: $scope.row.CONTENT_REGION,
				areaID: $scope.row.CONTENT_AREA,
				branchID : $scope.row.CONTENT_BRANCH,
				roleID: $scope.row.CONTENT_ROLE_ID,
				ivgResultSeq: $scope.row.IVG_RESULT_SEQ,
				ivgPlanSeq:	$scope.row.IVG_PLAN_SEQ,
				ivgType: $scope.row.IVG_TYPE,
				ivgPlanName: $scope.row.IVG_PLAN_NAME
			};
		};
		$scope.init();

		$scope.queryField = function(){
			$scope.sendRecv("CUS140", "queryField", "com.systex.jbranch.app.server.fps.cus140.CUS140InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.detailParamList = tota[0].body.resultList;
						
						//無資料flag
                		$scope.noDataflag = false;
                        //處理邏輯
                        angular.forEach($scope.detailParamList, function(rowList, index){
                        	//多筆無法刪除最後一筆，得要新增後，才能刪除
                        	var last = index;
                        	$scope.lastFieldFlag = last; 
                        	
                        	//針對日期轉型數值型態、處理COMBO、與無資料flag
                        	angular.forEach(rowList, function(row, index1){
                        		if(row.FIELD_VALUE == "" || row.FIELD_VALUE == null){
                        			$scope.noDataflag = true;
                        		}
                        		
								if(row.FIELD_TYPE == 4){
									if(row.FIELD_VALUE)
										$scope.detailParamList[index][index1].FIELD_VALUE = parseFloat(row.FIELD_VALUE);
								}
								else if(row.FIELD_TYPE == 5){
									row.DropDown = [];
									var dropCont = $scope.detailParamList[index][index1].DROPDOWN_CONTENT;
	
									var dropList = (dropCont)
										? angular.forEach(
										dropCont.split(','), function(dropDownValue) {
												row.DropDown.push({LABEL : dropDownValue, DATA : dropDownValue});
											}
										)
										: null;
	                            }
                        	})	
                        });
                        
                        //newResult
                        if($scope.noDataflag) {
                        	// 2017/7/18 detailParamList有多筆吧?
                        	$scope.newResult = angular.copy($scope.detailParamList);
                        	// old code
                        	angular.forEach($scope.newResult, function(row) {
                        		angular.forEach(row, function(row2, index2) {
                            		if(row2.FIELD_TYPE == 4){
        								if(row2.FIELD_VALUE)
        									row2.FIELD_VALUE = parseFloat(row2.FIELD_VALUE);
        							}
        							else if(row2.FIELD_TYPE == 5){
        								row2.DropDown = [];
        								var dropCont = row2.DROPDOWN_CONTENT;
        								var dropList = (dropCont)
        									? angular.forEach(
        											dropCont.split(','), function(dropDownValue) {
        												row2.DropDown.push({LABEL : dropDownValue, DATA : dropDownValue});
        											}
        									)
        									: null;
        	                        }
                            	});
                        	});
                        }
                        // old code
                        else {
                        	// 2017/7/18 我有改forEach
                        	$scope.newResult = [];
                        	$scope.newResult.push(angular.copy($scope.detailParamList[0]));
                        	angular.forEach($scope.newResult, function(row) {
                        		angular.forEach(row, function(row2, index2) {
                            		row2.IVG_FIELD_SEQ = -999;
                            		row2.FIELD_VALUE = "";
                            		
                            		// old code
                            		if(row2.FIELD_TYPE == 4){
        								if(row2.FIELD_VALUE)
        									row2.FIELD_VALUE = parseFloat(row2.FIELD_VALUE);
        							}
        							else if(row2.FIELD_TYPE == 5){
        								row2.DropDown = [];
        								var dropCont = row2.DROPDOWN_CONTENT;
        								var dropList = (dropCont)
        									? angular.forEach(
        											dropCont.split(','), function(dropDownValue) {
        												row2.DropDown.push({LABEL : dropDownValue, DATA : dropDownValue});
        											}
        									)
        									: null;
        	                        }
                            	});
                        	});
                        }
						return;
					}
				});
		}
		$scope.queryField();
        
		$scope.download = function() {
        	$scope.sendRecv("CUS130", "download", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'fileID': $scope.row.DOC_ID,'fileName': $scope.row.DOC_NAME},
					function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
			);
        };
		
        // for old code add 多筆
        $scope.btnAdd = function (detailParamList) {
        	var check = false;
        	angular.forEach(detailParamList, function(row) {
        		if(!row.FIELD_VALUE)
        			check = true;
            });
        	if(check) {
        		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
            $scope.inputVO.inputDynamicField = detailParamList;
            $scope.sendRecv("CUS140", "saveData", "com.systex.jbranch.app.server.fps.cus140.CUS140InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                		$scope.queryField();
	                	};
			});
        };
        //
        $scope.btnSave = function (detailParamList) {
        	if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
            $scope.inputVO.inputDynamicField = detailParamList;
            $scope.sendRecv("CUS140", "saveData", "com.systex.jbranch.app.server.fps.cus140.CUS140InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                		$scope.closeThisDialog('successful');
	                	};
			});
        };
        
        $scope.delField = function(row) {
			$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
				if($scope.lastFieldFlag == 0) {
					$scope.showErrorMsg('無法刪除最後一筆回報，請先新增一筆，再刪除');
	        		return;
				}
				
				$scope.inputVO.ivgPlanSeq = $scope.row.IVG_PLAN_SEQ;
				$scope.inputVO.ivgResultSeq = row[0].IVG_RESULT_SEQ;
				$scope.sendRecv("CUS140", "delField", "com.systex.jbranch.app.server.fps.cus140.CUS140InputVO", $scope.inputVO,
		    			function(totas, isError) {
		        			if (isError) {
		        				$scope.showErrorMsgInDialog(totas.body.msgData);
		        				return;
			                }
			                if (totas.length > 0) {
			                	$scope.showSuccessMsg('ehl_01_common_004');
			                	$scope.queryField();
			                };
			            }
				);
			});
		}
        
        $scope.modify = function(row) {
        	row.showSave = true;
        }
        
        $scope.save = function(row) {
        	row.showSave = false;
        	$scope.btnAdd(row);
        }
});