package com.dongy.demo.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CopyFile {

    public static void main(String[] args) {
        System.out.println("这是什么东东");
    }

    /**
     * 直接缓冲区复制文件
     */
    public static void directCache() {
        try (FileChannel inChannel = FileChannel.open(Paths.get("/home", "/tiger/图片/a.txt"), StandardOpenOption.READ);
             FileChannel outChannel = FileChannel.open(Paths.get("2.txt"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE)) {

            MappedByteBuffer inBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            MappedByteBuffer outBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

            byte[] bytes = new byte[1];
            ByteBuffer buffer = inBuffer.get(bytes);
            outBuffer.put(bytes);
            inBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 非直接缓冲区复制文件
     */
    public static void nonDirectCache2() {
        try (FileChannel inChannel = FileChannel.open(Paths.get("/home", "/tiger/图片/a.txt"), StandardOpenOption.READ);
             FileChannel outChannel = FileChannel.open(Paths.get("2.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer buffer = ByteBuffer.allocate(512);
            while (inChannel.read(buffer) != -1) {
                // 切换读模式
                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 非缓存区copy文件
     */
    public static void nonDirectCache1() {

        try (FileInputStream inputStream = new FileInputStream("/home/tiger/图片/lufei.jpg");
             FileOutputStream outputStream = new FileOutputStream("lufei.jpg");
             FileChannel inChannel = inputStream.getChannel();
             FileChannel outChannel = outputStream.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(512);
            while (inChannel.read(buffer) != -1) {
                // 切换读模式
                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
