/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT701Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT701Controller";

		/*** date picker start ***/
		$scope.esbDateOptions = {
			maxDate: $scope.maxDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};

		//放入起訖日期
		$scope.esbDate = function() {
			$scope.esbDateOptions.maxDate = $scope.esbDate || $scope.maxDate;
		}
		/*** date picker end ***/

		//清除
		$scope.init = function(){
			$scope.esbDate();
		}
		$scope.init();

		//查詢前清除參數與結果
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();

		//查詢 esb log
		$scope.query = function(){
			$scope.sendRecv("SOT701", "queryEsbData", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.paramList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						
						return;
					}
				});
		}

		//show esb content
		$scope.download = function(row, status) {
			$scope.inputVO.status = status;
			$scope.inputVO.sno = row.ESB_SNO;
			$scope.inputVO.esbDate = $rootScope.toJsDate(row.CREATETIME);

			if(!$scope.inputVO.sno|| !$scope.inputVO.esbDate){
				$scope.showMsg("查詢檔案內容發生錯誤");
			}
			else {
				$scope.sendRecv("SOT701", "readEsbFile", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", $scope.inputVO,
					function (tota, isError) {
						if (!isError) {
							if (tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								return;
							}
							$scope.outputVO = tota[0].body.resultList[0];

							$scope.file = new Uint8Array($scope.outputVO.ESB_FILE);
							var file = new Blob([$scope.file], { type: 'text/xml' });
							saveAs(file, 'ESB_'+$scope.inputVO.sno+'_'+status);
							return;
						}
					}
				);
			}
		}
})
	.factory('GetCustAcctDataService', function(inputVO){
		var acctList = [];
		var acctVO = {
			acctNo: undefined,
			openDate: undefined,
			currency: undefined,
			avbBalance: undefined,
			acctBalance: undefined,
			bckBalance: undefined
		};

		angular.forEach(inputVO, function(row, index){
			var trustCurrType = row.trustCurrType; //台幣信託
			var acctNo = row.acctNo; //帳號
			var openDate = row.openDate; //開戶日期

			if(trustCurrType == 'N'){
				var act3 = acctNo.substring(3 ,5);
				var act2 = acctNo.substr(3, 2)
				if(act3.match(/102|104|120|168|210|211/g)){
					acctList.push(acctNo);
				}
				else if(act3 == '168' || act2.match(/17|18/g)){
					acctList.push(acctNo);
				}
			}
		});

		/* sort acctList
		 * 預設信託帳號邏輯
		 * 非當天帳戶，預設理專歸屬行的信託帳號
		 * 非當天帳戶，取信託帳號的最小分行(取信託帳號前三碼排序，取最小帳號)。
		 * 當日帳戶，理專歸屬行的信託帳號(信託帳號前三碼與理專歸屬行一致)。
		 * 當天帳戶，取信託帳號的最小分行(取信託帳號前三碼排序，取最小帳號)。
		 * 取得預設信託帳放在List中第一筆
		 */
		acctList.sort(function(a, b){
			if (a.openDate < SYSDATE) return -1;
			if (a.openDate > SYSDATE) return 1;
			if (b.openDate < SYSDATE) return -1;
			if (b.openDate > SYSDATE) return 1;
			if (a.acctNo == projInfoService.getBranchID() ) return -1;
			if (a.acctNo != projInfoService.getBranchID() ) return 1;
			if (b.acctNo == projInfoService.getBranchID() ) return -1;
			if (b.acctNo != projInfoService.getBranchID() ) return 1;
			if (a.acctNo < b.acctNo) return -1;
			if (a.acctNo > b.acctNo) return 1;
			return 0;
		});

	});