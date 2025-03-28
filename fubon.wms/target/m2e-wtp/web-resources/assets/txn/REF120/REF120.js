'use strict';
eSoafApp.controller('REF120Controller', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "REF120Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["CAM.REF_PROD", "CAM.REF_SALES_ROLE", "CAM.REF_USER_ROLE", "CAM.REF_INS_CONT_RSLT", "CAM.REF_LOAN_CONT_RSLT", "CAM.REF_NON_GRANT_REASON"], function(totas) {
		if (totas) {
			$scope.mappingSet['CAM.REF_PROD'] = totas.data[totas.key.indexOf('CAM.REF_PROD')];
			$scope.mappingSet['CAM.REF_SALES_ROLE'] = totas.data[totas.key.indexOf('CAM.REF_SALES_ROLE')];
			$scope.mappingSet['CAM.REF_USER_ROLE'] = totas.data[totas.key.indexOf('CAM.REF_USER_ROLE')];
			$scope.mappingSet['CAM.REF_INS_CONT_RSLT'] = totas.data[totas.key.indexOf('CAM.REF_INS_CONT_RSLT')];
			$scope.mappingSet['CAM.REF_LOAN_CONT_RSLT'] = totas.data[totas.key.indexOf('CAM.REF_LOAN_CONT_RSLT')];
			$scope.mappingSet['CAM.REF_NON_GRANT_REASON'] = totas.data[totas.key.indexOf('CAM.REF_NON_GRANT_REASON')];
		}
	});
    //
	
	
	$scope.altInputFormats = ['M!/d!/yyyy'];
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.startDateOptions = {};
	$scope.endDateOptions = {};
	
	$scope.limitDate = function() {
		$scope.startDateOptions.maxDate = $scope.inputVO.eDate;
		$scope.startDateOptions.minDate = $scope.inputVO.sDate;
		
		if ($scope.inputVO.sDate) {
			var compare = new Date(($scope.inputVO.sDate.getFullYear() + 1), ($scope.inputVO.sDate.getMonth()), $scope.inputVO.sDate.getDate() - 1);
			$scope.endDateOptions.maxDate = compare;
		}
		
		if ($scope.inputVO.eDate) {
			var compare = new Date(($scope.inputVO.eDate.getFullYear() - 1), ($scope.inputVO.eDate.getMonth()), $scope.inputVO.eDate.getDate() + 1);
			$scope.startDateOptions.minDate = compare;
		}
	};
	
    $scope.init = function(){
    	var startDate = new Date();
		var month = startDate.getMonth();
		startDate.setMonth(month - 2);
		
		$scope.inputVO = {
				regionID: '',
				branchAreaID:  '',
				branchID: '', 
				seq: '',
				salesPerson: '', 
				salesName: '', 
				salesRole: '', 
				userID: '', 
				userName: '', 
				userRole: '', 
				custID: '', 
				custName: '', 
				sDate: startDate,
				eDate: new Date(),
				refProd: '', 
				refInsContRslt: '', 
				refLoanContRslt: ''
    	};
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "regionID", "REGION_LIST", "branchAreaID", "AREA_LIST", "branchID", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
        
		$scope.inputVO.regionID = ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
		$scope.inputVO.branchAreaID = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
		$scope.inputVO.branchID = ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
        
        $scope.limitDate();
	};
	$scope.init();
    
	// 初始分頁資訊
    $scope.inquireInit = function(){
    	$scope.initLimit();
    	$scope.salerecList = [];
    }
    $scope.inquireInit();
    
    $scope.inquire = function(){
		$scope.sendRecv("REF120", "query", "com.systex.jbranch.app.server.fps.ref120.REF120InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.salerecList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						
						$scope.loginSysRole = tota[0].body.loginSysRole;
						
						angular.forEach($scope.salerecList, function(row, index, objs){
							row.set = [];
							if (($scope.loginSysRole == "BMMGR" && row.BRANCH_NBR == sysInfoService.getBranchID()) || 
								(row.SALES_PERSON == sysInfoService.getUserID())) {
								//修改功能：主管可修改轄下轉介件，而轉介人可修改自己之轉介件。
								row.set.push({LABEL: "修改", DATA: "U"});
							} 
							
							if ((row.USERID == sysInfoService.getUserID())) {
								row.set.push({LABEL: "回報進度", DATA: "U"});
							}
							
							// 2017/11/7 sa:登入者045可以刪
							if (projInfoService.getPriID()[0] == '045') {
								row.set.push({LABEL: "刪除", DATA: "D"});
							}
						});
						return;
					}
		});
	};
	
	$scope.action = function(row) {
		if(row.cmbAction) {
			if(row.cmbAction == "D") {
				$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
					$scope.sendRecv("REF120", "del", "com.systex.jbranch.app.server.fps.ref120.REF120InputVO", {seq: row.SEQ},
            				function(totas, isError) {
                            	if (isError) {
                            		$scope.showErrorMsg(totas[0].body.msgData);
                            	}
                            	if (totas.length > 0) {
                            		$scope.showSuccessMsg("ehl_01_common_003");
                            		$scope.inquireInit();
                            		$scope.inquire();
                            	};
            				}
            		);
				});
			} else
				$scope.edit(row);
			
			row.cmbAction = "";
		}
	};
    
	$scope.edit = function (row) {
    	$scope.connector('set','REF110PAGE', "REF120");
    	if(row){
    		$scope.connector('set','ACTION_TYPE', "update");
    		$scope.connector('set', 'SEQ', row.SEQ);
    		$rootScope.menuItemInfo.url = "assets/txn/REF110/REF110.html";
    	}
	};
	
	$scope.checkProd = function (type) {
		if (type == "loan" && $scope.inputVO.refLoanContRslt != "") {
			$scope.inputVO.refProd = "";
		} else if (type == "ins" && $scope.inputVO.refInsContRslt != "") {
			$scope.inputVO.refProd = "5";
		}
	}
	
	$scope.checkContRslt = function () {
		if ($scope.inputVO.refProd != "5") {
			$scope.inputVO.refInsContRslt = "";
		} else if ($scope.inputVO.refProd == "5") {
			$scope.inputVO.refLoanContRslt = "";
		}
	}
	
	$scope.export = function() {
		$scope.inputVO.EXPORT_LST = $scope.salerecList;
		
		$scope.sendRecv("REF120", "export", "com.systex.jbranch.app.server.fps.ref120.REF120InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {}
				}
		);
	};
});
