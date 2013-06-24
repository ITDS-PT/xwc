XVW.upload = {}

/*XVW.upload.hideAttach = function( attach ){
	attach.style.display = 'none';
};*/

/*XVW.upload.restoreAttach = function(attach){
	attach.style.display = '';
};*/

/*XVW.upload.message = function(messagesContainer, message, hideTimeout){
	messagesContainer.innerHTML = message;
	var messages = messagesContainer;
	if (hideTimeout){
		var time = parseInt(hideTimeout);
		window.setTimeout(function(){ messages.innerHTML = ''; },time);
	}
};*/

/*XVW.upload.progress = function(messagesContainer , newValue, message ){
	var progresses = messagesContainer.getElementsByTagName( 'progress' );
	for (var z = 0 ; z < progresses.length ; z++){
		progresses[z].value = newValue;
	}
	var span = messagesContainer.getElementsByTagName( 'span' );
	for (var k = 0 ; k < progresses.length ; k++){
		span[k].innerHTML = message;
	}
};*/

/*XVW.upload.errorMessage = function(errorContainer, message){
	errorContainer.innerHTML = message;
};

XVW.upload.clearErrorMessages = function(errorContainer){
	errorContainer.innerHTML = "";
};


XVW.upload.changeContent = function(fileContainer, content){
	fileContainer.innerHTML = content;
};


XVW.upload.createLink = function( uploadCmp, fileContainer, linkToFile, fileName, formId, cmdId){
	
	var link = document.createElement('a');
	link.href = 'javascript:void(0)';
	link.innerHTML = fileName;
	if (window.addEventListener){
		link.addEventListener('click',function(){
			if (!uploadCmp.isDisabled()){
				XVW.downloadFile('"'+linkToFile+'"');
			}
			else 
				return false;
		});
	} else if (window.attachEvent){
		link.attachEvent('onclick',function(){
			if (!uploadCmp.isDisabled()){
				XVW.downloadFile('"'+linkToFile+'"');
			}
			else 
				return false;
		});
	}
	
	var remove = document.createElement('img');
	remove.src = "ext-xeo/icons/icon-delete.png";
	remove.className = 'remove';
	remove.width = 16;
	remove.height = 16;
	if (window.addEventListener){
		remove.addEventListener('click',function(){
			console.log(uploadCmp);
			if (!uploadCmp.isDisabled())
				XVW.AjaxCommand(formId, cmdId, fileName, 1);
			else 
				return false;
		});
	} else if (window.attachEvent){
		remove.attachEvent('onclick',function(){
			if (!uploadCmp.isDisabled())
				XVW.AjaxCommand(formId, cmdId, fileName, 1);
			else
				return false;
			
		});
	}
	
	
	var span = document.createElement('span');
	span.id = fileName;
	span.className = 'file' 
	span.appendChild(link);
	span.appendChild(remove);
	var first = fileContainer.childNodes[0];
	if (first)
		fileContainer.insertBefore(span,fileContainer.childNodes[0]);
	else
		fileContainer.appendChild(span);
};


XVW.upload.removeLink = function(fileName){
	var elem = document.getElementById(fileName);
	XVW.upload.purge(elem); //Remove attributes and events, prevent circular references
	elem.parentNode.removeChild(elem);
}

XVW.upload.purge = function(elem){
    var a = elem.attributes, i, l, n;
    if (a) {
        for (i = a.length - 1; i >= 0; i -= 1) {
            n = a[i].name;
            if (typeof elem[n] === 'function') {
                elem[n] = null;
            }
        }
    }
    a = elem.childNodes;
    if (a) {
        l = a.length;
        for (i = 0; i < l; i += 1) {
            purge(elem.childNodes[i]);
        }

    }	
}

XVW.upload.cleanMessages = function(messagesContainer){
	messagesContainer.innerHTML = "";
};*/

