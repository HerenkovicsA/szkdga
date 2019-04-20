var token = $("meta[name='_csrf']").attr("content");
var CARGO_SIZE = 14900000;

$(document).ready(function() {
	bindListeners();
	fixValues();
});

function bindListeners() {
    	
	$("#openNav").click(function() {
		openSideBar();
	});
	
	$("#closebtn").click(function () {
		closeSideBar();
	});
	
	$('#addProduct').click(function(event) {
		event.preventDefault();
		$('#editModal').modal('toggle');
		addCheckForSize();
		addProduct(event);
	});
	
	$('.delete').click(function(event) {
		deleteFunc(event);
	});
	
	$('.userButton').click(function(event) {
		userButton(event);
    });
	
	$('#submitOrder').click(function(event) {
		submitOrder(event);
	});
		
	$('#submitUser').click(function(event) {
		submitUser(event);
	});
	
	$('.productButton').click(function(event) {
		productButton(event);
	});
	
	$('.orderButton').click(function(event) {
		orderButton(event)
	});
	
	$('.deliveryButton').click(function(event) {
		deliveryButton(event)
	});
	
	$('#submitDelivery').click(function(event) {
		submitDelivery(event);
	});
	
	$('.recycleProduct').click(function(event) {
		recycleProduct(event);
	});
}

function openSideBar() {
	$('#mySidenav').css('width','250px');
	$('#main').css('marginLeft','250px');
	$(document.body).css('background-color','rgba(0,0,0,0.4)');
}

function closeSideBar() {
	$('#mySidenav').css('width','0');
    $('#main').css('marginLeft','0');
    $(document.body).css('background-color','white');
}

function clearErrorMessages() {
	$('.errorMessages').hide();
}

function showFormError(errorVal) {
	for(var i=0; i < errorVal.length; i++) {
		if(errorVal[i].fieldName === 'email') {
			$('#emailErrorDiv').text(errorVal[i].message);
			$('#emailErrorDiv').show();
		}else if(errorVal[i].fieldName === 'name'){
			$('#nameErrorDiv').text(errorVal[i].message);
			$('#nameErrorDiv').show();
		}else if(errorVal[i].fieldName === 'birthday'){
			$('#birthdayErrorDiv').text(errorVal[i].message);
			$('#birthdayErrorDiv').show();
		}else if(errorVal[i].fieldName === 'phoneNumber'){
			$('#phoneNumberErrorDiv').text(errorVal[i].message);
			$('#phoneNumberErrorDiv').show();
		}else if(errorVal[i].fieldName === 'postCode'){
			$('#postCodeErrorDiv').text(errorVal[i].message);
			$('#postCodeErrorDiv').show();
		}else if(errorVal[i].fieldName === 'postCodeError'){
			$('#postCodeError').text(errorVal[i].message);
			$('#postCodeError').show();
		}else if(errorVal[i].fieldName === 'emailError'){
			$('#emailError').text(errorVal[i].message);
			$('#emailError').show();
		}else if(errorVal[i].fieldName === 'invalidAddress'){
			$('#invalidAddress').text(errorVal[i].message);
			$('#invalidAddress').show();
		}
	}
}

function deleteFunc(event){
	var anchor = $(event.target).parent();
	var id = anchor.attr("id");
	var sure = confirm("Biztos benne, hogy törölni akarja");
	
	if(anchor.hasClass('delete')){
		anchor.removeClass('delete');
	}
	var orderId = $(anchor).data('orderid');
	var whatToDelete = anchor.attr('class');
	var url = "/admin/delete";
	if(orderId == null) {
		url += whatToDelete + "?id=" + id;
	} else {
		url += whatToDelete + "?productId=" + id + "&orderId=" + orderId;
	}
	if(sure){
		$.ajax({
			
			type : "POST",
			url : url, 
			data : "_csrf=" + token,
			success : function(response) {
				if(response == 'deleted') {
					window.location.reload();
				} else if(response == 'removed') {
					window.location = window.location.origin + "/admin/orders?o";
				} else {
					alert("Error: " + id + " id-vel rendelkező termék nem létezik.");
					window.location.reload();
				}	
			},
			error : function(ex) {
				console.log(ex);
			}
		});
	}
}

function recycleProduct(event){
	var anchor = $(event.target).parent();
	var id = anchor.attr("id");
	var sure = confirm("Biztos benne, hogy vissza akarja hozni a terméket?");
	
	if(sure){
		$.ajax({
			
			type : "POST",
			url : "/admin/recycleProduct?id=" + id, 
			data : "_csrf=" + token,
			success : function(response) {
				if(response == 'recycled') {
					window.location.reload();
				} else {
					alert("Error: " + id + " id-vel rendelkező termék nem létezik.");
					window.location.reload();
				}	
			},
			error : function(ex) {
				console.log(ex);
			}
		});
	}
}

function userButton(event){
	var button = $(event.target);
	$('#id').val(button.data('id'));
	$('#email').val(button.data('email'));
	$('#name').val(button.data('name'));
	$('#birthday').val(button.data('birthday'));
	$('#phoneNumber').val(button.data('phonenumber'));
	$('#sex').val(button.data('sex'));
	$('#postCode').val(button.data('postcode'));
	$('#city').val(button.data('city'));
	$('#address').val(button.data('address'));
	$('#houseNumber').val(button.data('housenumber'));
	$('#role').val(button.data('role'));
}

function submitUser(event) {
	event.preventDefault();
	clearErrorMessages();
	var ID = $('#id').val();
	var EMAIL = $('#email').val();
	var PASSWORD = $('#password').val();
	var NAME =$('#name').val();
	var BIRTHDAY = $('#birthday').val();
	var PHONE_NUMBER = $('#phoneNumber').val();
	var SEX = $('#sex').val();
	var POST_CODE = $('#postCode').val();
	var CITY = $('#city').val();
	var ADDRESS = $('#address').val();
	var HOUSE_NUMBER = $('#houseNumber').val();
	var ROLE = $('#role').val();
	
	$.ajax({
		
		type : "POST",
		url : "/admin/editUser", 
		data : "_csrf=" + token + "&id=" + ID + "&email=" + EMAIL + "&password=" + PASSWORD + "&name=" + NAME 
			+ "&birthday=" + BIRTHDAY + "&phoneNumber=" + PHONE_NUMBER + "&sex=" + SEX + "&postCode=" + POST_CODE 
			+ "&city=" + CITY + "&address=" + ADDRESS + "&houseNumber=" + HOUSE_NUMBER + "&role=" + ROLE,
		success : function(response) {
			if(response.status == 'FAIL') {
				showFormError(response.errorMessageList);
			} else {
					window.location.reload();
			}	
		},
		error : function(ex) {
			console.log(ex);
		}
	});
}

function checkSize(){
	var h = $('#height');
	var l = $('#length');
	var w = $('#width');
	if(h.val() * l.val() * w.val() > CARGO_SIZE) {
		$('#submitAddProduct').hide();
		alert("A termék térogata nem lehet nagyobb, mint " + CARGO_SIZE + " cm3");
	} else if($('#submitAddProduct').is(":hidden")) {
		$('#submitAddProduct').show();
	}
}

function addCheckForSize() {
	$('#height').change(function() {
		checkSize();
	});
	$('#width').change(function() {
		checkSize();
	});
	$('#length').change(function() {
		checkSize();
	});
}

function addProduct(event){
	var button = $(event.target);
	$('#id').val(-1);
	$('#name').val('');
	$('#onStock').val(0);
	$('#price').val(0.0);
	$('#height').val(0);
	$('#length').val(0);
	$('#width').val(0);
	$('#pathToPicture').val('');
	$('#picture').attr('required','required');
	$('#submitAddProduct').val('Hozzáad');
	$('#infoPicEdit').attr('hidden','hidden');
}

function productButton(event){
	var button = $(event.target);
	$('#id').val(button.data('id'));
	$('#name').val(button.data('name'));
	$('#onStock').val(button.data('onstock'));
	$('#price').val(button.data('price'));
	$('#height').val(button.data('height'));
	$('#length').val(button.data('length'));
	$('#width').val(button.data('width'));
	$('#pathToPicture').val(button.data('pathtopicture'));
	$('#picture').removeAttr('required');
	$('#submitAddProduct').val('Módosít');
	$('#infoPicEdit').removeAttr('hidden');
}

function orderButton(event) {
	var button = $(event.target);
	var userSelect = $('#user');
	var orderTable = $('#orderTable');
	$('#id').val(button.data('id'));
    $('#deadLine').val(button.data('deadline').substring(0,10));
    $('#value').val(button.data('value'));
    $('#done').prop('checked', button.data('done'));
    
    $.ajax({			
		type : "POST",
		url : "/getAllUser?user=true", 
		data : "_csrf=" + token,
		success : function(response) {
			if(response == 'error') {
				userSelect.append($("option")
	                    .attr("value",-2)
	                    .addClass("form-control")
	                    .text("Nincs felhasználó az adatbázisban"));
				$('#submitOrder').hide();
			} else {
				userSelect.empty();
				$.each(response, function(id, name) { 						
					userSelect.append($("<option></option>")
				                    .attr("value",id)
				                    .text(name)); 
				});
				userSelect.val(button.data('userid'));
			}	
		},
		error : function(ex) {
			console.log(ex);
		}
	});
    
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
				orderTable.append("<thead><tr><th scope='col'>Név</th><th scope='col'>Ár</th><th>Mennyiség</th><th>Törlés</th></tr></thead><tbody>");
				var row = "<tr><td><input type='text' class='{id} name form-control' value='{name}' disabled/></td>" +
						"<td><input type='number' class='{id} price form-control' value='{price}' disabled/></td>" +
						"<td><input type='number' class='{id} quantity form-control' value='{quantity}' min='0'/></td>" +
						"<td><input type='checkbox' class='{id} deleteBox form-control'/></td></tr>";
				$.each(response, function(number, value) {
					orderTable.append(row.replace("{name}",value.name).replace("{price}",value.price)
							.replace("{quantity}",value.quantity).replace(new RegExp("{id}","g"),value.id));
					$( "." + value.id + ".deleteBox" ).change(function(event) {
						console.log("deletbox");
						updateSummOnOrderModal(event);
					});
					$( "." + value.id + ".quantity" ).change(function(event) {
						console.log("quantity");
						updateSummOnOrderModal(event);
					});
				});
				orderTable.append("</tbody>");
			}	
		},
		error : function(ex) {
			console.log(ex);
		}
    });
}

function updateSummOnOrderModal(){
	var target = $(event.target);
	var value = $('#value');
	var summ = 0;
	var prices = $('.price');
	var quantities = $('.quantity');
	var deleteBoxes = $('.deleteBox');
	for(var i = 0; i < prices.length; i++){
		if(target.hasClass("deleteBox")) {
			if($(deleteBoxes[i]).prop("checked")) {
				$(quantities[i]).val(0);
			}else if($(quantities[i]).val() == 0){
				$(quantities[i]).val(1);
			}
		}else{
			if(quantities[i].value == 0) {
				$(deleteBoxes[i]).prop("checked",true);
			}else {
				$(deleteBoxes[i]).prop("checked",false);
			}
		}
		summ += prices[i].value * quantities[i].value;
	}
	value.val(summ.toFixed(2));
}

function getId(clazz) {
	var clazzArray = clazz.split(' ');
	return clazzArray[0];
}

function submitOrder(event){
	event.preventDefault();
    var deleteBoxes = $('.deleteBox');
    var quantities = $('.quantity');
    var products = [];
    
    for(var i = 0; i < quantities.length; i++) {
    	products[i] = getId($(quantities[i]).prop('class')) + ";" + $(quantities[i]).val() + ";" + $(deleteBoxes[i]).prop("checked");
    }
    
	var toSend = {
			"orderId" : $('#id').val(),
			"userId" : $('#user').val(),
		    "deadLine" : $('#deadLine').val(),
		    "value" : $('#value').val(),
		    "done" : $('#done').prop('checked'),
		    "products" : products
	};
	
    $.ajax({
		type : "POST",
		url : "/admin/editOrder", 
		data : "_csrf=" + token + "&json=" + JSON.stringify(toSend) ,
		success : function(response) {
			if(response == 'FAIL') {
				alert("Tranzakció sikertelen");
			}
			window.location.reload();
		},
		error : function(ex) {
			console.log(ex);
		}
	});
}

function deliveryButton(event) {
	var button = $(event.target);
	var userSelect = $('#employee');
	var orderTable = $('#orderTable');
	$('#id').val(button.data('id'));
    $('#deliveryDate').val(button.data('deliverydate').substring(0,10));
    $('#employee').val(button.data('employeeid'));
    $('#done').prop('checked', button.data('done'));
    
    $( "#done" ).change(function(event) {
		deliveryAndOrderDone(event);
	});
    
    $.ajax({			
		type : "POST",
		url : "/getAllUser?user=false", 
		data : "_csrf=" + token,
		success : function(response) {
			if(response == 'error') {
				userSelect.append($("option")
	                    .attr("value",-2)
	                    .addClass("form-control")
	                    .text("Nincs felhasználó az adatbázisban"));
				$('#submitOrder').hide();
			} else {
				userSelect.empty();
				$.each(response, function(id, name) { 						
					userSelect.append($("<option></option>")
				                    .attr("value",id)
				                    .text(name)); 
				});
				userSelect.val(button.data('employeeid'));
			}	
		},
		error : function(ex) {
			console.log(ex);
		}
	});

	$.ajax({			
		type : "POST",
		url : "/getOrdersForModalTable?deliveryId="+button.data('id'), 
		data : "_csrf=" + token,
		success : function(response) {
			if(typeof(response) == 'string') {
				console.log("error");
			} else {
				orderTable.empty();
				orderTable.append("<thead><tr><th class='modal-delivery-deadline' scope='col'>Határidő</th><th scope='col'>Kiszállítva</th><th>Vásárló</th><th>Érték</th><th>Törlés</th></tr></thead><tbody>");
				var row = "<tr><td><input type='date' class='{id} deadLine form-control' value='{deadLine}'/></td>" +
						"<td><input type='checkbox' class='{id} orderDone form-control'/></td>" +
						"<td><a href='/admin/user?u&userId={userId}' class='{id} user'>{user}</a></td>" +
						"<td><input type='number' class='{id} value form-control' value='{value}' disabled/></td>" +
						"<td><input type='checkbox' class='{id} deleteBox form-control'/></td></tr>";
				var email;
				var id;
				var checked;
				$.each(response, function(key, order) {
					userId = key.substring(key.indexOf("|")+1,key.lastIndexOf("|"));
					email = key.substring(key.lastIndexOf("|")+1);
					checked = (order.done == "true");
					orderTable.append(row.replace("{deadLine}",fixTimeZone(order.deadLine)).replace("{user}",email)
						.replace("{userId}",userId).replace("{value}",order.value).replace(new RegExp("{id}","g"),order.id));
					
					$( "." + order.id + ".orderDone" ).change(function(event) {
						deliveryAndOrderDone(event);
					});

				});
				orderTable.append("</tbody>");
			}	
		},
		error : function(ex) {
			console.log(ex);
		}
    });
}

function submitDelivery(event){
	event.preventDefault();
    var deleteBoxes = $('.deleteBox');
    var deadLines = $('.deadLine');
    var orderDone = $('.orderDone');
    var orders = [];
    
    for(var i = 0; i < deleteBoxes.length; i++) {
    	orders[i] = $(deleteBoxes[i]).prop("class").charAt(0) + ";" + $(deadLines[i]).val() + ";"
    		+ $(orderDone[i]).prop("checked") + ";" + $(deleteBoxes[i]).prop("checked"); 
    }
    
	var toSend = {
			"deliveryId" : $('#id').val(),
			"deliveryDate" : $('#deliveryDate').val(),
		    "employeeId" : $('#employee').val(),
		    "done" : $('#done').prop('checked'),
		    "orders" : orders
	};
	
    $.ajax({
		type : "POST",
		url : "/admin/editDelivery", 
		data : "_csrf=" + token + "&json=" + JSON.stringify(toSend) ,
		success : function(response) {
			if(response == 'FAIL') {
				alert("Tranzakció sikertelen");
			}
			window.location.reload();
		},
		error : function(ex) {
			console.log(ex);
		}
	});
}

function deliveryAndOrderDone(event) {
	var orderDone = $('.orderDone');
	var deliveryDone = $('#done');
	var target = $(event.target);
	var allChecked;
	if(target.hasClass("orderDone")) {
		allChecked = true;
		for (var i = 0; i < orderDone.length; i++) {
			if(!$(orderDone[i]).prop("checked")) allChecked = false;
		}
		deliveryDone.prop("checked",allChecked);
	} else {
		for (var i = 0; i < orderDone.length; i++) {
			$(orderDone[i]).prop("checked",deliveryDone.prop("checked"));
		}
	}
}

function fixTimeZone(date) {
	helper = new Date(date);
	var month = ("0" + (helper.getMonth()+1)).slice(-2);
	var day = ("0" + helper.getDate()).slice(-2); 
	return helper.getFullYear() + "-" + month + "-" + day;
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