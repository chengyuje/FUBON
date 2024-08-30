'use strict';
eSoafApp.controller('PMS504Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $compile, sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS504Controller";
	
	// 繼承共用的組織連動選單
	$controller('RegionController', {$scope: $scope});
	
	var NowDate = new Date();
	var yr = NowDate.getFullYear();
    var mm = NowDate.getMonth() + 1;
    var strmm = '';
    $scope.mappingSet['timeE'] = [];
    for(var i = 0; i < 13; i++){
    	if(mm == 0){
    		mm = 12;
    		yr = yr - 1;
    	}
    	
    	if(mm < 10)
    		strmm = '0' + mm;
    	else
    		strmm = mm;  
    	
    	$scope.mappingSet['timeE'].push({
    		LABEL: yr + '/' + strmm,
    		DATA: yr + '' + strmm
    	});       
    	
    	mm = mm -1;
    }

    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function() {
	   if($scope.inputVO.sCreDate != ''){
	   		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	   } else {   
		   $scope.inputVO.sCreDate = '201701';
		   $scope.inputVO.reportDate = $scope.inputVO.sCreDate;    
		   $scope.inputVO.sCreDate = '';
	   } 
    	$scope.inputVO.dataMon = $scope.inputVO.sCreDate; 
    }; 
    
	$scope.init = function(){
		$scope.priID = String(sysInfoService.getPriID());

		$scope.inputVO = {
			FILE_NAME          	: '',
			ACTUAL_FILE_NAME   	: '',
			exportList         	: [],
			sCreDate		   	: '',
			region_center_id   	: '',   // 區域中心
			branch_area_id		: '', 	// 營運區
			branch_nbr			: '',	// 分行	
			memLoginFlag        : String(sysInfoService.getMemLoginFlag())
		};
	
		$scope.paramList = [];
		
		$scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
		$scope.RegionController_setName($scope.test);
	};
	
	$scope.init();
	
	// 查詢
	$scope.query = function(){
		if ($scope.inputVO.sCreDate == null || $scope.inputVO.sCreDate == '' || $scope.inputVO.sCreDate == undefined) {
			$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料月份)');
    		return;
		}
		
		$scope.totals = {};
		
		$scope.paramList = [];
		$scope.outputVO = [];
		
		$scope.sendRecv("PMS504", "query", "com.systex.jbranch.app.server.fps.pms504.PMS504InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.paramList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;

				return;
			}
		});
	};
	
	// 取得範例
	$scope.getExample = function() {
		$scope.sendRecv("PMS504", "getExample", "com.systex.jbranch.app.server.fps.pms504.PMS504InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
			}
		});
	};
	
	// 上傳
	$scope.updFile = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PMS504", "updFile", "com.systex.jbranch.app.server.fps.pms504.PMS504InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				} else {
					$scope.showMsg("ehl_01_common_010");
					$scope.query();
				}
			});
		});
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='updFile(name, rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	// 匯出
	$scope.export = function() {
		angular.copy($scope.paramList, $scope.inputVO.exportList);
		$scope.inputVO.exportList.push($scope.totals);
		
		$scope.sendRecv("PMS504", "export", "com.systex.jbranch.app.server.fps.pms504.PMS504InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
                		if(totas[0].body.paramList && totas[0].body.paramList.length == 0) {
                			$scope.showMsg("ehl_01_common_009");
                			return;
                		}
                	};
				}
		);
	};
	
});
