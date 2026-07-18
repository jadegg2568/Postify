package ru.jadegg2568.Postify.util;

import org.jetbrains.annotations.NotNull;
import ua_parser.Client;
import ua_parser.Parser;

public class RequestDataUtils {

    private static final Parser uaParser = new Parser();

    public static @NotNull DeviceData parseDeviceData(String userAgentStr) {
        if (userAgentStr == null) {
            return new DeviceData("Unknown", "Unknown");
        }

        Client client = uaParser.parse(userAgentStr);

        return new DeviceData(
                String.format("%s %s", client.userAgent.family, client.userAgent.major), // e.g., Chrome 148
                String.format("%s %s", client.os.family, client.userAgent.minor) // e.g., Windows 10 22H2
                );
    }
}
