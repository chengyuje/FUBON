/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS301Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS301Controller";
		$controller('PMSRegionController', {$scope: $scope});
		var Today=new Date();
		
		$scope.mappingSet['srchType'] = [];
        $scope.mappingSet['srchType'].push({LABEL:'當日', DATA:'NOW'}, {LABEL:'MTD月報', DATA:'MTD'});
        
        $scope.inputVO.srchType ='NOW';
        
        $scope.sendRecv("PMS301", "getYearMon", "com.systex.jbranch.app.server.fps.pms301.PMS301InputVO", {},
    		    function(totas, isError) {
    	             	if (totas.length > 0) {
    	             		$scope.ymList = totas[0].body.resultList;
    	             		debugger;
    	               	    //#0000375: 報表留存時間 四個月
							$scope.ymList.splice(4);
    	               	};
    		    }
        );	
        
//        var NowDate = new Date();
//	    var strMon='';
//	    NowDate.setMonth(NowDate.getMonth()-1); 
//	    $scope.mappingSet['timeE'] = [];
//	    //資料日期區間限制為半年內資料
//	    for(var i=1; i<=6; i++){
//	    	
//	    	strMon = NowDate.getMonth()+1;
//	    	//10月以下做文字處理，+0在前面
//	    	if(strMon < 10 ){
//	    		strMon = '0'+strMon;
//	    	}
//	    	
//	    	$scope.mappingSet['timeE'].push({
//	    		LABEL: NowDate.getFullYear()+'/'+strMon,
//	    		DATA: NowDate.getFullYear() +''+ strMon
//	    	}); 
//	    	//每一筆減一個月，倒回去取前六個月內日期區間
//	    	NowDate.setMonth(NowDate.getMonth()-1);
//	    }
		
	/*** 資料統計日期 ***/	
		$scope.init = function(){
			
			$scope.inputVO = {
					region_center_id  :'',   //區域中心
					branch_area_id  :'',	//營運區
					branch_nbr:'',			//分行
					ao_code  :'',			//理專
					sCreDate      :new Date(),
					srchType: 'NOW'
//					tx_ym : $scope.mappingSet['timeE'][0].DATA
			}
			$scope.startMaxDate = $scope.maxDate;
			$scope.csvList=[];
			$scope.TIME='';
           
		};
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.resultList = [];
		}
		$scope.inquireInit();
		
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	 if($scope.inputVO.sCreDate!='')
      	   { 	
        		$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
      	       	$scope.RegionController_getORG($scope.inputVO);
		      
      	   }
        };  
        $scope.dateChange ();
		
		//跳轉分行畫面
        $scope.bn=function(url,row) {
        	//存放前端row
        	var rows=row;
        	var date = $scope.inputVO.sCreDate;
        	var srchType = $scope.inputVO.srchType;
        	var tx_ym = $scope.inputVO.tx_ym;
        	var dialog=ngDialog.open({
        	    template:'assets/txn/PMS301/PMS301_detail.html',
        	    className:'PMS301_QUERY',     
        	    controller:['$scope',function($scope) {
        	    	$scope.sCreDate = date;
        	    	$scope.branch_nbr = url;
        	    	$scope.srchType = srchType;
        	    	$scope.tx_ym = tx_ym;
        	    	$scope.rows=rows;
        	    }]        	 
        	});
        };
        
        $scope.numGroups = function(input){
        	var i=0;    
            for(var key in input) i++;      
 			return i;
        }
        
          $scope.getSum = function(group, key) {
        	  var sum = 0;
              for (var i = 0; i < group.length; i++){
              	 if(group[i][key]!=null){
              		sum += group[i][key];
              	 }
               }  
              return sum;
          }
       
        //查詢結果
        $scope.inquire = function(){
        	
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
        	
			$scope.sendRecv("PMS301", "inquire", "com.systex.jbranch.app.server.fps.pms301.PMS301InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.resultList = [];
								$scope.csvList=[];
								$scope.sumAllList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}														
							$scope.resultList = tota[0].body.resultList;
							$scope.resultSumList = [];
							for (var i = $scope.resultList.length - 1; i >= 0; i--) {
								$scope.TIME=$scope.resultList[0].REPORT_TIME;
								if($scope.resultList[i].BRANCH_NBR=='區合計'){
									$scope.resultSumList.push($scope.resultList[i]);
									$scope.resultList.splice(i,1);
								}
							}
							
//							$scope.sumCol();
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;	
							$scope.TIME = tota[0].body.cList[0].CDATE;
							return;
						}else{
							$scope.showBtn = 'none';
					
						}						
			});
		};
       
		
		$scope.queryaodata = function(){
			$scope.sendRecv("PMS301", "queryDetail",	"com.systex.jbranch.app.server.fps.pms301.PMS301OutputVO",$scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = tota[0].body.resultList;
							
							
							return;
						}
					});
			};
		
		
		
		
		
		
		//匯出
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS301", "export",	"com.systex.jbranch.app.server.fps.pms301.PMS301InputVO",{'csvList': $scope.csvList, 'srchType': $scope.inputVO.srchType, 'tx_ym': $scope.inputVO.tx_ym},
				function(tota, isError) {
					if (!isError) {
						return;
					}
			});
		};
        
       
        /**** date picker ****/
        
        $scope.bgn_sDateOptions = {
          		maxDate: Today
    	};
	   
        //config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};  
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate =  Today;
			
		};
		
	    /**** date picker end ****/
		
		
		//計算  [營運區][區域中心] 合計
//		$scope.sumCol = function (collect,collect2) 
//		{
//			$scope.inputVO.sumCollect=[];
//			if(collect!=undefined)
//				$scope.inputVO.sumCollect.push({"BRANCH_AREA_NAME":collect,"REGION_CENTRER_NAME":collect});
//			if(collect2!=undefined)
//				$scope.inputVO.sumCollect.push({"BRANCH_AREA_NAME":collect2,"REGION_CENTRER_NAME":collect2});
//			$scope.sendRecv("PMS301", "queryAllSum", "com.systex.jbranch.app.server.fps.pms301.PMS301InputVO", $scope.inputVO,
//					function(tota, isError) 
//			{
//				if(!isError)
//				{
//					
//					//用來合計
//					$scope.sumAllList=tota[0].body.sumAllList;
////					$scope.DynamicForm();
//				}
//			
//			});
//		}
		
		//計算  [營運區][區域中心] 欄位顯示
		$scope.sumColnum = function (DEPT_NAME,col) 
		{	
			col=col.toUpperCase();
			var deptNameCol="";
			angular.forEach($scope.resultSumList, function(row, index, objs)
			{
				if(row.REGION_CENTER_ID=='全行總計' && DEPT_NAME=='ALL')
					deptNameCol=row[col];
				if(row.BRANCH_AREA_ID=='處合計' && DEPT_NAME==row.REGION_CENTER_NAME)
					deptNameCol=row[col];
				if(row.BRANCH_AREA_NAME==DEPT_NAME)
					deptNameCol=row[col];
//				//放入時間
//				if(row.REGION_CENTER_ID=='TIME' && !$scope.TIME)
//				{
//					//產出日重組
//					var INV_FEE=row.INV_FEE+"";
//					$scope.TIME=INV_FEE.substr(0,4)+"/"+(row.INV_FEE+"").substr(4,2)+"/"+(row.INV_FEE+"").substr(6,2);
//				}
				
			
			});
			return deptNameCol			
		}
		
        
});