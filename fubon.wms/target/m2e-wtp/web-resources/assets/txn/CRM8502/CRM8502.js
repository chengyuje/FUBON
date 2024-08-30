/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM8502Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter,sysInfoService, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM8501Controller";
		
		getParameter.XML(["COMMON.YES_NO","CRM.APPLY_PRINT_STATUS", "FUBONSYS.HEADMGR_ROLE"], function(totas) {
			if (totas) {
				//YES;NO
		        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
		        //已申請，已取消
		        $scope.mappingSet['CRM.APPLY_PRINT_STATUS'] = totas.data[totas.key.indexOf('CRM.APPLY_PRINT_STATUS')];
		        var headMgrList = totas.data[totas.key.indexOf("FUBONSYS.HEADMGR_ROLE")].map(obj => obj.DATA);
				//#1898_【客戶資產現況表】開放聯行列印之可行性
				$scope.IS_HEAD_MGR = headMgrList.indexOf(projInfoService.getRoleID()) > -1;	       
				$scope.initial2();
			}
		});		
		
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
				
        /****======================================================== date picker =======================================================****/
    	$scope.bgn_sDateOptions = {
    		maxDate: $scope.maxDate,
      		minDate: $scope.minDate
        };

        $scope.bgn_eDateOptions = {
    		maxDate: $scope.maxDate,
      		minDate: $scope.minDate
        };

        $scope.model = {};
    	
    	$scope.open = function($event, elementOpened) {
    		$event.preventDefault();
    		$event.stopPropagation();
    		$scope.model[elementOpened] = !$scope.model[elementOpened];
    	};
    		
//    	$scope.applyStage = "";
    	$scope.limitDate = function() {
    		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.applyEdate || $scope.maxDate;
    		$scope.bgn_eDateOptions.minDate = $scope.inputVO.applySdate || $scope.minDate;		
    	};
        /**** date picker end ****/
        
		//初始化
		$scope.initial = function() {
			$scope.inputVO = {
					custID: '', 						//客戶ID
					branchNbr:'',						//分行別
					applySdate: new Date(), 			//申請起日
					applyEdate: undefined, 				//申請迄日					
					printStatus:'ALL',					//列印狀態
					printReportDisabled: false,			//列印資況表按鈕，避免連續點擊
					printPreviewDisabled: false 		//列印資況表按鈕，避免連續點擊
			};
				
			$scope.show_hide = true;
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "region", "REGION_LIST", "area_id", "AREA_LIST", "branchNbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
	    };
	    $scope.initial();
	    
	    //#1898_【客戶資產現況表】開放聯行列印之可行性
		$scope.initial2 = function() {
			if ($scope.inputVO.branchNbr != '') {
				$scope.radioValue = "branch";
			} else {
				$scope.radioValue = "";
			}
			if ($scope.IS_HEAD_MGR) {
				$scope.BRANCH_LIST = [
					{ LABEL: "全分行", DATA: "ALL" },
					...$scope.BRANCH_LIST
				];
			}
		};
		    
		//查詢客戶申請狀態
	    $scope.inquire = function() {
//	    	if ($scope.parameterTypeEditForm.$invalid) {
//	    		$scope.showErrorMsg("ehl_01_common_022");
//	    		return;
//		    }
	    	$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
	    	$scope.sendRecv("CRM8502", "inquire", "com.systex.jbranch.app.server.fps.crm8502.CRM8502InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
							return;
						}

						$scope.custAssetDocList1=[];
				        $scope.outputVO = {};
//						console.log(JSON.stringify(tota[0].body.custAssetDocList1));
						if((tota[0].body.custAssetDocList1 == null || tota[0].body.custAssetDocList1.length == 0)
								&& (tota[0].body.custAssetDocList2 == null || tota[0].body.custAssetDocList2.length == 0)
								&& (tota[0].body.custAssetPrintHisList == null || tota[0].body.custAssetPrintHisList.length == 0)) {
							$scope.custAssetDocList1 = tota[0].body.custAssetDocList1;
							$scope.outputVO = $scope.custAssetDocList1;
							$scope.custAssetDocList2 = tota[0].body.custAssetDocList2;
							$scope.outputVO2 = $scope.custAssetDocList2;
							$scope.custAssetPrintHisList = tota[0].body.custAssetPrintHisList;
							$scope.outputVO3 = $scope.custAssetPrintHisList;
							$scope.showMsg("ehl_01_common_009");//查無資料
							return;
						}else{
							$scope.custAssetDocList1 = tota[0].body.custAssetDocList1;
							$scope.outputVO = $scope.custAssetDocList1;
							angular.forEach($scope.custAssetDocList1, function(row){
								var applyDate = row.APPLY_DATE==null?"":row.APPLY_DATE.substring(0,10).replace(/\-/g,"");//去除日期中所有"-"
								var seq;//取流水號6碼
								if (row.SEQ.length > 6 ) {
									seq = row.SEQ.substring(row.SEQ.length-6,row.SEQ.length);
								} else {									
									seq = padLeft("0" +row.SEQ,6);
								}								
								row.APPLY_NO = row.CUST_ID + "-" + applyDate + seq;
//								row.CUST_NAME = tota[0].body.custName;

								var today = new Date();
								today = ""+(today.getYear()+1900)
										+((today.getMonth()+1)<10?"0"+(today.getMonth()+1):""+(today.getMonth()+1))
										+((today.getDate())<10?"0"+(today.getDate()):(today.getDate()));
								row.applyStage = (applyDate == today);
							});
							
							$scope.custAssetDocList2=[];
					        $scope.outputVO2 = {};
							$scope.custAssetDocList2 = tota[0].body.custAssetDocList2;
							$scope.outputVO2 = $scope.custAssetDocList2;
							angular.forEach($scope.custAssetDocList2, function(row){
								var applyDate = row.APPLY_DATE==null?"":row.APPLY_DATE.substring(0,10).replace(/\-/g,"");//去除日期中所有"-"
								var seq;//取流水號6碼
								if (row.SEQ.length > 6 ) {
									seq = row.SEQ.substring(row.SEQ.length-6,row.SEQ.length);
								} else {									
									seq = padLeft("0" +row.SEQ,6);
								}								
								row.APPLY_NO = row.CUST_ID + "-" + applyDate + seq;
//								#scope.applyStage = row.ASSET_PRINT_FLAG;
//								row.CUST_NAME = tota[0].body.custName;
//								row.CUST_NAME = tota[0].body.custName;
							});
							$scope.custAssetPrintHisList=[];
					        $scope.outputVO3 = {};
							$scope.custAssetPrintHisList = tota[0].body.custAssetPrintHisList;
							$scope.outputVO3 = $scope.custAssetPrintHisList;
							angular.forEach($scope.custAssetPrintHisList, function(row){
								var applyDate = row.APPLY_DATE==null?"":row.APPLY_DATE.substring(0,10).replace(/\-/g,"");//去除日期中所有"-"
								var seq;//取流水號6碼
								if (row.SEQ.length > 6 ) {
									seq = row.SEQ.substring(row.SEQ.length-6,row.SEQ.length);
								} else {									
									seq = padLeft("0" +row.SEQ,6);
								}								
								row.APPLY_NO = row.CUST_ID + "-" + applyDate + seq+"-"+row.PRINT_COUNT;
//								row.CUST_NAME = tota[0].body.custName;
							});	
							$scope.show_hide = false;						
						}
			});
	    
	    };
	    	    
	    //匯出
	    $scope.exPort = function() {
//	    	console.log("export");
//	    	angular.forEach($scope.custAssetDocList2, function(row){
//	    		row.APPLY_DATE = row.APPLY_DATE.substring(0,10).replace(/\-/g,"/");
//	    		row.PRINT_STATUS = "是";
//	    	});
//	    	angular.forEach($scope.custAssetPrintHisList, function(row){
//	    		row.PRINT_DATE = row.PRINT_DATE.substring(0,10).replace(/\-/g,"/");
//	    		row.APPLY_DATE = row.APPLY_DATE.substring(0,10).replace(/\-/g,"/");
//	    		row.PRINT_STATUS = "是";
//	    	});
	    	$scope.outputVO = {};
	    	$scope.outputVO["custAssetDocList2"]= $scope.outputVO2;
	    	$scope.outputVO["custAssetPrintHisList"]=$scope.outputVO3;
	    	$scope.sendRecv("CRM8502", "exPort", "com.systex.jbranch.app.server.fps.crm8502.CRM8502OutputVO", $scope.outputVO,
					function(tota, isError) {						
			});	 
	    };
	    
	    //列印PDF
	    $scope.printReport = function(row) {
	    	$scope.inputVO.printReportDisabled = true;
	    	
	    	$timeout(function() {
				$scope.do_printReport(row); 
				$scope.inputVO.printReportDisabled = false;}
			, 1000);			    	
	    };
	    
	    $scope.do_printReport = function(row) {
	    	$scope.seq = row.SEQ;
	    	$scope.applyDate = Date.parse(row.APPLY_DATE);
	    	$scope.custID = row.CUST_ID;
	    	$scope.sendRecv("CRM8502", "printReport", "com.systex.jbranch.app.server.fps.crm8502.CRM8502InputVO", {seq: $scope.seq, applyDate: $scope.applyDate,
	    		custID:$scope.custID},
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
							return;
						}
						$scope.inquire();
						
			});
	    }
	    
	    //預覽列印
	    $scope.printReview = function(row) {
	    	$scope.inputVO.printPreviewDisabled = true;
	    	
	    	$timeout(function() {
				$scope.do_printReview(row); 
				$scope.inputVO.printPreviewDisabled = false;}
			, 1000);			
	    };
	    
	    $scope.do_printReview = function(row) {
	    	var dialog = ngDialog.open({
				template: 'assets/txn/CRM8502/CRM8502_PRINT_REVIEW.html',
				className: 'CRM8502',
				showClose: false,
	            controller: ['$scope', function($scope) {
	            	$scope.custID = row.CUST_ID;
	            	$scope.seq = row.SEQ;
	            }]
			});			
	    }
	    
	    //補列印
	    $scope.getFile = function(row) {
	    	$scope.seq = row.SEQ;
	    	$scope.sendRecv("CRM8502", "getFile", "com.systex.jbranch.app.server.fps.crm8502.CRM8502InputVO", {seq: $scope.seq},
					function(tota, isError) {						
			});	 
	    };
		//確認如果是連動來的，請自動查詢待處理事項。
	    $scope.LinkedData = $scope.connector('get','LinkFlag_CRM8502');
	    if ($scope.LinkedData!=undefined) {
		    if ($scope.LinkedData.linked="Y"){
				$scope.inputVO.applySdate = Date.parse($scope.LinkedData.lastDate);
				$scope.inputVO.applyEdate = Date.parse($scope.LinkedData.lastDate);
				$scope.inquire();
	//			$scope.connector('set','LinkFlag_CRM8502','');
			}
	    }
	    
	    //#1898_【客戶資產現況表】開放聯行列印之可行性
	    $scope.conditionChange = function() {
			if($scope.inputVO.custID != "" && $scope.radioValue == "branch"){
				$scope.inputVO.custID = "";
			}else if($scope.inputVO.branchNbr != "" && $scope.radioValue == "cust_id") {
				$scope.inputVO.branchNbr = "";
			}
		};
   
});