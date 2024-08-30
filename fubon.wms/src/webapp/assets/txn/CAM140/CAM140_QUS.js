/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM140_QUSController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM140_QUSController";
		
		//===filter
        var vo = {'param_type': 'COMMON.YES_NO', 'desc': false};
        if(!projInfoService.mappingSet['COMMON.YES_NO']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['COMMON.YES_NO'] = totas[0].body.result;
        			$scope.mappingSet['COMMON.YES_NO'] = projInfoService.mappingSet['COMMON.YES_NO'];
        		}
        	});
        } else {
        	$scope.mappingSet['COMMON.YES_NO'] = projInfoService.mappingSet['COMMON.YES_NO'];
        }
        
        var vo = {'param_type': 'SYS.QUESTION_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['SYS.QUESTION_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['SYS.QUESTION_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['SYS.QUESTION_TYPE'] = projInfoService.mappingSet['SYS.QUESTION_TYPE'];
        		}
        	});
        } else {
        	$scope.mappingSet['SYS.QUESTION_TYPE'] = projInfoService.mappingSet['SYS.QUESTION_TYPE'];
        }
        //===
        
		$scope.init = function(){
			$scope.data = [];
			$scope.questionList = [];
			
			$scope.inputVO = {
					moduleCategory: 'CAM', 
					examVersion: $scope.exam_id, 
					questionVersionList: [], 
					examName: ''
        	};

    		if ($scope.exam_id == '' || $scope.exam_id == null) {
            	$scope.sendRecv("CAM140", "getExamVersion", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.examVersion = tota[0].body.examVersion;
								return;
							}
						}
				);
			} else {
            	$scope.sendRecv("CAM140", "getExam", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO,
    					function(tota, isError) {
    						if (!isError) {
    							$scope.inputVO.questionVersionList = tota[0].body.questionVersionList;
    							$scope.inputVO.examName = tota[0].body.examName;
    							return;
    						}
    			});
			}
		};
        $scope.init();
        
        // 初始分頁資訊
        $scope.inquireInit = function(){
			$scope.questionList = [];
		}
		$scope.inquireInit();
		
		$scope.inquire = function() {
        	$scope.sendRecv("CAM140", "getQuestionList", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO,
        			function(tota, isError) {
						if (!isError) {
							if(tota[0].body.questionList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}
							$scope.questionList = tota[0].body.questionList;
							$scope.outputVO = tota[0].body;
							return;
						}
					}
			);
        }
		$scope.inquire();
		
		$scope.addQuestion = function(row) {
			var idx = $scope.inputVO.questionVersionList.indexOf(row.QUESTION_VERSION);
        	if (idx > -1) {
        		$scope.inputVO.questionVersionList.splice(idx, 1);
        	} else {
        		$scope.inputVO.questionVersionList.push(row.QUESTION_VERSION);
        	}
		}
		
		$scope.save = function(){
        	$scope.sendRecv("CAM140", "addQuestion", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", $scope.inputVO,
    			function(totas, isError) {
	                if (isError) {
	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	                    return;
	                }
	                 if (totas.length > 0) {
	                	 $scope.showMsg("ehl_01_common_025");
	                	 $scope.closeThisDialog($scope.inputVO.examVersion);
	                 };
	            }
        	);
        }
});
