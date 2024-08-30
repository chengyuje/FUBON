/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG110_REVIEWController', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG110_REVIEWController";
		
		// filter
		getParameter.XML(["COMMON.YES_NO", "COMMON.REVIEW_STATUS", "COMMON.ACT_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['COMMON.REVIEW_STATUS'] = totas.data[totas.key.indexOf('COMMON.REVIEW_STATUS')];
				$scope.mappingSet['COMMON.ACT_TYPE'] = totas.data[totas.key.indexOf('COMMON.ACT_TYPE')];
			}
		});
        //
		
		$scope.init = function(){
			$scope.inputVO = {
				EMP_ID: $scope.EMP_ID
        	};
		};
        $scope.init();

        $scope.sendRecv("ORG110", "getReviewList", "com.systex.jbranch.app.server.fps.org110.ORG110InputVO", $scope.inputVO, 
        	function(tota, isError) {
				if (!isError) {
					if(tota[0].body.reviewList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					
					$scope.reviewList = tota[0].body.reviewList;
					$scope.outputVO = tota[0].body;
					
					return;
				}
			}
        );
        
        $scope.getProfilePicture = function(row) {
        	if (row.EMP_PHOTO) {
        		var bufView = new Uint8Array(row.EMP_PHOTO);
				var length = bufView.length;
			    var result = '';
			    var addition = Math.pow(2,16)-1;
			    for(var i = 0; i<length; i += addition) {
			    	if(i + addition > length)
			    		addition = length - i;
			    	result += String.fromCharCode.apply(null, bufView.subarray(i,i+addition));
			    }
			    row.pictureSrc = "data:image/png;base64,"+btoa(result);
        	}
    	};
    	
});