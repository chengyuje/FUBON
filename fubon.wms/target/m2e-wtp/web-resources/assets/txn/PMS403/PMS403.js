/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS403Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS403Controller";
		$controller('PMSRegionController', {$scope: $scope});
		var NowDate = new Date();
		
		getParameter.XML(["PMS.INVESTMENT_TYPE"],function(totas){
		if (totas) {
		$scope.mappingSet['INVESTMENT_TYPE']=totas.data[totas.key.indexOf('PMS.INVESTMENT_TYPE')];
    	   
		
		   }
    	});	
		getParameter.XML(["PMS.TRAN_TYPE"],function(totas){
			if (totas) {
			$scope.mappingSet['TRAN_TYPE']=totas.data[totas.key.indexOf('PMS.TRAN_TYPE')];
			
			   }
	    	});		
			
		getParameter.XML(["PMS.PRD_TYPE_MAPPING"],function(totas){
			if (totas) {
			$scope.mappingSet['PRD_TYPE_MAPPING']=totas.data[totas.key.indexOf('PMS.PRD_TYPE_MAPPING')];
		
			   }
	    	});	
		
		var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
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
        
        /***TEST ORG COMBOBOX START***/
        var org = [];
       
        //區域中心
        $scope.Region=function(){
        	$scope.inputVO.rc_id=$scope.inputVO.region_center_id;
        }
        //營運區
       $scope.Area=function(){
    	   $scope.inputVO.op_id =$scope.inputVO.branch_area_id; 
       } 
        //分行
        $scope.Branch=function(){
        	$scope.inputVO.br_id =$scope.inputVO.branch_nbr;
        }
        //理專
        $scope.Ao_code=function(){
        	$scope.inputVO.emp_id =$scope.inputVO.ao_code;
        }
        
    
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	   if($scope.inputVO.sCreDate!=''){
        	   		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	
        	//alert($scope.inputVO.sCreDate);
       //	alert($scope.inputVO.reportDate);
        	
        	$scope.RegionController_getORG($scope.inputVO);
        	   }else
        	   {   
        		   $scope.inputVO.sCreDate='201701';
        		  
        		   $scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        		   $scope.RegionController_getORG($scope.inputVO);        
        		   $scope.inputVO.sCreDate='';
        	   } 
        	$scope.inputVO.dataMonth=$scope.inputVO.sCreDate; 
        	//alert($scope.inputVO.sCreDate)	
        	$scope.inputVO.rc_id = '';
        	$scope.inputVO.op_id = '';
        	$scope.inputVO.br_id = '';
        	$scope.inputVO.emp_id = '';
        }; 
        
        /***ORG COMBOBOX END***/
        
 
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		
		$scope.init = function(){
			$scope.inputVO = {					
					aoFlag           :'Y',
					psFlag           :'N',
					sTime            : '',
					
					
					INVESTMENT_TYPE:'',
					TRAN_TYPE:'',
					sCreDate:'',
					dataMonth: '',					
					region_center_id: '',
					branch_nbr: '' ,
					branch_area_id: '',
					emp_id: '',
					channel: '',
					invType: ''
        	};
			$scope.curDate = new Date();
			$scope.dateChange();
			$scope.showBtn = 'none';
		    
			$scope.sumFlag = false;
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			
			var vo = {'param_type': 'FUBONSYS.FC_ROLE', 'desc': false};
        	$scope.requestComboBox(vo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['FUBONSYS.FC_ROLE'] = totas[0].body.result;
        			for(var key in projInfoService.mappingSet['FUBONSYS.FC_ROLE']){
        	    		if(projInfoService.mappingSet['FUBONSYS.FC_ROLE'][key].DATA == projInfoService.getRoleID()){
        	    			$scope.inputVO.empHistFlag = 'Y';
        	    		}
        	    	}
        		}
	    	});
		
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.originalList = [];
		}
		$scope.inquireInit();
		
		$scope.query = function(){
			if($scope.inputVO.sCreDate=='') {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:日期必要輸入欄位');
        		return;
        	}
			
			$scope.sendRecv("PMS403", "queryData", "com.systex.jbranch.app.server.fps.pms403.PMS403InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.totalData =[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;
							getParameter.XML(["KYC.RISK_DESC"], function(totas) {
								if (totas) {				
									//alert(JSON.stringify( totas.data[totas.key.indexOf('KYC.RISK_DESC')]));
									
								}});
							
							
							angular.forEach($scope.paramList,function(row,index,objs){
								if(row.CUST_ID != null){
									row.CUST_ID=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10);    //隱藏身分證 四碼
									angular.forEach($scope.mappingSet['PRD_TYPE_MAPPING'],function(row1, index, objs){
										var split=row1.LABEL.split(',');
                                         
										for(var i=0;i<split.length;i++)
										{  
										    if(split[i]==row.PRD_TYPE.replace(/^\s*|\s*$/g,""))
											 row.PRD_TYPE=row1.DATA;
										}

									}); 
								
								}
							  row.STATUS=( row.STATUS=='N')?'未持有':'已持有';	  
						      /*暫時用*/ 
							  if(row.RISK_ATTR!=null)
							  {
								  switch(row.RISK_ATTR.toLowerCase()) 
								  {
							       case 'c1': row.RISK_ATTR='C1-保守型';break; 
							       case 'c2': row.RISK_ATTR='C2-穩健型'; break; 
							       case 'c3': row.RISK_ATTR='C3-成長型'; break;
							       default :  row.RISK_ATTR='C4-積極型';  break;
								  } 
							  }
							 
							
							  if(row.NOTE=='null')
								  row.NOTE='';
							  
							  
						  });  	
							
							
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;
							$scope.showBtn = 'block';
							return;
						}						
			});
		};
		
		$scope.save = function () {
			$scope.sendRecv("PMS403", "save", "com.systex.jbranch.app.server.fps.pms403.PMS403InputVO", {'list':$scope.paramList, 'list2':$scope.originalList},
					function(tota, isError) {
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		$scope.showMsg('儲存成功');
		            		$scope.query();
		            	};		
			});
		};
		
		$scope.exportRPT = function(){
			angular.forEach($scope.outputVO.totalList,function(row,index,objs) {
				angular.forEach($scope.mappingSet['PRD_TYPE_MAPPING'],function(row1, index, objs) {
					var split=row1.LABEL.split(',');
					for (var i=0;i<split.length;i++) {  
						if (split[i]==row.PRD_TYPE.replace(/^\s*|\s*$/g,"")) {
							row.PRD_TYPE=row1.DATA;
						}
					}
					
				}); 
				
		  });  
			
			$scope.sendRecv("PMS403", "export", "com.systex.jbranch.app.server.fps.pms403.PMS403OutputVO", $scope.outputVO,
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
