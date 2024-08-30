/**
 * 
 */
'use strict';
eSoafApp.controller('PRD172Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,$filter,getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD172Controller";
        // 分行留存文件
        var vo = {'param_type': 'PRD.DOC_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['PRD.DOC_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['PRD.DOC_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['PRD.DOC_TYPE'] = projInfoService.mappingSet['PRD.DOC_TYPE'];
        		}
        	});
        } else
        	$scope.mappingSet['PRD.INS_TYPE'] = projInfoService.mappingSet['PRD.INS_TYPE'];
        // 文件重要性
        var vo = {'param_type': 'IOT.DOC_CHK_LEVEL', 'desc': false};
        if(!projInfoService.mappingSet['IOT.DOC_CHK_LEVEL']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['IOT.DOC_CHK_LEVEL'] = totas[0].body.result;
        			$scope.mappingSet['IOT.DOC_CHK_LEVEL'] = projInfoService.mappingSet['IOT.DOC_CHK_LEVEL'];
        		}
        	});
        } else
        	$scope.mappingSet['IOT.DOC_CHK_LEVEL'] = projInfoService.mappingSet['IOT.DOC_CHK_LEVEL'];
        // 登錄種類
        var vo = {'param_type': 'IOT.REG_TYPE34', 'desc': false};
        if(!projInfoService.mappingSet['IOT.REG_TYPE34']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['IOT.REG_TYPE34'] = totas[0].body.result;
        			$scope.mappingSet['REG_TYPE_PRD172'] = projInfoService.mappingSet['IOT.REG_TYPE34'];
        		}
        	});
        } else
        	$scope.mappingSet['REG_TYPE_PRD172'] = projInfoService.mappingSet['IOT.REG_TYPE34'];
        
     // 登錄種類
        var vo = {'param_type': 'IOT.OTH_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['IOT.OTH_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['IOT.OTH_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['IOT.OTH_TYPE'] = projInfoService.mappingSet['IOT.OTH_TYPE'];
        		}
        	});
        } else
        	$scope.mappingSet['IOT.OTH_TYPE'] = projInfoService.mappingSet['IOT.OTH_TYPE'];
        
        //登入種類/其他文件登錄文件種類
//		getParameter.XML(["IOT.REG_TYPE34","IOT.OTH_TYPE"],function(totas){
//			if(totas){
//				//其他文件登錄文件種類
//				$scope.mappingSet['REG_TYPE_PRD172'] = totas.data[0];
//				$scope.mappingSet['IOT.OTH_TYPE'] = totas.data[1];
//			}
//		});

		//初始化
        $scope.init = function(){
        	$scope.inputVO = {
        		Q_TYPE:'',
        		REG_TYPE:''
        	};

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
        	if($scope.inputVO.REG_TYPE != ''){
            	$scope.sendRecv("PRD172","queryData","com.systex.jbranch.app.server.fps.prd172.PRD172InputVO",
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
    								row.edit.push({LABEL: "刪除", DATA: "D"});
    							});
    							return;
            				}
            	});
        	}else{
        		$scope.showErrorMsg('ehl_01_common_022');
        	}
        };
        

        
        $scope.edit = function(index,row){
        	switch (row.editto) {
        	case 'U':
        		var REG_TYPE = $scope.mappingSet['REG_TYPE_PRD172'];
        		var dialog = ngDialog.open({
	        		template: 'assets/txn/PRD172/PRD172_EDIT.html',
	        		className: 'PRD172',
	        		controller: ['$scope',function($scope){
	        			$scope.title_type='Update';
	        			$scope.row_data = row;
	        			$scope.REG_TYPE = REG_TYPE;
	        		}]
	        	});
	        	dialog.closePromise.then(function(data){
	        		$scope.queryData();
	        	});
        		break;
			case 'D':
				var txtMsg = $filter('i18n')('ehl_02_common_001');
				$scope.inputVO.SEQ = row.KEY_NO;
	        	$confirm({text: txtMsg},{size: 'sm'}).then(function(){
	               	$scope.sendRecv("PRD172","deleteData","com.systex.jbranch.app.server.fps.prd172.PRD172EDITInputVO",
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
		
			default:
				break;
			}
        }
        
        $scope.addRow = function(){
        	var REG_TYPE = $scope.mappingSet['REG_TYPE_PRD172'];
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PRD172/PRD172_EDIT.html',
        		className: 'PRD172',
        		controller: ['$scope',function($scope){
        			$scope.title_type='Add';
        			$scope.REG_TYPE = REG_TYPE;
        		}]
        	});
        	dialog.closePromise.then(function(data){
        		$scope.inputVO.REG_TYPE = data.value;
        		$scope.queryData();
        	});

        	
        }

        
	}     
);