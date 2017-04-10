package ch.fhnw.bacnetit.stack.network.transport.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ByteBufLogger extends ChannelDuplexHandler {

    private final OutputStreamWriter writer;
    private final static Random random = new Random();

    public ByteBufLogger() throws IOException {
        this(generateFileName());
    }

    public ByteBufLogger(final String logFilePath) throws IOException {
        writer = new OutputStreamWriter(new FileOutputStream(logFilePath),
                StandardCharsets.UTF_8);
        // writer = new FileWriter(logFilePath);
        System.out.println("Created new binary log file: " + logFilePath);
    }

    private static String generateFileName() {
        return "outer-channel-" + Integer.toHexString(random.nextInt())
                + ".log";
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx)
            throws Exception {
        writer.flush();
        writer.close();
        System.out.println("outer writer closed");
        super.channelUnregistered(ctx);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg,
            final ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            System.out.println("Outer channel write: " + msg);
            final ByteBuf byteBuf = ((ByteBuf) msg);
            final byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, bytes);
            writer.write("out;" + System.currentTimeMillis() + ";"
                    + Arrays.toString(bytes) + "\n");
            writer.flush();
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg)
            throws Exception {
        if (msg instanceof ByteBuf) {
            System.out.println("Outer channel read: " + msg);
            final ByteBuf byteBuf = ((ByteBuf) msg);
            final byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, bytes);
            writer.write("in;" + System.currentTimeMillis() + ";"
                    + Arrays.toString(bytes) + "\n");
            writer.flush();
        }
        super.channelRead(ctx, msg);
    }

}
