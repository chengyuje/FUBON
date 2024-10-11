'use strict';
eSoafApp.controller('MAO231Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "MAO231Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["MAO.DEV_STATUS_AO", "MAO.USE_PERIOD"], function(totas) {
		if (totas) {
			$scope.mappingSet['MAO.DEV_STATUS_AO'] = totas.data[totas.key.indexOf('MAO.DEV_STATUS_AO')];
			$scope.mappingSet['MAO.USE_PERIOD'] = totas.data[totas.key.indexOf('MAO.USE_PERIOD')];
		}
	});
    //
	
	//輸出欄初始化
	$scope.inquireInit = function(){
		$scope.resultList = [];
	}
	$scope.inquireInit();
	
	// date picker
	$scope.use_date_bgnOptions = {};
	$scope.use_date_endOptions = {};
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.limitDate = function() {
		$scope.use_date_bgnOptions.maxDate = $scope.inputVO.use_date_end || $scope.maxDate;
		$scope.use_date_endOptions.minDate = $scope.inputVO.use_date_bgn || $scope.minDate;
	};
	
	//查詢
	$scope.inquire = function() {
		if($scope.connector('get','MAO151_PARAMS') != undefined){
			$scope.connector('set','MAO151_PARAMS', undefined);
    	} else {
    		$scope.inputVO.seq = "";
    	}
		
		$scope.sendRecv("MAO231", "inquire", "com.systex.jbranch.app.server.fps.mao231.MAO231InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.resultList = tota[0].body.resultList;
				return;
			}
		});
    };
    
    //回覆
    $scope.reply = function(row, type) {
    	$scope.inputVO.seq = row.SEQ;
    	$scope.inputVO.reply_type = type;
    	$scope.inputVO.email_id = row.APL_EMP_ID;
    	$confirm({text: '是否' + ((type == 'N') ? '不同意' : ((type == 'Y') ? '同意' : '註銷')) + '本次申請？'}, {size: 'sm'}).then(function() {
    		$scope.sendRecv("MAO231", "reply", "com.systex.jbranch.app.server.fps.mao231.MAO231InputVO", $scope.inputVO, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(totas.body.msgData);
					return;
				}
				if (totas.length > 0) {
					if (totas[0].body === 'Error') {
						$scope.showErrorMsgInDialog('審核成功，但申請人E-mail錯誤，未能發送通知');
					} else {
						$scope.showMsg('審核成功');
					}
					
					$scope.inquireInit();
					$scope.inquire();
				}
			});
    	});
    };
	
	$scope.init = function() {
		$scope.inputVO = {
				seq : '',
				region_center_id : '',
				branch_area_id : '',
				branch_nbr : '',
				use_date_bgn : new Date(),
				use_date_end : undefined,
				emp_id : '',
				email_id : '',
				reply_type : ''
		}
		
		$scope.limitDate();
		
		if($scope.connector('get','MAO151_PARAMS') != undefined){
			$scope.inputVO.seq = $scope.connector('get','MAO151_PARAMS').SEQ;
			$scope.inquire();
    	}
		
		$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'MAO231'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				$scope.uhrmRCList = [];
				$scope.uhrmOPList = [];

				if (null != tota[0].body.uhrmORGList) {
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmRCList.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
					});	
					
					$scope.inputVO.region_center_id = tota[0].body.uhrmORGList[0].REGION_CENTER_ID;
					
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmOPList.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
					});
					
					$scope.inputVO.branch_area_id = tota[0].body.uhrmORGList[0].BRANCH_AREA_ID;
		        }
			}						
		});
	}
	$scope.init();
	
    //字串分成[]
    $scope.comma_split = function(value) {
    	return value.split(',');
    }
    
    // 連至客戶首頁
    $scope.custDTL = function(row) {
    	$scope.custVO = {
				CUST_ID   : row.substring(0, row.indexOf(":")),
				CUST_NAME : row.substring(row.indexOf(":") + 1, row.length)	
		}
		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
    	
    	var path = "assets/txn/CRM610/CRM610_MAIN.html";
		var set = $scope.connector("set","CRM610URL",path);
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM610/CRM610.html',
			className: 'CRM610',
			showClose: false
		});
	}
});