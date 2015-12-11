package info.chinnews.instagram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;


/**
 * Created by Tsarevskiy
 */
public class FrontServer {

    private static Logger logger = LoggerFactory.getLogger(FrontServer.class.getClass());

    public static int PORT = 8000;

    public static void subscribe() {
        logger.info("Subscribing to the port: " + PORT);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Charset charset = Charset.forName("ISO-8859-1");
                CharsetEncoder encoder = charset.newEncoder();
                CharsetDecoder decoder = charset.newDecoder();

                ByteBuffer buffer = ByteBuffer.allocate(512);

                Selector selector = Selector.open();

                ServerSocketChannel server = ServerSocketChannel.open();
                server.socket().bind(new java.net.InetSocketAddress(PORT));

                server.configureBlocking(false);
                SelectionKey serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
                while (true) {
                    selector.select();
                    Set keys = selector.selectedKeys();

                    for (Iterator i = keys.iterator(); i.hasNext(); ) {
                        SelectionKey key = (SelectionKey) i.next();
                        logger.info("Received a key: " + key.toString());
                        i.remove();

                        if (key == serverKey) {
                            if (key.isAcceptable()) {
                                SocketChannel client = server.accept();
                                client.configureBlocking(false);
                                SelectionKey clientkey = client.register(selector, SelectionKey.OP_READ);
                                clientkey.attach(0);
                            }
                        } else {
                            SocketChannel client = (SocketChannel) key.channel();
                            if (!key.isReadable())
                                continue;
                            int bytesRead = client.read(buffer);
                            if (bytesRead == -1) {
                                key.cancel();
                                client.close();
                                continue;
                            }
                            buffer.flip();
                            String request = decoder.decode(buffer).toString();
                            logger.info(request);
                            buffer.clear();

                            int index = request.indexOf("hub.challenge");
                            if (index > 0) {
                                int startIndex = index + 14;
                                int endIndex = startIndex + 32;
                                String hub_challenge = request.substring(startIndex, endIndex);
                                logger.info("Calculated hub_challenge: " + hub_challenge);
                                client.write(encoder.encode(CharBuffer.wrap(hub_challenge)));
                            }
                            key.cancel();
                            client.close();

//                        if (request.trim().equals("quit")) {
//                            client.write(encoder.encode(CharBuffer.wrap("Bye.")));
//                            key.cancel();
//                            client.close();
//                        } else {
//                            int num = (Integer) key.attachment();
//                            String response = num + ": " + request.toUpperCase();
//                            client.write(encoder.encode(CharBuffer.wrap(response)));
//                            key.attach(num + 1);
//                        }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }
}