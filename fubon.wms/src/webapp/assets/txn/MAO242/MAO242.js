'use strict';
eSoafApp.controller('MAO242Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $timeout) {
	$controller('BaseController', {$scope: $scope});
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	$scope.controllerName = "MAO242Controller";
	
	// filter
	getParameter.XML(["MAO.DEV_STATUS_AO", "MAO.DEV_STATUS_MG", "MAO.DEV_STATUS_CODI"], function(totas) {
		if (totas) {
			$scope.mappingSet['MAO.DEV_STATUS_AO'] = totas.data[totas.key.indexOf('MAO.DEV_STATUS_AO')];
			$scope.mappingSet['MAO.DEV_STATUS_MG'] = totas.data[totas.key.indexOf('MAO.DEV_STATUS_MG')];
			$scope.mappingSet['MAO.DEV_STATUS_CODI'] = totas.data[totas.key.indexOf('MAO.DEV_STATUS_CODI')];
		}
	});
	
	// 設定USE_PERIOD參數
	$scope.mappingSet['USE_PERIOD'] = [{LABEL: "09:00~13:00", DATA: "1"}, {LABEL: "13:00~17:00", DATA: "2"}, {LABEL: "17:00~09:00(隔日)", DATA: "3"}];
    //
	
	// date picker
	// 有效起始日期
	$scope.use_date_bgnOptions = {};
	$scope.use_date_endOptions = {};
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.use_date_bgnOptions.maxDate = $scope.inputVO.use_date_end;
		$scope.use_date_endOptions.minDate = $scope.inputVO.use_date_bgn;
	};

	$scope.timer = function(row) {
		if(row) {
			var time = new Date();
			var use_time_end = undefined;
			var use_date = row.USE_DATE.substring(0,10) + " " + "00:00:00";
			
			switch(row.USE_PERIOD_E_TIME) {
			    case '0900':
					use_time_end = ($scope.toJsDate(use_date)).addHours(9);
			        break;
			    case '1000':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(10);
			    	break;
			    case '1100':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(11);
			    	break;
			    case '1200':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(12);
			    	break;
			    case '1300':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(13);
			    	break;
			    case '1400':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(14);
			    	break;
			    case '1500':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(15);
			    	break;
			    case '1600':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(16);
			    	break;
			    case '1700':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(17);
			    	break;
			    case '1800':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(18);
			    	break;
			    case '0800':
			    	use_time_end = ($scope.toJsDate(use_date)).addHours(32);
			    	break;
			}
			
			if (Date.parse(time).valueOf() > Date.parse(use_time_end).valueOf())
				return true; //現在時間超過使用時間
			else
				return false; //現在時間小於使用時間
		}
	}
	
	Date.prototype.addHours = function(h){
		this.setHours(this.getHours()+h);
		return this;
	}

	$scope.init = function() {
		$scope.inputVO = {
			regionCenterId : '',
			bra_areaID : '',
			use_date_bgn : undefined,
			use_date_end : undefined,
			dev_status : ''
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
					
					$scope.inputVO.regionCenterId = tota[0].body.uhrmORGList[0].REGION_CENTER_ID;
					
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmOPList.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
					});
					
					$scope.inputVO.bra_areaID = tota[0].body.uhrmORGList[0].BRANCH_AREA_ID;
		        }
			}						
		});
	}
	$scope.init();
	
	$scope.inquireInit = function(){
		$scope.resultList = [];
	}
	$scope.inquireInit();
	
	//查詢
	$scope.inquire = function() {
		$scope.sendRecv("MAO242", "inquire", "com.systex.jbranch.app.server.fps.mao242.MAO242InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}
		});
    };
    
    //回覆
    $scope.reply = function(row,type) {
    	$scope.inputVO.seq = row.SEQ;
    	$scope.inputVO.reply_type = type;
		$scope.sendRecv("MAO242", "reply", "com.systex.jbranch.app.server.fps.mao242.MAO242InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}
			if (totas.length > 0) {
				$scope.showMsg('操作成功');
				$scope.inquireInit();
				$scope.inquire();
			}
		});
    };
});