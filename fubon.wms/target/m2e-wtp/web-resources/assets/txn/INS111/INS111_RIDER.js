/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS111_RIDERController',//Juan看這邊
						function($rootScope, $confirm, $scope,$filter, $controller, socketService, ngDialog, projInfoService,sysInfoService,getParameter,$timeout,$http) {
									$controller('BaseController', {$scope : $scope});
									$scope.controllerName = "INS111_RIDERController";//Juan看這邊
									
									// 有效起始日期
									$scope.bgn_sDateOptions = {
										maxDate: new Date(),
										minDate: new Date($scope.temp_EFFECTED_DATE)
									};
									
									// config
									$scope.model = {};
									$scope.open = function($event, elementOpened) {
										$event.preventDefault();
										$event.stopPropagation();
										$scope.model[elementOpened] = !$scope.model[elementOpened];
									};
									
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
									getParameter.XML(["IOT.CM_FLAG"],function(totas){
										if(totas){
											//戶況檢核
											$scope.mappingSet['IOT.CM_FLAG'] = totas.data[0];
										}
									});
							        
									$scope.getDrop_down = function(){
										$scope.PAYMENTYEAR_SELList = [];
										$scope.COVERYEAR_SELList = [];
										$scope.INSURED_OBJECTList = [];
										$scope.KIND_SELList = [];
										$scope.show_PAYMENTYEAR_SEL = false;
										$scope.show_COVERYEAR_SEL = false;
										$scope.show_INSURED_OBJECT = false;
										$scope.show_KIND_SEL = false;
										$scope.IS_WL = '';
										$scope.WL_TERM = '';
										
										
										$scope.QUANTITY_STYLEList = [];
										$scope.QUANTITY_STYLE = '';
										$scope.COVERCACULUNITDESC = '';
										
//										$scope.inputVO.PAYMENTYEAR_SEL = '';
//										$scope.inputVO.COVERYEAR_SEL = '';
//										$scope.inputVO.INSURED_OBJECT = '';
//										$scope.inputVO.KIND_SEL = '';
										debugger
										$scope.sendRecv("INS111","getDrop_down","com.systex.jbranch.app.server.fps.ins111.INS111InputVO",
												$scope.inputVO,function(tota,isError){
											$scope.IS_WL = tota[0].body.IS_WL;
											$scope.WL_TERM = tota[0].body.WL_TERM;
											if(tota[0].body.PAYMENTYEAR_SELList){
												$scope.PAYMENTYEAR_SELList = tota[0].body.PAYMENTYEAR_SELList;
												// 繳費年期 只有一筆請帶預設第一筆
												$scope.setDefaultFirst("PAYMENTYEAR_SEL", $scope.PAYMENTYEAR_SELList);
											} else {
												$scope.show_PAYMENTYEAR_SEL = true;
											}
											
											if(tota[0].body.COVERYEAR_SELList){
												$scope.COVERYEAR_SELList = tota[0].body.COVERYEAR_SELList;
												// 保障年期 只有一筆請帶預設第一筆
												$scope.setDefaultFirst("COVERYEAR_SEL", $scope.COVERYEAR_SELList);
											} else {
												$scope.show_COVERYEAR_SEL = true;
											}
											debugger;
											if(tota[0].body.INSURED_OBJECTList){
												$scope.INSURED_OBJECTList = tota[0].body.INSURED_OBJECTList;
												// 保險對象 只有一筆請帶預設第一筆
												$scope.setDefaultFirst("INSURED_OBJECT", $scope.INSURED_OBJECTList);
											} else {
												$scope.show_INSURED_OBJECT = true;
											}
											
											if(tota[0].body.KIND_SELList){
												$scope.KIND_SELList = tota[0].body.KIND_SELList;
												// 種類 只有一筆請帶預設第一筆
												$scope.setDefaultFirst("KIND_SEL", $scope.KIND_SELList);
											} else {
												$scope.show_KIND_SEL = true;
											}
											
											
											if (tota[0].body.QUANTITY_STYLEList) {
												$scope.QUANTITY_STYLEList = tota[0].body.QUANTITY_STYLEList;
												// 保額 只有一筆請帶預設第一筆
												$scope.setDefaultFirst("UPQTY_SEL", $scope.QUANTITY_STYLEList);
												$scope.inputVO.UPTYPE = '02';
											} 
																															
											
											$scope.QUANTITY_STYLE = tota[0].body.QUANTITY_STYLE;
											if (tota[0].body.QUANTITY_STYLE) {
												$scope.inputVO.UPTYPE = '01';
											}
											$scope.COVERCACULUNITDESC = tota[0].body.COVERCACULUNITDESC;
											
										});
									}
							        
									/**initialize**/
									$scope.init = function() {
										$scope.show_IS_MAIN_N = false;

										debugger
										// label
										$scope.inputVO = {
												KEY_NO:$scope.KEY_NO,
												TYPE:$scope.TYPE,
												COM_ID:$scope.COM_ID,
												PRD_KEY_NO:$scope.PRD_KEY_NO,
												INSURED_ID:$scope.INSURED_ID,
												INSURED_BIRTHDAY:new Date($scope.INSURED_BIRTHDAY),
												INSURED_NAME:$scope.INSURED_NAME,
												PRD_NAME:$scope.PRD_NAME,
												PRD_ID:$scope.PRD_ID,
												PAYMENTYEAR_SEL:$scope.PAYMENTYEAR_SEL,
												EFFECTED_DATE:new Date($scope.EFFECTED_DATE),
												PAY_TYPE:$scope.PAY_TYPE,
												INSURED_OBJECT:$scope.INSURED_OBJECT,
												KIND_SEL:$scope.KIND_SEL,
												INSYEARFEE:$scope.INSYEARFEE,
												BENEFICIARY_YN:$scope.BENEFICIARY_YN,
												IS_MAIN:'N',
												IS_MAIN_NList:$scope.IS_MAIN_NList,
												GENDER:'',
												UPQTY_SEL:$scope.UPQTY_SEL,
												INSUREDAMT:$scope.INSUREDAMT,
												UPTYPE:$scope.UPTYPE,
												CURR_CD:$scope.temp_CURR_CD,
												labelTITLE_Y:'', //	TITLE_Y – 繳費年期的動態欄位名稱
												labelTITLE_A:'', //	TITLE_A – 保障年期的動態欄位名稱
												labelTITLE_O:'', //	TITLE_O – 投保對象的動態欄位名稱
												labelTITLE_K:'', // TITLE_K – 種類的動態欄位名稱
										}
										
									}
									$scope.init();//initialize variable
									if($scope.inputVO.TYPE == 'update') {
										$scope.inputVO.PRD_KEY_NO = $scope.inputVO.PRD_KEY_NO;
										$scope.getDrop_down();
										$scope.inputVO.UPQTY_SEL = $scope.UPQTY_SEL;
										if($scope.inputVO.INSURED_ID === $scope.temp_INSURED_ID) {
											$scope.sam_IS_MAIN = true;
										}
									} else {
										$scope.inputVO.INSURED_ID = $scope.temp_INSURED_ID;
										$scope.inputVO.INSURED_NAME = $scope.temp_INSURED_NAME;
										$scope.inputVO.INSURED_BIRTHDAY = new Date($scope.temp_INSURED_BIRTHDAY);
										$scope.inputVO.GENDER = $scope.temp_INSURED_GENDER;
										$scope.sam_IS_MAIN = true;
										$scope.inputVO.EFFECTED_DATE = new Date($scope.temp_EFFECTED_DATE);
									}
									
									$scope.inputVO.labelTITLE_Y = $scope.inputVO.TYPE == 'update'? $scope.labelTITLE_Y : '';
									$scope.inputVO.labelTITLE_A = $scope.inputVO.TYPE == 'update'? $scope.labelTITLE_A : '';
									$scope.inputVO.labelTITLE_O = $scope.inputVO.TYPE == 'update'? $scope.labelTITLE_O : '';
									$scope.inputVO.labelTITLE_K = $scope.inputVO.TYPE == 'update'? $scope.labelTITLE_K : '';
									
								//根據欄位帶出分行代碼、招攬人員姓名、要保人姓名、留存分行地址	
								$scope.getInfo = function(in_column){
									$scope.inputVO.in_column = in_column;
									if($scope.inputVO.in_column == 'INSURED'){
										if($scope.inputVO.INSURED_ID){
											$scope.inputVO.INSURED_ID = $scope.inputVO.INSURED_ID.toUpperCase();
											if($scope.inputVO.INSURED_ID === $scope.temp_INSURED_ID) {
												$scope.sam_IS_MAIN = true;
											} else {
												$scope.sam_IS_MAIN = false;
											}
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
											if(tota[0].body != null && tota[0].body.length>0){
												if($scope.inputVO.INSURED_ID !=''){
													$scope.inputVO.INSURED_NAME = tota[0].body[0].CUST_NAME.replace(/\s/g,'');
													var birthday = new Date(tota[0].body[0].BIRTH_DATE);
													$scope.inputVO.INSURED_BIRTHDAY = birthday;
//													$scope.inputVO.INSURED_CM_FLAG = tota[0].body.INSURED_NAME[0].INSURED_CM_FLAG;
													$scope.inputVO.GENDER = tota[0].body[0].GENDER;
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
								
								//商品查詢(附約)
								$scope.getProduc = function(IS_MAIN_TYPE){
									if($scope.inputVO.COM_ID == ''){
										$scope.showErrorMsg('ehl_01_INS112_001');
									}else{
										var COM_ID = $scope.inputVO.COM_ID;
										var CURR_CD = $scope.inputVO.CURR_CD;
										var PRD_NAME = '';
										var PRD_ID = '';
										console.log($scope.inputVO.PRD_ID)
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
												$scope.CURR_CD = CURR_CD;
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
													$scope.inputVO.PRD_NAME = data.value.PRD_NAME;
													$scope.inputVO.PRD_ID = data.value.PRD_ID;
													$scope.inputVO.PRD_KEY_NO = data.value.KEY_NO;
													$scope.getDrop_down();
													
													
													// 動態顯示欄位名稱
													$scope.inputVO.labelTITLE_Y = '';
													$scope.inputVO.labelTITLE_A = '';
													$scope.inputVO.labelTITLE_O = '';
													$scope.inputVO.labelTITLE_K = '';
													
													if(data.value.TITLE_Y != null){
														$scope.inputVO.labelTITLE_Y = data.value.TITLE_Y;
													}
													
													if(data.value.TITLE_A != null){
														$scope.inputVO.labelTITLE_A = data.value.TITLE_A;
													}
													
													if(data.value.TITLE_O !=null){
														$scope.inputVO.labelTITLE_O = data.value.TITLE_O;
													}
													
													if(data.value.TITLE_K != null){
														$scope.inputVO.labelTITLE_K = data.value.TITLE_K
													}
													
													$scope.inputVO.PAYMENTYEAR_SEL = null;
													$scope.inputVO.COVERYEAR_SEL = null;
													$scope.inputVO.INSURED_OBJECT = null;
													$scope.inputVO.KIND_SEL = null;
													
													$scope.inputVO.INSUREDAMT = null
													$scope.inputVO.UPQTY_SEL = null;
													$scope.inputVO.UPTYPE = null;
												}
											}
										});
									}
								}
								

								
								$scope.Reture_IS_MAIN_N = function(){
									if($scope.parameterTypeEditForm_RIDER.$invalid) {
							    		$scope.showErrorMsg("ehl_01_common_022");
							    		return;
								    }
									debugger
									$scope.sendRecv("INS111","addIS_MAIN_N","com.systex.jbranch.app.server.fps.ins111.INS111_IS_MAIN_NInputVO",
											$scope.inputVO,function(tota,isError){
										$scope.closeThisDialog(tota[0].body.IS_MAIN_NList);
									});
								}
								
								$scope.Same_Of_IS_MAIN = function(){
									if($scope.sam_IS_MAIN){
										$scope.inputVO.INSURED_ID = $scope.temp_INSURED_ID;
										$scope.inputVO.INSURED_NAME = $scope.temp_INSURED_NAME;
										$scope.inputVO.INSURED_BIRTHDAY = new Date($scope.temp_INSURED_BIRTHDAY);
										$scope.inputVO.GENDER = $scope.temp_INSURED_GENDER;
									}else{
										$scope.inputVO.INSURED_ID = undefined;
										$scope.inputVO.INSURED_NAME = undefined;
										$scope.inputVO.INSURED_BIRTHDAY = undefined;
										$scope.inputVO.GENDER = '';
									}
								}
								
								//保費轉千分位
								$scope.change_number_ins = function(number){
									$scope.inputVO.INSYEARFEE = $scope.moneyFormat(number);
								}
								
								$scope.setDefaultFirst = function(inputVO_column, dataList) {
									if(dataList.length == 1) {
										$scope.inputVO[inputVO_column] = dataList[0].DATA;
									} 
			
									//新增附約預設對象為本人
									if(inputVO_column == 'INSURED_OBJECT' && dataList.length>0) {
										angular.forEach(dataList, function(row, index) {
											if(!$scope.inputVO[inputVO_column]){
												if(row.DATA == '本人') {
													$scope.inputVO[inputVO_column] = row.DATA;
												}
											}											
										})
									}
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
								
								//清除保險相關欄位
								$scope.clear_Prduct_Data = function(){
									$scope.inputVO.PRD_KEY_NO = '';
									$scope.inputVO.PRD_ID = '';
									$scope.inputVO.PRD_NAME = '';
									$scope.tempPRD_ID = '';
									$scope.tempPRD_NAME = '';
									$scope.inputVO.CUUR_CD = '';
									$scope.inputVO.PAYMENTYEAR_SEL = '';
									$scope.inputVO.COVERYEAR_SEL = '';
//									$scope.inputVO.EFFECTED_DATE = undefined;
									$scope.inputVO.PAY_TYPE = '';
									$scope.inputVO.INSURED_OBJECT = '';
									$scope.inputVO.KIND_SEL = '';
									$scope.QUANTITY_STYLE = null;
									$scope.COVERCACULUNITDESC = '';
									$scope.inputVO.UPTYPE = '';
									$scope.inputVO.UPQTY_SEL = '';
									$scope.inputVO.INSUREDAMT = '';
									$scope.inputVO.BENEFICIARY_YN = '';
									
									$scope.PAYMENTYEAR_SELList = [];
									$scope.COVERYEAR_SELList = [];
									$scope.INSURED_OBJECTList = [];
									$scope.KIND_SELList = [];
									$scope.show_PAYMENTYEAR_SEL = false;
									$scope.show_COVERYEAR_SEL = false;
									$scope.show_INSURED_OBJECT = false;
									$scope.show_KIND_SEL = false;
									$scope.IS_WL = '';
									$scope.WL_TERM = '';
									
									
									$scope.QUANTITY_STYLEList = undefined;
									$scope.COVERCACULUNITDESC = '';
									$scope.inputVO.labelTITLE_Y = '';
									$scope.inputVO.labelTITLE_A = '';
									$scope.inputVO.labelTITLE_O = '';
									$scope.inputVO.labelTITLE_K = '';
								}
									
				});
