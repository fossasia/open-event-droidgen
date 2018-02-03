package org.fossasia.openevent.config;

import org.fossasia.openevent.config.strategies.EventBusStrategy;
import org.fossasia.openevent.config.strategies.HttpStrategy;
import org.fossasia.openevent.config.strategies.LanguageStrategy;
import org.fossasia.openevent.config.strategies.LeakCanaryStrategy;
import org.fossasia.openevent.config.strategies.MapModuleStrategy;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StrategyRegistryTest {

    private void genericRegistryTest(Class<? extends ConfigStrategy> clazz,
                                                                Consumer<ConfigStrategy> strategyConsumer,
                                                                Supplier<? extends ConfigStrategy> strategySupplier) {
        ConfigStrategy strategy = Mockito.mock(clazz);
        strategyConsumer.accept(strategy);

        assertEquals(strategy, strategySupplier.get());
        assertTrue("Strategy is not in holder", StrategyRegistry.getInstance()
                .getDefaultHolder()
                .getStrategies()
                .contains(strategy));
    }

    @Test
    public void testLanguageStrategy() {
        StrategyRegistry registry = StrategyRegistry.getInstance();
        genericRegistryTest(LanguageStrategy.class,
                configStrategy -> registry.setLanguageStrategy((LanguageStrategy) configStrategy),
                registry::getLanguageStrategy);
    }

    @Test
    public void testHttpStrategy() {
        StrategyRegistry registry = StrategyRegistry.getInstance();
        genericRegistryTest(HttpStrategy.class,
                configStrategy -> registry.setHttpStrategy((HttpStrategy) configStrategy),
                registry::getHttpStrategy);
    }

    @Test
    public void testMapModuleStrategy() {
        StrategyRegistry registry = StrategyRegistry.getInstance();
        genericRegistryTest(MapModuleStrategy.class,
                configStrategy -> registry.setMapModuleStrategy((MapModuleStrategy) configStrategy),
                registry::getMapModuleStrategy);
    }

    @Test
    public void testEventStrategy() {
        StrategyRegistry registry = StrategyRegistry.getInstance();
        genericRegistryTest(EventBusStrategy.class,
                configStrategy -> registry.setEventBusStrategy((EventBusStrategy) configStrategy),
                registry::getEventBusStrategy);
    }

    @Test
    public void testLeakCanaryStrategy() {
        StrategyRegistry registry = StrategyRegistry.getInstance();
        genericRegistryTest(LeakCanaryStrategy.class,
                configStrategy -> registry.setLeakCanaryStrategy((LeakCanaryStrategy) configStrategy),
                registry::getLeakCanaryStrategy);
    }

}