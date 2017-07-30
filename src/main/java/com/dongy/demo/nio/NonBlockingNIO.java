package com.dongy.demo.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 * 非阻塞NIO
 */
public class NonBlockingNIO {

    /**
     * 客户端
     */
    @Test
    public void client() {
        // 1 获取通道
        try (SocketChannel channel = SocketChannel
                .open(new InetSocketAddress("192.168.31.110", 10010))) {
            // 2 切换非阻塞模式
            channel.configureBlocking(false);
            // 3 分配制定大小的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 4 发送数据给服务端
            buffer.put(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .getBytes(StandardCharsets.UTF_8));
            // 切换读模式
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 服务端
     */
    @Test
    public void server() {

        // 获取通道
        try (ServerSocketChannel channel = ServerSocketChannel.open()) {
            channel.configureBlocking(false);

            // 绑定连接
            channel.bind(new InetSocketAddress(10010));

            // 获取选择器
            Selector selector = Selector.open();

            // 将通道注册到选择器上, 并指定为监听事件
            channel.register(selector, SelectionKey.OP_ACCEPT);

            // 轮询式的获取选择器上已经准备就绪的事件
            while (selector.select() > 0) {
                // 获取当前选择器中所有注册的已就绪的监听事件选择键
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {

                    SelectionKey key = iterator.next();

                    // 判断事件类型
                    if (key.isAcceptable()) {
                        // 获取客户端连接
                        SocketChannel accept = channel.accept();
                        // 切换非阻塞模式
                        accept.configureBlocking(false);
                        // 将该通道注册到选择器
                        accept.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        // 获取当前选择器上读就绪状态的通道
                        SocketChannel readChannel = (SocketChannel) key.channel();
                        // 读取数据
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        int length = 0;
                        while ((length = readChannel.read(buffer)) > 0) {
                            buffer.flip();
                            System.out.println(new String(buffer.array(), 0, length, StandardCharsets.UTF_8));
                            buffer.clear();
                        }
                    }

                    // 取消选择键
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
