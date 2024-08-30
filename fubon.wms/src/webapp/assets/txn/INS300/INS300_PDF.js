angular.module('INS300', [])
    .controller('INS300_PDFController',
        function($scope) {
            $scope.href = [];
            $scope.href = window.location.href.replace('&&', '').split('pdfurl=').splice(0, 1);
            $scope.hrefLength = $scope.href.length;
            $scope.windowName = window.name;
        });