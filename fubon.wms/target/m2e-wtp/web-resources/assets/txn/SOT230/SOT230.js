/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp
		.controller(
				'SOT230Controller',
				function($rootScope, $scope, $controller, $filter, $confirm,
						getParameter, socketService, ngDialog, projInfoService,
						$q, validateService) {
					$controller('BaseController', {
						$scope : $scope
					});
					$scope.controllerName = "SOT230Controller";

					getParameter
							.XML(
									[ "CRM.CRM822_BUY_RESULT" ],
									function(totas) {
										if (totas) {
											$scope.mappingSet['CRM.CRM822_BUY_RESULT'] = totas.data[totas.key
													.indexOf('CRM.CRM822_BUY_RESULT')];
										}
									});
					getParameter
					.XML(
							[ "CRM.CRM822_SELL_RESULT" ],
							function(totas) {
								if (totas) {
									$scope.mappingSet['CRM.CRM822_SELL_RESULT'] = totas.data[totas.key
											.indexOf('CRM.CRM822_SELL_RESULT')];
								}
							});
					getParameter
					.XML(
							[ "FPS.CURRENCY" ],
							function(totas) {
								if (totas) {
									$scope.mappingSet['FPS.CURRENCY'] = totas.data[totas.key
											.indexOf('FPS.CURRENCY')];
								}
							});
					
					$scope.model = {};
					$scope.altInputFormats = [ 'M!/d!/yyyy' ];

					$scope.query = function() {
						if ($scope.inputVO.custId == ''
								|| $scope.inputVO.custId == undefined) {
							$scope.showErrorMsg("ehl_01_common_022");
							return;
						}
						$scope.inputVO.custId = $filter('uppercase')(
								$scope.inputVO.custId);
						$scope.inputVO.prodId = $filter('uppercase')(
								$scope.inputVO.prodId);
						$scope.inputVO.prodName = $filter('uppercase')(
								$scope.inputVO.prodName);
						var validCustID = validateService
								.checkCustID($scope.inputVO.custId); // 自然人和法人檢查
						if (validCustID == false) {
							$scope.inputVO.custId = '';
							return;
						}

						$scope
								.sendRecv(
										"SOT230",
										"query",
										"com.systex.jbranch.app.server.fps.sot230.SOT230InputVO",
										$scope.inputVO,
										function(tota, isError) {
											if (!isError) {
												if (tota[0].body.custOrderETFVOs.length > 0
														|| tota[0].body.custFillETFVOs.length > 0) {

													$scope.custId = $scope.inputVO.custId;
													$scope.custOrderETFVOs = tota[0].body.custOrderETFVOs;
													$scope.custFillETFVOs = tota[0].body.custFillETFVOs;
													$scope.outputVO = tota[0].body;
												} else {
													
													$scope
															.showErrorMsg("ehl_01_common_009");
													$scope.outputVO = [];
													$scope.custOrderETFVOs = [];
													$scope.custFillETFVOs = [];
													$scope.data = [];
												}
											} else {
												$scope.outputVO = [];
												$scope.custOrderETFVOs = [];
												$scope.custFillETFVOs = [];
												$scope.data = [];
											}
										});
					};
				      $scope.next = function (row) {
				    	  
			              var dialog = ngDialog.open({
			                  template: 'assets/txn/SOT230/SOT230_dt.html',
			                  className: 'SOT230',
			                  showClose : true,
			                  controller: ['$scope', function($scope) {
			                	  $scope.row = row;
			                  }]
			           
			              });
			         
			             
			          };
			  		$scope.init = function(){
						$scope.outputVO=[];
						$scope.custAssetBondList=[];
						$scope.data = [];
						$scope.inputVO = {
								custId:'',
						};
						var custID = $scope.connector('get','ORG110_custID');
						if(custID != undefined){
							$scope.inputVO.custId = custID;
						}
					};
					$scope.init();
				});