var token = $("meta[name='_csrf']").attr("content");

$(document).ready(function() {
	checkIfAllIsDone();
	bindListeners();
	fixValues();
});

function bindListeners() {
	
	$('#allIsReady').click(function(event) {
		allIsReady(event);
	});
	
	$('.orderCheckDone').change(function(event) {
		orderCheckDone(event);
	});
	
	$('.makeAllDone').change(function(event) {
		makeAllDone(event);
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
		$('.makeAllDone').prop("checked",true);
	} else {
		$("#allIsReady").hide();
		$('.makeAllDone').prop("checked",false);
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

function makeAllDone(event){
	var orderCheckBoxes = $('.orderCheckDone');
	var checked = $(event.target).prop("checked");
	var id;
	for(var i = 0; i < orderCheckBoxes.length; i++){
		$(orderCheckBoxes[i]).prop("checked",checked);
		id = $(orderCheckBoxes[i]).prop("class").substring(0,2).trim();
		$.post("/employee/orderIsDone?orderId=" + id + "&b=" + checked, "_csrf=" + token);
	}
	if(checked){
		$("#allIsReady").show();
	} else {
		$("#allIsReady").hide();
	}
}

function fixValues() {
	var url = window.location.href;
	if(url.indexOf("orders?o") >= 0) {
		var values = $('.orderValue');
		for(var i = 0; i < values.length; i++) {
			$(values[i]).html(editValue( $(values[i]).html() ));
		}
	}
}

function editValue(value) {
	console.log(value);
	if(value.indexOf('E') >= 0) {
		value = value.slice(0,-3);
		var numbers = value.indexOf('E') - value.indexOf('.') -1;
		var exponential = value.charAt(value.indexOf('E')+1);
		var diff = exponential - numbers;
		value = value.replace('E','').replace('.','');
		for(var j = 0; j < diff; j++) {
			value += '0';
		}
		value += ".0 Ft";
	}
	return value;
}