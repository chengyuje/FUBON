/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3A1_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3A1_EDITController";
		
		$scope.inputVO = {};
		
		//錄音日期參數
		$scope.rec_dateOptions = {
				maxDate : new Date(),
				minDate : null
		};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		
		$scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        };
        
        $scope.init = function() {
        	
        	$scope.inputVO = {
        			seq : $scope.row.SEQ,
        			recDate : null,
        			recSeq : $scope.row.REC_SEQ,
        			fileRealName : $scope.row.CMDT_FILE_NAME
        	};
        	if($scope.inputVO.fileRealName) {
        		$scope.value = $scope.row.SHOW_FILE_NAME;
        	}
        	
        	if($scope.row.REC_DATE) {
        		$scope.inputVO.recDate = new Date($scope.row.REC_DATE).getTime();
        	}
        }
        $scope.init();
        
        $scope.save = function() {
        	$scope.sendRecv("CRM3A1", "edit", "com.systex.jbranch.app.server.fps.crm3a1.CRM3A1InputVO", $scope.inputVO,
					function(tota, isError) {
		        		if (isError) {
		            		$scope.showErrorMsg(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		$scope.showSuccessMsg('ehl_01_common_004');
		            		$scope.closeThisDialog('successful');
		            	};
			});
        };
        
        $scope.clear = function() {
        	$scope.inputVO.recDate = null;
        	$scope.inputVO.recSeq = null;
        	$scope.inputVO.fileName = null;
        	$scope.inputVO.fileRealName = null;
        	$scope.value = null;
        }
		
		
});