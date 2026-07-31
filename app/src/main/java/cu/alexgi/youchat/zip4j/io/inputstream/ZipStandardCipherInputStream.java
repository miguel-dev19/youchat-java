package cu.alexgi.youchat.zip4j.io.inputstream;

import java.io.IOException;

import cu.alexgi.youchat.zip4j.crypto.StandardDecrypter;
import cu.alexgi.youchat.zip4j.model.LocalFileHeader;

import static cu.alexgi.youchat.zip4j.util.InternalZipConstants.STD_DEC_HDR_SIZE;

class ZipStandardCipherInputStream extends CipherInputStream<StandardDecrypter> {

  public ZipStandardCipherInputStream(ZipEntryInputStream zipEntryInputStream, LocalFileHeader localFileHeader,
                                      char[] password, int bufferSize) throws IOException {
    super(zipEntryInputStream, localFileHeader, password, bufferSize);
  }

  @Override
  protected StandardDecrypter initializeDecrypter(LocalFileHeader localFileHeader, char[] password) throws IOException {
    return new StandardDecrypter(password, localFileHeader.getCrc(), localFileHeader.getLastModifiedTime(),
        getStandardDecrypterHeaderBytes());
  }

  private byte[] getStandardDecrypterHeaderBytes() throws IOException {
    byte[] headerBytes = new byte[STD_DEC_HDR_SIZE];
    readRaw(headerBytes);
    return headerBytes;
  }
}
