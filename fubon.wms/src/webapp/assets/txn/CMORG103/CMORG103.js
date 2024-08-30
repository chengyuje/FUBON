/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMORG103Controller',
    function($scope, $controller, socketService, ngDialog, projInfoService, $confirm) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMORG103Controller";
        
        $scope.showMenu2 = true;
        $scope.showMenu3 = true;
        $scope.showMenu4 = true;
        $scope.showMenu5 = true;
        $scope.showMenu6 = true;
        $scope.init = function(){
        	$scope.inputVO = {
        			treeORGTreeBase: [],
        			tipOrgSeq: '',
        			tipOrgName: '',
        			tipOrgType: '',
        			tipParOrgSeq: '',
        			tipParOrgName: '',
        			rdgAreaBranch: '',
        			circle: 0
        	};
        	$scope.prerow = undefined;
        	$scope.row = undefined;
        };
        $scope.init();
        
        $scope.loadOrg = function(){
        	$scope.sendRecv("CMORG103", "getOrgTree", "com.systex.jbranch.app.server.fps.cmorg103.CMORG103DataVO", {},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		if(totas[0].body.orgTreeLS.length == 0) {
	                			$scope.showMsg("ehl_01_common_001");
	                			return;
	                		}
	                		$scope.treeORGTreeBase = refreshOrgs(totas[0].body.orgTreeLS);
	                	};
					}
			);
        };
        $scope.loadOrg();
        function refreshOrgs(orgs) {
			var ans = [];
			for(var i=0;i<orgs.length;i++) {
				generateOrg(ans,orgs[i],0);
			}
			console.log('refreshOrgs='+JSON.stringify(ans));
			return ans;
		}
		function generateOrg(ansRow,row,circle) {
			var obj = {},exist = false;
			for(var i=0;i<ansRow.length;i++) {
				if(row["LEV"+circle+"_ORG_SEQ"] == ansRow[i]["ORG_SEQ"]) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				obj["ORG_SEQ"] = row["LEV"+circle+"_ORG_SEQ"];
				obj["ORG_NAME"] = row["LEV"+circle+"_ORG_NAME"];
				obj["IS_AREA"] = row["LEV"+circle+"_IS_AREA"];
				obj["IS_BRANCH"] = row["LEV"+circle+"_IS_BRANCH"];
				obj["CIRCLE"] = circle;
				if(row["LEV"+(circle+1)+"_ORG_SEQ"] == null) {
					ansRow.push(obj);
					return;
				}
				obj["SUBITEM"] = [];
				ansRow.push(obj);
				generateOrg(obj["SUBITEM"],row,circle+1);
				return;
			}
			var old = ansRow.slice(-1).pop();
			generateOrg(old["SUBITEM"],row,circle+1);
		}
		
		$scope.show = function(row,prerow,lv1row){
			$scope.inputVO.tipOrgSeq = row.ORG_SEQ;
			$scope.inputVO.tipOrgName = row.ORG_NAME;
			if (row.CIRCLE == 0)
				$scope.inputVO.tipOrgType = '';
			else
				$scope.inputVO.tipOrgType = lv1row.ORG_NAME;
			$scope.inputVO.circle = row.CIRCLE;
			$scope.row = row;
			if(prerow) {
				$scope.inputVO.tipParOrgSeq = prerow.ORG_SEQ;
				$scope.inputVO.tipParOrgName = prerow.ORG_NAME;
				$scope.prerow = prerow;
			} else {
				$scope.inputVO.tipParOrgSeq = "";
				$scope.inputVO.tipParOrgName = "";
				$scope.prerow = undefined;
			}
			if (row.IS_AREA)
				$scope.inputVO.rdgAreaBranch = "1";
			else if (row.IS_BRANCH)
				$scope.inputVO.rdgAreaBranch = "2";
			else 
				$scope.inputVO.rdgAreaBranch = '';
		};
		
		// 初始分頁資訊
		$scope.inquireInit = function(){
			$scope.initLimit();
		};
	    $scope.inquireInit();
	    
	    $scope.btnAdd = function() {
	    	if(!$scope.prerow){
	    		$scope.showErrorMsg('ehl_02_cmorg103_001');
        		return;
        	}
	    	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('ehl_02_cmorg103_000');
        		return;
        	}
	    	$scope.prerow.SUBITEM.push({"ORG_NAME":$scope.inputVO.tipOrgName,"IS_AREA":$scope.inputVO.rdgAreaBranch == "1","IS_BRANCH":$scope.inputVO.rdgAreaBranch == "2","CIRCLE":$scope.prerow.CIRCLE+1});
	    	$scope.showSuccessMsg('新增成功');
	    	$scope.init();
	    };
	    
	    $scope.btnSubAdd = function() {
	    	if($scope.row.CIRCLE >= 5){
	    		$scope.showErrorMsg('ehl_02_cmorg103_002');
        		return;
	    	}
	    	if(!$scope.row || !$scope.row.ORG_NAME){
	    		$scope.showErrorMsg('ehl_02_cmorg103_001');
        		return;
        	}
	    	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('ehl_02_cmorg103_000');
        		return;
        	}
	    	if (!$scope.row.SUBITEM)
	    		$scope.row.SUBITEM = [];
	    	$scope.row.SUBITEM.push({"ORG_NAME":$scope.inputVO.tipOrgName,"IS_AREA":$scope.inputVO.rdgAreaBranch == "1","IS_BRANCH":$scope.inputVO.rdgAreaBranch == "2","CIRCLE":$scope.row.CIRCLE+1});
	    	$scope.showSuccessMsg('新增成功');
	    	$scope.init();
	    };
	    
	    $scope.btnEdit = function() {
	    	if(!$scope.row || !$scope.row.ORG_NAME){
	    		$scope.showErrorMsg('ehl_02_cmorg103_001');
        		return;
        	}
	    	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('ehl_02_cmorg103_000');
        		return;
        	}
	    	$scope.row.ORG_NAME = $scope.inputVO.tipOrgName;
	    	$scope.row.IS_AREA = $scope.inputVO.rdgAreaBranch == "1";
	    	$scope.row.IS_BRANCH = $scope.inputVO.rdgAreaBranch == "2";
	    	$scope.showSuccessMsg('修改成功');
	    	$scope.init();
	    };
	    
	    $scope.btnDel = function() {
	    	if(!$scope.row || !$scope.row.ORG_NAME){
	    		$scope.showErrorMsg('ehl_02_cmorg103_001');
        		return;
        	}
	    	if($scope.row.CIRCLE <= 0){
	    		$scope.showErrorMsg('ehl_02_cmorg103_003');
        		return;
	    	}
	    	$confirm({text: '請確定是否刪除此筆資料？ 注意:刪除此節點會連同節點下功能目錄及功能名稱一起刪除'})
            .then(function() {
            	var delIndex = $scope.prerow.SUBITEM.map(function(e) { return e.ORG_NAME; }).indexOf($scope.row.ORG_NAME);
            	$scope.prerow.SUBITEM.splice(delIndex,1);
            	$scope.showSuccessMsg('刪除成功');
            	$scope.init();
            });
	    };
	    
	    $scope.btnConfirm = function() {
	    	$scope.inputVO.treeORGTreeBase = $scope.treeORGTreeBase;
	    	var is = {'check':false,'type':''};
	    	checkOrgs($scope.inputVO.treeORGTreeBase,is);
	    	if(is.check) {
	    		if(is.type=="1")
	    			$scope.showErrorMsg('ehl_02_cmorg103_004');
	    		else if(is.type=="2")
	    			$scope.showErrorMsg('ehl_02_cmorg103_005');
        		return;
	    	}
	    	$scope.sendRecv("CMORG103", "confirmORG", "com.systex.jbranch.app.server.fps.cmorg103.CMORG103DataVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.loadOrg();
	                		$scope.showSuccessMsg('全部存檔成功');
	                		$scope.init();
	                	};
					}
			);
	    };
	    function checkOrgs(orgs,is) {
	    	angular.forEach(orgs, function(row, index){
	    		if(row.ORG_NAME == "") {
	    			is.check = true;
	    			is.type="1";
    				return;
	    		} else if (row.IS_AREA == true && row.IS_BRANCH == true) {
	    			is.check = true;
	    			is.type="2";
    				return;
	    		}
	    		if(row.SUBITEM) {
    				checkOrgs(row.SUBITEM,is);
    			}
			});
	    }
        
    }
);
