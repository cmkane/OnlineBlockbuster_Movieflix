package edu.uci.ics.kanec1.service.movies.core;

import edu.uci.ics.kanec1.service.movies.MovieService;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBUses {
    public static ArrayList<MovieModel> getMovies(SearchRequestModel requestModel, boolean priv) {
        ServiceLogger.LOGGER.info("Getting movies...");

        Map<String, Integer> idAdded = new HashMap<String, Integer>();
        ArrayList<MovieModel> modelList = new ArrayList<MovieModel>();

        try {
            String title = requestModel.getTitle();
            String genre = requestModel.getGenre();
            Integer year = requestModel.getYear();
            String director = requestModel.getDirector();
            Boolean hidden = requestModel.isHidden();
            Integer offset = requestModel.getOffset();
            Integer limit = requestModel.getLimit();
            String direction = requestModel.getDirection();
            String orderby = requestModel.getOrderby();

            ServiceLogger.LOGGER.info("direction = "+direction);
            ServiceLogger.LOGGER.info("orderby = "+orderby);

            PreparedStatement ps;
            ResultSet rs;

            String SQL = "SELECT movies.id, movies.title, movies.year, movies.director, movies.backdrop_path, "+
                    "movies.budget, movies.overview, movies.poster_path, movies.revenue, movies.hidden, ratings.rating, "+
                    "ratings.numVotes, genres FROM (movies LEFT JOIN ratings ON movies.id = ratings.movieId) " +
                    "LEFT JOIN (SELECT movieId, GROUP_CONCAT(distinct concat( genreId,'=',genres.name)) AS genres FROM genres_in_movies " +
                    "left join genres on genres_in_movies.genreId = genres.id group by movieId)" +
                    "gim on movies.id = gim.movieId";

            String SQLAddOn = "";
            boolean prev = false;
            if(title != null) {
                SQLAddOn = SQLAddOn + " movies.title LIKE \'%" + title + "%\' ";
                prev = true;
            }
            if(genre != null) {
                if(prev) SQLAddOn += " AND ";
                SQLAddOn += " genres LIKE \'%" + genre + "%\' ";
                prev = true;
            }
            if (year != null && year > 0) {
                if(prev) SQLAddOn += " AND ";
                SQLAddOn += " movies.year = " + year;
                prev = true;
            }
            if(director != null) {
                if(prev) SQLAddOn += " AND ";
                SQLAddOn += " movies.director LIKE \'%" + director + "%\' ";
                prev = true;
            }
            if(hidden != null) {
                if(prev) SQLAddOn += " AND ";
                if(hidden) SQLAddOn += " movies.hidden = 1 ";
                else SQLAddOn += " movies.hidden = 0 ";
            }

            if(prev) SQLAddOn = " WHERE " + SQLAddOn;

            String ordering = "";
            String ordering2 = ", ";

            if(orderby == null || (!orderby.equals("title") && !orderby.equals("rating"))) {
                ordering = " ORDER BY ratings.rating DESC, movies.title ASC ";
                ordering2 = "";
            }
            else {
                if(orderby.equals("title")) {
                    ordering += " ORDER BY movies." + orderby + " ";
                    ordering2 += "ratings.rating DESC ";
                }
                else {
                    ordering += " ORDER BY ratings." + orderby + " ";
                    ordering2 += "movies.title ASC ";
                }

                if(direction == null || (!direction.toLowerCase().equals("asc") && !direction.toLowerCase().equals("desc"))) {
                    ordering = " ORDER BY ratings.rating DESC, movies.title ASC ";
                    ordering2 = "";
                }
                else {
                    ordering += direction;
                }
            }

            SQLAddOn += ordering;
            SQLAddOn += ordering2;

            if(limit != null && limit >= 0 && (limit == 10 || limit == 25 || limit == 50 || limit == 100)) {
                SQLAddOn += "LIMIT " + limit + " ";
            } else {
                SQLAddOn += "LIMIT 10 ";
            }

            if(offset != null && offset >= 0 && offset % limit == 0) {
                SQLAddOn += "OFFSET " + offset + ";";
            } else {
                SQLAddOn += "OFFSET 0;";
            }

            SQL = SQL + SQLAddOn;
            ps = MovieService.getCon().prepareStatement(SQL);
            ServiceLogger.LOGGER.info("Attempting query: "+ps.toString());
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query success.");
            MovieModel model;
            while(rs.next()) {
                if(!idAdded.containsKey(rs.getString("id"))) {
                    idAdded.put(rs.getString("id"), 1);
                    Boolean hidden2 = false;
                    Integer budget2 = rs.getInt("budget");
                    if(budget2 == 0) budget2 = null;
                    Integer revenue2 = rs.getInt("revenue");
                    if(revenue2 == 0) revenue2 = null;
                    if(rs.getInt("hidden") == 1) hidden2 = true;
                    model = new MovieModel(rs.getString("id"), rs.getString("title"), rs.getString("director"),
                            rs.getInt("year"), rs.getFloat("rating"), rs.getInt("numVotes"), hidden2);
                    if(priv)
                        modelList.add(model);
                    else if(!model.getHidden()) {
                        model.setHidden(null);
                        modelList.add(model);
                    }
                }
            }

            return modelList;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to retreive movies.");
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<MovieByIDModel> retreiveMovie(String id) {
        ServiceLogger.LOGGER.info("Starting DB Movie retreival...");

        String SQL = "SELECT * FROM movies WHERE id=?;";

        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            int count = 0;

            ArrayList<MovieByIDModel> modelList = new ArrayList<MovieByIDModel>();

            Integer year;
            Integer budget;
            Integer revenue;
            Integer hidden;
            Boolean hidden2;

            while(rs.next()) {
                count++;
                year = rs.getInt("year");
                if(year == 0 || year < 0) year = null;
                budget = rs.getInt("budget");
                if(budget == 0) budget = null;
                revenue = rs.getInt("revenue");
                if(revenue == 0) revenue = null;
                hidden = rs.getInt("hidden");
                if(hidden == 0) hidden2 = false;
                else hidden2 = true;
                modelList.add(new MovieByIDModel(rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("director"),
                        year,
                        (float) 0.0,
                        null,
                        null,
                        null,

                        rs.getString("backdrop_path"),
                        budget,
                        rs.getString("overview"),
                        rs.getString("poster_path"),
                        revenue,
                        hidden2));
            }

            //Check if no movies found
            if(count == 0) {
                ServiceLogger.LOGGER.info("No movies found with id = "+ id);
                return modelList;
            }

            // Get the genres, stars, and rating/numvotes for a movie
            MovieByIDModel curr;
            for(int x = 0; x < modelList.size(); x++) {
                curr = modelList.get(x);
                //getStars
                ArrayList<StarModel> stars = getStars(curr.getMovieId());
                //Add stars to current MovieModel
                curr.buildStarsFromList(stars);
                //getGenres
                ArrayList<GenreModel> genres = getGenres(curr.getMovieId());
                //Add genres to current MovieModel
                curr.buildGenreFromList(genres);
                //setRatings
                setRatings(curr);
            }

            return modelList;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("SQLException occurred.");
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<StarModel> getStars(String movieId) {
        ServiceLogger.LOGGER.info("Getting stars for movie "+movieId);

        String SQL = "SELECT * FROM stars_in_movies WHERE movieId = ?;";

        ArrayList<StarModel> list = new ArrayList<StarModel>();

        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, movieId);
            ResultSet rs = ps.executeQuery();

            String id = null;
            ResultSet starInfoRS;
            StarModel model = null;
            while(rs.next()) {
                id = rs.getString("starId");
                SQL = "SELECT * FROM stars WHERE id = ?;";
                ps = MovieService.getCon().prepareStatement(SQL);
                ps.setString(1, id);
                starInfoRS = ps.executeQuery();
                while(starInfoRS.next()) {
                    model = new StarModel(starInfoRS.getString("id"), starInfoRS.getString("name"), null);
                }
                //Add star model to the array list
                ServiceLogger.LOGGER.info("Adding star: id = "+model.getId()+" name = "+model.getName());
                list.add(model);
            }
            return list;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to get stars for movieId = "+movieId);
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<GenreModel> getGenres(String movieId) {
        ServiceLogger.LOGGER.info("Getting genres for movie "+movieId);

        String SQL = "SELECT * FROM genres_in_movies WHERE movieId = ?;";

        ArrayList<GenreModel> list = new ArrayList<GenreModel>();

        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, movieId);
            ResultSet rs = ps.executeQuery();

            String id = null;
            ResultSet genreInfoRS;
            GenreModel model = null;
            while(rs.next()) {
                id = rs.getString("genreId");
                SQL = "SELECT * FROM genres WHERE id = ?;";
                ps = MovieService.getCon().prepareStatement(SQL);
                ps.setString(1, id);
                genreInfoRS = ps.executeQuery();
                while(genreInfoRS.next()) {
                    model = new GenreModel(genreInfoRS.getInt("id"), genreInfoRS.getString("name"));
                }
                //Add genre model to the array list
                ServiceLogger.LOGGER.info("Adding genre: id = "+model.getId()+" name = "+model.getName());
                list.add(model);
            }
            return list;
        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Unable to get genres for movieId = "+movieId);
            e.printStackTrace();
            return null;
        }
    }

    public static void setRatings(MovieByIDModel model) {
        ServiceLogger.LOGGER.info("Setting rating and numvotes for movie...");

        if(model == null) {
            ServiceLogger.LOGGER.info("The model passed in is null.");
            return;
        }

        String SQL = "SELECT * FROM ratings WHERE movieId = ?;";

        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, model.getMovieId());
            ResultSet rs = ps.executeQuery();
            Float rating;
            int numVotes;
            while(rs.next()) {
                rating = rs.getFloat("rating");
                numVotes = rs.getInt("numVotes");
                ServiceLogger.LOGGER.info("Setting rating = "+ rating + ", numVotes = " + numVotes);
                model.setRating(rating);
                model.setNumVotes(numVotes);
            }
            ServiceLogger.LOGGER.info("Finished setting ratings.");
        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Unable to get ratings/numvotes for movieId = "+model.getMovieId());
            e.printStackTrace();
            return;
        }
    }

    public static boolean doesMovieExist(String title) {
        ServiceLogger.LOGGER.info("Checking if movie exists...");
        String SQL = "SELECT COUNT(*) FROM movies WHERE title = ?;";
        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, title);
            ServiceLogger.LOGGER.info("Trying Query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful");
            int count = -1;
            while(rs.next()) {
                count = rs.getInt("COUNT(*)");
            }
            if(count >= 1) {
                ServiceLogger.LOGGER.info("Movie exists.");
                return true;
            }
            ServiceLogger.LOGGER.info("Movie does not exist.");
            return false;
        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Some error occurred when checking if movie exists.");
            e.printStackTrace();
            return false;
        }
    }

    public static Integer hasMovieBeenDeleted(String id) {
        ServiceLogger.LOGGER.info("Checking if movie exists...");
        String SQL = "SELECT * FROM movies WHERE id = ?;";
        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Trying Query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful");
            Integer hidden = null;
            while(rs.next()) {
                hidden = rs.getInt("hidden");
            }
            return hidden;
        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Some error occurred when checking if movie has been deleted.");
            e.printStackTrace();
            return null;
        }
    }

    public static AddPageResponseModel addMovieToDB(AddPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Entering addMovieToDB...");

        String SQL = "SELECT * FROM movies WHERE id LIKE \'%cs%\' ORDER BY id DESC Limit 1";

        String title = requestModel.getTitle();
        String director = requestModel.getDirector();
        Integer year = requestModel.getYear();
        String backdrop_path = requestModel.getBackdrop_path();
        Integer budget = requestModel.getBudget();
        String overview = requestModel.getOverview();
        String poster_path = requestModel.getPoster_path();
        Integer revenue = requestModel.getRevenue();
        GenreModel genres[] = requestModel.getGenres();

        boolean movieExists = doesMovieExist(title);
        if(movieExists) {
            return new AddPageResponseModel(216, "Movie already exists.", null, null);
        }

        try {
            // Insert into movies
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ServiceLogger.LOGGER.info("Attempting Query: "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query success.");
            String id = null;
            while(rs.next()) {
                id = rs.getString("id");
            }
            if(id == null) id = "cs0000001";
            else {
                ServiceLogger.LOGGER.info("The biggest id currently: " + id);
                id = id.replaceAll("cs", "");
                /*char[] idArray = id.toCharArray();
                for(int x = 0; x < id.length(); x++) {
                    if(idArray[x] == '0') idArray[x] = ' ';
                    else break;
                }
                id = idArray.toString();
                id = id.trim();
                ServiceLogger.LOGGER.info("The id after parsing: " + id);
                int y = Integer.parseInt(id) + 1;
                ServiceLogger.LOGGER.info("The number got: " + y);
                int places = y / 10;
                id = Integer.toString(y);
                for(int j = 0; j < (6-places); j++) {
                    id = "0" + id;
                }
                id = "cs" + id;*/
                int y = Integer.parseInt(id) + 1;
                int places = y / 10;
                id = "" + y;
                for(int j = 0; j < (6-places); j++) {
                    id = "0" + id;
                }
                id = "cs" + id;
            }

            ServiceLogger.LOGGER.info("We are using movieId = " + id);

            SQL = "INSERT INTO movies (id, title, director, year, backdrop_path, budget, overview, poster_path, revenue, hidden) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            ps = MovieService.getCon().prepareStatement(SQL);

            ps.setString(1, id);
            if(title == null) ps.setNull(2, Types.VARCHAR);
            else ps.setString(2, title);
            if(director == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, director);
            if(year == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, year);
            if(backdrop_path == null) ps.setNull(5, Types.VARCHAR);
            else ps.setString(5, backdrop_path);
            if(budget == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, budget);
            if(overview == null) ps.setNull(7, Types.VARCHAR);
            else ps.setString(7, overview);
            if(poster_path == null) ps.setNull(8, Types.VARCHAR);
            else ps.setString(8, poster_path);
            if(revenue == null) ps.setNull(9, Types.INTEGER);
            else ps.setInt(9, revenue);
            ps.setInt(10, 0);

            ServiceLogger.LOGGER.info("Attempting query: "+ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query success");

            // Initialize the Rating Table for movie id
            initRating(id);

            // Insert into genres_in_movies and genres
            int len = genres.length;
            GenreModel curr = null;
            ArrayList<Integer> genreIdList = new ArrayList<Integer>();
            for(int x = 0; x < len; x++) {
                curr = genres[x];
                ServiceLogger.LOGGER.info("Handling genre: id = "+curr.getId()+", name = "+curr.getName());
                SQL = "SELECT * FROM genres WHERE name LIKE ?;";
                ps = MovieService.getCon().prepareStatement(SQL);
                ps.setString(1, curr.getName());
                rs = ps.executeQuery();
                boolean exist = false;

                // Add genre into genres table if it doesn't already exist
                int genreId = -1;

                while(rs.next()) {
                    exist = true;
                    genreId = rs.getInt("id");
                }

                if(!exist) {
                    ServiceLogger.LOGGER.info("This genre does not currently exists. Inserting into genres...");
                    SQL = "INSERT INTO genres (name) VALUES (?);";
                    ps = MovieService.getCon().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, curr.getName());
                    ServiceLogger.LOGGER.info("Attempting insertion: "+ps.toString());
                    ps.execute();
                    ServiceLogger.LOGGER.info("Insertion success.");
                    rs = ps.getGeneratedKeys();
                    if(rs.next()) {
                        genreId = rs.getInt(1);
                        ServiceLogger.LOGGER.info("Next new genreId = " + genreId);
                    }
                }
                else {
                    ServiceLogger.LOGGER.info("Genre already exists.");
                }

                SQL = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?,?);";
                ps = MovieService.getCon().prepareStatement(SQL);
                ps.setInt(1, genreId);
                ps.setString(2, id);
                ServiceLogger.LOGGER.info("Attempting insertion: "+ps.toString());
                ps.execute();
                ServiceLogger.LOGGER.info("Insertion successful");

                genreIdList.add((Integer) genreId);
            }

            // TODO Set up the response model
            int l = genreIdList.size();
            int[] genreid = new int[l];
            for(int y = 0; y < l; y++) {
                genreid[y] = genreIdList.get(y);
            }
            AddPageResponseModel responseModel = new AddPageResponseModel(214, "Movie successfully added.", id, genreid);
            return responseModel;


        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to insert movie into DB.");
            e.printStackTrace();
            return new AddPageResponseModel(215, "Could not add movie.", null, null);
        }
    }

    public static AddPageResponseModel addMovie(String id, AddPageRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Entering addMovieToDB...");

        String title = requestModel.getTitle();
        String director = requestModel.getDirector();
        Integer year = requestModel.getYear();
        String backdrop_path = requestModel.getBackdrop_path();
        Integer budget = requestModel.getBudget();
        String overview = requestModel.getOverview();
        String poster_path = requestModel.getPoster_path();
        Integer revenue = requestModel.getRevenue();
        GenreModel genres[] = requestModel.getGenres();

        boolean movieExists = doesMovieExist(title);
        if(movieExists) {
            return new AddPageResponseModel(216, "Movie already exists.", null, null);
        }

        try {
            ServiceLogger.LOGGER.info("We are using movieId = " + id);

            String SQL = "INSERT INTO movies2 (id, title, director, year, backdrop_path, budget, overview, poster_path, revenue, hidden) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);

            ps.setString(1, id);
            if(title == null) ps.setNull(2, Types.VARCHAR);
            else ps.setString(2, title);
            if(director == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, director);
            if(year == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, year);
            if(backdrop_path == null) ps.setNull(5, Types.VARCHAR);
            else ps.setString(5, backdrop_path);
            if(budget == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, budget);
            if(overview == null) ps.setNull(7, Types.VARCHAR);
            else ps.setString(7, overview);
            if(poster_path == null) ps.setNull(8, Types.VARCHAR);
            else ps.setString(8, poster_path);
            if(revenue == null) ps.setNull(9, Types.INTEGER);
            else ps.setInt(9, revenue);
            ps.setInt(10, 0);

            ServiceLogger.LOGGER.info("Attempting query: "+ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query success");

            // Initialize the Rating Table for movie id
            initRating(id);

            // Insert into genres_in_movies and genres
            int len = genres.length;
            GenreModel curr = null;
            ArrayList<Integer> genreIdList = new ArrayList<Integer>();
            for(int x = 0; x < len; x++) {
                curr = genres[x];
                ServiceLogger.LOGGER.info("Handling genre: id = "+curr.getId()+", name = "+curr.getName());
                SQL = "SELECT * FROM genres2 WHERE name LIKE ?;";
                ps = MovieService.getCon().prepareStatement(SQL);
                ps.setString(1, curr.getName());
                ResultSet rs = ps.executeQuery();
                boolean exist = false;

                // Add genre into genres table if it doesn't already exist
                int genreId = -1;

                while(rs.next()) {
                    exist = true;
                    genreId = rs.getInt("id");
                }

                if(!exist) {
                    ServiceLogger.LOGGER.info("This genre does not currently exists. Inserting into genres...");
                    SQL = "INSERT INTO genres2 (name) VALUES (?);";
                    ps = MovieService.getCon().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, curr.getName());
                    ServiceLogger.LOGGER.info("Attempting insertion: "+ps.toString());
                    ps.execute();
                    ServiceLogger.LOGGER.info("Insertion success.");
                    rs = ps.getGeneratedKeys();
                    if(rs.next()) {
                        genreId = rs.getInt(1);
                        ServiceLogger.LOGGER.info("Next new genreId = " + genreId);
                    }
                }
                else {
                    ServiceLogger.LOGGER.info("Genre already exists.");
                }

                SQL = "INSERT INTO genres_in_movies2 (genreId, movieId) VALUES (?,?);";
                ps = MovieService.getCon().prepareStatement(SQL);
                ps.setInt(1, genreId);
                ps.setString(2, id);
                ServiceLogger.LOGGER.info("Attempting insertion: "+ps.toString());
                ps.execute();
                ServiceLogger.LOGGER.info("Insertion successful");

                genreIdList.add((Integer) genreId);
            }

            // TODO Set up the response model
            int l = genreIdList.size();
            int[] genreid = new int[l];
            for(int y = 0; y < l; y++) {
                genreid[y] = genreIdList.get(y);
            }
            AddPageResponseModel responseModel = new AddPageResponseModel(214, "Movie successfully added.", id, genreid);
            return responseModel;


        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to insert movie into DB.");
            e.printStackTrace();
            return new AddPageResponseModel(215, "Could not add movie.", null, null);
        }
    }

    public static void initRating(String movieId) {
        ServiceLogger.LOGGER.info("Initializing Rating . . .");
        String SQL = "INSERT INTO ratings (movieId, rating, numVotes) VALUES (?, 0.0, 0);";
        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, movieId);
            ServiceLogger.LOGGER.info("Attempting Insertion: "+ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Insert successful.");
            ServiceLogger.LOGGER.info("Rating initialized.");

        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Unable to initialize Rating.");
            e.printStackTrace();
        }
    }

    public static GenericResponseModel removeMovie(String id) {
        ServiceLogger.LOGGER.info("Attempting to remove movie with id = "+id);

        String SQL = "UPDATE movies SET hidden=1 WHERE id = ?;";
        Integer movieDeleted = hasMovieBeenDeleted(id);
        if(movieDeleted == null) {
            ServiceLogger.LOGGER.info("Movie does not exist.");
            return new GenericResponseModel(241, "Could not remove movie.");
        }
        if(movieDeleted == 1) {
            ServiceLogger.LOGGER.info("Movie has already been removed.");
            return new GenericResponseModel(242, "Movie has been already removed.");
        }

        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Attempting Update: "+ps.toString());
            int count = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update success. Updated "+count+" movie(s).");

            return new GenericResponseModel(240, "Movie successfully removed.");
        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Unable to remove movie.");
            e.printStackTrace();
            return new GenericResponseModel(241, "Could not remove movie.");
        }
    }

    public static GenreListResponseModel fetchGenres() {
        ServiceLogger.LOGGER.info("Entered fetch genres...");
        String SQL = "SELECT * FROM genres";
        ArrayList<GenreModel> modelList = new ArrayList<GenreModel>();

        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ServiceLogger.LOGGER.info("Attempting Query: "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query success.");

            String name;
            int id;
            while(rs.next()) {
                name = rs.getString("name");
                id = rs.getInt("id");
                modelList.add(new GenreModel(id, name));
            }

            int len = modelList.size();
            if(len == 0) {
                return new GenreListResponseModel(219, "Genres successfully retrieved.", null);
            }
            GenreModel[] array = new GenreModel[len];
            for(int x = 0; x < len; x++) {
                array[x] = modelList.get(x);
            }

            return new GenreListResponseModel(219, "Genres successfully retrieved.", array);
        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Unable to fetch genres.");
            e.printStackTrace();
            return null;
        }
    }

    public static int addGenre(String name) {
        ServiceLogger.LOGGER.info("Entered add genre...");
        String SQL = "INSERT INTO genres (name) VALUES (?);";

        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, name);
            ServiceLogger.LOGGER.info("Attempting Insert: "+ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Insert success.");

            return 217;
        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Unable to add genre.");
            e.printStackTrace();
            return 218;
        }
    }

    public static ArrayList<StarModel> searchStars(SearchStarsRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Beggining search of stars.");

        Map<String, Integer> idAdded = new HashMap<String, Integer>();
        ArrayList<StarModel> modelList = new ArrayList<StarModel>();

        PreparedStatement ps;
        ResultSet rs;

        try {
            String name = requestModel.getName();
            Integer birthYear = requestModel.getBirthYear();
            String movieTitle = requestModel.getMovieTitle();
            Integer offset = requestModel.getOffset();
            Integer limit = requestModel.getLimit();
            String orderby = requestModel.getOrderby();
            String direction = requestModel.getDirection();

            String SQL = "SELECT stars.id, stars.name, stars.birthYear, titles FROM stars " +
                    "LEFT JOIN (SELECT stars_in_movies.starId, GROUP_CONCAT(distinct title) AS titles " +
                    "FROM (stars_in_movies LEFT JOIN movies m on stars_in_movies.movieId = m.id) GROUP BY starId) as stuff " +
                    "ON stars.id = stuff.starId";

            String SQLAddOn = "";
            boolean prev = false;
            if(name != null) {
                SQLAddOn = SQLAddOn + " stars.name LIKE \'%" + name + "%\' ";
                prev = true;
            }
            if(birthYear != null && birthYear > 0) {
                if(prev) SQLAddOn += " AND ";
                SQLAddOn += " stars.birthYear = " + birthYear;
                prev = true;
            }
            if(movieTitle != null) {
                if(prev) SQLAddOn += " AND ";
                SQLAddOn += " titles LIKE \'%" + movieTitle + "%\' ";
                prev = true;
            }

            if(prev) SQLAddOn = " WHERE " + SQLAddOn;

            String ordering = "";
            String ordering2 = ", ";

            if(orderby == null || (!orderby.equals("name") && !orderby.equals("birthYear"))) {
                ordering = " ORDER BY stars.name ASC, stars.birthYear ASC ";
                ordering2 = "";
            }
            else {
                ordering += " ORDER BY stars." + orderby + " ";
                if(orderby.equals("name")) {
                    ordering2 += "stars.birthYear ASC ";
                } else {
                    ordering2 += "stars.name ASC ";
                }
                if(direction == null || (!direction.toLowerCase().equals("asc") && !direction.toLowerCase().equals("desc"))) {
                    ordering = " ORDER BY stars.name ASC, stars.birthYear ASC ";
                    ordering2 = "";
                }
                else {
                    ordering += direction + " ";
                }
            }

            SQLAddOn += ordering;
            SQLAddOn += ordering2;

            if(limit != null && limit >= 0 && (limit == 10 || limit == 25 || limit == 50 || limit == 100)) {
                SQLAddOn += "LIMIT " + limit + " ";
            } else {
                SQLAddOn += "LIMIT 10 ";
            }

            if(offset != null && offset >= 0 && offset % limit == 0) {
                SQLAddOn += "OFFSET " + offset + ";";
            } else {
                SQLAddOn += "OFFSET 0;";
            }

            SQL = SQL + SQLAddOn;
            ps = MovieService.getCon().prepareStatement(SQL);
            ServiceLogger.LOGGER.info("Attempting query: "+ps.toString());
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query success.");

            StarModel model;
            String id;
            Integer birthYear2;
            while(rs.next()) {
                if(!idAdded.containsKey(rs.getString("id"))) {
                    id = rs.getString("id");
                    idAdded.put(id, 1);
                    birthYear2 = rs.getInt("birthYear");
                    if(birthYear2 == 0) birthYear2 = null;
                    model = new StarModel(rs.getString("id"), rs.getString("name"), birthYear2);
                    ServiceLogger.LOGGER.info("Adding star: id = "+id+", name = "+rs.getString("name")+", birthYear = "+rs.getInt("birthYear"));
                    modelList.add(model);
                }
            }

            return modelList;


        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Unable to retrieve stars.");
            e.printStackTrace();
            return null;
        }
    }

    // For star/{id} endpoint
    public static StarModel retrieveStar(String id) {
        ServiceLogger.LOGGER.info("Retrieving star with id "+id);

        String SQL = "SELECT * FROM stars WHERE id = ?;";

        StarModel model = null;

        try {
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1,id);
            ResultSet rs = ps.executeQuery();
            String name = "";
            Integer birthYear = -1;
            int count = 0;
            while(rs.next()) {
                count++;
                name = rs.getString("name");
                birthYear = rs.getInt("birthYear");
                if(birthYear == 0) birthYear = null;
            }
            if(count == 0) return new StarModel(null, null, 0);

            model = new StarModel(id, name, birthYear);

            return model;


        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to get star with id "+id);
            e.printStackTrace();
            return null;
        }
    }

    public static int addStarToDB(AddStarRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Adding star to DB...");

        try {
            String name = requestModel.getName();
            Integer year = requestModel.getBirthYear();

            // does the star already exist?
            String SQL = "SELECT * FROM stars WHERE name = ? AND birthYear ";
            if (year != null) SQL += "= ?;";
            else SQL += "IS ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, name);
            if (year != null) ps.setInt(2, year);
            else ps.setNull(2, Types.INTEGER);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while(rs.next()) {
                count++;
            }
            if(count >= 1) return 222;

            // Get the new id
            SQL = "SELECT * FROM stars WHERE id LIKE \'%ss%\' ORDER BY id DESC Limit 1;";
            ps = MovieService.getCon().prepareStatement(SQL);
            ServiceLogger.LOGGER.info("Attempting Query: " + ps.toString());
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Successful.");
            String id = null;
            while(rs.next()) {
                id = rs.getString("id");
            }

            if(id == null) id = "ss0000001";
            else {
                id = id.replaceAll("ss", "");
                id = id.replace('0', ' ');
                id = id.trim();
                int y = Integer.parseInt(id) + 1;
                int places = y / 10;
                id = Integer.toString(y);
                for(int j = 0; j < (6-places); j++) {
                    id = "0" + id;
                }
                id = "ss" + id;
            }
            ServiceLogger.LOGGER.info("Using new id = " + id);

            SQL = "INSERT INTO stars (id, name, birthYear) VALUES(?,?,?);";
            ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1,id);
            ps.setString(2, name);
            if(year == null || year <= 0 || year > 2019) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, year);
            }
            ServiceLogger.LOGGER.info("Attempting insert: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Insert successful.");
            return 220;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to add star to DB");
            e.printStackTrace();
            return 221;
        }
    }

    public static int addToStarsInDB(StarsInRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Adding to stars in movies db...");
        try {
            String SQL = "SELECT * FROM movies WHERE id = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, requestModel.getMovieid());
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while(rs.next()) {
                count++;
            }
            if(count == 0) return 211;

            String SQL2 = "INSERT INTO stars_in_movies (starid, movieid) VALUES (?,?);";
            ps = MovieService.getCon().prepareStatement(SQL2);
            ps.setString(1, requestModel.getStarid());
            ps.setString(2, requestModel.getMovieid());
            ServiceLogger.LOGGER.info("Attempting insert: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Insert Successful.");
            return 230;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to enter into stars_in_movie.");
            e.printStackTrace();
            String message = e.getMessage();
            if(e instanceof SQLIntegrityConstraintViolationException && (message.contains("Duplicate") || message.contains("duplicate"))) {
                ServiceLogger.LOGGER.info("Star already exists in movie.");
                return 232;
            } else {
                ServiceLogger.LOGGER.info("Star does not exist or some other error.");
                return 231;
            }
        }
    }

    public static int updateRating(RatingRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Entering update rating...");
        try {
            String SQL = "SELECT * FROM ratings WHERE movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(SQL);
            ps.setString(1, requestModel.getId());
            ResultSet rs = ps.executeQuery();
            Float currRate = null;
            Integer currNumVotes = null;
            while(rs.next()) {
                currRate = rs.getFloat("rating");
                currNumVotes = rs.getInt("numVotes");
            }
            if(currRate == null) return 211;

            ServiceLogger.LOGGER.info("currNumVotes = " + currNumVotes);
            ServiceLogger.LOGGER.info("currRate = " + currRate);

            Integer newNumVotes = currNumVotes + 1;
            Float newRate = ((float) (currNumVotes * currRate) + requestModel.getRating()) / (float) newNumVotes;

            String SQL1 = "UPDATE ratings SET rating = ?, numVotes = ? WHERE movieId = ?";
            PreparedStatement ps1 = MovieService.getCon().prepareStatement(SQL1);
            ps1.setFloat(1, newRate);
            ps1.setInt(2, newNumVotes);
            ps1.setString(3, requestModel.getId());
            ServiceLogger.LOGGER.info("Attempting update: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update Successful");
            return 250;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to update ratings");
            e.printStackTrace();
            return 251;
        }
    }
}
