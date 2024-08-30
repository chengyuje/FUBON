/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS140Controller', function(
	$rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter
) {

	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "INS140Controller";
	
	/**function 定義**/
	//初始化
	$scope.init = function(isChecked) {
		if (undefined != $scope.connector('get', "TO_INS140")) {
			$scope.CUST_VO = $scope.connector('get', "TO_INS140");	
			$scope.connector('set', "TO_INS140", undefined);
		}
		
		$scope.inputVO = {
				custID		:	'',
				examKeyNO	:	'',
				self		:	true,
				relation	:	false,
				object		:	'self',
//				itemListRitem	:	[],
//				itemListLitem	:	[],
				itemList	:	[],
				lstFamily	:	[],
				lstFamilyTemp:	[],
				lstFamilyOrg:	[],
				familyFinItemList:	[] // 添加紀錄 家庭財務安全報告書部分 sen
		};
		
		$scope.hasPolicy = true; // 有行內外保單
		$scope.hasQuestionnaire = true; // 有做過財物安全問卷
		
		$scope.inputVO.itemList = [];
		//列印所有報表 - 左方清單
		$scope.selectAll('L' , $scope.selectAllLift = (isChecked == 'N' ? 'N' : 'Y'));
		$scope.selectAll('R' , $scope.selectAllRight = (isChecked == 'N' ? 'N' : 'Y'));
		
		if ($scope.CUST_VO != undefined) {
			if ($scope.CUST_VO.CUST_ID == undefined) {
				// 回到INS000
				$scope.connector('set', "INS_PARGE", "INS100");
				$rootScope.menuItemInfo.url = "assets/txn/INS100/INS100.html";
				$scope.showMsg('未有進行健診紀錄。');
			} else {
				$scope.inputVO.custID = $scope.CUST_VO.CUST_ID;
				$scope.inputVO.examKeyNO = $scope.CUST_VO.examKeyNO;
			}
		}
		
		var queryRelCallBack = function(tota,isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			} else {
				$scope.inputVO.lstFamily = tota[0].body.resultList
				
				if ($scope.inputVO.lstFamily.length == 0) {
					// TODO
					$scope.inputVO.object = "self";
					$scope.inputVO.self = true;
					$scope.inputVO.relation = false;
				}
			}
		};
		
		//關係戶
		$scope.sendRecv("INS140", "relationList", "com.systex.jbranch.app.server.fps.ins140.INS140InputVO", {custID : $scope.inputVO.custID},
   			function(tota,isError) {
    			if (isError) {
    				$scope.showErrorMsg(tota[0].body.msgData);
    			} else {
    				$scope.inputVO.lstFamilyTemp = tota[0].body.relationList;
    				
    				if ($scope.inputVO.lstFamilyTemp.length == 0) {
    					$scope.inputVO.object = "self";
    					$scope.inputVO.self = true;
    					$scope.inputVO.relation = false;
    				}
    			}
    	});
		
		$scope.checkCustPrint();
		console.log(JSON.stringify($scope.inputVO.itemList));
	};
	
	// 確認使用者是否能列印
	/**
	 * 	如果沒有任何行內或行外保單
   		1. [保單健診報告書] 下的 6 個 tick 全部不能勾選
   		2. 跳出一般訊息: 沒有任何保單，無法列印[保單健診報告書]

		如果沒有做過[家庭財務安全規劃]
   		1. [家庭財務安全報告書] 下的 3 個 tick 全部不能勾選
   		2. 跳出一般訊息: 沒有規劃家庭財務安全，無法列印[家庭財務安全報告書]
	 */
	$scope.checkCustPrint = function() {
		$scope.sendRecv("INS140", "checkCustPrint", "com.systex.jbranch.app.server.fps.ins140.INS140InputVO", {custID : $scope.inputVO.custID},
	   			function(tota,isError) {
				debugger
	    			if (isError) {
	    				$scope.showErrorMsg(tota[0].body.msgData);
	    			} else {
	    				$scope.hasPolicy = tota[0].body[0];
	    				$scope.hasQuestionnaire = tota[0].body[1];
	    				
	    				if(!$scope.hasPolicy && !$scope.hasQuestionnaire) {
	    					$scope.selectAll('L' , $scope.selectAllLift = 'N');
	    					$scope.selectAll('R' , $scope.selectAllRight = 'N');
	    					$scope.showMsg("客戶並未有在本行建立過保單資料，或攜帶非本行保單進行過健診資料；也並未有進行過 家庭財務安全規劃 相關填寫；因此無法進行 保單健診報告書 與 家庭財務安全報告書 列印操作。");
	    					
	    				} else {
	    					if(!$scope.hasPolicy) {
		    					// 不能操作 保單健診報告書
	    						$scope.selectAll('L' , $scope.selectAllLift = 'N');
	    						$scope.showMsg("客戶並未有在本行建立過保單資料，或攜帶非本行保單進行過健診資料；因此無法進行 保單健診報告書 列印操作");
		    				} 
	    					if(!$scope.hasQuestionnaire) {
		    					// 不能操作 家庭財務安全報告書
		    					$scope.selectAll('R' , $scope.selectAllRight = 'N');
		    					$scope.showMsg("客戶並未有進行過 家庭財務安全規劃 相關填寫；因此無法進行 家庭財務安全報告書 列印操作");
		    				} 	
	    				}
	    			}
	    	});
	}
	
	//列印
	$scope.printPolicyReport = function() {
		//判斷是否勾選家庭戶
		/// ????? 沒用到阿= =||
//		isErr = isErr || (
//			$scope.inputVO.allINS == 'N' &&  //現有保單一覽表
//			$scope.inputVO.insItme == 'N' && //保障項目彙總表
//			$scope.inputVO.custINS == 'N' && //個人保險保障彙總表
//			$scope.inputVO.relationINS == 'N' && //家庭保險保障彙總表
//			$scope.inputVO.insDetail == 'N'//保障項目明細表
//		);
		console.log(JSON.stringify($scope.inputVO.itemList));
		// 改 radio 不會有都沒勾的情況 default 就是 self
		var isErr = ($scope.inputVO.object == 'relation') && $scope.inputVO.lstFamily.length == 0; 
		
		if(isErr) {
			$scope.showMsg('對象為:包含家庭關係戶，請選擇家庭關係戶成員');
			return;
		}
		
		//判斷有沒有勾選列印表單
		isErr = isErr || $scope.inputVO.itemList.length == 0;
		
		if (isErr) {
			$scope.showMsg('至少選擇一個項目進行列印，不能不選');
			return;
		}
		$scope.inputVO.self = $scope.inputVO.object == 'self';
		$scope.inputVO.relation = $scope.inputVO.object == 'relation';
		
		$scope.showMsg('健診時請檢視要保人、被保險人及受益人關係。如滿期金、生存金給付時，要、受不同人可能產生相關稅負，請點選上方保經代服務網連結，查詢保單詳細資訊。');
		$scope.sendRecv("INS140", "printPolicyReport", "com.systex.jbranch.app.server.fps.ins140.INS140InputVO", 
			$scope.inputVO, function (tota, isError) {
				if (!isError) {
					debugger
				} else {
					$scope.showMsg(tota.body[msgData][0]);
				}
			});
	};
	
	//選取其中一項
	$scope.select = function(key, value) {
		if (value == 'Y') {
			console.log(key + "," + value);
			$scope.inputVO.itemList.push({key : key, value : value});
		} 
		else {
			for (var i = 0 ; i < $scope.inputVO.itemList.length ; i++) {
				if ($scope.inputVO.itemList[i].key == key) {
					$scope.inputVO.itemList.splice(i, 1);
				}
			}
		}
		
		$scope.selectAllLift = isValueYes(
			$scope.policyDetail , $scope.viewStructure , $scope.indYearSum , $scope.familyYear , $scope.viewSum
		) ? 'Y' : 'N';
		
		$scope.selectAllRight = isValueYes($scope.familyFeeGap , $scope.familySave) ? 'Y' : 'N';
	}
	
	//列印所有表單，所有checkbox打勾或取消打勾
	$scope.selectAll = function(selType , value) {
		if(selType == 'L'){
			var itemArr = ['policyDetail' , 'viewStructure' , 'indYearSum' , 'familyYear' , 'viewSum'];
			for(var i = 0 ; i < itemArr.length ; i++){
				$scope[itemArr[i]] = value;
				$scope.select(itemArr[i], value);
			}
		}
		else if(selType == 'R'){
			var itemArr = ['familyFeeGap' , 'familySave'];
			for(var i = 0 ; i < itemArr.length ; i++){
				$scope[itemArr[i]] = value;
				$scope.select(itemArr[i], value);
			}
		}
	};
	
	$scope.goRelation = function(CUST_ID) {
		$scope.inputVO.self = $scope.inputVO.object == 'self';
		$scope.inputVO.relation = $scope.inputVO.object == 'relation';
		var scope = $scope;
		var dialog = ngDialog.open({
			template: 'assets/txn/INS140/INS140_RELATION.html',
			className: 'INS140',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.CUST_ID = CUST_ID;
				$scope.paramList = scope.inputVO.lstFamilyTemp;
			}]
		})
		.closePromise.then(function (data) {
			$scope.inputVO.lstFamily = data.value;
        });
	};
	
	function isValueYes(){
		var isSuccess = true;
		
		for (var i = 0; i < arguments.length; i++){
			isSuccess = isSuccess && arguments[i] == 'Y';
		}
		
		return isSuccess;
	}
	
	//開始執行
	var page = $scope.connector('get','INS140_query');
	if(page) {
		$scope.init();
		$scope.connector('set','INS140_query', undefined);
	}
});