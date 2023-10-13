package org.example;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.file.FileAlreadyExistsException;

public class Main {
    public static void main(String[] args) throws SocketException, UnknownHostException {
        //Declare the variables
        String fileName = "burros.jpg";
        String server = "localhost";
        Integer chunkSize = 2000;
        int port = 2000;
        int myPort = 2001;
        String response = null;
        DatagramPacket p = new DatagramPacket(fileName.getBytes(), fileName.getBytes().length, InetAddress.getByName(server), port);
        try (        DatagramSocket s = new DatagramSocket(myPort);
                     ){
            //Send the name file to the server
            s.send(p);

            //Receive the response from the server
            p.setData(new byte[256]);
            p.setLength(256);
            s.receive(p);
            response = new String(p.getData(),0, p.getLength());
            System.out.println("[SERVER RESPONSE]:" + response);

            //We send the size of the chunk we want
            p.setData(String.valueOf(2000).getBytes());
            p.setLength(p.getData().length);
            s.send(p);

            //We wait for the number of chunks that will be received
            s.receive(p);
            Integer iterations = Integer.valueOf(new String(p.getData(), 0 , p.getLength())) + 1;
            System.out.println("I will receive " + iterations + " chunks");

            //Create the file
            File f = new File("./files/" + fileName);
            try{
                f.createNewFile();
            }catch(FileAlreadyExistsException e){
                System.out.println(e);
            }
            p.setData(new byte[chunkSize]);
            p.setLength(p.getData().length);
            FileOutputStream fos = new FileOutputStream(f);
            for (Integer i = 1; i < iterations-1; i++) {
                System.out.println("waiting for chunk " + i);
                s.receive(p);
                fos.write(p.getData());
            }

        }catch (SocketException e ){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}