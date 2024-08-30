/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS345Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS345Controller";
		$controller('PMSRegionController', {$scope: $scope});
		$scope.init = function(){
			var Today=new Date("2016/06/01");
			$scope.inputVO = {
					aoFlag           :'Y',
					psFlag           :'N',
					sTime            : '',
					
					
					sCreDate: undefined,
					eCreDate: undefined,
					sCreDate2: undefined,
					eCreDate2: undefined,
					num:'',
					id:'',
					type:'',
					clas:'',
					aocode  :'',
					branch  :'',
					region  :'',
					op      :'',
					
			};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList2 = [];
            $scope.paramList = [];
            $scope.chang=[];
            
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
		var vo = {'param_type': 'PMS.NOTE_TYPE', 'desc': false};
		if(!projInfoService.mappingSet['PMS.NOTE_TYPE']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['PMS.NOTE_TYPE'] = totas[0].body.result;
	    			$scope.mappingSet['PMS.NOTE_TYPE'] = projInfoService.mappingSet['PMS.NOTE_TYPE'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['PMS.NOTE_TYPE'] = projInfoService.mappingSet['PMS.NOTE_TYPE'];
	    }
		
		var vo = {'param_type': 'PMS.NOTE_STATUS', 'desc': false};
		if(!projInfoService.mappingSet['PMS.NOTE_STATUS']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['PMS.NOTE_STATUS'] = totas[0].body.result;
	    			$scope.mappingSet['PMS.NOTE_STATUS'] = projInfoService.mappingSet['PMS.NOTE_STATUS'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['PMS.NOTE_STATUS'] = projInfoService.mappingSet['PMS.NOTE_STATUS'];
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
				
				$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
				$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
				
				
//				if($scope.inputVO.eCreDate.getTime()==$scope.inputVO.sCreDate.getTime())
//					$scope.inputVO.eCreDate=undefined;
			 
			};
		
			$scope.limitDate2 = function() {
				$scope.bgn_sDateOptions2.maxDate = $scope.inputVO.eCreDate2 || $scope.maxDate2;
				$scope.bgn_eDateOptions2.minDate = $scope.inputVO.sCreDate2 || $scope.minDate2;
//				if($scope.inputVO.eCreDate2.getTime()==$scope.inputVO.sCreDate2.getTime())
//					$scope.inputVO.eCreDate2=undefined;
			
			};
		
		
		
		
		
		
	$scope.model = {};
	    	$scope.open = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();
				$scope.model[elementOpened] = !$scope.model[elementOpened];
			};
	        
	
	
		
//	$scope.isq = function(){
//    	    
//            var NowDate=new Date();               
//            var t=NowDate.setMonth(NowDate.getMonth()-6);           
//            var susdate=new Date(t);
//            var y=susdate.getFullYear();
//            var m=susdate.getMonth()+1;
//            var xm='';
//            $scope.mappingSet['timeE'] = [];
//            for(var i=0;i<7;i++){
//            	if(m<=9){xm='0'+m;}
//            	if(m>=10){xm=m;}            	
//            		$scope.mappingSet['timeE'].push({
//    					LABEL : y+'/'+xm,
//    					DATA : y+''+xm
//            		});            		
//            		if(m<=11)
//            			{	
//            			m=m+1;               		
//            			}
//            		if(m==12)
//            			{
//            			m=1;
//            			y=y+1;            		
//            			}
//            
//            }
//        	
//        
//		};
//        $scope.isq();
        
        
        $scope.mappingSet['type']=[];
        $scope.mappingSet['type'].push({
			LABEL : '完成',
			DATA :'1'
		},{
			LABEL : '未完成',
			DATA :'0'
		});
        
        
        $scope.mappingSet['class']=[];
        $scope.mappingSet['class'].push({
			LABEL : '完成',
			DATA :'1'
		},{
			LABEL : '未完成',
			DATA :'0'
		});
     
      //保單號碼下跳
        $scope.sizelimit1=function(){
             
        	var c=$scope.inputVO.POLICY_NO;
        	var e=c.match(/.*[A-Za-z0-9]/);
        	if(e!=c&&c!='')
           {
        		$scope.showMsg("只能輸入英文或數字");
            	$scope.inputVO.POLICY_NO='';
           }
        		
        		
        	if($scope.inputVO.POLICY_NO.length==10){
        		
        		
        	 document.getElementById('P1').focus();
         	 document.getElementById('P1').select();             
             }
         } 
         $scope.sizelimit2=function(){
        	 var c=$scope.inputVO.num;
         	var e=c.match(/.*[A-Za-z0-9]/);
         	if(e!=c&&c!='')
            {
         		$scope.showMsg("只能輸入英文或數字");
             	$scope.inputVO.num='';
            }
        	 if($scope.inputVO.num.length==2){
         		document.getElementById('P2').focus();
            	 document.getElementById('P2').select();
             	
         		
         	}
         }
      
         $scope.sizelimit3=function(){
        	 var c=$scope.inputVO.ID_DUP;
         	var e=c.match(/.*[A-Za-z0-9]/);
         	if(e!=c&&c!='')
            {
         		$scope.showMsg("只能輸入英文或數字");
             	$scope.inputVO.ID_DUP='';
            }
        	
         }
        
        
      //選取月份下拉選單 --> 重新設定可視範圍
 $scope.dateChange = function(){
        	
        	if($scope.inputVO.sCreDate!=''&&$scope.inputVO.sCreDate!=undefined)
    	   { 	
        		
        		
        		$scope.inputVO.reportDate =$filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
    	
    	//alert($scope.inputVO.sCreDate);
   
    	
    	$scope.RegionController_getORG($scope.inputVO);
    	
    	// alert($scope.AREA_LIST);  
    	   }else
    	   {   
    		   $scope.inputVO.sCreDate='201701';
    		  
    		   $scope.inputVO.reportDate = $scope.inputVO.sCreDate;
    		   $scope.RegionController_getORG($scope.inputVO);        
    		   $scope.inputVO.sCreDate=undefined; 
    	   } 
    	
    	//alert($scope.inputVO.sCreDate)	
    	$scope.inputVO.region = '';
    	$scope.inputVO.op = '';
    	$scope.inputVO.branch = '';
    	$scope.inputVO.ao_code = '';
    	//$scope.genRegion();
    }; 
		
    $scope.dateChange();	
        /***以下連動區域中心.營運區.分行別***/

     //區域中心資訊
    $scope.Region=function(){
    	$scope.inputVO.region=$scope.inputVO.region_center_id;
   
    
    }
  
   //營運區資訊
    $scope.Area=function(){
 	   $scope.inputVO.op =$scope.inputVO.branch_area_id; 
 	   
    } 
    
    //分行資訊
    $scope.Branch=function(){
    	$scope.inputVO.branch =$scope.inputVO.branch_nbr;
    	
    }
    //理專資訊
    $scope.Ao_code=function(){
    	//$scope.inputVO.aocode =$scope.inputVO.ao_code;
    	
    }
    
    
    
    //		//大區域資訊
//        
//		$scope.genRegion = function() {
//			$scope.inputVO.region='';
//			$scope.mappingSet['region'] = [];
//			angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){
//					
//					$scope.mappingSet['region'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
//			
//					
//			});
//			        };
//        $scope.genRegion();
//		
//		
//		//區域資訊
//		$scope.genArea = function() {
//			$scope.inputVO.op='';
//			$scope.mappingSet['op'] = [];
//			angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){
//					if(row.REGION_CENTER_ID == $scope.inputVO.region)	
//					$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
//					
//			});
//			
//			
//        };	      
//		
//		//分行資訊
//		$scope.genBranch = function() {
//			$scope.inputVO.branch='';
//			$scope.mappingSet['branch'] = [];
//			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){					
//				if(row.BRANCH_AREA_ID == $scope.inputVO.op)					
//					$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
//			});				
//        };
//        //理專員邊
//    	$scope.bran = function(){
//    		
//	        $scope.sendRecv("PMS202", "aoCode", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", {},
//					function(totas, isError) {
//	        	
//	                	if (isError) {
//	                		$scope.showErrorMsg(totas[0].body.msgData);
//	                		
//	                	}
//	                	if (totas.length > 0) {
//	                		$scope.mappingSet['aocode'] = [];
//	                		angular.forEach(totas[0].body.aolist, function(row, index, objs){
//	                			if(row.BRANCH_NBR==$scope.inputVO.branch)
//	                			$scope.mappingSet['aocode'].push({LABEL: row.NAME, DATA: row.EMP_ID});
//	            			});
//	                	};
//					}
//			);
//	    	}

    
    
    
					
			
			$scope.inquire = function(){
				
				if($scope.inputVO.sCreDate==undefined)
				{
					$scope.showMsg("照會日期為必填欄位");
					return;
				}
				
				
				$scope.sendRecv("PMS345", "inquire", "com.systex.jbranch.app.server.fps.pms345.PMS345InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.csvList.length == 0) {
									$scope.paramList = [];
									$scope.csvList=[];
									$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.paramList = tota[0].body.csvList;
								//問題單號0002089
								angular.forEach($scope.paramList,function(row,index,objs){
									   if( row.NOTE_URL == '無' || row.NOTE_URL == null ){
										   row.FF='1';
									   }
									});
//								alert($scope.paramList[0].CDATE);
								
						       //alert($scope.paramList[0].POLICY_NO_2)
								
						$scope.paramList[0].aa=1;
						if($scope.paramList.length>1)	
						{	 /*表格合併處理*/
							      var old=-1,bool=false,count=1;
							      var index=0,tab=0 ;
							      for(;index<$scope.paramList.length-1;index++)
								  {	
									 
							    	 
							    	  $scope.paramList[index].aa=1;	
								       /*需要修改區*/     
									  if((tab<8)&&($scope.paramList[index].POLICY_NO_2==$scope.paramList[index+1].POLICY_NO_2)&&($scope.paramList[index].NOTE_TYPE==$scope.paramList[index+1].NOTE_TYPE)&&($scope.paramList[index].REPLY_DATE==$scope.paramList[index+1].REPLY_DATE))
								    {
								  
										 // alert(index);
										  if(bool==true) 
								           {
								           	 
											  $scope.paramList[index].aa=-1; 
								               count++;	 
								           }else{
								    	      old=index; 
								              count++; 	 
								              bool=true;
								                }	  
								    	   
								    }else     
								    {
								          if(old!=-1)
								           {
								    	    $scope.paramList[old].aa=count;
								    	    $scope.paramList[index].aa=-1; 
								    	    old=-1;
								    	    count=0;
								    	    bool=false;
								           } 								           
								    }	  
								/*分頁處理*/
								if(tab==9)    
									tab=0;   
								 else		  
								 tab++;
								  }
							     /*最後一筆處理*/
							      if(old!=-1)
						            $scope.paramList[old].aa=count;

							      if(($scope.paramList[index-1].POLICY_NO_2==$scope.paramList[index].POLICY_NO_2)&&($scope.paramList[index-1].NOTE_TYPE==$scope.paramList[index].NOTE_TYPE)&&($scope.paramList[index-1].REPLY_DATE==$scope.paramList[index].REPLY_DATE))
							      $scope.paramList[index].aa=-1;    
							      else
							    $scope.paramList[index].aa=1;
							}	      
								      $scope.csvList=tota[0].body.csvList;
								  $scope.outputVO = tota[0].body;
						   
					    	//for(var i=0;i<$scope.paramList.length;++i)		
							//alert($scope.paramList[i].aa);	
					    		
					    		return;
							}
				});
			};
			
	
		$scope.chnum = function (ind) {
			$scope.mappingSet[ind.leng]=0;
			$scope.mappingSet[ind.leng]=ind.leng;
		};
		//問題單號0002089
		$scope.doc = function (a) {
			if(a==null || a=='無')
			$scope.showMsg("無照會單附件");
			else
			window.open(a);
		}
	$scope.export=function(){
		$scope.sendRecv("PMS345","export","com.systex.jbranch.app.server.fps.pms345.PMS345OutputVO",{'list':$scope.csvList},
		function(tota,isError){
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
		
		
	}	
		
		
});
