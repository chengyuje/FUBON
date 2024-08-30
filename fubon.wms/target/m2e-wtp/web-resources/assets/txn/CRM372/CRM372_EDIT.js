/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM372_EDITController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService ) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM372_EDITController";
		
		// date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		const distinct = (value, index, self) => {
			  return self.indexOf(value) === index
		}
		
		// combobox
		getParameter.XML(["CRM.CHG_TYPE", "CRM.REVIEW_STATUS"], function(totas) {
			if (totas) {
				$scope.crmChgTypeList = totas.data[totas.key.indexOf('CRM.CHG_TYPE')];
				$scope.reviewStatusList = totas.data[totas.key.indexOf('CRM.REVIEW_STATUS')];
				$scope.chgTypeList2= [{"DATA":"M","LABEL":"主要"},{"DATA":"R","LABEL":"參考"},{"DATA":"MR","LABEL":"主要"}];
			}
		});
 		
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		
		$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
			
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.endDate || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.startDate || $scope.minDate;
		};
 		
		$scope.checkID = function() {
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("CRM372", "checkID", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", {'prd_id':$scope.inputVO.prd_id},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.name){
									$scope.showErrorMsg('欄位檢核錯誤:此專案代碼已存在');
									$scope.showPrdIDError = true;		
									$scope.showPrdIDOk = false;
								}else{
									$scope.showPrdIDError = false;		
									$scope.showPrdIDOk = true;
								}	
							}
				});
			}
		};
		
		$scope.createPrjCode = function() {
        	$scope.sendRecv("CRM372", "createPrjCode", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.inputVO.prj_code = tota[0].body.prj_code;
	                	};
			});
        };
				
		$scope.init = function() {
			$scope.checkVO = {
				clickAll: false,
				clickAll2: false
			};
			
			if($scope.row) {
				$scope.inputVO = {
						prj_code:$scope.row.PRJ_CODE,
						prd_name:$scope.row.PRJ_NAME,
						startDate:new Date($scope.row.START_DATE),
						endDate:new Date($scope.row.END_DATE),
						csvDataList:$scope.row.AO_LIST
				};
				
				$scope.isNew = false;
				
				//查詢AO_LIST
				$scope.sendRecv("CRM372", "inquireAOList", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.inputVO.csvDataList = tota[0].body.resultList;
		        			$scope.outputVO = tota[0].body;
		        			
		        			//將資料寫入list 
		        			$scope.mainList=[];
		        			$scope.referenceList=[];
		        			var replyStatusListTmp=[];
		        			angular.forEach($scope.inputVO.csvDataList,function(row,index){
		        				if(row.CHG_TYPE2 == 'M' || row.CHG_TYPE2 == 'MR'){
		        					$scope.mainList.push({EMP_ID: row.EMP_ID, CHG_TYPE2: row.CHG_TYPE2});
		        				} else if(row.CHG_TYPE2 == 'R'){
		        					$scope.referenceList.push({EMP_ID: row.EMP_ID, CHG_TYPE2: row.CHG_TYPE2});
		        				}
		        				if(row.STATUS != 'C'){
		        					replyStatusListTmp.push(row.REGION_CENTER_NAME);
		        				}
		        			});
		        			
		        			//處理顯示那一個處還沒回報完成
		        			var replyStatusList = replyStatusListTmp.filter(distinct);
		        			if(replyStatusList.length >0){
		        				$scope.replyStatus = "尚未回報完成單位：";
			        			for(var i=0;i<replyStatusList.length;i++){
			        				$scope.replyStatus　+= replyStatusList[i]+"|";
			        			}
			        			$scope.replyStatus = $scope.replyStatus.substring(0,$scope.replyStatus.length-1);
		        			}		        			
	                	};
				});
				
			} else {
				$scope.inputVO = {
						prj_code:'',
						prd_name:'',
						startDate:undefined,
						endDate:undefined,
						csvDataList:undefined
				};
				
				$scope.isNew = true;
				//自動產生專案代碼
				$scope.createPrjCode();
				
			}
			
		};
		$scope.init();
		
		$scope.downloadSimple = function() {
        	$scope.sendRecv("CRM372", "downloadSimple", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
		
		$scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        	$scope.inputFile();
        };
		
        $scope.inputFile = function() {
        	$scope.sendRecv("CRM372", "upload", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", $scope.inputVO,
				function(tota, isError) {
	        		if (!isError) {
	        			$scope.inputVO.csvDataList = tota[0].body.resultList;
	        			$scope.outputVO = tota[0].body;
	        			var errorList = tota[0].body.errorList;
	        			
	        			if(errorList.length > 0){
	        				var dialog = ngDialog.open({
	        					template: 'assets/txn/CRM372/CRM372_ERROR.html',
	        					className: 'CRM372',
	        					showClose: false,
	        		            controller: ['$scope', function($scope) {
	        		            	$scope.errorList = errorList;
	        		            }]
	        				});
	        			}
	        			//將資料寫入list 
//	        			$scope.mainList=[];
//	        			$scope.referenceList=[];
//	        			angular.forEach($scope.inputVO.csvDataList,function(row,index){
//	        				if(row.CHG_TYPE2 == 'M'){
//	        					$scope.mainList.push({EMP_ID: row.EMP_ID, CHG_TYPE2: row.CHG_TYPE2});
//	        				} else if(row.CHG_TYPE2 == 'R'){
//	        					$scope.referenceList.push({EMP_ID: row.EMP_ID, CHG_TYPE2: row.CHG_TYPE2});
//	        				}
//	        			});
	            	};
			});
        };
        
        $scope.confirm = function() {
        	if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if(!$scope.inputVO.prj_code){
        		$scope.showErrorMsg('欄位檢核錯誤:專案代碼必要輸入欄位');
        		return;
        	}
        	if(!$scope.inputVO.prd_name){
        		$scope.showErrorMsg('欄位檢核錯誤:商品名稱必要輸入欄位');
        		return;
        	}
        	if(!$scope.inputVO.startDate){
        		$scope.showErrorMsg('欄位檢核錯誤:起始日期必要輸入欄位');
        		return;
        	}
        	if(!$scope.inputVO.endDate){
        		$scope.showErrorMsg('欄位檢核錯誤:結束日期必要輸入欄位');
        		return;
        	}
        	if($scope.inputVO.startDate > $scope.inputVO.endDate){
        		$scope.showErrorMsg('欄位檢核錯誤:起始日期不可以大於結束日期');
        		return;
        	}
        	
        	//將參考名單移入主要名單 CHG_TYPE2 狀態須改為MR
        	var mainListTemp =[];
        	angular.forEach($scope.mainList,function(row,index){
        		mainListTemp.push(row.EMP_ID);
			});
        	
        	angular.forEach($scope.inputVO.csvDataList,function(row,index){
				if((row.CHG_TYPE2 == 'R' ||  row.CHG_TYPE2 == 'MR') && mainListTemp.indexOf(row.EMP_ID)>-1){
					row.CHG_TYPE2 = 'MR';
				} else if (row.CHG_TYPE2 == 'M'){
					row.CHG_TYPE2 = 'M';
				} else {
					row.CHG_TYPE2 = 'R';
				}
			});
        	
			$scope.sendRecv("CRM372", "confirm", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog('successful');
	                	};
			});
        };
                
        //啟用
        $scope.prjEnabled = function() {
        	var error = false;
        	angular.forEach($scope.inputVO.csvDataList ,function(row,index){
        		if(!error){
            		if(!error && (row.CHG_TYPE2 == 'M' || row.CHG_TYPE2 == 'MR') && (row.CHG_TYPE == null || row.CHG_TYPE == '')){
            			error = true;
            			$scope.showErrorMsg('名單內有主要類別理專尚未回報, 無法啟用');
                	}
        		};
			});
        	if(error) return;
        	        	
        	$scope.sendRecv("CRM372", "prjEnabled", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg('ehl_01_common_024');
	                	} else {
	                		$scope.showSuccessMsg('ehl_01_common_023');
	                		$scope.closeThisDialog('successful');
	                	}
			});
        };
		
        $scope.reject = function() {
        	//取出有勾選項目
			var data = $scope.inputVO.csvDataList.filter(function(row) {
				return (row.SELECTED == true);
	    	});
        	if(data.length == 0) {
        		$scope.showMsg('請勾選資料');
        		return;
        	}
        	$scope.sendRecv("CRM372", "reject", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", 
        			{'prj_code' : $scope.inputVO.prj_code,'csvDataList' : data},
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog('successful');
	                	};
			});
        }
        
        //本頁全選
        $scope.checkrow = function() {
        	if ($scope.checkVO.clickAll) {
        		//先將全選取消
        		$scope.checkVO.clickAll2 = false;
            	$scope.checkrow2();
            	
            	//本頁全選
        		angular.forEach($scope.data, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.data, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
        
        //全選
        $scope.checkrow2 = function() {
        	if ($scope.checkVO.clickAll2) {
        		//先將本頁全選取消
        		$scope.checkVO.clickAll = false;
            	$scope.checkrow();
        		
        		angular.forEach($scope.inputVO.csvDataList, function(row) {
        			row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.inputVO.csvDataList, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
});