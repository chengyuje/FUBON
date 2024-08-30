/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS213Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS213Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		
		//xml參數初始化    /*軌跡類型下拉框* *調整類別下拉框*/
		getParameter.XML(["PMS.CNR_ADJ_TYPE", "PMS.CNR_SYS_ADJ_DESC","PMS.CNR_PROD_SOURCE"], function(totas) {
			if (totas) {
				$scope.mappingSet['adjType'] = totas.data[totas.key.indexOf('PMS.CNR_ADJ_TYPE')];
				$scope.mappingSet['adjDesc'] = totas.data[totas.key.indexOf('PMS.CNR_SYS_ADJ_DESC')];
				$scope.mappingSet['adjProd'] = totas.data[totas.key.indexOf('PMS.CNR_PROD_SOURCE')];
			}
		});
		
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
        	
			$scope.inputVO.userId = projInfoService.getUserID();
			//頁面初始化 清空查詢條件
			$scope.EMP_LIST=[];
			$scope.BRANCH_LIST=[];
			$scope.outputLargeAgrList=[];
			$scope.inputVO.yearMon = '';
			$scope.inputVO.AOCODE = '';
			$scope.inputVO.adjType = '';
			$scope.inputVO.custId = '';
			$scope.inputVO.prodCode = '';
			$scope.inputVO.adjDesc = '';			
			$scope.inputVO.branch_nbr='';
			$scope.inputVO.emp_id='';
			//2017/06/23 產品類型補上清空
			$scope.inputVO.PRODTYPE='';
			
		}
		$scope.init();
		
		//可是範圍觸發function 2017/06/13
		$scope.dateChange = function()
		{
	        if($scope.inputVO.yearMon!='')	        
	        { 	
	        	$scope.inputVO.reportDate = $scope.inputVO.yearMon;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };
		
		/**
		 * 計算獎勵金按鈕
		 */
		$scope.query = function(){
			/*判斷計積年月是否為空*/
			if(''==$scope.inputVO.yearMon || null==$scope.inputVO.yearMon){
				$scope.showErrorMsg("尚未選擇計積年月");
				return;
			}
			angular.forEach($scope.mappingSet['adjProd'], function(row2){
				if(row2.DATA==$scope.inputVO.PRODTYPE && row2.LABEL!='請選擇')
					$scope.inputVO.PRODTYPE=row2.LABEL;
			});
			$scope.sendRecv("PMS213", "queryData","com.systex.jbranch.app.server.fps.pms213.PMS213QueryInputVO",
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
//						$scope.showErrorMsg("查詢筆數過多, 請增加查詢條件");
						$scope.showBtn = 'none';
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
				template: 'assets/txn/PMS213/PMS213_UPLOAD.html',
				className: 'PMS213_UPLOAD',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.yearMon = yearmon;
	            }]
			});
			dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
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
			$scope.sendRecv("PMS213", "export", "com.systex.jbranch.app.server.fps.pms213.PMS213QueryInputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							return;
						}else{
							if (tota.length > 0) 
							{
		                		if(tota[0].body.outputLargeAgrList && tota[0].body.outputLargeAgrList.length <= 0) 
		                		{
		                			$scope.outputLargeAgrList=[];
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
			$scope.index = index;
			var trNode = $("<tr></tr>");
			$scope.addList = $scope.outputLargeAgrList[index];
			for(var value in $scope.addList){
				if(null == $scope.addList[value])
					$scope.addList[value] = '';
			}
			var inputNode1 = $("<input type='text' name='YEARMON' size='10' style='text-align:right;' />");
			var inputNode5 = $("<input type='text' name='AOCODE' size='10' style='text-align:right;' />");
			var inputNode10 = $("<input type='text' name='PRODBEN' size='10' style='text-align:right;' />");
			var inputNode11 = $("<input type='text' name='TXN_FEE' size='10' style='text-align:right;' />");
			var inputNode12 = $("<input type='text' name='PRODREALBEN' size='10' style='text-align:right;'/>");
			var inputNode13 = $("<input type='text' name='PRODCNRBEN' size='10' style='text-align:right;' />");
			var inputNode14 = $("<input type='checkbox' name='adj' checked disabled='true' size='10' style='text-align:right;'/>");
			var inputNode16 = $("<input type='text' name='ADJ_DESC' size='10' style='text-align:right;' />");
			var aNode1 = $("<button class='btn btn-info' name='a_save' style='text-align:center;'>儲存</button>")
			var aNode2 = $("<button class='btn btn-info' name='a_add_del' style='text-align:center;'></button>").html(" 取消");
			var tdNode1 = $("<td align='center'></td>").append(inputNode1);
			var tdNode5 = $("<td align='center'></td>").append(inputNode5);
			var tdNode10 = $("<td align='center'></td>").append(inputNode10);
			var tdNode11 = $("<td align='center'></td>").append(inputNode11);
			var tdNode12 = $("<td align='center'></td>").append(inputNode12);
			var tdNode13 = $("<td align='center'></td>").append(inputNode13);
			var tdNode14 = $("<td align='center'></td>").append(inputNode14);
			var tdNode16 = $("<td align='center'></td>").append(inputNode16);
			var tdNode17 = $("<td align='center'></td>").append(aNode1,aNode2);
			
			var tdNode2 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.TRANDATE);
			var tdNode3 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.CUSTID);
			var tdNode4 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.CUSTNAME);
			var tdNode6 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.TRANAOCODE);
			var tdNode7 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.PRODCODE);
			var tdNode8 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.PRODTYPE);
			var tdNode9 = $("<td style='text-align:center;color:blue'></td>").text($scope.addList.PRODNAME);
			var tdNode15 = $("<td style='text-align:center;color:blue'></td>").text($scope.mappingSet['adjType'][2].LABEL);
			trNode.append(tdNode1,tdNode2,tdNode3,tdNode4,tdNode5,tdNode6,tdNode7,
					tdNode8,tdNode9,tdNode10,tdNode11,tdNode12,tdNode13,tdNode14,tdNode15,tdNode16,tdNode17);
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
			if($(this).parents("tr").children().eq(12).children().filter("[name='adj']").is(':checked')){
				addList.ADJ = 'V';
			}else{
				addList.ADJ = '';
			}
			addList.YEARMON = $(this).parents("tr").children().eq(0).children().filter("[name='YEARMON']").val();
			addList.AOCODE = $(this).parents("tr").children().eq(4).children().filter("[name='AOCODE']").val();
			addList.PRODBEN = $(this).parents("tr").children().eq(9).children().filter("[name='PRODBEN']").val();
			addList.TXN_FEE = $(this).parents("tr").children().eq(10).children().filter("[name='TXN_FEE']").val();
			addList.PRODREALBEN = $(this).parents("tr").children().eq(11).children().filter("[name='PRODREALBEN']").val();
			addList.PRODCNRBEN = $(this).parents("tr").children().eq(12).children().filter("[name='PRODCNRBEN']").val();
			addList.ADJ_TYPE = '1';
			addList.ADJ_DESC = $(this).parents("tr").children().eq(15).children().filter("[name='ADJ_DESC']").val();
			
			/*判斷輸入的年月是否合法*/
			var flag = true;
			for(var key in $scope.mappingSet['timeE']){
				var str = $.trim(addList.YEARMON);
				if(null==str || ''==str){
					flag = true;
					break;
				}
				if(str == $scope.mappingSet['timeE'][key].DATA){
					flag = false;
					break;
				}
			}
			if(flag){
				$scope.showErrorMsg("輸入的計積年月有誤");
				return;
			}
			/*判斷主鍵是否重複*/
			if(addList)
			/*判斷輸入的是否為數字且不能空*/
			if(null==addList.AOCODE || ""==addList.AOCODE){
				$scope.showErrorMsg("輸入不能為空");
				return;
			}
			if(null==addList.PRODBEN || ""==addList.PRODBEN){
				$scope.showErrorMsg("輸入不能為空");
				return;
			}
			if(null==addList.TXN_FEE || ""==addList.TXN_FEE){
				$scope.showErrorMsg("輸入不能為空");
				return;
			}
			if(null==addList.PRODREALBEN || ""==addList.PRODREALBEN){
				$scope.showErrorMsg("輸入不能為空");
				return;
			}
			if(null==addList.PRODCNRBEN || ""==addList.PRODCNRBEN){
				$scope.showErrorMsg("輸入不能為空");
				return;
			}
			if(isNaN(addList.PRODBEN) || isNaN(addList.TXN_FEE) || isNaN(addList.PRODREALBEN) || isNaN(addList.PRODCNRBEN)){
				$scope.showErrorMsg("非法輸入，產品收益|產品實際收益|產品CNR收益 只能為數字");
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
			$scope.sendRecv("PMS213", workFlag,"com.systex.jbranch.app.server.fps.pms213.PMS213UpdateInputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.showMsg("操作成功");
						$scope.query();				
					}else{
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
			$scope.sendRecv("PMS213", "delData",
					"com.systex.jbranch.app.server.fps.pms213.PMS213UpdateInputVO",
					$scope.delVO, function(tota, isError) {
						if (!isError) {
							$scope.showMsg("ehl_01_common_003");
							$scope.query();				
						}else{
							$scope.showErrorMsg("失敗");
						}
					});
			$scope.outputLargeAgrList.splice(index,1);
			})
		}
		
		/**
		 * 編輯按鈕
		 */
		$scope.save = function(evnet, index){
			console.log("123");
			$scope.index = index;
			var trNode = $(event.target).parent().parent();
			var inputNode1 = $("<input type='text' name='YEARMON' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].YEARMON);
			var inputNode5 = $("<input type='text' name='AOCODE' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].AOCODE);
			var inputNode10 = $("<input type='text' name='PRODBEN' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].PRODBEN);
			var inputNode11 = $("<input type='text' name='TXN_FEE' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].TXN_FEE);
			var inputNode12 = $("<input type='text' name='PRODREALBEN' size='10' style='text-align:right;'/>").val($scope.outputLargeAgrList[index].PRODREALBEN);
			var inputNode13 = $("<input type='text' name='PRODCNRBEN' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].PRODCNRBEN);
			var inputNode14 = $("<input type='checkbox' name='adj' size='10' style='text-align:right;'/>");
			var inputNode16 = $("<input type='text' name='ADJ_DESC' size='10' style='text-align:right;' />").val($scope.outputLargeAgrList[index].ADJ_DESC);
			var aNode1 = $("<button class='btn btn-info' name='a_save' style='text-align:center;'>儲存</button>")
			var aNode2 = $("<button class='btn btn-info' name='a_save_del' style='text-align:center;'></button>").html(" 取消");
			if($scope.outputLargeAgrList[index].ADJ=='V')
				inputNode13.attr('checked','checked');
			
			var tdNode1 = $("<td align='center'></td>").append(inputNode1);
			var tdNode5 = $("<td align='center'></td>").append(inputNode5);
			var tdNode10 = $("<td align='center'></td>").append(inputNode10);
			var tdNode11 = $("<td align='center'></td>").append(inputNode11);
			var tdNode12 = $("<td align='center'></td>").append(inputNode12);
			var tdNode13 = $("<td align='center'></td>").append(inputNode13);
			var tdNode14 = $("<td align='center'></td>").append(inputNode14);
			var tdNode16 = $("<td align='center'></td>").append(inputNode16);
			var tdNode17 = $("<td align='center'></td>").append(aNode1,aNode2);
			trNode.children("td").eq(0).replaceWith(tdNode1);
			trNode.children("td").eq(4).replaceWith(tdNode5);
			trNode.children("td").eq(9).replaceWith(tdNode10);
			trNode.children("td").eq(10).replaceWith(tdNode11);
			trNode.children("td").eq(11).replaceWith(tdNode12);
			trNode.children("td").eq(12).replaceWith(tdNode13);
			trNode.children("td").eq(13).replaceWith(tdNode14);
			trNode.children("td").eq(15).replaceWith(tdNode16);
			trNode.children("td").eq(16).replaceWith(tdNode17);
		}
		/**
		 * 編輯取消按鈕
		 */
		$(document).on("click","[name='a_save_del']",function(){
			$scope.query();
		});
		
		
		/**
		 * 匯出功能
		 */
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS213", "exportMast", "com.systex.jbranch.app.server.fps.pms213.PMS213QueryInputVO", $scope.inputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
							if(tota[0].body.errorMessage =='匯出筆數過多, 請增加查詢條件'){
								$scope.showErrorMsg("匯出筆數過多, 請增加查詢條件");
								return;
							}
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.outputLargeAgrList=[];
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                	}
	                }
			});
		};
		
});