/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS100Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$q, $filter, getParameter,validateService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS100Controller";
		
		if($scope.connector('get','INS_PARGE')){
			$rootScope.menuItemInfo.url = "assets/txn/INS000/INS000.html";
		}
		
		// 有效起始日期
		$scope.bgn_sDateOptions = {
			maxDate: new Date(),
			minDate: $scope.minDate
		};
		
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		//XML
		getParameter.XML(["CRM.CUST_GENDER"],function(totas){
			if(totas){
				//性別
				$scope.mappingSet['CRM.CUST_GENDER'] = totas.data[0];
			}
		});
		
		// 身分驗證
		$scope.checkIdentify = function(){
			//檢核客戶ID合理性
			if(!$scope.inputVO.custId) {
				return;
			}
			$scope.inputVO.CUST_ID = $scope.inputVO.custId;
			$scope.sendRecv("INS200","vaildCUSTID","com.systex.jbranch.app.server.fps.ins200.INS200InputVO",
					$scope.inputVO,function(tota,isError){
						if(!isError){
							if(!tota[0].body.vaildcustid){
								$scope.inputVO.custId = '';
								$scope.showErrorMsg('ehl_01_common_030');
							}
						}
			});
		}
		
		
		
		//初始化
		$scope.initial = function() {
			$scope.disabled_button = true;
			$scope.none_data = false;
			$scope.choic_data = undefined;
			//選取資料
			$scope.inputVO = {
					custId: $scope.connector('get', "INS100_VIEW_DATA_BACK") ? $scope.connector('get', "INS100_VIEW_DATA_BACK").custId:'', 						//身分證字號
					add_custId:'',						//要新增的身分證字號
//					roleSelect: $scope.connector('get', "INS100_VIEW_DATA_BACK") ? $scope.connector('get', "INS100_VIEW_DATA_BACK").roleSelect:'1', 					//角色:本人
//					idSelect: $scope.connector('get', "INS100_VIEW_DATA_BACK") ? $scope.connector('get', "INS100_VIEW_DATA_BACK").idSelect:'1', 						//對象:要保人
					cust_name:'',						//客戶姓名(查無資料時)
					birth_date:undefined,				//生日(查無資料時)
					age:'',								//年齡
					gender:'',							//性別
					click:undefined,					//radio button
					resultList:[]
			};
	    };
	    
	    //清除要新增的客戶資料
	    $scope.clear_addCustData = function(query_none){
	    	if(query_none){
	    		$scope.inputVO.custId = '';
	    		$scope.inputVO.click = undefined;
	    	}
    		
    		$scope.inputVO.add_custId = '';
    		$scope.inputVO.cust_name = '';
    		$scope.inputVO.birth_date = undefined;
    		$scope.inputVO.age = '';
			$scope.inputVO.gender = '';
	    };
	    
	    //確認新增欄位enabled下排button
	    $scope.check_input_data = function(){
	    	if($scope.inputVO.cust_name != '') {
	    		var isNoPass = $scope.regclick();
	    		if(isNoPass) {
	    			$scope.disabled_button = true;
	    			return;
	    		}
	    	}
	    	if($scope.inputVO.birth_date != undefined){
	    		$scope.disabled_button = false;
	    	}else{
	    		$scope.disabled_button = true;
	    	}
	    }
	    
	    
	    //清對象 Radio
//	    $scope.checkRadio = function() {
//	    	$scope.inputVO.idSelect = '';
//	    };
	    
	    //查詢
	    $scope.query  = function() {
	    	
	    	$scope.checkIdentify();
	    	
	    	//查詢結果初始化
	    	$scope.none_data = false;
	    	$scope.clear_addCustData(false);
	    	$scope.inputVO.resultList = [];
	    	$scope.outputVO = [];
	    	$scope.choic_data = undefined;
	    	//欄位驗證
	    	if($scope.connector('get','INS_PARGE_BACK_INS100')) {
	    		
	    	} else {
	    		if($scope.parameterTypeEditForm.$invalid) {
	    			$scope.showErrorMsg("ehl_01_common_022");
	    			return;
	    		}
	    	}
	    	//CUST_ID轉大寫
	    	$scope.inputVO.custId = $filter('uppercase')($scope.inputVO.custId);
	    	
	    	$scope.sendRecv("INS100","query","com.systex.jbranch.app.server.fps.ins100.INS100InputVO",	$scope.inputVO,
	       			function(tota,isError){
		    			if (isError) {
		    				$scope.showErrorMsg(tota[0].body.msgData);
		    			} else {
		    				if(tota[0].body.resultList.length >0){
		    					//避免新增欄位有值
		    					$scope.none_data = false;
		    					$scope.clear_addCustData(false);
		    					
		    					$scope.inputVO.resultList = tota[0].body.resultList;
		    					$scope.outputVO = tota[0].body;
		    					$scope.inputVO.click = 1;
		    					$scope.choic_data = $scope.inputVO.resultList[0];
		    					$scope.disabled_button = false;
		    				}else{
		    					$scope.showMsg('ehl_01_INS100_004');
	    						$scope.disabled_button = true;
	    						//自然人和法人檢查
		    					var validCustID = validateService.checkCustID($scope.inputVO.custId);
		    					$scope.custIdCheck(validCustID);
			    			}
		    			}
	    	});
	    	
//    		if($scope.inputVO.roleSelect == '2'){
//    			//關係戶
//    			$scope.sendRecv("INS100","query_REL","com.systex.jbranch.app.server.fps.ins100.INS100InputVO",	$scope.inputVO,
//		       			function(tota,isError){
//			    			if (isError) {
//			    				$scope.showErrorMsg(tota[0].body.msgData);
//			    			} else {
//			    				if(tota[0].body.resultList.length >0){
//			    					//避免新增欄位有值
//			    					$scope.none_data = false;
//			    					$scope.clear_addCustData(false);
//			    					
//			    					$scope.inputVO.resultList = tota[0].body.resultList;
//			    					$scope.outputVO = tota[0].body;
//			    					$scope.inputVO.click = 1;
//			    					$scope.choic_data = $scope.inputVO.resultList[0];
//			    					$scope.disabled_button = false;
//			    				}else{
//			    					// $scope.showMsg('ehl_01_INS100_005');//查無資料，請至客戶首頁之關係戶維護畫面維護關係戶資料
//			    					//用被保人重新查詢
//			    					$scope.inputVO.roleSelect = '1';
//			    					$scope.inputVO.idSelect = '2';
//			    		    		$scope.query_AstInsMast();
//			    				}
//			    				
//			    			}//query_REL isError end
//		    	});//query_REL end
//    		}else{
//    			//個人查詢
//    			$scope.query_AstInsMast();
//    		}

	    	
	    };
	    
	    //個人查詢
//	    $scope.query_AstInsMast = function(){
//    		$scope.sendRecv("INS100","query","com.systex.jbranch.app.server.fps.ins100.INS100InputVO",	$scope.inputVO,
//	       			function(tota,isError){
//		    			if (isError) {
//		    				$scope.showErrorMsg(tota[0].body.msgData);
//		    			} else {
//		    				if(tota[0].body.resultList.length >0){
//		    					//避免新增欄位有值
//		    					$scope.none_data = false;
//		    					$scope.clear_addCustData(false);
//		    					
//		    					$scope.inputVO.resultList = tota[0].body.resultList;
//		    					$scope.outputVO = tota[0].body;
//		    					$scope.inputVO.click = 1;
//		    					$scope.choic_data = $scope.inputVO.resultList[0];
//		    					$scope.disabled_button = false;
//		    				}else{
//		    					$scope.showMsg('ehl_01_INS100_004');
//	    						$scope.disabled_button = true;
//	    						//自然人和法人檢查
//		    					var validCustID = validateService.checkCustID($scope.inputVO.custId);
//		    					$scope.custIdCheck(validCustID);
//			    			}
//		    			}
//	    	});
//	    };
	    
	    //完全查無資料時，檢核CUST_ID並開放使用者鍵入
	    $scope.custIdCheck = function(validCustID){
	    	if(validCustID){
				$scope.none_data = true;
				$scope.inputVO.add_custId = $scope.inputVO.custId;
				$scope.inputVO.click = 0;
				var cust_IdSecond = $scope.inputVO.add_custId.substring(1,2);
				$scope.determine_gender(cust_IdSecond);
			}else{
				$scope.clear_addCustData(true);
				$scope.none_data = false;
				$scope.showErrorMsg('請輸入正確的身分證字號')
			}
	    }
	    
	    //計算保險年齡
	    $scope.getAge = function(){
	    	$scope.sendRecv("INS810","getAge","com.systex.jbranch.app.server.fps.ins810.INS810InputVO",	{birthday:$scope.inputVO.birth_date},
	       			function(tota,isError){
		    			if (!isError) {
		    				$scope.inputVO.age = tota[0].body.age
		    				$scope.check_input_data();
		    			}
	    	});
	    };
	    
	    //依身分證判斷性別
	    $scope.determine_gender = function(cust_IdSecondNumber){
	    	if(cust_IdSecondNumber=='1'){
	    		$scope.inputVO.gender = 1;
	    	}else if(cust_IdSecondNumber=='2'){
	    		$scope.inputVO.gender = 2;
	    	}
	    }
	    
	    //被選取的資料列
	    $scope.radio_choic = function(row){
	    	$scope.choic_data = row;
	    }
	    
	    
		//新增客戶資料
		$scope.insertCustData = function(isCust){
			if(isCust){
				var dated = new Date($scope.choic_data.BIRTH_DATE);
					//有客戶資料
					$scope.INS_CUST_MASTIutputVO = {
							CUST_ID:$scope.choic_data.CUST_ID,
							CUST_NAME:$scope.choic_data.CUST_NAME,
							birthDay:dated.getFullYear()+ "-" + (dated.getMonth()+1) + "-" + dated.getDate(),
							GENDER:$scope.choic_data.GENDER,
							FB_CUST:'Y'					//是否為本行客戶
					}
				}else{
					//無客戶資料
					var dated = $scope.inputVO.birth_date;
					$scope.INS_CUST_MASTIutputVO = {
							CUST_ID:$scope.inputVO.add_custId,
							CUST_NAME:$scope.inputVO.cust_name,
							birthDay:dated.getFullYear()+ "-" + (dated.getMonth()+1) + "-" + dated.getDate(),
							GENDER:$scope.inputVO.gender,
							FB_CUST:'N'					//是否為本行客戶
					}
				}
	
				$scope.sendRecv("INS810","saveInsCustMast","com.systex.jbranch.app.server.fps.ins810.INS_CUST_MASTIutputVO",
						$scope.INS_CUST_MASTIutputVO,function(tota,isError){
							if(!isError){
							}
				});
		}

	     //點選保單健診同意書歷史轉跳INS120
	    $scope.Date_toINS120 = function(row){
	    	$scope.connector('set',"INS100_Date",row);
	    	$rootScope.menuItemInfo.url = "assets/txn/INS120/INS120.html";
	    };
	    
	    //下載[保單健診同意書]
		$scope.ins100R1 = function(){
			var custId = '';
			//未選取資料時使用查詢欄位cust_id
			if($scope.choic_data){
				custId = $scope.choic_data.CUST_ID;
			}else{
				custId = $scope.inputVO.custId;
			}
			
			if($scope.none_data){
				$scope.insertCustData(false);
			}else{
				$scope.insertCustData(true);
			}
			$scope.sendRecv("INS100","print1","com.systex.jbranch.app.server.fps.ins100.INS100InputVO",	{custId: custId},
	       			function(tota,isError){
	    			if (isError) {
	    				$scope.showErrorMsg(tota[0].body.msgData);
	    			} else {
	    				$scope.showMsg("ehl_01_common_023");//執行成功
	    			}
	    	});		
		};
	
	    
		//下載[保單返還簽收單]
		$scope.ins100R2 = function(){
			var custId = '';
			//未選取資料時使用查詢欄位cust_id
			if($scope.choic_data){
				custId = $scope.choic_data.CUST_ID;
			}else{
				custId = $scope.inputVO.custId;
			}
			
			if($scope.none_data){
				$scope.insertCustData(false);
			}else{
				$scope.insertCustData(true);
			}
			
			$scope.sendRecv("INS100","download_Single","com.systex.jbranch.app.server.fps.ins100.INS100InputVO",{custId:custId},
	       			function(tota,isError){
	    			if (isError) {
	    				$scope.showErrorMsg(tota[0].body.msgData);
	    			} else {
	    				$scope.showMsg("ehl_01_common_023");//執行成功
	    			}
	    	});
		};
		
		//下載[家庭財務安全問卷]
		$scope.ins100R3 = function(){
			$scope.sendRecv("INS100","download_Family","com.systex.jbranch.app.server.fps.ins100.INS100InputVO",$scope.inputVO,
	       			function(tota,isError){
	    			if (isError) {
	    				$scope.showErrorMsg(tota[0].body.msgData);
	    			} else {
	    				$scope.showMsg("ehl_01_common_023");//執行成功
	    			}
	    	});
		}
		
		
	    //進行健診
	    $scope.step2  = function() {
	    	if($scope.none_data){
				$scope.insertCustData(false);
			}else{
				$scope.insertCustData(true);
			}
	    	$scope.connector('set', "INS_custID", $scope.choic_data);
	    	$scope.connector('set', "INS_PARGE", "INS110");
	    	
	    	$scope.viewdatas = {
	    			custId : $scope.inputVO.custId
	    	}
	    	
	    	$scope.connector('set', "INS100_VIEW_DATA", $scope.viewdatas);
			$rootScope.menuItemInfo.url = "assets/txn/INS100/INS100.html";
	    };
	    
	    // 非同步操作
	    $scope.policyDo = function() {
	    	var defer = $q.defer();
	    	if($scope.none_data){
				$scope.insertCustData(false);
			}else{
				$scope.insertCustData(true);
			}
	    	defer.resolve("success");
	    	return defer.promise;
	    }
	    
	    //保單號碼維護
		$scope.ins100Policy = function() {
			var custId = '';
			//未選取資料時使用查詢欄位cust_id
			if($scope.choic_data){
				custId = $scope.choic_data.CUST_ID;
			}else{
				custId = $scope.inputVO.custId;
			}
			
			$scope.policyDo().then(function(data){
				var dialog = ngDialog.open({
					template: 'assets/txn/INS100/INS100_POLICY.html',	//Juan看這邊
					className: 'INS100_POLICY',	
					showClose: false,
					scope :$scope,
					controller:['$scope',function($scope){
						$scope.custId = custId;
					}]
				});
				
				dialog.closePromise.then(function(data){
					if(data.value != undefined){
						if(data.value != 'cancel'){
							$scope.ins100R1();
						}
					}
				});
			})
		};	
		
		//上傳[保單健診同意書]
		$scope.ins100Upload = function(){
			var custId = '';
			//未選取資料時使用查詢欄位cust_id
			if($scope.choic_data){
				custId = $scope.choic_data.CUST_ID;
			}else{
				custId = $scope.inputVO.custId;
			}
			
			var dialog = ngDialog.open({
				template: 'assets/txn/INS100/INS100_UPLOAD.html',
				className: 'INS100_UPLOAD',
				showClose: false,
				scope :$scope,
				controller: ['$scope', function($scope){
					$scope.custId = custId;
				}]
			});
			
			//關閉子界面時，刷新主界面
	    	dialog.closePromise.then(function(data) {
	    		if(data.value == 'cancel1'){
	    			 $scope.query();
	    			 if($scope.none_data){
	    					$scope.insertCustData(false);
	    				}else{
	    					$scope.insertCustData(true);
	    				}
					}
	    	});
		}
		
		$scope.initial();
		if($scope.inputVO.custId) {
			if($scope.connector('get','INS_PARGE_BACK_INS100')){
				$scope.query();
//				$scope.connector('set','INS_PARGE_BACK', null);
			}
		}
		
		$scope.regclick = function(){
			var isNoPass = false;
			var reg=/[@#\$%\^&\*]+/g ;
			var str = $scope.inputVO.cust_name;
		    if(reg.test(str)){
		    	$scope.inputVO.cust_name = null;
		    	isNoPass = true;
				$scope.showErrorMsg('請勿輸入特殊符號！');
		    }
		    if(str.match(/^[^ ].*\s+.*[^ ]$/)){
		    	$scope.inputVO.cust_name = null;
		    	isNoPass = true;
		    	$scope.showErrorMsg('請勿輸入空白！');
		    }
		    return isNoPass;
		}
});