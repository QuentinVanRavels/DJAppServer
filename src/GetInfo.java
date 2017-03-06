/**
 * Created by Quentin Van Ravels on 28-Feb-17.
 */


import com.mongodb.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;


@CrossOrigin
@Path("/info")
public class GetInfo {

    @Path("/songs")
    @GET
    @Produces("application/json")
    public String getSong() throws SQLException, ClassNotFoundException {

        Statement stmt = null;
        String json = "[";

        try {
            Class.forName("com.mysql.jdbc.Driver");

           // String url = "jdbc:mysql://localhost:3306?useSSL=false";
            String url = "jdbc:mysql://143.129.39.117:3306?useSSL=false";
            Connection conn = DriverManager.getConnection(url, "Dries", "password");

            String query = "select id, artist, song, year from sqldb.Track";

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String artist = rs.getString("artist");
                String song = rs.getString("song");
                int year = rs.getInt("year");
                json = json + "{ \"id\": " + id + ", \"artist\": \"" + artist + "\", \"song\": \"" + song + "\", \"year\": \"" + year + "\"},";
            }

            json = json.substring(0,json.length()-1) + "]";

        } catch (SQLException e) {
            System.out.print(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
            if (!json.equals("[")) {
                return json;
            }
            return "Error";
            // String json = "{ \"Title\":\"Knights of Cydonia\", \"Artist\":\"Muse\"}";
            // return json;
    }

    @Path("/stats")
    @GET
    @Produces("application/json")
    public String getStats() throws SQLException, ClassNotFoundException {

        Statement stmt = null;
        String json = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");

            // String url = "jdbc:mysql://localhost:3306?useSSL=false";
            String url = "jdbc:mysql://143.129.39.117:3306?useSSL=false";
            Connection conn = DriverManager.getConnection(url, "Dries", "password");

            String query = "select id_track, likes, dislikes from sqldb.Play";

            stmt = conn.createStatement();

            boolean first = true;
            int id_track_high = 0;
            int id_track_low = 0;
            double likes_high = 0;
            double dislikes_high = 0;

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id_track = rs.getInt("id_track");
                double likes = rs.getDouble("likes");
                double dislikes = rs.getDouble("dislikes");

                if(first){
                    likes_high = likes;
                    dislikes_high = dislikes;
                    id_track_high = id_track;
                    id_track_low = id_track;
                    first = false;
                }

                if(likes > likes_high) {
                    likes_high = likes;
                    id_track_high = id_track;
                }
                if(dislikes > dislikes_high){
                    dislikes_high = likes;
                    id_track_low = id_track;
                }
            }

            json = json + "{\"mostpopularsong\": " + id_track_high + ", \"leastpopularsong\": " + id_track_low + ", ";

            query = "SELECT id FROM sqldb.MRB ORDER BY votes DESC";

            stmt = conn.createStatement();

            rs = stmt.executeQuery(query);

            rs.next();

            int mrb_ID = rs.getInt("id");

            json = json + "\"mostactivevoter\": " + mrb_ID + "}";

       } catch (SQLException e) {
            System.out.print(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
            if(!json.equals("")){
                return json;
            }

            return "Error";
            // String json = "{ \"Title\":\"Knights of Cydonia\", \"Artist\":\"Muse\"}";
            // return json;
    }


    @Path("/stats/{c}")
    @GET
    @Produces("application/json")
    public String getStatSong(@PathParam("c") int c) throws SQLException, UnknownHostException, ClassNotFoundException {

        //get start of song
        Statement stmt = null;

        Class.forName("com.mysql.jdbc.Driver");

        String url = "jdbc:mysql://143.129.39.117:3306?useSSL=false";
        Connection conn = DriverManager.getConnection(url, "Dries", "password");

        String query = "select time from sqldb.Play where id_track =" + c;

        stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(query);

        rs.next();

        Timestamp start = rs.getTimestamp("id_track");

        //nosql part

        MongoClient mongoClient = new MongoClient( "143.129.39.119" , 27017 );
        @SuppressWarnings("deprecation")
        DB db = mongoClient.getDB( "NoSqlDb" );
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("topic", "songVote");// TODO: 01-Mar-17 CreatedDate is searchQuery.put("")
        searchQuery.put("song_ID",c);
        DBCollection table = db.getCollection("MRB");
        DBCursor cursor = table.find(searchQuery);

        ArrayList<Timestamp> likes = new ArrayList<Timestamp>();
        ArrayList<Timestamp> dislikes = new ArrayList<Timestamp>();

        while (cursor.hasNext()) {
            System.out.println(cursor.next());
            int vote = Integer.parseInt(cursor.curr().get("vote").toString());
            Timestamp time = (Timestamp) cursor.curr().get("createdDate");


            if(vote == 1){
                likes.add(time);
            }else{
                dislikes.add(time);
            }
        }

        Collections.sort(likes);
        Collections.sort(dislikes);

        Timestamp frame1 = start;
        Timestamp frame2 = start;
        Timestamp frame3 = start;
        Timestamp frame4 = start;
        Timestamp frame5 = start;

        int frame1likes = 0;
        int frame2likes = 0;
        int frame3likes = 0;
        int frame4likes = 0;

        int frame1dislikes = 0;
        int frame2dislikes = 0;
        int frame3dislikes = 0;
        int frame4dislikes = 0;

        frame2.setTime(frame2.getTime() + 1*60*1000);
        frame3.setTime(frame3.getTime() + 2*60*1000);
        frame4.setTime(frame4.getTime() + 3*60*1000);
        frame5.setTime(frame5.getTime() + 4*60*1000);

        for(int i = 0; i < likes.size(); i++){
            if(likes.get(i).after(frame1) && likes.get(i).before(frame2)){
                frame1likes++;
            }else if(likes.get(i).after(frame2) && likes.get(i).before(frame3)){
                frame2likes++;
            }else if(likes.get(i).after(frame3) && likes.get(i).before(frame4)){
                frame3likes++;
            }else if(likes.get(i).after(frame4) && likes.get(i).before(frame5)){
                frame4likes++;
            }
        }

        for(int i = 0; i < dislikes.size(); i++){
            if(dislikes.get(i).after(frame1) && dislikes.get(i).before(frame2)){
                frame1dislikes++;
            }else if(dislikes.get(i).after(frame2) && dislikes.get(i).before(frame3)){
                frame2dislikes++;
            }else if(dislikes.get(i).after(frame3) && dislikes.get(i).before(frame4)){
                frame3dislikes++;
            }else if(dislikes.get(i).after(frame4) && dislikes.get(i).before(frame5)){
                frame4dislikes++;
            }
        }

        String json = "[{ \"frame\": 1, \"likes\": " + frame1likes + ", \"dislikes\": "+ frame1dislikes + "}," +
                      "{ \"frame\": 2, \"likes\": " + frame2likes + ", \"dislikes\": "+ frame2dislikes + "}," +
                      "{ \"frame\": 3, \"likes\": " + frame3likes + ", \"dislikes\": "+ frame3dislikes + "}," +
                      "{ \"frame\": 4, \"likes\": " + frame4likes + ", \"dislikes\": "+ frame4dislikes + "}]";

        if(!json.equals(null)){
            return json;
        }

        return "Error or no data";
    }

    @Path("{c}")
    @GET
    @Produces("application/json")
    public String getInput(@PathParam("c") String c){
        String json = "{ \"Input\":\"" + c + "\"}";

        System.out.print(json);

        return json;
    }

    @Path("/yolo")
    @GET
    @Produces("application/json")
    public String getYolo(){

        return "[{ \"id\": 1, \"artist\": \"Red Hot Chilli Peppers\", \"song\": \"Californication\", \"year\": \"1999\"},{ \"id\": 2, \"artist\": \"Queen \", \"song\": \"Bohemian Rhapsody\", \"year\": \"1975\"},{ \"id\": 3, \"artist\": \"Nirvana\", \"song\": \"Smells like teen spirit\", \"year\": \"1991\"},{ \"id\": 4, \"artist\": \"Oasis\", \"song\": \"Wonderwall\", \"year\": \"1995\"},{ \"id\": 5, \"artist\": \"The Rolling Stones\", \"song\": \"You can't always get what you want\", \"year\": \"1969\"},{ \"id\": 6, \"artist\": \"Green Day\", \"song\": \"American Idiot\", \"year\": \"2004\"}]}";
    }

}
