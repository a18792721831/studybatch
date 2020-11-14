package com.study.study4.job;

/**
 * @author jiayq
 * @Date 2020-11-11
 */
public class PlayGameSimpleJob extends PlaySimpleJob {

    public PlayGameSimpleJob() {
        this("play-game-simple-job");
    }

    public PlayGameSimpleJob(String name) {
        setName(name);
    }

}
