/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM610_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM610_DETAILController";
		
		$scope.priID = String(sysInfoService.getPriID());
		$scope.roleID = String(projInfoService.getRoleID());
		
		$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
		$scope.sendRecv("CRM610", "initial", "com.systex.jbranch.app.server.fps.crm610.CRM610InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {			
						if(tota[0].body.resultList != null && tota[0].body.resultList.length > 0) {
							$scope.resultList = tota[0].body.resultList;
							$scope.vip_degree = $scope.resultList[0].VIP_DEGREE;
                		}
						
						if(tota[0].body.resultList2 != null && tota[0].body.resultList2.length > 0) {
							$scope.rel_type_desc = tota[0].body.resultList2[0].REL_TYPE_DESC
							if($scope.rel_type_desc == "私人銀行理財"){
								$scope.rel_type_desc = "私人銀行";
							}
								
						}
						
						//#0003219 : 跟關係戶入口"詳細資料/新增異動"相同
//						//透過FAMILY_DEGREE顯示客戶首頁私人銀行人會員(V及A顯示)
						$scope.VATYPE = $scope.connector('get', 'CRM661_VATYPE');
						if($scope.VATYPE == '2') {
							$scope.familynum = true;
							$scope.familynum2 = true;
						} else  {
							$scope.familynum = false;
							$scope.familynum2 = false;
						}
					}
			}
		);
		
		$scope.pagechose = function(chose){
        	switch(chose){
	        	/* 客戶基本資料 */
	        	case 1:
	        		$scope.page_title="客戶基本資料";
	        		$scope.page_inde= "assets/txn/CRM611/CRM611_DETAIL.html";
	        		break;
	    		/* 客戶註記 */
	        	case 2:
	        		$scope.page_title="客戶註記";
	        		$scope.page_inde= "assets/txn/CRM612/CRM612.html";
	        		break;
	    		/* 訪談紀錄 */
	        	case 3:
	        		$scope.page_title="訪談紀錄";
	        		$scope.page_inde= "assets/txn/CRM671/CRM671_detail.html";
	        		break;
        		/* 聯絡方式(即時) */
	        	case 4:
	        		$scope.page_title="聯絡方式(即時)";
	        		$scope.page_inde= "assets/txn/CRM621/CRM621_detail.html";
	        		break;
        		/* 貢獻度分析 */
	        	case 5:
	        		$scope.page_title="貢獻度分析";
	        		$scope.page_inde= "assets/txn/CRM615/CRM615.html";
	        		break;
        		/* 關係戶 */
	        	case 6:
	        		$scope.page_title="關係戶";
	        		$scope.page_inde= "assets/txn/CRM661/CRM661.html";
	        		break;
        		/*家庭會員*/
	        	case 7:
	        		$scope.page_title="家庭會員";
	        		$scope.page_inde= "assets/txn/CRM662/CRM662.html";
	        		break;
        		/*等級變更紀錄查詢*/
	        	case 8:
	        		$scope.page_title="等級變更紀錄查詢";
	        		$scope.page_inde= "assets/txn/CRM613/CRM613.html";
	        		break;
	        	/*信託海外所得查詢*/
	        	case 9:
	        		$scope.page_title="信託海外所得查詢";
	        		$scope.page_inde= "assets/txn/CRM616/CRM616.html";
	        		break;
		        /*深度KYC */
	        	case 10:
	        		$scope.page_title="深度KYC";
	        		$scope.page_inde= "assets/txn/CRM651/CRM651_DETAIL.html";
	        		break;
			    /* 電子通路申請狀態 */
	        	case 11:
	        		$scope.page_title="電子通路申請狀態";
	        		$scope.page_inde= "assets/txn/CRM617/CRM617.html";
	        		break;	
	    		/*最近18個月*/
	        	case 12:
	        		$scope.page_title="最近18個月";
	                $scope.page_inde= "assets/txn/CRM614/CRM614.html";
	        	}
        };
        
		if($scope.connector('get','CRM610_tab')!=undefined){
			$scope.activeJustified = $scope.connector('get','CRM610_tab');
			var choose = $scope.activeJustified+1;
			$scope.pagechose(choose);
		}else{
			$scope.pagechose(1);
		}   
});