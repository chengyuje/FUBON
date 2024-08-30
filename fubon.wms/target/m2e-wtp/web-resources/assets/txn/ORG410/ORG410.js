/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG410Controller', function(sysInfoService, $scope, $controller, $filter, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG410Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["ORG.CHANGE_TYPE", "ORG.JOB_RANK"], function(totas) {
		if (totas) {
			$scope.mappingSet['ORG.CHANGE_TYPE'] = totas.data[totas.key.indexOf('ORG.CHANGE_TYPE')];
			$scope.mappingSet['ORG.JOB_RANK'] = totas.data[totas.key.indexOf('ORG.JOB_RANK')];
		}
	});
    //
	$scope.getAoJobRankList = function() {
		$scope.sendRecv("ORG410", "getAoJobRankList", "com.systex.jbranch.app.server.fps.org410.ORG410InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['AO_JOB_RANK'] = [];
						$scope.aoJobRankList = tota[0].body.aoJobRankList;
						angular.forEach($scope.aoJobRankList, function(row, index, objs){
				   			$scope.mappingSet['AO_JOB_RANK'].push({LABEL: row.JOB_TITLE_NAME, DATA: row.ROLE_ID});
				   		});
						
						return;
					}
		});
	};
    
	$scope.init = function() {
		$scope.inputVO = {
				CHANGE_TYPE: '',
				EMP_ID: '',
				EMP_NAME: '',
				HIS_BRANCH_AREA_ID: '',
				BRANCH_AREA_ID: '',
				HIS_BRANCH_ID: '',
				BRANCH_ID: '',
				HIS_JOB_TITLE_NAME: '',
				JOB_TITLE_NAME: '',
				HIS_JOB_RANK: '',
				JOB_RANK: '',
				START_TIME: '',
				END_TIME: '',
				EXPORT_LST: []
		};
		
		$scope.empChangeLst = [];
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "REGION_CENTER_ID", "REGION_LIST", "BRANCH_AREA_ID", "AREA_LIST", "BRANCH_ID", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
        
        $scope.region_his = ['N', $scope.inputVO, "HIS_REGION_CENTER_ID", "HIS_REGION_LIST", "HIS_BRANCH_AREA_ID", "HIS_AREA_LIST", "HIS_BRANCH_ID", "HIS_BRANCH_LIST", "ao_code2", "AO_LIST2", "emp_id2", "EMP_LIST2"];
        $scope.RegionController_setName($scope.region_his);
        
		$scope.getAoJobRankList();
	};
	
	//啟始日設定
	$scope.startDateOptions = {
			maxDate: $scope.inputVO.END_TIME || $scope.maxDate,
			minDate: $scope.minDate
	};
	
	//終止日設定
	$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.START_TIME || $scope.minDate
	};
	
	$scope.altInputFormats = ['M!/d!/yyyy'];
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();   //防止父事件被觸發
			$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	//ng-change時會觸發的功能
	$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.END_TIME || $scope.maxDate;
			$scope.endDateOptions.minDate = $scope.inputVO.START_TIME || $scope.minDate;
	};
	
	$scope.query = function() {
		$scope.sendRecv("ORG410", "query", "com.systex.jbranch.app.server.fps.org410.ORG410InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.empChangeLst = tota[0].body.empChangeLst;
				$scope.inputVO.EXPORT_LST = tota[0].body.EXPORT_LST;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.export = function() {
		$scope.sendRecv("ORG410", "export", "com.systex.jbranch.app.server.fps.org410.ORG410InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	};
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.init();
	
});
