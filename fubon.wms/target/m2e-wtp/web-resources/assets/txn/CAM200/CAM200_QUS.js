/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM200_QUSController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM200_QUSController";
		
		$scope.disableType = ("read" == $scope.actionType ? true : false);
		
		$scope.init = function(){
			$scope.inputVO = {
					examVersion: $scope.examVersion, 
					custID: $scope.custID, 
					custName: $scope.custName, 
					questionnaireList: []
        	};
			
			
		}
		$scope.init();
		
		$scope.getQuestionList = function() {
        	$scope.sendRecv("CAM200", "getQuestionList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
        			function(tota, isError) {
						if (!isError) {
							if(tota[0].body.questionList.length == 0) {
//								$scope.showMsg("ehl_01_common_009");
								return;
							}
							$scope.questionList = tota[0].body.questionList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.questionList, function(row, index, objs){
					        	$scope.sendRecv("CAM200", "getAnswerList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", {questionVersion: row.QUESTION_VERSION, 
					        																										  examVersion: row.EXAM_VERSION, 
					        																										  custID: $scope.inputVO.custID},
										function(tota1, isError) {
					        				row.answer = [];
					        				
											if (!isError) {
												angular.forEach(tota1[0].body.answerList, function(resRow, index, objs) {
													row.answer.push({ANSWER_DESC: resRow.ANSWER_DESC, 
																	 ANSWER_SEQ: resRow.ANSWER_SEQ,
																	 ANSWER_NGCHECK: resRow.EXIST, 
																	 ANSWER_REMARK: resRow.REMARK});
													
													if (resRow.EXIST == 'Y') {
														var question = resRow.QUESTION_VERSION;
														var answerSEQ = resRow.ANSWER_SEQ;
														var answer = (null == resRow.ANSWER_DESC || "其他" == resRow.ANSWER_DESC ? resRow.REMARK : resRow.ANSWER_DESC);
														$scope.inputVO.questionnaireList.push({questionVersion: question, custAnswer: answerSEQ, custAnswerDesc: answer});
													}
												});
											}
										}
								);
							});
							return;
						}
	        		}
        	);
		}
		$scope.getQuestionList();
		
		$scope.toggleSelection = function toggleSelection(type, question, answerSEQ, answerDESC, answer) {
			if ($scope.inputVO.questionnaireList.length > 0) {
					var index = 0;
					var temp;
					$scope.inputVO.questionnaireList.map(function(e) { 
						if (type == "checkbox") {
							if (e.questionVersion == question && e.custAnswer == answerSEQ) {
								temp = index;
								return;
							}
						} else if (type == "radio" || type == "text") {
							if (e.questionVersion == question && e.custAnswerDesc == answer) {
								temp = "noAction";
								return;
							} else if (e.questionVersion == question && e.custAnswerDesc != answer) {
								temp = index;
								return;
							} 
						} else if (type == "others" && e.questionVersion == question && e.custAnswer == answerSEQ) {
							temp = index;
							return;
						}
						index++;
					});
					
					if (typeof(temp) !== "undefined" ) {
						if (temp != "noAction" && (type == "radio" || type == "text" || type == "others")) {
							$scope.inputVO.questionnaireList.splice(temp, 1);
							$scope.inputVO.questionnaireList.push({questionVersion: question, custAnswer: answerSEQ, custAnswerDesc: (typeof(answer) !== "undefined" ? answer : "")});
						} else if (temp != "noAction"){
							$scope.inputVO.questionnaireList.splice(temp, 1);
						}
					} else if (type != "others"){
						$scope.inputVO.questionnaireList.push({questionVersion: question, custAnswer: answerSEQ, custAnswerDesc: (typeof(answer) !== "undefined" ? answer : "")});
					}
			} else if (type != "others"){
				$scope.inputVO.questionnaireList.push({questionVersion: question, custAnswer: answerSEQ, custAnswerDesc: (typeof(answer) !== "undefined" ? answer : "")});
			}
        };
        
        $scope.save = function() {
        	if (!$scope.disableType) {
	        	$scope.sendRecv("CAM200", "saveQuestionnaire", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
	        			function(totas, isError) {
	    	                if (isError) {
	    	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	    	                    return;
	    	                }
	    	                 if (totas.length > 0) {
	    	                	 $scope.showMsg('儲存成功');
	    	                	 $scope.closeThisDialog("successful");
	    	                 };
	    	            }
	            	);
        	} else {
        		$scope.closeThisDialog("cancel");
        	}
        }
});
