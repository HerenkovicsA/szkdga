$(document).ready(function() {
	bindListeners();

});

function bindListeners() {
    var token = $("meta[name='_csrf']").attr("content");
	
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
		//$('#editModal').modal('show');
	});
	
	$('.delete').click(function(event) {
		var id = $(event.target).parent().attr("id");
		var sure = confirm("Biztos benne, hogy törölni akarja");
		if(sure){
			$.ajax({
				
				type : "POST",
				url : "/admin/deleteProduct?id=" + id, 
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
	});
	
	$('.userButton').click(function(event) {
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
    });
	
	$('#submitUser').click(function(event) {
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
