package com.business.unknow.services.util.helpers;

import com.business.unknow.Constants.FacturaConstants;
import com.business.unknow.model.error.InvoiceCommonException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.ssl.PKCS8Key;

public class SignHelper {

  private String getKey(String key) {
    byte[] decode = Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8));
    return DatatypeConverter.printBase64Binary(decode);
  }

  public String getSign(String cadena, String keyWord, String key) throws InvoiceCommonException {
    try {
      String archivoLlavePrivada = this.getKey(key);
      InputStream myInputStream =
          new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(archivoLlavePrivada));
      PKCS8Key pkcs8 = new PKCS8Key(myInputStream, keyWord.toCharArray());
      PrivateKey pk = pkcs8.getPrivateKey();
      Signature signature = Signature.getInstance("SHA256withRSA");
      signature.initSign(pk);
      signature.update(cadena.getBytes(StandardCharsets.UTF_8));
      return DatatypeConverter.printBase64Binary(signature.sign());
    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
      throw new InvoiceCommonException(e.getMessage());
    }
  }

  public String getCadena(String xml) throws InvoiceCommonException {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Source xslt = new StreamSource(new File(FacturaConstants.CADENA_ORIGINAL));
      Transformer transformer = transformerFactory.newTransformer(xslt);
      Source xmlSource = new StreamSource(new StringReader(xml));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Result out = new StreamResult(baos);
      transformer.transform(xmlSource, out);
      byte[] cadenaOriginalArray = baos.toByteArray();
      return new String(cadenaOriginalArray, StandardCharsets.UTF_8);
    } catch (TransformerException e) {
      throw new InvoiceCommonException(e.getMessage());
    }
  }
}
