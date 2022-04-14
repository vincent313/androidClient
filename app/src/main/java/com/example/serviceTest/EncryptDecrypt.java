package com.example.serviceTest;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.commons.lang3.ArrayUtils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
When start application , this app will start as service
Server store client AES key, when client log in (RSA), server new this instance with client AES key.

So
1. Server dont need to generate AES key(get it from client)
2. Server only need DecryptRSA() function.
3. Server call rsaGenerateKeyPair()function when RsaPrivatekey is null.
4. Because server only need to decrypt message(which include client AES key)
*/
public class EncryptDecrypt {

    private static IvParameterSpec iv=new IvParameterSpec("aaaaaaaaaaaaaaaa".getBytes(StandardCharsets.UTF_8));
    private static byte[] AesKey ;
    private static final String AES_TYPE="AES/CBC/NoPadding";
    private static final int AES_Block_Size=16;
    private static String MineRsaPublickey =null;
    private static String MineRsaPrivatekey = null;
    private static String ServerPublickey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCM5JMdbJl2qn94xAiL/IsHr9v4ul0yWtxopPB9U67ezqHNra+2/7kw85nxaGifpmDo29Frhi9Dp14SYql71UdUl3+AswdFydETG6BhWVd7inM4eoLof/iFdGfaNtE0ufi2CfZalYLZKAVVve+jaUVSjaFjHcIUOk9TGFKKtUAZpwIDAQAB";

    protected static String getMyRSAKEY(){
        return MineRsaPublickey;
    }

    protected  static void setServerPublickey(String key){
        ServerPublickey=key;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected  static void initKeys() throws Exception {
        AesKey=aesGenerateKey();
        rsaGenerateKeyPair();
    }

    protected static byte[] aesGenerateKey() throws NoSuchAlgorithmException {
        SecureRandom sr=null;
        // Generate random instance, in SHA1PRNG algorithm not exist, using new secureRandom  (NativePRNG algorithm)
        try {
            sr=SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            sr=new SecureRandom();
        }
        //set seed
        //sr.setSeed(Calendar.getInstance().getTimeInMillis());

        // generate 128bit(16 byte) aes key

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128, sr);
            return keyGen.generateKey().getEncoded();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static String getAesKey(){
        return new String(Base64.getEncoder().encode(AesKey));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static String getiv(){
        return new String(Base64.getEncoder().encode(iv.getIV()));
    }

    private static byte[] padding(byte [] b) throws UnsupportedEncodingException {

        int padsize;
        //calculate how many bit need to pad
        if(b.length%AES_Block_Size==0){
            padsize=AES_Block_Size;
        }else{
            padsize=(AES_Block_Size-(b.length%AES_Block_Size));
        }
        // generate padding array
        byte[] padinfo=new byte[padsize];
        for(int i=0;i<padsize;i++){
            padinfo[i]=inttobyte(padsize);
        }
        //combine two arrary
        byte [] combine =(byte[]) ArrayUtils.addAll(b,padinfo);

        return combine;
    }

    private static String unPadding(byte[] b){
        int index= (b.length-b[(b.length-1)]);
        byte [] a= Arrays.copyOfRange(b,0,index);
        String s= new String(a);
        return s;
    }

    private static byte inttobyte (int x){
        return (byte)x;
    }

    private static int bytetoint(byte b){
        return b&0xFF;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static String aesEncry(String s) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        byte[] byteContent = s.getBytes("utf-8");
        byteContent=padding(byteContent);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec,iv);
        return new String(Base64.getEncoder().encode(cipher.doFinal(byteContent)));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static String aesDecrypt(String s) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");


        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec,iv);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }


        byte[] result = cipher.doFinal(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)));

        return unPadding(result);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static String aesEncry(String s,String key) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        byte[] byteContent = s.getBytes("utf-8");
        byteContent=padding(byteContent);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec,iv);
        return new String(Base64.getEncoder().encode(cipher.doFinal(byteContent)));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static String aesDecrypt(String s,String key) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");


        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec,iv);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        byte[] result = cipher.doFinal(Base64.getDecoder().decode(s.getBytes()));

        return unPadding(result);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void rsaGenerateKeyPair() throws Exception {
          //KeyPairGenerator instance use for generate public and private key
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024,new SecureRandom());
            //generate key pair and store in key pair
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();//得到私钥
            PublicKey publicKey = keyPair.getPublic();//得到公钥
            //get public key
            String publicKeyString=new String(Base64.getEncoder().encode(publicKey.getEncoded()));
            //get private key
            String privateKeyString=new String(Base64.getEncoder().encode(privateKey.getEncoded()));
            MineRsaPublickey=publicKeyString;
            MineRsaPrivatekey=privateKeyString;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String rsaEncrypt(String str) throws Exception {


        //get string public key in byte[]
        byte[] decodedPublicKey = Base64.getDecoder().decode(ServerPublickey);

        // generate public key instance
        RSAPublicKey pubKey = (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));

        //generate cipher instance(RSA mode)
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(1, pubKey);
        //get RSA encrypted byte[] file
        byte[] outBytes = cipher.doFinal(str.getBytes("UTF-8"));



        //encode byte[] to string(BASE64)
       // Log.d("tag",str);
        String outStr =new String(Base64.getEncoder().encode(outBytes));
        return outStr;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String rsaEncrypt(String str,String friendPublicKey) throws Exception {


        //get string public key in byte[]
        byte[] decodedPublicKey = Base64.getDecoder().decode(friendPublicKey);

        // generate public key instance
        RSAPublicKey pubKey = (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));

        //generate cipher instance(RSA mode)
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(1, pubKey);
        //get RSA encrypted byte[] file
        byte[] outBytes = cipher.doFinal(str.getBytes("UTF-8"));



        //encode byte[] to string(BASE64)

        String outStr =new String(Base64.getEncoder().encode(outBytes));
        return outStr;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static String rsaDecrypt(String str) throws Exception {


        // all encrypted message are encode by Base64, so decoder first
        byte [] input = Base64.getDecoder().decode(str);
        // get private key in byte[]
        byte[] privateKeyInBytes=Base64.getDecoder().decode(MineRsaPrivatekey);
        //generate key instance
        PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyInBytes));
        //generate cipher
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, priKey);
        //decrypt , get plain byte[] , trans to String
        String outStr = new String(cipher.doFinal(input));
        return outStr;
    }

}
