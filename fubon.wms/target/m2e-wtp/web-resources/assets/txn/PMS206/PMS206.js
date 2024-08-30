/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS206Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS206Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		var c=0;
		// filter
	
		$scope.init = function(){
			$scope.last1='';
			$scope.last2='';
			$scope.now1='';
			$scope.now2='';
			$scope.last1w='';
			$scope.last2w='';
			$scope.now1w='';
			$scope.now2w='';
			$scope.nowdate='';
			$scope.w1='';
			$scope.w2='';
			$scope.w3='';
			$scope.flag=false;



			$scope.inputVO = {
        			camId: '',
        			camName: '',
        			MAIN_COM_NBR: '',
        			REL_COM_NBR: '',
        			eTime:'',
        			region_center_id  :'',   //區域中心
					branch_area_id  :'',	//營運區
					branch_nbr:'',			//分行
					ao_code  :'',			//理專
        			YEARMON:'',
        			AO_CODE:'',
        			WEEK_START_DATE:'',
        			EMP_ID:'',
        			VIP_DEGREE:'',        		
        			checked:'1',
        			WEEK:'',
        			MONTH:''
        	};
			$scope.paramList = [{AO_JOB_RANK:'FC'}];
			
			$scope.mappingSet['seture'] = [{DATA : '1',LABEL : '週報'},{DATA : '2',LABEL : '月報'}];
			
			$scope.inputVO.reportDate = $filter('date')(new Date(),'yyyyMMdd');
//			console.log("$scope.inputVO.reportDate = "+$scope.inputVO.reportDate);
        	$scope.RegionController_getORG($scope.inputVO);
		};
        $scope.init();
        $scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.paramList1 = [];
		}
		$scope.inquireInit();
        
        $scope.getReportDate = function(){
        	$scope.sendRecv("PMS206","getReportDate", "com.systex.jbranch.app.server.fps.pms206.PMS206InputVO",{},
					function(tota, isError) {
						if (!isError) {							
							$scope.weekList = tota[0].body.weekList;
							$scope.monthList = tota[0].body.meonthList;
							return;
						}
			});
        }
        $scope.getReportDate();
        
        $scope.pins= function(){
        	  $scope.init();
        };
            
                
        $scope.dateChange_month = function(){
        	if($scope.inputVO.checked == '1'){
        		var row_num = 0;
        		for(var i=0;i<$scope.weekList.length;i++){
        			row_num++;
        			if($scope.weekList[i].WEEK_START_DATE == $scope.inputVO.WEEK){
        				break;
        			}
        			
        		}
        		if( $scope.weekList[row_num]!=undefined){
        			$scope.w1 = $scope.weekList[row_num].WEEK_END_DATE;
        			$scope.inputVO.reportDate = $scope.inputVO.WEEK;
        		}
        	}else{
        		$scope.inputVO.reportDate = $scope.inputVO.MONTH;
        	}
        	$scope.RegionController_getORG($scope.inputVO);
        }
        
        $scope.inquire = function(){        	
        	if($scope.parameterTypeEditForm.$invalid) {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:區域中心、營運區、分行、理專、資料日期及檢視頻率為必要輸入欄位');
        		return;
        	}
        	var datasq="queryData";
        	if($scope.inputVO.checked=='1')
        	{
        		datasq="queryData";        		        		
        	}
        	if($scope.inputVO.checked=='2')
        	{
        		datasq="queryData2";        		        		
        	}
        	//alert(JSON.stringify($scope.inputVO));
        	$scope.sendRecv("PMS206",datasq, "com.systex.jbranch.app.server.fps.pms206.PMS206InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
//								$scope.init();
								$scope.showMsg("ehl_01_common_009");
//	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
						 // alert(JSON.Stringify(tota[0].body.resultList));
							if($scope.inputVO.checked=='1'){
								if($scope.paramList.length!=0){
									$scope.w1 = $scope.paramList[0].WEEK_START_DATE;
									$scope.w2 = $scope.paramList[0].WEEK_END_DATE;
									$scope.w3 = $scope.paramList[0].LA_WEEK_START_DATE;
									$scope.w4 = $scope.paramList[0].LA_WEEK_END_DATE;
									
								}
							}
							if($scope.inputVO.checked=='2'){
								if($scope.paramList.length!=0){
									$scope.m1 = $scope.paramList[0].MON_START_DATE;
									$scope.m2 = $scope.paramList[0].MON_END_DATE;
									$scope.m3 = $scope.paramList[0].LA_MON_START_DATE;
									$scope.m4 = $scope.paramList[0].LA_MON_END_DATE;
								}
								
							}
							$scope.outputVO= tota[0].body;
							return;
						}
			});
		};
	
  		
});
