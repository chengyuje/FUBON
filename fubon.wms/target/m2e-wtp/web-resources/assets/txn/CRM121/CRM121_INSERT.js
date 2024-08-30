/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM121_INSERTController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('PPAPController', {$scope: $scope});
		$scope.controllerName = "CRM121_INSERTController";
		
		$scope.init = function(){
			$scope.loginPriID = sysInfoService.getPriID()[0];
			$scope.inputVO ={
					CUST_ID :'',
					TASK_DATE:undefined,
					TASK_TITLE:'',
					TASK_MEMO:'',
					TASK_STIME:'',
					TASK_ETIME:''
			}
			// 2017/8/4
			$scope.act = "T";
		};
		$scope.init();
		
		$scope.mappingSet['set'] = [];
		
		//FAIAtype =>>> FAIA :輔銷人員  FAIA9 : 輔銷科長
		if ($scope.loginPriID == '001'|| $scope.loginPriID == '002'||  $scope.loginPriID == '003'||
		    $scope.loginPriID == '009'|| $scope.loginPriID == '011') {
//			$scope.mappingSet['set'].push({LABEL: '待辦事項', DATA: 'T'});
			$scope.mappingSet['set'].push({LABEL: '待辦事項', DATA: 'T'}, {LABEL:'銷售計劃', DATA:'A'});
		} 
		else if ($scope.FAIAtype == 'FAIA') {
			$scope.mappingSet['set'].push({LABEL: '待辦事項', DATA: 'T'}, {LABEL:'輔銷提醒', DATA:'R'});
		} 
		else {
			$scope.mappingSet['set'].push({LABEL: '待辦事項', DATA: 'T'});
		}
				
		//輔銷新增待辦事項
		$scope.add = function (){
			$scope.sendRecv("CRM121", "getAddTodo", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
	
						if(totas.length > 0) {
							$scope.closeThisDialog('successful');
						}else{
							$scope.showMsg("ehl_01_common_008");
						}
				})
		};
		
		
		$scope.action = function (){
			
			//銷售計畫
			var CUST_ID = $scope.inputVO.CUST_ID.toUpperCase();
			if($scope.act == 'A'){
				$scope.inputVO.role = 'ao';
				$scope.inputVO.ao_code = String(sysInfoService.getAoCode());
				$scope.inputVO.cust_id = CUST_ID;
				$scope.sendRecv("CRM110", "inquire", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.sendRecv("CRM110", "inquireCust", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.inputVO,
									function(tota, isError) {
										if (!isError) {
											if(tota[0].body.resultList.length == 0) {
												//查無資料
												$scope.showErrorMsg('ehl_01_cus130_002',[$scope.inputVO.cust_id]);			//無此客戶ID：{0}
												return;
											}
											if(tota[0].body.resultList.length == 1) {
												if(tota[0].body.resultList[0].BRA_NBR == null){
													//無歸屬行
													$scope.showErrorMsg('ehl_01_cus130_002',[$scope.inputVO.cust_id]);		//無此客戶ID：{0}
													return;
												}else{
													if(tota[0].body.resultList[0].EMP_NAME == null){
														//空code客戶
														var list = [tota[0].body.resultList[0].BRA_NBR, tota[0].body.resultList[0].BRANCH_NAME];
														$scope.showErrorMsg('ehl_01_cus130_006', list);		//客戶歸屬：{0}-{1}，不提供客戶首頁查詢。
														return;	
													}else{
														//有歸屬行&所屬理專
														var list = [tota[0].body.resultList[0].BRA_NBR, tota[0].body.resultList[0].BRANCH_NAME, tota[0].body.resultList[0].EMP_NAME];
														$scope.showErrorMsg('ehl_01_cus130_005', list);		//客戶歸屬( {0} {1} ) {2} 理專，不提供客戶首頁查詢。
														return;									
													}
												}	
											}											
										}
									});	
								return;
							}
//							//AMC
							$scope.ppap(undefined, undefined, CUST_ID, undefined, '3', 'add');
						}
					});	
			}
			//提醒、待辦事項
			else{
				$scope.closeThisDialog($scope.act);	
			}
		};
				
/**=========================================date picker ==============================================**/
     $scope.DateOptions = {
    		maxDate: $scope.maxDate,
      		minDate: $scope.minDate
	};
   
    //config
	$scope.model = {};
	
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.limitDate = function() {
		$scope.DateOptions.maxDate =  $scope.maxDate;
	};
		
});