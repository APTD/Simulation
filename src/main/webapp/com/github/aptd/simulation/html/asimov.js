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
        lo_options.method = lo_options.method || "get";
        lo_options.data   = lo_options.data   || {};
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

               + "[" + jQuery.map(
                                po_literal.annotation,
                                function(i){
                                    return i.raw
                                           ? (classof( i.raw, 'string' ) ? '"' + i.raw + '"' : i.raw)
                                           : px_modul.literal(i);
                                }
                            ).join(", ") + "]";
    };
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    return px_modul;
}(Asimov || {}));
