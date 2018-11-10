var token = $("meta[name='_csrf']").attr("content");

$(document).ready(function() {
	bindListeners();

});

function bindListeners() {
    	
	document.getElementById("openNav").onclick = function() {
	    document.getElementById("mySidenav").style.width = "250px";
	    document.getElementById("main").style.marginLeft = "250px";
	    document.body.style.backgroundColor = "rgba(0,0,0,0.4)";
	};
	
	document.getElementById("closebtn").onclick = function () {
	    document.getElementById("mySidenav").style.width = "0";
	    document.getElementById("main").style.marginLeft= "0";
	    document.body.style.backgroundColor = "white";
	};
	
	$('#addProduct').click(function(event) {
		event.preventDefault();
		$('#editModal').modal('toggle');
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
	
	var whatToDelete = anchor.attr('class');
	
	if(sure){
		$.ajax({
			
			type : "POST",
			url : "/admin/delete" + whatToDelete + "?id=" + id, 
			data : "_csrf=" + token,
			success : function(response) {
				console.log(response);
				if(response == 'deleted') {
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
			console.log("response");
			console.log(response);
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

function productButton(event){
	var button = $(event.target);
	$('#id').val(button.data('id'));
	$('#name').val(button.data('name'));
	$('#onStock').val(button.data('onstock'));
	$('#price').val(button.data('price'));
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
		url : "/getAllUser", 
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
					$( "." + value.id + ".price" ).change(function(event) {
						  updateSummOnOrderModal();
					});
					$( "." + value.id + ".quantity" ).change(function(event) {
						  updateSummOnOrderModal();
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
	var value = $('#value');
	var summ = 0;
	var prices = $('.price');
	var quantities = $('.quantity');
	for(var i = 0; i < prices.length; i++){
		summ += prices[i].value * quantities[i].value;
	}
	value.val(summ);
}

function submitOrder(event){
	event.preventDefault();
	var orderId = $('#id').val();
	var userId = $('#user').val();
    var deadLine = $('#deadLine').val();
    var value = $('#value').val();
    var done = $('#done').prop('checked');
    var deleteBoxes = $('.deleteBox');
    var quantities = $('.quantity');
    var products = [];
    console.log("deleteBoxes");
    console.log(deleteBoxes);
    console.log("quantities");
    console.log(quantities);
    
    for(var i = 0; i < quantities.length; i++) {
    	products[i] = $(quantities[i]).prop("class").charAt(0) + ";"
    		+ $(quantities[i]).val() + ";" + $(deleteBoxes[i]).prop("checked");
    }
    console.log(products);
    
	var toSend = {
			"orderId" : $('#id').val(),
			"userId" : $('#user').val(),
		    "deadLine" : $('#deadLine').val(),
		    "value" : $('#value').val(),
		    "done" : $('#done').prop('checked'),
		    "products" : products
	};
	
	console.log(toSend);
	
    $.ajax({
		//TODO: finish order update
		type : "POST",
		url : "/admin/editOrder", 
		data : "_csrf=" + token + "&json=" + JSON.stringify(toSend) ,
		success : function(response) {
			console.log("response");
			console.log(response);
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