/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM452ListController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', { $scope: $scope });
		$scope.controllerName = "CRM452ListController";

		$scope.init = function() {
			$scope.inputVO = $scope.connector("get", "CRM453_inputVO");
			$scope.connector("set", "CRM453_inputVO", null);

			// 測試用
			//			$scope.inputVO = {
			//				empID : '008918', 
			//				type  : 'period', 
			//				custID: 'A221560424',
			//				seqNum: ''
			//			};
		};
		$scope.init();

		$scope.action = function(type, row) {
			if ($scope.inputVO.type == 'single') { // 1:期間 / 2:單筆
				$scope.inputVO.apply_cat = "2";
			} else {
				$scope.inputVO.apply_cat = "1";
			}

			$scope.inputVO.actionType = type;

			$scope.inputVO.con_degree = $scope.con_degree;
			$scope.inputVO.prod_type = "";
			if (($scope.inputVO.apply_cat == '1' && $scope.inputVO.apply_type == '1') ||
				($scope.inputVO.apply_cat == '2' && ($scope.inputVO.apply_type == '1' || $scope.inputVO.apply_type == '2' || $scope.inputVO.apply_type == '6'))) { // 單筆 & 基金 or 期間 & 基金議價
				$scope.inputVO.prod_type = 1;
			} else {
				$scope.inputVO.prod_type = 2;
			}

			$scope.inputVO.fromMPlus = true;

			if (undefined != row) {
				$scope.inputVO.apply_seq = row.APPLY_SEQ;
				$scope.inputVO.comments = row.comments;
				$scope.inputVO.cust_id = row.CUST_ID;
				$scope.inputVO.auth_date_end = new Date(row.BRG_END_DATE);
				$scope.inputVO.reviewList = null;	// 避免先按全部同意/退回後，再按單筆同意/退回，所以要將$scope.inputVO.reviewList清空。
				$confirm({ text: (type == 'accept' ? '是否同意' : ''), text13: (type == 'accept' ? '' : '是否退回') }, { size: 'sm' }).then(function() {
					//$confirm({text: '是否' + (type == 'accept' ? '同意' : '退回')}, {size: 'sm'}).then(function() {
					$scope.sendRecv("CRM431", "review", "com.systex.jbranch.app.server.fps.crm431.CRM431InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota.length > 0) {
									$scope.viewDetail();
									return;
								}
							}
						});
				});
			} else {
				$scope.inputVO.reviewList = $scope.paramList;
				$confirm({ text: (type == 'allAccept' ? '全部同意' : ''), text13: (type == 'allAccept' ? '' : '全部退回') }, { size: 'sm' }).then(function() {
					//$confirm({text: '是否' + (type == 'allAccept' ? '全部同意' : '全部退回')}, {size: 'sm'}).then(function() {
					$scope.sendRecv("CRM431", "review", "com.systex.jbranch.app.server.fps.crm431.CRM431InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota.length > 0) {
									$scope.viewDetail();
									return;
								}
							}
						});
				});
			}
		}

		$scope.viewDetail = function() {
			$scope.sendRecv("CRM452", "viewDetail", "com.systex.jbranch.app.server.fps.crm452.CRM452InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if (tota[0].body.applyList.length == 0) {
							$confirm({ title: '提醒', text: '授權完成', hideCancel: true }, { size: 'sm' }).then(function() {
								location.replace(location.href + "&time=" + new Date().getTime());
							});
							return;
						} else {
							$scope.paramList = tota[0].body.applyList;
							$scope.apply_type = $scope.paramList[0].APPLY_TYPE;
							$scope.cust_id = $scope.paramList[0].CUST_ID;
							$scope.cust_name = $scope.paramList[0].CUST_NAME;
							$scope.vip_degree = $scope.paramList[0].VIP_DEGREE;
							$scope.con_degree = $scope.paramList[0].CON_DEGREE;
							$scope.apply_date = $scope.paramList[0].APPLY_DATE;
							$scope.aum_amt = $scope.paramList[0].AUM_AMT;
							$scope.y_profee = $scope.paramList[0].Y_PROFEE;
						}
					}
				});
		};
		$scope.viewDetail();
	});