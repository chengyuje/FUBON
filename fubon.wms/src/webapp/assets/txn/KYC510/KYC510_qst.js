/**
 * 
 */
'use strict';
eSoafApp.controller('KYC510QstController', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "KYC510QstController";
        

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
        			eDate : undefined,
        			EXAM_VERSION : $scope.EXAM_VERSION,
        			preview_data : $scope.preview_data
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
					$scope.typelist = totas[0].body.result;
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
        	$scope.sendRecv("KYC510","queryQstData","com.systex.jbranch.app.server.fps.kyc510.KYC510InputVO",
        			$scope.inputVO,function(tota,isError){
        				if(!isError){
                			$scope.questionList = tota[0].body.qstQustionLst;
                			$scope.outputVO = tota[0].body;
        					if($scope.questionList.length == 0){
                				$scope.showMsg("ehl_01_common_009");
                    			return;
                			}

        				}
        	});
        };
        
        $scope.saveData = function(){
        	$scope.inputVO.selectData = []
        	for(var a=0;a<$scope.questionList.length;a++){
        			if($scope.questionList[a].select == true){
        				$scope.inputVO.selectData.push($scope.questionList[a])
        			}
        		}
        	$scope.closeThisDialog($scope.inputVO.selectData);
//        	$scope.connector('set','KYC214_addData',$scope.inputVO.selectData);
//        	$scope.sendRecv("KYC214","addData","com.systex.jbranch.app.server.fps.kyc214.KYC214InputVO",
//        			$scope.inputVO,function(tota,isError){
//        			$scope.closeThisDialog('successful');
//        	});
        }

        
	}     
);