/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS349Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService,$filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS349Controller";	
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.init = function(){
			$scope.inputVO = {
					ao_code  :'',
					eCreDate :undefined,     //日期
					region_center_id  :'',   //區域中心
					branch_area_id  :'',     //營運區
					branch_nbr      :'',     //分行
					cust_id			:''		 //客戶ID	
			};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList2 = [];
            $scope.paramList = [];
            $scope.curDate = new Date();
		};
		$scope.init();
		//初始化
		
		 /*** 可示範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.eCreDate,'yyyyMMdd');
        	$scope.RegionController_getORG($scope.inputVO);
        };
        
		$scope.curDate = new Date();
		
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		/*** 可示範圍  JACKY共用版  END***/
		
		$scope.model = {};
	    $scope.open = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();
				$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		/****匯出****/
	    $scope.export = function() {
			$scope.sendRecv("PMS349", "export",
					"com.systex.jbranch.app.server.fps.pms349.PMS349OutputVO",
					{'list':$scope.csvList}, function(tota, isError) {
							if (!isError) {
								$scope.paramList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
								return;
							}
					});
		};
		
		/****主查詢****/
		$scope.inquire = function(){
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:日期必要輸入欄位');
        		return;
        	}
			$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();

			$scope.sendRecv("PMS349", "inquire", "com.systex.jbranch.app.server.fps.pms349.PMS349InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.csvList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList= tota[0].body.resultList;
							
							
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							return;
						}
			});
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("PMS349", "deleteGroup", "com.systex.jbranch.app.server.fps.PMS349.PMS349InputVO", {'group_id': row.GROUP_ID},
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
		
	
		/****明細資訊****/
		$scope.detail = function (row,set) {
			row.set=set;
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS349/PMS349_DETAIL.html',
				className: 'PMS349_DETAIL',
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
		
		$scope.bgn_eDateOptions = {
				maxDate: $scope.maxDate,
				//#0000375: 報表留存時間 三個月  實際操作是90天
				minDate: new Date(new Date().setDate(new Date().getDate() - 90))
			};
		
		
});


