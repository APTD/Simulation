"use strict";

jQuery(function() {

    // initialize UI
    jQuery(".tabs" ).tabs();
    jQuery(".selectable").selectable({
        autoRefresh: true,
        selected: function(po_event, po_ui) {
            jQuery(po_ui.selected).addClass("ui-selected").siblings().removeClass("ui-selected");
        },
        stop: function() {
            jQuery( ".ui-selected", this ).each(function() {

                Asimov.ajax( "/agent/" + jQuery( this ).text() + "/view" )
                .done( function(po) {
                    var lo_beliefs = jQuery("<ul></ul>");
                    po.belief.forEach( function(i){ lo_beliefs.append( jQuery("<li></li>").text( Asimov.literal(i) ) ); });
                    jQuery("#agentdetail_belief").empty().append( lo_beliefs );
                });

            });
        }
    });

    // read agent list
    Asimov.ajax( "/agent/list" )
    .done( function(po) {
        po.forEach( function(pc) {

            jQuery("#agentlist").append(
                jQuery("<li></li>")
                .addClass("ui-widget-content")
                .text(pc)
            );
        } );
    });

});