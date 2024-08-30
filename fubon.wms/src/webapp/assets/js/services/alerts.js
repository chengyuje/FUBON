eSoafApp.service('alerts',['$q', '$window', function($q, $window) {
  this.confirm = function(message, title, buttonLabels) {
    var defer = $q.defer();

    if (navigator.notification && navigator.notification.confirm) {
      var onConfirm = function(idx) {
        idx === 1 ? defer.resolve() : defer.reject()
      }

      navigator.notification.confirm(message, onConfirm, title, buttonLabels)
    } else {
      window.confirm(message) ? defer.resolve() : defer.reject()
    }

    return defer.promise;
  }
  
  this.alert = function(message){
	  window.alert(message);
  }
}]);