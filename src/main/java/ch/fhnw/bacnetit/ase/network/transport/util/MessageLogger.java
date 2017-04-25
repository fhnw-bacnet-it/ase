package ch.fhnw.bacnetit.ase.network.transport.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import ch.fhnw.bacnetit.ase.encoding.TPDU;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class MessageLogger extends ChannelDuplexHandler {

    private final OutputStreamWriter writer;
    private final static Random random = new Random();

    public MessageLogger() throws IOException {
        this(generateFileName());
    }

    public MessageLogger(final String logFilePath) throws IOException {
        writer = new OutputStreamWriter(new FileOutputStream(logFilePath),
                StandardCharsets.UTF_8);
        System.out.println("Created new message log file: " + logFilePath);
    }

    private static String generateFileName() {
        return "inner-channel-" + Integer.toHexString(random.nextInt())
                + ".log";
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx)
            throws Exception {
        writer.flush();
        writer.close();
        System.out.println("inner writer closed");
        super.channelUnregistered(ctx);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg,
            final ChannelPromise promise) throws Exception {
        if (msg instanceof TPDU) { // record TPDU
            final TPDU parsedMsg = ((TPDU) msg);
            final String message = parsedMsg.getSourceEID().getIdentifier()
                    + "," + parsedMsg.getDestinationEID().getIdentifier() + ","
                    + parsedMsg.getInvokeId();
            writer.write("out;" + System.currentTimeMillis() + ";" + message
                    + ";t\n");
            writer.flush();
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg)
            throws Exception {
        if (msg instanceof TPDU) { // record TPDU
            final TPDU parsedMsg = ((TPDU) msg);
            final String message = parsedMsg.getSourceEID().getIdentifier()
                    + "," + parsedMsg.getDestinationEID().getIdentifier() + ","
                    + parsedMsg.getInvokeId();
            writer.write("in;" + System.currentTimeMillis() + ";" + message
                    + ";t\n");
            writer.flush();
        }
        super.channelRead(ctx, msg);
    }

}
