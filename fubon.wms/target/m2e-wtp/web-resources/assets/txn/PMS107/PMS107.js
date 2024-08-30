/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS107Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS107Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.init = function(){
			$scope.inputVO = {
					sTime :'',
					aocode  :'',
					branch  :'',
					region  :'',
					op      :'',
					sup:'',
					supb:'',
					radio:''    //radioo button 輔消人員
			};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList2 = [];
            $scope.paramList = [];
            $scope.disableRegionCombo = true;
			$scope.disableAreaCombo = true;
			$scope.disableBranchCombo = true;
			$scope.disableAoCombo = true;
			$scope.disablefaiaList = true;
		};
		$scope.init();
		
		$scope.initLoad = function(){
			$scope.sendRecv("PMS000", "getLastYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
					   function(totas, isError) {
				             	if (totas.length > 0) {
				               		$scope.ymList = totas[0].body.ymList;
//				               		console.log("$scope.ymList = "+JSON.stringify($scope.ymList));
				               	};
					   }
			);
		}
		$scope.initLoad();
		
		$scope.chk_roleID = function(){
			if(projInfoService.getRoleID() == 'IA' || projInfoService.getRoleID() =='FA')
				$scope.chk_role = true;
			if(projInfoService.getRoleID() == 'IA9' || projInfoService.getRoleID() =='FA9')
				$scope.chk_role = false;
		}
		$scope.chk_roleID();
		
		//切換選項
		$scope.radioChange = function(){
			if($scope.inputVO.radio == "1"){
				$scope.disableRegionCombo = true;
				$scope.disableAreaCombo = true;
				$scope.disableBranchCombo = true;
				$scope.disableAoCombo = true;
				$scope.disablefaiaList = false;
				$scope.inputVO.region_center_id = "";
				$scope.inputVO.branch_area_id = "";
				$scope.inputVO.branch_nbr = "";
				$scope.inputVO.ao_code = "";
			}else{
				$scope.disableRegionCombo = false;
				$scope.disableAreaCombo = false;
				$scope.disableBranchCombo = false;
				$scope.disableAoCombo = false;
				$scope.disablefaiaList = true;
				$scope.inputVO.faia = "";
			}
		}
		
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sTime;
        	if($scope.inputVO.sTime!=''){
        		$scope.sendRecv("PMS107", "getFAIA", "com.systex.jbranch.app.server.fps.pms107.PMS107InputVO", $scope.inputVO,
    					function(totas, isError) {
    		               	if (totas.length > 0) {
    		               		$scope.FAIA_LIST = totas[0].body.faiaList;
    		               		if($scope.FAIA_LIST.length==1)  //FA/IA長度為1時候放入單一值
    		               			$scope.inputVO.faia=$scope.FAIA_LIST[0].DATA;   //放入單一FA/IA
    		               	};
    					}
    			);
        		
        		$scope.RegionController_getORG($scope.inputVO).then(function(){
        			if($scope.inputVO.radio != "2"){
        				$scope.disableRegionCombo = true;
            			$scope.disableAreaCombo = true;
            			$scope.disableBranchCombo = true;
            			$scope.disableAoCombo = true;
        			}            		
            	});
        	}
        	var deferred = $q.defer();
        };
  
		
	    	$scope.export = function() {
				$scope.sendRecv("PMS107", "export",
						"com.systex.jbranch.app.server.fps.pms107.PMS107OutputVO",
						{'list':$scope.csvList}, function(tota, isError) {
							if (!isError) {
								$scope.paramList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
								return;
							}
				});
			};
		
		
	
		
		
		$scope.inquire = function(){
			if($scope.parameterTypeEditForm.$invalid){
				$scope.showErrorMsg('欄位檢核錯誤:月份為必要輸入欄位');
				return;
			}	
			$scope.sendRecv("PMS107", "inquire", "com.systex.jbranch.app.server.fps.pms107.PMS107InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.ROWNUM=index+1;
								row.IDS=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10);    //隱藏身分證 四碼
								
							});	
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
						
							return;
						}
			});
		};
							
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS107/PMS107_DETAIL.html',
				className: 'PMS107_DETAIL',
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
