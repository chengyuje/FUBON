/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD178_EDITController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PRD178_EDITController";

	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened' + index] = true;
	};

	// date picker
	$scope.bgn_sDateOptions = {
		maxDate : $scope.maxDate,
		minDate : $scope.minDate
	};
	$scope.bgn_eDateOptions = {
		maxDate : $scope.maxDate,
		minDate : $scope.minDate
	};
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.VALIDTHRU || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.VALIDFROM || $scope.minDate;
	};
	// date picker end

	$scope.init = function() {
		$scope.row = $scope.row || {};
		$scope.inputVO = {
			CNAME : $scope.row.CNAME,
			PROJECTID : $scope.row.PROJECTID,
			PROJECTNAME : $scope.row.PROJECTNAME,
			DEPARTMENTTYPE : $scope.row.DEPARTMENTTYPE,
			PROJECTTYPE2 : $scope.row.PROJECTTYPE2,
			PROJECTTYPE : $scope.row.PROJECTTYPE,
			PROJECTTYPE1 : $scope.row.PROJECTTYPE1,
			PERMER : $scope.row.PERMER,
			VALIDFROM : $scope.row.VALIDFROM ? new Date($scope.row.VALIDFROM) : null,
			VALIDTHRU : $scope.row.VALIDTHRU ? new Date($scope.row.VALIDTHRU) : null,
			INSCOPRODUCTNAME : $scope.row.INSCOPRODUCTNAME,
			INSCOPRODUCTID : $scope.row.INSCOPRODUCTID,
			MEMO : $scope.row.MEMO,
			PAYANDPROTECT : $scope.row.PRODUCTPUD + $scope.row.PRODUCTPUDTYPE + '/' + $scope.row.PRODUCTED + $scope.row.PRODUCTEDTYPE,
			MPRODUCTID : $scope.row.MPRODUCTID,
			MPRODUCTNAME : $scope.row.MPRODUCTNAME,
			COVERAGEDUEPREMIUM : $scope.row.COVERAGEDUEPREMIUM,
			CONTCOVERAGEDUEPREMIUM : $scope.row.CONTCOVERAGEDUEPREMIUM,
			updateTBJSB_INS_PROD_PROJECT : false,
			updateTBJSB_INS_PROD_PROJECT_CONT : false
		};
		$scope.sendRecv("PRD178", "init", "com.systex.jbranch.app.server.fps.prd178.PRD178InputVO", {}, function(tota, isError) {
			if (!isError) {
				$scope.mappingSet['PROJECT_TYPE2'] = tota[0].body.projectBigClassList;
				$scope.mappingSet['PROJECT_TYPE'] = tota[0].body.projectMidClassList;
				$scope.mappingSet['PROJECT_TYPE1'] = tota[0].body.projectSmallClassList;
			}
		});
		$scope.mappingSet['YESNO'] = [];
		$scope.mappingSet['YESNO'].push({
			LABEL : '是',
			DATA : '是'
		}, {
			LABEL : '否',
			DATA : '否'
		});
	};
	$scope.init();

	$scope.save = function() {
		$scope.sendRecv("PRD178", "update", "com.systex.jbranch.app.server.fps.prd178.PRD178InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}
			if (tota.length > 0) {
				$scope.showSuccessMsg('ehl_01_common_006');
				$scope.closeThisDialog('successful');
			}
			;
		});
	};

	$scope.changeUpdateFlag = function(number) {
		switch (number) {
		case 1:
			$scope.inputVO.updateTBJSB_INS_PROD_PROJECT = true;
			break;
		case 2:
			$scope.inputVO.updateTBJSB_INS_PROD_PROJECT_CONT = true;
			break;
		}
	};

});
