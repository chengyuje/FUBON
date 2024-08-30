/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS214Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('PMSRegionController', {$scope: $scope});
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS214Controller";
		//使用共用可視範圍		
		$scope.dateChange = function(){
	        if($scope.inputVO.yearMon!=''){ 	
	        	$scope.inputVO.reportDate = $scope.inputVO.yearMon;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };
		$scope.init = function(){
			/*計積年月下拉框*/
			var NowDate = new Date();
	        var yr = NowDate.getFullYear();
	        var mm = NowDate.getMonth()+1;
	        var strmm='';
			$scope.mappingSet['timeE'] = [];
		        for(var i=0; i<12; i++){
		        	mm = mm -1;
		        	if(mm == 0){
		        		mm = 12;
		        		yr = yr-1;
		        	}
		        	if(mm<10)
		        		strmm = '0' + mm;
		        	else
		        		strmm = mm;        		
		        	$scope.mappingSet['timeE'].push({
		        		LABEL: yr+'/'+strmm,
		        		DATA: yr +''+ strmm
		        	});        
		        }
		        
		    /*軌跡類型下拉框*/
			$scope.mappingSet['adjType'] = [];		
        	$scope.mappingSet['adjType'].push({
        		LABEL: '原始資料',
        		DATA: '0'
        	});
        	$scope.mappingSet['adjType'].push({
        		LABEL: '人工調整',
        		DATA: '1'
        	});
        	$scope.mappingSet['adjType'].push({
        		LABEL: '系統調整',
        		DATA: '2'
        	});
        	$scope.mappingSet['adjType'].push({
        		LABEL: '上傳資料',
        		DATA: '3'
        	});
        	
        	/*調整類別下拉框*/
        	$scope.mappingSet['adjDesc'] = [];	
        	$scope.mappingSet['adjDesc'].push({
        		LABEL: '歷史AO CODE調整客戶',
        		DATA: '1'
        	});
        	$scope.mappingSet['adjDesc'].push({
        		LABEL: 'AC版塊調整客戶',
        		DATA: '2'
        	});
        	$scope.mappingSet['adjDesc'].push({
        		LABEL: '個金轉介法金客戶',
        		DATA: '3'
        	});
        	$scope.mappingSet['adjDesc'].push({
        		LABEL: '保險AUM遞延',
        		DATA: '4'
        	});
        	$scope.mappingSet['adjDesc'].push({
        		LABEL: 'RM TABLE 客戶',
        		DATA: '5'
        	});

//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: 'RM轉介客戶',
//        		DATA: 'RM轉介客戶'
//        	});
//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: '轉介個別商品客戶',
//        		DATA: '轉介個別商品客戶'
//        	});
//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: '個金轉介法金客戶',
//        		DATA: '個金轉介法金客戶'
//        	});
//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: 'RM TABLE 客戶',
//        		DATA: 'RM TABLE 客戶'
//        	});
//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: '歷史AO CODE調整客戶',
//        		DATA: '歷史AO CODE調整客戶'
//        	});
//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: '加碼專案客戶',
//        		DATA: '加碼專案客戶'
//        	});
//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: '保險負項佣金',
//        		DATA: '保險負項佣金'
//        	});
//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: '特定排除客戶',
//        		DATA: '特定排除客戶'
//        	});
//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: '保險同意書補簽',
//        		DATA: '保險同意書補簽'
//        	});
//        	$scope.mappingSet['adjDesc'].push({
//        		LABEL: '上月短TRAND交易補扣',
//        		DATA: '上月短TRAND交易補扣'
//        	});
			$scope.inputVO.userId = projInfoService.getUserID();
			$scope.outputLargeAgrList = '';
			//頁面初始化 清空查詢條件
						
			$scope.inputVO.yearMon = '';
			$scope.inputVO.AOCODE = '';  //2017/06/29改 AOCODE->ao_code
			$scope.inputVO.adjType = '';
			$scope.inputVO.custId = '';
			$scope.inputVO.assisId = '';  //2017/06/29改 assisId->emp_id 
			$scope.inputVO.adjDesc = '';
			$scope.inputVO.branch_nbr='';  //分行清空
			$scope.dateChange();
		}
		$scope.init();
		
		/**
		 * 計算獎勵金按鈕
		 */
		$scope.query = function(){
			/*判斷計積年月是否為空*/
			if(''==$scope.inputVO.yearMon || null==$scope.inputVO.yearMon){
				$scope.showErrorMsg("尚未選擇計積年月");
				return;
			}
			$scope.sendRecv("PMS214", "queryData","com.systex.jbranch.app.server.fps.pms214.PMS214QueryInputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						if(tota[0].body.errorMessage =='查詢筆數過多, 請增加查詢條件' ){
							$scope.showErrorMsg(tota[0].body.errorMessage);
							return;
						}
						$scope.outputLargeAgrList = tota[0].body.outputLargeAgrList;
						$scope.outputVO = tota[0].body;
						if(null==tota[0].body.outputLargeAgrList || tota[0].body.outputLargeAgrList.length <= 0){
							$scope.outputLargeAgrList=[];
							$scope.showMsg("ehl_01_common_009");
							return;
						}else{
//							for(var index=0; index<$scope.outputLargeAgrList.length; index++){
//								$scope.outputLargeAgrList[index].ADJ_TYPE = Number($scope.outputLargeAgrList[index].ADJ_TYPE) + 1;
//							}
						}
					}else{
						$scope.outputLargeAgrList=[];
						$scope.showBtn = 'none';
					}
				});
		};
		
	    
    	/**
		 * 匯出
		 */
    	$scope.exportRPT = function()
    	{
    		
			$scope.sendRecv("PMS214", "exportMain", "com.systex.jbranch.app.server.fps.pms214.PMS214QueryInputVO",
				$scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.errorMessage =='匯出筆數過多, 請增加查詢條件'){
								$scope.showErrorMsg("匯出筆數過多, 請增加查詢條件");
								return;
							}
							return;
						}
			});
		};		
    	
		
		
		
		/**
		 * 整批上傳功能
		 * 
		 */
		$scope.upload = function(yearmon){
			/*判斷計積年月是否為空*/
			if(''==$scope.inputVO.yearMon || null==$scope.inputVO.yearMon){
				$scope.showErrorMsg("尚未選擇計積年月");
				return;
			}
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS214/PMS214_UPLOAD.html',
				className: 'PMS214_UPLOAD',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.yearMon = yearmon;
	            }]
			});
			dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        		//	 $scope.query();
 				}
        	});
			
		}
		/**
		 * 匯出異常數據
		 * 
		 */
		$scope.download = function() {
			/*判斷計積年月是否為空*/
			if(''==$scope.inputVO.yearMon || null==$scope.inputVO.yearMon){
				$scope.showErrorMsg("尚未選擇計積年月");
				return;
			}
			$scope.sendRecv("PMS214", "export", "com.systex.jbranch.app.server.fps.pms214.PMS214InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							return;
						}else{
							if (tota.length > 0) 
							{
		                		if(tota[0].body.outputLargeAgrList && tota[0].body.outputLargeAgrList.length == 0) 
		                		{
		                			$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
		                	};
						}
					});
	    };
		
		/**
		 * 新增按鈕，在該行的下一行插入一條
		 * 
		 */
		$scope.add = function(event,index) {
			console.log('214');
			$scope.index = index;
			var trNode = $("<tr></tr>");
			$scope.addList = $scope.outputLargeAgrList[index];
			for(var value in $scope.addList){
				if(null == $scope.addList[value])
					$scope.addList[value] = '';
			}
			
			var inputNode8 = $("<input type='text' name='SAVE_INCREMENT' size='10' style='text-align:right;' />");
			var inputNode11 = $("<input type='text' name='SET_INCREMENT' size='10' style='text-align:right;' />");
			var inputNode14 = $("<input type='text' name='COVER_INCREMENT' size='10' style='text-align:right;' />");
			var inputNode17 = $("<input type='checkbox' checked disabled='true' name='adj'  style='text-align:center;'/>");
			var inputNode19 = $("<input type='text' name='ADJ_DESC' size='10' style='text-align:right;' />");
			var aNode1 = $("<button class='btn btn-info' name='a_save' style='text-align:center;' >儲存</button>")
			var aNode2 = $("<button class='btn btn-info' name='a_add_del' style='text-align:center;'></button>").html(" 取消");
			
			var tdNode8 = $("<td style='text-align:right;'></td>").append(inputNode8);
			var tdNode11 = $("<td style='text-align:right;'></td>").append(inputNode11);
			var tdNode14 = $("<td style='text-align:right;'></td>").append(inputNode14);
			var tdNode17 = $("<td style='text-align:center;'></td>").append(inputNode17);
			var tdNode19 = $("<td style='text-align:left;'></td>").append(inputNode19);
			var tdNode20 = $("<td align='center'></td>").append(aNode1,aNode2);
			
			var tdNode1 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.YEARMON);
			var tdNode2 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.CUSTID);
			var tdNode3 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.AOCODE);
			var tdNode4 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.LAST_AOCODE);
			var tdNode5 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.ASSIS_ID);
			var tdNode6 = $("<td style='text-align:right;color:blue'></td>").text($scope.addList.SAVE_AUM_BAL);
			var tdNode7 = $("<td style='text-align:right;color:blue'></td>").text($scope.addList.LAST_SAVE_AUM_BAL);
			var tdNode9 = $("<td style='text-align:right;color:blue'></td>").text($scope.addList.SET_AUM_BAL);
			var tdNode10 = $("<td style='text-align:right;color:blue'></td>").text($scope.addList.LAST_SET_AUM_BAL);
			var tdNode12 = $("<td style='text-align:right;color:blue'></td>").text($scope.addList.COVER_AUM_BAL);
			var tdNode13 = $("<td style='text-align:right;color:blue'></td>").text($scope.addList.LAST_COVER_AUM_BAL);
			var tdNode15 = $("<td style='text-align:right;color:blue'></td>").text($scope.addList.LAST_EIP_LEVEL);
			var tdNode16 = $("<td style='text-align:right;color:blue'></td>").text($scope.addList.EIP_LEVEL);
			var tdNode18 = $("<td style='text-align:center;color:blue'></td>").text($scope.mappingSet['adjType'][2].LABEL);
			
			trNode.append(tdNode1,tdNode2,tdNode3,tdNode4,tdNode5,tdNode6,tdNode7,
					tdNode8,tdNode9,tdNode10,tdNode11,tdNode12,tdNode13,tdNode14,tdNode15,tdNode16,tdNode17,tdNode18,tdNode19,tdNode20);
			$(event.target).parent().parent().after(trNode);
		};
		
		$(document).off("click");
		/**
		 * 取消按鈕
		 * 
		 */
		$(document).on("click","[name='a_add_del']",function(){
			var trNode =this.parentNode.parentNode;
			var tab = document.getElementById("showTab").children[1];
			tab.removeChild(trNode);
		});
		
		/**
		 *  儲存按鈕
		 */
		$(document).on("click","[name='a_save']",function(){
			var addList = new Object();
			for(var key in $scope.outputLargeAgrList[$scope.index]){
				addList[key] = $scope.outputLargeAgrList[$scope.index][key];
			}
			if($(this).parents("tr").children().eq(16).children().filter("[name='adj']").is(':checked')){
				addList.ADJ = 'V';
			}else{
				addList.ADJ = '';
			}
			addList.SAVE_INCREMENT = $(this).parents("tr").children().eq(7).children().filter("[name='SAVE_INCREMENT']").val();
			addList.SET_INCREMENT = $(this).parents("tr").children().eq(10).children().filter("[name='SET_INCREMENT']").val();
			addList.COVER_INCREMENT = $(this).parents("tr").children().eq(13).children().filter("[name='COVER_INCREMENT']").val();
			addList.ADJ_TYPE = '1';
			addList.ADJ_DESC = $(this).parents("tr").children().eq(18).children().filter("[name='ADJ_DESC']").val();
		
			/*判斷輸入的是否為數字且不能空*/
			if(null==addList.SAVE_INCREMENT || ""==addList.SAVE_INCREMENT){
				$scope.showErrorMsg("輸入不能為空");
				return;
			}
			if(null==addList.SET_INCREMENT || ""==addList.SET_INCREMENT){
				$scope.showErrorMsg("輸入不能為空");
				return;
			}
			if(null==addList.COVER_INCREMENT || ""==addList.COVER_INCREMENT){
				$scope.showErrorMsg("輸入不能為空");
				return;
			}
			if(isNaN(addList.SAVE_INCREMENT) || isNaN(addList.SET_INCREMENT) || isNaN(addList.COVER_INCREMENT)){
				$scope.showErrorMsg("非法輸入，本月存款增量|本月台定增量|本月投保增量只能為數字");
				return;
			}
			/*將數據裝載到inputVO，并執行對應的操作*/
			$scope.inputVO.inputList = [];
			$scope.inputVO.inputList.push(addList);
			var seq = $scope.inputVO.inputList[0].SEQ_NO;
			if(seq.substr(seq.length-3,3) == '000')		//若主鍵是以000結尾，則作插入操作，對應的方法insertData;否則作更新操作
				var workFlag = "insertData";
			else
				var workFlag = "updateData";
			
			/*執行對應workFlag操作*/
			$scope.sendRecv("PMS214", workFlag,"com.systex.jbranch.app.server.fps.pms214.PMS214UpdateInputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.showMsg("操作成功");
						$scope.query();	
					}else{
						$scope.showErrorMsg("失敗");
						return;
					}
				});
		
		});
		/**
		 * 刪除按鈕
		 */
		$scope.del = function(event,index){
			$scope.delVO = new Object();
			$scope.delVO.seqNo = $scope.outputLargeAgrList[index].SEQ_NO;
			$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function(){
			$scope.sendRecv("PMS214", "delData",
					"com.systex.jbranch.app.server.fps.pms214.PMS214UpdateInputVO",
					$scope.delVO, function(tota, isError) {
						if (!isError) {
							$scope.showMsg("ehl_01_common_003");
							$scope.query();				
						}else{
							$scope.showErrorMsg("失敗");
						}
					});
			$scope.outputLargeAgrList.splice(index,1);
			});
		}
		
		/**
		 * 編輯按鈕
		 */
		$scope.save = function(evnet, index){
			$scope.index = index;
			var trNode = $(event.target).parent().parent();
			var inputNode8 = $("<input type='text' name='SAVE_INCREMENT' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].SAVE_INCREMENT);
			var inputNode11 = $("<input type='text' name='SET_INCREMENT' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].SET_INCREMENT);
			var inputNode14 = $("<input type='text' name='COVER_INCREMENT' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].COVER_INCREMENT);
			var inputNode17 = $("<input type='checkbox' name='adj'  style='text-align:center;'/>");
			var inputNode19 = $("<input type='text' name='ADJ_DESC' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].ADJ_DESC);
			var aNode1 = $("<button class='btn btn-info' name='a_save' style='text-align:center;'>儲存</button>")
			var aNode2 = $("<button class='btn btn-info' name='a_save_del' style='text-align:center;'></button>").html(" 取消");
			if($scope.outputLargeAgrList[index].ADJ=='V')
				inputNode17.attr('checked','checked');
			var tdNode8 = $("<td style='text-align:right;'></td>").append(inputNode8);
			var tdNode11 = $("<td style='text-align:right;'></td>").append(inputNode11);
			var tdNode14 = $("<td style='text-align:right;'></td>").append(inputNode14);
			var tdNode17 = $("<td style='text-align:center;'></td>").append(inputNode17);
			var tdNode19 = $("<td style='text-align:left;'></td>").append(inputNode19);
			var tdNode20 = $("<td align='center'></td>").append(aNode1,aNode2);
		
			trNode.children("td").eq(7).replaceWith(tdNode8);
			trNode.children("td").eq(10).replaceWith(tdNode11);
			trNode.children("td").eq(13).replaceWith(tdNode14);
			trNode.children("td").eq(16).replaceWith(tdNode17);
			trNode.children("td").eq(18).replaceWith(tdNode19);
			trNode.children("td").eq(19).replaceWith(tdNode20);
		}
		/**
		 * 編輯取消按鈕
		 */
		$(document).on("click","[name='a_save_del']",function(){
			$scope.query();
		});
		
		$(document).on("change","[name='SAVE_INCREMENT']",function(){
			$(this).parents("tr").children().eq(16).children().filter("[name='adj']").prop("checked",true);
		})
		$(document).on("change","[name='SET_INCREMENT']",function(){
			$(this).parents("tr").children().eq(16).children().filter("[name='adj']").attr("checked",true);
		})
		$(document).on("change","[name='COVER_INCREMENT']",function(){
			$(this).parents("tr").children().eq(16).children().filter("[name='adj']").attr("checked",true);
		})
		
});
