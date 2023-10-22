package com.hudong.study.netty.nettynio.decode;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/10/17 23:04
 */
class UserInfoTest {

    @Test
    public void testSerializeUserInfo() throws IOException {
        UserInfo info = new UserInfo();
        info.buildUserID(100).buildUserName("welcome to netty");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(info);
        os.flush();
        os.close();
        byte[] b = bos.toByteArray();
        System.out.println("The jdk serializable length is : " + b.length);
        bos.close();
        System.out.println("---------------------------------------------");
        System.out.println("The byte array serializable length is : " + info.codeC().length);
    }

    @Test
    public void testSerializeUserInfoByPerformance() throws IOException {
        UserInfo info = new UserInfo();
        int loop = 1000000;
        info.buildUserID(100).buildUserName("welcome to netty");
        long startTime = System.currentTimeMillis();
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        for (int i = 0; i < loop; i++) {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(info);
            os.flush();
            os.close();
            byte[] b = bos.toByteArray();
            bos.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("The jdk serializable cost time is : " + (endTime - startTime) + "ms");
        System.out.println("---------------------------------------------");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            info.codeC();
        }
        endTime = System.currentTimeMillis();
        System.out.println("The byte array serializable cost time is : " + (endTime - startTime) + "ms");
    }
}