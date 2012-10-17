/* 
 * From
 * Darren Ingram 
 * http://www.darreningram.net/pages/examples/jQuery/CollapsiblePanelPlugin.aspx */
(function($) {
    $.fn.extend({
        collapsiblePanel: function(collapsible) {
            // Call the ConfigureCollapsiblePanel function for the selected element
            return $(this).each( function () {
            	
            	$(this).addClass("ui-widget");

                // Wrap the contents of the container within a new div.
                $(this).children().wrapAll("<div class='xwc-panel-content ui-widget-content'></div>");

                // Create a new div as the first item within the container.  Put the title of the panel in here.
                $("<div class='xwc-panel-title ui-widget-header'><div>" + $(this).attr("title") + "</div></div>").prependTo($(this));

                // Assign a call to CollapsibleContainerTitleOnClick for the click event of the new title div.
                if (collapsible)
                	$(".xwc-panel-title", this).click(CollapsibleContainerTitleOnClick);
            } 
            		
            );
        }
        
    });

})(jQuery);

function ConfigureCollapsiblePanel(collapsible) {
    $(this).addClass("ui-widget");

    // Wrap the contents of the container within a new div.
    $(this).children().wrapAll("<div class='xwc-panel-content ui-widget-content'></div>");

    // Create a new div as the first item within the container.  Put the title of the panel in here.
    $("<div class='xwc-panel-title ui-widget-header'><div>" + $(this).attr("title") + "</div></div>").prependTo($(this));

    // Assign a call to CollapsibleContainerTitleOnClick for the click event of the new title div.
    if (collapsible)
    	$(".xwc-panel-title", this).click(CollapsibleContainerTitleOnClick);
}

function CollapsibleContainerTitleOnClick() {
    // The item clicked is the title div... get this parent (the overall container) and toggle the content within it.
    $(".xwc-panel-content", $(this).parent()).slideToggle();
}