package cz.zcu.kiv.eeg.basil.data.providers.xdf;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Created by Tomas Prokop on 14.01.2019.
 */
public class XdfReader {
    public boolean read(String file) {
        File f = new File(file);
        if (!f.exists())
            return false;

        try {
            byte[] buffer = new byte[4];
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(buffer);
            String s = new String(buffer, StandardCharsets.UTF_8);
            if (!s.equals("XDF:")) {
                return false;
            }

            int chunks = 0;
            boolean readChunk = true;
            while (readChunk) {
                long len = readVarLenInteger(bis);
                short tag = readTag(bis);
                String xml = null;

                XmlMapper mapper = new XmlMapper();
                chunks++;

                switch (tag) {
                    case 1:
                        buffer = new byte[(int) len - 2]; //todo what if len > int.MaxValue???
                        bis.read(buffer);
                        xml = new String(buffer, StandardCharsets.UTF_8);
                        FileHeader header = mapper.readValue(xml, FileHeader.class);
                        break;
                    case 2:
                        int id = readStreamId(bis);
                        buffer = new byte[(int) len - 6]; //todo what if len > int.MaxValue???
                        bis.read(buffer);
                        xml = new String(buffer, StandardCharsets.UTF_8);
                        mapper = new XmlMapper();
                        StreamHeader sHeader = mapper.readValue(xml, StreamHeader.class);
                        break;
                    default:
                        readChunk = false;
                        break;
                }
            }

            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }

    private int readStreamId(InputStream is) throws IOException {
        byte[] buff = new byte[4];
        is.read(buff);
        ByteBuffer wrapped = ByteBuffer.wrap(buff);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);

        return wrapped.getInt();
    }

    private short readTag(InputStream is) throws IOException {
        byte[] buff = new byte[2];
        is.read(buff);
        ByteBuffer wrapped = ByteBuffer.wrap(buff);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);

        return wrapped.getShort();
    }

    private long readVarLenInteger(InputStream is) throws IOException {
        int n = is.read();
        if (n == 1) {
            return is.read();
        }

        if (n != 4 && n != 8)
            throw new IOException();

        byte[] buff = new byte[n];
        is.read(buff);
        ByteBuffer wrapped = ByteBuffer.wrap(buff);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);

        return n == 4 ? wrapped.getInt() : wrapped.getLong();
    }
}
