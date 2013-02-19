<#if !this.renderedOnClient>

	<div id='${this.clientId}'>
		<div id='${this.clientId}_wrapper'>
			<div id='${this.clientId}_files' style=''>
				<#list this.filenames as file>
					<a href='${this.servletUrl}&download=download&fileName=${file}'>${file}</a>
					<img src='ext-xeo/icons/icon-delete.png' class='remove' width='16' height='16' 
					onclick="XVW.AjaxCommand('${this.formId}','${this.clientId}_rmCmd','${file}',1)"/>
				</#list>
			</div>
			<div id="${this.clientId}_attatch" class='upload'>
			  <span>Click to upload</span>
			</div>
			<div id="messages"></div>
			<div id='${this.clientId}_messages'>
			</div>
			<div id='${this.clientId}_errors' class='upload-errors'></div>
		</div>
	</div>
	
	<@xvw_script position='HEADER' src='js/fileupload/uploadUtils.js' id='uploadUtils'>
	</@xvw_script>
	
	<@xvw_css>
		span.file{
			
		}
		
		span.file a{
			margin-right : 3px;
		}
		
		img.remove{
			vertical-align:middle;
		}
		
		img.remove:hover{
			cursor:pointer;
		}
		
		div.disabled img {
		    filter: url("data:image/svg+xml;utf8,<svg xmlns=\'http://www.w3.org/2000/svg\'><filter id=\'grayscale\'><feColorMatrix type=\'matrix\' values=\'0.3333 0.3333 0.3333 0 0 0.3333 0.3333 0.3333 0 0 0.3333 0.3333 0.3333 0 0 0 0 0 1 0\'/></filter></svg>#grayscale"); /* Firefox 10+, Firefox on Android */
		    filter: gray; /* IE6-9 */
		    -webkit-filter: grayscale(100%); /* Chrome 19+, Safari 6+, Safari 6+ iOS */
		}
		div.disabled a {
		    cursor : text !important;
		}
		div.disabled {
		    text-decoration:none !important;
			cursor:text !important;
			color:gray !important;
		}
		div.disabled a {
		    text-decoration:none !important;
			cursor:text !important;
			color:gray !important;
		}
		
		div.upload-errors{
			color : red;
			font-weight:bold;
		}
		
		div.upload{
			text-decoration:underline;
			cursor:pointer;
			color:blue;
		}
		div.upload-disabled{
			text-decoration:none;
			cursor:text;
			color:gray;
		}
		
		div.upload:hover{
			text-decoration:none;
		}
		
	</@xvw_css>
	
	<@xvw_script position='FOOTER'>
	
	$(document).ready(function() {
	  
	    var files = XVW.get('${this.clientId}_files');
		var button = XVW.get('${this.clientId}_attatch');
		var messages = XVW.get('${this.clientId}_messages');
		var errors = XVW.get('${this.clientId}_errors');
	 	
	  	var uploader = new qq.FineUploaderBasic({
	      button: button
	      , maxFiles : ${this.maxFiles}
	      , request: {
	        endpoint: '${this.servletUrl}'
		  }
		  , classes : {
			  buttonHover : 'x-form-trigger-hover'
		  }
		  , messages: {
            typeError: "{file} has an invalid extension. Valid extension(s): {extensions}.",
            sizeError: "{file} is too large, maximum file size is {sizeLimit}.",
            minSizeError: "{file} is too small, minimum file size is {minSizeLimit}.",
            emptyError: "{file} is empty, please select files again without it.",
            noFilesError: "No files to upload.",
            onLeave: "The files are being uploaded, if you leave now the upload will be cancelled."
          }
		  , validation: {
		    allowedExtensions: ${this.validExtensionsSerialized}
		    , sizeLimit: ${this.maxFileSize}
		    <#if this.maxFileSize??>
		    	, minSizeLimit : ${this.minFileSize} 
		    </#if>
		  },
		  callbacks: {
		    onSubmit: function(id, fileName) {
		    	XVW.upload.hideAttach(button);
		    	XVW.upload.message(messages,'Iniciar');
		    },
		    onUpload: function(id, fileName) {
		    	if (window.FileReader)
		      		XVW.upload.message(messages,'<progress value="0" max="100" style="display:inline"></progress><span></span>');
		      	else		
		      		XVW.upload.message(messages,'<img src="jquery-xeo/images/loading5.gif" /> Enviando');
		    },
		    onProgress: function(id, fileName, loaded, total) {
		      if (loaded < total) {
		      	progress = Math.round(loaded / total * 100);
		        message = progress + '% de ' + Math.round(total / 1024 / 1024) + ' MB';
		        XVW.upload.progress(messages, progress , message);
		      } else {
				XVW.upload.message(messages,'Saving');
		      }
		    },
		    onComplete: function(id, fileName, responseJSON) {
		      XVW.upload.restoreAttach(button);
		      if (responseJSON.success) {
		        XVW.upload.message(messages,'');
		        XVW.upload.createLink( files ,'${this.servletUrl}&download=download&fileName='+fileName,fileName,'${this.formId}','${this.clientId}_rmCmd');
		        XVW.upload.clearErrorMessages(errors);	
		      } else {
		      	XVW.upload.errorMessage(errors,"Upload failed");
		      }
		    }
		    , onValidate : function (fileData ) {
		    	console.log(fileData);
		    }
		    , onError : function ( id,  fileName,  errorReason){
		    	XVW.upload.restoreAttach(button);
		    	XVW.upload.errorMessage(errors,errorReason);
		    } 
		  }
		  
	  })
	
	 });
	 
	 
	</@xvw_script>

<#else>
	<@xvw_script position='FOOTER'>
		<#if this.visibleChanged>
			<#if this.visible>
				XVW.get('${this.clientId}_wrapper').style.display = '';
			<#else>
				XVW.get('${this.clientId}_wrapper').style.display = 'none';
			</#if>
		</#if>
		<#if this.disabledChanged>
			<#if this.disabled>
				XVW.get('${this.clientId}_wrapper').className = 'disabled';
				XVW.get('${this.clientId}_attatch').className = 'upload-disabled';
				XVW.get('${this.clientId}_attatch').style.display = 'none';
				var input = XVW.get('${this.clientId}_wrapper').getElementsByTagName( 'input' ); 
			    for ( var z = 0; z < input.length; z++ ) { 
			        input[z].disabled = 'disabled'; 
			    } 
			<#else>
				XVW.get('${this.clientId}_wrapper').className = '';
				XVW.get('${this.clientId}_attatch').className = 'upload';
				XVW.get('${this.clientId}_attatch').style.display = '';
				var input = XVW.get('${this.clientId}_wrapper').getElementsByTagName( 'input' ); 
			    for ( var z = 0; z < input.length; z++ ) { 
			        input[z].disabled = false;
		        } 
			</#if>
		</#if>	
	</@xvw_script>
</#if>