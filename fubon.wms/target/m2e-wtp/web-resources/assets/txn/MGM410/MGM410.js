/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM410Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('RegionController', {$scope: $scope});
		
		$scope.controllerName = "MGM410Controller";
		
		// filter
		getParameter.XML(["MGM.GIFT_KIND", "MGM.DELIVERY_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.GIFT_KIND'] = totas.data[totas.key.indexOf('MGM.GIFT_KIND')];
				$scope.mappingSet['MGM.DELIVERY_STATUS'] = totas.data[totas.key.indexOf('MGM.DELIVERY_STATUS')];
			}
		});
		
		// date picker
		$scope.s_createtimeOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
		};
		$scope.e_createtimeOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
		};
		
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.s_createtimeOptions.maxDate = $scope.inputVO.e_createtime || $scope.maxDate;
			$scope.e_createtimeOptions.minDate = $scope.inputVO.s_createtime || $scope.minDate;
		};
		
		//初始化
		$scope.init = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			$scope.inputVO = {};
			//取得活動代碼
	        $scope.sendRecv("MGM110", "getActSeq", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.mappingSet['ACT_SEQ'] = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								$scope.mappingSet['ACT_SEQ'].push({LABEL: row.ACT_NAME, DATA: row.ACT_SEQ});
		        			});
							return;
						}
			});
		}
		$scope.init();
		
		//查詢
		$scope.inquire = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			if($scope.inputVO.cust_id != undefined && $scope.inputVO.cust_id != ''){
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();	//轉大寫
			}
			$scope.sendRecv("MGM410", "inquire", "com.systex.jbranch.app.server.fps.mgm410.MGM410InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");		//查無資料
		            			return;
		            		} else {
		            			$scope.resultList = tota[0].body.resultList;
		            			$scope.outputVO = tota[0].body;		            			
		            		}
						}	
			});
		}
		
		//整批修改
		$scope.multipleEdit = function() {
			var resultList = $scope.resultList
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM410/MGM410_EDIT.html',
				className: 'MGM410_EDIT',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 	$scope.resultList = resultList;
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquire();
				}
			});
		}
		
		//完整記錄匯出
		$scope.completeExport = function() {
			$scope.sendRecv("MGM410", "completeExport", "com.systex.jbranch.app.server.fps.mgm410.MGM410InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
						}	
			});
		}
		
		//產製所得人建檔文件
		$scope.getIncomeData = function() {
			$scope.sendRecv("MGM410", "getIncomeData", "com.systex.jbranch.app.server.fps.mgm410.MGM410InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
						}	
			});
		}
		
		//報表下載
		$scope.reportDownload = function() {
			if($scope.inputVO.act_seq != undefined && $scope.inputVO.act_seq != ''){
				$scope.sendRecv("MGM410", "reportDownload", "com.systex.jbranch.app.server.fps.mgm410.MGM410InputVO", 
					{'act_seq' : $scope.inputVO.act_seq},
						function(tota, isError) {
					if (!isError) {
						
					}	
				});				
			} else {
				$scope.showErrorMsg('請選擇活動代碼。');
				return;
			}
		}
});
		