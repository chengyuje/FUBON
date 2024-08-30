/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IMPORTFILEController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IMPORTFILEController";

		var set_names = {};
		
		//init SET_NAME
		$scope.initial = function() {
			$scope.set_code = '';
			$scope.sendRecv("IMPORTFILE", "initial", "com.systex.jbranch.app.server.fps.importfile.IMPORTFILEInputVO",{},
					function(totas, isError) {
						if (!isError) {
							$scope.mappingSet['SET_NAME'] = [];
	                		angular.forEach(totas[0].body.NameList, function(row, index, objs){
	            				$scope.mappingSet['SET_NAME'].push({LABEL: row.NAME, DATA: row.CODE});
	            			});
	                		set_names = totas[0].body.NameList;
	                		$scope.inputVO.set_name = '';
							return;
						}
			});
		};
		$scope.initial();
		
		//import xlsx
		$scope.importfile = function(set_code) {
			$scope.inputVO.set_code = set_code;
			$scope.sendRecv("IMPORTFILE", "importfile", "com.systex.jbranch.app.server.fps.importfile.IMPORTFILEInputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(totas.body.msgData);
							return;
						}
						if (totas.length > 0) {
							$scope.showMsg('上傳成功');
						};
					}
			);
	    };
	    
	    //import csv
		$scope.test = function(set_name) {
			$scope.inputVO.set_name = set_name;
			$scope.sendRecv("IMPORTFILE", "test", "com.systex.jbranch.app.server.fps.importfile.IMPORTFILEInputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(totas.body.msgData);
							return;
						}
						if (totas.length > 0) {
							$scope.showMsg('上傳成功');
						};
					}
			);
	    };
	    
	    //upload
	    $scope.uploadFinshed = function(name, rname) {
	    	$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
        };
        
        //add set
        $scope.add = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/IMPORTFILE/IMPORTFILE_detail.html',
				className: 'IMPORTFILE_detail',
				showClose: false,
				controller: ['$scope', function($scope) {
                	$scope.set_names = set_names;
                }]
			});
			dialog.closePromise.then(function (data) {
                if(data.value === 'successful' || data.value === 'close'){
              	  $scope.initial();
                }
            });
		};
		
		//delete set
		$scope.delete = function(set_code) {
			if (set_code == '' || set_code == undefined) {
				$scope.showErrorMsg('尚未選擇設定名稱');
				return;
			}
			$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
				$scope.inputVO.set_code = set_code;
				$scope.sendRecv("IMPORTFILE", "delete", "com.systex.jbranch.app.server.fps.importfile.IMPORTFILEInputVO", $scope.inputVO,
						function(totas, isError) {
							if (isError) {
								$scope.showErrorMsgInDialog(totas.body.msgData);
								return;
							}	
							if (totas.length > 0) {
								$scope.showMsg('刪除成功');
							};
							$scope.set_name = '';
							$scope.initial();
						}
				);
			});
	    };
	    
	    //modify set
	    $scope.detail = function(set_code) {
	    	if (set_code == '' || set_code == undefined) {
				$scope.showErrorMsg('尚未選擇設定名稱');
				return;
			}
	    	$scope.inputVO.set_code = set_code;
	    	$scope.sendRecv("IMPORTFILE", "detail", "com.systex.jbranch.app.server.fps.importfile.IMPORTFILEInputVO",$scope.inputVO,
					function(totas, isError) {
						if (!isError) {
							var dialog = ngDialog.open({
								template: 'assets/txn/IMPORTFILE/IMPORTFILE_detail.html',
								className: 'IMPORTFILE_detail',
								showClose: false,
				                controller: ['$scope', function($scope) {
				                	$scope.Sheets = totas[0].body.ResultList;
				                	$scope.set_names = set_names;
				                }]
							});
							dialog.closePromise.then(function (data) {
				                if(data.value === 'successful' || data.value === 'close'){
				              	  	$scope.initial();
				                }
				            });
						}
			});
	    	
		};
	    
	    
});