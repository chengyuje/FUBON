/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM311Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM311Controller";
	
		getParameter.XML(["CRM.VIP_DEGREE", "COMMON.YES_NO"], function(totas) {
			if (totas) {			
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];	
				//#0002678 : 刪除經營等級「一般」類別！ 只留下「私人、白金、個人」	
				$scope.mappingSet["NEW_CRM.VIP_DEGREE"] = [];
				angular.forEach($scope.mappingSet["CRM.VIP_DEGREE"], function(row, index, objs){
		        	if(row.DATA != "M"){
		        		$scope.mappingSet["NEW_CRM.VIP_DEGREE"].push({LABEL: row.LABEL, DATA: row.DATA});
		        	}			
				});
				// 2017/6/16
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			}
		});
	
		// 2017/2/15
		$scope.sendRecv("CRM311", "getAo", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.AO_JOB_RANK = tota[0].body.resultList1;
						return;
					}
		});
		//
       
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.resultList =[];
			$scope.List = [];
			$scope.outputVO = {};
		};
		$scope.init();
		
		$scope.initial = function() {
			$scope.sendRecv("CRM311", "initial", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = tota[0].body.resultList1;
						}
			});
		};
		$scope.initial();
		$scope.initial2 = function() {
			$scope.sendRecv("CRM311", "initial2", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.List = tota[0].body.resultList2;
                			$scope.outputVO = tota[0].body;
						}
			});
		};
		$scope.initial2();
		
        //理專設定彈跳視窗
        $scope.aoEdit = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM311/CRM311_edit.html',
				className: 'CRM311_edit',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
                if(data.value === 'successful'){
                	$scope.init();
                	$scope.initial();
                	$scope.initial2();
              	 }
            });
		};
		
		//客戶設定彈跳視窗
		 $scope.custEdit = function(row) {
				var dialog = ngDialog.open({
					template: 'assets/txn/CRM311/CRM311_custEdit.html',
					className: 'CRM311_custEdit',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.row = row;
	                }]
				});
				dialog.closePromise.then(function (data) {
	                if(data.value === 'successful'){
	                	$scope.init();
		              	$scope.initial();
		              	$scope.initial2();
	              	 }
	            });
			};
			
			//理專設定新增
	        $scope.aoAdd = function(){
	        	if($scope.parameterTypeEditForm.$invalid){
	        		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
	        		return;
	        	}
	        	$scope.sendRecv("CRM311", "aoAdd", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
    	                	$scope.showErrorMsg(totas[0].body.msgData);
    	                }
    	                if (totas.length > 0) {
    	                  	$scope.showSuccessMsg('新增成功');
    	                  	$scope.init();
    	                  	$scope.initial();
    	                  	$scope.initial2();
    	                };
				});
	       }
	        
	        //客戶設定新增
	        $scope.custAdd = function(){
	        	if($scope.parameterTypeEditForm2.$invalid) {
	        		$scope.showErrorMsgInDialog('欄位檢核錯誤:檢視頻率必要輸入欄位');
	        		return;
	        	}
	        	$scope.sendRecv("CRM311", "custAdd", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
					function(totas, isError) {
						if (!isError) {
							if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                  		$scope.showSuccessMsg('新增成功');
    	                  		$scope.init();
        	                  	$scope.initial();
        	                  	$scope.initial2();
    	                	};
						}
				});
	       }
	        
			//理專設定刪除資料
			$scope.aoDelete = function(row) {
				$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
					$scope.inputVO.ao_ao_job_rank = row.AO_JOB_RANK;
					$scope.sendRecv("CRM311", "aoDelete", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
        				function(totas, isError) {
                        	if (isError) {
                        		$scope.showErrorMsg(totas[0].body.msgData);
                        	}
                        	if (totas.length > 0) {
                        		$scope.showSuccessMsg('刪除成功');
                        		$scope.init();
                        		$scope.initial();
                        		$scope.initial2();
                        	};
           				}
            		);
				});
			}
			
			//客戶設定刪除資料
			$scope.custDelete = function(row) {
				$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
					$scope.inputVO.cust_ao_job_rank = row.AO_JOB_RANK;
					$scope.inputVO.vip_degree = row.VIP_DEGREE;
					$scope.sendRecv("CRM311", "custDelete", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
        				function(totas, isError) {
                        	if (isError) {
                        		$scope.showErrorMsg(totas[0].body.msgData);
                        	}
                        	if (totas.length > 0) {
                        		$scope.showSuccessMsg('刪除成功');
                        		$scope.init();
                        		$scope.initial();
                        		$scope.initial2();
                        	};
        				}
            		);
				});
			};
			
			
});