/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS000Controller', function(sysInfoService,$scope,$rootScope, $controller, $confirm, socketService, ngDialog, projInfoService,getParameter, $q) {
	$controller('BaseController', {$scope : $scope});  //改在此撈取system參數
	$scope.controllerName = "PMS000Controller";
	//STEP.1-1宣告各個變數
	var JSON_LOG_ID ;
	var JSON_LOG_LABEL = [];
	var JSON_ID = [];
	//STEP.1-2撈取system參數
	$scope.priID = function() {
		var deferred = $q.defer();
		getParameter.XML(["PMS.FUNCTION_PRI","PMS.FUNCTION_NAME"], function(totas) {
			if (totas) {
				$scope.mappingSet['PMS.FUNCTION_PRI'] = totas.data[totas.key.indexOf('PMS.FUNCTION_PRI')];
				$scope.mappingSet['PMS.FUNCTION_NAME'] = totas.data[totas.key.indexOf('PMS.FUNCTION_NAME')];
				deferred.resolve();
			}
		});
		return deferred.promise;
	};
	//STEP.1-3設定撈取到值後再做後續。
	$scope.priID().then(function(data) {
	//STEP.2設定登入權限
	angular.forEach($scope.mappingSet['PMS.FUNCTION_PRI'], function(row, index, objs){
		if($scope.mappingSet['PMS.FUNCTION_PRI'][index].DATA == sysInfoService.getPriID()){ //篩選
			JSON_LOG_ID = $scope.mappingSet['PMS.FUNCTION_PRI'][index].DATA;      //放登入者PrID
			JSON_LOG_LABEL = $scope.mappingSet['PMS.FUNCTION_PRI'][index].LABEL;  //放登入權限集合
		}
	});
	//STEP.3設定全功能名稱
	angular.forEach($scope.mappingSet['PMS.FUNCTION_NAME'], function(row, index, objs){
		JSON_ID.push(row.DATA);  //放ID
	});
	
	$scope.orgTree = null;
	$scope.orgTree1 = null;
	$scope.orgTree2 = null;
	$scope.orgTree3 = null;
	$scope.selectedTreeNode = null;
	$scope.selectedTreeNode1 = null;
	$scope.selectedTreeNode2 = null;
	$scope.selectedTreeNode3 = null;
	var setting = {
		data : {
			simpleData : {
				enable : true
			}
		},
		callback : {
			onClick : function(event, treeId, treeNode) {
				$scope.orgClick(event, treeId, treeNode)
			}
		},
		view : {
			dblClickExpand : false
		}
	};
	var setting1 = {
			data : {
				simpleData : {
					enable : true
				}
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					$scope.orgClick1(event, treeId, treeNode)
				}
			},
			view : {
				dblClickExpand : false
			}
		};
	var setting2 = {
			data : {
				simpleData : {
					enable : true
				}
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					$scope.orgClick2(event, treeId, treeNode)
				}
			},
			view : {
				dblClickExpand : false
			}
		};
	var setting3 = {
			data : {
				simpleData : {
					enable : true
				}
			},
			
			callback : {
				onClick : function(event, treeId, treeNode) {
					$scope.orgClick3(event, treeId, treeNode)
				}
			},
			view : {
				dblClickExpand : false
			}
		};
	var zNodes = [];
	var zNodes1 = [];
	var zNodes2 = [];
	var zNodes3 = [];
	var parentLst = {};
	var parentLst1 = {};
	var parentLst2 = {};
	var parentLst3 = {};

	$scope.mappingSet['parentDeptLst'] = [];
	

	

	$scope.orgClick = function(event, treeId, treeNode) {
		if((treeNode.id+"").length==6)
		{
		var path = $rootScope.menuItemInfo.txnPath;
//		$scope.connector('set', "CRM251_ROW", row);
//		$scope.connector('set', "CRM251_PAGE", "CRM251");
		path.push({'MENU_ID':treeNode.id+"",'MENU_NAME':treeNode.name+""});
		$rootScope.GeneratePage({'txnName':treeNode.id+"",'txnId':treeNode.id+"",'txnPath':path});
//		$rootScope.menuItemInfo.url = "assets/txn/"+treeNode.id+"/"+treeNode.id+".html";
//		$scope.connector('set','PMS_TITLE',treeNode.name+"");
//		$rootScope.menuItemInfo.url = "assets/txn/"+treeNode.id+"/"+treeNode.id+".html";
		return;
		}
		
	if (treeNode.isParent) {
		$scope.orgTree.removeChildNodes(treeNode);
	} else {
				var newNode = [];
				var newNodes=[{
					'id' : 'PMS110',
					'pId' : '10',
					'name' : 'PIPELINE計劃'
				},{
					'id' : 'PMS120',
					'pId' : '10',
					'name' : '預估核貸量'
				}
				];
					if(JSON_LOG_ID == sysInfoService.getPriID()){
						for(var a=0; a<JSON_LOG_LABEL.split(',').length ; a++){
							if(JSON_LOG_LABEL.split(',')[a] == 'X'){
								for(var j=0;j<newNodes.length;j++){
									if(newNodes[j].id==JSON_ID[a]){
										newNodes.splice(j,1);
									}
								}
							}
						}
					}

				angular.forEach(newNodes, function(row, index, objs){							
					if(treeNode.id==row.pId)
						{
							newNode.push(row);								
						}
				});
				
			
				$scope.orgTree.addNodes(treeNode, newNode);
		
	}

};
	
	$scope.orgClick3 = function(event, treeId, treeNode) {
//		$scope.connector('set','PMS_TITLE',treeNode.name+"");
//		$rootScope.menuItemInfo.url = "assets/txn/"+treeNode.id+"/"+treeNode.id+".html";		
		var path = $rootScope.menuItemInfo.txnPath;
//		$scope.connector('set', "CRM251_ROW", row);
//		$scope.connector('set', "CRM251_PAGE", "CRM251");
		path.push({'MENU_ID':treeNode.id+"",'MENU_NAME':treeNode.name+""});
		$rootScope.GeneratePage({'txnName':treeNode.id+"",'txnId':treeNode.id+"",'txnPath':path});
//		$rootScope.menuItemInfo.url = "assets/txn/"+treeNode.id+"/"+treeNode.id+".html";
		return;
	};
	
	
	
	$scope.orgClick1 = function(event, treeId, treeNode) {
		if((treeNode.id+"").length==6)
			{
			var path = $rootScope.menuItemInfo.txnPath;
//			$scope.connector('set', "CRM251_ROW", row);
//			$scope.connector('set', "CRM251_PAGE", "CRM251");
			path.push({'MENU_ID':treeNode.id+"",'MENU_NAME':treeNode.name+""});
			$rootScope.GeneratePage({'txnName':treeNode.id+"",'txnId':treeNode.id+"",'txnPath':path});
//			$rootScope.menuItemInfo.url = "assets/txn/"+treeNode.id+"/"+treeNode.id+".html";
//			$scope.connector('set','PMS_TITLE',treeNode.name+"");
//			$rootScope.menuItemInfo.url = "assets/txn/"+treeNode.id+"/"+treeNode.id+".html";
			return;
			}
			
		if (treeNode.isParent) {
			$scope.orgTree1.removeChildNodes(treeNode);
		} else {
					var newNode = [];
					var newNodes=[{
						'id' : 'PMS203',
						'pId' : '20',
						'name' : '生產力目標維護'
					},{
						'id' : 'PMS204',
						'pId' : '20',
						'name' : '主管評估頻率維護'
					},{
						'id' : 'PMS205',
						'pId' : '20',
						'name' : '追蹤產品設定'
					},{
						'id' : 'PMS228',
						'pId' : '20',
						'name' : '消金PS生產力目標維護'
					},{
						'id' : 'PMS206',
						'pId' : '21',
						'name' : 'AMC活動量'
					},{
						'id' : 'PMS207',
						'pId' : '21',
						'name' : '預估收益'
					},{
						'id' : 'PMS208',
						'pId' : '22',
						'name' : '落點排名(含晉級目標達成率)'
					},{
						'id' : 'PMS209',
						'pId' : '22',
						'name' : '競爭力趨勢'
					},{
						'id' : 'PMS210',
						'pId' : '23',
						'name' : 'CNR獎金指標參數維護' 
					},{
						'id' : 'PMS211',
						'pId' : '23',
						'name' : 'CNR獎金計算作業'
					},{
						'id' : 'PMS212',
						'pId' : '23',
//						'name' : '客戶歷史AOCODE計績修改'  //OLD
						'name' : '調整客戶歷史AOCODE' 
					},{
						'id' : 'PMS213',
						'pId' : '23',
						'name' : '調整客戶產品收益'
					},{
						'id' : 'PMS214',
						'pId' : '23',
						'name' : '調整理專AUM&客戶數' 
					},{
						'id' : 'PMS717',
						'pId' : '23',
						'name' : '歷史月份商品主檔調整' 
					},{
						'id' : 'PMS215',
						'pId' : '24',
//						'name' : '理專財務非財務報表' //OLD
						'name' : 'FC理專財務非財務報表'
					},{
						'id' : 'PMS705',
						'pId' : '24',
						'name' : 'FCH理專財務非財務報表'
					},{
						'id' : 'PMS216',
						'pId' : '24',
//						'name' : 'FC獎勵金查詢' //OLD
						'name' : 'FC獎勵金報表' 
					},{
						'id' : 'PMS217',
						'pId' : '24',
//						'name' : 'FCH獎勵金查詢'//OLD
						'name' : 'FCH獎勵金報表'
					},{
						'id' : 'PMS218',
						'pId' : '24',
						'name' : '理專計績商品報表'
					},{
						'id' : 'PMS707',
						'pId' : '25',
						'name' : '消金獎勵金參數設定'
					},{
						'id' : 'PMS225',
						'pId' : '25',
//						'name' : '消金專員獎勵金查詢'//OLD
						'name' : '消金PS獎勵金'
					}
					
					];
						if(JSON_LOG_ID == sysInfoService.getPriID()){
							for(var a=0; a<JSON_LOG_LABEL.split(',').length ; a++){
								if(JSON_LOG_LABEL.split(',')[a] == 'X'){
									for(var j=0;j<newNodes.length;j++){
										if(newNodes[j].id==JSON_ID[a]){
											newNodes.splice(j,1);
										}
									}
								}
							}
						}

					angular.forEach(newNodes, function(row, index, objs){							
						if(treeNode.id==row.pId)
							{
								newNode.push(row);								
							}
					});
					
				
					$scope.orgTree1.addNodes(treeNode, newNode);
			
		}

	};
	
	
	$scope.orgClick2 = function(event, treeId, treeNode) {
		
		$scope.selectedTreeNode = treeNode;
	
		if((treeNode.id+"").length==6)
			{
			var path = $rootScope.menuItemInfo.txnPath;
//			$scope.connector('set', "CRM251_ROW", row);
//			$scope.connector('set', "CRM251_PAGE", "CRM251");
			path.push({'MENU_ID':treeNode.id+"",'MENU_NAME':treeNode.name+""});
			$rootScope.GeneratePage({'txnName':treeNode.id+"",'txnId':treeNode.id+"",'txnPath':path});
//			$rootScope.menuItemInfo.url = "assets/txn/"+treeNode.id+"/"+treeNode.id+".html";
//			$scope.connector('set','PMS_TITLE',treeNode.name+"");
//			$rootScope.menuItemInfo.url = "assets/txn/"+treeNode.id+"/"+treeNode.id+".html";
			return;
			}
			
		if (treeNode.isParent) {
			$scope.orgTree2.removeChildNodes(treeNode);
		} else {
			
					var newNode = [];
					var newNodes=[{
						'id' : 'PMS303',
						'pId' : '30',
						'name' : '分行保險戰報統計' 
					},{
						'id' : 'PMS304',
						'pId' : '30',
						'name' : '分行保險戰報明細' 
					},{
						'id' : 'PMS305',
						'pId' : '30',
						'name' : '險種別統計戰報' 
					},{
						'id' : 'PMS306',
						'pId' : '30',
						'name' : '保險戰報作業' 
					},{
						'id' : 'PMS308',
						'pId' : '31',
						'name' : '分行收益達成率'
					},{
						'id' : 'PMS309',
						'pId' : '31',
						'name' : '各商品收益/各商品銷量'
					},{
						'id' : 'PMS310',
						'pId' : '32',
						'name' : '分行消金戰報業績目標設定'
					},{
						'id' : 'PMS311',
						'pId' : '32',
						'name' : '房信貸PS生產力週報' 
					},{
						'id' : 'PMS313',
						'pId' : '32',
						'name' : '房信貸撥款及好運貸保費戰報' 
					},{
						'id' : 'PMS312',
						'pId' : '32',
						'name' : '房信貸案件來源統計表' 
					},{
						'id' : 'PMS324',
						'pId' : '33',
						'name' : '理專晉級報表'
					},{
						'id' : 'PMS325',
						'pId' : '33',
						'name' : '理專降級報表'
					},{
						'id' : 'PMS328',
						'pId' : '34',
						'name' : '客戶數'
					},{
						'id' : 'PMS329',
						'pId' : '34',
						'name' : 'AUM'
					},{
						'id' : 'PMS330',
						'pId' : '34',
						'name' : '理專各級客戶數增減'
					},{
						'id' : 'PMS332',
						'pId' : '34',
						'name' : '百大貢獻度客戶報表'
					},{
						'id' : 'PMS333',
						'pId' : '34',
//						'name' : '未掛Code客戶報表' //OLD
						'name' : '未掛Code客戶'
					},{
						'id' : 'PMS334',
						'pId' : '34',
						'name' : '分行最適經營客戶管理報表'
					},{
						'id' : 'PMS335',
						'pId' : '34',
						'name' : '重覆經營客戶報表'
					},{
						'id' : 'PMS353',
						'pId' : '35',
//						'name' : 'IPO戰報參數設定' //OLD
						'name' : 'IPO專案戰報參數設定'
					},{
						'id' : 'PMS354',
						'pId' : '35',
//						'name' : '投資IPO專案戰報 ' //OLD
						'name' : 'IPO專案戰報查詢'
					},{
						'id' : 'PMS711', //從PMS711改為PMS326  -->改為PMS711
						'pId' : '36',
						'name' : 'KPI指標設定' 
					},{
						'id' : 'PMS715', //從PMS715改為PMS327  -->改為PMS715 
						'pId' : '36',
						'name' : '年度KPI成績查詢'
					},{
						'id' : 'PMS716', //從PMS715改為PMS327  -->改為PMS715 
						'pId' : '36',
						'name' : 'KPI其他非系統計算人工上傳'
					},{
						'id' : 'PMS337',  
						'pId' : '37',
						'name' : '保險月底提存及月中核實報表'
					},{
						'id' : 'PMS338',
						'pId' : '37',
						'name' : '房貸壽險佣金報表'
					},{
						'id' : 'PMS339',
						'pId' : '37',
						'name' : '房貸壽險獎勵金報表'
					},{
						'id' : 'PMS340',
						'pId' : '37',
						'name' : '房貸壽險獎勵金明細表'
					},{
						'id' : 'PMS341',
						'pId' : '37',
						'name' : '房貸火地險佣金明細'
					},{
						'id' : 'PMS336',  
						'pId' : '38',
						'name' : '輔銷目標達成報表'
					},{
						'id' : 'PMS342',
						'pId' : '39',
						'name' : '大額異動報表'
					},{
						'id' : 'PMS343',
						'pId' : '39',
						'name' : '基金/ETF/股票贖回金流報表'
					},{
						'id' : 'PMS344',
						'pId' : '39',
						'name' : '投資商品TOP10排名'
					},{
						'id' : 'PMS345',
						'pId' : '39',
						'name' : '新契約照會日報'
					},{
						'id' : 'PMS346',
						'pId' : '39',
						'name' : '非新契約照會日報'
					},{
						'id' : 'PMS347',
						'pId' : '39',
						'name' : '固定收益型商品提前解約損失日報表'
					},{
						'id' : 'PMS348',
						'pId' : '39',
						'name' : '固定收益型商品提前解約損失月報表'
					},{
						'id' : 'PMS349',
						'pId' : '39',
						'name' : '客戶整體損益分析'
					},{
						'id' : 'PMS350',
						'pId' : '39',
//						'name' : '動態報表' //OLD
						'name' : '動態報表設定人員'
					}
//					,{
//						'id' : 'PMS355', //已刪除
//						'pId' : '35',
//						'name' : '保險IPO專案戰報' 
//					}
					];
					//新版篩選
					if(JSON_LOG_ID == sysInfoService.getPriID()){
						for(var a=0; a<JSON_LOG_LABEL.split(',').length ; a++){
							if(JSON_LOG_LABEL.split(',')[a] == 'X'){
								for(var j=0;j<newNodes.length;j++){
									if(newNodes[j].id==JSON_ID[a]){
										newNodes.splice(j,1);
									}
								}
							}
						}
					}
						angular.forEach(newNodes, function(row, index, objs){							
							if(treeNode.id==row.pId)
								{newNode.push(row);								
								}
						});
					$scope.orgTree2.addNodes(treeNode, newNode);
		}

	};

	$scope.init = function() {
		zNodes=[{
			'id' : 'PMS101',
			'pId' : '1',
			'name' : '金流名單'
		},{
			'id' : 'PMS108',
			'pId' : '1',
			'name' : '潛力金流名單系統參數設定'
		},{
			'id' : 'PMS102',
			'pId' : '1',
			'name' : '潛力金流名單' //old
//			'name' : '潛力金流客戶'
		},{
			'id' : 'PMS103',
			'pId' : '1',
			'name' : '檢視銷售計劃明細'
		},{
			'id' : 'PMS104',
			'pId' : '1',
			'name' : '金流運用分析'
		},{
			'id' : 'PMS105',
			'pId' : '1',
			'name' : '金流已入帳未規劃'
		},{
			'id' : 'PMS106',
			'pId' : '1',
			'name' : '到期金流月統計報表'
		},{
			'id' : 'PMS107',
			'pId' : '1',
			'name' : '陪訪成效分析'
		},{
			'id' : '10',
			'pId' : '1',
			'name' : '消金業務計劃' ,open:true
		},{
			'id' : 'PMS110',
			'pId' : '10',
			'name' : 'PIPELINE計劃'
		},{
			'id' : 'PMS120',
			'pId' : '10',
			'name' : '預估核貸量'
		},{
			'id' : 'PMS107',
			'pId' : '1',
			'name' : '陪訪成效分析'
		}];
		
		zNodes1=[{
			'id' : 'PMS201',
			'pId' : '1',
			'name' : '理專個人生產力診斷書'
		},{
			'id' : 'PMS202',
			'pId' : '1',
			'name' : '主管評估排程管理'
		},{
			'id' : '20',
			'pId' : '1',
			'name' : '生產力診斷書維護' ,open:true
		},{
			'id' : 'PMS203',
			'pId' : '20',
			'name' : '生產力目標維護'
		},{
			'id' : 'PMS204',
			'pId' : '20',
			'name' : '主管評估頻率維護'
		},{
			'id' : 'PMS205',
			'pId' : '20',
//			'name' : '追蹤產品設定' //OLD
			'name' : '追蹤商品設定'	
		},{
			'id' : 'PMS228',
			'pId' : '20',
			'name' : '消金PS生產力目標維護'
		},{
			'id' : '21',
			'pId' : '1',
			'name' : '差異分析' ,open:true
		},{
			'id' : 'PMS206',
			'pId' : '21',
			'name' : 'AMC活動量'
		},{
			'id' : 'PMS207',
			'pId' : '21',
			'name' : '預估收益'
		},{
			'id' : '22',
			'pId' : '1',
			'name' : '競爭力分析' ,open:true
		},{
			'id' : 'PMS208',
			'pId' : '22',
			'name' : '落點排名(含晉級目標達成率)'
		},{
			'id' : 'PMS209',
			'pId' : '22',
			'name' : '競爭力趨勢'
		},{
			'id' : '23',
			'pId' : '1',
			'name' : 'CNR獎金作業' ,open:true
		},{
			'id' : 'PMS210',
			'pid' : '23',
			'name' : 'CNR獎金指標參數維護'
		},{
			'id' : 'PMS211',
			'pid' : '23',
			'name' : 'CNR獎金計算作業' 
		},{
			'id' : 'PMS212',
			'pid' : '23',
//			'name' : '客戶歷史AOCODE計績修改'  //OLD
			'name' : '調整客戶歷史AOCODE'
		},{
			'id' : 'PMS213',
			'pid' : '23',
			'name' : '調整客戶產品收益' 
		},{
			'id' : 'PMS214',
			'pid' : '23',
			'name' : '調整理專AUM&客戶數' 
		},{
			'id' : 'PMS717',
			'pId' : '23',
			'name' : '歷史月份商品主檔調整' 
		},{
			'id' : 'PMS701',
			'pId' : '1',
			'name' : '調整客戶名單'
		},{
			'id' : 'PMS710',
			'pId' : '1',
			'name' : '調整獎勵金(整批)'
		},{
			'id' : '24',
			'pId' : '1',
			'name' : '理專獎勵金' ,open:true
		},{
			'id' : 'PMS215',
			'pId' : '24',
//			'name' : '理專財務非財務報表' //OLD
			'name' : 'FC理專財務非財務報表'
		},{
			'id' : 'PMS705',
			'pId' : '24',
			'name' : 'FCH理專財務非財務報表'
		},{
			'id' : 'PMS216',
			'pId' : '24',
//			'name' : 'FC獎勵金查詢' //OLD
			'name' : 'FC獎勵金報表' 
		},{
			'id' : 'PMS217',
			'pId' : '24',
//			'name' : 'FCH獎勵金查詢'//OLD
			'name' : 'FCH獎勵金報表'
		},{
			'id' : 'PMS218',
			'pId' : '24',
			'name' : '理專計績商品報表'
		},{
			'id' : 'PMS220',
			'pId' : '1',
			'name' : '主管財務非財務報表'
		},{
			'id' : 'PMS221',
			'pId' : '1',
			'name' : '主管獎勵金報表'
		},{
			'id' : 'PMS222',
			'pId' : '1',
			'name' : '分行收益報表'
		},{
			'id' : 'PMS702', //已從PMS223變更為PMS702
			'pId' : '1',
			'name' : '保險負項明細報表'
		},{
			'id' : 'PMS703', //已從PMS224變更為PMS703
			'pId' : '1',
			'name' : '保險新承作明細報表'
		},{
			'id' : 'PMS226',
			'pId' : '1',
			'name' : '新戶轉介查詢'
		},{
			'id' : 'PMS227',
			'pId' : '1',
			'name' : '個別商品轉介查詢'
		},{
			'id' : 'PMS704', //已刪除
			'pId' : '1',
			'name' : 'FCH轉介獎金查詢'
		},{
			'id' : 'PMS709', 
			'pId' : '1',
			'name' : 'FCH轉介客戶登錄'
		},{
			'id' : 'PMS714', 
			'pId' : '1',
			'name' : '每月保險同意書補簽報表'
		},{
			'id' : '25',
			'pId' : '1',
			'name' : 'PS獎勵金' ,open:true
		},{
			'id' : 'PMS707',
			'pId' : '25',
			'name' : '消金獎勵金參數設定'
		},{
			'id' : 'PMS225',
			'pId' : '25',
			'name' : '消金專員獎勵金查詢'
		}];
		
		zNodes2=[{
			'id' : 'PMS301',
			'pId' : '1',
			'name' : '分行即時速報'
		},{
			'id' : 'PMS302',
			'pId' : '1',
			'name' : '理專即時速報'
		},{
			'id' : '30',
			'pId' : '1',
			'name' : '分行保險戰報' , open:true
		},{
			'id' : 'PMS303',
			'pId' : '30',
			'name' : '分行保險戰報統計' 
		},{
			'id' : 'PMS304',
			'pId' : '30',
			'name' : '分行保險戰報明細' 
		},{
			'id' : 'PMS305',
			'pId' : '30',
			'name' : '險種別統計戰報' 
		},{
			'id' : 'PMS306',
			'pId' : '30',
			'name' : '保險戰報作業' 
		},{
			'id' : 'PMS307',
			'pId' : '1',
			'name' : '保險核實報表查詢'
		},{
			'id' : '31',
			'pId' : '1',
			'name' : '每日業績戰報' , open:true
		},{
			'id' : 'PMS308',
			'pId' : '31',
			'name' : '分行收益達成率'
		},{
			'id' : 'PMS309',
			'pId' : '31',
			'name' : '各商品收益/各商品銷量'
		},{
			'id' : 'PMS360',
			'pId' : '1',
			'name' : '專員生產力每日戰報'
		},{
			'id' : '32',
			'pId' : '1',
			'name' : '分行消金戰報' , open:true
		},{
			'id' : 'PMS310',
			'pId' : '32',
			'name' : '分行消金戰報業績目標設定'
		},{
			'id' : 'PMS311',
			'pId' : '32',
			'name' : '房信貸PS生產力週報' 
		},{
			'id' : 'PMS313',
			'pId' : '32',
			'name' : '房信貸撥款及好運貸保費戰報' 
		},{
			'id' : 'PMS312',
			'pId' : '32',
			'name' : '房信貸案件來源統計表' 
		},{
			'id' : 'PMS708',
			'pId' : '1',
			'name' : '專員信用卡業績上傳'
		},{
			'id' : '35',
			'pId' : '1',
			'name' : '專案戰報' , open:true
		},{
			'id' : 'PMS353',
			'pId' : '35',
//			'name' : 'IPO戰報參數設定' //OLD
			'name' : 'IPO專案戰報參數設定'
		},{
			'id' : 'PMS354',
			'pId' : '35',
//			'name' : '投資IPO專案戰報 ' //OLD
			'name' : 'IPO專案戰報查詢'
		},{
			'id' : '333',
			'pId' : '1',
			'name' : '專案戰報' , open:true
		},{
			'id' : 'PMS324',
			'pId' : '33',
			'name' : '理專晉級報表'
		},{
			'id' : 'PMS325',
			'pId' : '33',
			'name' : '理專降級報表'
		},{
			'id' : '36',
			'pId' : '1',
			'name' : 'KPI及分行經營管理報表' ,open:true    
		},{
			'id' : 'PMS711',   //PMS326---->PMS711
			'pId' : '36',
			'name' : 'KPI指標設定' 
		},{
			'id' : 'PMS715',   //PMS327---->PMS715
			'pId' : '36',
			'name' : '年度KPI成績查詢'
		},{
			'id' : 'PMS716', //從PMS715改為PMS327  -->改為PMS715 
			'pId' : '36',
			'name' : 'KPI其他非系統計算人工上傳'
		},{
			'id' : 'PMS356',
			'pId' : '1',
			'name' : '存投保收益報表'
		},{
			'id' : 'PMS357',
			'pId' : '1',
			'name' : '各分行損益表'
		},{
			'id' : '34',
			'pId' : '1',
			'name' : '客戶經營報表' ,open:true    
		},{
			'id' : 'PMS328',
			'pId' : '34',
			'name' : '客戶數'
		},{
			'id' : 'PMS329',
			'pId' : '34',
			'name' : 'AUM'
		},{
			'id' : 'PMS330',
			'pId' : '34',
			'name' : '理專各級客戶數增減'
		},{
			'id' : 'PMS332',
			'pId' : '34',
			'name' : '百大貢獻度客戶報表'
		},{
			'id' : 'PMS333',
			'pId' : '34',
			'name' : '未掛Code客戶'
		},{
			'id' : 'PMS334',
			'pId' : '34',
			'name' : '分行最適經營客戶管理報表'
		},{
			'id' : 'PMS335',
			'pId' : '34',
			'name' : '重覆經營客戶報表'
		},{
			'id' : 'PMS358',
			'pId' : '1',
			'name' : '台幣定存增減報表'
		},{
			'id' : 'PMS359',
			'pId' : '1',
			'name' : '業務通路週報'
		},{
			'id' : '38',
			'pId' : '1',
			'name' : '輔銷管理報表' ,open:true    
		},{
			'id' : 'PMS336',  
			'pId' : '38',
			'name' : '輔銷目標達成報表'
		},{
			'id' : '37',
			'pId' : '1',
			'name' : '總行管理報表' ,open:true    
		},{
			'id' : 'PMS337',  
			'pId' : '37',
			'name' : '保險月底提存及月中核實報表'
		},{
			'id' : 'PMS338',
			'pId' : '37',
			'name' : '房貸壽險佣金報表'
		},{
			'id' : 'PMS339',
			'pId' : '37',
			'name' : '房貸壽險獎勵金報表'
		},{
			'id' : 'PMS340',
			'pId' : '37',
			'name' : '房貸壽險獎勵金明細表'
		},{
			'id' : 'PMS341',
			'pId' : '37',
			'name' : '房貸火地險佣金明細'
		},{
			'id' : '39',
			'pId' : '1',
			'name' : '其他管理報表' ,open:true    
		},{
			'id' : 'PMS342',
			'pId' : '39',
			'name' : '大額異動報表'
		},{
			'id' : 'PMS343',
			'pId' : '39',
			'name' : '基金/ETF/股票贖回金流報表'
		},{
			'id' : 'PMS344',
			'pId' : '39',
			'name' : '投資商品TOP10排名'
		},{
			'id' : 'PMS345',
			'pId' : '39',
			'name' : '新契約照會日報'
		},{
			'id' : 'PMS346',
			'pId' : '39',
			'name' : '非新契約照會日報'
		},{
			'id' : 'PMS347',
			'pId' : '39',
			'name' : '固定收益型商品提前解約損失日報表'
		},{
			'id' : 'PMS348',
			'pId' : '39',
			'name' : '固定收益型商品提前解約損失月報表'
		},{
			'id' : 'PMS349',
			'pId' : '39',
			'name' : '客戶整體損益分析'
		},{
			'id' : 'PMS350',
			'pId' : '39',
//			'name' : '動態報表' //OLD
			'name' : '動態報表設定人員'
		}
		];
		
		zNodes3=[{
			'id' : 'PMS401',
			'pId' : '1',
			'name' : '業務人員當日存款異動明細表'
		},{
			'id' : 'PMS402',
			'pId' : '1',
			'name' : '業務人員當月存款異動明細表'
		},{
			'id' : 'PMS403',
			'pId' : '1',
			'name' : '禁銷戶交易明細月報'
		},{
			'id' : 'PMS406',
			'pId' : '1',
			'name' : '基金短線進出交易報表'
		},{
			'id' : 'PMS407',
			'pId' : '1',
			'name' : '庫存資產減損月報'
		},{
			'id' : 'PMS408',
			'pId' : '1',
			'name' : '客戶6個月內提高KYC報表'
		},{
			'id' : 'PMS409',
			'pId' : '1',
			'name' : 'KYC例外管理報表'
		},{
			'id' : 'PMS410',
			'pId' : '1',
			'name' : '客戶投資風險承受度測試統計報表'
		},{
			'id' : 'PMS411',
			'pId' : '1',
			'name' : '風險部位警示報表'
		},{
			'id' : 'PMS412',
			'pId' : '1',
			'name' : '系統權限管理報表'
		},{
			'id' : 'PMS413',
			'pId' : '1',
			'name' : '分行電腦IP查詢與維護'
		},{
			'id' : 'PMS414',
			'pId' : '1',
			'name' : '公用電腦查核日報'
		}];
		
		/******依照登入Prid刪除不要項目******/
		if(JSON_LOG_ID == sysInfoService.getPriID()){
			for(var a=0; a<JSON_LOG_LABEL.split(',').length ; a++){
				if(JSON_LOG_LABEL.split(',')[a] == 'X'){
						for(var j=0;j<zNodes.length;j++){
							if(zNodes[j].id==JSON_ID[a]){
								zNodes.splice(j,1);
							}
						}
						for(var k=0;k<zNodes1.length;k++){
							if(zNodes1[k].id==JSON_ID[a]){
								zNodes1.splice(k,1);
							}
						}
						for(var l=0;l<zNodes2.length;l++){
							if(zNodes2[l].id==JSON_ID[a]){
								zNodes2.splice(l,1);
							}
						}
						for(var m=0;m<zNodes3.length;m++){
							if(zNodes3[m].id==JSON_ID[a]){
								zNodes3.splice(m,1);
							}
						}
					}
				}
			}
		$scope.orgTree  = $.fn.zTree.init($("#orgTree") , setting , zNodes );
		$scope.orgTree1 = $.fn.zTree.init($("#orgTree1"), setting1, zNodes1);
		$scope.orgTree2 = $.fn.zTree.init($("#orgTree2"), setting2, zNodes2);
		$scope.orgTree3 = $.fn.zTree.init($("#orgTree3"), setting3, zNodes3);
		$scope.orgClick1({},{},{});
		$scope.orgClick2({},{},{});
	};
	$scope.init();
	});
});
