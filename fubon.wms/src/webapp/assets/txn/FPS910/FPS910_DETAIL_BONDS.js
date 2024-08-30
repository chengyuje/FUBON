'use strict';
eSoafApp.controller('FPS910_DETAIL_BONDSController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS910_DETAIL_BONDSController";
		
		// combobox
		getParameter.XML(["FPS.INV_PRD_TYPE", "FPS.INV_PRD_TYPE_2"], function(totas) {
			if (totas) {
				$scope.INV_PRD_TYPE = totas.data[totas.key.indexOf('FPS.INV_PRD_TYPE')];
				$scope.INV_PRD_TYPE_2 = totas.data[totas.key.indexOf('FPS.INV_PRD_TYPE_2')];
			}
		});
		
		// init
		$scope.currUser = projInfoService.getUserID();
		$scope.init = function() {
			$scope.inputVO = {
				param_no: $scope.Datarow.PARAM_NO,
				setting_type: $scope.Datarow.SETTING_TYPE,
				date: $scope.toJsDate($scope.Datarow.EFFECT_START_DATE),
				inv_amt_type: $scope.Datarow.INV_AMT_TYPE || '0',
				totalList: [],
				bondsList: []
			};
			//
			$scope.sendRecv("FPS910", "init_detail", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", 
				{'param_no': $scope.inputVO.param_no, 'stock_bond_type': 'B'},
				function(tota, isError) {
					if (!isError) {
						// 查無資料
						if(tota[0].body.resultList.length == 0) {
							$scope.inputVO.bondsList = [];
							$scope.inputVO.bondsList.push(
								 {'CUST_RISK_ATR': 'C1','CIRCLE': 1,'SUBITEM': []}
								,{'CUST_RISK_ATR': 'C2','CIRCLE': 1,'SUBITEM': []}
								,{'CUST_RISK_ATR': 'C3','CIRCLE': 1,'SUBITEM': []}
								,{'CUST_RISK_ATR': 'C4','CIRCLE': 1,'SUBITEM': []}
							);
	            			return;
	            		}
						$scope.inputVO.bondsList = refreshDetail(tota[0].body.resultList);
					}
			});
		};
		
		$scope.init();
		
		function refreshDetail(detail) {
			var ans = [];
			for(var i=0; i<detail.length; i++) {
				generateDetail(ans, detail[i], 1);
			}
			return ans;
		};
		
		function generateDetail(ansRow, row, circle) {
			var obj = {},exist = false;
			if(circle == 1) {
				for(var i = 0; i < ansRow.length; i++) {
					if(row["CUST_RISK_ATR"] == ansRow[i]["CUST_RISK_ATR"]) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					obj["SEQNO"] = row["SEQNO"];
					obj["CUST_RISK_ATR"] = row["CUST_RISK_ATR"];
					obj["LASTUPDATE"] = row["LASTUPDATE"];
					obj["MODIFIER"] = row["MODIFIER"];
					obj["CIRCLE"] = circle;
					obj["SUBITEM"] = [];
					ansRow.push(obj);
					generateDetail(obj["SUBITEM"], row, circle+1);
					return;
				}
				var old = ansRow.slice(-1).pop();
				generateDetail(old["SUBITEM"], row, circle+1);
			}
			else if(circle == 2) {
				// 固定收益商品
				if(row.INV_PRD_TYPE == '2') {
					obj["SEQNO"] = row["SEQNO"];
					obj["CUST_RISK_ATR"] = row["CUST_RISK_ATR"];
					obj["INV_PRD_TYPE"] = row["INV_PRD_TYPE"];
					obj["INV_PERCENT"] = row["INV_PERCENT"];
					obj["LASTUPDATE"] = row["LASTUPDATE"];
					obj["MODIFIER"] = row["MODIFIER"];
					obj["CIRCLE"] = circle;
					ansRow.push(obj);
					return;
				} else {
					for(var i = 0; i < ansRow.length; i++) {
						if(row["INV_PRD_TYPE"] == ansRow[i]["INV_PRD_TYPE"]) {
							exist = true;
							break;
						}
					}
					if (!exist) {
						obj["SEQNO"] = row["SEQNO"];
						obj["INV_PRD_TYPE"] = row["INV_PRD_TYPE"];
						obj["LASTUPDATE"] = row["LASTUPDATE"];
						obj["MODIFIER"] = row["MODIFIER"];
						obj["CIRCLE"] = circle;
						obj["SUBITEM"] = [];
						ansRow.push(obj);
						generateDetail(obj["SUBITEM"],row,circle+1);
						return;
					}
					var old = ansRow.slice(-1).pop();
					generateDetail(old["SUBITEM"], row, circle+1);
				}
			} else {
				obj["SEQNO"] = row["SEQNO"];
				obj["CUST_RISK_ATR"] = row["CUST_RISK_ATR"];
				obj["INV_PRD_TYPE"] = row["INV_PRD_TYPE"];
				obj["INV_PRD_TYPE_2"] = row["INV_PRD_TYPE_2"];
				obj["PARAM_NO"] = row["PARAM_NO"];
				obj["PRD_ID"] = row["PRD_ID"];
				obj["PRD_NAME"] = row["PRD_NAME"];
				obj["RISKCATE_ID"] = row["RISKCATE_ID"];
				obj["PRD_TYPE"] = row["PRD_TYPE"];
				obj["fund_list"] = row["fund_list"];
				obj["CURRENCY_STD_ID"] = row["CURRENCY_STD_ID"];
				obj["INV_PERCENT"] = row["INV_PERCENT"];
				obj["LASTUPDATE"] = row["LASTUPDATE"];
				obj["MODIFIER"] = row["MODIFIER"];
				obj["CIRCLE"] = circle;
				ansRow.push(obj);
				return;
			}
		};
		
		$scope.SUM_INV_PERCENT = function(child) {
			var total = 0;
			angular.forEach(child, function(row) {
				total += row.INV_PERCENT ? parseFloat(row.INV_PERCENT) : 0;
			});
			return Math.round(total * 100) / 100;
		};
		
		$scope.add = function () {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS910/FPS910_EDIT.html',
				className: 'FPS910_EDIT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.edit_type = '2';
                	$scope.stock_bond_type = 'B';
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value != "cancel") {
					var addData = angular.copy(data.value);
					var rootIndex = $scope.inputVO.bondsList.map(function(e) { return e.CUST_RISK_ATR; }).indexOf(addData.cust_risk_atr);
					var childRow = $scope.inputVO.bondsList[rootIndex].SUBITEM;
					var parentIndex = childRow.map(function(e) { return e.INV_PRD_TYPE; }).indexOf(addData.inv_prd_type);
					if(parentIndex == -1) {
						var obj = {'CUST_RISK_ATR': addData.cust_risk_atr, 'INV_PRD_TYPE': addData.inv_prd_type, 'INV_PRD_TYPE_2': addData.inv_prd_type_2, 'PARAM_NO': $scope.inputVO.param_no,
								'PRD_ID': addData.prd_id, 'PRD_NAME': addData.prd_name, 'RISKCATE_ID': addData.risk_id, 'PRD_TYPE': addData.prd_type, 'CURRENCY_STD_ID': addData.currency_std, 'INV_PERCENT': addData.inv_percent, 'CIRCLE': 3};
						childRow.push({'INV_PRD_TYPE': addData.inv_prd_type, 'CIRCLE': 2, 'SUBITEM': [obj]});
					}
					else {
						childRow[parentIndex].SUBITEM.push({'CUST_RISK_ATR': addData.cust_risk_atr, 'INV_PRD_TYPE': addData.inv_prd_type, 'INV_PRD_TYPE_2': addData.inv_prd_type_2, 'PARAM_NO': $scope.inputVO.param_no,
							'PRD_ID': addData.prd_id, 'PRD_NAME': addData.prd_name, 'RISKCATE_ID': addData.risk_id, 'PRD_TYPE': addData.prd_type, 'CURRENCY_STD_ID': addData.currency_std, 'INV_PERCENT': addData.inv_percent, 'CIRCLE': 3});
						childRow[parentIndex].SUBITEM = _.orderBy(childRow[parentIndex].SUBITEM, function(data) {
							var rank = {
								'MFD': 1,
								'ETF': 2,
								'INS': 3,
								'NANO': 4
							};
							if(!data.RISKCATE_ID)
								return "5" + rank[data.PRD_TYPE];
							else
								return data.RISKCATE_ID.substring(1, 2) + rank[data.PRD_TYPE];
						});
					}
				}
			});
		};
		
		$scope.delRow = function(child, parent, root) {
			$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
            	var delIndex = parent.SUBITEM.indexOf(child);
            	parent.SUBITEM.splice(delIndex,1);
            	if(true && parent.SUBITEM.length == 0) {
            		var delIndex2 = root.SUBITEM.indexOf(parent);
            		root.SUBITEM.splice(delIndex2, 1);
            	}	
//            	$scope.showSuccessMsg('刪除成功');
            });
		};
		
		$scope.openFUND = function (dataRow) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD210/PRD210_FUND.html',
				className: 'PRD210_FUND',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.dataRow = {'prd_id': dataRow.PRD_ID};
					$scope.stock_bond_type = 'B';
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					dataRow.fund_list = angular.copy(data.value);
				}
			});
		};
		
		$scope.$on('getResultList', function(){
			$scope.connector('set', 'bondsList', $scope.inputVO.bondsList);
		});
});