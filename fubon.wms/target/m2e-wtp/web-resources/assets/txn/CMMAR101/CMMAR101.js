/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CMMAR101Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CMMAR101Controller";
		
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		$scope.limitDate = function() {
			$scope.startMaxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.endMinDate = $scope.inputVO.sDate || $scope.minDate;
		};
		
		$scope.init = function(){
			$scope.inputVO = {
					tipInfoTitle: '',
					tipCreator: '',
					sDate: '',
					eDate: ''
        	};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		$scope.inquire = function(){
			$scope.sendRecv("CMMAR101", "inquire", "com.systex.jbranch.app.server.fps.cmmar101.CMMAR101DataVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_001");
	                			return;
	                		}
							$scope.pagingList($scope.paramList, tota[0].body.resultList);
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.set = [];
								if(row.INFO_TYPE == "A") {
									row.actionIF = false;
								} else {
									row.actionIF = true;
									row.set.push({LABEL: "修改", DATA: "U"});
								}
							});
							return;
						}
			});
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "U")
					$scope.edit(row);
				row.cmbAction = "";
			}
		};
		
		$scope.edit = function (row) {
			console.log('edit data='+JSON.stringify(row));
			var dialog = ngDialog.open({
				template: 'assets/txn/CMMAR101/CMMAR101_EDIT.html',
				className: 'CMMAR101',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = row;
				}]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		$scope.downloadByteFile = function(row){
			$scope.sendRecv("CMMAR101", "downloadFile", "com.systex.jbranch.app.server.fps.cmmar101.CMMAR101AUVO", {tipAttachName:row.ATTACH_NAME,tipAttach:row.ATTACH},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		$scope.empDetail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CMMAR101/CMMAR101_EMP.html',
				className: 'CMMAR101',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = row;
				}]
			});
		};
		
});
