package com.example.batchdemo.job;

import javax.batch.api.chunk.listener.ItemReadListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemListener implements ItemReadListener {

    /**
     * The beforeRead method receives control before an item reader is called to read the next
     * item.
     *
     * @throws Exception is thrown if an error occurs.
     */
    @Override public void beforeRead() throws Exception {
        log.info("[before read] thread: {}", Thread.currentThread().getName());
    }

    /**
     * The afterRead method receives control after an item reader reads an item. The method receives
     * the item read as an input.
     *
     * @param item specifies the item read by the item reader.
     * @throws Exception is thrown if an error occurs.
     */
    @Override public void afterRead(Object item) throws Exception {
        log.info("[after read] thread: {}, item: {}", Thread.currentThread().getName(), item.toString());
    }

    /**
     * The onReadError method receives control after an item reader throws an exception in the
     * readItem method. This method receives the exception as an input.
     *
     * @param ex specifies the exception that occurred in the item reader.
     * @throws Exception is thrown if an error occurs.
     */
    @Override public void onReadError(Exception ex) throws Exception {
        log.error("[read error] thread: {}", Thread.currentThread().getName());
    }
}
