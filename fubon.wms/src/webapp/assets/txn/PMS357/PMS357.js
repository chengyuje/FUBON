/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS357Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS357Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
        console.log(projInfoService.getRoleID() == 'B027');
        $scope.chk_role = projInfoService.getRoleID() == 'B027' || projInfoService.getRoleID() == 'B026' ? true : false
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dataMonthChange = function(){
        	if(!$scope.inputVO.sTime) 
        		return;
        	$scope.inputVO.reportDate = $scope.inputVO.sTime;
        	$scope.RegionController_getORG($scope.inputVO);
        };
        	
        
        /*有資料日期下拉選單查詢 */
        $scope.init1=function(){
        	var deferred=$q.defer();
        	$scope.paramList2=[];
        	$scope.sendRecv("PMS357", "date_query", "com.systex.jbranch.app.server.fps.pms357.PMS357InputVO",{},
        			function(tota, isError) {
        		if (!isError) {
        			if(tota[0].body.resultList.length == 0) {
        				$scope.showMsg("ehl_01_common_009");
        				return;
        			}
        			$scope.paramList2 = tota[0].body.resultList;
        			deferred.resolve();
        			return;
        		}						
  			});
        	return deferred.promise;  
        }
        
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
		
        $scope.init = function(){
        	$scope.mappingSet['timeE']=[];
        	angular.forEach($scope.paramList2,function(row, index, objs){
        		var sp=[]; 
        		sp[0]=row.YEARMON.substring(0,4);
        		sp[1]=row.YEARMON.substring(4,6);
        		
        		var time=new Date();
        		time.setFullYear(sp[0],sp[1]-1);
        		
        		$scope.mappingSet['timeE'].push(
        				{ 
        					LABEL:sp[0]+'/'+sp[1], 
        					DATA:row.YEARMON		   
        				});   				   
        	});
        	
        	$scope.inputVO = {					
        			dataMonth: '',					
        			region_center_id: '',
        			branch_area_id: '' ,
        			branch_nbr: '',
        			sTime  :'',
        			checked:'1'
        				
        	};
        	$scope.disableRegionCombo = false;
        	$scope.disableAreaCombo = false;
        	$scope.disableBranchCombo = false;
        	$scope.disableAoCombo = false;
        	$scope.curDate = new Date();
        	$scope.paramList = [];
//			$scope.genRegion();
        	$scope.csvList= [];
        	
        };
		$scope.init1().then(function(){$scope.init();});
			
		
        /**==類型年月==**/
        $scope.mappingSet['seture']=[];
        $scope.mappingSet['seture'].push({
			DATA : '1',
			LABEL : 'MTD'
		},{
			DATA : '2',
			LABEL : 'YTD'
		});
        
        
        
        
        
        /****upload****/
	    $scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.fileName = name;
            	$scope.inputVO.realfileName = rname;
        	}
        };
        
        
        
        /***以下連動區域中心.營運區.分行別***/
      
       
		$scope.inquireInit = function(){
		
			$scope.paramList = [];
			$scope.originalList = [];
		}
		$scope.inquireInit();
		
	/***new***/	
		$scope.numGroups = function(input) {
			var ans = $filter('groupBy')(input, 'BRANCH_AREA_NAME'); 
            return Object.keys(ans).length;
        };
         
           $scope.getSum = function(group, key) {
            var sum = 0;
            for (var i = 0; i < group.length; i++){
             sum += group[i][key];
            }  
            return sum;
           }
           
           
           
           /****檢視分行****/
           $scope.detail = function () {
   			var dialog = ngDialog.open({
   				template: 'assets/txn/PMS357/PMS357_DETAIL.html',
   				className: 'PMS357_DETAIL',
   				showClose: false,
                   controller: ['$scope', function($scope) {
                 
                   }]
   			});
   			dialog.closePromise.then(function (data) {
   				if(data.value === 'successful'){
   				
   					$scope.inquireInit();
   					$scope.inquire();
   				
   				}
   			});
   		};    
       
           
           
		
		
		
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid) {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:報表年月及查詢類型為必要輸入欄位');
        		return;
        	}
			
//			if($scope.rcList.length == 0){				
//				$scope.inputVO.emp_id = sysInfoService.getUserID();				
//			}
			
			$scope.sendRecv("PMS357", "inquire", "com.systex.jbranch.app.server.fps.pms357.PMS357InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								$scope.paramList = [];
								$scope.csvList= [];	
								$scope.outputVO = [];
	                			return;
	                		}							
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;	
							$scope.outputVO = tota[0].body;			
							$scope.curDate = tota[0].body.resultList[0].CREATETIME;

							return;
						}					
			});
		};
		
		$scope.runJob = function(){				
			$scope.sendRecv("PMS357", "runJob","com.systex.jbranch.app.server.fps.pms357.PMS357InputVO",$scope.inputVO, function(tota, isError) {
				   if (!isError) {					
						$scope.showMsg("報表資料已更新,請重新查詢");
						return;
					}
			});
		};
		
		$scope.btnNew = function(){				
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS357/PMS357_EDIT.html',
				className: 'PMS357_EDIT',
				controller:['$scope', function($scope){
					$scope.row = {};
				}]
			});
			dialog.closePromise.then(function(data) {				
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.query();						
				}
			});
		};
		
		
	//匯出exceil
    	$scope.export = function() {			
			$scope.sendRecv("PMS357", "export",
					"com.systex.jbranch.app.server.fps.pms357.PMS357OutputVO",
					{'list':$scope.csvList,'checked':$scope.inputVO.checked}, function(tota, isError) {
						if (!isError) {					
				
							return;
						}
					});
		};
		
	document.getElementById("wrap").addEventListener("scroll",function(){
		   var translate = "translate(0,"+this.scrollTop+"px)";
		   if(/MSIE 9/.test(navigator.userAgent))
		      this.querySelector("thead").style.msTransform = translate;
		   else
		      this.querySelector("thead").style.transform = translate;
		});
});
