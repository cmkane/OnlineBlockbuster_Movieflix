var json;
var intervalID;
var currTitle;

$("#quickSearchSubmit").on("click", function() {
    console.log("Pressed submit button!");
    $("#result").empty();
    clearSubmitButtons();

    let total = "&limit=" + 100;

    let title = $(".title").val();
    console.log("Trying to retrieve movies with title: " + title);
    if(title != null && title != "") {
        title = "&title=" + title;
        currTitle = title;
    }

    let limit = $(".limit").val();
    console.log("Limit was " + limit);
    if(limit == null || limit == 0) {
        limit = 10;
    }

    let sortby = $("input[name=sortby]:checked").val();
    console.log("Sortby = " + sortby);
    if(sortby != null && sortby != 'undefined') {
        sortby = "&orderby=" + sortby;
    } else {
        sortby = "";
    }

    let orderby = $("input[name=orderby]:checked").val();
    console.log("Orderby = " + orderby);
    if(orderby != null && orderby != 'undefined') {
        orderby = "&direction=" + orderby;
    } else {
        orderby = "";
    }

    let email = getCookie("email");
    let sessionID = getCookie("sessionID");

    let pageContentsDom = $('#pagecontents');
    pageContentsDom.empty();
    pageContentsDom.append('<img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');

    $.ajax({
        method: "GET", // Declare request type
        url: getEndpoint() + "/movies/search?" + title + sortby + orderby + total,
        headers:{"email":email, "sessionID":sessionID},
        datatype: "json",
        contentType:"application/json",
        success: function (json, resultText, xhr) {
            console.log("res = " + json);
            console.log("status = " + resultText);
            console.log("xhr = " + xhr.getAllResponseHeaders());

            let transactionID = xhr.getResponseHeader("transactionID");
            let delay = xhr.getResponseHeader("requestDelay");
            console.log("transactionID = " + transactionID);
            console.log("requestDelay = " + delay);
            let requestDelay = parseInt(delay, 10);

            intervalID = setInterval(getQuickSearchResponse, requestDelay, transactionID, limit, 1);
        } // Bind event handler as a success callback
    });
});

function performQuickSearch(limit, offset, sortby, orderby, page) {
    console.log("Performing quick search again...");
    let email = getCookie("email");
    let sessionID = getCookie("sessionID");
    let total = '&limit=' + 100;

    let pageContentsDom = $('#pagecontents');
    pageContentsDom.empty();
    clearSubmitButtons();
    pageContentsDom.append('<img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');

    $.ajax({
        method: "GET", // Declare request type
        url: getEndpoint() + "/movies/search?" + currTitle + offset + sortby + orderby + total,
        headers:{"email":email, "sessionID":sessionID},
        datatype: "json",
        contentType:"application/json",
        success: function (json, resultText, xhr) {
            console.log("res = " + json);
            console.log("status = " + resultText);
            console.log("xhr = " + xhr.getAllResponseHeaders());

            let transactionID = xhr.getResponseHeader("transactionID");
            let delay = xhr.getResponseHeader("requestDelay");
            console.log("transactionID = " + transactionID);
            console.log("requestDelay = " + delay);
            let requestDelay = parseInt(delay, 10);

            intervalID = setInterval(getQuickSearchResponse, requestDelay, transactionID, limit, page);
        } // Bind event handler as a success callback
    });
}

function getQuickSearchResponse(transactionID, limit, whatPage) {
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
                if(code == '211') {
                    resultDom.html('<p>' + message + '</p>');
                    clearInterval(intervalID);
                    return;
                }
                console.log("json message = " + message);
                let jsonList = json.movies;
                loadMovieListPage(jsonList, limit, whatPage, "quick");

                clearInterval(intervalID);
            }
        }
    });
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