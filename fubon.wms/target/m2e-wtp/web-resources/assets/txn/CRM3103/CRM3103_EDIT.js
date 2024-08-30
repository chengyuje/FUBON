/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3103_EDITController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3103_EDITController";
        
		// combobox
    	getParameter.XML(["CRM.TRS_PRJ_ROT_STATUS_D"], function(totas) {
    		if (totas) {
    			$scope.mappingSet['CRM.TRS_PRJ_ROT_STATUS_D'] = totas.data[totas.key.indexOf('CRM.TRS_PRJ_ROT_STATUS_D')];
    		}
    	});
    	
		$scope.init = function() {
			debugger
			$scope.inputVO = {};
			//主畫面過來的資料
			$scope.inputVO = $scope.row;
			$scope.inputVO.REC_DATE = $scope.toJsDate($scope.inputVO.REC_DATE);
			$scope.inputVO.isHeadMgr = $scope.isHeadMgr;
			//總行編輯，取得理專AOCODE
			if($scope.inputVO.isHeadMgr && !($scope.inputVO.addCustYN && $scope.inputVO.addCustYN == "Y")) {
				$scope.sendRecv("CRM3103", "aoInquire", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO",{"emp_id": $scope.inputVO.EMP_ID},
		        		function(tota, isError) {
							if(!isError) {
								$scope.inputVO.aoCodeList = [];
								angular.forEach(tota[0].body.list, function(row) {
									$scope.inputVO.aoCodeList.push({'LABEL': row.AO_CODE_SHOW, 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
								});
						   }
				});
			}
			//總行新增客戶
			if($scope.inputVO.isHeadMgr && $scope.inputVO.addCustYN && $scope.inputVO.addCustYN == "Y") {
				$scope.sendRecv("CRM3103", "aoInquire", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO",{"bra_nbr": $scope.inputVO.BRANCH_NBR},
		        		function(tota, isError) {
							if(!isError) {
								debugger
								$scope.inputVO.empList = [];
								angular.forEach(tota[0].body.list, function(row) {
									$scope.inputVO.empList.push({'LABEL': row.EMP_ID+"-"+row.EMP_NAME+"("+row.AO_CODE_SHOW+")", 'DATA': row.AO_CODE, 'AO_CODE': row.AO_CODE, 'EMP_ID': row.EMP_ID});
								});
						   }
				});
			}
			
		}
		$scope.init();
		
		$scope.uploadFinshed = function(name, rname) {
			$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
		};
		
		// date picker
		$scope.rec_dateOptions = {
				maxDate : new Date(),
				minDate : null
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};

		$scope.timesys = new Date();
		$scope.endDateOptions = {
			maxDate : $scope.maxDate,
			minDate : $scope.timesys
		};
		// date picker end
		
		//儲存
        $scope.save = function(type) {
        	if(type == "11") {
        		//有權主管送出覆核時檢核錄音資料
        		if(($scope.checkNull($scope.inputVO.REC_SEQ) && !$scope.checkNull($scope.inputVO.REC_DATE)) ||
        				(!$scope.checkNull($scope.inputVO.REC_SEQ) && $scope.checkNull($scope.inputVO.REC_DATE))) {
        			$scope.showErrorMsg("錄音序號與錄音日期皆須輸入");
    				return;
        		}
        	}
        	
        	//總行編輯：若修改AOCODE，將AOCODE放AO_CODE_CHG(原CRM3103InputVO中ao_code參數也被使用)
        	if(type == "13" && $scope.inputVO.headEditType == "2") $scope.inputVO.AO_CODE_CHG = $scope.inputVO.AO_CODE;
        	
        	$scope.inputVO.saveType = type;
        	$scope.sendRecv("CRM3103", "save", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO",$scope.inputVO,
        		function(tota, isError) {
					if(!isError) {
						$scope.closeThisDialog('cancel');
				   }
			});
        }
        
        //總行編輯客戶時，修改AOCODE
        $scope.getEditAoData = function(){
        	var ao = $filter('filter')($scope.inputVO.aoCodeList, {'DATA': $scope.inputVO.AO_CODE});
        	if(ao != null && ao != undefined && ao.length > 0) {
        		$scope.inputVO.AO_TYPE_CHG = ao[0].TYPE;
        	}
        }
        
        //總行新增客戶時，選擇理專下拉選單
        $scope.getNewEmpData = function(){
        	debugger
        	var ao = $filter('filter')($scope.inputVO.empList, {'DATA': $scope.inputVO.NEW_EMP});
        	if(ao != null && ao != undefined && ao.length > 0) {
        		$scope.inputVO.NEW_EMP_ID = ao[0].EMP_ID;
            	$scope.inputVO.NEW_AO_CODE = ao[0].AO_CODE;
        	}
        }
        
		$scope.checkNull = function(data) {
			if (data == undefined || data == null || data == '') {
				return true;
			}
			return false;
		}
});