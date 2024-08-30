/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM112Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('RegionController', {$scope: $scope});
		
		$scope.controllerName = "MGM112Controller";
		$scope.ao_code = String(sysInfoService.getAoCode());
		$scope.loginBranchID = sysInfoService.getBranchID();
//		alert($scope.loginBranchID);
		$scope.loginID = sysInfoService.getUserID();
//		alert($scope.loginID);
		$scope.priID = sysInfoService.getPriID();
//		alert($scope.priID > '040');
		
		// filter
		getParameter.XML(["MGM.SIGN_STATUS", "MGM.APPR_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.SIGN_STATUS'] = totas.data[totas.key.indexOf('MGM.SIGN_STATUS')];
				$scope.mappingSet['MGM.APPR_STATUS'] = totas.data[totas.key.indexOf('MGM.APPR_STATUS')];
			}
		});
		
		$scope.inquire = function() {
			$scope.sendRecv("MGM112", "inquire", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
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
			$scope.today = new Date();
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
					
//					$scope.sendRecv("MGM112", "inquire", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
//							function(tota, isError) {
//								if (!isError) {
//									if(tota[0].body.resultList.length == 0) {
//										$scope.showMsg("ehl_01_common_009");		//查無資料
//			                			return;
//			                		}
//									$scope.resultList = tota[0].body.resultList;
//									$scope.outputVO = tota[0].body;
//									return;
//								}
//					});
				}
			}
			
			
		}
		$scope.init();
		
		$scope.edit = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM110/MGM110_MGM.html',
				className: 'MGM110_MGM',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 $scope.row = row;
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.init();
				}
			});
		}
		
		$scope.$on('MGM110_inquire', function(){
//			debugger
			$scope.init();
		});
		
});
		