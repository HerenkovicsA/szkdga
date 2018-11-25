var token = $("meta[name='_csrf']").attr("content");

$(document).ready(function() {
	sizeFooter();
	bindListeners();

});

function bindListeners() {
	fillFormForUser();
	
	runSumAtStart();
	
	$('.addToCart').click(function(event){
		addToCart(event);
	});
	
	$('.quantity').change(function(event){
		changeAmount(event);
	});
	
	$('#buy').click(function(){
		buy();
	});
	
	$( window ).resize(function() {
		sizeFooter();
	});
	
	$('.removeFromCart').click(function(event){
		removeFromCart(event);
	});
	
	$('.deleteOrder').click(function(event){
		deleteOrder(event);
	});
	
	$('.showOrder').click(function(event){
		showOrder(event);
	});
}

function fillFormForUser() {
	if($('#userEditForm').length != 0){
		$.ajax({
			type : "POST",
			url : "/getLogedUser", 
			data : "_csrf=" + token,
			success : function(response) {
				console.log(response.id);
				console.log($("#id"));
				$("#address").val(response.address);
				$("#birthday").val(fixTimeZone(response.birthday));
				$("#city").val(response.city);
				$("#email").val(response.email);
				$("#fullAddress").val(response.fullAddress);
				$("#houseNumber").val(response.houseNumber);
				$("#id").val(response.id);
				$("#name").val(response.name);
				$("#phoneNumber").val(response.phoneNumber);
				$("#postCode").val(response.postCode);
				$("#sex").val(response.sex);
			},
			error : function(ex) {
				console.log(ex);
			}
		});
	}
}

function fixTimeZone(date) {
	helper = new Date(date);
	var month = ("0" + (helper.getMonth()+1)).slice(-2);
	var day = ("0" + helper.getDate()).slice(-2); 
	return helper.getFullYear() + "-" + month + "-" + day;
}

function sizeFooter() {
	var footer = $('.footer');
	var model = $('.container').children()[0];
	footer.width($(model).width());
}

function addToCart(event) {
	event.preventDefault();
	var anchor = $(event.target);
	var url = anchor.prop("href");
	
	$.ajax({			
		type : "POST",
		url : url, 
		data : "_csrf=" + token,
		success : function(response) {
			if(response == 'error') {
				alert("Nem sikerült hozzáadni a kocsihoz.");
			}
		},
		error : function(ex) {
			console.log(ex);
		}
	});
}

function changeAmount(event) {
	var target = $(event.target);
	var quantity = target.val();
	var id = target.prop("class").substring(0,target.prop("class").indexOf(' '));
	$.ajax({			
		type : "POST",
		url : "/changeAmount?productId=" + id + "&quantity=" + quantity, 
		data : "_csrf=" + token,
		success : function(response) {
			if(response == 'error') {
				alert("Nem sikerült változtatni az értéken.");
			}
		},
		error : function(ex) {
			console.log(ex);
		}
	});
	sum();
}

function sum(){
	var quantities = $('.quantity');
	var prices = $('.price');
	var sum = $('#sum');
	var final = 0;
	for(var i = 0; i < prices.length; i++) {
		final += $(quantities[i]).val() * $(prices[i]).text().substring(0, $(prices[i]).text().indexOf(' '));
	}
	sum.text(final);
}

function runSumAtStart(){
	if($('#sum').length != 0) sum();
}

function removeFromCart(event) {
	event.preventDefault();
	var anchor = $(event.target);
	var id = anchor.parent().prop('id');
	
	$.ajax({			
		type : "POST",
		url : "/removeFromCart?productId=" + id, 
		data : "_csrf=" + token,
		success : function(response) {
			if(response == 'error') {
				alert("Nem sikerült eltávolítani a kocsiból.");
			} else {
				window.location.reload();
			}
		},
		error : function(ex) {
			console.log(ex);
		}
	});
}

function buy() {
	$.ajax({			
		type : "POST",
		url : "/makeAnOrder", 
		data : "_csrf=" + token,
		success : function(response) {
			if(response == "ok"){
				window.location = window.location.origin;
			} else {
				alert(response);
			}
		},
		error : function(ex) {
			console.log(ex);
		}
	});
}

function deleteOrder(event) {
	var sure = confirm("Biztos benne, hogy törölni akarja a rendelést?");
	if(!sure) {
		event.preventDefault();
	}
	window.location.reload();
}

function showOrder(event) {
	var anchor = $(event.target);
	var url = anchor.attr("href");
	var orderTable = $('#orderTable');
	$.ajax({			
		type : "POST",
		url : url, 
		data : "_csrf=" + token,
		success : function(response) {
			if(response == "error"){
				alert("Nem található");
			} else {
				orderTable.empty();
				orderTable.append("<thead><tr><th scope='col'>Kép</th><th scope='col'>Név</th>" +
						"<th scope='col'>Ár</th><th>Mennyiség</th></tr></thead><tbody>");
				var row = "<tr><td><img class='modalTablePic' src='{path}'></td><td>{name}</td><td>{price} Ft</td><td>{quantity}</td></tr>";
				var product;
				var quantity;
				$.each(response, function(key) {
					product = response[key].key;
					quantity = response[key].value;
					orderTable.append(row.replace("{path}",product.pathToPicture).replace("{name}",product.name)
								.replace("{price}",product.price).replace("{quantity}",quantity));
				});
				
				orderTable.append("</tbody>");
			}
		},
		error : function(ex) {
			console.log(ex);
		}
	});
}