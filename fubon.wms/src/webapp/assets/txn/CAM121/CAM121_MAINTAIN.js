/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM121_MAINTAINController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM121_MAINTAINController";
		
		var questionVersion = ($scope.actionType == 'update' ? $scope.questionVersion : "");

		$scope.init = function(){
			$scope.inputVO = {
					questionVersion: questionVersion,
					questionDesc: '', 
					questionType: '',
					ansOtherFlag: false, 
					ansMemoFlag: false,
					answerDesc: '', 
					answerSEQ: ''
			}
			
			if ($scope.actionType == 'update') {
	        	$scope.sendRecv("CAM121", "query", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", {questionVersion: questionVersion},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.questionDesc = tota[0].body.questionList[0].QUESTION_DESC;
								$scope.inputVO.questionType = tota[0].body.questionList[0].QUESTION_TYPE;
								$scope.inputVO.ansOtherFlag = (tota[0].body.questionList[0].ANS_OTHER_FLAG == 'Y' ? true : false);
								$scope.inputVO.ansMemoFlag = (tota[0].body.questionList[0].ANS_MEMO_FLAG == 'Y' ? true : false);

								return;
							}
				});
			}
			
			if (questionVersion == "") {
				$scope.sendRecv("CAM121", "initial", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", {},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.questionVersion = tota[0].body.questionVersion;
								return;
							}
						}
				);
			}
		};
		$scope.init();
		
		$scope.getAnswerList = function() {
			$scope.sendRecv("CAM121", "getAnswerList", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.answerList = [];
				
						if (!isError) {
							if(tota[0].body.answerList.length == 0) {
		            			return;
		            		}
							$scope.answerList = tota[0].body.answerList;
							$scope.outputVO = tota[0].body;
														
							return;
						}
					}
			);
		}
		$scope.getAnswerList();
		
		$scope.addAnswer = function() {
			$scope.sendRecv("CAM121", "addAnswer", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", $scope.inputVO,
	    			function(totas, isError) {
	        			if (isError) {
	        				$scope.showErrorMsgInDialog(totas.body.msgData);
	        				return;
		                }
		                if (totas.length > 0) {
		                	$scope.inputVO.answerDesc = "";
		                	$scope.getAnswerList();
		                };
		            }
	        	);
		}
		
		$scope.delAnswer = function(row) {
			$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
				$scope.inputVO.answerSEQ = row.ANSWER_SEQ;
				$scope.sendRecv("CAM121", "delAnswer", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", $scope.inputVO,
		    			function(totas, isError) {
		        			if (isError) {
		        				$scope.showErrorMsgInDialog(totas.body.msgData);
		        				return;
			                }
			                if (totas.length > 0) {
			                	$scope.inputVO.answerDesc = "";
			                	$scope.getAnswerList();
			                };
			            }
				);
			});
		}
		
		$scope.submitAction = function() {
			if ($scope.actionType == 'insert') {
				$scope.sendRecv("CAM121", "addQuestion", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
    	                		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
			} else { //update
				$scope.sendRecv("CAM121", "updQuestion", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
    	                		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
			}
		}
		
		$scope.checkQusExist = function() {
			$scope.sendRecv("CAM121", "checkQusExist", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.closeThisDialog('cencel');
	                	};
					}
			);
		}
		
		$scope.checkFlagStatus = function() {
			if (!$scope.inputVO.ansOtherFlag) {
				$scope.inputVO.ansMemoFlag = $scope.inputVO.ansOtherFlag;
			}
		}
		
});
