package edu.uci.ics.kanec1.service.api_gateway.threadpool;

import edu.uci.ics.kanec1.service.api_gateway.logger.ServiceLogger;

public class ClientRequestQueue {
    private ListNode head;
    private ListNode tail;

    public ClientRequestQueue() {
        head = null;
        tail = null;
    }

    public synchronized void enqueue(ClientRequest clientRequest) {
        ServiceLogger.LOGGER.info("Entered enqueue");
        ListNode node = new ListNode(clientRequest, null);
        if(head == null) {
            head = node;
            tail = node;
        } else {
            ListNode curr = tail;
            curr.setNext(node);
            tail = node;
        }
        ServiceLogger.LOGGER.info("Enqueue done.");
    }

    public synchronized ClientRequest dequeue() {
        ListNode curr = head;
        if(curr == null) return null;
        ListNode next = curr.getNext();
        if(next == null) {
            head = null;
            tail = null;
            return curr.getClientRequest();
        } else {
            curr.setNext(null);
            head = next;
            return curr.getClientRequest();
        }
    }

    synchronized boolean isEmpty() {
        if(head == null && tail == null) return true;
        return false;
    }
}
