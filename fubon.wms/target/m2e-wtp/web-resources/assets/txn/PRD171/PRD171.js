/**
 *
 */
'use strict';
eSoafApp.controller('PRD171Controller',
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,$filter,getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD171Controller";
		$controller('CommonUtil', {$scope: $scope}); // 載入 CommonUtil

		//filter
        getParameter.XML(["IOT.PRODUCT_TYPE","PRD.INS_CLASS","FPS.CURRENCY","PRD.DOC_TYPE","IOT.DOC_CHK_LEVEL","PRD.MAIN_RIDER","IOT.PAY_TYPE","PRD.CERT_TYPE","PRD.TRAINING_TYPE","PRD.INS_ANCDOC_Q_TYPE","PRD.FEE_STATE"],
        	function(totas){
        		if(totas){
        			$scope.mappingSet['IOT.PRODUCT_TYPE'] = totas.data[totas.key.indexOf('IOT.PRODUCT_TYPE')];//IOT.PRODUCT_TYPE
        			$scope.mappingSet['PRD.INS_CLASS'] = totas.data[totas.key.indexOf('PRD.INS_CLASS')];//PRD.INS_CLASS
        			$scope.mappingSet['FPS.CURRENCY'] = totas.data[totas.key.indexOf('FPS.CURRENCY')];//FPS.CURRENCY  幣別
        			$scope.mappingSet['PRD.DOC_TYPE'] = totas.data[totas.key.indexOf('PRD.DOC_TYPE')];
        			$scope.mappingSet['IOT.DOC_CHK_LEVEL'] = totas.data[totas.key.indexOf('IOT.DOC_CHK_LEVEL')];
        			$scope.mappingSet['PRD.MAIN_RIDER'] = totas.data[totas.key.indexOf('PRD.MAIN_RIDER')];//PRD.MAIN_RIDER
        			$scope.mappingSet['IOT.PAY_TYPE'] = totas.data[totas.key.indexOf('IOT.PAY_TYPE')];
        			$scope.mappingSet['PRD.CERT_TYPE'] = totas.data[totas.key.indexOf('PRD.CERT_TYPE')]; //PRD.CERT_TYPE
        			$scope.mappingSet['PRD.TRAINING_TYPE'] = totas.data[totas.key.indexOf('PRD.TRAINING_TYPE')];//TRAINING_TYPE
        			$scope.typelist = totas.data[totas.key.indexOf('PRD.INS_ANCDOC_Q_TYPE')];//題目類型
        			$scope.mappingSet['PRD.FEE_STATE'] = totas.data[totas.key.indexOf('PRD.FEE_STATE')];
        		}
        });

        // 檢核登入者
        $scope.chkAuth = function(){
        	$scope.sendRecv("PRD171","chkAuth","com.systex.jbranch.app.server.fps.prd171.PRD171InputVO",{},function(tota,isError){
        		if(!isError){
        			$scope.Auth = '';
        			if(tota[0].body.chk_list[0].CHKAUTH == '1')
        				$scope.Auth = 'confirm';
        			else
        				$scope.Auth = 'maintenance';
        			$scope.mappingSet['APPROVER_REVIEW'] = [];
        			$scope.mappingSet['APPROVER_REVIEW'].push({LABEL:'已覆核' ,DATA:'已覆核'});
        			$scope.mappingSet['APPROVER_REVIEW'].push({LABEL:'未覆核' ,DATA:'未覆核'});
        			// defult為未覆核
        			if($scope.Auth == 'confirm')
        				$scope.inputVO.APPROVER = $scope.mappingSet['APPROVER_REVIEW'][1].LABEL;
        			else
        				$scope.inputVO.APPROVER = '';
        		}
        	});
        };

		const inquireCompany = () => {
			$scope.sendRecv("PRD176", "inquire", "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO", {},
				function (tota, isError) {
					if (!isError) {
						$scope.mappingSet['COMPANY'] = _.chain(tota[0].body.resultList)
							.map(each => ({LABEL: each.CNAME, DATA: each.SERIALNUM + ''}))
							.value();
					}
				});
		};

        // init
		$scope.init = function(){
			$scope.inputVO = {
				COMPANY_NAME: '',
        		P_TYPE: '1',
        		INSPRD_TYPE: '',
        		INSPRD_NAME: '',
        		INSPRD_ID : '',
        		select_all:'N'
        	};
			$scope.chkAuth();
			inquireCompany();
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.ins_ancdoclist = [];
		}
		$scope.inquireInit();

		$scope.select_all = function() {
        	if($scope.inputVO.select_all == 'Y' && $scope.ins_ancdoclist != undefined){
    			angular.forEach($scope.ins_ancdoclist, function(row, index, objs){
					row.choice = 'Y';
				});
        	}
        	if($scope.inputVO.select_all == 'N' && $scope.ins_ancdoclist != undefined){
    			angular.forEach($scope.ins_ancdoclist, function(row, index, objs){
					row.choice = 'N';
				});
        	}
        }

        $scope.select_false = function(choice){
        	if(choice == 'N'){
        		$scope.inputVO.select_all = 'N';
        	}
        }


        //查詢
        $scope.queryData = function(){
        	if($scope.inputVO.P_TYPE != ''){
        		$scope.inputVO.INSPRD_ID = $scope.inputVO.INSPRD_ID.toUpperCase();
            	$scope.sendRecv("PRD171","queryData","com.systex.jbranch.app.server.fps.prd171.PRD171InputVO",
            			$scope.inputVO,function(tota,isError){
            				if(!isError){
            					if(tota[0].body.INS_ANCDOCList.length == 0) {
    								$scope.showMsg("ehl_01_common_009");
    	                			return;
    	                		}
            					$scope.ins_ancdoclist=tota[0].body.INS_ANCDOCList;
                    			$scope.outputVO = tota[0].body;
                    			angular.forEach($scope.ins_ancdoclist, function(row, index, objs){
    								row.edit = [];
    			    				row.edit.push({LABEL: "修改", DATA: "U"});
    								row.edit.push({LABEL: "刪除", DATA: "D"});
    							});
    							return;
            				}
            	});
        	}else{
        		$scope.showErrorMsg('ehl_01_common_022');
        	}
        };

		$scope.edit = function(index,row){
			if (!$scope.firstCallInFreeze()) return;

			switch (row.editto) {
        	case 'U':
				const companies = $scope.mappingSet['COMPANY'];
				const ptype = $scope.inputVO.P_TYPE;
				var dialog = ngDialog.open({
					template: `assets/txn/PRD171/PRD171_EDIT${ptype}.html`,
					className: 'PRD171_EDIT' + ptype,
					controller: ['$scope', function ($scope) {
						$scope.title_type = 'Update';
						$scope.row_data = row;
						// 基本設定、連結標的設定、佣金檔設定傳入保險公司 Combobox 資料
						if (/[148]/.test(ptype))
							$scope.companies = companies;
					}]
				});
				dialog.closePromise.then(function (data) {
					$scope.inputVO.currentPageIndex = $scope.outputVO.currentPageIndex || 0;  // 帶回修改當前頁面
					$scope.inquireInit();
					$scope.queryData();
				});
				break;
			case 'D':
				var txtMsg = $filter('i18n')('ehl_02_common_001');
				$scope.inputVO.TYPE_TABLE=$scope.inputVO.P_TYPE;

				if($scope.inputVO.TYPE_TABLE=='1')
					{
					$scope.inputVO.INSPRD_KEYNO = row.INSPRD_KEYNO;
					}else{
						$scope.inputVO.KEY_NO = row.KEY_NO;
					}

					if($scope.inputVO.TYPE_TABLE=='3'){
						$scope.inputVO.INSPRD_ID = row.INSPRD_ID;
						$scope.inputVO.INSPRD_ANNUAL = row.INSPRD_ANNUAL;
					}

	        	$confirm({text: txtMsg},{size: 'sm'}).then(function(){
	               	$scope.sendRecv("PRD171","deleteData","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
	            			$scope.inputVO,function(tota,isError){
	            			if(isError){
	            				$scope.queryData();
	            			}
	                       	if (tota.length > 0) {
	                    		$scope.showSuccessMsg('ehl_01_common_003');
	                    		if($scope.ins_ancdoclist.length==1)
	                    			$scope.ins_ancdoclist = [];
	                    		$scope.queryData();
	                    		$scope.inputVO.currentPageIndex=$scope.outputVO.currentPageIndex || 0;
	                    	};
	            	});
	        	});
				break;

			default:
				break;
			}
        }

        $scope.addRow = function(){
			const ptype = $scope.inputVO.P_TYPE;
			if(ptype!=''){
        		var topage = '';
        		if(ptype == '3'){
        			topage = 'PRD171_ADD'+ptype;
        		}else{
        			topage = 'PRD171_EDIT'+ptype;
        		}
				const companies = $scope.mappingSet['COMPANY'];
				var dialog = ngDialog.open({
	        		template: 'assets/txn/PRD171/'+topage+'.html',
	        		className: topage,
	        		controller: ['$scope',function($scope){
	        			$scope.title_type='Add';
						// 基本設定、連結標的設定、佣金檔設定傳入保險公司 Combobox 資料
						if (/[148]/.test(ptype))
							$scope.companies = companies;
	        		}]
	        	});
	        	dialog.closePromise.then(function(data){
	        		$scope.queryData();
	        	});
        	}else{
        		$scope.showErrorMsg('欄位檢核錯誤:請輸入***產品類別***');
        	}


        }

        $scope.review = function(type){
        	$scope.checkstatus(type);
        	if($scope.inputVO.APPROVER == '已覆核' && type == 'Y'){
        		$scope.showErrorMsg('已覆核商品不可再核可');
        		return;
        	}
        	if($scope.inputVO.APPROVER == '未覆核' && type == 'N'){
        		$scope.showErrorMsg('未覆核商品不可再退回');
        		return;
        	}
        	$scope.inputVO.DILOGList = $scope.ins_ancdoclist;
        	$scope.sendRecv("PRD171", "updataAPPROVER", "com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO", $scope.inputVO,function(totas, isError){
        		console.log(totas);
        		if(!isError){
        			$scope.showSuccessMsg(totas[0].body);
        			$scope.queryData();
        		}
        	});
        };

        $scope.checkstatus = function(type){
        	for(var a=0;a<$scope.ins_ancdoclist.length;a++){
        		if($scope.inputVO.APPROVER == '已覆核' && type == 'Y'){
        			if($scope.ins_ancdoclist[a].choice == 'Y'){
        				if($scope.ins_ancdoclist[a].APPROVER != null){
        					$scope.ins_ancdoclist[a].choice = 'N';
        					$scope.unpack = false;
        					$scope.select_false('N');
        				}else{
        					$scope.unpack = true;
        				}
        			}
        		}
        		if($scope.inputVO.APPROVER == '未覆核' && type == 'N'){
        			if($scope.ins_ancdoclist[a].choice == 'Y'){
        				if($scope.ins_ancdoclist[a].APPROVER == null){
        					$scope.ins_ancdoclist[a].choice = 'N';
        					$scope.unpack = false;
        					$scope.select_false('N');
        				}else{
        					$scope.unpack = true;
        				}
        			}
        		}
        	}
        	return $scope.unpack;
        }


        // moron 2016/10/06
        $scope.download = function() {
			$scope.sendRecv("PRD171", "download", "com.systex.jbranch.app.server.fps.prd171.PRD171InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		if(totas[0].body.resultList && totas[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
					}
			);
		};

		$scope.upload = function() {
			var type = $scope.inputVO.P_TYPE;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD171/PRD171_UPLOAD.html',
				className: 'PRD171',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.type = type;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.queryData();
				}
			});
		};
		//


	}
);
