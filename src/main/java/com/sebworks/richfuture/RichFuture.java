package com.sebworks.richfuture;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by seb on 28.10.2015.
 */
public interface RichFuture<V> extends ListenableFuture<V> {

	static <V> RichFuture<V> wrap(ListenableFuture<V> obj) {
		if(obj instanceof RichFuture) return (RichFuture<V>) obj;
		else return new RichFutureImpl<V>(obj);
	}

	V getUnchecked();

	V getUnchecked(long timeout, TimeUnit unit);

	RichFuture addCallback(FutureCallback<? super V> callback);

	RichFuture addCallback(FutureCallback<? super V> callback, Executor executor);

	RichFuture onSuccess(Consumer<V> consumer);

	RichFuture onSuccess(Consumer<V> consumer, Executor executor);

	RichFuture onFailure(Consumer<Throwable> consumer);

	RichFuture onFailure(Consumer<Throwable> consumer, Executor executor);
}
