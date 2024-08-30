/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MAO221Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $filter, $timeout, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MAO221Controller";
		
		// filter
		getParameter.XML(["MAO.DEV_STATUS_AO", "MAO.USE_PERIOD"], function(totas) {
			if (totas) {
				$scope.mappingSet['MAO.DEV_STATUS_AO'] = totas.data[totas.key.indexOf('MAO.DEV_STATUS_AO')];
				$scope.mappingSet['MAO.USE_PERIOD'] = totas.data[totas.key.indexOf('MAO.USE_PERIOD')];
			}
		});
	    //
		
		/*
		 * 取得UHRM人員清單(由員工檔+角色檔)
		 */
		$scope.getUHRMList = function() {
			$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
						if ($scope.mappingSet['UHRM_LIST'].length <= 2) {
							$scope.inputVO.emp_id = tota[0].body.uhrmList[0].DATA;
						}
					}
				}
			);
		};
		
		//設定USE_PERIOD參數
//		$scope.mappingSet['USE_PERIOD'] = [{LABEL: "09:00~13:00", DATA: "1"}, {LABEL: "13:00~17:00", DATA: "2"}, {LABEL: "17:00~09:00(隔日)", DATA: "3"}];
		
		// datepicker
		$scope.use_date_bgnOptions = {
			maxDate: $scope.inputVO.use_date_end || $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.use_date_endOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.use_date_bgn || $scope.minDate
		};
		$scope.altInputFormats = ['M!/d!/yyyy'];
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
		
		$scope.init = function() {
			$scope.data = [];
			$scope.resultList = [];
			
			$scope.inputVO = {
					use_date_bgn : new Date(),
					use_date_end : new Date()
			}
			
			$scope.limitDate();
			
			$scope.getUHRMList();
		}
		
		$timeout(function(){$scope.init();},500);
		
		//輸出欄初始化
		$scope.inquireInit = function(){
			$scope.resultList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		//查詢
		$scope.inquire = function() {
			$scope.inquireInit();
			$scope.sendRecv("MAO221", "inquire", "com.systex.jbranch.app.server.fps.mao221.MAO221InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.inquireInit();
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList = _.sortBy(tota[0].body.resultList, ['USE_DATE']);
							$scope.outputVO = tota[0].body;
							return;
						}
			});
	    };
	    

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
        
        //回覆
	    $scope.reply = function(row, type) {
	    	$scope.inputVO.seq = row.SEQ;
	    	$scope.inputVO.reply_type = type;
	    	$scope.inputVO.email_id = row.APL_EMP_ID;
	    	$confirm({text: '是否' + ((type == 'N') ? '不同意' : ((type == 'Y') ? '同意' : '註銷')) + '本次申請？'}, {size: 'sm'}).then(function() {
	    		$scope.sendRecv("MAO231", "reply", "com.systex.jbranch.app.server.fps.mao231.MAO231InputVO", $scope.inputVO,
						function(totas, isError) {
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
	    				}
				);
        	});
	    };
});