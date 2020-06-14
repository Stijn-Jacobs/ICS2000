package me.stijn;

import com.google.gson.*;
import java.util.Arrays;
import java.util.Base64;

public class Main {

    public final static String AES_KEY = ""; //your aes key
    public final static String MAC = ""; //your mac address

    public static void main(String[] args){

        //printDevices();
        //generateMessage();
    }

    /**
     *  Generate a command to send to the api, to for example turn a switch or light on.
     */
    private static void generateMessage(){
        Message m = new Message();
        m.setFrameNumber(1);
        m.setMessageType(128); //128 = CONTROL_ENTITY
        m.setMacAddress(Bytes.macAddressToBytes(MAC));
        m.setMagicNumber();
        m.setEntityId(-1L); //change to id of item to switch on / off
        m.setData("{\"module\":{\"id\":<id>,\"function\":0,\"value\":1}}"); //value: 1 = on / 2 = off, also change the id
        m.setVersion();

        System.out.println(Bytes.bytesToHexString(m.toBytes()));
    }

    private static void printDevices(){
        JsonParser parser = new JsonParser();

        String get_devices = ""; //put your json response array from the get devices call here to get more details.
        JsonArray devices = parser.parse(get_devices).getAsJsonArray();

        for (JsonElement o : devices) {
            JsonObject obj = o.getAsJsonObject();
            System.out.println("decrypting ID: " + obj.get("id"));
            System.out.println("data: " + decrypt(obj.get("data").getAsString()) );
        }
    }

    private static String decrypt(String str){
        byte[] decoded = Base64.getDecoder().decode(str);
        byte[] arr = Arrays.copyOf(decoded, 16);
        String output = Cryptographer.decryptToString(Arrays.copyOfRange(decoded, 16, decoded.length), Bytes.hexStringToByteArray(AES_KEY), arr);
        return output;
    }


}

