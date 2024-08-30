/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS335Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog,$filter , projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS335Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.dateChange = function(){
	        if($scope.inputVO.sCreDate!=''){ 	
	        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };

	
		$scope.init = function(){
			$scope.inputVO = {
					sCreDate :'',
					sTime :'',
					aocode  :'',
					branch  :'',
					region  :'',
					op      :''
					
			};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList2 = [];
            $scope.paramList = [];
            $scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
		};
		$scope.init();
		
		$scope.isq = function(){
			
			var NowDate = new Date();
			var y = NowDate.getFullYear();
	        var m = NowDate.getMonth()+2;
	        var strmm='';
	        $scope.mappingSet['timeE'] = [];
	        for(var i=0; i<7; i++){
	        	m = m -1;
	        	if(m == 0){
	        		m = 12;
	        		y = y-1;
	        	}
	        	if(m<10)
	        		strmm = '0' + m;
	        	else
	        		strmm = m;        		
	        	$scope.mappingSet['timeE'].push({
	        		LABEL: y+'/'+strmm,
	        		DATA: y +''+ strmm
	        	});        
	        }  		        
		};
		
        $scope.isq();		
		
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
        
		 /***以下連動業務處.營運區.分行別***/
        
		//業務處資訊       
		$scope.genRegion = function() {
			$scope.inputVO.region='';
			$scope.mappingSet['region'] = [];
			angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){				
				$scope.mappingSet['region'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});						
			});
		};
		$scope.genRegion();
			
		//營運區資訊
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
       
       //理專員編
       $scope.bran = function(){ 		
	        $scope.sendRecv("PMS202", "aoCode", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", {},
					function(totas, isError) {
	        	
	                	if (isError)
	                		$scope.showErrorMsg(totas[0].body.msgData);	
	                	
	                	if (totas.length > 0) {
	                		$scope.mappingSet['aocode'] = [];
	                		angular.forEach(totas[0].body.aolist, function(row, index, objs){
	                			if(row.BRANCH_NBR==$scope.inputVO.branch)
	                			$scope.mappingSet['aocode'].push({LABEL: row.NAME, DATA: row.AO_CODE});
	                		});
	                	};
					});
	    }
   	
		
	   	$scope.export = function() {				
			$scope.sendRecv("PMS335", "export","com.systex.jbranch.app.server.fps.pms335.PMS335OutputVO",{'list':$scope.csvList}, 
				function(tota, isError) {
					if (!isError) {							
						$scope.paramList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;						
						return;
					}
				});
		};
	
		$scope.inquire = function(){
			//資料日期沒選 = undefined
			if($scope.inputVO.sCreDate == undefined) {
	    		$scope.showErrorMsg('欄位檢核錯誤:月份必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("PMS335", "inquire", "com.systex.jbranch.app.server.fps.pms335.PMS335InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.csvList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							
							//組產出日字串
							$scope.sendRecv("PMS335", "queryCDate", "com.systex.jbranch.app.server.fps.pms335.PMS335InputVO", $scope.inputVO,
									function(tota, isError) {
								//日期組字串
								if(tota[0].body.resultList[0].CREATDATE !=null){
									$scope.CREATE_DATE = tota[0].body.resultList[0].CREATDATE;				
									$scope.CREATE_DATE_YEAR = $scope.CREATE_DATE.substr(0,4);
									$scope.CREATE_DATE_MONTH = $scope.CREATE_DATE.substr(5,2);
									$scope.CREATE_DATE_DAY = $scope	.CREATE_DATE.substr(8,2);
									$scope.CREATE_DATE = $scope.CREATE_DATE_YEAR + "/" + $scope.CREATE_DATE_MONTH + "/" + $scope.CREATE_DATE_DAY;
								//該月沒建表才有可能發生的例外狀況
								}else{			
									$scope.CREATE_DATE ='';
								}	
							});	
							
							return;
						}
			});
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("PMS335", "deleteGroup", "com.systex.jbranch.app.server.fps.pms335.PMS335InputVO", {'group_id': row.GROUP_ID},
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg('刪除成功');
                                		$scope.inquireInit();
                                		$scope.inquire();
                                	};
                				}
                		);
					});
				} else
					$scope.edit(row);
				row.cmbAction = "";
			}
		};
		
	
		
		$scope.detail = function (row) {
			var s=$scope.inputVO;
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS335/PMS335_DETAIL.html',
				className: 'PMS335_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                    $scope.InputVO=s;
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
