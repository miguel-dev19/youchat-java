package com.yanzhenjie.album;

/**
 * <p>Filter.</p>
 * Created by YanZhenjie on 2017/10/15.
 */
public interface Filter<T> {

    boolean filter(T attributes);

}