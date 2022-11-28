'use strict';

angular.module('cifra2gestattiApp')
    .filter('entityAttoDetailFilter', function($filter){
    	  return function(items,toFilter){
    		  var entityId = toFilter['entityId'];
    		  if(items instanceof Array){
    			  var values=[];
	       	       if(items!=undefined && items!=null && items){
	   	    	    	 for(var v = 0; v<items.length; v++){
	   	    	    		 if(Object.keys(toFilter)[0].indexOf(".") < 0){
		       	    			 if((entityId && entityId == items[v].id) || toFilter[Object.keys(toFilter)[0]] === items[v][Object.keys(toFilter)[0]]){
		       	    				values.push(items[v]);
		       	    			 }
	   	    	    		 }else{
	   	    	    			var leftAttr = Object.keys(toFilter)[0].split(".")[0];
	   	    	    			var rigthAttr = Object.keys(toFilter)[0].substring(leftAttr.length + 1);
	   	    	    			if((entityId && entityId == items[v].id) || toFilter[Object.keys(toFilter)[0]] === items[v][leftAttr][rigthAttr]){
		       	    				values.push(items[v]);
		       	    			 }
	   	    	    		 }
	   	    	    	 }
	   	    	    	return values;
	       	       }else{
	       	    	   return items;
	       	       }
    		  }else{
    			  var values={};
	       	       if(items!=undefined && items!=null && items){
	   	    	    	 Object.keys(items).forEach(function(v){
	   	    	    		if(Object.keys(toFilter)[0].indexOf(".") < 0){
		   	    	    		 if((entityId && entityId == items[v].id) || toFilter[Object.keys(toFilter)[0]] === items[v][Object.keys(toFilter)[0]]){
		       	    				values[v] = items[v];
		       	    			 }
	   	    	    		}else{
	   	    	    			var leftAttr = Object.keys(toFilter)[0].split(".")[0];
	   	    	    			var rigthAttr = Object.keys(toFilter)[0].substring(leftAttr.length + 1);
	   	    	    			if((entityId && entityId == items[v].id) || toFilter[Object.keys(toFilter)[0]] === items[v][leftAttr][rigthAttr]){
	   	    	    				values[v] = items[v];
		       	    			 }
	   	    	    		 }
	   	    	        });
	   	    	    	return values;
	       	       }else{
	       	    	   return items;
	       	       }
    		  }    	       
    	  };
    	});