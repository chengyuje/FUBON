/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT360Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT360Controller";
	
		$scope.init = function(){
			$scope.branch_lock = false;
			var Today=new Date("2016/06/01");
			$scope.inputVO = {
				
					INS_ID           :'',
					KEYIN_DATE_FROM  :undefined,
					KEYIN_DATE_TO    :undefined,
					BRANCH_NBR       :'',
					CUST_ID          :'',
					INSURED_ID       :'',
					APPLY_DATE       :undefined,
					PPT_TYPE         :''

				
					
			};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList2 = [];
            $scope.paramList = [];
            
            
		};
		$scope.init();
		
		//險種下拉式選單
		$scope.PPT_TYPE = function(){
			$scope.sendRecv("IOT920","getPPTID","com.systex.jbranch.app.server.fps.iot920.PPTIDDataVO",
        			{},function(tota,isError){
					$scope.mappingSet['PPT_TYPE']=[];
						angular.forEach(tota[0].body.PPTIDData, function(row, index, objs){
							$scope.mappingSet['PPT_TYPE'].push({DATA: row.INSPRD_ID, LABEL: row.INSPRD_NAME});
						});
				});
		}
		$scope.PPT_TYPE();
		
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.originalList = [];
		}
		$scope.inquireInit();	
		
		//清除
		$scope.clear = function(){
			$scope.inputVO.INS_ID='';
			$scope.inputVO.KEYIN_DATE_FROM=undefined;
			$scope.inputVO.KEYIN_DATE_TO=undefined;
			$scope.inputVO.CUST_ID='';
			$scope.inputVO.INSURED_ID='';
			$scope.inputVO.APPLY_DATE=undefined;
			$scope.inputVO.PPT_TYPE='';
			if(projInfoService.getAvailBranch().length>1){
				$scope.inputVO.BRANCH_NBR = '';
			}
			$scope.limitDate();
			$scope.limitDate2();
		}
		
		  var vo = {'param_type': 'IOT.MAIN_STATUS', 'desc': false};
	        if(!projInfoService.mappingSet['IOT.MAIN_STATUS']) {
	        	$scope.requestComboBox(vo, function(totas) {
	        		if (totas[totas.length - 1].body.result === 'success') {
	        			projInfoService.mappingSet['IOT.MAIN_STATUS'] = totas[0].body.result;
	        			$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];
	        		}
	        	});
	        } else {
	        	$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];
	        }
	        
	        
	        //IOT.PPT_TYPE  險種
	    	var vo = {'param_type': 'IOT.PPT_TYPE', 'desc': false};
	        if(!projInfoService.mappingSet['IOT.PPT_TYPE']) {
	        	$scope.requestComboBox(vo, function(totas) {
	        		if (totas[totas.length - 1].body.result === 'success') {
	        			projInfoService.mappingSet['IOT.PPT_TYPE'] = totas[0].body.result;
	        			$scope.mappingSet['IOT.PPT_TYPE'] = projInfoService.mappingSet['IOT.PPT_TYPE'];
	        		}
	        	});
	        } else {
	        	$scope.mappingSet['IOT.PPT_TYPE'] = projInfoService.mappingSet['IOT.PPT_TYPE'];
	        }
		
	
		$scope.bgn_sDateOptions2 = {
				maxDate: $scope.maxDate2,
				minDate: $scope.minDate2
			};
			$scope.bgn_eDateOptions2 = {
					maxDate: $scope.maxDate2,
					minDate: $scope.minDate2
				};
		$scope.bgn_sDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
			};
			$scope.bgn_eDateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
				};
			// config
			$scope.model = {};
			$scope.open = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();
				$scope.model[elementOpened] = !$scope.model[elementOpened];
			};
			
			$scope.open2 = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();
				$scope.model[elementOpened] = !$scope.model[elementOpened];
			};
			$scope.limitDate = function() {
				$scope.bgn_sDateOptions.maxDate = $scope.inputVO.KEYIN_DATE_TO || $scope.maxDate;
				$scope.bgn_eDateOptions.minDate = $scope.inputVO.KEYIN_DATE_FROM || $scope.minDate;
			};
		
			$scope.limitDate2 = function() {
				$scope.bgn_sDateOptions2.maxDate = $scope.inputVO.KEYIN_DATE_TO2 || $scope.maxDate2;
				$scope.bgn_eDateOptions2.minDate = $scope.inputVO.APPLY_DATE || $scope.minDate2;
			};
		
		
			
		
	$scope.model = {};
	    	$scope.open = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();
				$scope.model[elementOpened] = !$scope.model[elementOpened];
			};
	        
	
		
		    	
	     /***以下連動區域中心.營運區.分行別***/
			//大區域資訊
	        
		
			//分行資訊
			$scope.genBranch = function() {
				$scope.inputVO.branch='';
				$scope.mappingSet['branch'] = [];
				angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){					
						$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						if(projInfoService.getAvailBranch().length<=1){
							$scope.inputVO.BRANCH_NBR=row.BRANCH_NBR;
							$scope.branch_lock = true;
						}
				});				
	        };
	        $scope.genBranch();
	        //理專員邊
	    	$scope.bran = function(){
	    		
			        $scope.sendRecv("PMS202", "aoCode", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", {},
							function(totas, isError) {		        	
			                	if (isError) {
			                		$scope.showErrorMsg(totas[0].body.msgData);
			                		
			                	}
			                	if (totas.length > 0) {
			                		$scope.mappingSet['aocode'] = [];
			                		angular.forEach(totas[0].body.aolist, function(row, index, objs){
			                			if(row.BRANCH_NBR==$scope.inputVO.branch)
			                			$scope.mappingSet['aocode'].push({LABEL: row.NAME, DATA: row.EMP_ID});
			            			});
			                	};
							}
					);
		    	}
    
	    /***以下連動區域中心.營運區.分行別***/
			//大區域資訊
	        
			$scope.genRegion = function() {
				$scope.inputVO.region='';
				$scope.mappingSet['region'] = [];
				angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){
						
						$scope.mappingSet['region'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
				
						
				});
				        };
	        $scope.genRegion();
			
			
			//區域資訊
			$scope.genArea = function() {
				$scope.inputVO.op='';
				$scope.mappingSet['op'] = [];
				angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){
						if(row.REGION_CENTER_ID == $scope.inputVO.region)	
						$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						
				});
				
				
	        };	      
			
			//分行資訊
			$scope.genBranch = function() {
				$scope.inputVO.branch='';
				$scope.mappingSet['branch'] = [];
				angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){					
					if(row.BRANCH_AREA_ID == $scope.inputVO.op)					
						$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
				});				
	        };
	        //理專員邊
	    	$scope.bran = function(){
		        $scope.sendRecv("PMS202", "aoCode", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", {},
						function(totas, isError) {
		        	
		                	if (isError) {
		                		$scope.showErrorMsg(totas[0].body.msgData);
		                		
		                	}
		                	if (totas.length > 0) {
		                		$scope.mappingSet['aocode'] = [];
		                		angular.forEach(totas[0].body.aolist, function(row, index, objs){
		                			if(row.BRANCH_NBR==$scope.inputVO.branch)
		                			$scope.mappingSet['aocode'].push({LABEL: row.NAME, DATA: row.EMP_ID});
		            			});
		                	};
						}
				);
		    }

	 
	        
		
	    	$scope.export = function() {
				
				$scope.sendRecv("IOT360", "export",
						"com.systex.jbranch.app.server.fps.iot360.IOT360OutputVO",
						{'list':$scope.csvList}, function(tota, isError) {
							if (!isError) {							
								$scope.paramList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;							
								return;
							}
						});
			};
		
		
	
			
			
			$scope.inquire = function(){
				$scope.sendRecv("IOT360", "inquire", "com.systex.jbranch.app.server.fps.iot360.IOT360InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length>0){
									$scope.paramList = tota[0].body.resultList;
									$scope.csvList=tota[0].body.csvList;
									$scope.outputVO = tota[0].body;
									return;
								}else{
									$scope.showMsg('ehl_01_common_009');
									$scope.paramList = [];
									$scope.csvList = [];
									$scope.outputVO = [];
								}
							}
				});
			};
			
	
		$scope.chnum = function (ind) {		
			$scope.mappingSet[ind.leng]=0;		
			$scope.mappingSet[ind.leng]=ind.leng;
		};
		
	
	
		
		
		
});
