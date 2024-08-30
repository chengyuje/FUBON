'use strict';
eSoafApp.controller('CRM611Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, sysInfoService , getParameter, $q) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM611Controller";

	$scope.inputVO.custID = $scope.cust_id;
	$scope.getPara = function(){
        getParameter.XML(['CRM.VIP_DEGREE','CRM.CUST_GENDER','COMMON.YES_NO','KYC.MARRAGE','CRM.VIP_ATR'], function(totas) {
			if(len(totas)>0){
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.mappingSet['CRM.CUST_GENDER'] = totas.data[totas.key.indexOf('CRM.CUST_GENDER')];
				$scope.mappingSet['CRM.VIP_ATR'] = totas.data[totas.key.indexOf('CRM.VIP_ATR')];
			}
			 
		});
    }
    $scope.getPara();
    
    $scope.cust_id =  $scope.custVO.CUST_ID;
	$scope.data = {'isnull':true};
	$scope.login = String(sysInfoService.getPriID());

	function getFP032151() {
		var defer = $q.defer();

		$scope.sendRecv("CRM611", "inquire", "com.systex.jbranch.app.server.fps.crm611.CRM611InputVO", {'cust_id': $scope.cust_id, 'data067050_067101_2': crm610Data.data067050_067101_2, 'data067050_067000': crm610Data.data067050_067000, 'data067050_067112': crm610Data.data067050_067112}, function (tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList != null && tota[0].body.resultList.length > 0) {
					$scope.dbData = tota[0].body.resultList[0];
					$scope.inputVO.infrom_way = $scope.dbData.PREF_WF_ROUTE_MTHD;
					$scope.inputVO.infrom_way = $scope.dbData.PREF_INFORM_WAY;

					$scope.esbData2 = tota[0].body.fp032151OutputVO;
					defer.resolve("success");
				}
			} else {
				defer.reject();
			}
		});
		return defer.promise;
	}

	function getCustNote(custData, esbData) {
		//客戶註記：禁銷戶只要顯示"NS",拒銷戶只要顯示"RS"
		$scope.cust_note = "";
		
		if (custData.COMM_NS_YN == "Y") {
			$scope.cust_note = $scope.cust_note + "NS/";
		}
		
		if (custData.COMM_RS_YN == "Y") {
			$scope.cust_note = $scope.cust_note + "RS/";
		}
		
		if (esbData.custRemarks == "Y") {
			$scope.cust_note = $scope.cust_note + "特定客戶/";
		}
		
		if (custData.COMPLAIN_YN == "Y") {
			$scope.cust_note = $scope.cust_note + "客訴戶/";
		}
		
		if ($scope.cust_note != "") {
			$scope.cust_note = $scope.cust_note.substring(0, $scope.cust_note.length - 1);
		}
	}

	// 客戶基本資料
	function getCust() {
		var defer = $q.defer();

		$scope.sendRecv("CRM611", "getCust", "com.systex.jbranch.app.server.fps.crm611.CRM611InputVO", {'cust_id':$scope.cust_id}, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList && tota[0].body.resultList.length) {
					$scope.data = tota[0].body.resultList[0];

					$scope.custName = $scope.data.CUST_NAME;
					$scope.oldTodo = $scope.data.TODO;

					$scope.custVO.AO_CODE = tota[0].body.AO_CODE;
					$scope.custVO.AO_ID = tota[0].body.AO_ID;
					$scope.custVO.VIP_DEGREE = tota[0].body.VIP_DEGREE;
					$scope.custVO.RISK_ATR = tota[0].body.RISK_ATR;
					$scope.custVO.CUST_BRANCH = tota[0].body.CUST_BRANCH;
					$scope.custVO.CUST_AREA = tota[0].body.CUST_AREA;
					$scope.custVO.CUST_REGION = tota[0].body.CUST_REGION;
					$scope.connector('set', 'CRM611_DATA', $scope.data);
					defer.resolve($scope.data);
				}
			} else {
				defer.reject();
			}
		});
		return defer.promise;
	}

	function getSDACTQ8Data() {
		var defer = $q.defer();
		$scope.sendRecv("SOT701", "getSDACTQ8Data", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {'custID': $scope.cust_id},
			function (tota, isError) {
				if (!isError) {
					$scope.siPromData = tota[0].body.siPromDataVO;
					$scope.siPromData.isEffetive = $scope.siPromData.isSign;
					//若已終止或已失效，則顯示為未簽署
					if ($scope.siPromData.status != null && ($scope.siPromData.status == "C" || $scope.siPromData.status == "E"))
						$scope.siPromData.isEffetive = "N";

					$scope.connector('set', 'CRM611_siPromData', $scope.siPromData);
					defer.resolve("success");
				} else {
					defer.reject();
				}
			});
		return defer.promise;
	}

	function getFP032675Data() {
		var defer = $q.defer();

		// data
		$scope.sendRecv("SOT701", "getFP032675Data", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {'custID':$scope.cust_id, 'isOBU': crm610Data.isObu, 'data067050_067101_2': crm610Data.data067050_067101_2, 'data067050_067000': crm610Data.data067050_067000}, function(tota, isError) {
			if (!isError) {
				$scope.esbData = tota[0].body.fp032675DataVO;
				$scope.connector('set','CRM611_esbData',$scope.esbData);
				defer.resolve($scope.esbData);
			} else {
				defer.reject();
			}
		});
		return defer.promise;
	}
	
	function getW8BenData() {
		var defer = $q.defer();

		// data
		$scope.sendRecv("SOT701", "getW8BenData", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {'custID':$scope.cust_id}, function(tota, isError) {
			if (!isError) {
				$scope.w8BenData = tota[0].body.w8BenDataVO;
				$scope.connector('set','CRM611_w8BenData', $scope.w8BenData);
				defer.resolve($scope.esbData);
			} else {
				defer.reject();
			}
		});
		return defer.promise;
	}

	//取得高資產客戶註記資料
	function getHNWCData() {
		var defer = $q.defer();

		// data
		$scope.sendRecv("SOT701", "getHNWCData", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {'custID':$scope.cust_id}, function(tota, isError) {
			if (!isError) {
				$scope.hnwcData = tota[0].body.hnwcDataVO;
				defer.resolve("success");
			} else {
				defer.reject();
			}
		});
		return defer.promise;
	}
	
	//取得客戶失聯註記
	function getLostFlagData() {
		var defer = $q.defer();

		// data
		$scope.sendRecv("CRM611", "getLostFlag", "com.systex.jbranch.app.server.fps.crm611.CRM611InputVO", {'cust_id':$scope.cust_id}, function(tota, isError) {
			if (!isError) {
				$scope.lostFlag = tota[0].body.lostFlag;
				defer.resolve("success");
			} else {
				defer.reject();
			}
		});
		return defer.promise;
	}
	
	function init() {
		$q.all({
			SDACTQ8Data: getSDACTQ8Data(),
			FP032675Data: getFP032675Data(),
			FP032151Data: getFP032151(),
			custData: getCust(),
			W8BENData:getW8BenData(),
			HNWCData:getHNWCData(),
			lostFlagData:getLostFlagData()
		}).then(function(result) {
			var str = '請注意！';
			
			var msgCustTxFlag = '';
			if ($scope.esbData.custTxFlag == 'N' || $scope.siPromData.isEffetive == 'N') {

				if ($scope.esbData.custProFlag == 'Y') {
					msgCustTxFlag = '該客戶為專業投資人，未簽署';
				} else {
					msgCustTxFlag = '該客戶未簽署';
				}

				if ($scope.esbData.custTxFlag == 'N' && $scope.siPromData.isEffetive == 'N') {
					msgCustTxFlag += '信託及衍商(結構型商品)';
				} else if ($scope.esbData.custTxFlag == 'N') {
					msgCustTxFlag += '信託';
				} else if ($scope.siPromData.isEffetive == 'N') {
					msgCustTxFlag += '衍商(結構型商品)';
				}

				if ($scope.esbData.custProFlag == 'Y') {
					msgCustTxFlag += '推介同意書';
				} else {
					msgCustTxFlag += '推介同意書，不得主動推介商品！';
				}
			} 
			
			var msgKycExpiryYN = '';
			if ($scope.data.KYC_EXPIRY_YN != 'Y' ) {
				msgKycExpiryYN += '客戶無有效KYC，請先完成KYC問卷，始得進行商品推介。';
			}
			
			var msgOldTODO = '';
			if ($scope.oldTodo != null ) {
				msgOldTODO += $scope.oldTodo;
			}
			
			var msgAMToverKYC = '';
			if ($scope.data.KYC_EXPIRY_YN == 'Y' && $scope.data.AMT_KYC_FLAG == 'Y') {
				msgAMToverKYC += '高齡客戶投資比重(含投資型保險)高於KYC資產配置佔比偏好，請加強關懷。';
			}
			
			var msgLostFlag = '';
			if ($scope.lostFlag) {
				msgLostFlag += '客戶地址或e-mail有失聯註記，請與客戶確認更新。';
			}
			
			if (msgCustTxFlag != '' || msgKycExpiryYN != '' || msgOldTODO != '' || msgAMToverKYC != '' || msgLostFlag != '') {
				$confirm({
						text	: str, 
						text1: msgCustTxFlag, 
						text2: msgKycExpiryYN, 
						text3: msgOldTODO, 
						text4: msgAMToverKYC,
						text5: msgLostFlag,
						title: '提醒', hideCancel: true
					}, 
					{size: '120px'}).then(function() {}
				);
			}
			
			getCustNote(result.custData, result.FP032675Data)
		});
	}

	// CRM611 如果是由客戶首頁 include 的，則監聽來自於客戶首頁的廣播，以取得已載入的共用資料
	console.log('客戶基本資料_fromCRM610:' + $scope.fromCRM610);
	let crm610Data = {};
	if ($scope.fromCRM610) {
		$scope.$on('CRM610_DATA', function(event, data) {
			console.log('【客戶基本資料】已收到【客戶首頁】的廣播...');
			crm610Data = data;
			init();
		});
	} else {
		init();
	}
	
	$scope.jump = function() {
		var openDialog = $scope.connector('get','openDialog')			
			//walalala 2017/1/9 修改 
			//原本是一定要執行jump function 才會傳值到CRM611_DETAIL.js
			//如果沒執行此FUNCTION就會錯誤, 所以改成只要接到值就馬上傳
			//傳值太亂~"~ 目前先這樣改吧
//				$scope.connector('set','CRM611_DATA', $scope.data);
//				$scope.connector('set','CRM611_esbData',$scope.esbData);
			$scope.connector('set','CRM610_tab', 0);
			var path = "assets/txn/CRM610/CRM610_DETAIL.html";
			var set = $scope.connector("set","CRM610URL",path);
			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
//				$rootScope.menuItemInfo.url = "assets/txn/CRM610/CRM610.html";
			//從名單CAM200 連接至客戶首頁詳細資料 -- BY Stella
			if(openDialog != undefined) {
				$scope.custVO = {
						CUST_ID :  $scope.custID || $scope.cust_id,
						CUST_NAME :$scope.custName	
				}
				$scope.connector('set','CRM_CUSTVO',$scope.custVO);
				var dialog = ngDialog.open({
					template: 'assets/txn/CRM610/CRM610.html',
					className: 'CRM610',
					showClose: false
				});
				return;
			}			
    };		
});
