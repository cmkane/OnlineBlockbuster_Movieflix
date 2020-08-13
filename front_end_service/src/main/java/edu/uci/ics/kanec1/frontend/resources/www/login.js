// Event handler, the callback function to handle the API response
var intervalID;

function getEndpoint() {
    return "http://localhost:9592/api/g";
}

function handleResult(res, status, xhr) {
    console.log("res = " + res);
    console.log("status = " + status);
    console.log("xhr = " + xhr.getAllResponseHeaders());

    let transactionID = xhr.getResponseHeader("transactionID");
    let delay = xhr.getResponseHeader("requestDelay");
    console.log("transactionID = " + transactionID);
    console.log("requestDelay = " + delay);
    let requestDelay = parseInt(delay, 10);

    intervalID = setInterval(getResponse, requestDelay, transactionID);
}

function getResponse(transactionID) {
    console.log("Trying to retrieve data tId: " + transactionID);

    let resultDom = $('#result');
    resultDom.empty();

    $.ajax({
        method: "GET",
        url: getEndpoint() + "/report",
        headers:{"transactionID":transactionID},
        datatype:"json",
        success: function(json, resultCode, xhr){
            if(resultCode == "nocontent"){
                console.log("The status was 204");
                return;
            }
            else {
                console.log("Status = " + resultCode);
                let res2 = json.message;
                console.log("res2 = " + res2);
                let rowHTML = "<p>";
                rowHTML += res2;
                rowHTML += "</p>";

                let sessionID = json.sessionID;
                console.log("sessionId = " + sessionID);
                setCookie("sessionID", sessionID, 1);

                resultDom.append(rowHTML);

                clearInterval(intervalID);
            }
        }
    });
}

// Overwrite the default submit behaviour of the HTML Form
$("#loginSubmit").on("click", function (event) {
        console.log("Submitted login form.");

        let email = $(".email").val() ;// Extract data from search input box to be the title argument
        let password = $(".password").val();
        setCookie("email", email, 1);
	var text = '{ "email" : "' + email + '", "password" : "' + password + '"}';
        //var formData = JSON.stringify(text);
        console.log("Trying login with email: " + email);

        $.ajax({
            method: "POST", // Declare request type
            url: getEndpoint() + "/idm/login",
            datatype: "json",
            contentType:"application/json",
            data: text,
            success: handleResult, // Bind event handler as a success callback
        });
    }
);


function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
