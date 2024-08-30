/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS410Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS410Controller";
        $scope.mapping = {};
		/** 規劃狀態filter **/
        // getXML
        getParameter.XML([
            'INS.STATUS','INS.SPP_TYPE'], function (totas) {
            if (totas) {
                $scope.mapping.insStatus = totas.data[totas.key.indexOf('INS.STATUS')];
                $scope.mapping.insType = totas.data[totas.key.indexOf('INS.SPP_TYPE')];
            }
        });
        
		/** 設定日期選項 **/
		$scope.startDateOptions = {
    			maxDate: $scope.inputVO.plan_eDate || $scope.maxDate,
    			minDate: $scope.minDate
    	};
    	
		$scope.endDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.inputVO.plan_sDate || $scope.minDate
		};
		
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();   //防止父事件被觸發
				$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
				$scope.startDateOptions.maxDate = $scope.inputVO.plan_eDate || $scope.maxDate;
				$scope.endDateOptions.minDate = $scope.inputVO.plan_sDate || $scope.minDate;
		};
		
		/** 初始化 **/
		$scope.init = function() {
			$scope.inputVO = {
					cust_id: '',				//客戶ID
					status: '',					//狀態
					plan_sDate: undefined,		//期間(起)
					plan_eDate: undefined		//期間(迄)
			}
			$scope.maxDate = undefined;
			$scope.minDate = undefined;	
			$scope.limitDate();
			$scope.resultList = [];
			$scope.outputVO = [];
			
			
		}
		
		
		/** 查詢 **/
		$scope.queryHisPlan = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			$scope.inputVO.cust_id = $scope.connector('get','INS400_cust_id');
			
			if ($scope.inputVO.cust_id == '') {
				$scope.init();
				$scope.showErrorMsg("ehl_01_KYC310_004");	//請輸入客戶ID
				return;
			}
				console.log($scope.inputVO);
				$scope.sendRecv("INS410", "queryHisPlan", "com.systex.jbranch.app.server.fps.ins410.INS410InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length > 0){
									$scope.resultList = tota[0].body.resultList;
									console.log(tota[0].body.resultList);
									$scope.outputVO = tota[0].body;
								}else{
									$scope.showMsg("ehl_01_common_009");	//查無資料
									return;
								}
							}
				});
		}
		
		$scope.openFile =function(row){
//        	$scope.inputVO.planID = row.PLAN_ID;
			$scope.inputVO.sppID = row.SPP_ID;
			$scope.inputVO.sppName = row.SPP_NAME;
			angular.forEach($scope.mapping.insType, function (data, index) {
				if(data.DATA == row.SPP_TYPE) {
					$scope.inputVO.sppType = data.LABEL;
				}
			});
			console.log(JSON.stringify($scope.mapping.insType));
        	$scope.sendRecv('INS410', 'openFile', 'com.systex.jbranch.app.server.fps.ins410.INS410InputVO',
                    $scope.inputVO,
                    function (tota, isError) {
        				if(!isError) {}
//        				$scope.showErrorMsg(tota);
//        				return false;
                    });
			
		}
		$scope.init();
		
		
});
