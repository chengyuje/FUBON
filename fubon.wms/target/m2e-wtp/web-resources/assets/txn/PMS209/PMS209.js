/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS209Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS209Controller";
		
		$controller('PMS209_CHARTController', {$scope : $scope});
		
		//組織連動
		$controller('PMSRegionController', {$scope: $scope});
		
		// filter
		getParameter.XML(["FPS.PROD_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['FPS.PROD_TYPE'] = totas.data[totas.key.indexOf('FPS.PROD_TYPE')];
			}
		});
        //
		$scope.paramList2=[];
		$scope.clearRes = function() {
			$scope.paramList = [];
			$scope.titleList = [];
			$scope.data = [];
			$scope.outputVO = [];
		}
		
		$scope.init = function(){
			$scope.paramList = [];
			$scope.titleList = [];
			$scope.data = [];
			$scope.outputVO = [];
			
			$scope.inputVO = {
        			region_center_id : '',
        			branch_area_id : '',
        			branch_nbr : '',
        			
        			eTime   	: '',
        			sTime   	: '',
        			aocode  	: '',    //理專
//					branch  	: '',    //分行
//					region  	: '',
//					op      	: '',
					aojob   	: '',    //職級
					ROB     	: '',
					type    	: '',
					type_Q		: '1',
					prodType	: '',     //商品類型
					COMVIEW		: '',
					CHKE        :1
        	};
			
			$scope.mappingSet['type'] = [];
			$scope.mappingSet['type'].push({LABEL: '收益', DATA: '1'},
										   {LABEL: '銷量', DATA : '2'},
										   {LABEL: '達成率', DATA: '3'});
			$scope.mappingSet['param'] = [];
		}
        $scope.init();
        
        $scope.initLoad = function(){
        	$scope.sendRecv("PMS209", "initLoad", "com.systex.jbranch.app.server.fps.pms209.PMS209InputVO", {},
					function(totas, isError) {
						if (!isError) {
							$scope.ymList = totas[0].body.ymList;
							$scope.prodList = totas[0].body.prodList;
							$scope.mappingSet['COMVIEW'] = [];
							angular.forEach($scope.prodList , function(row, index, objs){				            			
								$scope.mappingSet['COMVIEW'].push({LABEL: row.PNAME, DATA: row.PRD_ID});
							});
						};
					}
			);
        
		}
		$scope.initLoad();
		
		$scope.curDate = new Date();
		
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dataMonthChange = function(){
        	if(!$scope.inputVO.eTime){
        		return;
        	}
        	$scope.inputVO.reportDate = $scope.inputVO.eTime;
        	$scope.RegionController_getORG($scope.inputVO);
        };
        
        /***商品視觀表***/
     	$scope.viewProdType = function(){
     		$scope.inputVO.prodID = "";
     		if ($scope.prodList != null && $scope.prodList.length > 0) {
        		$scope.mappingSet['COMVIEW'] = [];	                	
        		angular.forEach($scope.prodList , function(row, index, objs) {
        			if ($scope.inputVO.prodType == '' || $scope.inputVO.prodType == undefined || $scope.inputVO.prodType == null) {
        				$scope.mappingSet['COMVIEW'].push({LABEL: row.PNAME, DATA: row.PRD_ID});
        			} else {
        				if (row.PTYPE == $scope.inputVO.prodType) {
            				$scope.mappingSet['COMVIEW'].push({LABEL: row.PNAME, DATA: row.PRD_ID});
            			}
        			}
    			});
        	}
     	
	    }
     	$scope.viewProdType();
     	
     	/***查詢資料***/
        $scope.inquiredata = function(){
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if ($scope.inputVO.sTime > $scope.inputVO.eTime) {
        		$scope.showErrorMsg('資料迄月小於資料起月');
        		return;
        	}
			$scope.sendRecv("PMS209", "queryData", "com.systex.jbranch.app.server.fps.pms209.PMS209InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = tota[0].body.DATA;
							$scope.titleList = tota[0].body.TITLE;
							
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
     	

		$scope.export = function() {
			$scope.inputVO.paramList = $scope.paramList;
			$scope.inputVO.titleList = $scope.titleList;
			
			$scope.sendRecv("PMS209", "export", "com.systex.jbranch.app.server.fps.pms209.PMS209InputVO", $scope.inputVO, 
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (tota.length > 0) {
						}
			});
		};
		

});
