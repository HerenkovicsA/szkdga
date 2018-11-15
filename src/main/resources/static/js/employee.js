var token = $("meta[name='_csrf']").attr("content");

$(document).ready(function() {
	bindListeners();

});

function bindListeners() {
	
	$('#past-deliveries').click(function(event) {
		pastDeliveries(event);
	});
}

function pastDeliveries(event) {
	
}