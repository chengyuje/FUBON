/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG150Controller', function(sysInfoService, $scope, $controller, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG150Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["ORG.RESIGN_DESTINATION", "ORG.RESIGN_REASON", "ORG.DESTINATION_BANK_ID"], function(totas) {
		if (totas) {
			$scope.mappingSet['ORG.RESIGN_DESTINATION'] = totas.data[totas.key.indexOf('ORG.RESIGN_DESTINATION')];
			$scope.mappingSet['ORG.RESIGN_REASON'] = totas.data[totas.key.indexOf('ORG.RESIGN_REASON')];
			$scope.mappingSet['ORG.DESTINATION_BANK_ID'] = totas.data[totas.key.indexOf('ORG.DESTINATION_BANK_ID')];
		}
	});
    //
	
	$scope.init = function() {
		$scope.mappingSet['jtLst'] = [];
		$scope.resignMemberLst = [];
		$scope.memberPhotoLst = [];
		$scope.selectedRow = null;
		
		$scope.inputVO = {
			region_center_id   : '',
			branch_area_id     : '',
			branch_nbr         : '',
			EMP_ID             : '',
			EMP_NAME           : '',
			RESIGN_REASON      : '',
			RESIGN_DESTINATION : '',
            DESTINATION_BANK_ID: '',
            dateS : undefined,
            dateE : undefined,            
		};
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
		
		$scope.photoInVO = {
			EMP_ID : ''
		};
    };

/**========================================================日期區間======================================================**/
    $scope.dateChange = function(){
        if($scope.inputVO.dateS == undefined && $scope.inputVO.dateE != undefined){
            $scope.inputVO.reportDate = $filter('date')($scope.inputVO.dateE,'yyyyMMdd');
            $scope.RegionController_getORG($scope.inputVO);
        }else{
            $scope.inputVO.reportDate = $filter('date')($scope.inputVO.dateS,'yyyyMMdd');
            $scope.RegionController_getORG($scope.inputVO);
        }
    };


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
        $scope.bgn_sDateOptions.maxDate = $scope.inputVO.dateE || $scope.maxDate;
        $scope.bgn_eDateOptions.minDate = $scope.inputVO.dateS || $scope.minDate;
    };
    // date picker end

    // 日期相差的天數
    function getDiffDays(dateS, dateE){
        var diffSeconds = dateE.getTime() - dateS.getTime(); // 時間差的毫秒数
        var diffDays = Math.floor(diffSeconds/(24*3600*1000));// 轉成相差天數
        return diffDays;
    };
            

/**========================================================日期區間======================================================**/

   
	$scope.getResignMemberLst = function() {  
		if($scope.inputVO.dateS != undefined && $scope.inputVO.dateE != undefined) {
	        if (getDiffDays($scope.inputVO.dateS, $scope.inputVO.dateE) > 365){
	            $scope.inputVO.dateE = undefined;
	            $scope.showErrorMsg('日期起迄區間不得超過一年');
	            return;
	        }
	        
	        if($scope.inputVO.dateE < $scope.inputVO.dateS){
	            //$scope.inputVO.dateS = $scope.inputVO.dateE;				
	            //$scope.dateChange();
	            $scope.showErrorMsg('迄日不可小於起日');
	            return;				
	        }
    	}
        
		$scope.sendRecv("ORG150", "getResignMemberLst", "com.systex.jbranch.app.server.fps.org150.ORG150InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.resignMemberLst = tota[0].body.resignMemberLst;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.showORG150Modify = function(row) {
		var dialog = ngDialog.open({
			template: 'assets/txn/ORG150/ORG150MOD.html',
			className : 'ORG150',
			controller:['$scope',function($scope){
				$scope.selectedRow = row;
			}]
		});
		dialog.closePromise.then(function (data) {
			$scope.getResignMemberLst();		
		});
	};
	
	$scope.init();
	
	$scope.export = function() {
		$scope.inputVO.EXPORT_LST = $scope.resignMemberLst;
		
		$scope.sendRecv("ORG150", "export", "com.systex.jbranch.app.server.fps.org150.ORG150InputVO", $scope.inputVO, 
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
