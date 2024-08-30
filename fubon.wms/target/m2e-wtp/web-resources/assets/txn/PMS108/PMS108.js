/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS108Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {	
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS108Controller";
	
	$scope.init = function() {
		$scope.row = $scope.row || {};
		$scope.inputVO = {
			/**
			 * 潛力金流條件種類 含息報酬率 台幣庫存金額
			 */
			ROI1 : '', // 基金
			ROI2 : '', // ETF
			ROI3 : '', // SI/SN
			ROI4 : '', // 海外債
			AMT_TWD1 : '', // 基金
			AMT_TWD2 : '', // ETF
			AMT_TWD3 : '', // SI/SN
			AMT_TWD4 : '', // 海外債
			TYPE : '',
			ROI : '',
			AMT_TWD : '',
			TER_FEE_YEAR : '', // 險種年期
			INS_NBR : '', // 險種代號
			INS_NAME : '', // 險種名稱
			INS : '',//頁面險種代號
			YEAR : ''//頁面險種年期
		};
		$scope.List = []; // 存放新增修改的
		$scope.resultList = []; // 存放前四筆List
		$scope.paramList = []; // 存放最後List
		$scope.insList = [];//暫存保險資料List
	};
	$scope.init();
	
	//檢查是否大於零
	$scope.check=function(data){
	   data=data.replace(/[^\d\.]/g,'');
	   
	   if(data==0&&data!='') 
		  $scope.showErrorMsg("數值不得零及負數，請重新輸入");
	   return data;
	   
   }
	
	
	$scope.inquire = function() {
		//查詢基金/ETF/SI/SN/海外債
		$scope.sendRecv("PMS108", "queryMod","com.systex.jbranch.app.server.fps.pms108.PMS108InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.resultList = tota[0].body.resultList;
						angular.forEach($scope.resultList, function(row, index, objs) {
							if (row.TYPE == '0' + (1)) {
								$scope.inputVO.ROI1 = row.ROI;
								$scope.inputVO.AMT_TWD1 = row.AMT_TWD;
							}
							if (row.TYPE == '0' + (2)) {
								$scope.inputVO.ROI2 = row.ROI;
								$scope.inputVO.AMT_TWD2 = row.AMT_TWD;
							}
							if (row.TYPE == '0' + (3)) {
								$scope.inputVO.ROI3 = row.ROI;
								$scope.inputVO.AMT_TWD3 = row.AMT_TWD;
							}
							if (row.TYPE == '0' + (4)) {
								$scope.inputVO.ROI4 = row.ROI;
								$scope.inputVO.AMT_TWD4 = row.AMT_TWD;
							}
						});
					}
				});
		
		//查詢保險
		$scope.sendRecv("PMS108", "queryData","com.systex.jbranch.app.server.fps.pms108.PMS108InputVO",$scope.inputVO, 
				function(tota, isError) {
					if (!isError) {
						$scope.paramList = tota[0].body.resultList2;
						$scope.outputVO = tota[0].body;
						
						$scope.insList = [];
						//放入暫存List中供前端編輯用
						for(var i = 0; i < $scope.paramList.length; i++) {	
							$scope.insList.push({INS_NBR:$scope.paramList[i].INS_NBR  , INS_NAME:$scope.paramList[i].INS_NAME , TER_FEE_YEAR:$scope.paramList[i].TER_FEE_YEAR});										
						}
						$scope.insList
						return;
					}
				});
	}
	$scope.inquire();

	//保險加入
	$scope.saveins = function() {
		//頁面值InputVO
		$scope.inputVO.TER_FEE_YEAR = $scope.inputVO.YEAR ;
		$scope.inputVO.INS_NBR = $scope.inputVO.INS ;
		
		//檢查暫存前端List代碼有無重複
		for(var i = 0; i < $scope.insList.length; i++) {
			if($scope.insList[i].INS_NBR == $scope.inputVO.INS_NBR){
				$scope.showErrorMsg("險種代號重複，請重新輸入");
				return;
			}		
		}
		// 檢查代碼是否正確
		$scope.sendRecv("PMS108", "queryINS","com.systex.jbranch.app.server.fps.pms108.PMS108InputVO",$scope.inputVO, 
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length > 0){
						// 將資料放在暫存用陣列	
						$scope.insList.push({INS_NBR:$scope.inputVO.INS  , INS_NAME:tota[0].body.resultList[0].INS_NAME , TER_FEE_YEAR:$scope.inputVO.YEAR});
						$scope.inputVO.YEAR = '';
						$scope.inputVO.INS = '';
						return;
						
					}else{
						$scope.showErrorMsg("險種代號錯誤，請重新輸入");
						return;
					}			
				}
			});
	}
	
	//保險刪除
	$scope.delins = function(row) {
		//刪除前端資料，不更動後端資料庫
		for(var i = 0; i < $scope.insList.length; i++) {
			if($scope.insList[i].INS_NBR == row.INS_NBR){
				$scope.insList.splice(i,1);
				return;
			}
		}
	
	}
	
	/**彈跳視窗打開PMS108
	 * $scope.dialog = '1'
	 * 以此判斷是否關閉彈跳視窗
	 */
	
	//確定儲存
	$scope.save = function() {	
		
		//儲存基金/ETF/SI/SN/海外債====================================================
		$scope.List.push(
						{'TYPE' : '01','ROI' : $scope.inputVO.ROI1,'AMT_TWD' : $scope.inputVO.AMT_TWD1},
						{'TYPE' : '02','ROI' : $scope.inputVO.ROI2,'AMT_TWD' : $scope.inputVO.AMT_TWD2}, 
						{'TYPE' : '03','ROI' : $scope.inputVO.ROI3,'AMT_TWD' : $scope.inputVO.AMT_TWD3}, 
						{'TYPE' : '04','ROI' : $scope.inputVO.ROI4,'AMT_TWD' : $scope.inputVO.AMT_TWD4}
						);
		
		$scope.sendRecv("PMS108", "rMod","com.systex.jbranch.app.server.fps.pms108.PMS108InputVO", {'List' : $scope.List},
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg("修改成功");
					}
				});
		
		
		//儲存前端保險資料==============================================================
		var delcheck = 0 ;	
		var inscheck = 0 ;	
		
		//先刪除再新增
		for(var j = 0; j < $scope.paramList.length; j++) {
			/**刪除**/
			$scope.inputVO.INS_NBR = $scope.paramList[j].INS_NBR;			
			$scope.sendRecv("PMS108", "delRes","com.systex.jbranch.app.server.fps.pms108.PMS108InputVO",$scope.inputVO,
				function(tota, isError) {	
					delcheck = delcheck + 1 ;
					//刪除完後再新增
					if(delcheck == $scope.paramList.length){
						/**新增**/
						for(var k = 0; k < $scope.insList.length; k++) {						
							$scope.inputVO.INS_NBR = $scope.insList[k].INS_NBR;
							$scope.inputVO.TER_FEE_YEAR = $scope.insList[k].TER_FEE_YEAR;
							$scope.sendRecv("PMS108", "retMod","com.systex.jbranch.app.server.fps.pms108.PMS108InputVO",$scope.inputVO,
								function(tota, isError) {
									inscheck = inscheck + 1 ;
									//新增完後再關閉/重整
									if(inscheck == $scope.insList.length){
										$scope.showMsg("修改成功");
										if($scope.dialog == '1'){
											$scope.closeThisDialog('cancel');
										}else{				
											$scope.inquire();
										}
									}
								});						
						}
						
					}
				});	
			
		}
	}
	
	//取消復原
	$scope.cancel = function() {
		if($scope.dialog == '1'){
			$scope.closeThisDialog('cancel');
		}else{
			$scope.showMsg("取消編輯:資料未修正");
			$scope.inquire();
			return;
		}		
	}

});
