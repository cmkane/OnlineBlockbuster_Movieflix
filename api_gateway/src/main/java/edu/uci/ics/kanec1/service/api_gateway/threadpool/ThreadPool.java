package edu.uci.ics.kanec1.service.api_gateway.threadpool;

public class ThreadPool {
    private int numWorkers;
    private Worker[] workers;
    private ClientRequestQueue queue;

    public ThreadPool(int numWorkers) {
        // Create the workers threads
        this.numWorkers = numWorkers;
        queue = new ClientRequestQueue();
        workers = new Worker[numWorkers];
        for(int x = 0; x < numWorkers; x++) {
            workers[x] = Worker.CreateWorker(x, this);
        }
        // Start the working threads
        for(int x = 0; x < numWorkers; x++) {
            workers[x].start();
        }
    }

    public void add(ClientRequest clientRequest) {
        queue.enqueue(clientRequest);
    }

    public ClientRequest remove() {
        return queue.dequeue();
    }

    public ClientRequestQueue getQueue() {
        return queue;
    }
}
