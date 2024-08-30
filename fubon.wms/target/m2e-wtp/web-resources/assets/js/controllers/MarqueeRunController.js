eSoafApp.controller("MarqueeRunController", ['$rootScope', '$scope', '$http', '$cookies', 'socketService', 'sysInfoService',
	function($rootScope, $scope, $http, $cookies, socketService, sysInfoService)
		{
			$rootScope.initMarquee = function(){
				$scope.sendRecv("MARQUEE", "inquireToday", "com.systex.jbranch.app.server.fps.marquee.MARQUEEInputVO", {},
						function(totas, isError) {
		                	if (isError) {
		                		$scope.showErrorMsg(totas[0].body.msgData);
		                		return;
		                	}
		                	if (totas.length > 0) {
		                		$scope.Marquee = totas[0].body.resultList;
//		                		if($scope.Marquee.length == 0)
//		                			$scope.Marquee.push({"MSG_LEVEL":"1","TITLE":"無資料","CONTENT":"本日沒有訊息"});
//		                		console.log('MarqueeList='+JSON.stringify($scope.Marquee));
		                	};
						}
				);
			};
			$rootScope.initMarquee();
		}
]);