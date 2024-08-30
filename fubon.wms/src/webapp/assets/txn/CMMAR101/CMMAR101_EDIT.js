/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMAR101_EditController',
    function($rootScope, $scope, $controller, socketService, alerts, projInfoService, $q) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMAR101_EditController";
        
        $scope.mappingSet['cmbBranch'] = [];
        angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['cmbBranch'].push({LABEL: row.BranchName, DATA: row.BranchNbr});
		});
        
        $scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		$scope.limitDate = function() {
			$scope.startMaxDate = $scope.inputVO.auEDate || $scope.maxDate;
			$scope.endMinDate = $scope.inputVO.auSDate || $scope.minDate;
		};
        
        $scope.getEmpList = function() {
        	var deferred = $q.defer();
			var vo;
			if($scope.inputVO.cmbBranch)
				vo = {'cmbBranch': $scope.inputVO.cmbBranch};
			else
				vo = {'branchList': projInfoService.getAvailBranch().map(function(e) { return e.BranchNbr; })};
			$scope.sendRecv("CMMAR101", "getEmpData", "com.systex.jbranch.app.server.fps.cmmar101.CMMAR101EMPVO", vo,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsgInDialog(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		deferred.resolve(totas[0].body.empLS);
                    	};
    				}
    		);
			return deferred.promise;
		};
		
		$scope.getINFODefault = function() {
        	var deferred = $q.defer();
        	if($scope.row) {
        		$scope.inputVO.info = $scope.row.INFO_NO;
        		$scope.sendRecv("CMMAR101", "getINFODefault", "com.systex.jbranch.app.server.fps.cmmar101.CMMAR101AUVO", {'info': $scope.row.INFO_NO},
        				function(totas, isError) {
                        	if (isError) {
                        		$scope.showErrorMsgInDialog(totas[0].body.msgData);
                        	}
                        	if (totas.length > 0) {
                        		var ans = totas[0].body.resultList[0];
                        		$scope.inputVO.tipAUInfoTitle = ans.INFO_TITLE;
                        		$scope.inputVO.tipAUInfoDesc = ans.INFO_DESC;
                        		$scope.inputVO.tipRealAttachName = ans.ATTACH_NAME;
                        		if (ans.INFO_SDATE)
                        			$scope.inputVO.auSDate = $scope.toJsDate(ans.INFO_SDATE);
                        		if (ans.INFO_EDATE)
                        			$scope.inputVO.auEDate = $scope.toJsDate(ans.INFO_EDATE);
                        		$scope.startMaxDate = $scope.inputVO.auEDate || $scope.maxDate;
                        		$scope.endMinDate = $scope.inputVO.auSDate || $scope.minDate;
                        		deferred.resolve(totas[0].body.empLS);
                        	};
        				}
        		);
        	} else {
        		$scope.startMaxDate = $scope.maxDate;
        		$scope.endMinDate = $scope.minDate;
        	}
			return deferred.promise;
		};
        
        $scope.init = function(){
        	$scope.inputVO = {
        		info: '',
        		tipAUInfoTitle: '',
        		tipAUInfoDesc: '',
        		tipAttachName: '',
        		auSDate: undefined,
        		auEDate: undefined,
        		territoryID: projInfoService.getBranchID(),
        		empIDLS: []
        	};
        	$q.all([
        	    $scope.getEmpList().then(function(data) {
        	    	$scope.adgEmpData = data;
                	// slim-scroll
                	$rootScope.escroll();
                	$scope.mappingSet['cmbEmp'] = [];
                	angular.forEach(data, function(row, index, objs){
                		$scope.mappingSet['cmbEmp'].push({LABEL: row.EMP_NAME , DATA: row.EMP_ID});
            		});
        	    }),
        	    $scope.getINFODefault().then(function(data) {
        	    	$scope.EditEmpData = data;
        	    })
        	]).then(function() {
        		angular.forEach($scope.adgEmpData, function(row){
        			angular.forEach($scope.EditEmpData, function(row2){
                		if(row.EMP_ID == row2.INFO_EMP_ID)
                			row.SELECTED = true;
            		});
        		});
        	});
        };
        $scope.init();
        
        $scope.cmbgetEmpList = function() {
        	$scope.getEmpList().then(function(data) {
        		$scope.adgEmpData = data;
        		$scope.adgEmpDataAll = data;
            	// slim-scroll
            	$rootScope.escroll();
        		$scope.mappingSet['cmbEmp'] = [];
            	angular.forEach(data, function(row, index, objs){
            		$scope.mappingSet['cmbEmp'].push({LABEL: row.EMP_NAME , DATA: row.EMP_ID});
        		});
        	});
        };
        
        $scope.cmbadgEmpData = function() {
        	$scope.adgEmpData = [];
        	$.extend($scope.adgEmpData, $scope.adgEmpDataAll);
        	$scope.adgEmpData = $.map($scope.adgEmpData, function(e) {
        		return e.EMP_ID == $scope.inputVO.cmbEmp ? e : null;
        	});
        };
        
        $scope.selectAll = function(data) {
        	if(data) {
        		angular.forEach($scope.adgEmpData, function(row, index, objs){
        			row.SELECTED = true;
        		});
        	} else {
        		angular.forEach($scope.adgEmpData, function(row, index, objs){
        			row.SELECTED = false;
        		});
        	}
        };
        
        // upload
        $scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.tipAttachName = name;
        	$scope.inputVO.tipRealAttachName = rname;
        };
    	
        $scope.save = function() {
        	if($scope.parameterTypeEditForm.$invalid) {
        		console.log('form=' + JSON.stringify($scope.parameterTypeEditForm));
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	var temp = [];
        	angular.forEach($scope.adgEmpData, function(row, index, objs){
				if(row.SELECTED)
					temp.push(row.EMP_ID);
			});
        	$scope.inputVO.empIDLS = temp;
        	$scope.sendRecv("CMMAR101", "saveINFO", "com.systex.jbranch.app.server.fps.cmmar101.CMMAR101AUVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsgInDialog(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.showMsg('儲存成功');
                    		$scope.closeThisDialog('successful');
                    	};
    				}
    		);
        };
        
    }
);