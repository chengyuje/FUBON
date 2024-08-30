///======================
/// 原StrUtil.as
///======================
eSoafApp.factory('strService', function() {

	return {
		padLeft : function(value, count, char) {
			console.log('value=' + value + ', count=' + count + ', char='
					+ char);

			while (value.length < count) {
				value = char + value;
			}
			return value;
		},
		padRight : function(value, count, char) {
			console.log('value=' + value + ', count=' + count + ', char='
					+ char);

			while (value.length < count) {
				value = value + char;
			}
			return value;
		},
		getRandomString : function(length, char) {
			char = char || '0';
			var randomNumber = (Math.floor(Math.random() * 10));
			var value;
			var tmp = randomNumber.toString();

			if (tmp.length < length) {
				value = padLeft(tmp, length, char);
			} else {
				value = tmp.substring(0, length);
			}
			return value;
		},
		toFullShapeString : function(value) {
			var val = "";

			for (var i = 0; i < value.length; i++) {
				var code = value.charCodeAt(i);

				if (code == 32) { // 處理空白
					code = 12288;
				} else {
					if (code < 127) {
						code += 65248;
					}
				}
				val = val + String.fromCharCode(code);
			}
			return val;
		}
	};
});