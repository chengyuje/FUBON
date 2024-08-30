/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM827_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM827_DETAILController";
		
		//=== filter
		getParameter.XML(["CRM.CRM827_V0023", "FPS.CURRENCY", "CRM.NMVP3A_T0101", "CRM.NMVP3A_T0102", "CRM.MON_REL_INS_TYPE", "CRM.MON_REL_INS_TYPE_1", "CRM.MON_REL_INS_TYPE_2", "CRM.MON_REL_INS_TYPE_3", "CRM.MON_REL_INS_TYPE_4", "CRM.MON_REL_INS_TYPE_5", "CRM.MON_REL_INS_TYPE_6", "CRM.MON_REL_INS_TYPE_7", "CRM.MON_REL_TYPE", "CRM.MON_REL_INS_WAY", "CRM.MON_RES_AREA", "CRM.MON_RES_PROD_TYPE", "CRM.MON_RES_EXDIVIDENT"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM827_V0023'] = totas.data[totas.key.indexOf('CRM.CRM827_V0023')];
				$scope.mappingSet['FPS.CURRENCY'] = totas.data[totas.key.indexOf('FPS.CURRENCY')];
				$scope.mappingSet['CRM.NMVP3A_T0101'] = totas.data[totas.key.indexOf('CRM.NMVP3A_T0101')];
				$scope.mappingSet['CRM.NMVP3A_T0102'] = totas.data[totas.key.indexOf('CRM.NMVP3A_T0102')];
				
				$scope.mappingSet['CRM.MON_REL_INS_TYPE'] = totas.data[totas.key.indexOf('CRM.MON_REL_INS_TYPE')];
				$scope.mappingSet['CRM.MON_REL_INS_TYPE_1'] = totas.data[totas.key.indexOf('CRM.MON_REL_INS_TYPE_1')];
				$scope.mappingSet['CRM.MON_REL_INS_TYPE_2'] = totas.data[totas.key.indexOf('CRM.MON_REL_INS_TYPE_2')];
				$scope.mappingSet['CRM.MON_REL_INS_TYPE_3'] = totas.data[totas.key.indexOf('CRM.MON_REL_INS_TYPE_3')];
				$scope.mappingSet['CRM.MON_REL_INS_TYPE_4'] = totas.data[totas.key.indexOf('CRM.MON_REL_INS_TYPE_4')];
				$scope.mappingSet['CRM.MON_REL_INS_TYPE_5'] = totas.data[totas.key.indexOf('CRM.MON_REL_INS_TYPE_5')];
				$scope.mappingSet['CRM.MON_REL_INS_TYPE_6'] = totas.data[totas.key.indexOf('CRM.MON_REL_INS_TYPE_6')];
				$scope.mappingSet['CRM.MON_REL_INS_TYPE_7'] = totas.data[totas.key.indexOf('CRM.MON_REL_INS_TYPE_7')];
				$scope.mappingSet['CRM.MON_REL_TYPE'] = totas.data[totas.key.indexOf('CRM.MON_REL_TYPE')];
				$scope.mappingSet['CRM.MON_REL_INS_WAY'] = totas.data[totas.key.indexOf('CRM.MON_REL_INS_WAY')];
				$scope.mappingSet['CRM.MON_RES_AREA'] = totas.data[totas.key.indexOf('CRM.MON_RES_AREA')];
				$scope.mappingSet['CRM.MON_RES_PROD_TYPE'] = totas.data[totas.key.indexOf('CRM.MON_RES_PROD_TYPE')];
				$scope.mappingSet['CRM.MON_RES_EXDIVIDENT'] = totas.data[totas.key.indexOf('CRM.MON_RES_EXDIVIDENT')];
			}
		});

		$scope.inputVO.prod_id = $scope.row.DA_ARR03;  //契約編號
		$scope.inputVO.contract_no = $scope.row.DA_ARR03;  //契約編號

		$scope.sendRecv("CRM827", "inquire2", "com.systex.jbranch.app.server.fps.crm827.CRM827InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList_data == null || tota[0].body.resultList_data.length == 0) {
//						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					$scope.resultList_data = tota[0].body.resultList_data;
					$scope.list_NF = [];   //基金
					$scope.list_SN = [];   //海外商品
					$scope.list_SI = [];   //組合式商品
					angular.forEach($scope.resultList_data, function(row, index, objs){
						row.T0104 = row.T0104.trim();
						if (row.T0102 >= '1' && row.T0102 <= '7') {   //基金
							$scope.list_NF.push(row);
						}
						if (row.T0102 == '8') {   //海外商品
							$scope.list_SN.push(row);
						}
						if (row.T0102 == 'A') {   //組合式商品
							$scope.list_SI.push(row);
						}
					});

					return;
				}
		});
		
		$scope.sendRecv("CRM827", "inquireRelatives", "com.systex.jbranch.app.server.fps.crm827.CRM827InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.resultList1 = tota[0].body.resultList1;
					$scope.resultList2 = tota[0].body.resultList2;
					$scope.resultList3 = tota[0].body.resultList3;
					$scope.resultList_data = tota[0].body.resultList_data;
					
					$scope.outputVO1 = tota[0].body.resultList1;
					$scope.outputVO2 = tota[0].body.resultList2;
					$scope.outputVO3 = tota[0].body.resultList3;
					$scope.resOutputVO = tota[0].body.resultList_data;
					
					return;
				}
		});
		
		$scope.numGroups = function(input){
	  		  var i=0;    
	              for(var key in input) i++;      
	  			return i;
	  		};
});
