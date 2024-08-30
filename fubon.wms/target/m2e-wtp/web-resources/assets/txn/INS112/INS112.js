/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS112Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS112Controller";
		
		// filter
		getParameter.XML(["INS.IS_SALE", "INS.TYPE_CLASS"], function(totas) {
			if (totas) {
				$scope.mappingSet['INS.IS_SALE'] = totas.data[totas.key.indexOf('INS.IS_SALE')];
				$scope.mappingSet['INS.TYPE_CLASS'] = totas.data[totas.key.indexOf('INS.TYPE_CLASS')];
			}
		});

		
		$scope.init = function(){
			$scope.inputVO = {
					COM_ID:$scope.COM_ID,
					PRD_ID:$scope.PRD_ID,
					CURR_CD:$scope.CURR_CD,
					PRD_NAME:$scope.PRD_NAME,
					IS_SALE:'',
					QID:'',
					IS_MAIN_TYPE:$scope.IS_MAIN_TYPE
			}
			$scope.COMList = []
			$scope.sendRecv("INS810","queryCOM_ID","com.systex.jbranch.app.server.fps.ins810.INS810InputVO",
					$scope.inputVO,function(tota,isError){
				$scope.COMList = tota[0].body.COMList;
				if($scope.inputVO.COM_ID != ''){
					$scope.queryData();
				}
			});
			
		}
		$scope.init();
		
		$scope.checkData = function(){
			if($scope.inputVO.COM_ID != undefined && $scope.inputVO.COM_ID != ''){
				return true;
			}else{
				return false;
			}
		}
		
		$scope.queryData = function(){
			$scope.queryDataList = [];
			if($scope.checkData()){
				if($scope.inputVO.PRD_NAME == '' && $scope.PRD_ID ==''){
					$scope.inputVO.PRD_ID = '';
				}
				$scope.sendRecv("INS112","queryData","com.systex.jbranch.app.server.fps.ins112.INS112InputVO",
						$scope.inputVO,function(tota,isError){
					$scope.queryDataList = tota[0].body.queryList;
				});
			}else{
				$scope.showErrorMsg('ehl_01_INS112_001');
			}
		}
		
		$scope.PROD_NAME = function(){
			if($scope.inputVO.PRD_NAME == ''){
				$scope.inputVO.PRD_ID = '';
			}
		}

		//資料返回前頁
		$scope.returnSelectINSPRDData = function(row){
			if(typeof(webViewParamObj) != 'undefined'){
				webViewParamObj.formatWebViewResParam(row);
//				alert('準備調用')
				try {
					window.webkit.messageHandlers.resultCompleted.postMessage('resultCompleted');
//				    nativeApp.resultCompleted();
//				    alert('執行了一次')
				}
				catch(err) {
					alert(err.message);
				}
			}	

			if(typeof($scope.closeThisDialog) != 'undefined'){
				$scope.closeThisDialog(row);		
			}
		}

	}
);