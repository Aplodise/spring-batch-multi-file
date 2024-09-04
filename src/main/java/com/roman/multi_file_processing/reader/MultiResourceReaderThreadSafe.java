package com.roman.multi_file_processing.reader;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.core.io.Resource;

import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
public class MultiResourceReaderThreadSafe<T> implements ItemStreamReader<T>{

    private static final Logger log = LoggerFactory.getLogger(MultiResourceReaderThreadSafe.class);
    private final MultiResourceItemReader<T> delegate;
    private final ReentrantLock lock = new ReentrantLock();
    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        this.lock.lock();
        try{
           return delegate.read();
        }finally {
            this.lock.unlock();
        }
    }

    public void open(ExecutionContext executionContext){
        this.delegate.open(executionContext);
    }

    public void close(){
        this.delegate.close();
    }
    public void update(ExecutionContext executionContext) {
        this.delegate.update(executionContext);
    }

}
