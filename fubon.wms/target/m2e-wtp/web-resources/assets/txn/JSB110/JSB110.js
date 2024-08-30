/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('JSB110Controller',
	function($rootScope, $scope, $controller, getParameter, ngDialog, $compile) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "JSB110Controller";
		
		getParameter.XML(["JSB.INS_COM_NAME_01", "JSB.INS_TYPE", "JSB.INS_COM_PROD"], function(totas) {
			if (totas) {
				$scope.mappingSet['JSB.INS_COM_NAME_01'] = totas.data[totas.key.indexOf('JSB.INS_COM_NAME_01')];
				$scope.mappingSet['JSB.INS_TYPE'] = totas.data[totas.key.indexOf('JSB.INS_TYPE')];
				$scope.INS_COM_PROD = totas.data[totas.key.indexOf('JSB.INS_COM_PROD')];
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
			$scope.sendRecv("JSB110", "inquire", "com.systex.jbranch.app.server.fps.jsb110.JSB110InputVO", {},
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
			$scope.INS_TYPE_LIST = [];
			$scope.resultList = [];
			
			$scope.inquire();
		}
		
		$scope.changeCom = function(){
			$scope.inputVO.ins_type = undefined;
			
			angular.forEach($scope.INS_COM_PROD, function(row){
				if (row.DATA == $scope.inputVO.ins_com_id) {
					if (row.LABEL.length > 1) {
						$scope.INS_TYPE_LIST = [];
						$scope.INS_TYPE_LIST.push({DATA: '1', LABEL: '傳統型'});
						$scope.INS_TYPE_LIST.push({DATA: '2', LABEL: '投資型'});
						
					} else if (row.LABEL == 1) {
						$scope.INS_TYPE_LIST = [];
						$scope.INS_TYPE_LIST.push({DATA: '1', LABEL: '傳統型'});
						$scope.inputVO.ins_type = '1';
						
					} else {
						$scope.INS_TYPE_LIST = [];
						$scope.INS_TYPE_LIST.push({DATA: '2', LABEL: '投資型'});
						$scope.inputVO.ins_type = '2';
					}
				}
			});
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
			
			$scope.sendRecv("JSB110", "upload", "com.systex.jbranch.app.server.fps.jsb110.JSB110InputVO", $scope.inputVO, 
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
						template: 'assets/txn/JSB110/JSB110_RESULT.html',
						className: 'JSB110',
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
						ins_type    :row.INS_TYPE,
						data_date   :new Date(row.DATA_DATE)
				};				
			}
	    	$scope.sendRecv("JSB110", "export", "com.systex.jbranch.app.server.fps.jsb110.JSB110InputVO", vo, 
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
						ins_com_id  :row.INS_COM_ID,
						ins_type    :row.INS_TYPE,
						data_date   :new Date(row.DATA_DATE)
				};				
			}
			$scope.sendRecv("JSB110", "exportUP", "com.systex.jbranch.app.server.fps.jsb110.JSB110InputVO", vo, 
			function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(tota.body.msgData);
					return;
				}
			});
		}
		
		$scope.init();
});
