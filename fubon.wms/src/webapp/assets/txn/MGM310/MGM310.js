/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM310Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('RegionController', {$scope: $scope});
		
		$scope.controllerName = "MGM310Controller";
		
		// filter
		getParameter.XML(["MGM.GIFT_KIND"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.GIFT_KIND'] = totas.data[totas.key.indexOf('MGM.GIFT_KIND')];
//				$scope.mappingSet['MGM.GIFT_GET_WAY'] = totas.data[totas.key.indexOf('MGM.GIFT_GET_WAY')];
			}
		});
		
		//初始化
		$scope.init = function() {
			$scope.inputVO.gift_seq = '';
			//取得贈品代碼
	        $scope.sendRecv("MGM310", "getGiftSeq", "com.systex.jbranch.app.server.fps.mgm310.MGM310InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.mappingSet['GIFT_SEQ'] = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								$scope.mappingSet['GIFT_SEQ'].push({LABEL: row.GIFT_NAME, DATA: row.GIFT_SEQ});
		        			});
							return;
						}
			});
		}
		$scope.init();
		
		//輸出欄初始化
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		//查詢
		$scope.inquire = function() {
			$scope.sendRecv("MGM310", "inquire", "com.systex.jbranch.app.server.fps.mgm310.MGM310InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");		//查無資料
		            			return;
		            		}
							
							angular.forEach(tota[0].body.resultList, function(row){
								row.fn = [];
								row.fn.push({LABEL: "修改", DATA: "U"});
								row.fn.push({LABEL: "刪除", DATA: "D"});
							});
							
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
						}	
			});
		}
		
		//新增禮贈品項目
		$scope.addGift = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM310/MGM310_GIFT.html',
				className: 'MGM310_GIFT',
				showClose: false,
				 controller: ['$scope', function($scope) {
	                	
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		}
		
		//刪除or修改
		$scope.edit = function(row) {
			if(row.edit == 'U'){
				var dialog = ngDialog.open({
					template: 'assets/txn/MGM310/MGM310_GIFT.html',
					className: 'MGM310_GIFT',
					showClose: false,
					 controller: ['$scope', function($scope) {
						 $scope.row = row;	
		             }]
				});
				dialog.closePromise.then(function (data) {
					if(data.value === 'successful'){
						$scope.inquireInit();
						$scope.inquire();
					}
				});
			} else if (row.edit == 'D'){
				$confirm({text: '是否確認刪除' + row.GIFT_SEQ + '？ '}, {size: 'sm'}).then(function() {
					$scope.sendRecv("MGM310", "delete", "com.systex.jbranch.app.server.fps.mgm310.MGM310InputVO", 
						{'gift_seq' : row.GIFT_SEQ},
							function(tota, isError) {
								if (!isError) {
									$scope.showSuccessMsg('ehl_01_common_003');		//刪除成功
									$scope.init();
									$scope.inquireInit();
									$scope.inquire();
								}	
					});
	            });
			}
		}
});
		