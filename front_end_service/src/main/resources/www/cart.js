var cartResults;
var cartIntervalID;
var done;
var intervalList;

function loadCartPage() {
    console.log("Loading cart page...");
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
    pageContentsDom.append('<img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');
    let email = getCookie("email");
    let sessionID = getCookie("sessionID");
    let text = '{"email":"' + email + '"}';
    cartResult = null;
    $.ajax({
        method: "POST", // Declare request type
        url: getEndpoint() + "/billing/cart/retrieve",
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

            cartIntervalID = setInterval(getCartResponse, requestDelay, transactionID);
        } // Bind event handler as a success callback
    });

    console.log("Page loaded!");
}

function getCartResponse(transactionID) {
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
                $('#pagecontents').empty();
                console.log("Status = " + resultCode);
                let message = json.message;
                let code = json.resultCode;
                console.log("Code = " + code);
                if(code != "3130") {
                    if(code != "312") message = "Unable to retrieve cart.";
                    else message = "No items in cart.";
                    resultDom.html('<p>' + message + '</p>');
                    clearInterval(cartIntervalID);
                    return;
                }
                console.log("json message = " + message);
                let jsonList = json.items;
                console.log(jsonList);
                cartResults = jsonList;

                if(cartResults != null) {
                    let innerHTML = '<h3>Cart Contents</h3>';
                    innerHTML += '<table style="width:100%">';
                    innerHTML += '<tr><th>Movie ID</th><th>Quantity</th><th>Update Quantity?</th><th>Remove?</th>';
                    var i;
                    let len = cartResults.length;
                    for(i = 0; i < len; i++) {
                        let curr = cartResults[i];
                        innerHTML += '<tr>';
                        innerHTML += '<td>' + curr.movieId + '</td>';
                        innerHTML += '<td>' + curr.quantity + '</td>';
                        innerHTML += '<td><input type="number" name="'+ curr.movieId + '" class="' + curr.movieId + '" value="' + curr.quantity + '" step="1" min="1"/></td>';
                        innerHTML += '<td><a onclick="deleteMovie(\'' + curr.movieId + '\')"href="#">X</a></td>'
                        innerHTML += '</tr>';
                    }
                    innerHTML += '</table>';
                    $('#updateCartSubmit').html('<button type="button" name="updateCartButton">Update Cart</button>');
                    $('#clearCartSubmit').html('<button type="button" name="clearCartButton">Clear Cart</button>');
                    $('#checkoutSubmit').html('<button type="button" name="checkoutButton">Checkout</button>');
                    $('#pagecontents').append(innerHTML);
                }

                clearInterval(cartIntervalID);
            }
        }
    });
}

function deleteMovie(movieId) {
    console.log("Delete from cart movie " + movieId);
    let email = getCookie("email");
    let sessionID = getCookie("sessionID");
    let text = '{"email":"' + email + '", "movieId":"' + movieId + '"}';
    $.ajax({
        method: "POST", // Declare request type
        url: getEndpoint() + "/billing/cart/delete",
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

            done = cartResults.length - 1;
            intervalList = [];
            intervalList.push(setInterval(waitForResponse, requestDelay, transactionID, 0));

        } // Bind event handler as a success callback
    });
}

$('#updateCartSubmit').on("click", function() {
    console.log("Update Cart!");
    let len = cartResults.length;
    let email = getCookie("email");
    let sessionID = getCookie("sessionID");
    var i;
    done = 0;
    intervalList = [];
    for(i = 0; i < len; i++) {
        let cur = cartResults[i];
        let itemInputName = '.' + cur.movieId;
        let text = '{"email":"' + email + '", "movieId":"' + cur.movieId + '", "quantity":"' + $(itemInputName).val() + '"}';
        console.log("Updating " + cur.movieId + "to quantity = " + $(itemInputName).val());
        $.ajax({
            method: "POST", // Declare request type
            url: getEndpoint() + "/billing/cart/update",
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
                let intervalLen = intervalList.length;
                intervalList.push(setInterval(waitForResponse, requestDelay, transactionID, intervalLen));

            } // Bind event handler as a success callback
        });
    }
});

$('#clearCartSubmit').on("click", function() {
    console.log("Clear Cart!");
    let email = getCookie("email");
    let sessionID = getCookie("sessionID");
    let text = '{"email":"' + email + '"}';
    cartResult = null;
    $.ajax({
        method: "POST", // Declare request type
        url: getEndpoint() + "/billing/cart/clear",
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

            done = cartResults.length - 1;
            intervalList = [];
            intervalList.push(setInterval(waitForResponse, requestDelay, transactionID, 0));
        } // Bind event handler as a success callback
    });
});

function waitForResponse(transactionID, i) {
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

                done = done + 1;
                if(done == cartResults.length) {
                    loadCartPage();
                } else {
                    console.log("Not done updating all cart items yet. done = " + done);
                }
                clearInterval(intervalList[i]);
            }
        }
    });
}