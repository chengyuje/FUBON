/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('REF111Controller',function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter , $http , sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = 'REF111Controller';
	
	console.log(sysInfoService.getLoginSourceToken());
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(['CAM.REF_SALES_ROLE', 'CAM.REF_PROD'], function(totas) {
		if (totas) {
			$scope.mappingSet['CAM.REF_SALES_ROLE'] = totas.data[totas.key.indexOf('CAM.REF_SALES_ROLE')];
			$scope.mappingSet['CAM.REF_PROD'] = totas.data[totas.key.indexOf('CAM.REF_PROD')];
		}
	});
	
	var leTenFullZero = function(num){
		return num < 10 ? ('0' + num) : num;
	}
	
	//組織連動
    $scope.region = ['N', $scope.inputVO, 'newRow.regionID', 'REGION_LIST', 'newRow.branchAreaID', 'AREA_LIST', 'newRow.BRANCH_NBR', 'BRANCH_LIST', 'newRow.ao_code', 'AO_LIST', 'newRow.userID', 'EMP_LIST'];
    $scope.RegionController_setName($scope.region);
    
    //登入者Id
	$scope.userId = projInfoService.getUserID();
	
	$scope.nowTimeStr = function(){
		var day = new Date();
		return	day.getFullYear() + '-' + leTenFullZero(day.getMonth() + 1) + '-' + day.getDate() + ' ' + 
			leTenFullZero(day.getHours()) + ':' + leTenFullZero(day.getMinutes()) + ':' + leTenFullZero(day.getSeconds());
	};
	
	$scope.initNewRow = function(){
		$scope.newRow = {};//new line
		var nowTime = $scope.nowTimeStr();
		$scope.newRow.CREATETIME = nowTime;
		$scope.newRow.LASTUPDATE = nowTime; 
		$scope.newRow.dateYearMonth = angular.copy($scope.inputVO.dateYearMonth);
	}; 
	
	$scope.init = function(queryType){
		$scope.model = {};
		$scope.inputVO = {};
		$scope.outputVO = {};
		$scope.outputRoleVO = {};
		$scope.outputBrnVO = {};
		$scope.paramListRole = [];//result list
		$scope.paramListBrn = [];
		$scope.initNewRow();
		$scope.inputVO.queryType = queryType;
	};
	
	$scope.initOutputVO = function(){
		if($scope.inputVO.queryType == 0){
			$scope.outputRoleVO = [];
		}
		else if($scope.inputVO.queryType == 1){
			$scope.outputBrnVO = [];
		}
	};
	
    $scope.init(0);
    
	//開啟月曆
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	//查詢
    $scope.query = function(){ 
    	$scope.paramListRole = [];
    	$scope.paramListBrn = [];
    	$scope.outputRoleVO = {};
    	$scope.outputBrnVO = {};
    	$scope.initNewRow();
    	
		$scope.sendRecv('REF111', 'query', 'com.systex.jbranch.app.server.fps.ref111.REF111InputVO',  $scope.inputVO , function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg('ehl_01_common_009');
        			return;
        		}
				if($scope.inputVO.queryType == 0){
					$scope.outputRoleVO = tota[0].body;
					$scope.paramListRole = tota[0].body.resultList;
				}
				else if($scope.inputVO.queryType == 1){
					$scope.outputBrnVO = tota[0].body;
					$scope.paramListBrn = tota[0].body.resultList;
				}
			}
		});
	};
	
	$scope.save = function(){
		var objByQt = $scope.getByQTypeVal('DCTPL');
		
		for(var i = 0 ; i < objByQt.paramList.length - 1 ; i++){
			for(var j = i + 1 ; j < objByQt.paramList.length ; j++){
				if($scope.isTheSameForNewRow(objByQt.paramList[i] , objByQt.paramList[j] , objByQt)){
					$scope.showMsg('序號' + objByQt.paramList[i].ROWNUM + '與序號' +　objByQt.paramList[j].ROWNUM + '重複');
					return;
				}
			}
		}
		$scope.inputVO.list = objByQt.paramList;
		
		$scope.sendRecv('REF111', 'save', 'com.systex.jbranch.app.server.fps.ref111.REF111InputVO',  $scope.inputVO , function(tota, isError) {
			if (isError || tota[0].body.deleteSize == 0 && tota[0].body.insertSize == 0) {
				$scope.showMsg('ehl_01_common_007');
    			return;
			}
			else if (tota.length > 0) {
        		$scope.showMsg('ehl_01_common_006');
        		$scope.init($scope.inputVO.queryType);
        	};
		});
	}
	
	$scope.delNewRow = function(rowNum){
		var objByQt = $scope.getByQTypeVal('DCTPL');
		
		for(var i = 0 ; i < objByQt.paramList.length ; i++){
			if(objByQt.paramList[i].ROWNUM == rowNum){
				objByQt.paramList.splice(i , 1);
				$scope.initOutputVO();
				break;
			}
		}
	}
	
	//新增
	$scope.addRow = function(){
		var objByQt = $scope.getByQTypeVal('DCTPL');
		var newObj = {};
		
		for(var j = 0 ; j < objByQt.paramList.length ; j++){
			if($scope.isTheSameForNewRow(objByQt.paramList[j] , $scope.newRow , objByQt)){
				$scope.showMsg('資料重複：序號' + objByQt.paramList[j].ROWNUM);
				return;
			}
		}
		
		for(var key in $scope.newRow){
			newObj[key] = $scope.newRow[key];
		}
		
		newObj.ROWNUM = objByQt.paramList[objByQt.paramList.length - 1].ROWNUM + 1;
		objByQt.paramList.push(newObj);
		delete $scope.newRow.dateYearMonth;
		$scope.initOutputVO();
		$scope.initNewRow();
	}
	
	$scope.isTheSameForNewRow = function(column , column2 , objByQt){		
		return column.YYYYMM == column2.YYYYMM && column[objByQt.differedColumn] == column2[objByQt.differedColumn] && column.REF_PROD == column2.REF_PROD;
	}
	
	//依照tab來取得對應的欄位名稱、資料集合
	$scope.getByQTypeVal = function(needValType){
		var isRole = $scope.inputVO.queryType == 0;
		var isBrn = $scope.inputVO.queryType == 1;
		
		if(needValType == 'DC'){
			return isRole ? 'SALES_ROLE' : isBrn ? 'BRANCH_NBR' : null;
		}
		else if(needValType == 'TPL'){
			return isRole ? $scope.paramListRole : isBrn ? $scope.paramListBrn : null;
		}
		else if(needValType == 'DCTPL'){
			if(isRole){
				return {differedColumn : 'SALES_ROLE', paramList: $scope.paramListRole}; 
			}
			else if(isBrn){
				return {differedColumn : 'BRANCH_NBR', paramList: $scope.paramListBrn};
			}
			return null;
		}
	}
});