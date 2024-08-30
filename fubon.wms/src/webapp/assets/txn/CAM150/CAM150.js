/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM150Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM150Controller";
        
		var vo = {'param_type': 'CAM.CHANNEL_CODE', 'desc': false};
        if(!projInfoService.mappingSet['CAM.CHANNEL_CODE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CAM.CHANNEL_CODE'] = totas[0].body.result;
        			$scope.mappingSet['CAM.CHANNEL_CODE'] = projInfoService.mappingSet['CAM.CHANNEL_CODE'];
        		}
        	});
        } else {
        	$scope.mappingSet['CAM.CHANNEL_CODE'] = projInfoService.mappingSet['CAM.CHANNEL_CODE'];
        }
        	
        
		// 不顯示"無須 放行"	
		var vo = {'param_type': 'CAM.CHECK_STATUS', 'desc': false};
		if(!projInfoService.mappingSet['CAM.CHECK_STATUS']) {
			$scope.requestComboBox(vo, function(totas) {
				if (totas[totas.length - 1].body.result === 'success') {
					var delIndex = totas[0].body.result.map(function(e) { return e.DATA; }).indexOf('00');
	      			totas[0].body.result.splice(delIndex,1);
	      			projInfoService.mappingSet['CAM.CHECK_STATUS'] = totas[0].body.result;
	      			$scope.mappingSet['CAM.CHECK_STATUS'] = projInfoService.mappingSet['CAM.CHECK_STATUS'];
				}
			});
		} else {
        	$scope.mappingSet['CAM.CHECK_STATUS'] = projInfoService.mappingSet['CAM.CHECK_STATUS'];
        }
		
        $scope.startDateOptions = {
			maxDate: $scope.inputVO.eDate,
			minDate: $scope.minDate
		};
        $scope.startDateOptions2 = {
			maxDate: $scope.inputVO.eDate,
			minDate: $scope.inputVO.sDate
		};
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.sDate
		};
		$scope.endDateOptions2 = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.eDate
		};
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate =  $scope.inputVO.eDate || $scope.inputVO.sDate2;
			$scope.startDateOptions2.minDate = $scope.inputVO.sDate;
			
			$scope.endDateOptions.minDate = $scope.inputVO.sDate;
			$scope.endDateOptions.maxDate = $scope.inputVO.eDate2;
			
			$scope.endDateOptions2.minDate = $scope.inputVO.eDate;
		};
		
        $scope.init = function(){
        	$scope.paramList = [];
			$scope.inputVO = {
					campID: '', 
					campName: '',
					source_id: '',
					channel: '', 
					checkStatus: '01', // 待放行
					sDate: undefined, 
					sDate2: undefined, 
					eDate: undefined, 
					eDate2: undefined
        	};
			
			$scope.limitDate();
		};
        $scope.init();
		
        // 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        }
        $scope.inquireInit();
        
        $scope.inquire = function(){
        	console.log('inquire');
        	
        	// 使用部隊-全部FC
			if ($scope.inputVO.channel == "FCALL")
				$scope.inputVO.channel = "FC%";
        	
			$scope.sendRecv("CAM150", "queryData", "com.systex.jbranch.app.server.fps.cam150.CAM150InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.set = [];
								
								if (row.CREATOR != (sysInfoService.getUserID()).trim()) {
									if (row.CHECK_STATUS == "01") {
										row.set.push({LABEL: "放行", DATA: "02"});
									}
								}

								if (row.LEAD_TYPE == "05" || row.LEAD_TYPE == "06" || row.LEAD_TYPE == "07" || row.LEAD_TYPE == "08") {
									// 留資名單不可作廢
									row.set.push({LABEL: "試算", DATA: "01"});
								} else {
									if (row.CHECK_STATUS != "03" && row.IMP_STATUS == "IN") { // 未作廢且未匯入之活動，才可作廢
										row.set.push({LABEL: "作廢", DATA: "03"});
										row.set.push({LABEL: "試算", DATA: "01"});
									}
								}
							});
							return;
						}
			});
		};
		
		// 進入交易先查詢一次
		$scope.inquire();
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				$scope.connector('set','CAM140PAGE', "CAM160");
				
				if(row.cmbAction == "01") { //01 試算
					var dialog = ngDialog.open({
						template: 'assets/txn/CAM150/CAM150_SPR.html',
						className: 'CAM150_SPR',
						showClose: false,
		                controller: ['$scope', function($scope) {
		                	$scope.seqNo = row.SEQNO;
		                }]
					}).closePromise.then(function (data) {
						 $scope.inquire();
					});
				} else if(row.cmbAction == "02" || row.cmbAction == "03") { //02 放行
					var checkStatusTemp = row.cmbAction;
					$confirm({text: '是否' + (checkStatusTemp == "02" ? '放行' : '作廢') + '此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CAM150", "updCheckStatus", "com.systex.jbranch.app.server.fps.cam150.CAM150InputVO", {'seqNo': row.SEQNO, checkStatus: checkStatusTemp},
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg( (checkStatusTemp == "02" ? "ehl_01_cam150_001" : "ehl_01_cam150_002"));
                                		$scope.inquireInit();
                                		$scope.inquire();
                                	};
                				}
                		);
					});
				} 
				
				row.cmbAction = "";
			}
		};
		
		$scope.openDtl = function (row, expType) {
			$scope.sendRecv("CAM130", "export", "com.systex.jbranch.app.server.fps.cam130.CAM130InputVO", {'seq': row.SEQNO, 'expType': expType},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                	};
					}
			);
		};
		
		$scope.goDtl = function(row) {
			$scope.connector('set','CAM140PAGE', "CAM160");
			
			$scope.connector('set','CAM140', "view");
    		$scope.connector('set','IMP_SEQNO', row.SEQNO);
    		$scope.connector('set','CAM160_INPUTVO', $scope.inputVO);
    		$rootScope.menuItemInfo.url = "assets/txn/CAM140/CAM140.html";
		};
});
