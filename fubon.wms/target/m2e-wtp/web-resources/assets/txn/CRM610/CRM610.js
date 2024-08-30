'use strict';
eSoafApp.controller('CRM610Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM610Controller";

	// 2017/11/13
	$scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
	
	if (!$scope.IsMobile) {
		$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag());
		$scope.priID = parseInt(String(sysInfoService.getPriID()));
		$scope.role = sysInfoService.getPriID();
		$scope.ao_code = String(sysInfoService.getAoCode());
		
		//消金
		if ($scope.role == '004') {
			$scope.inputVO.role = 'ps';	
		} else if ($scope.role == '004AO') {
			//個金AO
			$scope.inputVO.role = 'pao';	
		} else if ($scope.role == '014' || $scope.role == '015' || $scope.role == '023' || $scope.role == '024' ) {
			//輔銷FA
			$scope.inputVO.role = 'faia';
		} else if ($scope.role == 'UHRM002' || $scope.role == 'UHRM012' || $scope.role == 'UHRM013') {
			//UHRM
			$scope.inputVO.role = 'UHRM';
		} else if ($scope.ao_code != '' &&  $scope.ao_code != undefined && $scope.role != '001') {
			//理專 : $scope.role == '001' ===>是AFC，AFC 應可從快查KEY ID或姓名來查詢同歸屬行內的任一客戶(#0002070)
			$scope.inputVO.role = 'ao';
			$scope.inputVO.ao_code = $scope.ao_code;
		} else {
			$scope.inputVO.role = 'other';
		}
	}

	// 客戶首頁儲存體(CRM610VO)
	// 取代connector,避免多開情況互相影響.
	$scope.CRM610VO = $scope.CRM610VO || {
		CRM681_CUSTDEPOSIT: undefined,
		CRM681_Investment: undefined,
		CRM681_Sec: undefined
	}; 
	
	$scope.custVO = {
			CUST_ID		: '',    //客戶ID
	  		CUST_NAME   : '',    //客戶姓名
	  		AO_CODE     : '',    //AO_CODE
	  		AO_ID       : '',    //AO_ID
	  		VIP_DEGREE  : '',    //客戶等級
	  		RISK_ATTR   : '',    //客戶風險屬性
	  		CUST_BRANCH : '',    //客戶主往來分行
	  		CUST_AREA   : '',     //客戶主往來營運區
	  		CUST_REGION : ''     //客戶主往來區域
	};
	
	$scope.loadData = function() {
		//檢查完轄下客戶才開始載入資料
		$scope.routeURL = $scope.connector("get","CRM610URL");
		
		// 監聽來自 CRM610_MAIN.js 載入好的共用資料，並將這些資料廣播以使得底下各個子元件都可以監聽取得
		$scope.$on('CRM610_MAIN', function(event, data) {
			console.log('【客戶首頁】已取得【客戶首頁MAIN】載入的資料並且廣播中...');
			$scope.$broadcast("CRM610_DATA", data);
		});

		// 客戶首頁事件接收器
		// data.action = 動作 set | get
		// data.type = 事件類型key
		// data.data = 資料
		$scope.$on("CRM610VO", function(e, data){
			if(data.action === 'set'){
				console.log('****** [CRM610][SET](data===false: ${(len(data.data)===0 || len(data.data) === false)}) ******');
				if(len(data.data)===0 || len(data.data) === false)return;
				if(data.type=='URL'){
					// 監聽引入的子元件 emit 的 url 已達到變換客戶首頁頁面的效果
					console.log("****** [CRM610][SET][URL] ******");
					console.log('url:${data.data}')
					$scope.routeURL = data.data;
				}
				if(data.type=='CRM681_CUSTDEPOSIT'){
					console.log("****** [CRM610][SET][CRM681_CUSTDEPOSIT] ******");
					$scope.CRM610VO.CRM681_CUSTDEPOSIT = data.data;
				}
				if(data.type=='CRM681_Investment'){
					console.log("****** [CRM610][SET][CRM681_Investment] ******");
					$scope.CRM610VO.CRM681_Investment = data.data;
				}
				if(data.type=='CRM681_Sec'){
					console.log("****** [CRM610][SET][CRM681_Sec] ******");
					$scope.CRM610VO.CRM681_Sec = data.data;
				}
			}

			if(data.action === 'get'){
				console.log('****** [CRM610][GET] ******');
				if(data.type=='CRM681_CUSTDEPOSIT'){
					console.log("****** [CRM610][GET][CRM681_CUSTDEPOSIT] ******");
					data.data($scope.CRM610VO.CRM681_CUSTDEPOSIT);
				}
				if(data.type=='CRM681_Investment'){
					console.log("****** [CRM610][GET][CRM681_Investment] ******");
					data.data($scope.CRM610VO.CRM681_Investment);
				}
				if(data.type=='CRM681_Sec'){
					console.log("****** [CRM610][SET][CRM681_Sec] ******");
					data.data($scope.CRM610VO.CRM681_Sec);
				}
			}
		});
	}
	
	$scope.CRM_CUSTVO = $scope.connector('get','CRM_CUSTVO');
	$scope.connector('set','CRM_CUSTVO',[]);
	if($scope.CRM_CUSTVO != null) {
		$scope.custVO = {
			CUST_ID   : $scope.CRM_CUSTVO.CUST_ID,
			CUST_NAME : $scope.CRM_CUSTVO.CUST_NAME
		};
		$scope.cust_name = $scope.CRM_CUSTVO.CUST_NAME;
		$scope.cust_id = $scope.CRM_CUSTVO.CUST_ID;
				
		if (!$scope.IsMobile) {
			//檢查客戶是否為轄下客戶
			$scope.inputVO.type = 'ID';
			$scope.inputVO.cust_id = $scope.cust_id;
			
			$scope.sendRecv("CRM110", "inquire", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					/******查無資料時(查詢為何查無資料)******/
					if(tota[0].body.resultList.length == 0 && ($scope.inputVO.cust_id).indexOf("SFA") < 0) { // 留資名單客戶不查詢
						if($scope.inputVO.type == 'ID') {
							$scope.inputVO.cust_id = $scope.cust_id;
							$scope.sendRecv("CRM110", "inquireCust", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.inputVO, function(tota, isError) {
								if (!isError) {
									if(tota[0].body.resultList.length == 0) {
										//查無資料
										$scope.showErrorMsg('ehl_01_cus130_002',[$scope.inputVO.cust_id]);			//無此客戶ID：{0}
										$scope.closeThisDialog('cancel');
										return;
									}
									
									if(tota[0].body.resultList.length == 1) {
										if(tota[0].body.resultList[0].BRA_NBR == null){
											//無歸屬行
											$scope.showErrorMsg('ehl_01_cus130_002',[$scope.inputVO.cust_id]);		//無此客戶ID：{0}
											$scope.closeThisDialog('cancel');
											return;
										}else{
											if(tota[0].body.resultList[0].EMP_NAME == null){
												//空code客戶
												var list = [tota[0].body.resultList[0].BRA_NBR, tota[0].body.resultList[0].BRANCH_NAME, tota[0].body.resultList[0].RM_NAME ? '、 RM' + tota[0].body.resultList[0].RM_NAME : ''];
												$scope.showErrorMsg('ehl_01_cus130_006', list);		// 客戶歸屬行：{0}-{1}{2}
												$scope.closeThisDialog('cancel');
												return;	
											}else{
												//有歸屬行&所屬理專
												var list = [tota[0].body.resultList[0].BRA_NBR, tota[0].body.resultList[0].BRANCH_NAME, tota[0].body.resultList[0].AO_CODE, tota[0].body.resultList[0].EMP_NAME, tota[0].body.resultList[0].RM_NAME ? '、 RM' + tota[0].body.resultList[0].RM_NAME : ''];
												$scope.showErrorMsg('ehl_01_cus130_005', list);		// 客戶歸屬行：{0}-{1}，理專{2}{3}{4}
												$scope.closeThisDialog('cancel');
												return;											
											}
										}											
									}									
								}								
							});								
						}
					} else {	
						$scope.loadData();
					}
				}
			});
			
			// === 寫入分行理財客戶查詢紀錄日報 ===
			if ($scope.memLoginFlag == 'brhMem' && $scope.priID != '011') {
				$scope.sendRecv("CRM610", "addInquireLog", "com.systex.jbranch.app.server.fps.crm610.CRM610InputVO", {'cust_id'  : $scope.cust_id, 'cust_name': $scope.cust_name}, function(tota, isError) {});
			}
		} else {
			$scope.loadData();
		}
	} else {
		$scope.showErrorMsg('必要客戶ID未傳入');
	}

});