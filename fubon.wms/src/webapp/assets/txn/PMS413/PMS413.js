'use strict';
eSoafApp.controller('PMS413Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "PMS413Controller";
		
		$scope.mappingSet['comType'] = [];
        $scope.mappingSet['comType'].push({LABEL:'公用電腦', DATA:'1'}, {LABEL:'分行電腦', DATA:'2'});
        $scope.mappingSet['action'] = [];
        $scope.mappingSet['action'].push({LABEL:'請選擇', DATA:''}, {LABEL:'修改', DATA:'M'}, {LABEL:'刪除', DATA:'D'});
		
        $scope.inputVO = {};
        $scope.inputVO.comType = "1";
        $scope.init = function() {
			// 2017/8/2 add
			if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["PMS413"])
				$scope.CanEdit = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["PMS413"].FUNCTIONID["maintenance"];
			else
				$scope.CanEdit = false;
			//
			$scope.inputVO = {
				comType: $scope.inputVO.comType
	    	};
			
			$scope.sendRecv("PMS413", "querymember", "com.systex.jbranch.app.server.fps.pms413.PMS413InputVO",{},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.x)
							$scope.member = tota[0].body.x;
						else
							$scope.member = false;
					}
			});
			
			//組織連動
			// 2017/11/9 mantis:3938 直接輸入且不檢核
	        $scope.region = ['N', $scope.inputVO, "rc_id", "REGION_LIST", "op_id", "AREA_LIST", "br_id", "BRANCH_LIST", "ao_code", "AO_LIST", "nothingDO", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region).then(function(data) {
	        	$scope.inputVO.emp_id = angular.copy($scope.inputVO.nothingDO);
	        });
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.comTypeChange = function() {
			$scope.inquireInit();
			$scope.inputVO.ip = "";
			
			$scope.RegionController_setName($scope.region).then(function(data) {
				if($scope.inputVO.comType == '1')
					$scope.inputVO.emp_id = "";
				else
					$scope.inputVO.emp_id = angular.copy($scope.inputVO.nothingDO);
	        });
		};
	
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid){			
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇「電腦類型」');
        		return;
        	}
//			if($scope.rcList.length == 0){				
//				$scope.inputVO.emp_id = sysInfoService.getUserID();				
//			}
			$scope.sendRecv("PMS413", "queryData", "com.systex.jbranch.app.server.fps.pms413.PMS413InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.totalData =[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}							
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;							
							return;
						}						
			});
		};
		
        //新增資料
		$scope.btnNew = function(row){	
			var ct = $scope.inputVO.comType;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS413/PMS413_EDIT.html',
				className: 'PMS413_EDIT',
				controller:['$scope', function($scope){
					$scope.row = row;
					$scope.cType = ct;
				}]
			});
			dialog.closePromise.then(function(data) {					
				if(data.value === 'success'){
//					$scope.inputVO.comType = '';
					$scope.query();											
				}										
			});
		};
		
		$scope.execAct = function(row){			
			//執行修改			
			if(row.action == 'M'){				
				var dialog = ngDialog.open({
					template: 'assets/txn/PMS413/PMS413_EDIT.html',
					className: 'PMS413_EDIT',
					controller:['$scope', function($scope){						
						$scope.row = row;
						$scope.act = "M";
					}]
				});	
				dialog.closePromise.then(function(data) {					
						row.action = '';
						if(data.value === 'success') {
							$scope.inquireInit();
							$scope.query();											
						}
				});
			}
			//執行刪除
			if(row.action == 'D'){				
				$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
					$scope.sendRecv("PMS413", "delData", "com.systex.jbranch.app.server.fps.pms413.PMS413InputVO", {'seq': row.SEQ},
            				function(totas, isError) {
                            	if (isError) {
                            		$scope.showErrorMsg(totas[0].body.msgData);
                            	}
                            	if (totas.length > 0) {
                            		$scope.showSuccessMsg('刪除成功');
                            		$scope.inquireInit();
                            		$scope.query();
                            	};
            				}
            		);
				});
			}
			row.action = '';
		}
        
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS413", "export", "com.systex.jbranch.app.server.fps.pms413.PMS413OutputVO", $scope.outputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};
		
});
