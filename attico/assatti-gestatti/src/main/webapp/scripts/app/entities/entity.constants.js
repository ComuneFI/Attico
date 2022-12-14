'use strict';

angular.module('cifra2gestattiApp')
        .constant('SCENARIO_RULES', [
        				{"code": "PubblicazioneNonModificabile","fields": [
		                         {id:".atto-divulgazione textarea:not(.everActive)",action:"disabled", value:true},
		                         {id:".atto-divulgazione select:not(.everActive)",action:"disabled", value:true},
		                         {id:".atto-divulgazione input:not(.everActive)",action:"disabled", value:true, not:'.everActive .ui-select-search'},
		                         {id:".atto-divulgazione button:not(.everActive, #saveAttoButton)",action:"disabled", value:true},
		                         {id:".atto-divulgazione .note-editable",action:"contenteditable", value:false, not:".everActive .note-editable"},
		                         {id:".atto-divulgazione #uploader",action:"class", value:'hide'},
		                         {id:".atto-divulgazione #areaAllegati",action:"class", value:'hide'},
		                         {id:".atto-divulgazione select:not(.everActive)",action:"disabled", value:true},
		                         {id:".atto-divulgazione .ui-select-toggle:not(.everActive .ui-select-toggle)",action:"disabled", value:true},
		                         {id:".atto-divulgazione .ui-select-match-item:not(.everActive .ui-select-match-item)",action:"disabled", value:true},
		                         {id:".atto-divulgazione .modal-dialog *",action:"disabled", value:false},
		                         {id:".atto-divulgazione #attoAdempimentiContabiliEdit *",action:"disabled", value:true},
                        		 {id:".atto-divulgazione #attoAdempimentiContabiliEdit .note-editable",action:"contenteditable", value:false},
	                         	 {id:".atto-divulgazione #attoAdempimentiContabiliEdit button",action:"disabled", value:true},
	                         	 {id:".atto-divulgazione #includiMovimentiAttoEdit",action:"disabled", value:true},
	                         	 {id:".atto-divulgazione #nascondiBeneficiariMovimentiEdit",action:"disabled", value:true},
	                         ]
                        },
        				{"code": "DatiIdentificativiNonModificabili","fields": [
		                         {id:".dati-identificativi textarea:not(.everActive)",action:"disabled", value:true},
		                         {id:".dati-identificativi select:not(.everActive)",action:"disabled", value:true},
		                         {id:".dati-identificativi input:not(.everActive)",action:"disabled", value:true, not:'.everActive .ui-select-search'},
		                         {id:".dati-identificativi button:not(.everActive, #saveAttoButton)",action:"disabled", value:true},
		                         {id:".dati-identificativi .note-editable",action:"contenteditable", value:false, not:".everActive .note-editable"},
		                         {id:".dati-identificativi #uploader",action:"class", value:'hide'},
		                         {id:".dati-identificativi #areaAllegati",action:"class", value:'hide'},
		                         {id:".dati-identificativi select:not(.everActive)",action:"disabled", value:true},
		                         {id:".dati-identificativi .ui-select-toggle:not(.everActive .ui-select-toggle)",action:"disabled", value:true},
		                         {id:".dati-identificativi .ui-select-match-item:not(.everActive .ui-select-match-item)",action:"disabled", value:true},
		                         {id:".dati-identificativi .modal-dialog *",action:"disabled", value:false},
		                         {id:".dati-identificativi #attoAdempimentiContabiliEdit *",action:"disabled", value:true},
                        		 {id:".dati-identificativi #attoAdempimentiContabiliEdit .note-editable",action:"contenteditable", value:false},
	                         	 {id:".dati-identificativi #attoAdempimentiContabiliEdit button",action:"disabled", value:true},
	                         	 {id:".dati-identificativi #includiMovimentiAttoEdit",action:"disabled", value:true},
	                         	 {id:".atto-divulgazione #nascondiBeneficiariMovimentiEdit",action:"disabled", value:true},
	                         ]
                        },
                        {"code": "EmananteNonModificabile","fields": [{id:"#emananteSottoscrittore",action:"disabled", value:true},{id:"#adempimentiContabili *",action:"disabled", value:true}]},
                        {"code": "TipoIterNonModificabile","fields": [
                        		{id:"#tipoIter",action:"disabled", value:true},
                        		{id:"#attoAdempimentiContabiliEdit *",action:"disabled", value:true},
                        		{id:"#attoAdempimentiContabiliEdit.note-editable",action:"contenteditable", value:false},
	                         	{id:"#attoAdempimentiContabiliEdit button",action:"disabled", value:true},
                        		]
                        },
                        {"code": "DurataPubblicazioneNonModificabile","fields": [{id:"#numeroGiorni",action:"disabled", value:true},{id:"#testoMotivazione",action:"class", value:'hide'},{id:"#attoAdempimentiContabiliEdit *",action:"disabled", value:true},
                        		{id:"#attoAdempimentiContabiliEdit .note-editable",action:"contenteditable", value:false},
	                         	{id:"#attoAdempimentiContabiliEdit button",action:"disabled", value:true},
	                         	{id:"#includiMovimentiAttoEdit",action:"disabled", value:true},
	                         	{id:"#nascondiBeneficiariMovimentiEdit",action:"disabled", value:true},
	                         	]
	                    },
                        {"code": "SezioneSottoscrittoriNonModificabile","fields": [{id:".sectionSottoscrittore",action:"disabled", value:true},{id:"#attoAdempimentiContabiliEdit *",action:"disabled", value:true},
                        		{id:"#attoAdempimentiContabiliEdit .note-editable",action:"contenteditable", value:false},
	                         	{id:"#attoAdempimentiContabiliEdit button",action:"disabled", value:true},
	                         	]
	                    },
                        {"code": "TuttoNonModificabile","fields": [
		                         {id:"textarea:not(.everActive)",action:"disabled", value:true},
		                         {id:"select:not(.everActive)",action:"disabled", value:true},
		                         {id:"input:not(.everActive)",action:"disabled", value:true, not:'.everActive .ui-select-search'},
		                         {id:"button:not(.everActive, #saveAttoButton)",action:"disabled", value:true},
		                         {id:".note-editable",action:"contenteditable", value:false, not:".everActive .note-editable"},
		                         {id:"#uploader",action:"class", value:'hide'},
		                         {id:"#areaAllegati",action:"class", value:'hide'},
		                         {id:"select:not(.everActive)",action:"disabled", value:true},
		                         {id:".ui-select-toggle:not(.everActive .ui-select-toggle)",action:"disabled", value:true},
		                         {id:".ui-select-match-item:not(.everActive .ui-select-match-item)",action:"disabled", value:true},
		                         {id:".modal-dialog *",action:"disabled", value:false},
		                         {id:"#attoAdempimentiContabiliEdit *",action:"disabled", value:true},
                        		 {id:"#attoAdempimentiContabiliEdit .note-editable",action:"contenteditable", value:false},
	                         	 {id:"#attoAdempimentiContabiliEdit button",action:"disabled", value:true},
	                         	 {id:"#includiMovimentiAttoEdit",action:"disabled", value:true},
	                         	 {id:"#nascondiBeneficiariMovimentiEdit",action:"disabled", value:true},
	                         ]
                        },
                        {"code": "TestoPropostaModificabile","fields": [
                        	 // FIX abilitazione oggetto
                        	 {id:"textarea",action:"disabled", value:false},
	                         {id:"textarea",action:"disabled", value:true, not:".everActive, .oggettoActive"},
	                         {id:"select:not(.everActive)",action:"disabled", value:true},
	                         {id:"input:not(.everActive)",action:"disabled", value:true, not:'.everActive .ui-select-search'},
	                         {id:"button:not(.everActive, #saveAttoButton)",action:"disabled", value:true},
	                         {id:".note-editable",action:"contenteditable", value:false, not:".everActive .note-editable"},
	                         {id:"select:not(.everActive)",action:"disabled", value:true},
	                         {id:".ui-select-toggle:not(.everActive .ui-select-toggle)",action:"disabled", value:true},
	                         {id:".ui-select-match-item:not(.everActive .ui-select-match-item)",action:"disabled", value:true},
	                         {id:"#attoFinanziamentiEdit *",action:"disabled", value:false},
	                         {id:"#attoPreamboloEdit *",action:"disabled", value:false},
	                         {id:"#attoPreamboloEdit .note-editable",action:"contenteditable", value:true},
	                         {id:"#attoPreamboloButtons button",action:"disabled", value:false},
	                         {id:"#attoDomandaEdit *",action:"disabled", value:false},
	                         {id:"#attoDomandaEdit .note-editable",action:"contenteditable", value:true},
	                         {id:"#includiMovimentiAttoEdit",action:"disabled", value:false},
	                         {id:"#nascondiBeneficiariMovimentiEdit",action:"disabled", value:false},
	                         {id:"#attoDomandaButtons button",action:"disabled", value:false},
	                         {id:"#attoMotivazioneEdit *",action:"disabled", value:false},
	                         {id:"#attoMotivazioneEdit .note-editable",action:"contenteditable", value:true},
	                         {id:"#attoMotivazioneButtons button",action:"disabled", value:false},
	                         {id:"#attoDispositivoEdit *",action:"disabled", value:false},
	                         {id:"#attoDispositivoEdit .note-editable",action:"contenteditable", value:true},
	                         {id:"#attoDispositivoButtons button",action:"disabled", value:false},
	                         {id:".modal-dialog *",action:"disabled", value:false},
	                         {id:"#attoAdempimentiContabiliEdit *",action:"disabled", value:true},
                        	 {id:"#attoAdempimentiContabiliEdit .note-editable",action:"contenteditable", value:false},
	                         {id:"#attoAdempimentiContabiliEdit button",action:"disabled", value:true},
	                         {id:"#tipoIter",action:"disabled", value:true},
	                        ]
	                    },
	                    {"code": "DatiPubblicazioneModificabili","fields": [
	                         {id:"textarea",action:"disabled", value:false},
	                         {id:"textarea",action:"disabled", value:true, not:".everActive, .oggettoActive"},
	                         {id:"select:not(.everActive)",action:"disabled", value:true},
	                         {id:"input:not(.everActive)",action:"disabled", value:true, not:'.everActive .ui-select-search'},
	                         {id:"button:not(.everActive, #saveAttoButton)",action:"disabled", value:true},
	                         {id:".note-editable",action:"contenteditable", value:false, not:".everActive .note-editable"},
	                         {id:"#uploader",action:"class", value:'hide'},
	                         {id:"#areaAllegati",action:"class", value:'hide'},
	                         {id:"select:not(.everActive)",action:"disabled", value:true},
	                         {id:".ui-select-toggle:not(.everActive .ui-select-toggle)",action:"disabled", value:true},
	                         {id:".ui-select-match-item:not(.everActive .ui-select-match-item)",action:"disabled", value:true},
	                         {id:"#pubblicazioneFormFields input:not(.everActive)",action:"disabled", value:false},
	                         {id:"#pubblicazioneFormFields select:not(.pubblicazioneIntegrale)",action:"disabled", value:false},
	                         {id:"#pubblicazioneFormFields .note-editable",action:"contenteditable", value:true},
	                         {id:"#pubblicazioneFormFields button",action:"disabled", value:false},
	                         {id:".modal-dialog *",action:"disabled", value:false},
	                         {id:"#attoAdempimentiContabiliEdit *",action:"disabled", value:true},
                        	 {id:"#attoAdempimentiContabiliEdit .note-editable",action:"contenteditable", value:false},
	                         {id:"#attoAdempimentiContabiliEdit button",action:"disabled", value:true},
	                         {id:"#includiMovimentiAttoEdit",action:"disabled", value:true},
	                         {id:"#nascondiBeneficiariMovimentiEdit",action:"disabled", value:true},
	                         {id:"#attoFinanziamentiEdit *",action:"disabled", value:false},
	                        ]
	                    },
	                    // DISABILITA CAMPI PRESENZE/VOTAZIONI PER INSERIMENTO ESITO SEDUTA
	                    {"code": "VotazioniPresenzeNonModificabili","fields": [
	                    	 {id:"#resocontoAtto textarea:not(.everActive)",action:"disabled", value:true},
	                         {id:"#resocontoAtto input:not(.everActive)",action:"disabled", value:true},
	                         {id:"#resocontoAtto button:not(.everActive)",action:"disabled", value:true},
	                         {id:"#resocontoAtto .closeModalEsito:not(.everActive)",action:"disabled", value:false},	                         
	                         {id:"#resocontoAtto select:not(.everActive)",action:"disabled", value:true},
	                         {id:".ui-select-toggle:not(.everActive .ui-select-toggle)",action:"disabled", value:true},
	                         {id:".ui-select-match-item:not(.everActive .ui-select-match-item)",action:"disabled", value:true},
	                       ]
	                    },
	                    {"code": "ModificheCoordinamentoTesto","fields": [
	                    	 {id:".esito-select-control",action:"disabled", value:true}
	                    	]
	                    },
	                    {"code": "NoteContabiliEditabili","fields": [
		                         {id:"textarea:not(.everActive)",action:"disabled", value:true},
		                         {id:"select:not(.everActive)",action:"disabled", value:true},
		                         {id:"input:not(.everActive)",action:"disabled", value:true, not:'.everActive .ui-select-search'},
		                         {id:"button:not(.everActive, #saveAttoButton)",action:"disabled", value:true},
		                         {id:".note-editable",action:"contenteditable", value:false, not:".everActive .note-editable"},
		                         {id:"#uploader",action:"class", value:'hide'},
		                         {id:"#areaAllegati",action:"class", value:'hide'},
		                         {id:".ui-select-toggle:not(.everActive .ui-select-toggle)",action:"disabled", value:true},
		                         {id:".ui-select-match-item:not(.everActive .ui-select-match-item)",action:"disabled", value:true},
		                         {id:".modal-dialog *",action:"disabled", value:false},
		                         {id:"#adempimentiContabili *",action:"disabled", value:false},
		                         {id:"#adempimentiContabili .note-editable",action:"contenteditable", value:true},
		                         {id:"#adempimentiContabiliButtons button",action:"disabled", value:false},
		                         {id:"#informazioniAnagraficoContabili *",action:"disabled", value:false, not:".everActive",},
		                         {id:"#informazioniAnagraficoContabili .note-editable",action:"contenteditable", value:true},
		                         {id:"#informazioniAnagraficoContabiliButtons button",action:"disabled", value:false},
		                         {id:"#includiMovimentiAttoEdit",action:"disabled", value:false},
		                         {id:"#nascondiBeneficiariMovimentiEdit",action:"disabled", value:false},
	                         ]
                        },
                        {"code": "NoteContabiliEditabiliDL","fields": [
	                         {id:"textarea:not(.everActive)",action:"disabled", value:true},
	                         {id:"input:not(.everActive)",action:"disabled", value:true, not:'.everActive .ui-select-search'},
	                         {id:"button:not(.everActive, #saveAttoButton)",action:"disabled", value:true},
	                         {id:".note-editable",action:"contenteditable", value:false, not:".everActive .note-editable"},
	                         {id:"#uploader",action:"class", value:'hide'},
	                         {id:"#areaAllegati",action:"class", value:'hide'},
	                         {id:"select:not(.everActive)",action:"disabled", value:true},
	                         {id:".ui-select-toggle:not(.everActive .ui-select-toggle)",action:"disabled", value:true},
	                         {id:".ui-select-match-item:not(.everActive .ui-select-match-item)",action:"disabled", value:true},
	                         {id:".modal-dialog *",action:"disabled", value:false},
	                         {id:"#adempimentiContabili *",action:"disabled", value:false},
	                         {id:"#adempimentiContabili .note-editable",action:"contenteditable", value:true},
	                         {id:"#adempimentiContabiliButtons button",action:"disabled", value:false},
	                         {id:"#informazioniAnagraficoContabili *",action:"disabled", value:false, not:".everActive",},
	                         {id:"#informazioniAnagraficoContabili .note-editable",action:"contenteditable", value:true},
	                         {id:"#informazioniAnagraficoContabiliButtons button",action:"disabled", value:false},
	                         {id:"#includiMovimentiAttoEdit",action:"disabled", value:true},
	                         {id:"#nascondiBeneficiariMovimentiEdit",action:"disabled", value:true},
                        ]
                   },
                        {"code": "PostPubblicazioneTrasparenza","fields": [
		                         {id:"textarea",action:"disabled", value:false},
		                         {id:"textarea",action:"disabled", value:true, not:".everActive, .oggettoActive"},
		                         {id:"select:not(.everActive)",action:"disabled", value:true},
		                         {id:"input:not(.everActive)",action:"disabled", value:true, not:'.everActive .ui-select-search'},
		                         {id:"button:not(.everActive, #saveAttoButton)",action:"disabled", value:true},
		                         {id:".note-editable",action:"contenteditable", value:false, not:".everActive .note-editable"},
		                         {id:"#uploader",action:"class", value:'hide'},
		                         {id:"#areaAllegati",action:"class", value:'hide'},
		                         {id:"select:not(.everActive)",action:"disabled", value:true},
		                         {id:".ui-select-toggle:not(.everActive .ui-select-toggle)",action:"disabled", value:true},
		                         {id:".ui-select-match-item:not(.everActive .ui-select-match-item)",action:"disabled", value:true},
		                         {id:"#pubblicazioneFormFields input:not(.everActive)",action:"disabled", value:false},
		                         {id:"#pubblicazioneFormFields select:not(.pubblicazioneIntegrale)",action:"disabled", value:false},
		                         {id:"#pubblicazioneFormFields .note-editable",action:"contenteditable", value:true},
		                         {id:"#pubblicazioneFormFields .pubblicazione",action:"disabled", value:true},
		                         {id:"#pubblicazioneFormFields button",action:"disabled", value:false},
		                         {id:".modal-dialog *",action:"disabled", value:false},
		                         {id:"#attoAdempimentiContabiliEdit *",action:"disabled", value:true},
	                        	 {id:"#attoAdempimentiContabiliEdit .note-editable",action:"contenteditable", value:false},
		                         {id:"#attoAdempimentiContabiliEdit button",action:"disabled", value:true},
		                         {id:"#includiMovimentiAttoEdit",action:"disabled", value:true}
	                         ]
                        },
                        ]);