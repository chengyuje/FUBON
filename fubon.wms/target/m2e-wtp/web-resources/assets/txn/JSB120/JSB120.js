/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('JSB120Controller',
	function($rootScope, $scope, $controller, getParameter, ngDialog, $compile) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "JSB120Controller";
		
		getParameter.XML(["JSB.INS_COM_NAME_02"], function(totas) {
			if (totas) {
				$scope.mappingSet['JSB.INS_COM_NAME_02'] = totas.data[totas.key.indexOf('JSB.INS_COM_NAME_02')];
			}
		});
		
		// date picker
		// 參考日期不能 > 系統日期
		$scope.data_dateOptions = {
				maxDate: new Date()
		};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.inquire = function(){
			$scope.sendRecv("JSB120", "inquire", "com.systex.jbranch.app.server.fps.jsb120.JSB120InputVO", {},
			function(tota, isError) {
				if (!isError) {
					$scope.resultList = [];
					if (tota[0].body.resultList.length > 0) {
						$scope.resultList = tota[0].body.resultList;
					}
				}
			});
		}
				
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.resultList = [];
			
			$scope.inquire();
		}
		
		$scope.uploadFinshed = function(name, rname) {
			$scope.inputVO.fileName = name;
			$scope.inputVO.fileRealName = rname;
		}
		
		$scope.upload = function(){
			if ($scope.parameterTypeEditForm.$invalid || $scope.inputVO.fileName == undefined) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			
			$scope.sendRecv("JSB120", "upload", "com.systex.jbranch.app.server.fps.jsb120.JSB120InputVO", $scope.inputVO, 
			function(tota, isError) {
				if (isError) {
//					$scope.showErrorMsg(tota.body.msgData);
					return;
				} else {
					$scope.list = [];
					if (tota[0].body.resultList.length > 0) {
						$scope.list = tota[0].body.resultList;
					}
					var inputVO = $scope.inputVO;
					var resultList = $scope.list;
					var dialog = ngDialog.open({
						template: 'assets/txn/JSB120/JSB120_RESULT.html',
						className: 'JSB120',
						showClose: false,
						 controller: ['$scope', function($scope) {
							 $scope.inputVO = inputVO;
							 $scope.resultList = resultList;
			             }]
					});
					dialog.closePromise.then(function (data) {
						if(data.value === 'successful' || data.value === 'cancel'){
							// 清空『*資料檔案』
							angular.element($("#csvUpload")).remove();
							angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='uploadFinshed(name,rname)' model='inputVO.realfileName' width='180px' required></e-upload>");
							var content = angular.element($("#csvBox"));
							var scope = content.scope();
							$compile(content.contents())(scope);
							
							$scope.init();
						}
					});
				}
			});
		}
		
		$scope.export = function(row){
			if (row == 'ALL') {
				var vo = {export_type : "OK"};
			} else {
				var vo = {
						export_type	:"OK",
						ins_com_id  :row.INS_COM_ID,
						data_date   :new Date(row.DATA_DATE)
				};				
			}
	    	$scope.sendRecv("JSB120", "export", "com.systex.jbranch.app.server.fps.jsb120.JSB120InputVO", vo, 
			function(tota, isError) {
	    		if (isError) {
	    			$scope.showErrorMsg(tota.body.msgData);
					return;
				}
			});
	    }
		
		$scope.exportUpdate = function(row){
			if (row == 'ALL') {
				var vo = {};
			} else {
				var vo = {
						ins_com_id  	:row.INS_COM_ID,
						data_date   	:new Date(row.DATA_DATE),
						acc_value_date	:new Date(row.ACC_VALUE_DATE)
				};				
			}
			$scope.sendRecv("JSB120", "exportUP", "com.systex.jbranch.app.server.fps.jsb120.JSB120InputVO", vo, 
			function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(tota.body.msgData);
					return;
				}
			});
		}
		
		$scope.init();
});
