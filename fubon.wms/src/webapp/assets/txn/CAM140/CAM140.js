/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM140Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM140Controller";
		
		var seqNo = $scope.connector('get','IMP_SEQNO'); // 若為CAM160修改參數、CAM160修改活動，則有此值
		$scope.connector('set','IMP_SEQNO', null);
		var pageID = $scope.connector('get','CAM140PAGE'); // 從哪前來CAM140
		$scope.ISCAM120 = pageID == "CAM120";
		$scope.connector('set','CAM140PAGE', null);
		$scope.data = $scope.connector('get','CAM140');
		$scope.connector('set','CAM140', null);
		var date = new Date();
		
		// combobox
		// 參數取得
        $scope.getParam = function () {
            var deferred = $q.defer();
//          alert('getParam');
            getParameter.XML(["CAM.LEAD_SOURCE", "CAM.CHANNEL_CODE", "CAM.RESPONSE_CODE", "CAM.LEAD_TYPE", "CAM.CAMP_PURPOSE"], function(totas) {
    			if (totas) {
    				$scope.CAMP_PURPOSE_XML = totas.data[totas.key.indexOf('CAM.CAMP_PURPOSE')];
    				$scope.TEMP = totas.data[totas.key.indexOf('CAM.LEAD_SOURCE')];
    				if(pageID == "CAM120") {
    					$scope.LEAD_SOURCE = [];
    					angular.forEach($scope.TEMP, function(row) {
    						if(row.DATA == "01" || row.DATA == "03")
    							$scope.LEAD_SOURCE.push(row);
    					});
    				} else {
    					$scope.LEAD_SOURCE = $scope.TEMP; 
    				}

    				$scope.TEMP2 = totas.data[totas.key.indexOf('CAM.LEAD_TYPE')];
    				if(pageID == "CAM120") {
    					$scope.LEAD_TYPE = $scope.TEMP2;
    				} else {
    					$scope.LEAD_TYPE = []; 
    					angular.forEach($scope.TEMP2, function(row) {
    						if(row.DATA == "03" || row.DATA == "04" || row.DATA == "99" || row.DATA == "UX" || row.DATA == "10")
    							$scope.LEAD_TYPE.push(row);
    					});
    				}
    				
    				$scope.CHANNEL_CODE2 = [];
    				$scope.cc2 = totas.data[totas.key.indexOf('CAM.CHANNEL_CODE')];
    				for (var i = 0; i < $scope.cc2.length; i++) {
    					if ($scope.cc2[i].DATA.indexOf('BM') == 0 || $scope.cc2[i].DATA.indexOf('OPH') == 0 || $scope.cc2[i].DATA.indexOf('SH') == 0 || $scope.cc2[i].DATA.indexOf('B01') == 0 || $scope.cc2[i].DATA.indexOf('P01') == 0) {
    					} else {
    						$scope.CHANNEL_CODE2.push({
    							LABEL: $scope.cc2[i].LABEL,
    							DATA: $scope.cc2[i].DATA
    						});
    					}
    				}
    				
    				$scope.RESPONSE_CODE = totas.data[totas.key.indexOf('CAM.RESPONSE_CODE')];
    				
    				// 取得參數：CAM.CAMP_PURPOSE
    				$scope.CAMP_PURPOSE = [];
    				$scope.sendRecv("CAM140", "getCampPurpose", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {},
    				function(tota, isError) {
    					if (!isError) {
    						$scope.CAMP_PURPOSE = tota[0].body.resultList;
//    						alert(JSON.stringify($scope.CAMP_PURPOSE));
    						deferred.resolve();
    					}
    				});
    			}
    		});
//          deferred.resolve();
            return deferred.promise;
        };
		
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.eDate || $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.sDate || $scope.minDate
		};
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.endDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		};
		
		//字串分成[]
		$scope.comma_split = function(value) {
			if (value == null) {
				return [];
			}
			
			return String(value).split(',');
		};
		
		// 依據所選『名單類型』變更『名單目的』下拉選項
		$scope.getPurpose = function(){
			$scope.inputVO.camp_purpose = undefined;
			$scope.PURPOSE = [];

			if ($scope.inputVO.type != undefined) {
				angular.forEach($scope.CAMP_PURPOSE, function(row) {
					if (row.PARAM_DESC.indexOf($scope.inputVO.type) >= 0) {
						$scope.PURPOSE.push({LABEL:row.PARAM_NAME, DATA: row.PARAM_CODE});
					}
				});
			}
			if ($scope.PURPOSE.length == 0) {
				$scope.PURPOSE = $scope.CAMP_PURPOSE_XML;
			}
			
			if ($scope.camp_purpose_ori != undefined) {
				$scope.inputVO.camp_purpose = angular.copy($scope.camp_purpose_ori);
				$scope.camp_purpose_ori = undefined;
			}
//			alert(JSON.stringify($scope.PURPOSE));
		}
	     
		$scope.init = function(){
			$scope.inputVO = {
					sfaParaID: '',
					camp_id: '',
					camp_name: '' ,
					sDate: undefined,
					eDate: undefined,
					channel: '',
					chkCode: [],
					source_id: '',
					type: '', 
					camp_desc: '',
					lead_para1: 'N',
					lead_para2: '',
					sales_pitch: '',
					gift_camp_id: '',
					fileName: [],
					realfileName: [],
					tempName: '',
					realTempName: '', 
					loginUser: projInfoService.getUserID(),
					loginDate: date.getFullYear() + '-' + (date.getMonth() + 1) + '-' +  date.getDate(), 
					exam_id: '', 
					responseCode: '',
					camp_purpose: undefined
        	};
			
			$scope.sourceFlag = true; //名單來源:欄位是否准許編輯 ture:不可 / false:可
			$scope.columnFlag = true; //欄位是否可編輯 ture:不可 / false:可
			if ($scope.data == "view") {
				alert('view');
				$scope.sourceFlag = true;
				$scope.columnFlag = true;
								
				$scope.sendRecv("CAM140", "getImpDtl", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {seqNo: seqNo, interType: 'view'},
    					function(tota, isError) {
    						if (!isError) {
    							$scope.camp_purpose_ori = tota[0].body.camp_purpose;
    							$scope.inputVO.seqNo = tota[0].body.seqNo;
    							$scope.inputVO.camp_id = tota[0].body.camp_id;
    							$scope.inputVO.camp_name = tota[0].body.camp_name;
    							$scope.inputVO.sDate = $scope.toJsDate(tota[0].body.sDate);
    							$scope.inputVO.eDate = $scope.toJsDate(tota[0].body.eDate);
    							$scope.inputVO.channel = tota[0].body.channel;
    							$scope.inputVO.chkCode = $scope.comma_split(tota[0].body.chkCode);
    							$scope.inputVO.source_id = tota[0].body.source_id;
    							$scope.inputVO.type = tota[0].body.type; 
    							$scope.inputVO.camp_desc = tota[0].body.camp_desc;
    							$scope.inputVO.lead_para1 = tota[0].body.lead_para1;
    							$scope.inputVO.lead_para2 = tota[0].body.lead_para2;
    							$scope.inputVO.sales_pitch = tota[0].body.sales_pitch;
    							$scope.inputVO.gift_camp_id = tota[0].body.gift_camp_id;
    							$scope.inputVO.exam_id = tota[0].body.exam_id;
    							$scope.inputVO.delId = [];
    							$scope.inputVO.responseCode = tota[0].body.response_code;
    							
    							$scope.tempSource = true;
    							return;
    						}
    			});
			} else if ($scope.data == "insert") {
				alert('insert');
				$scope.sourceFlag = false;
				$scope.columnFlag = false;
			} else if ($scope.data == "update") {
				alert('update');
				$scope.sourceFlag = false;
				$scope.columnFlag = false;
				$scope.isUpdate = true;
				
				var sfaParaID = $scope.connector('get','CAM140EDIT');
				$scope.sendRecv("CAM140", "getParameterDtl", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {sfaParaID: sfaParaID},
    					function(tota, isError) {
    						if (!isError) {
    							$scope.camp_purpose_ori = tota[0].body.camp_purpose;
    							$scope.inputVO.sfaParaID = tota[0].body.sfaParaID;
    							$scope.inputVO.camp_id = tota[0].body.camp_id;
    							$scope.inputVO.camp_name = tota[0].body.camp_name;
    							$scope.inputVO.sDate = $scope.toJsDate(tota[0].body.sDate);
    							$scope.inputVO.eDate = $scope.toJsDate(tota[0].body.eDate);
    							$scope.inputVO.channel = tota[0].body.channel;
    							$scope.inputVO.chkCode = $scope.comma_split(tota[0].body.chkCode);
    							$scope.inputVO.source_id = tota[0].body.source_id;
    							$scope.inputVO.responseCode = tota[0].body.response_code;
    							$scope.inputVO.type = tota[0].body.type; 
    							$scope.inputVO.camp_desc = tota[0].body.camp_desc;
    							$scope.inputVO.lead_para1 = tota[0].body.lead_para1;
    							$scope.inputVO.lead_para2 = tota[0].body.lead_para2;
    							$scope.inputVO.sales_pitch = tota[0].body.sales_pitch;
    							$scope.inputVO.gift_camp_id = tota[0].body.gift_camp_id;
    							$scope.inputVO.exam_id = tota[0].body.exam_id;
    							$scope.inputVO.delId = [];
    						
    							$scope.tempSource = true;
    							return;
    						}
    			});
				
				$scope.sendRecv("CAM140", "getParameterUpdateFile", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {sfaParaID: sfaParaID},
    					function(tota, isError) {
    						if (!isError) {
    							$scope.inputVO.fileName = [];
//    							$scope.inputVO.realfileName = [];
    							
    							$.extend($scope.inputVO.fileName, tota[0].body.fileList);
    							$scope.inputVO.fileName = $scope.inputVO.fileName.map(function(e) { return {'DOC_ID':e.DOC_ID,'DOC_NAME':e.DOC_NAME}; });
//    							$.extend($scope.inputVO.realfileName, tota[0].body.fileList);
//    							$scope.inputVO.realfileName = $scope.inputVO.realfileName.map(function(e) { return {'DOC_ID':e.DOC_ID,'DOC_NAME':e.DOC_NAME}; });

    							return;
    						}
    			});
			} else if ($scope.data == "updateImp") {
				$scope.sourceFlag = true;
				$scope.columnFlag = true;
				$scope.disStartDt = true;
				$scope.isUpdate = true;
				
				$scope.sendRecv("CAM140", "getImpDtl", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {seqNo: seqNo, interType: 'updateImp'},
    					function(tota, isError) {
    						if (!isError) {
    							$scope.camp_purpose_ori = tota[0].body.camp_purpose;
    							$scope.inputVO.seqNo = tota[0].body.seqNo;
    							$scope.inputVO.camp_id = tota[0].body.camp_id;
    							$scope.inputVO.camp_name = tota[0].body.camp_name;
    							$scope.inputVO.sDate = $scope.toJsDate(tota[0].body.sDate);
    							$scope.inputVO.eDate = $scope.toJsDate(tota[0].body.eDate);
    							$scope.inputVO.channel = tota[0].body.channel;
    							$scope.inputVO.chkCode = $scope.comma_split(tota[0].body.chkCode);
    							$scope.inputVO.source_id = tota[0].body.source_id;
    							$scope.inputVO.type = tota[0].body.type; 
    							$scope.inputVO.camp_desc = tota[0].body.camp_desc;
    							$scope.inputVO.lead_para1 = tota[0].body.lead_para1;
    							$scope.inputVO.lead_para2 = tota[0].body.lead_para2;
    							$scope.inputVO.sales_pitch = tota[0].body.sales_pitch;
    							$scope.inputVO.gift_camp_id = tota[0].body.gift_camp_id;
    							$scope.inputVO.exam_id = tota[0].body.exam_id;
    							$scope.inputVO.delId = [];
    							$scope.inputVO.responseCode = tota[0].body.response_code;
    						
    							$scope.tempSource = true;
    							return;
    						}
    			});
				
				$scope.sendRecv("CAM140", "getImpUpdateFile", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {seqNo: seqNo},
    					function(tota, isError) {
    						if (!isError) {
    							$scope.inputVO.fileName = [];
//    							$scope.inputVO.realfileName = [];
    							
    							$.extend($scope.inputVO.fileName, tota[0].body.fileList);
    							$scope.inputVO.fileName = $scope.inputVO.fileName.map(function(e) { return {'DOC_ID':e.DOC_ID,'DOC_NAME':e.DOC_NAME}; });
//    							$.extend($scope.inputVO.realfileName, tota[0].body.fileList);
//    							$scope.inputVO.realfileName = $scope.inputVO.realfileName.map(function(e) { return {'DOC_ID':e.DOC_ID,'DOC_NAME':e.DOC_NAME}; });

    							return;
    						}
    			});
			} else {
				$scope.sourceFlag = true; 
				$scope.columnFlag = false;
				
				// 手動匯入
				$scope.inputVO.source_id = '05';
				// 取隨機亂碼
    			$scope.sendRecv("CAM140", "initial", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {},
    					function(tota, isError) {
    						if (!isError) {
    							$scope.inputVO.camp_id = tota[0].body.seqNum;
    							return;
    						}
    			});
			}
			
			$scope.limitDate();
		};
//		$scope.init();
        
        $scope.toggleSelection = function toggleSelection(data) {
        	var idx = $scope.inputVO.chkCode.indexOf(data);
        	if (idx > -1) {
        		$scope.inputVO.chkCode.splice(idx, 1);
        	} else {
        		$scope.inputVO.chkCode.push(data);
        	}
        };
        //===
        
        //=== upload
        $scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.tempName = name;
            	$scope.inputVO.realTempName = rname;
        	}
        };
        //===
        
        // 手動匯入
        $scope.addData = function() {
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg("ehl_01_common_022");
        		return;
        	}
        	if($scope.isUpdate) {
        		$scope.inputVO.action = pageID;
        		$scope.inputVO.seqNo = seqNo; // 若由CAM160前來修改參數設定，則SEQNO有值
        		
        		$scope.sendRecv("CAM140", "updImp", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
								$scope.btnCancel();
							};
    					}
    			);
        	} else {
        		$scope.sendRecv("CAM140", "execBatch", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO,
    					function(totas, isError) {
        					if (!isError) {
        						if ($scope.inputVO.type == 'UX') {
        							$scope.showSuccessMsg("已上傳名單，待名單生效。");
        						} else {
        							$scope.showSuccessMsg("已上傳名單，待主管放行。");
        						}
        						
								$scope.btnCancel();
        					}
    					}
    			);
        	}
	    };
	    
	    // 名單參數維護
	    $scope.addParameter = function() {
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg("ehl_01_common_022");
        		return;
        	}
        	if($scope.isUpdate) {
        		$scope.inputVO.action = pageID;
        		$scope.inputVO.seqNo = seqNo; // 若由CAM160前來修改參數設定，則SEQNO有值
        		
        		$scope.sendRecv("CAM140", "updParameter", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
								$scope.btnCancel();
    	                	};
    					}
    			);
        	} else {
        		$scope.inputVO.action = "checkParameter";
        		$scope.inputVO.checkStatus = false;
        		
        		$scope.sendRecv("CAM140", "addParameter", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO,
    					function(tota, isError) {
    						if (!isError) {
    							$scope.inputVO.action = "";
    							$scope.inputVO.checkStatus = tota[0].body.checkStatus;
    							
    							if ($scope.inputVO.checkStatus) {
    								$scope.showErrorMsg("ehl_01_cam140_003");
    							} else {
    								$scope.sendRecv("CAM140", "addParameter", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO,
    				    					function(totas, isError) {
    				    	                	if (isError) {
    				    	                		$scope.showErrorMsg(totas[0].body.msgData);
    				    	                	}
    				    	                	if (totas.length > 0) {
    				    	                		$scope.showSuccessMsg('ehl_01_common_004');
													$scope.btnCancel();
    				    	                	};
    				    					}
    				    			);
    							}
    							    							
    							return;
    						}
    			});
    			
        	}
	    };
	    
	    $scope.responseCodeDisable = false;
	    $scope.editRes = function() {
	    	if(!$scope.inputVO.camp_id) {
	    		$scope.showErrorMsg("ehl_01_cam140_004");
	    		return;
	    	}
	    	var id = $scope.inputVO.camp_id;
	    	var responseCode = $scope.inputVO.responseCode;
	    	var data = $scope.data;
	    	var dialog = ngDialog.open({
				template: 'assets/txn/CAM140/CAM140_RES.html',
				className: 'CAM140_RES',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.data = data;
                	$scope.camp_id = id;
                	$scope.responseCode = responseCode;
                }]
			}).closePromise.then(function (data) {
				if (data.value != undefined && data.value != 0) {
					$scope.responseCodeDisable = true;
				} else {
					$scope.responseCodeDisable = false;
				}
			});;
		};
		
		$scope.editQUS = function() {
	    	if(!$scope.inputVO.camp_id) {
	    		$scope.showErrorMsg("ehl_01_cam140_004");
	    		return;
	    	}
	    	var id = $scope.inputVO.camp_id;
	    	var exam_id = $scope.inputVO.exam_id;
	    	var dialog = ngDialog.open({
				template: 'assets/txn/CAM140/CAM140_QUS.html',
				className: 'CAM140_QUS',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.camp_id = id;
                	$scope.exam_id = exam_id;
                }]
			}).closePromise.then(function (data) {
				if (data.value != 'cancel') {
					$scope.inputVO.exam_id = data.value;
				}
			});
		};
		
		$scope.btnCancel = function() {
			
			if(typeof pageID === "undefined"){
				pageID = "CAM130";
			}

    		$rootScope.menuItemInfo.url = "assets/txn/" + pageID + "/" + pageID + ".html";
		};
		
		$scope.getExample = function() {
			$scope.sendRecv("CAM140", "getExample", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					
				}
			});
		};
		
		// 2017/4/24
		$scope.removeUpload = function(index) {
			if($scope.isUpdate) {
				var delI = $scope.inputVO.delId.indexOf($scope.inputVO.fileName[index].DOC_ID);
    			if(delI == -1 && $scope.inputVO.fileName[index].DOC_ID != '') {
    				$scope.inputVO.delId.push($scope.inputVO.fileName[index].DOC_ID);
    			}
			}
			$scope.inputVO.fileName.splice(index,1);
        };
		
		$scope.upload = function () {
			var current = $scope.inputVO.fileName;
			var flag = $scope.isUpdate;
			var delList = $scope.inputVO.delId;
			var dialog = ngDialog.open({
				template: 'assets/txn/CAM140/CAM140_UPLOAD.html',
				className: 'CAM140_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.current = current;
                	$scope.flag = flag;
                	$scope.delList = delList;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value != 'cancel') {
					$scope.inputVO.fileName = data.value[0];
					if($scope.isUpdate)
						$scope.inputVO.delId = data.value[1];
				}
			});
		};
		
		$scope.getParam().then(function() {	
			$scope.init();
        });
});
