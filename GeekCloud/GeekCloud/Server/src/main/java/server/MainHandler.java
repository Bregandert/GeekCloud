package server;

import common.FileList;
import common.FileMessage;
import common.FileRequest;
import common.ListRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class MainHandler extends ChannelInboundHandlerAdapter {

    private String nickname;

    public MainHandler(String nickname){
        this.nickname=nickname;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;
            if (Files.exists(Paths.get("ServerStorage/" + fr.getFilename()))) {
                FileMessage fm = new FileMessage(Paths.get("ServerStorage/" + fr.getFilename()));
                System.out.println("файл отправлен");
                ctx.writeAndFlush(fm);
            }
        }
        if (msg instanceof FileMessage) {
            FileMessage fm = (FileMessage) msg;
            if (!Files.exists(Paths.get("ServerStorage/" + fm.getFilename()))) {
                Files.write(Paths.get("ServerStorage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                System.out.println("Файл загружен на сервер");

            }
        }
        if (msg instanceof ListRequest) {
            ArrayList<String> serverList=new ArrayList<String>();
            Files.list(Paths.get("ServerStorage"))
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.getFileName().toString())
                    .forEach(o -> serverList.add(o));
            FileList fl = new FileList(serverList);
            System.out.println("Список файлов отправлен");
            ctx.writeAndFlush(fl);
        }

        }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
