var intervalID;

$("#registerSubmit").on("click", function () {
    console.log("Pressed submit button!");
    let email = $(".email").val() ;
    let password = $(".password").val();
    var text = '{ "email" : "' + email + '", "password" : "' + password + '"}';
    console.log("Trying register with email: " + email);
    $.ajax({
        method: "POST", // Declare request type
        url: getEndpoint() + "/idm/register",
        datatype: "json",
        contentType:"application/json",
        data: text,
        success: handleRegisterResult, // Bind event handler as a success callback
    });
});

function handleRegisterResult(res, status, xhr) {
    console.log("res = " + res);
    console.log("status = " + status);
    console.log("xhr = " + xhr.getAllResponseHeaders());

    let transactionID = xhr.getResponseHeader("transactionID");
    let delay = xhr.getResponseHeader("requestDelay");
    console.log("transactionID = " + transactionID);
    console.log("requestDelay = " + delay);
    let requestDelay = parseInt(delay, 10);

    intervalID = setInterval(getRegisterResponse, requestDelay, transactionID);
}

function getRegisterResponse(transactionID) {
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
                console.log("json message = " + res2);
                let rowHTML = "<p>";
                rowHTML += res2;
                rowHTML += "</p>";

                resultDom.append(rowHTML);

                clearInterval(intervalID);
            }
        }
    });
}