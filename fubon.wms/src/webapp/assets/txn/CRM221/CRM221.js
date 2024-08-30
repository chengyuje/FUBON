'use strict';
eSoafApp.controller('CRM221Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $filter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('CRM210Controller', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "CRM221Controller";
		
		$scope.priID = String(sysInfoService.getPriID());
		$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();

		$scope.empListShowEXT = function () {
			$scope.empListShowFlag = "AO";
			
			if ($scope.memLoginFlag.startsWith('UHRM') && $scope.memLoginFlag != 'UHRM') {
				$scope.empListShowFlag = "UHRM";
			} else if ($scope.memLoginFlag.startsWith('BS') && $scope.memLoginFlag != 'BS') {
				$scope.empListShowFlag = "BS";
			} else {
				$scope.empListShowFlag = "AO";
			}		
		};
		$scope.empListShowEXT();
		
		$scope.login = String(sysInfoService.getPriID());
		
		/*
		 * 取得UHRM人員清單(由員工檔+角色檔)
		 */
		$scope.getUHRMList = function() {
			$scope.inputVO.regionCenterID = $scope.inputVO.ao_03;
			$scope.inputVO.branchAreaID = $scope.inputVO.ao_04;
			
			$scope.sendRecv("ORG260", "getUHRMListByType", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO,  function(tota, isError) {
				if (isError) {
					return;
				}
				if (tota.length > 0) {
					$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
					if ($scope.mappingSet['UHRM_LIST'].length >= 1 && $scope.login == 'UHRM002') {
						$scope.inputVO.uEmpID = $scope.mappingSet['UHRM_LIST'][0].DATA;
					} else {
						$scope.inputVO.uEmpID = '';
					}
				}
			});
		};
		$scope.getUHRMList();
		
		//初始化
		$scope.init = function() {
			$scope.empListShowFlag = "AO";
			
			$scope.priID = String(sysInfoService.getPriID());;
			
			// mantis 0004529: 理專調分行,原分行客戶可被新分行主管移轉 sen 2018/04/10
			// ["塞空ao_code用Y/N", $scope.inputVO, "區域NAME", "區域LISTNAME", "營運區NAME", "營運區LISTNAME", "分行別NAME", "分行別LISTNAME", "ao_codeNAME", "ao_codeLISTNAME", "emp_idNAME", "emp_idLISTNAME"]
			if ($scope.memLoginFlag.startsWith('BS') && $scope.memLoginFlag != 'BS') {
				$scope.test = ['N', $scope.inputVO, "ao_03", "REGION_LIST", "ao_04", "AREA_LIST", "ao_05", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
			} else {
				$scope.test = ['Y', $scope.inputVO, "ao_03", "REGION_LIST", "ao_04", "AREA_LIST", "ao_05", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
			}
			
	        $scope.RegionController_setName($scope.test).then(function(data) {
	        	// 2018/7/3 for LA IA
	        	var temp = ['014', '015', '023', '024'];
	        	if(temp.indexOf($scope.login) > -1) {
	        		if($scope.AVAIL_REGION.length > 2) $scope.inputVO.ao_03 = "";
	        		if($scope.AREA_LIST.length > 2) $scope.inputVO.ao_04 = "";
	        		if($scope.BRANCH_LIST.length > 2) $scope.inputVO.ao_05 = "";
	        		if($scope.AO_LIST.length > 2) $scope.inputVO.ao_code = "";
	        	}
	        });
	        $scope.inputVO.cust_id = "";
	        $scope.inputVO.cust_name = "";
	        // 2018/4/3 4496 hide 客戶群組
	        $scope.inputVO.disableGroupCombobox = true;
	        
	        $scope.empListShowEXT();
		};
		$scope.init();
						
		$scope.inquire = function() {
			if($scope.inputVO.cust_id != undefined && $scope.inputVO.cust_id.trim() != ""){
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();				
			}
			//手收貢獻度檢查
			if($scope.inputVO.manage_05_Date != undefined){
				if($scope.inputVO.manage_06_fee_s == undefined || $scope.inputVO.manage_06_fee_e == undefined){
					$scope.showMsg("請輸入完整手續費收入");
					return;
				}
				if($scope.inputVO.manage_06_fee_s >= $scope.inputVO.manage_06_fee_e){
					$scope.showMsg("手續費收入由小到大");
					return;
				}
			}
			$scope.inputVO.aolist = $scope.AO_LIST;
			$scope.inputVO.branch_list = [];
			angular.forEach($scope.BRANCH_LIST, function(row, index, objs){
				if(row.DATA != "" && row.DATA != "0"){
					$scope.inputVO.branch_list.push({LABEL: row.LABEL, DATA: row.DATA});					
				}
			});
			$scope.inputVO.role = $scope.role;
			$scope.sendRecv("CRM221", "inquire", "com.systex.jbranch.app.server.fps.crm221.CRM221InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.obj.resultList = tota[0].body.resultList;
						$scope.obj.outputVO = tota[0].body;
					}
				}
			);
	    }   
		
		$scope.initQuery = function(){
			$scope.sendRecv("CRM211", "initQuery", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", $scope.inputVO,
				function(tota, isError) {
				if (!isError) {
					if(tota[0].body.showMsg){
						$scope.showErrorMsg(tota[0].body.errorMsg);
					}
					
				}					
			});
		};
		$scope.initQuery();
});
