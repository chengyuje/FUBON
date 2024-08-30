/**
 * 
 */
'use strict';
eSoafApp.controller('IOT330Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,getParameter,$filter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IOT330Controller";
        
     	$scope.inputVOS=$scope.connector('get','IOT330I') || {};
     
       //fillter  IOT.MAIN_STATUS
    	var vo = {'param_type': 'IOT.MAIN_STATUS', 'desc': false};
        if(!projInfoService.mappingSet['IOT.MAIN_STATUS']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['IOT.MAIN_STATUS'] = totas[0].body.result;
        			$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];
        		}
        	});
        } else {
        	$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];
        }
        
        //IOT.PPT_TYPE  險種
    	var vo = {'param_type': 'IOT.PPT_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['IOT.PPT_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['IOT.PPT_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['IOT.PPT_TYPE'] = projInfoService.mappingSet['IOT.PPT_TYPE'];
        		}
        	});
        } else {
        	$scope.mappingSet['IOT.PPT_TYPE'] = projInfoService.mappingSet['IOT.PPT_TYPE'];
        }
        
        
        
        //IOT.PACK_REG_TYPE
        var vo = {'param_type': 'IOT.PACK_REG_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['IOT.PACK_REG_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['IOT.PACK_REG_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['IOT.PACK_REG_TYPE'] = projInfoService.mappingSet['IOT.PACK_REG_TYPE'];
        			$scope.mappingSet['IOT.PACK_REG_TYP']=[]; 
        				angular.forEach($scope.mappingSet['IOT.PACK_REG_TYPE'], function(row, index, objs){
        					if(row.DATA!='3')
        						$scope.mappingSet['IOT.PACK_REG_TYP'].push(row);
        				});
        		}
        	});
        } else {
        	$scope.mappingSet['IOT.PACK_REG_TYPE'] = projInfoService.mappingSet['IOT.PACK_REG_TYPE'];
        }
        
        
        
        
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
		//初始化
        $scope.init = function(){
        	var now = new Date();
        	var lastdate = new Date();
        	lastdate.setDate(lastdate.getDate()-1);
        	$scope.showOTH_TYPE = false;
        	$scope.showINSPRD_ID = false;
        	$scope.showINSPRD_NAME = false;
        	$scope.showFXD_PROD_PERIOD = false;
        	$scope.showTERMINATED_INC = false;
        	$scope.showSIGN_INC = false;
        	$scope.update_return = $scope.connector('get','IOT130I');
        	$scope.inputVO={
        			REG_TYPE:'',
        			KEYIN_DATE_F:lastdate,
        			KEYIN_DATE_T:now,
        			INCLUDED:'N',
        			INSURED_ID:'',
        			CUST_ID:'',
        			POLICY_NO1:'',
        			POLICY_NO2:'',
        			POLICY_NO3:'',
        			INS_ID:'',
        			OP_BATCH_OPRID:'',
        			OP_BATCH_OPRNAME:''
        	}
        	
        	//2017/01/10 modify by jimmy 產險不需要檢查退件
//        	$scope.sendRecv("IOT330","Initial","com.systex.jbranch.app.server.fps.iot330.IOT330InputVO",$scope.inputVO,
//        			function(tota,isError){
//        			$scope.BouncedList = tota[0].body.Bounced;
//        			for(var a = 0;a<$scope.BouncedList.length;a++){
//        				var list = [$scope.BouncedList[a].OP_BATCH_NO,$scope.BouncedList[a].CNT];
//        				$scope.showErrorMsg("ehl_01_IOT330_001",list);
//        			}
//        	});
 
        };
        $scope.init();
        
		//英文字母轉大寫
		$scope.text_toUppercase = function(text,type){
			var toUppercase_text = text.toUpperCase();
			switch (type) {
			case 'INSURED_ID':
				$scope.inputVO.INSURED_ID = toUppercase_text;
				break;
			case 'CUST_ID':
				$scope.inputVO.CUST_ID = toUppercase_text;
				break;
			case 'POLICY_NO':
				$scope.inputVO.POLICY_NO = toUppercase_text;
				break;
			case 'INS_ID':
				$scope.inputVO.INS_ID = toUppercase_text;
				break;
			case 'OP_BATCH_OPRID':
				$scope.inputVO.OP_BATCH_OPRID = toUppercase_text;
				break;
			default:
				break;
			}
		}
        
        $scope.showField = function(){
        	switch ($scope.inputVO.REG_TYPE) {
			case '1':
				$scope.showINSPRD_ID = true;
				$scope.showINSPRD_NAME = true;
				$scope.showTERMINATED_INC = true;
				$scope.showSIGN_INC = true;
				$scope.showFXD_PROD_PERIOD = false;
				$scope.showOTH_TYPE = false;
				break;
			case '2':
				$scope.showINSPRD_ID = true;
				$scope.showINSPRD_NAME = true;
				$scope.showTERMINATED_INC = true;
				$scope.showSIGN_INC = true;
				$scope.showOTH_TYPE = true;
				$scope.showFXD_PROD_PERIOD = false;
				break;
			case '3':
				$scope.showFXD_PROD_PERIOD = true;
				$scope.showINSPRD_ID = false;
				$scope.showINSPRD_NAME = false;
				$scope.showTERMINATED_INC = false;
				$scope.showSIGN_INC = false;
				$scope.showOTH_TYPE = false;
				break;
			default:

				break;
			}
        }
        
    	//根據欄位帶出分行代碼、招攬人員姓名、要保人姓名、留存分行地址	
		$scope.getInfo = function(in_column){
			$scope.text_toUppercase($scope.inputVO.OP_BATCH_OPRID, 'OP_BATCH_OPRID');
			$scope.inputVO.in_column = in_column;
			//清空
			if($scope.inputVO.in_column == 'RECRUIT'){
				$scope.inputVO.OP_BATCH_OPRNAME = '';
			}
			$scope.inputVO.RECRUIT_ID = $scope.inputVO.OP_BATCH_OPRID;
			$scope.sendRecv("IOT920","getCUSTInfo","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",
					$scope.inputVO,function(tota,isError){
				if(!isError){
					if(tota[0].body.EMP_NAME != null){
						if(tota[0].body.EMP_NAME.length>0){
							$scope.inputVO.OP_BATCH_OPRNAME = tota[0].body.EMP_NAME[0].EMP_NAME;
						}else{
							$scope.inputVO.OP_BATCH_OPRNAME = undefined;
						}
					}
				}
			});
		}
        //clear
        $scope.btnClear = function(){

        	$scope.inputVO={
        			REG_TYPE:'',
        			KEYIN_DATE_F:undefined,
        			KEYIN_DATE_T:undefined,
        			INCLUDED:'N',
        			INSURED_ID:'',
        			CUST_ID:'',
        			POLICY_NO1:'',
        			POLICY_NO2:'',
        			POLICY_NO3:'',
        			INS_ID:'',
        			OP_BATCH_OPRID:'',
        			OP_BATCH_OPRNAME:''
        	}
        }
		
        //分頁初始化

        
			getParameter.XML(["IOT.MAIN_STATUS"], function(totas) {
			   if (totas) {
				//狀態
			    $scope.mappingSet['IOT.MAIN_STATUS'] = totas.data[0];
			   }
			  });
        
        //查詢
        $scope.queryData = function(){
        	$scope.showField();
        	if($scope.inputVO.REG_TYPE !='' && $scope.inputVO.KEYIN_DATE_F !=undefined){
               	$scope.sendRecv("IOT330","queryData","com.systex.jbranch.app.server.fps.iot330.IOT330InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			if(tota[0].body.IOT_MAIN.length>0){
		               				$scope.IOT_MAINList = tota[0].body.IOT_MAIN;		
			                   		$scope.outputVO = tota[0].body;
		                			angular.forEach($scope.IOT_MAINList, function(row, index, objs){
										row.edit = [];
										if(row.STATUS=='30'){
											row.edit.push({LABEL: "檢視", DATA: "R"});
										}else{
											row.edit.push({LABEL: "修改", DATA: "U"});
											row.edit.push({LABEL: "刪除", DATA: "D"});
											row.edit.push({LABEL: "檢視", DATA: "R"});
										}
									});
		               			}else{
		               				$scope.showErrorMsg('ehl_01_common_009');
		               			}
		               		}
            	});
        	}else{
        		$scope.showMsg('保險送件類型及鍵機日不得為空');
        	}
        }

        $scope.select_all = function() {
        	if($scope.inputVO.select_all == 'Y' && $scope.IOT_MAINList != undefined){
    			angular.forEach($scope.IOT_MAINList, function(row, index, objs){
					row.CHECK = 'Y';
				});
        	}
        	if($scope.inputVO.select_all == 'N' && $scope.IOT_MAINList != undefined){
    			angular.forEach($scope.IOT_MAINList, function(row, index, objs){
					row.CHECK = 'N';
				});
        	}
        }
        
        $scope.select_false = function(choice){
        	if(choice == 'N'){
        		$scope.inputVO.select_all = 'N';
        	}
        }
        
        $scope.Pack = function(){
        	if($scope.IOT_MAINList != undefined){
        		$scope.inputVO.IOT_MAINList = $scope.IOT_MAINList;
               	$scope.sendRecv("IOT330","submit","com.systex.jbranch.app.server.fps.iot330.IOT330InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			if(tota[0].body != true){
		               				$scope.notpackINS_KEYNO = tota[0].body.INS_KEYNO;
			               			if($scope.notpackINS_KEYNO != undefined){
			                			angular.forEach($scope.IOT_MAINList, function(row, index, objs){
											if(row.INS_KEYNO == $scope.notpackINS_KEYNO){
												row.CHECK = 'N';
												$scope.inputVO.select_all = 'N';
												var uncheckindex = [index+1];
					               				$scope.showErrorMsg("第{0}筆狀態無法打包",uncheckindex);
											}
										});
			               			}
		               			}else{
		               				$scope.showMsg('ehl_01_common_006');
		               				$scope.inputVO.select_all = 'N';
		               				$scope.queryData();
		               			}
		               		}
            	});
        	}
        }
        
        
        
        
        $scope.unPack = function(){
        	if($scope.IOT_MAINList != undefined){
        		$scope.inputVO.IOT_MAINList = $scope.IOT_MAINList;
               	$scope.sendRecv("IOT330","unPack","com.systex.jbranch.app.server.fps.iot330.IOT330InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			if(tota[0].body !=null){
			               			$scope.unpackINS_KEYNO = tota[0].body.INS_KEYNO;
			               			if($scope.unpackINS_KEYNO != undefined){
			                			angular.forEach($scope.IOT_MAINList, function(row, index, objs){
											if(row.INS_KEYNO == $scope.unpackINS_KEYNO){
												row.CHECK = 'N';
												$scope.inputVO.select_all = 'N';
												var uncheckindex = [index+1];
					               				$scope.showErrorMsg("ehl_02_iot140_002",uncheckindex);
											}
										});
			               			}
		               			}else{
		               				$scope.showMsg('ehl_01_common_006');
		               				$scope.inputVO.select_all = 'N';
			               			$scope.queryData();
		               			}
		               		}
            	});
        	}
        }
        
        $scope.DeleteData = function(row){
        	$scope.inputVO.IOT_MAIN = row;
        	$scope.inputVO.NEXT_STATES = "99";
        	$scope.sendRecv("IOT330","statusChk","com.systex.jbranch.app.server.fps.iot330.IOT330InputVO",$scope.inputVO,
        			function(tota,isError){
        		if(!isError){
        			$scope.Delete(row);
        		}else
        		{
        			$scope.showErrorMsg("ehl_01_IOT_002");
        		}
        	});
          
        }
        
        
        
        $scope.Delete = function(row){
        	$scope.inputVO.IOT_MAIN = row;
	     	$scope.sendRecv("IOT330","delData","com.systex.jbranch.app.server.fps.iot330.IOT330InputVO",$scope.inputVO,
	    			function(tota,isError){
	               		if(!isError){
	               			if(!tota[0].body){
	               				$scope.showErrorMsg("ehl_01_IOT_002");
	               				$scope.queryData();
	               			}
	               			if(tota[0].body){
	               				$scope.queryData();
	               			}
	               		}
	       	});
        }

        $scope.UpdateData = function(row){
        	switch ($scope.inputVO.REG_TYPE) {
			case '1':
				$scope.updateData = {
        			OPR_STATUS:'UPDATE',
        			INS_KEYNO:row.INS_KEYNO
        		}
	        	$scope.connector('set','IOT330',$scope.updateData);
				$scope.connector('set','IOT330I',$scope.inputVO);
	        	$rootScope.menuItemInfo.url = "assets/txn/IOT310/IOT310.html";
				break;
			default:
				$scope.updateData = {
        			OPR_STATUS:'UPDATE',
        			INS_KEYNO:row.INS_KEYNO
        		}
	        	$scope.connector('set','IOT330',$scope.updateData);
				$scope.connector('set','IOT330I',$scope.inputVO);
	        	$rootScope.menuItemInfo.url = "assets/txn/IOT320/IOT320.html";
				break;
			}
        }
        
        $scope.ReadData = function(row){
        	switch ($scope.inputVO.REG_TYPE) {
			case '1':
				$scope.readData = {
        			OPR_STATUS:'Read',
        			INS_KEYNO:row.INS_KEYNO
        		}
	        	$scope.connector('set','IOT330',$scope.readData);
				$scope.connector('set','IOT330I',$scope.inputVO);
	        	$rootScope.menuItemInfo.url = "assets/txn/IOT310/IOT310.html";
				break;
			default:
				$scope.readData = {
        			OPR_STATUS:'Read',
        			INS_KEYNO:row.INS_KEYNO
        		}
	        	$scope.connector('set','IOT330',$scope.readData);
				$scope.connector('set','IOT330I',$scope.inputVO);
	        	$rootScope.menuItemInfo.url = "assets/txn/IOT320/IOT320.html";
				break;
			}
        }
        
        $scope.edit = function(row){
        	switch (row.editto) {
			case 'U':
	        	$scope.UpdateData(row);
				break;
			case 'D':
				var txtMsg = $filter('i18n')('ehl_02_IOT_001');
				$confirm({text:txtMsg},{size:'sm'}).then(function(){
					$scope.DeleteData(row);
				})
				break;
			case 'R':
				$scope.ReadData(row);
				break;
			default:
				break;
			}
        }
        
        
        
        
        
        //尋找NAME
    	$scope.INSID = function(type1,type2){	
			if(type1=='INSURED'){
				$scope.inputVO.in_column='INSURED';
				if(type2=='INSURED_ID'){
					var CUST_ID=$scope.inputVO.INSURED_ID;
					if($scope.inputVO.INSURED_ID=='' || $scope.inputVO.INSURED_ID==null)
						 CUST_ID='空';
				
				}
				if(type2=='CUST_ID'){
					var CUST_ID=$scope.inputVO.CUST_ID;
					if($scope.inputVO.CUST_ID=='' || $scope.inputVO.CUST_ID==null)
						 CUST_ID='空';
				
				}
				
			}else if(type1=='RECRUIT'){
				$scope.inputVO.in_column='RECRUIT';	
				
			}
		
					$scope.sendRecv("IOT120","getInfo","com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
        					{'in_column':$scope.inputVO.in_column,'CUST_ID':CUST_ID,'RECRUIT_ID':$scope.inputVO.RECRUIT_ID},function(tota,isError){
						if(!isError){
							if(type1=='INSURED' && tota[0].body.CUST_NAME.length>0){
								if(type2=='CUST_ID')
									{
								$scope.inputVO.PROPOSER_NAME=tota[0].body.CUST_NAME[0].CUST_NAME;
								$scope.inputVO.UNOPEN_ACCT='Y';
									}
								if(type2=='INSURED_ID')	{
									$scope.inputVO.INSURED_NAME=tota[0].body.CUST_NAME[0].CUST_NAME;
									$scope.inputVO.UNOPEN_ACCT='Y';
								}
							}else if(type1=='INSURED' && tota[0].body.CUST_NAME.length==0){
								if(type2=='CUST_ID'){
									$scope.inputVO.PROPOSER_NAME='';
									$scope.inputVO.UNOPEN_ACCT='N';
								}
								if(type2=='INSURED_ID')	{
									$scope.inputVO.INSURED_NAME='';
									$scope.inputVO.UNOPEN_ACCT='N';
								}
							}
							if(type1=='RECRUIT'  && tota[0].body.EMP_NAME.length>0){
								$scope.inputVO.RECRUIT_NAME=tota[0].body.EMP_NAME[0].EMP_NAME;
							}else if(type1=='RECRUIT' && tota[0].body.EMP_NAME.length==0){
								$scope.inputVO.RECRUIT_NAME='';
							}
						}
					});
		}
//     	if($scope.inputVOS.REG_TYPE!=undefined){
//     		$scope.connector('set','IOT330I','');
//     		$scope.inputVO=$scope.inputVOS;
//     		$scope.queryData();
//     	}
     	
     	$scope.update_returnQuery = function(){
        	if($scope.update_return){
            	$scope.connector('set','IOT130I','');
            	$scope.inputVO = $scope.connector('get','IOT130');
            	if($scope.inputVO){
            		$scope.connector('set','IOT130','');
            	}
            	$scope.queryData();
            	
        	}
        }
        $scope.update_returnQuery();
	});  
