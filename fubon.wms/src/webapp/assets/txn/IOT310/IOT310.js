/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT310Controller',
						function($rootScope, $q, $confirm, $scope,$filter, $controller, socketService, ngDialog, projInfoService,sysInfoService,$timeout) {
									$controller('BaseController', {$scope : $scope});
									$scope.controllerName = "IOT310Controller";
									
									  //複選
							    
									// config
									$scope.model = {};
									$scope.open = function($event, elementOpened) {
										$event.preventDefault();
										$event.stopPropagation();
										$scope.model[elementOpened] = !$scope.model[elementOpened];
									};
									
									$scope.open_apply_date = function($event, elementOpened) {
										$event.preventDefault();
										$event.stopPropagation();
										$scope.model[elementOpened] = !$scope.model[elementOpened];
									};
									$scope.timsys=new Date();
									$scope.bgn_sDateOptions={
											maxDate:$scope.timsys
									};
									
									//判斷用途in_OprStatus
									if($scope.connector('get','IOT330')!=undefined)
										{	
											$scope.in_OprStatus= $scope.connector('get','IOT330').OPR_STATUS;
											$scope.INS_KIND= $scope.connector('get','IOT330').INS_KEYNO;
											$scope.connector('set','IOT330','');
										}else{
											$scope.in_OprStatus='NEW';
											$scope.INS_KIND= '';
										}
//									
								
									/**initialize**/
									$scope.init = function() {
										//設定$scope.mappingSet['IOT.PPT_DOC_SEQ']的值
										var vo = {'param_type': 'IOT.PPT_DOC_SEQ', 'desc': false};
								    	$scope.requestComboBox(vo, function(totas) {
								    		if (totas[totas.length - 1].body.result === 'success') {
								    			projInfoService.mappingSet['IOT.PPT_DOC_SEQ'] = totas[0].body.result;
								    			$scope.mappingSet['IOT.PPT_DOC_SEQ'] = projInfoService.mappingSet['IOT.PPT_DOC_SEQ'];
								    		}
								    	});
								    	
								    	$scope.mappingSet[0]=false;
										$scope.mappingSet[1]=false;
										$scope.mappingSet[2]=false;
										$scope.mappingSet[3]=false;
										$scope.mappingSet[4]=false;
										$scope.mappingSet[5]=false;
								    	
										$scope.inputVO = {
												SYSDATE:new Date(),
												INS_KEYNO           :'',          //主鍵
												INS_KIND            :'',          //產險種類
												INS_ID              :'',          //產險文件編號
												POLICY_NO           :'',          //保單號碼
												REG_TYPE            :'1',          //登錄類型
												BRANCH_NBR          :sysInfoService.getBranchID(),          //分行代碼
												CUST_ID             :undefined,          //要保人ID
												PROPOSER_NAME       :'',          //要保人姓名
												INSURED_ID          :'',          //被保人ID
												INSURED_NAME        :'',          //被保人姓名
												PPT_TYPE            :'',          //產險險種												
												REAL_PREMIUM        :'',          //總保費
												BATCH_INFO_KEYNO    :'',          //送件打包主鍵
												REF_CON_ID          :'',          //轉介編號
												REF_CON_EMPID :'',
												REF_CON_EMPNAME :'',
												RECRUIT_ID          :sysInfoService.getUserID(),          //招攬人員編
												RECRUIT_NAME          :sysInfoService.getUserName(),
												STATUS              :'20',          //狀態
												PPT_DOC             :'N',          //產險相關文件
												PPT_DOC_OTHER       :'',          //產險相關文件(其他)
												UNOPEN_ACCT         :'',          //未開戶註記
												c1:false,
												c2:false,
												c3:false,
												c4:false,
												c5:false,	
												in_column :'',
												DOC_SEQ:[],
												KEYIN_DATE:new Date(),
												APPLY_DATE:undefined,
												RECRUIT_ID_SAVE:'',
												INSPRD_ID:''
											};
										$scope.PPTIDDataVO=[];
										if($scope.in_OprStatus != "NEW"){
											$scope.sendRecv("IOT310","Initial","com.systex.jbranch.app.server.fps.iot310.IOT310InputVO",
						        				{'INS_KEYNO':$scope.INS_KIND},function(tota,isError){
												
												if(!isError){
													angular.forEach(tota[0].body.InitList, function(row, index, objs){
														$scope.inputVO.SYSDATE=new Date();
														$scope.inputVO.INS_KEYNO                   =row.INS_KEYNO                          ;                  
														$scope.inputVO.INS_KIND                    =row.INS_KIND                           ;
														$scope.inputVO.INS_ID                      =row.INS_ID                             ;
														$scope.inputVO.POLICY_NO                   =row.POLICY_NO                          ;
														$scope.inputVO.REG_TYPE                    =row.REG_TYPE                           ;
														$scope.inputVO.BRANCH_NBR                  =row.BRANCH_NBR                         ;
														$scope.inputVO.CUST_ID                     =row.CUST_ID                            ;
														$scope.inputVO.PROPOSER_NAME               =row.PROPOSER_NAME                      ;
														$scope.inputVO.INSURED_ID                  =row.INSURED_ID                         ;
														$scope.inputVO.INSURED_NAME                =row.INSURED_NAME                       ;
														$scope.inputVO.PPT_TYPE                    =row.PPT_TYPE                           ;
														$scope.inputVO.REAL_PREMIUM                =row.REAL_PREMIUM                       ;
														$scope.inputVO.BATCH_INFO_KEYNO            =row.BATCH_INFO_KEYNO                   ;
														$scope.inputVO.REF_CON_ID                  =row.REF_CON_ID                         ;
														$scope.inputVO.REF_CON_EMPID               =row.REF_CON_EMPID                      ;
														$scope.inputVO.REF_CON_EMPNAME             =row.REF_CON_EMPNAME                    ;
														$scope.inputVO.RECRUIT_ID                  =row.RECRUIT_ID                         ;
														$scope.inputVO.RECRUIT_NAME                =row.RECRUIT_NAME                       ;
														$scope.inputVO.STATUS                      ='20'                            ;
														$scope.inputVO.PPT_DOC                     =row.PPT_DOC                            ;
														$scope.inputVO.PPT_DOC_OTHER               =row.PPT_DOC_OTHER                      ;
														$scope.inputVO.UNOPEN_ACCT                 =row.UNOPEN_ACCT                        ;
														$scope.inputVO.KEYIN_DATE                  =new Date(row.KEYIN_DATE)      ;
														$scope.inputVO.c1                   		=row.c1                         ;
														$scope.inputVO.c2                   		=row.c2                         ;
														$scope.inputVO.c3                  			=row.c3                         ;
														$scope.inputVO.c4                  			=row.c4                         ;
														$scope.inputVO.c5	               			=row.c5	                        ;
														$scope.inputVO.in_column                    =row.in_column                          ;
														$scope.inputVO.APPLY_DATE                   =$scope.toJsDate(row.APPLY_DATE)               ;
														$scope.inputVO.RECRUIT_ID                =row.RECRUIT_ID      ;
														$scope.inputVO.OP_BATCH_NO               =row.OP_BATCH_NO     ;
														$scope.inputVO.CREATOR                   =row.CREATOR         ;
														$scope.inputVO.CREATETIME                =row.CREATETIME      ;
														$scope.inputVO.BEF_SIGN_OPRID            =row.BEF_SIGN_OPRID  ;
														$scope.inputVO.BEF_SIGN_DATE             =row.BEF_SIGN_DATE   ;
														$scope.inputVO.SIGN_OPRID                =row.SIGN_OPRID      ;
														$scope.inputVO.SIGN_DATE                 =row.SIGN_DATE       ;
														$scope.inputVO.AFT_SIGN_OPRID            =row.AFT_SIGN_OPRID  ;
														$scope.inputVO.AFT_SIGN_DATE             =row.AFT_SIGN_DATE   ;
														$scope.inputVO.DOC_SEQ=[];
													});
													if($scope.inputVO.REAL_PREMIUM != undefined){
														$scope.inputVO.REAL_PREMIUM = $filter('number')($scope.inputVO.REAL_PREMIUM);
													}
													
													angular.forEach(tota[0].body.InitList1, function(row, index, objs){
														
														if(row.DOC_SEQ!=''){
															var nums=parseInt(row.DOC_SEQ)-1;
															$scope.mappingSet[nums]=true;
														
														}
														if(row.DOC_SEQ=='6'){
															$scope.inputVO.PPT_DOC_OTHER=row.DOC_OTH;
														}
														
													});
													
												}
												$scope.connector('set','IOT330',undefined);
											});
										}
										
										//要保書類型
										
										 var vo = {'param_type': 'IOT.MAIN_STATUS', 'desc': false};
									        if(!projInfoService.mappingSet['IOT.MAIN_STATUS']) {
									        	$scope.requestComboBox(vo, function(totas) {
									        	
									        		if (totas[totas.length - 1].body.result === 'success') {
									        		
									        			projInfoService.mappingSet['IOT.MAIN_STATUS'] = totas[0].body.result;
									        			$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];
									        		
									        		}
									        	});
									        } else
									        	$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];
									   
									}
									$scope.init();//initialize variable
									//險種下拉式選單
									$scope.PPT_TYPE = function(){
										$scope.sendRecv("IOT920","getPPTID","com.systex.jbranch.app.server.fps.iot920.PPTIDDataVO",
							        			$scope.PPTIDDataVO,function(tota,isError){
												$scope.mappingSet['PPT_TYPE']=[];
													angular.forEach(tota[0].body.PPTIDData, function(row, index, objs){
														$scope.mappingSet['PPT_TYPE'].push({DATA: row.INSPRD_ID, LABEL: row.INSPRD_NAME});
													});
											});
									}
									$scope.PPT_TYPE();
									
									//IOT.PPT_DOC_SEQ
							        //全選
							        $scope.selectAll = function(){
							        	if($scope.check){
							        		for(var i = 0; i < $scope.mappingSet['IOT.PPT_DOC_SEQ'].length; i++){
							        			$scope.inputVO.chkCode.push($scope.mappingSet['IOT.PPT_DOC_SEQ'][i].DATA);
							        		}
							        	}else{
							        		$scope.inputVO.chkCode = [];
							        	}
							        }
							        
							        
							        $scope.toggleSelection = function toggleSelection(data) {
							        	var idx = $scope.inputVO.chkCode.indexOf(data);
							        	if (idx > -1) {
							        		$scope.inputVO.chkCode.splice(idx, 1);      //若已存在，就將它移除
							        	} else {
							        		$scope.inputVO.chkCode.push(data);          //若不存在，就將它加入
							        	}
							        };
									
								
									//根據欄位帶出分行代碼、招攬人員姓名、要保人姓名、留存分行地址	
									$scope.getInfo = function(in_column){
										$scope.inputVO.in_column = in_column;
										if($scope.inputVO.in_column == 'INSURED'){
											if($scope.inputVO.INSURED_ID != undefined){
												$scope.inputVO.INSURED_ID.toUpperCase();
											}
											$scope.inputVO.INSURED_NAME = '';
											$scope.inputVO.INSURED_CM_FLAG = '';
										}
										if($scope.inputVO.in_column == 'CUST'){
											if($scope.inputVO.CUST_ID != undefined){
												$scope.inputVO.CUST_ID.toUpperCase();
											}
											$scope.inputVO.PROPOSER_NAME = '';
											$scope.inputVO.PROPOSER_BIRTH = undefined;
											$scope.inputVO.PROPOSER_CM_FLAG = '';
											$scope.inputVO.UNOPEN_ACCT='Y';
											//未開戶判定
											$scope.inputVO.UNOPEN_ACCT=$scope.inputVO.CUST_ID==$scope.inputVO.INSURED_ID?'N':'Y';
										}
										if($scope.inputVO.in_column == 'ADDR'){
											$scope.inputVO.REPRESET_NAME = '';
											$scope.inputVO.COM_ADDRESS = '';
											$scope.inputVO.REPRESET_CM_FLAG = '';
										}
										if($scope.inputVO.in_column == 'RECRUIT'){
											$scope.EMP_NAME = '';
											$scope.inputVO.AO_CODE = '';
											//檢查是否有財產保險證照資格病重置險種下拉選單
											$scope.inputVO.INSPRD_ID = $scope.inputVO.PPT_TYPE;
											$scope.inputVO.EMP_ID = $scope.inputVO.RECRUIT_ID;
											if($scope.inputVO.APPLY_DATE != undefined){
												$scope.sendRecv("IOT920","chk_PPTCert","com.systex.jbranch.app.server.fps.iot920.chk_PPTCInputVO",
							        					$scope.inputVO,function(tota,isError){
													if(!isError){
														if(tota[0].body.Chk_Pass=='N'){
															$scope.inputVO.PPT_TYPE='';
															$scope.PPT_TYPE();
															$scope.Chk_pass = tota[0].body.Chk_Pass;
															$scope.showMsg("ehl_02_IOT_005");
															return;
														}
														$scope.Chk_pass = tota[0].body.Chk_Pass;
													}
												});
											}else{
												if($scope.inputVO.PPT_TYPE != ''){
													$scope.inputVO.PPT_TYPE = '';
													$scope.showErrorMsg('請輸入文件申請日')
												}
											}
											
										}
										if($scope.inputVO.in_column == 'REF_CON_ID'){
											$scope.inputVO.REF_CON_EMPID = '';
											$scope.inputVO.REF_CON_NAME = '';
										}
										$scope.sendRecv("IOT920","getCUSTInfo","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",
					        					$scope.inputVO,function(tota,isError){
											if(!isError){
												if(tota[0].body.EMP_NAME != null){
													if(tota[0].body.EMP_NAME.length>0){
														$scope.inputVO.RECRUIT_NAME = tota[0].body.EMP_NAME[0].EMP_NAME;
														$scope.inputVO.AO_CODE = tota[0].body.EMP_NAME[0].AO_CODE;
													}
												}
												if(tota[0].body.CUST_NAME != null){
													if(tota[0].body.CUST_NAME.length>0){
														$scope.notBranchCust = false;
														$scope.inputVO.PROPOSER_NAME = tota[0].body.CUST_NAME[0].CUST_NAME;
														var birthday = $scope.toJsDate(tota[0].body.CUST_NAME[0].BIRTHDAY);
														$scope.inputVO.PROPOSER_BIRTH = birthday;
														//改由招攬人帶出
//														$scope.inputVO.AO_CODE = tota[0].body.CUST_NAME[0].AO_CODE;
														$scope.inputVO.PROPOSER_CM_FLAG = tota[0].body.CUST_NAME[0].PROPOSER_CM_FLAG;
														$scope.inputVO.UNOPEN_ACCT='N';
													}
												}
												if(tota[0].body.INSURED_NAME != null){
													if(tota[0].body.INSURED_NAME.length>0){
														if($scope.inputVO.INSURED_ID !=''){
															$scope.inputVO.INSURED_NAME = tota[0].body.INSURED_NAME[0].CUST_NAME;
															$scope.inputVO.INSURED_CM_FLAG = tota[0].body.INSURED_NAME[0].INSURED_CM_FLAG;
														}
													}
												}
												if(tota[0].body.REPRESETList != null){
													if(tota[0].body.REPRESETList.length>0){
														if($scope.inputVO.REPRESETList !=''){
															$scope.inputVO.REPRESET_NAME = tota[0].body.REPRESETList[0].CUST_NAME;
															$scope.inputVO.REPRESET_CM_FLAG = tota[0].body.REPRESETList[0].REPRESET_CM_FLAG;
															$scope.RepresetCust = true;
														}
													}
												}
												if(tota[0].body.COM_ADDRESS != null){
													if(tota[0].body.COM_ADDRESS.length>0){
														$scope.COM_ADDRESS = tota[0].body.COM_ADDRESS[0].COM_ADDRESS;
													}
												}
												if(tota[0].body.REFList != null){
													if(tota[0].body.REFList.length>0){
														$scope.inputVO.REF_CON_EMPID = tota[0].body.REFList[0].SALES_PERSON;
														$scope.inputVO.REF_CON_NAME = tota[0].body.REFList[0].SALES_NAME;
													}
												}
												
											}else{
												if($scope.inputVO.in_column == 'INSURED'){
													$scope.inputVO.INSURED_ID = undefined;
												}
												if($scope.inputVO.in_column == 'CUST'){
													$scope.inputVO.CUST_ID = undefined;
												}
												if($scope.inputVO.in_column == 'ADDR'){
													$scope.inputVO.REPRESET_ID = undefined;
												}
											}
										});
									}

									
									
									
									//複製內容
									$scope.copys= function(){
										if($scope.inputVO.copys==true){
											$scope.inputVO.PROPOSER_NAME=$scope.inputVO.INSURED_NAME;
											$scope.inputVO.CUST_ID=$scope.inputVO.INSURED_ID;
											$scope.inputVO.UNOPEN_ACCT='N';
										}else{
											$scope.inputVO.PROPOSER_NAME='';
											$scope.inputVO.CUST_ID='';
											$scope.inputVO.UNOPEN_ACCT='';
										}
									}
								
								$scope.Submit = function(){
									if($scope.parameterTypeEditForm.$invalid){
										$scope.inputVO.STATUS='20';
										$scope.showMsg('ehl_01_common_022');
						        		return;
						        	}
									if($scope.Chk_pass=='N'){
										$scope.showMsg("ehl_02_IOT_005");
										return;
									}
									$scope.inputVO.in_OprStatus=$scope.in_OprStatus;
									
									if( $scope.mappingSet[0] && $scope.mappingSet[1] && $scope.mappingSet[2] && $scope.mappingSet[3] && $scope.mappingSet[4] && $scope.mappingSet[5])
									{										
										$scope.inputVO.STATUS='20';
									}
									for(var i=0;i<6;i++){
										if( $scope.mappingSet[i]){
											$scope.inputVO.DOC_SEQ.push(i);
										}
									}
								  	$scope.sendRecv("IOT310","submit","com.systex.jbranch.app.server.fps.iot310.IOT310InputVO",
					            			$scope.inputVO,function(tota,isError){
					            			if(isError){
					            				$scope.init();
					            				$scope.showErrorMsg(tota[0].body.msgData);
					            			}
					                       	if (tota.length > 0) {
					                       		$scope.init();
					                    		$scope.showSuccessMsg('ehl_01_common_004');					                    	
					                    	};
					            	});
								  	if($scope.in_OprStatus!='NEW'){
								  		
								  		$scope.connector('set','IOT330I',$scope.connector('get','IOT330I'));
										$rootScope.menuItemInfo.url = "assets/txn/IOT330/IOT330.html";
									}
								
								}
								
								//回上一頁
								$scope.back = function(){
									$rootScope.menuItemInfo.url = "assets/txn/IOT330/IOT330.html";
								}
								
								//總保費加千分位
								$scope.chang_number=function(data,type){
									if(data.match(',') !=null){
										var change=data.replace(/,/g, '');
										if(type == 'realPremium'){
											$scope.inputVO.REAL_PREMIUM = $filter('number')(change);
										}
									}else{
										if(type == 'realPremium'){
											$scope.inputVO.REAL_PREMIUM = $filter('number')(data);
										}
									}
								}
				});
