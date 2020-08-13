var intervalID;
var mId;
var jsonResponse;
var textResponse;
var xhrResponse;

function loadMovieDetails(movieId) {
    console.log("Getting details for movieId: " + movieId);
    clearSubmitButtons();
    $('#pagecontents').empty();
    $('#pagecontents').html('<img src="eatingpop.gif" style="margin-left: 400px;margin-right: 400px"/>');
    let email = getCookie("email");
    let sessionID = getCookie("sessionID");
    mId = movieId;
    $.ajax({
        method: "GET", // Declare request type
        url: getEndpoint() + "/movies/get/" + movieId,
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

            intervalID = setInterval(getMovieDetailResponse, requestDelay, transactionID);
        } // Bind event handler as a success callback
    });
}

function getMovieDetailResponse(transactionID) {
    console.log("Getting movie details...");
    console.log("tId = " + transactionID);

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
                let pageContentsDom = $('#pagecontents');
                pageContentsDom.empty();
                console.log("Status = " + resultCode);
                let message = json.message;
                let code = json.resultCode;
                if(resultCode == "211") {
                    resultDom.html('<p>' + message + '</p>');
                    return;
                }
                console.log("json message = " + message);
                let movie = json.movie;

                let innerHTML = '<h2>Movie Details for ' + movie.title + '</h2>';
                //innerHTML += '<p>Movie ID: ' + movie.movieId + '</p>';
                innerHTML += '<p>Director: ' + movie.director + '</p>';
                innerHTML += '<p>Year: ' + movie.year + '</p>';
                //innerHTML += '<p>Backdrop Path: ' + movie.backdrop_path + '</p>';
                innerHTML += '<p>Overview: ' + movie.overview + '</p>';
                //innerHTML += '<p>Poster Path: ' + movie.poster_path + '</p>';
                innerHTML += '<p>Revenue: ' + movie.revenue + '</p>';
                innerHTML += '<p>Rating: ' + movie.rating + '</p>';
                innerHTML += '<p>Number of Votes: ' + movie.numVotes + '</p>';
                let genres = movie.genres;
                innerHTML += 'Genres: <p>';
                var i;
                for(i = 0; i < genres.length; i++) {
                    let cur = genres[i];
                    innerHTML += cur.name;
                    if(i != genres.length - 1) {
                        innerHTML += ', ';
                    }
                }
                innerHTML += '</p>';
                let stars = movie.stars;
                innerHTML += 'Stars: <p>';
                for(i = 0; i < stars.length; i++) {
                    let cur = stars[i];
                    innerHTML += cur.name;
                    if(i != stars.length - 1) {
                        innerHTML += ', ';
                    }
                }
                innerHTML += '</p>';
                innerHTML += 'Add Movie to Cart:<input type="number" name="numToAdd" class="numToAdd" step="1" min="1"/>';
                $("#addToCartSubmit").html('<button type="button" name="addToCartButton" style="margin-bottom: 10px;">Submit</button>');
                $("#ratingInput").html('Rate This Movie (Out of 10):<input type="number" name="ratingToAdd" class="ratingToAdd" step="0.1" min="0.0" max="10.0" style="margin-bottom: 10px;"/>');
                $("#addRatingSubmit").html('<button type="button" name="addRatingButton">Submit</button>')
                pageContentsDom.append(innerHTML);

                clearInterval(intervalID);
            }
        }
    });
}

$("#addToCartSubmit").on("click", function() {
   let quantity = $('.numToAdd').val();
   let email = getCookie("email");
   let sessionID = getCookie("sessionID");
   let text = '{ "email":"' + email + '", "movieId":"' + mId + '", "quantity":"' + quantity + '"}';
    $.ajax({
        method: "POST", // Declare request type
        url: getEndpoint() + "/billing/cart/insert",
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

            intervalID = setInterval(getResponseGenericMessage, requestDelay, transactionID);
        } // Bind event handler as a success callback
    });
});

$("#addRatingSubmit").on("click", function() {
    let rating = $('.ratingToAdd').val();
    let email = getCookie("email");
    let sessionID = getCookie("sessionID");
    let text = '{"id":"' + mId + '", "rating":"' + rating + '"}';
    $.ajax({
        method: "POST", // Declare request type
        url: getEndpoint() + "/movies/rating",
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

            intervalID = setInterval(getResponseGenericMessage, requestDelay, transactionID);
        } // Bind event handler as a success callback
    });
});

function getResponseGenericMessage(transactionID) {
    console.log("Getting response from gateway...");
    console.log("tId = " + transactionID);

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
                console.log("Got a response!");
                console.log("Status = " + resultCode);
                let message = json.message;
                console.log("message = " + message);
                let code = json.resultCode;
                console.log("Result Code = " + code);

                jsonResponse = json;
                textResponse = resultCode;
                xhrResponse = xhr;

                $("#result").empty();
                $("#result").html('<p>' + jsonResponse.message + '</p>');

                clearInterval(intervalID);
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