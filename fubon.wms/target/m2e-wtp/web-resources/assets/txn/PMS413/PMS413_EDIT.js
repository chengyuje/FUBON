/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS413_EDITController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "PMS413_EDITController";
		
		$scope.mappingSet['comType'] = [];
        $scope.mappingSet['comType'].push({LABEL:'公用電腦', DATA:'1'}, {LABEL:'分行電腦', DATA:'2'});
		
		$scope.comTypeChange = function() {
	    	if($scope.inputVO.comType == '2')
	    		$scope.ctp = '分行';	    		    		
	    	else
	    		$scope.ctp = '公用';	    		    			    			
	    };
		
        /**取員工姓名*/
    	$scope.getEmpName = function(empID) {
    		var deferred = $q.defer();
    		var temp = empID ? empID : $scope.inputVO.emp_id;
    		$scope.sendRecv("PMS413", "getEmpName", "com.systex.jbranch.app.server.fps.pms413.PMS413InputVO", {emp_id: temp},
    			function(totas, isError) {
					if(!isError) {
						if(totas[0].body.empName.length > 0)
							$scope.inputVO.emp_name = totas[0].body.empName[0].EMP_NAME;
						else
							$scope.inputVO.emp_name = "";
						deferred.resolve("success");
					}
    			}
    		);
    		return deferred.promise;
    	};
        
		$scope.init = function(){
			if($scope.row)
				$scope.isUpdate = true;
			
			$scope.inputVO = {
					comType: '',
					ip: '',
					rc_id: '',
					rc_name: '',
					op_id: '',
					op_name: '',
					br_id: '',
					br_name: '',
					emp_id: '',
					emp_name: '',
					name_fg: ''
        	};			

    		//組織連動
			// 2017/11/9 mantis:3938 直接輸入且不檢核
	        $scope.region = ['N', $scope.inputVO, "rc_id", "REGION_LIST", "op_id", "AREA_LIST", "br_id", "BRANCH_LIST", "ao_code", "AO_LIST", "nothingDO", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
	        
	        if($scope.inputVO.comType == '1')
				$scope.ctp = '公用';
			else
				$scope.ctp = '分行';
	    /* 0002196 mark add */   
	        if( $scope.cType == '1')
	        {	
	        	$scope.Ctp = '公用';
	        	$scope.inputVO.comType=1;
	        	$scope.cmbLock='true';
	        }else
	        {	
	         $scope.Ctp = '分行';
	        $scope.inputVO.comType=2;
	        $scope.cmbLock='true'; 
	        }
	        
//    		$scope.cmbLock = 'false';
    		$scope.RegionController_setName($scope.region).then(function(data) {
    			if($scope.act == 'M'){
        			if($scope.row.COM_TYPE == '1')
        				$scope.ctp = '公用';
        			else
        				$scope.ctp = '分行';
        			
        			$scope.inputVO.tempIP = $scope.row.IPADDRESS;
        			$scope.inputVO.tempRcID = $scope.row.REGION_CENTER_ID;    			
        			$scope.inputVO.tempOpID = $scope.row.OP_AREA_ID;    			
        			$scope.inputVO.tempBrID = $scope.row.BRANCH_NBR;
        			$scope.inputVO.tempEmpID = $scope.row.EMP_ID;
        			
        			$scope.inputVO.seq = $scope.row.SEQ;
        			$scope.inputVO.comType = $scope.row.COM_TYPE;
        			$scope.inputVO.ip = $scope.row.IPADDRESS;

        			if($scope.AVAIL_BRANCH.length > 1){
//        			因組織連動關係，所以只需要塞進BRANCH_NBR，其他REGION_CENTER_ID與OP_AREA_ID會自動帶出
//	        			$scope.inputVO.rc_id = $scope.row.REGION_CENTER_ID;    	  		
//	        			$scope.inputVO.op_id = $scope.row.OP_AREA_ID;    			
	        			$scope.inputVO.br_id = $scope.row.BRANCH_NBR;
        			}
//        			只有分行電腦才需要帶EMP_ID
        			if($scope.EMP_LIST.length > 2 && $scope.row.COM_TYPE != '1'){
	        			$scope.inputVO.emp_id = $scope.row.EMP_ID;
        			}
        			
        			if($scope.row.COM_TYPE == '2')
        				$scope.getEmpName($scope.row.EMP_ID);
        			
        			$scope.cmbLock = 'true';
        			$scope.comTypeChange();    			    			
        		}
    		});
        };
        $scope.init();
		               
        $scope.save = function() {
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	// 2017/11/10 test
        	if($scope.inputVO.rc_id)
        		$scope.inputVO.rc_name = $filter('filter')($scope.REGION_LIST, {DATA: $scope.inputVO.rc_id})[0].LABEL;
        	else
        		$scope.inputVO.rc_name = "";
        	if($scope.inputVO.op_id)
        		$scope.inputVO.op_name = $filter('filter')($scope.AREA_LIST, {DATA: $scope.inputVO.op_id})[0].LABEL;
        	else
        		$scope.inputVO.op_name = "";
        	if($scope.inputVO.br_id)
        		$scope.inputVO.br_name = $filter('filter')($scope.BRANCH_LIST, {DATA: $scope.inputVO.br_id})[0].LABEL;
        	else
        		$scope.inputVO.br_name = "";
        	//
        	
        	if($scope.inputVO.emp_id) {
        		$scope.getEmpName().then(function(data) {
					$scope.reallySave();
				});
        	} else
				$scope.reallySave();
        };
        
        // 2017/11/10 test2
        $scope.reallySave = function() {
        	// old code
        	if($scope.isUpdate) {
				$scope.sendRecv("PMS413", "updateIP", "com.systex.jbranch.app.server.fps.pms413.PMS413InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
    	                		var tt = $scope.inputVO.comType;
                        		$scope.closeThisDialog('success');
    	                	};
    					}
    			);
        	} else {
        		$scope.sendRecv("PMS413", "addIP", "com.systex.jbranch.app.server.fps.pms413.PMS413InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('success');
    	                	};
    					}
    			);
        	}
        };
        
        
                	
});