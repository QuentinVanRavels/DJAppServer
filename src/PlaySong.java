/**
 * Created by Quentin Van Ravels on 01-Mar-17.
 */

import javax.ws.rs.*;
import java.sql.*;

@Path("/play")
public class PlaySong {

    @Path("{c}")
    @GET
    public String getInput(@PathParam("c") int c) throws SQLException {
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

             String url = "jdbc:mysql://localhost:3306?useSSL=false";
            //String url = "jdbc:mysql://143.129.39.117:3306?useSSL=false";
            Connection conn = DriverManager.getConnection(url, "root", "root");

            String query = "select artist, song from test.songs where id = " + c;

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);

            MqttJavaApplication app = new MqttJavaApplication();
           // app.sendMessage(c,rs.getString("song"),rs.getString("artist"),rs.getString("album"),rs.getInt("year"));
            app.sendMessage(c,rs.getString("song"),rs.getString("artist"),"album",1995);

        } catch (SQLException e) {
            System.out.print(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }

        return "Have a nice day!";
    }
}
