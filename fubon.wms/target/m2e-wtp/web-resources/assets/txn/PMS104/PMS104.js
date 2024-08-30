/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS104Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$filter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS104Controller";
		
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+2;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<13; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
		
		 //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	if($scope.inputVO.sCreDate)
        	$scope.RegionController_getORG($scope.inputVO);
        };
        
		
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList2 = [];
            $scope.paramList = [];
            $scope.sumDUE_AMT=0;
            $scope.sumPRD_CNT=0;
            $scope.sumTOTAL_AMT=0;
            $scope.ya=0;$scope.ya1=0;
		};
		$scope.init();
		
		//***合計function***//
    	$scope.getSum = function(group, key) {
              var sum = 0;
              for (var i = 0; i < group.length; i++){
               sum += group[i][key];
              }  
              return sum;
         }
    	
    	//***主查詢***
		$scope.inquire = function(){
//			debugger
			if($scope.parameterTypeEditForm.$invalid) {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:檢核欄位年月必要輸入');
        		return;
        	}
			console.log($scope.inputVO)
			$scope.sendRecv("PMS104", "inquire", "com.systex.jbranch.app.server.fps.pms104.PMS104InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							$scope.ya = tota[0].body.currentPageIndex;
							$scope.ya1 = tota[0].body.totalPage;

							angular.forEach($scope.paramList, function(row, index, objs){
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							
							
							for(var i = 0; i < $scope.paramList.length; i++){
								$scope.sumDUE_AMT += $scope.paramList[i].DUE_AMT;
							}
							for(var i = 0; i < $scope.paramList.length; i++){
								$scope.sumPRD_CNT += $scope.paramList[i].PRD_CNT;
							}
							for(var i = 0; i < $scope.paramList.length; i++){
								$scope.sumTOTAL_AMT += $scope.paramList[i].TOTAL_AMT;
							}
							
						
							//拿總計
							$scope.sendRecv("PMS104", "inquire2", "com.systex.jbranch.app.server.fps.pms104.PMS104InputVO",$scope.inputVO ,
									function(tota, isError) {
										if (!isError) {
											$scope.paramList2 = tota[0].body.resultList2;
										}
							});

							
							
							return;
						}
			});
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("PMS104", "deleteGroup", "com.systex.jbranch.app.server.fps.pms104.PMS104InputVO", {'group_id': row.GROUP_ID},
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg('刪除成功');
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
		
		//承作商品筆數
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS104/PMS104_DETAIL.html',
				className: 'PMS104_DETAIL',
				showClose: false,
				
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
		};

		
		
		
});
