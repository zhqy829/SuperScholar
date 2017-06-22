package com.superscholar.android.tools;

/**
 * Created by Administrator on 2017/2/27.
 * 加密器
 */

public class EncryptionDevice {

    //明文加密
    public static String encrypt(String cleartext){
        char []cleartextArray=cleartext.toCharArray();
        for(int i=0;i<cleartextArray.length;i++){
            if(cleartextArray[i]=='\0'){
                break;
            }else{
                if((i+1)%3==0){
                    cleartextArray[i]=(char)((int)cleartextArray[i]+1);
                }else if((i+1)%2==0){
                    cleartextArray[i]=(char)((int)cleartextArray[i]+2);
                }else{
                    cleartextArray[i]=(char)((int)cleartextArray[i]+3);
                }
            }
        }
        return String.valueOf(cleartextArray);
    }

    //密文解密
    public static String decipher(String ciphertext){
        char []ciphertextArray=ciphertext.toCharArray();
        for(int i=0;i<ciphertextArray.length;i++){
            if(ciphertextArray[i]=='\0'){
                break;
            }else{
                if((i+1)%3==0){
                    ciphertextArray[i]=(char)((int)ciphertextArray[i]-1);
                }else if((i+1)%2==0){
                    ciphertextArray[i]=(char)((int)ciphertextArray[i]-2);
                }else{
                    ciphertextArray[i]=(char)((int)ciphertextArray[i]-3);
                }
            }
        }
        return String.valueOf(ciphertextArray);
    }
}
