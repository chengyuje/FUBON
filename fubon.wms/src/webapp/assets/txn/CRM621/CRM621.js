/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM621Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM621Controller";

		//輸出欄初始化
		$scope.inquireInit = function(){
			$scope.resultList = [];
		}
		$scope.inquireInit();
		
//		初始查詢
		$scope.inquire_A = function(){
		$scope.resultList_A = [];
		$scope.inputVO.detail_YN = 'N';
		$scope.sendRecv("CRM621", "inquire_A", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					if(tota[0].body.resultList_A != null && tota[0].body.resultList_A.length >0) {
						$scope.resultList_A = tota[0].body.resultList_A;
						$scope.outputVO_A = tota[0].body;
					}	
				}
		)};

		$scope.inquire_message = function(){

			$scope.sendRecv("CRM621", "inquire_message", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO",
				{'cust_id': $scope.inputVO.cust_id, 'data067050_067101_2': crm610Data.data067050_067101_2, 'data067050_067000': crm610Data.data067050_067000},
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
		
					$scope.mail = tota[0].body.mail;
					$scope.address = tota[0].body.addr;

					if(tota[0].body.custTxFlag !=null ){
						$scope.custTxFlag = tota[0].body.custTxFlag;
					}
	    	}
		)};


		$scope.inquire_phone = function(){
			$scope.sendRecv("CRM621", "inquire_phone", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO",
				{'cust_id': $scope.inputVO.cust_id, 'data067050_067101_2': crm610Data.data067050_067101_2, 'data067050_067000': crm610Data.data067050_067000},
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					tota[0].body.phoneList
						.forEach(info => {
							if (info.CODE === '3') $scope.tel = info.NUMBER;
							if (info.CODE === '5') $scope.phone = info.NUMBER;
						});
	    	}
		)};

		$scope.detail = function () {
			$scope.connector('set','CRM621_custTxFlag', $scope.custTxFlag);
			$scope.connector('set','CRM621_esbData', $scope.resultList_message);
//			$scope.connector('set','CRM110_CUST_ID', $scope.cust_id);
        	$scope.connector('set','CRM610_tab', 3);
        	var path = "assets/txn/CRM610/CRM610_DETAIL.html";
			$scope.connector("set","CRM610URL",path);
			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
			var openDialog = $scope.connector('get','openDialog');
			//從名單CAM200 連接至客戶首頁詳細資料 -- BY Stella
			if(openDialog != undefined) {
				$scope.CRM_CUSTVO = $scope.connector('get','CRM_CUSTVO', $scope.custVO);
				
				$scope.custVO = {
						CUST_ID :  $scope.CRM_CUSTVO.CUST_ID,
						CUST_NAME :$scope.CRM_CUSTVO.CUST_NAME	
				}
				
				$scope.connector('set','CRM_CUSTVO',$scope.custVO);
				var dialog = ngDialog.open({
					template: 'assets/txn/CRM610/CRM610.html',
					className: 'CRM610',
					showClose: false
				});
				return;
			}
        }

		//初始化
		function init(){
			$scope.cust_id = '';
//			$scope.cust_id = $scope.connector('get','CRM110_CUST_ID');
			$scope.cust_id =  $scope.custVO.CUST_ID;
			$scope.inputVO.cust_id = $scope.cust_id;
			$scope.login = String(sysInfoService.getPriID());


			$scope.inquire_A();
			$scope.inquire_message();
			$scope.inquire_phone();
		}

		// CRM621 如果是由客戶首頁 include 的，則監聽來自於客戶首頁的廣播，以取得已載入的共用資料
		let crm610Data = {}
		if ($scope.fromCRM610) {
			$scope.$on('CRM610_DATA', function(event, data) {
				console.log('【聯絡方式】已收到【客戶首頁】的廣播...');
				crm610Data = data;
				init()
			});
		} else {
			init();
		}
	});