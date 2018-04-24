$(document).ready(function	() {
	//When click submit button
	//The hidden field content set visibility to hidden
	//Get password and user name from fields
	//If password field is empty show error message
	//Else send the password and the user name to server side
	//Handle the data
	//If data equal "error" set hidden field visibility to visible, else redirect to bankaccount.html
	//Error handling
	$(".submit").click(function() {
		document.getElementById('hiddenfield').style.visibility = 'hidden';
		var username = $(".username").val();
		var password = $(".password").val();
		if(password.length < 1){
			document.getElementById('hiddenfield').style.visibility = 'visible';
		} else {
			$.ajax({
				method: "GET",
				url: "login",
				datatype: "json",
				data: { "username": username, "password": password },
				success: function(data){
					if(data == "error"){
						document.getElementById('hiddenfield').style.visibility = 'visible';
					} else {
						window.location.href = "bankaccount.html";
					}
				},
				error: function(jqXHR, exception) {
					var statusCode = jqXHR.status;
					document.getElementsByClassName("errormessage")[0].innerHTML = "";
					if(statusCode == 500){
						$(".errormessage").append("<p>Sorry, our database servers are temporarily down.</p>");
					} else {
						$(".errormessage").append("<p>Something went wrong. Please try again a few minutes later.</p>")
					}
				}
			})
		}
	});
	registration();
})

function registration(){
	$(".register").click(function() {
		window.location.href = "registration.html";
	})
}