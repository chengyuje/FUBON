/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM114Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		
		$scope.inquire = function() {
			$scope.sendRecv("MGM114", "inquire", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");		//查無資料
	                			return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		}
		
		//初始化
		$scope.init = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			
			if($scope.connector('get', 'MGM110_inquireVO') != null){
				$scope.inputVO = $scope.connector('get', 'MGM110_inquireVO');
				
				if($scope.inputVO.act_seq != undefined && $scope.inputVO.act_seq != null && $scope.inputVO.act_seq != ''){
					
					$scope.inputVO.role = '';
					$scope.ao_list = String(sysInfoService.getAoCode());
					if ($scope.ao_list != '' &&  $scope.ao_list != undefined) {
						$scope.inputVO.role = 'ao';
						$scope.inputVO.ao_list = $scope.ao_list;
					}
					$scope.inquire();
				}
			}
		}
		$scope.init();
		
		//推薦人核點明細
		$scope.pointDetail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM114/MGM114_pointDetail.html',
				className: 'MGM114_pointDetail',
				showClose: false,
				 controller: ['$scope', function($scope) {
	                	$scope.row = row;
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					
				}
			});
		}
		
		//禮贈品兌換紀錄
		$scope.exchangeDetail = function(row) {
			var act_type = $scope.inputVO.act_type;
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM114/MGM114_exchangeDetail.html',
				className: 'MGM114_exchangeDetail',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 	$scope.row = row;
					 	$scope.act_type = act_type;
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					
				}
			});
		}
		
		$scope.$on('MGM110_inquire', function(){
			$scope.init();
		});
});
		