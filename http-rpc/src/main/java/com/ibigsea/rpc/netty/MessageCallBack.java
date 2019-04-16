/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibigsea.rpc.netty;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ibigsea.rpc.serizlize.NettyRequest;

import io.netty.channel.Channel;

/**
 * netty callback
 * @author jiang 
 * 2019年1月24日
 */
public class MessageCallBack {

    private NettyRequest request;
    private String response;
    private Lock lock = new ReentrantLock();
    private Condition finish = lock.newCondition();

    public MessageCallBack(NettyRequest request) {
        this.request = request;
    }

    public String start(Channel channel) {
        try {
            lock.lock();
            finish.await();
            if (this.response != null) {
                return this.response;
            } else {
                return null;
            }
        } catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
        finally {
            lock.unlock();
            try {
				channel.closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }

    public void over(String reponse) {
        try {
            lock.lock();
            finish.signal();
            this.response = reponse;
        } finally {
            lock.unlock();
        }
    }
}
