$(document).ready(function	() {
	$(".submit").click(function() {
		$(".errormessage").empty();
		
		var username = $(".username").val();
		var password = $(".password").val();
		var passwordagain = $(".passwordagain").val();
		
		if(password != passwordagain){
			$(".errormessage").append("<p>Passwords are not the same.</p>")
		} else {
			$.ajax({
				method: "GET",
				url: "register",
				datatype: "json",
				data: { "username": username, "password": password},
				success: function(data){
					console.log(data);
					if (data != "ok"){
						$(".errormessage").append("<p>" + data + "</p>");
					} else {
						alert("Registration successful.");
						window.location.href = "index.html";
					}
				}
			});
		}
	})
})