var IP = "54.215.164.205";


function signUpfun(){
	
alert('user  '+ formToJSON());	

jQuery.ajax({
    type: "POST",
    url: "/cloudservices/rest/users/signup",
    data: formToJSON(),
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function (data, status, jqXHR) {
    	alert("user created successfully"+data);
    },

    error: function (jqXHR, status) {            
    	alert(jqXHR+" "+status);
    }

});
}

function formToJSON() {
    return JSON.stringify({
    "firstName": $('#firstName').val(),
    "lastName": $('#lastName').val(),
    "email": $('#email').val(),
    "passwd": $('#passwd').val()
    });
}