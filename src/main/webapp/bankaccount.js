//When the page is infuse, get bank account details from server
//If data is empty, show error message
//Else show bank account details
//Error handling
$(document).ready(function	() {
	$.ajax({
		method: "GET",
		url: "bankaccount",
		datatype: "json",
		success: function(data){
			if(data.length == 0){
				document.getElementsByClassName("details")[0].innerHTML = "";
				$(".message").append("<h3>You haven't got any bank account.</h3>");
			}else {
				$.each(data, function(ArrayID, BankAccount){
					$(".details").append("<tr><td>" + BankAccount.accountNumber + 
							"</td><td> " + BankAccount.currency + 
							"</td><td>" + BankAccount.balance + "</td>" +
							"<td class='lastColumn'><button type='button' " +
							"class='btn btn-danger " + BankAccount.accountNumber + 
							"' onclick='deleteAccountNumber(\"" + BankAccount.accountNumber + "\")'>X</button>" +
							"</td></tr>");
				})
			}
		},
		error: function(jqXHR, exception) {
			var statusCode = jqXHR.status;
			document.getElementsByClassName("message")[0].innerHTML = "";
			if(statusCode == 500){
				$(".message").append("<p>Sorry, our database servers are temporarily down.</p>");
			} else {
				$(".message").append("<p>Something went wrong. Please try again a few minutes later.</p>")
			}
		}
	})
	generateAcc();
})

function generateAcc() {
	$(".generate-button").click(function() {
		var currency = $(".currency").val();
		var confirmation = confirm("Are you sure you want to generate a new account?");
	    if (confirmation == true) {
	    	$.ajax({
	    		method: "GET",
	    		url: "generate",
	    		datatype: "json",
	    		data: { "currency": currency },
	    		success: function(){
	    			window.location.reload();
	    		},
	    		error: function(){
	    			$(".message").append("<p>Something went wrong. Please try again a few minutes later.</p>")
				}
			})
	    }
	})
}

function deleteAccountNumber(id) {
	var accountNumber = id;
	var confirmation = confirm("Are you sure you want to delete this account?");
	    if (confirmation == true) {
	    	$.ajax({
	    		method: "POST",
	    		url: "deleteaccountnumber",
	    		datatype: "json",
	    		data: { "accountNumber": accountNumber },
	    		success: function(data){
	    			if(data == "ok") {
	    				window.location.reload();
	    			} else {
	    				alert(data);
	    			}
	    		},
	    		error: function(){
	    			$(".message").append("<p>Something went wrong. Please try again a few minutes later.</p>")
				}
			})
	   }
}