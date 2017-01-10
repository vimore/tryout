package com.securityx.modelfeature.utils;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;


@SuppressWarnings("serial")
public class ConcreteMultiValuedMap<K, V> extends HashMap<K, List<V>> implements MultivaluedMap<K, V>{
    @Override
    public void putSingle(K key, V value)
    {
        List<V> list = new ArrayList<V>();
        list.add(value);
        put(key, list);
    }

    @Override
    public final void add(K key, V value)
    {
        getList(key).add(value);
    }

    public final void addMultiple(K key, Collection<V> values)
    {
        getList(key).addAll(values);
    }

    @Override
    public V getFirst(K key)
    {
        List<V> list = get(key);
        return list == null ? null : list.get(0);
    }

    private final List<V> getList(K key)
    {
        List<V> list = get(key);
        if (list == null)
            put(key, list = new ArrayList<V>());
        return list;
    }

    public void addAll(MultivaluedMap<K, V> other)
    {
        for (Map.Entry<K, List<V>> entry : other.entrySet())
        {
            getList(entry.getKey()).addAll(entry.getValue());
        }
    }
    public void addAll(Map<K, V> other)
    {
        for (Map.Entry<K, V> entry : other.entrySet())
        {
           putSingle(entry.getKey(), entry.getValue());
        }
    }
    public String toJsonString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        boolean appendComa = false;
        for(K key: keySet()){
            if(appendComa){
                stringBuilder.append(",");
            }
            stringBuilder.append(String.format("\"%s\"", key.toString()));
            List<V> values = get(key);
            switch (values.size()) {
                case 0:
                    // TODO this may be wrong
                    stringBuilder.append(":\"\"");
                    break;
                case 1:
                    stringBuilder.append(String.format(":\"%s\"", values.get(0).toString()));
                    break;
                default:
                    boolean ac = false;
                    stringBuilder.append(":[");
                    for (V value : values) {
                        if (ac) stringBuilder.append(",");
                        stringBuilder.append(String.format("\"%s\"",value.toString()));
                        ac = true;
                    }
                    stringBuilder.append("]");
            }

            appendComa = true;
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}