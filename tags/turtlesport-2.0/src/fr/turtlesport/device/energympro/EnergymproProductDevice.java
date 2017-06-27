package fr.turtlesport.device.energympro;

import fr.turtlesport.device.IProductDevice;
import fr.turtlesport.util.ByteUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by denisapparicio on 16/06/2015.
 */
public class EnergymproProductDevice implements IProductDevice {

    protected static String FILENAME = "SetupInfo.cpi";

    private String product;

    private short version;

    protected EnergymproProductDevice(File file) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            parse(in);
        }
        finally {
            if (in !=null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String getProduct() {
        return product;
    }

    public short getVersion() {
        return version;
    }

    public void parse(InputStream in) throws IOException {
        // Version
        byte[] buf = new byte[15];
        in.read(buf);
        int i = 0;
        for (i = 0; i < buf.length && buf[i] != 0; i++);
        product = new String(buf,0, i);

        // Version.VerNum
        version = ByteUtil.toShort((byte)in.read(), (byte) in.read());
    }

    @Override
    public String displayName() {
        return product + "v" + softwareVersion();
    }

    @Override
    public String id() {
        return product;
    }

    @Override
    public String softwareVersion() {
        return Short.toString(version);
    }
}
