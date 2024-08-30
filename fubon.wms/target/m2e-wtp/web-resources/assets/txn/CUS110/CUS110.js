'use strict';
eSoafApp.controller('CUS110Controller', function($scope, $rootScope,$controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CUS110Controller";
	
	$scope.inputVO = {};
	if($scope.isConfirm) {
		// combobox
		$scope.inputVO.confirmNAME = $scope.confirmNAME;
		$scope.SUBJECT = [];
		$scope.SUBJECT.push({LABEL: '來自台北富邦銀行  ' + $scope.inputVO.confirmNAME + '  的訊息', DATA: '1'});
		$scope.SUBJECT.push({LABEL: '手動輸入內容', DATA: '2'});
		// get ori
		$scope.sendRecv("CUS110", "getConfirmData", "com.systex.jbranch.app.server.fps.cus110.CUS110InputVO", {'seq': $scope.confirmSEQ},
				function(tota, isError) {
					if (!isError) {
						// exist data
						$scope.dataList = tota[0].body.resultList[0].queryCustData;
						$scope.recipient = $scope.dataList.map(function(e) { return e.CUST_DATA; }).toString();
						$scope.mail = $scope.dataList.map(function(e) { return e.EMAIL; }).toString();
						
						$scope.inputVO.email = $scope.mail;
						$scope.inputVO.subject = tota[0].body.resultList[0].SUBJECT;
						$scope.inputVO.subjectTxt = tota[0].body.resultList[0].SUBJECT_CUS;
						$scope.inputVO.contentList = tota[0].body.resultList[0].CONTENT;
						$scope.inputVO.centerTextarea = tota[0].body.resultList[0].CONTENT_CUS;
						$scope.inputVO.annexID = [];
						// 附件
						if(tota[0].body.resultList[0].ATTACHMENT) {
							$scope.attachList = tota[0].body.resultList[0].getUploadFile;
							angular.forEach($scope.attachList, function(row) {
								$scope.inputVO.annexID.push({'ID':row.DOC_ID});
							});
						}
						return;
					}
		});
	} else {
		$scope.inputVO.custID = $scope.custID;
		$scope.inputVO.recipientType = $scope.recipientType;
		$scope.inputVO.loginUser = projInfoService.getUserName();
		$scope.inputVO.annexID = [];
		
		// combobox
		$scope.SUBJECT = [];
		$scope.SUBJECT.push({LABEL: '來自台北富邦銀行  ' + $scope.inputVO.loginUser + '  的訊息', DATA: '1'});
		$scope.SUBJECT.push({LABEL: '手動輸入內容', DATA: '2'});
		
		$scope.sendRecv("CUS110", "queryCustData", "com.systex.jbranch.app.server.fps.cus110.CUS110InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.dataList = tota[0].body.resultList;
						$scope.recipient = $scope.dataList.map(function(e) { return e.CUST_DATA; }).toString();
						$scope.mail = $scope.dataList.map(function(e) { return e.EMAIL; }).toString();
						$scope.inputVO.email = $scope.mail;
						return;
					}
		});
	}
	
	$scope.sendRecv("CUS110", "queryContentList", "com.systex.jbranch.app.server.fps.cus110.CUS110InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.CONTENTLIST = [];
					angular.forEach(tota[0].body.resultList, function(row) {
	                	$scope.CONTENTLIST.push({LABEL: row.CONTENT, DATA: row.SEQ});
	            	});
					$scope.CONTENTLIST.push({LABEL: '手動輸入內容', DATA: '-1'});
					return;
				}
	});
	//
	
	$scope.checkSubject = function() {
		if ($scope.inputVO.subject != '2')
			$scope.inputVO.subjectTxt = "";
	};
	
	$scope.checkContent = function() {
		if ($scope.inputVO.contentList != '-1')
			$scope.inputVO.centerTextarea = "";
	};
	
	$scope.clickAttachFiles = function() {
		$scope.inputVO.annexID = [];
		var dialog = ngDialog.open({
			template: 'assets/txn/CUS110/CUS111.html',
			className: 'CUS111',
			showClose: false,
            controller: ['$scope', function($scope) {
            	
            }]
		});
		dialog.closePromise.then(function(data) {
			if(data.value != "cancel") {
				$scope.attachList = data.value;
				angular.forEach($scope.attachList, function(row) {
					$scope.inputVO.annexID.push({'ID':row.DOC_ID});
				});
			}
		});
	};
	
	$scope.deleteFiles = function(index) {
		$scope.attachList.splice(index,1);
		$scope.inputVO.annexID.splice(index,1);
	};
	
	$scope.btnConfirm = function(status) {
		$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可')}, {size: 'sm'}).then(function() {
			$scope.inputVO.seq = $scope.confirmSEQ;
			$scope.inputVO.status = status;
			$scope.sendRecv("CUS110", "ConfirmMessage", "com.systex.jbranch.app.server.fps.cus110.CUS110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
							if(tota[0].body.message)
								$scope.showMsg(tota[0].body.message);
							$scope.closeThisDialog('successful');
						}
			});
		});
	};
	
	//寄出郵件SendMail
	$scope.btnSendMail = function() {
		if($scope.parameterTypeEditForm.$invalid) {
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
    		return;
    	}
		$scope.sendRecv("CUS110", "sendMail", "com.systex.jbranch.app.server.fps.cus110.CUS110InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.message)
							$scope.showMsg(tota[0].body.message);
						else
							$scope.showSuccessMsg('ehl_01_common_004');
						$scope.closeThisDialog('successful');
					}
		});
	};
	
	$scope.download = function(row) {
    	$scope.sendRecv("CUS130", "download", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'fileID': row.DOC_ID,'fileName': row.FILENAME},
				function(totas, isError) {
	        		if (!isError) {
						return;
					}
				}
		);
    };
	
	
});