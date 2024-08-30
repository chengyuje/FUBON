/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD100Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, $q, validateService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD100Controller";
		
		//高齡客戶評估表檢核
		$scope.validSeniorCustEval = function () {
			debugger
			//$scope.inputVO.type == "1" 依客戶可申購商品查詢，作高齡客戶評估表檢核
			//商品查詢或特金下單
			//先不包含金錢信託 2023/11/27
			if($scope.inputVO.type == "1" && $scope.inputVO.cust_id && 
					($scope.inputVO.seniorAuthType == "A" || ($scope.inputVO.seniorAuthType == "S" && (!$scope.inputVO.trustTS || $scope.inputVO.trustTS !="M")))) { //商品查詢或特金下單
				//高齡客戶評估表檢核				
				$scope.sendRecv("SeniorValidation", "validSeniorCustEval", "com.systex.jbranch.fubon.commons.seniorValidation.SeniorValidationInputVO", {custID: $scope.inputVO.cust_id},
					function(tota, isError) {
						if (!isError) {
							var evalResult = tota[0].body.seniorCustEvalResult;
							debugger
							//看有哪些訊息需顯示
							//C：高齡客戶資訊觀察表取得能力表現是否填答需主管確認方可申購
							var findCRow = $filter('filter')(evalResult, {invalidCode: "C"}); //需主管覆核
							//D：高齡客戶資訊觀察表是否有填答不建議申購之選項(健康情況第二項及第四項)
							var findDRow = $filter('filter')(evalResult, {invalidCode: "D"}); //需提示訊息
							//E：高齡客戶資訊觀察表填寫日是否為適配當日
							var findERow = $filter('filter')(evalResult, {invalidCode: "E"}); //需顯示問卷內容
							//F：金融認知是否填答第一選項，需主管確認方可申購
							var findFRow = $filter('filter')(evalResult, {invalidCode: "F"}); //需主管覆核
							
							//C：高齡客戶資訊觀察表取得能力表現是否填答需主管確認方可申購
				        	//F：金融認知是否填答第一選項，需主管確認方可申購
							if((findCRow != null && findCRow.length > 0) || (findFRow != null && findFRow.length > 0)) {
								var custID = $scope.inputVO.custID;
								var prodType = $scope.inputVO.prodType;
					        	var tradeType = $scope.inputVO.tradeType; 
					        	var seniorAuthType = $scope.inputVO.seniorAuthType; //高齡評估表授權種類
					        	var trustTS = $scope.inputVO.trustTS;
					        	var matchDate = "";
					        	var invalidMsgC = (findCRow != null && findCRow.length > 0) ? findCRow[0].invalidMsg : "";
					        	var invalidMsgF = (findFRow != null && findFRow.length > 0) ? findFRow[0].invalidMsg : "";
					        	var invalidMessage = "";
					        	if(invalidMsgC != "" && invalidMsgF != "") {
					        		invalidMessage = invalidMsgC + "," + invalidMsgF;
					        		matchDate = findCRow[0].matchDate;
					        	} else {
					        		invalidMessage = (invalidMsgC == "" ? invalidMsgF : invalidMsgC);
					        		matchDate = (invalidMsgC == "" ? findFRow[0].matchDate : findCRow[0].matchDate);
					        	}
					        	
								//覆核頁面顯示的答案訊息
								var invalidMsgList = (invalidMessage == "") ? {} : invalidMessage.split(",");
								var dialog = ngDialog.open({
									template: 'assets/txn/PRD100/PRD100_BOSS.html',
									className: 'PRD100_BOSS',
									showClose: false,
									scope : $scope,
									controller: ['$scope', function($scope) {
										$scope.invalidMsgList = invalidMsgList;
										$scope.matchDate = matchDate;
										$scope.prodType = prodType;
										$scope.tradeType = tradeType;
										$scope.seniorAuthType = seniorAuthType;
										$scope.custID = custID;
										$scope.trustTS = trustTS;
										$scope.invalidMsgC = invalidMsgC;
										$scope.invalidMsgF = invalidMsgF;
									}]
								}).closePromise.then(function (data) {
									if (data.value === 'successful') {
										if(findERow != null && findERow.length > 0) {
											//E：高齡客戶資訊觀察表填寫日是否為適配當日
											var custid = $scope.inputVO.cust_id;
											var dialog = ngDialog.open({
												template: 'assets/txn/PRD100/PRD100_SENIOR_QZ.html',
												className: 'PRD100_SENIOR_QZ',
												showClose: false,
												scope : $scope,
												controller: ['$scope', function($scope) {
													$scope.custId = custid;
													$scope.fromPRD = "Y";
												}]
											}).closePromise.then(function (data) {
												if (data.value === 'successful') {
//													//確認問卷通過
//													if(findDRow != null && findDRow.length > 0) {
//														$scope.showMsg(findDRow[0].invalidMsg);
//													}
//													$scope.reallyInquire(); //**都通過，查詢商品或客戶資料
													
													//客戶狀態已有變化(不管有無變化都須重新檢核)
													$scope.showMsg("客戶狀態已有變化或今日已更新，請重新輸入客戶ID");
													$scope.clearCustInfo(); //高齡客戶評估表檢核"不"通過
												} else {
													if (data.value === 'statusChanged') {
														//客戶狀態已有變化
														$scope.showMsg("客戶狀態已有變化或今日已更新，請重新輸入客戶ID");
													}
													$scope.clearCustInfo(); //高齡客戶評估表檢核"不"通過
												}
											});
										} else {
											if(findDRow != null && findDRow.length > 0) {
												//D：高齡客戶資訊觀察表是否有填答不建議申購之選項(健康情況第二項及第四項)
												$scope.showMsg(findDRow[0].invalidMsg);
											}
											$scope.reallyInquire(); //**都通過，查詢商品或客戶資料
										}
									} else {
										$scope.clearCustInfo(); //高齡客戶評估表檢核"不"通過
									}
								});
							} else {
								if(findERow != null && findERow.length > 0) {
									//E：高齡客戶資訊觀察表填寫日是否為適配當日
									var custid = $scope.inputVO.cust_id;
									var dialog = ngDialog.open({
										template: 'assets/txn/PRD100/PRD100_SENIOR_QZ.html',
										className: 'PRD100_SENIOR_QZ',
										showClose: false,
										scope : $scope,
										controller: ['$scope', function($scope) {
											$scope.custId = custid;
											$scope.fromPRD = "Y";
										}]
									}).closePromise.then(function (data) {
										if (data.value === 'successful') {
//											//確認問卷通過
//											if(findDRow != null && findDRow.length > 0) {
//												$scope.showMsg(findDRow[0].invalidMsg);
//											}
//											$scope.reallyInquire(); //**都通過，查詢商品或客戶資料
											
											//客戶狀態已有變化(不管有無變化都須重新檢核)
											$scope.showMsg("客戶狀態已有變化或今日已更新，請重新輸入客戶ID");
											$scope.clearCustInfo(); //高齡客戶評估表檢核"不"通過
										} else {
											if (data.value === 'statusChanged') {
												//客戶狀態已有變化
												$scope.showMsg("客戶狀態已有變化或今日已更新，請重新輸入客戶ID");
											}
											$scope.clearCustInfo(); //高齡客戶評估表檢核"不"通過
										}
									});
								} else {
									if(findDRow != null && findDRow.length > 0) {
										//D：高齡客戶資訊觀察表是否有填答不建議申購之選項(健康情況第二項及第四項)
										$scope.showMsg(findDRow[0].invalidMsg);
									}
									$scope.reallyInquire(); //**都通過，查詢商品或客戶資料
								}
							}
						} else {
							$scope.clearCustInfo(); //高齡客戶評估表檢核"不"通過
						}
				});
			} else {
				//非依客戶可申購商品查詢
				$scope.reallyInquire(); //**都通過，查詢商品或客戶資料
			}
		}
		
});