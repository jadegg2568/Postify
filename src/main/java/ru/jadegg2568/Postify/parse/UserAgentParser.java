package ru.jadegg2568.Postify.parse;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

@Component
@RequiredArgsConstructor
public class UserAgentParser {

    private final Parser uaParser;

    public @NotNull Device parseDevice(String userAgentStr) {
        if (userAgentStr == null || userAgentStr.isBlank()) {
            return new Device("Unknown", "Unknown");
        }

        Client client = uaParser.parse(userAgentStr);

        String browser = String.format("%s %s", client.userAgent.family, client.userAgent.major);
        String os = String.format("%s %s", client.os.family, client.os.major != null ? client.os.major : "").trim();

        return new Device(browser, os);
    }
}