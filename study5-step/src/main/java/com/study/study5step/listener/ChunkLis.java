package com.study.study5step.listener;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;


/**
 * @author jiayq
 * @Date 2020-11-25
 */
public class ChunkLis implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
        System.out.println("3. chunk lis before");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        System.out.println("10. chunk lis after");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        System.out.println("0. chunk lis error");
    }
}
