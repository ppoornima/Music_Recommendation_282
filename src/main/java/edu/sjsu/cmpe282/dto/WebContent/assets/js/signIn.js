var IP ="54.215.164.205";



function LogInfun(){

	var URL = "/cloudservices/rest/users/signin";
	
	alert("signin button clicked");//+formToJSON());
	
	$.ajax({
			type: "POST",
			url: URL,
			contentType: "application/json",
			dataType: 'json',
			redirect: true,
			data : formToJSON(),
			
				//success: function () { //success(data); }
			success: function(redir, textStatus, jqXHR){
					alert("you are logged in");
					location.href="http://" + IP +"/CloudServices/index1.html";
						//"http://" + IP +"/CloudServices/rest/users/home";
				},
			error: function(textStatus, jqXHR,errorThrown){
				alert(textStatus+" "+jqXHR);
			}

		});
}

function formToJSON() {
    return JSON.stringify({
    "email": $('#userName').val(),
    "passwd": $('#passwd').val(),
    });
}