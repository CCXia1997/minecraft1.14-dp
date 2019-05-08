package net.minecraft.util;

import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Actor<Msg> extends AutoCloseable
{
    String getName();
    
    void send(final Msg arg1);
    
    default void close() {
    }
    
    default <Source> CompletableFuture<Source> createAndSendFutureActor(final Function<? super Actor<Source>, ? extends Msg> function) {
        final CompletableFuture<Source> completableFuture2 = new CompletableFuture<Source>();
        final Msg object3 = (Msg)function.apply(Actor.createConsumerActor("ask future procesor handle", completableFuture2::complete));
        this.send(object3);
        return completableFuture2;
    }
    
    default <Msg> Actor<Msg> createConsumerActor(final String string, final Consumer<Msg> consumer) {
        return new Actor<Msg>() {
            @Override
            public String getName() {
                return string;
            }
            
            @Override
            public void send(final Msg message) {
                consumer.accept(message);
            }
            
            @Override
            public String toString() {
                return string;
            }
        };
    }
}
