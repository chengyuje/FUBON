/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS407Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS407Controller";	
		$controller('PMSRegionController', {$scope: $scope});
		var NowDate = new Date();
        
		
		var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
		//#0000375: 報表留存時間 四個月
		for(var i=0; i<4; i++){
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

        
        //選取月份下拉選單 --> 重新設定可視範圍
     
        $scope.dateChange = function(){
        	if($scope.inputVO.sCreDate!='') {
        		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        		$scope.RegionController_getORG($scope.inputVO);
        		
        	}
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
					
					region_center_id  :'',   //區域中心
					branch_area_id  :'',	//營運區
					branch_nbr:'',			//分行
					ao_code  :'',			//理專
					sCreDate      :''				
			};			 			
			$scope.curDate = new Date();			
			
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
		}
		$scope.inquireInit();
		
		/**查詢資料**/
		$scope.query = function(){	
			if($scope.inputVO.sCreDate=='') {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:檢核欄位年月必要輸入');
        		return;
        	}
		 	if($scope.inputVO.region_center_id=='') {
		 		$scope.showErrorMsgInDialog('欄位檢核錯誤:檢核欄位區域中心必要輸入');
        		return;	
			}
			
		 	if($scope.inputVO.branch_area_id==''){
		 		$scope.showErrorMsgInDialog('欄位檢核錯誤:檢核欄位營運區必要輸入');
        		return;
		 	}
		 	if($scope.inputVO.branch_nbr==''){
		 		$scope.showErrorMsgInDialog('欄位檢核錯誤:檢核欄位分行必要輸入');
        		return;
			}
			
			$scope.sendRecv("PMS407", "queryData", "com.systex.jbranch.app.server.fps.pms407.PMS407InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.totalData = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.totalData = tota[0].body.totalList;
  
//							var row=$scope.paramList[0].ROWNUM;
//							$scope.paramList[0].aa=1;//是否顯示表格與合併數
//							$scope.paramList[0].bb=row;//表格序號
//							if($scope.paramList.length>1){	 
//								/*表格合併處理*/
//								var old=-1,bool=false,count=1;
//								var index=0,tab=0 ;
//								for(;index<$scope.paramList.length-1;index++){	
//									$scope.paramList[index].bb=row;
//								    $scope.paramList[index].aa=1;	
//									/*需要修改區*/     
//								    var ch=($scope.paramList[index].REGION_CENTER_NAME ==$scope.paramList[index+1].REGION_CENTER_NAME)&&
//								    	   ($scope.paramList[index].BRANCH_AREA_NAME ==$scope.paramList[index+1].BRANCH_AREA_NAME)&&
//								    	   ($scope.paramList[index].BRANCH_NBR ==$scope.paramList[index+1].BRANCH_NBR)&&
//								    	   ($scope.paramList[index].BRANCH_NAME ==$scope.paramList[index+1].BRANCH_NAME)&&
//								    	   ($scope.paramList[index].AO_CODE ==$scope.paramList[index+1].AO_CODE)&&
//								    	   ($scope.paramList[index].CUST_ID ==$scope.paramList[index+1].CUST_ID)&&
//								    	   ($scope.paramList[index].CUST_NAME ==$scope.paramList[index+1].CUST_NAME)&&
//								    	   ($scope.paramList[index].FUND_VALU ==$scope.paramList[index+1].FUND_VALU)&&
//								    	   ($scope.paramList[index].FUND_COST ==$scope.paramList[index+1].FUND_COST)&&
//								    	   ($scope.paramList[index].FUND_LOSS_RATE ==$scope.paramList[index+1].FUND_LOSS_RATE)&&
//								    	   ($scope.paramList[index].ETF_VALU ==$scope.paramList[index+1].ETF_VALU)&&
//								    	   ($scope.paramList[index].ETF_COST ==$scope.paramList[index+1].ETF_COST)&&
//								    	   ($scope.paramList[index].ETF_LOSS_RATE ==$scope.paramList[index+1].ETF_LOSS_RATE)&&
//								    	   ($scope.paramList[index].STK_VALU ==$scope.paramList[index+1].STK_VALU)&&
//								    	   ($scope.paramList[index].STK_COST ==$scope.paramList[index+1].STK_COST)&&
//								    	   ($scope.paramList[index].STK_LOSS_RATE ==$scope.paramList[index+1].STK_LOSS_RATE)&&
//								    	   ($scope.paramList[index].INTERVIEW_YN ==$scope.paramList[index+1].INTERVIEW_YN);
//								    if((tab<9)&&(ch==true)){
//								    	
//								    	if(bool==true) {
//								    		$scope.paramList[index].aa=-1; 
//								    		count++;	 
//								    	}else{
//								    		old=index; 
//								    		count++; 	 
//								    		bool=true;
//								    	}	  
//								    	
//								    }else{
//								    	if(old!=-1){
//								    		$scope.paramList[old].aa=count;
//								    		$scope.paramList[old].bb=row;
//								    		$scope.paramList[index].aa=-1; 
//								    		
//								    		old=-1;
//								    		count=1;
//								    		bool=false;
//								    	} 								           
//								    	row++;
//								    }	  
//									/*分頁處理*/
//									if(tab==9)    
//										tab=0;   
//									else		  
//										tab++;
//								}
//								     
//								/*最後一筆處理*/
//								
//								if(old!=-1)
//									$scope.paramList[old].aa=count;
//								
//								var ch=($scope.paramList[index-1].REGION_CENTER_NAME ==$scope.paramList[index].REGION_CENTER_NAME)&&
//									   ($scope.paramList[index-1].BRANCH_AREA_NAME ==$scope.paramList[index].BRANCH_AREA_NAME)&&
//									   ($scope.paramList[index-1].BRANCH_NBR ==$scope.paramList[index].BRANCH_NBR)&&
//									   ($scope.paramList[index-1].BRANCH_NAME ==$scope.paramList[index].BRANCH_NAME)&&
//									   ($scope.paramList[index-1].AO_CODE ==$scope.paramList[index].AO_CODE)&&
//									   ($scope.paramList[index-1].CUST_ID ==$scope.paramList[index].CUST_ID)&&
//									   ($scope.paramList[index-1].CUST_NAME ==$scope.paramList[index].CUST_NAME)&&
//									   ($scope.paramList[index-1].FUND_VALU ==$scope.paramList[index].FUND_VALU)&&
//									   ($scope.paramList[index-1].FUND_COST ==$scope.paramList[index].FUND_COST)&&
//									   ($scope.paramList[index-1].FUND_LOSS_RATE ==$scope.paramList[index].FUND_LOSS_RATE)&&
//									   ($scope.paramList[index-1].ETF_VALU ==$scope.paramList[index].ETF_VALU)&&
//									   ($scope.paramList[index-1].ETF_COST ==$scope.paramList[index].ETF_COST)&&
//									   ($scope.paramList[index-1].ETF_LOSS_RATE ==$scope.paramList[index].ETF_LOSS_RATE)&&
//									   ($scope.paramList[index-1].STK_VALU ==$scope.paramList[index].STK_VALU)&&
//									   ($scope.paramList[index-1].STK_COST ==$scope.paramList[index].STK_COST)&&
//									   ($scope.paramList[index-1].STK_LOSS_RATE ==$scope.paramList[index].STK_LOSS_RATE)&&
//									   ($scope.paramList[index-1].INTERVIEW_YN ==$scope.paramList[index].INTERVIEW_YN); 
//								
//								
//								if(ch==true) {  
//									$scope.paramList[index].aa=-1;    
//								}else{
//									$scope.paramList[index].aa=1;
//									$scope.paramList[index].bb=row;
//								}
//							}	      	
//							console.log($scope.paramList);
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		/**匯出csv**/
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS407", "export", "com.systex.jbranch.app.server.fps.pms407.PMS407OutputVO",$scope.outputVO,
					function(tota, isError) {						
				if (isError) {
					$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
				}
				if (tota.length > 0) {
					if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
						return;
					}
				}
			});
		};
		
		/**訊息查詢、上傳**/
		$scope.upload = function(ym){
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS407/PMS407_UPLOAD.html',
				className: 'PMS407_UPLOAD',				
                controller: ['$scope', function($scope) {                	
                	$scope.ym = ym;
                }]
            });
		};
		
		/**商品類別：代碼 -> 名稱**/
		var vo = {'param_type': 'PMS.PRD_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['PMS.PRD_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['PMS.PRD_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['PMS.PRD_TYPE'] = projInfoService.mappingSet['PMS.PRD_TYPE'];        		
        		}
        	});
        } else
        	$scope.mappingSet['PMS.PRD_TYPE'] = projInfoService.mappingSet['PMS.PRD_TYPE'];
        
        /**訊息類別：代碼 -> 名稱**/
        var vo = {'param_type': 'PMS.INFO_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['PMS.INFO_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['PMS.INFO_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['PMS.INFO_TYPE'] = projInfoService.mappingSet['PMS.INFO_TYPE'];        		
        		}
        	});
        } else
        	$scope.mappingSet['PMS.INFO_TYPE'] = projInfoService.mappingSet['PMS.INFO_TYPE'];
		
});
