/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM211Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM211Controller";
		
		// filter
		getParameter.XML(["MGM.ACT_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.ACT_TYPE'] = totas.data[totas.key.indexOf('MGM.ACT_TYPE')];
			}
		});
		
		// date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		//生效日期
		$scope.eff_dateOptions = {
			maxDate: $scope.inputVO.deadline || $scope.maxDate,
			minDate: $scope.minDate
		};
		
		//截止日期
		$scope.deadlineOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.eff_date || $scope.minDate
		};
		
		//兌換截止日期
		$scope.exc_deadlineOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.deadline
		};
		
		$scope.limitDate = function() {
			$scope.eff_dateOptions.maxDate = $scope.inputVO.deadline || $scope.maxDate;
			$scope.deadlineOptions.minDate = $scope.inputVO.eff_date || $scope.minDate;
			$scope.exc_deadlineOptions.minDate = $scope.inputVO.deadline;
		};
		
		//初始化
		$scope.init = function() {
			
			if($scope.row != undefined){
				//活動維護
//				alert(JSON.stringify($scope.row));
//				$scope.inputVO.act_type = $scope.row.ACT_TYPE;
				$scope.inputVO.act_seq = $scope.row.ACT_SEQ;
				$scope.inputVO.act_name = $scope.row.ACT_NAME;
				$scope.inputVO.eff_date = new Date($scope.row.EFF_DATE);
				$scope.inputVO.deadline = new Date($scope.row.DEADLINE);
				$scope.inputVO.exc_deadline = new Date($scope.row.EXC_DEADLINE);
				$scope.inputVO.act_content = $scope.row.ACT_CONTENT;
				$scope.inputVO.act_approach = $scope.row.ACT_APPROACH;
				$scope.inputVO.precautions = $scope.row.PRECAUTIONS;
				
				//查詢活動附件
				$scope.sendRecv("MGM211", "getActFile", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", {'act_seq': $scope.inputVO.act_seq}, 
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.fileList = tota[0].body.resultList;
								
								angular.forEach($scope.inputVO.fileList, function(row){
									row.rname = row.ACT_FILE_NAME;
								});
							}
				});
				
				//查詢活動(推薦人/被推薦人)簽署表單檔名
				$scope.sendRecv("MGM211", "getSignFormName", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", {'act_seq': $scope.inputVO.act_seq}, 
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length > 0){
									$scope.mgm_form_name = tota[0].body.resultList[0].MGM_FORM_NAME;
									$scope.be_mgm_form_name = tota[0].body.resultList[0].BE_MGM_FORM_NAME;
								}
							}
				});
				
				$scope.inputVO.giftList = [];
				$scope.outputVO = [];
				//查詢活動適用贈品
				$scope.sendRecv("MGM211", "getActGift", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", {'act_seq': $scope.inputVO.act_seq}, 
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.giftList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
							}
				});
				
			}else{
				//活動新增
				$scope.inputVO = {};
				$scope.inputVO.giftList = [];
				$scope.inputVO.listGiftNbr = [];
				$scope.inputVO.fileList = [];
				$scope.outputVO = [];
				$scope.isError = false;				
			}
		}
		$scope.init();
		
		//設定適用贈品
	    $scope.setGift = function () {
//	    	var act_type = $scope.inputVO.act_type;
	    	var act_type = 'M';
	    	var editGiftList = $scope.inputVO.giftList;
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM211/MGM211_GIFT.html',
				className: 'MGM211_GIFT',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.act_type = act_type;
					$scope.editGiftList = editGiftList;
	            }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inputVO.giftList = [];
					$scope.outputVO = [];
					$scope.inputVO.giftList = $scope.connector('get', 'MGM211_giftList');
					$scope.outputVO = $scope.connector('get', 'MGM211_giftList');
					$scope.connector('set', 'MGM211_giftList', null);
				}
			});
	    }
	    
	    //範例.csv下載
	    $scope.getExample = function() {
			$scope.sendRecv("MGM211", "getExample", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", $scope.inputVO, 
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (tota.length > 0) {
							
						}
			});
		};
		
		//上傳VIP名單
		$scope.upload = function(name, rname) {
			$scope.inputVO.listGiftNbr = [];
			$scope.inputVO.file_name = name;
			$scope.inputVO.actual_file_name = rname;
			
			$scope.sendRecv("MGM211", "checkVipList", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", $scope.inputVO, 
					function(tota, isError) {
						if (isError) {
							$scope.isError = true;
						}else{
							$scope.isError = false;
							if(tota[0].body.resultList.length > 0){
								$scope.inputVO.listGiftNbr = tota[0].body.resultList;
//								alert(JSON.stringify($scope.inputVO.listGiftNbr));
							}
						}
			});
		}
		
		//上傳推薦人簽屬表單
		$scope.uploadMgmSignForm = function(name, rname) {
			$scope.inputVO.mgm_form_name = name;
			$scope.inputVO.real_mgm_form_name = rname;
		}
		
		//上傳被推薦人簽屬表單
		$scope.uploadBeMgmSignForm = function(name, rname) {
			$scope.inputVO.be_mgm_form_name = name;
			$scope.inputVO.real_be_mgm_form_name = rname;
		}
		
//		//上傳客戶簽收單
//		$scope.uploadReceipt = function(name, rname) {
//			$scope.inputVO.receipt_name = name;
//			$scope.inputVO.actual_receipt_name = rname;
//		}
		
//		//上傳活動附件
//		$scope.upload = function($event) {
//			$scope.inputVO.fileList = $event.files;
//			$scope.fileList = [];
//			if($scope.inputVO.fileList.length > 0){
//				angular.forEach($scope.inputVO.fileList, function(row){
//					$scope.fileList.push({LABEL: "FILE_NAME", DATA: row.name});
//				});
//			}
//			$event.value = '';	//傳值後將檔案清除
//	    }
		
		//上傳活動附件
	    $scope.openFilePage = function () {
	    	var fileList = [];
	    	if($scope.inputVO.fileList != []){
	    		fileList = $scope.inputVO.fileList;
	    	}
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM211/MGM211_FILE.html',
				className: 'MGM211_FILE',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.fileList = fileList;
	            }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inputVO.fileList = $scope.connector('get', 'MGM211_fileList');
					$scope.connector('set', 'MGM211_fileList', null);
				}
			});
	    }
	    
	    //暫存
	    $scope.tempSave = function() {
	    	if($scope.inputVO.act_seq == undefined || $scope.inputVO.act_seq == ''){
	    		$scope.showErrorMsg("「暫存」需至少輸入活動代碼。");
	    		return;
	    		
	    	} else {
	    		$confirm({text: '本活動設定「暫存」後，活動狀態即為「未生效」！ '}, {size: 'sm'}).then(function() {
	    			$scope.sendRecv("MGM211", "tempSave", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", $scope.inputVO, 
	    					function(tota, isError) {
	    				if (!isError) {
	    					$scope.showMsg("暫存成功");
	    					
	    					if(tota[0].body.resultList != undefined && tota[0].body.resultList.length > 0){
//	    						alert(JSON.stringify(tota[0].body.resultList));
	    						$scope.row = tota[0].body.resultList[0];
	    						$scope.init();	    						
	    					}
//	    					$scope.closeThisDialog('successful');
	    				}
	    			});	    		
	    		});	    		
	    	}
	    }
	    
	    //儲存
	    $scope.save = function() {
	    	//欄位檢核
	    	if($scope.inputVO.act_seq == undefined || $scope.inputVO.act_seq == '' ||
    			$scope.inputVO.act_name == undefined || $scope.inputVO.act_name == '' ||	
    			$scope.inputVO.eff_date == undefined || 
    			$scope.inputVO.deadline == undefined || 
    			$scope.inputVO.exc_deadline == undefined || 
    			($scope.inputVO.mgm_form_name == undefined && $scope.mgm_form_name == undefined) ||
    			($scope.inputVO.be_mgm_form_name == undefined && $scope.be_mgm_form_name == undefined) ||
    			$scope.inputVO.giftList.length == 0){
			    		$scope.showErrorMsg("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
			    		return;
	    	} else {
	    		$confirm({text: '本活動設定「儲存」後，活動即生效！ '}, {size: 'sm'}).then(function() {
	    			$scope.sendRecv("MGM211", "save", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", $scope.inputVO, 
	    					function(tota, isError) {
			    				if (!isError) {
			    					$scope.showMsg("ehl_01_common_025");			//儲存成功
			    					$scope.closeThisDialog('successful');
			    				}
	    			});	    			    			
	    		});
	    	}
	    }
		
		//儲存
//		$scope.save = function() {
////			alert(JSON.stringify($scope.inputVO.giftList));
////			alert(JSON.stringify($scope.inputVO.listGiftNbr));
//			
//			//活動附件刪除序號
//			$scope.inputVO.delFileSeq = $scope.connector('get', 'MGM211_delFileSeq');
//			$scope.connector('set', 'MGM211_delFileSeq', null);
//			
//			//VIP：比對上傳名單之贈品代碼是否存在於適用贈品清單
//			if($scope.inputVO.act_type == 'V' && $scope.row == undefined){
//				$scope.sendRecv("MGM211", "checkVipGift", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", 
//						{'giftList':$scope.inputVO.giftList, 'listGiftNbr':$scope.inputVO.listGiftNbr}, 
//						function(tota, isError) {
//							if (!isError) {
//								$scope.saveAll();				
//							}
//				});				
//			} else {
//				alert($scope.inputVO.actual_receipt_name);
//				$scope.saveAll();		
//			}
//			
//		}
	    
	    //檢視已上傳的空白簽署表單
		$scope.formView = function(formType){
			$scope.sendRecv("MGM211", "formView", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", 
				{'formType': formType, 'act_seq' : $scope.inputVO.act_seq},
					function(tota, isError) {
						if (!isError) {
							var description = tota[0].body.pdfUrl;
							window.open('./INS300_PDF.html?pdfurl=' + description, 'Description');
							return;
						}
			});
		}
		
		//檢核活動代碼是否重複輸入
		$scope.checkActSeq = function(){
			if($scope.inputVO.act_seq != undefined && $scope.inputVO.act_seq != ''){
				$scope.sendRecv("MGM211", "checkActSeq", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", 
					{'act_seq' : $scope.inputVO.act_seq},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length > 0){
									$scope.showErrorMsg('ehl_01_fps950_001',[$scope.inputVO.act_seq]);		//代碼重複：{0}。
									$scope.inputVO.act_seq = undefined;
								}
							}
						});				
			}
		}
});
