/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS008Controller', function($rootScope, $scope,
		$controller, socketService, ngDialog, projInfoService, $q, $confirm,sysInfoService,
		$filter,$interval) {
	$controller('BaseController', {
		$scope : $scope
	});
	$controller('PMSRegionController', {$scope: $scope});

	var auto = $interval(function() {
        if($scope.count==0)
        	$scope.count++;
	}, 100);
	
	$scope.controllerName = "PMS008Controller";
	$scope.init = function() {
		$scope.row = $scope.row || {};
		$scope.inputVO = {
			eTime : '',
			aocode : '',
			branch : '',
			region : '',
			op : '',
			
			region_center_id  :'',   //區域中心
			branch_area_id  :'',	//營運區
			branch_nbr:'',			//分行
			ao_code  :'',			//理專

			
			aojob : '',
			ROB : '1', // 判斷是否為理專 或 分行 或 區域中心 或營運區
			type : ''
		};
		// 設置
		$scope.mappingSet['type'] = [];
		$scope.mappingSet['type'].push({
			LABEL : 'TOTAL',
			DATA : '(TOTAL)'
		}, {
			LABEL : '客戶權益',
			DATA : '(客戶權益)'
		}, {
			LABEL : 'EIP',
			DATA : '(EIP)'
		}, {
			LABEL : '投保',
			DATA : '(投保)'
		});
		$scope.paramList = [];
		$scope.paramList = [];
		
		$scope.inputVO.sTime=  $scope.connector('get','PMS328_M1');//時間
		$scope.inputVO.eTime=  $scope.connector('get','PMS328_M2');//時間
		$scope.paramList = $scope.connector('get','PMS328_L');



	};
	$scope.init();
	
//	$scope.inputVO.region_center_id=  $scope.connector('get','PMS328_R');//業務處
//	$scope.inputVO.branch_area_id=  $scope.connector('get','PMS328_BA');//營運區
//	$scope.inputVO.branch_nbr=  $scope.connector('get','PMS328_BN');//分行別
//	$scope.inputVO.ao_code=  $scope.connector('get','PMS328_A');//理專

	
    /*** 可示範圍  JACKY共用版  START ***/
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    
    	angular.forEach($scope.mappingSet['timeS'], function(row, index, objs){
			if(row.DATA==$scope.inputVO.sTime)
				$scope.inputVO.sTimes=row.LABEL.substring(0,4)+row.LABEL.substring(5,7);
		});
    
    	angular.forEach($scope.mappingSet['timeE'], function(row, index, objs){
			if(row.DATA==$scope.inputVO.eTime)
				$scope.inputVO.eTimes=row.LABEL.substring(0,4)+row.LABEL.substring(5,7);
		});
    	//取得轉換年月
    	if($scope.inputVO.sTimes!='請選擇')
    		$scope.inputVO.reportDate = $scope.inputVO.sTimes;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
    	if($scope.inputVO.eTime!=''){
    		if($scope.inputVO.eTimes!='請選擇')
    			$scope.inputVO.reportDate = $scope.inputVO.eTimes;   
    	}
    	//可是範圍  觸發 
    	if($scope.inputVO.sTime!='' || $scope.inputVO.eTime!=''){
    		$scope.RegionController_getORG($scope.inputVO);
    	}


    };
    $scope.dateChange();
    
    
	$scope.curDate = new Date();
	
	var rp = "RC";
	if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
		rp = "AO";
	if(sysInfoService.getRoleID() == 'A161')
		rp = "BR";
	
	/*** 可示範圍  JACKY共用版  END***/

	
	/***
	 * 主查詢(趨勢圖)
	 * 
	 */
	$scope.inquire = function() {
		
		//預設無值中斷
//    	if($scope.AO_LIST==null){
//        	if($scope.parameterTypeEditForm.$invalid){				
//	    		$scope.showErrorMsg('欄位檢核錯誤:年月為必輸入欄位!!!');
//	    		$scope.connector('set','CRM171_AO_CODE', undefined);
//        		return;
//        	}	
//    		$scope.paramList=$scope.AO_LIST;
//    		$scope.inputVO.sTime=  $scope.connector('get','PMS328_M1');//時間
//    		$scope.inputVO.eTime=  $scope.connector('get','PMS328_M2');//時間
//			$scope.paramList = $scope.REGION_LIST;

//    		return;
//    	}

//		if($scope.inputVO.region_center_id!='' ||  $scope.inputVO.aocode!='' ||  $scope.inputVO.branch_area_id!='' ||  $scope.inputVO.branch_nbr!=''){
			  $scope.prodType=0;
			  if (angular.isDefined(auto)) {
		          $interval.cancel(auto);
		          auto = $interval(function() {
		  	        if($scope.count==0)
		  	        	$scope.count++;
		  	     
		          }, 800);
		          $scope.count=0;
		        }
			 
//		if($scope.inputVO.aocode!='')  //判斷理專  員編
//			$scope.paramList=$scope.mappingSet['aocodes'];
		$scope.sendRecv("PMS328", "queryImage",
				"com.systex.jbranch.app.server.fps.pms328.PMS328IInputVO", {
					'list' : $scope.paramList,
					'ROB' : $scope.inputVO.ROB,
					'type' : $scope.inputVO.type,
					'aocode':$scope.inputVO.aocode,
					'sTimes':$scope.inputVO.sTimes,
					'eTimes':$scope.inputVO.eTimes,
					'list_AOCODE':$scope.AO_LIST
				}, function(tota, isError) {
					if (!isError) {
						var Array = []; 
						var Array0 = []; // 存放X,Y陣列
						var nam = [];    //存放所有查資料    
						var c = [];
						$scope.data=[];   //清空data
						$scope.outputVO = tota[0].body;  
						//單一指向AO_CODE
//						alert("處理前="+$scope.paramList.length);
						if($scope.inputVO.aocode!=''){
							$scope.paramList=[];   //清空查詢資料
//							$scope.paramList=$scope.AO_LIST;
							angular.forEach($scope.AO_LIST, function(row, index,
									objs) {
								// push 請選擇  空字串列
								if(row.DATA==$scope.inputVO.aocode || row.LABEL=="請選擇"){
//									|| row.LABEL=="請選擇"
									//置入一筆row
									$scope.paramList.push(row);  
//									alert(JSON.stringify($scope.paramList));
								}
							});
						}
//						alert(JSON.stringify(tota[0].body.resultList));

						//*******====以下放置圖的資料 START===*********//
						for (var x = 0; x < $scope.paramList.length; x++) {
							/***=====以下是最多可以幾條線======***/
							angular.forEach(tota[0].body.resultList, function(
									row, index, objs) {
								if (row.AVG != undefined && x == 0) {  
									Array.push({x: row.YEARMON,y: row.AVG });    //全行平均
								}
								if (row.AVG0 != undefined && x == 0) {    //第一條線
									Array0.push({x: row.YEARMON,y: row.AVG0 });
								}
								if (row.AVG1 != undefined && x == 1) {   //第二條線
									Array0.push({x: row.YEARMON,y: row.AVG1 });
								}
								if (row.AVG2 != undefined && x == 2) {    //第三條線
									Array0.push({x: row.YEARMON,y: row.AVG2 });
								}
								if (row.AVG3 != undefined && x == 3) {     //第四條線
									Array0.push({x: row.YEARMON,y: row.AVG3 });
								}
								if (row.AVG4 != undefined && x == 4) {     //第五條線
									Array0.push({x: row.YEARMON,y: row.AVG4 });
								}
								if (row.AVG5 != undefined && x == 5) {    //第六條線
									Array0.push({x: row.YEARMON,y: row.AVG5 });
								}
								if (row.AVG6 != undefined && x == 6) {    //第七條線
									Array0.push({x:  row.YEARMON,y:row.AVG6 });
								}
								if (row.AVG7 != undefined && x == 7) {    //第八條線
									Array0.push({x: row.YEARMON,y: row.AVG7 });
								}
								if (row.AVG8 != undefined && x == 8) {    //第九條線
									Array0.push({x: row.YEARMON,y:row.AVG8 });
								}
								if (row.AVG9 != undefined && x == 9) {     //第十條線
									Array0.push({x: row.YEARMON,y: row.AVG9 });
								}
								if (row.AVG10 != undefined && x == 10) {    //第十一條線
									Array0.push({x: row.YEARMON,y: row.AVG10 });
								}
								if (row.AVG11 != undefined && x == 11) {    //第十二條線
									Array0.push({x: row.YEARMON,y: row.AVG11 });
								}
								if (row.AVG12 != undefined && x == 12) {    //第十三條線
									Array0.push({x: row.YEARMON,y: row.AVG12 });
								}
								if (row.AVG13 != undefined && x == 13) {    //第十四條線  
									Array0.push({x: row.YEARMON,y: row.AVG13 });
								}
								if (row.AVG14 != undefined && x == 14) {    //第十五條線  
									Array0.push({x:  row.YEARMON,y: row.AVG14 });
								}
								if (row.AVG15 != undefined && x == 15) {    //第十六條線  
									Array0.push({x:  row.YEARMON,y: row.AVG15 });
								}
								if (row.AVG16 != undefined && x == 16) {     //第十七條線  
									Array0.push({x: row.YEARMON,y:row.AVG16 });
								}
								if (row.AVG17 != undefined && x == 17) {     //第十八條線  
									Array0.push({x: row.YEARMON,y: row.AVG17 });
								}
								if (row.AVG18 != undefined && x == 18) {     //第十九條線  
									Array0.push({x:  row.YEARMON,y: row.AVG18 });
								}
								if (row.AVG19 != undefined && x == 19) {
									Array0.push({x:  row.YEARMON,y:row.AVG19 });   //第二十條線
								}
								if (row.AVG20 != undefined && x == 20) {
									Array0.push({x:  row.YEARMON,y:row.AVG20 });    //第二十一條線
								}
								if (row.AVG21 != undefined && x == 21) {
									Array0.push({x:  row.YEARMON,y: row.AVG21});     //第二十二條線
								}
								if (row.AVG22 != undefined && x == 22) {
									Array0.push({x:  row.YEARMON,y: row.AVG22 });     //第二十三條線
								}
								if (row.AVG23 != undefined && x == 23) {
									Array0.push({x:  row.YEARMON,y: row.AVG23 });      //第二十四條線
								}
								if (row.AVG24 != undefined && x == 24) {
									Array0.push({x:  row.YEARMON,y: row.AVG24 });      //第二十五條線
								}
								if (row.AVG25 != undefined && x == 25) {
									Array0.push({x:  row.YEARMON,y: row.AVG25 });       //第二十六條線
								}
							});
							/**************KEY是顯示欄位名稱,VALUE是X時間,Y值*****************/
							//==  除了全行平均  ==  各線條設定
 							if ((x + 1) != $scope.paramList.length 
 									) {
// 								alert(JSON.stringify($scope.paramList.length));
 								nam.push({
 									"key" : $scope.inputVO.type
 											+ $scope.paramList[x + 1].LABEL,
 									"values" : Array0
 								});
 							}			
 							
							Array0 = [];
						}

						//*******====以下放置圖的資料 END===*********//
						//====全行平均 線=====

						//**===設定data===**
						nam.push({
							"key" : "全行平均",
							"values" : Array
						});
						//清空 全行平均
						Array=[];
						//設定data  <------圖的資料
						$scope.data = nam;  
						//設定  區域中心 / 營運區  /分行  為空字串
						$scope.inputVO.ROB = "";
						$scope.aotype = '0';
						
						$scope.options = {
								chart : {
									type : 'lineChart',
									height : 480,
									margin : {
										   top: 20,
						                   right: 20,
						                   bottom: 60,
						                   left: 80
									},
									clipEdge : false,
									duration : 1000,    //控制速度
									useInteractiveGuideline : true,
									xAxis : {
										axisLabel : '',
										showMaxMin : true,
										 "staggerLabels": true,
										  tickValues:[1454198400000
										              ,1456704000000
										              ,1459382400000
										              ,1461974400000
										              ,1464652800000
										              ,1467244800000
										              ,1469923200000
										              ,1472601600000
										              ,1475193600000
										              ,1477872000000
										              ,1480464000000
										              ,1483142400000
										              ,1485820800000
										              ,1488220800000],
										tickFormat : function(d) {
											return d3.time.format('%Y/%m')(new Date(d));
										}
									},
									yAxis : {
										  axisLabel: '',
										  showMaxMin: true,
										
										tickFormat : function(d) {
											return d3.format(',.2f')(d);
											
										}
									
									},
									x2Axis : {
										showMaxMin : false,
										 "staggerLabels": true,
										tickFormat : function(d) {
											return d3.time.format('%Y-%m-%d')(new Date(d));
										}
									},

									y2Axis : {
										tickFormat : function(d) {
											return d3.format(',.2f')(d);
										}
									},

								}
							};
						return;
					}

				});
//		}
	};
	$scope.inquire();

	//轉換type  ng-change
	$scope.types = function() {
//		if($scope.inputVO.aocode!=''){
//			$scope.brchs();
//			return;
//		}
//		if($scope.inputVO.branch!='')	{
//			$scope.bran();
//			return;
//		}
//		if($scope.inputVO.op!=''){
//			$scope.genBranch();
//			return;
//		}	
//		if($scope.inputVO.region!=''){
//			$scope.genArea();
//			return;
//		}	

		if($scope.inputVO.aocode!=''){
			$scope.brchs();
			return;
		}
		if($scope.inputVO.branch_nbr!='')	{
			$scope.bran();
			return;
		}
		if($scope.inputVO.branch_area_id!=''){
			$scope.genBranch();
			return;
		}	
		if($scope.inputVO.region_center_id!=''){
			$scope.genArea();
			return;
		}	
		$scope.genRegion();
	};
	
	//使用在理專判斷 ng-change   
	$scope.brchs = function() {
//		if($scope.mappingSet['aocodes'].length>0 && $scope.mappingSet['branch'].length>0 && $scope.mappingSet['op'].length>0 && $scope.mappingSet['region'].length>0){
//		$scope.bran();
//		return;
//	}
		
//		if($scope.AO_LIST.length>0 && $scope.AREA_LIST.length>0 && $scope.BRANCH_LIST.length>0 && $scope.REGION_LIST.length>0){
//		$scope.bran();
//		return;
//	}

		
		if($scope.inputVO.aocode!=''){
			if($scope.inputVO.branch_nbr=='' || $scope.inputVO.branch_area_id=='' || $scope.inputVO.region_center_id==''){
//				$scope.inquire();
				$scope.showMsg("需選分行別、業務處及營運處");
				return;
			}
			$scope.inquire();
			return;
		}
	};


	/** *以下連動區域中心.營運區.分行別** */
	// 大區域資訊
	$scope.genRegion = function() {
//		$scope.REGION_LIST = [];
//		$scope.inputVO.aocode='';  //清空理專  員編
//		$scope.mappingSet['region'] = [];
//		$scope.AREA_LIST = [];
//		$scope.mappingSet['op'] = [];
//		$scope.inputVO.region = "";
//		$scope.inputVO.op = "";
//		$scope.inputVO.branch_nbr = "";
////		$scope.REGION_LIST =[];
//		angular.forEach(projInfoService.getAvailRegion(), function(row, index,
//				objs) {
//			$scope.mappingSet['region'].push({
//				LABEL : row.REGION_CENTER_NAME,
//				DATA : row.REGION_CENTER_ID
//			});
//			if ($scope.mappingSet['region'].length > 0) {
//				$scope.inputVO.ROB = "1"; // 營運區為一
//			}
//		});
//		if ($scope.mappingSet['region'].length > 0) {
//			$scope.paramList = $scope.mappingSet['region'];
//		}
		//可視範圍啟動
//	    $scope.dateChange();

//		if($scope.REGION_LIST.length >0){
//		}
//		$scope.paramList = $scope.BRANCH_LIST;

		
		$scope.inputVO.ROB = "1"; // 營運區為一
		$scope.inputVO.aocode='';

		if($scope.REGION_LIST != undefined){
			$scope.paramList = $scope.REGION_LIST;
			if($scope.inputVO.sTime && $scope.inputVO.eTime)
			$scope.inquire();

		}

	};
	$scope.genRegion();

	// 區域資訊
	$scope.genArea = function() {

//		$scope.inputVO.branch_area_id = [];

//		$scope.inputVO.op='';
//		$scope.inputVO.op = [];
//		$scope.mappingSet['op'] = [];
//		angular.forEach(projInfoService.getAvailArea(), function(row, index,
//				objs) {
//
//			if (row.REGION_CENTER_ID == $scope.inputVO.region) {
//
//				$scope.mappingSet['op'].push({
//					LABEL : row.BRANCH_AREA_NAME,
//					DATA : row.BRANCH_AREA_ID
//				});
//				$scope.inputVO.ROB = "2";
//			}
//
//		});
//		if ($scope.mappingSet['op'].length > 0) {
//			$scope.paramList = $scope.mappingSet['op'];
//		}
//		if ($scope.mappingSet['op'].length == 0) {
//			$scope.genRegion();
//		}
		
//		if($scope.REGION_LIST.length > 1){
//		$scope.paramList = $scope.REGION_LIST;

//		$scope.genRegion();
//		}
//		if (row.REGION_CENTER_ID == $scope.inputVO.region_center_id) {
		$scope.inputVO.ROB = "2";		
		$scope.inputVO.aocode='';
//		$scope.inputVO.branch_nbr='';  
//		$scope.inputVO.branch_area_id='';
//		$scope.BRANCH_LIST = [];
		
		if($scope.inputVO.region_center_id !=''){
			$scope.paramList = $scope.AREA_LIST;
		}
		//營運區不等於空就跳去營運區
		if($scope.inputVO.branch_area_id !=''){
			$scope.inputVO.aocode='';

			$scope.genBranch();
			return;
		}
		//分行別不等於空就跳去分行別
		if($scope.inputVO.branch_nbr !='') {
			$scope.inputVO.aocode='';

			$scope.bran();
			return;
		}
//		}
		if($scope.AREA_LIST.length > 20){
			$scope.paramList = $scope.REGION_LIST;
		}
//		if($scope.inputVO.branch_area_id != ''){
//			$scope.paramList = $scope.BRANCH_LIST;
//			return;
//		}
		$scope.inquire();
		
	};
	
	/***返回葉面***/
	$scope.returnback = function() {
		var path = $rootScope.menuItemInfo.txnPath;
		var paths=[];
		for(var i=0;i<3;i++)
			paths.push(path[i]);
		paths.push({'MENU_ID':"PMS328",'MENU_NAME':"客戶數"});
		$rootScope.GeneratePage({'txnName':"PMS328",'txnId':"PMS328",'txnPath':paths});
		return;
	};
	
	/**返回上一頁**/
	$scope.returnback1 = function() {
		$scope.connector('set','XPMS328_M1', $scope.inputVO.sTime ); //時間
		$scope.connector('set','XPMS328_M2', $scope.inputVO.eTime ); //時間
		$scope.connector('set','XPMS328_R', $scope.inputVO.region_center_id ); //業務處
		$scope.connector('set','XPMS328_BA', $scope.inputVO.branch_area_id ); //營運區
		$scope.connector('set','XPMS328_BN', $scope.inputVO.branch_nbr ); //分行別
		$scope.connector('set','XPMS328_A', $scope.inputVO.ao_code ); //理專

		$rootScope.menuItemInfo.url = "assets/txn/PMS328/PMS328.html";

	}

	
	/******分行別******/
	$scope.bran = function() {
		
//		$scope.sendRecv("PMS202", "aoCodeView",
//				"com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", {},
//				function(totas, isError) {
//					if (isError) {
//						$scope.showErrorMsg(totas[0].body.msgData);
//					}
//					if (totas.length > 0) {
//						$scope.mappingSet['aocodes'] = [];
//						angular.forEach(totas[0].body.aolist, function(row,
//								index, objs) {
//							if (row.BRANCH_NBR == $scope.inputVO.branch) {
//								$scope.mappingSet['aocodes'].push({
//									LABEL : row.NAME,
//									DATA : row.EMP_ID
//								});
//								//rob設定初始值
//								$scope.inputVO.ROB = "4";
//							}
//						});
//					};
//					if ($scope.mappingSet['aocodes'].length >= 1) {
//						$scope.paramList = $scope.mappingSet['aocodes'];
//						$scope.aotype = '1';
//					}
////					if(row.BRANCH_NBR == $scope.inputVO.branch_nbr)
//					$scope.inputVO.ROB = "4";
//					$scope.mappingSet['aocodes']=[];
//					angular.forEach($scope.AO_LIST, function(row,
//							index, objs) {
//						if (row.BRANCH_NBR == $scope.inputVO.branch_nbr) {
//							$scope.mappingSet['aocodes'].push({
//								LABEL : row.NAME,
//								DATA : row.EMP_ID
//							});
//							//rob設定初始值
//							$scope.inputVO.ROB = "4";
//						}
//					});
		
					//分行別為空就跳去營運區
					if($scope.inputVO.branch_nbr=='')		
					{
						$scope.inputVO.aocode='';
						$scope.genBranch();
					 return;
					}

					$scope.inputVO.ROB = "4";
					$scope.mappingSet['aocodes']=[];
					$scope.inputVO.aocode='';  //清空理專  員編
					if($scope.inputVO.branch_nbr !='' ){
						$scope.paramList =$scope.AO_LIST;
						$scope.aotype = '1';
					} 
//					if($scope.inputVO.branch_area_id =='' ){
//						$scope.paramList =$scope.AO_LIST;
//						$scope.aotype = '1';
//					} 
					$scope.inquire();
					
//				});
	};

	// 分行資訊(營運區)
	$scope.genBranch = function() {
//		
//		$scope.BRANCH_LIST = [];
//		$scope.inputVO.branch_nbr='';   //
//		angular.forEach(projInfoService.getAvailBranch(), function(row, index,
//				objs) {
//			if (row.BRANCH_AREA_ID == $scope.inputVO.op) {
//				$scope.mappingSet['branch'].push({
//					LABEL : row.BRANCH_NAME,
//					DATA : row.BRANCH_NBR
//				});
//				$scope.inputVO.ROB = "3";
//			}
//		});
//		if ($scope.mappingSet['branch'].length > 0) {
//			$scope.paramList = $scope.mappingSet['branch'];
//		}
		
		//營運區為空跳去業務處
		if($scope.inputVO.branch_area_id=='')		
		{
			$scope.inputVO.aocode='';
			$scope.genArea();
		 return;
		}

		
		$scope.inputVO.aocode='';   //清空理專  員編
		$scope.inputVO.ROB = "3";
		if($scope.BRANCH_LIST.length>0){
			//分行別不等於空就跳去分行別
			if($scope.inputVO.branch_nbr !='' ){
				$scope.bran();
				return;
			} 
			$scope.paramList = $scope.BRANCH_LIST;

		}
		
		$scope.inquire();

	};


});
