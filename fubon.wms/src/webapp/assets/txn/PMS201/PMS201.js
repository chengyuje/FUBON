'use strict';
eSoafApp.controller('PMS201Controller', 
    function($rootScope, $scope, sysInfoService, $controller, ngDialog,projInfoService, getParameter) {
        $controller('BaseController', {$scope: $scope});
        
        $scope.controllerName = "PMS201Controller";
        
        //組織連動
		$controller('PMSRegionController', {$scope: $scope});
		
		// filter
		getParameter.XML(["PMS.PROD_ITEM", "PMS.COACHING_STATE", "PMS.PRDCIVT1_TITLE_NAME", "PMS.PRDCIVT3_TITLE_NAME", "PMS.PERFORMANCE_TITLE_NAME"], function(totas) {
			if (totas) {
				$scope.mappingSet['PMS.PROD_ITEM'] = totas.data[totas.key.indexOf('PMS.PROD_ITEM')];
				$scope.mappingSet['PMS.COACHING_STATE'] = totas.data[totas.key.indexOf('PMS.COACHING_STATE')];
				$scope.mappingSet['PMS.PRDCIVT1_TITLE_NAME'] = totas.data[totas.key.indexOf('PMS.PRDCIVT1_TITLE_NAME')];
//				$scope.mappingSet['PMS.PRDCIVT3_TITLE_NAME'] = totas.data[totas.key.indexOf('PMS.PRDCIVT3_TITLE_NAME')];
				$scope.mappingSet['PMS.PERFORMANCE_TITLE_NAME'] = totas.data[totas.key.indexOf('PMS.PERFORMANCE_TITLE_NAME')];
			}
		});
		
		/*** 可示範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
		$scope.dateChange = function(){
			$scope.inputVO.sCreDate = $scope.inputVO.eTime;
	        if($scope.inputVO.sCreDate!=''){ 	
	        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };
        /*** 可示範圍  JACKY共用版  END***/
	    

		/**客戶要求:AUM淨增減===>AUM淨增減(百萬)
		 * 資料庫的資料不可變動
		 * 因此直接在前端新增*/
		  
		$scope.mappingSet['PMS.PRDCIVT3_TITLE_NAME'] = [];
		$scope.mappingSet['PMS.PRDCIVT3_TITLE_NAME'].push({LABEL:'AUM淨增減(百萬)' , DATA:'AUM_DIFF'},
														  {LABEL:'CIF淨增減' , DATA:'CIF_DIFF'},
														  {LABEL:'客戶數' , DATA:'CUST_CNT'},
														  {LABEL:'未聯繫客戶數' , DATA:'NON_CNTCT_CNT'},
														  {LABEL:'未符合經營頻次數' , DATA:'UNSERVE_CNT'}
														 );
		
		
		$scope.curDate = new Date();
		$scope.Appendzero =  function (obj) {
			if (obj < 10) 
				return "0" + "" + obj;
			else 
				return obj; 
		};
		
		$scope.init = function () {
			var d = new Date();
			$scope.inputVO = {	
//					reportDate		: d.getFullYear() + "" + $scope.Appendzero(d.getMonth()),
					region_center_id: '',
					branch_area_id  : '',
					branch_nbr   	: '',
					ao_code  		: '',
					tgtType:'TAR',
					sCreDate		: '',
					reportDate:'',
					eTime:'',
			       
			};
			
//			$scope.RegionController_getORG($scope.inputVO);
		};
		$scope.init();
        
    	$scope.monweek = function(){
            $scope.sendRecv("PMS201", "monweek", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", {},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    		
                    		return;
                    	}
                    	if (totas.length > 0) {
                    		$scope.mappingSet['timeE'] = [];
                    		angular.forEach(totas[0].body.weekDate, function(row, index, objs){
                    			$scope.mappingSet['timeE'].push({LABEL: row.LABEL, DATA: row.WEEK_START_DATE});
                			});
                    		
                    		$scope.inputVO.eTime = $scope.mappingSet['timeE'][0].DATA;
                    	};
    				}
    		);
        }
    	$scope.monweek();
    	
    	$scope.query = function () {
    		if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
    		/*0001575 修改*/
    		$scope.inputVO.reportDate=$scope.inputVO.sCreDate;
    		$scope.sendRecv("PMS201", "aorank", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg();
                    		return;
                    	}
                    	
                    	if (totas[0].body.resultList.length > 0) {
                    		
                    	$scope.AO_JOB_RANK=	totas[0].body.resultList[0].AO_JOB_RANK;
                    	$scope.PRODUCT_GOALS=totas[0].body.resultList[0].PRODUCT_GOALS;
                    	};
    				}
    		);
    		$scope.inputVO2=angular.copy($scope.inputVO);
    		$scope.inputVO2.reportDate=$scope.inputVO2.reportDate.substring(0, 6);
//    		alert($scope.inputVO2.reportDate);
    		/*0002293 修改*/
//    		$scope.sendRecv("PMS203", "queryData", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO2,
//					function(tota, isError) {
//						if (!isError) {
//				
//							if(tota[0].body.resultList.length == 0) {
////								$scope.showMsg("ehl_01_common_009");
//	                			return;
//	                		}
//						 $scope.PRODUCT_GOALS=tota[0].body.resultList[0].PRODUCT_GOALS;
//							
////							$scope.paramList = tota[0].body.resultList;
////							$scope.totalData = tota[0].body.totalList;
////							$scope.outputVO = tota[0].body;
////							$scope.outputVO.targetType = $scope.inputVO.tgtType;
////							
////					        var NowDate4 = new Date();
////							var yr4 = NowDate4.getFullYear();
////					        var mm4 = NowDate4.getMonth()+1;
////					        var strmm4= mm4<10? '0' + mm4:mm4;
////					        var ym4 = yr4+strmm4;
////							var reportDate = $scope.inputVO.reportDate;
////							
////							if(reportDate == ym4) {
////								$scope.editflag=true;
////							}else{
////								$scope.editflag=false;
////							}
//
//							return;
//						}						
//			});
    		
    		
    		$scope.EST_AMT = 0;
        	$scope.EST_EARNINGS = 0;
        	$scope.SALETARGET=0  //目標達成
//        	alert(1);
        	$scope.inputVO3=angular.copy($scope.inputVO);
        	$scope.inputVO3.sTime=$scope.inputVO.reportDate.substring(0, 6);
//        	alert(JSON.stringify($scope.inputVO3));
			$scope.sendRecv("PMS103", "queryData", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", $scope.inputVO3,
					function(tota, isError) {
						if (!isError) {
							$scope.paramLi = [];
							$scope.paramLi = tota[0].body.resultList;  //結果
							$scope.SALETARGET = tota[0].body.tarList[0].SALETARGET;
							$scope.outputVO = tota[0].body;
							$scope.PLAN_AMT=0;
							$scope.EST_AMT=0;
							$scope.EST_EARNINGS=0;
							//金流金額
							for(var i = 0; i < $scope.paramLi.length; i++){
								$scope.EST_AMT += $scope.paramLi[i].EST_AMT;
						        $scope.EST_EARNINGS += $scope.paramLi[i].EST_EARNINGS;
						      
							}
//							alert( $scope.EST_EARNINGS);
						
							return;
						}
			});	
    		
    		
    		// A GROUP
    		$scope.sendRecv("PMS201", "getEmpData", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    		return;
                    	}
                    	if (totas.length > 0) {
                    		$scope.empDtl = totas[0].body.empDtl;
                    		if ($scope.empDtl.length > 0) {
                    			$scope.inputVO.selfNote = $scope.empDtl[0].SELF_NOTE;
                    		}
                    		
							$scope.empDtlOutputVO = totas[0].body;
                    	};
    				}
    		);
    		
    		$scope.sendRecv("PMS201", "getResultBehaveRate", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    		return;
                    	}
                    	if (totas.length > 0) {
                    		$scope.aGroupTitleList = totas[0].body.aGroupTitleList;
                    		$scope.resultBehaveRateList = totas[0].body.resultBehaveRate;

							$scope.resultBehaveRateOutputVO = totas[0].body;
                    	};
    				}
    		);
    		
    		
    		// B GROUP
    		$scope.sendRecv("PMS201", "getProdGroupIndex", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    		return;
                    	}
                    	if (totas.length > 0) {
                    		$scope.bGroupTitleList = totas[0].body.bGroupTitleList;
                    		$scope.prodGroupIndexList = totas[0].body.prodGroupIndex;
                    		$scope.prodTrackDtlList = totas[0].body.prodTrackDtl;

							$scope.prodGroupIndexOutputVO = totas[0].body;
                    	};
    				}
    		);
    		
    		// C GROUP
    		$scope.sendRecv("PMS201", "getPrdctvt1", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    		return;
                    	}
                    	if (totas.length > 0) {
                    		$scope.prdctvt1List = totas[0].body.prdctvt1List;
                    		$scope.cGroupTitle1List = totas[0].body.cGroupTitle1List;

							$scope.prdctvt1ListOutputVO = totas[0].body;
                    	};
    				}
    		);
    		
    		$scope.sendRecv("PMS201", "getPrdctvt2", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    		return;
                    	}
                    	if (totas.length > 0) {
                    		$scope.prdctvt2List = totas[0].body.prdctvt2List;
                    		$scope.cGroupTitle2List = totas[0].body.cGroupTitle2List;
                    		$scope.cGroupTitle3List = totas[0].body.cGroupTitle3List;
                    		angular.forEach($scope.prdctvt2List[1], function(row, index, objs){
                    			if(row != null){
                    				if('.' == row.substring(0,1)){
                    					$scope.prdctvt2List[1][index] = '0'+ row;
                    				}
                    			}
                			});
                    		
                    		
							$scope.prdctvt2ListOutputVO = totas[0].body;
                    	};
    				}
    		);
    		
    		$scope.sendRecv("PMS201", "getPrdctvt3", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    		return;
                    	}
                    	if (totas.length > 0) {
                    		$scope.prdctvt3List = totas[0].body.prdctvt3List;
                    		$scope.cGroupTitle4List = totas[0].body.cGroupTitle4List;

							$scope.prdctvt3ListOutputVO = totas[0].body;
                    	};
    				}
    		);
    		
    		// D GROUP
    		$scope.sendRecv("PMS201", "getActIndex", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    		return;
                    	}
                    	if (totas.length > 0) {
                    		$scope.actIndexList = totas[0].body.actIndexList;
                    		$scope.dGroupTitleList = totas[0].body.dGroupTitleList;

							$scope.actIndexListOutputVO = totas[0].body;
                    	};
    				}
    		);
    		
    		$scope.sendRecv("PMS201", "getTransCust", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    		return;
                    	}
                    	if (totas.length > 0) {
                    		$scope.transCustList = totas[0].body.transCustList;

							$scope.transCustListOutputVO = totas[0].body;
                    	};
    				}
    		);
    	}
    	
    	$scope.save = function () {
    		$scope.inputVO.empID = $scope.empDtl[0].EMP_ID
        	$scope.sendRecv("PMS201", "save", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsgInDialog(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.showMsg('儲存成功');
                    		$scope.query();
                    	};
    				}
    		);
    	}
    	
		//計算合併列數
		$scope.numGroupsA = function(input){
			if(input == null)
				return;
			var sum = 0;
			var ans = $filter('groupBy')(input, 'PROD_TYPE');
			sum += Object.keys(ans).length * 2;
			
            return sum;
        }
    	
    	$scope.getDtl = function (row, vipType) {
    		var eTime = $scope.inputVO.eTime;
    		var aoCode = $scope.inputVO.ao_code;
    		
    		var dialog = ngDialog.open({
				template: 'assets/txn/PMS201/PMS201_CUST.html',
				className: 'PMS201_CUST',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.vipType = vipType;
                	$scope.eTime = eTime;
                	$scope.ao_code = aoCode;
                }]
			}).closePromise.then(function (data) {
			});
    	}
    	
    	
    	
    	
    	  /****查詢  103 算出預估收益****/  
        $scope.inquire = function(){ 
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必輸條件(*)必須輸入!!');
        		return;
        	}	
        	$scope.paramList = [];
        	$scope.PLAN_AMT = 0;
        	$scope.EST_AMT = 0;
        	$scope.EST_EARNINGS = 0;
        	$scope.SALETARGET=0  //目標達成
//        	alert(1);
			$scope.sendRecv("PMS103", "queryData", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramLi = [];
//							alert(2);	
							$scope.paramLi = tota[0].body.resultList;  //結果
							$scope.SALETARGET = tota[0].body.tarList[0].SALETARGET;
							$scope.outputVO = tota[0].body;
							$scope.PLAN_AMT=0;
							$scope.EST_AMT=0;
							$scope.EST_EARNINGS=0;
							

							//金流金額
							for(var i = 0; i < $scope.paramLi.length; i++){
								$scope.EST_AMT += $scope.paramLi[i].EST_AMT;
						        $scope.EST_EARNINGS += $scope.paramLi[i].EST_EARNINGS;						      
							}
							return;
						}
			});	
        }
    }
);