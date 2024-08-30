/**
 * 
 */
'use strict';
eSoafApp.controller('IOT140Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,getParameter,$filter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IOT140Controller";
        

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
        	$scope.update_return = $scope.connector('get','IOT140_updateSubmit');
        	$scope.showOTH_TYPE = false;
        	$scope.showINSPRD_ID = false;
        	$scope.showINSPRD_NAME = false;
        	$scope.showFXD_PROD_PERIOD = false;
        	$scope.showTERMINATED_INC = false;
        	$scope.showSIGN_INC = false;
        	$scope.inputVO={
        			REG_TYPE:'',
        			KEYIN_DATE_F:lastdate,
        			KEYIN_DATE_T:now,
        			INCLUDED_REJECT:'N',
        			INSURED_ID:'',
        			CUST_ID:'',
        			POLICY_NO1:'',
        			POLICY_NO2:'',
        			POLICY_NO3:'',
        			INS_ID:'',
        			OP_BATCH_OPRID:'',
        			OP_BATCH_OPRNAME:'',
        			select_all:'N',
        			FB_COM_YN:''
        	}
        	$scope.bgn_sDateOptions.maxDate = $scope.inputVO.KEYIN_DATE_T || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.KEYIN_DATE_F || $scope.minDate;
        	$scope.sendRecv("IOT140","Initial","com.systex.jbranch.app.server.fps.iot140.IOT140InputVO",$scope.inputVO,
        			function(tota,isError){
        			$scope.BouncedList = tota[0].body.Bounced;
        			for(var a = 0;a<$scope.BouncedList.length;a++){
        				var list = [$scope.BouncedList[a].OP_BATCH_NO,$scope.BouncedList[a].CNT];
        				$scope.showErrorMsg("ehl_01_iot140_001",list);
        			}
        	});
        };
        $scope.init();
        
        $scope.showField = function(){
        	switch ($scope.inputVO.REG_TYPE) {
			case '1':
				$scope.showINSPRD_ID = true;
				$scope.showINSPRD_NAME = true;
				$scope.showTERMINATED_INC = true;
				$scope.showSIGN_INC = true;
				$scope.showFXD_PROD_PERIOD = false;
				$scope.showOTH_TYPE = false;
				$scope.IOT_MAINList = [];
           		$scope.outputVO = [];
				break;
			case '2':
				$scope.showINSPRD_ID = true;
				$scope.showINSPRD_NAME = true;
				$scope.showTERMINATED_INC = true;
				$scope.showSIGN_INC = true;
				$scope.showOTH_TYPE = true;
				$scope.showFXD_PROD_PERIOD = false;
				$scope.IOT_MAINList = [];
           		$scope.outputVO = [];
				break;
			case '3':
				$scope.showFXD_PROD_PERIOD = true;
				$scope.showINSPRD_ID = false;
				$scope.showINSPRD_NAME = false;
				$scope.showTERMINATED_INC = false;
				$scope.showSIGN_INC = false;
				$scope.showOTH_TYPE = false;
				$scope.IOT_MAINList = [];
           		$scope.outputVO = [];
				break;
			default:
//				$scope.showOTH_TYPE = false;
//	        	$scope.showINSPRD_ID = false;
//	        	$scope.showINSPRD_NAME = false;
//	        	$scope.showFXD_PROD_PERIOD = false;
//	        	$scope.showTERMINATED_INC = false;
//	        	$scope.showSIGN_INC = false;
				break;
			}
        }
        
    	//根據欄位帶出分行代碼、招攬人員姓名、要保人姓名、留存分行地址	
		$scope.getInfo = function(in_column){
			$scope.inputVO.in_column = in_column;
			if($scope.inputVO.in_column == 'RECRUIT'){
				$scope.EMP_NAME = '';
			}
			$scope.inputVO.RECRUIT_ID = $scope.inputVO.OP_BATCH_OPRID;
			$scope.sendRecv("IOT920","getCUSTInfo","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",
					$scope.inputVO,function(tota,isError){
				if(!isError){
					if(tota[0].body.EMP_NAME != null){
						if(tota[0].body.EMP_NAME.length>0){
							$scope.EMP_NAME = tota[0].body.EMP_NAME[0].EMP_NAME;
						}else{
							$scope.inputVO.OP_BATCH_OPRNAME = undefined;
						}
					}
				}
			});
		}
		
        //clear
        $scope.btnClear = function(){
        	var now = new Date();
        	var lastdate = new Date();
        	lastdate.setDate(lastdate.getDate()-1);
        	$scope.inputVO={
        			REG_TYPE:'',
        			KEYIN_DATE_F:lastdate,
        			KEYIN_DATE_T:now,
        			INCLUDED_REJECT:'N',
        			INSURED_ID:'',
        			CUST_ID:'',
        			POLICY_NO1:'',
        			POLICY_NO2:'',
        			POLICY_NO3:'',
        			INS_ID:'',
        			OP_BATCH_OPRID:'',
        			OP_BATCH_OPRNAME:'',
        			select_all:'N',
        			FB_COM_YN:''
        	}
        	$scope.limitDate();
        }
		
        //分頁初始化
//        $scope.inquireInit = function(){
//        	$scope.questionList = [];
//        };
//        $scope.inquireInit();
        
        getParameter.XML(["IOT.MAIN_STATUS","IOT.OTH_TYPE"], function(totas) {
        	if (totas) {
				//狀態
			    $scope.mappingSet['IOT.MAIN_STATUS'] = totas.data[totas.key.indexOf('IOT.MAIN_STATUS')];
			    //文件種類
			    $scope.mappingSet['IOT.OTH_TYPE'] = totas.data[totas.key.indexOf('IOT.OTH_TYPE')];
			    
			   }
		});
        
		// INS_SOURCE
		$scope.mappingSet['INS_SOURCE'] = [];
		$scope.mappingSet['INS_SOURCE'].push({LABEL : '請選擇',DATA : ''},{LABEL : '富壽',DATA : 'Y'},{LABEL : '非富壽',DATA : 'N'});
        
        //查詢
        $scope.queryData = function(){
        	$scope.showField();
        	if($scope.inputVO.REG_TYPE !='' && $scope.inputVO.KEYIN_DATE_F !=undefined){
	        	$scope.connector('set','IOT140_querytemp',$scope.inputVO);
               	$scope.sendRecv("IOT140","queryData","com.systex.jbranch.app.server.fps.iot140.IOT140InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			if(tota[0].body.IOT_MAIN.length>0){
			                   		$scope.IOT_MAINList = tota[0].body.IOT_MAIN;
			                   		$scope.outputVO = tota[0].body
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
        		$scope.showMsg('ehl_01_common_022');
        	}
        }
        
        $scope.select_all = function() {
        	if($scope.inputVO.select_all == 'Y' && $scope.IOT_MAINList != undefined){
    			angular.forEach($scope.IOT_MAINList, function(row, index, objs){
					row.choice = 'Y';
				});
        	}
        	if($scope.inputVO.select_all == 'N' && $scope.IOT_MAINList != undefined){
    			angular.forEach($scope.IOT_MAINList, function(row, index, objs){
					row.choice = 'N';
				});
        	}
        }
        
        $scope.select_false = function(choice){
        	if(choice == 'N'){
        		$scope.inputVO.select_all = 'N';
        	}
        }
 
        //檢查補印
        $scope.checkrePrintStatus = function(){
        	var notRePrint = false;
        	for(var a=0;a<$scope.IOT_MAINList.length;a++){
        		if($scope.IOT_MAINList[a].choice == 'Y'){
        			if($scope.IOT_MAINList[a].OP_BATCH_NO == undefined){
        				$scope.IOT_MAINList[a].choice = 'N';
        				notRePrint = false;
        				break;
        			}else{
        				notRePrint = true;
        			}
        		}
        	}
        	return notRePrint;
        }
        
        
        //補印送件明細
        $scope.rePrint = function(){
        	if($scope.IOT_MAINList != undefined){
        		if($scope.checkrePrintStatus()){
            		$scope.inputVO.IOT_MAINList = $scope.IOT_MAINList;
            		$scope.sendRecv("IOT140","rePrint","com.systex.jbranch.app.server.fps.iot140.IOT140InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
		               				if($scope.inputVO.select_all == 'Y'){
		               					$scope.inputVO.select_all='N';
		               				}
		               				$scope.queryData();
		               		}
            		});
        		}else{
        			$scope.showErrorMsg('目前資料狀態不能補印，請確認。');
        		}
        	}
        }
        
        
        $scope.Pack = function(){
        	if($scope.IOT_MAINList != undefined){
        		if($scope.checkstatus()){
            		$scope.inputVO.IOT_MAINList = $scope.IOT_MAINList;
            		$scope.sendRecv("IOT140","submit","com.systex.jbranch.app.server.fps.iot140.IOT140InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
//		               			if(tota[1].body.submit_check){
		               				if($scope.inputVO.select_all == 'Y'){
		               					$scope.inputVO.select_all='N';
		               				}
		               				$scope.queryData();
//		               			}
		               		}
            		});
        		}else{
        			$scope.showErrorMsg('ehl_01_IOT_001');
        		}
        	}
        }
        
        $scope.checkstatus = function(){
        	for(var a=0;a<$scope.IOT_MAINList.length;a++){
        		if($scope.IOT_MAINList[a].choice == 'Y'){
        			if($scope.IOT_MAINList[a].STATUS == '10'){
        				$scope.IOT_MAINList[a].choice = 'N';
        				$scope.unpack = false;
        				break;
        			}else{
        				$scope.unpack = true;
        			}
        		}
        	}
        	return $scope.unpack;
        }
        
        $scope.unPack = function(){
        	if($scope.IOT_MAINList != undefined){
        		if($scope.checkUnpackStatus()){
	        		$scope.inputVO.IOT_MAINList = $scope.IOT_MAINList;
	               	$scope.sendRecv("IOT140","unPack","com.systex.jbranch.app.server.fps.iot140.IOT140InputVO",$scope.inputVO,
	            			function(tota,isError){
			               		if(!isError){
			               			if(tota[0].body !=null){
				               			$scope.unpackINS_KEYNO = tota[0].body.INS_KEYNO;
				               			if($scope.unpackINS_KEYNO != undefined){
				                			angular.forEach($scope.IOT_MAINList, function(row, index, objs){
												if(row.INS_KEYNO == $scope.unpackINS_KEYNO){
													row.choice = 'N';
													var uncheckindex = [index+1];
						               				$scope.showErrorMsg("ehl_02_iot140_002",uncheckindex);
												}
											});
				               			}
			               			}else{
			               				$scope.queryData();
			               			}
			               		}
	            	});
        		} else {
        			$scope.showErrorMsg("目前狀態不可取消打包");
        		}
        	}
        }
        
        //只有以下狀態可以取消打包
        //30:OP打包送件
        //38:總行行助設定批次
        //42:總行行助退件(簽署前)
        $scope.checkUnpackStatus = function(){
        	$scope.unpack = true;
        	for(var a=0;a<$scope.IOT_MAINList.length;a++){
        		if($scope.IOT_MAINList[a].choice == 'Y'){
        			if($scope.IOT_MAINList[a].STATUS != '30' && $scope.IOT_MAINList[a].STATUS != '38' && $scope.IOT_MAINList[a].STATUS != '42'){
        				$scope.IOT_MAINList[a].choice = 'N';
        				$scope.unpack = false;
        			}
        		}
        	}
        	return $scope.unpack;
        }
        
        $scope.DeleteData = function(row){
			debugger
        	$scope.inputVO.IOT_MAIN = row;
           	$scope.sendRecv("IOT140","delData","com.systex.jbranch.app.server.fps.iot140.IOT140InputVO",$scope.inputVO,
        			function(tota,isError){
						debugger
	               		if(!isError){
	               			if(!tota[0].body){
	               				$scope.showErrorMsg("ehl_01_IOT_002");
	               				$scope.queryData();
	               			}
	               			if(tota[0].body){
								$scope.showMsg("刪除成功");
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
	        	$scope.connector('set','IOT140',$scope.updateData);
	        	$rootScope.menuItemInfo.url = "assets/txn/IOT120/IOT120.html";
				break;
			default:
				$scope.updateData = {
        			OPR_STATUS:'UPDATE',
        			INS_KEYNO:row.INS_KEYNO,
        			REG_TYPE:Number($scope.inputVO.REG_TYPE)+1
        		}
	        	$scope.connector('set','IOT140',$scope.updateData);
	        	$rootScope.menuItemInfo.url = "assets/txn/IOT130/IOT130.html";
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
	        	$scope.connector('set','IOT140',$scope.readData);
	        	$rootScope.menuItemInfo.url = "assets/txn/IOT120/IOT120.html";
				break;
			default:
				$scope.readData = {
        			OPR_STATUS:'Read',
        			INS_KEYNO:row.INS_KEYNO,
        			REG_TYPE:Number($scope.inputVO.REG_TYPE)+1
        		}
	        	$scope.connector('set','IOT140',$scope.readData);
	        	$rootScope.menuItemInfo.url = "assets/txn/IOT130/IOT130.html";
				break;
			}
        }
        
        
		//列印 產生PDF檔
    	$scope.print = function() {
			$scope.sendRecv("IOT140", "print",
					"com.systex.jbranch.app.server.fps.iot140.IOT140OutputVO",
					{'IOT_MAIN':$scope.IOT_MAINList,'TYPE':'R1'}, function(tota, isError) {
						if (!isError) {	
							return;
						}
					});
		};
		
		//列印 產生PDF檔,封面
		$scope.printPage = function() {
			$scope.sendRecv("IOT140", "print",
					"com.systex.jbranch.app.server.fps.iot140.IOT140OutputVO",
					{'IOT_MAIN':$scope.IOT_MAINList,'TYPE':'R2'}, function(tota, isError) {
						if (!isError) {	
							return;
						}
					});
		};
        
        $scope.edit = function(row){
        	switch (row.editto) {
			case 'U':
	        	$scope.UpdateData(row);
				break;
			case 'D':
			debugger
				var txtMsg = $filter('i18n')('ehl_02_IOT_001');
				$confirm({text:txtMsg},{size:'sm'}).then(function(){
					$scope.DeleteData(row);
				})
				row.editto = "";
				break;
			case 'R':
				$scope.ReadData(row);
				break;
			default:
				break;
			}
        }
        
        $scope.update_returnQuery = function(){
        	if($scope.update_return){
            	$scope.connector('set','IOT140_updateSubmit','');
            	$scope.inputVO = $scope.connector('get','IOT140_querytemp');
            	if($scope.inputVO){
            		$scope.connector('set','IOT140_querytemp','');
            	}
            	$scope.queryData();
            	
        	}
        }
        $scope.update_returnQuery();
	});  
