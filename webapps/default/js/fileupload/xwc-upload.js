/**
 * Object that acts as register for upload components
 * */
XVW.uploadManager = {
	components : []
	, registerUpload : function (componentId, component){
		this.components[componentId] = component;	
	}
	, deRegisterUpload : function (componentId){
		delete this.components[componentId];	
	}
	, get : function (componentId){
		if (this.components[componentId])
			return this.components[componentId];
		return null;
	}
}



/**
 * Class that creates upload widget with drag-and-drop and file list
 * @inherits qq.FineUploaderBasic
 */
qq.FineUploaderXEO = function(o){
    // call parent constructor
    qq.FineUploaderBasic.apply(this, arguments);
    
    // additional options
    qq.extend(this._options, {
    	disabled : false
	  , visible : true
	  , readOnly : false
	  , fileCount : 0
	  , clientId : null
	  , formId : null
	  , maxFiles : 0
	  , startMessage : "Starting"
	  , savingMessage : "Saving {file}"
	  , uploadFailed : "Upload Failed"
	  , sendingMessage : "Sending"
	  , progressMessage : "{progress}% of {total} MB"	  
    }, true);

    // overwrite options with user supplied
    qq.extend(this._options, o, true);
    this._wrapCallbacks();
    
};

// inherit from Basic Uploader
qq.extend(qq.FineUploaderXEO.prototype, qq.FineUploaderBasic.prototype);

qq.extend(qq.FineUploaderXEO.prototype, {
	
    clearStoredFiles: function() {
        qq.FineUploaderBasic.prototype.clearStoredFiles.apply(this, arguments);
    }

	, _onSubmit : function (id , fileName){
		qq.FineUploaderBasic.prototype._onSubmit.apply(this, arguments);
		this.hideAttach(this.getAttatchElem());
		this.createProgressContainer(id);
		this.messageOnProgress(id,this._options.startMessage);
	} 
	
	, _onUpload: function(id, fileName) {
		qq.FineUploaderBasic.prototype._onUpload.apply(this, arguments);
		var messages = this.getMessagesElem();
		if (window.FileReader){
			//this.message(this.getMessagesElem(),'');
			this.createProgressBar(id,'<progress value="0" max="100" style="display:inline"></progress><span>'+
      		this._options.sendingMessage + '</span>');
		}
      	else		
      		this.message(messages,'<img src="jquery-xeo/images/loading5.gif" /> ' + this._options.sendingMessage );
	}
	
	, _onProgress: function(id, fileName, loaded, total) {
		qq.FineUploaderBasic.prototype._onProgress.apply(this, arguments);
		console.log("Progress " + id + " " + fileName);
		var messages = XVW.get('A'+id);
	      if (loaded < total) {
	      	progress = Math.round(loaded / total * 100);
	      	total = Math.round(total / 1024 / 1024);
	      	message = this._options.progressMessage.replace('{progress}',progress);
	      	message = message.replace('{total}',total);
	        this.progress(id, progress , message);
	      } else {
	    	var saving = this._options.savingMessage;
	    	saving = saving.replace('{file}',fileName);
			this.messageOnProgress(id,saving);
	      }
    }
	 
	, _onComplete: function(id, fileName, responseJSON) {
		qq.FineUploaderBasic.prototype._onComplete.apply(this, arguments);
		console.log("Complete " +  id + " " + fileName);
		 var button = this.getAttatchElem();
		 var messages = this.getMessagesElem();
		 var errors = this.getErrorsElem();
		 var files = this.getFilesElem();
	     this.restoreAttach(button);
	      if (responseJSON.success) {
	        this.removeProgressContainer(id);
	        this.createLink(this,  files ,this._options.request.endpoint+'&download=download&fileName='+encodeURIComponent(fileName),fileName,this._options.formId,this._options.clientId + '_rmCmd');
	        this.clearMessages();
	        this.clearErrorMessages(errors);
	        this.addFile();
	        /*if (this.getFileCount() >= this._options.maxFiles){
	        	this.hideAttach(this.getAttatchElem());
	        }*/
	      } else {
	      	this.errorMessage(errors,this._options.uploadFailed);
	      }
	}
	
	, _onError : function ( id,  fileName,  errorReason){
		qq.FineUploaderBasic.prototype._onError.apply(this, arguments);
		var buttons = this.getAttatchElem();
		var errors = this.getErrorsElem();
    	this.restoreAttach(button);
    	this.errorMessage(errors,errorReason);
    }

	, isDisabled : function() {return this._options.disabled }	
	, disable : function (){
		this._options.disabled = true;
		this.getWrapperElem().className = 'disabled';
		this.getAttatchElem().className = 'upload-disabled';
		this.getAttatchElem().style.display = 'none';
		this.getInput().disabled = 'disabled';
	}
	, enable : function () {
		this._options.disabled = false;
		this.getWrapperElem().className = '';
		this.getAttatchElem().className = 'upload';
		this.getAttatchElem().style.display = '';
		this.getInput().disabled = false;
	}
    
    , isVisible : function(){
    	return this._options.visible 
    }
    , show : function(){
    	this.getWrapperElem().style.display = '';
    }
    , hide : function(){
    	this.getWrapperElem().style.display = 'none';
    }
    , readOnly : function(){
    	this._options.readOnly = true;
    	this.getWrapperElem().className = 'disabled';
		this.getAttatchElem().className = 'upload-disabled';
		this.getAttatchElem().style.display = 'none';
		this.getInput().disabled = 'disabled';
    }
    , fullControl : function(){
    	this._options.readOnly = false;
    	this.getWrapperElem().className = '';
		this.getAttatchElem().className = 'upload';
		this.getAttatchElem().style.display = '';
		this.getInput().disabled = false;
    }
    , isReadOnly : function () { 
    	return this._options.readOnly; 
    }
    , addFile : function (){ 
    	this._options.fileCount = (this._options.fileCount + 1)
    }
    , getFileCount : function() {
    	return this._options.fileCount;
    }
    , removeFile : function (file){
    	if (this.getFileCount() > 0 ){
    		this._options.fileCount = (this._options.fileCount + 1);
    	}
    } 
	, getClientId : function (){
		return this._options.clientId;
	}
	, getMessagesElem : function (){
		return XVW.get(this._options.clientId + "_messages");
	}
	, getWrapperElem : function (){
		return XVW.get(this._options.clientId + "_wrapper");
	}
	, getFilesElem : function (){
		return XVW.get(this._options.clientId + "_files");
	}
	, getAttatchElem : function (){
		return XVW.get(this._options.clientId + "_attatch");
	}
	, getErrorsElem : function (){
		return XVW.get(this._options.clientId + "_errors");
	}
	, getInput : function (){
		var wrapper = this.getWrapperElem();
		var inputs = wrapper.getElementsByTagName( 'input' );
		return inputs[0];
	}
	, hideAttach : function( attach ){
	 	attach.style.display = 'none';
	}
	
	, restoreAttach : function(attach){
		attach.style.display = '';
	}
	
	, message : function(messagesContainer, message, hideTimeout){
		messagesContainer.innerHTML = message;
		var messages = messagesContainer;
		if (hideTimeout){
			var time = parseInt(hideTimeout);
			window.setTimeout(function(){ messages.innerHTML = ''; },time);
		}
	}
	, progress : function(id , newValue, message ){
		var messagesContainer = XVW.get('A' + id);
		var progresses = messagesContainer.getElementsByTagName( 'progress' );
		for (var z = 0 ; z < progresses.length ; z++){
			progresses[z].value = newValue;
		}
		var span = messagesContainer.getElementsByTagName( 'span' );
		for (var k = 0 ; k < progresses.length ; k++){
			span[k].innerHTML = message;
		}
	}
	
	, errorMessage : function(errorContainer, message){
		errorContainer.innerHTML = message;
	}

	, clearErrorMessages : function(errorContainer){
		errorContainer.innerHTML = "";
	}
	
	, changeContent : function(fileContainer, content){
		fileContainer.innerHTML = content;
	}
	
	, createLink : function( uploadCmp, fileContainer, linkToFile, fileName, formId, cmdId){
		
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
				if (!uploadCmp.isDisabled()){
					XVW.AjaxCommand(formId, cmdId, fileName, 1);
				}
				else 
					return false;
			});
		} else if (window.attachEvent){
			remove.attachEvent('onclick',function(){
				if (!uploadCmp.isDisabled()){
					XVW.AjaxCommand(formId, cmdId, fileName, 1);
				}
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
	}
	
	, removeLink : function(fileName){
		var elem = document.getElementById(fileName);
		this.purge(elem); //Remove attributes and events, prevent circular references
		elem.parentNode.removeChild(elem);
	}
	
	, purge : function(elem){
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
	, clearMessages : function(){
		var messagesContainer = this.getMessagesElem();
		messagesContainer.innerHTML = "";
	}
	
	, createProgressContainer : function (id){
		var messages = this.getMessagesElem();
		var elem = document.createElement('div');
		elem.id = 'A' + id;
		messages.appendChild(elem);
	}
	
	, createProgressBar : function (id, progressHtml){
		var elem = XVW.get('A'+id);
		elem.innerHTML = progressHtml;
	}
	
	, removeProgressContainer : function (id){
		var elem = XVW.get('A'+id);
		//elem.parentNode.removeChild;
	}
	, messageOnProgress : function (id, message){
		var elem = XVW.get('A'+id);
		elem.innerHTML = message;
	}

});