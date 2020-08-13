var checkoutIntervalID;

$('#checkoutSubmit').on("click", function() {
    console.log("Loading checkout page...");
    clearSubmitButtons();
    verifySessionID();
    if(validSession == 0) {
        console.log("Session was not active!");
        loadLoginPage();
        $("#result").html('<p>Please log in again.</p>');
        return;
    }
    console.log("Session was active!");

    let pageContentsDom = $("#pagecontents");
    pageContentsDom.empty();
    pageContentsDom.append('<h3>Billing Information</h3>');
    pageContentsDom.append('First Name: <input type="text" name="firstname" class="firstname"/><br>');
    pageContentsDom.append('Last Name: <input type="text" name="lastname" class="lastname"/><br>');
    pageContentsDom.append('Credit Card Number: <input type="text" name="ccId" class="ccId"/><br>');
    pageContentsDom.append('Address: <input type="text" name="address" class="address"/><br>');
    $('#billingInfoSubmit').html('<button type="button" name="billingInfoButton">Submit</button>');

    console.log("Page loaded!");
});

$('#billingInfoSubmit').on("click", function() {
    let email = getCookie('email');
    let sessionID = getCookie('sessionID');
    let firstname = $('.firstname').val();
    let lastname = $('.lastname').val();
    let ccId = $('.ccId').val();
    let address = $('.address').val();
    let text = '{"email":"' + email + '", "firstName":"' + firstname + '", "lastName":"' + lastname + '", "ccId":"' + ccId + '", "address":"' + address + '"}';
    cartResult = null;
    $.ajax({
        method: "POST", // Declare request type
        url: getEndpoint() + "/billing/customer/insert",
        headers:{"email":email, "sessionID":sessionID},
        datatype: "json",
        contentType:"application/json",
        data: text,
        success: function (json, resultText, xhr) {
            console.log("res = " + json);
            console.log("status = " + resultText);
            console.log("xhr = " + xhr.getAllResponseHeaders());

            let transactionID = xhr.getResponseHeader("transactionID");
            let delay = xhr.getResponseHeader("requestDelay");
            console.log("transactionID = " + transactionID);
            console.log("requestDelay = " + delay);
            let requestDelay = parseInt(delay, 10);

            checkoutIntervalID = setInterval(getInsertCustomerResponse, requestDelay, transactionID);
        } // Bind event handler as a success callback
    });
});

function getInsertCustomerResponse(transactionID) {
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
                let message = json.message;
                console.log("message = " + message);
                let code = json.resultCode;
                console.log("Code = " + code);

                if(code != '3300' && code != '333') {
                    debugger;
                    $('#result').empty();
                    $('#result').append('<p>' + message + '</p>');
                    clearInterval(checkoutIntervalID);
                } else {
                    debugger;
                    placeOrder();
                    clearInterval(checkoutIntervalID);
                }

            }
        }
    });
}

function placeOrder() {
    debugger;
    let pageContentsDom = $("#pagecontents");
    pageContentsDom.empty();
    $('#pagecontents').html('<p>Please wait...</p><img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');
    clearSubmitButtons();
    let email = getCookie("email");
    let sessionID = getCookie("sessionID");
    let text = '{"email":"' + email + '"}';
    console.log("Placing order...");
    cartResult = null;
    $.ajax({
        method: "POST", // Declare request type
        url: getEndpoint() + "/billing/order/place",
        headers:{"email":email, "sessionID":sessionID},
        datatype: "json",
        contentType:"application/json",
        data: text,
        success: function (json, resultText, xhr) {
            console.log("res = " + json);
            console.log("status = " + resultText);
            console.log("xhr = " + xhr.getAllResponseHeaders());

            let transactionID = xhr.getResponseHeader("transactionID");
            let delay = xhr.getResponseHeader("requestDelay");
            console.log("transactionID = " + transactionID);
            console.log("requestDelay = " + delay);
            let requestDelay = parseInt(delay, 10);

            checkoutIntervalID = setInterval(getOrderPlaceResponse, requestDelay, transactionID);
        } // Bind event handler as a success callback
    });
}

function getOrderPlaceResponse(transactionID, i) {
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
                $('#pagecontents').empty();
                console.log("Status = " + resultCode);
                let message = json.message;
                console.log("message = " + message);
                let code = json.resultCode;
                console.log("Code = " + code);
                let redirectURL = json.redirectURL;
                console.log("redirectURL = " + redirectURL);

                //window.location.replace(redirectURL);
                $('#payNowSubmit').empty();
                //$('#payNowSubmit').html('<a href="' + redirectURL + '" target="_blank">Pay Now</a>');
                $('#payNowSubmit').append('Please follow the link to pay with PayPal:<br>');
                $('#payNowSubmit').append('<a href="' + redirectURL + '" target="_blank">Pay Now</a>');

                clearInterval(checkoutIntervalID);
            }
        }
    });
}