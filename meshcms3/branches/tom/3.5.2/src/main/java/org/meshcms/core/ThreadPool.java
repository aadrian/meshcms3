package org.meshcms.core;

import java.util.ArrayList;
import java.util.List;

public class ThreadPool{

    private List tasks;

    public ThreadPool( int numThreads ){
        tasks = new ArrayList();

        for( int i = 0; i < numThreads; i++ ){
            Thread thread = new Thread( new TaskExecutor() );
            thread.setDaemon( true );
            thread.setPriority( Thread.MIN_PRIORITY );
            thread.start();
        }
    }

    public void submit( Runnable task ){
        synchronized( tasks ){
            tasks.add( task );
            tasks.notify();
        }
    }

    private Runnable getTask(){
        synchronized( tasks ){
            if( tasks.size() == 0 ){
                try{
                    tasks.wait();
                }
                catch( InterruptedException e ){
                    e.printStackTrace();
                    return null;
                }
            }
            Object task = tasks.get( 0 );
            tasks.remove( 0 );
            return ( Runnable )task;
        }
    }

    private class TaskExecutor implements Runnable{

        public void run(){

            Runnable task = null;

            while( ( task = getTask() ) != null ){

                try{
                    task.run();
                }
                catch( Exception e ){
                    e.printStackTrace();
                }
            }
        }

    }

}
