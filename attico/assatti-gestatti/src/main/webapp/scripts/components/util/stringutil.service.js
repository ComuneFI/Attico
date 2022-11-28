'use strict';

angular.module('cifra2gestattiApp')
    .service('StringUtils', function () {
      this.removeAccentsFromString = function(str) {
	  var map = {
	        'a' : 'á|à|ã|â|À|Á|Ã|Â',
	        'e' : 'é|è|ê|É|È|Ê',
	        'i' : 'í|ì|î',
	        'o' : 'ó|ò|ô|õ|Ó|Ò|Ô|Õ',
	        'u' : 'ú|ù|û|ü|Ú|Ù|Û|Ü',
	        'c' : 'ç|Ç',
	        'n' : 'ñ|Ñ'
	    };
	    
	    angular.forEach(map, function (pattern, newValue) {
	        str = str.replace(new RegExp(pattern, 'g'), newValue);
	    });

	    return str;
      };
      this.removeAccentsFromJson = function(obj) {
    	    var str = JSON.stringify(obj);
    	    str = this.removeAccentsFromString(str);
    	    obj = JSON.parse(str);
    	    return obj;
      };
    });
