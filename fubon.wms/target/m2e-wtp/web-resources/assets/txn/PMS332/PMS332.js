/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS332Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,getParameter,sysInfoService,$filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS332Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		//filter
		getParameter.XML(["CRM.VIP_DEGREE"], function(totas) {
			if (totas) {				
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
			}
		});
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
        var strmm='';
		$scope.mappingSet['timeE'] = [];
	        for(var i=0; i<36; i++){
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
	        
		var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        		}
        	});
        } else
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        
        
        /*** 可示範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
        	//可視範圍  觸發 
        	if($scope.inputVO.sCreDate!=''){
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
        
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		/*** 可示範圍  JACKY共用版  END***/
		
		$scope.init = function(){
			$scope.inputVO = {
					BT:'',
					VIP:'',
					aojob:'',
					sTime:'',
					ao_code:'',
					branch_nbr:'',
					region_center_id:'',
					branch_area_id:'',
					NNUM:'',
					sCreDate:''
			};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList = [];
		};
		$scope.init();
		
		//名次下拉選單
		$scope.inquireInit = function(){
				$scope.mappingSet['10']=[];
				for(var x=10;x<=100;x=x+10){
					$scope.mappingSet['10'].push({LABEL: x, DATA: x});
				}
			
		}
		$scope.inquireInit();
		
	    $scope.export = function() {
			$scope.sendRecv("PMS332", "export","com.systex.jbranch.app.server.fps.pms332.PMS332OutputVO", 
					{'list':$scope.csvList}, function(tota, isError) {
						if (!isError) {
//								$scope.paramList = tota[0].body.resultList;
//								$scope.outputVO = tota[0].body;
								return;
						}
			});
		};
		
		//查詢
		$scope.inquire = function(){
			if ($scope.inputVO.sCreDate=='') {
				$scope.showErrorMsgInDialog('欄位檢核錯誤："資料統計月份"為必要輸入欄位');
				return;
			}
			if($scope.inputVO.NNUM==''){
				$scope.showErrorMsgInDialog('欄位檢核錯誤："前N名"為必要輸入欄位')
				return;
			}
			if($scope.inputVO.BT==''){
				$scope.showErrorMsgInDialog('欄位檢核錯誤："排名依據"為必要輸入欄位')
				return;
			}
			$scope.sendRecv("PMS332", "inquire", "com.systex.jbranch.app.server.fps.pms332.PMS332InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.csvList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							for(var i = 0 ; i < $scope.paramList.length ; i++){
								//如果分行沒有代號，則分行名稱不顯示，否則正常顯示
								$scope.paramList[i].BRANCH_NBR = $scope.paramList[i].BRANCH_NAME == undefined ? "" : $scope.paramList[i].BRANCH_NBR;
								
								//如果沒有專員，則不顯示AO_CODE，否則正常顯示
								$scope.paramList[i].AO_CODE = $scope.paramList[i].EMP_NAME == undefined ? "" : $scope.paramList[i].AO_CODE;
								
								//如果手收貢獻率為無限，則顯示0.00%，否則正常顯示
								$scope.paramList[i].CRT = $scope.paramList[i].YTD_FEE/$scope.paramList[i].AUM == Infinity ? "0.00" : $scope.paramList[i].YTD_FEE / $scope.paramList[i].AUM
							}
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.paramList, function(row, index, objs){
								
								if(row.CUST_ID != null){
									row.CUST_ID=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10);    //隱藏身分證 四碼
								}
								
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							return;
						}
			});
		};
});
