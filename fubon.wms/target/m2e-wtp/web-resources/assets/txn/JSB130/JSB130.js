/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('JSB130Controller',
	function($rootScope, $scope, $controller, getParameter, ngDialog, $compile) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "JSB130Controller";
		
		getParameter.XML(["JSB.INS_COM_NAME_03"], function(totas) {
			if (totas) {
				$scope.mappingSet['JSB.INS_COM_NAME_03'] = totas.data[totas.key.indexOf('JSB.INS_COM_NAME_03')];
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
			$scope.sendRecv("JSB130", "inquire", "com.systex.jbranch.app.server.fps.jsb130.JSB130InputVO", {},
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
			
			// 『對帳單年月』可以下拉近3個月
			$scope.sendRecv("JSB130", "getYearMonth", "com.systex.jbranch.app.server.fps.jsb130.JSB130InputVO", {},
			function(tota, isError) {
				if (!isError) {
					$scope.mappingSet['YEARMONTH'] = [];
					angular.forEach(tota[0].body.resultList, function(row){
						$scope.mappingSet['YEARMONTH'].push({DATA: row.YEAR_MONTH, LABEL:  row.YEAR_MONTH});   			
					});
					// 預設值本月
					$scope.inputVO.data_month = tota[0].body.thisMonth;
				}
			});
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
			
			$scope.sendRecv("JSB130", "upload", "com.systex.jbranch.app.server.fps.jsb130.JSB130InputVO", $scope.inputVO, 
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
						template: 'assets/txn/JSB130/JSB130_RESULT.html',
						className: 'JSB130',
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
	    	$scope.sendRecv("JSB130", "export", "com.systex.jbranch.app.server.fps.jsb130.JSB130InputVO", vo, 
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
						ins_com_id : row.INS_COM_ID,
						data_date  : new Date(row.DATA_DATE),
				};				
			}
			$scope.sendRecv("JSB130", "exportUP", "com.systex.jbranch.app.server.fps.jsb130.JSB130InputVO", vo, 
			function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(tota.body.msgData);
					return;
				}
			});
		}
		
		$scope.init();
});
