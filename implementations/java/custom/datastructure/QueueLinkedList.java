package custom.datastructure;

public class QueueLinkedList {
    private Node head;
    private Node tail;
    private int size;

    private class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
            this.next = null;
        }
    }

    public QueueLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void enqueue(int value) {
        Node newNode = new Node(value);
        if (tail != null) {
            tail.next = newNode;
        }
        tail = newNode;
        if (head == null) {
            head = newNode;
        }
        setSize(getSize() + 1);
    }

    public Integer dequeue() {
        if (head == null) {
            return null; // Queue is empty
        }
        int value = head.value;
        head = head.next;
        if (head == null) {
            tail = null; // Queue is now empty
        }
        setSize(getSize() - 1);
        return value;
    }

    public Integer peek() {
        if (head == null) {
            return null; // Queue is empty
        }
        return head.value;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public static void main(String[] args) {
        QueueLinkedList queue = new QueueLinkedList();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        System.out.println("Front element: " + queue.peek()); // Output: 1
        System.out.println("Dequeue: " + queue.dequeue());    // Output: 1
        System.out.println("Front element: " + queue.peek()); // Output: 2
        System.out.println("Queue size: " + queue.size());    // Output: 2
    }
}
