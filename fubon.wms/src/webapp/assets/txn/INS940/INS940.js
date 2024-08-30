'use strict';
eSoafApp.controller('INS940Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS940Controller";
		
		// combobox
		getParameter.XML(["INS.PARA_HEADER_STATUS", "COMMON.YES_NO", "INS.CARE_WAY", "INS.CARE_STYLE", "INS.UNIT", "INS.PARA_NO4_SUGGEST_TYPE"], function(totas) {
			if (totas) {
				$scope.PARA_HEADER_STATUS = totas.data[totas.key.indexOf('INS.PARA_HEADER_STATUS')];
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.CARE_WAY = totas.data[totas.key.indexOf('INS.CARE_WAY')];
				$scope.CARE_STYLE = totas.data[totas.key.indexOf('INS.CARE_STYLE')];
				$scope.INS_UNIT = totas.data[totas.key.indexOf('INS.UNIT')];
				$scope.PARA_NO4_SUGGEST_TYPE = totas.data[totas.key.indexOf('INS.PARA_NO4_SUGGEST_TYPE')];
			}
		});
		
		// 判斷主管直接根據有無覆核權限
		if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["INS940"])
			$scope.CanConfirm = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["INS940"].FUNCTIONID["confirm"];
		else
			$scope.CanConfirm = false;
		// 排序用
		$scope.sortableOptions = {
			stop: function(e, ui) {
				angular.forEach(ui.item.sortable.droptargetModel, function(row, index) {
					row.SEQ_NUM = index + 1;
				});
			},
			disabled: $scope.CanConfirm
		};
		
		$scope.init = function() {
			$scope.inputVO = {
				cal_desc: '',
				typeCancer: 'N',
				typeMajor: 'N',
				typeLt: 'N',
				diseaseList: []
			};
			$scope.DisEditIndex = null;
			$scope.sendRecv("INS940", "initial", "com.systex.jbranch.app.server.fps.ins940.INS940InputVO", {},
				function(totas, isError) {
                	if (!isError) {
                		$scope.inputVO.para_no = totas[0].body.para_no;
                		$scope.INIT_STATUS = totas[0].body.status;
                		$scope.inputVO.cal_desc = totas[0].body.cal_desc;
                		$scope.inputVO.diseaseList = totas[0].body.diseaseList;
                		angular.forEach($scope.inputVO.diseaseList, function(row, index) {
                			row.set = [];
							row.set.push({LABEL: "修改", DATA: "U"});
							row.set.push({LABEL: "刪除", DATA: "D"});
    						// 讓之後編輯能快速找到那筆, 定一個ID
    						row.ROW_ID = "DISEASE" + (index + 1);
    					});
                		$scope.inputVO.ltcareList = totas[0].body.ltcareList;
                		if(totas[0].body.reportList.length > 0) {
                			$scope.inputVO.file_seq = totas[0].body.reportList[0].KEYNO;
                    		$scope.REPORT_FILE_NAME = totas[0].body.reportList[0].FILE_NAME;
                		}
                		// init_suggest
                		$scope.inputVO.suggestList = totas[0].body.suggestList;
                	};
				}
    		);
		};
		$scope.init();
		
		$scope.downloadDoc = function() {
			$scope.sendRecv("INS930", "downloadDoc", "com.systex.jbranch.app.server.fps.ins930.INS930InputVO", {'para_no': $scope.inputVO.file_seq},
				function(totas, isError) {
                	if (!isError) {
                		
                	};
				}
    		);
		};
		$scope.uploadT = function () {
			var btn = angular.element(document.getElementById('fileup'));
			btn.trigger('click');
        };
        $scope.finishUpload = function (name, rname) {
            if(!name) {
            	$scope.showErrorMsg('欄位檢核錯誤：請選擇上傳檔案');
            	return;
    		}
            $scope.inputVO.file_seq = null;
            $scope.inputVO.fileName = name;
            $scope.inputVO.fileRealName = rname;
            $scope.REPORT_FILE_NAME = rname;
        };
		
		$scope.addDis = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if($scope.inputVO.typeCancer != 'Y' && $scope.inputVO.typeMajor != 'Y' && $scope.inputVO.typeLt != 'Y') {
				$scope.showErrorMsg("對應類型至少勾選一個");
				return;
	    	}
			$scope.inputVO.diseaseList.push({'SEQ_NUM': 999, 'DIS_NAME': $scope.inputVO.disName, 'DIS_DESC': $scope.inputVO.disDesc, 'TYPE_CANCER': $scope.inputVO.typeCancer, 'TYPE_MAJOR': $scope.inputVO.typeMajor, 'TYPE_LT': $scope.inputVO.typeLt, 'set': [{LABEL: "修改", DATA: "U"}, {LABEL: "刪除", DATA: "D"}]});
			angular.forEach($scope.inputVO.diseaseList, function(row, index) {
				row.SEQ_NUM = index + 1;
				// 讓之後編輯能快速找到那筆, 定一個ID
				row.ROW_ID = "DISEASE" + (index + 1);
			});
			$scope.inputVO.disName = "";
			$scope.inputVO.disDesc = "";
			$scope.inputVO.typeCancer = "N";
			$scope.inputVO.typeMajor = "N";
			$scope.inputVO.typeLt = "N";
			$scope.showSuccessMsg('ehl_01_common_004');
		};
		
		$scope.editDis = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if($scope.inputVO.typeCancer != 'Y' && $scope.inputVO.typeMajor != 'Y' && $scope.inputVO.typeLt != 'Y') {
				$scope.showErrorMsg("對應類型至少勾選一個");
				return;
	    	}
			var newIndex = $scope.inputVO.diseaseList.map(function(e) { return e.ROW_ID; }).indexOf($scope.DisEditIndex);
			$scope.inputVO.diseaseList[newIndex].DIS_NAME = angular.copy($scope.inputVO.disName);
			$scope.inputVO.diseaseList[newIndex].DIS_DESC = angular.copy($scope.inputVO.disDesc);
			$scope.inputVO.diseaseList[newIndex].TYPE_CANCER = angular.copy($scope.inputVO.typeCancer);
			$scope.inputVO.diseaseList[newIndex].TYPE_MAJOR = angular.copy($scope.inputVO.typeMajor);
			$scope.inputVO.diseaseList[newIndex].TYPE_LT = angular.copy($scope.inputVO.typeLt);
			$scope.DisEditIndex = null;
			$scope.inputVO.disName = "";
			$scope.inputVO.disDesc = "";
			$scope.inputVO.typeCancer = "N";
			$scope.inputVO.typeMajor = "N";
			$scope.inputVO.typeLt = "N";
			$scope.showSuccessMsg('ehl_01_common_004');
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
						$scope.DisEditIndex = null;
						var delIndex = $scope.inputVO.diseaseList.indexOf(row);
						$scope.inputVO.diseaseList.splice(delIndex, 1);
						angular.forEach($scope.inputVO.diseaseList, function(row, index) {
							row.SEQ_NUM = index + 1;
							// 讓之後編輯能快速找到那筆, 定一個ID
							row.ROW_ID = "DISEASE" + (index + 1);
						});
						$scope.inputVO.disName = "";
						$scope.inputVO.disDesc = "";
						$scope.inputVO.typeCancer = "N";
						$scope.inputVO.typeMajor = "N";
						$scope.inputVO.typeLt = "N";
						$scope.showSuccessMsg('ehl_01_common_004');
		            });
				} else {
					$scope.DisEditIndex = angular.copy(row.ROW_ID);
					$scope.inputVO.disName = row.DIS_NAME;
					$scope.inputVO.disDesc = row.DIS_DESC;
					$scope.inputVO.typeCancer = row.TYPE_CANCER;
					$scope.inputVO.typeMajor = row.TYPE_MAJOR;
					$scope.inputVO.typeLt = row.TYPE_LT;
				}
				row.cmbAction = "";
			}
		};
		
		$scope.clearDis = function() {
			$scope.DisEditIndex = null;
			$scope.inputVO.disName = "";
			$scope.inputVO.disDesc = "";
			$scope.inputVO.typeCancer = "N";
			$scope.inputVO.typeMajor = "N";
			$scope.inputVO.typeLt = "N";
		};
		
		$scope.editData = function(DataRow) {
        	var dialog = ngDialog.open({
				template: 'assets/txn/INS910/INS910_AddData.html',
				className: 'INS910_AddData',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.DataRow = angular.copy(DataRow);
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value != "cancel") {
					var temp = angular.copy(data.value);
					if(DataRow) {
						// 編輯
						DataRow.KEY_NO = temp.KEY_NO;
						DataRow.SUGGEST_TYPE = temp.SUGGEST_TYPE;
						DataRow.POLICY_AMT_DISTANCE = temp.POLICY_AMT_DISTANCE;
						DataRow.CVRG_RATIO = temp.CVRG_RATIO;
						DataRow.POLICY_AMT_MIN = temp.POLICY_AMT_MIN;
						DataRow.POLICY_AMT_MAX = temp.POLICY_AMT_MAX;
						// just show
						DataRow.PRD_ID = temp.PRD_ID;
						DataRow.INSPRD_NAME = temp.INSPRD_NAME;
						DataRow.INSPRD_ANNUAL = temp.INSPRD_ANNUAL;
						DataRow.PRD_UNIT = temp.PRD_UNIT;
						DataRow.MIN_AGE = temp.MIN_AGE;
						DataRow.MAX_AGE = temp.MAX_AGE;
						DataRow.INSDATA_KEYNO = temp.insdata_key_no;
					} else {
						// 新增
						$scope.inputVO.suggestList.push({
							'KEY_NO': temp.KEY_NO,
							'SUGGEST_TYPE' : temp.SUGGEST_TYPE,
							'POLICY_AMT_DISTANCE' : temp.POLICY_AMT_DISTANCE,
							'CVRG_RATIO' : temp.CVRG_RATIO,
							'POLICY_AMT_MIN' : temp.POLICY_AMT_MIN,
							'POLICY_AMT_MAX' : temp.POLICY_AMT_MAX,
							'PRD_ID' : temp.PRD_ID,
							'INSPRD_NAME' : temp.INSPRD_NAME,
							'INSPRD_ANNUAL' : temp.INSPRD_ANNUAL,
							'PRD_UNIT' : temp.PRD_UNIT,
							'MIN_AGE' : temp.MIN_AGE,
							'MAX_AGE' : temp.MAX_AGE,
							'INSDATA_KEYNO' : temp.insdata_key_no,
						});
					}
				}
			});
        };
        
        $scope.delData = function(DataRow) {
        	$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
        		var delIndex = $scope.inputVO.suggestList.indexOf(DataRow);
				$scope.inputVO.suggestList.splice(delIndex, 1);
        	});
        };
        
        $scope.goReview = function() {
        	// check suggestList
        	var uniq = $scope.inputVO.suggestList.map((e) => {
          	  return {'count': 1, 'OBJ': e.PRD_ID + ':' + $filter('mapping')(e.SUGGEST_TYPE, $scope.PARA_NO4_SUGGEST_TYPE) + ':' + e.INSPRD_ANNUAL}
          	})
          	.reduce((a, b) => {
          	  a[b.OBJ] = (a[b.OBJ] || 0) + b.count
          	  return a
          	}, {});
          	var duplicates = Object.keys(uniq).filter((a) => uniq[a] > 1);
          	if(duplicates.length > 0) {
          		$scope.showErrorMsg('有同樣的險種代碼及繳費年期: ' + duplicates + '，請重新修改。');
          		return;
          	}
        	//
        	$scope.sendRecv("INS940", "goReview", "com.systex.jbranch.app.server.fps.ins940.INS940InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		$scope.showSuccessMsg('ehl_01_common_006');
                		$scope.init();
                	};
				}
    		);
        };
        
        $scope.review = function (status) {
			$confirm({text: '是否' + ((status == 'R') ? '退回' : '核可')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("INS940", "review", "com.systex.jbranch.app.server.fps.ins940.INS940InputVO", {'para_no': $scope.inputVO.para_no, 'status': status},
					function(totas, isError) {
	                	if (!isError) {
	                		$scope.showSuccessMsg((status == 'R') ? "ehl_01_common_020" : "ehl_01_common_021");
	                		$scope.init();
	                	};
					}
	    		);
			});
		};
		
		
		
		
});