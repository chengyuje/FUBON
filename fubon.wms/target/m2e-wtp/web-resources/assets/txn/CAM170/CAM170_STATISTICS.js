/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM170_STATISTICSController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM170_STATISTICSController";
		
		$scope.init = function(){
			$scope.inputVO = {
				examVersion: $scope.examVersion
	    	};
		};
		$scope.init();
		
		$scope.sendRecv("CAM170", "getQstListByExamID", "com.systex.jbranch.app.server.fps.cam170.CAM170InputVO", {examVersion: $scope.examVersion},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.questionList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.questionList = tota[0].body.questionList;
						$scope.outputVO = tota[0].body;
						
						angular.forEach($scope.questionList, function(row, index, objs){
							row.answer = [];
							if (row.QUESTION_TYPE == "S" || row.QUESTION_TYPE == "M") {
					        	$scope.sendRecv("CAM170", "getAnsList", "com.systex.jbranch.app.server.fps.cam170.CAM170InputVO", {questionVersion: row.QUESTION_VERSION, 
					        																									   examVersion: row.EXAM_VERSION},
										function(tota1, isError) {
											if (!isError) {
												angular.forEach(tota1[0].body.answerList, function(resRow, index, objs) {
													row.answer.push({ANSWER_SEQ: resRow.ANSWER_SEQ, 
																	 ANSWER_DESC: resRow.ANSWER_DESC,
																	 COUNTS: resRow.COUNTS});
													
													
												});
											}
										}
								);
							} else { //
								$scope.sendRecv("CAM170", "getAnsOthersList", "com.systex.jbranch.app.server.fps.cam170.CAM170InputVO", {questionVersion: row.QUESTION_VERSION, 
																																		 examVersion: row.EXAM_VERSION},
										function(tota1, isError) {
										   if (!isError) {
											   angular.forEach(tota1[0].body.answerList, function(resRow, index, objs) {
												   row.answer.push({ANSWER_SEQ: resRow.ANSWER_SEQ, 
													   				ANSWER_DESC: resRow.ANSWER_DESC,
													   				REMARK: resRow.REMARK});
										
										
											   });
										   }
											}
										);
							}
						});
						
						return;
					}
		});
		
		$scope.others = function(row, answerRow) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CAM170/CAM170_OTHERS.html',
				className: 'CAM170_OTHERS',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.examVersion = row.EXAM_VERSION;
                	$scope.questionVersion = row.QUESTION_VERSION;
                	$scope.questionDesc = row.QUESTION_DESC;
                	$scope.answerSEQ = answerRow.ANSWER_SEQ;
                	$scope.answerDesc = answerRow.ANSWER_DESC;
                }]
			})
		}
});
