/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC320Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC320Controller";

		/** 初始化 **/
		function initial() {
			$scope.pri_type = false;
			getParam().then(() => {
				configureTrTypes();
			});

			$scope.init();
		}

		function getParam() {
			let defer = $q.defer();
			getParameter.XML(["KYC.TR_TYPE", "KYC.OUT_ACCESS"], function(totas) {
				if (totas) {
					$scope.access_List = totas.data[totas.key.indexOf('KYC.OUT_ACCESS')];
					$scope.trTypeList = totas.data[totas.key.indexOf('KYC.TR_TYPE')];
					defer.resolve("success");
				}
			});
			return defer.promise;
		}

		function configureTrTypes() {
			$scope.sendRecv("KYC320", "init", "com.systex.jbranch.app.server.fps.kyc320.KYC320InputVO",{},
				function(tota, isError) {
					$scope.inputVO.br_id = '';
					if (!isError) {
						if(tota[0].body.isBoss == true){
							$scope.type='0';
						}
						else{
							$scope.type='1';
						}

						//企金op or 企金主管
						if(tota[0].body.DEPT_ID != null && tota[0].body.DEPT_NAME != null){
							$scope.DEPT_ID = tota[0].body.DEPT_ID;
							$scope.DEPT_NAME = tota[0].body.DEPT_NAME;
						}
					}

					// 承作通路統一在此 function scope 裡賦值，避免 e-combobox 發生問題
					$scope.mappingSet['trType'] = $scope.trTypeList;

					//法金op or 法人主管
					if($scope.DEPT_ID != null && $scope.DEPT_NAME != null){
						$scope.mappingSet['trType'].push({LABEL: $scope.DEPT_ID + "-" + $scope.DEPT_NAME, DATA: $scope.DEPT_ID});
					}
					else{
						angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
							$scope.mappingSet['trType'].push({LABEL: row.BRANCH_NBR+"-"+row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						});
					}
					// for 列印時能夠匹配承作通路
					$scope.trTypeMap = {};
					$scope.trTypeList.forEach(e => $scope.trTypeMap[e.DATA] = e.LABEL);

					//可覆核冷靜期權限
					$scope.pri_type = tota[0].body.reviewCoolingPeriod ;
				}
			);
		}

		/**初始化輸入**/
		$scope.init = function(){	
			$scope.inputVO = {
					CUST_ID : '' ,
					sTime   : undefined,
					eTime   : undefined,
					INV     : '' ,
					TYPE    : '' ,			
					REC_SEQ    : ''			
        	};
			//清除時，連同查詢結果一起清除
			$scope.isClear = false;
		};

		
		$scope.clear = function(){
			$scope.init();
			$scope.outputVO = {};
			$scope.data = {};
			$scope.printList = [];
			$scope.isClear = true;
			$scope.paramList = [];
		}
        
		//英文字母轉大寫
		$scope.text_toUppercase = function(text,type){
			var toUppercase_text = text.toUpperCase();
			switch (type) {
			case 'CUST_ID':
				$scope.inputVO.CUST_ID = toUppercase_text;
				break;
			default:
				break;
			}
		}
		
		$scope.altInputFormats = ['M!/d!/yyyy'];
		
		//時間
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		/****修改----內訪外訪****/
		$scope.detail = function (row) {        	  
		var dialog = ngDialog.open({
			template: 'assets/txn/KYC320/KYC320_DETAIL.html',
			className: 'KYC320_DETAIL',
			showClose: false,
		       controller: ['$scope', function($scope) {
		    	   $scope.row = row;
		       }]
		});
		dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){	   				
					$scope.query();  		   				
				}
			});
		};
		
		/****錄音編號編輯****/
		$scope.to_REC = function (row) {        	  
			var dialog = ngDialog.open({
				template: 'assets/txn/KYC320/KYC320_REC.html',
				className: 'KYC320_REC',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = row;
				}]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){	   				
					$scope.query();  		   				
				}
			});
		};    
      
		/****冷靜期覆核****/
		$scope.to_COOLING_REC = function (row) {     	  
			var dialog = ngDialog.open({
				template: 'assets/txn/KYC320/KYC320_COOLINGREC.html',
				className: 'KYC320_COOLINGREC',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = row;
				}]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){	   				
					$scope.query();  		   				
				}
			});
		};



		/********查詢********/
		$scope.query = function(){
			//重新查詢時，上一次查詢結果需清除
			$scope.data = {};
			
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('客戶ID統編/評估日期 起訖    必須擇一輸入!!!');
        		return;
        	}			
			
			if($scope.inputVO.sTime && $scope.inputVO.eTime){
				if($scope.inputVO.sTime > $scope.inputVO.eTime){
					$scope.showErrorMsg('評估日期起日不可以大於迄日');
	        		return;
				}
			}
			
			$scope.sendRecv("KYC320", "inquire", "com.systex.jbranch.app.server.fps.kyc320.KYC320InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.list.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							$scope.paramList=[];
	            			return;
	            		}
						let data = tota[0].body.list;
						filterMemo(data);

						$scope.paramList = data;
						$scope.printList = angular.copy(data);

						$scope.outputVO = tota[0].body;
						return;
					}					
			});
			
			$scope.isClear = false;
		};

		/** #0509 備註只顯示冷靜期的特定訊息，其餘訊息清空 **/
		/** #0950 日盛弱勢戶註記 **/
		function filterMemo(data) {
			data.forEach(each => {
				if (each.REMARK !== '冷靜期解除生效日大於KYC到期日。則不解除冷靜期且不傳送主機。' && each.REMARK !== '日盛弱勢戶')
					each.REMARK = null
			})
		}

		$scope.queryKYC = function(){
			var dialog = ngDialog.open({
				 template:  'assets/txn/KYC320/KYC320_KYCebank.html',
				 className : "KYC320_KYCebank",
				 controller: ['$scope', function($scope) {
					 
				 }]
			});

		}
		
		//列印 產生PDF檔
    	$scope.print = function() {
			if($scope.inputVO.sTime && $scope.inputVO.eTime){
				if($scope.inputVO.sTime > $scope.inputVO.eTime){
					$scope.showErrorMsg('評估日期起日不可以大於迄日');
	        		return;
				}
			}
			
    		if($scope.paramList != undefined){
    			angular.forEach($scope.paramList, function(row, index, objs){
        			if(row.OUT_ACCESS=='2')
        				row.OUT_ACCESS='外訪';
        			
        			if(row.OUT_ACCESS=='1')
    					row.OUT_ACCESS='行內';
        			
        			var air = row.CREATE_DATE + "";
        			row.CREATE_DATE=air.substring(0,16);

        			if(row.INVEST_BRANCH_NBR=='999')
						row.INVEST_BRANCH_NBR = $scope.trTypeMap[row.EMP_ID] || '網銀';
        		});
    		}

    		console.log($scope.paramList);
    		
			$scope.sendRecv("KYC320", "print", "com.systex.jbranch.app.server.fps.kyc320.KYC320OutputVO",
				{
					'list':$scope.paramList , 
					'sTime' : $scope.inputVO.sTime , 
					'eTime' : $scope.inputVO.eTime , 
					'inv' : $scope.inputVO.INV,
					'custId' : $scope.inputVO.CUST_ID,
				}, 
				function(tota, isError) {
					if (!isError) {
						return;
					}
				});
		};
		
		//列印KYC
		$scope.printKYC = function(row, isEng) {
			$scope.showErrorMsg(row.SEQ);
			$scope.sendRecv("KYC320", "printKYC", "com.systex.jbranch.app.server.fps.kyc320.KYC320InputVO",{SEQ: row.SEQ, isPrintKYCEng: isEng},
					function(tota, isError) {
			});
		}
		
		initial();
});
