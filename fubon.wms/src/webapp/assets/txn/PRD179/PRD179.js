/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD179Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PRD179Controller";

		// date picker
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};

		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.EAPPLY_DATE || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.SAPPLY_DATE || $scope.minDate;
		};

        // combobox
    	getParameter.XML(['PRD.INSURED_TYPE','PRD.PAYER_TYPE','PRD.OCCUPATION_CLASS','PRD.PAYMETHOD_RELATION'], function(totas) {
    		if (totas) {
//    			$scope.mappingSet['FUBONSYS.FC_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.FC_ROLE')];
    			$scope.mappingSet['CASETYPE'] = [];
    			$scope.mappingSet['CASETYPE'].push({LABEL: '傳統型契約', DATA: 'F'},{LABEL: '投資型契約', DATA: 'J'});
    			$scope.mappingSet['PRD.INSURED_TYPE'] = totas.data[totas.key.indexOf('PRD.INSURED_TYPE')];
    			$scope.mappingSet['PRD.PAYER_TYPE'] = totas.data[totas.key.indexOf('PRD.PAYER_TYPE')];
    			$scope.mappingSet['PRD.PAYMETHOD_RELATION'] = totas.data[totas.key.indexOf('PRD.PAYMETHOD_RELATION')];
    			$scope.mappingSet['PRD.OCCUPATION_CLASS'] = totas.data[totas.key.indexOf('PRD.OCCUPATION_CLASS')];
    		}
    	});


		$scope.init = function(){
			let defaultDate = new Date();
			$scope.inputVO = {
					region_center_id	: undefined,
	                branch_area_id		: undefined,
	                branch_nbr			: undefined,
	                ao_code             : undefined,
	                CASETYPE : undefined,
	                ACCEPTID : undefined,
	                POLICY_TYPE_2 : undefined,
	                POLICY_NO : undefined,
	                POLICYSTATUS : undefined,
	                CNAME : undefined,
	                SALES_ID : undefined,
	                PAYER_APPL_ID : undefined,
	                PRODUCTID : undefined,
	                PROJECTID : undefined,
	                INSURED_ID : undefined,
	                SAPPLY_DATE : undefined,
	                EAPPLY_DATE : undefined,
	                reportDate			: $filter('date')(defaultDate, 'yyyyMM')
			};

			$scope.paramList = [];
			$scope.outputVO = [];

			$scope.RegionController_getORG($scope.inputVO);
			$scope.inputVO.loginRole = sysInfoService.getRoleID();


			$scope.sendRecv("PRD179", "init", "com.systex.jbranch.app.server.fps.prd179.PRD179InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.mappingSet['POLICY_TYPE_2'] = tota[0].body.policeTypeList
							$scope.mappingSet['POLICYSTATUS'] = tota[0].body.policeStatusList
							$scope.mappingSet['CNAME'] = tota[0].body.companyNameList
							return;
						}
			});

		};

		$scope.init();

		$scope.inquireInit = function(){
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();

		$scope.inquire = function(){
			const otherCondition = $scope.inputVO.PAYER_APPL_ID || $scope.inputVO.POLICY_NO;
			if($scope.parameterTypeEditForm.$invalid && !otherCondition) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位。或至少輸入「要保人ID」、「保單號碼」兩者其一。');
        		return;
        	}

			$scope.inputVO.regionList = $scope.REGION_LIST;
			$scope.inputVO.areaList = $scope.AREA_LIST;
            $scope.inputVO.branchList = $scope.BRANCH_LIST;
            $scope.inputVO.aoCodeList = $scope.AO_LIST;

//			alert(JSON.stringify($scope.inputVO));
			$scope.sendRecv("PRD179", "inquire", "com.systex.jbranch.app.server.fps.prd179.PRD179InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {

							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								$scope.paramList=[];
								return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};

		//匯出
		$scope.downloadCSV = function(){
			$scope.sendRecv("PRD179","downloadCSV","com.systex.jbranch.app.server.fps.prd179.PRD179InputVO",
					{'exportList': $scope.paramList}, function(tota, isError) {
						if (isError) {
							return;
		            	}
					});
		};

});
