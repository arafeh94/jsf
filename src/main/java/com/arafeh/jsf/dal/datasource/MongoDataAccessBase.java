package com.arafeh.jsf.dal.datasource;

import com.arafeh.jsf.config.AppProperties;
import com.arafeh.jsf.core.protocols.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.slf4j.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

public abstract class MongoDataAccessBase<T extends ModelInterface>
        implements DataAccessInterface<T, MongoCollection, Long> {

    MongoDataSource dataSource = MongoDataSource.getInstance();

    public abstract String getTableName();

    public abstract Class<T> getModelClass();

    public abstract Logger getLogger();

    protected String _id() {
        return "_id";
    }

    protected MongoCollection<T> collection() {
        return dataSource.client().getDatabase(AppProperties.getInstance().getMongodbDatabase()).getCollection(getTableName(), getModelClass());
    }

    @Override
    public Optional<T> find(Long id) {
        return Optional.ofNullable(collection().find(eq(_id(), id)).first());
    }

    @Override
    public List<T> find(Map<String, Object> query) {
        ArrayList<Bson> filters = new ArrayList<>();
        for (String key : query.keySet()) {
            filters.add(and(eq(key, query.get(key))));
        }
        return toList(collection().find(and(filters)));
    }

    @Override
    public Optional<T> findOne(Map<String, Object> query) {
        List<T> found = find(query);
        if (found.isEmpty()) return Optional.empty();
        return Optional.of(found.get(0));
    }

    @Override
    public List<T> all() {
        return toList(collection().find());
    }

    @Override
    public long size() {
        return (int) collection().countDocuments();
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
        return collection().find().iterator();
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
            collection().insertOne(model);
            return true;
        } catch (Exception e) {
            getLogger().error(e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        if (o instanceof Long) {
            return collection().deleteOne(eq(_id(), o))
                    .getDeletedCount() > 0;
        }
        if (getModelClass().isAssignableFrom(o.getClass())) {
            try {
                return collection().deleteOne(eq(_id(),
                        ((ModelInterface) o).getId())).getDeletedCount() > 0;
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
            collection().insertMany(new ArrayList<>(c));
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
            return collection().deleteMany(or(idsFilter(c)))
                    .getDeletedCount() > 0;
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
        collection().drop();
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

    @Override
    public T set(Long id, T element) {
        try {
            return collection().findOneAndReplace(eq(_id(), id), element);
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
            return collection().findOneAndDelete(eq(_id(), id));
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
        return toList(collection().find());
    }

    public List<T> toList(FindIterable<T> findIterable) {
        ArrayList<T> ts = new ArrayList<>();
        findIterable.forEach((Consumer<? super T>) ts::add);
        return ts;
    }

    @Override
    public List<T> query(Function<List<T>, MongoCollection> executor) {
        MongoDatabase database = dataSource.client().getDatabase(AppProperties.getInstance().getMongodbDatabase());
        MongoCollection collection = database.getCollection(getTableName(), getModelClass());
        return executor.run(collection);
    }

    @Override
    public List<T> findAll(ArrayList<Long> ids) {
        return toList(collection().find(or(idsFilter(ids))));
    }

    protected ArrayList<Bson> idsFilter(Collection<?> c) {
        ArrayList<Bson> filters = new ArrayList<>();
        c.forEach(item -> {
            if (item.getClass().isAssignableFrom(getModelClass())) {
                filters.add(eq(_id(), ((ModelInterface) item).getId()));
            }
            if (item.getClass().isAssignableFrom(Long.class)
                    || item.getClass().isAssignableFrom(Integer.class)) {
                filters.add(eq(_id(), item));
            }
        });
        return filters;
    }

    @Override
    public String toString() {
        return asList().toString();
    }
}
