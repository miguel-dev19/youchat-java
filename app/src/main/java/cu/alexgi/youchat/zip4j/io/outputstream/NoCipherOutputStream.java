package cu.alexgi.youchat.zip4j.io.outputstream;

import java.io.IOException;
import java.io.OutputStream;

import cu.alexgi.youchat.zip4j.crypto.Encrypter;
import cu.alexgi.youchat.zip4j.exception.ZipException;
import cu.alexgi.youchat.zip4j.model.ZipParameters;

class NoCipherOutputStream extends CipherOutputStream<NoCipherOutputStream.NoEncrypter> {

  public NoCipherOutputStream(ZipEntryOutputStream zipEntryOutputStream, ZipParameters zipParameters, char[] password) throws IOException, ZipException {
    super(zipEntryOutputStream, zipParameters, password);
  }

  @Override
  protected NoEncrypter initializeEncrypter(OutputStream outputStream, ZipParameters zipParameters, char[] password) {
    return new NoEncrypter();
  }

  static class NoEncrypter implements Encrypter {

    @Override
    public int encryptData(byte[] buff) {
      return encryptData(buff, 0, buff.length);
    }

    @Override
    public int encryptData(byte[] buff, int start, int len) {
      return len;
    }
  }
}
