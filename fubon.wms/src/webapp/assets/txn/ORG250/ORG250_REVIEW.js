/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG250_REVIEWController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG250_REVIEWController";
		
		$scope.init = function(){
			$scope.inputVO = {
					SEQNO              : '',
					REGION_CENTER_ID   : '',
					BRANCH_AREA_ID     : '',
					BRANCH_NBR         : '',
					EMP_ID             : '',
					EMP_NAME           : '',
					AGENT_ID_1         : '',
					AGENT_ID_2         : '',
					AGENT_ID_3         : '',
					REVIEW_STATUS	   : '',
					CREATETIME         : undefined,
					CREATOR            : '',
					MODIFIER           : '',
					LASTUPDATE         : undefined
			}
			
		};
		$scope.init();
		$scope.mappingSet['act_type'] = [];
		$scope.mappingSet['act_type'].push({LABEL: '新增', DATA:'A'},{LABEL: '修改', DATA: 'M'},{LABEL: '刪除', DATA: 'D'});
		
		$scope.mappingSet['review'] = [];
		$scope.mappingSet['review'].push({LABEL: '待覆核', DATA:'W'},{LABEL: '已覆核', DATA: 'Y'},{LABEL: '退回', DATA: 'N'});
		
		
		$scope.alert = function(row){
			if($scope.row )
			$scope.inputVO.EMP_ID = $scope.row.EMP_ID;
			$scope.inputVO.EMP_NAME = $scope.row.EMP_NAME;
			$scope.sendRecv("ORG250", "alert", "com.systex.jbranch.app.server.fps.org250.ORG250InputVO",$scope.inputVO,
				function(tota, isError) {
			
				if(!isError){
					
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
				
					if(tota.length > 0) {
						$scope.alertLst = tota[0].body.alertLst;
						$scope.outputVO = tota[0].body;
					}
				}
		 	});
		};
		$scope.alert();
});