/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMORG101Controller',
    function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMORG101Controller";
        
        $scope.showMenu2 = true;
        $scope.showMenu3 = true;
        $scope.showMenu4 = true;
        $scope.showMenu5 = true;
        $scope.showMenu6 = true;
        $scope.open = function($event, index) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope['opened'+index] = true;
        };
        $scope.getInit = function(){
        	$scope.sendRecv("CMORG101", "getInit", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101DataVO", {},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.mappingSet['orgsDesc'] = [];
	                		angular.forEach(totas[0].body.orgTypeLS, function(row, index, objs){
	                			$scope.mappingSet['orgsDesc'].push({LABEL: row.ORG_NAME, DATA: row.ORG_SEQ});
	                		});
	                		$scope.mappingSet['rolesDesc'] = [];
	                		angular.forEach(totas[0].body.roleLS, function(row, index, objs){
	                			$scope.mappingSet['rolesDesc'].push({LABEL: row.SA_JOB_TITLE_DESC, DATA: row.SA_JOB_TITLE_ID});
	                		});
	                	};
					}
			);
        };
        $scope.getOrgSeq = function() {
        	var deferred = $q.defer();
        	$scope.sendRecv("CMORG101", "getOrgSeq", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101DataVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		var ans = [];
	                		angular.forEach(totas[0].body.orgSeqLS, function(row, index, objs){
	                			ans.push({LABEL: row.ORG_NAME , DATA: row.ORG_SEQ});
	                		});
	                		deferred.resolve(ans);
	                	};
					}
			);
        	return deferred.promise;
        };
        $scope.init = function(){
        	$scope.inputVO.treeDEFNTreeBase = [];
        	$scope.inputVO.tipTerritoryID = '';
        	$scope.inputVO.tipDESCR = '';
        	$scope.inputVO.cmbOrgType = '';
        	$scope.inputVO.cmbOrgSeq = '';
        	$scope.inputVO.tipIsBranch = '';
        	$scope.inputVO.cmbBrchCLS = '';
        	$scope.inputVO.tipNextOrgSeq = '';
        	$scope.inputVO.tipParTerritoryID = '';
        	$scope.inputVO.tipParDESCR = '';
        	$scope.inputVO.circle = 0;
        	$scope.adgEmp = [];
        	$scope.prerow = undefined;
        	$scope.row = undefined;
        	$scope.cmbTypeDisabled = false;
        	$scope.cmbSeqDisabled = false;
			$scope.cmbClsDisabled = false;
			$scope.getInit();
			$scope.getOrgSeq().then(function(data) {
				$scope.mappingSet['seqsDesc'] = data;
			});
        };
        $scope.init();
        
        $scope.loadDefn = function(){
        	$scope.sendRecv("CMORG101", "getDEFNTree", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101DataVO", {},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		if(totas[0].body.defnTreeLS.length == 0) {
	                			$scope.showMsg("ehl_01_common_001");
	                			return;
	                		}
	                		$scope.treeDEFNTreeBase = refreshDefns(totas[0].body.defnTreeLS);
	                	};
					}
			);
        };
        $scope.loadDefn();
        function refreshDefns(defns) {
			var ans = [];
			for(var i=0;i<defns.length;i++) {
				generateDefn(ans,defns[i],0);
			}
			console.log('refreshDefns='+JSON.stringify(ans));
			return ans;
		}
		function generateDefn(ansRow,row,circle) {
			var obj = {},exist = false;
			for(var i=0;i<ansRow.length;i++) {
				if(row["L"+circle+"_TERRITORY_ID"] == ansRow[i]["TERRITORY_ID"]) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				obj["TERRITORY_ID"] = row["L"+circle+"_TERRITORY_ID"];
				obj["DESCR"] = row["L"+circle+"_DESCR"];
				obj["BRCH_CLS"] = row["L"+circle+"_BRCH_CLS"];
				obj["ORG_SEQ"] = row["L"+circle+"_ORG_SEQ"];
				obj["IS_BRANCH"] = row["L"+circle+"_IS_BRANCH"];
				obj["CIRCLE"] = circle;
				if(row["L"+(circle+1)+"_TERRITORY_ID"] == null) {
					ansRow.push(obj);
					return;
				}
				obj["SUBITEM"] = [];
				ansRow.push(obj);
				generateDefn(obj["SUBITEM"],row,circle+1);
				return;
			}
			var old = ansRow.slice(-1).pop();
			generateDefn(old["SUBITEM"],row,circle+1);
		}
        
		$scope.show = function(row,prerow,lv1row) {
			$scope.inputVO.tipTerritoryID = row.TERRITORY_ID;
			$scope.inputVO.tipDESCR = row.DESCR;
			if (row.CIRCLE == 0) {
				$scope.inputVO.cmbOrgType = "";
			} else if (row.CIRCLE == 1) {
				$scope.inputVO.cmbOrgType = lv1row.ORG_SEQ;
				$scope.cmbTypeDisabled = false;
				$scope.cmbSeqDisabled = true;
			} else {
				$scope.inputVO.cmbOrgType = lv1row.ORG_SEQ;
				$scope.cmbTypeDisabled = true;
				$scope.cmbSeqDisabled = false;
			}
			$scope.inputVO.cmbOrgSeq = "";
			$scope.getOrgSeq().then(function(data) {
				$scope.mappingSet['seqsDesc'] = data;
				$scope.inputVO.cmbOrgSeq = row.ORG_SEQ;
			});
			$scope.inputVO.tipIsBranch = row.IS_BRANCH;
			$scope.inputVO.cmbBrchCLS = row.BRCH_CLS;
			$scope.inputVO.circle = row.CIRCLE;
			$scope.row = row;
			if(row.SUBITEM) {
				$scope.inputVO.tipNextOrgSeq = row.SUBITEM[0].ORG_SEQ;
			} else {
				$scope.inputVO.tipNextOrgSeq = undefined;
				if(row.CIRCLE < 5) {
					$scope.sendRecv("CMORG101", "getNextORG_SEQ", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101InputVO", {'cmbOrgSeq':row.ORG_SEQ},
							function(totas, isError) {
			                	if (isError) {
			                		$scope.showErrorMsg(totas[0].body.msgData);
			                	}
			                	if (totas.length > 0) {
			                		if(totas[0].body.data.length == 0) {
//			                			$scope.showMsg("ehl_01_common_001");
			                			return;
			                		}
			                		$scope.inputVO.tipNextOrgSeq = totas[0].body.data[0].ORG_SEQ;
			                	};
							}
					);
				}
			}
			if(prerow) {
				$scope.inputVO.tipParTerritoryID = prerow.TERRITORY_ID;
				$scope.inputVO.tipParDESCR = prerow.DESCR;
				$scope.prerow = prerow;
			} else {
				$scope.inputVO.tipParTerritoryID = "";
				$scope.inputVO.tipParDESCR = "";
				$scope.prerow = undefined;
			}
			// circle 0 no need
			if(row.CIRCLE > 0) {
				$scope.inquireInit();
				$scope.getEmp();
			}
		};
		
		$scope.typechange = function() {
			$scope.getOrgSeq().then(function(data) {
				$scope.mappingSet['seqsDesc'] = data;
			});
        };
		
		$scope.getEmp = function() {
			$scope.sendRecv("CMORG101", "getEmp", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101ItemVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		if(totas[0].body.data.length == 0) {
	                			$scope.showMsg("ehl_01_common_001");
	                			return;
	                		}
	                		$scope.pagingList($scope.adgEmp, totas[0].body.data);
	                		$scope.outputVO = totas[0].body;
	                		angular.forEach($scope.adgEmp, function(row, index, objs){
        						row.set = [];
        						row.set.push({LABEL: "修改", DATA: "U"},{LABEL: "刪除", DATA: "D"});
        					});
	                	};
					}
			);
		};
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					row.cmbAction = "";
					$confirm({text: '是否要刪除？'}, {size: 'sm'})
		            .then(function() {
		            	$scope.sendRecv("CMORG101", "saveEmp", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101ItemVO", {'tipEmpID': row.EMP_ID,'audFlag': "D"},
		    					function(totas, isError) {
		    	                	if (isError) {
		    	                		$scope.showErrorMsg(totas[0].body.msgData);
		    	                	}
		    	                	if (totas.length > 0) {
		    	                		$scope.btnClear1();
		    	                		$scope.inquireInit();
		    	                		$scope.getEmp();
		    	                		$scope.showSuccessMsg('刪除成功');
		    	                	};
		    					}
		    			);
		            });
				} else if(row.cmbAction == "U") {
					$("#btnSave").text("儲存");
					$scope.inputVO.tipEmpID = row.EMP_ID;
					$scope.inputVO.oriEmpID = row.EMP_ID;
			    	$scope.inputVO.tipEmpName = row.EMP_NAME;
			    	// dont know why has space
			    	if(row.SA_JOB_TITLE_ID)
			    		row.SA_JOB_TITLE_ID = row.SA_JOB_TITLE_ID.trim();
			    	$scope.inputVO.cmbRole = row.SA_JOB_TITLE_ID;
			    	$scope.inputVO.tipAOCode = row.AO_CODE;
			    	$scope.inputVO.oriAOCode = row.AO_CODE;
			    	$scope.inputVO.tipPID = row.EMP_PID;
			    	$scope.inputVO.onboardDT = $scope.toJsDate(row.ON_BOARD_DATE);
			    	$scope.inputVO.tipEmail = row.E_MAIL;
					row.cmbAction = "";
				}
			}
		};
		
		$scope.checkSeq = function() {
			var index = $scope.mappingSet['seqsDesc'].map(function(e) { return e.DATA; }).indexOf($scope.inputVO.cmbOrgSeq);
			if($scope.inputVO.tipIsBranch == true || ($scope.mappingSet['seqsDesc'][index] && $scope.mappingSet['seqsDesc'][index].LABEL == "分行")) {
				$scope.cmbClsDisabled = false;
				$scope.inputVO.tipIsBranch = true;
			}
			else {
				$scope.cmbClsDisabled = true;
				$scope.inputVO.cmbBrchCLS = "";
				$scope.inputVO.tipIsBranch = false;
			}
        };
		
		//      初始分頁資訊
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.adgEmp = [];
		};
	    $scope.inquireInit();
	    
	    $scope.btnAdd = function() {
	    	var is = {check:false};
	    	checkRepeat($scope.treeDEFNTreeBase,is);
	    	if(is.check) {
	    		$scope.showErrorMsg('單位代碼及單位名稱 不可重覆');
        		return;
	    	}
	    	if($scope.row.CIRCLE <= 0){
	    		$scope.showErrorMsg('ehl_02_cmorg101_006');
        		return;
	    	}
	    	if($scope.inputVO.tipParDESCR=='' || $scope.inputVO.tipParDESCR==null){
	    		$scope.showErrorMsg('ehl_02_cmorg101_004');
        		return;
        	}
	    	if($scope.cmbClsDisabled == false && $scope.inputVO.cmbBrchCLS == ''){
	    		$scope.showErrorMsg('ehl_02_cmorg101_003');
        		return;
	    	}
	    	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
	    	$scope.prerow.SUBITEM.push({"TERRITORY_ID":$scope.inputVO.tipTerritoryID,"DESCR":$scope.inputVO.tipDESCR,"BRCH_CLS":$scope.inputVO.cmbBrchCLS,"ORG_SEQ":$scope.inputVO.cmbOrgSeq,"IS_BRANCH":$scope.inputVO.tipIsBranch ? "1" : "0","CIRCLE":$scope.inputVO.circle});
	    	$scope.showSuccessMsg('新增成功');
	    	$scope.init();
	    };
	    
	    $scope.btnSubAdd = function() {
	    	if($scope.row.CIRCLE >= 5){
	    		$scope.showErrorMsg('ehl_02_cmorg101_007');
        		return;
	    	}
	    	var is = {check:false};
	    	checkRepeat($scope.treeDEFNTreeBase,is);
	    	if(is.check) {
	    		$scope.showErrorMsg('單位代碼及單位名稱 不可重覆');
        		return;
	    	}
	    	if($scope.row.CIRCLE <= 0){
	    		$scope.showErrorMsg('ehl_02_cmorg101_006');
        		return;
	    	}
	    	if($scope.row.CIRCLE > 1) {
	    		if($scope.inputVO.tipNextOrgSeq) {
	    			if($scope.inputVO.tipNextOrgSeq != $scope.inputVO.cmbOrgSeq){
			    		$scope.showErrorMsg('ehl_02_cmorg101_006');
		        		return;
			    	}
	    		}
	    	}
	    	if($scope.cmbClsDisabled == false && $scope.inputVO.cmbBrchCLS == ''){
	    		$scope.showErrorMsg('ehl_02_cmorg101_003');
        		return;
	    	}
	    	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
	    	if (!$scope.row.SUBITEM)
	    		$scope.row.SUBITEM = [];
	    	$scope.row.SUBITEM.push({"TERRITORY_ID":$scope.inputVO.tipTerritoryID,"DESCR":$scope.inputVO.tipDESCR,"BRCH_CLS":$scope.inputVO.cmbBrchCLS,"ORG_SEQ":$scope.inputVO.cmbOrgSeq,"IS_BRANCH":$scope.inputVO.tipIsBranch ? "1" : "0","CIRCLE":$scope.inputVO.circle+1});
	    	$scope.showSuccessMsg('新增成功');
	    	$scope.init();
	    };
	    function checkRepeat(defns,is) {
	    	angular.forEach(defns, function(row, index){
	    		// circle 0 no need
	    		if (row.CIRCLE == 0) {
	    			checkRepeat(row.SUBITEM,is);
	    		} else {
	    			if(row.TERRITORY_ID == $scope.inputVO.tipTerritoryID){
		    			is.check = true;
	    				return;
		    		}
	    			if(row.DESCR == $scope.inputVO.tipDESCR){
		    			is.check = true;
	    				return;
		    		}
		    		if(row.SUBITEM != null) {
		    			checkRepeat(row.SUBITEM,is);
		    		}
	    		}
			});
	    }
	    
	    $scope.btnEdit = function() {
	    	if(!$scope.row){
	    		$scope.showErrorMsg('ehl_02_cmorg101_004');
        		return;
        	}
	    	if($scope.row.CIRCLE <= 0){
	    		$scope.showErrorMsg('ehl_02_cmorg101_006');
        		return;
	    	}
	    	if($scope.cmbClsDisabled == false && $scope.inputVO.cmbBrchCLS == ''){
	    		$scope.showErrorMsg('ehl_02_cmorg101_003');
        		return;
	    	}
	    	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
	    	$scope.row.TERRITORY_ID = $scope.inputVO.tipTerritoryID;
	    	$scope.row.DESCR = $scope.inputVO.tipDESCR;
	    	$scope.row.BRCH_CLS = $scope.inputVO.cmbBrchCLS;
	    	$scope.row.ORG_SEQ = $scope.inputVO.cmbOrgSeq;
	    	$scope.row.IS_BRANCH = $scope.inputVO.tipIsBranch ? "1" : "0";
	    	$scope.showSuccessMsg('修改成功');
	    	$scope.init();
	    };
	    
	    $scope.btnDel = function() {
	    	if($scope.row.CIRCLE <= 0){
	    		$scope.showErrorMsg('ehl_02_cmorg103_003');
        		return;
	    	}
	    	if($scope.inputVO.tipTerritoryID=='' || $scope.inputVO.tipTerritoryID==null || $scope.inputVO.tipDESCR=='' || $scope.inputVO.tipDESCR==null){
	    		$scope.showErrorMsg('ehl_02_cmorg101_001');
        		return;
        	}
	    	$confirm({text: '請確定是否刪除此筆資料？ 注意:刪除此節點會連同節點下功能目錄及功能名稱一起刪除'})
            .then(function() {
            	var delIndex = $scope.prerow.SUBITEM.map(function(e) { return e.DESCR; }).indexOf($scope.inputVO.tipDESCR);
            	$scope.prerow.SUBITEM.splice(delIndex,1);
            	$scope.showSuccessMsg('刪除成功');
            	$scope.init();
            });
	    };
	    
	    $scope.btnClear1 = function() {
	    	$scope.inputVO.tipEmpID = '';
	    	$scope.inputVO.oriEmpID = '';
	    	$scope.inputVO.tipEmpName = '';
	    	$scope.inputVO.cmbRole = '';
	    	$scope.inputVO.tipAOCode = '';
	    	$scope.inputVO.oriAOCode = '';
	    	$scope.inputVO.tipPID = '';
	    	$scope.inputVO.onboardDT = undefined;
	    	$scope.inputVO.tipEmail = '';
	    	$("#btnSave").text("新增");
        };
	    
	    $scope.btnSave = function() {
	    	if($scope.inputVO.tipTerritoryID=='' || $scope.inputVO.tipTerritoryID==null){
	    		$scope.showErrorMsg('ehl_02_cmorg101_001');
        		return;
        	}
	    	if($scope.parameterTypeEditForm2.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
	    	if($("#btnSave").text() == "新增"){
	    		$scope.inputVO.audFlag = "A";
	    		$scope.sendRecv("CMORG101", "saveEmp", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101ItemVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.btnClear1();
    	                		$scope.inquireInit();
    	                		$scope.getEmp();
    	                		$scope.showSuccessMsg('新增成功');
    	                	};
    					}
    			);
	    	} else if($("#btnSave").text() == "儲存"){
	    		$scope.inputVO.audFlag = "U";
	    		$scope.sendRecv("CMORG101", "saveEmp", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101ItemVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.btnClear1();
    	                		$scope.inquireInit();
    	                		$scope.getEmp();
    	                		$scope.showSuccessMsg('修改成功');
    	                	};
    					}
    			);
	    	}
	    };
	    
	    $scope.btnSaveDEFN = function() {
	    	if($scope.inputVO.tipTerritoryID=='' || $scope.inputVO.tipTerritoryID==null){
	    		$scope.showErrorMsg('ehl_02_cmorg101_001');
        		return;
        	}
	    	angular.forEach($scope.adgEmp, function(row, index){
	    		if(row.IsEmpID == "Y")
	    			row.TERRITORY_ID = $scope.inputVO.tipTerritoryID;
	    		else
	    			row.TERRITORY_ID = "";
			});
	    	$scope.sendRecv("CMORG101", "saveDEFN", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101ItemVO", {'defnLS': $scope.adgEmp},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.btnClear1();
	                		$scope.inquireInit();
	                		$scope.getEmp();
	                		$scope.showSuccessMsg('存入成功');
	                	};
					}
			);
        };
	    
	    $scope.btnConfirm = function() {
	    	$scope.inputVO.treeDEFNTreeBase = $scope.treeDEFNTreeBase;
	    	var is = {check:false,type:''};
	    	checkDefns($scope.inputVO.treeDEFNTreeBase,is);
	    	if(is.check) {
	    		if (is.type == 1)
	    			$scope.showErrorMsg('ehl_02_cmorg101_001');
	    		else if (is.type == 2)
	    			$scope.showErrorMsg('ehl_02_cmorg101_001');
	    		else if (is.type == 3)
	    			$scope.showErrorMsg('ehl_02_cmorg101_002');
	    		else
	    			$scope.showErrorMsg('ehl_02_cmorg101_003');
        		return;
	    	}
	    	$scope.sendRecv("CMORG101", "confirmDEFN", "com.systex.jbranch.app.server.fps.cmorg101.CMORG101InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.loadDefn();
	                		$scope.init();
	                		$scope.showSuccessMsg('存檔成功');
	                	};
					}
			);
	    };
	    function checkDefns(defns,is) {
	    	angular.forEach(defns, function(row, index){
	    		// circle 0 no need
	    		if (row.CIRCLE == 0) {
	    			checkDefns(row.SUBITEM,is);
	    		} else {
	    			if(row.TERRITORY_ID == '' || row.TERRITORY_ID == null){
		    			is.check = true;
		    			is.type = 1;
	    				return;
		    		}
	    			if(row.DESCR == '' || row.DESCR == null){
		    			is.check = true;
		    			is.type = 2;
	    				return;
		    		}
	    			if(row.ORG_SEQ == '' || row.ORG_SEQ == null){
		    			is.check = true;
		    			is.type = 3;
	    				return;
		    		}
	    			if(row.IS_BRANCH == '1' && (row.BRCH_CLS == '' || row.BRCH_CLS == null)){
		    			is.check = true;
		    			is.type = 4;
	    				return;
		    		}
		    		if(row.SUBITEM != null) {
		    			checkDefns(row.SUBITEM,is);
		    		}
	    		}
			});
	    }
        
    }
);
