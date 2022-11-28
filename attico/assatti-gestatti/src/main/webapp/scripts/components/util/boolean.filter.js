'use strict';

angular.module('cifra2gestattiApp')
    .filter('booleanFilter', function($filter){
    	  return function(items,toFilter){
    		  if(items instanceof Array){
    			  var values=[];
	       	       if(items!=undefined && items!=null && items){
	   	    	    	 Object.keys(items).forEach(function(v){
	       	    			 if(toFilter[Object.keys(toFilter)[0]] === items[v][Object.keys(toFilter)[0]]){
	       	    				 values.push(items[v]);
	       	    			 }
	   	    	        });
	   	    	    	return values;
	       	       }else{
	       	    	   return items;
	       	       }
    		  }else{
    			  var values={};
	       	       if(items!=undefined && items!=null && items){
	   	    	    	 Object.keys(items).forEach(function(v){
	       	    			 if(toFilter[Object.keys(toFilter)[0]] === items[v][Object.keys(toFilter)[0]]){
	       	    				 values[v] = items[v];
	       	    			 }
	   	    	        });
	   	    	    	return values;
	       	       }else{
	       	    	   return items;
	       	       }
    		  }    	       
    	  };
    	});