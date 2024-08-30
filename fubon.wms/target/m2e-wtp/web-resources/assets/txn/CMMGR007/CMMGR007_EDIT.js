/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR007_EditController',
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR007_EditController";
        
        $scope.init = function(){
        	if($scope.row){
        		$scope.isUpdate = true
        	}
            $scope.row = $scope.row || {};
            
        	$scope.inputVO = {
        			jobid: $scope.row.JOBID,
        			jobname: $scope.row.JOBNAME,
        			description: $scope.row.DESCRIPTION,
        			beanid: $scope.row.BEANID,
        			classname: $scope.row.CLASSNAME,
        			parameters: $scope.row.PARAMETERS,
        			precondition: $scope.row.PRECONDITION,
        			postcondition: $scope.row.POSTCONDITION,
					updatejobid: $scope.row.JOBID //2016.08.01; Sebastian; 紀錄原PK欄位值,不受angularjs binding影響
			};
        };
        $scope.init();
        
        /**
         * Job 參數資訊初始化
         */
        $scope.initParameterInfo = function() {
        	//參數設置頁面
        	$scope.templateUrl = "jobParamSetterTemplate.html";
        	//標準參數訊息
        	$scope.standardType = [	{LABEL:'method', TIP:'選擇呼叫BeanId的指定方法'},
        	                       	{LABEL:'ftpCode', TIP:'執行SQLLDR所需要的FTP傳輸設定'},
        	                       	{LABEL:'ctlName', TIP:'執行SQLLDR所需要的控制檔名'},
        	                       	{LABEL:'ftpGetCode', TIP:'遠端下載所需要的FTP傳輸設定'},
        	                       	{LABEL:'ftpPutCode', TIP:'遠端上傳所需要的FTP傳輸設定'},
        	                       	{LABEL:'pckName', TIP:'指定欲執行的Package名稱'},
        	                       	{LABEL:'pckArg', TIP:'指定欲執行的Package的參數'},
        	                       	{LABEL:'arg', TIP:'指定欲執行的method的參數'}];
        }
        $scope.initParameterInfo();
        
        /**
         * Job 參數初始化
         */
        $scope.initParameter = function() {
         	$scope.groups = [];
        	var param = $scope.inputVO.parameters;
        	if (param) {
        		param.split(';').map((each)=>{
        			if (each) {
        				var map = each.split('=');
        				var group = {title: map[0], content: map[1], tip: '自定義參數'};
        				
        				$scope.standardType.filter((type)=>{
        					if (type.LABEL == group.title) group.tip = type.TIP;
        				});
        				
        				$scope.groups.push(group);
        			}
        		})
        	} else {
        		$scope.standardType.map((type)=>{
        			$scope.groups.push({title: type.LABEL, tip: type.TIP});
        		});
        	}
        	
        	$scope.addModel = {
            		add: true,
                 	title: '新增參數',
                 	addTitle:'',
                 	content: '',
                 	tip: '設定標準參數以外的自定義參數'
            }
        	$scope.groups.push(angular.copy($scope.addModel));
        }
        $scope.initParameter();
        
        
        /**
         * 設置參數名稱下拉欄
         */
        $scope.initParaCombo = function() {
        	$scope.PARATYPE = $scope.standardType.filter((each) =>
    				$scope.groups.filter((g)=>g.title == each.LABEL).length == 0
        	);	
        }
        $scope.initParaCombo();
        
        
        /**
         * 取得對應的Tip
         */
        $scope.getTip = function(group) {
        	if (!group.addTitle) group.tip = '設定標準參數值';
        	else group.tip = '設定標準參數以外的自定義參數';
        	
        	$scope.standardType.map((type)=>{
				if (type.LABEL == group.addTitle) group.tip = type.TIP;
			});
        }
        
        /**
         * 控管編輯畫面
         */
        $scope.pop = function(group) {
        	$scope.groups.map((each)=>{
        		if (each!=group) each.show = false;
        		else each.show = true;
        	})
        }
        
        /**
         * 陳列參數值
         */
        $scope.appeal = function() {
        	$scope.inputVO.parameters = '';
        	$scope.groups.map((each)=>{
        		if (each.addTitle || each.content) {
        			$scope.inputVO.parameters += `${each.addTitle || each.title}=${each.content};`;
        		}
        	})
        }
        
        /**
         * 新增參數
         */
        $scope.addPara = function(group) {
        	if (!group.addTitle || !group.content) {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	group.show = false;
        	
        	setTimeout(()=>{
        		group.add = false
        		group.title = group.addTitle;
        		group.addTitle = '';
        		$scope.initParaCombo();
        	},500);
        	$scope.groups.push(angular.copy($scope.addModel));
        }
        
        /**
         * 移除參數
         */
        $scope.removePara = function(group) {
        	$scope.groups = $scope.groups.filter((e)=>e!=group);
        	$scope.initParaCombo();
        	$scope.appeal();
        }
        
        /**
         * 分割參數值
         */
        $scope.splitParameter = function() {
        	 $scope.initParameter();
        	 $scope.initParaCombo();
        }
        
        $scope.save = function(){
        	if($scope.parameterTypeEditForm.$invalid){
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}

        	if (!$scope.inputVO.parameters.length) {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:參數為必要輸入欄位');
        		return;
        	}

        	$scope.inputVO.type="Create";
        	if($scope.row.JOBID != undefined){
        		$scope.inputVO.type="Update";
        	}
        	$scope.sendRecv("CMMGR007", "operation", "com.systex.jbranch.app.server.fps.cmmgr007.CMMGR007InputVO", $scope.inputVO,
    			function(totas, isError) {
	                if (isError) {
	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	                    return;
	                }
	                 if (totas.length > 0) {
	                	 $scope.showMsg('儲存成功');
		       			$scope.closeThisDialog('successful');
	                 };
	            }
        	);
        }
        
        $scope.del = function(){
        	$scope.inputVO.type="Delete";
    		$scope.sendRecv("CMMGR007", "operation", "com.systex.jbranch.app.server.fps.cmmgr007.CMMGR007InputVO", $scope.inputVO,
	    		function(totas, isError) {
    				if (isError) {
		               	$scope.showErrorMsgInDialog(totas.body.msgData);
		            	return;
    				}
    				if (totas.length > 0) {
    					$scope.showMsg('刪除成功');
    					$scope.closeThisDialog('successful');
    				};
    			}
	        );
        }
        
    }
);
