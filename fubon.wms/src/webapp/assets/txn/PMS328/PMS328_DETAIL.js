/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS328_DETAILController', function($rootScope, $scope,
		$controller, socketService, ngDialog, projInfoService, $q, $confirm,
		$filter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS328_DETAILController";

	$scope.inquire = function() {
		
		document.onkeydown = function(theEvent) {
		        if (theEvent != null) {
		                event = theEvent;
		        }
		        if (event.keyCode ==27) {
		                try {
		                	$scope.showMsg("請按右上角按鍵離開");
		                        // Firefox 會丟 Exception，所以用 try-cache 擋住
		           	var dialog = ngDialog.open({
						template: 'assets/txn/PMS328/PMS328_DETAIL.html',
						className: 'PMS328_DETAIL',
						showClose: false,
		                controller: ['$scope', function($scope) {
		                	$scope.row = $scope.rowlist;
		                }]
					});
		                        event.keyCode = 0; 
		                } catch(e){}
		                return false;
		        }
		}

		$scope.sendRecv("PMS328", "queryImage",
				"com.systex.jbranch.app.server.fps.pms328.PMS328IInputVO", {
					'list' : $scope.paramList,
					'ROB' : $scope.inputVO.ROB,
					'type' : $scope.inputVO.type
				}, function(tota, isError) {
					if (!isError) {
						var Array = [];
						var Array0 = []; // 存放X,Y陣列
						debugger;
						var nam = [];
						var c = [];
						var banchavg = 0;
						$scope.outputVO = tota[0].body;

						for (var x = 0; x < $scope.paramList.length; x++) {

							/***=====以下是最多可以幾條線======***/
							angular.forEach(tota[0].body.resultList, function(
									row, index, objs) {
								if (banchavg == 0) {   //全行平均
									Array.push([ row.YEARMON, row.AVG ]);    //全行平均
								}
								if (row.AVG0 != undefined && x == 0) {    //第一條線
									Array0.push([ row.YEARMON, row.AVG0 ]);
								}
								if (row.AVG1 != undefined && x == 1) {   //第二條線
									Array0.push([ row.YEARMON, row.AVG1 ]);
								}
								if (row.AVG2 != undefined && x == 2) {    //第三條線
									Array0.push([ row.YEARMON, row.AVG2 ]);
								}
								if (row.AVG3 != undefined && x == 3) {     //第四條線
									Array0.push([ row.YEARMON, row.AVG3 ]);
								}
								if (row.AVG4 != undefined && x == 4) {     //第五條線
									Array0.push([ row.YEARMON, row.AVG4 ]);
								}
								if (row.AVG5 != undefined && x == 5) {    //第六條線
									Array0.push([ row.YEARMON, row.AVG5 ]);
								}
								if (row.AVG6 != undefined && x == 6) {    //第七條線
									Array0.push([ row.YEARMON, row.AVG6 ]);
								}
								if (row.AVG7 != undefined && x == 7) {    //第八條線
									Array0.push([ row.YEARMON, row.AVG7 ]);
								}
								if (row.AVG8 != undefined && x == 8) {    //第九條線
									Array0.push([ row.YEARMON, row.AVG8 ]);
								}
								if (row.AVG9 != undefined && x == 9) {     //第十條線
									Array0.push([ row.YEARMON, row.AVG9 ]);
								}
							
								if (row.AVG10 != undefined && x == 10) {    //第十一條線
									Array0.push([ row.YEARMON, row.AVG10 ]);
								}
								if (row.AVG11 != undefined && x == 11) {    //第十二條線
									Array0.push([ row.YEARMON, row.AVG11 ]);
								}
								if (row.AVG12 != undefined && x == 12) {    //第十三條線
									Array0.push([ row.YEARMON, row.AVG12 ]);
								}
								if (row.AVG13 != undefined && x == 13) {    //第十四條線  
									Array0.push([ row.YEARMON, row.AVG13 ]);
								}
								if (row.AVG14 != undefined && x == 14) {    //第十五條線  
									Array0.push([ row.YEARMON, row.AVG14 ]);
								}
								if (row.AVG15 != undefined && x == 15) {    //第十六條線  
									Array0.push([ row.YEARMON, row.AVG15 ]);
								}
								if (row.AVG16 != undefined && x == 16) {     //第十七條線  
									Array0.push([ row.YEARMON, row.AVG16 ]);
								}
								if (row.AVG17 != undefined && x == 17) {     //第十八條線  
									Array0.push([ row.YEARMON, row.AVG17 ]);
								}
								if (row.AVG18 != undefined && x == 18) {     //第十九條線  
									Array0.push([ row.YEARMON, row.AVG18 ]);
								}
								if (row.AVG19 != undefined && x == 19) {
									Array0.push([ row.YEARMON, row.AVG19 ]);   //第二十條線
								}
								if (row.AVG20 != undefined && x == 20) {
									Array0.push([ row.YEARMON, row.AVG20 ]);    //第二十一條線
								}
								if (row.AVG21 != undefined && x == 21) {
									Array0.push([ row.YEARMON, row.AVG21 ]);     //第二十二條線
								}
								if (row.AVG22 != undefined && x == 22) {
									Array0.push([ row.YEARMON, row.AVG22 ]);     //第二十三條線
								}
								if (row.AVG23 != undefined && x == 23) {
									Array0.push([ row.YEARMON, row.AVG23 ]);      //第二十四條線
								}
								if (row.AVG24 != undefined && x == 24) {
									Array0.push([ row.YEARMON, row.AVG24 ]);      //第二十五條線
								}
								if (row.AVG25 != undefined && x == 25) {
									Array0.push([ row.YEARMON, row.AVG25 ]);       //第二十六條線
								}

							});
							if (banchavg == 0) {
								nam.push({
									"key" : "全行平均",
									"values" : Array
								});
								banchavg++;
							}
							/**************KEY是顯示欄位名稱,VALUE是X時間,Y值*****************/
 							if ((x + 1) != $scope.paramList.length  && $scope.aotype != '1'
 									) {

 								nam.push({
 									"key" : $scope.inputVO.type
 											+ $scope.paramList[x + 1].LABEL,
 									"values" : Array0
 								});
 							}
 							/**************KEY是顯示欄位名稱,VALUE是X時間,Y值*****************/
 							if (x != $scope.paramList.length
									&& $scope.aotype == '1' ) {
								nam.push({
									"key" : $scope.inputVO.type
											+ $scope.paramList[x].LABEL,
									"values" : Array0
								});
							}
							Array0 = [];
						}
						/******===設定data===********/
						$scope.data = nam;   //設定data
						$scope.inputVO.ROB = "";
						$scope.aotype = '0';
						return;
					}
				});
	};

	$scope.types = function() {

		$scope.genRegion();
	};

	$scope.init = function() {
		$scope.row = $scope.row || {};
		$scope.inputVO = {

			eTime : '',
			aocode : '',
			branch : '',
			region : '',
			op : '',
			aojob : '',
			ROB : '1', // 判斷是否為理專 或 分行 或 區域中心 或營運區
			type : '',

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

	};
	$scope.init();
	

	
	
	
	
	

	/** *以下連動區域中心.營運區.分行別** */
	// 大區域資訊
	$scope.genRegion = function() {

		$scope.mappingSet['region'] = [];
		$scope.mappingSet['branch'] = [];
		$scope.mappingSet['op'] = [];
		$scope.inputVO.region = "";
		$scope.inputVO.op = "";
		$scope.inputVO.branch = "";
		angular.forEach(projInfoService.getAvailRegion(), function(row, index,
				objs) {

			$scope.mappingSet['region'].push({
				LABEL : row.REGION_CENTER_NAME,
				DATA : row.REGION_CENTER_ID
			});
			if ($scope.mappingSet['region'].length > 0) {

				$scope.inputVO.ROB = "1"; // 營運區為一
			}

		});
		if ($scope.mappingSet['region'].length > 0) {
			$scope.paramList = $scope.mappingSet['region'];
		}

		$scope.inquire();

	};
	$scope.genRegion();

	// 區域資訊
	$scope.genArea = function() {

		$scope.inputVO.op = [];
		$scope.mappingSet['branch'] = [];
		$scope.mappingSet['op'] = [];
		angular.forEach(projInfoService.getAvailArea(), function(row, index,
				objs) {

			if (row.REGION_CENTER_ID == $scope.inputVO.region) {

				$scope.mappingSet['op'].push({
					LABEL : row.BRANCH_AREA_NAME,
					DATA : row.BRANCH_AREA_ID
				});
				$scope.inputVO.ROB = "2";
			}

		});
		if ($scope.mappingSet['op'].length > 0) {
			$scope.paramList = $scope.mappingSet['op'];
		}
		if ($scope.mappingSet['op'].length == 0) {
			$scope.genRegion();
		}
		$scope.inquire();

	};

	$scope.bran = function() {
		$scope.sendRecv("PMS202", "aoCode",
				"com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", {},
				function(totas, isError) {

					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);

					}
					if (totas.length > 0) {
					
						$scope.mappingSet['aocodes'] = [];

						angular.forEach(totas[0].body.aolist, function(row,
								index, objs) {
							if (row.BRANCH_NBR == $scope.inputVO.branch) {
								$scope.mappingSet['aocodes'].push({
									LABEL : row.NAME,
									DATA : row.EMP_ID
								});
								$scope.inputVO.ROB = "4";
							}
						});
					}
					;

					if ($scope.mappingSet['aocodes'].length >= 1) {
						$scope.paramList = $scope.mappingSet['aocodes'];
						
						$scope.aotype = '1';
					}

					$scope.inquire();
				});
	}

	// 分行資訊
	$scope.genBranch = function() {
		$scope.mappingSet['branch'] = [];
		$scope.inputVO.aocode='';

		angular.forEach(projInfoService.getAvailBranch(), function(row, index,
				objs) {

			if (row.BRANCH_AREA_ID == $scope.inputVO.op) {

				$scope.mappingSet['branch'].push({
					LABEL : row.BRANCH_NAME,
					DATA : row.BRANCH_NBR
				});
				$scope.inputVO.ROB = "3";

			}
		});
		if ($scope.mappingSet['branch'].length > 0) {
			$scope.paramList = $scope.mappingSet['branch'];
			
		}

		$scope.inquire();

	};

	$scope.options = {
		chart : {
			type : 'lineWithFocusChart',
			height : 480,
			margin : {
				top : 100,
				right : 100,
				bottom : 100,
				left : 100
			},
			x : function(d) {
				return d[0];
			},
			y : function(d) {
				return d[1];
			},
			useVoronoi : true,
			clipEdge : true,
			duration : 1000,
			useInteractiveGuideline : true,
			xAxis : {
				axisLabel : 'X 時間軸',
				showMaxMin : true,
				tickFormat : function(d) {
					return d3.time.format('%x')(new Date(d))
				}
			},
			yAxis : {

				tickFormat : function(d) {
					return d3.format(',.2f')(d);
				}
			},
			x2Axis : {
				showMaxMin : 'X',
				tickFormat : function(d) {
					return d3.time.format('%x')(new Date(d))
				}
			},

			y2Axis : {
				tickFormat : function(d) {
					return d3.format(',.3f')(d);
				}
			}

		}
	};

});
