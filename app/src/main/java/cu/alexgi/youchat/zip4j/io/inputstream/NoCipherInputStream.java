package cu.alexgi.youchat.zip4j.io.inputstream;

import java.io.IOException;

import cu.alexgi.youchat.zip4j.crypto.Decrypter;
import cu.alexgi.youchat.zip4j.model.LocalFileHeader;

class NoCipherInputStream extends CipherInputStream {

  public NoCipherInputStream(ZipEntryInputStream zipEntryInputStream, LocalFileHeader localFileHeader,
                             char[] password, int bufferSize) throws IOException {
    super(zipEntryInputStream, localFileHeader, password, bufferSize);
  }

  @Override
  protected Decrypter initializeDecrypter(LocalFileHeader localFileHeader, char[] password) {
    return new NoDecrypter();
  }

  static class NoDecrypter implements Decrypter {

    @Override
    public int decryptData(byte[] buff, int start, int len) {
      return len;
    }
  }
}
