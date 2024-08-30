/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS350_EDITController',
	function(sysInfoService,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS350_EDITController";
		
		// combobox
//		getParameter.XML(["PMS.CHANNEL_CODE"], function(totas) {
//			if (totas) {
//				$scope.CHANNEL_CODE = totas.data[totas.key.indexOf('PMS.CHANNEL_CODE')];
//			}
//		});
		//
		var currDate = new Date();
		var year = currDate.getFullYear();
		var month = currDate.getMonth()+1;
		var day = currDate.getDate();
		var hours = currDate.getHours();
		var minutes = currDate.getMinutes();
		var seconds = currDate.getSeconds();
		 var month2 = parseInt(month) + 1;  
         if (month2 == 13) {  
             year = parseInt(year) + 1;  
             month2 = 1;  
         }  
         var day2 = day;  
         var days2 = new Date(year, month2, 0);  
         days2 = days2.getDate();  
         if (day2 > days2) {  
             day2 = days2;  
         }
         if(day2 < 10){
        	 day2 = '0' + day2;
         }
         if (month2 < 10) {  
             month2 = '0' + month2;  
         }
         if (hours < 10) {  
        	 hours = '0' + hours;  
         }
         if (minutes < 10) {  
        	 minutes = '0' + minutes;  
         }
         if (seconds < 10) {  
        	 seconds = '0' + seconds;  
         }
         //公告結束日日期自動帶入下一個月的今天
         var nextMonth = year + '-' + month2 + '-' + day2+' '+hours+':'+minutes+':'+seconds;
		$scope.init = function(){
			if($scope.wtfflag != 'save'){
				$scope.isUpdate = true;
			}else{
				$scope.isUpdate = false;
			}
			$scope.inputVO = {				
				sCreDate: currDate,
				eCreDate: currDate,
				valid: 'N',
				rptName: '',
				rptExplain: '',
				marqueeFlag: 'N',
				marqueeTxt: '',
				fileName: '',
				realFileName: '',
				uploadFlag: 'N',
				RPT_DEPT:'',
				RPT_DEPT_1:'',
				RPT_DEPT_2:'',
				RPT_TYPE:'',
				EXPORT_YN: $scope.row.EXPORT_YN !== 'Y' ? false : true
        	};			
			$scope.check=[];
				$scope.inputVO.seq = $scope.row.SEQ;
				$scope.inputVO.sCreDate = $scope.toJsDate($scope.row.BEGIN_DATE);
				if($scope.wtfflag != 'save'){
					$scope.inputVO.eCreDate = $scope.row.BEGIN_DATE == $scope.row.END_DATE ? $scope.toJsDate(nextMonth) : $scope.toJsDate($scope.row.END_DATE);
				}else{
					$scope.inputVO.eCreDate = $scope.toJsDate(nextMonth);
				}
				$scope.inputVO.valid = $scope.row.VALID_FLAG;
				$scope.inputVO.rptName = $scope.row.RPT_NAME;
				$scope.inputVO.rptExplain = $scope.row.RPT_EXPLAIN;
				$scope.inputVO.marqueeFlag = $scope.row.MARQUEE_FLAG;
				$scope.inputVO.marqueeTxt = $scope.row.MARQUEE_TXT;
//				$scope.inputVO.realFileName = $scope.row.FILENAME;
				$scope.inputVO.RPT_TYPE = $scope.row.RPT_TYPE;
//				$scope.inputVO.UPLOAD_ROLES = $scope.row.UPLOAD_ROLES;
//				$scope.inputVO.USER_ROLES = $scope.row.USER_ROLES;
				$scope.inputVO.RPT_DEPT = $scope.row.RPT_DEPT;
				$scope.inputVO.RPT_DEPT_1 = $scope.row.RPT_DEPT_1;
				$scope.inputVO.RPT_DEPT_2 = $scope.row.RPT_DEPT_2;
				$scope.inputVO.uploadFlag = $scope.uploadFlag;
				$scope.inputVO.NEW_USER_ROLES = $scope.row.NEW_USER_ROLES;
			if($scope.INSERT==true)
				$scope.showcheck=1;
			else if($scope.UPDATE==true)
				$scope.showcheck=2;
		
			$scope.default1=false;
		
//			if($scope.row.default2!=undefined)
//				$scope.default2=$scope.row.default2;
		
		
			$scope.check=false;
			if($scope.x==true) {
				$scope.default1=true;	
			} else {
		
//			for(var i=0;i<$scope.ID.length;++i) {
//				if(sysInfoService.getUserID()==$scope.ID[i]) 
//				$scope.default1=true;
//		  	}
				if($scope.default1==false) {
					if($scope.row.UPLOAD_ROLES!=null) {
						var sp=$scope.row.UPLOAD_ROLES.split("、");	
						for(var i=0;i<sp.length;++i) {
							if(sysInfoService.getPriID()==sp[i])
								$scope.check=true;
						}
			
					}
				}
			}	
			$scope.wtfupload = [];
			$scope.wtfuser = {
				chkCode : [],
				code : []
			}
			if($scope.isUpdate == false){
				$scope.inputVO.valid = 'N';
				$scope.inputVO.rptExplain = '';
				$scope.inputVO.marqueeFlag = 'N';
				$scope.inputVO.marqueeTxt = '';
			}
		};
        $scope.init();
        
        // date picker
		$scope.bgn_sDateOptions = {};
		$scope.bgn_eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate;
		};
		// date picker end
		
		/**upload csv files**/
		$scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.fileName = name;
            	$scope.inputVO.realFileName = rname;
            	$scope.inputVO.uploadFlag = 'Y';
            	$scope.fileChange = 'Y';
        	}
        };
		
        /**download sample files**/
		$scope.downloadSample = function() {			
        	$scope.sendRecv("PMS350", "downloadSample", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
        $scope.save = function() {
        	if(!($scope.onlyflag=='true' || $scope.wtfflag=='true')) {
        		$scope.inputVO.updateUpdater = "Y";	//#5908 由這裡修改，需記錄最後修改人RPT_UPDATER
        	}
        	
        	if($scope.inputVO.rptExplain == ''){
        		$scope.showErrorMsg("欄位檢核錯誤:報表說明為必要輸入欄位");
        		return;
        	}
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if($scope.inputVO.UPLOAD_ROLES == "" || $scope.inputVO.USER_ROLES == ""){
        		$scope.showErrorMsg('欄位檢核錯誤:請選擇使用角色/可上傳角色');
        		return;
        	}
        	if($scope.FILENAME == null){
        		if($scope.wtfflag == undefined || $scope.wtfflag == ""){
            		if($scope.inputVO.realFileName == undefined || $scope.inputVO.realFileName ==""){
       				 $scope.showMsg("欄位檢核錯誤:請選擇檔案");
       				 return;
            		}
            	}
        	}
        	if($scope.wtfflag == true){
        		if($scope.wtfupload.length == 0 || $scope.wtfuser.chkCode == undefined || $scope.wtfuser.chkCode.length == 0){
            		$scope.showMsg("欄位檢核錯誤:請選擇可上傳角色資料及使用角色");
      				return;
            	}
        	}
        	$scope.inputVO.wtfflag = $scope.wtfflag;
        	$scope.inputVO.wtfupload = [];//可上傳角色
        	if($scope.wtfupload.length > 0){
        		angular.forEach($scope.wtfupload, function(row) {
    				$scope.inputVO.wtfupload.push(row.PRIVILEGEID);
    			});
        	}
        	
        	$scope.inputVO.wtfuser = []; //使用角色
        	if($scope.wtfuser.chkCode.length > 0){
        		angular.forEach($scope.wtfuser.chkCode, function(row) {
    				$scope.inputVO.wtfuser.push(row.PRIVILEGEID);
    			});
        	}
        	
			$scope.inputVO.roles = []; //檢視所屬角色
			if($scope.wtfuser.code.length > 0){
				angular.forEach($scope.wtfuser.code, function(row) {
					$scope.inputVO.roles.push(row.PRIVILEGEID);
				});
			}
			
        	//
        	if($scope.isUpdate) {
        		// 上傳使用角色
        		if($scope.inputVO.wtfupload.length == 0){
        			angular.forEach($scope.row.upload_roles_temp, function(row) {
        				$scope.inputVO.wtfupload.push(row.PRIVILEGEID);
        			});
        		}
        		// 使用角色 && 使用角色(檢視所屬資料)
        		if($scope.inputVO.wtfuser.length == 0 && $scope.inputVO.roles.length == 0){
        			angular.forEach($scope.row.use_roles_temp, function(row) {
        				$scope.inputVO.wtfuser.push(row.PRIVILEGEID);
        			});
        			angular.forEach($scope.row.ROLES_temp, function(row) {
    					$scope.inputVO.roles.push(row.PRIVILEGEID);
    				});
        		}
        		
        		$scope.sendRecv("PMS350", "updateRPT", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
    					function(totas, isError) {
		        			if(totas[0].body.errorList==null||totas[0].body.errorList==""){
		        				$scope.reportId = totas[0].body.reportId;
		        				$scope.uploadFlag = totas[0].body.uploadFlag;
		        				if($scope.uploadFlag != 'N' && $scope.fileChange == 'Y'){
		        					$scope.callStored();
		        				}else{
		        					$scope.showMsg("ehl_01_common_002");
		        					$scope.closeThisDialog('successful');
		        				}
							}else{
								$scope.showErrorMsg(totas[0].body.errorList[0]);
							};
    					}
    			);
        	} else {
        		if($scope.inputVO.fileName == '' || $scope.inputVO.realFileName == ''){
            		$scope.showErrorMsg('欄位檢核錯誤：請選擇上傳檔案');
            		return;
            	}
        		if($scope.inputVO.wtfupload.length == 0){
        			angular.forEach($scope.row.upload_roles_temp, function(row) {
        				$scope.inputVO.wtfupload.push(row.PRIVILEGEID);
        			});
        		}
        		if($scope.inputVO.wtfuser.length == 0){
        			angular.forEach($scope.row.use_roles_temp, function(row) {
        				$scope.inputVO.wtfuser.push(row.PRIVILEGEID);
        			});
        		}
        		if($scope.inputVO.roles.length == 0){
        			angular.forEach($scope.row.ROLES_temp, function(row) {
    					$scope.inputVO.roles.push(row.PRIVILEGEID);
    				});
        		}
        		$scope.sendRecv("PMS350", "addRPT", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
        				function(totas, isError) {
		        			if(totas[0].body.errorList==null||totas[0].body.errorList==""){
								$scope.reportId = totas[0].body.reportId;
		        				$scope.callStored();
								$scope.showMsg("ehl_01_common_010");
								$scope.closeThisDialog('cancel');
							}else{
								$scope.showErrorMsg(totas[0].body.errorList[0]);
							};
    					}
    			);         		
        	}        	
        };
        
      //調用存儲過程存excel表
 	   $scope.callStored = function() {
 		    $scope.inputVO.seq = $scope.reportId;
 			$scope.sendRecv("PMS350", "callStored", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
				function(totas, isError) {
					if(totas[0].body.errorList==null||totas[0].body.errorList==""){
						$scope.showMsg("ehl_01_common_010");
						$scope.closeThisDialog('cancel');
					}else{
						$scope.showErrorMsg(totas[0].body.errorList[0]);
					}
				})
 	    };
 	    
        // 2017/7/5 copy from old
        $scope.qu = function(type,name) {
			var dialog = ngDialog.open({			      
				template: 'assets/txn/PMS350/PMS350_INSLIST.html',
				className: 'PMS305_INSLIST',
				controller: ['$scope', function($scope) {
					$scope.name = name;
					$scope.type = type;
				}]
			});     
			dialog.closePromise.then(function (data) {
				if(type == 'uploader')
					$scope.wtfupload = data.value;
	        	if(type == 'user'){
	        		$scope.wtfuser = data.value;
	        		$scope.wtfuser2 = data.value.chkCode;
	        		$scope.roles = data.value.code;
	        	}
	        });
         };
        
                	
});