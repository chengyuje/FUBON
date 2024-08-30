/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS110Controller', function($rootScope, $confirm, $scope,$filter, $controller, socketService, ngDialog, projInfoService,sysInfoService,getParameter,$timeout,$http) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "INS110Controller";
	
	//xml參數初始化
	getParameter.XML(["INS.CONSENT","INS.IS_MAIN","INS.PAY_TYPE","CRM.CRM239_CONTRACT_STATUS","INS.FROM","CRM.CRM831_PAY_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['INS.CONSENT'] = totas.data[totas.key.indexOf('INS.CONSENT')];//保單健診同意書
			$scope.mappingSet['INS.IS_MAIN'] = totas.data[totas.key.indexOf('INS.IS_MAIN')];//主附約
			$scope.mappingSet['INS.PAY_TYPE'] = totas.data[totas.key.indexOf('INS.PAY_TYPE')];//繳別
			$scope.mappingSet['CRM.CRM831_PAY_TYPE'] = totas.data[totas.key.indexOf('CRM.CRM831_PAY_TYPE')];//繳別
			$scope.mappingSet['CRM.CRM239_CONTRACT_STATUS'] = totas.data[totas.key.indexOf('CRM.CRM239_CONTRACT_STATUS')];//保單狀態
			$scope.mappingSet['INS.FROM'] = totas.data[totas.key.indexOf('INS.FROM')];//資料來源
		}
	});
	
	/**initialize**/
	$scope.init = function() {
		$scope.connector('set', "INS_PARGE", undefined);
    	$scope.connector('set', "INS100_VIEW_DATA_BACK", undefined);
    	$scope.connector('set', "INS_PARGE_BACK_INS100", undefined);
		if($scope.connector('get', "INS_custID")){
			var from_INS100_Data = $scope.connector('get', "INS_custID");
			$scope.to_INS140 = $scope.connector('get', "INS_custID");
			$scope.inputVO = {
					INSURED_ID:from_INS100_Data.CUST_ID,
					INSURED_NAME:from_INS100_Data.CUST_NAME,
					GENDER:from_INS100_Data.GENDER,		//性別
					BIRTH_DATE:from_INS100_Data.BIRTH_DATE,		//生日
					REPORT_DATE:from_INS100_Data.REPORT_DATE,	//保單健診同意書歷史
					TOTAL_INS_YEAR_FEE:0,
			}
			
			//保單健診同意書狀態
			if($scope.inputVO.REPORT_DATE == null){
				$scope.CONSENT = 'NONE';
			}else{
				var now = new Date();
				var RD = Date.parse($scope.inputVO.REPORT_DATE);
				var CONSENT_Date = (now-RD)/(1000 * 60 * 60 * 24);
				if(CONSENT_Date <= 7){
					$scope.CONSENT = '<=7';//已簽屬
				}else if(CONSENT_Date > 7){
					$scope.CONSENT = '>7';//已過期
				}
			}
			
			$scope.sendRecv("INS110", "queryData", "com.systex.jbranch.app.server.fps.ins110.INS110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							var totalInsYearFee = 0;
							$scope.queryData = tota[0].body.DataList;
							angular.forEach($scope.queryData, function(row, index) {
								totalInsYearFee += row['INSYEARFEE_YEAR'];
							})
							$scope.inputVO.TOTAL_INS_YEAR_FEE = totalInsYearFee;
						}
			});
		}
	}
	$scope.init();//initialize variable
	
	//前次健診紀錄
	$scope.Print_LastTime = function(){
		$scope.sendRecv("INS110", "PrintLastTime", "com.systex.jbranch.app.server.fps.ins110.INS110InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					debugger
					if(tota[0].body.DataList) {
						if(tota[0].body.DataList.length == 0) {
							$scope.showSuccessMsg("無前次健診紀錄");
						}
					}
				}
		});
	}
	
	//刪除行外保單
	$scope.DeleteINS111Data = function(row){
		if(row.IS_MAIN == 'Y'){
			var textMsg = '刪除主約時，主約底下的附約也會跟著刪除';
			$scope.DELETEInputVO = {
					IS_MAIN:row.IS_MAIN,
					INSSEQ:row.INSSEQ
			}
			$scope.toDelete(textMsg)
		}else if(row.IS_MAIN == 'N'){
			var textMsg = '確定要刪除附約';
			$scope.DELETEInputVO = {
					IS_MAIN:row.IS_MAIN,
					KEYNO:row.KEYNO
			}
			$scope.toDelete(textMsg);
		}
	}
	
	//刪除行外保單跳出視窗
	$scope.toDelete = function(textMsg){
		$confirm({text:textMsg},{size:'sm'}).then(function(){
			$scope.sendRecv("INS110", "deleteOutBuyDtl", "com.systex.jbranch.app.server.fps.ins110.INS110InputVO", $scope.DELETEInputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota.length > 0){
							$scope.showSuccessMsg("ehl_01_common_003");
							$scope.init();
						}
					}
			});
		});
	}
	
	//回保單健診入口
    $scope.return_INS100  = function() {
    	$scope.connector('set', "INS_PARGE", "INS100");
    	$scope.connector('set', "INS100_VIEW_DATA_BACK", $scope.connector('get', "INS100_VIEW_DATA"));
    	$scope.connector('set', "INS_PARGE_BACK_INS100", "INS110-100");
		$rootScope.menuItemInfo.url = "assets/txn/INS100/INS100.html";
    };

    //新增行外保單
	$scope.To_INS111 = function(type,data){
		//保單健診同意書"已簽署"才可進入新增行外保單
		if($scope.CONSENT == '<=7'){
			var INSURED_ID=$scope.inputVO.INSURED_ID;
			var dialog = ngDialog.open({
				template: 'assets/txn/INS111/INS111.html',
				className: 'INS111',
				controller:['$scope',function($scope){
					if(data == null && type == 'new'){
						if(INSURED_ID != undefined){
							$scope.INSURED_ID = INSURED_ID.toUpperCase();
							$scope.type = type
						}
					}else if(type == 'update'){
						$scope.type = type;
						$scope.INSSEQ = data.INSSEQ;
					}
					
				}]
			});
			dialog.closePromise.then(function(data){
				if(data.value === 'successful'){
					$scope.init();
				}
			});
		}else{
			$scope.showMsg('請回到【保單健診入口】上傳保單健診同意書');
		}
	}

	//轉跳家庭財務安全問卷
	$scope.To_INS130 = function(){
		$scope.connector('set', "INS_PARGE", "INS130");
		$rootScope.menuItemInfo.url = "assets/txn/INS100/INS100.html";
	}
	
	//轉跳保險規劃入口
	$scope.To_INS200 = function(){
		$scope.connector('set', "FROM_INS110", $scope.inputVO.INSURED_ID);
		$rootScope.menuItemInfo.url = "assets/txn/INS200/INS200.html";
	}
	
	//轉跳列印報告書
	$scope.To_INS140 = function(){
		$scope.connector('set', "INS_PARGE", "INS140");	// 頁面跳轉
		$scope.connector('set', "TO_INS140", $scope.to_INS140);	// 資料連結進INS140
		$rootScope.menuItemInfo.url = "assets/txn/INS100/INS100.html";
	}			
});


