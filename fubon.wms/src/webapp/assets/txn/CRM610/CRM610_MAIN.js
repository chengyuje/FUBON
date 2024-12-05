/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM610_MAINController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM610_MAINController";

		// 如果是 APP 來的要加上取得 role id
		if(typeof(webViewParamObj) != 'undefined'){
			$scope.login = $scope.connector("get","CRM610ROLE_ID");
			$scope.connector("set","CRM610ROLE_ID", null);
		} else {
			$scope.login = String(sysInfoService.getPriID());	
		}

		// 是否為 CRM610 最初載入的頁面（消金 CRM711.html、非消金  CRM610_MAIN.html）
		$scope.fromCRM610 = true;

		// 部分程式直接呼叫 CRM610_MAIN 再判斷是否要變換為消金首頁
		var path = '';
		if($scope.login == '004'){
			path ="assets/txn/CRM711/CRM711.html";
		}else{
			path ="assets/txn/CRM610/CRM610_MAIN.html";
		}
//		var set = $scope.connector("set","CRM610URL",path);
		$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});

		// 取得客戶帳戶資料
		function getCustAccountDetail() {
			var defer = $q.defer();
			$scope.sendRecv("SOT701", "getCustAccountDetail", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {"custID": $scope.custVO.CUST_ID},
				function (tota, isError) {
					debugger;
					if (!isError) {
						defer.resolve(tota[0].body);
					} else {
						defer.reject();
					}
				});
			return defer.promise;
		}

		// 取得客戶 Obu 註記
		function getCustObuFlag(acctList) {
			var defer = $q.defer();
			$scope.sendRecv("SOT701", "getCustObuFlag", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO",
				{"custID": $scope.custVO.CUST_ID, acctData: acctList},
				function (tota, isError) {
					if (!isError) {
						defer.resolve(tota[0].body);
					} else {
						defer.reject();
					}
				});
			return defer.promise;
		}

		// 依照 Type 取得 067050 相關資料
		function getData067050ByType(type) {
			var defer = $q.defer();
			$scope.sendRecv("SOT701", "getData067050ByType", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO",
				{"custID": $scope.custVO.CUST_ID, data067050Type: type},
				function (tota, isError) {
					if (!isError) {
						defer.resolve(tota[0].body);
					} else {
						defer.reject();
					}
				});
			return defer.promise;
		}

		$scope.loadingCustHomeData = function () {
			console.log('【客戶首頁MAIN】正在載入所需的資料...');
			$q.all({
				acctData: getCustAccountDetail(),
				data067050_067101_2: getData067050ByType("67050_067101_2"),
				data067050_067000: getData067050ByType("067050_067000"),
				data067050_067112: getData067050ByType("067050_067112"),
			}).then(function(result) {
				getCustObuFlag(result.acctData).then(function(isObu) {
					// 將 isObu 併入 result
					result.isObu = isObu;
					// 消金角色的首頁 CRM711.html 有著相似的介面，同樣也需要這些資料。其餘角色的首頁則是 CRM610_MAIN.html
					// 他們有一個共通的父級是 CRM610，所以將資料 emit 後，藉由父級監聽的方式取得，並由父級 broadcast
					// 使 CRM711.html、CRM610_MAIN.html 的各個區塊元件（客戶基本資料、資產負債總覽...）都可以取得這些資料。
					console.log('【客戶首頁MAIN】將載入好的資料發送給【客戶首頁】...');
					$scope.$emit('CRM610_MAIN', result);
				})
			});
		}

		// 非消金直接呼叫方法，消金則交給 CRM711.js 決定什麼時候呼叫
		if($scope.login !== '004'){
			$scope.loadingCustHomeData();
		}
	});