package ex.dev.tool.blesample;

import java.util.UUID;

public class Constants
{
    public final static int REQUEST_ENABLE_BT = 3054;
    public final static int REQUEST_FINE_LOCATION = 3055;
    public final static int SCAN_PERIOD = 5000;

    public static String SERVICE_STRING = "CB660002-4339-FF22-A1ED-DEBFED27BDB4";
    public static String CHARACTERISTIC_STRING = "CB660004-4339-FF22-A1ED-DEBFED27BDB4";
    public static String CONFIG_STRING = "00005609-0000-1001-8080-00705c9b34cb";

    public static final UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);
    public static final UUID CHARACTERISTIC_UUID = UUID.fromString(CHARACTERISTIC_STRING);
    public static final UUID CONFIG_UUID = UUID.fromString(CONFIG_STRING);
}
