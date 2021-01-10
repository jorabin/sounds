/*
 * Copyright (c) 2011 - 2021 Jo Rabin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.linguafranca.sound.player;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Jo
 * Date: 06/01/2012
 * Time: 09:30
 * To change this template use File | Settings | File Templates.
 */
public class  QueueItem <I>  {


    private final I item;
    private enum Statuses {IDLE, STARTED, FINISHED, ABANDONED}
    private Statuses status;

    public interface QueueItemUpdateListener<J>{
        void onUpdate(QueueItem<J> item);
    }

    protected CopyOnWriteArrayList<QueueItemUpdateListener<I>> listeners = new CopyOnWriteArrayList<>();

    public QueueItem(I item) {
        this.item = item;
        this.status = Statuses.IDLE;
    }

    public I getItem() {
        return item;
    }

    public synchronized boolean isStarted() {
        return status == Statuses.STARTED;
    }

    public synchronized boolean isFinished() {
        return status == Statuses.FINISHED;
    }

    public synchronized boolean isAbandoned() {
        return status == Statuses.ABANDONED;
    }

    public synchronized void setStarted() {
        this.status = Statuses.STARTED;
        for (QueueItemUpdateListener<I> listener: listeners){
            listener.onUpdate(this);
        }
        this.notify();
    }

    public synchronized void setFinished() {
        this.status = Statuses.FINISHED;
        for (QueueItemUpdateListener<I> listener: listeners){
            listener.onUpdate(this);
        }
        this.notify();
    }

    public synchronized void setAbandoned() {
        this.status = Statuses.ABANDONED;
        for (QueueItemUpdateListener<I> listener: listeners){
            listener.onUpdate(this);
        }
        this.notify();
    }

    public void addCompletionListener(QueueItemUpdateListener<I> queueItemUpdateListener){
        listeners.add(queueItemUpdateListener);
    }

    public boolean removeCompletionListener(QueueItemUpdateListener<I> queueItemUpdateListener){
        return listeners.remove(queueItemUpdateListener);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.status, this.item);
    }
}
