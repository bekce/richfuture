package com.sebworks.richfuture;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ForwardingListeningExecutorService;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by seb on 28.10.2015.
 */
public interface RichExecutorService extends ListeningExecutorService {

	class RichExecutorServiceImpl extends ForwardingListeningExecutorService implements RichExecutorService{

		private ListeningExecutorService delegate;

		public RichExecutorServiceImpl(ListeningExecutorService delegate) {
			this.delegate = delegate;
		}

		@Override
		protected ListeningExecutorService delegate() {
			return delegate;
		}

		@Override
		public <T> RichFuture<T> submit(Callable<T> task) {
			return RichFuture.wrap(super.submit(task));
		}

		@Override
		public RichFuture<?> submit(Runnable task) {
			return RichFuture.wrap(super.submit(task));
		}

		@Override
		public <T> RichFuture<T> submit(Runnable task, T result) {
			return RichFuture.wrap(super.submit(task, result));
		}
	}

	/**
	 * Wrap given ExecutorService as RichExecutorService
	 */
	static RichExecutorService wrap(ExecutorService es){
		Preconditions.checkNotNull(es);
		if(es instanceof RichExecutorService) return (RichExecutorService) es;
		else if(es instanceof ListeningExecutorService) return new RichExecutorServiceImpl((ListeningExecutorService) es);
		else return new RichExecutorServiceImpl(MoreExecutors.listeningDecorator(es));
	}

	/**
	 * @return a {@code ListenableFuture} representing pending completion of the task
	 * @throws RejectedExecutionException {@inheritDoc}
	 */
	@Override
	<T> RichFuture<T> submit(Callable<T> task);

	/**
	 * @return a {@code ListenableFuture} representing pending completion of the task
	 * @throws RejectedExecutionException {@inheritDoc}
	 */
	@Override
	RichFuture<?> submit(Runnable task);

	/**
	 * @return a {@code ListenableFuture} representing pending completion of the task
	 * @throws RejectedExecutionException {@inheritDoc}
	 */
	@Override
	<T> RichFuture<T> submit(Runnable task, T result);

}
