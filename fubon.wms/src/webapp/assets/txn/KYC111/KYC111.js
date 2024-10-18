/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC111Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC111Controller";
		
		$scope.init = function(){
			debugger
			if($scope.row){
				var answer = $scope.row
				$scope.isUpdate = true;
				$scope.inputVO = {
						QUESTION_VERSION : $scope.row.QUESTION_VERSION,
						QUESTION_DESC : $scope.row.QUESTION_DESC,
						QUESTION_DESC_ENG : $scope.row.QUESTION_DESC_ENG,
						ANSWER_DESC : answer.Ans,
						ANS_OTHER_FLAG : $scope.row.ANS_OTHER_FLAG,
						ANS_MEMO_FLAG : $scope.row.ANS_MEMO_FLAG,
						QUESTION_TYPE : $scope.row.QUESTION_TYPE,
						DEL_ANSWER_DESC : [],
						DOC_ID : $scope.row.DOC_ID,
						ANSWER_DESC_COMP : answer.AnsComp,
						DEL_ANSWER_DESC_COMP : []
				};
			}else{
				$scope.isAdd = true;
				$scope.inputVO = {
						QUESTION_VERSION : '',
						QUESTION_DESC : '',
						QUESTION_DESC_ENG : '',
						ANSWER_DESC : [],
						ANS_OTHER_FLAG : 'N',
						ANS_MEMO_FLAG : 'N',
						QUESTION_TYPE : '',
						ANSWER_DESC_COMP : []
						
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
				$scope.inputVO.DEL_ANSWER_DESC.push(row.ANSWER_SEQ);
			}
			$scope.inputVO.ANSWER_DESC.splice(index,1);
			angular.forEach($scope.inputVO.ANSWER_DESC,function(row,index){
				row.Display_order = index +1;
			});
		}
		
		$scope.saveData = function(){
			if($scope.isAdd){
				if($scope.inputVO.QUESTION_DESC == undefined || $scope.inputVO.QUESTION_DESC == null || $scope.inputVO.QUESTION_DESC == ''
						|| $scope.inputVO.QUESTION_DESC_ENG == undefined || $scope.inputVO.QUESTION_DESC_ENG == null || $scope.inputVO.QUESTION_DESC_ENG == ''
						|| $scope.inputVO.QUESTION_TYPE == undefined || $scope.inputVO.QUESTION_TYPE == null || $scope.inputVO.QUESTION_TYPE == ''
						|| $scope.inputVO.ANSWER_DESC.length < 1 
						|| ($scope.inputVO.ANSWER_DESC.length >= 1 && 
								($scope.inputVO.ANSWER_DESC[0].ANS_DESC == undefined || $scope.inputVO.ANSWER_DESC[0].ANS_DESC_ENG == undefined
								 || $scope.inputVO.ANSWER_DESC[0].ANS_DESC == null || $scope.inputVO.ANSWER_DESC[0].ANS_DESC_ENG == null
								 || $scope.inputVO.ANSWER_DESC[0].ANS_DESC == '' || $scope.inputVO.ANSWER_DESC[0].ANS_DESC_ENG == ''))) {
					$scope.showErrorMsg('ehl_01_common_022');
				}else{
					$scope.sendRecv("KYC111","saveData","com.systex.jbranch.app.server.fps.kyc111.KYC111InputVO",
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
				if($scope.inputVO.QUESTION_DESC == '' || $scope.inputVO.QUESTION_DESC_ENG == '' || $scope.inputVO.QUESTION_TYPE == ''){
					$scope.showErrorMsg('ehl_01_common_022');
				}else{
					$scope.sendRecv("KYC111","updateData","com.systex.jbranch.app.server.fps.kyc111.KYC111InputVO",
							$scope.inputVO,function(tota,isError){
							$scope.showSuccessMsg('ehl_01_common_006');
	            			$scope.closeThisDialog('successful');
	
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
		
        $scope.addRowComp = function(){
			$scope.inputVO.ANSWER_DESC_COMP.push({});
			angular.forEach($scope.inputVO.ANSWER_DESC_COMP,function(row,index){
				row.Display_order = index + 1;
			});
		}
        
		$scope.deleteRowComp = function(index,row){
			if(row.ANSWER_SEQ_COMP){
				$scope.inputVO.DEL_ANSWER_DESC_COMP.push(row.ANSWER_SEQ_COMP);
			}
			$scope.inputVO.ANSWER_DESC_COMP.splice(index,1);
			angular.forEach($scope.inputVO.ANSWER_DESC_COMP,function(row,index){
				row.Display_order = index +1;
			});
		}
		
		
	}
);