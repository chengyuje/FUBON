/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MAO241Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MAO241Controller";

		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		
		// filter
		getParameter.XML(["MAO.DEV_STATUS", "MAO.DEV_SITE_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['MAO.DEV_STATUS'] = totas.data[totas.key.indexOf('MAO.DEV_STATUS')];
				$scope.mappingSet['MAO.DEV_SITE_TYPE'] = totas.data[totas.key.indexOf('MAO.DEV_SITE_TYPE')];
			}
		});
		
		// 登入角色驗證(是否開放儲存功能)
    	$scope.sendRecv("MAO241", "checkAuthForSave", "com.systex.jbranch.app.server.fps.mao241.MAO241InputVO", {}, 
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body > 0){
							$scope.auth = true;
						}else{
							$scope.auth = false;
						}
						
					}
				}
		);

		$scope.init = function() {
			$scope.inputVO = {
					dev_nbr : '',
					dev_status : 'N',
					dev_site_type : '',
					dev_take_emp : ''
			}
		}
		$scope.init();
		
		//輸出欄初始化
		$scope.inquireInit = function(){
			$scope.resultList = [];
			$scope.data = [];
		}
		$scope.inquireInit();
		
		//呼叫MAP
		$scope.map = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/MAO241/MAO241_map.html',
				className: 'MAO241_map',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		//查詢
		$scope.inquire = function() {
			$scope.sendRecv("MAO241", "inquire", "com.systex.jbranch.app.server.fps.mao241.MAO241InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList = _.sortBy(tota[0].body.resultList,['BRA_NBR']);
							$scope.outputVO = tota[0].body;
							return;
						}
			});
	    };

	    //add_page
	    $scope.add_page = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/MAO241/MAO241_add.html',
				className: 'MAO241_add',
				showClose: false,
                
			});
			dialog.closePromise.then(function (data) {
                if(data.value === 'successful'){
                	$scope.inquireInit();
                	$scope.inquire();
                }
            });
		};
		
		//修改成刪除狀態
		$scope.deletefile = function(){
			var ans = $scope.resultList.filter(function(obj){
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		$scope.showErrorMsgInDialog('請選取!');
        		return;
        	}
        	$scope.sendRecv("MAO241", "delete", "com.systex.jbranch.app.server.fps.mao241.MAO241InputVO", {'chkSEQ': ans},
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('刪除成功');
	                	$scope.inquireInit();
	                	$scope.inquire();
	                };
	            }
        	);
        }
		
		//修改成遺失狀態
		$scope.lost = function(){
			var ans = $scope.resultList.filter(function(obj){
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		$scope.showErrorMsgInDialog('請選取!');
        		return;
        	}
        	
        	$scope.sendRecv("MAO241", "lost", "com.systex.jbranch.app.server.fps.mao241.MAO241InputVO", {'chkSEQ': ans},
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('修改成功');
	                	$scope.inquireInit();
	                	$scope.inquire();
	                };
	            }
        	);
        }
		
		//儲存裝置保管人
		$scope.save = function(){
			var ans = $scope.resultList.filter(function(obj){
				return (obj.SELECTED == true);
			});
			if(ans.length == 0){
				$scope.showErrorMsgInDialog('請選取!');
				return;
			}
		
			$scope.sendRecv("MAO241", "save", "com.systex.jbranch.app.server.fps.mao241.MAO241InputVO", {'chkSEQ': ans},
					function(totas, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(totas.body.msgData);
					return;
				}
				if (totas.length > 0) {
					$scope.showMsg('修改成功');
					$scope.inquireInit();
					$scope.inquire();
				};
			}
			);
		}
		
		//顯示裝置保管人姓名
		$scope.showName = function(row){
			$scope.inputVO.dev_take_emp = row.DEV_TAKE_EMP;
			
			if($scope.inputVO.dev_take_emp.length<6){
				$scope.showMsg('員編錯誤');
				row.DEV_TAKE_EMP = '';
				row.KEEPER_NAME = ''
				return;
			}
			
			$scope.sendRecv("MAO241", "showName", "com.systex.jbranch.app.server.fps.mao241.MAO241InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							debugger;
							row.KEEPER_NAME = tota[0].body;
							if(row.KEEPER_NAME == ''){
								$scope.showMsg('查無此人');
								row.DEV_TAKE_EMP = '';
							}
						}
			});
			
		}
		
		//匯出
        $scope.exportfile = function() {
    		$scope.inputVO.chkSEQ = $scope.resultList;
    	
        	$scope.sendRecv("MAO241", "export", "com.systex.jbranch.app.server.fps.mao241.MAO241InputVO", $scope.inputVO, 
    				function(tota, isError) {
    					if (isError) {
    						$scope.showErrorMsgInDialog(tota.body.msgData);
    						return;
    					}
    					if (tota.length > 0) {}
    				}
    		);
        };

});