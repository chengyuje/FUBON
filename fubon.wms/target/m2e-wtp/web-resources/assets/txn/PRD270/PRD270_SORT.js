'use strict';
eSoafApp.controller('PRD270_SORTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD270_SORTController";
		
		$scope.init = function() {
			$scope.listBase = [{'rank': 1, 'prd_id': null, 'canEdit': true},{'rank': 2, 'prd_id': null, 'canEdit': true},{'rank': 3, 'prd_id': null, 'canEdit': true},{'rank': 4, 'prd_id': null, 'canEdit': true},{'rank': 5, 'prd_id': null, 'canEdit': true},
								{'rank': 6, 'prd_id': null, 'canEdit': true},{'rank': 7, 'prd_id': null, 'canEdit': true},{'rank': 8, 'prd_id': null, 'canEdit': true},{'rank': 9, 'prd_id': null, 'canEdit': true},{'rank': 10, 'prd_id': null, 'canEdit': true}];
			$scope.sendRecv("PRD270", "getRank", "com.systex.jbranch.app.server.fps.prd270.PRD270InputVO", {},
				function(tota, isError) {
					if (!isError) {
						angular.forEach(tota[0].body.resultList, function(row) {
							var exiIndex = $scope.listBase.map(function(e) { return e.rank; }).indexOf(row.PRD_RANK);
							if(exiIndex != -1)
								$scope.listBase[exiIndex].prd_id = row.PRD_ID;
								$scope.listBase[exiIndex].sn_name = row.SN_CNAME;
								$scope.listBase[exiIndex].risk_id = row.RISKCATE_ID;
						});
					}
			});
		};
		$scope.init();
		
		$scope.checkID = function(dataRow) {
			if(dataRow.prd_id) {
				$scope.sendRecv("PRD270", "checkID", "com.systex.jbranch.app.server.fps.prd270.PRD270InputVO", {'prd_id':dataRow.prd_id, 'status':'S'},
					function(tota, isError) {
						if (!isError) {
							dataRow.sn_name = tota[0].body.cname;
							dataRow.risk_id = tota[0].body.rick_id;
							dataRow.canEdit = tota[0].body.canEdit;
						}
				});
			} else {
				dataRow.sn_name = "";
				dataRow.risk_id = "";
				dataRow.canEdit = true;
			}
		};
		
		$scope.goPRD140_SN = function (dataRow) {
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT210/SOT210_ROUTE.html',
				className: 'PRD140',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop = true;
					$scope.is270 = true;
					$scope.txnName = "SN搜尋";
	        		$scope.routeURL = 'assets/txn/PRD140/PRD140.html';
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					dataRow.prd_id = data.value.PRD_ID;
					dataRow.risk_id = data.value.RISKCATE_ID;
					$scope.checkID(dataRow);
				}
			});
		};
		
		$scope.save = function() {
			var data = $scope.listBase.filter(function(row) {
				return row.prd_id;
	    	});
			// ----------------------------------------------------------------------
			var check1 = false;
			var check2 = false;
			angular.forEach(data, function(row) {
				if(!row.canEdit)
					check1 = true;
//				if(row.risk_id == "P1")
//					check2 = true;
			});
			if(check1) {
				$scope.showErrorMsg('尚有錯誤的代碼，請重新輸入。');
				return;
			}
//			if(!check2) {
//				$scope.showErrorMsg('商品風險等級至少需有一個"P1"，請重新輸入。');
//				return;
//			}
			// ----------------------------------------------------------------------
			var uniq = data.map((e) => {
        	  return {'count': 1, 'prd_id': e.prd_id}
        	})
        	.reduce((a, b) => {
        	  a[b.prd_id] = (a[b.prd_id] || 0) + b.count
        	  return a
        	}, {});
        	var duplicates = Object.keys(uniq).filter((a) => uniq[a] > 1);
        	if(duplicates.length > 0) {
        		$scope.showErrorMsg('有重複代碼: ' + duplicates + '，請重新選擇。');
        		return;
        	}
        	// ----------------------------------------------------------------------
        	
        	$scope.sendRecv("PRD270", "saveSort", "com.systex.jbranch.app.server.fps.prd270.PRD270InputVO", {'review_list': data},
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
						$scope.init();
					}
			});
		};
		
		
		
});