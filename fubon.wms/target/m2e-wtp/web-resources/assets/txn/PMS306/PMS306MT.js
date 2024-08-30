/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS306MTController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS306MTController";
	
	
		
	$scope.init = function() {
		
		$scope.inputVO = {
			sTime:'',	   //年月
			FILE_NAME :'',    //檔名
			ACTUAL_FILE_NAME :'',   //真實檔名
			state:'1',   //確認是否狀態可上傳
			YEARS:''   //年月
		};
		$scope.mothlist=[];
		$scope.outputVO={};
	};
	$scope.init();
	
	
	
	//月份初始化
	$scope.monthstat = function(){
					var NowDate = new Date();
					NowDate.setMonth(NowDate.getMonth()+3);  //將下拉選單顯示往後3個月
					var yr = NowDate.getFullYear();
					var mm = NowDate.getMonth() + 2;
					var strmm = '';
					var xm = '';
					$scope.mappingSet['timeE'] = [];
					for (var i = 0; i < 25; i++) {
						mm = mm - 1;
						if (mm == 0) {
							mm = 12;
							yr = yr - 1;
						}
						if (mm < 10)
							strmm = '0' + mm;
						else
							strmm = mm;
						$scope.mappingSet['timeE'].push({
							LABEL : yr + '/' + strmm,
							DATA : yr + '' + strmm
						});
					} 
	};
    $scope.monthstat();
	
	
	
	
	
	
	
	/*****月目標查詢*****/
	$scope.queryMoth = function() {
		$scope.sendRecv("PMS306", "mothtar", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				
				return;
			}
			if (tota.length > 0) {
				if(tota[0].body.mothlist.length == 0) {
					$scope.mothlist = [];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.mothlist = tota[0].body.mothlist;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.exportRPT = function(){
		$scope.sendRecv("PMS306", "export", "com.systex.jbranch.app.server.fps.pms306.PMS306OutputVO", $scope.outputVO,
				function(tota, isError) {						
					if (isError) {
	            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
	            	}
					if (tota.length > 0) {
                		if(tota[0].body.mothlist && tota[0].body.mothlist.length == 0) {
                			$scope.showMsg("ehl_01_common_009");
                			return;
                		}
                	};
		});
	};
	
	
	
	$scope.upload = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		
		$scope.sendRecv("PMS306", "checkSql", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (!isError) {
				$scope.inputVO.state = tota[0].body.state;
				$scope.inputVO.NBR_state = tota[0].body.NBR_state;
				$scope.inputVO.BRANCH_NBR = tota[0].body.BRANCH_NBR;
				$scope.check($scope.inputVO.FILE_NAME,$scope.inputVO.ACTUAL_FILE_NAME);
				return;
			}
		
		});
		
	};
	
	//問題單號0002072
	$scope.check=function(name,rname){
		if($scope.inputVO.state=='1'){
			if($scope.inputVO.BRANCH_NBR != ''){
				$confirm({text: $scope.inputVO.BRANCH_NBR+'是否繼續上傳?'}, {size: 'sm'}).then(function(){
					$scope.sendRecv("PMS306", "uploadmoth", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO, function(tota, isError) {
						if (isError) {
							
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (tota.length > 0) {
						
							
							if(tota[0].body.state=='lenfail'){
								
								$scope.showErrorMsgInDialog("欄位數量不符請照畫面上傳");	
								
								return;
							}
								
							if(tota[0].body.state=='linefail'){
								
								$scope.showErrorMsgInDialog("欄位位置錯誤請照畫面上傳");
								
								return;
							}
						}	
							$scope.showMsg("已經新增此月份分行保險目標報表");
							$scope.inputVO.sTime="";	   //年月
							$scope.inputVO.FILE_NAME="";    //檔名
							return;
							
						
					});
				});
			}else{
				$scope.sendRecv("PMS306", "uploadmoth", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO, function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {
							if(tota[0].body.state=='lenfail'){
								
								$scope.showErrorMsgInDialog("欄位數量不符請照畫面上傳");	
								
								return;
							}
								
							if(tota[0].body.state=='linefail'){
								
								$scope.showErrorMsgInDialog("欄位位置錯誤請照畫面上傳");
								
								return;
							}
						
						
						$scope.showMsg("已經替換此月份分行保險目標報表");
						$scope.inputVO.sTime="";	   //年月
						$scope.inputVO.FILE_NAME="";    //檔名
						return ;
					}
				});
			}
				
			
		}
	if($scope.inputVO.state=='0'){
		if($scope.inputVO.BRANCH_NBR != ''){
			$confirm({text: $scope.inputVO.BRANCH_NBR+'是否繼續上傳?'}, {size: 'sm'}).then(function(){
				$scope.sendRecv("PMS306", "uploadmoth", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO, function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {
							if(tota[0].body.state=='lenfail'){
								
								$scope.showErrorMsgInDialog("欄位數量不符請照畫面上傳");	
								
								return;
							}
								
							if(tota[0].body.state=='linefail'){
								
								$scope.showErrorMsgInDialog("欄位位置錯誤請照畫面上傳");
								
								return;
							}
						
						
						$scope.showMsg("已經替換此月份分行保險目標報表");
						$scope.inputVO.sTime="";	   //年月
						$scope.inputVO.FILE_NAME="";    //檔名
						return ;
					}
				});
			});
		}else{
			$scope.sendRecv("PMS306", "uploadmoth", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
						if(tota[0].body.state=='lenfail'){
							
							$scope.showErrorMsgInDialog("欄位數量不符請照畫面上傳");	
							
							return;
						}
							
						if(tota[0].body.state=='linefail'){
							
							$scope.showErrorMsgInDialog("欄位位置錯誤請照畫面上傳");
							
							return;
						}
					
					
					$scope.showMsg("已經替換此月份分行保險目標報表");
					$scope.inputVO.sTime="";	   //年月
					$scope.inputVO.FILE_NAME="";    //檔名
					return ;
				}
			});
		}
				
		}

	}
	
	
	
	
	
});
