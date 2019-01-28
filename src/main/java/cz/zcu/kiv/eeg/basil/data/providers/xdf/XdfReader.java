package cz.zcu.kiv.eeg.basil.data.providers.xdf;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Tomas Prokop on 14.01.2019.
 */
public class XdfReader {
    private HashMap<Integer, StreamHeader> streamheaders;
    private HashMap<Integer, StreamData> streamdata;

    public boolean read(String file) {
        File f = new File(file);
        if (!f.exists())
            return false;

        try {
            streamheaders = new HashMap<>();
            streamdata = new HashMap<>();

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

                XmlMapper mapper;
                chunks++;

                switch (tag) {
                    case 1: //file header
                        buffer = new byte[(int) len - 2]; //todo what if len > int.MaxValue???
                        bis.read(buffer);
                        xml = new String(buffer, StandardCharsets.UTF_8);
                        mapper = new XmlMapper();
                        FileHeader header = mapper.readValue(xml, FileHeader.class);
                        break;
                    case 2: //stream header
                        int id = readStreamId(bis);
                        buffer = new byte[(int) len - 6]; //todo what if len > int.MaxValue???
                        bis.read(buffer);
                        xml = new String(buffer, StandardCharsets.UTF_8);
                        mapper = new XmlMapper();
                        StreamHeader sHeader = mapper.readValue(xml, StreamHeader.class);
                        streamheaders.put(id, sHeader);
                        streamdata.put(id, createStreamData(sHeader));
                        break;
                    case 3:
                        id = readStreamId(bis);
                        len = readVarLenInteger(bis);
                        sHeader = streamheaders.get(id);

                        if (sHeader.getChannelFormat() != ChannelFormat.string) {
                            readData(bis, len, id);
                        } else {
                            //not implemented yet
                            continue;
                        }
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

    private void readData(InputStream bis, long len, int id) throws IOException {
        StreamHeader header = streamheaders.get(id);
        StreamData data = streamdata.get(id);

        ByteBuffer wrap = ByteBuffer.allocate(128);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        byte[] buffer;
        Double[] stamps = new Double[(int) len];
        Arrays.fill(stamps, 0);
        int bytesToRead = header.getChannelCount() * ChannelFormat.getBytesCount(header.getChannelFormat());

        for (int i = 0; i < len; i++) {
            int b = bis.read();

            if (b > 0) {
                buffer = new byte[8];
                bis.read(buffer);
                wrap.put(buffer);
                stamps[i] = wrap.getDouble();
                wrap.clear();
            }

            buffer = new byte[bytesToRead];
            bis.read(buffer);
            wrap.put(buffer);
            for (int k = 0; k < header.getChannelCount(); k++) {
                switch (header.getChannelFormat()) {
                    case int8:
                        data.addSample(buffer[k], k);
                        break;
                    case int16:
                        data.addSample(wrap.getShort(), k);
                        break;
                    case int32:
                        data.addSample(wrap.getInt(), k);
                        break;
                    case int64:
                        data.addSample(wrap.getLong(), k);
                        break;
                    case float32:
                        data.addSample(wrap.getFloat(), k);
                        break;
                    case double64:
                        data.addSample(wrap.getDouble(), k);
                        break;
                    default:
                        break;
                }
            }
            wrap.clear();
        }

        data.addTimeStamps(stamps);
    }

    private StreamData createStreamData(StreamHeader header) {
        int channels = header.getChannelCount();
        switch (header.getChannelFormat()) {
            case int8:
                return new StreamData<Byte>(channels);
            case int16:
                return new StreamData<Short>(channels);
            case int32:
                return new StreamData<Integer>(channels);
            case int64:
                return new StreamData<Long>(channels);
            case float32:
                return new StreamData<Float>(channels);
            case double64:
                return new StreamData<Double>(channels);
            default:
                break;
        }

        return null;
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
