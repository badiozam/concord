/*
function AjaxURL(panels)
{
	this.panels = panels;
}

function setPageURL( subDiv, link, confirmMessage )
{
	var pageURLs = {};
	pageURLs[subDiv] = link;

	if ( confirmMessage )
	{
		if ( confirm(confirmMessage) )
		{
			setPageURLs(pageURLs);
		}
	}
	else
	{
		setPageURLs(pageURLs);
	}
}

// Expects an associative array of id to URL
//
function setPageURLs( pageURLs )
{
	var currentPanels = {};
	$("div[loaded]").each(function(i, elem)
	{
		var key = $(elem).attr("id");
		var value = $(elem).attr("loaded");

		//console.log(i + ". Added " + key + " = " + value);
		currentPanels[key] = value;

	});

	// Override the current panel with the one passed in
	//
	if ( pageURLs )
	{
		for ( id in pageURLs )
		{
			//console.log("Overriding " + id + " with " + pageURLs[id]);
			currentPanels[id] = pageURLs[id];
		}
	}
	else
	{
		console.log("PageURLs is " + pageURLs);
	}

	try
	{
		var jsonPanels = JSON.stringify(currentPanels);
		console.log("Panels: " + jsonPanels);

		var encoded = base64Encode(jsonPanels);
		$.address.value(encoded);
	}
	catch (e)
	{
		console.log(e);
	}
}

function getPageURL(pageURLStr)
{
	var emptyPanels = {};
	try
	{
		if ( !pageURLStr )
		{
			pageURLStr = $.address.value();
		}

		// By now we likely have a leading '/' due to the $.address plugins
		// way of setting state
		//
		pageURLStr = pageURLStr.replace('/', '');

		if ( pageURLStr === '' )
		{
			return emptyPanels;
		}
		else
		{
			var retVal = JSON.parse( base64Decode(pageURLStr) );
			//console.log("Returning " + retVal);

			return retVal;
		}
	}
	catch (e)
	{
		console.log(e);

		return emptyPanels;
	}
}

function initForms()
{
	if ( usingAjax )
	{
		var options =
		{
			success: showResponse
		};

		// Register all form submissions for jQuery processing
		//
		$("form[target!=_top]").ajaxForm(options);
	}
}

function showResponse(responseText, statusText, xhr, $form)
{
	var subDiv = "page";
	if ( $form.attr("result") )
	{
		subDiv = $form.attr("result");
	}

	var reloadAll = Boolean($form.attr("reloadall"));
	var formAction = $form.attr("action");

	var responseObj = divSubText(subDiv, responseText, function()
	{
		$("#" + subDiv).attr("loaded", formAction + "#" + new Date().getTime());
		if ( reloadAll )
		{
			reloadAllDivs("#" + subDiv);
		}
		else
		{
			$.triggerReady();
		}
	});

	// Errors don't need JavaScript in them so we skip the triggerReady
	// 
	var errDiv = "jq_errors";
	if ( $form.attr("errors") )
	{
		errDiv = $form.attr("errors");
	}

	divSubXML(errDiv, responseObj);
}
*/

function divSubText(divName, responseText, callback)
{
	return divElemSubText($("#" + divName), responseText, callback);
}

function divElemSubText(div, responseText, callback)
{
	var responseTextXML = "<xml>" + responseText + "</xml>";
	var responseObj = $(responseTextXML);

	var divName = $(div).attr("id");
	$(div).html(responseObj.find("#" + divName).html());

	// jQuery workaround: http://stackoverflow.com/questions/2699320/jquery-script-tags-in-the-html-are-parsed-out-by-jquery-and-not-executed
	//
	responseObj.filter('script').each(function(){ $.globalEval(this.text || this.textContent || this.innerHTML || ''); });
	if ( callback ) {  callback(div); }

	return responseObj;
}

function divSubXML(divName, responseXML, callback)
{
	responseXML.find("#" + divName).each(function()
	{
		$("#" + divName).html($(this).html());
		if ( callback ) { callback(); }
	});

	return responseXML;
}

/*
function reloadAllDivs(except)
{
	$("div[loaded]").not(except).each(function(i, elem)
	{
		var link = $(elem).attr("loaded");

		// $.triggerReady must be called after each link is loaded since the function returns immediately
		// and different panels may load at different rates and we don't want any of them uninitialized
		//
		loadLink(link, $(elem), true, $.triggerReady);

	});
}

function initHrefs(anchorSelector, divName, noCache)
{
		// For items that use href, the browser will take care of loading
		//
		$(anchorSelector).off('click.initHrefs');
		$(anchorSelector).on('click.initHrefs', function(event)
		{
			var link = $(this).attr('link');
			var confirmMessage = $(this).attr('confirm');

			// How fast to scroll the page up (defaults to -1 for no scrolling)
			var scrollUp = $(this).attr('scrollup');
			if ( !scrollUp )
			{
				scrollUp = -1;
			}

			// See if the anchor specifies a result div
			//
			var subDiv = $(this).attr("result");
			if ( !subDiv )
			{
				subDiv = divName;
			}

			// Whether to reload all registered divs since the state of
			// the system has changed
			//
			var reloadAll = Boolean($(this).attr("reloadAll"));
			var doneLoading = reloadAll ? reloadAllDivs : $.triggerReady;

			// Only do this if we're using ajax
			//
			if ( link && usingAjax )
			{
				// See if we should scroll the page up or not
				//
				if ( scrollUp < 0 )
				{
					setPageURL(subDiv, link, confirmMessage);
				}
				else
				{
					$('html, body').animate({scrollTop: $("#page").offset().top}, scrollUp, function()
					{
						setPageURL(subDiv, link, confirmMessage);
					});
				}

				return false;
			}
			else
			{
				return true;
			}
		});
}

// Load a link into a particular div and set the loaded attribute
//
function loadLink(link, subDivElem, noCache, doneLoading)
{
	console.log("noCache = " + noCache + ", loaded = " + subDivElem.attr("loaded") + " vs " + link);

	// The noCache variable will force loading regardless
	//
	if ( noCache || subDivElem.attr("loaded") != link )
	{
		// We say that the element was loaded before the content is actually posted in order
		// to avoid multiple calls requesting the same URL over and over again.
		//
		subDivElem.attr("loaded", link);
		$.get(link, function(responseText)
		{
			divElemSubText(subDivElem, responseText, doneLoading);
		});
	}
	else
	{
		// Do nothing since this is the currently loaded item
		//
	}
}
// Overrides the default hrefs with a new div
//
function initMenus(divName, noCache)
{
	initHrefs("#menu a", divName, noCache);
}

// This needs only be called only when the page is loaded the first time
// subsequent panel loads need not trigger this
//
$.address.change(function(event)
{
	var panels = getPageURL(event.value);

	if ( panels && !jQuery.isEmptyObject(panels) )
	{
		for ( key in panels )
		{
			//console.log("Address change loading " + key + " = " + panels[key]);
			loadLink(panels[key], $("#" + key), false, $.triggerReady);
		}
	}
	else
	{
		//console.log("URL object is not defined or is empty for " + event.value + " with address value " + $.address.value());
	}

	// Don't load items that don't exist
	//
	if ( event.value !== "/" )
	{
		loadLink(event.value, $("#page"), false, $.triggerReady);
	}
});
*/

$(function()
{
	/*
	initForms();

	initHrefs("a", "page", false);
	initHrefs("input[type=button]", "page", false);
	*/

	// Avoid iPhone 300ms delay since double-tap is not needed anywhere on the site
	//
	FastClick.attach(document.body);

	// This is a workaround for Firefox that disables buttons
	// once we click the back button
	//
	$('input[type=submit],button[type=submit]', this).attr('disabled', false);

	// Depends on the jQuery placeholder plugin
	//
	if ( typeof placeholder !== 'undefined' )
	{
		$('input[placeholder], textarea[placeholder]').placeholder();
	}

	// Depends on the bootstrap-maxlength  plugin
	//
	if ( typeof maxlength !== 'undefined' )
	{
		$('input[maxlength], textarea[maxlength]').maxlength(
		{
			'alwaysShow': 'true',
			'warningClass': 'label label-success',
			'placement': 'top'
		});
	}

	if ( typeof Ladda !== 'undefined')
	{
		Ladda.bind('.ladda-button');
	}

	// Boostrap submenus
    	$('ul.dropdown-menu [data-toggle=dropdown]').on('click', function(event) {
		// Avoid following the href location when clicking
		event.preventDefault(); 

		// Avoid having the menu to close when clicking
		event.stopPropagation(); 

		// If a menu is already open we close it
		$('ul.dropdown-menu [data-toggle=dropdown]').parent().removeClass('open');

        	// opening the one you clicked on
		$(this).parent().addClass('open');
	});
});

function formConfirm(msg)
{
	if ( confirm(msg) )
	{
		return true;
	}
	else
	{
		Ladda.stopAll();
		return false;
	}
}
