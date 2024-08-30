'use strict';
eSoafApp.controller('FPS910_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS910_EDITController";
		
		$scope.canEdit = true;
//		$scope.inputVO = {};
//        if($scope.edit_type == '1')
//			$scope.inputVO.inv_prd_type = '3';
//        else
//        	$scope.inputVO.inv_prd_type = '1';
		
		$scope.init = function() {
			$scope.inputVO = {
					inv_prd_type: '3'
			};
			$scope.canEdit = true;
		};
		
		$scope.clearCURR = function() {
			$scope.inputVO.currency_std = "";
		};
		
		$scope.checkID = function() {
			if($scope.inputVO.prd_id) {
//				alert($scope.stock_bond_type);
				// 基金
				if($scope.inputVO.prd_type == 'MFD') {
					$scope.sendRecv("PRD230", "checkID", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", 
						{'prd_id':$scope.inputVO.prd_id, 'status': 'S', 'stock_bond_type': $scope.stock_bond_type},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.prd_name = tota[0].body.cname;
								$scope.inputVO.risk_id = tota[0].body.risk_id;
								$scope.inputVO.currency_std = tota[0].body.currency;
								$scope.canEdit = tota[0].body.canEdit;
							}
					});
				}
				// ETF
				else if($scope.inputVO.prd_type == 'ETF') {
					$scope.sendRecv("PRD240", "etf_checkID", "com.systex.jbranch.app.server.fps.prd240.PRD240InputVO", 
						{'prd_id':$scope.inputVO.prd_id, 'status': 'S', 'stock_bond_type': $scope.stock_bond_type},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.prd_name = tota[0].body.etf_name;
								$scope.inputVO.risk_id = tota[0].body.rick_id;
								$scope.inputVO.currency_std = tota[0].body.currency;
								$scope.canEdit = tota[0].body.canEdit;
							}
					});
				}
				// INS
				else if($scope.inputVO.prd_type == 'INS') {
					$scope.sendRecv("PRD210", "checkID", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", 
						{'prd_id':$scope.inputVO.prd_id, 'status': 'S', 'ins_type': '2'},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.prd_name = tota[0].body.ins_name;
								$scope.inputVO.risk_id = "";
								$scope.inputVO.currency_std = tota[0].body.currency;
								$scope.canEdit = tota[0].body.canEdit;
							}
					});
				}
				// NANO 2020.1.16 新增奈米投 by Jacky
				else if($scope.inputVO.prd_type == 'NANO') {
					$scope.sendRecv("PRD300", "checkID", "com.systex.jbranch.app.server.fps.prd300.PRD300InputVO", 
						{'prd_id':$scope.inputVO.prd_id},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.prd_name = tota[0].body.name;
								$scope.inputVO.risk_id =  tota[0].body.riskcate_id;
								$scope.inputVO.currency_std = tota[0].body.currency;
								$scope.canEdit = tota[0].body.canEdit;
							}
					});
				}
			} else {
				$scope.inputVO.prd_name = "";
				$scope.inputVO.risk_id = "";
				$scope.inputVO.currency_std = "";
				$scope.canEdit = true;
			}
		};
		
		$scope.goSearch = function () {
			var stock_bond_type = $scope.stock_bond_type;
			var prd_type = $scope.inputVO.prd_type;
			if(!prd_type) {
				$scope.showErrorMsg('請選擇商品類型');
        		return;
			}
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT210/SOT210_ROUTE.html',
				className: 'PRD140',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop = true;
					if(prd_type == 'MFD') {
						$scope.is910 = true;
						$scope.stock_bond_type = stock_bond_type;
						$scope.txnName = "基金搜尋";
						$scope.routeURL = 'assets/txn/PRD110/PRD110.html';
					}
					else if(prd_type == 'ETF') {
						$scope.is240 = true;
						$scope.stock_bond_type = stock_bond_type;
						$scope.txnName = "ETF搜尋";
		        		$scope.routeURL = 'assets/txn/PRD120/PRD120_ETF.html';
		        		$scope.pType = "1";
					}
					else if(prd_type == 'INS') {
						$scope.is910 = true;
						$scope.is910_ins_type = '2';
						$scope.txnName = "保險搜尋";
						$scope.routeURL = 'assets/txn/PRD160/PRD160.html';
					}
					// NANO 2020.1.16 新增奈米投 by Jacky
					else if(prd_type == 'NANO') {
						$scope.is910 = true;
						$scope.stock_bond_type = stock_bond_type;
						$scope.txnName = "奈米投搜尋";
						$scope.routeURL = 'assets/txn/PRD190/PRD190.html';
					}
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					$scope.inputVO.prd_id = data.value.PRD_ID;
					$scope.inputVO.risk_id = data.value.RISKCATE_ID;
					$scope.checkID();
				}
			});
		};
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(!$scope.canEdit) {
				$scope.showErrorMsg('欄位檢核錯誤:商品代號');
        		return;
			}
			if($scope.inputVO.risk_id && ($scope.inputVO.risk_id.substring(1, 2) > $scope.inputVO.cust_risk_atr.substring(1, 2))) {
				$scope.showErrorMsg('欄位檢核錯誤:客戶風險屬性' + $scope.inputVO.cust_risk_atr + '與商品風險屬性' + $scope.inputVO.risk_id + ' 不適配');
        		return;
			}
			$scope.closeThisDialog($scope.inputVO);
		};
		
		$scope.init();
		
});