var retval = "ready";

function checkHiddenSingle(el, text) {
	if(el === undefined) {
		return;
	}
    if(el.offsetParent !== null) {
    	retval = "not ready "+el.offsetParent +" "+el.outerHTML;
    }
    //if(el.parent !== null)
    //	checkHiddenSingle(el.parent, text);
}

function checkHidden(arr, text) {
	//retval = "arrnull";
	if(arr==null || arr.length==0) {
		return;
	}
	//retval = "arrnotnull "+arr.outerHTML;
	//for (var i = 0, len = arr.length; i < len; i++) {
	  checkHiddenSingle(arr, text);
	//}
}
if (typeof Ajax !== 'undefined' &&  Ajax.activeRequestCount != 0)
	return "active request count = " + (Ajax.activeRequestCount);


if(document.readyState !== 'complete')
	return document.readyState;
var img = document.querySelector(".caf-progress-bar");
if(img != null) {
	checkHidden(img,".caf-progress-bar");
}

img = document.querySelector("div[id$='loadingPanelApplet'] img");
if(img != null) {
	checkHidden(img,".caf-progress-bar");
}


img = document.querySelector(".caf-progress");
if(img != null) {
	checkHidden(img,"caf-progress");
}

return retval;
