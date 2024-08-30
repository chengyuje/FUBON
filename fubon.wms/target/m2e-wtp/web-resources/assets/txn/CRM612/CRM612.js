/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM612Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $filter, $timeout, getParameter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "CRM612Controller";

	$scope.role = projInfoService.getPriID();

	// bra
	$scope.mappingSet['branchsDesc'] = [];
	angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs) {
		$scope.mappingSet['branchsDesc'].push({
			LABEL : row.BRANCH_NAME,
			DATA : row.BRANCH_NBR
		});
	});

	// xml
	getParameter.XML([ 'CRM.VIP_DEGREE', 'CRM.COM_EXPERIENCE', 'KYC.REC_TYPE', 'FPS.CUST_RISK_ATTR' ], function(totas) {
		if (len(totas) > 0) {
			$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
			$scope.com_expList = totas.data[totas.key.indexOf('CRM.COM_EXPERIENCE')];
			$scope.mappingSet['KYC.REC_TYPE'] = totas.data[totas.key.indexOf('KYC.REC_TYPE')];
			$scope.mappingSet['FPS.CUST_RISK_ATTR'] = totas.data[totas.key.indexOf('FPS.CUST_RISK_ATTR')];
		}
	});

	// 初始化
	$scope.init = function() {
		// 客戶首頁傳來
		// $scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
		$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
		// $scope.inputVO.cust_id = 'N120913984';

		$scope.ao_check = String(sysInfoService.getAoCode());
		$scope.inputVO.assets_name = '';
		$scope.inputVO.assets_amt = '';
		$scope.inputVO.assets_note = '';
		$scope.inputVO.group_id = '';
		$scope.inputVO.group_name = '';
		$scope.inputVO.ao_code = '';
		$scope.inputVO.com_experience = '';
		$scope.groups = '';
		$scope.emp_name = String(sysInfoService.getUserName());

	}

	$scope.init();

	// cust區域初始化
	$scope.init_cust = function() {
		$scope.inputVO.decis = '';
		$scope.inputVO.ao_best_visit_datetime = undefined;
	}

	$scope.init_cust();

	// 輸出欄初始化
	$scope.inquireInit = function() {
		$scope.resultList = [];
		$scope.resultList_kyc = [];
//		$scope.resultList_group = [];
		$scope.resultList_assets = [];
		$scope.resultList_voc = [];
	}

	$scope.inquireInit();

	// date picker
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};

	$scope.getGroups = function() {
		$scope.sendRecv("CRM612", "getGroups", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", {}, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsg(totas[0].body.msgData);
			}
			if (totas.length > 0) {
				$scope.mappingSet['Groups'] = [];
				angular.forEach(totas[0].body.resultList, function(row, index, objs) {
					$scope.mappingSet['Groups'].push({
						LABEL : row.GROUP_NAME,
						DATA : row
					});
				});
			}
			;
		});
	};

	$scope.getGroups();

	// 理專人員(002/003)限定
	if (projInfoService.getPriID() == "002" || projInfoService.getPriID() == "003") {
		$scope.kyc_action = '列印KYC問卷';
	} else {
		// 非理專disable 按鈕
		$scope.myvalue = true;
	}
	// 功能切換
	$scope.kyc = function() {
		// 列印KYC問卷檔案
		$scope.sendRecv("KYC310", "printBlank", "com.systex.jbranch.app.server.fps.kyc310.KYC310InputVO", {
			'CUST_ID' : $scope.inputVO.cust_id,
			'COMMENTARY_STAFF' : projInfoService.getUserID()
		}, function(tota, isError) {
			// if(tota[0].body == true){
			// $scope.showMsg("列印空白");
			// }
		});
	}

	// KYC查詢
	$scope.kyc_inquire = function() {
		console.log('kyc_inquire');
		$scope.sendRecv("CRM612", "kyc_inquire", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}
			if (tota[0].body.resultList_kyc.length == 0) {
				// $scope.showMsg("ehl_01_common_009");
				return;
			}
			$scope.resultList_kyc = tota[0].body.resultList_kyc;
			$scope.outputVO_kyc = tota[0].body;
		})
	};
	$scope.kyc_inquire();

	// assets查詢
	$scope.assets_inquire = function() {
		console.log('assets_inquire');
		$scope.sendRecv("CRM612", "assets_inquire", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}
			if (tota[0].body.resultList_assets != null && tota[0].body.resultList_assets.length > 0) {
				$scope.resultList_assets = tota[0].body.resultList_assets;
				$scope.outputVO_assets = tota[0].body;
			}
		})
	};
	$scope.assets_inquire();

	// assets新增
	$scope.assets_add = function() {
		$scope.sendRecv("CRM612", "assets_add", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				$scope.init();
				$scope.resultList_assets = [];
				$scope.assets_inquire();
			}
			;
		});
	};

	// assets修改
	$scope.assets_edit = function(row_assets) {
		console.log('edit data=' + JSON.stringify(row_assets));
		var dialog = ngDialog.open({
			template : 'assets/txn/CRM612/CRM612_assets_edit.html',
			className : 'CRM612_assets_edit',
			controller : [ '$scope', function($scope) {
				$scope.row_assets = row_assets;
			} ]
		});
		dialog.closePromise.then(function(data) {
			if (data.value === 'successful') {
				$scope.init();
				$scope.resultList_assets = [];
				$scope.assets_inquire();
			}
		});
	};

	// assets刪除資料
	$scope.assets_delete = function(row_assets) {
		$confirm({
			text : '是否刪除此筆資料!!'
		}, {
			size : 'sm'
		}).then(function() {
			$scope.inputVO.assets_id = row_assets.ASSETS_ID;
			$scope.sendRecv("CRM612", "assets_delete", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}
				if (totas.length > 0) {
					$scope.showSuccessMsg('ehl_01_common_003');
					$scope.init();
					$scope.resultList_assets = [];
					$scope.assets_inquire();
				}
				;
			});
		});
	}

	// group查詢
	$scope.group_inquire = function() {
		console.log('group_inquire');
		$scope.sendRecv("CRM612", "group_inquire", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}
			if (tota[0].body.resultList_group != null && tota[0].body.resultList_group.length > 0) {
				$scope.resultList_group = tota[0].body.resultList_group;
				$scope.outputVO_group = tota[0].body;
			}
		})
	};
	$scope.group_inquire();
	
	// 保管箱查詢
	$scope.voc_inquire = function() {
		console.log('voc_inquire');
		$scope.sendRecv("CRM612", "voc_inquire", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}
			if (tota[0].body.resultList_voc != null && tota[0].body.resultList_voc.length > 0) {
				$scope.resultList_voc = tota[0].body.resultList_voc;
				$scope.outputVO_voc = tota[0].body;
			}
		})
	};
	$scope.voc_inquire();

	// group加入
	$scope.group_join = function() {
		$scope.inputVO.group_id = $scope.groups.GROUP_ID;
		$scope.inputVO.group_name = $scope.groups.GROUP_NAME;
		$scope.sendRecv("CRM612", "group_join", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				// $scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				$scope.showMsg('ehl_01_common_001');
				$scope.init();
				$scope.resultList_group = [];
				$scope.group_inquire();
			}
			;
		});
	}

	// group刪除
	$scope.group_delete = function(row_group) {
		$confirm({
			text : '是否刪除此群組!!'
		}, {
			size : 'sm'
		}).then(function() {
			$scope.inputVO.group_id = row_group.GROUP_ID;
			$scope.sendRecv("CRM612", "group_delete", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}
				if (totas.length > 0) {
					$scope.showSuccessMsg('ehl_01_common_003');
					$scope.init();
					$scope.resultList_group = [];
					$scope.group_inquire();
				}
				;
			});
		});
	};

	// group新增
	$scope.group_add = function() {
		var ao_code = $scope.custVO.AO_CODE;
		var dialog = ngDialog.open({
			template : 'assets/txn/CRM612/CRM612_group_add.html',
			className : 'CRM612_group_add',
			controller : [ '$scope', function($scope) {
				$scope.ao_code = ao_code;
			} ]
		});

		dialog.closePromise.then(function(data) {
			if (data.value === 'successful') {
				$scope.init();
				$scope.getGroups();
			}
		});
	}

	// cust 取得客戶投資經驗
	$scope.sendRecv("SOT701", "getCustComExp", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {
		'custID' : $scope.inputVO.cust_id
	}, function(tota, isError) {
		if (!isError) {
			if (tota[0].body != null) {

				// var expList = tota[0].body.trim().split(",");
				var expList = tota[0].body;
				var store;
				if (expList.length > 0) {
					for (var i = 0; i < expList.length - 1; i++) {
						if (expList.substr(i, 1) == store) {
							$scope.com_expList[i].SELECTED = true;
						}
					}
				}

			}
		}
	});

	// cust查詢
	$scope.cust_inquire = function() {
		console.log('cust_inquire');
		$scope.sendRecv("CRM612", "cust_inquire", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}

			if (tota[0].body.resultList_cust != null && tota[0].body.resultList_cust.length > 0) {
				$scope.resultList_cust = tota[0].body.resultList_cust;

				$scope.inputVO.cust_id = $scope.resultList_cust[0].CUST_ID;
				$scope.inputVO.decis = $scope.resultList_cust[0].DECIS;
				$scope.inputVO.ao_best_visit_datetime = $scope.toJsDate($scope.resultList_cust[0].AO_BEST_VISIT_DATETIME);
			}
		})
	};
	$timeout(function() {
		$scope.cust_inquire();
	}, 100);

	$scope.saveDecis = function() {
		for (var i = 0; i < $scope.com_expList.length; i++) {
			if ($scope.com_expList[i].SELECTED == true) {
				$scope.inputVO.com_experience += 'Y';
			} else {
				$scope.inputVO.com_experience += 'N';
			}
		}

		$scope.sendRecv("CRM612", "cust_edit", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				$scope.showMsg('ehl_01_common_006');
				$scope.init_cust();
				$scope.cust_inquire();
			}
			;
		});
	}

	$scope.checkKYC = function() {
		// 查詢當日是否有承作最新一筆KYC問卷[Q8買過商品]未勾選項(6)衍生性金融商品(包括結構型商品等)。
		$scope.sendRecv("CRM612", "checkKYC", "com.systex.jbranch.app.server.fps.crm612.CRM612InputVO", {
			'cust_id' : $scope.inputVO.cust_id
		}, function(totas, isError) {
			if (!isError) {
				if (totas[0].body.resultList.length > 0) {
					var answer = totas[0].body.resultList[0].ANSWER_2;
					if (answer.includes("6")) {
						$scope.cust_edit();
					} else {
						var txtMsg = "";
						txtMsg = $filter('i18n')('ehl_01_CRM612_01');
						$confirm({
							text : txtMsg
						}, {
							size : 'sm'
						}).then(function() {
							$scope.cust_edit();
						});
					}
				} else {
					$scope.cust_edit();
				}
			}
		});
	}

	// cust修改
	$scope.cust_edit = function() {
		$scope.COM_exp = '';
		var store;
		for (var i = 0; i < $scope.com_expList.length; i++) {
			if ($scope.com_expList[i].SELECTED == true) {
				store = i+1;
				$scope.COM_exp = $scope.COM_exp + store;
			} else {
				$scope.COM_exp = $scope.COM_exp + ' ';
			}
		}
		$scope.COM_exp = $scope.COM_exp + ' ';

		$scope.sendRecv("SOT701", "updateFC81Data", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {
			'custID' : $scope.inputVO.cust_id,
			'comExp' : $scope.COM_exp
		}, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			} else {
				if (totas.length > 0) {

					$scope.showMsg('ehl_01_common_006');
					$scope.init_cust();
					$scope.cust_inquire();
				}
			}
		});

		$scope.inputVO.com_experience = '';
		$scope.COM_exp = '';
	}

});
