package com.sebworks.richfuture;

import com.google.common.util.concurrent.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Created by seb on 28.10.2015.
 */
public class RichFutureImpl<V> extends ForwardingListenableFuture<V> implements RichFuture<V> {

	private ListenableFuture delegate;

	public RichFutureImpl(ListenableFuture delegate) {
		this.delegate = delegate;
	}

	@Override
	protected ListenableFuture delegate() {
		return delegate;
	}

	@Override
	public V getUnchecked() {
		return Futures.getUnchecked(this);
	}

	@Override
	public V getUnchecked(long timeout, TimeUnit unit) {
		try {
			return getUninterruptibly(this, timeout, unit);
		} catch (ExecutionException | TimeoutException e) {
			wrapAndThrowUnchecked(e.getCause());
			throw new AssertionError();
		}
	}

	@Override
	public RichFuture addCallback(FutureCallback<? super V> callback) {
		Futures.addCallback(this, callback);
		return this;
	}

	@Override
	public RichFuture addCallback(FutureCallback<? super V> callback, Executor executor) {
		Futures.addCallback(this, callback, executor);
		return this;
	}

	@Override
	public RichFuture onSuccess(Consumer<V> consumer) {
		Futures.addCallback(this, new FutureCallback<V>() {
			@Override
			public void onSuccess(V result) {
				consumer.accept(result);
			}

			@Override
			public void onFailure(Throwable t) {
			}
		});
		return this;
	}

	@Override
	public RichFuture onSuccess(Consumer<V> consumer, Executor executor) {
		Futures.addCallback(this, new FutureCallback<V>() {
			@Override
			public void onSuccess(V result) {
				consumer.accept(result);
			}

			@Override
			public void onFailure(Throwable t) {
			}
		}, executor);
		return this;
	}

	@Override
	public RichFuture onFailure(Consumer<Throwable> consumer) {
		Futures.addCallback(this, new FutureCallback<V>() {
			@Override
			public void onSuccess(V result) {
			}

			@Override
			public void onFailure(Throwable t) {
				consumer.accept(t);
			}
		});
		return this;
	}

	@Override
	public RichFuture onFailure(Consumer<Throwable> consumer, Executor executor) {
		Futures.addCallback(this, new FutureCallback<V>() {
			@Override
			public void onSuccess(V result) {
			}

			@Override
			public void onFailure(Throwable t) {
				consumer.accept(t);
			}
		}, executor);
		return this;
	}

	private static void wrapAndThrowUnchecked(Throwable cause) {
		if (cause instanceof Error) {
			throw new ExecutionError((Error) cause);
		}
		throw new UncheckedExecutionException(cause);
	}

	private static <V> V getUninterruptibly(java.util.concurrent.Future<V> future, long timeout, TimeUnit unit)
			throws ExecutionException, TimeoutException {
		boolean interrupted = false;
		try {
			while (true) {
				try {
					return future.get(timeout, unit);
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
