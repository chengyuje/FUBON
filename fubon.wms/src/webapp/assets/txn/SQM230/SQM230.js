/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM230Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "SQM230Controller";
		
		// filter
		getParameter.XML(["SQM.ANS_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['SQM.ANS_TYPE'] = totas.data[totas.key.indexOf('SQM.ANS_TYPE')];
			}
		});
		
		//處長裁示
		$scope.mappingSet['SQM.DEDUCTION_INITIAL'] = [];
    	$scope.mappingSet['SQM.DEDUCTION_INITIAL'].push({LABEL: '扣分', DATA: '1'},
    													{LABEL: '不扣分', DATA: '2'});
    	
    	//總行裁示
    	$scope.mappingSet['SQM.DEDUCTION_FINAL'] = [];
    	$scope.mappingSet['SQM.DEDUCTION_FINAL'].push({LABEL: '未裁示', DATA: '0'},
    												  {LABEL: '扣分', DATA: '1'},
    												  {LABEL: '不扣分', DATA: '2'});
		
		//選取月份下拉選單 --> 重新設定可視範圍
		//設定回傳時間
    	$scope.inputVO.reportDate = new Date();;
    	//可是範圍  觸發 
    	$scope.RegionController_getORG($scope.inputVO);

    	$scope.sendRecv("SQM230", "getYearMon", "com.systex.jbranch.app.server.fps.sqm230.SQM230InputVO", {},
		    function(totas, isError) {
	             	if (totas.length > 0) {
	               		$scope.ymList = totas[0].body.resultList;
	               	};
		    }
		);				
        
    	// filter
		getParameter.XML(["ORG.JOB_TITLE_NAME_SEARCH","SQM.ANS_TYPE"], function(totas) {
			if (totas) {
				$scope.JOB_TITLE_NAME_SEARCH = totas.data[totas.key.indexOf('ORG.JOB_TITLE_NAME_SEARCH')];
				$scope.ANS_TYPE = totas.data[totas.key.indexOf('SQM.ANS_TYPE')];
			}
		});
    	
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.resultList = [];
			$scope.outputVO = [];
		}
		
		$scope.init();
		
		$scope.inquire = function(){
			if($scope.parameterTypeEditForm.$invalid){
				if ($scope.inputVO.yearMon == null || $scope.inputVO.yearMon == '') {
					$scope.showMsg('資料年月未選取');	//ehl_01_SQM230_001
					return;
				}
			}
			
			$scope.sendRecv("SQM230", "inquire", "com.systex.jbranch.app.server.fps.sqm230.SQM230InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
							if(tota[0].body.resultList.length == 0) {
								$scope.resultList = [];
								$scope.outputVO = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;						
							return;
							
							
						}						
			});
		};
		
		$scope.openSetting = function(){
			var dialog = ngDialog.open({
				template: 'assets/txn/SQM230/SQM230_SET.html',
				className: 'SQM230_SET',
				showClose: false,
				 controller: ['$scope', function($scope) {
	                	
	             }]
			});
//			dialog.closePromise.then(function (data) {
//				if(data.value === 'successful'){
//					
//				}
//			});
		}

		//將表單數據插入或更新到數據庫
		$scope.save = function() {
			$scope.inputVO.resultList = $scope.resultList;
			$scope.sendRecv("SQM230", "save", "com.systex.jbranch.app.server.fps.sqm230.SQM230InputVO", $scope.inputVO,
				function(tota, isError) {
					if(!isError){
						$scope.showMsg("ehl_01_common_002");	//修改成功
						$scope.inquire();
					}else{
						$scope.showMsg("ehl_01_common_024");	//執行失敗
					}
					return;
			});
		}
		
		//匯出
		$scope.exportRPT = function(){	
			$scope.inputVO.resultList = $scope.resultList;
			$scope.sendRecv("SQM230", "exportRPT", "com.systex.jbranch.app.server.fps.sqm230.SQM230InputVO", $scope.inputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                	}
	                }
			});
		};
});
