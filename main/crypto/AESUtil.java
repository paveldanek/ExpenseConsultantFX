package crypto;

import entities.Transaction;
import entities.TransactionList;
import main_logic.Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This was taken from GitHub, where one is forwarded from a teaching course
 * website, hosted by Baeldung company. The link to it is
 * https://www.baeldung.com/java-aes-encryption-decryption. THANK YOU!!!
 *
 * Source:
 * https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-security-algorithms
 */
public class AESUtil {
    /*
    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }
    */

    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /*
    public static void encryptFile(String algorithm, SecretKey key, IvParameterSpec iv, File inputFile, File outputFile)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static void decryptFile(String algorithm, SecretKey key, IvParameterSpec iv, File encryptedFile,
                                   File decryptedFile) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(encryptedFile);
        FileOutputStream outputStream = new FileOutputStream(decryptedFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] output = cipher.doFinal();
        if (output != null) {
            outputStream.write(output);
        }
        inputStream.close();
        outputStream.close();
    }

    public static SealedObject encryptObject(String algorithm, Serializable object, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IOException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        SealedObject sealedObject = new SealedObject(object, cipher);
        return sealedObject;
    }

    public static Serializable decryptObject(String algorithm, SealedObject sealedObject, SecretKey key,
                                             IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        Serializable unsealObject = (Serializable) sealedObject.getObject(cipher);
        return unsealObject;
    }
    */

    public static String encryptPasswordBased(String plainText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }

    public static String decryptPasswordBased(String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
    }

    private static String tListIntoString(TransactionList tList) {
        String output = "";
        Request r = Request.instance();
        for (int i = 0; i < tList.size(); i++) {
            r.reset();
            r.setTFields(tList.get(i));
            output += r.getTDate()+(char)0+r.getTRef()+(char)0+r.getTDesc()+(char)0+r.getTMemo()+
                    (char)0+r.getTAmount()+(char)0+r.getTCat()+(char)1;
        }
        return output;
    }

    private static TransactionList stringIntoTList(String str) {
        TransactionList tList = new TransactionList();
        String singleT;
        String[] tArray = new String[6];
        int i;
        while (str.length()>0) {
            i = 0;
            while (str.charAt(i) != (char) 1) { i++; }
            singleT = str.substring(0, i);
            str = str.substring(i+1);
            tArray = singleT.split(String.valueOf((char) 0));
            Transaction t = new Transaction(Transaction.returnCalendarFromYYYYMMDD(tArray[0]),
                    tArray[1], tArray[2], tArray[3], Double.parseDouble(tArray[4]), tArray[5]);
            tList.add(t);
        }
        return tList;
    }

    public static String encryptHistory(String plainText, String password) throws NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        String output = "";
        String salt = "";
        for (int i = 0; i < 5; i++) {
            salt += (char) ((int) (Math.random() * 57 + 65));
        }
        SecretKey SK = getKeyFromPassword(password, salt);
        IvParameterSpec IV = generateIv();
        byte[] IVbytes = IV.getIV();
        String IVstring = "";
        for (int i = 0; i < IVbytes.length; i++) {
            IVstring += (char) (IVbytes[i] & 0xff);
        }
        output += salt;
        output += encryptPasswordBased(plainText, SK, IV);
        output += IVstring;
        return output;
    }

    public static String decryptHistory(String cipher, String password) throws NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        String salt = cipher.substring(0,5);
        String IVstring = cipher.substring(cipher.length()-16, cipher.length());
        cipher = cipher.substring(5, cipher.length()-16);
        SecretKey SK = getKeyFromPassword(password, salt);
        byte[] IVbytes = new byte[IVstring.length()];
        for (int i = 0; i < IVstring.length(); i++) {
            IVbytes[i] = (byte) IVstring.charAt(i);
        }
        IvParameterSpec IV = new IvParameterSpec(IVbytes);
        String output = decryptPasswordBased(cipher, SK, IV);
        return output;
    }

    /*
	public static void main(String[] args)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        String input = "TEST1 TEST2 TEST3 TEST4";
        String CT = "";
        String PT = "";
        // --------------------------------------------------
        // TEST 1
		for (int i = 1; i <= 5; i++) {
			System.out.println("Attempt # " + i + ":");
			SecretKey SK = getKeyFromPassword("P@55word", "12345");
			IvParameterSpec IV = generateIv();
			CT = encryptPasswordBased(input, SK, IV);

			PT = decryptPasswordBased(CT, SK, IV);
			System.out.println("Original text   = " + input);
			System.out.println("Ciphered text   = " + CT);
			System.out.println("Deciphered text = " + PT);
            System.out.println("Secret key = " + SK + " from password = P@55word and salt = 12345");
            System.out.println("IV = " + IV.getIV() + "\n");
		}
		System.out.println("THANK YOU!\n\n");
		//----------------------------------------------------
        // TEST 2
        String password = "P@55word";
        String salt = "";
        for (int i = 0; i < 5; i++) {
            salt += (char) ((int) (Math.random()*57+65));
        }
        System.out.println();
        SecretKey SK = getKeyFromPassword(password, salt);
        IvParameterSpec IV = generateIv();
        byte[] IVbytes = IV.getIV();
        String IVstring = "";
        for (int i = 0; i < IVbytes.length; i++) {
            IVstring += (char) (IVbytes[i] & 0xff);
        }
        System.out.println("Input = " + input + "\nPassword = " + password +
                "\nSalt = " + salt + "\nIV string[" + IVstring.length()+ "] = " + IVstring + "\n\n");
        CT = encryptPasswordBased(input, SK, IV);
        System.out.println("Ciphred input = " + CT);
        // working with: password (fetch user's password), salt (always 5 chars),
        // IVstring (divided from the ciper by agreed-upon character (e.g. '.'))
        SK = getKeyFromPassword(password, salt);
        IVbytes = new byte[IVstring.length()];
        for (int i = 0; i < IVstring.length(); i++) {
            IVbytes[i] = (byte) IVstring.charAt(i);
        }
        IV = new IvParameterSpec(IVbytes);
        PT = decryptPasswordBased(CT, SK, IV);
        System.out.println("DeCiphred input = " + PT + "\n");
        // -------------------------------------------------------
        // TEST 3
        Transaction tr1 = new Transaction(Transaction.returnCalendarFromOFX("20230317000000"),
                "ABC123DEF456", "Trans1", "memo1", 15, "Car Insurance");
        Transaction tr2 = new Transaction(Transaction.returnCalendarFromOFX("20230318000000"),
                "qwe987rtz654", "Trans 2", "no memo!!!", -84.7, "<OTHER>");
        TransactionList tl = new TransactionList();
        tl.add(tr1);
        tl.add(tr2);
        String st = tListIntoString(tl);
        System.out.println(st);
        st = encryptHistory(st, "Pavel's SMART!!!");
        System.out.println(st);
        st = decryptHistory(st, "Pavel's SMART!!!");
        System.out.println(st);
        tl = new TransactionList();
        tl = stringIntoTList(st);
        for (int i = 0; i < tl.size(); i++) {
            System.out.println(tl.get(i));
        }
    }
    */

}