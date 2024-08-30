/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD281_UPLOADController',
	function($scope, $controller, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD281_UPLOADController";
		
		// combobox
		getParameter.XML(["PRD.FCI_CURRENCY", "PRD.FCI_DOC_TYPE"], function(totas) {
			if (totas) {
				$scope.FCI_CURRENCY = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY')];
				$scope.FCI_DOC_TYPE = totas.data[totas.key.indexOf('PRD.FCI_DOC_TYPE')];
			}
		});
		
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.DOC_TYPE = "";
			$scope.inputVO.CURR_ID = "";
			$scope.inputVO.fileName = "";
			$scope.inputVO.realfileName = "";
			$scope.inputVO.currDisalbed = false;
		}
		$scope.init();		
		        
		$scope.uploadFinshed = function(name, rname) {
			$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
		};
		
		//取得文件檔案資料
		$scope.getUploadList = function() {
			$scope.resultList = [];
			$scope.outputVO = {};
			
			$scope.sendRecv("PRD281", "getUploadList", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.resultList = tota[0].body.resultList;
						$scope.ouputVO = tota[0].body;
					}
			});
		}
		$scope.getUploadList();		
		
		//儲存文件檔案資料
		$scope.saveFile = function() {
			if($scope.inputVO.DOC_TYPE == ""){
	    		$scope.showErrorMsg("請選擇文件種類");
        		return;
        	}
			
			if($scope.inputVO.DOC_TYPE == "1" && $scope.inputVO.CURR_ID == "") {
				$scope.showErrorMsg("請選擇幣別");
        		return;
			}
			
			if($scope.inputVO.fileName == "") {
				$scope.showErrorMsg("請選擇上傳檔案");
        		return;
			}
						
			$scope.inputVO.saveType = "4";
			$scope.sendRecv("PRD281", "save", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
                   		
                   		$scope.init();
                   		$scope.getUploadList();
					}
			});
		};
		
		//檢視已上傳文件
        $scope.viewFile = function(row) {
			$scope.sendRecv("PRD281", "viewFile", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", {'SEQ_NO': row.SEQ_NO},
				function(totas, isError) {
		        	if (!isError) {
						return;
					}
				}
			);
		};
		
		//文件種類變更時
		$scope.docTypeChanged = function() {
			debugger
			$scope.inputVO.currDisalbed = false;
			if($scope.inputVO.DOC_TYPE != "1") {
				$scope.inputVO.CURR_ID = "";
				$scope.inputVO.currDisalbed = true;
			}
		}
});