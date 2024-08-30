/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG140Controller', 
	function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG140Controller";
		
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		
		// filter
		getParameter.XML(["COMMON.YES_NO", "ORG.CERT", "ORG.CERT_ALL"], function(totas) {
			if (totas) {
				$scope.mappingSet['ORG.CERT'] = totas.data[totas.key.indexOf('ORG.CERT')];
				$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['ORG.CERT_ALL'] = totas.data[totas.key.indexOf('ORG.CERT_ALL')];
			}
		});
		//===
		
    	$scope.sendRecv("ORG140", "getRoleList", "com.systex.jbranch.app.server.fps.org140.ORG140InputVO", $scope.inputVO,
				function(tota, isError) {
    		if (!isError) {
    			if(tota[0].body.roleList.length == 0) {
    				$scope.roleList = [];   //若查無資料，就將結果清空。
        			return;
    			}
    			$scope.mappingSet['ORG.CERT_ROLE_LIST'] = tota[0].body.roleList;
    			$scope.roleListOutputVO = tota[0].body;   // tota[0].body 就是 OutputVO
    			
				return;       			
    		}      		
    	});
	    
	    $scope.minDate = new Date(new Date().getFullYear() - 130, 0, 1);
		$scope.maxDate = new Date(new Date().getFullYear() + 130, 11, 31);
		
    	$scope.startDateOptions = {
    			maxDate: $scope.inputVO.onboardDateEnd || $scope.maxDate,
    			minDate: $scope.minDate
    	};
    	
		$scope.endDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.inputVO.onboardDateStart || $scope.minDate
		};
		
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();   //防止父事件被觸發
				$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
				$scope.startDateOptions.maxDate = $scope.inputVO.onboardDateEnd || $scope.maxDate;
				$scope.endDateOptions.minDate = $scope.inputVO.onboardDateStart || $scope.minDate;
		};
		
		//初始化
		$scope.init = function(){
			$scope.resultList = [];  //查詢用
			$scope.csvList = [];    //匯出用

			var startDate = new Date();
			var month = startDate.getMonth();
			startDate.setMonth(month - 3);
			
			$scope.inputVO = {
					region_center_id: '',
					branch_area_id	: '',
					branch_nbr		: '',
					empId			: '',
					empName			: '',
					probation		: '',
					onboardDateStart: startDate,
					onboardDateEnd	: new Date(),
					chkCode			: [], 
					certID			: '',
					privilegeID		: ''
			};
			
			$scope.data = [];  //查詢用
			$scope.resultList = [];  //查詢用
			$scope.csvList = [];    //匯出用

			$scope.check = false;
			
			if (typeof($scope.mappingSet['ORG.CERT']) !== 'undefined') {
				for(var i = 0; i < $scope.mappingSet['ORG.CERT'].length; i++){    //點選清除時能清除單獨勾選的證照
					$scope.mappingSet[i] = false;				
				}
			}
			
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
		};		
 		
        //全選
        $scope.selectAll = function(){
        	if($scope.check){
        		for(var i = 0; i < $scope.mappingSet['ORG.CERT'].length; i++){
        			$scope.inputVO.chkCode.push($scope.mappingSet['ORG.CERT'][i].DATA);
        		}
        	}else{
        		$scope.inputVO.chkCode = [];
        	}
        }
        
        //複選
        $scope.toggleSelection = function toggleSelection(data) {
        	var idx = $scope.inputVO.chkCode.indexOf(data);
        	if (idx > -1) {
        		$scope.inputVO.chkCode.splice(idx, 1);      //若已存在，就將它移除
        	} else {
        		$scope.inputVO.chkCode.push(data);          //若不存在，就將它加入
        	}
        };
        
        //判斷適用到期日
        $scope.fun = function(data){
        	var nowdate = new Date();
        	var date1 = new Date(data);
        	if(data != null){
        		return (date1 < nowdate);        		
        	}
        	return false;
        }
              
        //查詢
        $scope.inquire = function(){
        	$scope.data = [];  //查詢用
			$scope.resultList = [];  //查詢用
			
        	$scope.sendRecv("ORG140", "queryData", "com.systex.jbranch.app.server.fps.org140.ORG140InputVO", $scope.inputVO,
					function(tota, isError) {
        		if (!isError) {
        			if(tota[0].body.resultList.length == 0) {
        				$scope.resultList = [];   //若查無資料，就將結果清空。
            			return;
        			}
        			$scope.outputVO = tota[0].body;   // tota[0].body 就是 OutputVO
        			$scope.resultList = tota[0].body.resultList;
//					$scope.csvList = tota[0].body.csvList;    //匯出用，拿來放入ORG140OutputVO 的 list      			
					return;       			
        		}      		
        	});
        }
        
        //匯出 
	 	$scope.export = function() {
	 		$scope.sendRecv("ORG140", "export", "com.systex.jbranch.app.server.fps.org140.ORG140InputVO",  $scope.inputVO, 
	 				function(tota, isError) {
	 					if (!isError) {
	 						$scope.paramList=[];
							$scope.csvList=[];
							return;
						}
			});
		};
        
        $scope.init();
        
        $scope.comma_split = function(value) {
        	if (null == value) {
        		return '';
        	} else {
        		return value.split(',');
        	}
	    	
	    }
});
