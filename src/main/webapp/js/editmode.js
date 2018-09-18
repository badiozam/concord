function enterEditMode()
{
	$("body").addClass("edit");
}

function exitEditMode()
{
	$("body").removeClass("edit");
}

function isEditMode()
{
	return $("body").hasClass("edit");
}

$(function()
{
	$("#editbutton").click( function(event)
	{
		event.preventDefault();

		if ( isEditMode() )
		{
			exitEditMode();
		}
		else
		{
			enterEditMode();
		}

		return false;
	});

	$(document).on('click', 'body.edit span.msg', function()
	{
		var id = $(this).prop("id");

		console.log("Editing " + id);
		window.open(baseurl + '/base/admin/messages/index.html?key=' + id);

		exitEditMode();

		return false;
	});

	$(document).on('click', 'body.edit div.editable', function()
	{
		var ser = $(this).data("ser");
		var item = $(this).data("item");
		var location = $(this).data("location");
		if ( !item || item === '' )
		{
			item="id";
		}

		if ( !location || location === '' )
		{
			if ( item === 'categoryid')
			{
				location = "categories/update";
			}
			else
			{
				location = 'update';
			}
		}

		var editLocation = document.location.href;
		editLocation = editLocation.replace("\/user\/", "\/admin\/");
		editLocation = editLocation.replace(/\/[^/]*html.*$/, "/" + location + ".html");
		editLocation = editLocation.replace(/$/, "?" + item + "=" + ser);

		console.log("Editing " + ser);
		window.open(editLocation);

		exitEditMode();

		return false;
	});
});