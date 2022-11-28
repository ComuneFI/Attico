/**
 * @license Copyright (c) 2003-2021, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see https://ckeditor.com/legal/ckeditor-oss-license
 */

CKEDITOR.editorConfig = function( config ) {
	/*
	// %REMOVE_START%
	// The configuration options below are needed when running CKEditor from source files.
	config.plugins = 'dialogui,dialog,a11yhelp,dialogadvtab,basicstyles,bidi,blockquote,notification,button,toolbar,clipboard,panelbutton,panel,floatpanel,colorbutton,colordialog,menu,contextmenu,copyformatting,editorplaceholder,resize,elementspath,enterkey,entities,popup,filetools,find,fakeobjects,floatingspace,listblock,richcombo,font,format,horizontalrule,htmlwriter,wysiwygarea,indent,indentblock,indentlist,justify,menubutton,link,list,liststyle,magicline,maximize,newpage,pagebreak,pastetext,xml,ajax,pastetools,pastefromgdocs,pastefromlibreoffice,pastefromword,removeformat,selectall,showblocks,showborders,sourcearea,specialchar,scayt,stylescombo,tab,table,tabletools,tableselection,tableresize,undo,lineutils,widgetselection,widget,notificationaggregator';
	config.skin = 'moonocolor';
	config.language = 'it';
	config.removeButtons = 'Anchor';
	config.extraPlugins = 'custom';
	config.enterMode = CKEDITOR.ENTER_BR;
	
	
	// %REMOVE_END%

	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	*/
	
	config.plugins = 'dialogui,dialog,a11yhelp,dialogadvtab,basicstyles,bidi,notification,button,toolbar,clipboard,panelbutton,panel,floatpanel,colordialog,menu,contextmenu,copyformatting,editorplaceholder,resize,elementspath,enterkey,entities,popup,filetools,find,fakeobjects,floatingspace,listblock,richcombo,format,horizontalrule,htmlwriter,wysiwygarea,indent,indentblock,indentlist,justify,menubutton,list,liststyle,magicline,newpage,pagebreak,pastetext,xml,ajax,pastetools,pastefromgdocs,pastefromlibreoffice,pastefromword,removeformat,selectall,showblocks,showborders,sourcearea,specialchar,scayt,stylescombo,tab,table,tabletools,tableselection,tableresize,undo,lineutils,widgetselection,widget,notificationaggregator';
	config.skin = 'moonocolor';
	config.language = 'it';
	config.extraPlugins = 'custom';
	config.removeButtons = 'PasteText,PasteFromWord,Anchor,Styles';
	
	config.enterMode = CKEDITOR.ENTER_BR;
	
	//config.coreStyles_italic = { element: 'i', overrides: 'em' };

	config.coreStyles_italic = {
	    element: 'i',
	    styles:         { 'font-family': 'Arial' }
	};
	
//	config.font_style = {
//		    element:        'i',
//		    styles:         { 'font-family': 'Arial' },
//		    overrides:      [ { element: 'font', attributes: { 'face': null } } ]
//		};
};
