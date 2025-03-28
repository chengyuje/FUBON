
'use strict';
eSoafApp.controller('PMS432Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService) {
	$controller('BaseController', { $scope: $scope });
	$scope.controllerName = "PMS432Controller";
	
	debugger;
	
	// 修改頁籤寬度
	$("#eJumping select").css({
		"width":      "95px",
		"text-align": "center",
		"height":     "30px",
		"display":    "inline",
	});

	getParameter.XML(["PMS.SIMILAR_INFO", "PMS.COMPARE_SOURCE", "PMS.COMPARE_RESULT", "PMS.CHECK_RESULT_YN", "PMS.RELATION_YN", "PMS.RELATION"], function(totas) {
		if (totas) {
			$scope.similarInfoList = totas.data[totas.key.indexOf("PMS.SIMILAR_INFO")];
			$scope.compareSourceList = totas.data[totas.key.indexOf("PMS.COMPARE_SOURCE")];
			$scope.compareResultList = totas.data[totas.key.indexOf("PMS.COMPARE_RESULT")];
			$scope.checkResult_YN = totas.data[totas.key.indexOf("PMS.CHECK_RESULT_YN")];
			$scope.relationList = totas.data[totas.key.indexOf("PMS.RELATION")];
			$scope.relation_YN = totas.data[totas.key.indexOf("PMS.RELATION_YN")];
		}
	});

	$scope.init = function() {
		$scope.inputVO = {
			checkInterval: "",		//查核區間
			similarInfo: "", 		//相似項目
			compareSource: "", 		//比對檔案來源
			compareResult: "",		//比對結果
			custID: "",				//客戶ID
			empCustID: "",          //理專ID
			compareType: "",
			checkedResult: "",
			list: [],
			uploadMark: false	    //上傳
		};
		$scope.paramList = [];
		$scope.outputVO = {};
		$scope.sourceTypeList = [];
		$scope.exportList = [];
		$scope.tmpList = [];
	};
	$scope.init();

	$scope.inquireInit = function() {
		$scope.paramList = [];
		$scope.outputVO = {};
		$scope.sourceTypeList = [];
		$scope.inputVO.list = [];
		$scope.exportList = [];
		$scope.tmpList = [];
		$scope.inputVO.compareType = "";
	};
	$scope.inquireInit();

	//查核區間初始化
	$scope.initCheckInterval = function() {
		$scope.checkIntervalList = [];
		$scope.sendRecv("PMS432", "initCheckInterval", "com.systex.jbranch.app.server.fps.pms432.PMS432InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.checkIntervalList = tota[0].body.resultList;
				}
			});
	};
	$scope.initCheckInterval();
	
	function getNewList(resultList) {
		var list = [];
		angular.forEach(resultList, function(data, idx) {
			debugger;
			var disabled = false;
			var chkResult = "";
			var insurNo = "";
			var othRel = "";
			var rel = "";
			var selected = false;
			if (data.CHECKED_RESULT != null) {
				//已維護過的資料(儲存後)，直接設值
				chkResult = data.CHECKED_RESULT;
				insurNo = data.INSURANCE_NO;
				othRel = data.OTHER_REL;
				rel = data.RELATION;
				disabled = true;
			} else if ($scope.tmpList.length > 0) {
				//把在其他分頁需要維護的資料(儲存前)
				//再把值重新塞回去
				angular.forEach($scope.tmpList, function(tmpData, tmpIdx) {
					if (data.SEQ === tmpData.SEQ) {
						chkResult = tmpData.CHECKED_RESULT;
						insurNo = tmpData.INSURANCE_NO;
						othRel = tmpData.OTHER_REL;
						rel = tmpData.RELATION;
						if (tmpData.SELECTED) {
							selected = true;
						}
					}
				});

			}
			data = {
				...data, CHECKED_RESULT: chkResult,
				INSURANCE_NO: insurNo, OTHER_REL: othRel,
				RELATION: rel, SELECTED: selected,
				chkBoxDisabled: disabled
			}
			list.push(data);
		});
		return list;
	};
	
	//tmpList obj 不存在 => push，已存在 => 取代
	function dealTmpList() {
		angular.forEach($scope.paramList, function(data, idx) {
			if ($scope.tmpList.length == 0) {
				$scope.tmpList.push(data);
			} else {
				var needPush = true;
				for(var tmpIdx = 0; tmpIdx < $scope.tmpList.length; tmpIdx++) {
					var tmpData = $scope.tmpList[tmpIdx];
					if (data.SEQ === tmpData.SEQ) {
						//取代
						$scope.tmpList.splice(tmpIdx, 1, data);
						needPush = false;
						break;
					}
				}
				if (needPush) {
					$scope.tmpList.push(data);
				}
			}
		});
	};
	
	//查詢
	$scope.query = function() {
		if ($scope.inputVO.checkInterval == "") {
			$scope.showMsg("請選擇查核區間!");
			return;
		}
		if ($scope.inputVO.similarInfo == "") {
			$scope.showMsg("請選擇相似項目!");
			return;
		}
		if ($scope.inputVO.compareSource == "" && $scope.inputVO.compareResult != "2") {
			$scope.showMsg("請選擇比對檔案來源!");
			return;
		}
		if ($scope.inputVO.compareResult == "") {
			$scope.showMsg("請選擇比對結果!");
			return;
		}

		if ($scope.inputVO.compareResult != 1) {
			$scope.sendRecv("PMS432", "query", "com.systex.jbranch.app.server.fps.pms432.PMS432InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						const { resultList } = tota[0].body;
						if (resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.paramList = getNewList(resultList);
						dealTmpList();
						$scope.totalData = tota[0].body.totalList;
						$scope.outputVO = tota[0].body;
						$scope.exportList = angular.copy(tota[0].body.totalList);
						$scope.inputVO.list = tota[0].body.totalList;

						if ($scope.inputVO.compareResult != '2') {
							//頁面[比對檔案來源]動態顯示
							$scope.sourceTypeList = [
								...new Set(
									$scope.totalData.map(obj => obj.SOURCE_TYPE).sort()
								)
							];
						}

					}
				});
		}
	};
	
	
	$scope.conditionReset = function() {
		if ($scope.inputVO.checkInterval == "" 
			  || $scope.inputVO.similarInfo == "" 
		      || $scope.inputVO.compareSource == "" 
			  || $scope.inputVO.compareResult == "") {
			$scope.inputVO.custID = "";
			$scope.inputVO.empCustID = "";
			$scope.inputVO.checkedResult = "";
		}
	};

	$scope.checkRelation = function(row) {
		if (row.RELATION != "9" && row.OTHER_REL != "") {
			row.OTHER_REL = "";
		}
	};
	
	$scope.rowClicked = function(row) {
		if(!row.SELECTED){
			row.CHECKED_RESULT = "";
			row.RELATION = "";
			row.OTHER_REL = "";
			row.INSURANCE_NO = "";
		}
	};

	//儲存
	$scope.save = function(type) {
		var updateList = [];
		$scope.inputVO.compareType = type;
		for (var i = 0; i < $scope.tmpList.length; i++) {
			var data = $scope.tmpList[i];
			if (data.SELECTED) {
				if (data.CHECKED_RESULT == "Y" || data.CHECKED_RESULT == "N") {
					if (data.RELATION == "" || data.INSURANCE_NO == "") {
						$scope.showMsg("請選擇關係說明以及填寫保險文件編號");
						return;
					} else {
						if (data.RELATION == "9" && (data.OTHER_REL == "" || data.OTHER_REL == null)) {
							$scope.showMsg("請填寫其他關係欄位!");
							return;
						}
					}
					if (data.CHECKED_RESULT == "Y") {
						data.MATCH_YN = "Y";
					} else {
						data.MATCH_YN = "N";
					}
					updateList.push(data);
				} else if (data.SELECTED) {
					$scope.showMsg("勾選的資料列，請選擇查核結果、關係說明以及填寫保險文件編號!");
					return;
				}
			}
		}
		if (updateList.length > 0) {
			$scope.inputVO.list = updateList;
			$scope.sendRecv("PMS432", "checkUpdateInsert", "com.systex.jbranch.app.server.fps.pms432.PMS432InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_025');
						$scope.inquireInit();
						$scope.query();
					}
				});
		} else {
			$scope.showMsg("請勾選資料!");
		}

	};

	//匯出
	$scope.export = function() {
		if ($scope.exportList.length > 0) {
			$scope.inputVO.list = $scope.exportList;
			$scope.sendRecv("PMS432", "export", "com.systex.jbranch.app.server.fps.pms432.PMS432InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg("資料匯出成功!");
					}
				});
		} else {
			$scope.showMsg('目前沒有資料，請先查詢!');
		}
	};

	//上傳
	$scope.upload = function() {
		$scope.inputVO.uploadMark = true;
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS432/PMS432_UPLOAD.html',
			className: 'PMS432',
			showClose: false,
			controller: ['$scope', function($scope) {
				
			}]
		});
		dialog.closePromise.then(function(data) {
			if (data.value != "cancel" && data.value.resultList.length > 0) {
				const {resultList, compareType} = data.value;
				$scope.inputVO.compareType = compareType;
				$scope.inputVO.list = resultList;
				$scope.sendRecv("PMS432", "checkUpdateInsert", "com.systex.jbranch.app.server.fps.pms432.PMS432InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg("資料上傳成功!");
							$scope.inputVO.uploadMark = false;
							$scope.inquireInit();
						}
					});
			} else if (data.value == "cancel") {
				$scope.inputVO.uploadMark = false;
			}
		});
	};
});