/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS211Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS211Controller";
		
		$scope.init = function(){
			var NowDate = new Date();
	        var yr = NowDate.getFullYear();
	        var mm = NowDate.getMonth()+1;
	        var strmm='';
			$scope.mappingSet['timeE'] = [];
		        for(var i=0; i<12; i++){
		        	mm = mm -1;
		        	if(mm == 0){
		        		mm = 12;
		        		yr = yr-1;
		        	}
		        	if(mm<10)
		        		strmm = '0' + mm;
		        	else
		        		strmm = mm;        		
		        	$scope.mappingSet['timeE'].push({
		        		LABEL: yr+'/'+strmm,
		        		DATA: yr +''+ strmm
		        	});        
		        }
		    $scope.mappingSet['version'] = [];
		    $scope.mappingSet['version'].push({
        		LABEL: '最新版',
        		DATA: '0'
        	}); 
		    $scope.mappingSet['version'].push({
        		LABEL: '上簽版',
        		DATA: '1'
        	}); 
		    $scope.openFlag = true;		//儲存設定按鈕不可點擊
		    $scope.saveFlag = true;		//儲存版本按鈕不可點擊
			$scope.inputVO.userId = projInfoService.getUserID();
		}
		$scope.init();
		
		/*計算獎勵金按鈕*/
		$scope.caculate = function(){
			if(''==$scope.inputVO.docYearMon || null==$scope.inputVO.docYearMon){
				$scope.showErrorMsg("尚未選擇計算日期");
				return;
			}
			$scope.inputVO.inputDataMonth = $scope.inputVO.docYearMon;
			var version = $("input[name='version']:checked").val();
			if("最新版" == version)
				$scope.inputVO.inputVersionChoose = '0';
			else if("上簽版" == version)
				$scope.inputVO.inputVersionChoose = '1';
			$scope.sendRecv("PMS211", "caculatePri","com.systex.jbranch.app.server.fps.pms211.PMS211QueryInputVO",$scope.inputVO, function(tota, isError) {
				if (!isError) {
					var errorMsg = tota[0].body.errorMsg;
					var message = tota[0].body.message;
					if(errorMsg != null){		//判断是否有错误信息，若有 显示错误信息内容
						$scope.showMsg(errorMsg);
						return;
					}else{
						$confirm({text: message}, {size: 'sm'}).then(function() {
							var date = new Date();			
							var mon = date.getMonth()+1;
							var day = date.getDate();
							if(day<10)
								day = '0' + day;
							if(mon<10)
								mon = '0' + mon;
							$scope.inputVO.execDate = date.getFullYear()+""+mon+""+day;	//獲取系統時間， 格式為yyyyMMdd
							$scope.inputVO.yearMon = $scope.inputVO.docYearMon;						//獲取選擇的月份，格式為yyyyMM
							$scope.inputVO.selectFlag = '0'										//是否勾選，是為'1'，否則為'0'
							$scope.inputVO.versionFlag = $scope.inputVO.inputVersionChoose;		//版本類型裝載 0為最新版，1為上簽版
							
							$scope.sendRecv("PMS211", "procedureData","com.systex.jbranch.app.server.fps.pms211.PMS211InputVO",
									$scope.inputVO, function(tota, isError) {
								if (!isError) {
									if(tota[0].body.errorMsg != null){		//判断是否有错误信息，若有 显示错误信息内容
										var errorMsg = tota[0].body.errorMsg;		//执行存储过程的返回结果(出现的问题)，若为null，否则显示错误信息
										$scope.showMsg(errorMsg);
										return;
									}else{
										$scope.query();			//沒有錯誤信息，執行查詢
									}
								}else{
									$scope.showErrorMsg("失敗");
								}
							});
								
						});//
					}
				}else{
					$scope.showErrorMsg("失敗");
				}
			});				
		}
		
		/*重算理専獎勵金按鈕*/
		$scope.caculateF = function(){
			if(''==$scope.inputVO.docYearMon || null==$scope.inputVO.docYearMon){
				$scope.showErrorMsg("尚未選擇計算日期");
				return;
			}
			$scope.inputVO.inputDataMonth = $scope.inputVO.docYearMon;
			var version = $("input[name='version']:checked").val();
			if("最新版" == version)
				$scope.inputVO.inputVersionChoose = '0';
			else if("上簽版" == version)
				$scope.inputVO.inputVersionChoose = '1';
			var message = "是否確定重算"+$scope.inputVO.inputDataMonth+version;
			$confirm({text: message}, {size: 'sm'}).then(function() {
				$scope.inputVO.yearMon = $scope.inputVO.docYearMon;						//獲取選擇的月份，格式為yyyyMM
				$scope.inputVO.selectFlag = '0'										//是否勾選，是為'1'，否則為'0'
					$scope.inputVO.versionFlag = $scope.inputVO.inputVersionChoose;		//版本類型裝載 0為最新版，1為上簽版
					$scope.sendRecv("PMS211", "procedureData","com.systex.jbranch.app.server.fps.pms211.PMS211InputVO",
						$scope.inputVO, function(tota, isError) {
						if (!isError) {
							if(tota[0].body.errorMsg != null){		//判断是否有错误信息，若有 显示错误信息内容
								var errorMsg = tota[0].body.errorMsg;		//执行存储过程的返回结果(出现的问题)，若为null，否则显示错误信息
								$scope.showMsg(errorMsg);
								return;
							}else{
								$scope.query();			//沒有錯誤信息，執行查詢
							}
						}else{
							$scope.showErrorMsg("失敗");
						}
					});								
				});	
			}
		
		$scope.query = function(){
			if(''==$scope.inputVO.docYearMon || null==$scope.inputVO.docYearMon){
				$scope.showErrorMsg("尚未選擇計算日期");
				return;
			}
			$scope.inputVO.inputDataMonth = $scope.inputVO.docYearMon;
			$scope.sendRecv("PMS211", "queryData","com.systex.jbranch.app.server.fps.pms211.PMS211QueryInputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.upLargeAgrList = null;
						$scope.newLargeAgrList = null;
						if(null == tota[0].body){
							$scope.showMsg("ehl_01_common_009");
							return;
						}else{	//查詢成功
							$scope.resultList = tota[0].body.outputLargeAgrList;
							for(var i=0; i<$scope.resultList.length; i++){
								if($scope.resultList[i].VERSION_CHOOSE == '1')
									$scope.upLargeAgrList = $scope.resultList[i];
								else if($scope.resultList[i].VERSION_CHOOSE == '0')
									$scope.newLargeAgrList = $scope.resultList[i];
							}
						}
					}else{
						$scope.showErrorMsg("失敗");
					}
			});
		}
		
		/*儲存設定   儲存版本按鈕，*/
		$scope.saveData = function(inputList){
			$scope.inputVO.yearMon = inputList.DATA_MONTH;
			$scope.inputVO.execDate = inputList.JOB_BEGIN_TIME;
			$scope.inputVO.versionFlag = inputList.VERSION_CHOOSE;
			$scope.inputVO.selectFlag = '1';
			
			$scope.sendRecv("PMS211", "updateData",
				"com.systex.jbranch.app.server.fps.pms211.PMS211InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						var errorMsg = tota[0].body.errorMsg;
						if(errorMsg != null){		//判断是否有错误信息，若有 显示错误信息内容
							$scope.showMsg(errorMsg);
							return;
						}else{
							$scope.showMsg("ehl_01_common_025");
						}
					}else{
						$scope.showErrorMsg("Error");
					}
				});
		}
				
		/*下載報表*/
		$scope.download = function(inputList,tableName){
			$scope.inputVO.yearMon = inputList.DATA_MONTH;
			$scope.inputVO.execDate = inputList.JOB_BEGIN_TIME;
			$scope.inputVO.versionFlag = inputList.VERSION_CHOOSE;
			$scope.inputVO.tableName = tableName;
			$scope.sendRecv("PMS211", "downloadData",
					"com.systex.jbranch.app.server.fps.pms211.PMS211InputVO",
					$scope.inputVO, function(tota, isError) {
						if (!isError) {
							var errorMsg = tota[0].body.errorMsg;
							if(errorMsg != null){		//判断是否有错误信息，若有 显示错误信息内容
								$scope.showMsg(errorMsg);
								return;
							}else{
								$scope.showMsg("下載成功");
							}
						}else{
							$scope.showErrorMsg("Error");
						}
					});
		}
		
		/*checkbox change事件,改變按鈕的disabled*/
		$("#openSel").on("click", function(){
			if(null == $scope.upLargeAgrList ){
				$scope.openFlag = true;
				return;
			}
			if(($scope.upLargeAgrList.JOB_STATE=='已完成') && ($("#openSel").is(':checked')))
				$scope.openFlag = false;
			else 
				$scope.openFlag = true;
		})
		$("#openSave").on("click", function(){
			if(null == $scope.newLargeAgrList ){
				$scope.saveFlag = true;
				return;
			}
			if(($scope.newLargeAgrList.JOB_STATE=='已完成') && ($("#openSave").is(':checked')))
				$scope.saveFlag = false;
			else 
				$scope.saveFlag = true;
		})
		/*給a標籤綁定點擊事件，若控制位為1，則連接可用，否則提示‘報表尚未開放查詢’*/
		/*$("#fc_ind_flag").click(function(){
			if($scope.upLargeAgrList.OPEN_SELECT == '0'){
				$scope.showMsg('報表尚未開放查詢');
			}else{
				$scope.showMsg('開放查詢');
			}
		});
		$("#fch_ind_flag").click(function(){
			if($scope.upLargeAgrList.OPEN_SELECT == '0'){
				$scope.showMsg('報表尚未開放查詢');
			}else{
				$scope.showMsg('開放查詢');
			}
		});
		$("#zg_ind_flag").click(function(){
			if($scope.upLargeAgrList.OPEN_SELECT == '0'){
				$scope.showMsg('報表尚未開放查詢');
			}else{
				$scope.showMsg('開放查詢');
			}
		});
		$("#fc_flag").click(function(){
			if($scope.upLargeAgrList.OPEN_SELECT == '0'){
				$scope.showMsg('報表尚未開放查詢');
			}else{
				$scope.showMsg('開放查詢');
			}
		});
		$("#fch_flag").click(function(){
			if($scope.upLargeAgrList.OPEN_SELECT == '0'){
				$scope.showMsg('報表尚未開放查詢');
			}else{
				$scope.showMsg('開放查詢');
			}
		});
		$("#zg_flag").click(function(){
			if($scope.upLargeAgrList.OPEN_SELECT == '0'){
				$scope.showMsg('報表尚未開放查詢');
			}else{
				$scope.showMsg('開放查詢');
			}
		});
		$("#lzjx_flag").click(function(){
			if($scope.upLargeAgrList.OPEN_SELECT == '0'){
				$scope.showMsg('報表尚未開放查詢');
			}else{
				$scope.showMsg('開放查詢');
			}
		});
		$("#fhsy_flag").click(function(){
			if($scope.upLargeAgrList.FHSY_FLAG == '0'){
				$scope.showMsg('報表尚未開放查詢');
			}else{
				$scope.showMsg('開放查詢');
			}
		});*/
});