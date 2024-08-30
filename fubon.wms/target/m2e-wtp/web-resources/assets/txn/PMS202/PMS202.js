/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS202Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS202Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		//filter
		getParameter.XML(["PMS.COACHING_STATE"], function(totas) {
			if (totas) {				
				$scope.mappingSet['PMS.COACHING_STATE'] = totas.data[totas.key.indexOf('PMS.COACHING_STATE')];
			}
		});
		
		/*** 可視範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
        	//可視範圍  觸發 
        	if($scope.inputVO.sCreDate!=''){
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
        
		$scope.curDate = new Date();
		
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		/*** 可視範圍  JACKY共用版  END***/
		
		// filter
		
		$scope.init = function(){
			$scope.AO_TYPE = '1';
			$scope.inputVO = {
					ao_code	:'',
					branch_nbr	:'',
					region	:'',
					branch_area_id	:'',
					aojob	:'',
					type	:'',
        			camId	:'',
        			camName	:'',
        			eTime	:'',
        			sCreDate:''
        	};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
			$scope.paramList = [];
		};
        $scope.init();
        
    	 /***設時間資料***/
    	$scope.isq = function(){
    		 $scope.sendRecv("PMS202", "weekly", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", {},
 					function(tota, isError) {
 	                	if (isError) {
 	                		$scope.showErrorMsg(totas[0].body.msgData);
 	                	}
 	                	if (tota.length > 0) {
 	                		$scope.mappingSet['timeE'] = []; 	
 	                		var old='',count=1;
 	                		angular.forEach(tota[0].body.resultList, function(row, index, objs){
 	                			$scope.mappingSet['timeE'].push({LABEL: row.DATA_DATE, DATA:row.WEEK_START_DATE });
 	            			});
 	                	
 	                	};
 					}
 			);
    		  
            $scope.mappingSet['aojob']=[];
            $scope.mappingSet['type']=[];
            $scope.mappingSet['type'].push({LABEL : '完成',DATA :'1'},{LABEL : '未完成',DATA :'0'});    	
    	};
        $scope.isq();
        
        $scope.getAojob = function(){
        	if($scope.inputVO.ao_code != ''){
        		$scope.sendRecv("PMS202" , "aoCode" , "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO" , $scope.inputVO , 
        				function(tota , isError){
        			if(!isError){
        				if(tota[0].body.aolist.length != 0){
        					if(tota[0].body.aolist[0].AO_JOB_RANK.includes('FCH'))
            					$scope.inputVO.aojob = 'FCH';
            				else
            					$scope.inputVO.aojob = tota[0].body.aolist[0].AO_JOB_RANK;
        				}else{
        					$scope.inputVO.aojob = '';
        					$scope.showMsg("查無理專職級");
        				}
        			}
        		});
        	}
        };
        
        //彈跳視窗
        $scope.updates = function(row){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS202/PMS202_UPDATE.html',
        		className: 'PMS202_UPDATE',
        		controller: ['$scope', function($scope) {
        			$scope.row = row;
        		}]
             });
             dialog.closePromise.then(function (data) {         	
            	 if(data.value === 'successful'){
            		 $scope.inquire();
                 }
             });
        }
        
  
        /***查詢***/
        $scope.inquire = function(){
        	$scope.sendRecv("PMS202", "queryData", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", $scope.inputVO,
        		function(tota, isError) {
				if (!isError) {
					if (tota[0].body.resultList.length == 0) {
						$scope.paramList =[];
						$scope.showMsg("ehl_01_common_009");
						return;
					}
					$scope.paramList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
					return;
				}
			});
		};
	
        $scope.edit = function () {
        	$scope.sendRecv("PMS202", "ddlModify", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('新增成功');
    	                	};
							return;
						}
			});
        }
		
        
        
        //#0002649 : 理專職級連動AO_CODE
        $scope.AO_LIST_change = function () {
        	$scope.NEW_AO_LIST = [];
        	$scope.JOBList = [];
        	$scope.sendRecv("PMS202", "JOB_change", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", $scope.inputVO,
				function(tota, isError) {
        			if(!isError){
        				if(tota[0].body.resultList != []){
        					$scope.JOBList = tota[0].body.resultList;
            				for(var i = 0; i < JSON.stringify($scope.JOBList.length); i++){
            					for(var j = 0; j < JSON.stringify($scope.AO_LIST.length); j++){
            						if($scope.JOBList[i].AO_CODE == $scope.AO_LIST[j].DATA){
            							$scope.NEW_AO_LIST.push($scope.AO_LIST[j]);
                					}
            					}
            	 			}
            				$scope.AO_TYPE = '2';
        				}else{
        					$scope.AO_TYPE = '1';
        				}
        			}
        			
        	});
        }
});
