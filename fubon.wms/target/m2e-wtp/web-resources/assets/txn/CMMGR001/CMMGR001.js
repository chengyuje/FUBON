/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR001_Controller',
    function($scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR001_Controller";
        
        $scope.initInquire = function() {
        	$scope.sendRecv("CMMGR001", "initInquire", "com.systex.jbranch.app.server.fps.cmmgr001.CMMGR001InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = tota[0].body.roleList;
							$scope.outputVO = tota[0].body;
			              	$scope.mappingSet['groupsDesc'] = [];
			              	angular.forEach(tota[0].body.groupList, function(row, index, objs){
			    				$scope.mappingSet['groupsDesc'].push({LABEL: row.GROUPNAME, DATA: row.GROUP_ID});
			    			});
//			              	$scope.queryRoleFunList($scope.paramList[0].ROLEID);
							return;
						}
			});
        };
        
        $scope.init = function(){
        	$scope.inputVO = {};
        	$scope.inputVO2 = {};
        	$scope.paramList = [];
        	$scope.roleList = [];
        	$scope.initInquire();
        };
        $scope.init();
        
        $scope.queryRoleFunList = function(roleID) {
        	$scope.clickAll = false;
        	$scope.clickAll2 = false;
        	$scope.funcList = [];
        	$scope.funcVO = {};
        	$scope.inputVO2.roleID = roleID;
        	$scope.sendRecv("CMMGR001", "queryRoleFun", "com.systex.jbranch.app.server.fps.cmmgr001.CMMGR001InputVO", $scope.inputVO2,
    				function(tota, isError) {
	                    	if (!isError) {
	                    		$scope.funcList = tota[0].body.functionList;
	                    		$scope.funcVO = {'data':$scope.funcList};
	                    		for (var i = $scope.funcList.length - 1; i >= 0; i--) {
	                    			var k = getIndexByRoleID($scope.roleList,$scope.funcList[i].itemid);
	                    			if(k != -1){
	                    				$scope.roleList.splice(k, 1);
	                				}
	                    		}
	                    		$scope.roleVO = {'data':$scope.roleList};
	                        	return;
	                    	}
    		});
        };
        
        $scope.inquire = function(){
        	$scope.clickAll = false;
        	$scope.clickAll2 = false;
        	$scope.roleList = [];
        	$scope.roleVO = {};
        	if ($scope.ragSelect === 'rdoFunc'){
        		$scope.inputVO.queryType = "QUERY_BY_FUNC";
        		$scope.sendRecv("CMMGR001", "query", "com.systex.jbranch.app.server.fps.cmmgr001.CMMGR001InputVO", $scope.inputVO,
        			function(tota, isError) {
  	                   	if (!isError) {
  	                   		for (var i = tota[0].body.functionList.length - 1; i >= 0; i--) {
  	                   			if(!ifContainItemID($scope.funcList,tota[0].body.functionList[i].itemid)){
  	                   				$scope.roleList.push(tota[0].body.functionList[i]);
  	                   			}
  	                   		}
  	                   		$scope.roleVO = {'data':$scope.roleList};
  	                       	return;
  	                   	}
        		});
        	} else if ($scope.ragSelect === 'rdoGroup'){
        		$scope.inputVO.queryType = "QUERY_BY_GROUP";
        		$scope.sendRecv("CMMGR001", "query", "com.systex.jbranch.app.server.fps.cmmgr001.CMMGR001InputVO", $scope.inputVO,
            			function(tota, isError) {
      	                   	if (!isError) {
      	                   		for (var i = tota[0].body.functionList.length - 1; i >= 0; i--) {
      	                   			if(!ifContainItemID($scope.funcList,tota[0].body.functionList[i].itemid)){
      	                   				$scope.roleList.push(tota[0].body.functionList[i]);
      	                   			}
      	                   		}
      	                   		$scope.roleVO = {'data':$scope.roleList};
      	                       	return;
      	                   	}
            	});
        	}
        };
        
        function ifContainItemID(funcList,itemID){
        	if(funcList == null){
				return false;
			}
        	for (var i = 0; i < funcList.length; i++) {
				if(funcList[i].itemid == itemID){
					return true
				}
			}
			return false;
        };
        function getIndexByRoleID(totalRolArr,itemID){
        	var ret = -1;
        	if(totalRolArr == null){
				return ret;
			}
        	var index = 0;
        	for (var i = 0; i < totalRolArr.length; i++) {
        		if(totalRolArr[i].itemid == itemID){
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
        
        $scope.btnAddFun = function() {
//        	$scope.clickAll = false;
//        	$scope.clickAll2 = false;
        	for (var i = $scope.roleList.length - 1; i >= 0; i--) {
        		if ($scope.roleList[i].SELECTED) {
        			$scope.roleList[i].SELECTED = false;
        			$scope.roleList[i].maintenance = false;
        			$scope.roleList[i].query = false;
        			$scope.roleList[i].exports = false;
        			$scope.roleList[i].print = false;
        			$scope.roleList[i].watermark = false;
        			$scope.roleList[i].security = false;
        			$scope.roleList[i].confirm = false;
        			$scope.funcList.push($scope.roleList[i]);
        			$scope.roleList.splice(i, 1);
        		}
        	}
        	$scope.funcVO = {'data':$scope.funcList};
    		$scope.roleVO = {'data':$scope.roleList};
        };
        
        $scope.btnDelFun = function() {
//        	$scope.clickAll = false;
//        	$scope.clickAll2 = false;
        	for (var i = $scope.funcList.length - 1; i >= 0; i--) {
        		if ($scope.funcList[i].SELECTED) {
        			$scope.funcList[i].SELECTED = false;
        			$scope.roleList.push($scope.funcList[i]);
        			$scope.funcList.splice(i, 1);
        		}
        	}
        	$scope.funcVO = {'data':$scope.funcList};
    		$scope.roleVO = {'data':$scope.roleList};
        };
        
        $scope.save = function () {
        	if ($scope.funcList.length==0){
        		return;
        	}
        	
        	for (var i = $scope.funcList.length - 1; i >= 0; i--) {
        		if(!$scope.funcList[i].maintenance && !$scope.funcList[i].query && !$scope.funcList[i].exports
        				&& !$scope.funcList[i].print && !$scope.funcList[i].watermark && !$scope.funcList[i].security&& !$scope.funcList[i].confirm) {
        			$confirm({text: '尚有功能代號未設定。'}, {size: 'sm'});
        			return;
        		}
        	}
        	
        	$scope.inputVO2.functionList = $scope.funcList;
        	$scope.sendRecv("CMMGR001", "save", "com.systex.jbranch.app.server.fps.cmmgr001.CMMGR001InputVO", $scope.inputVO2,
        			function(tota, isError) {
  	                   	if (!isError) {
  	                   		$scope.queryRoleFunList($scope.inputVO2.roleID);
  	                       	return;
  	                   	}
        	});
        };
        
        $scope.edit = function (row) {
        	console.log('edit data='+JSON.stringify(row));
            var dialog = ngDialog.open({
                template: 'assets/txn/CMMGR001/CMMGR001_EDIT.html',
                className: 'ngdialog-theme-default custom-width',
                controller: ['$scope', function($scope) {
              	  $scope.row = row;
                }]
            });
            dialog.closePromise.then(function (data) {
                if(data.value === 'successful'){
                	$scope.init();
                }
            });
        };
        
    }
);