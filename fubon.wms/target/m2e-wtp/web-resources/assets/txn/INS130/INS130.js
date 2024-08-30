/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS130Controller',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, $q, validateService, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS130Controller";
		
		$scope.mapping = {};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		
		//xml參數初始化
		getParameter.XML(["CRM.CUST_GENDER","INS.INHERIT_TAX","FPS.INS_AVERAGE"], function(totas) {
			if (totas) {
				$scope.AVERAGE_AGE = totas.data[totas.key.indexOf('FPS.INS_AVERAGE')]; // 平均餘命
				$scope.mappingSet['CRM.CUST_GENDER'] = totas.data[totas.key.indexOf('CRM.CUST_GENDER')];//客戶性別
				$scope.mapping.links = totas.data[totas.key.indexOf('INS.INHERIT_TAX')].map(function (link) {
					return link.LABEL;
                });
			}
		});
				
		$scope.initial = function() {
			var page = $scope.connector('get','INS130_query');
			if(page){
				$scope.connector('set', "INS130_query", undefined);
				$scope.disabledEduAmt = false;
				$scope.inputVO = {
						hasRel:false,
						custId: '', 							//被保人ID
						custName: '', 							//被保人姓名
						birthDate:undefined,					//被保人生日
						age: undefined,							//年齡
						gender: '',								//性別
						partnerName:'',							//配偶(家庭成員)姓名
						partnerBirthDate:undefined,				//配偶(家庭成員)生日
						partnerGender:'',						//配偶(家庭成員)性別
						//應備費用
						livingExp: 0, 					//家庭生活基本開銷(月)(萬元)  
						notlivingExp: 0,				//家庭生活非必要開銷(月)(萬元)
						custLivingFee: 0,				//被保險人生活費用(月)
						childLivingFee: 0,				//子女生活費用(月)
						houDebtAmt: 0,					//房貸攤還金額(月)(萬元)
						houDebtY: 0,					//需幾年還清房貸
						eduAmt: 0,						//子女教育基金(萬元)
						carDebtAmt: 0,					//車貸(月)(萬元)
						carDebtY: 0,					//車貸還需幾年還清
						cardDebtAmt: 0,					//信貸(月)(萬元)
						cardDebtY: 0,					//信貸還需幾年還清
						otherDebtAmt: 0,				//其他貸款(月)(萬元)
						taxIn: 0,						//遺產稅(萬元)
						
						//已備費用
						income: 0,						//被保險人年薪(萬元)
						couIncome: 0,					//配偶年薪(萬元)
						rentIncome: 0,					//房房屋租金(年)(萬元)
						cashAmt: 0,						//現金及活期存款(萬元)
						stockAmt: 0,					//現有資產-股票(萬元)
						ctAmt: 0,						//現有資產-定存(萬元)
						fundAmt: 0,						//現有資產-基金(萬元)
						snAmt: 0,						//現有資產-連動債(萬元)
						investInsAmt: 0,				//現有資產-投資型保單(萬元)
						selfImmoveAmt: 0,				//自用不動產(萬元)
						investImmoveAmt: 0,				//房地產投資(萬元)
						trustAmt: 0,						//其他資產(萬元)
						
						//add by Brian
						paraType: undefined,                    //家庭財務安全類型判斷 : A.被保人生活費用, B.子女生活費用, C.子女教育基金
				
				};
				var custId = $scope.connector('get','INS_custID').CUST_ID;
				$scope.connector('set', "INS_custID", null);
				if(custId != undefined){
					$scope.inputVO.custId = custId;
				}	
				$scope.sendRecv("INS130", "queryData", "com.systex.jbranch.app.server.fps.ins130.INS130InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.custMastlist = tota[0].body.custMastlist;
								$scope.familyFinlist = tota[0].body.familyFinlist;							
								if($scope.custMastlist.length > 0) {
									//客戶基本資料
									$scope.inputVO.custName = $scope.custMastlist[0].CUST_NAME;
									$scope.inputVO.age = $scope.custMastlist[0].AGE;
									$scope.inputVO.gender = $scope.custMastlist[0].GENDER;
									$scope.inputVO.birthDate = $scope.custMastlist[0].BIRTH_DATE;
									if($scope.custMastlist[0].HAS_REL == true) {
										$scope.inputVO.hasRel = $scope.custMastlist[0].HAS_REL;
										$scope.inputVO.partnerName = $scope.custMastlist[0].REL_NAME;
										$scope.inputVO.partnerBirthDate = $scope.toJsDate($scope.custMastlist[0].REL_BIRTH);
										$scope.inputVO.partnerGender = $scope.custMastlist[0].REL_GENDER;
									} 								
									
		                		}
								
								if($scope.familyFinlist.length > 0) {
		                			//家庭財務安全問卷
		                			if($scope.custMastlist[0].HAS_REL == false) {
		                				$scope.inputVO.hasRel = $scope.custMastlist[0].HAS_REL;
										$scope.inputVO.partnerName = $scope.familyFinlist[0].PARTNER_NAME;
										$scope.inputVO.partnerBirthDate = $scope.toJsDate($scope.familyFinlist[0].PARTNER_BIRTH_DATE);
										$scope.inputVO.partnerGender = $scope.familyFinlist[0].PARTNER_GENDER;
									} 
		                			//應備費用
									$scope.inputVO.livingExp = $scope.familyFinlist[0].LIVING_EXP;
									$scope.inputVO.notlivingExp = $scope.familyFinlist[0].NOTLIVING_EXP;
									$scope.inputVO.custLivingFee = $scope.familyFinlist[0].CUST_LIVING_FEE;
									$scope.inputVO.childLivingFee = $scope.familyFinlist[0].CHILD_LIVING_FEE;
									$scope.inputVO.houDebtAmt = $scope.familyFinlist[0].HOU_DEBT_AMT;
									$scope.inputVO.houDebtY = $scope.familyFinlist[0].HOU_DEBT_Y;
									$scope.inputVO.eduAmt = $scope.familyFinlist[0].EDU_AMT;
									$scope.inputVO.carDebtAmt = $scope.familyFinlist[0].CAR_DEBT_AMT;
									$scope.inputVO.carDebtY = $scope.familyFinlist[0].CAR_DEBT_Y;
									$scope.inputVO.cardDebtAmt = $scope.familyFinlist[0].CARD_DEBT_AMT;
									$scope.inputVO.cardDebtY = $scope.familyFinlist[0].CARD_DEBT_Y;
									$scope.inputVO.otherDebtAmt = $scope.familyFinlist[0].OTHER_DEBT_AMT;
									$scope.inputVO.taxIn = $scope.familyFinlist[0].TAX_IN;
									//已備費用
									$scope.inputVO.income = $scope.familyFinlist[0].INCOME;
									$scope.inputVO.couIncome = $scope.familyFinlist[0].COU_INCOME;
									$scope.inputVO.rentIncome = $scope.familyFinlist[0].RENT_INCOME;
									$scope.inputVO.cashAmt = $scope.familyFinlist[0].CASH_AMT;
									$scope.inputVO.stockAmt = $scope.familyFinlist[0].STOCK_AMT;
									$scope.inputVO.ctAmt = $scope.familyFinlist[0].CT_AMT;
									$scope.inputVO.fundAmt = $scope.familyFinlist[0].FUND_AMT;
									$scope.inputVO.snAmt = $scope.familyFinlist[0].SN_AMT;
									$scope.inputVO.investInsAmt = $scope.familyFinlist[0].INVEST_INS_AMT;
									$scope.inputVO.selfImmoveAmt = $scope.familyFinlist[0].SELF_IMMOVE_AMT;
									$scope.inputVO.investImmoveAmt = $scope.familyFinlist[0].INVEST_IMMOVE_AMT;
									$scope.inputVO.trustAmt = $scope.familyFinlist[0].TRUST_AMT;
									
									var SPD_COUNT = $scope.familyFinlist[0].SPD_COUNT ? $scope.familyFinlist[0].SPD_COUNT:'0';
									$scope.disabledEduAmt = parseInt(SPD_COUNT) > 0 ? true:false
		                		}

								return;
							}
				});
		    
			}
		};
	    $scope.initial();
//	    家庭生活基本開銷(月)+非必要開銷(月) 應大於等於 被保險人生活費用(月)+子女生活費用(月)		
		//儲存
		$scope.saveData = function(isShowMsg) {
		  var defer = $q.defer();
		  var error = function(){
			  $timeout(function(){
				 defer.reject(false); 
			  });
			  return defer.promise;
		  }
			if($scope.parameterTypeEditForm.$invalid||$scope.inputVO.eduAmt == null || $scope.inputVO.eduAmt == undefined) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return error();
	    	}else if((Number($scope.inputVO.livingExp) + Number($scope.inputVO.notlivingExp)) < (Number($scope.inputVO.custLivingFee) + Number($scope.inputVO.childLivingFee))){
	    		$scope.showErrorMsg("ehl_01_INS130_001");
	    		return error();
	    	}
			$scope.sendRecv("INS130", "saveData", "com.systex.jbranch.app.server.fps.ins130.INS130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);								
							} else {
								if(isShowMsg) {
									$scope.showMsg("ehl_01_common_025");	
								}
							}
							debugger
							defer.resolve("success");
						} else {
							defer.reject();
						}							
			});
			return defer.promise;
		}
		
		//教育基金試算
		$scope.btnEdu = function() {
			var custId = $scope.inputVO.custId;//INS131需要$scope.custId
			var custName = $scope.inputVO.custName;//INS131需要$scope.custName
			var dialog = ngDialog.open({
				template: 'assets/txn/INS430/INS431_CHILDREN.html',
			    className: 'INS431',
			    controller:['$scope',function($scope){
			    	$scope.inputVO.custID = custId;
			    	$scope.formPage = 'INS130';
				}],
				showClose: false,
				scope : $scope
			 });
			
			dialog.closePromise.then(function(data){
//				console.log("INS131 data:" + JSON.stringify(data));
				if(data.value[0] == 'confirm') {
					$scope.disabledEduAmt = !(data.value[1] == undefined) && (data.value[1]!=0);
					$scope.inputVO.eduAmt = parseInt(data.value[1])/10000;
				} else {
					// 取消不做事
				}
			});
		}		
			
		// 百分比驗證
		$scope.checkPercentage = function(event) {
			if(event) {
				if(!(event.target.value>=0 && event.target.value<=100)) {
					$scope.showErrorMsg("請輸入 0~100 的值");	
					$scope.inputVO[event.target.id] = undefined;
				}
			}
		}
		
		// 生日換算年齡
		$scope.getAge = function(dateString) {
			var today = new Date();
            var birthDate = new Date(dateString);
            var age = today.getFullYear() - birthDate.getFullYear();
            var m = today.getMonth() - birthDate.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            return age;
        }
		
		// 家庭財務安全試算結果
		$scope.familyFinData = function() {
		
			$scope.saveData(false).then(function(){
				$scope.connector('set', "INS_PARGE", "INS132");
				$scope.connector('set','insCustID', $scope.inputVO.custId);		// 客戶ID
				$scope.connector('set','insCustName', $scope.inputVO.custName);		// 客戶姓名
				$scope.connector('set','insCustBirthday', $scope.inputVO.birthDate);	// 客戶生日
				$scope.connector('set','insCustGender', $scope.inputVO.gender);	// 客戶性別
				$rootScope.menuItemInfo.url = "assets/txn/INS132/INS132.html";
				debugger
			},function(){
				if($scope.parameterTypeEditForm.$invalid|| $scope.inputVO.eduAmt == null || $scope.inputVO.eduAmt == undefined) {
		    		$scope.showErrorMsg("ehl_01_common_022");
		    		return;
		    	}
				var result = undefined;
				angular.forEach($scope.AVERAGE_AGE, function(row, index) {
					if(row.DATA == $scope.inputVO.gender) {
						result = row;
					}
				});
				console.log(Number($scope.inputVO.livingExp) + Number($scope.inputVO.notlivingExp));
				console.log(Number($scope.inputVO.custLivingFee) + Number($scope.inputVO.childLivingFee));
				if(result) {
					if(parseInt(result.LABEL) < parseInt($scope.inputVO.age)) {
						$scope.showMsg("客戶目前年齡 " + $scope.inputVO.age + "歲 大於 " + ($scope.inputVO.gender == 1 ? '男性' : '女性') + " 平均餘命 " + result.LABEL + " 歲，無法進行試算!");
						return;
					}
				}
				debugger

			});
		}
		
		$scope.btnHelper = function(paraType){
			$scope.inputVO.paraType = paraType;
			
			$scope.sendRecv("INS130", "getPdfFile", "com.systex.jbranch.app.server.fps.ins130.INS130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.reportList = tota[0].body.reportList;
							$scope.inputVO.file_seq = tota[0].body.reportList[0].KEYNO;
							$scope.downloadDoc();
						}							
			});
			
		}
		
		//下載PDF檔案
		$scope.downloadDoc = function() {
			$scope.sendRecv("INS930", "downloadDoc", "com.systex.jbranch.app.server.fps.ins930.INS930InputVO", {'para_no': $scope.inputVO.file_seq},
				function(totas, isError) {
                	if (!isError) {
                		
                	};
				}
    		);
		};
});
