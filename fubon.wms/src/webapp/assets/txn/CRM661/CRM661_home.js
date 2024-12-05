/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM661_HOMEController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM661_HOMEController";
		
		//xml參數初始化
		getParameter.XML(['CRM.REL_TYPE'], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.REL_TYPE'] = totas.data[totas.key.indexOf('CRM.REL_TYPE')];
			}
		});
		$scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		
		/**順序問題導致資料錯誤，由於初始化方法只用過一次，直接將方法程式放入查詢方法中**/		
		$scope.inquire = function(){
			//init
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
			$scope.inputVO.cust_name = $scope.custVO.CUST_NAME;
			//inquireInit
			$scope.mainList = [];
			$scope.resultList_rel = [];
			
			//關係戶初始查詢
			$scope.sendRecv("CRM661", "initial_home", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", {'cust_id':$scope.custVO.CUST_ID},
				function(tota, isError) {
				
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					$scope.VATYPE = '1';
					$scope.resultList2 = tota[0].body.resultList2;
					
					//判斷家庭戶是否有資料，有可能V做從戶
					$scope.sendRecv("CRM661", "query_PRV", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", {'cust_id':$scope.custVO.CUST_ID},
						function(tota, isError) {			
							debugger;			
							if(tota[0].body.resultList.length > 0) {
								if(tota[0].body.resultList[0].CUST_ID_M == $scope.custVO.CUST_ID){		//主戶
									$scope.VATYPE = '2';
								}else if(tota[0].body.resultList[0].CUST_ID_S == $scope.custVO.CUST_ID){	//從戶
									$scope.VATYPE = '1';
								}
								
								$scope.custVO.CRM661_VATYPE = $scope.VATYPE;
							}else{
								//家庭戶沒資料才判斷是否為V/A => H/T/K
								if($scope.resultList2 && $scope.resultList2.length > 0) {
									debugger
									if($scope.resultList2[0].VIP_DEGREE == 'H' || $scope.resultList2[0].VIP_DEGREE == 'T' || $scope.resultList2[0].VIP_DEGREE == 'K'){
										//判斷ID長度是否小於10位
										//自然人ID長度10位,若小於10位則為非自然人,不可加入家庭戶
										if($scope.custVO.CUST_ID.length >= 10){
											$scope.VATYPE = '2';
											$scope.custVO.CRM661_VATYPE = $scope.VATYPE;
										}
									}
								}
							}
					});
					
					//主從戶判斷
					$scope.sendRecv("CRM661", "MS_check", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", {'cust_id':$scope.custVO.CUST_ID},
						function(tota, isError) {
							if(tota[0].body.resultList.length > 0) {
								if(tota[0].body.resultList[0].CUST_ID_M == $scope.custVO.CUST_ID){
									$scope.custVO.CRM661_ID_M = 'M'; //主戶
								}else{
									$scope.custVO.CRM661_ID_M = 'S'; //從戶
								}
							}else{
								$scope.custVO.CRM661_ID_M = 'M'; //第一次進入，當主戶
							}
					});
					//關係戶明細
					$scope.sendRecv("CRM661", "inquire", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", {'cust_id':$scope.custVO.CUST_ID},
						function(tota, isError) {
							if (isError) {
								$scope.showErrorMsg(tota[0].body.msgData);
							}
							$scope.mappingSet['JOIN_SRV_CUST_ID'] = [];
							$scope.resultList_rel = tota[0].body.resultList_rel;
							angular.forEach($scope.resultList_rel, function(row, index, objs){
								$scope.mappingSet['JOIN_SRV_CUST_ID'].push({LABEL: row.CUST_NAME, DATA: row.CUST_ID});
							});
					});
			});
		}	
		$scope.inquire();
		
		//詳細資料/家庭戶
		$scope.detail = function(data) {
			$scope.data = data;
			if($scope.data == 'CRM661'){
				$scope.connector('set','CRM610_tab', 5);
			}else{
				$scope.connector('set','CRM610_tab', 6);
			}
			$scope.custVO.CRM661_VATYPE = $scope.VATYPE;
			var path = "assets/txn/CRM610/CRM610_DETAIL.html";
			$scope.connector("set","CRM610URL",path);
			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
		}
		
		//連客戶首頁
		$scope.gohome = function(row) {
			$scope.sendRecv("CRM110", "inquire", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", {'cust_id':row},
				function(tota110, isError) {	
					if(tota110[0].body.resultList.length == 1) {
						var path = '';					
						$scope.CRM_CUSTVO = {
							CUST_ID :  tota110[0].body.resultList[0].CUST_ID,
							CUST_NAME :tota110[0].body.resultList[0].CUST_NAME
						}
						$scope.connector('set','CRM_CUSTVO',$scope.CRM_CUSTVO)
						path = "assets/txn/CRM610/CRM610_MAIN.html";					
						$scope.connector("set","CRM610URL",path);
						$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
						var dialog = ngDialog.open({
							template: 'assets/txn/CRM610/CRM610.html',
							className: 'CRM610',
							showClose: false
						});
					}			
			});
		};
		
});