CKEDITOR.plugins.add( 'custom', {
	lang: 'it',
	icons: 'omissis',
	hidpi: true, // %REMOVE_LINE_CORE%
	init: function( editor ) {
		var order = 0;
		var addButtonCommand = function( buttonName, buttonLabel, commandName, styleDefiniton ) {
				// Disable the command if no definition is configured.
				if ( !styleDefiniton )
					return;

				var style = new CKEDITOR.style( styleDefiniton ),
					forms = contentForms[ commandName ];

				// Put the style as the most important form.
				forms.unshift( style );

				// Listen to contextual style activation.
				editor.attachStyleStateChange( style, function( state ) {
					!editor.readOnly && editor.getCommand( commandName ).setState( state );
				} );

				// Create the command that can be used to apply the style.
				editor.addCommand( commandName, new CKEDITOR.styleCommand( style, {
					contentForms: forms
				} ) );

				// Register the button, if the button plugin is loaded.
				if ( editor.ui.addButton ) {
					editor.ui.addButton( buttonName, {
						label: buttonLabel,
						command: commandName,
						toolbar: 'basicstyles,' + ( order += 500 )
					} );
				}
			};

		var contentForms = {
				omissis: [
					'omissis',
					[ 'span', function( el ) {
						return el && el.attributes && el.attributes.omissis;
					} ]
				]
			},
			config = editor.config,
			lang = editor.lang.basicstyles;

		addButtonCommand( 'Omissis', 'Contenuto Omissis', 'omissis', config.coreStyles_omissis );

		editor.setKeystroke( [
			[ CKEDITOR.CTRL + 79 /*O*/, 'omissis' ]
		] );
	}
} );


CKEDITOR.config.coreStyles_omissis = {
	element: 'omissis',
	styles: { 'background-color': 'rgb(255, 214, 99)' }
};