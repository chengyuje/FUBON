/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MTC110Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, $q, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MTC110Controller";

		// filter
		getParameter.XML(["MTC.FOREIGN_CURR"], function(totas) {
			if (totas) {
				$scope.currList1 = totas.data[totas.key.indexOf('MTC.FOREIGN_CURR')];
			}
		});
		
		$scope.init = function(){
			$scope.resultList = [];
			$scope.outputVO = [];
			
			var startDate = new Date();
			var month = startDate.getMonth();
			startDate.setMonth(month - 1);
			
			$scope.inputVO = {
					cust_id : '',
					period_s : startDate,
					period_e : new Date(),
					CON_STATUS : 'A'
			}
		};
		$scope.init();
		
    	$scope.query = function(){
			$scope.resultList = [];
			$scope.outputVO = [];
			if ($scope.inputVO.cust_id != undefined && $scope.inputVO.cust_id != '') {
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();				
			}
			$scope.sendRecv("MTC110", "query", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("查無資料，請點選「新增契約」建立檔案。");
                			return;
                		}
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}
			});
    	}
    	
    	$scope.maintain = function(type, row) {
    		var currList1 = $scope.currList1;
    		var dialog = ngDialog.open({
    			template: 'assets/txn/MTC110/MTC110_MAINTAIN.html',
    			className: 'MTC110_MAINTAIN',
    			showClose: true,
    			controller: ['$scope', function($scope) {
    				$scope.row = row;
    				$scope.type = type;
    				$scope.currList1 = currList1;
    			}]
    		
    		});
    		dialog.closePromise.then(function(data) {
    			$scope.init();
    			$scope.query();
    		});
    	};
    	
    	$scope.delete = function(row) {
    		$scope.sendRecv("MTC110", "updateStatus", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", 
    			{'CON_NO': row.CON_NO, 'status': 'D'},
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg("ehl_01_common_003");	// 刪除成功
						$scope.query();
						return;
					}
			});
    	}
    	
    	$scope.print = function(row) {
    		var msg1 = "";
    		var msg2 = "";
    		// 委託人為未成年人：法定代理人、監護人須於附件一親簽
    		if (row.MINOR_YN == 'Y') {
    			msg1 = "法定代理人、監護人須於附件一親簽。";
    		}
    		// 設有次順位、第三順位監察人：次順位、第三順位監察人須簽/蓋附件二
    		if (row.CUST_ID2 != undefined || row.CUST_ID3 !=  undefined) {
    			msg2 = "次順位、第三順位監察人須簽/蓋附件二。";
    		}
    		$scope.showMsg("信託契約需為一式二份。" + msg1 + msg2);	
    		$scope.sendRecv("MTC110", "print", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", row,
				function(tota, isError) {
					if (!isError) {
					}
			});
    	}
    	
    	/*
    	 * 日期
    	 */
        $scope.sDateOptions = {
        		maxDate: $scope.maxDate,
        		minDate: $scope.minDate
        };

        $scope.eDateOptions = {
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
			$scope.sDateOptions.maxDate = $scope.inputVO.period_e || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.period_s || $scope.minDate;
		};
});