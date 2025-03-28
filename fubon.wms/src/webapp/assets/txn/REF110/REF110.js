'use strict';
eSoafApp.controller('REF110Controller', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "REF110Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	var pageID 	= $scope.connector('get','REF110PAGE'); // 從哪前來
	$scope.data = $scope.connector('get','ACTION_TYPE');
	$scope.seq 	= $scope.connector('get','SEQ');
	
	$scope.connector('set','REF110PAGE', null);
	$scope.connector('set','ACTION_TYPE', null);
	$scope.connector('set','SEQ', null);
	
	$scope.disableCustName = true;
	
	// filter
	getParameter.XML(["CAM.REF_SALES_ROLE", "CAM.REF_PROD", "COMMON.YES_NO"], function(totas) {
		if (totas) {
			$scope.mappingSet['CAM.REF_SALES_ROLE'] = totas.data[totas.key.indexOf('CAM.REF_SALES_ROLE')];
			$scope.mappingSet['CAM.REF_PROD'] = totas.data[totas.key.indexOf('CAM.REF_PROD')];
			$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
		}
	});
	// ===
	
	$scope.custQuery = function () {
		if ($scope.inputVO.custID != undefined || $scope.inputVO.custID != '' || $scope.inputVO.custID != null) {
			$scope.sendRecv("REF110", "queryCustProfile", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					// 高端_P3
					if (tota[0].body.uhrmYN == 'Y') { //此為高端客戶，請洽詢個人高端RM
						$scope.showErrorMsg("此為高端客戶，請洽詢個人高端RM");
						$scope.inputVO.custID = '';
						
						return;
					} 
					// ===

					$scope.inputVO.salerecCounts = tota[0].body.salerecCounts;
					$scope.inputVO.recYN    	 = tota[0].body.recYN;
					$scope.inputVO.custAoType	 = tota[0].body.custAoType;
					$scope.inputVO.custBraNbr	 = tota[0].body.custBraNbr;
					$scope.inputVO.viceCust	 	 = tota[0].body.viceCust;
					$scope.inputVO.maintainCust	 = tota[0].body.maintainCust;
					$scope.inputVO.massCust		 = tota[0].body.massCust;
				}
			});
		}
	}
    
	$scope.init = function () {
		$scope.tempRefEmpID = "";
		
		$scope.tempCustID = "";
		$scope.tempCustName = "";
		$scope.custAoEmpID = "";
		
		$scope.modifyCustIDFlag = false;
		$scope.checkCustStatus = false;
		$scope.checkRefEmpStatus = false;
		
		$scope.inputVO = {
				seq: $scope.seq,
				dataDate: '', 
				regionID: '',
				branchAreaID: '',
				branchID: '', 
				refProd: '', 
				empID: '', 
				empName: '',
				empRoleName: '',
				empJobTitleName: '',
				custID: '', 
				custName: '', 
				custAoCode: '',
				recYN: '',
				refEmpID: '', 
				refAoCode: '',
				refEmpName: '', 
				refEmpRoleName: '', 
				refEmpJobTitleName: '', 
				refEmpRegionID: '',
				refEmpBranchAreaID: '',
				refEmpBranchID: '',
				refEmpRoleNameTemp: '', 
				contRslt: '',
				nonGrantReason: '', 
				comments: ''
    	};
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
		
		if ($scope.data == "update") {
			$scope.sendRecv("REF110", "getDtl", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.inputVO.dataDate = tota[0].body.dataDate;
					
					//轉介人員資訊
					$scope.inputVO.empID    = tota[0].body.empID;
					$scope.inputVO.branchID = tota[0].body.branchID;
					
					$scope.querySaleProfile();
					
					$scope.inputVO.salerecRefEmpRoleName = tota[0].body.salerecRefEmpRoleName;
					
					//受轉介人資訊
					$scope.inputVO.refEmpID = tota[0].body.refEmpID;
					$scope.inputVO.refEmpName = tota[0].body.refEmpName;
					$scope.inputVO.refEmpRoleNameTemp = tota[0].body.salerecRefEmpRoleName;
					$scope.inputVO.refEmpRoleName = tota[0].body.salerecRefEmpRoleName;
					$scope.inputVO.contRslt = tota[0].body.contRslt;
					$scope.inputVO.nonGrantReason = tota[0].body.nonGrantReason;
					$scope.inputVO.comments = tota[0].body.comments;
					
					$scope.mappingSet['REF_RPOD'] = [];
					$scope.mappingSet['CAM.REF_USER_ROLE'] = [];
					if (tota[0].body.salerecRefEmpRoleName == '1') {
						$scope.inputVO.custAoCode = tota[0].body.aoCode;
						
						$scope.mappingSet['REF_RPOD'].push({LABEL: '投保商品', DATA: '5'}); 
						
						$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '個金RM', DATA: '1'});
					} else if (tota[0].body.salerecRefEmpRoleName == '2') {
						$scope.mappingSet['REF_RPOD'].push({LABEL: '房貸', DATA: '1'}); 
						$scope.mappingSet['REF_RPOD'].push({LABEL: '房貸壽', DATA: '6'}); 
						$scope.mappingSet['REF_RPOD'].push({LABEL: '信貸', DATA: '2'}); 
						$scope.mappingSet['REF_RPOD'].push({LABEL: '留學貸款', DATA: '3'}); 
						
						$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '消金PS', DATA: '2'});
					} else {
						$scope.mappingSet['REF_RPOD'].push({LABEL: '企業貸款', DATA: '4'});
						
						$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '新金PS', DATA: '3'});
						$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '商金RM', DATA: '4'});
						$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '新興PS', DATA: '7'});
					}
					
					if ((tota[0].body.custID).substr(0, 1) == "0") {
						$scope.modifyCustIDFlag = true;
						$scope.disableCustName = false;
					}
					
					//客戶資訊
					$scope.inputVO.custID   = tota[0].body.custID;
					$scope.tempCustID 		= tota[0].body.custID;
					$scope.inputVO.custName = tota[0].body.custName;
					$scope.tempCustName 	= tota[0].body.custName;
					
					$scope.inputVO.refProd = tota[0].body.refProd;
					
					$scope.custQuery();
					$scope.queryUserProfile();
				}
			});
		} else {
			$scope.modifyCustIDFlag = true;
			
			$scope.sendRecv("REF110", "initial", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", {}, function(tota, isError) {
				if (!isError) {
					$scope.inputVO.seq 		= tota[0].body.seq;
					$scope.inputVO.dataDate = tota[0].body.dataDate;
					$scope.inputVO.empID    = sysInfoService.getUserID();
					
					$scope.querySaleProfile();
				}
			});
		}
		
        $scope.loginID = sysInfoService.getUserID();
	};
	$scope.init();
	
	$scope.querySaleProfile = function() {
		$scope.sendRecv("REF110", "querySaleProfile", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError && tota[0].body.empName != null ) {
				$scope.inputVO.empName 	  	   = tota[0].body.empName;
				$scope.inputVO.empJobTitleName = tota[0].body.empJobTitleName;
				$scope.inputVO.empRoleName 	   = tota[0].body.empRoleName;
				$scope.inputVO.regionID 	   = tota[0].body.regionID;
				$scope.inputVO.regionName 	   = tota[0].body.regionName;
				$scope.inputVO.branchAreaID    = tota[0].body.branchAreaID;
				$scope.inputVO.branchAreaName  = tota[0].body.branchAreaName;
				$scope.inputVO.branchID 	   = tota[0].body.branchID;
				$scope.inputVO.branchName 	   = tota[0].body.branchName;
				
	            if ($scope.inputVO.empRoleName == "7") { //轉介人為其他時，清空區域中心及分行名稱
	            	$scope.inputVO.regionID 	  = "";
					$scope.inputVO.branchAreaID   = "";
					$scope.inputVO.branchID 	  = "";
					$scope.inputVO.regionName 	  = "";
					$scope.inputVO.branchAreaName = "";
					$scope.inputVO.branchName 	  = "";
	            }
				
	            if (!($scope.inputVO.refEmpRoleName == null || $scope.inputVO.refEmpRoleName == undefined || $scope.inputVO.refEmpRoleName == '')) {
	            	$scope.queryUserProfile();
	            }
	            
				// #1070: WMS-CR-20220420-01_主副Code專案-轉介功能調整 modify by ocean : 轉介人轉介投保時，客戶若有所屬理專，系統帶出理專員編；若非與轉介人同一分行，僅可轉介給同一分行理專。
				if (($scope.inputVO.branchID).length == 3 && ($scope.inputVO.branchID >= 200 && $scope.inputVO.branchID <= 900)) {
					if (!($scope.inputVO.custBraNbr == null || $scope.inputVO.custBraNbr == undefined || $scope.inputVO.custBraNbr == '') && 
						($scope.inputVO.branchID != $scope.inputVO.custBraNbr)) {
						if (!($scope.inputVO.refEmpBranchID == null || $scope.inputVO.refEmpBranchID == undefined || $scope.inputVO.refEmpBranchID == '') && 
							$scope.inputVO.refEmpBranchID == $scope.inputVO.branchID) {
							// 可轉介投保商品給同一分行RM
						} else {
//							$scope.showErrorMsg("該客戶非該分行客戶，投保商品僅可轉介給同一分行RM");
							$scope.clearRefEmp();
							
							return;
						}
					}
				}
				
				return;
			} else {
				$scope.inputVO.empID = "", 
				$scope.inputVO.empName = "",
				$scope.inputVO.empRoleName = "",
				$scope.inputVO.empJobTitleName = "",
				$scope.showErrorMsg("ehl_01_common_009");
				
				return;
			}
		});
	}
	
	$scope.errorClear = function () {
		$scope.custAoEmpID = "";
		
		$scope.disableCustName = true;
		$scope.inputVO.custID = "";
		$scope.inputVO.custName = "";
		
		$scope.checkRefEmpStatus = false;
		$scope.inputVO.refProd = "";
		$scope.inputVO.refEmpID = "";
		$scope.inputVO.refEmpName = "";
		$scope.inputVO.refEmpRoleName = "";
	}
	
	$scope.checkRefEmp = function (type, maintainCust, massCust, refEmpID, refJRMEmpID) {
		switch ($scope.inputVO.empRoleName) {
			case "1":	// 1:櫃員
			case "3":	// 3:消金PS
			case "4":	// 4:作業主管 
			case "5":	// 5:個金主管
			case "6":	// 6:業務主管
				if ($scope.tempRefEmpID == null || $scope.tempRefEmpID == "" || $scope.tempRefEmpID == undefined) {
					// 1.轉介時，自動帶入轉介人歸屬行的JRM(隨機取1)
					console.log("refJRMEmpID:" + refJRMEmpID);
					if ((maintainCust == 'Y' || massCust == 'Y') && (refJRMEmpID != null || refJRMEmpID != "" || refJRMEmpID != undefined)) {
						$scope.inputVO.refEmpID = refJRMEmpID;
					} 
					// 2.若查無JRM時，自動帶入客戶個金RM
					else {
						$scope.inputVO.refEmpID = refEmpID;
					}					
				} 
				
				break;
			case "7":	// 7:其他
			case "8":	// 8:個金AO
				// 轉介時，自動帶入客戶個金RM
				if ($scope.tempRefEmpID == null || $scope.tempRefEmpID == "" || $scope.tempRefEmpID == undefined) {
					$scope.inputVO.refEmpID = refEmpID;
				} 
				
				break;
			case "2":	// 2:理專
			case "9":	// 9:JRM 
			case "10":	// 10:新興PS
				// 轉介時，不自動帶出客戶個金RM
				$scope.inputVO.refEmpID = $scope.tempRefEmpID;
				
				break;
			default:
				
				break;
		}
		
		if (($scope.inputVO.refEmpID != null || $scope.inputVO.refEmpID != '' || $scope.inputVO.refEmpID == undefined) && $scope.inputVO.refEmpID != $scope.tempRefEmpID) {
			switch (type) {
				case "REF_EMP":					
					$scope.queryCustProfile();

					break;
				case "CUST":
					$scope.queryUserProfile();
					
					break;
			}
			$scope.tempRefEmpID = $scope.inputVO.refEmpID; 
		}
	}
	
	$scope.clearRefEmp = function () {
		$scope.tempRefEmpID = "";
		$scope.checkRefEmpStatus = false; // 受轉介人資訊輸入狀態
		
		$scope.inputVO.refProd = "";
		$scope.inputVO.refEmpID = "";
		$scope.inputVO.refEmpName = "";
		$scope.inputVO.refEmpRoleName = "";
		
		$scope.mappingSet['REF_RPOD'] = [];
	}
	

	$scope.setRefEmpDtl = function (errorMsg) {
		$scope.showErrorMsg(errorMsg);
		if ($scope.data != 'update') {
			$scope.clearRefEmp();
		}
		$scope.mappingSet['CAM.REF_USER_ROLE'] = [];
		
		$scope.checkRefEmpStatus = false; // 受轉介人資訊輸入狀態
	}
	
	$scope.queryCustProfile = function() {
		
		var deferred = $q.defer();
		
		if ($scope.inputVO.custID == undefined || $scope.inputVO.custID == '' || $scope.inputVO.custID == null) {
			$scope.disableCustName = true;
			
			$scope.tempCustID = "";
			$scope.tempCustName = "";
			$scope.inputVO.custID = "";
			$scope.inputVO.custName = ""; 
			
			$scope.inputVO.custAoCode = "";
		} else {
			$scope.inputVO.custID = $scope.inputVO.custID.toUpperCase();
			
			$scope.sendRecv("REF110", "queryCustProfile", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", $scope.inputVO, function(tota, isError) {
					if (!isError) {
						// 高端_P3
						if (tota[0].body.uhrmYN == 'Y') { //此為高端客戶，請洽詢個人高端RM
							$scope.showErrorMsg("此為高端客戶，請洽詢個人高端RM");
							$scope.inputVO.custID = '';
							
							return;
						} 
						// ===
						
						//修改轉介 為投保商品 且 無回報記錄  或  新增轉介 且 客戶ID與上次不同
						if (($scope.data == 'update' && $scope.inputVO.refProd == 5 && ($scope.inputVO.contRslt == null || $scope.inputVO.contRslt == undefined || $scope.inputVO.contRslt == '01')) || 
							($scope.data != 'update' && $scope.tempCustID != $scope.inputVO.custID)) {
							
							$scope.tempCustID = $scope.inputVO.custID;
							$scope.inputVO.custAoCode = "";
							$scope.disableCustName = true;
							$scope.tempRefEmpID = "";
							$scope.tempCustName = "";
							
							if ($scope.data != 'update') {
								$scope.clearRefEmp();
							}

							deferred.resolve("");
						}
						
						//前一日無CODE(當日掛CODE) 或當是無CODE才可以轉介
						if (tota[0].body.yesterdayNoAO == "Y" || tota[0].body.aoCode == null) { 
							$scope.inputVO.salerecCounts = tota[0].body.salerecCounts;

							if (tota[0].body.custID == null || (tota[0].body.custID == null && $scope.data == 'update' && ($scope.inputVO.custID).substr(0, 1) != "0")) { // 若該客戶非本行客戶，系統提示相關訊息，並開放輸入客戶姓名
								if ($scope.disableCustName) {
									$scope.disableCustName = false;
									$scope.showMsg("ehl_01_ref110_003");
									
									$scope.inputVO.custID = "0" + $scope.padLeft(($scope.inputVO.seq).substr(4, 9), 9);
									$scope.inputVO.custName = "";
									if ($scope.data != 'update') {
										$scope.clearRefEmp();
									}
									
									deferred.resolve("");									
									return deferred.promise;
								}
							} else {
								$scope.disableCustName = true;

								$scope.inputVO.custAoCode = tota[0].body.custAoCode;
								if(($scope.inputVO.refEmpID == tota[0].body.refEmpID) && ($scope.data == 'update')){
									$scope.inputVO.refAoCode = $scope.inputVO.custAoCode;
								}
								if (($scope.disableCustName) && 
									($scope.inputVO.refProd != null && $scope.inputVO.refProd == '5')) { 
									
									if ($scope.inputVO.empRoleName == '7') {
										// 轉介人身份為其它時，不判斷是否於同分行
									} 
									// ADD MARK BY OCEAN #1934:因應作業行調整轉介投保限制 (君榮)
//									else if ($scope.inputVO.refEmpBranchID != $scope.inputVO.branchID){
//										// #1070:WMS-CR-20220630-01_主副Code專案-轉介功能調整: 2022 add 投保商品僅可轉介至同分行理專
//										// 2022 mark 轉介商品為投保類時，受轉介人只能限制登打為客戶的AO專員，若客戶無AO COD則限制只能轉介給轉介人同分行人員 && ($scope.inputVO.custAoCode != null && ($scope.inputVO.custAoCode != $scope.inputVO.refAoCode))
//										// 客戶有AO_CODE時投保商品只可轉介給AO專員
//										$scope.showErrorMsg("ehl_01_ref110_004");
//										$scope.tempRefEmpID = "";
//										if ($scope.data != 'update') {
//											$scope.clearRefEmp();
//										}
//										
//										$scope.checkCustStatus = false;
//									}
									
									deferred.resolve("");									
									return deferred.promise;
								} else {
									$scope.inputVO.recYN      	= tota[0].body.recYN;
									$scope.inputVO.custName   	= tota[0].body.custName;
									$scope.inputVO.custAoType 	= tota[0].body.custAoType;
									$scope.inputVO.custBraNbr	= tota[0].body.custBraNbr;
									$scope.inputVO.viceCust	 	= tota[0].body.viceCust;
									$scope.inputVO.maintainCust	= tota[0].body.maintainCust;
									$scope.inputVO.massCust		= tota[0].body.massCust;
									
									$scope.tempCustName = $scope.inputVO.custName;
									
									$scope.checkCustStatus = true; // 客戶資訊輸入狀態
									
									if (!$scope.checkRefEmpStatus) {
										if ($scope.data != 'update' || 
											($scope.data == 'update' && ($scope.inputVO.contRslt == null || $scope.inputVO.contRslt == undefined || $scope.inputVO.contRslt == '01'))) {
											// 新增轉介 或 修改轉介(仍無回報進度)時
											if ($scope.data != 'update' || ($scope.data == 'update' && $scope.inputVO.refProd == 5 && ($scope.inputVO.contRslt == null || $scope.inputVO.contRslt == undefined || $scope.inputVO.contRslt == '01'))) {
												// 新增轉介 或 修改轉介非受轉介人且商品為投保商品時且未結案時
												$scope.custAoEmpID = tota[0].body.refEmpID;
												
												$scope.checkRefEmp("CUST", $scope.inputVO.maintainCust, $scope.inputVO.massCust, tota[0].body.refEmpID, tota[0].body.refJRMEmpID);
											}
										}
									}
								}
							}
								
							$scope.tempCustID = $scope.inputVO.custID;
							$scope.tempCustName = $scope.inputVO.custName;
							
							deferred.resolve("success");									
							return deferred.promise;
						} else {
							$scope.showErrorMsg("ehl_01_ref110_005"); 										
							$scope.checkCustStatus = false; // 客戶資訊輸入狀態
						}
						
						deferred.resolve("");									
						return deferred.promise;
					}
				}
			);
		};
		
		return deferred.promise;
	};
	
	$scope.queryCustNameProfile = function() {
		if ($scope.inputVO.custName.length > 0) {
			$scope.checkCustStatus = true; // 客戶資訊輸入狀態
		}
	}
	
	$scope.mappingSet['REF_RPOD'] = [];
	$scope.queryUserProfile = function () {
		
		$scope.custQuery();
		
		var deferred = $q.defer();
		
		if ($scope.inputVO.refEmpID == undefined && $scope.data != 'update') {
			
			$scope.clearRefEmp();
		}
		if (($scope.inputVO.refEmpID != "" && $scope.inputVO.refEmpID != undefined)) {
			$scope.sendRecv("REF110", "queryUserProfile", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					if (null != tota[0].body.refEmpName) {
						$scope.inputVO.refEmpName 	      = tota[0].body.refEmpName;
						$scope.inputVO.refEmpJobTitleName = tota[0].body.refEmpJobTitleName;
						$scope.inputVO.refEmpRoleName 	  = tota[0].body.refEmpRoleName;
						$scope.inputVO.refAoCode 	  	  = tota[0].body.refAoCode;
						$scope.inputVO.refEmpRegionID	  = tota[0].body.refEmpRegeionID;
						$scope.inputVO.refEmpBranchAreaID = tota[0].body.refEmpBranchAreaID;
						$scope.inputVO.refEmpBranchID	  = tota[0].body.refEmpBranchID;
						
						$scope.mappingSet['REF_RPOD'] = [];
						if (null != $scope.inputVO.refEmpRoleName && $scope.inputVO.refEmpRoleName == "1") {
							if ($scope.inputVO.empRoleName == "2") { //個金RM不可轉介給個金RM
								$scope.setRefEmpDtl("ehl_01_ref110_007");
								
								deferred.resolve("");									
								return deferred.promise;
							} 

							// #1934:因應作業行調整轉介投保限制(君榮) add mark by ocean
//							if (($scope.inputVO.custAoCode == null || $scope.inputVO.custAoCode == undefined || $scope.inputVO.custAoCode == '')) { 
//								//受轉介人員非本行人員
//								if ($scope.inputVO.branchID != undefined && $scope.inputVO.branchID != null && $scope.inputVO.branchID != '' && 
//									($scope.inputVO.refEmpBranchID != $scope.inputVO.branchID && $scope.inputVO.empRoleName != '8')
//									) {
//									
//									$scope.setRefEmpDtl("ehl_01_ref110_009");
//									
//									deferred.resolve("");									
//									return deferred.promise;
//								}
//							}

							// #1070: WMS-CR-20220420-01_主副Code專案-轉介功能調整 modify by ocean : 主CODE客戶/主CODE客戶其關係戶，不可轉介投保商品。
							if ($scope.inputVO.custAoType == 'N') {
								$scope.showErrorMsg("主CODE客戶，不可轉介投保商品。");///主CODE客戶其關係戶
								$scope.clearRefEmp();
								
								deferred.resolve("");									
								return deferred.promise;
							}
							
							if ($scope.inputVO.empRoleName == "9") { // JRM->RM，僅可轉介維護CODE與MASS戶
								if ($scope.inputVO.maintainCust == 'Y' || $scope.inputVO.massCust == 'Y') {
								} else {
									$scope.showErrorMsg("JRM僅可轉介維護CODE/MASS戶。");
									$scope.clearRefEmp();
									
									deferred.resolve("");									
									return deferred.promise;
								}
							}
							
							// #1070: WMS-CR-20220420-01_主副Code專案-轉介功能調整 modify by ocean : 轉介人轉介投保時，客戶若有所屬理專，系統帶出理專員編；若非與轉介人同一分行，僅可轉介給同一分行理專。
//							if (($scope.inputVO.branchID).length == 3 && ($scope.inputVO.branchID >= 200 && $scope.inputVO.branchID <= 900)) {
//								if (!($scope.inputVO.custBraNbr == null || $scope.inputVO.custBraNbr == undefined || $scope.inputVO.custBraNbr == '') && 
//									($scope.inputVO.branchID != $scope.inputVO.custBraNbr)) {
//									if (!($scope.inputVO.refEmpBranchID == null || $scope.inputVO.refEmpBranchID == undefined || $scope.inputVO.refEmpBranchID == '') && 
//										$scope.inputVO.refEmpBranchID == $scope.inputVO.branchID) {
//										// 可轉介投保商品給同一分行RM
//									} else {
////										$scope.showErrorMsg("該客戶非該分行客戶，投保商品僅可轉介給同一分行RM");
//										$scope.clearRefEmp();
//										
//										deferred.resolve("");									
//										return deferred.promise;
//									}
//								}
//							}
							
							$scope.mappingSet['REF_RPOD'].push({LABEL: '投保商品', DATA: '5'}); 
							$scope.inputVO.refProd = "5";
							$scope.mappingSet['CAM.REF_USER_ROLE'] = [];
							$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '個金RM', DATA: '1'});
						} else if (null != $scope.inputVO.refEmpRoleName && $scope.inputVO.refEmpRoleName == "2") {
							if ($scope.inputVO.empRoleName == "3") { //消金PS不可轉介給消金PS
								$scope.setRefEmpDtl("ehl_01_ref110_008");
								
								deferred.resolve("");									
								return deferred.promise;
							}
							$scope.mappingSet['REF_RPOD'].push({LABEL: '房貸', DATA: '1'});
							$scope.mappingSet['REF_RPOD'].push({LABEL: '房貸壽', DATA: '6'}); 
							$scope.mappingSet['REF_RPOD'].push({LABEL: '信貸', DATA: '2'}); 
							$scope.mappingSet['REF_RPOD'].push({LABEL: '留學貸款', DATA: '3'});
							// 2018/3/1 受轉介人員編在修改時沒法改, 這裡不用清空修改的refProd
							if($scope.data != 'update')
								$scope.inputVO.refProd = "";
							
							$scope.mappingSet['CAM.REF_USER_ROLE'] = [];
							$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '消金PS', DATA: '2'});
						} else if (null != $scope.inputVO.refEmpRoleName && $scope.inputVO.refEmpRoleName == "0") {
							if ($scope.inputVO.branchID != undefined && 
								$scope.inputVO.branchID != null && 
								$scope.inputVO.branchID != '' && 
								$scope.inputVO.refEmpBranchID != $scope.inputVO.branchID) { //受轉介人員非本行人員
								$scope.setRefEmpDtl("ehl_01_ref110_009");
								
								deferred.resolve("");									
								return deferred.promise;
							}
							
							$scope.mappingSet['REF_RPOD'].push({LABEL: '企業貸款', DATA: '4'});
							$scope.inputVO.refProd = "4";
							
							$scope.mappingSet['CAM.REF_USER_ROLE'] = [];
							$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '新金PS', DATA: '3'});
							$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '商金RM', DATA: '4'});
						} else if (null != $scope.inputVO.refEmpRoleName && $scope.inputVO.refEmpRoleName == "5") {
							if ($scope.inputVO.empRoleName == "8") { //個金AO不可轉介給個金AO
								$scope.setRefEmpDtl("ehl_01_ref110_010");
								
								deferred.resolve("");									
								return deferred.promise;
							}
							
							$scope.mappingSet['REF_RPOD'].push({LABEL: '房貸', DATA: '1'});
							$scope.mappingSet['REF_RPOD'].push({LABEL: '房貸壽', DATA: '6'}); 
							$scope.mappingSet['REF_RPOD'].push({LABEL: '信貸', DATA: '2'}); 
							$scope.mappingSet['REF_RPOD'].push({LABEL: '留學貸款', DATA: '3'});
							// 2018/3/1 受轉介人員編在修改時沒法改, 這裡不用清空修改的refProd
							if($scope.data != 'update')
								$scope.inputVO.refProd = "";
							
							$scope.mappingSet['CAM.REF_USER_ROLE'] = [];
							$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '個金AO', DATA: '5'});
						} else if (null != $scope.inputVO.refEmpRoleName && $scope.inputVO.refEmpRoleName == "6") {
							if ($scope.inputVO.empRoleName == "9") { //JRM不可轉介給JRM
								$scope.setRefEmpDtl("ehl_01_ref110_010");
								
								deferred.resolve("");									
								return deferred.promise;
							}
							
							if ($scope.inputVO.custAoType == 'N') {
								$scope.showErrorMsg("主CODE客戶，不可轉介投保商品。");///主CODE客戶其關係戶
								$scope.clearRefEmp();
								
								deferred.resolve("");									
								return deferred.promise;
							}
							
							if ($scope.inputVO.viceCust == 'Y') {
								$scope.showErrorMsg("副CODE客戶僅可轉介給RM。");
								$scope.clearRefEmp();
								
								deferred.resolve("");									
								return deferred.promise;
							}
							
							$scope.mappingSet['REF_RPOD'].push({LABEL: '投保商品', DATA: '5'}); 
							$scope.inputVO.refProd = "5";
							$scope.mappingSet['CAM.REF_USER_ROLE'] = [];
							$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: 'JRM', DATA: '6'});
						} else if (null != $scope.inputVO.refEmpRoleName && $scope.inputVO.refEmpRoleName == "7") {
							if ($scope.inputVO.empRoleName == "10") { //新興PS不可轉介給新興PS
								$scope.setRefEmpDtl("受轉介人員不得為新興PS");
								
								deferred.resolve("");									
								return deferred.promise;
							}
							
							$scope.mappingSet['REF_RPOD'].push({LABEL: '企業貸款', DATA: '4'});
							$scope.inputVO.refProd = "4";
							
							$scope.mappingSet['CAM.REF_USER_ROLE'] = [];
							$scope.mappingSet['CAM.REF_USER_ROLE'].push({LABEL: '新興PS', DATA: '7'});
						}
						// 2018/3/1 old code clear
						else {
							$scope.inputVO.refProd = "";
						}
						
						if ($scope.inputVO.refEmpRoleNameTemp.length > 0) {
							$scope.inputVO.refEmpRoleName = $scope.inputVO.refEmpRoleNameTemp;
						}
						
						$scope.checkRefEmpStatus = true; // 受轉介人資訊輸入狀態

						if (!$scope.checkRefEmpStatus) {
							$scope.checkRefEmp("REF_EMP", $scope.inputVO.maintainCust, $scope.inputVO.massCust, $scope.inputVO.refEmpID, $scope.inputVO.refEmpID);
						}
						deferred.resolve("success");									
						return deferred.promise;
					} else if (null == tota[0].body.refEmpName && "UHRM" == tota[0].body.refEmpRoleName) {
						$scope.setRefEmpDtl("此為高端客戶，請洽詢個人高端RM");
					} else {
						$scope.setRefEmpDtl("ehl_01_ref110_006");
					}
					
					deferred.resolve("");	
				}
			});
		}
		
		return deferred.promise;
	};
	
	$scope.addData = function() {
		$scope.sendRecv("REF110", "queryCustProfile", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				// 高端_P3
				if (tota[0].body.uhrmYN == 'Y') { //此為高端客戶，請洽詢個人高端RM
					$scope.showErrorMsg("此為高端客戶，請洽詢個人高端RM");
					$scope.inputVO.custID = '';
					
					return;
				} 
				// =====
				
				$scope.inputVO.salerecCounts = tota[0].body.salerecCounts;
				$scope.inputVO.recYN    	 = tota[0].body.recYN;
				$scope.inputVO.custRefEmpID  = tota[0].body.refEmpID;
				$scope.inputVO.custAoType    = tota[0].body.custAoType;
				$scope.inputVO.custBraNbr    = tota[0].body.custBraNbr;
				$scope.inputVO.viceCust	 	 = tota[0].body.viceCust;
				$scope.inputVO.maintainCust	 = tota[0].body.maintainCust;
				$scope.inputVO.massCust		 = tota[0].body.massCust;
				
				if (!($scope.data == "update" && ($scope.loginID == $scope.inputVO.refEmpID))) {
					switch ($scope.inputVO.empRoleName) {
						case "9":
							break;
						default:
							if ($scope.inputVO.salerecCounts != null && $scope.inputVO.salerecCounts == 'Y') { //客戶30天內只可轉介同類商品一次(已移除同分行)
								$scope.showErrorMsg("同客戶、同商品，T+3個月底內只能轉介一次。");		
								
								return;
							} 
							break;
					}
					
					if (($scope.disableCustName) && 
						($scope.inputVO.refProd != null && $scope.inputVO.refProd == '5') && 
						$scope.inputVO.refEmpBranchID != $scope.inputVO.branchID) { 
						if ($scope.inputVO.empRoleName == '7') {
							// 轉介人身份為其它時，不判斷是否於同分行
						} 
						// ADD MARK BY OCEAN #1934:因應作業行調整轉介投保限制 (君榮)
//						else if ($scope.inputVO.refEmpBranchID != $scope.inputVO.branchID){
//							// #1070:WMS-CR-20220630-01_主副Code專案-轉介功能調整: 2022 add 投保商品僅可轉介至同分行理專
//							// 2022 mark 該個金RM非客戶所屬個金RM && ($scope.inputVO.custAoCode != null && $scope.inputVO.custAoCode != $scope.inputVO.refAoCode)
//							$scope.setRefEmpDtl("ehl_01_ref110_004");
//							
//							return;
//						}
					}
					
					if (($scope.inputVO.refProd != null && $scope.inputVO.refProd != '5') && 
						($scope.inputVO.refEmpRoleName == "2" && $scope.inputVO.empRoleName == "3")) { //消金PS不可轉介給消金PS
						$scope.setRefEmpDtl("ehl_01_ref110_008");
						
						return;
					}
					
					if (($scope.inputVO.refProd != null && $scope.inputVO.refProd != '5') && 
						($scope.inputVO.refEmpRoleName == "5" && $scope.inputVO.empRoleName == "8")) { //個金AO不可轉介給個金AO
						$scope.setRefEmpDtl("ehl_01_ref110_010");
							
						return;
					}
					
//					if ($scope.custAoEmpID != "" && $scope.custAoEmpID != null && $scope.custAoEmpID != undefined && $scope.inputVO.refProd == '5') { // 應於輸入受轉介人員編後，若輸入為個金RM才需檢核是否有個金RM服務及投保註記。
//						if ($scope.inputVO.recYN != null && $scope.inputVO.recYN == "Y") { //有所屬個金RM且投保註記="Y"者不可轉介
//							$scope.errorClear();
//							$scope.showErrorMsg("ehl_01_ref110_002");											
//							$scope.checkCustStatus = false; // 客戶資訊輸入狀態
//							
//							return;
//						}
//					} 
				}
				
				if ($scope.data == "update") {
					if ($scope.inputVO.salerecCounts != null && $scope.inputVO.salerecCounts == 'Y') { //客戶30天內只可轉介同類商品一次(已移除同分行)
						$scope.showErrorMsg("同客戶、同商品，T+3個月底內只能轉介一次。");		
						
						return;
					} 
					
					$scope.sendRecv("REF110", "updRef", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", $scope.inputVO,
	    					function(tota, isError) {
								if (isError) {
									$scope.showErrorMsg(totas[0].body.msgData);
								} else {
									$scope.showSuccessMsg("ehl_01_common_006");
									$rootScope.menuItemInfo.url = "assets/txn/" + pageID + "/" + pageID + ".html";
								}
	    			});
				} else {
					$scope.sendRecv("REF110", "addRef", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", $scope.inputVO,
	    					function(tota, isError) {
								if (!isError) {
									if(tota[0].body.errorMsg)
										$scope.showWarningMsg(tota[0].body.errorMsg);
									$scope.showSuccessMsg("ehl_01_common_001");
									$rootScope.menuItemInfo.url = "assets/txn/REF900/REF900.html";
								}
	    			});
				}
			}
		});
	}
	
	$scope.padLeft = function (str, lenght) {
		if (str.length >= lenght)
	        return str;
	    else
	        return padLeft("0" + str, lenght);
	}
	
	$scope.goBack = function() {
		$rootScope.menuItemInfo.url = "assets/txn/" + pageID + "/" + pageID + ".html";
	}
	
});
