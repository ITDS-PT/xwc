/*
        		
Checks the number of options selected and activates the buttons accordingly
One check box selected activates the button to compare with the current version
Two checkboxes selected activates the button to compare the two versions

Zero checkboxes or more than tow checkboxes selected disables the buttons

@param field The radio list to verify

*/
XVW.checkButtons = function(field){
	var counter = 0;
	var buttonActual = document.getElementById('compareCurrent');
	for (i = 0; i < field.length; i++){
		if (field[i].checked)
			counter++;
	}
	
	if (counter == 1)
		buttonActual.disabled = false;
	else
	{
		if (field.checked)
			buttonActual.disabled = false;
		
	}
}
				
XVW.openDiffWindow = function( field ){
	
	XVW.Wait(1);
	var counter = 0;
	var version = 0;
	for (i = 0; i < field.length; i++)
	{
		if (field[i].checked)
		{
			version = field[i].value;
		}
	}
	
	if (version == 0){
		if (field.checked)
			version = field.value;
	}
	
	XVW.AjaxCommand( 'listVersionForm','listVersionForm:showDiff',version,2);
	XVW.NoWait();
}
				
/*

Opens a window with the specified URL

@param url The URL of the data

*/

XVW.openLogWindow  = function (bouiVersion)
{
	if ( bouiVersion > 0 )
		XVW.AjaxCommand( 'listVersionForm' , 'listVersionForm:showLogs' , bouiVersion , 2 );
}
