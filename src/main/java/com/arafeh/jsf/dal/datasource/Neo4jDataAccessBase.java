package com.arafeh.jsf.dal.datasource;

import com.arafeh.jsf.core.protocols.Function;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static org.neo4j.ogm.cypher.ComparisonOperator.EQUALS;

@SuppressWarnings("Duplicates")
public abstract class Neo4jDataAccessBase<T extends ModelInterface>
        implements DataAccessInterface<T, Session, Long> {

    private Neo4jDataSource dataSource = Neo4jDataSource.getInstance();

    public abstract String getTableName();

    public abstract Class<T> getModelClass();

    public abstract Logger getLogger();

    private Session session;

    private Session open() {
        if (session == null) {
            session = dataSource.client().openSession();
        }
        return session;
    }


    @SuppressWarnings("LoopStatementThatDoesntLoop")
    @Override
    public Optional<T> find(Long id) {
        Filter filter = new Filter(_id(), EQUALS, id);
        Optional<T> result = Optional.empty();
        for (T t : open().loadAll(getModelClass(), filter)) {
            result = Optional.of(t);
            break;
        }
        return result;
    }

    @Override
    public List<T> find(Map<String, Object> query) {
        return new ArrayList<>();
    }

    @Override
    public List<T> findAll(ArrayList<Long> ids) {
        return all();
    }

    @Override
    public List<T> all() {
        return asList();
    }

    @Override
    public long size() {
        return open().count(getModelClass(), null);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass().isAssignableFrom(getModelClass())) {
            return find(((ModelInterface) o).getId()).isPresent();
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return open().loadAll(getModelClass()).iterator();
    }

    @Override
    public Object[] toArray() {
        return asList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return asList().toArray(a);
    }

    @Override
    public boolean add(T model) {
        try {
            open().save(model);
            return true;
        } catch (Exception e) {
            getLogger().error(e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        if (o.getClass().isAssignableFrom(getModelClass())) {
            try {
                open().delete(o);
                return true;
            } catch (Exception e) {
                getLogger().error(e.getLocalizedMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        try {
            for (T t : c) {
                open().save(t);
            }
            return true;
        } catch (Exception e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * @param index
     * @param c
     * @return
     * @deprecated use addAll(Collection<? extends T> c) instead
     */
    @Override
    @Deprecated
    public boolean addAll(Long index, Collection<? extends T> c) {
        return addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        try {
            open().delete(getModelClass(), new It(c), false);
            return true;
        } catch (Exception e) {
            getLogger().error(e.getLocalizedMessage());
            return false;
        }
    }


    @Deprecated
    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        open().deleteAll(getModelClass());
    }

    /**
     * throw error if not found, it's better to use find() instead
     *
     * @param id
     * @return
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public T get(Long id) {
        return find(id).get();
    }

    public T get(Long id, T def) {
        Optional<T> search = find(id);
        return search.orElse(def);
    }

    @Override
    public T set(Long id, T element) {
        try {
            open().save(element);
            return element;
        } catch (Exception e) {
            getLogger().error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * @param index
     * @param element
     * @throws UnsupportedOperationException
     */
    @Deprecated
    @Override
    public void add(Long index, T element) {
        throw new UnsupportedOperationException("not supported in mongodb");
    }

    @Override
    public T removeById(Long id) {
        try {
            T t = open().load(getModelClass(), id);
            open().delete(t);
            return t;
        } catch (Exception e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * @param o
     * @return
     * @throws UnsupportedOperationException
     */
    @Deprecated
    @Override
    public Long indexOf(Object o) {
        throw new UnsupportedOperationException("not supported in mongodb");
    }

    /**
     * @param o
     * @return
     * @throws UnsupportedOperationException
     */
    @Deprecated
    @Override
    public Long lastIndexOf(Object o) {
        throw new UnsupportedOperationException("not supported in mongodb");
    }

    @Override
    public ListIterator<T> listIterator() {
        return asList().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return asList().listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return asList().subList(fromIndex, toIndex);
    }

    @Override
    public List<T> asList() {
        return new ArrayList<>(open().loadAll(getModelClass()));
    }


    @Override
    public List<T> query(Function<List<T>, Session> executor) {
        return executor.run(open());
    }

    class It implements Iterable<Filter> {
        final Collection<?> c;

        public It(Collection<?> c) {
            this.c = c;
        }

        @Override
        public Iterator<Filter> iterator() {
            ArrayList<Filter> filters = new ArrayList<>();
            c.forEach(item -> {
                if (item.getClass().isAssignableFrom(getModelClass())) {
                    filters.add(
                            new Filter(_id(), EQUALS, ((ModelInterface) item).getId())
                    );
                }
                if (item.getClass().isAssignableFrom(Long.class)
                        || item.getClass().isAssignableFrom(Integer.class)) {
                    filters.add(
                            new Filter(_id(), EQUALS, item)
                    );
                }
            });
            return filters.iterator();
        }
    }

    private String _id() {
        for (Field field : getModelClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field.getName();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return asList().toString();
    }
}
