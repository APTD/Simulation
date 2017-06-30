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


/**
 * base modul to represent base algorithms
 * and structure to encapsulate structures
 **/
var Asimov = (function (px_modul) {

    // ---- ajax request ---------------------------------------------------------------------------------------------------------------------------------------
    /**
     * redefined jQuery ajax request, with equal option fields
     * @see http://api.jquery.com/jquery.ajax/

     * @param px_options Ajax request object or URL
     * @return jQuery Ajax object
     **/
    px_modul.ajax = function( px_options )
    {
        // in strict mode a deep-copy is needed / string defines the URL
        // data is set explicit on string, because Java browser did not call it without on OSX
        var lo_options    = classof(px_options, "string") ? { "url" : px_options } : jQuery.extend( true, {}, px_options );
        lo_options.method = lo_options.method || "GET";
        lo_options.data   = lo_options.data   || {};
        lo_options.cache  = lo_options.cache  || false;
        lo_options.url    = lo_options.url.startsWith("http://") ? lo_options.url : lo_options.url = "/rest" + lo_options.url;

        return jQuery.ajax( lo_options );
    };
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------



    // --- literal object --------------------------------------------------------------------------------------------------------------------------------------
    /**
     * converts a object into the literal structure
     * @param po_literal
     */
    px_modul.literal = function( po_literal )
    {
        return (po_literal.parallel ? "@" : "")
               + (po_literal.negated ? "~" : "")
               + po_literal.functor
               + "(" + jQuery.map(
                                po_literal.value,
                                function(i){
                                    return i.raw
                                           ? (classof( i.raw, 'string' ) ? '"' + i.raw + '"' : i.raw)
                                           : px_modul.literal(i);
                                }
                       ).join(", ") + ")"
    };
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    return px_modul;
}(Asimov || {}));
