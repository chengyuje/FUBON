/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM116Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('RegionController', {$scope: $scope});
		
		// filter
		getParameter.XML(["MGM.DELIVERY_STATUS", "MGM.GIFT_KIND"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.DELIVERY_STATUS'] = totas.data[totas.key.indexOf('MGM.DELIVERY_STATUS')];
				$scope.mappingSet['MGM.GIFT_KIND'] = totas.data[totas.key.indexOf('MGM.GIFT_KIND')];
//				$scope.mappingSet['MGM.GIFT_GET_WAY'] = totas.data[totas.key.indexOf('MGM.GIFT_GET_WAY')];
			}
		});
		
		$scope.inquire = function() {
			//查詢贈品出貨紀錄
			$scope.sendRecv("MGM116", "inquire", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
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
		
		//匯出客戶簽收單
		$scope.getReceipt = function() {
			$scope.sendRecv("MGM116", "getReceipt", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'act_seq': $scope.inputVO.act_seq},
					function(tota, isError) {
						if (!isError) {
							
						}
			});
		}
		
		$scope.$on('MGM110_inquire', function(){
			$scope.init();
		});
});
		