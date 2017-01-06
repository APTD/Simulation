"use strict";

jQuery(function() {

    // read agent list
    jQuery.ajax({ "url" : "/rest/agent/list" })
    .done( function(po) {
        po.forEach( function(pc) {
            jQuery("#agentlist").append(
                jQuery("<li></li>")
                .addClass("ui-widget-content")
                .append(
                    jQuery("<a></a>").addClass("ui-all").text(pc)
                )
            );
            } );

            jQuery("#agentlist").selectable();
    });

});