/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS210Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS210Controller";

		//初始化
		$scope.init = function(){
			$scope.inputVO = {
					docYearMon:''
			};
			$scope.resultList = [];
		}
		$scope.init();
		$scope.ifShow = false;
		
		
		//月份選擇下拉框
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
    
        $scope.getTime = function() {
        	var insDate = $scope.inputVO.docYearMon;
            var yr = insDate.substring(0,4);
            var mm = insDate.substring(4,6);
        	if(mm >= 11){
        		yr = parseInt(yr)+1;
        		mm = parseInt(mm) + 2 -12;
        	}
        	else {
        		mm = 2+parseInt(mm); 
        	}
        	
        	if(mm < 10) {
        		mm = '0'+mm;
        	}
        	$scope.inputVO.insTime = yr+'年'+mm+'月';       
        	$scope.insTime1 = yr+mm;       
        }
        
        //查詢功能
        $scope.query = function(){
        	if($scope.inputVO.docYearMon==null||$scope.inputVO.docYearMon==''){
    		$scope.showMsg('欄位檢核錯誤:參數月份必填');
    		return;
        	}
        	$scope.inputVO.yearMon = $scope.inputVO.docYearMon;
			$scope.sendRecv("PMS210", "queryData", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.ifShow = false;
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.ifShow = true;
							$scope.resultList = tota[0].body.resultList[0];
							$scope.getTime();
							$scope.setVar();
							return;
						}
			});
        }
        
        //遞延至次月
        $scope.callProc = function(){
        	if($scope.inputVO.docYearMon==null||$scope.inputVO.docYearMon==''){
        		$scope.showMsg('欄位檢核錯誤:參數月份必填');
        		return;
            }
        	$scope.inputVO.yearMon = $scope.inputVO.docYearMon;
			$scope.sendRecv("PMS210", "callCNRMast", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.backResult) {
							$scope.showMsg("遞延至次月成功");  
							//$scope.query();
	            		}else{
	            			$scope.showMsg("遞延至次月失敗");  
	            		}
					}
				});
        }
        
        //轉化為需要的值
        $scope.setVar = function(){
        	//理專
        	$scope.SpAss_State = $scope.resultList.SP_ASS.substring(0,1);
        	$scope.SpAss_IfAuto = $scope.resultList.SP_ASS.substring(1,2);
        	if($scope.SpAss_State=='0'){
        		$scope.SpAss_State='未設定';
        	}else{
        		$scope.SpAss_State='已設定';
        	}
        	
        	$scope.SpBen_State = $scope.resultList.SP_BEN.substring(0,1);
        	$scope.SpBen_IfAuto = $scope.resultList.SP_BEN.substring(1,2);
        	if($scope.SpBen_State=='0'){
        		$scope.SpBen_State='未設定';
        	}else{
        		$scope.SpBen_State='已設定';
        	}
        	
        	$scope.SpLevd_State = $scope.resultList.SP_LEVD.substring(0,1);
        	$scope.SpLevd_IfAuto = $scope.resultList.SP_LEVD.substring(1,2);
        	if($scope.SpLevd_State=='0'){
        		$scope.SpLevd_State='未設定';
        	}else{
        		$scope.SpLevd_State='已設定';
        	}

        	$scope.BsGoal_State = $scope.resultList.BS_GOAL.substring(0,1);
        	$scope.BsGoal_IfAuto = $scope.resultList.BS_GOAL.substring(1,2);
        	if($scope.BsGoal_State=='0'){
        		$scope.BsGoal_State='未設定';
        	}else{
        		$scope.BsGoal_State='已設定';
        	}
        	
        	$scope.SpBouns_State = $scope.resultList.SP_BOUNS.substring(0,1);
        	$scope.SpBouns_IfAuto = $scope.resultList.SP_BOUNS.substring(1,2);
        	if($scope.SpBouns_State=='0'){
        		$scope.SpBouns_State='未設定';
        	}else{
        		$scope.SpBouns_State='已設定';
        	}

        	$scope.NewCustRate_State = $scope.resultList.NEW_CUST_RATE.substring(0,1);
        	$scope.NewCustRate_IfAuto = $scope.resultList.NEW_CUST_RATE.substring(1,2);
        	if($scope.NewCustRate_State=='0'){
        		$scope.NewCustRate_State='未設定';
        	}else{
        		$scope.NewCustRate_State='已設定';
        	}
        	
        	
        	$scope.NewSpBen_State = $scope.resultList.NEW_SP_BEN.substring(0,1);
        	$scope.NewSpBen_IfAuto = $scope.resultList.NEW_SP_BEN.substring(1,2);
        	if($scope.NewSpBen_State=='0'){
        		$scope.NewSpBen_State='未設定';
        	}else{
        		$scope.NewSpBen_State='已設定';
        	}
        	
        	$scope.NewBsGoal_State = $scope.resultList.NEW_BS_GOAL.substring(0,1);
        	$scope.NewBsGoal_IfAuto = $scope.resultList.NEW_BS_GOAL.substring(1,2);
        	if($scope.NewBsGoal_State=='0'){
        		$scope.NewBsGoal_State='未設定';
        	}else{
        		$scope.NewBsGoal_State='已設定';
        	}
        	
        	$scope.SpSalBen_State = $scope.resultList.SP_SAL_BEN.substring(0,1);
        	$scope.SpSalBen_IfAuto = $scope.resultList.SP_SAL_BEN.substring(1,2);
        	if($scope.SpSalBen_State=='0'){
        		$scope.SpSalBen_State='未設定';
        	}else{
        		$scope.SpSalBen_State='已設定';
        	}
        	
        	$scope.NewBounty_State = $scope.resultList.NEW_BOUNTY.substring(0,1);
        	$scope.NewBounty_IfAuto = $scope.resultList.NEW_BOUNTY.substring(1,2);
        	if($scope.NewBounty_State=='0'){
        		$scope.NewBounty_State='未設定';
        	}else{
        		$scope.NewBounty_State='已設定';
        	}
        	
        	$scope.FchBountyRate_State = $scope.resultList.FCH_BOUNTY_RATE.substring(0,$scope.resultList.FCH_BOUNTY_RATE.length-1);
        	$scope.FchBountyRate_IfAuto = $scope.resultList.FCH_BOUNTY_RATE.substring($scope.resultList.FCH_BOUNTY_RATE.length-1,$scope.resultList.FCH_BOUNTY_RATE.length);
        	
        	$scope.GoodsBountyRate_State = $scope.resultList.GOODS_BOUNTY_RATE.substring(0,$scope.resultList.GOODS_BOUNTY_RATE.length-1);
        	$scope.GoodsBountyRate_IfAuto = $scope.resultList.GOODS_BOUNTY_RATE.substring($scope.resultList.GOODS_BOUNTY_RATE.length-1,$scope.resultList.GOODS_BOUNTY_RATE.length);
        	
        	//主管
        	$scope.BsAss_State = $scope.resultList.BS_ASS.substring(0,1);
        	$scope.BsAss_IfAuto = $scope.resultList.BS_ASS.substring(1,2);
        	if($scope.BsAss_State=='0'){
        		$scope.BsAss_State='未設定';
        	}else{
        		$scope.BsAss_State='已設定';
        	}
        	
        	$scope.BsAum_State = $scope.resultList.BS_AUM.substring(0,1);
        	$scope.BsAum_IfAuto = $scope.resultList.BS_AUM.substring(1,2);
        	if($scope.BsAum_State=='0'){
        		$scope.BsAum_State='未設定';
        	}else{
        		$scope.BsAum_State='已設定';
        	}

        	$scope.BsPoint_State = $scope.resultList.BS_POINT.substring(0,1);
        	$scope.BsPoint_IfAuto = $scope.resultList.BS_POINT.substring(1,2);
        	if($scope.BsPoint_State=='0'){
        		$scope.BsPoint_State='未設定';
        	}else{
        		$scope.BsPoint_State='已設定';
        	}
        	
        	$scope.BsPrize_State = $scope.resultList.BS_PRIZE.substring(0,1);
        	$scope.BsPrize_IfAuto = $scope.resultList.BS_PRIZE.substring(1,2);
        	if($scope.BsPrize_State=='0'){
        		$scope.BsPrize_State='未設定';
        	}else{
        		$scope.BsPrize_State='已設定';
        	}

        	$scope.BsGoalTar_State = $scope.resultList.BS_GOAL_TAR.substring(0,1);
        	$scope.BsGoalTar_IfAuto = $scope.resultList.BS_GOAL_TAR.substring(1,2);
        	if($scope.BsGoalTar_State=='0'){
        		$scope.BsGoalTar_State='未設定';
        	}else{
        		$scope.BsGoalTar_State='已設定';
        	}
        	
        	$scope.BsBonusInse_State = $scope.resultList.BS_BONUS_INSE.substring(0,1);
        	$scope.BsBonusInse_IfAuto = $scope.resultList.BS_BONUS_INSE.substring(1,2);
        	if($scope.BsBonusInse_State=='0'){
        		$scope.BsBonusInse_State='未設定';
        	}else{
        		$scope.BsBonusInse_State='已設定';
        	}

        	
        	//共用參數
        	$scope.BonusDate_State = $scope.resultList.BONUS_DATE.substring(6,8);
        	$scope.BonusDate_IfAuto = $scope.resultList.BONUS_DATE.substring(8,9);
        	
        	$scope.ShortTran_State = $scope.resultList.SHORT_TRAN.substring(0,1);
        	$scope.ShortTran_IfAuto = $scope.resultList.SHORT_TRAN.substring(1,2);
        	if($scope.ShortTran_State=='0'){
        		$scope.ShortTran_State='未設定';
        	}else{
        		$scope.ShortTran_State='已設定';
        	}
        	
        	$scope.SpMar_State = $scope.resultList.SP_MAR.substring(0,1);
        	$scope.SpMar_IfAuto = $scope.resultList.SP_MAR.substring(1,2);
        	if($scope.SpMar_State=='0'){
        		$scope.SpMar_State='未設定';
        	}else{
        		$scope.SpMar_State='已設定';
        	}
        	
        	$scope.SpUnLackInd_State = $scope.resultList.SP_UN_LACK_IND.substring(0,1);
        	$scope.SpUnLackInd_IfAuto = $scope.resultList.SP_UN_LACK_IND.substring(1,2);
        	if($scope.SpUnLackInd_State=='0'){
        		$scope.SpUnLackInd_State='未設定';
        	}else{
        		$scope.SpUnLackInd_State='已設定';
        	}
        	
        	$scope.InsDate_State = $scope.resultList.INS_DATE.substring(0,2);
        	if($scope.InsDate_State=='00'){
        		$scope.Auto_State='C';
        	}else{
        		$scope.Auto_State='O';
        		
        	}
        	$scope.InsDate_IfAuto = $scope.resultList.INS_DATE.substring(2,3);
        	
        	$scope.ConNum_State = $scope.resultList.CON_NUM.substring(0,1);
        	$scope.ConNum_IfAuto = $scope.resultList.CON_NUM.substring(1,2);
        	if($scope.ConNum_State=='0'){
        		$scope.ConNum_State='未設定';
        	}else{
        		$scope.ConNum_State='已設定';
        	}
        	
        	$scope.DisRate_State = $scope.resultList.DIS_RATE.substring(0,1);
        	$scope.DisRate_IfAuto = $scope.resultList.DIS_RATE.substring(1,2);
        	if($scope.DisRate_State=='0'){
        		$scope.DisRate_State='未設定';
        	}else{
        		$scope.DisRate_State='已設定';
        	}
        	
        	$scope.DepositRate_State = $scope.resultList.DEPOSIT_RATE.substring(0,1);
        	$scope.DepositRate_IfAuto = $scope.resultList.DEPOSIT_RATE.substring(1,2);
        	if($scope.DepositRate_State=='0'){
        		$scope.DepositRate_State='未設定';
        	}else{
        		$scope.DepositRate_State='已設定';
        	}
        }
        
        //儲存修改
        $scope.saveChange = function(){
        	$scope.inputVO.yearMon = $scope.inputVO.docYearMon;
        	$scope.inputVO.YEARMON = $scope.inputVO.docYearMon;
        	$scope.inputVO.SP_ASS = $scope.resultList.SP_ASS.substring(0,1) +　$scope.SpAss_IfAuto;
        	$scope.inputVO.SP_BEN = $scope.resultList.SP_BEN.substring(0,1) +　$scope.SpBen_IfAuto;
        	$scope.inputVO.SP_LEVD = $scope.resultList.SP_LEVD.substring(0,1) +　$scope.SpLevd_IfAuto;
        	$scope.inputVO.BS_GOAL = $scope.resultList.BS_GOAL.substring(0,1) +　$scope.BsGoal_IfAuto;
        	$scope.inputVO.SP_BOUNS = $scope.resultList.SP_BOUNS.substring(0,1) +　$scope.SpBouns_IfAuto;
        	$scope.inputVO.NEW_CUST_RATE = $scope.resultList.NEW_CUST_RATE.substring(0,1) +　$scope.NewCustRate_IfAuto;
        	$scope.inputVO.NEW_SP_BEN = $scope.resultList.NEW_SP_BEN.substring(0,1) +　$scope.NewSpBen_IfAuto;
        	$scope.inputVO.NEW_BS_GOAL = $scope.resultList.NEW_BS_GOAL.substring(0,1) +　$scope.NewBsGoal_IfAuto;
        	$scope.inputVO.SP_SAL_BEN = $scope.resultList.SP_SAL_BEN.substring(0,1) +　$scope.SpSalBen_IfAuto;
        	$scope.inputVO.NEW_BOUNTY = $scope.resultList.NEW_BOUNTY.substring(0,1) +　$scope.NewBounty_IfAuto;
        	$scope.inputVO.FCH_BOUNTY_RATE = $scope.FchBountyRate_State +　$scope.FchBountyRate_IfAuto;
        	$scope.inputVO.GOODS_BOUNTY_RATE = $scope.GoodsBountyRate_State +　$scope.GoodsBountyRate_IfAuto;
        	
        	$scope.inputVO.BS_ASS = $scope.resultList.BS_ASS.substring(0,1) +　$scope.BsAss_IfAuto;
        	$scope.inputVO.BS_AUM = $scope.resultList.BS_AUM.substring(0,1) +　$scope.BsAum_IfAuto;
        	$scope.inputVO.BS_POINT = $scope.resultList.BS_POINT.substring(0,1) +　$scope.BsPoint_IfAuto;
        	$scope.inputVO.BS_PRIZE = $scope.resultList.BS_PRIZE.substring(0,1) +　$scope.BsPrize_IfAuto;
        	$scope.inputVO.BS_GOAL_TAR = $scope.resultList.BS_GOAL_TAR.substring(0,1) +　$scope.BsGoalTar_IfAuto;
        	$scope.inputVO.BS_BONUS_INSE = $scope.resultList.BS_BONUS_INSE.substring(0,1) +　$scope.BsBonusInse_IfAuto;
        	
        	$scope.getTime();
        	
        	if(parseInt($scope.BonusDate_State) < 10) 
        	{
        		$scope.BonusDate_State = '0'+parseInt($scope.BonusDate_State);
        	}
        	$scope.inputVO.BONUS_DATE =  $scope.insTime1 + $scope.BonusDate_State +　$scope.BonusDate_IfAuto;
        	$scope.inputVO.SHORT_TRAN = $scope.resultList.SHORT_TRAN.substring(0,1) +　$scope.ShortTran_IfAuto;
        	$scope.inputVO.SP_MAR = $scope.resultList.SP_MAR.substring(0,1) +　$scope.SpMar_IfAuto;
        	
        	
        	$scope.inputVO.SP_UN_LACK_IND = $scope.resultList.SP_UN_LACK_IND.substring(0,1) +　$scope.SpUnLackInd_IfAuto;
        	if($scope.Auto_State == 'C'){
        		$scope.inputVO.INS_DATE = '00' +　$scope.InsDate_IfAuto;
        	}else{
        		$scope.inputVO.INS_DATE = $scope.InsDate_State +　$scope.InsDate_IfAuto;        		
        	}
        	if($scope.Auto_State == 'O' ){
        		if(null == $scope.InsDate_State || ""==$scope.InsDate_State){
        			$scope.showMsg("請輸入保險遞延抽佣日");
        			return;
        		}else if($scope.InsDate_State<=0){
        			$scope.showMsg("錯誤的保險遞延抽佣日");
        			return;
        		}
        	}
        	$scope.inputVO.CON_NUM = $scope.resultList.CON_NUM.substring(0,1) +　$scope.ConNum_IfAuto;
        	$scope.inputVO.DIS_RATE = $scope.resultList.DIS_RATE.substring(0,1) +　$scope.DisRate_IfAuto;
        	$scope.inputVO.DEPOSIT_RATE = $scope.resultList.DEPOSIT_RATE.substring(0,1) +　$scope.DepositRate_IfAuto;

        	var f = 0;
        	for(var i in $scope.resultList){
        		if($scope.resultList[i]!=$scope.inputVO[i]){
        			f = 1;
        		}
        	}
        	
        	if(f==0){
        		return;//不調用saveChange方法
        	}
        	
        	if($scope.BonusDate_State<1||$scope.BonusDate_State>31||$scope.InsDate_State<0||$scope.InsDate_State>31){
        		$scope.showMsg('參數輸入有誤，請重新檢驗！');
        		return;
        	}
			$scope.sendRecv("PMS210", "saveChange", "com.systex.jbranch.app.server.fps.pms210.PMS210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.backResult) {
								$scope.showMsg("ehl_01_common_002");  //修改成功
								$scope.query();
	                		}else{
	                			$scope.showMsg("ehl_01_common_007");  //更新失敗
	                		}
						}
			});
        }
		
        //跳轉到設置頁面
        //理專參數1
        $scope.setSpAss = function(resultList){
            var dialog = ngDialog.open({
                template: 'assets/txn/PMS210/PMS210_setSpAss.html',
                className: 'PMS210',
//                showClose : true,
                controller: ['$scope', function($scope) {
                	$scope.resultList=resultList;
                }]
            });
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
      //理專參數2
        $scope.setSpBen = function(resultList){
            var dialog = ngDialog.open({
                template: 'assets/txn/PMS210/PMS210_setSpBen.html',
                className: 'PMS210',
//                showClose : true,
                controller: ['$scope', function($scope) {
                	$scope.resultList=resultList;
                }]
            });
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
      //理專參數3
        $scope.setSpLevd = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setSpLevd.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
      //理專參數4
        $scope.setBsGoal = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setBsGoal.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        
        //理專參數5
        $scope.setSpBouns = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setSpBouns.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
        	//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //理專參數6
        $scope.setNewCustRate = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setNewCustRate.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //理專參數7
        $scope.setNewSpBen = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setNewSpBen.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //理專參數8
        $scope.setNewBsGoal = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setNewBsGoal.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //理專參數9
        $scope.setSpSalBen = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setSpSalBen.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
        	//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //理專參數10
        $scope.setNewBounty = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setNewBounty.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
        	//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //主管參數1
        $scope.setBsAss = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setBsAss.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //主管參數2
        $scope.setBsAum = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setBsAum.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //主管參數3
        $scope.setBsPoint = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setBsPoint.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
        	//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //主管參數4
        $scope.setBsPrize = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setBsPrize.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //主管參數5
        $scope.setBsGoalTar = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setBsGoalTar.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //主管參數6
        $scope.setBsBonus = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setBsBonus.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
        	//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //共用參數2
        $scope.setShortTran = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setShortTran.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
      //共用參數3
        $scope.setSpMar = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setSpMar.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //共用參數4
        $scope.setSpUnLackInd = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setSpUnLackInd.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
	    	//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //共用參數6
        $scope.setConNum = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setConNum.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //共用參數7
        $scope.setDisRate = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setDisRate.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
	    	//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //共用參數8
        $scope.setDepositRate = function(resultList){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS210/PMS210_setDepositRate.html',
        		className: 'PMS210',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.resultList=resultList;
        		}]
        	});
	    	//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
});