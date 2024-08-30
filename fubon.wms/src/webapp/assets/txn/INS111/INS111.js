/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS111Controller',
						function($rootScope, $confirm, $scope,$filter, $controller, socketService, ngDialog, projInfoService,sysInfoService,getParameter,$timeout,$http) {
									$controller('BaseController', {$scope : $scope});
									$scope.controllerName = "INS111Controller";
									
//									TITLE_Y – 繳費年期的動態欄位名稱
//						            TITLE_A – 保障年期的動態欄位名稱
//						            TITLE_O – 投保對象的動態欄位名稱
//						            TITLE_K – 種類的動態欄位名稱
									// label
									$scope.labelTITLE_Y = '';
									$scope.labelTITLE_A = '';
									$scope.labelTITLE_O = '';
									$scope.labelTITLE_K = '';
									
									// 有效起始日期
									$scope.bgn_sDateOptions = {
										maxDate: new Date(),
										minDate: $scope.minDate
									};
									
									// config
									$scope.model = {};
									$scope.open = function($event, elementOpened) {
										var textMsg = '變更保單生效日時，將清空當前所有附約的保單生效日';
										$scope.toChangeEFFECTED_DATE(textMsg, $event, elementOpened)
									};
									
									// 變更保單生效日時提示:將會清空當前所有附約的保單生效日
									$scope.toChangeEFFECTED_DATE = function(textMsg, $event, elementOpened){
										if($scope.inputVO.IS_MAIN_NList.length != 0) {
											$confirm({text:textMsg},{size:'sm'}).then(function(){
												$event.preventDefault();
												$event.stopPropagation();
												$scope.model[elementOpened] = !$scope.model[elementOpened];
//												debugger
												for(var i=0; i<$scope.inputVO.IS_MAIN_NList.length;i++) {
													$scope.inputVO.IS_MAIN_NList[i].EFFECTED_DATE = undefined;
												}
											});
										} else {
											$event.preventDefault();
											$event.stopPropagation();
											$scope.model[elementOpened] = !$scope.model[elementOpened];
										}
										
					
									}
						
							        function getAge(dateString) {
							            var today = new Date();
							            var birthDate = new Date(dateString);
							            var age = today.getFullYear() - birthDate.getFullYear();
							            var m = today.getMonth() - birthDate.getMonth();
							            if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
							                age--;
							            }
							            return age;
							        }
									
							        //要保書類型/戶況檢核
									getParameter.XML(["IOT.CM_FLAG",'INS.IS_MAIN','COMMON.YES_NO'],function(totas){
										if(totas){
											//戶況檢核
											$scope.mappingSet['IOT.CM_FLAG'] = totas.data[0];
											$scope.mappingSet['INS.IS_MAIN'] = totas.data[totas.key.indexOf('INS.IS_MAIN')];//主附約
											$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
										}
									});
							        
							        
									/**initialize**/
									$scope.init = function() {
										$scope.inputVO = {
												INSURED_ID:undefined,
												GENDER:'',
												POLICY_NBR:'',
												COM_ID:'',
												PRD_NAME:'',
												PRD_ID:'',
												IS_MAIN:'Y',
												INSYEARFEE:'',
												BENEFICIARY_YN:'',
												IS_MAIN_NList:[]
										}
										
										$scope.COM_ID_temp = $scope.inputVO.COM_ID;
										$scope.tempPRD_ID = $scope.inputVO.PRD_ID;
										$scope.tempPRD_NAME = $scope.inputVO.PRD_NAME;
										//保險公司
										$scope.COMList = []
										$scope.sendRecv("INS810","queryCOM_ID","com.systex.jbranch.app.server.fps.ins810.INS810InputVO",
												$scope.inputVO,function(tota,isError){
											$scope.COMList = tota[0].body.COMList;
											
											// 只有一筆請帶預設第一筆
											$scope.setDefaultFirst($scope.inputVO.COM_ID, $scope.COMList);

											if($scope.INSURED_ID && $scope.type == 'new'){
												$scope.inputVO.INSURED_ID = $scope.INSURED_ID;
												$scope.getInfo('INSURED');
											}else if($scope.INSSEQ != undefined && $scope.type == 'update'){
												$scope.inputVO.INSSEQ = $scope.INSSEQ;
												$scope.queryData();
											}
										});
									}
									$scope.init();//initialize variable
									
									
								//根據欄位帶出分行代碼、招攬人員姓名、要保人姓名、留存分行地址	
								$scope.getInfo = function(in_column){
									$scope.inputVO.in_column = in_column;
									if($scope.inputVO.in_column == 'INSURED'){
										if($scope.inputVO.INSURED_ID){
											$scope.inputVO.INSURED_ID = $scope.inputVO.INSURED_ID.toUpperCase();
											$scope.get_FORMList("ADD");
										} else {
											$scope.inputVO.INSURED_ID = undefined;
											$scope.inputVO.INSURED_NAME = undefined;
											$scope.inputVO.INSURED_BIRTHDAY = undefined;
											return;
										}
										$scope.inputVO.INSURED_NAME = '';
										$scope.inputVO.INSURED_CM_FLAG = '';
									}
									$scope.sendRecv("INS111","getCustInfo","com.systex.jbranch.app.server.fps.ins111.INS111InputVO",
				        					$scope.inputVO,function(tota,isError){
										if(!isError){
											debugger
											if(tota[0].body != null && tota[0].body.length>0){
												if($scope.inputVO.INSURED_ID !=''){
													$scope.inputVO.INSURED_NAME = tota[0].body[0].CUST_NAME.replace(/\s/g,'');
													$scope.inputVO.INSURED_BIRTHDAY = Date.parse(tota[0].body[0].BIRTH_DATE);
//														$scope.inputVO.INSURED_CM_FLAG = tota[0].body.INSURED_NAME[0].INSURED_CM_FLAG;
													$scope.inputVO.GENDER = tota[0].body[0].GENDER;
													$scope.bgn_sDateOptions.minDate = new Date($scope.inputVO.INSURED_BIRTHDAY);
												}
											} else {
												$scope.inputVO.INSURED_NAME = undefined;
												$scope.inputVO.INSURED_BIRTHDAY = undefined;
												$scope.showErrorMsg('不存在客戶 ' + $scope.inputVO.INSURED_ID + " 的相關資料" );
											}
										}else{
											if($scope.inputVO.in_column == 'INSURED'){
												$scope.inputVO.INSURED_ID = undefined;
											}
										}
									});
								}
								
								//update進入時查詢
								$scope.queryData = function(){
									$scope.sendRecv("INS111","queryData","com.systex.jbranch.app.server.fps.ins111.INS111InputVO",
				        					$scope.inputVO,function(tota,isError){
										if(!isError){
											$scope.inputVO.IS_MAIN_NList = tota[0].body.IS_MAIN_NList;
											$scope.inputVO.KEYNO = tota[0].body.IS_MAIN_YList[0].KEYNO;//行外保單序號
											$scope.inputVO.INSURED_ID = tota[0].body.IS_MAIN_YList[0].INSURED_ID.toUpperCase();//被保險人ID
											$scope.inputVO.INSURED_NAME = tota[0].body.IS_MAIN_YList[0].INSURED_NAME;//被保險人姓名
											$scope.inputVO.INSURED_BIRTHDAY = Date.parse(tota[0].body.IS_MAIN_YList[0].INSURED_BIRTHDAY);//生日
											$scope.inputVO.POLICY_NBR = tota[0].body.IS_MAIN_YList[0].POLICY_NBR;//保單號碼
											$scope.get_FORMList("EDIT");
											$scope.inputVO.PRD_KEY_NO = tota[0].body.IS_MAIN_YList[0].PRD_KEYNO;
											$scope.inputVO.COM_ID = tota[0].body.IS_MAIN_YList[0].COM_ID;//保險公司
											$scope.inputVO.PRD_ID = tota[0].body.IS_MAIN_YList[0].PRD_ID;//險種代碼
											if($scope.inputVO.PRD_ID != ''){
												$scope.getDrop_down();
											}
											$scope.inputVO.PRD_NAME = tota[0].body.IS_MAIN_YList[0].PRD_NAME;//商品名稱
											$scope.inputVO.EFFECTED_DATE = Date.parse(tota[0].body.IS_MAIN_YList[0].EFFECTED_DATE);//保單生效日
											$scope.inputVO.PAYMENTYEAR_SEL = tota[0].body.IS_MAIN_YList[0].PAYMENTYEAR_SEL;//繳費年期
											$scope.inputVO.CURR_CD = tota[0].body.IS_MAIN_YList[0].CURR_CD;//幣別
											$scope.inputVO.PAY_TYPE = tota[0].body.IS_MAIN_YList[0].PAYTYPE;//繳別
											$scope.inputVO.INSURED_OBJECT = tota[0].body.IS_MAIN_YList[0].INSURED_OBJECT;//保險對象
											$scope.inputVO.KIND_SEL = tota[0].body.IS_MAIN_YList[0].KIND_SEL;//種類
											$scope.inputVO.INSYEARFEE = tota[0].body.IS_MAIN_YList[0].INSYEARFEE//保費(原幣)
											$scope.COVERCACULUNITDESC = tota[0].body.IS_MAIN_YList[0].COVERCACULUNITDESC;//保障計算單位(文字)
											$scope.inputVO.UPQTY_SEL = tota[0].body.IS_MAIN_YList[0].UPQTY_SEL;//單位或計畫數
											$scope.inputVO.INSUREDAMT = tota[0].body.IS_MAIN_YList[0].INSUREDAMT;//原幣保險金額(保額)											
											$scope.inputVO.COVERYEAR_SEL = tota[0].body.IS_MAIN_YList[0].COVERYEAR_SEL;
											$scope.inputVO.BENEFICIARY_YN = tota[0].body.IS_MAIN_YList[0].BENEFICIARY_YN;//要保人是否為身故受益人
											$scope.COM_ID_temp = $scope.inputVO.COM_ID;
											$scope.tempPRD_ID = $scope.inputVO.PRD_ID;
											$scope.tempPRD_NAME = $scope.inputVO.PRD_NAME;
											
											$scope.labelTITLE_Y = tota[0].body.IS_MAIN_YList[0].TITLE_Y == null ? '' : tota[0].body.IS_MAIN_YList[0].TITLE_Y;
											$scope.labelTITLE_A = tota[0].body.IS_MAIN_YList[0].TITLE_A == null ? '' : tota[0].body.IS_MAIN_YList[0].TITLE_A;
											$scope.labelTITLE_O = tota[0].body.IS_MAIN_YList[0].TITLE_O == null ? '' : tota[0].body.IS_MAIN_YList[0].TITLE_O;
											$scope.labelTITLE_K = tota[0].body.IS_MAIN_YList[0].TITLE_K == null ? '' : tota[0].body.IS_MAIN_YList[0].TITLE_K;
											
//											$scope.sendRecv("INS111","get_INSDATA_PROD","com.systex.jbranch.app.server.fps.ins111.INS111InputVO",
//												$scope.inputVO,function(tota,isError){
//												if(!isError) {
//												}
//											});
											
										}
									});
								}
						
								//取得保險單號 sen 2018/01/22
								$scope.get_FORMList = function(doType) {
									$scope.inputVO.custId = $scope.inputVO.INSURED_ID;
									$scope.inputVO.policyNbr = $scope.inputVO.POLICY_NBR;
									$scope.inputVO.isFilter = true;
									$scope.sendRecv("INS100","queryPolicyData","com.systex.jbranch.app.server.fps.ins100.INS100InputVO",
										$scope.inputVO,function(tota,isError){
										// 自己組 Map DATA-LABEL
										$scope.FORMList = [];
										angular.forEach(tota[0].body.policyList, function(row){
											$scope.FORMList.push({ LABEL: row.POLICY_NBR, DATA: row.POLICY_NBR});									 
										});
										if("ADD" === doType){
											// 只有一筆請帶預設第一筆
											$scope.setDefaultFirst("POLICY_NBR", $scope.FORMList);
										}
									});
								}
								
								//清除保險相關欄位
								$scope.clear_Prduct_Data = function(){
									$scope.inputVO.PRD_KEY_NO = '';
									$scope.inputVO.PRD_NAME = '';
									$scope.tempPRD_NAME = '';
									$scope.inputVO.PRD_ID = '';
									$scope.tempPRD_ID = '';
									$scope.CURR_CDList = [];
									$scope.inputVO.CUUR_CD = '';
									$scope.PAYMENTYEAR_SELList = [];
									$scope.inputVO.PAYMENTYEAR_SEL = '';
									$scope.inputVO.COVERYEAR_SEL = '';
									$scope.inputVO.EFFECTED_DATE = undefined;
									$scope.inputVO.PAY_TYPE = '';
									$scope.INSURED_OBJECTList = [];
									$scope.inputVO.INSURED_OBJECT = '';
									$scope.KIND_SELList = [];
									$scope.inputVO.KIND_SEL = '';
									$scope.QUANTITY_STYLEList = undefined;
									$scope.QUANTITY_STYLE = null;
									$scope.COVERCACULUNITDESC = '';
									$scope.inputVO.UPTYPE = '';
									$scope.inputVO.UPQTY_SEL = '';
									$scope.inputVO.INSUREDAMT = '';
									$scope.inputVO.BENEFICIARY_YN = '';
									
									
									$scope.labelTITLE_Y = '';
									$scope.labelTITLE_A = '';
									$scope.labelTITLE_O = '';
									$scope.labelTITLE_K = '';
								}
								
								//變動保險公司確認跳窗
								$scope.clear_COM_Data = function(){
									var textMsg = "變動保險公司會造成主附約資料重置"
										$confirm({text:textMsg},{size:'sm'}).then(function(){
											
											$scope.clear_Prduct_Data();
											
											if($scope.inputVO.IS_MAIN_NList.length>0){
												$scope.inputVO.IS_MAIN_NList = [];
											}
											$scope.COM_ID_temp = $scope.inputVO.COM_ID;
											$scope.labelTITLE_Y = '';
											$scope.labelTITLE_A = '';
											$scope.labelTITLE_O = '';
											$scope.labelTITLE_K = '';
											$scope.IS_WL = '';
										});
								}
								
								//保險公司變動
								$scope.change_COM_ID = function(){
									//一開始請選擇時
									if($scope.COM_ID_temp == ''){
										$scope.COM_ID_temp = $scope.inputVO.COM_ID;
									}else if($scope.inputVO.COM_ID == ''){//
										$scope.clear_COM_Data();
									}else{
										if($scope.COM_ID_temp != $scope.inputVO.COM_ID){
											$scope.clear_COM_Data();
										}
									}
								}
								
								//商品查詢(主約)
								$scope.getProduc = function(IS_MAIN_TYPE){
									if($scope.inputVO.COM_ID == ''){
										$scope.showErrorMsg('ehl_01_INS112_001');
									}else{
										var COM_ID = $scope.inputVO.COM_ID;
										var PRD_NAME = '';
										var PRD_ID = '';
										
										console.log($scope.inputVO.PRD_NAME);
										console.log($scope.inputVO.PRD_ID);
										
										if($scope.inputVO.PRD_NAME){
											PRD_NAME = $scope.inputVO.PRD_NAME.toUpperCase();
										}
										if($scope.inputVO.PRD_ID){
											PRD_ID = $scope.inputVO.PRD_ID.toUpperCase();
										}

										var dialog = ngDialog.open({
											template: 'assets/txn/INS112/INS112.html',
											className: 'INS112',
											controller:['$scope',function($scope){
												$scope.COM_ID = COM_ID;
												$scope.PRD_ID = PRD_ID;
												$scope.PRD_NAME = PRD_NAME;
												$scope.IS_MAIN_TYPE = IS_MAIN_TYPE;
											}]
										});
										
										dialog.closePromise.then(function(data){
											if(data.value != undefined){
												if(data.value != 'cancel'){
													$scope.inputVO.IS_MAIN_TYPE = IS_MAIN_TYPE;
													//存入資料庫的PRD_ID
													$scope.inputVO.PRD_KEY_NO = data.value.KEY_NO;
													$scope.inputVO.PRD_NAME = data.value.PRD_NAME;
													//商品名稱暫存
													$scope.tempPRD_NAME = $scope.inputVO.PRD_NAME;
													$scope.inputVO.PRD_ID = data.value.PRD_ID;
													//險種代碼暫存
													$scope.tempPRD_ID = $scope.inputVO.PRD_ID;
													$scope.getDrop_down();
																									
													// 動態顯示欄位名稱
													$scope.labelTITLE_Y = '';
													$scope.labelTITLE_A = '';
													$scope.labelTITLE_O = '';
													$scope.labelTITLE_K = '';
													
													if(data.value.TITLE_Y != null){
														$scope.labelTITLE_Y = data.value.TITLE_Y;
													}
													
													if(data.value.TITLE_A != null){
														$scope.labelTITLE_A = data.value.TITLE_A;
													}
													
													if(data.value.TITLE_O !=null){
														$scope.labelTITLE_O = data.value.TITLE_O;
													}
													
													if(data.value.TITLE_K != null){
														$scope.labelTITLE_K = data.value.TITLE_K;
													}
													
												}
											}
										});
									}
								}
								
								//取得下拉選單
								$scope.getDrop_down = function(){
									$scope.CURR_CDList = [];
									$scope.PAYMENTYEAR_SELList = [];
									$scope.COVERYEAR_SELList = [];
									$scope.INSURED_OBJECTList = [];
									$scope.KIND_SELList = [];
									$scope.QUANTITY_STYLEList = [];
									$scope.QUANTITY_STYLE = '';
									$scope.COVERCACULUNITDESC = '';
									$scope.IS_WL = '';
									$scope.WL_TERM = '';
									$scope.inputVO.UPTYPE = '';
									$scope.inputVO.UPQTY_SEL = '';
									$scope.inputVO.INSUREDAMT = '';
									$scope.show_PAYMENTYEAR_SEL = false;
									$scope.show_COVERYEAR_SEL = false;
									$scope.show_INSURED_OBJECT = false;
									$scope.show_KIND_SEL = false;
									
									$scope.inputVO.CURR_CD = '';
									$scope.inputVO.PAYMENTYEAR_SEL = '';
									$scope.inputVO.COVERYEAR_SEL = '';
									$scope.inputVO.INSURED_OBJECT = '';
									$scope.inputVO.KIND_SEL = '';
									//用來與躉繳連動用
									$scope.inputVO.PAY_TYPE = '';
									
									$scope.sendRecv("INS111","getDrop_down","com.systex.jbranch.app.server.fps.ins111.INS111InputVO",
											$scope.inputVO,function(tota,isError){
										if(tota[0].body.CURR_CDList){
											$scope.CURR_CDList = tota[0].body.CURR_CDList;
											// 幣別 只有一筆請帶預設第一筆
											$scope.setDefaultFirst("CURR_CD", $scope.CURR_CDList);
										}
										if(tota[0].body.PAYMENTYEAR_SELList){
											$scope.PAYMENTYEAR_SELList = tota[0].body.PAYMENTYEAR_SELList;
											// 繳費年期 只有一筆請帶預設第一筆
											$scope.setDefaultFirst("PAYMENTYEAR_SEL", $scope.PAYMENTYEAR_SELList);
										}
										if(tota[0].body.COVERYEAR_SELList){
											$scope.COVERYEAR_SELList = tota[0].body.COVERYEAR_SELList;
											// 保障年期 只有一筆請帶預設第一筆
											$scope.setDefaultFirst("COVERYEAR_SEL", $scope.COVERYEAR_SELList);
										}
										if(tota[0].body.INSURED_OBJECTList){
											$scope.INSURED_OBJECTList = tota[0].body.INSURED_OBJECTList;
											// 保險對象 只有一筆請帶預設第一筆
											$scope.setDefaultFirst("INSURED_OBJECT", $scope.INSURED_OBJECTList);
										}
										if(tota[0].body.KIND_SELList){
											$scope.KIND_SELList = tota[0].body.KIND_SELList;
											// 種類 只有一筆請帶預設第一筆
											$scope.setDefaultFirst("KIND_SEL", $scope.KIND_SELList);
										}
										if (tota[0].body.QUANTITY_STYLEList) {
											$scope.QUANTITY_STYLEList = tota[0].body.QUANTITY_STYLEList;
											// 保額 只有一筆請帶預設第一筆
											$scope.setDefaultFirst("UPQTY_SEL", $scope.QUANTITY_STYLEList);
										}
																														
										if ($scope.QUANTITY_STYLEList.length > 1 ) {
											$scope.inputVO.UPTYPE = '02';
										}
										
										$scope.QUANTITY_STYLE = tota[0].body.QUANTITY_STYLE;
										if (null != $scope.QUANTITY_STYLE) {
											$scope.inputVO.UPTYPE = '01';
										}
										$scope.COVERCACULUNITDESC = tota[0].body.COVERCACULUNITDESC;
										$scope.IS_WL = tota[0].body.IS_WL;
										$scope.WL_TERM = tota[0].body.WL_TERM;
										
										if ($scope.PAYMENTYEAR_SELList.length==0) {
											$scope.show_PAYMENTYEAR_SEL = true;
										}
										if ($scope.COVERYEAR_SELList.length==0) {
											$scope.show_COVERYEAR_SEL = true;
										}
										if ($scope.INSURED_OBJECTList.length==0) {
											$scope.show_INSURED_OBJECT = true;
										}
										if ($scope.KIND_SELList.length==0) {
											$scope.show_KIND_SEL = true;
										}
										
									});								
									
								}
								
								//檢核保額內容
								$scope.checkUpqty_sel = function(){
									debugger
									var QUANTITY_STYLE_ARY = $scope.QUANTITY_STYLE.split(",");
									var check = true;
									angular.forEach(QUANTITY_STYLE_ARY, function(row){
//										console.log("row---="+row);										
										var row_ary = row.split("-");
										if (row_ary.length == 2) {
											for (var i=parseInt(row_ary[0]);i<=parseInt(row_ary[1]);i++) {
												debugger
												if (i == $scope.inputVO.UPQTY_SEL) {
													check = false;
												}
											}
										} else {
											if (row == $scope.inputVO.UPQTY_SEL) {
												debugger
												check = false;
											}
										}										 
									});
									if (check) {
										 $scope.inputVO.UPQTY_SEL = '';
										 $scope.showErrorMsg('檢核保額內容失敗!!!');
									 }
								};
								
								$scope.checkPRD_NAME = function(){
									if($scope.inputVO.PRD_ID && $scope.inputVO.PRD_NAME){
										if(($scope.tempPRD_NAME != $scope.inputVO.PRD_NAME) || ($scope.tempPRD_ID != $scope.inputVO.PRD_ID)){
											$scope.clear_Prduct_Data();
										}
									}
								}
								
								//增加附約
								$scope.Add_IS_MAIN_N = function(){
									if($scope.parameterTypeEditForm.$invalid) {
							    		$scope.showErrorMsg("ehl_01_common_022");
							    		return;
								    }
									if($scope.inputVO.COM_ID != ''){
										var COM_ID = $scope.inputVO.COM_ID;
										var IS_MAIN_NList = $scope.inputVO.IS_MAIN_NList;
										var INSURED_ID = $scope.inputVO.INSURED_ID;
										var INSURED_NAME = $scope.inputVO.INSURED_NAME;
										var INSURED_BIRTHDAY = $scope.inputVO.INSURED_BIRTHDAY;
										var GENDER = $scope.inputVO.GENDER;
										var EFFECTED_DATE = $scope.inputVO.EFFECTED_DATE;
										var CURR_CD = $scope.inputVO.CURR_CD;
										debugger
										var dialog = ngDialog.open({
											template: 'assets/txn/INS111/INS111_RIDER.html',	//Juan看這邊
											className: 'INS111_RIDER',							//Juan看這邊
											controller:['$scope',function($scope){
												$scope.COM_ID = COM_ID;
												$scope.temp_INSURED_ID = INSURED_ID;
												$scope.temp_INSURED_NAME = INSURED_NAME;
												$scope.temp_INSURED_BIRTHDAY = INSURED_BIRTHDAY;
												$scope.temp_INSURED_GENDER = GENDER;
												$scope.IS_MAIN_NList = IS_MAIN_NList;
												$scope.TYPE='new';
												$scope.temp_EFFECTED_DATE = EFFECTED_DATE;
												$scope.temp_CURR_CD = CURR_CD;
											}]
										});
										
										dialog.closePromise.then(function(data){
											debugger
											if(data.value != undefined){
												if(data.value != 'cancel'){
													$scope.inputVO.IS_MAIN_NList = data.value;
												}
											}
										});
									}else{
										$scope.showErrorMsg('ehl_01_INS112_001');
									}
									
								}
								
								//查詢已新增附約
								$scope.Modify_IS_MAIN_N = function(row){
									var COM_ID = $scope.inputVO.COM_ID;
									var IS_MAIN_NList = $scope.inputVO.IS_MAIN_NList
									var INSURED_ID = $scope.inputVO.INSURED_ID;
									var INSURED_NAME = $scope.inputVO.INSURED_NAME;
									var INSURED_BIRTHDAY = $scope.inputVO.INSURED_BIRTHDAY;
									var EFFECTED_DATE = $scope.inputVO.EFFECTED_DATE;
									var CURR_CD = $scope.inputVO.CURR_CD;
									debugger
									var dialog = ngDialog.open({
										template: 'assets/txn/INS111/INS111_RIDER.html',	//Juan看這邊
										className: 'INS111_RIDER',							//Juan看這邊
										controller:['$scope',function($scope){
											$scope.TYPE = 'update';
											$scope.COM_ID = COM_ID;
											$scope.IS_MAIN_NList = IS_MAIN_NList;
											$scope.KEY_NO = row.KEYNO;
											$scope.PRD_KEY_NO = row.PRD_KEYNO;
											$scope.INSURED_ID = row.INSURED_ID;
											$scope.INSURED_BIRTHDAY = row.INSURED_BIRTHDAY;
											$scope.INSURED_NAME = row.INSURED_NAME;
											$scope.PRD_NAME = row.PRD_NAME;
											$scope.PRD_ID = row.PRD_ID;
											$scope.PAYMENTYEAR_SEL = row.PAYMENTYEAR_SEL;
											$scope.EFFECTED_DATE = row.EFFECTED_DATE;
											$scope.PAY_TYPE = row.PAY_TYPE;
											$scope.INSURED_OBJECT = row.INSURED_OBJECT;
											$scope.KIND_SEL = row.KIND_SEL;
											$scope.INSYEARFEE = row.INSYEARFEE;
											$scope.BENEFICIARY_YN = row.BENEFICIARY_YN;
											
											$scope.UPQTY_SEL = row.UPQTY_SEL;
											$scope.UPTYPE = row.UPTYPE;
											$scope.INSUREDAMT = row.INSUREDAMT;
											
											$scope.labelTITLE_Y = row.TITLE_Y == null ? '' : row.TITLE_Y;
											$scope.labelTITLE_A = row.TITLE_A == null ? '' : row.TITLE_A;
											$scope.labelTITLE_O = row.TITLE_O == null ? '' : row.TITLE_O;
											$scope.labelTITLE_K = row.TITLE_K == null ? '' : row.TITLE_K;
											
											$scope.temp_INSURED_ID = INSURED_ID;
											$scope.temp_INSURED_NAME = INSURED_NAME;
											$scope.temp_INSURED_BIRTHDAY = INSURED_BIRTHDAY;
											$scope.temp_EFFECTED_DATE = EFFECTED_DATE;
											$scope.temp_CURR_CD = CURR_CD;
										}]
									});
									
									dialog.closePromise.then(function(data){
										if(data.value != undefined){
											if(data.value != 'cancel'){
												$scope.inputVO.IS_MAIN_NList = data.value;
											}
										}
									});
								}
								
								
								$scope.Save = function(){
									if($scope.parameterTypeEditForm.$invalid || !$scope.inputVO.INSYEARFEE) {
							    		$scope.showErrorMsg("ehl_01_common_022");
							    		return;
								    }
									
									var errorMsg = '';
									for(var i=0; i<$scope.inputVO.IS_MAIN_NList.length; i++) {
										if($scope.inputVO.IS_MAIN_NList[i].EFFECTED_DATE == undefined) {
											errorMsg += (i+1) + '、';
										}
									}
									if(errorMsg.length != 0) {
										$scope.showErrorMsg('第' + errorMsg.substring(0,errorMsg.length-1) + '筆附約，欄位檢核錯誤：*為必要輸入欄位,請輸入後重試');										
										return;
									}
									
									if($scope.type == 'new'){
										$scope.sendRecv("INS111","Save_Data","com.systex.jbranch.app.server.fps.ins111.INS111InputVO",
												$scope.inputVO,function(tota,isError){
													if(!isError){
														if(tota.length > 0){
															$scope.showSuccessMsg('ehl_01_common_001');
									                		$scope.closeThisDialog('successful');
														}
													}
											
										});
									}else if($scope.type == 'update'){
										$scope.sendRecv("INS111","Update_Data","com.systex.jbranch.app.server.fps.ins111.INS111InputVO",
												$scope.inputVO,function(tota,isError){
													if(!isError){
														if(tota.length > 0){
															$scope.showSuccessMsg('ehl_01_common_006');
									                		$scope.closeThisDialog('successful');
														}
													}
											
										});
									}

								}
								
								// 確認附約資料必填 --> 保單生效日期 [額外判斷原因：可能因為主約調動日期導致undefined]
								$scope.checkIS_MAIN_NList = function() {
									
								}
								
								
								
								//保費轉千分位
								$scope.change_number_ins = function(number){
									$scope.inputVO.INSYEARFEE = $scope.moneyFormat(number);
								}
								
								$scope.setDefaultFirst = function(inputVO_column, dataList) {
									if(dataList.length == 1) {
										$scope.inputVO[inputVO_column] = dataList[0].DATA;
//										console.log("label:" + dataList[0].LABEL) 
//										console.log("data:" + dataList[0].DATA)
									}
									
									//當繳費年期為躉繳時，繳別自動帶入躉繳
									if(inputVO_column == 'PAYMENTYEAR_SEL' && dataList[0].DATA == '01' && dataList.length > 0){
										$scope.inputVO.PAY_TYPE = '0';
									}
								}
								
								// 試算保費
								$scope.calCalculationPolicy = function() {
									if($scope.parameterTypeEditForm.$invalid) {
							    		$scope.showErrorMsg("ehl_01_common_022");
							    		return;
								    }
									$scope.sendRecv("INS111","calculationPolicy","com.systex.jbranch.app.server.fps.ins111.INS111InputVO",
											$scope.inputVO, function(totas,isError){
										debugger
										if(!isError) {
											$scope.inputVO.INSYEARFEE = $scope.moneyFormat(totas[0].body);
										} else {
											$scope.inputVO.INSYEARFEE = undefined;
//											$scope.showErrorMsg(totas.body.msgData);
										}
									});
								}
								
								// 重新保險區間
//								$scope.reRangeDate = function() {
//									debugger;
//									
//									$scope.bgn_sDateOptions.maxDate = new Date();
//								}
								
									
				});
