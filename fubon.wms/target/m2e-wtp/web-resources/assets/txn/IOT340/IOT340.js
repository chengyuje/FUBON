/**
 * 
 */
'use strict';
eSoafApp.controller('IOT340Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,getParameter,$filter,$timeout) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IOT340Controller";
        

     // date picker
		// 有效起始日期
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_applysDateOptions = {
				maxDate:$scope.applymaxDate,
				minDate:$scope.applyminDate
		};
		$scope.bgn_applyeDateOptions = {
				maxDate:$scope.applymaxDate,
				minDate:$scope.applyminDate
		}
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.KEYIN_DATE_T || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.KEYIN_DATE_F || $scope.minDate;
		};
		$scope.applylimitDate = function(){
			$scope.bgn_applysDateOptions.maxDate = $scope.inputVO.APPLY_DATE_T || $scope.applymaxDate;
			$scope.bgn_applyeDateOptions.minDate = $scope.inputVO.APPLY_DATE_F || $scope.applyminDate;
		}
		//初始化
        $scope.init = function(){
    		$scope.showReasonList = false;
        	$scope.inputVO = {
        		OP_BATCH_NO:undefined,
        		REG_TYPE:'',
        		STATUS:'',
        		BRANCH_NBR:'',
        		INS_ID:'',
        		B_OVER_DAYS:'',
        		H_OVER_DAYS:'',
        		CUST_ID:'',
        		KEYIN_DATE_F:undefined,
        		KEYIN_DATE_T:undefined,
        		APPLY_DATE_F:undefined,
        		APPLY_DATE_T:undefined,
        		POLICY_NO1:'',
        		POLICY_NO2:'',
        		POLICY_NO3:'',
        		PROD_NAME:'',
        		PROD_ALL:'',
        		BEF_SIGN_OPRID:'',
        		BEF_SIGN_DATE:undefined,
        		SIGN_OPRID:'',
        		SIGN_DATE:undefined,
        		AFT_SIGN_OPRID:'',
        		AFT_SIGN_DATE:undefined
        	}
        };
        $scope.init();


        //clear
        $scope.btnClear = function(){
        	$scope.init();
        }
		
        //分頁初始化
//        $scope.inquireInit = function(){
//        	$scope.questionList = [];
//        };
//        $scope.inquireInit();
        
			getParameter.XML(["IOT.MAIN_STATUS","IOT.PAY_TYPE","IOT.PACK_REG_TYPE","IOT.PPT_TYPE"], function(totas) {
			   if (totas) {
				//狀態
			    $scope.mappingSet['IOT.MAIN_STATUS'] = totas.data[totas.key.indexOf('IOT.MAIN_STATUS')];
			    $scope.mappingSet['IOT.PAY_TYPE'] = totas.data[totas.key.indexOf('IOT.PAY_TYPE')];
			    $scope.mappingSet['IOT.PPT_TYPE'] = totas.data[totas.key.indexOf('IOT.PPT_TYPE')];
			    $scope.pack_reg_typeList = [];
			    for(var a=0;a<totas.data[totas.key.indexOf('IOT.PACK_REG_TYPE')].length;a++){
			    	if(totas.data[totas.key.indexOf('IOT.PACK_REG_TYPE')][a].DATA == '1' || totas.data[totas.key.indexOf('IOT.PACK_REG_TYPE')][a].DATA == '2'){
			    		$scope.pack_reg_typeList.push(totas.data[totas.key.indexOf('IOT.PACK_REG_TYPE')][a]);
			    	}
			    }
			   }
			  });
			
		$scope.checkqueryField = function(){
			if($scope.inputVO.OP_BATCH_NO !=undefined || $scope.inputVO.REG_TYPE != ''){
				return true;
			}else{
				return false;
			}
		}
        
        //查詢
        $scope.queryData = function(){
        	if($scope.inputVO.OP_BATCH_NO != undefined || $scope.inputVO.REG_TYPE != ''){
        		$scope.inputVO.OP_BATCH_NO = $scope.inputVO.OP_BATCH_NO.toUpperCase();
        		$scope.sendRecv("IOT340","queryData","com.systex.jbranch.app.server.fps.iot340.IOT340InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
		                   		$scope.IOT_MAINList = tota[0].body.IOT_MAIN;
		                   		$scope.outputVO = tota[0].body
		                   		if($scope.IOT_MAINList.length<=0){
		                   			$scope.showErrorMsg('ehl_01_common_009');
		                   		}
		               		}
            	});
        	}else{
        		if($scope.inputVO.OP_BATCH_NO == undefined){
					$scope.showErrorMsg('ehl_02_common_002',['分行送件批號']);
				}
        		if($scope.inputVO.REG_TYPE == ''){
					$scope.showErrorMsg('ehl_02_common_002',['保險送件類型']);
				}	
        	}
        }
        
        $scope.setBatch = function(){
        	
        }
 
        $scope.selectall = function(){
        	if($scope.inputVO.selectPass_all == 'Y'){
        		for(var a = 0;a<$scope.IOT_MAINList.length;a++){
        			$scope.IOT_MAINList[a].pass = 'Y';
        			$scope.IOT_MAINList[a].nopass = 'N';
        			$scope.chkXsetStatus($scope.IOT_MAINList[a], 'PASS');
        		}
        	}else{
        		for(var a = 0;a<$scope.IOT_MAINList.length;a++){
        			$scope.IOT_MAINList[a].pass = 'N';
        			$scope.IOT_MAINList[a].next_status = '';
        		}
        	}
        	if($scope.inputVO.selectNoPass_all == 'Y'){
        		for(var a = 0;a<$scope.IOT_MAINList.length;a++){
        			$scope.IOT_MAINList[a].nopass = 'Y';
        			$scope.IOT_MAINList[a].pass = 'N';
        			$scope.chkXsetStatus($scope.IOT_MAINList[a], 'REJECT');
        		}
        	}else{
        		for(var a = 0;a<$scope.IOT_MAINList.length;a++){
        			$scope.IOT_MAINList[a].nopass = 'N';
        			$scope.IOT_MAINList[a].next_status = '';
        		}
        	}
        }
        
        $scope.chkXsetStatus = function(row,in_opr){
        	if(row.pass == 'Y'){
        		if($scope.inputVO.selectNoPass_all != undefined){
        			if($scope.inputVO.selectNoPass_all == 'Y'){
        				$scope.inputVO.selectNoPass_all = 'N';
        			}
        		}
        		$scope.IOT920inputVO = {
        				in_opr:in_opr,
        				curr_status:row.STATUS,
        				SIGN_INC:row.SIGN_INC,
        				INS_KIND:row.INS_KIND,
        				REG_TYPE:row.REG_TYPE
        		}
               	$scope.sendRecv("IOT920","chkXsetStatus","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",$scope.IOT920inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			$scope.next_statusList = tota[0].body.next_statusList;
		               			if($scope.next_statusList.length>0){
			               			row.next_status = $scope.next_statusList[0].NEXT_STATUS;
		               			}else{
		               				row.pass='N';
		               				$scope.showErrorMsg('ehl_02_IOT_003');
		               			}
		               		}
            	});
        	}else{
        		row.next_status = '';
        	}
        	if(row.nopass == 'Y'){
        		$scope.showReasonList = true;
        		row.nopassreason = '';
        		row.REJ_REASON_OTH = '';
        		if($scope.inputVO.selectPass_all != undefined){
        			if($scope.inputVO.selectPass_all == 'Y'){
        				$scope.inputVO.selectPass_all = 'N';
        			}
        		}
        		$scope.IOT920inputVO = {
        				in_opr:in_opr,
        				curr_status:row.STATUS,
        				SIGN_INC:row.SIGN_INC,
        				INS_KIND:row.INS_KIND,
        				REG_TYPE:row.REG_TYPE
        		}
               	$scope.sendRecv("IOT920","chkXsetStatus","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",$scope.IOT920inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			$scope.next_statusList = tota[0].body.next_statusList;
		               			if($scope.next_statusList.length>0){
			               			row.next_status = $scope.next_statusList[0].NEXT_STATUS;
		               			}else{
		               				row.nopass = 'N'
		               				if($scope.inputVO.selectNoPass_all == 'Y'){
		               					$scope.inputVO.selectNoPass_all='N'
		               				}
		               				$scope.showErrorMsg('ehl_02_IOT_003');
		               			}
		               		}
            	});
        	}else{
        		row.next_status = '';
        	}
        }
        
        $scope.submit = function(){
        	if($scope.IOT_MAINList != undefined){
        		$scope.inputVO.IOT_MAINList = $scope.IOT_MAINList;
               	$scope.sendRecv("IOT340","submit","com.systex.jbranch.app.server.fps.iot340.IOT340InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			if(tota[0].body){
		               				$scope.queryData();
		               				if($scope.inputVO.selectNoPass_all == 'Y'){
		               					$scope.inputVO.selectNoPass_all='N'
		               				}
		               				if($scope.inputVO.selectPass_all == 'Y'){
		               					$scope.inputVO.selectPass_all='N'
		               				}
		               			}
		               		}
            	});
        	}
        	
        }
        

        
        
        
        
        
	});  
