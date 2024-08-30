'use strict';
eSoafApp.controller('CRM651_DETAILController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM651_DETAILController";
	
	// combobox
	getParameter.XML(["KYC.EDUCATION", "KYC.CAREER", "KYC.MARRAGE",'KYC.CHILD_NO'], function(totas) {
		if (totas) {
			$scope.EDUCATION = totas.data[totas.key.indexOf('KYC.EDUCATION')];
			$scope.CAREER = totas.data[totas.key.indexOf('KYC.CAREER')];
			$scope.MARRAGE = totas.data[totas.key.indexOf('KYC.MARRAGE')];
			$scope.CHILDNO = totas.data[totas.key.indexOf('KYC.CHILD_NO')];
			
		}
	});
	//
	$scope.crm651_esbData = $scope.connector('get','CRM651_esbData');
	
	$scope.cust_id =  $scope.custVO.CUST_ID;
	// date picker
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	//
	
	$scope.init = function() {
		$scope.sendRecv("CRM651", "fillKyc", "com.systex.jbranch.app.server.fps.crm651.CRM651InputVO", {'cust_id':$scope.cust_id}, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList !=null && tota[0].body.resultList.length > 0) {
					$scope.paramList = refreshList(tota[0].body.resultList);
					angular.forEach($scope.paramList, function(row){
						row.set = "";
						row.ANS_CONTENT = "";
						row.NOTE_CONTENT = "";
					});
					// 如果有資料
					$scope.sendRecv("CRM651", "getKyc", "com.systex.jbranch.app.server.fps.crm651.CRM651InputVO", {'cust_id':$scope.cust_id}, function(tota2, isError) {
						if (!isError) {
							angular.forEach(tota2[0].body.resultList, function(row){
								var index = $scope.paramList.map(function(e) { return e.QSTN_ID; }).indexOf(row.QSTN_ID);
								
								if (index > -1) {
									// 其他選項預設打勾
									if(row.ANS_CONTENT) {
										// 單選
										$scope.paramList[index].set = null;
										// 複選
										$scope.paramList[index].OTHERCHECK = true;
									}
									
									// 單選不會有值
									if(row.ANS_IDS) {
										$scope.paramList[index].set = row.ANS_IDS.split(",");
										if($scope.paramList[index].set.length == 1)
											$scope.paramList[index].set = row.ANS_IDS;
										// sub
										angular.forEach($scope.paramList[index].SUBITEM, function(row2){
											var idx = $scope.paramList[index].set.indexOf(row2.ANS_ID);
											if (idx > -1)
												row2.CHECK = true;
										});
									}
									
									// 其他選項
									if(row.QSTN_FORMAT == "D" && row.ANS_CONTENT) {
										$scope.paramList[index].ANS_CONTENT = parseFloat(row.ANS_CONTENT);
										$scope.paramList[index].OTHER = parseFloat(row.ANS_CONTENT);
									}
									else {
										$scope.paramList[index].ANS_CONTENT = row.ANS_CONTENT;
										$scope.paramList[index].OTHER = row.ANS_CONTENT;
									}
									
									// 備註說明
									$scope.paramList[index].NOTE_CONTENT = row.NOTE_CONTENT;
								}
							});
							return;
						}
					});
				}
				return;
			}
		});
		
    	if ($scope.cust_id.length >= 10) {	// 自然人
			$scope.inputVO.QUESTION_TYPE = '02';
		} else {							// 法人
			$scope.inputVO.QUESTION_TYPE = '03';
		}
    	
    	console.log('CRM651.getKycQ2Data');
		$scope.sendRecv("CRM651", "getKycQ2Data", "com.systex.jbranch.app.server.fps.crm651.CRM651InputVO" , $scope.inputVO , function(tota,isError){
			if (!isError) {
				$scope.quest_list = tota[0].body.questionnaireList;
				console.log(JSON.stringify($scope.quest_list));
			}
		});
	};
	
	$scope.init();
	
	function refreshList(list) {
		var ans = [];
		for(var i = 0; i < list.length; i++) {
			generateList(ans,list[i]);
		}
		console.log('refreshlist='+JSON.stringify(ans));
		return ans;
	}
	
	function generateList(ansRow,row) {
		var obj = {};
		var exist = false;
		for(var i = 0; i < ansRow.length; i++) {
			if(row["QSTN_ID"] == ansRow[i]["QSTN_ID"]) {
				exist = true;
				break;
			}
		}
		
		if (!exist) {
			obj["QSTN_ID"] = row["QSTN_ID"];
			obj["QSTN_TYPE"] = row["QSTN_TYPE"];
			obj["QSTN_CONTENT"] = row["QSTN_CONTENT"];
			obj["WORD_SURGERY"] = row["WORD_SURGERY"];
			obj["QSTN_FORMAT"] = row["QSTN_FORMAT"];
			obj["OTH_OPT_YN"] = row["OTH_OPT_YN"];
			obj["EXT_MEMO_YN"] = row["EXT_MEMO_YN"];
			obj["SUBITEM"] = [];
			var obj2 = {};
			obj2["ANS_ID"] = row["ANS_ID"];
			obj2["ANS_CONTENT"] = row["ANS_CONTENT"];
			obj["SUBITEM"].push(obj2);
			ansRow.push(obj);
			return;
		}
		var obj2 = {};
		obj2["ANS_ID"] = row["ANS_ID"];
		obj2["ANS_CONTENT"] = row["ANS_CONTENT"];
		var old = ansRow.slice(-1).pop();
		old["SUBITEM"].push(obj2);
	}
	
	// for 單選
    $scope.setSingle = function(row) {
    	row.ANS_CONTENT = "";
    	if(row.set == null)
    		row.ANS_CONTENT = row.OTHER;
    };
    
    // for 複選
    $scope.setToggle = function(row) {
    	row.set = [];
    	row.ANS_CONTENT = "";
    	angular.forEach(row.SUBITEM, function(sub){
			if(sub.CHECK)
				row.set.push(sub.ANS_ID);
		});
    	if (row.OTHERCHECK && row.OTHER)
			row.ANS_CONTENT = row.OTHER;
    };
  
	
	$scope.save = function() {
		$scope.sendRecv("CRM651", "kycConfirm", "com.systex.jbranch.app.server.fps.crm651.CRM651InputVO", {'cust_id':$scope.cust_id,'data':$scope.paramList}, function(totas, isError) {
        	if (isError) {
        		$scope.showErrorMsg(totas[0].body.msgData);
        	}
        	if (totas.length > 0) {
        		$scope.showMsg('ehl_01_common_004');
        	};
		});
    };
    
    //	還原還原修改過的資料
    $scope.cancel = function() {
    	/**MARK :　2016/12/06 modify 還原還原修改過的資料 by Stella */
    	$scope.init();
    };
        
});
