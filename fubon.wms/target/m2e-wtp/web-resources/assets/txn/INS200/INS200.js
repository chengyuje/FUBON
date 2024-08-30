'use strict';
eSoafApp.controller('INS200Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter){
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS200Controller";
		
		// 有效起始日期
		$scope.bgn_sDateOptions = {
			maxDate: new Date(),
			minDate: $scope.minDate
		};
		
		// config
		$scope.model = {};
		$scope.bgn_sDateOptions = {maxDate : new Date()};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
	
		$scope.init = function(){
			$scope.inputVO = {
					CUST_ID:'',
					CUST_NAME:undefined,
					BIRTHDAY:undefined,
					AGE:undefined,
					IS_CUST:false
			}
			//從INS110進入進行規劃入口
			if($scope.connector('get', "FROM_INS110")){
				$scope.inputVO.CUST_ID = $scope.connector('get', "FROM_INS110");
				if($scope.inputVO.CUST_ID != ''){
					$scope.connector('set', "FROM_INS110",undefined);
					$scope.query(false); // 建輝說不下載喔!
				}
			}
			$scope.temp_CUST_ID = $scope.inputVO.CUST_ID;
			$scope.PLAN_NAME = undefined;
			$scope.PLAN_DATE = undefined;
			$scope.EMP_NAME = undefined;
			$scope.NOT_CUST_COUNT_AGE = false;
			$scope.FB_CUST = undefined;//是否為本行客戶
			// 2018/3/2 如果舊CODE定義INS210Controller, 這四個TAG就是獨立的東西
			// TBINS_PLAN_MAIN 是共用, TBINS_PLAN_DTL獨立沒錯
			$scope.connector('set','INS210_PLAN_KEYNO', null);
		};
		
		
		//客戶ID欄位檢核
		$scope.custidToUppercase = function(){
			if($scope.inputVO.CUST_ID != '' && $scope.inputVO.CUST_ID != undefined){
				$scope.inputVO.CUST_ID = $scope.inputVO.CUST_ID.toUpperCase();
				$scope.query(false);
			}
			if($scope.temp_CUST_ID != $scope.inputVO.CUST_ID){
				//若客戶ID有修改清空所有資料
				$scope.inputVO.IS_CUST = false;
				$scope.inputVO.CUST_NAME = undefined;
				$scope.inputVO.BIRTHDAY = undefined;
				$scope.inputVO.AGE = undefined;
				$scope.PLAN_NAME = undefined;
				$scope.PLAN_DATE = undefined;
				$scope.EMP_NAME = undefined;
			}
		}
		
		//計算保險年齡
		$scope.count_age = function(){
			if($scope.NOT_CUST_COUNT_AGE && $scope.inputVO.CUST_ID != ''){
				$scope.sendRecv("INS200","countAge","com.systex.jbranch.app.server.fps.ins200.INS200InputVO",
						$scope.inputVO,function(tota,isError){
							if(!isError){
								$scope.inputVO.AGE = tota[0].body.Age;
							}
				});
			}
		}
		
		$scope.take_last_record = function() {
			$scope.sendRecv("INS200", "takeLastRecord", "com.systex.jbranch.app.server.fps.ins200.INS200InputVO", $scope.inputVO,
    			function(totas, isError) {
                	if (!isError) {
                	};
    			}
    		);
//			if($scope.TEMP_RESULT.FILE_NAME) {
//				var unit8_file = new Uint8Array($scope.TEMP_RESULT.REPORT_FILE);
//				var file = new Blob([unit8_file], {type: 'application/pdf'});
//				saveAs(file, $scope.PLAN_NAME);
//			}
//			else
//				$scope.showErrorMsg('ehl_01_common_009');
			
		};
		//最近一次紀錄查詢
		$scope.query = function(isPrint) {
			$scope.sendRecv("INS200","query","com.systex.jbranch.app.server.fps.ins200.INS200InputVO",
					$scope.inputVO,function(tota,isError){
						if(!isError){
							if(tota[0].body.resultList.length>0) {
								$scope.TEMP_RESULT = tota[0].body.resultList[0];
								
								$scope.inputVO.IS_CUST = true;
								$scope.FB_CUST='Y';//是否為本行客戶
								$scope.inputVO.CUST_NAME = $scope.TEMP_RESULT.CUST_NAME;
								$scope.inputVO.BIRTHDAY = $scope.toJsDate($scope.TEMP_RESULT.BIRTH_DATE);
								$scope.inputVO.AGE = $scope.TEMP_RESULT.age;
								$scope.inputVO.GENDER = $scope.TEMP_RESULT.GENDER;
								$scope.PLAN_NAME = $scope.TEMP_RESULT.FILE_NAME;
								$scope.PLAN_DATE = $filter('date')($scope.toJsDate($scope.TEMP_RESULT.PLAN_DATE),'yyyy/MM/dd');
								$scope.EMP_NAME = $scope.TEMP_RESULT.EMP_NAME;
								if(isPrint) {
									if($scope.TEMP_RESULT.FILE_NAME) {
										var unit8_file = new Uint8Array($scope.TEMP_RESULT.REPORT_FILE);
										var file = new Blob([unit8_file], {type: 'application/pdf'});
										saveAs(file, $scope.PLAN_NAME);
									}
									else
										$scope.showErrorMsg('ehl_01_common_009');
								}
							}else{
								//查無資料
								//檢核客戶ID合理性
								$scope.sendRecv("INS200","vaildCUSTID","com.systex.jbranch.app.server.fps.ins200.INS200InputVO",
										$scope.inputVO,function(tota,isError){
											if(!isError){
												if(!tota[0].body.vaildcustid){
													$scope.inputVO.CUST_ID = '';
													$scope.showErrorMsg('ehl_01_common_030');
												}
											}
								});
								$scope.FB_CUST='N';//是否為本行客戶
								$scope.inputVO.IS_CUST = false;
								$scope.NOT_CUST_COUNT_AGE = true;
								$scope.inputVO.CUST_NAME = undefined;
								$scope.inputVO.BIRTHDAY = undefined;
								$scope.inputVO.AGE = undefined;
								$scope.PLAN_NAME = undefined;
								$scope.PLAN_DATE = undefined;
								$scope.EMP_NAME = undefined;
								$scope.showErrorMsg('ehl_01_common_009');
							}
							$scope.temp_CUST_ID = $scope.inputVO.CUST_ID;
						}
			});
		};
	
		//進行規劃
		$scope.GoINS200Loader = function(){
			if($scope.parameterTypeEditForm.$invalid){
				$scope.showErrorMsg('ehl_01_common_022');
				return;
			}else{
				var dated = $scope.inputVO.BIRTHDAY;
				$scope.INS_CUST_MASTIutputVO = {
						CUST_ID:$scope.inputVO.CUST_ID,
						CUST_NAME:$scope.inputVO.CUST_NAME,
						birthDay:dated.getFullYear()+ "-" + (dated.getMonth()+1) + "-" + dated.getDate(),
						FB_CUST:$scope.FB_CUST,
						AGE: $scope.inputVO.AGE,
						GENDER: $scope.inputVO.GENDER
				}
				
				$scope.sendRecv("INS810","saveInsCustMast","com.systex.jbranch.app.server.fps.ins810.INS_CUST_MASTIutputVO",
						$scope.INS_CUST_MASTIutputVO,function(tota,isError){
							if(!isError){
								$scope.connector('set','INS200_CUST_ID',$scope.inputVO.CUST_ID);
								$scope.connector('set','INS200_CUST_DATA', $scope.INS_CUST_MASTIutputVO);
								$rootScope.menuItemInfo.url = "assets/txn/INS200/INS200_Loader.html";
							}
				});
			}
		}
		
		$scope.init();
		
});
