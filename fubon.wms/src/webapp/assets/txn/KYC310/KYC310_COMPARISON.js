/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC310_COMPARISONController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC310_COMPARISONController";

		//點選"確定"
		$scope.returnQuesCompData = function() {
			debugger
			var ansCompList = [];
			//檢查是否每個問題都有回答
			for(var a = 0 ; a < $scope.inputVO.COMP_QUES.length ; a++) {
				var ansComp = [];
				
				if($scope.inputVO.COMP_QUES[a].QST_NO == 3) {
					debugger
					//第三題
					for(var b = 0 ; b < $scope.inputVO.COMP_QUES[a].Q3_PROD_TYPEList_COMP.length ; b++) {
						if(!$scope.inputVO.COMP_QUES[a].Q3_PROD_TYPEList_COMP[b].sameAnsSelect) {
							var q3SelCount = 0;
							//本次與前次答案不相同，檢查是否有回答
							for(var c = 0 ; c < $scope.inputVO.COMP_QUES[a].Q3_PROD_TYPEList_COMP[b].ANSWER_LIST_COMP.length ; c++) {
								if($scope.inputVO.COMP_QUES[a].Q3_PROD_TYPEList_COMP[b].ANSWER_LIST_COMP[c].select){
									ansComp.push($scope.inputVO.COMP_QUES[a].Q3_PROD_TYPEList_COMP[b].ANSWER_LIST_COMP[c].ANSWER_SEQ);
									q3SelCount++;
								}
								//檢查是否每個問題都有回答
								if(c == $scope.inputVO.COMP_QUES[a].Q3_PROD_TYPEList_COMP[b].ANSWER_LIST_COMP.length - 1) {
									if(q3SelCount <= 0){
										$scope.showErrorMsgInDialog("Q"+$scope.inputVO.COMP_QUES[a].QST_NO+"未選取答案");
										return;
									}
								}
							}
						} else {
							//答案相同不用填答，放0
							ansComp.push(0);
						}
					}
				} else {
					//非第三題
					if(!$scope.inputVO.COMP_QUES[a].sameAnsSelect) {
						//本次與前次答案不相同，檢查是否有回答
						var selCount = 0;
						for(var b = 0 ; b < $scope.inputVO.COMP_QUES[a].ANSWER_LIST_COMP.length ; b++) {
							if($scope.inputVO.COMP_QUES[a].ANSWER_LIST_COMP[b].select){
								ansComp.push($scope.inputVO.COMP_QUES[a].ANSWER_LIST_COMP[b].ANSWER_SEQ);
								selCount++;
							}
							//檢查是否每個問題都有回答
							if(b == $scope.inputVO.COMP_QUES[a].ANSWER_LIST_COMP.length - 1) {
								if(selCount <= 0){
									$scope.showErrorMsgInDialog("Q"+$scope.inputVO.COMP_QUES[a].QST_NO+"未選取答案");
									return;
								}
							}
						}
					} else {
						//答案相同不用填答，放0
						ansComp.push(0);
					}
				}
				ansCompList.push(ansComp);
			}
			
			//沒有錯誤，回傳資料差異表填答答案內容
			$scope.closeThisDialog(ansCompList);
			return;
		}
		
		//列印空白差異表單
		$scope.printBlankCompRpt = function() {
			$scope.sendRecv("KYC310", "printBlankCompRpt", "com.systex.jbranch.app.server.fps.kyc310.KYC310InputVO", $scope.inputVO , function(tota,isError){
				if(tota[0].body == true){
    				//$scope.showMsg("列印空白");
				}
			});
		}
		
		//清除前項選擇項目
		$scope.ansClearComp = function(ansList, row) {
        	for(var a=0; a<ansList.length; a++){
        		if(ansList[a].ANSWER_SEQ != row.ANSWER_SEQ) {
        			ansList[a].select = undefined;
        		}
        	}
        }
		
		$scope.init = function() {
			debugger
			//沒幹嘛，看inputVO內容
			var inputVO = $scope.inputVO;
		};
        $scope.init();
        
        
});
