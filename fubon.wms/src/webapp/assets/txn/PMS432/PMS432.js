
'use strict';
eSoafApp.controller('PMS432Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService) {
	$controller('BaseController', { $scope: $scope });
	$scope.controllerName = "PMS432Controller";
	
	// 修改頁籤寬度
	$("#eJumping select").css({
		"width":      "85px",
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
			list: [],
			uploadMark: false	    //上傳
		};
		$scope.paramList = [];
		$scope.outputVO = {};
		$scope.sourceTypeList = [];
		$scope.exportList = [];
	};
	$scope.init();

	$scope.inquireInit = function() {
		$scope.paramList = [];
		$scope.outputVO = {};
		$scope.sourceTypeList = [];
		$scope.inputVO.list = [];
	};
	$scope.inquireInit();

	//查核區間初始化
	$scope.initCheckInterval = function() {
		$scope.checkIntervalList = [];
		$scope.sendRecv("PMS432", "initCheckInterval", "com.systex.jbranch.app.server.fps.pms432.PMS432InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					angular.forEach(tota[0].body.resultList, function(data, idx) {
						$scope.checkIntervalList.push(
							{
								LABEL: data.YYYYMM, DATA: data.YYYYMM,
								orgLabel: data.YYYYMM, label: data.YYYYMM, value: data.YYYYMM
							}
						);
					});
				}
			});
	};
	$scope.initCheckInterval();

	//查詢
	$scope.query = function(str) {
		if (str == "save") {
			$scope.inputVO.list = [];
		}
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
						var newList = [];
						angular.forEach(resultList, function(data, idx) {
							var disabled = false;
							if (data.CHECKED_RESULT == "Y" && data.INSURANCE_NO != null) {
								if (data.RELATION == "9" && data.OTHER_REL != null
									|| data.RELATION != null) {
									disabled = true;
								}
							}
							data = {
								...data, SELECTED: false,
								chkBoxDisbaled: disabled
							};
							newList.push(data);
						});

						$scope.paramList = newList;
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
		}
	};

	// 查核結果選無關係 => 重製關係說明 其他關係 證明文件說明 欄位
	$scope.checkReset = function(row) {
		if (row.CHECKED_RESULT == 'N') {
			row.RELATION = "";
			row.OTHER_REL = "";
			row.INSURANCE_NO = "";
		}
	};

	//重新比對
//	$scope.reCompare = function(str) {
//		$scope.sendRecv("PMS432", "reCompare", "com.systex.jbranch.app.server.fps.pms432.PMS432InputVO", $scope.inputVO,
//			function(tota, isError) {
//				if (!isError) {
//					if(str != "upload"){
//						$scope.query("reCompare");
//					}
//				}
//			});
//	};

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
	$scope.save = function() {
		var updateList = [];
		for (var i = 0; i < $scope.paramList.length; i++) {
			var data = $scope.paramList[i];
			if (data.SELECTED) {
				if (data.CHECKED_RESULT == "Y") {
					if (data.RELATION == "" || data.INSURANCE_NO == "") {
						$scope.showMsg("請選擇關係說明以及填寫保險文件編號");
						return;
					} else {
						if (data.RELATION == "9" && data.OTHER_REL == "") {
							$scope.showMsg("請填寫其他關係欄位!");
							return;
						}
					}
					data.SELECTED = false;
					data.MATCH_YN = "Y";
					updateList.push(data);
				} else {
					if (data.CHECKED_RESULT == "N") {
						data.MATCH_YN = "N";
						data.SELECTED = false;
						updateList.push(data);
					} else if (data.SELECTED) {
						$scope.showMsg("勾選的資料列，請選擇查核結果、關係說明以及填寫保險文件編號!");
						return;
					}
				}
			}
		}
		
		if (updateList.length > 0) {
			$scope.inputVO.list = updateList;
			$scope.sendRecv("PMS432", "checkUpdateInsert", "com.systex.jbranch.app.server.fps.pms432.PMS432InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_025');
						$scope.query("save");
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
			if (data.value != "cancel" && data.value.length > 0) {
				var list = [];
				angular.forEach(data.value, function(row, idx) {
					row = {...row, CHECKED_RESULT: "Y"};
					list.push(row);
				});
				$scope.inputVO.list = list;
				$scope.sendRecv("PMS432", "checkUpdateInsert", "com.systex.jbranch.app.server.fps.pms432.PMS432InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg("資料上傳成功!");
							$scope.inputVO.uploadMark = false;
						}
					});
			} else if (data.value == "cancel") {
				$scope.inputVO.uploadMark = false;
			}
		});
	};
});