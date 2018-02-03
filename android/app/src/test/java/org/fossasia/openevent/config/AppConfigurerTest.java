package org.fossasia.openevent.config;

import android.content.Context;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

public class AppConfigurerTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void testDefaultStrategyHolder() {
        ConfigStrategyHolder holder = Mockito.mock(ConfigStrategyHolder.class);

        StrategyRegistry.getInstance().setStrategyHolder(holder);
        AppConfigurer.configure(null);

        Mockito.verify(holder).getStrategies();

        StrategyRegistry.getInstance().setStrategyHolder(null);
    }

    @Test
    public void testRunAllStrategies() {
        Context context = Mockito.mock(Context.class);
        ConfigStrategyHolder holder = new ConfigStrategyHolder();
        List<ConfigStrategy> strategies = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ConfigStrategy strategy = Mockito.mock(ConfigStrategy.class);
            Mockito.when(strategy.configure(context)).thenReturn(false);

            strategies.add(strategy);
            holder.register(strategy);
        }

        InOrder inOrder = Mockito.inOrder(strategies.toArray());

        AppConfigurer.configure(context, holder);

        for (ConfigStrategy strategy : strategies)
            inOrder.verify(strategy).configure(context);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testHaltStrategies() {
        Context context = Mockito.mock(Context.class);
        ConfigStrategyHolder holder = new ConfigStrategyHolder();
        List<ConfigStrategy> strategies = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ConfigStrategy strategy = Mockito.mock(ConfigStrategy.class);
            Mockito.when(strategy.configure(context)).thenReturn(i == 7); // Halt on 7

            strategies.add(strategy);
            holder.register(strategy);
        }

        InOrder inOrder = Mockito.inOrder(strategies.toArray());

        AppConfigurer.configure(context, holder);

        for (int i = 0; i < 8; i++)
            inOrder.verify(strategies.get(i)).configure(context);

        inOrder.verifyNoMoreInteractions();
    }

}