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

                    var lo_plans = jQuery("<ul></ul>");
                    po.plan.forEach( function(i){ lo_plans.append( jQuery("<li></li>").text( i.trigger + Asimov.literal(i.literal) + " [Success: " + i.success + "/ Fail: " + i.fail + "]" ) ); });
                    jQuery("#agentdetail_plan").empty().append( lo_plans );

                    jQuery("#agentdetail_main")
                        .empty()
                        .append(
                            jQuery( "<ul></ul>" )
                                .append( "<li><strong>ID:</strong> " + po.id + "</li>" )
                                .append( "<li><strong>Sleeping Cycles:</strong> " + po.sleeping + "</li>" )
                                .append( "<li><strong>Cycle:</strong> " + po.cycle + "</li>" )
                        );

                    console.log(po);
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