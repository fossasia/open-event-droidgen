package org.fossasia.openevent.config;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public class ConfigStrategyHolder {

    private final Set<ConfigStrategy> strategies = new LinkedHashSet<>();

    public void register(ConfigStrategy configStrategy) {
        strategies.add(configStrategy);
    }

}
