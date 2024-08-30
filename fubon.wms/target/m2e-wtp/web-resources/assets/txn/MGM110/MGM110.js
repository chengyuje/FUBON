/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM110Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('RegionController', {$scope: $scope});
		
		$scope.controllerName = "MGM110Controller";
		
		$scope.role = sysInfoService.getPriID();
//		$scope.ao_code = String(sysInfoService.getAoCode());
//		$scope.login = sysInfoService.getPriID()[0];
		
		//初始化
		$scope.inquireInit = function(){
			$scope.mgm111 = {};
			$scope.mgm111_file = [];
			$scope.mgm111_gift = [];
			$scope.mgm111_gift_outputVO = [];
		}
		
		$scope.init = function() {
			$scope.inquireInit();
			$scope.activeJustified = 0;
			$scope.page_inde= "assets/txn/MGM111/MGM111.html";
			$scope.connector('set', 'MGM110_inquireVO', null);
			$scope.act_type = undefined;
			$scope.inputVO = {};
			$scope.inputVO.region_center_id = ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
			$scope.inputVO.branch_area_id = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
			$scope.inputVO.branch_nbr = ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
			
			if (projInfoService.getAoCode()[0] != "")
				$scope.inputVO.ao_code = projInfoService.getAoCode()[0];
			
//			["塞空ao_code用Y/N", $scope.inputVO, "區域NAME", "區域LISTNAME", "營運區NAME", "營運區LISTNAME", "分行別NAME", "分行別LISTNAME", "ao_codeNAME", "ao_codeLISTNAME", "emp_idNAME", "emp_idLISTNAME"]
			$scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.test);
	        $scope.inputVO.cust_id = '';
//	        $scope.inputVO.cust_name = '';
	        
	        //取得活動代碼
	        $scope.sendRecv("MGM110", "getActSeq", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.mappingSet['ACT_SEQ'] = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								$scope.mappingSet['ACT_SEQ'].push({LABEL: row.ACT_NAME, DATA: row.ACT_SEQ});
		        			});
							return;
						}
			});
		};
		$scope.init();
		
		//查詢活動詳情
		$scope.inquire = function(){
			$scope.inputVO.branch_list = [];
			angular.forEach($scope.BRANCH_LIST, function(row){
				if(row.DATA != "" && row.DATA != "0"){
					$scope.inputVO.branch_list.push({LABEL: row.LABEL, DATA: row.DATA});					
				}
			});
			
			//推薦人＆被推薦人ID轉大寫
			if($scope.inputVO.cust_id != '' && $scope.inputVO.cust_id != undefined){
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			}
			if($scope.inputVO.bemgm_cust_id != '' && $scope.inputVO.bemgm_cust_id != undefined){
				$scope.inputVO.bemgm_cust_id = $scope.inputVO.bemgm_cust_id.toUpperCase();
			}
			
			$scope.inputVO.act_type = $scope.act_type;
			$scope.connector('set', 'MGM110_inquireVO', $scope.inputVO);
			
			$scope.$broadcast('MGM110_inquire');
		}
		
		//頁籤
		$scope.pagechose = function(chose){
			switch(chose){
        	/* 活動詳情 */
        	case 1:
        		$scope.page_inde= "assets/txn/MGM111/MGM111.html";
        		break;
    		/* 轄下鍵機案件總覽 */
        	case 2:
        		$scope.page_inde= "assets/txn/MGM112/MGM112.html";
        		break;
    		/* 待覆核案件一覽 */
        	case 3:
        		$scope.page_inde= "assets/txn/MGM113/MGM113.html";
        		break;
    		/* 轄下兌換紀錄總覽 */
        	case 4:
        		$scope.page_inde= "assets/txn/MGM114/MGM114.html";
        		break;
    		/* 未兌換客戶數一覽 */
        	case 5:
        		$scope.page_inde= "assets/txn/MGM115/MGM115.html";
        		break;
    		/* 贈品出貨紀錄一覽 */
        	case 6:
        		$scope.page_inde= "assets/txn/MGM116/MGM116.html";
        		break;
        	}
		}
		
		//查詢活動類型 (M：MGM、V：VIP)
		$scope.checkActType = function() {
			if($scope.inputVO.act_seq != undefined && $scope.inputVO.act_seq != ''){
				$scope.sendRecv("MGM110", "checkActType", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'act_seq': $scope.inputVO.act_seq},
						function(tota, isError) {
							if (!isError) {
								$scope.act_type = tota[0].body.resultList[0].ACT_TYPE;
								return;
							}
				});
			}
		}
		
		/*
		 * 新增MGM鍵機
		 * 
		 * 2018-02-22 add by Carley
		 * 
		 */
		$scope.addMGM = function() {
			var act_seq = $scope.inputVO.act_seq;
			if($scope.inputVO.act_seq != undefined && $scope.inputVO.act_seq != ''){
				$scope.sendRecv("MGM110", "checkMmgApplyEndDate", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", 
						{'act_seq' : $scope.inputVO.act_seq},
		    				function(tota, isError) {
		    					if (!isError) {
		    						
		    						var dialog = ngDialog.open({
    									template: 'assets/txn/MGM110/MGM110_MGM.html',
    									className: 'MGM110_MGM',
    									showClose: false,
    									 controller: ['$scope', function($scope) {
    						                	$scope.act_seq = act_seq;
    						             }]
    								});
    								dialog.closePromise.then(function (data) {
    									if(data.value === 'successful'){
    										$scope.inquire();
    									}
    								});
		    						
//		    						if(tota[0].body.resultList.length > 0){
//		    							var mgmApplyEndDate = $scope.toJsDate(tota[0].body.resultList[0].MGM_APPLY_END_DATE);
//		    							var nowDate = (new Date()).yyyyMMdd('/');
//		    							var ApplyEndDate = mgmApplyEndDate.yyyyMMdd('/');
//		    							
//		    							if(nowDate > ApplyEndDate){
//		    								$scope.showErrorMsg('此活動(' + $scope.inputVO.act_seq + ')最後鍵機日為：' + ApplyEndDate);
//		    								
//		    							} else {
//		    								var dialog = ngDialog.open({
//		    									template: 'assets/txn/MGM110/MGM110_MGM.html',
//		    									className: 'MGM110_MGM',
//		    									showClose: false,
//		    									 controller: ['$scope', function($scope) {
//		    						                	$scope.act_seq = act_seq;
//		    						             }]
//		    								});
//		    								dialog.closePromise.then(function (data) {
//		    									if(data.value === 'successful'){
//		    										
//		    									}
//		    								});
//		    							}
//		    						}
		    					}
		    	});
			}
		}
		
		//列印簽署表單
		$scope.printForm = function(formType){
			$scope.sendRecv("MGM110", "printForm", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", 
					{'formType': formType, 'act_seq' : $scope.inputVO.act_seq},
	    				function(tota, isError) {
	    					if (!isError) {
//	    						var description = tota[0].body.pdfUrl;
//	    						window.open('./INS300_PDF.html?pdfurl=' + description, 'Description');
//	    						return;
	    					}
	    	});
		}
});
		