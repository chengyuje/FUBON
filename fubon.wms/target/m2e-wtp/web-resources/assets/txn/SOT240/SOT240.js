/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp
		.controller(
				'SOT240Controller',
				function($rootScope, $scope, $controller, $filter, $confirm,
						getParameter, socketService, ngDialog, projInfoService,
						$q, validateService) {
					$controller('BaseController', {
						$scope : $scope
					});
					$scope.controllerName = "SOT240Controller";

					getParameter.XML([ "CRM.CRM822_BUY_RESULT" ],
									function(totas) {
										if (totas) {
											$scope.mappingSet['CRM.CRM822_BUY_RESULT'] = totas.data[totas.key
													.indexOf('CRM.CRM822_BUY_RESULT')];
										}
									});
					getParameter.XML([ "FPS.CURRENCY" ],
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

						$scope.sendRecv(
										"SOT240",
										"query",
										"com.systex.jbranch.app.server.fps.sot230.SOT230InputVO",
										$scope.inputVO,
										function(tota, isError) {
											if (!isError) {
												if (tota[0].body.custOrderETFVOs.length > 0) {

													$scope.custId = $scope.inputVO.custId;
													//$scope.custOrderETFVOs = tota[0].body.custOrderETFVOs;
													$scope.custOrderETFVOs = _.sortBy(tota[0].body.custOrderETFVOs, ['entrustDate']).reverse();
													//$scope.custFillETFVOs = tota[0].body.custFillETFVOs;
													$scope.outputVO = tota[0].body;
												} else {

													$scope.showErrorMsg("ehl_01_common_009");
													$scope.outputVO = [];
													$scope.custOrderETFVOs = [];
													//$scope.custFillETFVOs = [];
													$scope.data = [];
												}
											} else {
												$scope.outputVO = [];
												$scope.custOrderETFVOs = [];
												//$scope.custFillETFVOs = [];
												$scope.data = [];
											}
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