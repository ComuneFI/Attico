'use strict';

angular.module('cifra2gestattiApp')
    .controller('ModelloHtmlController', function ($scope, ModelloHtml, $http, ngToast, ParseLinks, TipoDocumento) {
    	$scope.page = 1;
    	$scope.tempSearch = {};
        $scope.modelloHtmlSearch = {};
        
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.resetRicerca = function(){
        	$scope.page = 1;
        	$scope.modelloHtmlSearch = {};
        	$scope.tempSearch = {};
        	$scope.loadAll();
        };
        
        $scope.ricerca = function(){
        	$scope.page = 1;
        	/*copio tempSearch e suoi elementi in modelloHtmlSearch*/
        	$scope.modelloHtmlSearch = jQuery.extend(true, {}, $scope.tempSearch);
        	$scope.loadAll($scope.tempSearch);
        };
        
        $scope.loadAll = function(searchObject) {
        	var tipodocumentoCodice = null;
        	if(searchObject!=undefined && searchObject!=null){
        		if(searchObject.tipoDocumento!=undefined && searchObject.tipoDocumento!=null && searchObject.tipoDocumento.codice!=undefined && searchObject.tipoDocumento.codice!=null){
        			tipodocumentoCodice = searchObject.tipoDocumento.codice;
        		}
        		ModelloHtml.query({page: $scope.page, per_page: 5, titolo:searchObject.titolo, tipoDocumento:tipodocumentoCodice}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.modelloHtmls = result;
	            });
        	}else{
        		if($scope.modelloHtmlSearch.tipoDocumento!=undefined && $scope.modelloHtmlSearch.tipoDocumento!=null && $scope.modelloHtmlSearch.tipoDocumento.codice!=undefined && $scope.modelloHtmlSearch.tipoDocumento.codice!=null){
        			tipodocumentoCodice = $scope.modelloHtmlSearch.tipoDocumento.codice;
        		}
        		ModelloHtml.query({page: $scope.page, per_page: 5, titolo:$scope.modelloHtmlSearch.titolo, tipoDocumento:tipodocumentoCodice}, function(result, headers) {
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.modelloHtmls = result;
	            });
        	}
        };
        
        $scope.options = {
                height: 450,
                //width: 620,
                focus: false,
                airMode: false,
                lang: 'it-IT',
                toolbar: [
                        ['edit',['undo','redo']],
                        ['fontclr', ['color']],
                        ['headline', ['style']],
                        ['style', ['bold', 'italic', 'underline', 'superscript', 'subscript', 'strikethrough', 'clear']],
                        ['fontface', ['fontname']],
                        ['textsize', ['fontsize']],
                        
                        ['alignment', ['ul', 'ol', 'paragraph', 'lineheight']],
                        ['height', ['height']],
                        ['table', ['table']],
                        ['insert', ['picture','hr','pagebreak']],
                        ['view', ['fullscreen', 'codeview']],
                        ['cifra2', ['omissis','proprieta']],
                        ['help', ['help']]
                    ],
                codemirror: {
                    theme: 'monokai'
                },
                plugin : {
                    cifra2 : {
                        ricerca: function (callback) {
                            var tipoDocumento= $scope.modelloHtml.tipoDocumento;
                            var uri = 'scripts/components/summernote/plugin/'+tipoDocumento+'.json';
                            callback(uri);
                        }
                    }
                }
               
        };

        

        $scope.init = function() { console.log('Summernote is launched'); }
        $scope.enter = function() { console.log('Enter/Return key pressed'); }
        $scope.focus = function(e) { console.log('Editable area is focused'); }
        $scope.blur = function(e) { console.log('Editable area loses focus'); }
        $scope.paste = function(e) { console.log('Called event paste'); }
        $scope.change = function(contents) {
           console.log('contents are changed:', contents, $scope.editable);
        };
        $scope.keyup = function(e) { console.log('Key is released:', e.keyCode); }
        $scope.keydown = function(e) { console.log('Key is pressed:', e.keyCode); }
        $scope.imageUpload = function(files) {
            console.log('image upload:', files);
            console.log('image upload\'s editable:', $scope.editable);
        }

        $scope.toolbarClick = function(evt){
            console.log('toolbarClick:', evt);
        }

        $scope.modelloHtmls = [];
        TipoDocumento.query({}, function(result){
        	$scope.tipoDocumentos = result;
        });

       
        $scope.loadAll();


        $scope.newModelloHtml = function ( ) {
                $scope.editing = true;
                //$scope.attotest = ModelloHtml.attotest();
                $scope.modelloHtml = {titolo: null, html: null, id: null, pageOrientation: false};
        };

        $scope.showUpdate = function (id) {
            ModelloHtml.get({id: id}, function(result) {
                $scope.modelloHtml = result;
                $scope.editing = true;
                //$scope.attotest = ModelloHtml.attotest();
            });
        };

        $scope.save = function () {
            if ($scope.modelloHtml.id != null) {
                ModelloHtml.update($scope.modelloHtml,
                    function (result) {
                        //$scope.refresh();
                        $scope.modelloHtml = result;
                        ngToast.create(  { className: 'success', content: 'Salvataggio avvenuto con successo' });
                    });
            } else {
                ModelloHtml.save($scope.modelloHtml,
                    function (result) {
                        //$scope.refresh();
                        $scope.modelloHtml = result;
                        ngToast.create(  { className: 'success', content: 'Salvataggio avvenuto con successo' });
                    });
            }
        };

        $scope.delete = function (id) {
            ModelloHtml.get({id: id}, function(result) {
                $scope.modelloHtml = result;
                $('#deleteModelloHtmlConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ModelloHtml.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteModelloHtmlConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.modelloHtml = {titolo: null, html: null, id: null};
            $scope.editing = false;
        };

        $scope.populateModelloHtml = function(idTipoDoc){
			if(idTipoDoc=='DIR'){
                ModelloHtml.popDIR(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='DEL'){
                ModelloHtml.popDEL(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='SDL'){
                ModelloHtml.popSDL(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='COM'){
                ModelloHtml.popCOM(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='DDL'){
                ModelloHtml.popDDL(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='RFT'){
                ModelloHtml.popRFT(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='ORD'){
                ModelloHtml.popORD(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='DPR'){
                ModelloHtml.popDPR(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='ordinegiornogiunta'){
                ModelloHtml.popOdgGiunta(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='ordinegiornoconsiglio'){
                ModelloHtml.popOdgConsiglio(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='parere'){
                ModelloHtml.popParere(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='lettera'){
                ModelloHtml.popLettera(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='verbalegiunta'){
                ModelloHtml.popVerbaleGiunta(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='verbaleconsiglio'){
                ModelloHtml.popVerbaleConsiglio(function(){
                    ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                    $scope.refresh();
             })
			}else if(idTipoDoc=='resoconto'){
                ModelloHtml.popResoconto(function(){
                     ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                     $scope.refresh();
              })
			}else if(idTipoDoc=='scheda_anagrafico_contabile'){
                ModelloHtml.popSchedaAnagraficoContabile(function(){
                    ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                    $scope.refresh();
             })
			}else if(idTipoDoc=='report_ricerca'){
                ModelloHtml.popReportRicerca(function(){
                    ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
                    $scope.refresh();
             })
			}else if(idTipoDoc=='atto_inesistente'){
				ModelloHtml.popAttoInesistente(function(){
					ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
					$scope.refresh();
				})
			}else if(idTipoDoc=='restituzione_su_istanza_ufficio_proponente'){
				ModelloHtml.popRestituzioneSuIstanzaUfficioProponente(function(){
					ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
					$scope.refresh();
				})
			}else if(idTipoDoc=='relata_pubblicazione'){
				ModelloHtml.popRelataPubblicazione(function(){
					ngToast.create(  { className: 'success', content: 'Popolazione avvenuta con successo' });
					$scope.refresh();
				})
			}
        };

    });
