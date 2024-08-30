/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS431_CHILDRENController',
function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter) {
	$controller('BaseController', {
		$scope: $scope
	});
	$scope.controllerName = "INS431_CHILDRENController";

	$scope.init = function() {
		$scope.tempList = [];
		$scope.temp = {
			childName: '',
			childAge: '',
			eduEnd: '',
			eduCost: ''
		}
	}
	$scope.init();

	$scope.model = {};

	$scope.bgn_sDateOptions = {maxDate : new Date()};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.inquire = function() {
		$scope.inputVO.cust_id = $scope.inputVO.custID;
		console.log($scope.inputVO.custID);
		$scope.sendRecv('INS400', 'inquireChild', 'com.systex.jbranch.app.server.fps.ins400.INS400InputVO', $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.tempList = tota[0].body.outputList.map(function(row){
						row.CHILD_BIRTH = new Date(row.CHILD_BIRTH);
						//判斷這筆資料是否從DB，true從DB
						row.status = true;
						row.action = 'update';
						return row;
					});
					
					$scope.countEduAmt($scope.tempList);
				}
			});
		};
		

	$scope.add = function() {
		console.log($scope.temp.childBirth);
		var tempYN = $scope.temp.childName + $scope.temp.eduEnd + $scope.temp.eduCost;
		var nowDate = new Date();
		var nowDate1 = nowDate.getFullYear() +'/'+ (nowDate.getMonth()+1) + '/' + nowDate.getDate();
		$scope.temp.childAge = calAge($scope.temp.childBirth,new Date());
		if (tempYN.length > 0) {
			console.log(calAge($scope.temp.childBirth,new Date()));
			console.log(parseInt($scope.temp.eduEnd));
			if ($scope.temp.childAge >= parseInt($scope.temp.eduEnd)) {
				$scope.showErrorMsg('ehl_01_INS431_001');	//子女年齡不可大於學齡結束年紀
				return;
			}
			
			var exist = false;
			angular.forEach($scope.tempList, function(row){
				if(row.CHILD_NAME === $scope.temp.childName) {
					row['CHILD_NAME'] = $scope.temp.childName;
					row['CHILD_BIRTH'] = $scope.temp.childBirth;
					row['CHILD_AGE'] = calAge($scope.temp.childBirth,new Date());
					row['EDU_END'] = parseInt($scope.temp.eduEnd);
					row['EDU_COST'] = parseInt($scope.temp.eduCost); //$filter('number')(parseInt($scope.temp.eduCost)) || 0
					row['LASTUPDATE'] = nowDate1;
					row['action'] = 'update';
					exist = true;
				}
			});	

			if(!exist){
				$scope.tempList.push({
					CHILD_NAME: $scope.temp.childName,
					CHILD_BIRTH: $scope.temp.childBirth,
					CHILD_AGE: calAge($scope.temp.childBirth,new Date()),
					EDU_END: parseInt($scope.temp.eduEnd),
					EDU_COST: parseInt($scope.temp.eduCost), //$filter('number')(parseInt($scope.temp.eduCost)) || 0
					LASTUPDATE: nowDate1,
					action : 'create'
				});
			}
			
//			console.log(nowDate1);
//			console.log($scope.temp.childBirth);
//			console.log(nowDate1 - $scope.temp.childBirth);
	
			$scope.temp = {
				childName: '',
				childAge: '',
				eduEnd: '',
				eduCost: ''
			}
		}
		$scope.countEduAmt($scope.tempList);
		if ($scope.formPage == 'INS130') {
			$scope.inputVO.eduAmt = parseInt($scope.eduAmtTotal)/10000;
			$scope.sendRecv("INS130", "saveDataFromINS431", "com.systex.jbranch.app.server.fps.ins130.INS130InputVO", {custId: $scope.inputVO.custID, eduAmt: parseInt($scope.eduAmtTotal)/10000},
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);								
								return;
							}
							angular.forEach($scope.tempList, function(row){
								row.status = true;
								row.action = 'update';								
							});
						}							
			});
			
			$scope.inputVO.cust_id = $scope.inputVO.custID;
			$scope.inputVO.list = $scope.tempList;
			$scope.sendRecv('INS400', 'createChild', 'com.systex.jbranch.app.server.fps.ins400.INS400InputVO', $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
						}				
			});
		}
	}
	
	$scope.del = function(row, index) {
		if(row.status){
			var inputVO ={
				cust_id : $scope.inputVO.custID,
				child_Name : row.CHILD_NAME
			};
			$scope.sendRecv('INS400', 'deleteChild', 'com.systex.jbranch.app.server.fps.ins400.INS400InputVO', inputVO,
				function(tota, isError) {
					if (!isError) {
							//判斷這筆資料是否從DB，true從DB
//							row.status = true;
						$scope.showErrorMsg('ehl_01_common_003');	//刪除成功
						$scope.tempList.splice(index, 1);
						$scope.countEduAmt($scope.tempList);//因同步關係個別呼叫
						if ($scope.formPage == 'INS130') {//因同步關係個別呼叫
							$scope.inputVO.eduAmt = parseInt($scope.eduAmtTotal)/10000;
							$scope.sendRecv("INS130", "saveDataFromINS431", "com.systex.jbranch.app.server.fps.ins130.INS130InputVO", {custId: $scope.inputVO.custID, eduAmt: parseInt($scope.eduAmtTotal)/10000},
									function(tota, isError) {
										if (!isError) {
											if (tota[0].body.errorMsg != null) {
												$scope.showErrorMsg(tota[0].body.errorMsg);								
												return;
											}
										}							
							});							
						}
						return row;
					}
				});
		}else{
			$scope.tempList.splice(index, 1);
			$scope.countEduAmt($scope.tempList);//因同步關係個別呼叫
			if ($scope.formPage == 'INS130') {//因同步關係個別呼叫
				$scope.inputVO.eduAmt = parseInt($scope.eduAmtTotal)/10000;
				$scope.sendRecv("INS130", "saveDataFromINS431", "com.systex.jbranch.app.server.fps.ins130.INS130InputVO", {custId: $scope.inputVO.custID, eduAmt: parseInt($scope.eduAmtTotal)/10000},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.errorMsg != null) {
									$scope.showErrorMsg(tota[0].body.errorMsg);								
									return;
								}
							}							
				});							
			}
		}	
	}
	
	function calAge(birthday, currentDate) { 
		// birthday is a date
		currentDate = currentDate || Date.now;
		var age = currentDate.getFullYear() - birthday.getFullYear();
	    var m = currentDate.getMonth() - birthday.getMonth();
	    if (m < 0 || (m === 0 && currentDate.getDate() < birthday.getDate())) {
	        age--;
	    }
	    return age;
	}
	
	$scope.backToINS430 = function(row) {
		$scope.inputVO.cust_id = $scope.inputVO.custID;
		$scope.inputVO.list = $scope.tempList;
		console.log($scope.inputVO);
		console.log($scope.tempList);
		$scope.sendRecv('INS400', 'createChild', 'com.systex.jbranch.app.server.fps.ins400.INS400InputVO', $scope.inputVO,
				function(tota, isError) {
			if (!isError) {
				//					$scope.paramList = tota[0].body.outputList;
				$scope.outputVO = tota[0].body;
				console.log(tota[0].body);
				$scope.total = 0
				for (var i = 0; i < $scope.tempList.length; i++) {
					$scope.total = $scope.total + parseInt($scope.moneyUnFormat($scope.tempList[i].EDU_COST));
				}
			}
			
			if($scope.formPage == 'INS130') {
				$scope.closeThisDialog(['confirm',$scope.eduAmtTotal]);
			} else {
				$scope.closeThisDialog($scope.total);
			}
		});
	};


	// 編輯(onblur)試算教育基金
	$scope.onBlurEduCost = function() {
		$scope.countEduAmt($scope.tempList);
		if ($scope.formPage == 'INS130') {
			$scope.inputVO.eduAmt = parseInt($scope.eduAmtTotal)/10000;
			$scope.sendRecv("INS130", "saveDataFromINS431", "com.systex.jbranch.app.server.fps.ins130.INS130InputVO", {custId: $scope.inputVO.custID, eduAmt: parseInt($scope.eduAmtTotal)/10000},
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);								
								return;
							}
						}							
			});
			
			$scope.inputVO.cust_id = $scope.inputVO.custID;
			$scope.inputVO.list = $scope.tempList;
			$scope.sendRecv('INS400', 'createChild', 'com.systex.jbranch.app.server.fps.ins400.INS400InputVO', $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
						}				
			});
		}
	}
	
	// 即時試算教育基金
	$scope.countEduAmt = function(tempList) {
		var eduAmtTotal = 0;
		angular.forEach(tempList, function(row){
			eduAmtTotal += Number(row.EDU_COST);	
		});	
		$scope.eduAmtTotalShow = eduAmtTotal;
		$scope.eduAmtTotal = eduAmtTotal;
	}
	
	//點擊子女姓名 – 修改
	$scope.updateData = function(row) {
		$scope.temp.childName = row.CHILD_NAME;
		$scope.temp.childBirth = row.CHILD_BIRTH;
		$scope.temp.eduEnd = row.EDU_END;
		$scope.temp.eduCost = row.EDU_COST;
	}
	
	$scope.inquire();
});