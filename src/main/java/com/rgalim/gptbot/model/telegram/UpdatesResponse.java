package com.rgalim.gptbot.model.telegram;

import java.util.List;

public record UpdatesResponse(
        boolean ok,
        List<Update> result
) {
}
