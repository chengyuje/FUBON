'use strict';
eSoafApp.controller('INS910Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS910Controller";
		
		// combobox
		getParameter.XML(["INS.PLAN_TYPE", "INS.PARA_HEADER_STATUS", "INS.UNIT", "INS.EARNED_CAL_WAY"], function(totas) {
			if (totas) {
				$scope.PLAN_TYPE = totas.data[totas.key.indexOf('INS.PLAN_TYPE')];
				$scope.PARA_HEADER_STATUS = totas.data[totas.key.indexOf('INS.PARA_HEADER_STATUS')];
				$scope.INS_UNIT = totas.data[totas.key.indexOf('INS.UNIT')];
				$scope.EARNED_CAL_WAY = totas.data[totas.key.indexOf('INS.EARNED_CAL_WAY')];
			}
		});
		
		// 判斷主管直接根據有無覆核權限
		if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["INS910"])
			$scope.CanConfirm = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["INS910"].FUNCTIONID["confirm"];
		else
			$scope.CanConfirm = false;
		
		$scope.init = function() {
			$scope.inputVO = {
				'ins_type': $scope.connector('get','INS910_INSTYPE')
			};
			$scope.sendRecv("INS910", "initial", "com.systex.jbranch.app.server.fps.ins910.INS910InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		$scope.inputVO.para_no = totas[0].body.para_no;
                		$scope.INIT_STATUS = totas[0].body.status;
                		$scope.inputVO.cal_desc = totas[0].body.cal_desc;
                		if(totas[0].body.reportList.length > 0) {
                			$scope.inputVO.file_seq = totas[0].body.reportList[0].KEYNO;
                    		$scope.REPORT_FILE_NAME = totas[0].body.reportList[0].FILE_NAME;
                		}
                		// suggestList
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
						DataRow.CURR_CD = temp.CURR_CD;
						DataRow.SUGGEST_TYPE = temp.SUGGEST_TYPE;
						DataRow.POLICY_AMT_DISTANCE = temp.POLICY_AMT_DISTANCE;
						DataRow.CVRG_RATIO = temp.CVRG_RATIO;
						DataRow.POLICY_AMT_MIN = temp.POLICY_AMT_MIN;
						DataRow.POLICY_AMT_MAX = temp.POLICY_AMT_MAX;
						DataRow.INSDATA_KEYNO = temp.insdata_key_no;
						// just show
						DataRow.PRD_ID = temp.PRD_ID;
						DataRow.INSPRD_NAME = temp.INSPRD_NAME;
						DataRow.INSPRD_ANNUAL = temp.INSPRD_ANNUAL;
						DataRow.PRD_UNIT = temp.PRD_UNIT;
						DataRow.MIN_AGE = temp.MIN_AGE;
						DataRow.MAX_AGE = temp.MAX_AGE;
						
						debugger
						// 退休規劃- 資產傳承
						if($scope.inputVO.ins_type == '5') {
							DataRow.ESTATE_PLAN = temp.ESTATE_PLAN ? temp.ESTATE_PLAN : 'N';
						}
						
						// 商品組合-保障- 滿期金計算類型[EARNED_CAL_WAY]、滿期金年度[EARNED_YEAR]、滿期金比[EARNED_RATIO]
						if($scope.inputVO.ins_type == '9') {
							DataRow.EARNED_CAL_WAY = temp.EARNED_CAL_WAY; // 滿期金計算類型
							DataRow.EARNED_YEAR = temp.EARNED_YEAR; // 滿期金年度
							DataRow.EARNED_RATIO = temp.EARNED_RATIO; // 滿期金比
						}
					} else {
						// 新增
						debugger
						$scope.inputVO.suggestList.push({
							'KEY_NO': temp.KEY_NO,
							'SUGGEST_TYPE' : temp.SUGGEST_TYPE,
							'POLICY_AMT_DISTANCE' : temp.POLICY_AMT_DISTANCE,
							'CVRG_RATIO' : temp.CVRG_RATIO,
							'POLICY_AMT_MIN' : temp.POLICY_AMT_MIN,
							'POLICY_AMT_MAX' : temp.POLICY_AMT_MAX,
							'INSDATA_KEYNO' : temp.insdata_key_no,
							'PRD_ID' : temp.PRD_ID,
							'INSPRD_NAME' : temp.INSPRD_NAME,
							'INSPRD_ANNUAL' : temp.INSPRD_ANNUAL,
							'PRD_UNIT' : temp.PRD_UNIT,
							'MIN_AGE' : temp.MIN_AGE,
							'MAX_AGE' : temp.MAX_AGE,
							'CURR_CD' : temp.CURR_CD,
							'ESTATE_PLAN' : ($scope.inputVO.ins_type == '5') ?　(temp.ESTATE_PLAN ? temp.ESTATE_PLAN : 'N' ): undefined, // 資產傳承
							'EARNED_CAL_WAY' : ($scope.inputVO.ins_type == '9') ?　temp.EARNED_CAL_WAY : undefined, // 滿期金計算類型
							'EARNED_YEAR' : ($scope.inputVO.ins_type == '9') ?　temp.EARNED_YEAR : undefined, // 滿期金年度
							'EARNED_RATIO' : ($scope.inputVO.ins_type == '9') ?　temp.EARNED_RATIO : undefined // 滿期金比
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
          	  return {'count': 1, 'OBJ': e.PRD_ID + ':' + e.INSPRD_ANNUAL}
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
        	$scope.sendRecv("INS910", "goReview", "com.systex.jbranch.app.server.fps.ins910.INS910InputVO", $scope.inputVO,
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
				$scope.sendRecv("INS910", "review", "com.systex.jbranch.app.server.fps.ins910.INS910InputVO", {'para_no': $scope.inputVO.para_no, 'status': status, 'ins_type' : $scope.inputVO.ins_type},
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