/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD220_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD220_EDITController";
		
		// combobox
		getParameter.XML(["SYS.DOC_TYPE"], function(totas) {
			if (totas) {
				$scope.DOC_TYPE = [];
				angular.forEach(totas.data[totas.key.indexOf('SYS.DOC_TYPE')], function(row) {
					if(row.DATA == '02' || row.DATA == '03')
						$scope.DOC_TYPE.push(row);
				});
			}
		});
		
		// date picker
		$scope.doc_sDateOptions = {};
		$scope.doc_eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.doc_sDateOptions.maxDate = $scope.inputVO.doc_eDate;
			$scope.doc_eDateOptions.minDate = $scope.inputVO.doc_sDate;
			if($scope.inputVO.doc_eDate < $scope.doc_eDateOptions.minDate)
				$scope.inputVO.doc_eDate = "";
		};
		// date picker end
		
		$scope.checkID = function() {
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("PRD220", "checkID", "com.systex.jbranch.app.server.fps.prd220.PRD220InputVO", {'prd_id':$scope.inputVO.prd_id,'ptype': $scope.ptype},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.name = tota[0].body.name;
								$scope.canEdit = tota[0].body.canEdit;
								return;
							}
				});
			}
		};
		
		$scope.getDOC_ID = function() {
			$scope.sendRecv("PRD220", "getDOC_ID", "com.systex.jbranch.app.server.fps.prd220.PRD220InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.doc_id = tota[0].body.doc_id;
							return;
						}
			});
		};
		
		$scope.init = function() {
			if($scope.row) {
        		$scope.isUpdate = true;
        		$scope.inputVO = {
        			ptype: $scope.ptype,
        			prd_id: $scope.row.PRD_ID,
        			doc_id: $scope.row.DOC_ID,
        			doc_name: $scope.row.DOC_NAME,
        			type: $scope.row.DOC_FILE_TYPE == 'D' ? 1 : 2,
        			realfileName: $scope.row.FILE_NAME,
        			web: $scope.row.DOC_URL,
        			doc_type: $scope.row.DOC_TYPE,
        			shared: $scope.row.SHARED
        		};
        		if($scope.row.DOC_START_DATE)
                	$scope.inputVO.doc_sDate = $scope.toJsDate($scope.row.DOC_START_DATE);
                if($scope.row.DOC_DUE_DATE)
                	$scope.inputVO.doc_eDate = $scope.toJsDate($scope.row.DOC_DUE_DATE);
        		if($scope.row.PRD_ID)
        			$scope.checkID();
        	} else {
        		$scope.getDOC_ID();
        		$scope.inputVO = {
        			ptype: $scope.ptype,
        			doc_type: '02',
        			shared: 'N',
        			realfileName: ''
        		};
        	}
		};
		$scope.init();
		
		$scope.ClearID = function() {
			if($scope.inputVO.shared == 'Y')
				$scope.inputVO.prd_id = "";
		};
		
		$scope.uploadFinshed = function(name, rname) {
			$scope.inputVO.fileName = name;
			$scope.inputVO.realfileName = rname;
        };
        $scope.ClearUpload = function() {
        	if($scope.inputVO.type == 1)
        		$scope.inputVO.web = "";
        	else if($scope.inputVO.type == 2) {
        		$scope.inputVO.fileName = "";
    			$scope.inputVO.realfileName = "";
        	}
        };
        $scope.ClearUpload2 = function() {
        	$scope.inputVO.fileName = "";
			$scope.inputVO.realfileName = "";
        };
		
        $scope.save = function() {
        	if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if(($scope.inputVO.type == 1 && !$scope.inputVO.realfileName) || ($scope.inputVO.type == 2 && !$scope.inputVO.web)) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if(!$scope.canEdit && $scope.inputVO.shared == 'N') {
				$scope.showErrorMsg('ehl_01_common_026');
        		return;
			}
			if($scope.isUpdate) {
				$scope.sendRecv("PRD220", "editData", "com.systex.jbranch.app.server.fps.prd220.PRD220InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showSuccessMsg('ehl_01_common_004');
		                		$scope.closeThisDialog('successful');
		                	};
				});
			} else {
				$scope.sendRecv("PRD220", "saveData", "com.systex.jbranch.app.server.fps.prd220.PRD220InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showSuccessMsg('ehl_01_common_004');
		                		$scope.closeThisDialog('successful');
		                	};
				});
			}
        };
		
		
		
		
});