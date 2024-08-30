/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS336Controller',
	function($scope, sysInfoService,$controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS336Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
     var id=sysInfoService.getPriID();
    
     $scope.id=false;
     if(id=='013'||id=='015'||id=='014'||id=='023'||id=='024')
    	 $scope.id=true;
            
//        
//		$scope.open = function($event, index) {
//			$event.preventDefault();
//			$event.stopPropagation();
//			$scope['opened'+index] = true;
//		};				
   
	 $scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
	 }
	 $scope.inquireInit();
		
		//選取月份下拉選單 --> 重新設定可視範圍
     $scope.dateChange = function(){
        $scope.inputVO.reportDate = $scope.inputVO.sTime;
        if($scope.inputVO.sTime!=''){
        	$scope.RegionController_getORG($scope.inputVO);
        }
     };
     
   //2017/06/28 註解掉獨立的輔銷清單    由最新   共用可視範圍  撈區  對應可視清單  新增 FUNCTION
     $scope.getFAIA = function() {
    	//先清空
    	$scope.mappingSet['emp_id']=[];
    	//確定年月輸入資料
    	if($scope.inputVO.sTime!=''){
    		$scope.inputVO.reportDate=$scope.inputVO.sTime;
    		$scope.sendRecv("PMS107", "getFAIA", "com.systex.jbranch.app.server.fps.pms107.PMS107InputVO", $scope.inputVO,
    			function(totas, isError) {
    		          if (totas.length > 0) {
    		               	$scope.mappingSet['emp_id'] = totas[0].body.faiaList;
    		               	if($scope.mappingSet['emp_id'].length==1)  //FA/IA長度為1時候放入單一值
    		               		$scope.inputVO.emp_id=$scope.mappingSet['emp_id'][0].DATA;   //放入單一FA/IA
    		          };
    			}
    		); 
    	}
     }
     
     $scope.init = function(){
			$scope.inputVO = {					
					sTime: '',					
					ao_code  :'',        //理專
					branch_nbr  :'',        //分行
					region_center_id  :'',        //區域中心
					branch_area_id      :''        //營運區					
     	};
			$scope.curDate = new Date();
			$scope.dateChange();
			$scope.paramList = [];
			//把鎖定清掉
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			
			// 輔銷下拉選單用
			if (projInfoService.getRoleID() == "FA9" || projInfoService.getRoleID() == "IA9"){
				$scope.type = 1;
				$scope.remindList = [];		//輔銷提醒
				$scope.remindList2 = [];	//待辦事項
				$scope.remindList3 = [];	//陪訪紀錄
				//2017/06/28 註解掉獨立的輔銷清單    由最新   共用可視範圍  撈區  對應可視清單
				//輔銷人員選單
//				$scope.sendRecv("CRM124", "emp_inquire", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {},
//						function(tota, isError) {
//							if (!isError) {
//								var emp_id_list = [];
//								emp_id_list = tota[0].body.resultList;			
//								$scope.mappingSet['emp_id'] = [];
//								angular.forEach(emp_id_list, function(row, index, objs){				
//									$scope.mappingSet['emp_id'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
//								});	
//								return;
//							}
//				});
				//2017/06/28 註解掉獨立的輔銷清單    由最新   共用可視範圍  撈區  對應可視清單  新增 FUNCTION
				$scope.getFAIA();
			
			}
			
	 };
	 $scope.init();
        
	 
	 $scope.getSum = function(group, key) {
         var sum = 0;
         for (var i = 0; i < group.length; i++){
             {
         	 if(group[i][key]!=null)
         	 sum += group[i][key];
             }
          }  
         return sum;
        }
	 
//		// date picker
//		$scope.bgn_sDateOptions = {
//			maxDate: $scope.maxDate,
//			minDate: $scope.minDate
//		};
//		$scope.bgn_eDateOptions = {
//				maxDate: $scope.maxDate,
//				minDate: $scope.minDate
//			};
//		// config
//		$scope.model = {};
//		$scope.open = function($event, elementOpened) {
//			$event.preventDefault();
//			$event.stopPropagation();
//			$scope.model[elementOpened] = !$scope.model[elementOpened];
//		};
		
		
		$scope.initLoad = function(){
			$scope.sendRecv("PMS000", "getLastYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
				function(totas, isError) {
				       if (totas.length > 0) {
				           $scope.ymList = totas[0].body.ymList;
				    };
				}
			);
		}
		$scope.initLoad();
		
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid){				
	    		$scope.showErrorMsg('欄位檢核錯誤:資料月份為必輸條件');
        		return;
        	}
			$scope.sendRecv("PMS336", "queryData", "com.systex.jbranch.app.server.fps.pms336.PMS336InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS336", "export", "com.systex.jbranch.app.server.fps.pms336.PMS336OutputVO", $scope.outputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};		
});
