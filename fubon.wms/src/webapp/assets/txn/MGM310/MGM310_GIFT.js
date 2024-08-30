/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM310_GIFTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM310_GIFTController";

		// filter
		getParameter.XML(["MGM.GIFT_KIND", "MGM.GIFT_GET_WAY"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.GIFT_KIND'] = totas.data[totas.key.indexOf('MGM.GIFT_KIND')];
				$scope.mappingSet['MGM.GIFT_GET_WAY'] = totas.data[totas.key.indexOf('MGM.GIFT_GET_WAY')];
			}
		});
		
		// init
		$scope.init = function(){
			$scope.giftSeqCtrl = false;
			
			if($scope.row){
				$scope.giftSeqCtrl = true;
				
				$scope.inputVO = {
						gift_seq : $scope.row.GIFT_SEQ,
						gift_introduction : $scope.row.GIFT_INTRODUCTION,
						gift_name : $scope.row.GIFT_NAME,
						gift_kind : $scope.row.GIFT_KIND,
						gift_costs : $scope.row.GIFT_COSTS,
						gift_amount : $scope.row.GIFT_AMOUNT
				};
				
				$scope.gift_photo_name = $scope.row.GIFT_PHOTO_NAME;
					
			} else {
				$scope.inputVO = {
						gift_seq : '',
						gift_introduction : '',
						gift_name : '',
						gift_kind : '',
						gift_costs : '',
						gift_amount : ''
				};				
			}
		};
		$scope.init();
		
		//上傳圖片
        $scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.pictureName = name;
            	$scope.inputVO.realpictureName = rname;
        	}
        };
		
		$scope.save = function(){
			$scope.sendRecv("MGM310", "save", "com.systex.jbranch.app.server.fps.mgm310.MGM310InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('ehl_01_common_025'); 	//儲存成功
							$scope.closeThisDialog('successful');
						}
			});
		}
		
		//檢核贈品代碼是否重複輸入
		$scope.checkGiftSeq = function(){
			$scope.sendRecv("MGM310", "checkGiftSeq", "com.systex.jbranch.app.server.fps.mgm310.MGM310InputVO", 
				{'gift_seq' : $scope.inputVO.gift_seq},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length > 0){
								$scope.showErrorMsg('ehl_01_fps950_001',[$scope.inputVO.gift_seq]);		//代碼重複：{0}。
								$scope.inputVO.gift_seq = undefined;
							}
						}
			});
		}
		
		//預覽贈品圖片
		$scope.getImgView = function(){
			if($scope.inputVO.gift_seq){
				$scope.sendRecv("MGM310", "getImgView", "com.systex.jbranch.app.server.fps.mgm310.MGM310InputVO", 
					{'gift_seq': $scope.inputVO.gift_seq},
						function(tota, isError) {
							if (!isError) {
								var description = tota[0].body.pdfUrl;
								window.open('./INS300_PDF.html?pdfurl=' + description, 'Description');
								return;
							}
				});				
			}
		}
		
});
