var orderIntervalID;

function loadOrderHistoryPage() {
    console.log("Loading order history page...");
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
        url: getEndpoint() + "/billing/order/retrieve",
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

            orderIntervalID = setInterval(getOrderHistoryResponse, requestDelay, transactionID);
        } // Bind event handler as a success callback
    });
    console.log("Page loaded!");
}

function getOrderHistoryResponse(transactionID) {
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
                if(code != "3410") {
                    if(code == "332") message = "You have not made any purchases.";
                    resultDom.html('<p>' + message + '</p>');
                    clearInterval(orderIntervalID);
                    return;
                }
                console.log("json message = " + message);
                let jsonList = json.transactions;
                console.log(json);

                if(jsonList != null) {
                    let innerHTML = '<h3>Order History</h3>';
                    innerHTML += '<table style="width:100%">';
                    innerHTML += '<tr><th>Transaction ID</th><th>State</th><th>Amount</th><th>Creation Time</th><th>Last Updated</th><th>Movies</th></tr>';
                    var i;
                    let len = jsonList.length;
                    for(i = 0; i < len; i++) {
                        let curr = jsonList[i];
                        innerHTML += '<tr>';
                        innerHTML += '<td>' + curr.transactionId + '</td>';
                        innerHTML += '<td>' + curr.state + '</td>';
                        innerHTML += '<td>' + curr.amount.total + ' ' + curr.amount.currency + '</td>';
                        let myDate = new Date(curr.create_time);
                        innerHTML += '<td>' + myDate.toString() + '</td>';
                        myDate = new Date(curr.update_time);
                        innerHTML += '<td>' + myDate.toString() + '</td>';
                        let itemList = curr.items;
                        innerHTML += '<td>';
                        var j;
                        for(j = 0; j < itemList.length; j++) {
                            let currItem = itemList[j];
                            innerHTML += currItem.movieId + ': $' + currItem.unit_price + ' x ' + currItem.quantity + ' x ' + currItem.discount;
                            if(j != itemList.length - 1) innerHTML += '<br>';
                        }
                        innerHTML += '</td>';
                        innerHTML += '</tr>';
                    }
                    innerHTML += '</table>';
                    $('#pagecontents').append(innerHTML);
                }

                clearInterval(orderIntervalID);
            }
        }
    });
}