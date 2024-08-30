/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS441Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS441Controller";
		
		$scope.init = function() {
			$scope.inputVO = {
					custID	:	$scope.connector('get','INS400_cust_id'),
					insList	:	[],
					type	:	'1'
					
			};
			// 取得保險公司下拉選單
			$scope.sendRecv("INS441","getParam","com.systex.jbranch.app.server.fps.ins441.INS441InputVO", {'type' : 'COMPANY'},
					function(tota,isError) {
						if (!isError) {
							$scope.compList = tota[0].body.compList;
						}
			});
		};
		$scope.init();
		
		$scope.initial = function() {
			$scope.paramList = [];
			$scope.compList= [];
			$scope.prdList = [];
		}
		$scope.initial();
		
		// 取得該保險公司產品
		$scope.getPrdList = function(row, compID) {
			$scope.sendRecv("INS441","getParam","com.systex.jbranch.app.server.fps.ins441.INS441InputVO", {'type' : 'PRODUCT', 'compID' : compID},
					function(tota,isError) {
						if (!isError) {
							row.prdList = tota[0].body.prdList;
						}
			});
		}
		
		// 新增一列
		$scope.addRow = function(){
			$scope.inputVO.insList.push({});
			angular.forEach($scope.inputVO.insList,function(row,index){
				row.Display_order = index + 1;
			});
		}
		
		$scope.deleteRow = function(index,row){
			console.log(row);
			if(row.status){
				var inputVO ={
					keyNO : $scope.inputVO.keyNO
				};
//			if (row.KEY_NO == undefined) {
//				$scope.inputVO.insList.splice(index,1);
//				angular.forEach($scope.inputVO.insList,function(row,index){
//					row.Display_order = index +1;
//				});
				
//			} else {
				$scope.sendRecv("INS441","delete","com.systex.jbranch.app.server.fps.ins441.INS441InputVO", {keyNO : row.KEY_NO},
						function(tota,isError) {
							if (!isError) {
								//判斷這筆資料是否從DB，true從DB
		//							row.status = true;
								$scope.showErrorMsg('ehl_01_common_003');	//刪除成功
								$scope.inputVO.insList.splice(index, 1);
								row.action = 'delete';
								$scope.count(row, 'once');
								$scope.count(row, 'month');
								return row;
							}
						});
					}else{
						$scope.inputVO.insList.splice(index, 1);
					}
			$scope.count(row, 'once');
			$scope.count(row, 'month');
		}
		
		$scope.query = function() {
			$scope.sendRecv("INS441","query","com.systex.jbranch.app.server.fps.ins441.INS441InputVO", $scope.inputVO,
					function(tota,isError) {
						if (!isError) {
							$scope.inputVO.insList = tota[0].body.resultList;
							angular.forEach($scope.inputVO.insList, function(row, index) {
								row.Display_order = index + 1;
								row.status = true;
								row.action = 'update';
								$scope.count(row, 'month');
								$scope.count(row, 'once');
								$scope.getPrdList(row, row.COM_ID);
								return row;
//								if (row.ARTL_DEBT_AMT_ONCE != null) {
//									$scope.count(row, 'once');
//								} else {
//									$scope.count(row, 'month');
//								}
							});
						}
			});
		}
		$scope.query();
		
		$scope.save = function() {
			if ($scope.inputVO.insList.length != 0) {
				angular.forEach($scope.inputVO.insList, function(row, index) {
					if (row.AGE_E == '' || row.AGE_S == '') {
						$scope.showErrorMsg('ehl_01_common_022');
						return;
					}
				});
				console.log($scope.inputVO);
				$scope.sendRecv("INS441","save","com.systex.jbranch.app.server.fps.ins441.INS441InputVO", $scope.inputVO,
						function(tota,isError) {
							if (!isError) {
//								$scope.tempTotal = [];
//								$scope.tempTotal.push({
//									ARTL_DEBT_AMT_ONCE : $scope.onceTotal,
//									ARTL_DEBT_AMT_MONTHLY : $scope.monthTotal,
//								});
//								debugger;
								$scope.closeThisDialog($scope.inputVO.insList);
							}
				});
			} else {
				$scope.showMsg('');
			}
		}
		
		$scope.count = function(row, type) {
			if (type == 'once'){
				$scope.tempOnceNum = 0
				for (var i = 0; i < $scope.inputVO.insList.length; i++) {
					$scope.tempOnceNum = $scope.tempOnceNum + parseInt($scope.inputVO.insList[i].ARTL_DEBT_AMT_ONCE || 0);
					console.log($scope.inputVO.insList);
					console.log(parseInt($scope.inputVO.insList[i].ARTL_DEBT_AMT_ONCE || 0));
					console.log($scope.inputVO.insList[i].ARTL_DEBT_AMT_ONCE);
					console.log($scope.tempOnceNum);
				}
			}
			
			if (type == 'month') {
				$scope.onceTotal = 0
				for (var i = 0; i < $scope.inputVO.insList.length; i++) {
					$scope.onceTotal = $scope.onceTotal + parseInt($scope.inputVO.insList[i].ARTL_DEBT_AMT_MONTHLY || 0);
					console.log($scope.onceTotal);
					console.log(parseInt($scope.moneyUnFormat($scope.inputVO.insList[i].ARTL_DEBT_AMT_MONTHLY)));
				}
			}
		}
		
		//商品查詢(主約)
		$scope.toINS112 = function(row) {
			if (row.COM_ID == '') {
				$scope.showErrorMsg('ehl_01_INS112_001');
			} else {
				var COM_ID = row.COM_ID;
				var PRD_NAME = '';
				var PRD_ID = '';
				
				if (row.PRD_NAME != undefined) {
					PRD_NAME = row.PRD_NAME.toUpperCase();
				}
				if (row.PRD_ID != undefined) {
					PRD_ID = row.PRD_ID.toUpperCase();
				}

				var dialog = ngDialog.open({
					template: 'assets/txn/INS112/INS112.html',
					className: 'INS112',
					controller:['$scope',function($scope) {
						$scope.COM_ID = COM_ID;
						$scope.PRD_ID = PRD_ID;
						$scope.PRD_NAME = PRD_NAME;
						$scope.IS_MAIN_TYPE = row.IS_MAIN;
					}]
				});
				
				dialog.closePromise.then(function(data) {
					if (data.value != undefined) {
						if (data.value != 'cancel') {
							row.PRD_ID = data.value.PRD_ID;
						}
					}
				});
			}
		}
});
