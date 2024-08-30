/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT910Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT910Controller";
		
		$scope.init = function(){
			$scope.inputVO = {
					INSPRD_ID:$scope.INSPRD_ID,
					INS_RIDER_DLT:$scope.INS_RIDER_DLT,
					FB_COM_YN:$scope.FB_COM_YN,
					COMPANY_NUM:$scope.COMPANY_NUM
			}
			$scope.sendRecv("IOT910","queryINSPRD","com.systex.jbranch.app.server.fps.iot910.IOT910InputVO",
					$scope.inputVO,function(tota,isError){
					if (isError) {
	            		$scope.showErrorMsg(tota[0].body.msgData);
	            	}
					$scope.INSPRDList = tota[0].body.INSPRDList;
					if($scope.INSPRDList.length <=0){
						$scope.showMsg("ehl_01_iot110_003");
					}
			});
		}
		$scope.init();

        $scope.mapData = function(){
	    	//產品類型
	    	$scope.ngDatasource = projInfoService.mappingSet["IOT.PRODUCT_TYPE"];
			var comboboxInputVO = {'param_type': "IOT.PRODUCT_TYPE", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.insprd_typelist = totas[0].body.result;
			});
			
			//主/附約別
			$scope.ngDatasource = projInfoService.mappingSet["PRD.MAIN_RIDER"];
			var comboboxInputVO = {'param_type': "PRD.MAIN_RIDER", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.main_riderlList = totas[0].body.result;
			});
			
			//繳別
			$scope.ngDatasource = projInfoService.mappingSet["IOT.PAY_TYPE"];
			var comboboxInputVO = {'param_type': "IOT.PAY_TYPE", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.pay_typeList = totas[0].body.result;
			});
			
			//保費類型
			$scope.ngDatasource = projInfoService.mappingSet["PRD.FEE_STATE"];
			var comboboxInputVO = {'param_type': "PRD.FEE_STATE", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.class_List = totas[0].body.result;
			});
        }
        $scope.mapData();
		
		$scope.returnINSPRDData = function(row){
			$scope.INSPRDData = row;
			$scope.connector('set','IOT910',$scope.INSPRDData);
			$scope.closeThisDialog(row);
		}

	}
);