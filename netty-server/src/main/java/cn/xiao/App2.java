package cn.xiao;

import com.unionpay.magpie.common.thread.NamedThreadFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

/**
 * Hello world!
 *
 */
public class App2
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        ExecutorService bossExecutor = Executors
                .newCachedThreadPool(new NamedThreadFactory("NioServer-Boss", true));
        ExecutorService workerExecutor = Executors
                .newCachedThreadPool(new NamedThreadFactory("NioServer-Worker", true));
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                bossExecutor, workerExecutor));


        final ExecutorService serviceExecutor = new ThreadPoolExecutor(
                10, 50,
                300, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new NamedThreadFactory(
                        "NioServer-Service-", true),
                new RejectedExecutionHandler() {
                    public void rejectedExecution(final Runnable r,
                                                  final ThreadPoolExecutor e) {
                        System.out.println("NioServer service executor reject request.");
                    }
                });

        final ChannelGroup channelGroup = new DefaultChannelGroup(
                "NioServer-ChannelGroup");

        final ChannelHandler handler1 = new FrameDecoder() {
            @Override
            protected Object decode(ChannelHandlerContext ctx,
                                    Channel channel, ChannelBuffer buf)
                    throws Exception {
                if (buf.readableBytes() < 4) {
                    return null;
                }
                buf.markReaderIndex();
                int length = buf.readInt();
                if (buf.readableBytes() < length) {
                    buf.resetReaderIndex();
                    return null;
                }

                ChannelBuffer frame = buf.readBytes(length);
                return frame;
            }
        };
        final ChannelHandler handler2 = new SimpleChannelHandler() {
            @Override
            public void messageReceived(ChannelHandlerContext ctx,
                                        MessageEvent e) throws Exception {
                final ChannelBuffer cb = (ChannelBuffer) e.getMessage();
                final Channel subChannel = e.getChannel();
                serviceExecutor.submit(new Runnable() {
                    public void run () {
                        ChannelBuffer respCb = ChannelBuffers.buffer(128);
                        respCb.writeInt(123);
                        subChannel.write(respCb);
                    }
                });
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx,
                                        ExceptionEvent e) throws Exception {
                System.out.println("exception happened, channel = " + e.getChannel()
                        + ", " + e.getCause());
            }

            @Override
            public void channelClosed(ChannelHandlerContext ctx,
                                      ChannelStateEvent e) throws Exception {
                super.channelClosed(ctx, e);
                channelGroup.remove(e.getChannel());
            }

            @Override
            public void channelOpen(ChannelHandlerContext ctx,
                                    ChannelStateEvent e) throws Exception {
                super.channelOpen(ctx, e);
                channelGroup.add(ctx.getChannel());
            }
        };


        /**
         *   handler1 : FrameDecoder { decode() }
         *   handler2 : SimpleChannelHandler { messageReceived(), exceptionCaught(), channelClosed(), channelOpen() }
         *
         *   bootstrap.setPipelineFactory
         *       <- ChannelPipelineFactory { getPipeline() }
         *                                       <- Chanels.pipeline(handler1, handler2)
         */

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(handler1, handler2);
            }
        });

        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.tcpNoDelay", false);
        bootstrap.setOption("child.keepAlive", true);

        Channel channel = bootstrap.bind(new InetSocketAddress(8080));

        channelGroup.add(channel);
    }
}
