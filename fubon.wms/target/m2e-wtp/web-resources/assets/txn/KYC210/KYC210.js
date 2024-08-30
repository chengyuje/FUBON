/**
 * 
 */
'use strict';
eSoafApp.controller('KYC210Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,$filter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "KYC210Controller";
        

     // date picker

		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		//初始化
        $scope.init = function(){
        	$scope.inputVO={
        			EXAM_NAME : '',
        			EXAM_VERSION : '',
        			LASTUPDATE : undefined,
        			QUEST_TYPE : '',
        			STATUS : '',
        			edit : undefined,
        			Delete_Data:''
        	}
        	$scope.seq = '';
        	$scope.editType = '';
        };
        $scope.init();
        
        $scope.btnClear = function(){
        	$scope.init();
        }
        

        //分頁初始化
        $scope.inquireInit = function(){
        	$scope.questionList = []
        };
//        $scope.inquireInit();
        
        $scope.mapData = function(){
        	//問卷類別
        	$scope.ngDatasource = projInfoService.mappingSet["KYC.QUEST_STAUS"];
			var comboboxInputVO = {'param_type': "KYC.QUEST_STAUS", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.questtypelist = totas[0].body.result;
			});
        }
        $scope.mapData();
        var curDate = new Date();
		//查詢
        $scope.queryData = function(){
        	$scope.sendRecv("KYC210","queryData","com.systex.jbranch.app.server.fps.kyc210.KYC210InputVO",
        			$scope.inputVO,function(tota,isError){
        				if(!isError){
                			$scope.questionList = tota[0].body.questionList;
                			$scope.outputVO = tota[0].body;
        					if($scope.questionList.length == 0){
                				$scope.showMsg("ehl_01_common_009");
                    			return;
                			}
                			angular.forEach($scope.questionList, function(row, index, objs){
								row.edit = [];
								if(row.STATUS=='01'){ // 修改待審核
									row.edit.push({LABEL: "修改問卷", DATA: "U"});
									row.edit.push({LABEL: "刪除問卷", DATA: "D"});
									row.edit.push({LABEL: "預覽問卷", DATA: "P"});
									row.edit.push({LABEL: "複製問卷", DATA: "C"});
								}
								if(row.STATUS=='02'){ // 已修改待上限
									// 啟用日 > SysDate則未上限
									if(Date.parse(row.ACTIVE_DATE).valueOf() > Date.parse(curDate).valueOf())
										row.edit.push({LABEL: "刪除問卷", DATA: "D"});
									row.edit.push({LABEL: "預覽問卷", DATA: "P"});
									row.edit.push({LABEL: "複製問卷", DATA: "C"});
								}
								if(row.STATUS=='04'){ // 主管退回
									row.edit.push({LABEL: "刪除問卷", DATA: "D"});
									row.edit.push({LABEL: "預覽問卷", DATA: "P"});
									row.edit.push({LABEL: "複製問卷", DATA: "C"});
								}
								if(row.STATUS=='05'){ // 已刪除
									row.edit.push({LABEL: "預覽問卷", DATA: "P"});
									row.edit.push({LABEL: "複製問卷", DATA: "C"});
								}
							});
							return;
        				}
        	});
        };
        
        $scope.edit = function(row){
        	
        	if($scope.seq != row.EXAM_VERSION || $scope.editType != row.editto){
        		switch (row.editto) {
    			case 'P':
    				$scope.preview(row);
    				break;
    			case 'U':
    				$scope.update(row)
    				break;
    			case 'D':
    				$scope.deleteData(row);
    				row.editto = "";
    				break;
    			case 'C':
    				$scope.copy(row);
    				break;
    			default:
    				break;
    			}
        	}
        	$scope.seq = row.EXAM_VERSION;
        	$scope.editType = row.editto;
        }
        //預覽
        $scope.preview = function(row){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/KYC211/KYC211.html',
        		className: 'KYC211',
        		controller: ['$scope',function($scope){
        			$scope.titleType = 'P';
        			$scope.row = row;
        		}]
        	});
        	dialog.closePromise.then(function(data){
    			$scope.inquireInit();
        		$scope.queryData();
        		$scope.seq = '';
            	$scope.editType = '';
        	});
        }
        
        //新增
        $scope.addQuestionnaire = function(){
        	var exam_version ='KYC'+$filter('date')(new Date,'yyyyMMdd')+(String)(Math.floor((Math.random() * 10000) + 1));
        	if(exam_version.length<15){
        		exam_version=exam_version+1;
        	}
        	var dialog = ngDialog.open({
        		template: 'assets/txn/KYC211/KYC211.html',
        		className: 'KYC211',
        		controller: ['$scope',function($scope){
        			$scope.titleType = 'A';
        			$scope.exam_version = exam_version;
        		}]
        	});
        	dialog.closePromise.then(function(data){
        		if($scope.questionList.length>0){
        			$scope.inquireInit();
            		$scope.queryData();
        		}
    			$scope.inquireInit();
    			$scope.seq = '';
            	$scope.editType = '';
        	});
        }
        
        //修改
        $scope.update = function(row){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/KYC211/KYC211.html',
        		className: 'KYC211',
        		controller:['$scope',function($scope){
        			$scope.titleType = 'U'
        			$scope.row = row;
        		}]
        	});
        	dialog.closePromise.then(function(data){
        		if($scope.questionList.length>0){
        			$scope.inquireInit();
            		$scope.queryData();
        		}
    			$scope.inquireInit();
    			$scope.seq = '';
            	$scope.editType = '';
        	});
        }
        //複製
        $scope.copy = function(row){
        	var exam_version ='KYC'+$filter('date')(new Date,'yyyyMMdd')+(String)(Math.floor((Math.random() * 10000) + 1));
        	if(exam_version.length<15){
        		exam_version=exam_version+1;
        	}
        	var dialog = ngDialog.open({
        		template: 'assets/txn/KYC211/KYC211.html',
        		className: 'KYC211',
        		controller:['$scope',function($scope){
        			$scope.titleType = 'C'
        			$scope.row = row;
        			$scope.exam_version = exam_version;
        		}]
        	});
        	dialog.closePromise.then(function(data){
        		if($scope.questionList.length>0){
        			$scope.inquireInit();
            		$scope.queryData();
        		}
    			$scope.inquireInit();
    			$scope.seq = '';
            	$scope.editType = '';
        	});
        }
        //刪除
        $scope.deleteData = function(row){
        	$scope.inputVO.Delete_Data = row.EXAM_VERSION
        	$confirm({text: '是否刪除此筆資料!!'},{size: 'sm'}).then(function(){
            	$scope.sendRecv("KYC210","deleteData","com.systex.jbranch.app.server.fps.kyc210.KYC210InputVO",
            			$scope.inputVO,function(tota,isError){
            			if (tota.length > 0) {
                			$scope.inquireInit();
                    		$scope.queryData();
    	            		$scope.showSuccessMsg('刪除成功');
            			}
            	});
        	});

        }
        
        
	}     
);