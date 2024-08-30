/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC410EditController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC410EditController";
		
		$scope.init = function(){
			if($scope.row){
				var answer = $scope.row
				$scope.isUpdate = true;
				$scope.inputVO = {
						QUESTION_VERSION : $scope.row.QUESTION_VERSION,
						QUESTION_DESC :$scope.row.QUESTION_DESC,
						ANSWER_DESC : answer.Ans,
						QUESTION_TYPE : $scope.row.QUESTION_TYPE,
						DEL_ANSWER_DESC : [],
						CORR_ANS : answer.CORR_ANS
				};
			}else{
				$scope.isAdd = true;
				$scope.inputVO = {
						QUESTION_VERSION : '',
						QUESTION_DESC : '',
						ANSWER_DESC : [],
						QUESTION_TYPE : '',
						CORR_ANS : ""
				}
			}
		}
		$scope.init();
		
		
		$scope.addRow = function(){
			$scope.inputVO.ANSWER_DESC.push({});
			angular.forEach($scope.inputVO.ANSWER_DESC,function(row,index){
				row.Display_order = index + 1;
			});
		}
		
		$scope.deleteRow = function(index,row){
			if(row.ANSWER_SEQ){
				$scope.inputVO.DEL_ANSWER_DESC.push(row);
			}
			$scope.inputVO.ANSWER_DESC.splice(index,1);
			angular.forEach($scope.inputVO.ANSWER_DESC,function(row,index){
				row.Display_order = index +1;
			})
		}
		$scope.singleSelectAns = function (seq) {
			$scope.singleAnswer = seq;
		}
		//Get correct answers
		var getCorrAns = function (inputVo, row) {
			var result = "";
			inputVo.ANSWER_DESC.forEach(function(descs) {
				if (descs.CORR_ANS) {
					if (inputVo.QUESTION_TYPE=='M') {
							result = result.concat( descs.Display_order + " ");
					} else {
							result = descs.Display_order;
					}
				}
			});
			return result;
		}
		
		var refreshOrder = function(ANSWER_DESC){
			var index = 1;
			ANSWER_DESC.forEach(function(descs){
				descs.Display_order = index;
				index++;
			})
		};
		$scope.saveData = function(){
			refreshOrder($scope.inputVO.ANSWER_DESC);
			$scope.inputVO.CORR_ANS = getCorrAns($scope.inputVO, $scope.row);
			if ($scope.inputVO.CORR_ANS == "") {
				$scope.showErrorMsg('需選擇正確答案');
				return;
			}
			if($scope.isAdd){
				if($scope.inputVO.QUESTION_DESC == '' || $scope.inputVO.QUESTION_TYPE == '' || $scope.inputVO.ANSWER_DESC.length < 1 ||($scope.inputVO.ANSWER_DESC.length>=1 && $scope.inputVO.ANSWER_DESC[0].ANS_DESC == undefined)){
					$scope.showErrorMsg('ehl_01_common_022');
				}else{
					$scope.sendRecv("KYC410","saveData","com.systex.jbranch.app.server.fps.kyc410.KYC410InputVO",
							$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog('successful');
	                	};
					});
				}

			}
			if($scope.isUpdate){
				if($scope.inputVO.QUESTION_DESC == '' || $scope.inputVO.QUESTION_TYPE == ''){
					$scope.showErrorMsg('ehl_01_common_022');
				}else{
					$scope.sendRecv("KYC410","updateData","com.systex.jbranch.app.server.fps.kyc410.KYC410InputVO",
							$scope.inputVO,function(tota,isError){
						if(!isError){
							$scope.showSuccessMsg('ehl_01_common_006');
	            			$scope.closeThisDialog('successful');
						}else{
							$scope.showErrorMsg('ehl_01_common_009');
						}	
					});
				}
			}
		}
		
        $scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.tempName = name;
            	$scope.inputVO.realTempName = rname;
        	}
        };								
	}
);