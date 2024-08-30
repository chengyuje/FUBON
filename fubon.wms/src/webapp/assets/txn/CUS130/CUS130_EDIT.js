'use strict';
eSoafApp.controller('CUS130_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS130_EDITController";
		
		// combobox
		getParameter.XML(["CUS.IVG_TYPE", "CUS.IVG_PLAN_TYPE", "CUS.FIELD_TYPE"], function(totas) {
			if (totas) {
				$scope.IVG_TYPE_ALL = totas.data[totas.key.indexOf('CUS.IVG_TYPE')];
				$scope.IVG_TYPE = totas.data[totas.key.indexOf('CUS.IVG_TYPE')];
				$scope.IVG_PLAN_TYPE = totas.data[totas.key.indexOf('CUS.IVG_PLAN_TYPE')];
				$scope.FIELD_TYPE = totas.data[totas.key.indexOf('CUS.FIELD_TYPE')];
			}
		});
		$scope.regionQuery = [];
		angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){
			$scope.regionQuery.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
		});
		$scope.areaQuery = [];
		angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){
			$scope.areaQuery.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
		});
		$scope.branchQuery = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.branchQuery.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		//
		
		// init
		$scope.nowDate = new Date();
		$scope.startDisable = $scope.EditType == 'U' && $scope.row.IVG_START_DATE && $scope.nowDate >= $scope.toJsDate($scope.row.IVG_START_DATE);
		$scope.endDisable = $scope.EditType == 'U' && $scope.row.IVG_END_DATE && $scope.nowDate >= $scope.toJsDate($scope.row.IVG_END_DATE);
		$scope.canAddRoleFlag = true;
		$scope.init = function() {
        	if($scope.row){
        		$scope.isUpdate = true
        	}
        	$scope.row = $scope.row || {};
        	$scope.inputVO = {
    			seq: $scope.row.IVG_PLAN_SEQ,
    			setType: "1",
    			ivgPlanName: $scope.row.IVG_PLAN_NAME,
    			ivgType: $scope.row.IVG_TYPE,
    			ivgPlanType: $scope.row.IVG_PLAN_TYPE,
    			ivgPlanDesc: $scope.row.IVG_PLAN_DESC,
    			startDisable: $scope.startDisable
            };
        	if($scope.row.IVG_START_DATE)
            	$scope.inputVO.ivgStartDate = $scope.toJsDate($scope.row.IVG_START_DATE);
            if($scope.row.IVG_END_DATE)
            	$scope.inputVO.ivgEndDate = $scope.toJsDate($scope.row.IVG_END_DATE);
            $scope.alreadyFile = $scope.row.DOC_NAME;
            if($scope.isUpdate) {
            	$scope.sendRecv("CUS130", "getFIELD", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'seq': $scope.row.IVG_PLAN_SEQ},
    					function(totas, isError) {
    	                	if (!isError) {
    	                		$scope.inputVO.listBase = totas[0].body.resultList;
    	                		// 2018/2/22 4246 修改要能新增限type 1
    	                		$scope.canAddRoleFlag = totas[0].body.canAddRoleFlag || ($scope.EditType == "C");
    	                		// 2017/7/31 remove copy displayonly
    	                		if($scope.EditType == "C")
    	                			$scope.inputVO.listBase = _.filter($scope.inputVO.listBase, function(o) { return !o.DISPLAYONLY; });
    	                		angular.forEach($scope.inputVO.listBase, function(row, index) {
    	    						row.ORDER_NO = index + 1;
    	    						row.set = [];
    	    						row.set.push({LABEL: "修改", DATA: "U"});
    	    						row.set.push({LABEL: "刪除", DATA: "D"});
    	    					});
    	                	};
    					}
    			);
            } else
            	$scope.inputVO.listBase = [];
            $scope.inputVO.totalList = [];
            $scope.outputVO = {};
            $scope.inputVO.memberList = [];
        	$scope.inputVO.selectList = [];
        	$scope.inputVO.empList = [];
        	$scope.inputVO.custList = [];
        	$scope.inputVO.type4_empList = [];
        	$scope.type4_outputVO = {};
		}
		$scope.init();
		
		// date picker
		$scope.ivgStartDateOptions = {
			minDate: $scope.nowDate
		};
		$scope.ivgEndDateOptions = {
			minDate: $scope.nowDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.ivgStartDateOptions.maxDate = $scope.inputVO.ivgEndDate;
			if($scope.inputVO.ivgStartDate < $scope.nowDate)
				$scope.ivgEndDateOptions.minDate = $scope.nowDate;
			else
				$scope.ivgEndDateOptions.minDate = $scope.inputVO.ivgStartDate || $scope.nowDate;
		};
		if($scope.EditType == "C") {
			if($scope.inputVO.ivgStartDate < $scope.nowDate) {
				$scope.inputVO.ivgStartDate = null;
				$scope.inputVO.ivgEndDate = null;
			}
		}
		$scope.limitDate();
		// date picker end
		
		// upload
        $scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        	$scope.alreadyFile = "";
        };
        $scope.uploadFinshed2 = function(name, rname) {
        	$scope.sendRecv("CUS130", "readEMP", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'custFileName':name,'custFileRealName':rname},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		// to know if our add or not
	                		$scope.oritype1 = false;
	                		$scope.oritype3 = false;
	                		$scope.inputVO.empList = totas[0].body.resultList;
	                	};
					}
			);
        };
        $scope.uploadFinshed3 = function(name, rname) {
        	$scope.sendRecv("CUS130", "readCUST", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'custFileName':name,'custFileRealName':rname},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.inputVO.custList = totas[0].body.resultList;
	                		// to know if our add or not
	                		$scope.oritype1 = false;
	                		$scope.oritype3 = true;
	                		if($scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('備註內容') == -1)
	                			$scope.inputVO.listBase.splice(0, 0, {'FIELD_LABEL': '備註內容', 'FIELD_TYPE': '1', 'DISPLAYONLY': 'Y'});
	                		if($scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('客戶姓名') == -1)
	                			$scope.inputVO.listBase.splice(0, 0, {'FIELD_LABEL': '客戶姓名', 'FIELD_TYPE': '1', 'DISPLAYONLY': 'Y'});
	                		if($scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('客戶ID') == -1)
	                			$scope.inputVO.listBase.splice(0, 0, {'FIELD_LABEL': '客戶ID', 'FIELD_TYPE': '1', 'DISPLAYONLY': 'Y'});
	                		angular.forEach($scope.inputVO.listBase, function(row, index) {
	    						row.ORDER_NO = index + 1;
	    					});
	                	};
					}
			);
        };
        
        $scope.typeChange = function(){
        	if ($scope.inputVO.setType == "1") {
        		$scope.inputVO.empList = [];
            	$scope.inputVO.custList = [];
            	$scope.inputVO.type4_empList = [];
            	$scope.type4_outputVO = {'data':$scope.inputVO.type4_empList};
            	// remove if type3 add
            	if($scope.oritype3) {
            		var delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('客戶ID');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('客戶姓名');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('備註內容');
                	$scope.inputVO.listBase.splice(delIndex,1);
            	}
        	}
        	else if ($scope.inputVO.setType == "2") {
        		$scope.inputVO.totalList = [];
        		$scope.outputVO = {'data':$scope.inputVO.totalList};
        		$scope.inputVO.memberList = [];
        		$scope.inputVO.selectList = [];
        		$scope.inputVO.custList = [];
        		$scope.inputVO.type4_empList = [];
            	$scope.type4_outputVO = {'data':$scope.inputVO.type4_empList};
        		// remove if type1 upload
            	if($scope.oritype1) {
            		var delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('員工編號');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('員工姓名');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('備註內容');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	
                	$scope.IVG_TYPE = angular.copy($scope.IVG_TYPE_ALL);
            	}
        		// remove if type3 add
            	if($scope.oritype3) {
            		var delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('客戶ID');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('客戶姓名');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('備註內容');
                	$scope.inputVO.listBase.splice(delIndex,1);
            	}
        	}
        	else if ($scope.inputVO.setType == "3") {
        		$scope.inputVO.totalList = [];
        		$scope.outputVO = {'data':$scope.inputVO.totalList};
        		$scope.inputVO.memberList = [];
        		$scope.inputVO.selectList = [];
        		$scope.inputVO.empList = [];
        		$scope.inputVO.type4_empList = [];
            	$scope.type4_outputVO = {'data':$scope.inputVO.type4_empList};
        		// remove if type1 upload
            	if($scope.oritype1) {
            		var delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('員工編號');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('員工姓名');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('備註內容');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	
                	$scope.IVG_TYPE = angular.copy($scope.IVG_TYPE_ALL);
            	}
        	}
        	else if ($scope.inputVO.setType == "4") {
        		$scope.inputVO.totalList = [];
        		$scope.outputVO = {'data':$scope.inputVO.totalList};
        		$scope.inputVO.memberList = [];
        		$scope.inputVO.selectList = [];
        		$scope.inputVO.empList = [];
        		$scope.inputVO.custList = [];
        		// remove if type1 upload
            	if($scope.oritype1) {
            		var delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('員工編號');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('員工姓名');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('備註內容');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	
                	$scope.IVG_TYPE = angular.copy($scope.IVG_TYPE_ALL);
            	}
        		// remove if type3 add
            	if($scope.oritype3) {
            		var delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('客戶ID');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('客戶姓名');
                	$scope.inputVO.listBase.splice(delIndex,1);
                	delIndex = $scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('備註內容');
                	$scope.inputVO.listBase.splice(delIndex,1);
            	}
        	}
        };
		
        $scope.downloadSimple = function() {
        	$scope.sendRecv("CUS130", "downloadSimple", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'setType': '1'},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		$scope.downloadSimple2 = function() {
			$scope.sendRecv("CUS130", "downloadSimple", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'setType': '2'},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		// report
		$scope.setReport = function () {
			$scope.connector('set','CUS130_REPORT1', []);
			$scope.report1();
		};
		$scope.report1 = function () {
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS130/CUS130_REPORT1.html',
				className: 'CUS130_REPORT1',
				showClose: false,
				closeByEscape: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'report1Next'){
					$scope.report2();
				}
			});
		};
		$scope.report2 = function () {
			var CanUploadFlag = $scope.EditType != 'U';
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS130/CUS130_REPORT2.html',
				className: 'CUS130_REPORT2',
				showClose: false,
				closeByEscape: false,
                controller: ['$scope', function($scope) {
                	$scope.CanUploadFlag = CanUploadFlag;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'report2Before'){
					$scope.report1();
				} else if(data.value === 'successful'){
					$scope.inputVO.totalList = $scope.connector('get','CUS130_REPORT2');
					$scope.outputVO = {'data':$scope.inputVO.totalList};
					$scope.inputVO.memberList = $scope.connector('get','CUS130_REPORT2_MEMBER');
					$scope.inputVO.selectList = $scope.connector('get','CUS130_REPORT2_UPLOAD');
					// to know if our add or not
					if($scope.inputVO.selectList.length > 0) {
						$scope.oritype1 = true;
						$scope.inputVO.ivgType = "1";
						$scope.IVG_TYPE = _.filter($scope.IVG_TYPE, function(o) { return o.DATA == '1'; });
						
						if($scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('備註內容') == -1)
                			$scope.inputVO.listBase.splice(0, 0, {'FIELD_LABEL': '備註內容', 'FIELD_TYPE': '1', 'DISPLAYONLY': 'Y'});
						if($scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('員工姓名') == -1)
                			$scope.inputVO.listBase.splice(0, 0, {'FIELD_LABEL': '員工姓名', 'FIELD_TYPE': '1', 'DISPLAYONLY': 'Y'});
						if($scope.inputVO.listBase.map(function(e) { return e.FIELD_LABEL; }).indexOf('員工編號') == -1)
							$scope.inputVO.listBase.splice(0, 0, {'FIELD_LABEL': '員工編號', 'FIELD_TYPE': '1', 'DISPLAYONLY': 'Y'});
                		angular.forEach($scope.inputVO.listBase, function(row, index){
    						row.ORDER_NO = index + 1;
    					});
					} else
						$scope.oritype1 = false;
            		$scope.oritype3 = false;
				}
			});
		};
		// 2017/8/15
		$scope.setEMP_ID = function () {
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS130/CUS130_SET_EMP.html',
				className: 'CUS130_SET_EMP',
				showClose: false,
				closeByEscape: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful') {
					$scope.inputVO.type4_empList = $scope.connector('get','CUS130_EMP_LIST');
					$scope.type4_outputVO = {'data':$scope.inputVO.type4_empList};
				}
			});
		};
		
		$scope.add = function () {
			if($scope.inputVO.listBase.length >= 20) {
				$scope.showErrorMsg('不得超過20筆');
        		return;
			}
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS130/CUS130_ADD_PLAN.html',
				className: 'CUS130_ADD_PLAN',
				showClose: false,
				closeByEscape: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					var plan = $scope.connector('get','CUS130_PLAN');
					$scope.inputVO.listBase.push({'FIELD_LABEL': plan.label, 'FIELD_TYPE': plan.type, 'DROPDOWN_CONTENT': plan.content});
					angular.forEach($scope.inputVO.listBase, function(row, index){
						row.ORDER_NO = index + 1;
						row.set = [];
						row.set.push({LABEL: "修改", DATA: "U"});
						row.set.push({LABEL: "刪除", DATA: "D"});
					});
				}
			});
		};
		
		$scope.edit = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS130/CUS130_ADD_PLAN.html',
				className: 'CUS130_ADD_PLAN',
				showClose: false,
				closeByEscape: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					var plan = $scope.connector('get','CUS130_PLAN');
					row.FIELD_LABEL = plan.label;
					row.FIELD_TYPE = plan.type;
					row.DROPDOWN_CONTENT = plan.content;
				}
			});
		};
		
		$scope.seeTemp = function () {
			var temp = [];
			if($scope.inputVO.setType == "1")
				temp = $scope.inputVO.selectList;
			else if($scope.inputVO.setType == "2")
				temp = $scope.inputVO.empList;
			else if($scope.inputVO.setType == "3")
				temp = $scope.inputVO.custList;
			var type = $scope.inputVO.setType;
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS130/CUS130_TEMP.html',
				className: 'CUS130_TEMP',
				showClose: false,
				closeByEscape: false,
                controller: ['$scope', function($scope) {
                	$scope.totalList = temp;
                	$scope.type = type;
                }]
			});
		};
		
		$scope.seeReport = function () {
			var seq = $scope.row.IVG_PLAN_SEQ;
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS130/CUS130_REPORT3.html',
				className: 'CUS130_REPORT3',
				showClose: false,
				closeByEscape: false,
                controller: ['$scope', function($scope) {
                	$scope.seq = seq;
                }]
			});
		};
		
		// 排序用
		$scope.sortableOptions = {
			stop: function(e, ui) {
				angular.forEach(ui.item.sortable.droptargetModel, function(row, index){
					row.ORDER_NO = index + 1;
				});
			},
			cancel: ".cus130_unsortable",
			items: "tr:not(.cus130_unsortable)",
			disabled: $scope.startDisable
		};
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
			    		$scope.inputVO.listBase.splice((row.ORDER_NO-1),1);
			    		angular.forEach($scope.inputVO.listBase, function(row, index){
							row.ORDER_NO = index + 1;
						});
		            });
				} else {
					$scope.edit(row);
					row.cmbAction = "";	
				}
			}
		};
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if($scope.EditType != 'U') {
				if(!$scope.inputVO.setType || ($scope.inputVO.setType == "1" && $scope.inputVO.totalList.length == 0)
						|| ($scope.inputVO.setType == "2" && $scope.inputVO.empList.length == 0)
						|| ($scope.inputVO.setType == "3" && $scope.inputVO.custList.length == 0)
						|| ($scope.inputVO.setType == "4" && $scope.inputVO.type4_empList.length == 0)) {
					$scope.showErrorMsg('欄位檢核錯誤:無設定回報對象');
	        		return;
				}
			}
			if($scope.inputVO.listBase.length == 0 || (($scope.inputVO.selectList.length > 0 || $scope.inputVO.setType == "3") && $scope.inputVO.listBase.length == 3)) {
				$scope.showErrorMsg('欄位檢核錯誤:無設定回報欄位');
        		return;
        	}
			if ($scope.EditType == "U") {
        		$scope.sendRecv("CUS130", "update", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
        	}
			else if ($scope.EditType == "C") {
				$scope.sendRecv("CUS130", "copy", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		if(totas[0].body.errorList && totas[0].body.errorList.length > 0)
    		            			$scope.showErrorMsg('ehl_01_cus130_001',[totas[0].body.errorList.toString()]);
    	                		if(totas[0].body.errorList2 && totas[0].body.errorList2.length > 0)
    		            			$scope.showErrorMsg('ehl_01_cus130_002',[totas[0].body.errorList2.toString()]);
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
			} else {
        		$scope.sendRecv("CUS130", "insert", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
								if (totas[0].body.errorMsg) // 執行過程發生錯誤
									$scope.showErrorMsg(totas[0].body.errorMsg);
								else {
									if(totas[0].body.errorList && totas[0].body.errorList.length > 0)
										$scope.showErrorMsg('ehl_01_cus130_001',[totas[0].body.errorList.toString()]);
									if(totas[0].body.errorList2 && totas[0].body.errorList2.length > 0)
										$scope.showErrorMsg('ehl_01_cus130_002',[totas[0].body.errorList2.toString()]);
									$scope.showSuccessMsg('ehl_01_common_004');
									$scope.closeThisDialog('successful');
								}
    	                	};
    					}
    			);
        	}
		};
		
});
