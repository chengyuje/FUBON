/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MKT111Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MKT111Controller";
		
		// combobox
		getParameter.XML(["MKT.CHANNEL_CODE"], function(totas) {
			if (totas) {
				$scope.CHANNEL_CODEALL = totas.data[totas.key.indexOf('MKT.CHANNEL_CODE')];
				$scope.CHANNEL_CODE = totas.data[totas.key.indexOf('MKT.CHANNEL_CODE')];
			}
		});
		//
		
		var userBranch = projInfoService.getBranchID() == "000" ? "" : projInfoService.getBranchID();
		$scope.init = function() {
			if($scope.row){
        		$scope.isUpdate = true;
        		$scope.inputVO = {};
        		$scope.inputVO.seq = $scope.row.SEQ;
        		$scope.inputVO.orgn = userBranch;
        		// 非同部
        		$scope.inputVO.bType = $scope.row.BTYPE;
        		$scope.sendRecv("MKT110", "inqEdit", "com.systex.jbranch.app.server.fps.mkt110.MKT110InputVO", {seq:$scope.row.SEQ},
    					function(tota, isError) {
    						if (!isError) {
    							if(tota[0].body.resultList.length == 0) {
    								$scope.showMsg("ehl_01_common_001");
    	                			return;
    	                		}
    							var ans = tota[0].body.resultList[0];
    							$scope.inputVO.pType = ans.PTYPE;
    							$scope.inputVO.subject = ans.SUBJECT;
    							$scope.inputVO.sDate = $scope.toJsDate(ans.S_DATE);
    							$scope.inputVO.eDate = $scope.toJsDate(ans.E_DATE);
    							// date picker
    							$scope.sDateOptions.maxDate = $scope.inputVO.eDate;
    							$scope.eDateOptions.minDate = $scope.inputVO.sDate;
    							//
    							$scope.inputVO.contact = ans.CONTACT;
    							$scope.chkCode = ans.ROLE.split(",");
    							var idx = $scope.chkCode.indexOf('ALL');
    				        	if (idx > -1) {
    				        		$scope.clickAll = true;
    				        		angular.forEach($scope.CHANNEL_CODE, function(row) {
    									$scope.toggleSelection(row.DATA);
    				    			});
    				        	}
    							$scope.inputVO.content = ans.CONTENT;
    							$scope.inputVO.delId = [];
    							if(ans.PICTURE) {
    								// $scope.pictureSrc = "data:image/png;base64,"+btoa(String.fromCharCode.apply(null, new Uint8Array(ans.PICTURE)));
    								// String.fromCharCode(null,array) fails if the array buffer gets too big
    								var bufView = new Uint8Array(ans.PICTURE);
        							var length = bufView.length;
        						    var result = '';
        						    var addition = Math.pow(2,16)-1;
        						    for(var i = 0; i<length; i += addition) {
        						    	if(i + addition > length)
        						    		addition = length - i;
        						    	result += String.fromCharCode.apply(null, bufView.subarray(i,i+addition));
        						    }
        						    $scope.pictureSrc = "data:image/png;base64,"+btoa(result);
    								$scope.inputVO.exipicture = "Y";
    							}
    						}
    			});
        		$scope.sendRecv("MKT110", "getUpdateData", "com.systex.jbranch.app.server.fps.mkt110.MKT110InputVO", {seq:$scope.row.SEQ},
    					function(tota, isError) {
    						if (!isError) {
    							$scope.inputVO.pictureName = '';
    							$scope.inputVO.realpictureName = '已儲存的圖片';
    							$scope.inputVO.fileName = [];
    							$scope.inputVO.realfileName = [];
    							$.extend($scope.inputVO.fileName, tota[0].body.resultList);
    							$scope.inputVO.fileName = $scope.inputVO.fileName.map(function(e) { return {'DOC_ID':e.DOC_ID,'DOC_NAME':e.DOC_NAME}; });
    							$.extend($scope.inputVO.realfileName, tota[0].body.resultList);
    							$scope.inputVO.realfileName = $scope.inputVO.realfileName.map(function(e) { return {'DOC_ID':e.DOC_ID,'DOC_NAME':e.DOC_NAME}; });
    							$scope.inputVO.web = tota[0].body.resultList2;
    						}
    			});
        	} else {
        		$scope.inputVO = {
    				orgn: userBranch,
					bType: '',
					pType: '',
					subject: '' ,
					sDate: undefined,
					eDate: undefined,
					contact: '',
					chkCode: [],
					content: '',
					fileName: [],
					realfileName: [],
					webtext: '',
					web: [],
					pictureName: '',
					realpictureName: ''
            	};
        		$scope.chkCode = [];
        	}
		};
		$scope.init();
		
		$scope.check_mkt_code = function() {
			$scope.chkCode = [];
			$scope.CHANNEL_CODE = [];
			
			if($scope.inputVO.bType == '06') {
				$scope.CHANNEL_CODE.push({LABEL:'消金PS', DATA: '004'});
				$scope.CHANNEL_CODE.push({LABEL:'個金AO', DATA: '004AO'});
			} else {
				$.extend($scope.CHANNEL_CODE, $scope.CHANNEL_CODEALL);
			}
		};
		
		$scope.checkrow = function() {
			$scope.chkCode = [];
			if($scope.clickAll) {
				angular.forEach($scope.CHANNEL_CODE, function(row) {
					$scope.toggleSelection(row.DATA);
    			});
			}
		};
		$scope.toggleSelection = function toggleSelection(data) {
        	var idx = $scope.chkCode.indexOf(data);
        	if (idx > -1) {
        		$scope.chkCode.splice(idx, 1);
        	} else {
        		$scope.chkCode.push(data);
        	}
        };
		
        // date picker
		// 有效起始日期
		$scope.sDateOptions = {};
		// 有效截止日期
		$scope.eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		};
		// date picker end
		
		// upload
        $scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.exipicture = "Y";
        		$scope.inputVO.pictureName = name;
            	$scope.inputVO.realpictureName = rname;
        	}
        };
        $scope.uploadFinshed2 = function(name, rname) {
        	if(name){
        		$scope.inputVO.fileName.push({'DOC_ID':'','DOC_NAME':name});
            	$scope.inputVO.realfileName.push({'DOC_ID':'','DOC_NAME':rname});
        	}
        };
        $scope.addWeb = function(data) {
        	$scope.inputVO.web.push({'DOC_ID':'','DOC_NAME':data});
		};
        
		$scope.removePicture = function() {
			$scope.pictureSrc = "";
			$scope.inputVO.exipicture = "";
			$scope.inputVO.pictureName = "";
        	$scope.inputVO.realpictureName = "";
		};
        $scope.removeUpload = function(target, index) {
        	for (var i = 0; i < target.length; i++) {
        		if($scope.isUpdate) {
        			var delI = $scope.inputVO.delId.indexOf(target[i][index].DOC_ID);
        			if(delI == -1 && target[i][index].DOC_ID != '') {
        				$scope.inputVO.delId.push(target[i][index].DOC_ID);
        			}
        		}
        		target[i].splice(index,1);
    		}
        };
        
        $scope.addData = function() {
        	if(!$scope.inputVO.content || $scope.inputVO.content.trim().length == 0) {
        		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if($scope.chkCode.length == 0) {
        		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	// 全選
        	if($scope.clickAll) {
        		$scope.inputVO.chkCode = ['ALL'];
        	} else {
        		$scope.inputVO.chkCode = $scope.chkCode;
        	}
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if($scope.isUpdate) {
        		$scope.sendRecv("MKT110", "editData", "com.systex.jbranch.app.server.fps.mkt110.MKT110InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
        	} else {
        		$scope.sendRecv("MKT110", "addData", "com.systex.jbranch.app.server.fps.mkt110.MKT110InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
        	}
	    };
		
		
});
