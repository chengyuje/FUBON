/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS311Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS311Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
        /**報表類型下拉選單內容**/
        $scope.mappingSet['rptType'] = [];
        $scope.mappingSet['rptType'].push(
        		{LABEL: '房貸週報', DATA:'mortRPT'},        		
        		{LABEL: '信貸週報', DATA:'credRPT'}
        );
      //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	$scope.RegionController_getORG($scope.inputVO);
        };

		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		
		$scope.rptTypeChange=function(){
			$scope.paramList=[];
			$scope.outputVO={};
		};
		
		$scope.init = function(){
			//消金PS
			$scope.inputVO = {
					aoFlag           :'N',
					psFlag           :'Y',
			};
			//$scope.inputVO.rptType= 'credRPT'
			$scope.paramList = [];	
		};
		$scope.init();
		
		
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];	
		}
		$scope.inquireInit();	
//		$scope.toJsDate = new Date();
		// date picker
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
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = new Date();
		};
		// date picker end
		
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
		
        /**週報下拉式選單PART1**/
		$scope.init1=function(){
			if($scope.inputVO.rptType != undefined && $scope.inputVO.rptType != "" && $scope.inputVO.rptType != null){
				var deferred=$q.defer();
				$scope.paramList2=[];
				$scope.sendRecv("PMS311", "date_query", "com.systex.jbranch.app.server.fps.pms311.PMS311InputVO",$scope.inputVO,
						function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.paramList2 = tota[0].body.resultList;
						$scope.mappingSet['timeW'] = [];
						angular.forEach($scope.paramList2,
								function(row, index, objs) {
							var sp = [];
							sp[0] = row.DATA_DATE.substring(0, 4);
							sp[1] = row.DATA_DATE.substring(4, 6);
							sp[2] = row.DATA_DATE.substring(6, 8);
							var e = getMonthWeek(sp[0], sp[1], sp[2]);
							
							var time = new Date();
							time.setFullYear(sp[0], sp[1] - 1, sp[2]);
							var WeekFirstDay = new Date(time - (time.getDay() - 1)
									* 86400000);
							var WeekLastDay = new Date(
									(WeekFirstDay / 1000 + 6 * 86400) * 1000);
							var days = WeekFirstDay.getDate();
							days = (days < sp[2]) ? sp[2] : days;
							
							$scope.mappingSet['timeW'].push({
								LABEL : sp[0] + '/' + sp[1] + '/' + days,
								DATA : row.DATA_DATE
							});
						});
						deferred.resolve();
						return;
					}						
				});
				return deferred.promise;  
			}else{
				return;
			}
			 
		  }	
		var getMonthWeek = function (a, b, c) { 
		    var date = new Date(a, parseInt(b) - 1, c), w = date.getDay(), d = date.getDate(); 
		    return Math.ceil( (d + 6 - w) / 7 ); 
		};
        
        
        
        /*** 查詢資料 ***/
		$scope.query = function(){			
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇「*」欄位');
        		return;
        	}
			if($scope.inputVO.sCreDate != "")
				$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
			$scope.sendRecv("PMS311", "queryData", "com.systex.jbranch.app.server.fps.pms311.PMS311InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}	
							$scope.paramList = tota[0].body.resultList;	
							
							//員編不滿6碼，補零
							angular.forEach($scope.paramList, function(row, index, objs) {
								if(row.EMP_ID.trim() != "" && row.EMP_ID.length < 6) {
									while(row.EMP_ID.length < 6) {
										row.EMP_ID = "0" + row.EMP_ID;
									}
								}
							});	
							
//							alert(JSON.stringify($scope.paramList[0]));
							$scope.outputVO = tota[0].body;	
							$scope.outputVO.type = angular.copy($scope.inputVO.rptType);
//							$scope.showSuccessMsg('查詢成功');
							return;
						}						
			});
		};
		
		/***匯出EXCEL檔***/
		$scope.exportRPT = function(){					
			$scope.sendRecv("PMS311", "export", "com.systex.jbranch.app.server.fps.pms311.PMS311OutputVO", $scope.outputVO,
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
