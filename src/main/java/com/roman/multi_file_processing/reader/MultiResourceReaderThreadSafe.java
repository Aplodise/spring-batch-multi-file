package com.roman.multi_file_processing.reader;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.core.io.Resource;

@AllArgsConstructor
public class MultiResourceReaderThreadSafe<T> implements ItemReader<T> {

    private MultiResourceItemReader<T> delegate;
    private final Object lock = new Object();
    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        synchronized (lock){
            return delegate.read();
        }
    }

    public void setResources(Resource[] resources){
        synchronized (lock){
            delegate.setResources(resources);
        }
    }

    public void open(ExecutionContext executionContext){
        synchronized (lock){
            delegate.open(executionContext);
        }
    }

    public void close(){
        synchronized (lock){
            delegate.close();
        }
    }

}
