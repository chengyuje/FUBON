/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM381Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $q) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "CRM381Controller";

		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.pri_id = projInfoService.getPriID()[0];
			$scope.priList = ['012','013','033','045']; //營運督導、業務處處長、管銷科助理與總行限制一定要選擇分行別條件，避免查詢過久
			$scope.regionOBJ = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.regionOBJ);
	        
	        $scope.AO_CODE_TYPE = [];
	        $scope.AO_CODE_TYPE.push({LABEL: '主Code', DATA: '1'},{LABEL: '副Code', DATA: '2'},{LABEL: '維護Code', DATA: '3'});
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.resultList1 = [];
			$scope.resultList2 = [];
			$scope.resultList3 = [];
		}
		$scope.inquireInit();
		//
		
		$scope.inquire = function() {
			$scope.sendRecv("CRM381", "inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList1.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.resultList1 = tota[0].body.resultList1;
						$scope.outputVO = tota[0].body;
						return;
					}
			});
			$scope.sendRecv("CRM381", "inquire2", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.resultList2 = tota[0].body.resultList2;
					}
			});
			$scope.sendRecv("CRM381", "inquire3", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList3.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								return;
							}
							$scope.resultList3 = tota[0].body.resultList3;
							
							$scope.resultList3[0]['TITLE'] = '經營客戶數上限';
							$scope.resultList3[1]['TITLE'] = 'AUM上限';
							$scope.resultList3[2]['TITLE'] = '恆富理財會員@(3,000萬以上)';
							$scope.resultList3[3]['TITLE'] = '智富理財會員@(1,000 ~ 3,000萬)';
							$scope.resultList3[4]['TITLE'] = '穩富理財會員@(300 ~ 1,000萬)';
							$scope.resultList3[5]['TITLE'] = '一般存戶-跨優@(100 ~ 300萬)';
							$scope.resultList3[6]['TITLE'] = '潛力客群@(100萬以下後Code)';
							
//							$scope.resultList3[0]['TITLE'] = '經營客戶數上限';
//							$scope.resultList3[1]['TITLE'] = 'AUM上限';
//							$scope.resultList3[2]['TITLE'] = '私人銀行@(1,500萬以上)';
//							$scope.resultList3[3]['TITLE'] = '白金@(300 ~ 1,500萬)';
//							$scope.resultList3[4]['TITLE'] = '個人@(100 ~ 300萬)';
//							$scope.resultList3[5]['TITLE'] = '潛力客群@(100萬以下後Code)';
						}
			});
		};
		
		$scope.exportRPT = function(){			
			$scope.sendRecv("CRM381", "export", "com.systex.jbranch.app.server.fps.crm381.CRM381OutputVO", $scope.outputVO,
				function(tota, isError) {						
					if (isError) {
	            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
	            		return;
	            	}		
			});
		};

		//字串分成[]
	    $scope.comma_split = function(value) {
	    	return value.split('@');
	    };
	    
	    
	    
});