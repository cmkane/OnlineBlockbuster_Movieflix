// Javascript file for loading different pages

var validSession;

function clearSubmitButtons() {
    $('#loginSubmit').empty();
    $('#registerSubmit').empty();
    $('#quickSearchSubmit').empty();
    $('#addToCartSubmit').empty();
    $('#ratingInput').empty();
    $('#addRatingSubmit').empty();
    $('#advancedSearchSubmit').empty();
    $('#updateCartSubmit').empty();
    $('#clearCartSubmit').empty();
    $('#checkoutSubmit').empty();
    $('#payNowSubmit').empty();
    $('#billingInfoSubmit').empty();
    $('#result').empty();
    $('#pagination').empty();
    $('#paginationSubmit').empty();
}

function loadLoginPage() {
    console.log("Loading the loginpage...");
    let pageDom = $('.pagecontents');
    pageDom.empty();
    pageDom.append('<h3>Log In</h3>');
    let innerHTML = "Username:<br><input type=\"text\" name=\"email\" class=\"email\"/><br>" +
        "Password:<br><input type=\"password\" name=\"password\" class=\"password\"/><br><br>";
    clearSubmitButtons();
    $('#loginSubmit').html('<button type="submit" style="alignment: center">Submit</button>');
    pageDom.append(innerHTML)
    console.log("Page loaded!");
}

function loadRegisterPage() {
    console.log("Loading the registerpage...");
    let pageDom = $('.pagecontents');
    pageDom.empty();
    pageDom.append('<h3>Register a New User</h3>');
    let innerHTML = 'Username:<br>\n' +
        '    <input type="text" name="email" class="email"/>\n' +
        '    <br>\n' +
        '    Password:<br>\n' +
        '    <input type="password" name="password" class="password"/>\n' +
        '    <br><br>\n';
    clearSubmitButtons();
    $('#registerSubmit').html('<button type="button" name="registerButton">Submit</button>');
    pageDom.append(innerHTML);
    console.log("Page loaded!");
}

function loadQuickSearchPage() {
    console.log("Loading the quick search page...");
    clearSubmitButtons();
    verifySessionID();
    if(validSession == 0) {
        console.log("Session was not active!");
        loadLoginPage();
        $("#result").html('<p>Please log in again.</p>');
        return;
    }
    console.log("Session was active!");
    let pageDom = $('.pagecontents');
    pageDom.empty();
    pageDom.append("<h3>Search for Movie by Title</h3>Title:<input type=\"text\" name=\"title\" class=\"title\"/><br>");
    pageDom.append("Limit:<input type=\"number\" name=\"limit\" class=\"limit\" step=\"1\" min=\"1\"/><br>");
    pageDom.append("Sort by: <br><input type=\"radio\" name=\"sortby\" class=\"title\" value=\"title\"/>Title<br><input type=\"radio\" name=\"sortby\" class=\"rating\" value=\"rating\"/>Rating<br>");
    pageDom.append("Order by: <br><input type=\"radio\" name=\"orderby\" class=\"asc\" value=\"asc\"/>Ascending<br><input type=\"radio\" name=\"orderby\" class=\"desc\" value=\"desc\"/>Descending<br>");
    $('#quickSearchSubmit').html('<button type="button" name="quickSearchButton">Submit</button>');
    console.log("Page loaded!");
}

function loadAdvancedSearchPage() {
    console.log("Loading the advanced search page...");
    clearSubmitButtons();
    verifySessionID();
    if(validSession == 0) {
        console.log("Session was not active!");
        loadLoginPage();
        $("#result").html('<p>Please log in again.</p>');
        return;
    }
    console.log("Session was active!");
    let pageDom = $('.pagecontents');
    pageDom.empty();
    pageDom.append("<h3>Advanced Movie Search</h3>Title:<input type=\"text\" name=\"title\" class=\"title\"/><br>");
    pageDom.append('Director:<input type="text" name="director" class="director"/><br>');
    pageDom.append('Year:<input type="number" name="year" class="year" step="1" min="0"/><br>');
    pageDom.append('Genre:<input type="text" name="genre" class="genre"/><br>');
    pageDom.append("Limit:<input type=\"number\" name=\"limit\" class=\"limit\" step=\"1\" min=\"1\"/><br>");
    pageDom.append("Sort by: <br><input type=\"radio\" name=\"sortby\" class=\"title\" value=\"title\"/>Title<br><input type=\"radio\" name=\"sortby\" class=\"rating\" value=\"rating\"/>Rating<br>");
    pageDom.append("Order by: <br><input type=\"radio\" name=\"orderby\" class=\"asc\" value=\"asc\"/>Ascending<br><input type=\"radio\" name=\"orderby\" class=\"desc\" value=\"desc\"/>Descending<br>");
    $('#advancedSearchSubmit').html('<button type="button" name="advancedSearchButton">Submit</button>');

    console.log("Page loaded!");
}


$("button[name='homepage']").click(function (){
    console.log("Loading the homepage...");
    let pageDom = $('.pagecontents');
    pageDom.empty();
    clearSubmitButtons();
    pageDom.append("<p align=\"center\">Welcome to MovieFlix!</p>");
    console.log("Page loaded!");
});

$("button[name='loginpage']").click(function () {
    loadLoginPage();
});

$("button[name='registerpage']").click(function () {
    loadRegisterPage();
});

$("button[name='quicksearchpage']").click(function (){
    loadQuickSearchPage();
});

$("button[name='browsepage']").click(function (){
    loadBrowsePage();
});

$("button[name='advancedsearchpage']").click(function (){
    loadAdvancedSearchPage();
});

$("button[name='cartpage']").click(function (){
    loadCartPage();
});

$("button[name='orderhistorypage']").click(function (){
    loadOrderHistoryPage();
});

var intervalID;

function handleVerifySessionResponse(json, resultCode, xhr) {
    if(resultCode == "nocontent"){
        console.log("The status was 204");
        return;
    }
    else {
        console.log("Status = " + resultCode);

        let sessionID = json.sessionID;
        if(sessionID == null) {
            validSession = 0;
            let message = json.message;
            console.log("message = " + message);
            clearInterval(intervalID);
            return;
        }
        console.log("sessionId = " + sessionID);
        setCookie("sessionID", sessionID, 1);

        console.log("Setting validSession to 1.");
        validSession = 1;

        clearInterval(intervalID);
    }
}

function getVerifySessionResponse(transactionID) {
    console.log("Trying to retrieve data tId: " + transactionID);

    let resultDom = $('#result');
    resultDom.empty();

    $.ajax({
        method: "GET",
        url: getEndpoint() + "/report",
        headers:{"transactionID":transactionID},
        datatype:"json",
        success: handleVerifySessionResponse
    });
}

function verifySessionID() {
    console.log("Attempting to verify sessionID...");
    let sessionID = getCookie("sessionID");
    let email = getCookie("email");
    console.log("sessionID = " + sessionID);
    console.log("email = " + email);
    if(sessionID == null || sessionID == "" || sessionID == "undefined"){
        console.log("Session ID was not in cookie or is undefined.");
        validSession = 0;
        return;
    }

    let text = '{ "email": "' + email + '", "sessionID": "' + sessionID + '" }';

    $.ajax({
        method: "POST", // Declare request type
        url: getEndpoint() + "/idm/session",
        datatype: "json",
        contentType:"application/json",
        data: text,
        success: function (json, resultText, xhr) {
            if(resultText != "nocontent") {
                console.log("The response was not nocontent");
                validSession = 0;
                return;
            }
            let transactionID = xhr.getResponseHeader("transactionID");
            let delay = xhr.getResponseHeader("requestDelay");
            if(transactionID == null || delay == null) {
                console.log("Either the transactionID or the delay was null.");
                validSession = 0;
                return;
            }
            console.log("transactionID = " + transactionID);
            console.log("requestDelay = " + delay);
            let requestDelay = parseInt(delay, 10);
            intervalID = setInterval(getVerifySessionResponse, requestDelay, transactionID);
        }, // Bind event handler as a success callback
    });
    console.log("Verifying session ID done.");
}

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
