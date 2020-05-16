package com.thepot.bankaccountmanager.config;

import java.time.Instant;

public class InstantDeserializer extends com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer<Instant> {
    protected InstantDeserializer() {
        super(INSTANT, false);
    }
}
