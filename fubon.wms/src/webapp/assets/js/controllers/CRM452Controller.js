/**3
 * 
 */
'use strict';
eSoafApp.config(['$locationProvider', function($locationProvider) {  
	$locationProvider.html5Mode(true);  
}]);

eSoafApp.controller("CRM452Controller",['$rootScope', '$scope', '$controller', '$location', '$interpolate', '$timeout', 'projInfoService', '$confirm', 'sysInfoService', 'socketService', 'ngDialog', 'getParameter',
    function($rootScope, $scope, $controller, $location, $interpolate, $timeout, projInfoService, $confirm, sysInfoService, socketService, ngDialog, getParameter) {
		$controller('BaseController', {$scope : $scope});
		
		$scope.init = function() {
			debugger;
			$scope.inputVO = {
				empID : $location.search().empID, 
				type  : $location.search().type, 
				custID: $location.search().custID,
				seqNum: $location.search().seqNum
			};
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
				($scope.inputVO.apply_cat == '2' && ($scope.inputVO.apply_type == '1' || $scope.inputVO.apply_type == '2'))	) { // 單筆 & 基金 or 期間 & 基金議價
				$scope.inputVO.prod_type = 1;
			} else {
				$scope.inputVO.prod_type = 2;
			}
			
			$scope.inputVO.fromMPlus = true;
						
			if (undefined != row) {
//				if($scope.inputVO.type == 'period'){	//期間議價才需要判斷是否已超過期間議價迄日
//					var endDate = new Date(row.BRG_END_DATE);
//					var now = new Date();
//					var todayAtMidn = new Date(now.getFullYear(), now.getMonth(), now.getDate());
//					
//					if(endDate < todayAtMidn){
//						$scope.showMsg("已超過期間議價迄日，無法進行覆核。");
//						return;
//					}
//				}
				$scope.inputVO.apply_seq = row.APPLY_SEQ;
				$scope.inputVO.comments = row.comments;
				$scope.inputVO.cust_id = row.CUST_ID;
				$scope.inputVO.auth_date_end = new Date(row.BRG_END_DATE);
				$scope.inputVO.reviewList = null;	// 避免先按全部同意/退回後，再按單筆同意/退回，所以要將$scope.inputVO.reviewList清空。
				$confirm({text: (type == 'accept' ? '是否同意' : ''),text13: (type == 'accept' ? '' : '是否退回')}, {size: 'sm'}).then(function() {				
				//$confirm({text: '是否' + (type == 'accept' ? '同意' : '退回')}, {size: 'sm'}).then(function() {
					$scope.sendRecv("CRM431", "review", "com.systex.jbranch.app.server.fps.crm431.CRM431InputVO", $scope.inputVO,
							function(tota, isError) {
				 				if(!isError){
				 					if (isError) {
				 					}
				 					if(tota.length > 0) {
				 						location.replace(location.href + "&time=" + new Date().getTime()); 
				 						return;
				 					}
				 				}
		 			});
				});
			} else {
//				if($scope.inputVO.type == 'period'){	//期間議價才需要判斷是否已超過期間議價迄日
//					$scope.inputVO.reviewList = [];
//					var now = new Date();	//現在的日期 & 時間
//					var todayAtMidn = new Date(now.getFullYear(), now.getMonth(), now.getDate());	//現在的日期
//					
//					angular.forEach($scope.paramList, function(row) {
//						var endDate = new Date(row.BRG_END_DATE);
//						if(endDate >= todayAtMidn){
//							$scope.inputVO.reviewList.push(row);
//						}
//					});
//					
//					if($scope.inputVO.reviewList.length < 1){
//						$scope.showMsg("已超過期間議價迄日，無法進行覆核。");
//						return;
//					}
//					
//				}else{
//					$scope.inputVO.reviewList = $scope.paramList;
//				}
				$scope.inputVO.reviewList = $scope.paramList;
				$confirm({text: (type == 'allAccept' ? '全部同意' : ''),text13: (type == 'allAccept' ? '' : '全部退回')}, {size: 'sm'}).then(function() {
				//$confirm({text: '是否' + (type == 'allAccept' ? '全部同意' : '全部退回')}, {size: 'sm'}).then(function() {
					$scope.sendRecv("CRM431", "review", "com.systex.jbranch.app.server.fps.crm431.CRM431InputVO", $scope.inputVO,
							function(tota, isError) {
				 				if(!isError){
				 					if (isError) {
				 					}
				 					if(tota.length > 0) {
				 						location.replace(location.href + "&time=" + new Date().getTime());
				 						return;
				 					}
				 				}
		 			});
				});
			}
		};
		
		$scope.viewDetail = function() {
	   		$scope.sendRecv("CRM452", "viewDetail", "com.systex.jbranch.app.server.fps.crm452.CRM452InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.applyList.length == 0) {
              	                return;
							} else {
								$scope.paramList = tota[0].body.applyList;
								
								$scope.apply_type 	= $scope.paramList[0].APPLY_TYPE;
	                			$scope.cust_id 		= $scope.paramList[0].CUST_ID;
	                			$scope.cust_name 	= $scope.paramList[0].CUST_NAME;
				       			$scope.vip_degree 	= $scope.paramList[0].VIP_DEGREE;
	                			$scope.con_degree 	= $scope.paramList[0].CON_DEGREE;
	                			$scope.apply_date 	= $scope.paramList[0].APPLY_DATE;
	                			$scope.aum_amt 		= $scope.paramList[0].AUM_AMT;
	                			$scope.y_profee 	= $scope.paramList[0].Y_PROFEE;
							}
						}
	   			});
		};
		$scope.viewDetail();
}]);