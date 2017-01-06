"use strict";


/**
 * prototype overload - add startwidth to string
 * @param pc_prefix prefix
 * @return boolean existance
 **/
String.prototype.startsWith = String.prototype.startsWith || function( pc_prefix ) {
        return this.indexOf(pc_prefix) === 0;
};


/**
 * prototype overload - add endwidth to string
 * @param pc_suffix
 * @return boolean existance
 **/
String.prototype.endsWith = String.prototype.endsWith || function( pc_suffix ) {
    return this.match( pc_suffix+"$" ) === pc_suffix;
};


/**
 * clear null bytes from the string
 * @return cleared null bytes
 **/
String.prototype.clearnull = String.prototype.clearnull || function() {
    return this.replace(/\0/g, "");
};


/**
 * parse the string to a JSON object
 **/
String.prototype.toJSON = String.prototype.toJSON || function() {
    return jQuery.parseJSON(this.clearnull());
};


/**
 * Fowler–Noll–Vo hash function
 * @see https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function#FNV-1a_hash
 * @see http://stackoverflow.com/questions/7616461/generate-a-hash-from-string-in-javascript-jquery
 *
 * @return hash value
 **/
String.prototype.hash = String.prototype.hash || function() {
    var hval = 0x811c9dc5;

    // Strips unicode bits, only the lower 8 bits of the values are used
    for (var i = 0; i < this.length; i++) {
        hval = hval ^ (this.charCodeAt(i) & 0xFF);
        hval += (hval << 1) + (hval << 4) + (hval << 7) + (hval << 8) + (hval << 24);
    }

    var val = hval >>> 0;
    return ("0000000" + (val >>> 0).toString(16)).substr(-8);
};


/**
 * split a string into lines and remove empty lines
 * @param pc_separator separator
 * @return array of lines
 **/
String.prototype.splitLines = String.prototype.splitLines || function( pc_separator ) {
    var la_out = [];
    var lc_separator = "\n";
    if (pc_separator)
        lc_separator = pc_separator;

    jQuery.each(this.split(lc_separator), function (pn, pc) {
        if (pc)
            la_out.push(pc);
    });

    return la_out;
};


/**
 * replaces all linebreaks to HTML br tag
 * @return modified string
 **/
String.prototype.nl2br = String.prototype.nl2br || function() {
    return this.replace(/(\r\n|\n\r|\r|\n)/g, "<br/>");
};


/**
 * removes an element of an array
 * @param px_value value
 **/
Array.prototype.remove = Array.prototype.remove || function( px_value ) {
    delete this[ Array.prototype.indexOf.call(this, px_value) ];
};


/**
 * elementwise convert to build a new array
 * @param px_value modifier closure
 * @returns a new array
 **/
Array.prototype.convert = Array.prototype.convert || function( px_value ) {
    var la_result = [];
    this.forEach( function( px_item ) { la_result.push( px_value(px_item) ); } );
    return la_result;
};


/**
 * uniquefy the array
 **/
Array.prototype.unique = Array.prototype.unique || function( px_value ) {
    return function(){ return this.filter( px_value) }
}(function( a, b, c ) { return c.indexOf(a,b+1) < 0 });


/**
 * global function to get the object-type of a variable
 *
 * @param px_value value type
 * @param pc_type class type
 * @return class type
 **/
function classof( px_value, pc_type ) {
    return ({}).toString.call( px_value ).match(/\s([a-z|A-Z]+)/)[1].trim().toLowerCase() === pc_type.trim().toLowerCase();
}


/**
 * @Overload
 * add an empty trigger to the empty function
 **/
jQuery.fn.raw_empty = jQuery.fn.empty;
jQuery.fn.empty    = function(){ return this.raw_empty().trigger( "empty", this ) }
