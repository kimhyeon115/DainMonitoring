
function showLocationImg(element) {
	const location = $(element).data('location');
	const imgName = $(element).data('img').split('.')[0];
    const imgUrl = $(element).data('img-url');
	$('#location-name').text(location);
	$('#sensor-name').text(imgName);
    $('#popupImage').attr('src', imgUrl);
    $('#imgPopupOverlay').show();
}

function hideImgPopup() {
    $('#imgPopupOverlay').hide();
    $('#popupImage').attr('src', '');
}

function closeImgPopup(event) {
    if ($(event.target).attr('id') === 'imgPopupOverlay') {
        hideImgPopup();
    }
}

$(document).ready(function () {
    var prevLocation = null;
    var rowspan = 1;
    var $prevTd = null;

    $("#tbody_9 tr").each(function (i, row) {
        var $td = $(row).find("td:first");
        var $a = $td.find("a");
        var currLocation = $a.data("location");

        if (prevLocation === currLocation) {
            $td.remove();
            rowspan++;
            $prevTd.attr("rowspan", rowspan);
        } else {
            prevLocation = currLocation;
            rowspan = 1;
            $prevTd = $td;
        }
    });
});
