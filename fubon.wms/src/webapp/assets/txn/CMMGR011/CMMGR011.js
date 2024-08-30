/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR011_Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR011_Controller";
        
        $scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO2 = {};
			$scope.TotalPriList = [];
        	$scope.priList = [];
        	$scope.funcList = [];
        	$scope.allowFuns = [];
		};
		$scope.init();
		
		$scope.initInquire = function() {
	    	$scope.sendRecv("CMMGR011", "initInquire", "com.systex.jbranch.app.server.fps.cmmgr011.CMMGR011InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.TotalPriList = tota[0].body.priList;
			    			$scope.mappingSet['groupsDesc'] = [];
			    			angular.forEach($scope.TotalPriList, function(row, index, objs){
			    				$scope.mappingSet['groupsDesc'].push({LABEL: row.PRINAME, DATA: row.PRIID});
			    			});
							return;
						}
			});
	    };
	    $scope.initInquire();
		
	    $scope.inquire = function() {
	    	$scope.paramList = [];
	    	if ($scope.ragSelect === 'rdoFunc')
	    		$scope.inputVO.queryType = "QUERY_BY_FUNC";
	    	else if ($scope.ragSelect === 'rdoGroup')
	    		$scope.inputVO.queryType = "QUERY_BY_GROUP";
	    	$scope.sendRecv("CMMGR011", "query", "com.systex.jbranch.app.server.fps.cmmgr011.CMMGR011InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = tota[0].body.txnList;
							$scope.outputVO = tota[0].body;
							if($scope.paramList.length > 0)
                        		$scope.queryItemFunList($scope.paramList[0].TXNCODE);
							return;
						}
			});
	    };
        
        $scope.queryItemFunList = function(txtcode) {
        	// clone
        	$.extend($scope.priList, $scope.TotalPriList);
        	$scope.funcList = [];
        	$scope.inputVO2.txnCode = txtcode;
        	$scope.maintenanceAll = false;
        	$scope.queryAll = false;
        	$scope.exportsAll = false;
        	$scope.printAll = false;
        	$scope.watermarkAll = false;
        	$scope.securityAll = false;
        	$scope.confirmAll = false;
        	$scope.mobileAll = false;
        	$scope.screenAll = false;
        	$scope.sendRecv("CMMGR011", "inquireItemFun", "com.systex.jbranch.app.server.fps.cmmgr011.CMMGR011InputVO", $scope.inputVO2,
    				function(tota, isError) {
	                    	if (!isError) {
	                    		$scope.allowFuns = tota[0].body.allowFuns;
	                    		for (var i = tota[0].body.itemPriFuns.length - 1; i >= 0; i--) {
	                    			tota[0].body.itemPriFuns[i].allowMaintenance = isAllowFun("maintenance");
	                    			tota[0].body.itemPriFuns[i].allowQuery = isAllowFun("query");
	                    			tota[0].body.itemPriFuns[i].allowExport = isAllowFun("export");
	                    			tota[0].body.itemPriFuns[i].allowPrint = isAllowFun("print");
	                    			tota[0].body.itemPriFuns[i].allowWatermark = isAllowFun("watermark");
	                    			tota[0].body.itemPriFuns[i].allowSecurity = isAllowFun("security");
	                    			tota[0].body.itemPriFuns[i].allowConfirm = isAllowFun("confirm");
	                    			tota[0].body.itemPriFuns[i].allowMobile = isAllowFun("mobile");
	                    			tota[0].body.itemPriFuns[i].allowScreen = isAllowFun("screen");
	                    			
	                    			var k = getIndexByPriID($scope.priList,tota[0].body.itemPriFuns[i].priID);
	                    			if(k != -1){
	                    				$scope.priList.splice(k, 1);
	                				}
	                    		}
	                    		$scope.funcList = tota[0].body.itemPriFuns;
	                    		$scope.priVO = {'data':$scope.priList};
	                			$scope.funcVO = {'data':$scope.funcList};
	                			
	                			$scope.allowMaintenance = isAllowFun("maintenance");
	                    		$scope.allowQuery = isAllowFun("query");
                    			$scope.allowExport = isAllowFun("export");
                    			$scope.allowPrint = isAllowFun("print");
                    			$scope.allowWatermark = isAllowFun("watermark");
                    			$scope.allowSecurity = isAllowFun("security");
                    			$scope.allowConfirm = isAllowFun("confirm");
                    			$scope.allowMobile = isAllowFun("mobile");
                    			$scope.allowScreen = isAllowFun("screen");
	                        	return;
	                    	}
    		});
        };
        
        function isAllowFun(funid){
        	var ans = false;
        	for (var i = 0; i < $scope.allowFuns.length; i++) {
        		if($scope.allowFuns[i] == funid){
        			ans = true;
				}
        	}
        	return ans;
        };
        function getIndexByPriID(priList,priID){
        	var ret = -1;
        	if(priList == null){
				return ret;
			}
        	var index = 0;
        	for (var i = 0; i < priList.length; i++) {
        		if(priList[i].PRIID == priID){
					ret = index;
				}
				else
				{
					index ++;
				}
        	}
			return ret;
        };
        
        $scope.checkrow = function(click,to) {
        	if (click) {
        		for (var i = 0; i < to.length; i++) {
        			to[i].SELECTED = true;
        		}
        	} else {
        		for (var i = 0; i < to.length; i++) {
        			to[i].SELECTED = false;
        		}
        	}
        };
        
        $scope.clickrow = function(who) {
        	switch (who){
				case "maintenance":
					if ($scope.maintenanceAll) {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].maintenance = true;
		        		}
		        	} else {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].maintenance = false;
		        		}
		        	}
					break;
				case "query":
					if ($scope.queryAll) {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].query = true;
		        		}
		        	} else {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].query = false;
		        		}
		        	}
					break;
				case "exports":
					if ($scope.exportsAll) {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].exports = true;
		        		}
		        	} else {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].exports = false;
		        		}
		        	}
					break;
				case "print":
					if ($scope.printAll) {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].print = true;
		        		}
		        	} else {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].print = false;
		        		}
		        	}
					break;
				case "watermark":
					if ($scope.watermarkAll) {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].watermark = true;
		        		}
		        	} else {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].watermark = false;
		        		}
		        	}
					break;
				case "security":
					if ($scope.securityAll) {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].security = true;
		        		}
		        	} else {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].security = false;
		        		}
		        	}
					break;
				case "confirm":
					if ($scope.confirmAll) {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].confirm = true;
		        		}
		        	} else {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].confirm = false;
		        		}
		        	}
					break;
				case "mobile":
					if ($scope.mobileAll) {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].mobile = true;
		        		}
		        	} else {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].mobile = false;
		        		}
		        	}
					break;
				case "screen":
					if ($scope.screenAll) {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].screen = true;
		        		}
		        	} else {
		        		for (var i = 0; i < $scope.funcList.length; i++) {
		        			$scope.funcList[i].screen = false;
		        		}
		        	}
					break;
			}
        };
        
        $scope.btnAddFun = function() {
//        	$scope.clickAll = false;
//        	$scope.clickAll2 = false;
        	for (var i = $scope.priList.length - 1; i >= 0; i--) {
        		if ($scope.priList[i].SELECTED) {
        			$scope.priList[i].SELECTED = false;
        			$scope.priList[i].priID = $scope.priList[i].PRIID;
        			$scope.priList[i].priName = $scope.priList[i].PRINAME;
        			$scope.priList[i].maintenance = false;
        			$scope.priList[i].query = false;
        			$scope.priList[i].exports = false;
        			$scope.priList[i].print = false;
        			$scope.priList[i].watermark = false;
        			$scope.priList[i].security = false;
        			$scope.priList[i].confirm = false;
        			$scope.priList[i].mobile = false;
        			$scope.priList[i].screen = false;
        			$scope.priList[i].allowMaintenance = isAllowFun("maintenance");
        			$scope.priList[i].allowQuery = isAllowFun("query");
        			$scope.priList[i].allowExport = isAllowFun("export");
        			$scope.priList[i].allowPrint = isAllowFun("print");
        			$scope.priList[i].allowWatermark = isAllowFun("watermark");
        			$scope.priList[i].allowSecurity = isAllowFun("security");
        			$scope.priList[i].allowConfirm = isAllowFun("confirm");
        			$scope.priList[i].allowMobile = isAllowFun("mobile");
        			$scope.priList[i].allowScreen = isAllowFun("screen");
        			$scope.funcList.push($scope.priList[i]);
        			$scope.priList.splice(i, 1);
        		}
        	}
        	$scope.priList.sort(function(a,b) {return (a.PRIID > b.PRIID) ? 1 : ((b.PRIID > a.PRIID) ? -1 : 0);} );
        	$scope.funcList.sort(function(a,b) {return (a.priID > b.priID) ? 1 : ((b.priID > a.priID) ? -1 : 0);} );
        	$scope.priVO = {'data':$scope.priList};
			$scope.funcVO = {'data':$scope.funcList};
        };
        
        $scope.btnDelFun = function() {
//        	$scope.clickAll = false;
//        	$scope.clickAll2 = false;
        	for (var i = $scope.funcList.length - 1; i >= 0; i--) {
        		if ($scope.funcList[i].SELECTED) {
        			$scope.funcList[i].SELECTED = false;
        			$scope.funcList[i].PRIID = $scope.funcList[i].priID;
        			$scope.funcList[i].PRINAME = $scope.funcList[i].priName;
        			$scope.priList.push($scope.funcList[i]);
        			$scope.funcList.splice(i, 1);
        		}
        	}
        	$scope.priList.sort(function(a,b) {return (a.PRIID > b.PRIID) ? 1 : ((b.PRIID > a.PRIID) ? -1 : 0);} );
        	$scope.funcList.sort(function(a,b) {return (a.priID > b.priID) ? 1 : ((b.priID > a.priID) ? -1 : 0);} );
        	$scope.priVO = {'data':$scope.priList};
			$scope.funcVO = {'data':$scope.funcList};
        };
        
        $scope.add = function () {
            var dialog = ngDialog.open({
            	template: 'assets/txn/CMMGR011/CMMGR011_EDIT.html',
				className: 'CMMGR011',
				showClose: false,
            });
            dialog.closePromise.then(function (data) {
                if(data.value === 'successful'){
                	$scope.inquire();
                }
            });
        };
        
        $scope.edit = function (row) {
        	console.log('edit data='+JSON.stringify(row));
        	$scope.inputVO2.txnCode = row.TXNCODE;
        	$scope.sendRecv("CMMGR011", "detail", "com.systex.jbranch.app.server.fps.cmmgr011.CMMGR011InputVO", $scope.inputVO2,
        			function(tota, isError) {
        				if (!isError) {
    	                   	var dialog = ngDialog.open({
    	                   		template: 'assets/txn/CMMGR011/CMMGR011_EDIT.html',
    	        				className: 'CMMGR011',
    	        				showClose: false,
    	                        controller: ['$scope', function($scope) {
    	                        	$scope.row = row;
    	                      	  	$scope.apply = tota[0].body.apply;
    	                      	  	$scope.allowFuns = tota[0].body.functionList;
    	                        }]
    	                    });
    	                    dialog.closePromise.then(function (data) {
    	                        if (data.value === 'successful'){
    	                        	$scope.inquire();
    	                        } else if (data.value === 'delsuccessful') {
    	                        	$scope.inquire();
    	                        	$scope.priList = [];
    	                        	$scope.funcList = [];
    	                        	$scope.inputVO2.txnCode = '';
    	                        	$scope.priVO = {'data':$scope.priList};
    	                			$scope.funcVO = {'data':$scope.funcList};
    	                        }
    	                    });
    	                   	return;
        			}
        	});
        };
        
        $scope.save = function () {
        	var check = false;
        	angular.forEach($scope.funcList, function(row) {
				if(!row.maintenance && !row.query && !row.exports && !row.print && !row.watermark
						&& !row.security && !row.confirm && !row.mobile && !row.screen)
					check = true;
			});
        	if(check) {
	    		$scope.showErrorMsg('ehl_01_crm911_002');
	    		return;
	    	}
        	//
        	$scope.inputVO2.itemPriFuns = $scope.funcList;
        	$scope.sendRecv("CMMGR011", "save", "com.systex.jbranch.app.server.fps.cmmgr011.CMMGR011InputVO", $scope.inputVO2,
				function(tota, isError) {
                	if (!isError) {
                		$scope.showSuccessMsg('ehl_01_common_004');
                		$scope.queryItemFunList($scope.inputVO2.txnCode);
                    	return;
                	}
    		});
        };
        
	}
);