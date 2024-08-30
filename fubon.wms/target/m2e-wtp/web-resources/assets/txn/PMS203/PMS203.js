/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS203Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS203Controller";
		$scope.paramList = [];
		//月報選單
		
		debugger
		var NowDate = new Date();
	    //修正前人BUG 問題單號:0004027  BY 20171201-Willis
	    NowDate.setMonth(NowDate.getMonth()+1); 
	    var strMon = '';
		$scope.mappingSet['timeE'] = [];
		
	    //資料日期區間限制為三年內資料
	    for(var i = 0; i < 36; i++){
	    	
	    	strMon = NowDate.getMonth()+1;
	    	//10月以下做文字處理，+0 在前面
	    	if(strMon < 10 ){
	    		strMon = '0' + strMon;
	    	}
	    	
	    	$scope.mappingSet['timeE'].push({
	    		LABEL: NowDate.getFullYear() + '/' + strMon,
	    		DATA : NowDate.getFullYear() + ''  + strMon
	    	}); 
	    	//每一筆減一個月，倒回去取三年內日期區間
	    	NowDate.setMonth(NowDate.getMonth()-1);
	    }
        
		/*** 可示範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
		$scope.dateChange = function(){
	        if($scope.inputVO.sCreDate != ''){
				debugger
	        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };
		/*** 可示範圍  JACKY共用版  END***/
		

	    
		//目標類型下拉選單
		$scope.mappingSet['target'] = [];
		$scope.mappingSet['target'].push(
				{LABEL:'理專職級設定', DATA:'SET'},
				{LABEL:'理專生產力目標', DATA:'TAR'},
				{LABEL:'分行投保計績收益目標', DATA:'INS'},
				{LABEL:'分行投保計績銷量目標', DATA:'SALE'}
//				{LABEL:'理專追蹤商品目標', DATA:'PRD'}
		);
  
        $scope.target = function(){
			
        	if($scope.inputVO.tgtType === 'SET'){
        		$scope.AVAIL_BRANCH = 'true';
        	}else{
        		$scope.AVAIL_BRANCH = false;
			}
        }
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened' + index] = true;
		};		
		
		
		$scope.inquireInit = function(){
			$scope.paramList = [];
			$scope.delList_set = [];
			$scope.delList_tar = [];
			$scope.delList_ins = [];
			$scope.delList_sale = [];
			$scope.delList_prd = [];
		}
		
		$scope.init = function(){			
			$scope.editflag=false;
			$scope.inputVO = {
					tgtType: 'SET',
					rc_id: '',
					op_id: '' ,
					ao_code:'',
					branch_nbr:'',
					region_center_id:'',
					branch_area_id:'',
					sCreDate:''
        	};
			$scope.inquireInit();			
		};
		$scope.init();
		
			
		/***開啟新增視窗***/
		$scope.edit = function(row, type){
			var reportDate = $scope.inputVO.reportDate;
			var templ, clzName = '';

			if(type == 'SET'){
				templ = 'assets/txn/PMS203/PMS203_SET_EDIT.html';
				clzName = 'PMS203_SET_EDIT';
			}
			else if(type == 'TAR'){
				templ = 'assets/txn/PMS203/PMS203_TAR_EDIT.html';
				clzName = 'PMS203_TAR_EDIT';
			}
			else if(type == 'INS'){
				templ = 'assets/txn/PMS203/PMS203_INS_EDIT.html';
				clzName = 'PMS203_INS_EDIT';
			}
			else if(type == 'SALE'){
				templ = 'assets/txn/PMS203/PMS203_SALE_EDIT.html';
				clzName = 'PMS203_SALE_EDIT';
			}
			else if(type == 'PRD'){
				templ = 'assets/txn/PMS203/PMS203_PRD_EDIT.html';
				clzName = 'PMS203_PRD_EDIT';
			}
			else
				return;

			var dialog = ngDialog.open({
				template: templ,
				className: clzName,					
                controller: ['$scope', function($scope) {
                	if(reportDate != undefined){
						$scope.ym = reportDate;
                	} else {
						$scope.ym =  retrieveDate();
                	}
                	$scope.row = row;                	
                }]
			});
			      
            dialog.closePromise.then(function (data) {
            	if(data.value === 'successful'){
					if(reportDate != undefined){
						$scope.ym = reportDate;
	                } else {
						$scope.ym =  retrieveDate();
						$scope.inputVO.sCreDate = retrieveDate();
						$scope.inputVO.reportDate = retrieveDate();
	                }
					$scope.inquireInit();
					$scope.query();
				}
            });
		};
		
		/***開啟修改視窗***/
		$scope.edit2 = function(row, type){
			debugger	
			var templ, clzName = '';
			var reportDate = $scope.inputVO.reportDate;

			if(type == 'SET'){
				templ = 'assets/txn/PMS203/PMS203_SET_EDIT.html';
				clzName = 'PMS203_SET_EDIT';
			}
			else if(type == 'TAR'){
				templ = 'assets/txn/PMS203/PMS203_TAR_EDIT.html';
				clzName = 'PMS203_TAR_EDIT';
			}
			else if(type == 'INS'){
				templ = 'assets/txn/PMS203/PMS203_INS_EDIT.html';
				clzName = 'PMS203_INS_EDIT';
			}
			else if(type == 'SALE'){
				templ = 'assets/txn/PMS203/PMS203_SALE_EDIT.html';
				clzName = 'PMS203_SALE_EDIT';
			}
			else if(type == 'PRD'){
				templ = 'assets/txn/PMS203/PMS203_PRD_EDIT.html';
				clzName = 'PMS203_PRD_EDIT';
			}
			
			var dialog = ngDialog.open({
				template: templ,
				className: clzName,					
				controller: ['$scope', function($scope) {
					debugger
	               	if(reportDate != undefined){
						$scope.ym = reportDate;
	                } else {
						$scope.ym =  retrieveDate();
	                }
	                
	                $scope.row = row;                	
				}]
			});

			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					debugger
					if(reportDate != undefined){
						$scope.ym = reportDate;
	                } else {
						$scope.ym =  retrieveDate();
						$scope.inputVO.sCreDate = retrieveDate();
						$scope.inputVO.reportDate = retrieveDate();
	                }

					$scope.inquireInit();
					$scope.query();
				}
			});		
		};

		//刪除勾選陣列
        Array.prototype.remove = function () {
        	var what, a = arguments, L = a.length;
        	what = a[--L];
    		for(var i = 0; i < this.length; i++) {	
    			if(JSON.stringify(this[i]) == JSON.stringify(what)){
    				this.splice(i, 1);
    				return this;
    			}
    		}     	
		};
		
        /**刪除資料勾選**/        
        $scope.delrow = function(row, type){
        	
        	//理專職級設定
			if(type == 'SET'){
				if(row.CHECK_BOX == 'Y'){
	        		$scope.delList_set.push({
	        			data_yearmon	:	row.DATA_YEARMON,
	        			ol_title		:	row.OL_TITLE,
	        			job_title_id	:	row.JOB_TITLE_ID});
	        	}else{
	        		$scope.delList_set.remove({
	        			data_yearmon	:	row.DATA_YEARMON,
	        			ol_title		:	row.OL_TITLE,
	        			job_title_id	:	row.JOB_TITLE_ID});
	        	}
			}

			//理專生產力目標
			else if(type == 'TAR'){
				if(row.CHECK_BOX == 'Y'){
	        		$scope.delList_tar.push({
	        			data_yearmon	:	row.DATA_YEARMON,
	        			region_center_id:	row.REGION_CENTER_ID,
	        			branch_area_id	:	row.BRANCH_AREA_ID,
	        			branch_nbr		:	row.BRANCH_NBR,
	        			emp_id			:	row.EMP_ID});
	        	}else{
	        		$scope.delList_tar.remove({
	        			data_yearmon	:	row.DATA_YEARMON,
	        			region_center_id:	row.REGION_CENTER_ID,
	        			branch_area_id	:	row.BRANCH_AREA_ID,
	        			branch_nbr		:	row.BRANCH_NBR,
	        			emp_id			:	row.EMP_ID});
	        	}
			}

			//分行投保計績收益目標
			else if(type == 'INS'){
				if(row.CHECK_BOX == 'Y'){
	        		$scope.delList_ins.push({
	        			data_yearmon	:	row.DATA_YEARMON,
	        			region_center_id:	row.REGION_CENTER_ID,
	        			branch_area_id	:	row.BRANCH_AREA_ID,
	        			branch_nbr		:	row.BRANCH_NBR});
	        	}else{
	        		$scope.delList_ins.remove({
	        			data_yearmon	:	row.DATA_YEARMON,
	        			region_center_id:	row.REGION_CENTER_ID,
	        			branch_area_id	:	row.BRANCH_AREA_ID,
	        			branch_nbr		:	row.BRANCH_NBR});
	        	}
			}

			//分行投保計績銷量目標
			else if(type == 'SALE'){
				if(row.CHECK_BOX == 'Y'){
	        		$scope.delList_sale.push({
	        			data_yearmon	:	row.DATA_YEARMON,
	        			region_center_id:	row.REGION_CENTER_ID,
	        			branch_area_id	:	row.BRANCH_AREA_ID,
	        			branch_nbr		:	row.BRANCH_NBR});
	        	}else{
	        		$scope.delList_sale.remove({
	        			data_yearmon	:	row.DATA_YEARMON,
	        			region_center_id:	row.REGION_CENTER_ID,
	        			branch_area_id	:	row.BRANCH_AREA_ID,
	        			branch_nbr		:	row.BRANCH_NBR});
	        	}
			}

			//理專追蹤商品目標
			else if(type == 'PRD'){
				if(row.CHECK_BOX == 'Y'){
	        		$scope.delList_prd.push({
	        			data_yearmon	:	row.DATA_YEARMON,
		        		region_center_id:	row.REGION_CENTER_ID,
		        		branch_area_id	:	row.BRANCH_AREA_ID,
		        		branch_nbr		:	row.BRANCH_NBR,
	        			emp_id			:	row.EMP_ID,
	        			main_prd_id		:	row.MAIN_PRD_ID});
	        	}else{
	        		$scope.delList_prd.remove({
		        		data_yearmon	:	row.DATA_YEARMON,
			        	region_center_id:	row.REGION_CENTER_ID,
			       		branch_area_id	:	row.BRANCH_AREA_ID,
			       		branch_nbr		:	row.BRANCH_NBR,
		        		emp_id			:	row.EMP_ID,
		       			main_prd_id		:	row.MAIN_PRD_ID});
	        	}
			}
			
        }
		
		
		
		/**刪除資料**/
		$scope.delData = function(row, type){
			var delTYPE = "";
			var delLIST = [];
	        //===================================================================================
			$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
				debugger

				//理專職級設定
				if(type == 'SET'){
					delTYPE = 'delSET';
					delLIST = $scope.delList_set;	
				}
				//理專生產力目標
				else if(type == 'TAR'){
					delTYPE = 'delTAR';
					delLIST = $scope.delList_tar;
				}
				//分行投保計績收益目標
				else if(type == 'INS'){
					delTYPE = 'delINS';
					delLIST = $scope.delList_ins;
				}
				//分行投保計績銷量目標
				else if(type == 'SALE'){
					delTYPE = 'delSALE';
					delLIST = $scope.delList_sale;
				}
				//理專追蹤商品目標
				else if(type == 'PRD'){
					delTYPE = 'delPRD';
					delLIST = $scope.delList_prd;
				}

				$scope.sendRecv("PMS203", delTYPE, "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", {'list':delLIST},
						function(totas, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					}
					if (totas.length > 0) {
						$scope.showSuccessMsg('刪除成功');
						$scope.inquireInit();							
						$scope.query();
					};
				});					
				
			});
			//===================================================================================
			
		};


		///=========================
		$scope.delTAR = function(row){      					
					$scope.delList = [];
					$scope.delList.push(
						{
		        			data_yearmon	:	row.DATA_YEARMON,
		        			region_center_id:	row.REGION_CENTER_ID,
		        			branch_area_id	:	row.BRANCH_AREA_ID,
		        			branch_nbr		:	row.BRANCH_NBR,
		        			emp_id			:	row.EMP_ID
	        			}
	        		);
					
					$scope.inputVO.delTARList = $scope.delList;
					
					debugger
				    $confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
	               	$scope.sendRecv("PMS203","delTAR","com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
	               		function(tota,isError){
	            			if(isError){
	            				$scope.showErrorMsg(tota[0].body.msgData);
	            			}
	                       	if (tota.length > 0) {
								$scope.showSuccessMsg('刪除成功');
								$scope.inquireInit();							
								$scope.query();
	                    	};
	            		});
					});	
    			};

		///=========================
		
		/**查詢資料**/
		$scope.query = function(){
			debugger
			if ($scope.inputVO.sCreDate == '') {
				$scope.showErrorMsgInDialog('查詢功能，欄位檢核錯誤：資料統計月份為必要輸入欄位');
				return;
			}

			if ($scope.inputVO.tgtType == '') {
				$scope.showErrorMsgInDialog('查詢功能，欄位檢核錯誤：目標類型為必要輸入欄位');
				return;
			}

			if($scope.inputVO.tgtType == 'TAR'){
				$scope.rsTitle = '理專生產力目標';
				$scope.tbTitle = 'TAR';
			}
        	else if($scope.inputVO.tgtType == 'INS'){
        		$scope.rsTitle = '分行投保計績收益目標';
        		$scope.tbTitle = 'INS';
        	}
        	else if($scope.inputVO.tgtType == 'SALE'){
        		$scope.rsTitle = '分行投保計績銷量目標';
        		$scope.tbTitle = 'SALE';
        	}
        	else if($scope.inputVO.tgtType == 'SET'){
        		$scope.rsTitle = '理專職級設定';
        		$scope.tbTitle = 'SET';
        	}
        	else if($scope.inputVO.tgtType == 'PRD'){
        		$scope.rsTitle = '理專追蹤商品目標';
        		$scope.tbTitle = 'PRD';
        	}
        	else{
//        		$scope.rsTitle = '查詢結果';
        		$scope.tbTitle = '';
        	}        		
			
			//表格TITLE顯示查詢年月
			var dt = $scope.inputVO.reportDate;
			if(dt != '')
				$scope.rsDate = dt.substr(0, 4) + '/' + dt.substr(4,2);
			//--------------
			$scope.sendRecv("PMS203", "queryData", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
					function(tota, isError) {
						debugger
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;
							$scope.outputVO.targetType = $scope.inputVO.tgtType;
							
					        var NowDate4 = new Date();
							var yr4 = NowDate4.getFullYear();
					        var mm4 = NowDate4.getMonth()+1;
					        var strmm4 = mm4 < 10 ? ('0' + mm4) : mm4;
					        var ym4 = yr4.toString() + strmm4.toString();
							var reportDate = $scope.inputVO.reportDate;
							
							if(reportDate == ym4) {
								$scope.editflag=true;
							}else{
								$scope.editflag=false;
							}

							return;
						}						
			});
		};
		
		/**目標維護更新**/
		$scope.runJob = function(){
			if($scope.inputVO.sCreDate == undefined || $scope.inputVO.sCreDate == ''){
				 $scope.showMsg("查詢月份為必填欄位");	 
				return; 
			}
			$scope.sendRecv("PMS203", "runJob","com.systex.jbranch.app.server.fps.pms203.PMS203InputVO",$scope.inputVO, function(tota, isError) {
				   if (!isError) {					
						$scope.showMsg("資料已更新,請重新查詢");
						return;
					}
			});
		};
			
		/**匯出EXCEL**/
		$scope.exportRPT = function(){			
			$scope.sendRecv("PMS203", "export", "com.systex.jbranch.app.server.fps.pms203.PMS203OutputVO", $scope.outputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};
		
		/**資料整批匯入上傳**/
		$scope.upload = function(type, ym){
			var reportDate = $scope.inputVO.reportDate;
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS203/PMS203_UPLOAD.html',
				className: 'PMS203_UPLOAD',				
                controller: ['$scope', function($scope) {
					if(reportDate != undefined){
						$scope.ym = reportDate;
	                } else {
						$scope.ym = retrieveDate();
	                }          	
                	$scope.tp = type;
                }]
			});

			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					debugger
					if($scope.inputVO.sCreDate == undefined 
						|| $scope.inputVO.sCreDate == ''){
							$scope.inputVO.sCreDate = retrieveDate();
							$scope.inputVO.reportDate = retrieveDate();
					}
					$scope.inquireInit();
					$scope.query();
				}
			});	

		};

		function retrieveDate(){
			debugger
			var date = new Date();
			var yy = date.getFullYear();
			var mm = date.getMonth() + 1;
			var strmm = '';

			if(mm < 10)
				strmm = '0' + mm;
			else
				strmm = mm; 
			return yy.toString() + strmm.toString();
		}
		
});
