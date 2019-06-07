package dmeo;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

public class MyServer {
    private static final int BUF_SIZE = 9999;

    private ServerSocketChannel servSocketChannel = null;
    private Selector selector = null;


    public static void main(String[] args) {


        try {
            new MyServer().startServer("20000");
        } catch (BindException e) {
            e.printStackTrace();
            System.out.println("The Port Is In Use!");
        }


    }
    public void startServer(String port) throws BindException {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while (nias.hasMoreElements()) {
                    InetAddress ia = (InetAddress) nias.nextElement();
                    if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
                       // log.info("Current IP:" + ia);
                        System.out.println("Current IP:" + ia);

                    }
                }
            }
        } catch (SocketException e) {
            //log.error("IP Address Fetch Failed!");
            System.out.println("IP Address Fetch Failed!");
        }
        try {
            int p = Integer.parseInt(port);
            if(p > 0 && p < 65535){
               // log.info("Port:" + p);
                System.out.println("Port:" + p);
            }

            servSocketChannel = ServerSocketChannel.open();
            // 设置为非阻塞
            servSocketChannel.configureBlocking(false);
            // 绑定端口
            servSocketChannel.socket().bind(new InetSocketAddress(p));

            selector = Selector.open();
            // 注册监听事件
            servSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            listen();
        } catch (IOException e) {
            //log.error("The Port Is In Use!");
            System.out.println("The Port Is In Use!");
        }
    }
    private void listen() {
       // log.info("Is Listening!");
        System.out.println("Is Listening!");
        while (true) {
            try {
                //获得selector中选中项的迭代器
                selector.select();
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                while (((Iterator) iter).hasNext()) {
                    SelectionKey key = iter.next();

                        if (key.isValid() && key.isAcceptable()) {
                            // log.info("The Client Was Successfully Connected!");
                            System.out.println("The Client Was Successfully Connected!");
                            handleAccept(key);
                        }
                       else if (key.isValid() && key.isReadable()) {
                            handleRead(key);
                        }
                        else if (key.isValid() && key.isWritable()) {
                            //log.info("Send Messages To The Client!");
                            System.out.println("The Port Is In Use!");
                            handleWrite(key);
                        }
                        else if (key.isValid() && key.isConnectable()) {
                            System.out.println("isConnectable = true");
                        }
                        else{
                            System.out.println("ddd");
                        }

                    iter.remove();
                }
            } catch (IOException e) {
               // log.error("Read Error!");
                System.out.println("Error!");
            }
        }
    }
    //连接客户端
    public static void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);
        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
    }

    //读客户端发来的信息
    public static void handleRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            //传输数据
            ByteBuffer buf = (ByteBuffer) key.attachment();

            ByteBuffer writeBuffer = ByteBuffer.allocateDirect(1024);



            int bytesRead = sc.read(buf);


            if (bytesRead > 0) {
               // log.info("Read The Data From The Client!");
                System.out.println("read.....");
                buf.flip();
                byte[] bytes = new byte[bytesRead];
                buf.get(bytes, 0, bytesRead);
                String str = new String(bytes);
                System.out.println(str);
                buf.clear();
            }
            else{
                System.out.println("close");
                sc.close();

            }
        }
            catch (Exception e) {
            //log.error("Read Error!");
                System.out.println("Read Error!");
            key.cancel();
            sc.close();
        }

    }

    public static void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.put("Client Return".getBytes());
        buf.flip();
        SocketChannel sc = (SocketChannel) key.channel();
        while (buf.hasRemaining()) {
            sc.write(buf);
        }
        buf.compact();
    }

}
