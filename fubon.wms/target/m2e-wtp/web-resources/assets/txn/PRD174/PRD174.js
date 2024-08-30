/**
 * 
 */
'use strict';
eSoafApp.controller('PRD174Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,$filter,getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD174Controller";
        	
     // date picker
		// 有效起始日期
//		$scope.bgn_sDateOptions = {
//			maxDate: $scope.maxDate,
//			minDate: $scope.minDate
//		};
//		$scope.bgn_eDateOptions = {
//			maxDate: $scope.maxDate,
//			minDate: $scope.minDate
//		};
		// config
//		$scope.model = {};
//		$scope.open = function($event, elementOpened) {
//			$event.preventDefault();
//			$event.stopPropagation();
//			$scope.model[elementOpened] = !$scope.model[elementOpened];
//		};
//		$scope.limitDate = function() {
//			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
//			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
//		};		
		//初始化
        $scope.init = function(){
        	$scope.inputVO = {
        		Q_TYPE:'',
        		TEXT_STYLE_B:[],
        		TEXT_STYLE_U:[],
        		TEXT_STYLE_I:[],
        		TEXT_STYLE_A:[]
        	};
//			$scope.limitDate();
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
        	$scope.ngDatasource = projInfoService.mappingSet["PRD.INS_ANCDOC_Q_TYPE"];
			var comboboxInputVO = {'param_type': "PRD.INS_ANCDOC_Q_TYPE", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.typelist = totas[0].body.result;
			});
        }
        $scope.mapData();
        
		//查詢
        $scope.queryData = function(){
        	$scope.sendRecv("PRD174","queryData","com.systex.jbranch.app.server.fps.prd174.PRD174InputVO",
        			$scope.inputVO,function(tota,isError){
        				if(!isError){
                			$scope.ins_ancdoclist = tota[0].body.INS_ANCDOCList;
                			$scope.outputVO = tota[0].body;
        					if($scope.ins_ancdoclist.length == 0){
                				$scope.showMsg("ehl_01_common_009");
                    			return;
                			}
                			angular.forEach($scope.ins_ancdoclist, function(row, index, objs){
								row.edit = [];
			    				row.edit.push({LABEL: "修改", DATA: "U"});
								row.edit.push({LABEL: "複製", DATA: "C"});
								row.edit.push({LABEL: "刪除", DATA: "D"});
							});
							return;
        				}
        	});
        };
        

        
        $scope.edit = function(index,row){
        	switch (row.editto) {
        	case 'U':
        		var dialog = ngDialog.open({
	        		template: 'assets/txn/PRD174/PRD174_EDIT.html',
	        		className: 'PRD174',
	        		controller: ['$scope',function($scope){
	        			$scope.title_type='Update';
	        			$scope.row_data = row;
	        		}]
	        	});
	        	dialog.closePromise.then(function(data){
	        		$scope.queryData();
	        	});
        		break;
			case 'D':
				var txtMsg = $filter('i18n')('ehl_02_common_001');
				$scope.inputVO.Q_ID = row.Q_ID
	        	$confirm({text: txtMsg},{size: 'sm'}).then(function(){
	               	$scope.sendRecv("PRD174","deleteData","com.systex.jbranch.app.server.fps.prd174.PRD174InputVO",
	            			$scope.inputVO,function(tota,isError){
	            			if(isError){
	            				$scope.showErrorMsg(tota[0].body.msgData);
	            			}
	                       	if (tota.length > 0) {
	                    		$scope.showSuccessMsg('ehl_01_common_003');
	                    		$scope.queryData();
	                    	};
	            	});
	        	});				
				break;
			case 'C':
	        	var dialog = ngDialog.open({
	        		template: 'assets/txn/PRD174/PRD174_EDIT.html',
	        		className: 'PRD174',
	        		controller: ['$scope',function($scope){
	        			$scope.title_type='Copy';
	        			$scope.row_data = row;
	        		}]
	        	});
	        	dialog.closePromise.then(function(data){
	        		$scope.queryData();
	        	});
				break;
			default:
				break;
			}
        }
        
        $scope.addRow = function(){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PRD174/PRD174_EDIT.html',
        		className: 'PRD174',
        		controller: ['$scope',function($scope){
        			$scope.title_type='Add';
        		}]
        	});
        	dialog.closePromise.then(function(data){
        		$scope.queryData();
        	});
//        	$scope.ins_ancdoclist.push({});
//        	angular.forEach($scope.ins_ancdoclist, function(row, index, objs){
//				row.edit = [];
//				row.edit.push({LABEL: "複製", DATA: "C"});
//				row.edit.push({LABEL: "刪除", DATA: "D"});
//			});
        	
        }

        $scope.upload = function() {
			var type = $scope.inputVO.P_TYPE;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD174/PRD174_UPLOAD.html',
				className: 'PRD174',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.type = type;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.queryData();
				}
			});
		};
		
		//列印空白表單
        $scope.printBlankRpt = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD174/PRD174_BLANK_RPT.html',
				className: 'PRD174_BLANK_RPT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
		};
	}     
);