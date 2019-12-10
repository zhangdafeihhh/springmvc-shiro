package com.zhuanche.controller;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.encrypt.RSAWithBase64Utils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Calendar;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/unionLogin")
public class UnionLoginController {
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaocK3IbBrDu75Enw/bGP9qBE55hDjsfx+ESQdpE8lJmbk+wsc2YNMmbUcaL7c/VfqeCUiYQaS7lA8aDGa3O3BI/M2SQt6tse3JeXuW7VPndNHYkZcNfiCNKMebH/Wui8VZtdLq74O+PoRTfGA3g7zzTNLUL559efPKQjdGcy7mwIDAQAB";
    private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJqhwrchsGsO7vkSfD9sY/2oETnmEOOx/H4RJB2kTyUmZuT7CxzZg0yZtRxovtz9V+p4JSJhBpLuUDxoMZrc7cEj8zZJC3q2x7cl5e5btU+d00diRlw1+II0ox5sf9a6LxVm10urvg74+hFN8YDeDvPNM0tQvnn1588pCN0ZzLubAgMBAAECgYBkDr33UDZe4BfkVYDObW26ShFzfJp7K8WtMZxEDfkkmdfE3WkQbvMWEvPtgR3X05sa3klxGIVveAO1QtquUb0J0cmKe3fK37XjVbXH52PZ2uGVtoz9Cw//CtRRinRfjdXF56wVY+0iEzneo9UdOd8cSzsvbU2DAND6esQzyMm52QJBAPGDZgXAJa+tuVSH64nnrI2hdxqiGk45ImgImcFF1+1d+M2CPz3MlNybtRK4ZwU0hKRtq9wgQocyK1feXz7Ueh0CQQCj6D0iqf3/wyWMTNnUPNBpsU/dkmc5XTEqnhoRRBziy206nzQXPY7Z03GSsNuBebf3YGByeCWiWRCSCSKj6V8XAkEAixxJhqRv0Ko7JwKHXE6yqH3JsfCPQGFOfSf/EEFfp5zKM+2C6eDOGegiO182D9x5TpDRPiDi0SbHqnwu1mzizQJAA7bGc9ugOsRkPGqdYPbDzjFLWvzvQ9h9vbZ5ZrzBXs43OmXTG1e0vfDRmP5S6vskFQHlDZL1X1lfS7/TPh2SnwJBAL5k5rT/M1gwOkD156X+YJEgEb/WrBbtgEp/PQR9f0zWV1jKUXSldJ3EiQEctoOIk7zoUm8k1caTSm1au1PRhqo=";

    @RequestMapping("/key")
    @ResponseBody
    public AjaxResponse getUnionLoginKey() {
        Logger logger = LoggerFactory.getLogger(UnionLoginController.class);
        String phone= WebSessionUtil.getCurrentLoginUser().getMobile();
        Calendar calendar = Calendar.getInstance();
        String now = String.valueOf(calendar.getTimeInMillis());
        try {
            String cipherPhone = RSAWithBase64Utils.encrypt((phone + "||" + now).getBytes(), RSAWithBase64Utils.loadPrivateKeyByStr(PRIVATE_KEY));
            // 验证用
            String decodePhone = RSAWithBase64Utils.decrypt(Base64.decodeBase64(cipherPhone), RSAWithBase64Utils.loadPublicKeyByStr(PUBLIC_KEY));
            logger.info("获取联合登陆密文，手机号：{}，时间戳：{}, 密文：{}，解密后手机号：{}", phone, now, cipherPhone, decodePhone);

            return AjaxResponse.success(cipherPhone);
        } catch (Exception e) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, e.getMessage());
        }
    }

//    @RequestMapping("/getKey")
//    @ResponseBody
//    public static void genKeyPair(String filePath) {
//        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
//        KeyPairGenerator keyPairGen = null;
//        try {
//            keyPairGen = KeyPairGenerator.getInstance("RSA");
//        } catch (NoSuchAlgorithmException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        // 初始化密钥对生成器，密钥大小为96-1024位
//        keyPairGen.initialize(1024,new SecureRandom());
//        // 生成一个密钥对，保存在keyPair中
//        KeyPair keyPair = keyPairGen.generateKeyPair();
//        // 得到私钥
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        // 得到公钥
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        try {
//            // 得到公钥字符串
//            String publicKeyString = Base64.encodeBase64String(publicKey.getEncoded());
//            // 得到私钥字符串
//            String privateKeyString = Base64.encodeBase64String(privateKey.getEncoded());
//            System.out.println(publicKeyString);
//            System.out.println(privateKeyString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
