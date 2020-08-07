package com.javarush.task.task20.task2028;

import java.io.Serializable;
import java.util.*;

/*
Построй дерево(1)
*/
public class CustomTree extends AbstractList<String> implements Cloneable, Serializable {
    Entry root = new Entry<String>("elementName");
    LinkedList<Entry> elementList = new LinkedList();
    private int size;
    {
        elementList.add(root);
    }

    public String getParent(String s) {
        /*if (elementList.indexOf(s) >= 0) {
            return elementList.get(elementList.indexOf(s)).parent.elementName;
        }
        //return list.get(list.indexOf(s)).elementName;
        return null;*/
        Queue<Entry<String>> queue = new ArrayDeque<>();
        queue.add(root);
        Entry<String> node;
        Entry<String> parent = null;
        while (!queue.isEmpty()) {
            node = queue.poll();
            if (node.elementName.equalsIgnoreCase(s)) {
                parent = node.parent;
                break;
            } else {
                if (node.leftChild != null) {
                    queue.add(node.leftChild);
                }
                if (node.rightChild != null) {
                    queue.add(node.rightChild);
                }
            }
        }
        return parent != null ? parent.elementName : null;

    }

    static class Entry<T> implements Serializable {
        String elementName;
        boolean availableToAddLeftChildren, availableToAddRightChildren;
        Entry<T> parent, leftChild, rightChild = null;

        public Entry(String elementName) {
            this.elementName = elementName;
            this.availableToAddLeftChildren = true;
            this.availableToAddRightChildren = true;
            //list.add(this);
        }

        public boolean isAvailableToAddChildren() {
            return availableToAddLeftChildren | availableToAddRightChildren;
        }
    }

    @Override
    public boolean add(String s) {
        boolean result = false;
        Queue<Entry<String>> queue = new ArrayDeque<>();
        queue.add(root);
        Entry<String> node;
        while (!queue.isEmpty()) {
            node = queue.poll();
            if (node.rightChild == null) {
                node.availableToAddRightChildren = true;
            }
            if (node.leftChild == null) {
                node.availableToAddLeftChildren = true;
            }
            if (node.isAvailableToAddChildren()) {
                if (node.parent == null) {
                    node.parent = node;
                }
                if (node.availableToAddLeftChildren) {
                    node.leftChild = new Entry<>(s);
                    node.leftChild.parent = node;
                    node.availableToAddLeftChildren = false;
                    result = true;
                    break;
                }
                if (node.availableToAddRightChildren) {
                    node.rightChild = new Entry<>(s);
                    node.rightChild.parent = node;
                    node.availableToAddRightChildren = false;
                    result = true;
                    break;
                }
            } else {
                queue.add(node.leftChild);
                queue.add(node.rightChild);
            }
        }
        size++;
        return result;

        /*boolean add = false;
        Entry parent = elementList.peek();

        if (parent.isAvailableToAddChildren()) {
            if (parent.leftChild == null) {
                parent.leftChild = new Entry(s);
                parent.leftChild.parent = parent;
                parent.availableToAddLeftChildren = false;
                elementList.offerFirst(elementList.getLast());
                elementList.remove(elementList.getLast());
                add = true;
            } else {
                if (parent.rightChild == null) {
                    parent.rightChild = new Entry(s);
                    parent.rightChild.parent = parent;
                    parent.availableToAddRightChildren = false;
                    add = true;
                }
            }
        } else {
            elementList.add(parent.leftChild);
            elementList.add(parent.rightChild);
            this.add(s);
            add = true;
        }*/

        /*if (parent.leftChild == null) {
            parent.leftChild = new Entry(s);
            parent.availableToAddLeftChildren = false;
            add = true;
        }
        else if (parent.leftChild != null) {
            if (parent.rightChild == null) {
                parent.rightChild = new Entry(s);
                parent.availableToAddRightChildren = false;
                add = true;
            }
            else if (parent.leftChild != null && parent.rightChild != null) {
                elementList.add(parent.leftChild);
                elementList.add(parent.rightChild);
                parent.availableToAddRightChildren = false;
                parent.availableToAddLeftChildren = false;
                //elementList.remove(parent);
                add = true;
            }*/


        //return add;
    }

    private Entry<String> getEntryParent(String s) {
        Queue<Entry<String>> queue = new ArrayDeque<>();
        queue.add(root);
        Entry<String> node;
        Entry<String> parent = null;
        while (!queue.isEmpty()) {
            node = queue.poll();
            if (node.elementName.equalsIgnoreCase(s)) {
                parent = node.parent;
                break;
            } else {
                if (node.leftChild != null) {
                    queue.add(node.leftChild);
                }
                if (node.rightChild != null) {
                    queue.add(node.rightChild);
                }
            }
        }
        return parent;
    }

    public boolean remove(Object o) {
        if (!(o instanceof String)) {
            throw new UnsupportedOperationException();
        }
        boolean isModify = false;
        String str = (String) o;
        Entry<String> parent = getEntryParent(str);
        ArrayDeque<Entry<String>> queueA = new ArrayDeque<>();
        ArrayDeque<Entry<String>> queueB = new ArrayDeque<>();
        int count = 0;
        if (parent.rightChild != null && parent.rightChild.elementName.equalsIgnoreCase(str)) {
            queueA.add(parent.rightChild);
            queueB.add(parent.rightChild);
        }
        if (parent.leftChild != null && parent.leftChild.elementName.equalsIgnoreCase(str)) {
            queueA.add(parent.leftChild);
            queueB.add(parent.leftChild);
        }
        Entry<String> node;
        while (!queueA.isEmpty()) {
            node = queueA.poll();
            if (node.leftChild != null) {
                queueA.add(node.leftChild);
                queueB.add(node.leftChild);
            }
            if (node.rightChild != null) {
                queueA.add(node.rightChild);
                queueB.add(node.rightChild);
            }
        }
        count = queueB.size();
        while (!queueB.isEmpty()) {
            node = queueB.pollLast();
            if (node != null) {
                node.parent = null;
                node.leftChild = null;
                node.rightChild = null;
            }
        }
        if (parent.rightChild != null && parent.rightChild.elementName.equalsIgnoreCase(str)) {
            parent.rightChild = null;
            isModify = true;
        }
        if (parent.leftChild != null && parent.leftChild.elementName.equalsIgnoreCase(str)) {
            parent.leftChild = null;
            isModify = true;
        }
        size -= count;
        return isModify;
    }





    @Override
    public String get(int index) { throw new UnsupportedOperationException(); }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String set(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }
}
