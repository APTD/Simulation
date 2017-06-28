/*
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the Asimov - Agentbased Passenger Train Delay                 #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 */
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

    // OpenLayers
    // https://openlayers.org/en/latest/doc/quickstart.html
    // http://codepen.io/payamcf/pen/mEKpQj
    // http://wiki.openstreetmap.org/wiki/DE:OpenRailwayMap/API#Einbindung_in_OpenLayers_3
    new ol.Map({
        target: 'map',
        layers: [
            new ol.layer.Tile({
                source: new ol.source.OSM()
            })
        ],
        view: new ol.View({
            center: ol.proj.fromLonLat([9.92403, 51.53689]),
            zoom: 16
        })
    }).addLayer(
          new ol.layer.Tile({
              title: 'OpenRailwayMap',
              visible: true,
              source : new ol.source.XYZ({
                  url : 'http://{a-c}.tiles.openrailwaymap.org/standard/{z}/{x}/{y}.png',
                  crossOrigin: null,
                  tilePixelRatio: 2,
                  maxZoom: 19,
                  opaque: true
              })
          })
    );

});