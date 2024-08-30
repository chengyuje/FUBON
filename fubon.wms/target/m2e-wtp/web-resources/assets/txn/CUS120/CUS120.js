'use strict';
eSoafApp.controller('CUS120Controller',
	function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS120Controller";
		
		// combobox
		getParameter.XML(["SYS.CATEGORY_CONTENT", "SYS.CATEGORY_PRODUCT"], function(totas) {
			if (totas) {
				$scope.CATEGORY_CONTENT = totas.data[totas.key.indexOf('SYS.CATEGORY_CONTENT')];
				$scope.CATEGORY_PRODUCT = totas.data[totas.key.indexOf('SYS.CATEGORY_PRODUCT')];
			}
		});
		
		$scope.emailContent = {};
		$scope.attachContent = {};
		
		// ------------------------2017/4/18 email part---------------------------------
		$scope.emailContent = function() {
		   	 $scope.sendRecv("CUS120", "queryEmailContent", "com.systex.jbranch.app.server.fps.cus120.CUS120ContentInputVO", {},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
			                	return;
			                }
							$scope.emailContent.emailContentList = tota[0].body.resultList;
							$scope.emailContent.outputVO_1 = tota[0].body;
							$scope.emailContent.conDis = false;
							angular.forEach($scope.emailContent.emailContentList, function(row) {
								if(row.REVIEW_STATUS == 'W')
									$scope.emailContent.conDis = true;
								row.set = [];
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							return;
					}
			});
		};
		$scope.emailContent();
		
		$scope.checkrowContent = function() {
        	if ($scope.emailContent.clickAll) {
        		angular.forEach($scope.emailContent.data_1, function(row) {
        			if(row.REVIEW_STATUS == 'W')
        				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.emailContent.data_1, function(row) {
        			if(row.REVIEW_STATUS == 'W')
        				row.SELECTED = false;
    			});
        	}
        };
        
        $scope.btnAddContent = function() {
			var view = 'content';
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS120/CUS121.html',
				className: 'CUS120',
				showClose: false,
	            controller: ['$scope', function($scope) {
	            	$scope.openView = view;
	            }]
			});
			dialog.closePromise.then(function(data) {				
				if(data.value === 'successful'){
					$scope.emailContent();						
				}
			});
		}
		
		$scope.reviewContent = function (status) {
			// get select
			var ans = $scope.emailContent.emailContentList.filter(function(obj) {
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		return;
        	}
			$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("CUS120", "ReviewMessage", "com.systex.jbranch.app.server.fps.cus120.CUS120ContentInputVO", {'review_list': ans,'status': status},
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
		                	};
		                	$scope.emailContent();
				});
			});
		};
		
		$scope.actionContent = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CUS120", "delEmailContentData", "com.systex.jbranch.app.server.fps.cus120.CUS120ContentInputVO", {'seq': row.SEQ},
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg('ehl_01_common_003');
                                		$scope.emailContent();
                                	};
                				}
                		);
					});
				}
				row.cmbAction = "";
			}
		}
		// ----------------------------------------------------------------------------
		// ------------------------2017/4/18 file part---------------------------------
		$scope.emailAttachment = function() {
		   	 $scope.sendRecv("CUS120", "queryEmailAttachment", "com.systex.jbranch.app.server.fps.cus120.CUS120AttachmentInputVO", {},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
			                	return;
			                }
							$scope.attachContent.emailAttachmentList = tota[0].body.resultList;
							$scope.attachContent.outputVO_2 = tota[0].body;
							$scope.attachContent.conDis = false;
							angular.forEach($scope.attachContent.emailAttachmentList, function(row) {
								if(row.REVIEW_STATUS == 'W')
									$scope.attachContent.conDis = true;
								row.set = [];
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							return;
					}
			});
		};
		
		$scope.checkrowAttachment = function() {
        	if ($scope.attachContent.clickAll) {
        		angular.forEach($scope.attachContent.data_2, function(row) {
        			if(row.REVIEW_STATUS == 'W')
        				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.attachContent.data_2, function(row) {
        			if(row.REVIEW_STATUS == 'W')
        				row.SELECTED = false;
    			});
        	}
        };
		
        $scope.btnUploadAttachmnet = function() {
			var view = 'attachment';
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS120/CUS121.html',
				className: 'CUS120',
				showClose: false,
	            controller: ['$scope', function($scope) {
	            	$scope.openView = view;
	            }]
			});
			dialog.closePromise.then(function(data) {				
				if(data.value === 'successful'){
					$scope.emailAttachment();						
				}
			});
		};
		
		$scope.reviewAttachmnet = function (status) {
			// get select
			var ans = $scope.attachContent.emailAttachmentList.filter(function(obj) {
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		return;
        	}
			$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("CUS120", "ReviewData", "com.systex.jbranch.app.server.fps.cus120.CUS120AttachmentInputVO", {'review_list': ans,'status': status},
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
		                	};
		                	$scope.emailAttachment();
				});
			});
		};
		
		$scope.actionAttachmnet = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CUS120", "delEmailAttachmentData", "com.systex.jbranch.app.server.fps.cus120.CUS120AttachmentInputVO", {'docno': row.DOC_ID},
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg('ehl_01_common_003');
                                		$scope.emailAttachment();
                                	};
                				}
                		);
					});
				}
				row.cmbAction = "";
			}
		};
		// ----------------------------------------------------------------------------
		
		$scope.download = function(row) {
        	$scope.sendRecv("CUS130", "download", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'fileID': row.DOC_ID,'fileName': row.FILENAME},
					function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
			);
        };
		
        // old code CUS120 幫我把CUS110TEST那兩個BUTTON 隱藏
//		$scope.testCust = function () {
//	    	var custId = ['A123456789','A246813579']
//			var recipientType = 'CUST';
//			$scope.connector('set','custID', custId);
//			$scope.connector('set','recipientType', recipientType);
//			var dialog = ngDialog.open({
//				template: 'assets/txn/CUS110/CUS110.html',
//				className: 'CUS110',
//				showClose: false,
//                controller: ['$scope', function($scope) {
//                	$scope.row = custId ;
//                }]
//			});
//			dialog.closePromise.then(function (data) {
//				if(data.value === 'successful') {				
//					$scope.inquire();			
//				}
//			});
//		};
//		$scope.testAO = function () {		
//			$scope.inputData = {
//					custID: ['1000002373','10165468','1000005672'],
//					recipientType: 'AO',
//					subjectTxt: 'Subject',
//					centerTextarea: '<html> <body> <table border="1"> <tr> <td>這裡是第一行的第一個欄位</td> <td>這裡是第一行的第二個欄位</td> </tr> <tr> <td>這裡是第二行的第一個欄位</td> </tr> </table> </body> </html>'
//			};	
//			$scope.sendRecv("CUS110", "sendMail", "com.systex.jbranch.app.server.fps.cus110.CUS110InputVO",  $scope.inputData,
//					function(totas, isError) {
//				$scope.showMsg(totas[0].body.message);
//			});
//		};
		
});