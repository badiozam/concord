// Remove an element from the list
//
function delFromList(list, item)
{
	// See if we can find the item
	//
	for ( var i = 0; i < list.length; i++ )
	{
		if ( list[i] == item )
		{
			// Set the reference to null and return
			//
			list[i] = null;
			return;
		}
	}
}

// Add an element to the list while avoiding duplicates
//
function addToList(list, item)
{
	// See if we can find the item
	//
	for (var i = 0; i < list.length; i++ )
	{
		// Test to see if this item matches the list item
		//
		if ( list[i].value == item.value )
		{
			// It does so we can't add this prize
			//
			return;
		}
	}

	// If we got this far, we can safely add the item
	//
	list[list.length] = new Option(item.text, item.value);
}

// Function swap items, swap the position of two items in the list
//
function swapItems(list, item1Pos, item2Pos)
{
	// Save the item states so we know which ones to select
	// and which ones not to
	//
	var item1 = list[item1Pos];
	var item2 = list[item2Pos];

	// Set the first position to contain the second
	// item's values
	//
	list[item1Pos] = new Option(item2.text, item2.value);
	if ( item2.selected ) { list[item1Pos].selected = true; }

	// Now move the previous item into it's new position
	//
	list[item2Pos] = new Option(item1.text, item1.value);
	if ( item1.selected ) { list[item2Pos].selected = true; }
}

// Move an element in the list up
//
function moveUp(list, item)
{
	// See if we can find which item it is
	//
	for (var i = list.length - 1; i >= 0; i-- )
	{
		// If it's the top most element don't do
		// anything
		//
		if ( (list[i] == item) && (i > 0) )
		{
			// Swap the previous with this item
			//
			swapItems(list, i - 1, i);

			// Stop after one move
			//
			break;
		}
	}
}

// Move an element to the top of the list
//
function moveFirst(list, item)
{
	// See if we can find which item it is
	//
	for (var i = list.length - 1; i >= 0; i-- )
	{
		// If it's the top most element don't do
		// anything
		//
		if ( (list[i] == item) && (i > 0) )
		{
			// Swap the previous with this item
			//
			swapItems(list, i - 1, i);

			// Keep going as this item will match again
			// and therefore keep swapping to the top
			//
			item = list[i - 1];
		}
	}
}

// Move an element in the list up
//
function moveDown(list, item)
{
	// See if we can find which item it is
	//
	for (var i = 0; i < list.length; i++ )
	{
		// If it's the last element don't do
		// anything
		//
		if ( (list[i] == item) && (i < list.length - 1) )
		{
			// Swap the next item with this item
			//
			swapItems(list, i + 1, i);

			// Stop after one move
			//
			break;
		}
	}
}

// Move an element in the list up
//
function moveLast(list, item)
{
	// See if we can find which item it is
	//
	for (var i = 0; i < list.length; i++ )
	{
		// If it's the last element don't do
		// anything
		//
		if ( (list[i] == item) && (i < list.length - 1) )
		{
			// Swap the next item with this item
			//
			swapItems(list, i + 1, i);

			// Keep going as the next item will be matched
			// to this one
			//
			item = list[i + 1];
		}
	}
}

// Select all elements in a list
//
function selectAll(list)
{
	// Go through each item and set the select flag to true
	//
	for ( var i = 0; i < list.length; i++ )
	{
		list[i].selected = true;
	}
}

// Move all selected elements up by 1
//
function moveAllUp(list)
{
	// Go through all of the list elements and move them up
	//
	for (var i = 0; i < list.length; i++ )
	{
		var currOpt = list[i];

		// Only move the selected items
		//
		if ( currOpt.selected )
		{
			moveUp(list, currOpt);
		}
	}
}

// Move all selected elements to the top
//
function moveAllFirst(list)
{
	// Go through all of the list elements and move them up
	//
	for (var i = 0; i < list.length; i++ )
	{
		var currOpt = list[i];

		// Only move the selected items
		//
		if ( currOpt.selected )
		{
			moveFirst(list, currOpt);
		}
	}
}

// Move all selected elements down by 1
//
function moveAllDown(list)
{
	// Go through all of the list elements and move them down
	//
	for (var i = list.length - 1; i >= 0; i-- )
	{
		var currOpt = list[i];

		// Only move the selected items
		//
		if ( currOpt.selected )
		{
			moveDown(list, currOpt);
		}
	}
}

// Move all selected elements down to the last element
//
function moveAllLast(list)
{
	// Go through all of the list elements and move them down
	//
	for (var i = list.length - 1; i >= 0; i-- )
	{
		var currOpt = list[i];

		// Only move the selected items
		//
		if ( currOpt.selected )
		{
			moveLast(list, currOpt);
		}
	}
}

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

function dateTimeFirstField(input)
{
	endPos = dateTimeNextElementStart(input, 0);
	setTimeout(function () { input.setSelectionRange(0, endPos); }, 25);
	//input.setSelectionRange(0, endPos);
}

function dateTimeNextField(input)
{
	var cursorPos = cursorPosition(input);
	var startPos = dateTimeNextElementStart(input, cursorPos);

	if ( startPos < 0 || startPos == input.value.length )
	{
		return false;
	}
	else
	{
		var endPos = dateTimeNextElementStart(input, startPos + 1);
		endPos = endPos < 0 ? input.value.length : endPos;

		input.setSelectionRange(startPos + 1, endPos);

		return true;
	}
}

function dateTimePrevField(input)
{
	var cursorPos = cursorPosition(input);
	var endPos = dateTimePrevElementStart(input, cursorPos);

	if ( endPos <= 0 )
	{
		return false;
	}
	else
	{
		var startPos = dateTimePrevElementStart(input, endPos);
		startPos = startPos < 0 ? -1 : startPos;

		input.setSelectionRange(startPos + 1, endPos);

		return true;
	}
}

function dateTimePrevElementStart(field, currPos)
{
	if ( currPos < 0 )
	{
		// Invalid position
		//
		return -1;
	}
	else
	{
		var val = field.value;
		var fromCursorVal = val.substring(0, currPos);

		var delimIndex = fromCursorVal.lastIndexOf(':');
		if ( delimIndex < 0 ) { delimIndex = fromCursorVal.lastIndexOf(' '); }
		if ( delimIndex < 0 ) { delimIndex = fromCursorVal.lastIndexOf('/'); }

		return delimIndex;
	}
}

function dateTimeNextElementStart(field, currPos)
{
	if ( currPos < 0 )
	{
		// Invalid position
		//
		return -1;
	}
	else
	{
		var val = field.value;
		var fromCursorVal = val.substring(currPos);

		fromCursorVal = fromCursorVal.replace('/', ':');
		fromCursorVal = fromCursorVal.replace(' ', ':');
	
		var delimIndex = fromCursorVal.indexOf(':');
		if ( delimIndex >= 0 )
		{
			return currPos + delimIndex;
		}
		else
		{
			// No other delimiters found
			//
			return -1;
		}
	}
}

function cursorPosition(field)
{
	var pos = 0;

	if (document.selection)
	{
		field.focus();

		var sel = document.selection.createRange();
		sel.moveStart('character', -field.value.length);
		pos = sel.text.length;
	}
	else if (field.selectionStart || field.selectionStart == '0')
	{
		pos = field.selectionStart;
	}

	return pos;
}

var TAB = 9;
var LEFTARROW = 37;
var RIGHTARROW = 39;
var attachment = undefined;
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

	$('.help').popover({"trigger": "hover", "placement": "auto top", "delay": "{ show: 0, hide: 0 }" });

	// This is a workaround for Firefox that disables buttons
	// once we click the back button
	//
	$('input[type=submit],button[type=submit]', this).attr('disabled', false);

	// Depends on the jQuery placeholder plugin
	//
	$('input[placeholder], textarea[placeholder]').placeholder();

	// Depends on the bootstrap-maxlength  plugin
	//
	$('input[maxlength], textarea[maxlength]').maxlength(
	{
		'alwaysShow': 'true',
		'warningClass': 'label label-success',
		'placement': 'top'
	});

	// Disabled for now
	// Depends on the wyishtml5 plugin
	//
	//$('.wysihtml5').wysihtml5();

	if ( attachment )
	{
		$('section.listing > .row.item').on('mouseover', function(event)
		{
			$(this).addClass('highlight');
		});

		$('section.listing > .row.item').on('mouseout', function(event)
		{
			$(this).removeClass('highlight');
		});

		$('section.listing > .row.item').on('click', function(event)
		{
			var globalid = $(this).data('globalid');
			var attachURL = baseurl + "/base/admin/targeting/target.html?targetkey=" + encodeURIComponent(globalid);

			document.location.href=attachURL;
		});
	}

	// Chosen select search
	//
	$(".chosen-select").chosen({width: "100%"});

	Ladda.bind('.ladda-button');

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

	$('.dateTimePicker input[type="text"]').on('focus', function(event) { dateTimeFirstField(this); });
	$('.dateTimePicker input[type="text"]').keydown(function(event)
	{
		if ( (event.shiftKey && event.keyCode === TAB) ||
			(event.keyCode === LEFTARROW )
			)
		{
			if ( dateTimePrevField(this) )
			{
				event.preventDefault();
			}
		}
		else if ( event.keyCode === TAB || event.keyCode === RIGHTARROW )
		{
			if ( dateTimeNextField(this) )
			{
				event.preventDefault();
			}
		}
		else
		{
			// We don't handle
		}
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
