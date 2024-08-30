/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3101_MAINController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3101_MAINController";
		
		// combobox
		getParameter.XML(["CRM.TRS_PRJ_STATUS"], function(totas) {
			if (totas) {
				$scope.TRS_PRJ_STATUS = totas.data[totas.key.indexOf('CRM.TRS_PRJ_STATUS')];
			}
		});
		$scope.getAllPRJ = function() {
			$scope.sendRecv("CRM3101", "getAllPRJ", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO",{},
					function(tota, isError) {
					   if(!isError){
						   	$scope.PROJNAME = [];
		                	angular.forEach(tota[0].body.allPRJ, function(row) {
		                		$scope.PROJNAME.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_ID});
		                	});
					   }
				});
		};
		$scope.getAllPRJ();
		//
		
		// date picker
		$scope.sDateOptions = {};
		$scope.eDateOptions = {};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.edate;
			$scope.eDateOptions.minDate = $scope.inputVO.sdate;
		};
		//
		
		$scope.init = function() {
			$scope.inputVO = {};
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.data = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		//查詢
		$scope.inquire = function() {
			$scope.sendRecv("CRM3101", "inquire", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO",$scope.inputVO,
					function(tota, isError) {
						if(!isError) {
							if (tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
							}
							else {
								$scope.resultList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
								angular.forEach($scope.resultList, function(row) {
									row.set = [];
									// mantis 3264 請把「加入待移轉清單」的功能隱藏
//									row.set.push({LABEL: "加入待移轉清單", DATA: "1"});
									row.set.push({LABEL: "強制AO移轉", DATA: "2"});
									row.set.push({LABEL: "查看移轉成功名單", DATA: "6"});
									row.set.push({LABEL: "查看移轉失敗名單", DATA: "3"});
//									row.set.push({LABEL: "回收作業", DATA: "4"});
									row.set.push({LABEL: "刪除此筆匯入", DATA: "7"});
									row.set.push({LABEL: "刪除整個專案", DATA: "5"});
									
									row.expired_flag = '';
									var now = new Date();
									now.setHours(0,0,0,0);
									if(now < $scope.toJsDate(row.PRJ_DATE_BGN))
										row.expired_flag = '(未啟用)';
									else if(now > $scope.toJsDate(row.PRJ_DATE_END))
										row.expired_flag = '(已過期)';
								});
		                	}
					   }
				});
		};
		
		//新增專案名稱
		$scope.insert = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM3101/CRM3101_INSERT.html',
				className: 'CRM3101_INSERT',
				showClose: false
			});
			dialog.closePromise.then(function(data) {
				if(data.value === 'successful') {
					$scope.getAllPRJ();
					$scope.inquireInit();
					$scope.inquire();
	  			}
			});
		};
		
		//新增匯入
		$scope.insertExp = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM3101/CRM3101_EXPORT.html',
				className: 'CRM3101_EXPORT',
				showClose: false
			});
			dialog.closePromise.then(function(data) {
				if(data.value === 'successful') {
					$scope.inquireInit();
					$scope.inquire();
	  			}
			});
		};
		
		//查詢結果:功能 old code
		$scope.doFunc = function(row) {
			if(row.acttype) {
				var now = new Date();
				now.setHours(0,0,0,0);
				if((row.acttype == '1' || row.acttype == '2') && (now > $scope.toJsDate(row.PRJ_DATE_END))) {
					$scope.showErrorMsg("該專案已過期");
					return;
				} else if((row.acttype == '1' || row.acttype == '2') && (now < $scope.toJsDate(row.PRJ_DATE_BGN))) {
					$scope.showErrorMsg("該專案未啟用");
					return;
				}
				if(row.acttype != '3' && row.acttype != '6') {
					if (row.acttype == '4') {
						if(row.PRJ_STATUS != '07') {
							$scope.showErrorMsg("尚未執行“強制AO移轉”，無法執行");
							return;
						}
					}
					// 2017/2/18 add confirm only
					var selIndex = row.set.map(function(e) { return e.DATA; }).indexOf(row.acttype);
					var confirmText = '';
					if(row.acttype == 5)
						confirmText = '確定執行: 刪除專案會將其他同專案名稱相關匯入一起刪除?';
					else
						confirmText = '確定執行 : '+ row.set[selIndex].LABEL + '?';
					$confirm({text: confirmText}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CRM3101", "doFunction", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {'PRJ_ID': row.PRJ_ID, 'IMP_FILE_NAME': row.IMP_FILE_NAME, 'acttype': row.acttype},
								function(tota, isError) {
								   if(!isError) {
									   if(row.acttype=='1') {
										   if(tota[0].body.error == 'N')
											   $scope.showMsg("加入移轉成功");
										   else {
											   var dialog = ngDialog.open({
													template: 'assets/txn/CRM3101/CRM3101_ERRLST.html',
													className: 'CRM3101_ERRLST',
													showClose: false,
													controller: ['$scope', function($scope) {
														$scope.row = row;
														$scope.act = '3';
													}]
												});
										   }
									   } else if(row.acttype=='2') {
										   if(tota[0].body.error == 'N')
											   $scope.showMsg("強制AO移轉成功");
										   else {
											   var dialog = ngDialog.open({
													template: 'assets/txn/CRM3101/CRM3101_ERRLST.html',
													className: 'CRM3101_ERRLST',
													showClose: false,
													controller: ['$scope', function($scope) {
														$scope.row = row;
														$scope.act = '3';
													}]
												});
										   }
									   }
									   else if (row.acttype=='4')
										   $scope.showMsg("回收作業成功");
									   else if (row.acttype=='5') {
										   if(tota[0].body.error == 'N')
											   $scope.showMsg("刪除整個專案成功");
										   else
											   $scope.showErrorMsg("刪除整個專案失敗");
									   }
									   else if (row.acttype=='7') {
										   if(tota[0].body.error == 'N')
											   $scope.showMsg("刪除此筆匯入成功");
										   else
											   $scope.showErrorMsg("刪除此筆匯入失敗");
									   }
									   $scope.getAllPRJ();
									   $scope.inquireInit();
									   $scope.inquire();
								}
						});
					});
				} else if (row.acttype == '3'){   //查看移轉失敗名單
					var dialog = ngDialog.open({
						template: 'assets/txn/CRM3101/CRM3101_ERRLST.html',
						className: 'CRM3101_ERRLST',
						showClose: false,
						controller: ['$scope', function($scope) {
							$scope.row = row;
							$scope.act = '3';
						}]
					});
				} else if (row.acttype == '6'){	  //查看移轉成功名單
					var dialog = ngDialog.open({
						template: 'assets/txn/CRM3101/CRM3101_SCSLST.html',
						className: 'CRM3101_SCSLST',
						showClose: false,
						controller: ['$scope', function($scope) {
							$scope.row = row;
							$scope.act = '6';
						}]
					});
				}
			}
		};
		
		//匯入失敗名單
		$scope.failcount = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM3101/CRM3101_ERRLST.html',
				className: 'CRM3101_ERRLST',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = row;
				}]
			});
		};
		
		//匯出
		$scope.exportRPT = function() {
			$scope.sendRecv("CRM3101", "export","com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {'list':$scope.resultList},
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
		
		
});