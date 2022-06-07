package nl.iobyte.serviceloader.dag;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Parent --> Child
 *
 * @author KocproZ
 * Created 2018-08-14 at 10:35
 * <a href="https://github.com/KocproZ/DAG"></a>
 */
public class Node<T> {

    private final List<Node<T>> parents;
    private final List<Node<T>> children;
    private final T object;

    protected Node(T object) {
        this.object = object;
        parents = new LinkedList<>();
        children = new LinkedList<>();
    }


    void visit(Consumer<Node<T>> consumer, Set<Node<T>> visited) {
        consumer.accept(this);
        visited.add(this);
        for (Node<T> node : children) {
            if (visited.contains(node)) continue;
            node.visit(consumer, visited);
        }
    }

    void visitReverse(Consumer<Node<T>> consumer, Set<Node<T>> visited) {
        for (Node<T> node : children) {
            if (visited.contains(node)) continue;
            node.visitReverse(consumer, visited);
        }

        consumer.accept(this);
        visited.add(this);
    }

    public T getObject() {
        return object;
    }

    List<Node<T>> getParents() {
        return parents;
    }

    List<Node<T>> getChildren() {
        return children;
    }

    public void addParent(Node<T> parent) {
        if (parent == this) throw new CycleFoundException(this + "->" + this);
        parents.add(parent);
        if (parent.getChildren().contains(this)) return;
        parent.addChild(this);
    }

    public void addChild(Node<T> child) {
        if (child == this) throw new CycleFoundException(this + "->" + this);
        children.add(child);
        if (child.getParents().contains(this)) return;
        child.addParent(this);
    }

    @Override
    public String toString() {
        return "Node{" +
                "object=" + object.toString() +
                //                ", parents=" + parents.size() +
                //                ", children=" + children.size() +
                '}';
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node)
            return object.equals(((Node<T>) obj).getObject());
        else return false;
    }
}
