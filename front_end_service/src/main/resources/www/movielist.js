var movieList;
var limit;
var searchType;
var currPage;

function loadMovieListPage(jsonList, numPerPage, pageToLoad, type) {
    console.log("Loading Movie List Page...");
    clearSubmitButtons();
    movieList = jsonList;
    limit = parseInt(numPerPage, 10);
    searchType = type;

    loadPage(pageToLoad);
    console.log("Page loaded! (" + pageToLoad + ')');
}

function loadPage(pageNum) {
    if(pageNum > 100 / limit) pageNum = 1;
    currPage = pageNum;
    let pageContentsDom = $("#pagecontents");
    pageContentsDom.empty();
    let innerHTML = '<table style="width:100%" id="movielist">';
    innerHTML += '<tr><th>Title</th><th>Director</th><th>Year</th><th>Rating</th><th>Number of Votes</th></tr>';
    var i;
    let len = movieList.length;
    for(i = (pageNum-1) * limit; i < (pageNum * limit); i++) {
        if(i >= len) break;
        let curr = movieList[i];
        innerHTML += '<tr>';
        //innerHTML += '<td>' + curr.movieId + '</td>';
        innerHTML += '<td><a onclick="loadMovieDetails(\'' + curr.movieId+ '\')" href="#">' + curr.title + '</a></td>';
        innerHTML += '<td>' + curr.director + '</td>';
        innerHTML += '<td>' + curr.year + '</td>';
        innerHTML += '<td>' + curr.rating + '</td>';
        innerHTML += '<td>' + curr.numVotes + '</td>';
        innerHTML += '</tr>';
    }
    innerHTML += '</table>';
    // put page numbers at the bottom
    for(i = 0; i < (len / limit); i++) {
        if((i+1) == pageNum) innerHTML += (i+1) + ', ';
        else {
            if(i == (len / limit) - 1) innerHTML += '<a onclick="loadPage(' + (i+1) + ')" href="#">' + (i+1) + '</a> ';
            else innerHTML += '<a onclick="loadPage(' + (i+1) + ')" href="#">' + (i+1) + '</a>, ';
        }
    }
    pageContentsDom.append(innerHTML);

    let paginationDom = $('#pagination');
    paginationDom.empty();
    paginationDom.append("<h4>Update Search Parameters:</h4>");
    paginationDom.append("Limit:<input type=\"number\" name=\"limit\" class=\"limit\" step=\"1\" min=\"1\"/><br>");
    paginationDom.append("Offset:<input type=\"number\" name=\"offset\" class=\"offset\" step=\"1\" min=\"0\"/><br>");
    paginationDom.append("Sort by: <br><input type=\"radio\" name=\"sortby\" class=\"title\" value=\"title\"/>Title<br><input type=\"radio\" name=\"sortby\" class=\"rating\" value=\"rating\"/>Rating<br>");
    paginationDom.append("Order by: <br><input type=\"radio\" name=\"orderby\" class=\"asc\" value=\"asc\"/>Ascending<br><input type=\"radio\" name=\"orderby\" class=\"desc\" value=\"desc\"/>Descending<br>");
    $('#paginationSubmit').html('<button type="button" name="paginationButton">Update</button>');
}

$('#paginationSubmit').on("click", function() {
    console.log("Pagination Submit clicked!");
    let limit = $('.limit').val();
    if(limit == null) limit = 10;

    let offset = $('.offset').val();
    if(offset != null){
        offset = '&offset=' + (offset * limit);
    } else {
        offset = "";
    }

    let sortby = $("input[name=sortby]:checked").val();
    if(sortby != null && sortby != 'undefined') {
        sortby = "&orderby=" + sortby;
    } else {
        sortby = "";
    }

    let orderby = $("input[name=orderby]:checked").val();
    if(orderby != null && orderby != 'undefined') {
        orderby = "&direction=" + orderby;
    } else {
        orderby = "";
    }

    if(searchType == "quick") performQuickSearch(limit, offset, sortby, orderby, currPage);
    if(searchType == "advanced") performAdvancedSearch(limit, offset, sortby, orderby, currPage);
    if(searchType == "browseletter") performBrowse(limit, offset, sortby, orderby, searchType, currPage);
    if(searchType == "browsegenre") performBrowse(limit, offset, sortby, orderby, searchType, currPage);
});