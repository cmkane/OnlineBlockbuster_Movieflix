package edu.uci.ics.kanec1.service.movies.resources;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import edu.uci.ics.kanec1.service.movies.core.DBUses;
import edu.uci.ics.kanec1.service.movies.models.*;
import edu.uci.ics.kanec1.service.movies.MovieService;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.models.TDMMovieModel;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import java.io.*;
import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ClientInfoStatus;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.json.*;
@Path("tmbd")
public class Readidlist{
    public static ArrayList<TDMMovieModel> movies = new ArrayList<>();
    public static int currTurn;

    @Path("readidlist/{turn}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    public Response getMovie(@Context HttpHeaders header, @PathParam("turn") Integer turn){
        File movie_list = new File("movie_ids_04_23_2019.json");
        int misscont = 0;
        int count = 0;
        currTurn = turn;
        try {
            BufferedReader br = new BufferedReader(new FileReader(movie_list));
            String line;
            ObjectMapper mapper = new ObjectMapper();
            while((line = br.readLine()) != null){
                if(count >= turn*45471 && count < (turn+1)*45471) {
                    try {
                        TDMMovieModel model = mapper.readValue(line, TDMMovieModel.class);
                        ServiceLogger.LOGGER.info(model.toString());
                        movies.add(model);
                    } catch(IOException e){
                        ++misscont;
                    }
                }
                count++;
            }
        }catch(Exception e){
            ServiceLogger.LOGGER.info(ExceptionUtils.exceptionStackTraceAsString(e));

        }
        ServiceLogger.LOGGER.info("Missed this many movies: "+misscont+"");

        return Response.status(Response.Status.OK).entity(movies.size()).build();
    }

    @Path("getallmovies")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getallmovies(@Context HttpHeaders headers){
        int count = 0;
        try{
            Client c = ClientBuilder.newClient();
            c.register(JacksonFeature.class);
            String TMDB = "";
            // for loop here
            int len = movies.size();
            int i;
            for(i = 0; i < len; i++) {
                TMDB = "https://api.themoviedb.org/3/movie/";
                TMDB += movies.get(i).getId()+"";
                //TMDB += "?api_key=616ed3739355afeac091e141bd96f9fe"; //gabrieljcx
                TMDB += "?api_key=4646b7003da5a5fbfd50b6e188b5b890"; //maplemarshy1
                //TMDB += "?api_key=9d93f3e68527b1b821f8d3ae9e023c2c"; //maplemarshy2
                //TMDB += "?api_key=6e40ad05695cd43358a25fd93025964c"; //maplemarshy3
                WebTarget target = c.target(TMDB);
                Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
                Response response = builder.get();
                try {
                    if(response.getStatus() == 200){
                        ServiceLogger.LOGGER.info("Success!");
                        String jsonText = response.readEntity(String.class);
                        ServiceLogger.LOGGER.info("JSON TEXT = " + jsonText + "DONE\n");
                        JSONObject obj = new JSONObject(jsonText);
                        int movie_id = obj.getInt("id");
                        //File movie_list = new File("\\tmdb\\"+movie_id+".json");
                        //BufferedReader br = new BufferedReader(new FileReader(movie_list));
                        PrintWriter writer = new PrintWriter("./"+Integer.toString(currTurn)+"/"+movie_id+".json", "UTF-8");

                        //BufferedWriter bw = new BufferedWriter(new FileWriter(movie_list));
                        writer.println(jsonText);
                        writer.close();
                        ServiceLogger.LOGGER.info(jsonText+="not retarded");
                    }else{
                        ServiceLogger.LOGGER.info("Different response: "+response.getStatus());
                    }
                    count++;
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch(Exception e2) {
                    e2.printStackTrace();
                }

            }
        }catch(Exception e){
            ServiceLogger.LOGGER.info(ExceptionUtils.exceptionStackTraceAsString(e));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(count).build();
        }
        return Response.status(Response.Status.OK).entity(count).build();
    }

    @Path("insert")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertTMBDMovie() {
        File dir = new File("./excellenceMovies/");
        File[] directoryListing = dir.listFiles();
        int count = 0;
        if (directoryListing != null) {
            for (File child : directoryListing) {
                // Do something with child
                try {
                    BufferedReader br = new BufferedReader(new FileReader(child));
                    String line;
                    while((line = br.readLine()) != null){
                        if(count >= 10000) break;
                        try {
                            JSONObject obj = new JSONObject(line);
                            String imbd_id = obj.getString("imdb_id");
                            String title = obj.getString("original_title");
                            String director = "";
                            Integer year = 0;
                            try {
                                String release_date = obj.getString("release_date");
                                year = Integer.parseInt(release_date.substring(0, 4));
                            } catch(Exception e2) {
                                ServiceLogger.LOGGER.info("Year wasn't there.");
                            }
                            String backdrop_path;
                            try {
                                backdrop_path = obj.getString("backdrop_path");
                            } catch(Exception e) {
                                backdrop_path = "";
                            }
                            Integer budget;
                            try {
                                budget = obj.getInt("budget");
                            } catch(Exception e) {
                                budget = 0;
                            }
                            String overview;
                            try {
                                overview = obj.getString("overview");
                            } catch(Exception e) {
                                overview = null;
                            }
                            String poster_path;
                            try {
                                poster_path = obj.getString("poster_path");
                            } catch(Exception e) {
                                poster_path = null;
                            }
                            Integer revenue;
                            try {
                                revenue = obj.getInt("revenue");
                            } catch(Exception e) {
                                revenue = null;
                            }
                            //Genres

                            GenreModel[] genres;
                            try{
                                JSONArray genreObjs = obj.getJSONArray("genres");
                                genres=new GenreModel[genreObjs.length()];
                                for (int i=0;i<genreObjs.length();i++){
                                    JSONObject genreObj = genreObjs.getJSONObject(i);
                                    genres[i]=new GenreModel(1,genreObj.getString("name"));
                                }
                            }catch (Exception e){
                                genres = new GenreModel[0];
                            }

                            AddPageRequestModel requestModel = new AddPageRequestModel(title, director, year, backdrop_path, budget, overview, poster_path, revenue, genres);
                            DBUses.addMovie(imbd_id, requestModel);
                            count++;
                        } catch(Exception err) {
                            ServiceLogger.LOGGER.info("Unable to insert data.");
                        }

                    }
                } catch(Exception e) {
                    ServiceLogger.LOGGER.info("No such file.");
                    e.printStackTrace();
                }
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error").build();
        }
        return Response.status(Response.Status.OK).build();
    }
}