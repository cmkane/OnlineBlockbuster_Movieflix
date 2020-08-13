var intervalID;
var currAdvTitle;
var currAdvDirector;
var currAdvYear;
var currAdvGenre;

$("#advancedSearchSubmit").on("click", function() {
    console.log("Pressed submit button!");
    $("#result").empty();
    $('#pagecontents').empty();
    $('#pagecontents').html('<img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');

    let title = $(".title").val();
    console.log("Trying to retrieve movies with title: " + title);
    if(title != null && title != "") {
        title = "&title=" + title;
    }
    currAdvTitle = title;

    let limit = $(".limit").val();
    console.log("Limit was " + limit);
    if(limit == null || limit == "") {
        limit = 10;
    }
    console.log("Limit is " + limit);

    let sortby = $("input[name=sortby]:checked").val();
    console.log("Sortby = " + sortby);
    if(sortby != null) {
        sortby = "&orderby=" + sortby;
    }

    let orderby = $("input[name=orderby]:checked").val();
    console.log("Orderby = " + orderby);
    if(orderby != null) {
        orderby = "&direction=" + orderby;
    }

    let director = $(".director").val();
    console.log("Director: "+ director);
    if(director != null) {
        director = "&director=" + director;
    }
    currAdvDirector = director;

    let year = $(".year").val();
    console.log("Year: " + year);
    if(year != null && year != 0) {
        year = "&year=" + year;
    }
    currAdvYear = year;

    let genre = $(".genre").val();
    console.log("Genre: " + genre);
    if(genre != null) {
        genre = "&genre=" + genre;
    }
    currAdvGenre = genre;

    let email = getCookie("email");
    let sessionID = getCookie("sessionID");
    $.ajax({
        method: "GET", // Declare request type
        url: getEndpoint() + "/movies/search?" + title + sortby + orderby + director + year + genre,
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

            intervalID = setInterval(getSearchResponse, requestDelay, transactionID, limit);
        } // Bind event handler as a success callback
    });
});

function performAdvancedSearch(limit, offset, sortby, orderby, page) {
    let email = getCookie("email");
    let sessionID = getCookie("sessionID");

    let pageContentsDom = $('#pagecontents');
    pageContentsDom.empty();
    clearSubmitButtons();
    pageContentsDom.append('<img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');

    $.ajax({
        method: "GET", // Declare request type
        url: getEndpoint() + "/movies/search?" + currAdvTitle + sortby + orderby + currAdvDirector + currAdvYear + currAdvGenre + offset,
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

            intervalID = setInterval(getSearchResponse, requestDelay, transactionID, limit, page);
        } // Bind event handler as a success callback
    });
}

function getSearchResponse(transactionID, limit, whatPage) {
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
                if(code == "211") {
                    loadAdvancedSearchPage();
                    resultDom.html('<p>' + message + '</p>');
                    clearInterval(intervalID);
                    return;
                }
                console.log("json message = " + message);
                let jsonList = json.movies;
                console.log(jsonList);
                loadMovieListPage(jsonList, limit, whatPage, "advanced");

                clearInterval(intervalID);
            }
        }
    });
}