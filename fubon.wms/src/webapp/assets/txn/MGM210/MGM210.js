/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM210Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM210Controller";
		
		getParameter.XML(["MGM.ACT_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.ACT_STATUS'] = totas.data[totas.key.indexOf('MGM.ACT_STATUS')];		//活動狀態
//				$scope.mappingSet['MGM.ACT_TYPE'] = totas.data[totas.key.indexOf('MGM.ACT_TYPE')];			//活動類型
			}
		});
		
		//活動代碼下拉選單
	    $scope.getActSeq = function () {
	        $scope.sendRecv("MGM210", "getAct", "com.systex.jbranch.app.server.fps.mgm210.MGM210InputVO", $scope.inputVO,
	            function (tota, isError) {
	                if (!isError) {
	                    $scope.actList = [];
	                    angular.forEach(tota[0].body.outputList, function (row, index, objs) {
	                        $scope.actList.push({
	                            LABEL: row.ACT_NAME,
	                            DATA: row.ACT_SEQ
	                        });
	                    });
	                    return;
	                }
	            });
	    };
		
		$scope.init = function () {
			$scope.inputVO.actSEQ = '';
			$scope.inputVO.actStatus = '';
			$scope.inputVO.actSeq = [];
			$scope.resultList = [];
			$scope.outputVO = [];
			
			$scope.getActSeq();
		}
		
		$scope.init();
		
	    //查詢
	    $scope.query = function () {
//	        console.log($scope.inputVO);
	        $scope.sendRecv("MGM210", "query", "com.systex.jbranch.app.server.fps.mgm210.MGM210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = [];
							$scope.outputVO = [];
							if(tota[0].body.resultList.length > 0){
								$scope.resultList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
							}else{
								$scope.showMsg("ehl_01_common_009");	//查無資料
								return;
							}
						}
	        });
	    }
	    
	    $scope.addDeleteList = function(){
	    	$scope.inputVO.actSeq = [];
	    	angular.forEach($scope.resultList, function(row){
	    		if (row.DEL == true) {
	    			$scope.inputVO.actSeq.push(row.ACT_SEQ);
	    		}
	    	});
	    }
	    
	    //刪除
	    $scope.delRow = function(){
	    	if($scope.inputVO.actSeq.length > 0){
	    		$confirm({text: '您確定要刪除資料?'}, {size: 'sm' }).then(function () {
	    			
	    			$scope.sendRecv("MGM210", "deleteRow", "com.systex.jbranch.app.server.fps.mgm210.MGM210InputVO", {'actSeq': $scope.inputVO.actSeq},
	    					function(tota, isError) {
	    				if (!isError) {
	    					$scope.showMsg("ehl_01_common_003");	//刪除成功
	    					$scope.init();
	    					$scope.query();
	    					return;
	    				}
	    			});
	    		});	
	    	}
	    }
	    
	    //活動新增&維護
	    $scope.openMGM211 = function (row) {
//	    	alert(JSON.stringify(row));
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM211/MGM211.html',
				className: 'MGM211',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 	$scope.row = row;
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful' || data.value === 'cancel'){
					$scope.init();
					$scope.query();
				}
			});
	    }
	    
	    //點數放行
	    $scope.openMGM212 = function (row) {
//	    	alert(JSON.stringify(row));
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM212/MGM212.html',
				className: 'MGM212',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 	$scope.row = row;
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.init();
					$scope.query();
				}
			});
	    }

});
