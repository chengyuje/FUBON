'use strict';
eSoafApp.controller('INS960Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS960Controller";
		$scope.mapping = {};
        // getXML
        getParameter.XML([
            'PRD.INS_MAIN_RIDER','INS.UNIT','INS.IS_MAIN'], function (totas) {
            if (totas) {
                $scope.mapping.insMain = totas.data[totas.key.indexOf('PRD.INS_MAIN_RIDER')];
                $scope.mapping.insUnit = totas.data[totas.key.indexOf('INS.UNIT')];
                $scope.mapping.insFubonMain = totas.data[totas.key.indexOf('INS.IS_MAIN')];
            }
        });
		

		$scope.init = function() {
			$scope.insOutputList = [];
			$scope.insDataOutputList = [];
			$scope.inputVO = {};
			$scope.inputVO.checkData = [];
			$scope.inquireInit();
		};
		
		$scope.inquireInit = function(){
			$scope.inputVO.PRD_ID = ($scope.inputVO.prdID || '').toUpperCase();
			console.log($scope.inputVO.prdID);
			debugger
			$scope.sendRecv("INS960", "init", "com.systex.jbranch.app.server.fps.ins960.INS960InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		if(totas[0].body.insDataOutputList.length > 0){
                			$scope.insDataOutputList = totas[0].body.insDataOutputList;
                    		$scope.finishMapping();
                		}
                		$scope.insOutputList = totas[0].body.insOutputList;
                		
                	};
				}
    		);
		};
		
		$scope.finishMapping = function() {
			$scope.outputVO = [];
			$scope.inputVO.PRD_ID = ($scope.inputVO.fubonPrdID || '').toUpperCase();
			$scope.sendRecv("INS960", "mappingList", "com.systex.jbranch.app.server.fps.ins960.INS960InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		if(totas[0].body.outputList.length > 0){
                			$scope.outputList = totas[0].body.outputList;
                    		console.log($scope.outputList);
                    		$scope.outputVO = totas[0].body;
                		}
                		
                	};
				}
    		);
		};
	    
		$scope.save = function() {
			$scope.inputVO.KEY_NO = $scope.inputVO.insCHOICE;
			$scope.inputVO.INSDATA_KEYNO = $scope.inputVO.insDataCHOICE;
			if ($scope.inputVO.INSDATA_KEYNO == undefined) {
		        $scope.showErrorMsg('ehl_01_INS960_001'); //請勾選資訊源商品
		        return;
		      }else if ($scope.inputVO.KEY_NO == undefined) {
		        $scope.showErrorMsg('ehl_01_INS960_002'); //請勾選富邦人壽商品
		        return;
		      }
			console.log($scope.inputVO);
			$scope.sendRecv("INS960", "save", "com.systex.jbranch.app.server.fps.ins960.INS960InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		console.log(totas[0].body);
                		$scope.showMsg('ehl_01_common_001'); // 新增成功
                		$scope.init();
                	};
				}
    		);
		};
		
		$scope.deleteMapping = function() {
			$scope.sendRecv("INS960", "delete", "com.systex.jbranch.app.server.fps.ins960.INS960InputVO",{
				checkData: $scope.outputList.filter(function(row){
					return row.isCheck;
				}) 
			},
				function(totas, isError) {
                	if (!isError) {
                		console.log(totas[0].body);
                		$scope.showErrorMsg('ehl_01_common_003');	//刪除成功
                		$scope.init();
                	};
				}
    		);
		};


        $scope.init();	
});