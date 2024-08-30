/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS442Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS442Controller";
		
		$scope.init = function() {
			$scope.inputVO = {
					custID	:	$scope.connector('get','INS400_cust_id'),
					othList	:	[],
					type	:	'2'
			};

		};
		$scope.init();
		
		$scope.initial = function() {
			$scope.paramList = [];
			$scope.compList= [];
			$scope.prdList = [];
//			$scope.inputVO.onceTotal = 0;  	//其他每月給付金額合計
//			$scope.inputVO.tempOnceNum = 0;	//其他一次給付金額合計
		}
		$scope.initial();
		
		$scope.addRow = function(){
			$scope.inputVO.othList.push({});
			angular.forEach($scope.inputVO.othList,function(row,index){
				row.Display_order = index + 1;

			});
		}
		
		$scope.deleteRow = function(index, row){
			if(row.status){
				var inputVO ={
					keyNO : $scope.inputVO.keyNO
				};

				$scope.sendRecv("INS441","delete","com.systex.jbranch.app.server.fps.ins441.INS441InputVO", {keyNO : row.KEY_NO},
						function(tota,isError) {
							if (!isError) {
								//判斷這筆資料是否從DB，true從DB
	//							row.status = true;
								$scope.showErrorMsg('ehl_01_common_003');	//刪除成功
								$scope.inputVO.othList.splice(index, 1);
								row.action = 'delete';
								$scope.count(row, 'once');
								$scope.count(row, 'month');
								return row;
							}
						});
				}else{
					$scope.inputVO.othList.splice(index, 1);
					
				}
			$scope.count(row, 'month');
			$scope.count(row, 'once');
		}								

		
		$scope.query = function() {
			console.log($scope.inputVO);
			$scope.sendRecv("INS441","query","com.systex.jbranch.app.server.fps.ins441.INS441InputVO", $scope.inputVO,
					function(tota,isError) {
						if (!isError) {
							$scope.inputVO.othList = tota[0].body.resultList;
							angular.forEach($scope.inputVO.othList, function(row, index) {
								row.Display_order = index + 1;
								row.status = true;
								row.action = 'update';
								$scope.count(row, 'month');
								$scope.count(row, 'once');
								return row;
								
							});
						}
			});
		}
		$scope.query();
		
		$scope.save = function() {
			var bugFlag = false;
			angular.forEach($scope.inputVO.othList, function(row, index) {
				console.log(row.OTH_ITEM);
				if (row.OTH_ITEM == '' || row.OTH_ITEM == undefined) {
					$scope.showErrorMsg('ehl_01_common_022');
					bugFlag = true;
				}

			});
			if(bugFlag){
				return false;
			}
			$scope.sendRecv("INS441","save","com.systex.jbranch.app.server.fps.ins441.INS441InputVO", $scope.inputVO,
					function(tota,isError) {
						if (!isError) {
							$scope.othTempTotal = [];
//							console.log($scope.othTempTotal);
//							console.log($scope.inputVO.othList);
//							$scope.othTempTotal.push({
//								ARTL_DEBT_AMT_MONTHLY : $scope.onceTotal
//							});
							$scope.closeThisDialog($scope.inputVO.othList);
							console.log($scope.inputVO.othList);
						}
			});
		}
		
		$scope.count = function(row, type) {
//			退休後每個月給付
			if (type == 'month') {
				$scope.onceTotal = 0
				for (var i = 0; i < $scope.inputVO.othList.length; i++) {
					$scope.onceTotal = $scope.onceTotal + parseInt($scope.inputVO.othList[i].ARTL_DEBT_AMT_MONTHLY || 0);
				}
//				退休後一次給付
			}else if(type == 'once'){
				$scope.tempOnceNum = 0
				for (var i = 0; i < $scope.inputVO.othList.length; i++) {
					$scope.tempOnceNum = $scope.tempOnceNum + parseInt($scope.inputVO.othList[i].ARTL_DEBT_AMT_ONCE || 0);
				}
			}
		}
		
});
