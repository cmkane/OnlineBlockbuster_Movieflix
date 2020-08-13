var browseIntervalID;
var currBrowseLetter;
var currBrowseGenre;

function loadBrowsePage() {
    console.log("Loading the browse page...");
    clearSubmitButtons();
    verifySessionID();
    if(validSession == 0) {
        console.log("Session was not active!");
        loadLoginPage();
        $("#result").html('<p>Please log in again.</p>');
        return;
    }
    console.log("Session was active!");
    let pageDom = $('#pagecontents');
    pageDom.empty();
    let innerHTML = "";
    innerHTML += '<h3>Browse by Letter</h3>';
    let c = 'a';
    var i;
    for(i = 0; i < 26; i++) {
        innerHTML += '<a onclick="loadBrowseResults(\'' + c + '\')" href="#">' + c + '</a>';
        if(i != 25) innerHTML += ', ';
        c = String.fromCharCode(c.charCodeAt(0) + 1);
    }
    innerHTML += '<h3>Browse by Genre</h3>';
    innerHTML += getGenreScript('Action');
    innerHTML += getGenreScript('Adult');
    innerHTML += getGenreScript('Adventure');
    innerHTML += getGenreScript('Animation');
    innerHTML += getGenreScript('Biography');
    innerHTML += getGenreScript('Comedy');
    innerHTML += getGenreScript('Crime');
    innerHTML += getGenreScript('Documentary');
    innerHTML += getGenreScript('Drama');
    innerHTML += getGenreScript('Family');
    innerHTML += getGenreScript('Fantasy');
    innerHTML += getGenreScript('History');
    innerHTML += getGenreScript('Horror');
    innerHTML += getGenreScript('Music');
    innerHTML += getGenreScript('Musical');
    innerHTML += getGenreScript('Mystery');
    innerHTML += getGenreScript('Reality-TV');
    innerHTML += getGenreScript('Romance');
    innerHTML += getGenreScript('Sci-Fi');
    innerHTML += getGenreScript('Sport');
    innerHTML += getGenreScript('Thriller');
    innerHTML += getGenreScript('War');
    innerHTML += '<a onclick="loadBrowseResultsByGenre(\'' + 'Western' + '\')" href="#">' + 'Western' + '</a>';
    pageDom.append('<h3>Browse Movies</h3>');
    pageDom.append(innerHTML);
    console.log("Page loaded.");
}

function getGenreScript(genre) {
    let innerHTML = '<a onclick="loadBrowseResultsByGenre(\'' + genre + '\')" href="#">' + genre + '</a>';
    innerHTML += ', ';
    console.log(innerHTML);
    return innerHTML;
}

function loadBrowseResults(ch) {
    let title = 'title=' + ch;
    currBrowseLetter = title;
    let numPerPage = 10;
    let limit = '&limit=' + 100;

    let email = getCookie('email');
    let sessionID = getCookie('sessionID');

    let pageContentsDom = $('#pagecontents');
    pageContentsDom.empty();
    pageContentsDom.append('<img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');

    $.ajax({
        method: "GET", // Declare request type
        url: getEndpoint() + "/movies/search?" + title + limit,
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

            browseIntervalID = setInterval(getBrowseSearchResponse, requestDelay, transactionID, numPerPage, "browseletter", 1);
        } // Bind event handler as a success callback
    });
}

function loadBrowseResultsByGenre(genre) {
    let genre2 = 'genre=' + genre;
    currBrowseGenre = genre2;
    let numPerPage = 10;
    let limit = '&limit=' + 100;

    let email = getCookie('email');
    let sessionID = getCookie('sessionID');

    let pageContentsDom = $('#pagecontents');
    pageContentsDom.empty();
    pageContentsDom.append('<img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');

    $.ajax({
        method: "GET", // Declare request type
        url: getEndpoint() + "/movies/search?" + genre2 + limit,
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

            browseIntervalID = setInterval(getBrowseSearchResponse, requestDelay, transactionID, numPerPage, "browsegenre", 1);
        } // Bind event handler as a success callback
    });
}

function performBrowse(numPerPage, offset, sortby, orderby, type, page) {
    console.log("Performing browse again...");
    let extra = "";
    if(type == "browseletter") extra += currBrowseLetter;
    if(type == "browsegenre") extra += currBrowseGenre;
    let total = '&limit=' + 100;

    let email = getCookie('email');
    let sessionID = getCookie('sessionID');

    let pageContentsDom = $('#pagecontents');
    pageContentsDom.empty();
    clearSubmitButtons();
    pageContentsDom.append('<img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');

    $.ajax({
        method: "GET", // Declare request type
        url: getEndpoint() + "/movies/search?" + extra + total + offset + sortby + orderby,
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

            browseIntervalID = setInterval(getBrowseSearchResponse, requestDelay, transactionID, numPerPage, "browsegenre", page);
        } // Bind event handler as a success callback
    });
}

function getBrowseSearchResponse(transactionID, limit, type, whatPage) {
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
                    clearInterval(browseIntervalID);
                    return;
                }
                console.log("json message = " + message);
                let jsonList = json.movies;
                loadMovieListPage(jsonList, limit, whatPage, type);

                clearInterval(browseIntervalID);
            }
        }
    });
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