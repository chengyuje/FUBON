/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS359Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS359Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		
		
		$scope.dateChange = function(){
			
			if($scope.inputVO.sCreDate!=undefined)
           {
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	$scope.RegionController_getORG($scope.inputVO);
           }
           };

        /***TEST ORG COMBOBOX START***/
 		  //舊版可視範圍start
//        var org = [];
//       
//        //區域中心資訊
//        $scope.genRegion = function(){        	
//        	$scope.sendRecv("PMS359", "getOrgInfo", "com.systex.jbranch.app.server.fps.pms359.PMS359InputVO", $scope.inputVO,
//    				function(totas, isError) {          	
//        				if(!isError){          					
//        					org = totas[0].body.orgList;  //全部權限組織資訊        					
//        					$scope.rcList = _.uniqBy(org, 'V_REGION_CENTER_ID');
//        					$scope.mappingSet['region'] = [];
//        					angular.forEach($scope.rcList, function(row, index, objs){
//        						$scope.mappingSet['region'].push({LABEL: row.V_REGION_CENTER_NAME, DATA: row.V_REGION_CENTER_ID});
//        						if(row.V_REGION_CENTER_ID == '0000000000')
//        							$scope.mappingSet['region'].splice(index,1);
//        					});    			
//        					if($scope.rcList.length == 1)
//        						$scope.inputVO.rc_id = $scope.rcList[0].V_REGION_CENTER_ID;
//        				}        				        		
//    		});        	
//        };
//      
//       //營運區資訊
//        $scope.genArea = function() {        	
//			$scope.inputVO.op_id = '';
//			$scope.mappingSet['op'] = [];
//			$scope.opList = _.uniqBy(org, 'V_BRANCH_AREA_ID');
//			angular.forEach($scope.opList, function(row, index, objs){				
//				if(row.V_REGION_CENTER_ID == $scope.inputVO.rc_id){
//					if(row.V_BRANCH_AREA_ID == '0000000000')
//						row.V_BRANCH_AREA_NAME = 'N/A';
//					$scope.mappingSet['op'].push({LABEL: row.V_BRANCH_AREA_NAME, DATA: row.V_BRANCH_AREA_ID});				
//					if(row.V_BRANCH_AREA_ID == '0000000000' && $scope.opList.length > 1)						
//							$scope.mappingSet['op'].splice(index,1);					
//				}
//			});
//			if($scope.opList.length == 1 && $scope.inputVO.rc_id != '')
//				$scope.inputVO.op_id = $scope.opList[0].V_BRANCH_AREA_ID;
//        };
//        
//        //分行資訊
//        $scope.genBranch = function() {
//			$scope.inputVO.br_id = '';
//			$scope.mappingSet['branch'] = [];
//			$scope.brList = _.uniqBy(org, 'V_BRANCH_NBR');
//			angular.forEach($scope.brList, function(row, index, objs){				
//				if(row.V_BRANCH_AREA_ID == $scope.inputVO.op_id){
//					if(row.V_BRANCH_NBR == '0000000000')
//						row.V_BRANCH_NAME = 'N/A';
//					$scope.mappingSet['branch'].push({LABEL: row.V_BRANCH_NAME, DATA: row.V_BRANCH_NBR});
//					if(row.V_BRANCH_NBR == '0000000000' && $scope.brList.length > 1)
//						$scope.mappingSet['branch'].splice(index,1);
//				}			
//			});
//			if($scope.brList.length == 1 && $scope.inputVO.op_id != '')
//				$scope.inputVO.br_id = $scope.brList[0].V_BRANCH_NBR;
//        };
//        
//        //理專資訊
//        $scope.branchChange = function() {
//        	$scope.inputVO.ao_code = '';
//    		$scope.mappingSet['aoemp'] = [];    		  		
//    		$scope.empList = _.uniqBy(org, 'V_AO_CODE');
//			angular.forEach($scope.empList, function(row, index, objs){
//				if(row.V_BRANCH_NBR == $scope.inputVO.br_id)
//					$scope.mappingSet['aoemp'].push({LABEL: row.V_AO_CODE + '-' +row.V_EMP_NAME, DATA: row.V_AO_CODE});	                 					                 					                 					                 			    					    				
//			});			
//			if($scope.empList.length == 1 && $scope.inputVO.br_id != '')
//				$scope.inputVO.ao_code = $scope.empList[0].V_AO_CODE;
//        };
//        
//        //選取月份下拉選單 --> 重新設定可視範圍
//        $scope.dateChange = function(){         	
//        	$scope.inputVO.rc_id = '';
//        	$scope.inputVO.op_id = '';
//        	$scope.inputVO.br_id = '';
//        	$scope.inputVO.ao_code = '';
//        	$scope.genRegion();
//        };
        
        /***ORG COMBOBOX END***/
 		//舊版可視範圍end
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		/*日期下拉選單查詢 0002259*/
		$scope.init1=function(){
		  var deferred=$q.defer();
		  $scope.paramList2=[];
		 
		  
		  $scope.sendRecv("PMS359", "date_query", "com.systex.jbranch.app.server.fps.pms359.PMS359InputVO",{},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList2 = tota[0].body.resultList;
//						alert(JSON.stringify($scope.paramList2))
						 
						   
							
							deferred.resolve();
							return;
						}						
			});
		 return deferred.promise;  
	  }	
		
		
	  var getMonthWeek = function (a, b, c) { 
		    var date = new Date(a, parseInt(b) - 1, c), w = date.getDay(), d = date.getDate(); 
		    return Math.ceil( (d + 6 - w) / 7 ); 
		   };
		
		$scope.init = function(){
			/*日期下拉選單查詢 0002259*/
			$scope.mappingSet['timeE']=[];
			  angular.forEach($scope.paramList2,function(row, index, objs){
				  var sp=[]; 
				  sp[0]=row.DATA_DATE.substring(0,4);
				  sp[1]=row.DATA_DATE.substring(4,6);
				  sp[2]=row.DATA_DATE.substring(6,8);
				 var e=getMonthWeek(sp[0],sp[1],sp[2]);
				  
				 var time=new Date();
				 time.setFullYear(sp[0],sp[1]-1,sp[2]);
				 var WeekFirstDay=new Date(time-(time.getDay()-1)*86400000);
				 var WeekLastDay=new Date((WeekFirstDay/1000+6*86400)*1000);
				 var days=WeekFirstDay.getDate();
				 days=(days<sp[2])?sp[2]:days;
				
				 
				 $scope.mappingSet['timeE'].push(
						   { 
							   LABEL:sp[0]+'/'+sp[1]+'/'+days+'第'+e+'週', 
							   DATA:row.DATA_DATE		   
						   });   				   
			   });	
			 
			   $scope.inputVO = {					
					sCreDate: undefined,					
					rc_id: '',
					op_id: '' ,
					br_id: '',					
					ao_code: ''
        	};
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			$scope.paramList=[];
//			$scope.genRegion();
		};
		$scope.init1().then(function(){$scope.init();});
		
		
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
		}
	
		
		   
//     // date picker
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
//		$scope.limitDate = function() {
//			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
//			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
//		};
		// date picker end
		
		/**表格合併儲存格 & 資料合計**/
		$scope.numGroups = function(input){
			if(input == null)
				return;
            return Object.keys(input).length;
        }         
        $scope.getSum = function(group, key) {
        	var sum = 0;
            for (var i = 0; i < group.length; i++){
            	sum += group[i][key];
            }  
            return sum;
        }
		
		/**查詢資料**/
		$scope.query = function(){
			if($scope.inputVO.reportDate == undefined){
				$scope.showMsg("資料日期不得為空白");
				return
			}
			$scope.sendRecv("PMS359", "queryData", "com.systex.jbranch.app.server.fps.pms359.PMS359InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.totalList.length == 0) {
								$scope.paramList = [];
								$scope.totalData = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.totalList;
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		/**匯出報表**/
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS359", "export", "com.systex.jbranch.app.server.fps.pms359.PMS359OutputVO", $scope.outputVO,
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
