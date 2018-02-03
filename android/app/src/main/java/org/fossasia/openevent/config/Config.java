package org.fossasia.openevent.config;

import lombok.Builder;
import lombok.Data;

/**
 * Config to be replaced in {@link org.fossasia.openevent.config.strategies.AppConfigStrategy}
 *
 * TODO: Add the correct JSON parsing mechanism and populate config in this format and share across project
 *
 * NOT IMPLEMENTED YET
 */
@Data
@Builder
public class Config {
    private final String apiLink;
    private final String email;
    private final String appName;
    private final boolean authEnabled;
}
