package pl.databucket.web.database;


import java.util.HashMap;
import java.util.Map;

//TODO reimplement or drop entirely
public class ValidatorMap<K, V> extends HashMap implements Map {

    private final Class<Exception> exception;
    private final boolean required;

    public ValidatorMap(Class<Exception> exception){
        this(exception, false);
    }

    public ValidatorMap(Class<Exception> exception, boolean required){
        super();
        this.exception = exception;
        this.required = required;
    }

//    public V getOrThrow(K key, V defaultValue) throws IllegalAccessException, InstantiationException {
//        if(this.required){
////            throw exception.newInstance();
//        }
//        return (e = getNode(hash(key), key)) == null ? defaultValue : .e.value;
//    }

}
