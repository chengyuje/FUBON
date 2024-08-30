/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS433Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS433Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		
		getParameter.XML(["PMS.HIGH_SOT_CALL_RESULT"], function(totas) {
			if (totas) {
				$scope.mappingSet['PMS.HIGH_SOT_CALL_RESULT'] = totas.data[totas.key.indexOf("PMS.HIGH_SOT_CALL_RESULT")];
			}
		});
		
        
        
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	if($scope.inputVO.sCreDate=='') {
        		
        	} else {
        		//可視範圍  觸發 
        		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
            	$scope.RegionController_getORG($scope.inputVO);
        	}        	
        };

        
        

		
		$scope.init = function(){
			$scope.inputVO = {	
					reportDate: '',//可視範圍用
                    sCreDate: '',//資料統計月份
					trade_source: '', //交易管道
					call_result: '', //外撥結果
					region_center_id: '',   //業務處
					branch_area_id: '' ,   //營運區
					branch_nbr: ''	     //歸屬行  			
        	};
			
			$scope.sendRecv("PMS433", "init", "com.systex.jbranch.app.server.fps.pms433.PMS433InputVO", {}, function(tota, isError) {
				if (!isError) {
					$scope.mappingSet['date'] = tota[0].body.dateList;
					$scope.inputVO.reportDate = $scope.mappingSet['date'][0].DATA;
					$scope.inputVO.sCreDate = $scope.mappingSet['date'][0].DATA;
					$scope.RegionController_getORG($scope.inputVO);
				} else {
					$scope.showErrorMsg("參數初始化失敗，請重新登入此畫面或洽詢系統管理員。");
				}
			});	
		};
		$scope.init();
		
		$scope.inquirInit = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
		}
		

		
		$scope.query = function(){
			
			if($scope.inputVO.sCreDate == undefined || $scope.inputVO.sCreDate == '') {
	    		$scope.showErrorMsg('欄位檢核錯誤:月份必要輸入欄位');
        		return;
        	}
			
			$scope.sendRecv("PMS433", "query", "com.systex.jbranch.app.server.fps.pms433.PMS433InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.resultList = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
						
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.resultList = tota[0].body.resultList;
							angular.forEach($scope.resultList, function(row, index, objs){
//								row.IDS=row.ID.substring(0, 4)+"****"+row.ID.substring(8, 10);
//								
//								//作業主管能做的資料 by 2017/11/29 willis 需求變更
//								if($scope.role_id =='A150' || $scope.role_id == 'ABRF'){
//									if(row.ROLE_FLAG == 'BR'){
//										row.DISABLE_ROLE_FLAG = 'Y';
//									}
//								}
//								//分行主管能做的資料 by 2017/11/29 willis 需求變更
//								if($scope.role_id =='A161' || $scope.role_id == 'A149' || $scope.role_id =='ABRU' || $scope.role_id =='A308'){
//									if(row.ROLE_FLAG == 'OP'){
//										row.DISABLE_ROLE_FLAG = 'Y';
//									}
//								}
							});	

							$scope.outputVO = tota[0].body;	
							return;
						}						
			});
		};
		
		$scope.export = function(){
			$scope.sendRecv("PMS433", "export", "com.systex.jbranch.app.server.fps.pms433.PMS433InputVO", {'resultList':$scope.resultList},
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsg(tota[0].body.msgData);		            		
		            	}
			});
		};		
		
		$scope.openDialog = function (row) {
			var seq = row.SEQ;

			
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS433/PMS433_DETAIL.html',
				className: 'PMS433',
				showClose: false,
	            controller: ['$scope', function($scope) {
	            	$scope.seq = seq;
	            }]
			});
		};
		
		$scope.modifyResult = function (row) {
			row.MODIFY_FLAG = 'Y';
		};
		
		$scope.save = function(){
			$scope.modifyList = [];
			angular.forEach($scope.resultList, function(row){
				if (row.MODIFY_FLAG === 'Y') {
					$scope.modifyList.push(row);
				  }
				});
			if($scope.modifyList.length > 0) {
				$scope.sendRecv("PMS433", "save", "com.systex.jbranch.app.server.fps.pms433.PMS433InputVO", {'modifyList':$scope.modifyList},
						function(tota, isError) {						
							if (isError) {
								$scope.showErrorMsg(tota[0].body.msgData);		            		
			            	} else {
			            		$scope.showSuccessMsg("儲存完畢。");
			            		$scope.inquirInit();
			            		$scope.query();
			            	}
				});
			} else {
				$scope.showMsg("沒有欄位被異動。");
			}
		}
		
});
