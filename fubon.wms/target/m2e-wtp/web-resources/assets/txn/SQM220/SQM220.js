/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM220Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "SQM220Controller";
		
    	//設定回傳時間
		$scope.inputVO.reportDate = new Date();
    	//可是範圍  觸發 
    	$scope.RegionController_getORG($scope.inputVO);
    	
    	$scope.sendRecv("SQM220", "getYearMon", "com.systex.jbranch.app.server.fps.sqm220.SQM220InputVO", {},
		    function(totas, isError) {
	             	if (totas.length > 0) {
	             		debugger;
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
			$scope.inputVO = {
					yearMon: '',
					branchNbr: "",
					qtnType: ""
        	};

			$scope.paramList = [];
			$scope.totalData = [];
			$scope.outputVO = {};
			$scope.resultSumList = {};
		}
		
		 //選取月份 --> 重新設定可視範圍
		$scope.dateChange = function(){
        	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
        	$scope.RegionController_getORG($scope.inputVO);
		};

		$scope.init();
		
		

		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid){
				if($scope.inputVO.yearMon == '' || $scope.inputVO.yearMon == null){
		    		$scope.showMsg('資料年月未選取');
	        		return;
	        	}
			}
			$scope.sendRecv("SQM220", "inquire", "com.systex.jbranch.app.server.fps.sqm220.SQM220InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.totalData = [];
								$scope.outputVO = {};
								$scope.resultSumList = {};
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;

							$scope.outputVO = tota[0].body;
							$scope.resultSumList = {};
							angular.forEach($scope.paramList , function (row , index , objs) {
								var key = row.BRANCH_NBR ; 
									if (key in $scope.resultSumList) {
										$scope.resultSumList[key].VS_CNT	+= row.VS_CNT;
										$scope.resultSumList[key].S_CNT 	+= row.S_CNT;
										$scope.resultSumList[key].OS_CNT 	+= row.OS_CNT;
										$scope.resultSumList[key].NS_CNT	+= row.NS_CNT;
										$scope.resultSumList[key].VD_CNT	+= row.VD_CNT;
										$scope.resultSumList[key].NC_CNT	+= row.NC_CNT;
										$scope.resultSumList[key].TOT_CNT	+= row.TOT_CNT;
									} else {
										$scope.resultSumList[key] = angular.copy(row);
									}
							});

							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;							
							return;
						}						
			});
		};
		
		$scope.save = function () {
			//判斷有無修改
			if ($scope.originalList == $scope.paramList) {
				$scope.showMsg('資料沒有做任何修改!!!');
				return;
			}
			
			$scope.sendRecv("SQM220", "save", "com.systex.jbranch.app.server.fps.sqm220.SQM220InputVO", {'paramList':$scope.paramList},
					function(tota, isError) {
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		$scope.showMsg('儲存成功');
		            		$scope.inputVO.currentPageIndex=$scope.outputVO.currentPageIndex || 0;
		            		$scope.query();
		            	};		
			});
			$scope.query();
		};
		
		$scope.exportRPT = function(){
			$scope.sendRecv("SQM220", "export", "com.systex.jbranch.app.server.fps.sqm220.SQM220InputVO", $scope.inputVO,
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
