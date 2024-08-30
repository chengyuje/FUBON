/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3103_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3103_DETAILController";
        	
		// combobox
    	getParameter.XML(["ORG.AOCODE_TYPE", "CRM.TRS_PRJ_ROT_STEP_STATUS", "CRM.TRS_PRJ_ROT_STATUS_D"], function(totas) {
    		if (totas) {
    			$scope.mappingSet['ORG.AOCODE_TYPE'] = totas.data[totas.key.indexOf('ORG.AOCODE_TYPE')];
    			$scope.mappingSet['CRM.TRS_PRJ_ROT_STEP_STATUS'] = totas.data[totas.key.indexOf('CRM.TRS_PRJ_ROT_STEP_STATUS')];
    			$scope.mappingSet['CRM.TRS_PRJ_ROT_STATUS_D'] = totas.data[totas.key.indexOf('CRM.TRS_PRJ_ROT_STATUS_D')];
    		}
    	});
		
    	$scope.init = function() {
    		//主畫面過來的資料
    		$scope.inputVO = $scope.row;
    		$scope.inputVO.emp_id = $scope.row.EMP_ID;
    		$scope.inputVO.bra_nbr = $scope.row.BRANCH_NBR;
    		
    		$scope.checkVO = {};
    		$scope.inputVO.saveType = "";
    		$scope.inputVO.goCustNum = 0;
    		//登入人員ID
    		$scope.inputVO.loginUserId = projInfoService.getUserID();
    	}
    	$scope.init();
    	
		//查詢明細資料
		$scope.inquireDetail = function() {
			$scope.sendRecv("CRM3103", "inquireDetail", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO",$scope.inputVO,
					function(tota, isError) {
						if(!isError) {
							if (tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
							}
							else {
								$scope.resultList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
								//取得核心客戶數目
								var findGoCust = $filter('filter')($scope.resultList, {GO_CUST_YN: "Y"});
								$scope.inputVO.goCustNum = (findGoCust != null && findGoCust.length > 0) ? findGoCust.length : 0;
		                	}
					   }
				});
		};
		$scope.inquireDetail();
		
		//本頁全選
		$scope.checkrow = function() {
	    	if ($scope.checkVO.clickAll) {
        		angular.forEach($scope.data, function(row){
        			row.GO_CUST_YN = "Y";
    			});
        	} else {
        		angular.forEach($scope.data, function(row){
        			row.GO_CUST_YN = "N";
    			});
        	}
        };
        
        //全選
        $scope.checkrow2 = function() {
        	if ($scope.checkVO.clickAll2) {
        		angular.forEach($scope.resultList, function(row){
        			row.GO_CUST_YN = "Y";
    			});
        	} else {
        		angular.forEach($scope.resultList, function(row){
        			row.GO_CUST_YN = "N";
    			});
        	}
        };
        
        $scope.save = function() {
        	$scope.sendRecv("CRM3103", "save", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO",$scope.inputVO,
        		function(tota, isError) {
					if(!isError) {
					} else {
//						$scope.init();
//						$scope.inquireDetail(); //有錯誤重新整理
						$scope.inputVO.STEP_STATUS = $scope.inputVO.STEP_STATUS_PREV; //狀態回復
					}
					$scope.inquireDetail();
			});
        }
        
        //FC儲存/送出覆核
        $scope.FCSave = function(saveType) {
        	$scope.inputVO.custList = []; //resultList 所以客戶
        	$scope.inputVO.codeList = []; //勾選的核心客戶
        	$scope.inputVO.selectedMList = []; //勾選的核心客戶中為主CODE的客戶
        	$scope.inputVO.selectedSList = []; //勾選的核心客戶中為副CODE的客戶
        	$scope.inputVO.saveType = saveType;
        	$scope.inputVO.STEP_STATUS_PREV = $scope.inputVO.STEP_STATUS;
			
        	debugger
        	
        	$scope.inputVO.codeList = $filter('filter')($scope.resultList, {GO_CUST_YN: "Y"});
        	$scope.inputVO.selectedMList = $filter('filter')($scope.inputVO.codeList, {AO_TYPE: "1"});
        	$scope.inputVO.selectedSList = $filter('filter')($scope.inputVO.codeList, {AO_TYPE: "2"});
        	
        	if(saveType == "1") { //暫存
        		$scope.inputVO.custList = $scope.resultList;  
        	} else if(saveType == "2") { //送出覆核
	        	if($scope.inputVO.STEP_STATUS == "1" || $scope.inputVO.STEP_STATUS == "4" || $scope.inputVO.STEP_STATUS == "5") {
	        		$scope.inputVO.STEP_STATUS = "2"; //狀態改為：第一階段分行主管覆核中
	        		$scope.inputVO.custList = $scope.resultList;  
	        	} 
//	        	else if($scope.inputVO.STEP_STATUS == "6") { //第二階段：理專下載同意書、申請書
//	        		$scope.inputVO.STEP_STATUS = "7"; //狀態改為：第二階段有權以上主管上傳同意書、申請書
//	        	}
        	}
        	
        	$scope.save();
        }
        
        //檢核: FC儲存/送出覆核
        $scope.FCSelected = function(saveType) {
        	//取得核心客戶數目
        	//輪調RM可帶走客戶比例；不得超過其轄下總客戶數之35%，且不得超過50位總客戶數
			var findGoCust = $filter('filter')($scope.resultList, {GO_CUST_YN: "Y"});
			$scope.inputVO.goCustNum = (findGoCust != null && findGoCust.length > 0) ? findGoCust.length : 0;
			var goCustOver35 = (($scope.inputVO.goCustNum / $scope.inputVO.CUST_CNT) > 0.35);
			
			if(goCustOver35 || $scope.inputVO.goCustNum > 50) {
				$scope.showErrorMsg("可帶走客戶數比例不得超過其轄下總客戶數之35%，且不得超過50位總客戶數");
				return;
			} else {
				$scope.FCSave(saveType);
			}
        }
        
        //主管核可
        $scope.approval = function(row) {
        	$scope.inputVO.STEP_STATUS_PREV = $scope.inputVO.STEP_STATUS;
        	$scope.inputVO.ROT_SEQ = 0;
        	
        	if($scope.inputVO.isBMMGR) {
        		if($scope.inputVO.STEP_STATUS == "2") {
        			//2:第一階段：分行主管覆核中  => 3:第一階段：處長覆核中
//        			if($scope.inputVO.GO_CUST_PERC > 30) {
//        				$scope.showErrorMsg("核心客戶比例超過30%");
//        				return;
//        			}
        			$scope.inputVO.STEP_STATUS = "3";
        			$scope.inputVO.saveType = "3";
        		} else if($scope.inputVO.STEP_STATUS == "6") {
        			//第二階段分行主管核可
        			$scope.inputVO.ROT_SEQ = row.ROT_SEQ;
        			$scope.inputVO.saveType = "9";
        		}
        	} else if($scope.inputVO.isARMGR) {
        		//3:第一階段：處長覆核中  => 6:第二階段：進行中 (理專下載同意書、申請書)
        		$scope.inputVO.STEP_STATUS = "6";
        		$scope.inputVO.saveType = "3";
        	}
        	
        	$scope.save();
        }
        
        //主管退回
        $scope.deny = function(row) {
        	$scope.inputVO.STEP_STATUS_PREV = $scope.inputVO.STEP_STATUS;
        	$scope.inputVO.ROT_SEQ = 0;
        	
        	if($scope.inputVO.isBMMGR) {
        		if($scope.inputVO.STEP_STATUS == "2") {
        			//2:第一階段：分行主管覆核中  => 4:第一階段：分行主管退回
        			$scope.inputVO.STEP_STATUS = "4";
        			$scope.inputVO.saveType = "3";
        		} else if($scope.inputVO.STEP_STATUS == "6") {
        			//第二階段分行主管退回
        			$scope.inputVO.ROT_SEQ = row.ROT_SEQ;
        			$scope.inputVO.saveType = "10";
        		}
        	} else if($scope.inputVO.isARMGR) {
        		//3:第一階段：處長覆核中  => 5:第一階段：處長退回
        		$scope.inputVO.STEP_STATUS = "5";
        		$scope.inputVO.saveType = "3";
        	}
        	
        	$scope.save();
        }
        
        //分行人員管理科刪除名單中客戶
        $scope.deleteCust = function(row) {
        	$confirm({text: '是否由名單中刪除此客戶?'}, {size: 'sm'}).then(function () {
        		$scope.inputVO.STEP_STATUS_PREV = $scope.inputVO.STEP_STATUS;
            	$scope.inputVO.ROT_SEQ = row.ROT_SEQ;
            	$scope.inputVO.saveType = "12";
            	
            	$scope.save();
            });
		};
		
        //分行主管/總行編輯明細資料
        $scope.edit = function(row) {
        	var isHeadMgr = $scope.inputVO.isHeadMgr;
        	
        	var dialog = ngDialog.open({
				template: 'assets/txn/CRM3103/CRM3103_EDIT.html',
				className: 'CRM3103_EDIT',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = row;
					$scope.isHeadMgr = isHeadMgr;
				}]
			});
			dialog.closePromise.then(function(data) {
				$scope.inquireDetail();
			});
        }
        
        //檢視已上傳附件
        $scope.viewFile = function(row) {
			$scope.sendRecv("CRM3103", "viewFile", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO", {'ROT_SEQ': row.ROT_SEQ},
				function(totas, isError) {
		        	if (!isError) {
						return;
					}
				}
			);
		};
		
		//下載申請書空白表單
		$scope.download = function(row) {
			//先檢核是否為高齡客戶，高齡客戶跳題
			$scope.sendRecv("CRM512", "isHighAge", "com.systex.jbranch.app.server.fps.crm512.CRM512InputVO", {custID : row.CUST_ID},
					function(tota, isError) {
					if (!isError) {
						if(tota[0].body.isHighAge){
							$scope.showMsg("ehl_02_CRM512_001");
						}
						
						//下載
						$scope.sendRecv("CRM3103", "download", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO", {'ROT_SEQ': row.ROT_SEQ},
								function(totas, isError) {
						        	if (!isError) {
						        		$scope.inquireDetail(); //有變更狀態，重新整理
										return;
									}
						});
					}					
			});
		};
		
		//總行新增客戶至名單中
		$scope.addCust = function() {
			var row = $scope.inputVO;
			row.addCustYN = "Y";
			var isHeadMgr = $scope.inputVO.isHeadMgr;
        	
        	var dialog = ngDialog.open({
				template: 'assets/txn/CRM3103/CRM3103_EDIT.html',
				className: 'CRM3103_EDIT',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = row;
					$scope.isHeadMgr = isHeadMgr;
				}]
			});
			dialog.closePromise.then(function(data) {
				$scope.inquireDetail();
			});
		}
		
		$scope.queryByCust = function() {
			if($scope.inputVO.custId == undefined || $scope.inputVO.custId == null || $scope.inputVO.custId == "") {
				$scope.showErrorMsg("請輸入客戶ID查詢");
				return;
			}
			$scope.inquireDetail();
		}
		
		$scope.initQuery = function() {
			$scope.inputVO.custId = "";
			$scope.inquireDetail();
		}
		
});