/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM160Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM160Controller";
		
		// combobox 2016/12/8
		getParameter.XML(["CAM.BULLETIN_TYPE", "CAM.PRD_TYPE", "MKT.CHANNEL_CODE"], function(totas) {
			if (totas) {
				$scope.BULLETIN_TYPE = totas.data[totas.key.indexOf('CAM.BULLETIN_TYPE')];
				$scope.PRD_TYPE = totas.data[totas.key.indexOf('CAM.PRD_TYPE')];
				$scope.CHANNEL_CODE = totas.data[totas.key.indexOf('MKT.CHANNEL_CODE')];
			}
		});
		$scope.branchsDesc = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row) {
			$scope.branchsDesc.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		$scope.branchsDesc.push({LABEL: '總行人員', DATA: '000'});
		//
		
		$scope.init = function (){
			$scope.inputVO = {
					mroleid : '' 
			}
			$scope.resultList_CRM161 = [];
			$scope.resultList_CRM162 = [];
			$scope.resultList_CRM163_1 = []; //日
			$scope.resultList_CRM163_2 = []; //周
			$scope.resultList_CRM163_3 = []; //月
			$scope.resultList_CRM164 = []; 
			$scope.resultList_CRM165_1  = [];
			$scope.resultList_CRM165_2  = [];
			$scope.resultList_CRM165_3  = [];
			$scope.resultList_CRM165_4  = [];
			$scope.resultList_CRM165_5  = [];
			$scope.resultList_CRM165_6  = [];
			$scope.resultList_CRM166 =[];
		};
		$scope.init ();

		$scope.loginRole = function (){
			$scope.sendRecv("CRM160", "login", "com.systex.jbranch.app.server.fps.crm160.CRM160InputVO", {},function(tota, isError) {
				if (!isError) {
					if(tota[0].body.privilege.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					$scope.privilege = tota[0].body.privilege;
					$scope.inputVO.mroleid = $scope.privilege[0].COUNTS;
					
				}
			})
		};
		
		$scope.loginRole();
		$scope.inquire = function(){
			$scope.sendRecv("CRM160", "queryData", "com.systex.jbranch.app.server.fps.crm160.CRM160InputVO", {},
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					if(tota[0].body.resultList !=null && tota[0].body.resultList.length > 0) {
											
						$scope.resultList_CRM161 = _.filter(tota[0].body.resultList, { 'BTYPE' : '01'});
						$scope.resultList_CRM162 = _.filter(tota[0].body.resultList, { 'BTYPE' : '02'});
						$scope.resultList_CRM163_1 = _.filter(tota[0].body.resultList, { 'TYPE' : 1 });
						$scope.resultList_CRM163_2 = _.filter(tota[0].body.resultList, { 'TYPE' : 2 });
						$scope.resultList_CRM163_3 = _.filter(tota[0].body.resultList, { 'TYPE' : 3 });
						$scope.resultList_CRM164 =  _.filter(tota[0].body.resultList, { 'BTYPE' : '04'});
						$scope.resultList_CRM165_1 = _.filter(tota[0].body.resultList, { 'BTYPE' : '05', 'PTYPE':'FUND' });
						$scope.resultList_CRM165_2 = _.filter(tota[0].body.resultList, { 'BTYPE' : '05', 'PTYPE':'ETF'});
						$scope.resultList_CRM165_3 = _.filter(tota[0].body.resultList, { 'BTYPE' : '05', 'PTYPE':'STOCK'});
						$scope.resultList_CRM165_4 = _.filter(tota[0].body.resultList, { 'BTYPE' : '05', 'PTYPE':'BOND'});
						$scope.resultList_CRM165_5 = _.filter(tota[0].body.resultList, { 'BTYPE' : '05', 'PTYPE':'SI'});
						$scope.resultList_CRM165_6 = _.filter(tota[0].body.resultList, { 'BTYPE' : '05', 'PTYPE':'SN'});
						$scope.resultList_CRM165_7 = _.filter(tota[0].body.resultList, { 'BTYPE' : '05', 'PTYPE':'INS'});
						$scope.resultList_CRM166 = _.filter(tota[0].body.resultList, { 'BTYPE' : '06'});
			
					}
					
				})
		};
		
		$scope.inquire();
		
		$scope.detail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/MKT110/MKT110_DETAIL.html',
				className: 'MKT110',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		
});