package org.meshcms.core;

import junit.framework.TestCase;


public class ThreadPoolTest extends TestCase{

    private int done =0;
    private ThreadPool pool;
    
    public void test() throws InterruptedException{
        
         pool = new ThreadPool( 10 );
        
        for( int i = 0; i < 500; i++ ){
            pool.submit( new Task() );
        }
        
        Thread.sleep( 1000 );
        
        for( int i = 0; i < 500; i++ ){
            pool.submit( new Task() );
        }
        
        Thread.sleep( 10000 );
        
        assertEquals( 1000, done );
    }
    
    private class Task implements Runnable{

        public void run(){
            try{
                
                long sleep = (long)( Math.random()  * 100 );
                Thread.sleep( sleep );
                synchronized( pool ){
                    done++;
                }
                
            }
            catch( InterruptedException e ){
                e.printStackTrace();
            }
        }
        
    }
    
}
