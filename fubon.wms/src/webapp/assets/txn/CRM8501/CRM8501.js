/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM8501Controller',
	function(sysInfoService, $rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM8501Controller";
		
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
				
		
        //勾選
	    $scope.select_all = function() {
	    	if ($scope.inputVO.printAll == 'Y') {
	    		$scope.inputVO.printSav = 'N';	//存款
	    		$scope.inputVO.printInv = 'N';	//投資
	    		$scope.inputVO.printIns = 'N';	//保險
	    		$scope.inputVO.printLoan = 'N';	//融資
	    		$scope.show_hide_2 = true;
        	} else {
        		$scope.show_hide_2 = false;
        	}        	
        };

    	//問題0003831-理專資況表#3
        //勾選
	    $scope.select_all_chart = function() {
	    	if ($scope.inputVO.printAllChart == 'Y') {
	    		$scope.inputVO.printAUM = 'N';	//AUM圖
	    		$scope.inputVO.printIIL = 'N';	//存投保圖
	    		$scope.inputVO.printCUR = 'N';	//幣別圖
	    		$scope.inputVO.printType = 'N';	//基金市場圖
	    		$scope.inputVO.printFundMkt = 'N';	//基金市場圖
	    		$scope.inputVO.printPortfolio = 'N';	//投資組合圖
	    		$scope.show_hide_3 = true;
        	} else {
        		$scope.show_hide_3 = false;
        	}        	
        };
		//初始化
		$scope.initial = function() {
			$scope.inputVO = {
					custID: '', 						//客戶ID
					assetPrintFlag: '', 				//約定書註記
					printAll: '', 						//全選
					printAllChart: '',					//圖示全選
					printSav: '', 						//存款
					printInv: '', 						//投資
					printIns: '',						//保險					
					printLoan: '',						//融資
					printAUM: '',						//AUM圖
					printIIL: '',						//存投保圖
					printCUR: '',						//幣別圖
					printType: '',						//類別圖
					printFundMkt: '',					//基金市場圖
					printPortfolio: '',					//投資組合圖
					branchNbr:'',						//指定分行
					aoCode: '',							//理專
					fundSortType: 'byCERT_NBR'          //基金排序方式, Default => 基金憑證排序
			};
			$scope.show_hide_1 = false;
			$scope.show_hide_2 = false;
			$scope.show_hide_3 = false;
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "region", "REGION_LIST", "area_id", "AREA_LIST", "branchNbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
	        $scope.isScheduleUpdatedNotToday = false; 
	    };
	    $scope.initial();
   
	    //列印申請
	    $scope.applyPrint = function() {
	    	if ($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
		    }
	    	
	    	if ($scope.inputVO.printAll != 'Y' && $scope.inputVO.printSav != 'Y'
	    		&& $scope.inputVO.printInv != 'Y' && $scope.inputVO.printIns != 'Y'
	    		&& $scope.inputVO.printLoan != 'Y') {
	    		$scope.showErrorMsg("全選或部份明細列印至少勾選一個");
	    		return;
	    	}
	    	
	    	if ($scope.inputVO.fundSortType === '' || $scope.inputVO.fundSortType === undefined || $scope.inputVO.fundSortType === null) {
	    		$scope.showErrorMsg("請選擇基金排序方式");
	    		return;
	    	}
	    	//問題0003831-理專資況表#2
//	    	if ($scope.inputVO.printAUM != 'Y' && $scope.inputVO.printIIL != 'Y'
//	    		&& $scope.inputVO.printCUR != 'Y' && $scope.inputVO.printType != 'Y'
//		    	&& $scope.inputVO.printFundMkt != 'Y' && $scope.inputVO.printPortfolio != 'Y') {
//	    		$scope.showErrorMsg("列印圖示至少勾選一個");
//	    		return;
//	    	}
	    	
	    	$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
	    	$scope.inputVO.isPrint = true;
	    	$scope.sendRecv("CRM8501", "applyPrint", "com.systex.jbranch.app.server.fps.crm8501.CRM8501InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
							return;
						}
						
						if (!isError) {
							debugger;
							if(tota[1].body.custNoteList == null || tota[1].body.custNoteList.length == 0) {
								$scope.showMsg("非轄下客戶,無法申請列印");//查無資料
//								$scope.showMsg("ehl_01_common_009");//查無資料
								return;
							}
							if (tota[1].body.errorMsg != null) {
								$scope.showErrorMsg(tota[1].body.errorMsg);								
								return;
							} else {
								$scope.custNoteList = tota[1].body.custNoteList;
								$scope.inputVO.assetPrintFlag = $scope.custNoteList[0].ASSET_PRINT_FLAG;
								if ($scope.inputVO.assetPrintFlag == 'A') {
									$scope.initial();
									$scope.inputVO.custID = $scope.custNoteList[0].CUST_ID;
									$scope.showMsg("已申請成功");//如已申請則提示該客戶已申請
									return;
								} else if ($scope.inputVO.assetPrintFlag == 'T') {
									$scope.initial();
									$scope.inputVO.custID = $scope.custNoteList[0].CUST_ID;
									$scope.showMsg("該客戶已申請終止");//該客戶已申請終止
									return;
								} 									
							}							
						}
			});
	    	
	    };
	    
		$scope.initQuery = function(){
			$scope.sendRecv("CRM8501", "initQuery", "com.systex.jbranch.app.server.fps.crm8501.CRM8501InputVO", $scope.inputVO,
				function(tota, isError) {
				if (!isError) {
					if(tota[0].body.showMsg){
						$scope.showErrorMsg(tota[0].body.errorMsg);
						$scope.isScheduleUpdatedNotToday = true;
					}
					
				}					
			});				
		};
		$scope.initQuery();
		
		$scope.isHighAge = function(){		
			$scope.sendRecv("CRM512", "isHighAge", "com.systex.jbranch.app.server.fps.crm512.CRM512InputVO", {custID : $scope.inputVO.custID},
					function(tota, isError) {
					if (!isError) {
						if(tota[0].body.isHighAge){
							$scope.showMsg("ehl_02_CRM512_001");
						}
					}					
				});
		}; 
		
	    
});