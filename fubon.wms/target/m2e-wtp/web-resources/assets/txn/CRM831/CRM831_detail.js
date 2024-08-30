/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM831_detailController',
	function($rootScope, $scope, $controller, getParameter, $confirm, socketService, ngDialog, projInfoService, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM831_detailController";
		
		//設定INS_TYPE參數
//		$scope.mappingSet['CRM.INS_TYPE'] = [{LABEL: "儲蓄型保險", DATA: "1"},
//												{LABEL: "投資型保險", DATA: "2"},
//												{LABEL: "保障型保險", DATA: "3"}
//												];
//		
		//初始化
		$scope.init = function(){
        	$scope.inputVO = {
        			cust_id:  $scope.custID,
        			ins_type: $scope.row.INS_TYPE,
					policy_nbr_str: $scope.row.POLICY_NBR_STR,
					policy_nbr: $scope.row.POLICY_NBR,
					policy_seq: $scope.row.POLICY_SEQ,
					id_dup : $scope.row.ID_DUP
            };
		};
		$scope.init();
		
		//xml參數初始化
		$scope.mapData = function() {
			var deferred = $q.defer();
			getParameter.XML(["PRD.INS_TYPE","PMS.PAY_YQD","CRM.PAY_WAY","CRM.CRM239_CONTRACT_STATUS"], function(totas) {
				if (totas) {
					$scope.insTypeList = totas.data[totas.key.indexOf('PRD.INS_TYPE')];
					$scope.payTypeList = totas.data[totas.key.indexOf('PMS.PAY_YQD')];
					$scope.paywayList = totas.data[totas.key.indexOf('CRM.PAY_WAY')];
					$scope.contractStatusList = totas.data[totas.key.indexOf('CRM.CRM239_CONTRACT_STATUS')];
					deferred.resolve();
				}
			});
			return deferred.promise;
		}
		$scope.mapData().then(function() {$scope.inquire2();});
		
		//初始化
		$scope.inquireInit = function(){
			$scope.resultList = [];
			$scope.resultList2 = [];
		}
		$scope.inquireInit();
		
		//查詢
		$scope.inquire2 = function() {
			$scope.sendRecv("CRM831", "inquire2", "com.systex.jbranch.app.server.fps.crm831.CRM831InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							console.log(tota[0].body.resultList);
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.resultList2 = tota[0].body.resultList2;
							return;
						}
			});
	    };
//	    $scope.inquire2();
		
		
		
		
});