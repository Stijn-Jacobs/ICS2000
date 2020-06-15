package me.stijn;

import com.google.gson.*;
import java.util.Arrays;
import java.util.Base64;

public class Main {

    public final static String AES_KEY = ""; //your aes key
    public final static String MAC = ""; //your mac address

    public static void main(String[] args){

        //printDevices();
        //generateOnMessage();
        //generateDimMessage(-1L, 15);
    }

    /**
     *  Generate a command to send to the api, to for example turn a switch or light on.
     */
    private static void generateOnMessage(Long entitiyId){
        Message m = new Message();
        m.setFrameNumber(1);
        m.setMessageType(128); //128 = CONTROL_ENTITY
        m.setMacAddress(Bytes.macAddressToBytes(MAC));
        m.setMagicNumber();
        m.setEntityId(entitiyId); //change to id of item to switch on / off
        m.setData("{\"module\":{\"id\":" + entitiyId + ",\"function\":0,\"value\":1}}"); //value: 1 = on / 2 = off, also change the id
        m.setVersion();

        System.out.println(Bytes.bytesToHexString(m.toBytes()));
    }

    /**
     * Generate dim command, WARNING: DIM LEVEL NEEDS TO BE BETWEEN 0 AND 15
     * @param entitiyId
     * @param level
     */
    private static void generateDimMessage(Long entitiyId, Integer level){
        Message m = new Message();
        m.setFrameNumber(1);
        m.setMessageType(128); //128 = CONTROL_ENTITY
        m.setMacAddress(Bytes.macAddressToBytes(MAC));
        m.setMagicNumber();
        m.setEntityId(entitiyId); //change to id of item to switch on / off
        m.setData("{\"module\":{\"id\":" + entitiyId + ",\"function\":1,\"value\":" + level + "}}"); //level needs to be 1 / 15
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

