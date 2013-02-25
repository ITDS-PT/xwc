<#if !this.renderedOnClient>

 	<div id='${this.clientId}'>
		<div id='${this.clientId}_wrapper'>
			<div id='${this.clientId}_files'>
				
			</div>
			<div id="${this.clientId}_attatch" class='upload'>
			  <span>Click to upload</span>
			</div>
			<div id='${this.clientId}_messages'>
			</div>
			<div id='${this.clientId}_errors' class='upload-errors'></div>
		</div>
	</div>
	
	<@xvw_css>
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
		    filter: url("data :image/svg+xml;utf8,<svg xmlns=\'http://www.w3.org/2000/svg\'><filter id=\'grayscale\'><feColorMatrix type=\'matrix\' values=\'0.3333 0.3333 0.3333 0 0 0.3333 0.3333 0.3333 0 0 0.3333 0.3333 0.3333 0 0 0 0 0 1 0\'/></filter></svg>#grayscale"); /* Firefox 10+, Firefox on Android */
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
	  
		var uploader = new qq.FineUploaderXEO({
	      	button: XVW.get('${this.clientId}_attatch')
	      , maxFiles : ${this.maxFiles}
	      ,	disabled : ${this.disabled?string}
		  , visible : ${this.visible?string}
		  , readOnly : ${this.readOnly?string}
		  , fileCount : ${this.filenames?size}
		  , clientId : '${this.clientId}'
		  , formId : '${this.formId}'
		  , startMessage : '${this.startMessage}'
		  , savingMessage : '${this.savingMessage}'
	  	  , uploadFailed : '${this.uploadFailed}'
	  	  , sendingMessage : '${this.sendingMessage}'
	  	  , progressMessage : '${this.progressMessage}'
		  , request: {
	        endpoint: '${this.servletUrl}'
		  	}
		  , classes : {
			  buttonHover : 'x-form-trigger-hover'
		  }
		  , messages: {
            typeError: "${this.typeError}",
            sizeError: "${this.sizeError}",
            minSizeError: "${this.minSizeError}",
            emptyError: "${this.emptyError}",
            noFilesError: "${this.noFilesError}",
            onLeave: "${this.onLeaveError}"
          }
		  , validation: {
		    allowedExtensions: ${this.validExtensionsSerialized}
		    , sizeLimit: ${this.maxFileSize}
		    <#if this.minFileSize??>
		    	, minSizeLimit : ${this.minFileSize} 
		    </#if>
		  }
		  
	  })
	
	 XVW.uploadManager.registerUpload('${this.clientId}', uploader);
	 
	 <#-- Create Existing files -->
	 <#list this.filenames as file>
		uploader.createLink(uploader,  uploader.getFilesElem() ,${this.servletUrl}+'&download=download&fileName='+encodeURIComponent('${file}'),'${file}','${this.formId}',${this.clientId} + '_rmCmd');
	 </#list>
	 <#if !this.visible>
		XVW.uploadManager.get('${this.clientId}').hide();
	 </#if>
	 <#if this.disabled>
		XVW.uploadManager.get('${this.clientId}').disable();
	  </#if>
		<#if this.readOnly>
			XVW.uploadManager.get('${this.clientId}').readOnly();
		</#if>	
	 });
	 
	 
	</@xvw_script>

<#else>
	<@xvw_script position='FOOTER'>
		<#if this.visibleChanged>
			<#if this.visible>
				XVW.uploadManager.get('${this.clientId}').show();
			<#else>
				XVW.uploadManager.get('${this.clientId}').hide();
			</#if>
		</#if>
		<#if this.readOnlyChanged>
			<#if this.readOnly>
				XVW.uploadManager.get('${this.clientId}').readOnly();
			<#else>
				XVW.uploadManager.get('${this.clientId}').fullControl();
			</#if>	
		</#if>
		<#if this.disabledChanged>
			<#if this.disabled>
				XVW.uploadManager.get('${this.clientId}').disable();
			<#else>
				XVW.uploadManager.get('${this.clientId}').enable();
			</#if>
		</#if>	
	</@xvw_script>
</#if>