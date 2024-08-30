/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM210Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "SQM210Controller";
		
//		//選取月份下拉選單 --> 重新設定可視範圍
        	//設定回傳時間
    	$scope.inputVO.reportDate = new Date();;
    	//可是範圍  觸發 
    	$scope.RegionController_getORG($scope.inputVO);

    	$scope.sendRecv("SQM210", "getYearMon", "com.systex.jbranch.app.server.fps.sqm210.SQM210InputVO", {},
		    function(totas, isError) {
	             	if (totas.length > 0) {
	               		$scope.ymList = totas[0].body.resultList;
	               	};
		    }
		);			
//		$scope.QTN_LIST = [{'LABEL':'投資/保險', 'DATA': 'WMS01'},{'LABEL':'理專', 'DATA': 'WMS02'},{'LABEL':'開戶', 'DATA': 'WMS03'},{'LABEL':'櫃檯', 'DATA': 'WMS04'},{'LABEL':'簡訊', 'DATA': 'WMS05'}];				
    	getParameter.XML(["SQM.QTN_TYPE"], function(totas) {
			if (totas) {
				$scope.QTN_LIST = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
			}
		});
    	
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.paramList =[];
			$scope.totalData = [];
			$scope.outputVO={};
			
		}
		
		// filter
		getParameter.XML(["SQM.ROLE_ID_SHOW","SQM.ANS_TYPE"], function(totas) {
			if (totas) {
				//$scope.JOB_TITLE_NAME_SEARCH = totas.data[totas.key.indexOf('ORG.JOB_TITLE_NAME_SEARCH')];
				$scope.ROLE_ID_SHOW = totas.data[totas.key.indexOf('SQM.ROLE_ID_SHOW')];
				$scope.ANS_TYPE = totas.data[totas.key.indexOf('SQM.ANS_TYPE')];
			}
		});
		
		$scope.init();
		
		$scope.inquiry = function(){
			if($scope.parameterTypeEditForm.$invalid){
				if ($scope.inputVO.yearMon == null || $scope.inputVO.yearMon == "") {
					$scope.showMsg('資料年月未選取');//ehl_01_SQM210_001
					return;
				}
			}
			$scope.sendRecv("SQM210", "inquire", "com.systex.jbranch.app.server.fps.sqm210.SQM210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.totalData = [];
								$scope.outputVO={};
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
//							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;							
							return;
							
							
						}						
			});
		};


		//將表單數據插入或更新到數據庫
		$scope.save = function() {
			$scope.inputVO.paramList = $scope.paramList;
			console.log(JSON.stringify($scope.inputVO.paramList));
			$scope.sendRecv(
				"SQM210","save","com.systex.jbranch.app.server.fps.sqm210.SQM210InputVO",
				$scope.inputVO,
				function(tota, isError) {
					if(!isError){
						$scope.showMsg("ehl_01_common_002"); //成功
						$scope.inquiry();
					}else{
						$scope.showMsg("ehl_01_common_024"); //執行失敗
					}
				
					return;
				});
		}
		
		$scope.exportRPT = function(){						
			$scope.sendRecv("SQM210", "exportRPT", "com.systex.jbranch.app.server.fps.sqm210.SQM210InputVO", $scope.inputVO,
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
		
		$scope.change_final = function(row){
			switch(row.RESP_NO){
			case "0":
				row.DEDUCTION_FINAL='N';
				break;	
			case "4":
				row.DEDUCTION_FINAL='Y';
				break;
			case "5":
				row.DEDUCTION_FINAL='Y';
				break;
			case "6":
				row.DEDUCTION_FINAL='Y';
				break;
			default :
				row.DEDUCTION_FINAL='';
				break;
			}
			
			
			
		};
});
