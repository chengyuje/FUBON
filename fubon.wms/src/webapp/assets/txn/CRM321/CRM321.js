/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM321Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM321Controller";		
		$scope.mappingSet['branch'] = [];	
		$scope.mappingSet['branchFCH'] = [];	
		$scope.mappingSet['ass_brh'] = [];
		
		//排序FCH駐點行資料
		$scope.bra_list = _.sortBy(projInfoService.getAvailBranch(), ['BRANCH_NBR']);
		
		angular.forEach($scope.bra_list, function(row, index, objs){
			//取得FCH駐點行資料
			$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NBR + "-" + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			//避免在查詢時與CRM321_add的值衝突，另取FCH駐點行資料
			$scope.mappingSet['branchFCH'].push({LABEL: row.BRANCH_NBR + "-" + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			//取得分派行資料
			$scope.mappingSet['ass_brh'].push({LABEL: row.BRANCH_NBR + "-" + row.BRANCH_NAME, DATA: row.BRANCH_NBR});

		});
        
		//初始分頁資訊
		$scope.inquireInit = function(){
			$scope.data = [];
        	$scope.FCHList = [];
        	$scope.outputVO = [];
        }
        $scope.inquireInit();
        
        //FCH分派客戶數保留
        $scope.getCODE = function(){
        	$scope.sendRecv("CRM321", "init", "com.systex.jbranch.app.server.fps.crm321.CRM321InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO.crm_trs_total_fch_cust_no = tota[0].body.code;
					}
			});
        };   
		$scope.init = function(){
			$scope.inputVO = {
					fch_mast_brh: '',
					fch_branchNbr: '',
					branchNbr: ''
        	};
			$scope.getCODE();
		};
        $scope.init();
        
        //全行FCH分派客戶數設定
		$scope.setting = function(){
        	$scope.sendRecv("CRM321", "setting", "com.systex.jbranch.app.server.fps.crm321.CRM321InputVO", $scope.inputVO,
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('設定成功');
	                };
	            }
        	);
        }
                
		//查詢功能
		$scope.inquire = function(){
//        	console.log('inquire');
			$scope.sendRecv("CRM321", "inquire", "com.systex.jbranch.app.server.fps.crm321.CRM321InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.FCHList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
//							$scope.FCHList = _.sortBy(tota[0].body.FCHList, ['PRIORITY_ORDER']);
							$scope.FCHList = tota[0].body.FCHList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.FCHList, function(row, index, objs){
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							return;
						}
			});
		};
		
		//新增
        $scope.add = function(row){
            var dialog = ngDialog.open({
                template: 'assets/txn/CRM321/CRM321_add.html',
                className: 'CRM321_add',
                controller: ['$scope', function($scope) {
              	  $scope.row = row;
                }]
            });
            dialog.closePromise.then(function (data) {
                if(data.value === 'successful'){
                	$scope.inquireInit();
              	  	$scope.inquire();
                }
            });
        }
        
		//修改
        $scope.edit = function(row){
//        	alert(JSON.stringify(row));
            var dialog = ngDialog.open({
                template: 'assets/txn/CRM321/CRM321_add.html',
                className: 'CRM321_add',
                controller: ['$scope', function($scope) {
              	  $scope.row = row;
                }]
            });
//            var dialog = ngDialog.open({
//                template: 'assets/txn/CRM321/CRM321_edit.html',
//                className: 'CRM321_edit',
//                controller: ['$scope', function($scope) {
//              	  $scope.row = row;
//                }]
//            });
            dialog.closePromise.then(function (data) {
                if(data.value === 'successful'){
                	$scope.inquireInit();
                	$scope.inquire();
                }
            });
        }

		//刪除資料
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.inputVO.fch_branchNbr = row.FCH_MAST_BRH;
						$scope.inputVO.branchNbr = row.ASS_BRH;
						$scope.sendRecv("CRM321", "delete", "com.systex.jbranch.app.server.fps.crm321.CRM321InputVO", $scope.inputVO,
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg("ehl_01_common_003");
                                		$scope.inquireInit();
                                		$scope.inquire();
                                	};
                				}
                		);
					});
				} else
					$scope.edit(row);
				row.cmbAction = "";
			}
		};
		
        //匯出
        $scope.exportList = function() {
        	angular.forEach($scope.FCHList, function(row, index, objs){
    			row.FCH_MAST_BRH = $filter('mapping')(row.FCH_MAST_BRH,$scope.mappingSet['branch']);
    			row.ASS_BRH = $filter('mapping')(row.ASS_BRH,$scope.mappingSet['ass_brh']);
    		});
        	
	        $scope.sendRecv("CRM321", "exportList", "com.systex.jbranch.app.server.fps.crm321.CRM321InputVO", {'list': $scope.FCHList},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		if(totas[0].body.FCHList && totas[0].body.FCHList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
					}
			);
        };       
});