var token = $("meta[name='_csrf']").attr("content");

$(document).ready(function() {
	checkIfAllIsDone();
	bindListeners();

});

function bindListeners() {
	
	$('#allIsReady').click(function(event) {
		allIsReady(event);
	});
	
	$('.orderCheckDone').change(function(event) {
		orderCheckDone(event);
	});
	
	$('.orderButton').click(function(event) {
		orderButton(event);
	});
}

function allIsReady(event) {
	var id = getUrlParameter("deliveryId");
	$.ajax({			
		type : "POST",
		url : "/employee/deliveryIsDone?deliveryId=" + id, 
		data : "_csrf=" + token,
		success : function(response) {
			console.log(window.location);
			console.log(response);
			window.location = window.location.origin + response;
		},
		error : function(ex) {
			console.log(ex);
		}
    });
}

function orderCheckDone(event) {
	var orderCheckBox = $(event.target);
	var id = orderCheckBox.prop("class").substring(0,2).trim();
	var checked = orderCheckBox.prop("checked");
	$.post("/employee/orderIsDone?orderId=" + id + "&b=" + checked, "_csrf=" + token);
	checkIfAllIsDone();
}

function checkIfAllIsDone() {
	var orderCheckBoxes = $('.orderCheckDone');
	var isAllDone = true;
	for(var i = 0; i < orderCheckBoxes.length; i++){
		if(!$(orderCheckBoxes[i]).prop("checked")) {
			isAllDone = false;
			break;
		}
		
	}
	if(isAllDone){
		$("#allIsReady").show();
	} else {
		$("#allIsReady").hide();
	}
}

function orderButton(event) {
	var button = $(event.target);
	var orderTable = $('#orderTable');

	$.ajax({			
		type : "POST",
		url : "/getProductsForModalTable?orderId="+button.data('id'), 
		data : "_csrf=" + token,
		success : function(response) {
			if(response == 'error') {
				console.log("error");
				console.log(response);
			} else {
				orderTable.empty();
				orderTable.append("<thead><tr><th scope='col'>Név</th><th scope='col'>Ár</th><th>Mennyiség</th></tr></thead><tbody>");
				var row = "<tr><td>{name}</td><td>{price}</td><td>{quantity}</td></tr>";
				$.each(response, function(number, value) {
					console.log(value);
					orderTable.append(row.replace("{name}",value.name).replace("{price}",value.price)
							.replace("{quantity}",value.quantity));
				});
				orderTable.append("</tbody>");
			}	
		},
		error : function(ex) {
			console.log(ex);
		}
    });
}

function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
        }
    }
}
