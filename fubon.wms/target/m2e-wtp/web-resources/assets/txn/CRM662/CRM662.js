/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM662Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM662Controller";
		
		getParameter.XML(["CRM.FAMILY_DEGREE_AUM",'CRM.FAMILY_REL', 'CRM.REL_FAMILY_TYPE'],function(totas){
			if(totas){
				$scope.mappingSet['CRM.FAMILY_DEGREE_AUM'] = totas.data[0];
				$scope.mappingSet['CRM.FAMILY_REL'] = totas.data[1];
				$scope.mappingSet['CRM.REL_FAMILY_TYPE'] = totas.data[2];
			}
		});

		/** ----mappingSet設定----* */
		// 分行選單
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		// 理財會員等級
        var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(tota) {
        		if (tota[tota.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = tota[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        		}
        	});
        } else {
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        }
        
        // 關係類型:CRM.REL_TYPE=>顯示用(含本人) CRM.REL_TYPE_N00=>選單用(不含本人)
        var vo = {'param_type': 'CRM.REL_TYPE', 'desc': false};
        $scope.mappingSet['CRM.REL_TYPEN00'] = [];
        if(!projInfoService.mappingSet['CRM.REL_TYPE']) {
        	$scope.requestComboBox(vo, function(tota) {
        		if (tota[tota.length - 1].body.result === 'success') {
        			$scope.mappingSet['CRM.REL_TYPE'] = tota[0].body.result;
        			$scope.mappingSet['CRM.REL_TYPE_N00'] = tota[0].body.result.filter(function(obj){
        				return (obj.DATA != '00');
        	    	});
        		}
        	});
        } else {
        	$scope.mappingSet['CRM.REL_TYPE'] = projInfoService.mappingSet['CRM.REL_TYPE'];
        }

        /** ----初始化----* */
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
			$scope.inputVO.cust_name = $scope.custVO.CUST_NAME;
			$scope.inputVO.ao_code = $scope.custVO.AO_CODE;
			$scope.inputVO.rel_type = '';
			$scope.inputVO.rel_type_oth = '';
			$scope.inputVO.prv_mbr_no = '';
			$scope.inputVO.seq = '';
			$scope.inputVO.prv_list_length = '';
			$scope.inputVO.prv_nbr_yn = '';
			$scope.inputVO.prv_status = '';
			$scope.inputVO.prv_cust_id = '';
        	$scope.inputVO.prv_cust_name = '';
        	$scope.inputVO.prv_cust_aum = '';
        	$scope.inputVO.aum_total = 0;
        	$scope.inputVO.max_apl_ppl = 0;
        	$scope.inputVO.length = 0;
        	$scope.inputVO.reject_reason ='';
        	$scope.inputVO.vip_degree ='';
        	$scope.inputVO.aum_total_saved = 0;
		}
		$scope.init();
		
		// 權限
		$scope.pri = sysInfoService.getPriID()[0];
        $scope.ao = sysInfoService.getAoCode()[0] ? sysInfoService.getAoCode()[0] : '';
        
        if ($scope.ao != '' && $scope.ao != undefined) {
        	$scope.check_ao = true;
        } else {
        	$scope.check_ao = false;
        }
        
        if ($scope.pri == '009' || $scope.pri == '010' || $scope.pri == '011' ||
        	$scope.pri == '012' || $scope.pri == '013' || 
        	$scope.pri == 'UHRM012' || $scope.pri == 'UHRM013') {
        	$scope.check_rpy = true;
        } else {
        	$scope.check_rpy = false;
        }
        
		// 輸出欄初始化
		$scope.inquireInit = function(){
			$scope.mainList = [];
			$scope.resultList_prv = [];
			$scope.resultList_prv_add = [];
			$scope.resultList_prv_sort = [];
			$scope.addList_prv = [];
		}
		$scope.inquireInit();
		
		// 家庭戶初始查詢
		$scope.inquire = function() {
			$scope.VA_type = $scope.custVO.CRM661_VATYPE;
			$scope.sendRecv("CRM662", "inquire", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", {'cust_id':$scope.custVO.CUST_ID,'VA_type':$scope.VA_type},
				function(tota, isError) {	
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
				// =====================================================================================//
					// 判斷目前主戶是否有家庭戶成員
					// 若無則顯示自己
					if(tota[0].body.resultList_prv == null || tota[0].body.resultList_prv.length <= 0){
						$scope.resultList_prv = tota[0].body.resultList_main;
						if($scope.resultList_prv != null)
							$scope.inputVO.prv_list_length = $scope.resultList_prv.length;
						else
							$scope.inputVO.prv_list_length = 0;
					}else{
						$scope.resultList_prv = tota[0].body.resultList_prv;
						$scope.inputVO.prv_list_length = $scope.resultList_prv.length;
					}
				// =====================================================================================//
					// 總資產:會員清單+申請人數
					if($scope.resultList_prv != null) {
						for (var i = 0; i < $scope.resultList_prv.length; i++){
							$scope.inputVO.aum_total += $scope.resultList_prv[i].AUM_AMT;
						}
					}
					$scope.inputVO.aum_total_saved = $scope.inputVO.aum_total;
					
//					// 2017-11-15 by Jacky 還沒有新增從戶要計算主戶
//					if(tota[0].body.resultList_main != null){
//						$scope.resultList_main = tota[0].body.resultList_main;
//						if($scope.resultList_main.length > 1){
//							for (var i = 0; i < $scope.resultList_main.length; i++){
//								$scope.inputVO.aum_total += $scope.resultList_main[i].AUM_AMT;
//							}
//						}
//					}					
					// 可申請人數:總資產/15,000,000
					$scope.inputVO.max_apl_ppl = Math.floor($scope.inputVO.aum_total / 15000000);
				}
		)};
		$scope.inquire();
		
		
	    /** ----搜尋客戶彈跳視窗----* */
		// 防呆功能:避免多開彈跳視窗
	    $scope.flag = 0;
		$scope.open_prv = function(Flag, rowData, $index){
			// 只能在無該彈跳視窗的狀況下打開，打開後防呆功能啟動
			if (Flag !== $scope.flag) {
				$scope.flag = Flag;
				var cust_id = $scope.custVO.CUST_ID;
				var ao_code = $scope.inputVO.ao_code;
				var dialog = ngDialog.open({
					template: 'assets/txn/CRM662/CRM662_prv_add.html',
					className: 'CRM662_prv_add',
					showClose: false,
					controller: ['$scope', function($scope) {
						 $scope.cust_id = cust_id;
						 $scope.ao_code = ao_code;
		             }]
				});
				
				dialog.closePromise.then(function (list) {
					// 關閉彈跳視窗後，防呆功能關閉
					$scope.flag = 0;
					
					// 取消關閉
					if(list.value === 'cancel'){
						return;
		            }
					// 確定傳值
					else {
						/**CRM.VIP_DEGREE	H	0	1	恆富理財會員
						 * CRM.VIP_DEGREE	T	0	2	智富理財會員
						 * CRM.VIP_DEGREE	K	0	3	穩富理財會員
						 * **/
						var prv_vip_degree = $scope.resultList_prv[0].VIP_DEGREE == 'H' ? '恆富理財會員' : (
											 $scope.resultList_prv[0].VIP_DEGREE == 'T' ? '智富理財會員' : (
											 $scope.resultList_prv[0].VIP_DEGREE == 'K' ? '穩富理財會員' : ''));
						
						for(var i = 0; i < list.value.length; i++){
							if(list.value[i].CUST_ID != undefined && list.value[i].CUST_ID != ''){	
								
								var vip_degree = list.value[i].VIP_DEGREE == 'H' ? '恆富理財會員' : (
												 list.value[i].VIP_DEGREE == 'T' ? '智富理財會員' : (
												 list.value[i].VIP_DEGREE == 'K' ? '穩富理財會員' : ''));
								
								if (($scope.resultList_prv[0].VIP_DEGREE == 'T' && list.value[i].VIP_DEGREE == 'H') ||
									($scope.resultList_prv[0].VIP_DEGREE == 'K' && list.value[i].VIP_DEGREE == 'H') ||	
									($scope.resultList_prv[0].VIP_DEGREE == 'K' && list.value[i].VIP_DEGREE == 'T')) {
									var idx = i;
									$confirm({text:'該客戶'+list.value[i].CUST_ID + '為' + vip_degree + '等級，確定要新增成為' + prv_vip_degree + '嗎?'},{size:'sm'}).then(function(){
										// 只有一筆資料
										if(idx == 0) {
											doInsertRow(list, rowData, idx);
										}
										
										// 有多筆資料
										if(idx > 0){
											var row = {
												prv_cust_name : undefined,
												prv_cust_id   : undefined,
												rel_type_desc : undefined,
												rel_type      : undefined,
												prv_cust_aum  : undefined,
												vip_degree    : undefined,
												add_success   : "N",
												rel_status    : false,
												type          : true
											}
											doInsertRow(list, row, idx);	
											$scope.addList_prv.push(row);
										}
										
										var totalAmt = 0;
										for (var i = 0; i < $scope.addList_prv.length; i++){		
											totalAmt += $scope.addList_prv[i].prv_cust_aum;					
										}

										$scope.inputVO.aum_total = totalAmt + $scope.inputVO.aum_total_saved;
									});
								}else {
									// 只有一筆資料
									if(i == 0) {
										doInsertRow(list, rowData, i);
									}

									// 有多筆資料
									if(i > 0){
										var row = {
											prv_cust_name : undefined,
											prv_cust_id   : undefined,
											rel_type_desc : undefined,
											rel_type      : undefined,
											prv_cust_aum  : undefined,
											vip_degree    : undefined,
											add_success   : "N",
											rel_status    : false,
											type          : true
										}
										doInsertRow(list, row, i);	
										$scope.addList_prv.push(row);
									}
								}
							}else{
								for(var i = 0; i < $scope.addList_prv.length; i++){
									if(list.value[i].CUST_ID == undefined || list.value[i].CUST_ID == ''){
										if($scope.addList_prv[i].prv_cust_id == undefined || $scope.addList_prv[i].prv_cust_id == ""){
											if($index == i){
												$scope.addList_prv[i] = [];
												return;
											}
										}else{
											if($index == 0 && $index == i){
												if($scope.addList_prv.length > 1){
													$scope.addList_prv.splice($index,1);
													angular.forEach($scope.addList_prv,function(row,$index){
														row.Display_order = $index+1;
													})
													$scope.add_first_row();
													return;
												}else{
													$scope.addList_prv[i] = [];
													return; 
												}
											}else{
												$scope.addList_prv.splice($index,1);
												angular.forEach($scope.addList_prv,function(row,$index){
													row.Display_order = $index+1;
												})
												$scope.add_first_row();
												return;
											}
										}
									}
								}							
							}
						}
						
						var totalAmt = 0;
						for (var i = 0; i < $scope.addList_prv.length; i++){		
							totalAmt += $scope.addList_prv[i].prv_cust_aum;					
						}

						$scope.inputVO.aum_total = totalAmt + $scope.inputVO.aum_total_saved;
		            }
	            });
			}
	        return false;
		}		
		
		function doInsertRow(list, rowData, j){
	// =====================================================================================//
				// 檢核從彈跳視窗CRM662_prv_add.html帶回的值,是否已在畫面中
				// 若已存在，則不帶回此筆資料,反之則帶回
				for(var i = 0; i < $scope.resultList_prv.length; i++){
					if(list.value[j].CUST_ID == $scope.resultList_prv[i].CUST_ID){								
						$scope.showMsg("此筆資料已經加入，請重新選擇");								
						return;						
					}
				}
				
				// 避免待新增區塊一次增加2筆以上(含)的相同資料
				for(var i = 0; i < $scope.addList_prv.length; i++){
					if(list.value[j].CUST_ID == $scope.addList_prv[i].prv_cust_id){
						$scope.showMsg("此筆資料已加入待新增區塊，請重新選擇");								
						return;
					}
				}
			// =====================================================================================//
				
				// 排序 = 目前排序最大值+1
				$scope.maxSort = _.maxBy($scope.resultList_prv, 'PRV_MBR_NO') ? _.maxBy($scope.resultList_prv, 'PRV_MBR_NO').PRV_MBR_NO + 1 : 1;
				$scope.inputVO.prv_mbr_no = $scope.maxSort;
				
				
				rowData.prv_cust_id = list.value[j].CUST_ID;
				rowData.prv_cust_name = list.value[j].CUST_NAME;
				rowData.rel_type_desc = list.value[j].REL_TYPE_DESC;
				rowData.rel_type = list.value[j].REL_TYPE;
				rowData.rel_status = rowData.rel_type ? false : true;
				rowData.prv_cust_aum = list.value[j].AUM_AMT;
				rowData.add_success = "Y";
			// =====================================================================================//
				// 重新加總aum_total
			
//				for (var i = 0; i < $scope.addList_prv.length; i++){			
//						$scope.inputVO.aum_total = $scope.inputVO.aum_total + list.value[j].AUM_AMT;					
//				}

			// =====================================================================================//
				$scope.inputVO.max_apl_ppl = Math.floor($scope.inputVO.aum_total / 15000000);
				rowData.vip_degree = list.value[j].VIP_DEGREE;
				if('SEQ' in list.value){
					$scope.inputVO.seq = list.value[j].SEQ;
				}
				if('REL_TYPE' in list.value){
					rowData.rel_type_oth = list.value[j].REL_TYPE;
				}else {
					rowData.rel_type_oth = '';
				}
				if('REL_TYPE_OTH' in list.value){
					rowData.rel_type_oth = list.value[j].REL_TYPE_OTH;
				}else {
					rowData.rel_type_oth = '';
				}
				// 畫面(List)的最後一筆，顯示"+"的圖示
				if(j == list.value.length - 1){
					rowData.type = false;
				}else{
					rowData.type = true;
				}
				return;
		}
		
		/** ----變更排序----* */
		$scope.prv_sort = function() {			
			/** 新排序的數字不能超出原排序的數字範圍* */
			// 篩選 排除條件:新序號空值、原序號空值
			var ans = $scope.resultList_prv.filter(function(obj){
	    		return (obj.PRV_MBR_NO_NEW != '' && obj.PRV_MBR_NO_NEW != null);
	    	});
			
			// 新排序必須全部輸入(EX:原1234，新31_4)
			if(ans.length != ($scope.resultList_prv.length - 1)){
				$scope.showMsg("新排序沒有全部輸入");
				return;
			}
			
			// 排序比對用
			var OLD = []; 
			var NEW = [];
			angular.forEach($scope.resultList_prv, function(row, index, objs){
				if(row.PRV_MBR_NO != 0){
					OLD.push(row.PRV_MBR_NO);
					NEW.push(Number(row.PRV_MBR_NO_NEW));
				}
			});
			// 將新排序設定由小至大排好，必須跟原排序相同(EX: 原1234，新3412 => 1234，通過)
			NEW = _.sortBy(NEW);
			if(JSON.stringify(OLD) != JSON.stringify(NEW)){
				$scope.showMsg("新排序數字僅限於已有的原排序數字");
				return;
			}
			var M_SEQ = $scope.resultList_prv[0].SEQ;
			$scope.sendRecv("CRM662", "prv_sort", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", {'prv_sort_list': ans,'seq':M_SEQ} ,
	    			function(tota, isError) {
	        			if (isError) {
	        				$scope.showErrorMsgInDialog(tota.body.msgData);
	        				return;
		                }
		                if (tota.length > 0) {
		                	$scope.showMsg('排序申請成功，待主管覆核');
		                	$scope.init();
		                	$scope.inquireInit();
		                	$scope.inquire();
		                };
			});
		}

		
		$scope.add_first_row = function(){
			var row = {
					prv_cust_name : undefined,
					prv_cust_id   : undefined,
					rel_type_desc : undefined,
					rel_type      : undefined,
					prv_cust_aum  : undefined,
					vip_degree    : undefined,
					add_success   : "N",
					rel_status    : false,
					type          : false
				}
			$scope.addList_prv.push(row);
		}
		$scope.add_first_row();
		// 新增一筆資料
		$scope.add_row = function(rowData, $index){
			if (!rowData.prv_cust_id) {
				$scope.showErrorMsgInDialog('請選取成員客戶ID');
				return;
			}		
			rowData.type = true;
			$scope.add_first_row();
		}
		
		/** ----新增家庭戶----* */
		$scope.prv_add = function() {
			// 必須選擇一位關係戶成員才可以加入家庭戶
			if(!$scope.addList_prv[0].prv_cust_id){
				$scope.showErrorMsgInDialog('請選取成員客戶ID');
				return;				
			}

			for(var i = 0; i < $scope.addList_prv.length; i++){
				if(($scope.addList_prv[i].rel_type == undefined || $scope.addList_prv[i].rel_type == "") 
						&& ($scope.addList_prv[i].prv_cust_id != undefined) ){
					var id = $scope.addList_prv[i].prv_cust_id;
					var name = $scope.addList_prv[i].prv_cust_name;
					$scope.showErrorMsgInDialog('請選取成員 ' + id + ' ' + name + '與主戶的關係');
					return;	
				}
			}
			
//			// 抓出FAMILY_DEGREE相對應的整戶門檻金額
//			var listDegreeAUM = $scope.mappingSet['CRM.FAMILY_DEGREE_AUM'];
//			
//			// 將抓出來的資料整理成一個map
//			// map = {'A':3000, 'V':1000}
//			var mapDegreeAUM = {};
//			for (var i = 0; i < listDegreeAUM.length; i++){
//				mapDegreeAUM[listDegreeAUM[i].DATA] = listDegreeAUM[i].LABEL;
//			}
//			
//			// 抓出相對應的DEGREE --> 'V' or 'A'
//			var degree = $scope.resultList_prv[0].VIP_DEGREE;
//			// 將DEGREE轉換成相對應的金額
//			var Aum = Number(mapDegreeAUM[degree]) * 10000;
//			// 錯誤類型 : 整戶金額門檻未達3000/1000萬
//			if($scope.inputVO.aum_total < Aum){
//				// WMS-CR-20241119-02_調整家庭會員資格檢核邏輯
//				$scope.inputVO.reject_reason = '家庭往來資產總額未達6,000萬/2,000萬/600萬';
////				$scope.inputVO.reject_reason = '整戶金額門檻未達6000萬/2000萬/600萬';
////				$scope.inputVO.reject_reason = '整戶金額門檻未達3000/1000萬';
//				$scope.showErrorMsgInDialog($scope.inputVO.reject_reason);
//				return;
//			}
			
			/**
			 * WMS-CR-20241119-02_調整家庭會員資格檢核邏輯
			 * 檢視整戶家庭會員最近12週，
			 * 所有成員『每週加總餘額』是否皆達對應家庭會員門檻(600萬/2,000萬/6,000萬)」ex.家庭戶每週AuM週餘額加總皆達600萬門檻，因此符合穩富家庭會員資格。
			 * **/
			$scope.sendRecv("CRM662", "checkFamilyAum", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", 
			{'add_list_prv': $scope.addList_prv, 'cust_id': $scope.inputVO.cust_id, 'vip_degree': $scope.resultList_prv[0].VIP_DEGREE} ,
			function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
                if (tota.length > 0) {
					if (tota[0].body.family_flag == 'N') {
						$scope.inputVO.reject_reason = '家庭往來資產總額未達6,000萬/2,000萬/600萬';
						$scope.showErrorMsgInDialog($scope.inputVO.reject_reason);
						return;
						
					} else {
						$scope.inputVO.vip_degree = $scope.resultList_prv[0].VIP_DEGREE;
						$scope.inputVO.familyAumList = tota[0].body.familyAumList;
						$scope.inputVO.add_list_prv = $scope.addList_prv;
						$scope.inputVO.prv_list = $scope.resultList_prv;
						// $scope.inputVO.prv_list_length = $scope.resultList_prv.length;
						$confirm({text: $filter('i18n')('確定新增家庭成員?')}, {size: 'sm'}).then(function() {
							$scope.sendRecv("CRM662", "prv_add", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", $scope.inputVO,
							function(tota, isError) {
								if (isError) {
									$scope.showErrorMsgInDialog(tota.body.msgData);
									return;
								}
								if (tota.length > 0) {								
									// 錯誤類型C:申請人只能歸屬一位主戶成為家庭會員
									if (tota[0].body.prv_add_err_type == 'C') {
										$scope.inputVO.reject_reason = '每一位客戶僅能歸屬一位主戶成為其家庭會員';
										$scope.showErrorMsgInDialog($scope.inputVO.reject_reason);
									}
									// 錯誤類型N:無錯誤，通過檢核
									else if (tota[0].body.prv_add_err_type == 'N') {
										$scope.showMsg('新增申請成功，待主管覆核');
										$scope.inputVO.reject_reason = '';
										$scope.init();
						                $scope.inquireInit();
						                $scope.inquire();
						                $scope.add_first_row();
									}
				                };
							});
						});
					}
                }
			});			
		}
		
		/** ----刪除家庭戶----* */
		$scope.prv_delete = function(row) {
			$confirm({text: '確定要刪除家庭戶?'}, {size: 'sm'}).then(function() {
				$scope.inputVO.prv_status = row.PRV_STATUS;
				$scope.inputVO.seq = row.SEQ;
				$scope.inputVO.prv_nbr_yn = row.PRV_MBR_YN;
				$scope.sendRecv("CRM662", "prv_delete", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								$scope.showErrorMsgInDialog(tota.body.msgData);
								return;
							}
							if (tota.length > 0) {
								if(tota[0].body.prv_delete == 'D'){
									$scope.showMsg('刪除成功');
								}else{
									$scope.showMsg('刪除申請成功，待主管覆核');
								}								
								$scope.init();
			                	$scope.inquireInit();
			                	$scope.inquire();	
			                	$scope.add_first_row();	
			                };
				});
			});			
		}
				
		/** ----主管覆核----* */
		$scope.open_prv_rpy = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM662/CRM662_prv_rpy_dialog.html',
				className: 'CRM662_prv_rpy_dialog',
				showClose: false
			});
			dialog.closePromise.then(function (data) {
				$scope.init();
                $scope.inquireInit();
                $scope.inquire();
            });
		}
		
		/** ----連客戶首頁----* */
		$scope.gohome = function(row) {
			$scope.sendRecv("CRM110", "inquire", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", {'cust_id':row},
					function(tota110, isError) {	
				if(tota110[0].body.resultList.length == 1) {
					var path = '';					
					$scope.CRM_CUSTVO = {
						CUST_ID   : tota110[0].body.resultList[0].CUST_ID,
						CUST_NAME : tota110[0].body.resultList[0].CUST_NAME
					}
					$scope.connector('set','CRM_CUSTVO',$scope.CRM_CUSTVO)
					path = "assets/txn/CRM610/CRM610_MAIN.html";					
					$scope.connector("set","CRM610URL",path);
					var dialog = ngDialog.open({
						template: 'assets/txn/CRM610/CRM610.html',
						className: 'CRM610',
						showClose: false
					});
					return;
				}			
			});
		}
});
		