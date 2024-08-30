/**
 * 
 */
'use strict';
eSoafApp.controller('KYC410Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "KYC410Controller";
        

     // date picker
		// 有效起始日期
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		};		
		//初始化
        $scope.init = function(){
        	$scope.inputVO = {
        			QUESTION_DESC : '',
        			sDate : undefined,
        			eDate : undefined
        	};
			$scope.limitDate();
//        	$scope.edit=[{LABEL:'修改',DATA:'U'},{LABEL:'刪除',DATA:'D'}];
        };
        $scope.init();
        
        $scope.btnClear = function(){
        	$scope.init();
        }
        //分頁初始化
        $scope.inquireInit = function(){
        	$scope.questionList = [];
        };
        $scope.inquireInit();
        
        $scope.mapData = function(){
        	//題目類型
        	$scope.ngDatasource = projInfoService.mappingSet["SYS.QUESTION_TYPE"];
			var comboboxInputVO = {'param_type': "SYS.QUESTION_TYPE", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.typelist = totas[0].body.result.splice(0,2);
			});
			
			//是/否
			$scope.ngDatasource = projInfoService.mappingSet["COMMON.YES_NO"];
			var comboboxInputVO = {'param_type': "COMMON.YES_NO", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.ynlist = totas[0].body.result;
			});
        }
        $scope.mapData();
        
		//查詢
        $scope.queryData = function(){
        	$scope.sendRecv("KYC410","queryData","com.systex.jbranch.app.server.fps.kyc410.KYC410InputVO",
        			$scope.inputVO,function(tota,isError){
        				if(!isError){
                			$scope.questionList = tota[0].body.qstQustionLst;
                			$scope.outputVO = tota[0].body;
        					if($scope.questionList.length == 0){
                				$scope.showMsg("ehl_01_common_009");
                    			return;
                			}
                			angular.forEach($scope.questionList, function(row, index, objs){
								row.edit = [];
								row.edit.push({LABEL: "修改", DATA: "U"});
								row.edit.push({LABEL: "刪除", DATA: "D"});
//								row.PICTURE='N';
							});
                			angular.forEach($scope.questionList, function(row, index, objs){
                				var ansSplit = row.CORR_ANS.split(" ");
                				angular.forEach(row.Ans, function(rowSub, indexSub, objsSub){
                    				if (ansSplit.indexOf(String(rowSub.ANSWER_SEQ))>-1)
                    					rowSub.CORR_ANS = true;
    							});
							});
							return;
        				}
        	});
        };
        
        $scope.delQuestion = function(QUESTION_VERSION){
        	$scope.inputVO.QUESTION_VERSION = QUESTION_VERSION;
        	$confirm({text: '是否刪除此筆資料!!'},{size: 'sm'}).then(function(){
               	$scope.sendRecv("KYC410","delete","com.systex.jbranch.app.server.fps.kyc410.KYC410InputVO",
            			$scope.inputVO,function(tota,isError){
            			if(isError){
            				$scope.showErrorMsg(tota[0].body.msgData);
            			}
                       	if (tota.length > 0) {
                    		$scope.showSuccessMsg('刪除成功');
                    		$scope.inquireInit();
                    		$scope.queryData();
                    	};
            	});
        	});
        	
         }
        

        
        $scope.edit = function(row){
        	if(row.editto == 'D'){
				$scope.delQuestion(row.QUESTION_VERSION);
				row.editto = "";
				return;
        	}else if(row.editto == 'U'){
        		$scope.btnToUpdate(row,$scope.typelist);
        		row.editto = "";
        		return;
        	}else{
        		return;
        	}
        }
        
        $scope.btnToUpdate = function(row, list){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/KYC410/KYC410_edit.html',
        		className: 'KYC410',
        		controller: ['$scope',function($scope){
        			$scope.type = 'update';
        			$scope.row = row;
        			$scope.list = list;
        		}]
        	});
        	dialog.closePromise.then(function(data){     
        			$scope.inquireInit();
            		$scope.queryData();
        	});
        };
        $scope.add = function(list){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/KYC410/KYC410_edit.html',
        		className: 'KYC410',
        		controller: ['$scope',function($scope){
        			$scope.type = 'add';
        			$scope.list=list;
        		}]
        	});
        	dialog.closePromise.then(function(data){
        		if($scope.questionList.length > 0){
        			$scope.inquireInit();
            		$scope.queryData();
        		}
    			$scope.inquireInit();
    	});
        };
        
	}     
);