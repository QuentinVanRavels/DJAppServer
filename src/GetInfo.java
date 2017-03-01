/**
 * Created by Quentin Van Ravels on 28-Feb-17.
 */

//import com.mongodb.*;

import com.mongodb.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Iterator;


@Path("/info")
public class GetInfo {

    @Path("/songs")
    @GET
    @Produces("application/json")
    public String getSong() throws SQLException {

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

            if(json != "["){
                return json;
            }

            return "Error";
            // String json = "{ \"Title\":\"Knights of Cydonia\", \"Artist\":\"Muse\"}";
            // return json;
        }
    }


    @Path("/stats/{c}")
    @GET
    @Produces("application/json")
    public String getStat(@PathParam("c") int c) throws SQLException, UnknownHostException {

        String json = "[";

        MongoClient mongoClient = new MongoClient( "143.129.39.119" , 27017 );
        DB db = mongoClient.getDB( "NoSqlDb" );
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("topic", "songVote");// TODO: 01-Mar-17 CreatedDate is searchQuery.put("")
        //searchQuery.put("createDate", )
        DBCollection table = db.getCollection("MRB");
        DBCursor cursor = table.find(searchQuery);
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
            int mrb_ID = Integer.parseInt(cursor.curr().get("mrb_ID").toString());
            int song_ID = Integer.parseInt(cursor.curr().get("song_ID").toString());
            int vote = Integer.parseInt(cursor.curr().get("vote").toString());
            String time = cursor.curr().get("createdDate").toString();
        }

        return "Error";

    }

    @Path("{c}")
    @GET
    @Produces("application/json")
    public String getInput(@PathParam("c") String c){
        String input = c;
        String json = "{ \"Input\":\"" + c + "\"}";

        System.out.print(json);

        return json;
    }

    @Path("/yolo")
    @GET
    @Produces("application/json")
    public String getYolo(){

        return "[{ \"id\": 1, \"artist\": \"Red Hot Chilli Peppers\", \"song\": \"Californication\", \"year\": \"1999\"},{ \"id\": 2, \"artist\": \"Queen \", \"song\": \"Bohemian Rhapsody\", \"year\": \"1975\"},{ \"id\": 3, \"artist\": \"Nirvana\", \"song\": \"Smells like teen spirit\", \"year\": \"1991\"},{ \"id\": 4, \"artist\": \"Oasis\", \"song\": \"Wonderwall\", \"year\": \"1995\"},{ \"id\": 5, \"artist\": \"The Rolling Stones\", \"song\": \"You can't always get what you want\", \"year\": \"1969\"},{ \"id\": 6, \"artist\": \"Green Day\", \"song\": \"American Idiot\", \"year\": \"2004\"}]";
    }

}
