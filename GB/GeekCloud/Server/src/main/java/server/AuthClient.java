package server;
import common.AuthMessage;
import common.AuthOk;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.sql.*;

public class AuthClient extends ChannelInboundHandlerAdapter {

    private boolean authorised=false;

    private static Connection connection;
    private static Statement statement;

    public AuthClient() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:Clients.db");

            statement=connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg==null){
            return;
        }
        if (!authorised){
            if(msg instanceof AuthMessage){
                AuthMessage am=(AuthMessage) msg;
                if (getNickname(am.getLogin(),am.getPassword())!=null){
                    String nickname = getNickname(am.getLogin(),am.getPassword());
                    authorised=true;
                    AuthOk ao=new AuthOk(nickname);
                    ctx.writeAndFlush(ao).await();
                    ctx.pipeline().addLast(new MainHandler(nickname));
                    System.out.println(getNickname(am.getLogin(),am.getPassword()+" connected"));
                    connection.close();

                } else {
                    ctx.fireChannelRead(msg);
                }
            }
        }
    }



    synchronized static String getNickname(String login, String password) {
        String query = String.format("select nickname from users where login='%s' and password='%s'", login, password);
        try (ResultSet set = statement.executeQuery(query)) {
            if (set.next())
                return set.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    synchronized static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
