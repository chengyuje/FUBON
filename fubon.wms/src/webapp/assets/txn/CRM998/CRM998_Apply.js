/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM998_ApplyController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter, validateService, $filter, $timeout) {
	$controller('BaseController', {
		$scope : $scope
	});
	$controller('RegionController', {
		$scope : $scope
	});
	$scope.controllerName = 'CRM998_ApplyController';

	// init
	$scope.init = function() {
		$scope.inputVO.custId = '';
		$scope.inputVO.new_degree = '';
		$scope.inputVO.appl_emp_id = '';
		$scope.inputVO.appl_emp_name = '';
		$scope.datashow = false;
		$scope.applydata = [];
	}
	$scope.init();

	//切換頁面的時候進行初始
	$scope.$watch('doInit', function(newValue, oldValue) {
		if ((newValue != oldValue) ||
			(newValue && oldValue)	
		) {
			$scope.init();
		}  
    });
	
	//新增到覆核清單後，清空相關欄位
	$scope.addInit = function() {
		$scope.inputVO = null;
		$scope.custData = null;
	}
	
	//查詢時，清除申請資訊
	$scope.inquireInit = function() {
		$scope.inputVO.new_degree = '';
		$scope.inputVO.appl_emp_id = '';
		$scope.inputVO.appl_emp_name = '';
	}
	
	// 查詢客戶資料
	$scope.inquiredata = function() {
		//清除客戶資料 與申請資訊
		$scope.custData = null;
		$scope.inquireInit();
		
		if (validateService.checkCustID($scope.inputVO.custId)) {
			$scope.sendRecv('CRM998', 'inquireCustData', 'com.systex.jbranch.app.server.fps.crm998.CRM998InputVO', $scope.inputVO, function(tota, isError) {
				if (!isError) {
					if (tota[0].body.resultList.length > 0) {
						$scope.custData = tota[0].body.resultList[0];
						
						if ($scope.custData.WAITING) {
							$scope.showMsg('客戶已申請例外，但尚未覆核，不可以再申請');
							return;
						} else {
							$scope.datashow = true;
							
							//客戶資產預設為0
							$scope.custData.AUM_AMT = $scope.custData.AUM_AMT || 0;
							
							if ($scope.custData.EMP_ID) {
								//申請人員編欄位預設帶入客戶理專員編
								$scope.inputVO.appl_emp_id = $scope.custData.EMP_ID;
								$scope.getEMPName();
							}
						}
					} else {
						$scope.showMsg('ehl_01_common_009');
					}
				} else {
					$scope.showMsg('ehl_01_common_024');
				}
			});
		}
	}
	
	// 檢核等級
	$scope.isDifferentDegree = function() {
		if ($scope.custData.ORG_DEGREE == $scope.inputVO.new_degree) {
			$scope.showErrorMsg('異動後等級與原等級不可相同!');
			return false;
		}
		return true;
	}

	//檢核清單中是否有加入重複客戶
	$scope.isNewCust = function() {
		if ($scope.applydata.length) {
			if ($scope.applydata.filter((e) => e.CUST_ID == $scope.inputVO.custId).length) {
				$scope.showMsg('已加入該客戶ID : ' + $scope.inputVO.custId);
				return false;
			}
		}
		return true;
	}
	
	// 儲存申請的data
	$scope.addVIPDegreeImprove = function() {
		if ($scope.isDifferentDegree() && $scope.isNewCust()) {
			var degreeArray = $scope.VIP_DEGREE.map((e)=>e.DATA);
			
			if ($scope.custData.ORG_DEGREE && $scope.custData.ORG_DEGREE.trim()  //無客戶等級者，不需要檢核
					&& degreeArray.indexOf($scope.inputVO.new_degree) > degreeArray.indexOf($scope.custData.ORG_DEGREE)) {
				$confirm({
					text : '異動後等級比原等級低，請確認是否新增?'
				}, {
					size : 'sm'
				}).then(function() {
					$scope.addApprList();
				});
			} else {
				$scope.addApprList();
			}
		}
	};
	
	//加入將申請資料加入清單中
	$scope.addApprList = function() {
		var custVO = {
			CUST_ID : $scope.inputVO.custId,
			CUST_NAME : $scope.custData.CUST_NAME,
			BRA_NBR : $scope.custData.BRA_NBR,
			ORG_DEGREE : $scope.custData.ORG_DEGREE,
			AUM_AMT : $scope.custData.AUM_AMT,
			NEW_DEGREE : $scope.inputVO.new_degree,
			APPL_EMP_ID : $scope.inputVO.appl_emp_id,
			APPL_EMP_NAME : $scope.inputVO.appl_emp_name,
			APPR_STATUS : 'W'
		}

		$scope.applydata.push(custVO);
		//清除舊的客戶資料
		$scope.addInit();
	}
	
	// 取得申請人(理專)的姓名
	$scope.getEMPName = function() {
		if (!$scope.inputVO.appl_emp_id || $scope.inputVO.appl_emp_id.length < 6) {
			$scope.inputVO.appl_emp_name = '';
			return;
		}
		
		$scope.sendRecv('SOT712', 'getTellerName', 'com.systex.jbranch.app.server.fps.sot712.SOT712InputVO', {
			'tellerID' : $scope.inputVO.appl_emp_id
		}, function(tota, isError) {
			if (!isError) {
				if (!tota[0].body.EMP_NAME) {
					$scope.showMsg('ehl_01_common_009');
					$scope.inputVO.appl_emp_name = '';
					return;
				}

				$scope.inputVO.appl_emp_name = tota[0].body.EMP_NAME;
			}
		});
	};

	// 把例外升等申請資料傳給後端
	$scope.addApplydata = function() {

		$confirm({
			text : '確定要送出?'
		}, {
			size : 'sm'
		}).then(function() {
			$scope.sendRecv('CRM998', 'add', 'com.systex.jbranch.app.server.fps.crm998.CRM998InputVO', $scope.applydata, function(tota, isError) {
			});
			$scope.init();
			$scope.showMsg('送出成功');

		});

	};

	// e-combobox
	getParameter.XML([ 'CRM.VIP_DEGREE' ], function(totas) {
		if (totas) {
			$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
		}
	});
	
	//控管新增鍵
	$scope.addFlag = function() {
		return !$scope.inputVO.new_degree ||  !$scope.inputVO.appl_emp_id || !$scope.inputVO.appl_emp_name || !$scope.custData;
	}
	
	//控管顯示訊息
	$scope.getHit = function() {
		if ($scope.addFlag()) 
			return `缺少下列資訊 : ${$scope.custData?'':'欲申請客戶'} 
								${$scope.inputVO.appl_emp_name ?'':'申請人員編'} 
								${$scope.inputVO.new_degree?'':'新等級'} `;   
								
	}

});
