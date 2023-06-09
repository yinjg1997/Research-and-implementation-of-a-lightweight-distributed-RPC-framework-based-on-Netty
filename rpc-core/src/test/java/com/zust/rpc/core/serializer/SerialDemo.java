package com.zust.rpc.core.serializer;

public class SerialDemo {

    public static void main(String[] args) {
        ISerializer iSerializer=new JavaSerializer();
       // ISerializer iSerializer=new XStreamSerializer();
      //  ISerializer iSerializer=new FastJsonSeriliazer();
       // ISerializer iSerializer=new HessianSerializer();

        User user=new User();// JVM内存中.  如何把内存中的对象进行网络传输.(实体)->字节序列
        user.setAge(18);
        user.setName("test");

        //原生实现
        byte[] bytes=iSerializer.serialize(user);
        System.out.println("byte.length:"+bytes.length);
        /**
         * json: byte.leng 23
         * xmlstream: byte.leng 198
         * hessian: byte.length: 50
         * java原生： byte.length: 92
         * protobuf: byte.length:7
         */
        System.out.println(new String(bytes));// byte长度 198   -> 23


        User user1=iSerializer.deserialize(bytes,User.class);
        System.out.println(user1);



    }
}
