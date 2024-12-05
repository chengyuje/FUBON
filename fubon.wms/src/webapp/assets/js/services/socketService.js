///======================
/// 通訊模組
///======================
eSoafApp.factory('socketService', ['$rootScope', '$http', '$q', '$timeout', 'sysInfoService', 'ngDialog',
	function($rootScope, $http, $q, $timeout, sysInfoService, ngDialog) {
		///======================
		/// 常數定義
		///======================
//		var constServiceAddr = "./Service/MsgService.svc/rest";
	    return {
	    	///======================
	    	/// 收送電文
	    	///======================
	      	sendRecv : function(sTxnCode, bizCode, inputVOClass, inputVO) {
	      		/**marking for production**/
//	      		console.log("sendRecv=" + sTxnCode);
//	      		console.log('inputVOClass='+inputVOClass);
	      		
	      		inputVO.pageCount = inputVO.pageCount || sysInfoService.$paging.rowCountLimit; 
	      		
	      		var oTIOA = {}, oTime = 120000;
	      		oTIOA.header = {};
	      		oTIOA.header.InputVOClass = inputVOClass;
	      		oTIOA.header.TxnCode = sTxnCode;
//	      		oTIOA.header.FuncType = sFuncType;
	      		oTIOA.header.BizCode = bizCode;      		
	      		oTIOA.header.StampTime = true;
	      		oTIOA.header.SupvPwd = '';
	      		oTIOA.header.TXN_DATA = {};
	      		oTIOA.header.SupvID = '';
	      		oTIOA.header.CustID = '';
	      		oTIOA.header.ApplicationID = $rootScope.ApplicationID;
	      		oTIOA.header.BranchID = $rootScope.BranchID;
	      		oTIOA.header.REQUEST_ID = '';
	      		oTIOA.header.TlrID = $rootScope.TlrID;
	      		oTIOA.header.ClientTransaction = true;
	      		oTIOA.header.DevMode = false;
	      		oTIOA.header.WsID = $rootScope.WsID;
	      		// oTIOA.header.TXNSYSID = 'XX';
	      		oTIOA.header.SectionID = 'esoaf';
	      		oTIOA.header.TRMSEQ = '32';
	      		oTIOA.header["X-Content-Type-Options"] = 'nosniff';
	      		oTIOA.header["X-XSS-Protection"] = '1; mode=block';
	      		oTIOA.header["Content-Security-Policy"] = "default-src 'none';";
	      		oTIOA.header["Referrer-Policy"] = "no-referrer";
	      		
//	      		oTIOA.header.UserID = sysInfoService.UserId;
//	      		oTIOA.header.UserRole = sysInfoService.RoleId;
//	      		oTIOA.header.OrganizationID = sysInfoService.OrganizationID;
//	      		oTIOA.header.LangType = "zh-TW";	//先固定
//	      		oTIOA.body = {"iptAppUsername":"2002562","iptAppUserPassword":"2001","iptRACFUser_ID":"888803","iptRACF_Password":"","ddlBranchNo":"200","kickOut":false}
	      		oTIOA.body = inputVO;
	      		
	      		var bShowWaitMsg = true,
	      			_time = sysInfoService.getServerParameter(),
	      			_out = {
	      				_default: function(){$.map(_time, function(row) {
							if(row.DATA.toString().trim() === 'DEFAULT'){
								flag = true;
								oTime = parseInt(row.LABEL.toString().trim())*60*1000;
							}
						})[0]}
							,
	      				_pecific: function(){$.map(_time, function(row) {
							if(row.DATA.toString().trim() === sTxnCode.toString().trim()){
								flag = true;
								oTime = parseInt(row.LABEL.toString().trim())*60*1000;
							}
						})[0]}
	      			};
	      		_out._default();
	      		_out._pecific();
	      		
//				if (sysInfoService.Browser.ie) {
//	      			///======================
//	      			/// IE8 之前略過
//	      			///======================
//					var nVer = parseFloat(sysInfoService.Browser.ie);
//					if (nVer <= 8) {
//						if (sTxnCode=="CommonPage" || sTxnCode=="GetComboBox" || sTxnCode == 'COMBOBOX') {
//							bShowWaitMsg = false;	
//						}
//					}
//				}
	      		
	      		//Exclude Codes
	      		if (sysInfoService.Exclude.code.indexOf(sTxnCode)!==-1) {
	      			bShowWaitMsg = false;
	      		}
	      		
				if (bShowWaitMsg) {
					var dialog = ngDialog.open({
						template: '<div><img style="display:block; margin:auto;vertical-align:middle;" width="100px" height="100px" src="./resource/cat.gif"></div>',
						plain: true,
						showClose: false,
						closeByDocument: false,
						closeByEscape: false,
						className: 'loading-Img'
					});				
					var promise = $timeout(function() {
						/**marking for production**/
//						console.log('關閉轉齒輪');
						if (dialog)	dialog.close();
					}, oTime);
				}
				var tita = JSON.stringify(oTIOA);
				/**marking for production**/
//				console.log('tita=' + tita)
	      		var request = $http({	method: 'POST', 
										url: './EsoafDispatcher',
	              						data: tita,
	              						timeout: oTime
	              					});

	      		return (request.then (
          			function(oResp) {
            			// I transform the successful response, unwrapping the application data
            			// from the API response payload.
          				/**marking for production**/
//						console.log('tota='+ JSON.stringify(oResp.data));

						$timeout.cancel(promise);
						if (dialog)	dialog.close();
						/**重設閒置時間**/
						if($rootScope.resetLogout)$rootScope.resetLogout();
            			return oResp.data;
          			},      				
					function(oErr) {
	                    // The API response from the server should be returned in a
	                    // nomralized format. However, if the request was not handled by the
	                    // server (or what not handles properly - ex. server error), then we
	                    // may have to normalize it on our end, as best we can.
						$timeout.cancel(promise);
						if (dialog)	dialog.close();
						/**重設閒置時間**/
						if($rootScope.resetLogout)$rootScope.resetLogout();
	                    if (
	                        ! angular.isObject( oErr.data ) ||
	                        ! oErr.data.message
	                        ) {
	                        return( $q.reject( "An unknown error occurred." ) );
	                    }

                		// Otherwise, use expected error message.
                		return( $q.reject( oErr.data.message ) );
        			})
      			);
	      	},
	      	///======================
	      	/// 取得指定使用者、角色的選單內容
	      	///======================
	      	getMenu : function(sUserId, sRoleId) {
	      		/**marking for production**/
//	      		console.log("getMenu");
	      		var request = $http({ method: 'POST', 
										 url: constServiceAddr + '/GetMenu',
	              						data: {'UserId':sUserId, 
	              						        'RoleID':sRoleId},
	    	              			 timeout: 120000
	              					});

	      		return (request.then (
          			function(oResp) {
            			// I transform the successful response, unwrapping the application data
            			// from the API response payload. 							
          				//console.log(oResp);
            			return oResp.data;
          			},      				
					function(oErr) {
	                    // The API response from the server should be returned in a
	                    // nomralized format. However, if the request was not handled by the
	                    // server (or what not handles properly - ex. server error), then we
	                    // may have to normalize it on our end, as best we can.
	                    if (
	                        ! angular.isObject( oErr.data ) ||
	                        ! oErr.data.message
	                        ) {
	                        return( $q.reject( "An unknown error occurred." ) ); //"An unknown error occurred."
	                    }

                		// Otherwise, use expected error message.
                		return( $q.reject( oErr.data.message ) );
        			})
      			);
	      	}
	    };
 	}]
);
